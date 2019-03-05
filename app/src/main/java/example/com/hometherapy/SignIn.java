package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    // for log
    private static final String TAG = "SignIn_Activity";

    // private member variables
    private EditText _etSignInEmail;
    private EditText _etSignInPassword;
    private Button _btnLogin;
    private Button _btnRegister;

    String _currentUserID;

    // map of key: email address and value: userID, to simulate firebase auth
    Map<String, String> _userIDs;

    // map to simulate database with key: userID and value: Map of data elements in user's account
    Map<String, Map<String, String>> _userAccounts;

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // load the map with Email and UserId combos - simulate Firebase
        _userIDs = new HashMap<>();
        _userIDs.put("bobbie@gmail.com", "user001");
        _userIDs.put("aj@gmail.com", "user002");
        _userIDs.put("john.doe@gmail.com", "user003");
        _userIDs.put("alex@admin.com", "user004");
        _userIDs.put("sally@myclinic.com", "user005");
        _userIDs.put("harry@myclinic.com", "user006");

        _userAccounts = new HashMap<>();

        Map<String, String> an001 = new HashMap<>();
        Map<String, String> an002 = new HashMap<>();
        Map<String, String> an003 = new HashMap<>();
        Map<String, String> an004 = new HashMap<>();
        Map<String, String> an005 = new HashMap<>();
        Map<String, String> an006 = new HashMap<>();

        // put data elements into each hash map
        an001.put("email", "bobbie@gmail.com");
        an001.put("password", "pass1234");
        an001.put("first name", "Bobbie");
        an001.put("last name", "Client");
        an001.put("phone", "509-555-1111");
        an001.put("status", "active");
        an001.put("assigned clinic", "Wenatchee");
        an001.put("account type", "client");

        an002.put("email", "aj@gmail.com");
        an002.put("password", "pass1234");
        an002.put("first name", "Alan");
        an002.put("last name", "Jackson");
        an002.put("phone", "509-555-2222");
        an002.put("status", "active");
        an002.put("assigned clinic", "Spokane");
        an002.put("account type", "client");

        an003.put("email", "john.doe@gmail.com");
        an003.put("password", "pass1234");
        an003.put("first name", "John");
        an003.put("last name", "Doe");
        an003.put("phone", "509-555-3333");
        an003.put("status", "active");
        an003.put("assigned clinic", "Moses Lake");
        an003.put("account type", "client");

        an004.put("email", "alex@admin.com");
        an004.put("password", "pass1234");
        an004.put("first name", "Alex");
        an004.put("last name", "Admin");
        an004.put("phone", "509-555-4444");
        an004.put("status", "active");
        an004.put("assigned clinic", "Wenatchee");
        an004.put("account type", "admin");

        an005.put("email", "sally@myclinic.com");
        an005.put("password", "pass1234");
        an005.put("first name", "Sally");
        an005.put("last name", "Therapist");
        an005.put("phone", "509-555-5555");
        an005.put("status", "active");
        an005.put("assigned clinic", "Spokane");
        an005.put("account type", "therapist");

        an006.put("email", "harry@myclinic.com");
        an006.put("password", "pass1234");
        an006.put("first name", "Harry");
        an006.put("last name", "Therapist");
        an006.put("phone", "509-555-6666");
        an006.put("status", "active");
        an006.put("assigned clinic", "Kennewick");
        an006.put("account type", "therapist");

        // then put the maps into the userAccounts map
        _userAccounts.put("user001", an001);
        _userAccounts.put("user002", an002);
        _userAccounts.put("user003", an003);
        _userAccounts.put("user004", an004);
        _userAccounts.put("user005", an005);
        _userAccounts.put("user006", an006);

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
                    _etSignInEmail.setError("Invalid Email");
                    _etSignInEmail.requestFocus();
                    return;
                }

                String SignInEmail = _etSignInEmail.getText().toString();

                // check to see if email is a valid user account
                if (!_userIDs.containsKey(SignInEmail)) {
                    _etSignInEmail.setError("Email Account Not Found");
                    _etSignInEmail.requestFocus();
                    return;
                }

                _currentUserID = _userIDs.get(SignInEmail);

                Log.d(TAG, "onClick: _currentUserID = " + _currentUserID);

                // this map reference is a reference to the same map object returned
                // this is okay, because we don't necessarily need to clone the data
                Map<String, String> currentUserInfo = _userAccounts.get(_currentUserID);

                String accountType = null;

                if (currentUserInfo != null && currentUserInfo.containsKey("account type")) {
                    accountType = currentUserInfo.get("account type");
                } else {
                    _etSignInEmail.setError("Data issues"); // should be toast, but it's late :)
                    _etSignInEmail.requestFocus();
                    return;
                }

                Log.d(TAG, "onClick: account type - " + accountType);

                if (accountType == "therapist") {
                    // intent to go to Clients screen
                    Intent intentClients = new Intent(SignIn.this, Clients.class);
                    startActivity(intentClients);
                } else if (accountType == "client") {
                    // intent to go to Exercises screen
                    Intent intentExercises = new Intent(SignIn.this, Exercises.class);
                    startActivity(intentExercises);
                } else if (accountType == "admin") {
                    // intent to go to Users screen
                    Intent intentUsers = new Intent(SignIn.this, Users.class);
                    startActivity(intentUsers);
                } else {
                    _etSignInEmail.setError("Data issues"); // should be toast, but it's late :)
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
