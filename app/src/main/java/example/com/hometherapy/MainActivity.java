package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // for log
    private static final String TAG = "Main_Activity";

    private Button _btnGoToSignIn;
    private Button _btnGoToExercises;
    private Button _btnGoToMyClients;
    private Button _btnTestNavigation;
    private Button _btnGoToMyMessages;
    private Button _btnGoToUsers;
    private Button _btnGoToMyExercises;
    private Button _btnClearSharedPrefs;

    private String _userData;
    private String _exerciseData;
    private String _assignedExerciseData;

    // name Shared Preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";
    public static final String USER_DATA = "userData";
    public static final String EXERCISE_DATA = "exerciseData";

    // private member variables
    private SharedPreferences _sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _btnGoToSignIn =     (Button) findViewById(R.id.btnGoToSignIn);
        _btnGoToExercises =  (Button) findViewById(R.id.btnGoToExercises);
        _btnGoToMyClients =  (Button) findViewById(R.id.btnGoToMyClients);
        _btnGoToMyMessages = (Button) findViewById(R.id.btnGoToMyMessages);
        _btnTestNavigation = (Button) findViewById(R.id.btnTestNavigation);
        _btnGoToUsers      = (Button) findViewById(R.id.btnGoToUsers);
        _btnGoToMyExercises = (Button) findViewById(R.id.btnGoToMyExercises);
        _btnClearSharedPrefs = (Button) findViewById(R.id._btnClearSharedPrefs);

        // open up Shared Preferences db
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        _userData = _sharedPreferences.getString(USER_DATA, "");
        _exerciseData = _sharedPreferences.getString(EXERCISE_DATA, "");
        _assignedExerciseData = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

        // log shared preference data
        Log.d(TAG, "user data: " + _userData);
        Log.d(TAG, "exercise data: " + _exerciseData);
        Log.d(TAG, "assigned exercise data: " + _assignedExerciseData);

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

        // List of Exercises screen
        _btnGoToExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intent_Exercises_Screen = new Intent(MainActivity.this, Exercises.class);

                // start the new activity
                startActivity(intent_Exercises_Screen);
            }
        });

        // Exercises screen
        _btnGoToMyExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intent_My_Exercises_Screen = new Intent(MainActivity.this, MyExercises.class);

                // start the new activity
                startActivity(intent_My_Exercises_Screen);
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
                Intent intentTestNavScreen = new Intent(MainActivity.this, TestNavigation.class);

                // start the new activity
                startActivity(intentTestNavScreen);
            }
        });

        // My Messages screen
        _btnGoToMyMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentMessagesScreen = new Intent(MainActivity.this, MyMessages.class);

                // start the new activity
                startActivity(intentMessagesScreen);
            }
        });

        // Users screen
        _btnGoToUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to sign in screen
                Intent intentSignInScreen = new Intent(MainActivity.this, Users.class);

                // start the new activity
                startActivity(intentSignInScreen);
            }
        });

        // cleared shared preferences
        _btnClearSharedPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SharedPreferences.Editor editor = _sharedPreferences.edit();
               editor.putString(USER_DATA, "");
               editor.putString(EXERCISE_DATA, "");
               editor.putString(ASSIGNED_EXERCISE_DATA, "");
               editor.apply();
            }

        });

    }

    // create a new message
    public void createNewMessage(View view) {
        Messaging myMessage = new Messaging();
    }
}
