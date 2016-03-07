package dreamnyc.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class BookSave extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BOOK";
    public static final String TABLE_NAME = "book";
    public static final String COLUMN_NAME_ENTRY_ID = "_id";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_AUTHOR = "author";
    public static final String COLUMN_NAME_COVER_PATH = "cover";

    public static final String COLUMN_NAME_LAST_READ_POSITION = "lastReadPosition";
    public static final String COLUMN_NAME_BOOK_OBJECT = "bookObject";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public BookSave(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_ENTRY_ID + " INTEGER, " +
                COLUMN_NAME_TITLE + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_COVER_PATH + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BOOK_OBJECT + TEXT_TYPE + COMMA_SEP + COLUMN_NAME_LAST_READ_POSITION + " INTEGER" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}