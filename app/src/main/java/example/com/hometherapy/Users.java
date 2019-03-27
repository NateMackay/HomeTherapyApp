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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * The Users activity description:
 * Admins can see a list of all of the users.
 * From here, the admin can add a new user, click on an existing user to edit
 * their account, or open up the library of exercises.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class Users extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    // for log
    private static final String TAG = "Users_Activity";

    // Key for extra message for UID of user to edit in add-edit user - NOT the current auth UID
    public static final String MSG_PASSED_UID = "example.com.hometherapy.PASSED_UID";

    // private member variables
    private ListView _lvUserList;
    private Button _btnUsersAddNewUser;
    private List<User> _tempUserList;

    // array adapter for user list
    private UserListAdapter _adapterUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _lvUserList = (ListView) findViewById(R.id.lvUserList);
        _btnUsersAddNewUser = (Button) findViewById(R.id.btnUsersAddUser);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind user list to it
        _tempUserList = new ArrayList<>();
        _adapterUserList = new UserListAdapter(this, _tempUserList);

        // add listener to database reference
        // instantiate Firebase RTDB and DBREF
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mUsersDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        Log.d(TAG, "onCreate: after value listener: " + _tempUserList);

        // navigation
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

            // set the adapter after the list has been updated
            _lvUserList.setAdapter(_adapterUserList);

            // set on item click listener so we move to the add edit user activity when
            // a user is clicked, moving to that specific user's account
            _lvUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // get current user from position in list
                    User currentUser = _tempUserList.get(position);

                    Log.d(TAG, "onItemClick: Current User.Name" + currentUser.getFirstName());
                    Log.d(TAG, "onItemClick: Current User.UserID" + currentUser.getUserID());

                    // intent to go to Add Edit User screen, passing user via extra message
                    Intent intentAEU = new Intent(Users.this, AddEditUser.class);
                    intentAEU.putExtra(MSG_PASSED_UID, currentUser.getUserID());
                    startActivity(intentAEU);
                }
            });

            // add new user from user screen
            _btnUsersAddNewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // since adding a new user, we do not have a user email yet
                    // AddEditUser.java is expecting an intent for user email
                    // so we are sending an empty string value
                    Intent intentAEU = new Intent(Users.this, AddEditUser.class);
                    intentAEU.putExtra(MSG_PASSED_UID, "");
                    startActivity(intentAEU);
                }
            });

            Log.d(TAG, "onDataChange: tempUserList = " + _tempUserList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null), and update UI accordingly
        Log.d(TAG, "onStart: tempUserList = " + _tempUserList);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    } // [END on_start_check_user]

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "updateUI: tempUserList = " + _tempUserList);

        } else {
            Log.d(TAG, "updateUI: Firebase user is null. Move to Sign-in Activity");
            // move to sign-in screen if current user is not authenticated
            Intent intentLogIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intentLogIn);
        }
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
        getMenuInflater().inflate(R.menu.users, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* if (id == R.id.nav_users) {
            Intent intentUsers = new Intent(Users.this, Users.class);
            startActivity(intentUsers);
        } else */
            if (id == R.id.nav_exercise_library) {
            Intent intentExerciseLibrary = new Intent(Users.this, Exercises.class);
            startActivity(intentExerciseLibrary);
        } else if (id == R.id.nav_myProfile) {
            Intent intentProfile = new Intent(Users.this, MyProfile.class);
//            intentProfile.putExtra(MSG_USER_EMAIL, _adminUserEmail);
            startActivity(intentProfile);
        } else if (id == R.id.nav_LogOut) {
            Intent intentLogIn = new Intent(Users.this, SignIn.class);
            startActivity(intentLogIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
