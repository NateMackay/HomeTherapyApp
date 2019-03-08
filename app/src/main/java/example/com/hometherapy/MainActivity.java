package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button _btnGoToSignIn;
    private Button _btnGoToAddExToLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        _btnGoToSignIn = (Button) findViewById(R.id.btnGoToSignIn);
        _btnGoToAddExToLib = (Button) findViewById(R.id.btnGoToAddExToLib);


        _btnGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent (MainActivity.this, SignIn.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });

        _btnGoToAddExToLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intent_AETL_Screen = new Intent (MainActivity.this, AddExerciseToLibrary.class);

                // start the new activity
                startActivity(intent_AETL_Screen);
            }
        });
    }

    // create a new message
    public void createNewMessage(View view) {
        Messaging myMessage = new Messaging();
    }
}
