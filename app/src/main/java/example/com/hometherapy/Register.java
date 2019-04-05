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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    // FIREBASE instances
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

        // instantiate Firebase RTDB and DBREF
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

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
                String email = _etEmail.getText().toString();
                String password = _etPassword.getText().toString();

                // FIREBASE create new user account
                createAccount(email, password);

            }
        });

    } // END onCreate()

    // create new user function
    public void createAccount(String email, String password) {

        // Firebase auth method call to create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

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
                        }

                    }
                });

    } // end createAccount()

} // END Register.class
