package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client Dashboard.
 * This is the first view that the client sees when they log in.
 * From here, they can click on an existing exercise {@link MyExercise}
 * to see it and mark it complete. From here, they can also use the
 * menu to see their profile {@link MyProfile}, go to messaging
 * {@link MyMessaging}, or view their rewards {@link MyRewards}.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MyExercises extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "My_Exercises_Activity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAssignedExercisesRef;

    // Key for extra message for user email address to pass to activity
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";

    // member variables
    private List<AssignedExercise> _tempAssignedExerciseList;

    // views
    private ListView _lvMEAssignedExercises;

    // adapter for assigned exercise list
    private AssignedExerciseListAdapter _adapterAssignedExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _lvMEAssignedExercises = (ListView) findViewById(R.id.lvMEAssignedExercises);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind assigned exercise list to it, then set the adapter
        _tempAssignedExerciseList = new ArrayList<>();
        _adapterAssignedExercises = new AssignedExerciseListAdapter(this, _tempAssignedExerciseList);
        _lvMEAssignedExercises.setAdapter(_adapterAssignedExercises);

        // setup firebase references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAssignedExercisesRef = mFirebaseDatabase.getReference().child("assignedExercises");

        // add listener for assigned exercise data to pull from firebase and display in listview
        Query queryAE = mAssignedExercisesRef.orderByChild("_assignedUserID").equalTo(mAuth.getUid());
        queryAE.addListenerForSingleValueEvent(assignedExerciseEventListener);

        // client clicks on exercise to take them to a view of that individual exercise
        _lvMEAssignedExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current assigned exercise from position in the list
                AssignedExercise selectedExercise = _tempAssignedExerciseList.get(position);

                Log.d(TAG, "selected Exercise: " + selectedExercise);

                // intent to open up client's view of their own exercise
                Intent intentMyExercise = new Intent(MyExercises.this, MyExercise.class);
                intentMyExercise.putExtra(MSG_ASSIGNED_EXERCISE_ID, selectedExercise.get_assignedExerciseID());
                startActivity(intentMyExercise);
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
    // Adding a ValueEventListener to the assigned exercises will allow our app to extract
    // all the assigned exercises of the client user.
    ValueEventListener assignedExerciseEventListener = new ValueEventListener() {
        // onDataChange: An event callback method. This method is triggered once when the listener
        // is attached (with the initial value) and again every time the data, including children,
        // changes.
        // DataSnapshot: A DataSnapshot instance contains data from a Firebase Database location,
        // in this case the library of assigned exercises. Any time you read Database data, you
        // receive the data as a DataSnapshot.
        @Override
        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
            _tempAssignedExerciseList.clear();
            // is there a dataSnapShot...
            if (dataSnapshot.exists()) {
                // if so, get the assignedExercise data in the snapshot (from Realtime Database)...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // and store in local assignedExercise variable
                    AssignedExercise assignedExercise = snapshot.getValue(AssignedExercise.class);
                    // then add assignedExercise to temp AssignedExercise list.
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

        if (id == R.id.nav_myRewards) {
            Intent intentRewards = new Intent(MyExercises.this, MyRewards.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyExercises.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myProfile) {
            Intent intentProfile = new Intent(MyExercises.this, MyProfile.class);
            startActivity(intentProfile);
        } else if (id == R.id.nav_LogOut) {
            Intent intentSignIn = new Intent(MyExercises.this, SignIn.class);
            startActivity(intentSignIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
