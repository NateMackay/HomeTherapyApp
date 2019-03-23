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

    // validators for email, password, and phone number input fields
    private EmailValidator _emailValidator;
    private PasswordValidator _passwordValidator;
    private PhoneNumberValidator _phoneNumberValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        // register views
        _btnCreateAccount = (Button) findViewById(R.id.btnRegCreateAccount);
        _etFirstName = (EditText) findViewById(R.id.etRegFirstName);
        _etLastName = (EditText) findViewById(R.id.etRegLastName);
        _etEmail = (EditText) findViewById(R.id.etRegEmail);
        _etPhone = (EditText) findViewById(R.id.etRegPhone);
        _etPassword = (EditText) findViewById(R.id.etRegPassword);
        _etPasswordConfirm = (EditText) findViewById(R.id.etRegPasswordConfirm);

        // setup field validators
        // email
        _emailValidator = new EmailValidator();
        _etEmail.addTextChangedListener(_emailValidator);
        // password
        _passwordValidator = new PasswordValidator();
        _etPassword.addTextChangedListener(_passwordValidator);
        // phone number
        _phoneNumberValidator = new PhoneNumberValidator();
        _etPhone.addTextChangedListener(_phoneNumberValidator);

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

                // validate password
                if (!_passwordValidator.isValid()) {
                    _etPassword.setError("Invalid Password");
                    Toast.makeText(Register.this, "Password must,\n" +
                            "contain at least one digit,\n" +
                            "contain at least one lower case character,\n" +
                            "contain at least one upper case character,\n" +
                            "contain at least one special character, and" +
                            "be between 8 and 40 characters long", Toast.LENGTH_LONG).show();
                    _etPassword.requestFocus();
                    return;
                }

                // verify password confirmation matches password
                if (!_etPassword.getText().toString().equals(_etPasswordConfirm.getText().toString())) {
                    _etPasswordConfirm.setError("Password does not match");
                    _etPasswordConfirm.requestFocus();
                    return;
                }

                // validate phone number
                if (!_phoneNumberValidator.isValid()) {
                    _etPhone.setError("Invalid phone number.");
                    Toast.makeText(Register.this,
                            "Phone number must be of the form," +
                                    "1234567890,.\n" +
                                    "123-456-7890,\n" +
                                    "(123)456-7890, or\n" +
                                    "(123)4567890", Toast.LENGTH_LONG).show();
                    _etPhone.requestFocus();
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
                String firstName = _etFirstName.getText().toString();
                String lastName = _etLastName.getText().toString();
                String phone = _etPhone.getText().toString();
                String userID = "pending";
                String status = "pending";
                String assignedClinic = "pending";
                String accountType = "pending";
                String assignedTherapist = "pending";

                // FIREBASE create new user account
                createAccount(email, password);

                // FIREBASE: this may be necessary, but the User class will likely need
                // to change, so that we are storing the User ID so that that user
                // can be associated with the other data we want to capture and store in firestore
                // LIKELY MOVE THIS TO TASK SUCCESSFUL OR TO ANOTHER FUNCTION THAT IS CALLED
                // AFTER TASK IS SUCCESSFUL
                User newUser = new User(email, password, firstName, lastName, phone, userID,
                        status, assignedClinic, accountType, assignedTherapist);


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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "onStart-currentUser: " + currentUser);

        updateUI(currentUser);
    }

    public void createAccount(String email, String password) {

        // put in email validator here, or keep above in onclick function

        // showProgressDialog(); - consider adding this functionality

        // create Firebase user
        // note that the addOnCompleteListener is "after" the create user function
        // the createUserWithEmailAndPassword(String email, String password) is a
        // member function of the FirebaseAuth class
        // the listener is just an override - possibly not necessary, except
        // what do you want to do after the user has been created - go back to sign in page?
        // maybe this is where you will add an instance of the User class data
        // so other information can be stored for user, such as phone, etc.
        // or maybe it directs the user to go to their profile to update/add that
        // information there and not at the register screen
        // if the task is unsuccessful, then it just displays the toast
        // - what would be the exception
        // - is there an exception for user account already created with email
        //   that would prompt the user to create an account with a different email address?
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            // set name and phone info in Firebase user profile
//                            String phone = _etPhone.getText().toString();
                            String displayName = String.format("%s %s", _etFirstName.getText().toString(), _etLastName.getText().toString());

                            // reference for updating phone numbers - will be required for messaging no doubt
                            // but not as easy as just updating the phone number
                            // ref: https://stackoverflow.com/questions/45304912/firebase-users-setting-phone-number
                            // NOTE: not sure we want to do this - phone numbers are associated with user accounts
                            // for an alternate sign-in method - probably just want to stick with email/password method
                            // we can store the phone number just for display purposes in the User database, just like
                            // we store status, assigned clinic, therapist, etc.
                            // we probably want to store first name and last name separately in the User database as well
                            // but we can use the display name field for now
                            // QUESTION: do we want display name to be different than first and last name? maybe. we could
                            // use display name throughout the app, and we may not need first and last name at all anyway
                            // except therapists/admin would want to know first/last name information to
                            // know which client they are working with
                            // probably good to have first, last, and displayname as 3 separate fields in registration tab

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            if (user != null) {
                                user.updateProfile(profileUpdates);
//                                user.updatePhoneNumber(phone); - FIREBASE: can't do this - see ref above

                                // confirm user ID matches what is in Firebase authentication console - for testing
                                Log.d(TAG, "user ID: " + user.getUid());
                            }

                            // send verification email upon successful creation of user
                            // NOTE: change custom NonNull to add the support annotation instead throughout app where used
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Verification email sent.");
                                                }
                                            }
                                        });
                            }

                            // upon successful completion, go back to sign-in page
                            Intent intentSignIn = new Intent(Register.this, SignIn.class);
                            startActivity(intentSignIn);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
