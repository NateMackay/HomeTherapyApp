package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.gson.Gson;

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

    // for log
    private static final String TAG = "MyClients_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";
    public static final String LOGIN_USER = "loginUser";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_CLIENT_FIRST_NAME = "example.com.hometherapy.CLIENT_FIRST_NAME";

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

        // open up user database from shared preferences
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");
        String jsonLoginUser = _sharedPreferences.getString(LOGIN_USER, "");

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON user database int list of users
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);
        _tempUserList = _currentUsers.getUserList();

        // deserialize sharedPrefs JSON login user into current user
        _currentUser = _gson.fromJson(jsonLoginUser, User.class);
        _currentUserEmail = _currentUser.getEmail();

        // filter list of all users to only those users assigned to therapist
        _filteredUserList = _tempUserList.stream()
                .filter(user -> _currentUserEmail.equals(user.get_assignedTherapistUID())) // FIREBASE UPDATE
                .collect(Collectors.toList());

        Log.d(TAG, "list after filtering: " + _filteredUserList);

        // register views
        _tvClientViewTitle = (TextView) findViewById(R.id.tvClientViewTitle);
        _lvClientList = (ListView) findViewById(R.id.lvClientList);
        _btnMyClientsGoToExerciseLibrary = (Button) findViewById(R.id.btnMyClientsGoToExerciseLibrary);
        _btnMyClientsLogOut = (Button) findViewById(R.id.btnMyClientsLogOut);

        // initialize array adapter and bind user list to it
        _adapterUserList = new MyClientsAdapter(this, _filteredUserList);

        // set adapter
        _lvClientList.setAdapter(_adapterUserList);

        // add set on item click listener here, to go to the client's view of his/her exercises
        // need to make sure there is a return to dashboard button on that client's view
        // to return either to my clients for a therapist or users for an admin staff
        // set on item click listener so we move to the add edit user activity when
        // a user is clicked, moving to that specific user's account
        _lvClientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current assigned exercise from position in the list
                User selectedClient = _filteredUserList.get(position);

                // the following is for therapists and admin users only
                // intent to go to Add(Edit) Exercise to Client
                // need to pass an intent that has the user ID as well as the assigned exercise ID
                Intent intentClientExercises = new Intent(MyClients.this, ClientExercises.class);
                intentClientExercises.putExtra(MSG_CLIENT_FIRST_NAME, selectedClient.getFirstName());
                intentClientExercises.putExtra(MSG_USER_EMAIL, selectedClient.getEmail());
                startActivity(intentClientExercises);
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
        getMenuInflater().inflate(R.menu.my_clients, menu);
        return true;
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
            intentProfile.putExtra(MSG_USER_EMAIL, _currentUserEmail);
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
