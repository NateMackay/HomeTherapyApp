package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    public static final String MSG_ADD_OR_EDIT = "example.com.hometherapy.ADD_OR_EDIT";
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
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
    private String _assignment;
    private String _exercise;
    private String _discipline;
    private String _modality;
    private String _linkToVideo;
    private boolean _isIntentAdd;
    private Integer _assignedExerciseID;

    // array adapter for spinner views
    private String _exerciseStatusNames[] = {"Status", "not started", "started", "complete", "on hold", "inactive"};
    private String _pointValueOptions[] = {"5", "10", "15", "20", "25"};
    private ArrayAdapter<String> _adapterStatus;
    private ArrayAdapter<String> _adapterPointValue;

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

        // if no list has been created yet, create a new assigned exercise list
        if (_currentAssignedExercises == null) {
            _currentAssignedExercises = new AssignedExerciseList();
            Log.d(TAG, "tempExerciseList: " + _tempAssignedExerciseList);
        }

        // get list of assigned exercises from assigned exercise list object
        _tempAssignedExerciseList = _currentAssignedExercises.getAssignedExerciseList();
        Log.d(TAG, "tempExerciseList: " + _tempAssignedExerciseList);


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

        // get exercise data passed from client exercise library
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);

        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // depending on whether intent is to either add or edit a client exercise
        if (thisIntent.getStringExtra(MSG_ADD_OR_EDIT) == "add") {
            _isIntentAdd = true;
            _assignment = thisIntent.getStringExtra(MSG_ASSIGNMENT);
            _exercise = thisIntent.getStringExtra(MSG_EXERCISE_NAME);
            _discipline = thisIntent.getStringExtra(MSG_DISCIPLINE);
            _modality = thisIntent.getStringExtra(MSG_MODALITY);
            _linkToVideo = thisIntent.getStringExtra(MSG_VIDEO_LINK);

            // assign a new exercise ID
            // build in a function to do this, but for now, build it here
            _assignedExerciseID = 10;

        } else {
            _isIntentAdd = false;
            _assignedExerciseID = Integer.parseInt(thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID));

            // put logic here where we are only needing to look up an exercise ID
            // we also need to get the values for the text views and spinners, etc. in here
            // set the spinners here as well, and set the other text values below

        }

        // may want to add the set text values differently

        // set view text values
        _tvAETCAssignment.setText(_assignment);
        _tvAETCExercise.setText(_exercise);
        _tvAETCDiscipline.setText(_discipline);
        _tvAETCModality.setText(_modality);
        _tvLinkToVideo.setText(_linkToVideo);

        // listener for save changes button
        _btnAETCSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add new exercise to the assigned exercise list object
                _currentAssignedExercises.addAssignedExercise(new AssignedExercise(_exercise, _discipline,
                        _modality, _assignment, _linkToVideo, _currentUserEmail,
                        Integer.parseInt(_spinAETCPointValue.getSelectedItem().toString()),
                        _spinAETCStatus.getSelectedItem().toString(), false, _assignedExerciseID));

                // convert updated assigned exercise list back to JSON format
                String updatedList = _gson.toJson(_currentAssignedExercises);

                // update Shared Prefs
                SharedPreferences.Editor editor = _sharedPreferences.edit();
                editor.putString(ASSIGNED_EXERCISE_DATA, updatedList);
                editor.apply();

                // display contents for testing purposes
                String fromSharedPrefs = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");
                Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);

                Intent intentClientExercises = new Intent(AddExerciseToClient.this, ClientExercises.class);
                intentClientExercises.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                startActivity(intentClientExercises);

            }
        });

    }

}
