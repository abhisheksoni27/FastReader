package Activities;


import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import dreamnyc.myapplication.Book;
import dreamnyc.myapplication.R;

public class ChapterView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_view);
        Intent i = getIntent();
        WebView wv = (WebView) findViewById(R.id.webView);
        Gson parserJson = new Gson();
        String chapter = i.getStringExtra("CHAPTERURL");
        String toBeParsed = i.getStringExtra("BOOKOBJECT");
        final Book gotIt = parserJson.fromJson(toBeParsed, Book.class);
        Boolean extension1 = gotIt.getSpineExtension().contains(".html");
        Boolean extension2 = gotIt.getSpineExtension().contains(".xhtml");
        if (!chapter.contains("/") && extension1) {
            try {
                chapter = new File(gotIt.getSpinePath().get(0).toString().substring(0, gotIt.getSpinePath().get(0).toString().lastIndexOf("/") + 1) + chapter + ".html").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!chapter.contains("/") && extension2) {
            try {
                chapter = new File(gotIt.getSpinePath().get(0).toString().substring(0, gotIt.getSpinePath().get(0).toString().lastIndexOf("/")) + chapter + ".xhtml").getCanonicalPath();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        File f = new File(chapter);
        wv.loadUrl("file:///" + f.getAbsolutePath());

    }

}

