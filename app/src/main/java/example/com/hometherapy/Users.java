package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    // for log
    private static final String TAG = "Users_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    // note - we should put all of these constants into a constants class, and then
    // refer to that class whenever they are used throughout the program
    // this was a suggestion of Brother Falin at one point

    // private member variables
    private TextView _tvUsersLabel;
    private ListView _lvUserList;
    private Button _btnUserLogOut;
    private Button _btnUsersAddNewUser;
    private Button _btnGoToExerciseLibrary;
    private UserList _currentUsers;
    private User _currentUser;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<User> _tempUserList;

    // array adapter for user list
    private UserListAdapter _adapterUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON user database into List of Users
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);
        _tempUserList = _currentUsers.getUserList();

        // register views
        // _tvUsersLabel = (TextView) findViewById(R.id.tvUsersLabel);
        _lvUserList = (ListView) findViewById(R.id.lvUserList);
        _btnUsersAddNewUser = (Button) findViewById(R.id.btnUsersAddUser);
        //_btnUserLogOut = (Button) findViewById(R.id.btnUsersLogOut);
        // _btnGoToExerciseLibrary = (Button) findViewById(R.id.btnUsersGoToExerciseLibrary);

        // initialize array adapter and bind user list to it
        _adapterUserList = new UserListAdapter(this, _tempUserList);

        // set adapter
        _lvUserList.setAdapter(_adapterUserList);

        // set on item click listener so we move to the add edit user activity when
        // a user is clicked, moving to that specific user's account
        _lvUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current user from position in list
                User currentUser = _tempUserList.get(position);

                // intent to go to Add Edit User screen, passing user via extra message
                Intent intentAEU = new Intent(Users.this, AddEditUser.class);
                intentAEU.putExtra(MSG_USER_EMAIL, currentUser.getEmail());
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
                Intent intentAddNewUser = new Intent(Users.this, AddEditUser.class);
                intentAddNewUser.putExtra(MSG_USER_EMAIL, "");
                startActivity(intentAddNewUser);
            }
        });

        // on click, go to exercise library
        /*_btnGoToExerciseLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentExercises = new Intent(Users.this, Exercises.class);
                startActivity(intentExercises);

            }
        });*/

        // on click, just go back to signIn screen
        /*_btnUserLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentSignIn = new Intent(Users.this, SignIn.class);
                startActivity(intentSignIn);

            }
        });*/


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
