package Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import dreamnyc.myapplication.Book;
import dreamnyc.myapplication.BookSave;
import dreamnyc.myapplication.HelperFunctions;
import dreamnyc.myapplication.R;

public class FastRead extends AppCompatActivity {
    static boolean active = false;
    public static final String READING_POSITION = "READING_POSITION";
    public static final String LAST_READING_POSITION = "LAST_READING_POSITION";
    private int a = 0;
    private BookSave myDb;
    private TextView fastReadView;
    private String chapterName;
    int flag = 0;
    private ImageButton playPause, darkMode, setSpeed = (ImageButton) findViewById(R.id.speed), fastForward, reverse;
    private int speed = 500;
    private String[] streamSplit;
    private int mode = 0;
    private Gson gs = new Gson();
    private Book mBook;
    private SharedPreferences sharedPreferences;
    private HelperFunctions hf = new HelperFunctions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_read);

        SharedPreferences s1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speed = s1.getInt(getString(R.string.speedReadKey), speed);

        init();

        Intent i = getIntent();
        String url = i.getStringExtra("CHAPTERURL");
        chapterName = i.getStringExtra("CHAPTERNAME");
        String json = i.getStringExtra("BOOKOBJECT");

        mBook = gs.fromJson(json, Book.class);

        sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);

        a = sharedPreferences.getInt(LAST_READING_POSITION + chapterName + mBook.getTitle(), 0);

        String fileToBeReadAsText = hf.buildDocument(url, chapterName);

        final String stream = hf.fileAsText(fileToBeReadAsText);
        streamSplit = stream.split(" ");

        final Handler handler1 = new Handler();

        final Runnable autoSave = new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putInt(LAST_READING_POSITION + chapterName + mBook.getTitle(), a - 10 > 0 ? a - 10 : a).apply();


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

        myDb = new BookSave(this);

        final Toolbar tool = findViewById(R.id.view3);
        darkMode = findViewById(R.id.darkMode);
        fastForward = (ImageButton) findViewById(R.id.fastForward);
        reverse = (ImageButton) findViewById(R.id.reverse);
        final RelativeLayout hideShow = (RelativeLayout) findViewById(R.id.hideShow);


        fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = a + 10;
            }
        });

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a = a - 10;
            }
        });


        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode != 0) {
                    //black
                    hideShow.setBackgroundColor(Color.parseColor("#ffffff"));
                    fastReadView.setTextColor(Color.parseColor("#000000"));
                    setSpeed.setBackgroundResource(R.drawable.ic_trending_up_black_24dp);
                    if (flag == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    }
                    darkMode.setBackgroundResource(R.drawable.ic_brightness_4_black_24dp);


                    mode = 0;
                } else {
                    //white
                    hideShow.setBackgroundColor(Color.parseColor("#000000"));
                    fastReadView.setTextColor(Color.parseColor("#ffffff"));

                    setSpeed.setBackgroundResource(R.drawable.ic_trending_up_black_24dp);
                    if (flag == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    }
                    darkMode.setBackgroundResource(R.drawable.ic_brightness_4_black_24dp);
                    mode = 1;
                }


            }
        });

//        setSpeed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create custom dialog object
//                final Dialog dialog = new Dialog(FastRead.this);
//
//                dialog.setContentView(R.layout.speed_dialog);
//                dialog.setTitle("Set Speed");
//                final int copied = speed;
//                dialog.show();
//
//                SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
//                Button submitButton, declineButton;
//                submitButton = (Button) dialog.findViewById(R.id.submitButton);
//                declineButton = (Button) dialog.findViewById(R.id.declineButton);
//
//                submitButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                declineButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        speed = copied;
//                        dialog.dismiss();
//                    }
//                });
//
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        speed = progress;
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//
//                        speed = seekBar.getProgress();
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        speed = seekBar.getProgress();
//                    }
//                });
//            }
//        });

        hideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tool.getVisibility() == View.VISIBLE) {
                    tool.setVisibility(View.INVISIBLE);
                } else {
                    tool.setVisibility(View.VISIBLE);
                }

            }
        });

        handler1.postDelayed(r, speed);
        handler1.postDelayed(autoSave, 2000);


        playPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    onPause(chapterName, mBook.getTitle(), handler1, r);
                    if (mode == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    }
                } else if (flag == 1) {
                    onResume(chapterName, mBook.getTitle());
                    if (mode == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    }
                    handler1.postDelayed(r, speed);
                }

            }
        });


    }

    private void init() {
        fastReadView = (TextView) findViewById(R.id.FastReadView);
        playPause = (ImageButton) findViewById(R.id.playPause);
    }

    public void onResume(String chapterName, String title) {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        a = sharedPreferences.getInt(LAST_READING_POSITION + chapterName + title, 0);
        flag = 0;
    }

    public void onPause(String chapterName, String title, Handler h, Runnable r) {
        super.onPause();
        h.removeCallbacks(r);
        SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        sharedPreferences.edit().putInt(LAST_READING_POSITION + chapterName + title, a).apply();
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

}
