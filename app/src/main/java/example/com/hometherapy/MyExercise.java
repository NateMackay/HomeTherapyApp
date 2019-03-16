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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Client view of the selected assigned exercise.
 */
public class MyExercise extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_my_exercise";

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
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private AssignedExerciseList _currentAssignedExercises;
    private List<AssignedExercise> _tempAssignedExerciseList;
    private AssignedExercise _currentAssignedExercise;
    private String _assignment;
    private String _exercise;
    private String _discipline;
    private String _modality;
    private String _linkToVideo;
    private int _pointValue;
    private String _status;

    private boolean _isIntentAdd;
    private Integer _assignedExerciseID;

    // views
    private TextView _tvMyAssignment;
    private TextView _tvMyExercise;
    private TextView _tvMyDiscipline;
    private TextView _tvMyModality;
    private TextView _tvLinkToVideo;
    private Button _btnMyComplete;
    private TextView _myEPointValue;
    private Spinner _spinMyEStatus;

    // array adapter for spinner views
    private String _exerciseStatusNames[] = {"Status", "not started", "started", "complete", "on hold", "inactive"};
    private String _pointValueOptions[] = {"5", "10", "15", "20", "25"};
    private ArrayAdapter<String> _adapterStatus;
    //private ArrayAdapter<String> _adapterPointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exercise);

        // register views
        _tvMyAssignment = (TextView) findViewById(R.id.tvMyEAssignment);
        _tvMyExercise = (TextView) findViewById(R.id.tvMyExercise);
        _tvMyDiscipline = (TextView) findViewById(R.id.tvMyDiscipline);
        _tvMyModality = (TextView) findViewById(R.id.tvMyModality);
        _tvLinkToVideo = (TextView) findViewById(R.id.tvLinkToVideo);
        _btnMyComplete = (Button) findViewById(R.id.btnMyComplete);
        _myEPointValue = (TextView) findViewById(R.id.myEPointValue);
        _spinMyEStatus = (Spinner) findViewById(R.id.spinMyEStatus);

        // adapters
        //_adapterPointValue = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _pointValueOptions);
        _adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _exerciseStatusNames);

        // set adapters
        //_myEPointValue.setAdapter(_adapterPointValue);
        _spinMyEStatus.setAdapter(_adapterStatus);

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
        }

        // get list of assigned exercises from assigned exercise list object
        _tempAssignedExerciseList = _currentAssignedExercises.getAssignedExerciseList();
        Log.d(TAG, "tempExerciseList: " + _tempAssignedExerciseList);

        // get exercise data passed from client exercise library
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);

        Log.d(TAG, "verify current user: " + _currentUserEmail);

        /**
        // depending on whether intent is to either add or edit a client exercise
        // if intent is to add, then get values from intent passed in from client exercise library
        if (thisIntent.getStringExtra(MSG_ADD_OR_EDIT).equals("add")) {
            _isIntentAdd = true;
            _assignment = thisIntent.getStringExtra(MSG_ASSIGNMENT);
            _exercise = thisIntent.getStringExtra(MSG_EXERCISE_NAME);
            _discipline = thisIntent.getStringExtra(MSG_DISCIPLINE);
            _modality = thisIntent.getStringExtra(MSG_MODALITY);
            _linkToVideo = thisIntent.getStringExtra(MSG_VIDEO_LINK);

            // assign a new exercise ID
            // find maximum value of _assignedExerciseID within the list of assigned exercises
            // reference: https://stackoverflow.com/questions/19338686/java-getting-max-value-from-an-arraylist-of-objects
            // reference: https://dzone.com/articles/optional-ispresent-is-bad-for-you
            // if id is not present, for example if this is the first assigned exercise
            // then the assigned ID will be the default value of 0 + 1
            Integer maxID = 0;
            if (_tempAssignedExerciseList.stream()
                    .max(Comparator.comparing(AssignedExercise::get_assignedExerciseID)).isPresent()) {
                AssignedExercise assignedExerciseWithMaxID = _tempAssignedExerciseList.stream()
                        .max(Comparator.comparing(AssignedExercise::get_assignedExerciseID)).get();
                maxID = assignedExerciseWithMaxID.get_assignedExerciseID();
            }

            Log.d(TAG, "Verify max ID: " + maxID);

            // set ID for new assigned exercise to maxID plus one
            _assignedExerciseID = maxID + 1;

        } else { **/
            // if intent is to view/edit, not add, then lookup assigned exercise in exercise db
            // based on assigned exercise ID, and set the text views and spinners accordingly
            _isIntentAdd = false;
            _assignedExerciseID = Integer.parseInt(thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID));

            // get assigned exercise object with exercise ID passed in from intent
            _currentAssignedExercise = _tempAssignedExerciseList.stream()
                    .filter(assignedExercise -> _assignedExerciseID.equals(assignedExercise.get_assignedExerciseID()))
                    .findAny()
                    .orElse(null);

            // get assigned exercise values
            if (_currentAssignedExercise != null) {
                _assignment = _currentAssignedExercise.get_assignment();
                _exercise = _currentAssignedExercise.get_exerciseName();
                _discipline = _currentAssignedExercise.get_discipline();
                _modality = _currentAssignedExercise.get_modality();
                _linkToVideo = _currentAssignedExercise.get_videoLink();
                _pointValue = _currentAssignedExercise.get_pointValue();
                //_status = _currentAssignedExercise.get_status();
            }

            // set status and point value spinners based on what is in assigned exercise
            // first, get indices of these values
            //int iSpinAETCPointValue = Arrays.asList(_pointValueOptions).indexOf(_currentAssignedExercise.get_pointValue().toString());
            int iSpinAETCStatus = Arrays.asList(_exerciseStatusNames).indexOf(_currentAssignedExercise.get_status());

            // set spinner values based on current assigned exercise
            // note that a value of -1 means indexOf() did not find value searching for
            //if (iSpinAETCPointValue >= 0) {
            //    _myEPointValue.setSelection(iSpinAETCPointValue);
            //}

            if (iSpinAETCStatus >= 0) {
                _spinMyEStatus.setSelection(iSpinAETCStatus);
            }
        //}

        // set view text values
        _tvMyAssignment.setText(_assignment);
        _tvMyExercise.setText(_exercise);
        _tvMyDiscipline.setText(_discipline);
        _tvMyModality.setText(_modality);
        _tvLinkToVideo.setText(_linkToVideo);
        _myEPointValue.setText(Integer.toString(_pointValue));

        //
        _spinMyEStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString(); //this is your selected item

                if (selectedItem == _exerciseStatusNames[3]) {
                    Toast.makeText(MyExercise.this, "Well Done.", Toast.LENGTH_SHORT).show();
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // we could alternatively set the index value for the spinners in the above if/else statement
        // and then set the spinners here per the index, but I believe the default value is 0
        // which for a new exercise, would be set anyway by the user.

        // listener for save changes button
        _btnMyComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if we are here to add a new exercise,
                // the following adds a new exercise to the list within the AssignedEceriseList object
                //if (_isIntentAdd) {
                //    _currentAssignedExercises.addAssignedExercise(new AssignedExercise(_exercise,
                //            _discipline, _modality, _assignment, _linkToVideo, _currentUserEmail,
                //            Integer.parseInt(_myEPointValue.getSelectedItem().toString()),
                //            _spinMyEStatus.getSelectedItem().toString(), false,
                //            _assignedExerciseID));
                //} else {
                    // if not adding, we are simply viewing / editing
                    // update the _currentAssignedExercise, the individual AssignedExercise object
                    // that we referenced based on assigned exercise ID
                //    _currentAssignedExercise.set_exerciseName(_tvMyExercise.getText().toString());
                //    _currentAssignedExercise.set_assignment(_tvMyAssignment.getText().toString());
                //    _currentAssignedExercise.set_discipline(_tvMyDiscipline.getText().toString());
                //    _currentAssignedExercise.set_modality(_tvMyModality.getText().toString());
                //   _currentAssignedExercise.set_videoLink(_tvLinkToVideo.getText().toString());
                    _currentAssignedExercise.set_status(_spinMyEStatus.getSelectedItem().toString());
                //    _currentAssignedExercise.set_pointValue(Integer.parseInt(_myEPointValue.getSelectedItem().toString()));
                //}

                // convert updated assigned exercise list back to JSON format
                String updatedList = _gson.toJson(_currentAssignedExercises);

                // update Shared Prefs
                SharedPreferences.Editor editor = _sharedPreferences.edit();
                editor.putString(ASSIGNED_EXERCISE_DATA, updatedList);
                editor.apply();

                // display contents for testing purposes
                String fromSharedPrefs = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");
                Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);

                Intent intentClientExercises = new Intent(MyExercise.this, MyExercises.class);
                intentClientExercises.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                startActivity(intentClientExercises);

            }
        });

    }

}

