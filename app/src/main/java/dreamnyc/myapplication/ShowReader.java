package dreamnyc.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ShowReader extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public String toBeParsed;
    public Book gotIt = null;
    public List<String> gaggeredList;
    BookSave myDb;
    private SQLiteDatabase writeableDatabase;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
private View seperatorChapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_parent);
        Intent i = getIntent();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);;

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        Gson parserJson = new Gson();
        myDb = new BookSave(getApplicationContext());
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
        gaggeredList = gotIt.getSpine();

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChapterList(), "Table of Contents");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public String[] onClickCalled(int i1) {
        String urlSent = gotIt.findInSpine(i1, gotIt);
        String s = toBeParsed;
        String[] a = {s, urlSent};
        return a;
    }
}
