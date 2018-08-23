package Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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

import Adapters.MyListCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dreamnyc.myapplication.Book;
import dreamnyc.myapplication.BookSave;
import dreamnyc.myapplication.Filewalker;
import dreamnyc.myapplication.HelperFunctions;
import dreamnyc.myapplication.OPFParsing;
import dreamnyc.myapplication.R;


public class MainActivity extends AppCompatActivity {
    BookSave myDb;
    private Runnable runOnLaunch;
    private ArrayList gotAnEpub = new ArrayList();
    private ArrayList nameFiles = new ArrayList();
    private HelperFunctions hf = new HelperFunctions();
    private String sortOrder;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Context context;
    private SQLiteDatabase writeableDatabase;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private MyListCursorAdapter m;
    private Cursor cursor;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        init();
        setupToolbar();
        setupRefreshLayout();

        askForPermissions();

        recyclerView.setHasFixedSize(true);

        myDb = new BookSave(this);
        writeableDatabase = myDb.getReadableDatabase();

        runOnLaunch = new Runnable() {
            @Override
            public void run() {
                final String[] projection = {BookSave.COLUMN_NAME_ENTRY_ID,
                        BookSave.COLUMN_NAME_TITLE,
                        BookSave.COLUMN_NAME_AUTHOR,
                        BookSave.COLUMN_NAME_COVER_PATH,
                        BookSave.COLUMN_NAME_BOOK_OBJECT};
                sortOrder = BookSave.COLUMN_NAME_ENTRY_ID + " DESC";

                cursor = writeableDatabase.query(
                        BookSave.TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        null,
                        null,                                     // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        sortOrder                               // The sort order
                );

                m = new MyListCursorAdapter(getApplicationContext(), cursor);
                recyclerView.setAdapter(m);

                DisplayMetrics DM = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(DM);
                gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                recyclerView.setLayoutManager(gaggeredGridLayoutManager);
                m.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        };
        runOnLaunch.run();

    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != 3) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10);

        }
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyTask().execute();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
    }

    private void setupToolbar() {
        setToolbarMenu(toolbar);
        toolbar.setTitle("Flying Words");
        toolbar.inflateMenu(R.menu.menu_main);
    }

    private void setToolbarMenu(Toolbar toolbar) {
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.new_game:
                        swipeRefreshLayout.setRefreshing(true);
                        new MyTask().execute();
                        return true;
                    case R.id.action_settings:
                        startActivity(new Intent(context, SettingsActivity.class));
                }
                return true;
            }
        });
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
                new MyTask().execute();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

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
                    final SQLiteDatabase writeableDatabase = myDb.getWritableDatabase();
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("_id", Math.random());
                    insertValues.put("title", Uri.parse(book.getTitle()).toString());
                    insertValues.put("author", book.getAuthor());
                    insertValues.put("cover", book.getPathOfCover());
                    insertValues.put("bookObject", gs.toJson(book));

                    writeableDatabase.insert("book", null, insertValues);
                    m = new MyListCursorAdapter(getApplicationContext(), cursor);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
                index += 1;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            runOnLaunch.run();

        }
    }
}

