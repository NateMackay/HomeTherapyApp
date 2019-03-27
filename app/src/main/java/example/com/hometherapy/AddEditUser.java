package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This screen is used to add a new user or edit an existing user.
 * It is accessed only from the Users.java / .xml view.
 * If the user clicks “Add New User” from the users view,
 * it will open up a blank Add Edit User screen. If the user
 * clicks on an existing user, it will feed that user information
 * into the same Add Edit User screen, allowing the admin user
 * to edit the user information, including changing the password.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class AddEditUser extends AppCompatActivity {

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersDatabaseReference;

    // for log
    private static final String TAG = "AEU_Activity";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_PASSED_UID = "example.com.hometherapy.PASSED_UID";

    // validators for email, password, and phone number input fields
    private EmailValidator _emailValidator;
    private PasswordValidator _passwordValidator;
    private PhoneNumberValidator _phoneNumberValidator;

    // private member variables
    private TextView _tvAddEditUser;
    private EditText _etUserFirstName;
    private EditText _etUserLastName;
    private EditText _etUserEmail;
    private EditText _etUserPhone;
    private EditText _etUserPassword;
    private EditText _etUserPasswordConfirm;
    private Spinner _spinUserAccountType;
    private Spinner _spinUserAssignedClinic;
    private Spinner _spinUserStatus;
    private Spinner _spinAssignedTherapist;
    private Button _btnUserSave;
    private List<Therapist> _therapistList;
    private String _passedUID;
    private boolean _isNewUser;

    private List<Therapist> _therapistListTest;

    // array adapters for spinner views
    private String _userAccountTypes[] = {"Account Type", "pending", "client", "therapist", "admin"};
    private String _userClinicNames[] = {"Location", "pending", "wenatchee", "spokane", "moses lake", "kennewick"};
    private String _userStatusNames[] = {"Status", "pending", "active", "inactive"};
    // convert these arrays to resources
    // see reference: https://developer.android.com/guide/topics/ui/controls/spinner
    // follow the example to set the layout of the drop down list when it appears

    private ArrayAdapter<String> _adapterAccountTypes;
    private ArrayAdapter<String> _adapterClinics;
    private ArrayAdapter<String> _adapterStatus;
    private ArrayAdapter<Therapist> _adapterTherapists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // add listener to database reference
        // instantiate Firebase RTDB and DBREF
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

        // register views
        _tvAddEditUser = (TextView) findViewById(R.id.tvAEULabel);
        _etUserFirstName = (EditText) findViewById(R.id.etAEUFirstName);
        _etUserLastName = (EditText) findViewById(R.id.etAEULastName);
        _etUserEmail = (EditText) findViewById(R.id.etAEUEmail);
        _etUserPhone = (EditText) findViewById(R.id.etAEUPhone);
        _etUserPassword = (EditText) findViewById(R.id.etAEUPassword);
        _etUserPasswordConfirm = (EditText) findViewById(R.id.etAEUPasswordConfirm);
        _spinUserAccountType = (Spinner) findViewById(R.id.spinAEUAccountType);
        _spinUserAssignedClinic = (Spinner) findViewById(R.id.spinAEUAssignedClinic);
        _spinAssignedTherapist = (Spinner) findViewById(R.id.spinAEUAssignedTherapist);
        _spinUserStatus = (Spinner) findViewById(R.id.spinAEUStatus);
        _btnUserSave = (Button) findViewById(R.id.btnAEUSave);

        // setup field validators
        // email
        _emailValidator = new EmailValidator();
        _etUserEmail.addTextChangedListener(_emailValidator);
        // password
        _passwordValidator = new PasswordValidator();
        _etUserPassword.addTextChangedListener(_passwordValidator);
        // phone number
        _phoneNumberValidator = new PhoneNumberValidator();
        _etUserPhone.addTextChangedListener(_phoneNumberValidator);

        // create list of therapists
        _therapistList = new ArrayList<>();

        // adapters
        _adapterAccountTypes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userAccountTypes);
        _adapterClinics = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userClinicNames);
        _adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userStatusNames);
        _adapterTherapists = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, _therapistList);
        _adapterTherapists.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // set adapters
        _spinUserAccountType.setAdapter(_adapterAccountTypes);
        _spinUserAssignedClinic.setAdapter(_adapterClinics);
        _spinUserStatus.setAdapter(_adapterStatus);
        _spinAssignedTherapist.setAdapter(_adapterTherapists);

        // get userID of user to edit from extra message
        // if null, then new user
        Intent thisIntent = getIntent();
        _passedUID = thisIntent.getStringExtra(MSG_PASSED_UID);

        if (_passedUID.equals("")) {
            _isNewUser = true;
        } else {
            _isNewUser = false;
        }

        Log.d(TAG, "verify passed UserID: " + _passedUID);

        // query user database based on therapist
        Query queryTherapistList = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("_accountType")
                .equalTo("therapist");
        Log.d(TAG, "onCreate: therapist list 2" + _therapistList);
        queryTherapistList.addListenerForSingleValueEvent(valueEventListener);

        // query user database based on passed in UID
        Query query = mUsersDatabaseReference.orderByChild("userID").equalTo(_passedUID);
        query.addListenerForSingleValueEvent(valueEventListenerUser);

        // listeners
        _btnUserSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // validate email
                if (!_emailValidator.isValid()) {
                    _etUserEmail.setError("Invalid Email");
                    _etUserEmail.requestFocus();
                    return;
                }

                // validate password
                if (!_passwordValidator.isValid()) {
                    _etUserPassword.setError("Invalid Password");
                    Toast.makeText(AddEditUser.this, "Password must,\n" +
                            "contain at least one digit,\n" +
                            "contain at least one lower case character,\n" +
                            "contain at least one upper case character,\n" +
                            "contain at least one special character, and" +
                            "be between 8 and 40 characters long", Toast.LENGTH_LONG).show();
                    _etUserPassword.requestFocus();
                    return;
                }

                // verify password confirmation matches password
                // note no other password validity
                // consider adding a password validation class
                if (!_etUserPassword.getText().toString().equals(_etUserPasswordConfirm.getText().toString())) {
                    _etUserPasswordConfirm.setError("Password does not match");
                    _etUserPasswordConfirm.requestFocus();
                    return;
                }

                // validate phone number
                if (!_phoneNumberValidator.isValid()) {
                    _etUserPhone.setError("Invalid phone number.");
                    Toast.makeText(AddEditUser.this,
                            "Phone number must be of the form," +
                            "1234567890,.\n" +
                            "123-456-7890,\n" +
                            "(123)456-7890, or\n" +
                            "(123)4567890", Toast.LENGTH_LONG).show();
                    _etUserPhone.requestFocus();
                    return;
                }

                // set views to variables
                String etUserFirstName = _etUserFirstName.getText().toString();
                String etUserLastName = _etUserLastName.getText().toString();
                String etUserEmail = _etUserEmail.getText().toString();
                String etUserPhone = _etUserPhone.getText().toString();
                String etUserPassword = _etUserPassword.getText().toString();
                String spinUserAccountType = _spinUserAccountType.getSelectedItem().toString();
                String spinUserAssignedClinic = _spinUserAssignedClinic.getSelectedItem().toString();
                String spinUserStatus = _spinUserStatus.getSelectedItem().toString();

                Therapist selectedTherapist = (Therapist) _spinAssignedTherapist.getSelectedItem();
                String assignedTherapistUID = selectedTherapist.get_userID();
                String assignedTherapistName = selectedTherapist.get_firstName() + selectedTherapist.get_lastName();

                if (_isNewUser) {
                    // create a new user object from values on screen
                    createAccount(etUserEmail, etUserPassword);

                } else {
                    // update existing user

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

                    // update data
                    // reference: https://firebase.google.com/docs/database/admin/save-data
                    DatabaseReference userRef = mUsersDatabaseReference.child(_passedUID);

                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("_accountType", spinUserAccountType);
                    userUpdates.put("_assignedClinic", spinUserAssignedClinic);
                    userUpdates.put("_assignedTherapistName", assignedTherapistName);
                    userUpdates.put("_assignedTherapistUID", assignedTherapistUID);
                    userUpdates.put("_status", spinUserStatus);
                    userUpdates.put("firstName", etUserFirstName);
                    userUpdates.put("lastName", etUserLastName);
                    userUpdates.put("phone", etUserPhone);

                    // note, this is an async update
                    // ref: https://github.com/firebase/quickstart-android/issues/821
                    userRef.updateChildren(userUpdates);

                    // updateChildren has an add on complete listener
                    // consider adding this and only going back to users upon successful
                    // completion

                    // go back to Users activity after update
                    Intent intentUsers = new Intent(AddEditUser.this, Users.class);
                    startActivity(intentUsers);

                }
            }
        });

    } // END onCreate()

    // listener for user data
    ValueEventListener valueEventListenerUser = new ValueEventListener() {
        @Override
        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                }
                Log.d(TAG, "onDataChange: user = " + user);

                if (user != null) {

                    _etUserFirstName.setText(user.getFirstName());
                    _etUserLastName.setText(user.getLastName());
                    _etUserEmail.setText(user.getEmail());
                    _etUserPhone.setText(user.getPhone());

                    // get indices of user's account type, assigned clinic, and status
                    // this is for setting the value for each spinner
                    // ref: https://stackoverflow.com/questions/23160832/how-to-find-index-of-string-array-in-java-from-a-given-value
                    int iUserAccountType = Arrays.asList(_userAccountTypes).indexOf(user.get_accountType());
                    int iUserAssignedClinic = Arrays.asList(_userClinicNames).indexOf(user.get_assignedClinic());
                    int iUserStatus = Arrays.asList(_userStatusNames).indexOf(user.get_status());

                    String assignedTherapistUserID = user.get_assignedTherapistUID();
                    int iAssignedTherapist;
                    if (assignedTherapistUserID != null && _therapistList != null) {
                        iAssignedTherapist = _therapistList.indexOf(assignedTherapistUserID);
                        Log.d(TAG, "_therapistList: " + _therapistList);
                    } else {
                        iAssignedTherapist = -1;
                    }

                    // set spinner values based on current user
                    // note that a value of -1 means indexOf() did not find value searching for
                    if (iUserAccountType >= 0) {
                        _spinUserAccountType.setSelection(iUserAccountType);
                    }

                    if (iUserAssignedClinic >= 0) {
                        _spinUserAssignedClinic.setSelection(iUserAssignedClinic);
                    }

                    if (iUserStatus >= 0) {
                        _spinUserStatus.setSelection(iUserStatus);
                    }

                    if (iAssignedTherapist >= 0) {
                        _spinAssignedTherapist.setSelection(iAssignedTherapist);
                    } else {
                        _spinAssignedTherapist.setSelection(0);
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    // listener for user data to generate a filtered list of therapists
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
            _therapistList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        _therapistList.add(new Therapist(user.getUserID(), user.getFirstName(), user.getLastName()));
                    }
                }
            }
            // update adapter
            _adapterTherapists.notifyDataSetChanged();

            Log.d(TAG, "onDataChange: therapistList = " + _therapistList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    // for therapist spinner - to get value
    public void getSelectedTherapist (View v) {
        Therapist therapist = (Therapist) _spinAssignedTherapist.getSelectedItem();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        // Check if user is signed in (non-null), and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    } // [END on_start_check_user]

    // update user interface - if user is not authenticated, go back to sign-in screen
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // continue with loading app
            Log.d(TAG, "updateUI: NOT NULL - means authorized ");
        } else {
            Log.d(TAG, "updateUI: Firebase user is null. Move to Sign-in Activity");
            // move to sign-in screen if current user is not authenticated
            Intent intentLogIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intentLogIn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    // create new user function
    public void createAccount(String email, String password) {

        // put in email validator here, or keep above in onclick function

        // Firebase auth method call to create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@example.com.hometherapy.NonNull Task<AuthResult> task) {
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
                                String firstName = _etUserFirstName.getText().toString();
                                String lastName = _etUserLastName.getText().toString();
                                String phone = _etUserPhone.getText().toString();
                                String status = _spinUserStatus.getSelectedItem().toString();
                                String assignedClinic = _spinUserAssignedClinic.getSelectedItem().toString();
                                String accountType = _spinUserAccountType.getSelectedItem().toString();

                                Therapist selectedTherapist = (Therapist) _spinAssignedTherapist.getSelectedItem();
                                String assignedTherapistUID = selectedTherapist.get_userID();
                                String assignedTherapistName = selectedTherapist.get_firstName() + selectedTherapist.get_lastName();

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
                                // database is open to writing w/o authentication - NEED TO FIX

                                // save to users database with a key of userID
                                mUsersDatabaseReference.child(userID).setValue(newUser);
                            }

                            // upon successful completion, go back to sign-in page
                            Intent intentSignIn = new Intent(AddEditUser.this, SignIn.class);
                            startActivity(intentSignIn);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AddEditUser.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    } // end createAccount()

}
