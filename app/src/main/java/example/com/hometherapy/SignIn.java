package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // FIREBASE RTDB instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;

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
    private String _accountType;
    private User _loginUser;

    // value event listeners
    ValueEventListener _userValueEventListener; // for user object

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // instantiate Firebase RTDB and DBREF
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // Value Event Listener for User object
//        _userValueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    User tempUser = dataSnapshot.getValue(User.class);
//                    if (tempUser != null) {
//                        _accountType = tempUser.get_accountType();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        };

        // initialize view elements
        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _btnRegister = (Button) findViewById(R.id.btnRegister);
        _etSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        _etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etSignInEmail.addTextChangedListener(_emailValidator);


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
//                        // intent to go to example.com.hometherapy.Therapist's My Clients screen, passing user via extra message
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

        // initialize auth state listener
        // note - I think this makes sense to be on the Main Activity where the splash screen is
        // that way if you are logged in, it goes right to your dashboard
        // auth state listener may need to be on other pages so that the app can be "torn down"
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // check if firebase user is logged in wtih firebase user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                } else {
                    // user is signed out
                    // follow sign in flow
                }
            }
        };

    } // END onCreate()

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    } // [END on_start_check_user]

    @Override
    protected void onStop() {
        super.onStop();
//        signOut(); - thought about this, but doesn't make sense from this page
        // should be where data is stored, normally, but Firebase makes sure data is
        // synced, so not sure if this is needed
    }

    // use this for whenever we need to call sign out from any page
    private void signOut() {
        mAuth.signOut();

        // go to sign in page
        Intent intentSignIn = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intentSignIn);
//        updateUI(null);
    }

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

                            // FIREBASE: if user not null, then go to specified intent
                            if (user != null) {
                                Log.d(TAG, "Within SignIn() - UID: " + user.getUid());

                                Log.d(TAG, "account type before: " + _accountType);

                                // get an instance of the current logged in user
                                mUsersDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                _loginUser = dataSnapshot.getValue(User.class);

                                                Log.d(TAG, "onDataChange: _loginUser data: " + _loginUser);

                                                // get account type of logged in user
                                                if (_loginUser != null) {
                                                    _accountType = _loginUser.get_accountType();
                                                }

                                                // add intent to go to specific sign-in screen
                                                if (_accountType != null) {
                                                    switch (_accountType) {
                                                        case "therapist":
                                                            // intent to go to example.com.hometherapy.Therapist's My Clients screen, passing user via extra message
                                                            Intent intentClients = new Intent(SignIn.this, MyClients.class);
                                                            startActivity(intentClients);
                                                            break;

                                                        case "client":
                                                            // intent to go to Exercises screen, passing user via extra message
                                                            Intent intentExercises = new Intent(SignIn.this, MyExercises.class);
                                                            startActivity(intentExercises);
                                                            break;

                                                        case "admin":
                                                            // intent to go to Users screen, passing user via extra message
                                                            Intent intentUsers = new Intent(SignIn.this, Users.class);
                                                            startActivity(intentUsers);
                                                            break;

                                                        case "pending":
                                                            // inform user to confirm email and wait for user to confirm account
                                                            Toast.makeText(SignIn.this, "Account is pending approval from administrator.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            break;

                                                        default:
                                                            // display error message - contact administrator
                                                            Toast.makeText(SignIn.this, "Error with signin. Please contact administrator.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Log.e(TAG, "error with sign in.");
                                                            break;
                                                    }

                                                } // END accountType check
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        }
                                );

                                // query the database for userID and get user
//                                Query query = mUsersDatabaseReference
//                                        .orderByChild("userID")
//                                        .equalTo(user.getUid());
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()) {
//                                            Log.d(TAG, "onDataChange: exists");
//                                        } else {
//                                            Log.d(TAG, "onDataChange: doesn't exist");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        Log.d(TAG, "onCancelled: ");
//                                    }
//                                });

                                // another method of query that was attempted
//                                query.addValueEventListener(_userValueEventListener);

                            } // END if user is null

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

    // FIREBASE: THIS WOULD BE GOOD TO TAKE THE USER TO HIS/HER FIRST DASHBOARD
    // IF THE APP IS OPENED AND THEY ARE ALREADY LOGGED IN
    // FOR NOW, COMMENT OUT
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }


} // END SignIn.class
