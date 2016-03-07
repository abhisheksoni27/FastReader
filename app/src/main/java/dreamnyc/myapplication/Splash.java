package dreamnyc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {
    private TextView logo;
    private Handler waitPost = new Handler();
    private int SPLASH_TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initializeVariables();
        final Intent splashOver = new Intent(this, MainActivity.class);
        waitPost.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(splashOver);
                finish();

            }
        }, SPLASH_TIME);

    }

    private void initializeVariables() {

    }

}
