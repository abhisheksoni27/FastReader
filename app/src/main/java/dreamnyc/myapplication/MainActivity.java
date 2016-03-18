package dreamnyc.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public String BOOK_OBJECT = "BOOK_OBJECT";
    BookSave myDb;
    private ArrayList gotAnEpub = new ArrayList();
    private ArrayList nameFiles = new ArrayList();
    private HelperFunctions hf = new HelperFunctions();
    private Thread runImport;
    private Thread loadBooks;
    private String sortOrder;
    private int i = 0;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler mHandler = new Handler();
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomRecyclerView rv;
    private Context context;
    private SQLiteDatabase writeableDatabase;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        rv = (CustomRecyclerView) findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        myDb = new BookSave(this);
        writeableDatabase = myDb.getReadableDatabase();
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        final String[] projection = {BookSave.COLUMN_NAME_ENTRY_ID,
                BookSave.COLUMN_NAME_TITLE,
                BookSave.COLUMN_NAME_AUTHOR,
                BookSave.COLUMN_NAME_COVER_PATH,
                BookSave.COLUMN_NAME_BOOK_OBJECT};
        sortOrder = BookSave.COLUMN_NAME_ENTRY_ID + " DESC";

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runThread(runImport);
            }
        });

        Runnable readBooks = new Runnable() {
            @Override
            public void run() {


                final Cursor c = writeableDatabase.query(
                        BookSave.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,
                        null,                                     // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                               // The sort order
                );

                c.moveToFirst();
                MyListCursorAdapter m = new MyListCursorAdapter(getApplicationContext(), c);
                rv.setAdapter(m);
                gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                rv.setLayoutManager(gaggeredGridLayoutManager);
            }
        };

        Runnable importFiles = new Runnable() {
            @Override
            public void run() {


                ContentResolver cr = context.getContentResolver();
                Uri uri = MediaStore.Files.getContentUri("external");
                String[] projection = null;

                String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
                String[] selectionArgs = null; // there is no ? in selection so null here

                Cursor cur = cr.query(uri, projection, selection, selectionArgs, null);


                cur.moveToFirst();

                while (cur.isAfterLast() == false) {
                    int epubIndex = cur.getString(1).lastIndexOf(".epub");
                    if (epubIndex != -1 && cur.getString(1).endsWith("epub")) {
                        gotAnEpub.add(cur.getString(1));
                        i += 1;
                    }

                    cur.moveToNext();


                }


                cur.close();
                int index = 0;
                while (index < gotAnEpub.size()) {

                    String nameOfTheEpub = hf.extractName(gotAnEpub.get(index).toString());
                    File src = new File(gotAnEpub.get(index).toString());
                    File dst = new File(getExternalFilesDir(null).getPath() + File.separator + nameOfTheEpub + ".epub");
                    File newfile = new File(getExternalFilesDir(null).getPath());

                    try {

                        hf.saveFile(src, dst);


                        HelperFunctions.unpack(newfile.getPath() + "/" + nameOfTheEpub + ".epub", newfile.getPath() + "/" + nameOfTheEpub + "/");
                        hf.deleteFile(newfile.getPath() + "/" + nameOfTheEpub + ".epub");

                        String path = newfile.getPath() + "/" + nameOfTheEpub + "/";

                        File f = new File(path);
                        Filewalker fw = new Filewalker();
                        File OPF;
                        OPFParsing opfp = new OPFParsing();
                        String opf = fw.container(f, "container");


                        if (opf == "Not  Found") {
                            OPF = new File(path);

                        } else {
                            OPF = new File(opf);
                        }

                        Document doc = Jsoup.parse(OPF, "UTF-8");
                        String opfPath = doc.getElementsByTag("rootfile").attr("full-path");
                        Book book = new Book();
                        book.setPath(path);
                        book.getContents(path + opfPath, getExternalFilesDir(null).getAbsolutePath());
                        Gson gs = new Gson();

                        System.out.println(gs.toJson(book.getSpinePath()));
                        final SQLiteDatabase writeableDatabase = myDb.getWritableDatabase();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("_id", Math.random());
                        insertValues.put("title", Uri.parse(book.getTitle()).toString());
                        insertValues.put("author", book.getAuthor());
                        insertValues.put("cover", book.getPathOfCover());
                        insertValues.put("bookObject", gs.toJson(book));

                        writeableDatabase.insert("book", null, insertValues);


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    index += 1;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(getIntent());
                    }
                });


            }
        };
        loadBooks = new Thread(readBooks);
        loadBooks.start();
        runImport = new Thread(importFiles);
    }


    public void runThread(Thread t) {
        if (t.getState() == Thread.State.NEW) {
            t.start();
        } else {
        }
    }

    public void cl(String str) {
        Log.d("Okay", str);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                swipeRefreshLayout.setRefreshing(true);
                runThread(runImport);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFiles() {
        String path = getExternalFilesDir(null).toString();
        cl(path);
        File f = new File(path);
        File file[] = f.listFiles();

        for (int i = 0; i < file.length; i++) {
            nameFiles.add(file[i]);
        }
    }

    public void showFiles(ArrayList nameFiles) {

        String path = getExternalFilesDir(null).toString();
        int j = 0;
        for (j = 0; j < nameFiles.size(); j++) {
            path = nameFiles.get(j).toString();
            cl("PATH: " + path);

            File f = new File(path + "/");
            File file[] = f.listFiles();
            Filewalker fw = new Filewalker();
            fw.walk(f);
        }


    }

}
