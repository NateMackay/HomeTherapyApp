package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

/**
 * Client's view of a given assigned exercise. Clients navigate to this
 * view when they click on an assigned exercise from their dashboard,
 * MyExercises {@link MyExercises}.
 * This view is where the client can mark the exercise as complete.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MyExercise extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_my_exercise";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";
    public static final String USER_DATA = "userData";

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
    public static final String MSG_MY_POINTS = "example.com.hometherapy.MY_POINTS";

    // constant for setting status to complete when button is clicked
    private static final String COMPLETED = "complete";

    // member variables
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private AssignedExerciseList _currentAssignedExercises;
    private List<AssignedExercise> _tempAssignedExerciseList;
    private AssignedExercise _currentAssignedExercise;
    private UserList _currentUsers;
    private List<User> _tempUserList;
    private User _currentUser;
    private String _assignment;
    private String _exercise;
    private String _discipline;
    private String _modality;
    private String _linkToVideo;
    private Integer _pointValue;
    private String _status;
    private Integer _myPoints;
    private Integer _assignedExerciseID;

    // views
    private TextView _tvMyAssignment;
    private TextView _tvMyExercise;
    private TextView _tvMyDiscipline;
    private TextView _tvMyModality;
    private TextView _tvLinkToVideo;
    private Button _btnMyComplete;
    private Button _btnMyExercises;
    private TextView _myEPointValue;
    private TextView _tvMyEStatus;
    private TextView _tvWellDoneMSG;

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
        _btnMyComplete = (Button) findViewById(R.id.btnComplete);
        _btnMyExercises = (Button) findViewById(R.id.btnGoToExercises);
        _myEPointValue = (TextView) findViewById(R.id.tvMyEPointValue);
        _tvMyEStatus = (TextView) findViewById(R.id.tvMyEStatus);
        _tvWellDoneMSG = (TextView) findViewById(R.id.tvWellDoneMsg);

        // open up database for given assigned exercise, and current user. (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

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

        // Get User account to update user rewards, by,
        // deserializing sharedPrefs JSON user database into List of Users
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);

        if (_currentUsers != null) {
            List<User> tempUserList = _currentUsers.getUserList();

            // find matching user account by email
            // reference: section 3.5 on this page https://www.baeldung.com/find-list-element-java
            _currentUser = tempUserList.stream()
                    .filter(user -> _currentUserEmail.equals(user.getEmail()))
                    .findAny()
                    .orElse(null);

            // Pull myPoints from current User account
            _myPoints = _currentUser.get_myPoints();
            if (_myPoints == null) {
                _myPoints = 0;
            }

            Log.d(TAG, "User: " + _currentUser.getFirstName()
                    + " has current rewards total of " + _currentUser.get_myPoints() + ".");
        }

        // get ID of assigned exercise from intent
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
            _status = _currentAssignedExercise.get_status();

            // if assignment has been completed already, mark complete
            if (_status.equals(COMPLETED)) {
                hasCompleted();
            }
        }

        Log.d(TAG, "User: " + _currentUser.getFirstName()
                + " point value of current exercise is " + _pointValue + ".");

        // set view text values
        _tvMyAssignment.setText(_assignment);
        _tvMyExercise.setText(_exercise);
        _tvMyDiscipline.setText(_discipline);
        _tvMyModality.setText(_modality);
        _tvLinkToVideo.setText(_linkToVideo);
        _myEPointValue.setText(Integer.toString(_pointValue));
        _tvMyEStatus.setText(_status);

        // listener for Completed Exercises button
        _btnMyComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Complete button was clicked so update rewards
                _currentUser.set_myPoints(_myPoints + _pointValue);
                Log.d(TAG, "User: " + _currentUser.getFirstName()
                        + " Rewards after completion is " + _currentUser.get_myPoints() + ".");

                // Update user assigned exercise status
                _currentAssignedExercise.set_status(COMPLETED);
                Log.d(TAG, "User: " + _currentUser.getFirstName()
                        + " Exercise status is " + _currentAssignedExercise.get_status() + ".");

                // convert updated assigned exercise list back to JSON format
                String updatedAssignedExerciseList = _gson.toJson(_currentAssignedExercises);

                // convert updated UserList object back to JSON format
                String updatedUserList = _gson.toJson(_currentUsers);

                _currentUser.set_status(COMPLETED);
                Log.d(TAG, "2nd time, User: " + _currentUser.getFirstName()
                        + " Exercise status is " + _currentUser.get_status() + ".");

                // update Shared Prefs
                SharedPreferences.Editor editor = _sharedPreferences.edit();
                editor.putString(ASSIGNED_EXERCISE_DATA, updatedAssignedExerciseList);
                editor.putString(USER_DATA, updatedUserList);
                editor.apply();

                // display contents for testing purposes
                String fromAEDSharedPrefs = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");
                Log.d(TAG, "fromSharedPrefs (Assigned Exercises): " + fromAEDSharedPrefs);
                // display contents for testing purposes
                String fromUSharedPrefs = _sharedPreferences.getString(USER_DATA, "");
                Log.d(TAG, "fromSharedPrefs (Users): " + fromUSharedPrefs);

                // set the status to complete
                hasCompleted();
            }
        });

        // listener for Back to My Exercises button
        _btnMyExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClientExercises = new Intent(MyExercise.this, MyExercises.class);
                intentClientExercises.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                startActivity(intentClientExercises);
            }
        });
    }

    /**
     * If assigned exercise is already completed, then make the below changes to the view when called.
     */
    public void hasCompleted() {
        _btnMyComplete.setText("Completed");
        _btnMyComplete.setEnabled(false);
    }

}

