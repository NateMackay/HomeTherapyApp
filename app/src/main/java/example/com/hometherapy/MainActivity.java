package example.com.hometherapy;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Splash screen.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MainActivity extends AppCompatActivity {

    // for log
    private static final String TAG = "Main_Activity";

    // duration of delay for splash screen
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up delay and then move on to sign in screen
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // move to Sign-in after
                Intent mainIntent = new Intent(MainActivity.this, SignIn.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();

                Log.d(TAG, "intent to signIn.class");
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
