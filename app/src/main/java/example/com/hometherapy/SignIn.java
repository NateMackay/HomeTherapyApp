package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    // for log
    private static final String TAG = "SignIn_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // private member variables
    private EditText _etSignInEmail;
    private EditText _etSignInPassword;
    private Button _btnLogin;
    private Button _btnRegister;

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // initialize view elements
        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _btnRegister = (Button) findViewById(R.id.btnRegister);
        _etSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        _etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etSignInEmail.addTextChangedListener(_emailValidator);

        // login button events
        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!_emailValidator.isValid()) {
                    _etSignInEmail.setError("Invalid email address.");
                    _etSignInEmail.requestFocus();
                    return;
                }

                String signInEmail = _etSignInEmail.getText().toString();

                // pull existing user list from shared preferences into a JSON format "List" of
                // "User" or rather a UserList object
                // if nothing is in SHARED_PREFS then default value will be empty string
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String jsonUserList = sharedPreferences.getString(USER_DATA, "");

                // initiate GSON object
                Gson gson = new Gson();

                // for storing list of current users from Shared Prefs
                UserList currentUsers = null;

                // temp User object
                User loginUser = null;

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
                        if (tempUserList.get(i).getEmail().equals(signInEmail)) {
                            loginUser = tempUserList.get(i);
                        }
                    }
                } else {
                    _etSignInEmail.setError("Account not found. Please register for new account.");
                    _etSignInEmail.requestFocus();
                    return;
                }

                // get account type of user
                String accountType = null;
                if (loginUser != null) {
                    accountType = loginUser.get_accountType();
                }

                Log.d(TAG, "account type: " + accountType);

                if (accountType != null) {
                    if (accountType.equals("therapist")) {
                        // intent to go to Clients screen
                        Intent intentClients = new Intent(SignIn.this, Clients.class);
                        startActivity(intentClients);
                    } else if (accountType.equals("client")) {
                        // intent to go to Exercises screen
                        Intent intentExercises = new Intent(SignIn.this, Exercises.class);
                        startActivity(intentExercises);
                    } else if (accountType.equals("admin")) {
                        // intent to go to Users screen
                        Intent intentUsers = new Intent(SignIn.this, Users.class);
                        startActivity(intentUsers);
                    } else if (accountType.equals("pending")) {
                        // intent to go to add edit user screen - temporary - will
                        // need to display an error that user is not yet configured by admin
                        // once users screen is set up for admin to set user profile
                        // then this can be directed to error that user is not yet configured by admin
                        Intent intentAEU = new Intent(SignIn.this, AddEditUser.class);
                        startActivity(intentAEU);
                    } else {
                        _etSignInEmail.setError("Problem with account. See administrator. Error 001.");
                        _etSignInEmail.requestFocus();
                    }
                } else {
                    _etSignInEmail.setError("Problem with account. See administrator. Error 002.");
                    _etSignInEmail.requestFocus();
                }
            }
        });

        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Register activity
                Intent intentRegister = new Intent(SignIn.this, Register.class);

                // go to Register activity
                startActivity(intentRegister);
            }
        });
    }
}
