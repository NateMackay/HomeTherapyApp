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

    // Keys for extra messages
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";

    // constant for setting status to complete when button is clicked
    private static final String COMPLETED = "complete";

    // member variables
    private String _currentUserEmail;
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
    private TextView _tvMyEPointValue;
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
        _tvMyEPointValue = (TextView) findViewById(R.id.tvMyEPointValue);
        _tvMyEStatus = (TextView) findViewById(R.id.tvMyEStatus);
        _tvWellDoneMSG = (TextView) findViewById(R.id.tvWellDoneMsg);

        // set default value for myPoints
        _myPoints = 0;

        // open up database for given assigned exercise, and current user. (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // get assigned exercise ID and user email/ID passed from MyExercises.java
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        _assignedExerciseID = Integer.parseInt(thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID));

        // verify intent messages
        Log.d(TAG, "verify current user: " + _currentUserEmail);
        Log.d(TAG, "verify current assigned exercise ID: " + _assignedExerciseID);

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON assigned exercise and users databases
        _currentAssignedExercises = _gson.fromJson(jsonAssignedExerciseList, AssignedExerciseList.class);
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);

        // if assigned exercise database is empty, then log error and return to sign-in screen
        if (_currentAssignedExercises == null) {

            Log.e(TAG, "error: _currentAssignedExercises is null");
            Intent signInIntent = new Intent(MyExercise.this, SignIn.class);
            startActivity(signInIntent);
        }

        // if user database is empty, then log error and return to sign-in screen
        if (_currentUsers == null) {

            Log.e(TAG, "error: _currentUsers is null");
            Intent signInIntent = new Intent(MyExercise.this, SignIn.class);
            startActivity(signInIntent);
        }

        // pull user list from UserList object and get current user object based on user account
        // passed in via intent
        if (_currentUsers != null) {
            List<User> tempUserList = _currentUsers.getUserList();

            // find matching user account by email account
            // reference: section 3.5 on this page https://www.baeldung.com/find-list-element-java
            _currentUser = tempUserList.stream()
                    .filter(user -> _currentUserEmail.equals(user.getEmail()))
                    .findAny()
                    .orElse(null);

            if (_currentUser != null) {

                // get current point value of user
                _myPoints = _currentUser.get_myPoints();

                Log.d(TAG, "User: " + _currentUser.getFirstName()
                        + " has current rewards total of " + _myPoints + ".");
            }
        }

        // get assigned exercise information from database given assigned exercise ID passed
        // in via intent
        if (_currentAssignedExercises != null) {

            // get list of assigned exercises from assigned exercise list object
            _tempAssignedExerciseList = _currentAssignedExercises.getAssignedExerciseList();
            Log.d(TAG, "tempExerciseList: " + _tempAssignedExerciseList);

            // get assigned exercise object with exercise ID passed in from intent
            _currentAssignedExercise = _tempAssignedExerciseList.stream()
                    .filter(assignedExercise -> _assignedExerciseID.equals(assignedExercise.get_assignedExerciseID()))
                    .findAny()
                    .orElse(null);

            // get assigned exercise values and set views to those values
            if (_currentAssignedExercise != null) {
                _assignment = _currentAssignedExercise.get_assignment();
                _exercise = _currentAssignedExercise.get_exerciseName();
                _discipline = _currentAssignedExercise.get_discipline();
                _modality = _currentAssignedExercise.get_modality();
                _linkToVideo = _currentAssignedExercise.get_videoLink();
                _pointValue = _currentAssignedExercise.get_pointValue();
                _status = _currentAssignedExercise.get_status();

                // set text view values
                _tvMyAssignment.setText(_assignment);
                _tvMyExercise.setText(_exercise);
                _tvMyDiscipline.setText(_discipline);
                _tvMyModality.setText(_modality);
                _tvLinkToVideo.setText(_linkToVideo);
                _tvMyEPointValue.setText(Integer.toString(_pointValue));
                _tvMyEStatus.setText(_status);

                // if assignment has been completed already, mark complete
                if (_status.equals(COMPLETED)) {
                    hasCompleted();
                }
            }
        }

        // listener for Completed Exercises button
        _btnMyComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Complete button was clicked so update rewards
                if (_currentUser != null && _currentAssignedExercise != null) {

                    // Call addToRewardsTotal() to update rewards of
                    // current user (_currentUser), by the point value amount
                    // (_pointValue) of the current exercise.
                    addToRewardsTotal(_currentUser, _pointValue);
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
     * This method will update the current user rewards total, by the point value of the
     * assigned exercise.
     *
     * @param currentUser - The current user.
     * @param pointValue  - The point value of the assigned exercise, i.e. the current
     *                    exercise being viewed.
     */
    private static void addToRewardsTotal(User currentUser, Integer pointValue) {

        // Store the sum of the user's current total rewards point and point value of the
        // exercise in an Integer variable.
        Integer newMyPoints = currentUser.get_myPoints() + pointValue;

        // Set the user's reward points to the value of the the above integer variable.
        currentUser.set_myPoints(newMyPoints);
    }

    /**
     * When called, this method will test if the addition logic in addToRewardsTotal()
     * works as intended.
     *
     * @param currentUser - The current user.
     * @param pointValue - The point value of the assigned exercise, i.e. the current
     *                   exercise being viewed.
     * @return - Returns true if addition logic in addToRewardsTotal() works correctly.
     */
    public static boolean doesAddCorrectly(User currentUser, Integer pointValue) {
        // Calculate rewards before call to addToRewardsTotal() and store in Integer.
        Integer testEquals = currentUser.get_myPoints() + pointValue;

        // Call addToRewards to update user's rewards
        addToRewardsTotal(currentUser, pointValue);

        // Should return true, to indicate that logic in addToRewardsTotal works as intended.
        return testEquals.equals(currentUser.get_myPoints());
    }

    /**
     * If assigned exercise is already completed, then make the below changes to the view when called.
     */
    public void hasCompleted() {
        // set text and enabled status so that round_button.xml file, where shape properties are set
        // can be applied accordingly
        // references:
        // https://developer.android.com/guide/topics/ui/controls/button
        // https://www.journaldev.com/19850/android-button-design-custom-round-color (view in Chrome)
        // https://developer.android.com/guide/topics/resources/drawable-resource#Shape
        _btnMyComplete.setText("Completed");
        _btnMyComplete.setEnabled(false);
    }

}

