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
import java.util.stream.Collectors;

/**
 * Therapist view of client's assigned exercises. Intent only comes
 * from MyClients {@link MyClients}, which is the therapist's view
 * of their clients. When therapist selects a client from MyClients,
 * it takes you to this view.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link MyClients}
 */
public class ClientExercises extends AppCompatActivity {

    // for log
    private static final String TAG = "Client_Exercises_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_ADD_OR_EDIT = "example.com.hometherapy.ADD_OR_EDIT";
    public static final String MSG_CLIENT_FIRST_NAME = "example.com.hometherapy.CLIENT_FIRST_NAME";
    private String _currentUserEmail;
    private String _clientFirstName;

    // member variables
    private AssignedExerciseList _assignedExercises;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<AssignedExercise> _tempAssignedExerciseList;
    private List<AssignedExercise> _filteredList;

    // views
    private AssignedExerciseListAdapter _adapterAssignedExercises;
    private ListView _lvCEAssignedExercises;
    private Button _btnCEAddExercise;
    private TextView _tvCELabel;
    private Button _btnCEUserLogOut;
    private Button _btnCEReturnToMyClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercises);

        // register views
        _lvCEAssignedExercises = (ListView) findViewById(R.id.lvCEAssignedExercises);
        _btnCEAddExercise = (Button) findViewById(R.id.btnCEAddExercise);
        _tvCELabel = (TextView) findViewById(R.id.tvCELabel);
        _btnCEUserLogOut = (Button) findViewById(R.id.btnCEUsersLogOut);
        _btnCEReturnToMyClients = (Button) findViewById(R.id.btnCEReturnToMyClients);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

        Log.d(TAG, "jsonAssignedExerciseList: " + jsonAssignedExerciseList);

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account) from extra message
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        _clientFirstName = thisIntent.getStringExtra(MSG_CLIENT_FIRST_NAME);
        Log.d(TAG, "verify current user: " + _currentUserEmail);
        Log.d(TAG, "verify client first name: " + _clientFirstName);

        // set title

        String nameTitle = _clientFirstName + "'s Exercises";
        _tvCELabel.setText(nameTitle);

        // deserialize sharedPrefs JSON assigned exercises database into List of Assigned Exercises
        _assignedExercises = _gson.fromJson(jsonAssignedExerciseList, AssignedExerciseList.class);

        Log.d(TAG, "_assignedExercises: " + _assignedExercises);

        // if current assigned exercise database is not empty, then proceed with getting list of
        // assigned exercises and binding to array adapter
        if (_assignedExercises != null) {

            _tempAssignedExerciseList = _assignedExercises.getAssignedExerciseList();

            Log.d(TAG, "list before filtering: " + _tempAssignedExerciseList);

            // filter here to only show those exercises that are assigned to the user email
            // Reference: https://www.javabrahman.com/java-8/java-8-filtering-and-slicing-streams-tutorial-with-examples/
            _filteredList = _tempAssignedExerciseList.stream()
                    .filter(assignedExercise -> _currentUserEmail.equals(assignedExercise.get_assignedUserEmail()))
                    .collect(Collectors.toList());

            Log.d(TAG, "list after filtering: " + _filteredList);

            // initialize array adapter and bind exercise list to it
            // the view will need to be different depending upon whether the user type is
            // a client or a therapist/admin
            _adapterAssignedExercises = new AssignedExerciseListAdapter(this, _filteredList);

            // set the adapter to the list view
            _lvCEAssignedExercises.setAdapter(_adapterAssignedExercises);

            // if the user clicks on an existing exercise, we want to take the user to the add/edit
            // exercise to client activity where they can view and edit the exercise
            // only issue - we only want the following functionality if the user type is admin
            // or therapist
            // if a client clicks on an exercise, then he/she will be taken to the my exercise view
            // where the client can mark an exercise as complete
            _lvCEAssignedExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // get current assigned exercise from position in the list
                    AssignedExercise selectedExercise = _filteredList.get(position);

                    // the following is for therapists and admin users only
                    // intent to go to Add(Edit) Exercise to Client
                    // need to pass an intent that has the user ID as well as the assigned exercise ID
                    Intent intentAETC = new Intent(ClientExercises.this, AddExerciseToClient.class);
                    intentAETC.putExtra(MSG_ADD_OR_EDIT, "edit");
                    intentAETC.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                    intentAETC.putExtra(MSG_ASSIGNED_EXERCISE_ID, selectedExercise.get_assignedExerciseID().toString());
                    intentAETC.putExtra(MSG_CLIENT_FIRST_NAME, _clientFirstName);
                    startActivity(intentAETC);
                }
            });
        }

        // add an exercise button - this will pass user email to a Exercises
        _btnCEAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Exercises screen, passing user via extra message
                Intent intentClientExerciseLibrary = new Intent(ClientExercises.this, ClientExerciseLibrary.class);
                intentClientExerciseLibrary.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                intentClientExerciseLibrary.putExtra(MSG_CLIENT_FIRST_NAME, _clientFirstName);
                startActivity(intentClientExerciseLibrary);
            }
        });

        // return to list of my clients button
        _btnCEReturnToMyClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentMyClients = new Intent(ClientExercises.this, MyClients.class);
                startActivity(intentMyClients);
            }
        });

        // on click, just go back to signIn screen
        // for testing purposes
        _btnCEUserLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = new Intent(ClientExercises.this, SignIn.class);
                startActivity(intentSignIn);
            }
        });

    }
}
