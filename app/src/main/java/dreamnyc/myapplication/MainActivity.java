package dreamnyc.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private ArrayList<Book> books = new ArrayList<>();
    private HelperFunctions hf = new HelperFunctions();
    private ExtractChapters ec = new ExtractChapters();
    private Thread runImport;
    private Thread loadBooks;
    private String sortOrder;
    private int i = 0;
    private ProgressBar mProgress;
    private Handler mHandler = new Handler();
    private ListView lvItems;
    private Context context;
    private SQLiteDatabase writeableDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.INVISIBLE);
        mProgress.setIndeterminate(true);
        lvItems = (ListView) findViewById(R.id.listView);
        myDb = new BookSave(this);
        writeableDatabase = myDb.getReadableDatabase();

        final String[] projection = {BookSave.COLUMN_NAME_ENTRY_ID,
                BookSave.COLUMN_NAME_TITLE,
                BookSave.COLUMN_NAME_AUTHOR,
                BookSave.COLUMN_NAME_COVER_PATH,
                BookSave.COLUMN_NAME_BOOK_OBJECT};
        sortOrder = BookSave.COLUMN_NAME_ENTRY_ID + " DESC";


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

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        TodoCursorAdapter todoAdapter = new TodoCursorAdapter(getApplicationContext(), c, 0);
                        lvItems.setAdapter(todoAdapter);
                        /*lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i = new Intent(getApplicationContext(), ShowReader.class);
                                SQLiteCursor entry = (SQLiteCursor) parent.getItemAtPosition(position);
                                String send = entry.getString(4);
                                i.putExtra(BOOK_OBJECT,send);
                                startActivity(i);
                            }
                        });*/
                        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i = new Intent(getApplicationContext(), ShowReader.class);
                                SQLiteCursor entry = (SQLiteCursor) parent.getItemAtPosition(position);
                                String send = entry.getString(4);
                                i.putExtra(BOOK_OBJECT, send);
                                startActivity(i);
                            }
                        });
                    }
                });
            }
        };


        loadBooks = new Thread(readBooks);
        loadBooks.start();

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
                        mProgress.setVisibility(View.INVISIBLE);
                        finish();
                        startActivity(getIntent());
                    }
                });


            }
        };


        runImport = new Thread(importFiles);


    }


    public void runThread(Thread t) {
        if (t.getState() == Thread.State.NEW) {
            t.start();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.VISIBLE);
                }
            });


        } else {
            t = null;
        }
    }

    public void cl(String str) {
        Log.d("Okay", str);

    }

    public void clI(Integer i) {
        Log.d("Number", Integer.toString(i));

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

    public class TodoCursorAdapter extends CursorAdapter {
        public TodoCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.booklist, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView title = (TextView) view.findViewById(R.id.textView);
            TextView author = (TextView) view.findViewById(R.id.textView2);
            ImageView cover;
            cover = (ImageView) view.findViewById(R.id.imageView1);


            String titleString = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String authorString = cursor.getString(cursor.getColumnIndexOrThrow("author"));
            String coverString = cursor.getString(3);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            if (coverString != null) {

                Bitmap bitmap = BitmapFactory.decodeFile(coverString, options);
                cover.setImageBitmap(bitmap);
            } else {
                cover.setBackgroundColor(Color.parseColor("#000000"));
            }

            title.setText(titleString);
            author.setText(authorString);


        }
    }
}