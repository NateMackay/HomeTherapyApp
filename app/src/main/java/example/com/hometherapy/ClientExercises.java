package example.com.hometherapy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * example.com.hometherapy.Therapist view of client's assigned exercises. Intent only comes
 * from MyClients {@link MyClients}, which is the therapist's view
 * of their clients. When therapist selects a client from MyClients,
 * it takes you to this view.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link MyClients}
 */
public class ClientExercises extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "Client_Exercises_Activity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAssignedExercisesRef;
    private DatabaseReference mUsersRef;

    // Key for extra message for user email address to pass to activity
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // member variables
    private String _clientUID;
    private List<AssignedExercise> _tempAssignedExerciseList;

    // adapter for assigned exercise list
    private AssignedExerciseListAdapter _adapterAssignedExercises;

    // views
    private ListView _lvCEAssignedExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _lvCEAssignedExercises = (ListView) findViewById(R.id.lvCEAssignedExercises);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind assigned exercise list to it, then set the adapter
        _tempAssignedExerciseList = new ArrayList<>();
        _adapterAssignedExercises = new AssignedExerciseListAdapter(this, _tempAssignedExerciseList);
        _lvCEAssignedExercises.setAdapter(_adapterAssignedExercises);

        // get client ID from extra message
        Intent thisIntent = getIntent();
        _clientUID = thisIntent.getStringExtra(MSG_CLIENT_UID);
        Log.d(TAG, "verify client ID: " + _clientUID);

        // set up firebase references from database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAssignedExercisesRef = mFirebaseDatabase.getReference().child("assignedExercises");
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        // Add listener for assigned exercise data to pull from firebase and display in listview
        // query so that we are only pulling those exercises assigned to the client
        Query queryAE = mAssignedExercisesRef.orderByChild("_assignedUserID").equalTo(_clientUID);
        queryAE.addListenerForSingleValueEvent(assignedExerciseEventListener);

        // Add listener for client UID so that we can get display name to update label
        Query queryUser = mUsersRef.orderByChild("userID").equalTo(_clientUID);
        queryUser.addListenerForSingleValueEvent(clientUserEventListener);

        // if a client clicks on an exercise, then he/she will be taken to the my exercise view
        _lvCEAssignedExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current assigned exercise from position in the list
                AssignedExercise selectedExercise = _tempAssignedExerciseList.get(position);

                // the following is for therapists only
                // intent to go to Add(Edit) Exercise to Client
                // need to pass an intent that has the user ID as well as the assigned exercise ID
                Intent intentAETC = new Intent(ClientExercises.this, AddExerciseToClient.class);
                intentAETC.putExtra(MSG_ASSIGNED_EXERCISE_ID, selectedExercise.get_assignedExerciseID());
                intentAETC.putExtra(MSG_CLIENT_UID, _clientUID);
                intentAETC.putExtra(MSG_EXERCISE_ID, selectedExercise.get_exerciseID());
                startActivity(intentAETC);
            }
        });

        // navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    } // END onCreate()

    // listener for list of assigned exercises
    // Adding a ValueEventListener to the assigned exercise ID will allow our app to extract
    // assigned exercises assigned to the client.
    ValueEventListener assignedExerciseEventListener = new ValueEventListener() {
        // onDataChange: An event callback method. This method is triggered once when the listener
        // is attached (with the initial value) and again every time the data, including children,
        // changes.
        // DataSnapshot: A DataSnapshot instance contains data from a Firebase Database location,
        // in this case the assigned exercise. Any time you read Database data, you receive the
        // data as a DataSnapshot.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            _tempAssignedExerciseList.clear();
            // is there a dataSnapShot...
            if (dataSnapshot.exists()) {
                // if so, get the assignedExercise data in the snapshot (from Realtime Database)...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // and store in local assignedExercise variable
                    AssignedExercise assignedExercise = snapshot.getValue(AssignedExercise.class);
                    // then add to assigned exercises list (the client's).
                    _tempAssignedExerciseList.add(assignedExercise);
                }
            }
            // update the listView when there is a change
            _adapterAssignedExercises.notifyDataSetChanged();
        }
        // This method is called when onDataChange() fails to read the value.
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END assigned exercises listener

    // listener for to get specific user object associated with passed in client UID
    // Adding a ValueEventListener to the user ID will allow our app to extract
    // the client ID which matches the user ID.
    ValueEventListener clientUserEventListener = new ValueEventListener() {
        // onDataChange: An event callback method. This method is triggered once when the listener
        // is attached (with the initial value) and again every time the data, including children,
        // changes.
        // DataSnapshot: A DataSnapshot instance contains data from a Firebase Database location,
        // in this case the user. Any time you read Database data, you receive the
        // data as a DataSnapshot.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // is there a dataSnapShot...
            if (dataSnapshot.exists()) {
                User user = new User();
                // if so, get the user data in the snapshot (from Realtime Database)...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // and store in local user variable
                    user = snapshot.getValue(User.class);
                }

                // set label to client's first name + "'s Exercises".
                // With correct user info stored in local User object, a call to get the
                // user first name, as store in the database, is possible.
                if (user != null) {
                    ClientExercises.this.setTitle(String.format("%s's Exercises", user.getFirstName()));
                }
            }
        }
        // This method is called when onDataChange() fails to read the value.
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END client user listener

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Navigation menu options
     * Called when an item in the navigation menu is selected.
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myClients) {

            Intent intentMyClients = new Intent(ClientExercises.this, MyClients.class);
            startActivity(intentMyClients);

        } else if (id == R.id.nav_exercise_library) {

            Intent intentExerciseLibrary = new Intent(ClientExercises.this, Exercises.class);
            startActivity(intentExerciseLibrary);

        } else if (id == R.id.nav_assign_exercise_to_client) {

            // intent to go to Exercises screen, passing user and blank assigned exercise ID
            Intent intentClientExerciseLibrary = new Intent(ClientExercises.this, ClientExerciseLibrary.class);
            intentClientExerciseLibrary.putExtra(MSG_ASSIGNED_EXERCISE_ID, "");
            intentClientExerciseLibrary.putExtra(MSG_CLIENT_UID, _clientUID);
            startActivity(intentClientExerciseLibrary);

        } else if (id == R.id.nav_myProfile) {

            Intent intentProfile = new Intent(ClientExercises.this, MyProfile.class);
            startActivity(intentProfile);

        } else if (id == R.id.nav_LogOut) {

            Intent intentLogIn = new Intent(ClientExercises.this, SignIn.class);
            startActivity(intentLogIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    } // END onNavigationItemSelected

} // END Client Exercises class
