package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.stream.Collectors;

public class MyExercises extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "My_Exercises_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_ADD_OR_EDIT = "example.com.hometherapy.ADD_OR_EDIT";
    private String _currentUserEmail;

    // member variables
    private AssignedExerciseList _assignedExercises;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<AssignedExercise> _tempAssignedExerciseList;
    private List<AssignedExercise> _filteredList;
    private boolean isClient;

    // views
    //private ArrayAdapter<AssignedExercise> _adapter; // add custom adapter
    private ListView _lvMEAssignedExercises;
    private TextView _tvMELabel;

    // array adapter for exercise list
    private AssignedExerciseListAdapter _adapterAssignedExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* commenting this out gets rid of the floating email icon
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        // register views
        _lvMEAssignedExercises = (ListView) findViewById(R.id.lvMEAssignedExercises);
        _tvMELabel = (TextView) findViewById(R.id.tvMELabel);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // temporarily clear out ASSIGNED_EXERCISE_DATA
//        SharedPreferences.Editor editor = _sharedPreferences.edit();
//        editor.putString(ASSIGNED_EXERCISE_DATA, "");
//        editor.apply();

        String jsonAssignedExerciseList = _sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

        Log.d(TAG, "jsonAssignedExerciseList: " + jsonAssignedExerciseList);

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
            _adapterAssignedExerciseList = new AssignedExerciseListAdapter(this, _filteredList);

            // set the adapter to the list view
            _lvMEAssignedExercises.setAdapter(_adapterAssignedExerciseList);

            // if the user clicks on an existing exercise, we want to take the user to the add/edit
            // exercise to client activity where they can view and edit the exercise
            // only issue - we only want the following functionality if the user type is admin
            // or therapist
            // if a client clicks on an exercise, then he/she will be taken to the my exercise view
            // where the client can mark an exercise as complete
            _lvMEAssignedExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // get current assigned exercise from position in the list
                    AssignedExercise selectedExercise = _filteredList.get(position);

                    Log.d(TAG, "selected Exercise: " + selectedExercise);

                    Log.d(TAG, "_currentUserEmail: " + _currentUserEmail);

                    Log.d(TAG, "selectedExercise.get_assignedUserEmail(): " + selectedExercise.get_assignedUserEmail());

                    Log.d(TAG, "selectedExercise.get_assignedExerciseID().toString(): " +
                            selectedExercise.get_assignedExerciseID().toString());

                    // need to add logic that will take the client to view their exercise
                    // and mark complete - this is the myExercise view
                    // add if client is account type, go to specific exercise view
                    // need to wait until you have the therapist view, which is a list of clients
                    // which will take the therapist to a view of their clients that they can then
                    // click on and then add / edit exercises
                    // for now, if you are in the client list of exercises, it is from the standpoint
                    // of a therapist or admin looking at that client's exercises, with the ability
                    // to add/edit exercises.

                    // the following is for therapists and admin users only
                    // intent to go to Add(Edit) Exercise to Client
                    // need to pass an intent that has the user ID as well as the assigned exercise ID
                    Intent intentAETC = new Intent(MyExercises.this, MyExercise.class);
                    intentAETC.putExtra(MSG_ADD_OR_EDIT, "edit");
                    intentAETC.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                    intentAETC.putExtra(MSG_ASSIGNED_EXERCISE_ID, selectedExercise.get_assignedExerciseID().toString());
                    startActivity(intentAETC);
                }
            });
        }

        // add an exercise button - this will pass user email to a Exercises
        /*_btnCEAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Exercises screen, passing user via extra message
                Intent intentClientExerciseLibrary = new Intent(MyExercises.this, ClientExerciseLibrary.class);
                intentClientExerciseLibrary.putExtra(MSG_USER_EMAIL, _currentUserEmail);
                startActivity(intentClientExerciseLibrary);
            }
        });

        // on click, just go back to signIn screen
        // for testing purposes
        _btnCEUserLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = new Intent(MyExercises.this, SignIn.class);
                startActivity(intentSignIn);
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_exercises, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // commenting this out gets rid of the kebab icon
        //noinspection SimplifiableIfStatement
/*
        if (id == R.id.action_settings) {
            return true;
        }
*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_myExercises) {
            Intent intentMessage = new Intent(MyExercises.this, MyExercises.class);
            startActivity(intentMessage);
        } else */
        if (id == R.id.nav_myRewards) {
            Intent intentRewards = new Intent(MyExercises.this, MyRewards.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyExercises.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myProfile) {
            Intent intentProfile = new Intent(MyExercises.this, MyProfile.class);
            startActivity(intentProfile);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
