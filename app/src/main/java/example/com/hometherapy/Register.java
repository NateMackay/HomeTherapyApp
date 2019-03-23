package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Registration page for new users. Users click on register button
 * from sign-in view {@link SignIn} to register as a new user.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class Register extends AppCompatActivity {

    // FIREBASE declare auth
    private FirebaseAuth mAuth;

    // FIREBASE RTDB instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    // for log
    private static final String TAG = "Register_Activity";

    // views
    private Button _btnCreateAccount;
    private EditText _etFirstName;
    private EditText _etLastName;
    private EditText _etEmail;
    private EditText _etPhone;
    private EditText _etPassword;
    private EditText _etPasswordConfirm;

    // name shared preferences
//    public static final String SHARED_PREFS = "sharedPrefs";
//    public static final String USER_DATA = "userData";

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // instantiate Firebase RTDB and DBREF
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // notes on Firebase instance and reference
        // mFirebaseDatabase.getReference() gets reference to the root node
        // child.("users") gets reference to the users "database", which is nothing
        // more than a map of key/value pairs, where the key is a "push ID" - a String
        // and the value is a User object - map <String, User> structure
        // the User object is of a user class
        // the User object needs a UID for the user so that a given hashed entry
        // in the Users database can be linked with a given Firebase user
        // one would think that the "key" in the users "database" could be the
        // UID, but the UID is auto-generated whenever user information is added
        // which is after the fact that the user is added

        // register views
        _btnCreateAccount = (Button) findViewById(R.id.btnRegCreateAccount);
        _etFirstName = (EditText) findViewById(R.id.etRegFirstName);
        _etLastName = (EditText) findViewById(R.id.etRegLastName);
        _etEmail = (EditText) findViewById(R.id.etRegEmail);
        _etPhone = (EditText) findViewById(R.id.etRegPhone);
        _etPassword = (EditText) findViewById(R.id.etRegPassword);
        _etPasswordConfirm = (EditText) findViewById(R.id.etRegPasswordConfirm);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etEmail.addTextChangedListener(_emailValidator);

        _btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // register gson object
                Gson gson = new Gson();

                // validate email
                if (!_emailValidator.isValid()) {
                    _etEmail.setError("Invalid Email");
                    _etEmail.requestFocus();
                    return;
                }
                Log.d(TAG, "Email Validated");

                // set up a password validator before passing to createAccount method

                // verify password confirmation matches password
                if (!_etPassword.getText().toString().equals(_etPasswordConfirm.getText().toString())) {
                    _etPasswordConfirm.setError("Password does not match");
                    _etPasswordConfirm.requestFocus();
                    return;
                }

                // set values for new user object
                // FIREBASE: Firebase user only stores email, name, photo URL (not required)
                // and a unique ID supplied by Firebase
                // when we initially create the user, we are only adding email and name
                // we will need to link another data store that links the user ID
                // with the other information
                // when it says "name" it is unsure if it is a combined first and last name
                // or if it is in different fields
                // either way, doing this is okay, because we will initially just set
                // the email and password
                String email = _etEmail.getText().toString();
                String password = _etPassword.getText().toString();

                // FIREBASE create new user account
                createAccount(email, password);






                // FIREBASE: Shared Prefs logic will go away
                // no need to verify user, as Firebase will handle that
                // although we may need to check if existing and call error message function
//
//                // pull existing user list from shared preferences into a JSON format "List" of
//                // "User" or rather a UserList object
//                // if nothing is in SHARED_PREFS then default value will be empty string
//                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                String jsonUserList = sharedPreferences.getString(USER_DATA, "");
//
//                // for adding users
//                UserList currentUsers = new UserList();
//                boolean userExists = false;
//
//                // if there are existing users, convert JSON formatted string of existing users
//                // into a UserList object and store in currentUsers
//                // and check if user email is already in database
//                if (jsonUserList.length() > 0) {
//                    currentUsers = gson.fromJson(jsonUserList, UserList.class);
//
//                    // get reference to list of users
//                    List<User> tempUserList = currentUsers.getUserList();
//
//                    // loop through list of current users to see if newUser email already exists
//                    for (int i = 0; i < tempUserList.size(); i++) {
//                        // if email entered by user is in the database, set flag to true
//                        if (tempUserList.get(i).getEmail().equals(email)) {
//                            userExists = true;
//                        }
//                    }
//                } else {
//                    // if this is the first user added to the database, set user type to admin
//                    accountType = "admin";
//                }
//
//                // add new user to database only if user does not already exist
//                if (!userExists) {
//
//                    // add new user account to list of existing users
//                    currentUsers.addUser(newUser);
//
//                    // convert existing user list back to JSON format
//                    String updatedList = gson.toJson(currentUsers);
//
//                    // update Shared Prefs with updated data
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(USER_DATA, updatedList);
//                    editor.apply();
//
//                    // display contents for testing purposes
//                    String fromSharedPrefs = sharedPreferences.getString(USER_DATA, "");
//                    Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);
//                } else {
//                    _etEmail.setError("Email address is already registered");
//                    _etEmail.requestFocus();
//                    return;
//                }

            }
        });
    } // END onCreate()

    // create new user function
    public void createAccount(String email, String password) {

        // put in email validator here, or keep above in onclick function

        // Firebase auth method call to create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user); - probably not going to need this but keep until know for sure

                            if (user != null) {

                                // get values from activity for creating a new User object and updating Firebase User object
                                // change Register screen to capture a "display name" that we can user for any user
                                // for now, just make displayName the same as the users first name
                                String userID = user.getUid(); // store UID in User object
                                String firstName = _etFirstName.getText().toString();
                                String lastName = _etLastName.getText().toString();
                                String phone = _etPhone.getText().toString();
                                String status = "pending";
                                String assignedClinic = "pending";
                                String accountType = "pending";
                                String assignedTherapistUID = "";
                                String assignedTherapistName = "";

                                // set displayName directly linked to Firebase UID - not part of User class
                                // NOTE: may want to add a separate display name value to the users database
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName)
                                        .build();
                                user.updateProfile(profileUpdates);

                                // confirm user ID matches what is in Firebase authentication console - for testing
                                Log.d(TAG, "user ID: " + user.getUid());

                                // send verification email upon successful creation of user
                                // NOTE: change custom NonNull to add the support annotation instead throughout app where used
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Verification email sent.");
                                                }
                                            }
                                        });

                                // create a new user object from values on screen
                                User newUser = new User(userID, email, firstName, lastName, phone,
                                        status, assignedClinic, accountType,
                                        assignedTherapistUID, assignedTherapistName);

                                // FIREBASE - not sure why, but this doesn't work unless
                                // database is open to writing w/o authenication - NEED TO FIX

                                // save to users database with a key of userID
                                mUsersDatabaseReference.child(userID).setValue(newUser);
                            }

                            // upon successful completion, go back to sign-in page
                            Intent intentSignIn = new Intent(Register.this, SignIn.class);
                            startActivity(intentSignIn);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // hideProgressDialog(); - consider adding this functionality
                    }
                });
    } // end createAccount()

    private void updateUI(FirebaseUser user) {
//        hideProgressDialog(); - consider adding this functionality
        if (user != null) {

            // do stuff if user is not null

            // sample code
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
//                    user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
//
//            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {

            // do stuff if user IS null

            // sample code
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    } // END updateUI()


} // END Register.class
