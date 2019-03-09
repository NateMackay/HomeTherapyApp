package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class AddEditUser extends AppCompatActivity {

    // for log
    private static final String TAG = "AEU_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    private String _currentUserEmail;

    // validator for email input field
    private EmailValidator _emailValidator;

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
    private Button _btnUserSave;
    private UserList _currentUsers;
    private User _currentUser;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    // array adapters for spinner views
    private String _userAccountTypes[] = {"Account Type", "pending", "client", "therapist", "admin"};
    private String _userClinicNames[] = {"Location", "pending", "wenatchee", "spokane", "moses lake", "kennewick"};
    private String _userStatusNames[] = {"Status", "pending", "active", "inactive"};
    private ArrayAdapter<String> _adapterAccountTypes;
    private ArrayAdapter<String> _adapterClinics;
    private ArrayAdapter<String> _adapterStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account) from extra message
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // deserialize sharedPrefs JSON user database into List of Users
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);
        List<User> tempUserList = _currentUsers.getUserList();

        // find matching user account by email
        // reference: section 3.5 on this page https://www.baeldung.com/find-list-element-java
        _currentUser = tempUserList.stream()
                .filter(user -> _currentUserEmail.equals(user.getEmail()))
                .findAny()
                .orElse(null);

        // check current user
        Log.d(TAG, "currentUser: " + _currentUser);
        Log.e(TAG, _currentUser + "user not found");

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
        _spinUserStatus = (Spinner) findViewById(R.id.spinAEUStatus);
        _btnUserSave = (Button) findViewById(R.id.btnAEUSave);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etUserEmail.addTextChangedListener(_emailValidator);

        // adapters
        _adapterAccountTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userAccountTypes);
        _adapterClinics = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userClinicNames);
        _adapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userStatusNames);

        // set adapters
        _spinUserAccountType.setAdapter(_adapterAccountTypes);
        _spinUserAssignedClinic.setAdapter(_adapterClinics);
        _spinUserStatus.setAdapter(_adapterStatus);

        // set initial views of EditText values based on current user
        // only if _currentUser is not null
        if (_currentUser != null) {
            _etUserFirstName.setText(_currentUser.getFirstName());
            _etUserLastName.setText(_currentUser.getLastName());
            _etUserEmail.setText(_currentUser.getEmail());
            _etUserPhone.setText(_currentUser.getPhone());

            // get indices of _currentUser's account type, assigned clinic, and status
            // this is for setting the value for each spinner
            // ref: https://stackoverflow.com/questions/23160832/how-to-find-index-of-string-array-in-java-from-a-given-value
            int iUserAccountType = Arrays.asList(_userAccountTypes).indexOf(_currentUser.get_accountType());
            int iUserAssignedClinic = Arrays.asList(_userClinicNames).indexOf(_currentUser.get_assignedClinic());
            int iUserStatus = Arrays.asList(_userStatusNames).indexOf(_currentUser.get_status());

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
        }

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

                // verify password confirmation matches password
                // note no other password validity
                // consider adding a password validation class
                if (!_etUserPassword.getText().toString().equals(_etUserPasswordConfirm.getText().toString())) {
                    _etUserPasswordConfirm.setError("Password does not match");
                    _etUserPasswordConfirm.requestFocus();
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

                Log.d(TAG, "spin values: " + spinUserAccountType + ", " + spinUserAssignedClinic +
                        ", " + spinUserStatus);

                // update current user with any changes
                _currentUser.setFirstName(etUserFirstName);
                _currentUser.setLastName(etUserLastName);
                _currentUser.setEmail(etUserEmail);
                _currentUser.setPhone(etUserPhone);
                _currentUser.set_accountType(spinUserAccountType);
                _currentUser.set_assignedClinic(spinUserAssignedClinic);
                _currentUser.set_status(spinUserStatus);

                // update password only if password has been updated
                if (etUserPassword.length() > 0) {
                    _currentUser.setPassword(etUserPassword);
                }

                // convert updated UserList object back to JSON format
                String updatedList = _gson.toJson(_currentUsers);

                // update Shared Prefs with updated data
                SharedPreferences.Editor editor = _sharedPreferences.edit();
                editor.putString(USER_DATA, updatedList);
                editor.apply();

                // display contents for testing purposes
                String fromSharedPrefs = _sharedPreferences.getString(USER_DATA, "");
                Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);

                Intent intentUsers = new Intent(AddEditUser.this, Users.class);
                startActivity(intentUsers);
            }
        });

    }

}
