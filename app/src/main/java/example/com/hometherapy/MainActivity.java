package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button _btnGoToSignIn;
    private Button _btnGoToExercises;
    private Button _btnGoToMyClients;
    private Button _btnTestNavigation;
    private Button _btnGoToMyMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _btnGoToSignIn =     (Button) findViewById(R.id.btnGoToSignIn);
        _btnGoToExercises =  (Button) findViewById(R.id.btnGoToExercises);
        _btnGoToMyClients =  (Button) findViewById(R.id.btnGoToMyClients);
        _btnGoToMyMessages = (Button) findViewById(R.id.btnGoToMyMessages);
        _btnTestNavigation = (Button) findViewById(R.id.btnTestNavigation);

        // Sign in screen
        _btnGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent(MainActivity.this, SignIn.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });

        // Exercises screen
        _btnGoToExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intent_Exercises_Screen = new Intent(MainActivity.this, Exercises.class);

                // start the new activity
                startActivity(intent_Exercises_Screen);
            }
        });

        // My Clients list screen
        _btnGoToMyClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent(MainActivity.this, MyClients.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });

        // Test Navigation screen
        _btnTestNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent(MainActivity.this, TestNavigation.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });


        // My Clients list screen
        _btnGoToMyMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent(MainActivity.this, Messaging.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });
    }

    // create a new message
    public void createNewMessage(View view) {
        Messaging myMessage = new Messaging();
    }
}
