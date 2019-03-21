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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This screen is used to add a new user or edit an existing user.
 * It is accessed only from the Users.java / users.xml view.
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

    // for log
    private static final String TAG = "AEU_Activity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // Key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";
    public static final String MSG_AEU_EMAIL = "example.com.hometherapy.AEU_EMAIL";
    private String _adminUserEmail;
    private String _addEditUserEmail;

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
    private Spinner _spinAssignedTherapist;
    private Button _btnUserSave;
    private UserList _currentUsers;
    private User _currentUser;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    // array adapters for spinner views
    private String _userAccountTypes[] = {"Account Type", "pending", "client", "therapist", "admin"};
    private String _userClinicNames[] = {"Location", "pending", "wenatchee", "spokane", "moses lake", "kennewick"};
    private String _userStatusNames[] = {"Status", "pending", "active", "inactive"};
    private List<String> _therapists;
    private HashMap<String, String> _therapistNameEmailMap;
    private HashMap<String, String> _therapistEmailNameMap;
    // convert these arrays to resources
    // see reference: https://developer.android.com/guide/topics/ui/controls/spinner
    // follow the example to set the layout of the drop down list when it appears

    private ArrayAdapter<String> _adapterAccountTypes;
    private ArrayAdapter<String> _adapterClinics;
    private ArrayAdapter<String> _adapterStatus;
    private ArrayAdapter<String> _adapterTherapists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

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
        _emailValidator = new EmailValidator();
        _etUserEmail.addTextChangedListener(_emailValidator);

        // create list of therapists
        _therapists = new ArrayList<>(); // do I need to fill the list before setting the adapter? Right now it's null
        _therapistEmailNameMap = new HashMap<>(); // used to store Integer reference to email based on position of therapist in list
        _therapistNameEmailMap = new HashMap<>();

        // adapters
        _adapterAccountTypes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userAccountTypes);
        _adapterClinics = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userClinicNames);
        _adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _userStatusNames);
        _adapterTherapists = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _therapists);

        // set adapters
        _spinUserAccountType.setAdapter(_adapterAccountTypes);
        _spinUserAssignedClinic.setAdapter(_adapterClinics);
        _spinUserStatus.setAdapter(_adapterStatus);
//        _spinAssignedTherapist.setAdapter(_adapterTherapists);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account) from extra message of user to edit or if null, then new user
        Intent thisIntent = getIntent();
        _addEditUserEmail = thisIntent.getStringExtra(MSG_AEU_EMAIL);
        _adminUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify add_edit user: " + _addEditUserEmail);
        Log.d(TAG, "verify admin user: " + _adminUserEmail);

        // deserialize sharedPrefs JSON user database into List of Users
        _currentUsers = _gson.fromJson(jsonUserList, UserList.class);

        if (_currentUsers != null) {
            List<User> tempUserList = _currentUsers.getUserList();

            // find matching user account by email
            // reference: section 3.5 on this page https://www.baeldung.com/find-list-element-java
            _currentUser = tempUserList.stream()
                    .filter(user -> _addEditUserEmail.equals(user.getEmail()))
                    .findAny()
                    .orElse(null);

            List<User> therapistUserList = tempUserList.stream()
                    .filter(user -> user.get_accountType().equals("therapist"))
                    .collect(Collectors.toList());

            for (int i = 0; i < therapistUserList.size(); i++) {

                String fullName = (therapistUserList.get(i).getFirstName() + " " +
                        therapistUserList.get(i).getLastName());

                String therapistEmail = therapistUserList.get(i).getEmail();

                _therapists.add(fullName);

                _therapistEmailNameMap.put(therapistEmail, fullName);

                _therapistNameEmailMap.put(fullName, therapistEmail);

            }
        } else {
            // create a new user list if none is created and add a user to it
            _currentUsers = new UserList();
            _currentUser = new User("pending");
            _currentUsers.addUser(_currentUser);
        }

        // set adapter for therapists after list of therapists has been created
        _spinAssignedTherapist.setAdapter(_adapterTherapists);

        // check current user
        Log.d(TAG, "currentUser: " + _currentUser);

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

            String assignedTherapistEmail = _currentUser.get_assignedTherapist();
            int iAssignedTherapist;
            if (_therapistEmailNameMap.containsKey(assignedTherapistEmail)) {
                iAssignedTherapist = _therapists.indexOf(_therapistEmailNameMap.get(assignedTherapistEmail));
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
        } else {
            // create a new current user object to store stuff in so that it can be saved to the db
            // temporarily use this until fire base is implemented
            // may need to create a new fire base user
            // userID used here with the assumption that a user ID would be minimally required
            _currentUser = new User("pending");
            _currentUsers.addUser(_currentUser);
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

                String assignedTherapistEmail;

                if (!_therapists.isEmpty()) {

                    // get string name from spinner

                    Log.d(TAG, "therapists: " + _therapists);

                    String spinAssignedTherapist = _therapists.get(_spinAssignedTherapist.getSelectedItemPosition());

                    Log.d(TAG, "spinAssignedTherapist: " + spinAssignedTherapist);

                    if (_therapistNameEmailMap.containsKey(spinAssignedTherapist)) {
                        assignedTherapistEmail = _therapistNameEmailMap.get(spinAssignedTherapist);
                    } else {
                        assignedTherapistEmail = "";
                    }

                } else {
                    assignedTherapistEmail = "";
                }

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
                _currentUser.set_assignedTherapist(assignedTherapistEmail);

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
