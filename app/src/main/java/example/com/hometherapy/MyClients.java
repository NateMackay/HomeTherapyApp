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
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersRef;

    // for log
    private static final String TAG = "MyClients_Activity";

    // Key for extra message for client UID to pass to activity
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";
    public static final String MSG_THERAPIST_UID = "example.com.hometherapy.THERAPIST_UID";

    /**
    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";
    public static final String LOGIN_USER = "loginUser";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_CLIENT_FIRST_NAME = "example.com.hometherapy.CLIENT_FIRST_NAME";
     **/

    // private member variables
    private TextView _tvClientViewTitle;
    private ListView _lvClientList;
    private Button _btnAddClient;
    private Button _btnMyClientsGoToExerciseLibrary;
    private Button _btnMyClientsLogOut;
    private UserList _currentUsers;
    private User _currentUser;
    private String _currentUserEmail; // email of the therapist, also the current login user
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<User> _tempUserList; // all users
    private List<User> _filteredUserList; // only those users assigned to therapist

    // array adapter for user list
    private MyClientsAdapter _adapterUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _tvClientViewTitle = (TextView) findViewById(R.id.tvClientViewTitle);
        _lvClientList = (ListView) findViewById(R.id.lvClientList);
        _btnMyClientsGoToExerciseLibrary = (Button) findViewById(R.id.btnMyClientsGoToExerciseLibrary);
        _btnMyClientsLogOut = (Button) findViewById(R.id.btnMyClientsLogOut);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind user list to it, then set the adapter
        _tempUserList = new ArrayList<>();
        _adapterUserList = new MyClientsAdapter(this, _tempUserList);
        _lvClientList.setAdapter(_adapterUserList);

        // add listener for display reference to pull data from firebase and display in listview
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mFirebaseDatabase.getReference().child("users");
        mUsersRef.addListenerForSingleValueEvent(valueEventListener);

        Log.d(TAG, "onCreate: after value listener: " + _tempUserList);

        // add reference for Users so that we can identify the account type of the current mAuth user
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        Log.d(TAG, "users ref added");

        // query user database by UID to see the account type of the mAuth user; this is done
        // to know which dashboard to return the user to upon click of "return to dashboard"
        //Log.d(TAG, "onCreate: mAccountType before Query: " + _mAuthAccountType);
        Log.d(TAG, "onCreate: mAuth Get UID " + mAuth.getUid());
        Query query = mUsersRef.orderByChild("assignedTherapistUID").equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(valueEventListener);

        Log.d(TAG, "onCreate: valueEventListenerUser added");

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

        // go to exercise library
        _btnMyClientsGoToExerciseLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentExercises = new Intent(MyClients.this, Exercises.class);
                startActivity(intentExercises);
            }
        });

        // log out, go back to signIn screen
        _btnMyClientsLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentSignIn = new Intent(MyClients.this, SignIn.class);
                startActivity(intentSignIn);
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
                    if (user.get_assignedTherapistUID().equals(mAuth.getUid())) {
                        _tempUserList.add(user);
                    }
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

/*        if (id == R.id.nav_myClients) {
            // Handle the camera action
            Intent intentExercises = new Intent(MyClients.this, MyClients.class);
            startActivity(intentExercises);
        } else*/
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
