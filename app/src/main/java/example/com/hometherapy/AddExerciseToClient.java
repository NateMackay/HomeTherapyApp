package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class AddExerciseToClient extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_add_exercise_to_client";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    private String _currentUserEmail;

    // Keys for extra message to pass elements of exercise from library to add library
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISE_NAME";
    public static final String MSG_MODALITY = "example.com.hometherapy.MODALITY";
    public static final String MSG_DISCIPLINE = "example.com.hometherapy.DISCIPLINE";
    public static final String MSG_ASSIGNMENT = "example.com.hometherapy.ASSIGNMENT";
    public static final String MSG_VIDEO_LINK = "example.com.hometherapy.VIDEO_LINK";

    // member variables
    private TextView _tvAETCAssignment;
    private TextView _tvAETCExercise;
    private TextView _tvAETCDiscipline;
    private TextView _tvAETCModality;
    private TextView _tvLinkToVideo;
    private Button _btnAETCSave;
    private Spinner _spinAETCPointValue;
    private Spinner _spinAETCStatus;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private AssignedExerciseList _currentAssignedExercises;
    private List<AssignedExercise> _tempAssignedExerciseList;

    // array adapter for spinner views
    private String _exerciseStatusNames[] = {"Status", "not started", "started", "complete", "on hold", "inactive"};
    private Integer _pointValueOptions[] = {5, 10, 15, 20, 25};
    private ArrayAdapter<String> _adapterStatus;
    private ArrayAdapter<Integer> _adapterPointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_client);

        // register views
        _tvAETCAssignment = (TextView) findViewById(R.id.tvAETCAssignment);
        _tvAETCExercise = (TextView) findViewById(R.id.tvAETCExercise);
        _tvAETCDiscipline = (TextView) findViewById(R.id.tvAETCDiscipline);
        _tvAETCModality = (TextView) findViewById(R.id.tvAETCModality);
        _tvLinkToVideo = (TextView) findViewById(R.id.tvLinkToVideo);
        _btnAETCSave = (Button) findViewById(R.id.btnAETCSave);
        _spinAETCPointValue = (Spinner) findViewById(R.id.spinAETCPointValue);
        _spinAETCStatus = (Spinner) findViewById(R.id.spinAETCStatus);

        // adapters
        _adapterPointValue = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _pointValueOptions);
        _adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _exerciseStatusNames);

        // set adapters
        _spinAETCPointValue.setAdapter(_adapterPointValue);
        _spinAETCStatus.setAdapter(_adapterStatus);

        // open up database for given assigned exercise (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON assigned exercise database into List of Assigned Exercises
        _currentAssignedExercises = _gson.fromJson(jsonAssignedExerciseList, AssignedExerciseList.class);

        // if this is add only, then we don't need to filter for both user and assigned exercise title
        // we can just filter for user
        // actually you don't need to do that
        // you don't need to pull data in
        // you just need to open up the list of assigned exercises
        // then create an assigned exercise instance of all of the data elements that will go into it
        // then you just need to add it to the list
        // and then serialize it using JSON
        // and then store it in the shared prefs
        // and then do an intent back to the list of client exercises

        // however, we need to address when and how we're going to edit an existing exercise
        // if we're going to use this same activity
        // then we have to figure out how to filter for both user id and for title
        // can the filter method handle more than one option? maybe - AddEditUser.java lambda expression
        // used to filter for user email


        // get user email (i.e. account) from extra message
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify current user: " + _currentUserEmail);




    }




}
