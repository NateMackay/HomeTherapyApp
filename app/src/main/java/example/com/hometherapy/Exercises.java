package example.com.hometherapy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import example.com.hometherapy.adapter.ExerciseListAdapter;
import example.com.hometherapy.model.Exercise;
import example.com.hometherapy.model.User;

/**
 * This is the class that is for the exercise library view
 * linked to activity_exercises.xml.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class Exercises extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "ExercisesActivity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mExerciseLibraryRef; // for references to library node
    private DatabaseReference mUsersRef; // for references to users node

    // Key for extra message for exercise name to pass to activity
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // consider passing exercise ID, and not the name, in the intent to addEditExercise.java
    // this is only relevant for an existing exercise

    // member variables
    private List<Exercise> _tempExerciseList;
    private String _mAuthAccountType; // account type of mAuth user for return to dashboard intent

    // views
    private ListView _lvExerciseList;

    // array adapter for exercise list
    private ExerciseListAdapter _adapterExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register view widgets
        _lvExerciseList = (ListView) findViewById(R.id.lvExerciseList);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind exercise list to it, then set the adapter
        _tempExerciseList = new ArrayList<>();
        _adapterExerciseList = new ExerciseListAdapter(this, _tempExerciseList);
        _lvExerciseList.setAdapter(_adapterExerciseList);

        Log.d(TAG, "array adapter initialized");

        // add listener for display reference to pull data from firebase and display in listview
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mExerciseLibraryRef = mFirebaseDatabase.getReference().child("library");
        mExerciseLibraryRef.addListenerForSingleValueEvent(valueEventListener);

        Log.d(TAG, "library reference set and listener added");

        // add reference for Users so that we can identify the account type of the current mAuth user
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        Log.d(TAG, "users ref added");

        // query user database by UID to see the account type of the mAuth user; this is done
        // to know which dashboard to return the user to upon click of "return to dashboard"
        Log.d(TAG, "onCreate: mAccountType before Query: " + _mAuthAccountType);
        Log.d(TAG, "onCreate: mAuth Get UID " + mAuth.getUid());
        Query query = mUsersRef.orderByChild("userID").equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(valueEventListenerUser);

        Log.d(TAG, "onCreate: valueEventListenerUser added");

        // click on an existing exercise from the list
        // that will open up the existing exercise for editing in AddExerciseToLibrary.java
        _lvExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current exercise from position in list
                Exercise currentExercise = _tempExerciseList.get(position);

                Log.d(TAG, "Position: " + position);
                Log.d(TAG, "Current Exercise: " + currentExercise);
                Log.d(TAG, "Exercise ID: " + currentExercise.get_exerciseID());

                // intent to go to Add/Edit Exercise to Library screen, passing exercise via extra message
                Intent intentAddExerciseToLibrary = new Intent(Exercises.this, AddExerciseToLibrary.class);
                intentAddExerciseToLibrary.putExtra(MSG_EXERCISE_ID, currentExercise.get_exerciseID());
                startActivity(intentAddExerciseToLibrary);
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

    // value event listener for library of exercises
    // Adding a ValueEventListener to the library of exercises will allow our app to
    // extract library data in realtime.
    ValueEventListener valueEventListener = new ValueEventListener() {
        // onDataChange: An event callback method. This method is triggered once when the listener
        // is attached (with the initial value) and again every time the data, including children,
        // changes.
        // DataSnapshot: A DataSnapshot instance contains data from a Firebase Database location,
        // in this case the library of exercises. Any time you read Database data, you receive the
        // data as a DataSnapshot.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            _tempExerciseList.clear();
            // is there a dataSnapShot...
            if (dataSnapshot.exists()) {
                // if so, get the library exercise data in the snapshot (from Realtime Database)...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // and store in local exercise variable
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    // then add to library of exercises.
                    _tempExerciseList.add(exercise);
                }
            }
            // update the listView when there is a change
            _adapterExerciseList.notifyDataSetChanged();
        }
        // This method is called when onDataChange() fails to read the value.
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    // value event listener for query of user ID to identify user type
    // Adding a ValueEventListener to the user ID will allow our app to extract
    // the account type of the mAuth user.
    ValueEventListener valueEventListenerUser = new ValueEventListener() {
        // onDataChange: An event callback method. This method is triggered once when the listener
        // is attached (with the initial value) and again every time the data, including children,
        // changes.
        // DataSnapshot: A DataSnapshot instance contains data from a Firebase Database location,
        // in this case the user. Any time you read Database data, you receive the
        // data as a DataSnapshot.
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = new User();
            // is there a dataSnapShot...
            if (dataSnapshot.exists()) {
                // if so, get the user data in the snapshot (from Realtime Database)...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // and store in local user variable
                    user = snapshot.getValue(User.class);
                }
                // If user data extracted correctly and stored in local user variable...
                if (user != null) {
                    // store the account type of user, in the account type of mAuth user
                    // for return to dashboard intent.
                    _mAuthAccountType = user.get_accountType();
                }
            }
        }
        // This method is called when onDataChange() fails to read the value.
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

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

         if (id == R.id.nav_returnToDashboard) {

             Log.d(TAG, "on My Clients navigation: Return to Dashboard: " + _mAuthAccountType);

             if (_mAuthAccountType != null) {

                 if (_mAuthAccountType.equals("therapist")) {

                     // go to My Clients (therapist dashboard)
                     Intent intentReturnTherapistDashboard = new Intent(Exercises.this, MyClients.class);
                     startActivity(intentReturnTherapistDashboard);

                 } else if (_mAuthAccountType.equals("admin")){

                     // go to Users (admin dashboard)
                     Intent intentReturnAdminDashboard = new Intent(Exercises.this, Users.class);
                     startActivity(intentReturnAdminDashboard);

                 } else {

                     // only therapist and admin users should have had access to the exercise
                     // library, so if this is happening, then there is a problem with the
                     // login user SP or somehow a client was able to see this screen
                     Toast.makeText(Exercises.this, "Unable to return to dashboard: " +
                             "user not therapist or admin", Toast.LENGTH_SHORT).show();
                 }

             } else {

                 // if account type is not set then present an error message to the user
                 Toast.makeText(Exercises.this, "Unable to return to dashboard: " +
                         "error with user login", Toast.LENGTH_SHORT).show();
             }

        } else if (id == R.id.nav_addNewExercise) {

             // add new exercise button is clicked, go to add exercise to library activity
             Intent intentAddExercise = new Intent(Exercises.this, AddExerciseToLibrary.class);
             intentAddExercise.putExtra(MSG_EXERCISE_ID, "");
             startActivity(intentAddExercise);

        } else if (id == R.id.nav_myProfile) {

            // my profile button is clicked, go to my profile activity
            Intent intentProfile = new Intent(Exercises.this, MyProfile.class);
            startActivity(intentProfile);

        }  else if (id == R.id.nav_LogOut) {

             // log out button is clicked, go to sign in activity
             Intent intentLogOut = new Intent(Exercises.this, SignIn.class);
             startActivity(intentLogOut);
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    } // END listener for navigation menu

} // END Exercises class

