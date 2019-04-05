package example.com.hometherapy;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    // FIREBASE instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    // for log
    private static final String TAG = "SignIn_Activity";

    // private member variables
    private EditText _etSignInEmail;
    private EditText _etSignInPassword;
    private Button _btnLogin;
    private Button _btnRegister;
    private String _accountType;
    private User _loginUser;

    // validators for email, password, and phone number input fields
    private EmailValidator _emailValidator;
    private PasswordValidator _passwordValidator;

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Firebase references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

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

                // sign-in to firebase using email and password entered
                signIn(signInEmail, signInPassword);

                // verify current user
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "after call to signIn() - UID: " + user.getUid());
                    Log.d(TAG, "after call to signIn() - email: " + user.getEmail());
                    Log.d(TAG, "after call to signIn() - name: " + user.getDisplayName());
                }

            } // END Login Button onClick()

        }); // END Login Button

        // Register button listener
        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to register activity
                Intent intentRegister = new Intent(SignIn.this, Register.class);
                startActivity(intentRegister);
            }
        });

        // initialize auth state listener
        // note - I think this makes sense to be on the Main Activity where the splash screen is
        // that way if you are logged in, it goes right to your dashboard
        // auth state listener may need to be on other pages so that the app can be "torn down"
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // check if firebase user is logged in with firebase user
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

    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * [START on_start_check_user]
     * Called when the activity is becoming visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // if user is signed in - i.e if currentUser is not null - then
        // proceed with going to dashboard for given currentUser

    } // [END on_start_check_user]

    // Called when the activity is no longer visible to the user, because another
    // activity has been resumed and is covering this one.
    @Override
    protected void onStop() {
        super.onStop();
        //  signOut(); - thought about this, but doesn't make sense from this page
        // should be where data is stored, normally, but Firebase makes sure data is
        // synced, so not sure if this is needed
    }

    // use this for whenever we need to call sign out from any page
    private void signOut() {
        mAuth.signOut();

        // go to sign in page
        Intent intentSignIn = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intentSignIn);
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
        // don't do anything
    }

    // method for signing in, takes email and password and passes to Firebase signIn method
    private void signIn(String email, String password) {

        // Firebase method for signing in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithEmail:success");

                            // verify user
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Within SignIn() - User: " + user);

                            // if user not null, then go to specified intent
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
                                                    } // END switch statement

                                                } // END accountType check

                                            } // END on data change

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) { }

                                        } // END VALUE EVENT LISTENER CLASS DEFINITION

                                ); // END ADD LISTENER

                            } // END if user is null

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Invalid username and/or password. Please try again.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }); // END sign_in_with_email Firebase method

    } // END signIn() private method

} // END SignIn.class
