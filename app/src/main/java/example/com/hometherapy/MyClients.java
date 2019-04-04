package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
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
 * example.com.hometherapy.Therapist dashboard.
 * Allows therapist to see a list of their clients.
 * example.com.hometherapy.Therapist can click on a client to go view of client’s exercises.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19 *
 */
public class MyClients extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersRef;

    // for log
    private static final String TAG = "MyClients_Activity";

    // Key for extra message for client UID to pass to activity
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";

    // private member variables
    private ListView _lvClientList;
    private List<User> _tempUserList; // all users

    // array adapter for user list
    private MyClientsAdapter _adapterUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _lvClientList = (ListView) findViewById(R.id.lvClientList);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind user list to it, then set the adapter
        _tempUserList = new ArrayList<>();
        _adapterUserList = new MyClientsAdapter(this, _tempUserList);
        _lvClientList.setAdapter(_adapterUserList);

        // set up firebase references from database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        // verify mAuth ID
        Log.d(TAG, "mAuth ID: " + mAuth.getUid());

        // query users from firebase where therapist (mAuth id) equals assigned therapist ID
        Query query = mUsersRef.orderByChild("_assignedTherapistUID").equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(valueEventListener);

        // set on item click listener so we move to the add edit user activity when
        // a user is clicked, moving to that specific user's account
        _lvClientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current user from position in list
                User currentUser = _tempUserList.get(position);

                Log.d(TAG, "onItemClick: Current User.Name" + currentUser.getFirstName());
                Log.d(TAG, "onItemClick: Current User.UserID" + currentUser.getUserID());

                // intent to go to view the MyExercises of the selected client.
                Intent intentME = new Intent(MyClients.this, ClientExercises.class);
                intentME.putExtra(MSG_CLIENT_UID, currentUser.getUserID());
                startActivity(intentME);
            }
        });

        // for navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    } // END onCreate()

    // listener for user data
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            _tempUserList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    _tempUserList.add(user);
                }
            }
            _adapterUserList.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_exercise_library) {

            Intent intentExerciseLibrary = new Intent(MyClients.this, Exercises.class);
            startActivity(intentExerciseLibrary);

        } else if (id == R.id.nav_myMessages) {

            Intent intentRewards = new Intent(MyClients.this, MyMessages.class);
            startActivity(intentRewards);

        } else if (id == R.id.nav_myProfile) {

            Intent intentProfile = new Intent(MyClients.this, MyProfile.class);
            startActivity(intentProfile);

        } else if (id == R.id.nav_LogOut) {

            Intent intentLogOut = new Intent(MyClients.this, SignIn.class);
            startActivity(intentLogOut);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
