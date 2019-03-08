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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

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
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";
//    public static final String EXERCISE_DATA = "exerciseData";
//    public static final String ASSIGNED_EXERCISE_DATA = "assignedExerciseData";

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

                // verify password confirmation matches password
                // note no other password validity
                // consider adding a password validation class
                if (!_etPassword.getText().toString().equals(_etPasswordConfirm.getText().toString())) {
                    _etPasswordConfirm.setError("Password does not match");
                    _etPasswordConfirm.requestFocus();
                    return;
                }

                // set values for new user object
                String email = _etEmail.getText().toString();
                String password = _etPassword.getText().toString();
                String firstName = _etFirstName.getText().toString();
                String lastName = _etLastName.getText().toString();
                String phone = _etPhone.getText().toString();
                String userID = "pending";
                String status = "pending";
                String assignedClinic = "pending";
                String accountType = "pending";

                // create temporary user object
                User newUser = new User(email, password, firstName, lastName, phone, userID,
                        status, assignedClinic, accountType);

                // pull existing user list from shared preferences into a JSON format "List" of
                // "User" or rather a UserList object
                // if nothing is in SHARED_PREFS then default value will be empty string
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String jsonUserList = sharedPreferences.getString(USER_DATA, "");
//                String jsonExerciseList = sharedPreferences.getString(EXERCISE_DATA, "");
//                String jsonAssignedExerciseList = sharedPreferences.getString(ASSIGNED_EXERCISE_DATA, "");

                // for adding users
                UserList currentUsers = new UserList();
                boolean userExists = false;

                // if there are existing users, convert JSON formatted string of existing users
                // into a UserList object and store in currentUsers
                // and check if user email is already in database
                if (jsonUserList.length() > 0) {
                    currentUsers = gson.fromJson(jsonUserList, UserList.class);

                    // get reference to list of users
                    List<User> tempUserList = currentUsers.getUserList();

                    // loop through list of current users to see if newUser email already exists
                    for (int i = 0; i < tempUserList.size(); i++) {
                        // if email entered by user is in the database, set flag to true
                        if (tempUserList.get(i).getEmail().equals(email)) {
                            userExists = true;
                        }
                    }
                }

                // add new user to database only if user does not already exist
                if (!userExists) {

                    // add new user account to list of existing users
                    currentUsers.addUser(newUser);

                    // convert existing user list back to JSON format
                    String updatedList = gson.toJson(currentUsers);

                    // update Shared Prefs with updated data
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_DATA, updatedList);
                    editor.apply();

                    // display contents for testing purposes
                    String fromSharedPrefs = sharedPreferences.getString(USER_DATA, "");
                    Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);
                } else {
                    _etEmail.setError("Email address is already registered");
                    _etEmail.requestFocus();
                    return;
                }

                // create intent to move to Exercises activity
                Intent intentSignIn = new Intent(Register.this, SignIn.class);

                // go to Exercises activity after registration
                startActivity(intentSignIn);
            }
        });
    }
}
