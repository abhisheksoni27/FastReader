package dreamnyc.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;

import java.util.List;

public class ShowReader extends AppCompatActivity {
    public String toBeParsed;
    public Book gotIt = null;
    public List<String> gaggeredList;
    BookSave myDb;
    private SQLiteDatabase writeableDatabase;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        Intent i = getIntent();
        Gson parserJson = new Gson();
        myDb = new BookSave(this);
        writeableDatabase = myDb.getReadableDatabase();
        final String title = i.getStringExtra("BOOK_NAME");
        String[] a = {title};
        final String[] projection = {BookSave.COLUMN_NAME_ENTRY_ID,
                BookSave.COLUMN_NAME_TITLE,
                BookSave.COLUMN_NAME_AUTHOR,
                BookSave.COLUMN_NAME_COVER_PATH,
                BookSave.COLUMN_NAME_BOOK_OBJECT};

        final Cursor c = writeableDatabase.query(
                BookSave.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "title=?",
                a,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                               // The sort order
        );


        c.moveToFirst();
        toBeParsed = c.getString(c.getColumnIndexOrThrow(BookSave.COLUMN_NAME_BOOK_OBJECT));
        gotIt = parserJson.fromJson(toBeParsed, Book.class);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chapterList);
        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        List<String> gaggeredList = gotIt.getSpine();
        chapterListViewAdapter rcAdapter = new chapterListViewAdapter(getApplicationContext(), gaggeredList);
        recyclerView.setAdapter(rcAdapter);


        /*


                String i = your_array_list.get(position);
                Log.d("Okay Sent i", i);
                String urlSent = gotIt.findInSpine(i, gotIt);
                Log.d("Okay URL RECIEVED okay", urlSent + "");
                Intent read = new Intent(getApplicationContext(), FastRead.class);
                read.putExtra("CHAPTERURL", urlSent);
                read.putExtra("BOOKOBJECT", toBeParsed);
                read.putExtra("CHAPTERNAME", i);
                startActivity(read);

        */

    }

}
