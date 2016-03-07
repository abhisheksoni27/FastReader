package dreamnyc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.List;

public class ShowReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        Intent i = getIntent();
        Gson parserJson = new Gson();
        final String toBeParsed = i.getStringExtra("BOOK_OBJECT");
        Log.d("Spine", toBeParsed);
        final Book gotIt = parserJson.fromJson(toBeParsed, Book.class);
        ListView lv = (ListView) findViewById(R.id.spine);
        final List<String> your_array_list = gotIt.getSpine();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String i = your_array_list.get(position);
                Log.d("Okay Sent i", i);
                String urlSent = gotIt.findInSpine(i, gotIt);
                Log.d("Okay URL RECIEVED okay", urlSent + "");
                Intent read = new Intent(getApplicationContext(), FastRead.class);
                read.putExtra("CHAPTERURL", urlSent);
                read.putExtra("BOOKOBJECT", toBeParsed);
                read.putExtra("CHAPTERNAME", i);
                startActivity(read);
            }
        });


    }

}
