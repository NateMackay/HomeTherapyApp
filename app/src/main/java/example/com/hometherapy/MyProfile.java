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
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.List;

/**
 * View of user's profile for all user types.
 * This view enables a user to view/edit their profile
 * information. It also enables the user to change their password.
 * If they leave the password field blank, their existing
 * password will not be overwritten.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MyProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "MyProfile_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";

    // validator for email input field
    private EmailValidator _emailValidator;

    // Views
    private TextView _etUserProfFirstName;
    private TextView _etUserProfLastName;
    private TextView _etUserProfEmail;
    private TextView _etUserProfPhone;
    private TextView _etUserProfPwd;
    private TextView _etUserProfPwdConfirm;
    private Button _btnUserProfSave;

    // member variables
    private String _currentUserEmail;
    private UserList _userDatabase;
    private User _currentUser;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _etUserProfFirstName = (TextView) findViewById(R.id.etUserProfFirstName);
        _etUserProfLastName = (TextView) findViewById(R.id.etUserProfLastName);
        _etUserProfEmail = (TextView) findViewById(R.id.etUserProfEmail);
        _etUserProfPhone = (TextView) findViewById(R.id.etUserProfPhone);
        _etUserProfPwd = (TextView) findViewById(R.id.etUserProfPwd);
        _etUserProfPwdConfirm = (TextView) findViewById(R.id.etUserProfPwdConfirm);
        _btnUserProfSave = (Button) findViewById(R.id.btnUserProfSave);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etUserProfEmail.addTextChangedListener(_emailValidator);

        // open up User database (shared preferences)
        // JSON formatted string stored in shared preferences storing a UserList object
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account/ID) from extra message in intent
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // deserialize sharedPrefs from JSON user database into List of Users
        _userDatabase = _gson.fromJson(jsonUserList, UserList.class);

        if (_userDatabase != null) {

            List<User> tempUserList = _userDatabase.getUserList();

            // find matching user account by email
            _currentUser = tempUserList.stream()
                    .filter(user -> _currentUserEmail.equals(user.getEmail()))
                    .findAny()
                    .orElse(null);

            Log.d(TAG, "verify current user: " + _currentUser);
        }

        // populate profile information with current user info
        if (_currentUser != null) {

            _etUserProfFirstName.setText(_currentUser.getFirstName());
            _etUserProfLastName.setText(_currentUser.getLastName());
            _etUserProfEmail.setText(_currentUser.getEmail());
            _etUserProfPhone.setText(_currentUser.getPhone());
        }

        // save button listener
        _btnUserProfSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate email
                if (!_emailValidator.isValid()) {
                    _etUserProfEmail.setError("Invalid Email");
                    _etUserProfEmail.requestFocus();
                    return;
                }

                // verify password confirmation matches password
                // add password validation class
                if (!_etUserProfPwd.getText().toString().equals(_etUserProfPwdConfirm.getText().toString())) {
                    _etUserProfPwdConfirm.setError("Password does not match");
                    _etUserProfPwdConfirm.requestFocus();
                    return;
                }

                // set views to variables
                String etUserProfFirstName = _etUserProfFirstName.getText().toString();
                String etUserProfLastName = _etUserProfLastName.getText().toString();
                String etUserProfEmail = _etUserProfEmail.getText().toString();
                String etUserProfPhone = _etUserProfPhone.getText().toString();
                String etUserProfPwd = _etUserProfPwd.getText().toString();

                // update current user with any changes
                // if null do a snackbar error message and take user back to sign-in screen
                if (_currentUser != null) {
                    _currentUser.setFirstName(etUserProfFirstName);
                    _currentUser.setLastName(etUserProfLastName);
                    _currentUser.setEmail(etUserProfEmail);
                    _currentUser.setPhone(etUserProfPhone);

                    // update password only if password has been updated
                    if (etUserProfPwd.length() > 0) {
                        _currentUser.setPassword(etUserProfPwd);
                    }

                    // convert updated UserList object back to JSON format
                    String updatedUserList = _gson.toJson(_userDatabase);

                    // update Shared Prefs with updated data
                    SharedPreferences.Editor editor = _sharedPreferences.edit();
                    editor.putString(USER_DATA, updatedUserList);
                    editor.apply();  // considering using commit instead

                    // log contents for testing purposes
                    String fromSharedPrefs = _sharedPreferences.getString(USER_DATA, "");
                    Log.d(TAG, "User Database from Shared Prefs: " + fromSharedPrefs);

                    // intent to go back to admin, client, or therapist dashboard
                    // based on user account type
                    String accountType = _currentUser.get_accountType();

                    if (accountType != null) {
                        switch (accountType) {
                            case "therapist":
                                // go to my clients, or therapist dashboard
                                Intent intentClients = new Intent(MyProfile.this, MyClients.class);
                                intentClients.putExtra(MSG_USER_EMAIL, etUserProfEmail);
                                startActivity(intentClients);
                                break;

                            case "client":
                                // go to my exercises, or client dashboard
                                Intent intentExercises = new Intent(MyProfile.this, MyExercises.class);
                                intentExercises.putExtra(MSG_USER_EMAIL, etUserProfEmail);
                                startActivity(intentExercises);
                                break;

                            case "admin":
                                // go to users, or admin dashboard
                                Intent intentUsers = new Intent(MyProfile.this, Users.class);
                                intentUsers.putExtra(MSG_USER_EMAIL, etUserProfEmail);
                                startActivity(intentUsers);
                                break;

                            default:
                                // if account type is other than admin, therapist, or client
                                // send error message to contact administrator then go back to sign-in screen
                                // NOTE - ADD SNACKBAR AND THEN PUT IN SEPARATE FUNCTION CALL HERE AND BELOW
                                Log.e(TAG, "error: on save, user type other than admin, therapist, or client");
                                Intent intentSignIn = new Intent(MyProfile.this, SignIn.class);
                                startActivity(intentSignIn);
                                break;
                        }

                    } else {
                        // if account type is null
                        // send error message to contact administrator then go back to sign-in screen
                        Log.e(TAG, "error: on save, account type is null");
                        Intent intentSignIn = new Intent(MyProfile.this, SignIn.class);
                        startActivity(intentSignIn);
                    }

                } else {
                    // if current user is null
                    // send error message to contact administrator then go back to sign-in screen
                    Log.e(TAG, "error: on save, current user is null");
                    Intent intentSignIn = new Intent(MyProfile.this, SignIn.class);
                    startActivity(intentSignIn);
                }

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
        getMenuInflater().inflate(R.menu.my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myClients) {
            // Handle the camera action
            Intent intentExercises = new Intent(MyProfile.this, MyClients.class);
            startActivity(intentExercises);
        } else if (id == R.id.nav_myExercises) {
            Intent intentMessage = new Intent(MyProfile.this, MyExercise.class);
            startActivity(intentMessage);
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyProfile.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_manage) {
            Intent intentProfile = new Intent(MyProfile.this, MyProfile.class);
            startActivity(intentProfile);
        } else if (id == R.id.nav_send) {
            Intent intentProfile = new Intent(MyProfile.this, ClientProfile.class);
            startActivity(intentProfile);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
