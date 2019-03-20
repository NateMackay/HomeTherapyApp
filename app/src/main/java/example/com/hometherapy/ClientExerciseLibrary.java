package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

/**
 * View of library of exercises for therapist to select from to assign
 * an exercise to the client. From therapist's view of client's assigned exercises
 * {@link ClientExercises}, if a therapist clicks on the assign new
 * exercise to client button, it takes the therapist to this view,
 * which shows all of the exercises in the library for the therapist
 * to select from. This then takes the therapist to the Add Exercise
 * To Client view {@link AddExerciseToClient}.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link ClientExercises } therapist view of assigned exercises
 * {@link AddExerciseToClient} add/edit view of assigned exercise to client
 */
public class ClientExerciseLibrary extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_client_exercise_library";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_CLIENT_FIRST_NAME = "example.com.hometherapy.CLIENT_FIRST_NAME";

    // Keys for extra message to pass elements of exercise from library to add library
    public static final String MSG_ADD_OR_EDIT = "example.com.hometherapy.ADD_OR_EDIT";
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISE_NAME";
    public static final String MSG_MODALITY = "example.com.hometherapy.MODALITY";
    public static final String MSG_DISCIPLINE = "example.com.hometherapy.DISCIPLINE";
    public static final String MSG_ASSIGNMENT = "example.com.hometherapy.ASSIGNMENT";
    public static final String MSG_VIDEO_LINK = "example.com.hometherapy.VIDEO_LINK";

    // member variables
    private ExerciseList _currentExercises;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<Exercise> _tempExerciseList;
    private String _currentUserEmail;
    private String _clientFirstName;

    // views
    private ExerciseListAdapter _adapterExerciseList;
    private TextView _tvCELLabel;
    private ListView _lvCELExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercise_library);

        // initialize view widgets
        _lvCELExerciseList = (ListView) findViewById(R.id.lvCELExerciseList);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonExerciseList = _sharedPreferences.getString(EXERCISE_DATA, "");

        // get user email (i.e. account) from extra message
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        _clientFirstName = thisIntent.getStringExtra(MSG_CLIENT_FIRST_NAME);
        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON user database into List of Exercises
        _currentExercises = _gson.fromJson(jsonExerciseList, ExerciseList.class);

        Log.d(TAG, " _currentExercises: " + _currentExercises);

        // if current exercise database is not empty, then proceed with getting list of exercises
        // and binding to array adapter
        if (_currentExercises != null) {

            _tempExerciseList = _currentExercises.getExerciseList();

            Log.d(TAG, "tempExerciseList: " + _tempExerciseList);

            // initialize array adapter and bind exercise list to it
            _adapterExerciseList = new ExerciseListAdapter(this, _tempExerciseList);

            Log.d(TAG, "after adapter is defined");

            // set the adapter to the list view
            _lvCELExerciseList.setAdapter(_adapterExerciseList);

            Log.d(TAG, "after adapter is set");

            // set on item click listener
            _lvCELExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "on ITEM CLICK listener 1");

                    // get current exercise that is clicked on by user by its position
                    Exercise currentExercise = _tempExerciseList.get(position);

                    Log.d(TAG, "on ITEM CLICK listener 2");

                    // intent to go to add exercise to client activity
                    Intent intentAETC = new Intent(ClientExerciseLibrary.this, AddExerciseToClient.class);
                    intentAETC.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                    intentAETC.putExtra(MSG_CLIENT_FIRST_NAME, _clientFirstName);
                    intentAETC.putExtra(MSG_ADD_OR_EDIT, "add");
                    intentAETC.putExtra(MSG_EXERCISE_NAME, currentExercise.get_exerciseName());
                    intentAETC.putExtra(MSG_ASSIGNMENT, currentExercise.get_assignment());
                    intentAETC.putExtra(MSG_DISCIPLINE, currentExercise.get_discipline());
                    intentAETC.putExtra(MSG_MODALITY, currentExercise.get_modality());
                    intentAETC.putExtra(MSG_VIDEO_LINK, currentExercise.get_videoLink());

                    Log.d(TAG, "on ITEM CLICK listener 3");

                    // move to add exercise to client activity
                    startActivity(intentAETC);

                    Log.d(TAG, "on ITEM CLICK listener 4");
                }
            });

        }

    }
}
