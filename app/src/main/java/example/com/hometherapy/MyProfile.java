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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersRef;

    // validators for email, password, and phone number input fields
    private EmailValidator _emailValidator;
    private PasswordValidator _passwordValidator;
    private PhoneNumberValidator _phoneNumberValidator;

    // Views
    private TextView _etUserProfFirstName;
    private TextView _etUserProfLastName;
    private TextView _etUserProfEmail;
    private TextView _etUserProfPhone;
    private TextView _etUserProfPwd;
    private TextView _etUserProfPwdConfirm;
    private Button _btnUserProfSave;

    // member variables
    private String _currentUserID;
    private String _userAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        _currentUserID = mAuth.getUid();

        // register Firebase DB and References
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        Log.d(TAG, "onCreate _currentUserID: " + _currentUserID);

        // register views
        _etUserProfFirstName = (TextView) findViewById(R.id.etUserProfFirstName);
        _etUserProfLastName = (TextView) findViewById(R.id.etUserProfLastName);
        _etUserProfEmail = (TextView) findViewById(R.id.etUserProfEmail);
        _etUserProfPhone = (TextView) findViewById(R.id.etUserProfPhone);
        _etUserProfPwd = (TextView) findViewById(R.id.etUserProfPwd);
        _etUserProfPwdConfirm = (TextView) findViewById(R.id.etUserProfPwdConfirm);
        _btnUserProfSave = (Button) findViewById(R.id.btnUserProfSave);

        // setup field validators
        // email validator
        _emailValidator = new EmailValidator();
        _etUserProfEmail.addTextChangedListener(_emailValidator);

        // password validator
        _passwordValidator = new PasswordValidator();
        _etUserProfPwd.addTextChangedListener(_passwordValidator);

        // phone number validator
        _phoneNumberValidator = new PhoneNumberValidator();
        _etUserProfPhone.addTextChangedListener(_phoneNumberValidator);

        // query user database for authenticated user
        Query query = mUsersRef.orderByChild("userID").equalTo(_currentUserID);
        query.addListenerForSingleValueEvent(valueEventListenerUser);

        // navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    } // END onCreate()

    // value listener for authenticated user to populate view
    ValueEventListener valueEventListenerUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d(TAG, "contents of dataSnapshot: " + dataSnapshot.toString());
            if (dataSnapshot.exists()) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                }

                if (user != null) {

                    // populate views with user data
                    _etUserProfFirstName.setText(user.getFirstName());
                    _etUserProfLastName.setText(user.getLastName());
                    _etUserProfEmail.setText(user.getEmail());
                    _etUserProfPhone.setText(user.getPhone());

                    // set current user account type
                    _userAccountType = user.get_accountType();

                    // verify user ID and account type
                    Log.d(TAG, "onDataChange _currentUserID: " + _currentUserID);
                    Log.d(TAG, "onDataChange user.getUserID(): " + user.getUserID());
                    Log.d(TAG, "onDataChange _userAccountType: " + _userAccountType);

                    // set on click listener only after data has changed
                    _btnUserProfSave.setOnClickListener(saveChangesBtnListener);

                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    // onClick listener for save changes button
    View.OnClickListener saveChangesBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // validate email
            if (!_emailValidator.isValid()) {
                _etUserProfEmail.setError("Invalid Email");
                _etUserProfEmail.requestFocus();
                return;
            }

            // validate password only if not empty
            if (!_etUserProfPwd.getText().toString().isEmpty() && !_passwordValidator.isValid()) {
                _etUserProfPwd.setError("Invalid Password");
                Toast.makeText(MyProfile.this, "Password must,\n" +
                        "contain at least one digit,\n" +
                        "contain at least one lower case character,\n" +
                        "contain at least one upper case character,\n" +
                        "contain at least one special character, and" +
                        "be between 8 and 40 characters long", Toast.LENGTH_LONG).show();
                _etUserProfPwd.requestFocus();
                return;
            }

            // verify password confirmation matches password
            // add password validation class
            if (!_etUserProfPwd.getText().toString().equals(_etUserProfPwdConfirm.getText().toString())) {
                _etUserProfPwdConfirm.setError("Password does not match");
                _etUserProfPwdConfirm.requestFocus();
                return;
            }

            // validate phone number
            if (!_phoneNumberValidator.isValid()) {
                _etUserProfPhone.setError("Invalid phone number.");
                Toast.makeText(MyProfile.this,
                        "Phone number must be of the form," +
                                "1234567890,.\n" +
                                "123-456-7890,\n" +
                                "(123)456-7890, or\n" +
                                "(123)4567890", Toast.LENGTH_LONG).show();
                _etUserProfPhone.requestFocus();
                return;
            }

            // set views to variables
            String etUserProfFirstName = _etUserProfFirstName.getText().toString();
            String etUserProfLastName = _etUserProfLastName.getText().toString();
            String etUserProfEmail = _etUserProfEmail.getText().toString();
            String etUserProfPhone = _etUserProfPhone.getText().toString();
            String etUserProfPwd = _etUserProfPwd.getText().toString();

            // update data
            DatabaseReference userRef = mUsersRef.child(_currentUserID);

            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("firstName", etUserProfFirstName);
            userUpdates.put("lastName", etUserProfLastName);
            userUpdates.put("phone", etUserProfPhone);

            userRef.updateChildren(userUpdates);

            // update password and email logic
            // note, because you cannot update a password or user without the recent
            // authentication (i.e. on myProfile page), the functionality is removed
            // to update the password or email address from the admin portal
            // instead, a link to reset password can be sent or they can do it from
            // the portal if they have their current login credentials
            // for now, just ignore email addresses when the function is to update
            // email is still left in for new users
            // reference: https://firebase.google.com/docs/auth/android/manage-users
            // see Send password reset email, re-authenticate user, set a user's password
            // set a users email address, update user's profile
            // note, [my first name]'s Exercises on that activity will be
            // based on displayname, a property of the UID, not the Users database
            // have the user update it on myProfile if they want to change that value

                // update password only if password has been updated
//                    if (etUserProfPwd.length() > 0) {
//                        _currentUser.setPassword(etUserProfPwd);
//                    }

            Log.d(TAG, "onClick _userAccountType: " + _userAccountType);

                // intent to go back to admin, client, or therapist dashboard
                if (_userAccountType != null) {

                    switch (_userAccountType) {
                        case "therapist":
                            // go to my clients, or therapist dashboard
                            Intent intentClients = new Intent(MyProfile.this, MyClients.class);
                            startActivity(intentClients);
                            break;

                        case "client":
                            // go to my exercises, or client dashboard
                            Intent intentExercises = new Intent(MyProfile.this, MyExercises.class);
                            startActivity(intentExercises);
                            break;

                        case "admin":
                            // go to users, or admin dashboard
                            Intent intentUsers = new Intent(MyProfile.this, Users.class);
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
//                    Intent intentSignIn = new Intent(MyProfile.this, SignIn.class);
//                    startActivity(intentSignIn);
                }

            } // END onClick

        }; // END on click listener for save changes button

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

        // NOTE: Because MyProfile.java is used for all three user types, in order to have
        // MyClients or MyExercises or Users as available options, we would need to build in
        // the same logic above where it depends on the user type
        // Problem with this is that the user type will likely not be set at the time
        // this listener is set

        if (id == R.id.nav_myClients) {
            Intent intentExercises = new Intent(MyProfile.this, MyClients.class);
            startActivity(intentExercises);
        } else if (id == R.id.nav_myExercises) {
            Intent intentMessage = new Intent(MyProfile.this, MyExercise.class);
            startActivity(intentMessage);
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyProfile.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myProfile) {
            // Intent intentProfile = new Intent(MyProfile.this, MyProfile.class);
            // startActivity(intentProfile);
        } else if (id == R.id.nav_LogOut) {
            Intent intentProfile = new Intent(MyProfile.this, ClientProfile.class);
            startActivity(intentProfile);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    } // END on Navigation Item Selected

} // END myProfile class
