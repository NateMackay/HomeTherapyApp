package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sign-in screen. This is the first view after the splash screen. From here, users
 * can either sign in or click on the register {@link Register} button to register
 * as a new user.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class SignIn extends AppCompatActivity {

    // firebase authentication instance
    private FirebaseAuth mAuth;

    // for log
    private static final String TAG = "SignIn_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";
    public static final String LOGIN_USER = "loginUser";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_ACCT_TYPE = "example.com.hometherapy.ACCT_TYPE";

    // private member variables
    private EditText _etSignInEmail;
    private EditText _etSignInPassword;
    private Button _btnLogin;
    private Button _btnRegister;

    // validators for email, password, and phone number input fields
    private EmailValidator _emailValidator;
    private PasswordValidator _passwordValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize view elements
        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _btnRegister = (Button) findViewById(R.id.btnRegister);
        _etSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        _etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);

        // setup field validators
        // email
        _emailValidator = new EmailValidator();
        _etSignInEmail.addTextChangedListener(_emailValidator);
        // password
        _passwordValidator = new PasswordValidator();
        _etSignInPassword.addTextChangedListener(_passwordValidator);


        // FIREBASE: login button just needs to get email and password and call signin function
        // but then it also needs to pass information into shared prefs
        // only problem with doing this is you will need to make sure you don't delete the shared
        // prefs because it will have all of the UID from Firebase and how will you re-associate new
        // data with the UID?

        // START login button events
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate email first
                if (!_emailValidator.isValid()) {
                    _etSignInEmail.setError("Invalid email address.");
                    _etSignInEmail.requestFocus();
                    return;
                }

                // validate password
                if (!_passwordValidator.isValid()) {
                    _etSignInPassword.setError("Invalid Password");
                    _etSignInPassword.requestFocus();
                    return;
                }

                // store email and password from user entry
                String signInEmail = _etSignInEmail.getText().toString();
                String signInPassword = _etSignInPassword.getText().toString();

                // FIREBASE: sign-in to firebase using email and password entered
                signIn(signInEmail, signInPassword);

                // verify current user - for testing
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "after call to signIn() - UID: " + user.getUid());
                    Log.d(TAG, "after call to signIn() - email: " + user.getEmail());
                    Log.d(TAG, "after call to signIn() - name: " + user.getDisplayName());
                    Log.d(TAG, "after call to signIn() - phone: " + user.getPhoneNumber());
                }

                // FIREBASE: NOTE - although documentation doesn't list this, firebase user does
                // store phone number - probably for messaging purposes
                // add name and phone to information stored with firebase user in registration
                // addedit user will need to do a call to profile updates - see documentation on managing users


                // FIREBASE: commenting out all of the below code for testing - ideally implement firestore
//                // pull existing user list from shared preferences into a JSON format "List" of
//                // "User" or rather a UserList object
//                // if nothing is in SHARED_PREFS then default value will be empty string
//                // FIREBASE: May want to keep this to store user data in until we get firestore implemented
//                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//                String jsonUserList = sharedPreferences.getString(USER_DATA, "");
//
//                // FIREBASE: KEEP FOR NOW
//                // initiate GSON object
//                Gson gson = new Gson();
//
//                // FIREBASE: KEEP FOR NOW
//                // for storing list of current users from Shared Prefs
//                UserList currentUsers = null;
//
//                // temp User object
//                User loginUser = null;
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
//                        if (tempUserList.get(i).getEmail().equals(signInEmail)) {
//                            loginUser = tempUserList.get(i);
//                        }
//                    }
//                } else {
//                    // there are no users in the list
//                    // direct user to register for an account
//                    _etSignInEmail.setError("Account not found. Please register for new account.");
//                    _etSignInEmail.requestFocus();
//                    return;
//                }
//
//                // if loginUser is still null, it means that user wasn't found in the list
//                // direct user to register
//                if (loginUser == null) {
//                    // user is not in the list
//                    // direct user to register for an account
//                    _etSignInEmail.setError("Account not found. Please register for new account.");
//                    _etSignInEmail.requestFocus();
//                    return;
//                }
//
//                // if password entered does not match saved password, return to password field
//                if (!loginUser.getPassword().equals(_etSignInPassword.getText().toString())) {
//                    _etSignInPassword.setError("Password is incorrect. Re-enter password");
//                    _etSignInPassword.requestFocus();
//                    return;
//                }
//
//                // login user is not null and there is a password match
//                // get account type of user and log account type
//                String accountType = loginUser.get_accountType();
//                Log.d(TAG, "account type: " + accountType);
//
//                // since login user is not null and the user is a valid user
//                // store user in current user shared prefs
//                // note that this should replace whatever is in SP for LOGIN_USER
//                // so should not need to clear it out
//                String jsonLoginUser = gson.toJson(loginUser);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(LOGIN_USER, jsonLoginUser);
//                editor.apply();
//
//                if (accountType != null) {
//                    if (accountType.equals("therapist")) {
//                        // intent to go to Therapist's My Clients screen, passing user via extra message
//                        Intent intentClients = new Intent(SignIn.this, MyClients.class);
//                        intentClients.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
////                        intentClients.putExtra(MSG_ACCT_TYPE, loginUser.get_accountType()); // not sure if we need this
//                        startActivity(intentClients);
//                    } else if (accountType.equals("client")) {
//                        // intent to go to Exercises screen, passing user via extra message
//                        Intent intentExercises = new Intent(SignIn.this, MyExercises.class);
//                        intentExercises.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
////                        intentExercises.putExtra(MSG_ACCT_TYPE, loginUser.get_accountType()); // not sure if we need this
//                        startActivity(intentExercises);
//                    } else if (accountType.equals("admin")) {
//                        // intent to go to Users screen, passing user via extra message
//                        Intent intentUsers = new Intent(SignIn.this, Users.class);
//                        intentUsers.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
//                        startActivity(intentUsers);
//                    } else if (accountType.equals("pending")) {
//                        // intent to go to add edit user screen - temporary - will
//                        // need to display an error that user is not yet configured by admin
//                        // once users screen is set up for admin to set user profile
//                        // then this can be directed to error that user is not yet configured by admin
//                        Intent intentAEU = new Intent(SignIn.this, AddEditUser.class);
//                        intentAEU.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
//                        startActivity(intentAEU);
//                    } else {
//                        _etSignInEmail.setError("Problem with account. See administrator. Error 001.");
//                        _etSignInEmail.requestFocus();
//                    }
//                } else {
//                    _etSignInEmail.setError("Problem with account. See administrator. Error 002.");
//                    _etSignInEmail.requestFocus();
//                }


            } // END Login Button onClick()
        }); // END Login Button

        // START register button events
        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Register activity
                Intent intentRegister = new Intent(SignIn.this, Register.class);

                // go to Register activity
                startActivity(intentRegister);
            }
        }); // END register button events

    } // END onCreate()

    @Override
    public void onBackPressed() {
        // don't do anything
    }

    // START signIn
    private void signIn(String email, String password) {

        // consider adding validation here instead of up above
//        Log.d(TAG, "signIn:" + email);
//        if (!validateForm()) {
//            return;
//        }

//        showProgressDialog(); - consider adding this function

        // START sign_in_with_email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Log.d(TAG, "Within SignIn() - User: " + user);
                            if (user != null) {
                                Log.d(TAG, "Within SignIn() - UID: " + user.getUid());
                            }

                            // add intent to go to specific sign-in screen
                            // only issue is, we need to know the account type
                            // could still use shared prefs to store user account info
                            // but instead of key in User Data being email, it could
                            // simply be UID
                            // but... what we actually want is to use firestore database

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Invalid username and/or password. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

//                        hideProgressDialog(); - consider adding this functionality

                    }
                }); // END sign_in_with_email

    } // END signIn

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


} // END SignIn.class
