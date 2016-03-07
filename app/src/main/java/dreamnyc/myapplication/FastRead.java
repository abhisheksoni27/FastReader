package dreamnyc.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class FastRead extends AppCompatActivity {
    static boolean active = false;
    public String READING_POSITION = "READING_POSITION";
    public String LAST_READING_POSITION = "LAST_READING_POSITION";
    int a = 0;
    BookSave myDb;
    TextView fastReadView;
    String chapterName;
    int flag = 0;
    Button playPause;
    TextView setSpeedShow;
    SeekBar setSpeed;
    private int speed = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_read);

        SharedPreferences s1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speed = s1.getInt(getString(R.string.speedReadKey), Integer.valueOf(getString(R.string.speedReadDefaultValue)));
        fastReadView = (TextView) findViewById(R.id.FastReadView);
        playPause = (Button) findViewById(R.id.playPause);
        setSpeed = (SeekBar) findViewById(R.id.seekBar);
        Intent i = getIntent();
        String url = i.getStringExtra("CHAPTERURL");
        chapterName = i.getStringExtra("CHAPTERNAME");
        String json = i.getStringExtra("BOOKOBJECT");
        Gson gs = new Gson();
        final Book b = gs.fromJson(json, Book.class);
        final SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        a = sharedPreferences.getInt(LAST_READING_POSITION + chapterName + b.getTitle(), 0);
        Log.d("Value of A ", a + "");
        HelperFunctions hf = new HelperFunctions();
        String fileToBeReadAsText = hf.buildDocument(url, chapterName);
        String stream = hf.fileAsText(fileToBeReadAsText);
        final String[] streamSplit = stream.split(" ");
        final Handler handler1 = new Handler();

        myDb = new BookSave(this);
        final TextView setSpeedShow = (TextView) findViewById(R.id.setSpeedShow);

        setSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSpeedShow.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences s1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                s1.edit().putInt(getApplicationContext().getResources().getString(R.string.speedReadKey), seekBar.getProgress()).commit();
                speed = seekBar.getProgress();

            }
        });

        final Runnable autoSave = new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putInt(LAST_READING_POSITION + chapterName + b.getTitle(), a - 10 > 0 ? a - 10 : a).commit();


                if (active) {

                    handler1.postDelayed(this, 2000);
                }
            }
        };

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final String a1 = streamSplit[a];

                fastReadView.setText(a1);
                a++;
                if (a < streamSplit.length) {
                    handler1.postDelayed(this, speed);
                }
            }
        };

        handler1.postDelayed(r, speed);
        handler1.postDelayed(autoSave, 2000);


        playPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    onPause(chapterName, b.getTitle(), handler1, r);
                } else if (flag == 1) {
                    onResume(chapterName, b.getTitle());
                    handler1.postDelayed(r, 300);
                }

            }
        });


    }

    public void onResume(String chapterName, String title) {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        a = sharedPreferences.getInt(LAST_READING_POSITION + chapterName + title, 0);
        flag = 0;
    }

    public void onPause(String chapterName, String title, Handler h, Runnable r) {
        super.onPause();
        Log.d("Pause", "is called.");
        h.removeCallbacks(r);
        SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        sharedPreferences.edit().putInt(LAST_READING_POSITION + chapterName + title, a).commit();
        flag = 1;

    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public void changeProgress() {

        setSpeedShow.setText(Integer.toString(setSpeed.getProgress()));
    }

}
