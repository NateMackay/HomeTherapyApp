package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class ClientExercises extends AppCompatActivity {

    // for log
    private static final String TAG = "Client_Exercises_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    private String _currentUserEmail;

    // member variables
    private AssignedExerciseList _assignedExercises;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    // views
    private ArrayAdapter<AssignedExercise> _adapter; // add custom adapter
    private ListView _lvCEAssignedExercises;
    private Button _btnCEAddExercise;
    private TextView _tvCELabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercises);

        // register views
        _lvCEAssignedExercises = (ListView) findViewById(R.id.lvCEAssignedExercises);
        _btnCEAddExercise = (Button) findViewById(R.id.btnCEAddExercise);
        _tvCELabel = (TextView) findViewById(R.id.tvCELabel);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account) from extra message
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // deserialize sharedPrefs JSON user database into List of Assigned Exercises
        _assignedExercises = _gson.fromJson(jsonAssignedExerciseList, AssignedExerciseList.class);

        Log.d(TAG, "_assignedExercises: " + _assignedExercises);

        // if current assigned exercise database is not empty, then proceed with getting list of
        // assigned exercises and binding to array adapter
        if (_assignedExercises != null) {

            List<AssignedExercise> tempAssignedExerciseList = _assignedExercises.getAssignedExerciseList();

            Log.d(TAG, "tempExerciseList: " + tempAssignedExerciseList);

            // initialize array adapter and bind exercise list to it
            _adapter = new ArrayAdapter<AssignedExercise>(this, android.R.layout.simple_selectable_list_item, tempAssignedExerciseList);

            // set the adapter to the list view
            _lvCEAssignedExercises.setAdapter(_adapter);
        }

        // add an on item click listerner to go to existing exercise to edit

        // add an exercise button - this will pass user email to a Exercises
        _btnCEAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Exercises screen, passing user via extra message
                Intent intentClientExerciseLibrary = new Intent(ClientExercises.this, ClientExerciseLibrary.class);
                intentClientExerciseLibrary.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                startActivity(intentClientExerciseLibrary);

            }
        });

    }
}
