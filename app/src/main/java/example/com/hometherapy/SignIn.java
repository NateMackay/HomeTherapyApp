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

/**
 * Sign-in screen description
 * @author Team06
 * @version beta 1.0
 * @since   3/15/2019
 */
public class SignIn extends AppCompatActivity {

    // future feature: add to this class functionality to log to a file anytime someone has logged in

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
                    // there are no users in the list
                    // direct user to register for an account
                    _etSignInEmail.setError("Account not found. Please register for new account.");
                    _etSignInEmail.requestFocus();
                    return;
                }

                // if loginUser is still null, it means that user wasn't found in the list
                // direct user to register
                if (loginUser == null) {
                    // user is not in the list
                    // direct user to register for an account
                    _etSignInEmail.setError("Account not found. Please register for new account.");
                    _etSignInEmail.requestFocus();
                    return;
                }

                // if password entered does not match saved password, return to password field
                if (!loginUser.getPassword().equals(_etSignInPassword.getText().toString())) {
                    _etSignInPassword.setError("Password is incorrect. Re-enter password");
                    _etSignInPassword.requestFocus();
                    return;
                }

                // login user is not null and there is a password match
                // get account type of user and log account type
                String accountType = loginUser.get_accountType();
                Log.d(TAG, "account type: " + accountType);

                // since login user is not null and the user is a valid user
                // store user in current user shared prefs
                // note that this should replace whatever is in SP for LOGIN_USER
                // so should not need to clear it out
                String jsonLoginUser = gson.toJson(loginUser);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LOGIN_USER, jsonLoginUser);
                editor.apply();

                if (accountType != null) {
                    if (accountType.equals("therapist")) {
                        // intent to go to Therapist's My Clients screen, passing user via extra message
                        Intent intentClients = new Intent(SignIn.this, MyClients.class);
                        intentClients.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
//                        intentClients.putExtra(MSG_ACCT_TYPE, loginUser.get_accountType()); // not sure if we need this
                        startActivity(intentClients);
                    } else if (accountType.equals("client")) {
                        // intent to go to Exercises screen, passing user via extra message
                        Intent intentExercises = new Intent(SignIn.this, MyExercises.class);
                        intentExercises.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
//                        intentExercises.putExtra(MSG_ACCT_TYPE, loginUser.get_accountType()); // not sure if we need this
                        startActivity(intentExercises);
                    } else if (accountType.equals("admin")) {
                        // intent to go to Users screen, passing user via extra message
                        Intent intentUsers = new Intent(SignIn.this, Users.class);
                        intentUsers.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
                        startActivity(intentUsers);
                    } else if (accountType.equals("pending")) {
                        // intent to go to add edit user screen - temporary - will
                        // need to display an error that user is not yet configured by admin
                        // once users screen is set up for admin to set user profile
                        // then this can be directed to error that user is not yet configured by admin
                        Intent intentAEU = new Intent(SignIn.this, AddEditUser.class);
                        intentAEU.putExtra(MSG_USER_EMAIL, loginUser.getEmail());
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

    @Override
    public void onBackPressed() {
        // don't do anything
    }

}
