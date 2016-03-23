package dreamnyc.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
    ImageButton playPause, darkMode, setSpeed, fastForward, reverse;
    private int speed = 500;
    String[] streamSplit;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_read);

        SharedPreferences s1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        speed = s1.getInt(getString(R.string.speedReadKey), speed);
        fastReadView = (TextView) findViewById(R.id.FastReadView);
        playPause = (ImageButton) findViewById(R.id.playPause);
        Intent i = getIntent();
        String url = i.getStringExtra("CHAPTERURL");
        chapterName = i.getStringExtra("CHAPTERNAME");
        String json = i.getStringExtra("BOOKOBJECT");
        Gson gs = new Gson();
        final Book b = gs.fromJson(json, Book.class);
        final SharedPreferences sharedPreferences = getSharedPreferences(READING_POSITION, MODE_PRIVATE);
        a = sharedPreferences.getInt(LAST_READING_POSITION + chapterName + b.getTitle(), 0);
        HelperFunctions hf = new HelperFunctions();
        String fileToBeReadAsText = hf.buildDocument(url, chapterName);
        final String stream = hf.fileAsText(fileToBeReadAsText);
        streamSplit = stream.split(" ");
        final Handler handler1 = new Handler();
        final Runnable autoSave = new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putInt(LAST_READING_POSITION + chapterName + b.getTitle(), a - 10 > 0 ? a - 10 : a).apply();


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

        final Toolbar tool = (Toolbar) findViewById(R.id.view3);
        darkMode = (ImageButton) findViewById(R.id.darkMode);
        setSpeed = (ImageButton) findViewById(R.id.speed);
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
                    setSpeed.setBackgroundResource(R.drawable.ic_trending_up_24dp);
                    if (flag == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24px);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_24dp);
                    }
                    darkMode.setBackgroundResource(R.drawable.ic_brightness_4_24dp);



                    mode = 0;
                } else {
                    //white
                    hideShow.setBackgroundColor(Color.parseColor("#000000"));
                    fastReadView.setTextColor(Color.parseColor("#ffffff"));

                    setSpeed.setBackgroundResource(R.drawable.ic_trending_up_white_24px);
                    if (flag == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_white_24px);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_24px);
                    }
                    darkMode.setBackgroundResource(R.drawable.ic_brightness_4_white_24px);
                    mode = 1;
                }


            }
        });

        setSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create custom dialog object
                final Dialog dialog = new Dialog(FastRead.this);

                dialog.setContentView(R.layout.speed_dialog);
                dialog.setTitle("Set Speed");
                final int copied = speed;
                dialog.show();

                SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
                Button submitButton, declineButton;
                submitButton = (Button) dialog.findViewById(R.id.submitButton);
                declineButton = (Button) dialog.findViewById(R.id.declineButton);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        speed = copied;
                        dialog.dismiss();
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        speed = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {


                        speed = seekBar.getProgress();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        speed = seekBar.getProgress();
                    }
                });
            }
        });

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
                    onPause(chapterName, b.getTitle(), handler1, r);
                    if (mode == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24px);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_play_circle_outline_white_24px);
                    }
                } else if (flag == 1) {
                    onResume(chapterName, b.getTitle());
                    if (mode == 0) {
                        playPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_24dp);
                    } else {
                        playPause.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_24px);
                    }
                    handler1.postDelayed(r, speed);
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
