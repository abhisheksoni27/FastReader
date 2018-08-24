package Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import Adapters.ChapterListViewAdapter;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dreamnyc.myapplication.Book;
import dreamnyc.myapplication.BookSave;
import dreamnyc.myapplication.ExtractChapters;
import dreamnyc.myapplication.R;

public class ShowReader extends AppCompatActivity {
    private static final String TAG = "ShowReader";
    private String toBeParsed;
    private Book gotIt;
    private ArrayList<String> chapterPathList;
    private ArrayList<String> chapterList;
    private BookSave myDb;
    private SQLiteDatabase writeableDatabase;

    public static final String[] PROJECTION = new String[]{BookSave.COLUMN_NAME_ENTRY_ID, BookSave.COLUMN_NAME_TITLE, BookSave.COLUMN_NAME_AUTHOR, BookSave.COLUMN_NAME_COVER_PATH, BookSave.COLUMN_NAME_BOOK_OBJECT};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);
        Gson parserJson = new Gson();

        myDb = new BookSave(this);
        writeableDatabase = myDb.getReadableDatabase();
        String title = getIntent().getStringExtra("BOOK_NAME");
        String[] a = {title};

        Cursor c = writeableDatabase.query(BookSave.TABLE_NAME,
                PROJECTION,
                "title=?",
                a,
                null, null, null);
        c.moveToFirst();

        toBeParsed = c.getString(c.getColumnIndexOrThrow(BookSave.COLUMN_NAME_BOOK_OBJECT));
        gotIt = parserJson.fromJson(toBeParsed, Book.class);
        chapterPathList = ExtractChapters.extractChapters(gotIt.getPathOfTOC(), gotIt.getOPFFile());

        RecyclerView recyclerView = findViewById(R.id.chapterListItemsContainer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new ChapterListViewAdapter(this, chapterPathList));
        Log.d(TAG, "onCreate: " + chapterPathList.toString());

    }

    public String[] onClickCalled(int i1) {
        String urlSent = gotIt.findInSpine(i1, gotIt);
        String s = toBeParsed;
        return new String[]{s, urlSent};
    }
}
