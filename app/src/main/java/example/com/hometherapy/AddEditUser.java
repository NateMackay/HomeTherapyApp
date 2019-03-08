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

    // note - add email validator here later

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
    private String _userAccountTypes[] = {"Account Type", "pending", "Client", "Therapist", "Admin"};
    private String _userClinicNames[] = {"Location", "pending", "Wenatchee", "Spokane", "Moses Lake", "Kennewick"};
    private String _userStatusNames[] = {"Status", "pending", "Active", "Inactive"};
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

        // need to do a null check for currentUser

        // initialize view elements
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

        // set initial views of EditText values based on current user
        _etUserFirstName.setText(_currentUser.getFirstName());
        _etUserLastName.setText(_currentUser.getLastName());
        _etUserEmail.setText(_currentUser.getEmail());
        _etUserPhone.setText(_currentUser.getPhone());


        // setup field validators here later

        // set views to variables
        String tvAddEditUser = _tvAddEditUser.getText().toString();
        String etUserFirstName = _etUserFirstName.getText().toString();
        String etUserLastName = _etUserLastName.getText().toString();
        String etUserEmail = _etUserEmail.getText().toString();
        String etUserPhone = _etUserPhone.getText().toString();
        String etUserPassword = _etUserPassword.getText().toString();
        String etUserPasswordConfirm = _etUserPasswordConfirm.getText().toString();

        // adapters
        _adapterAccountTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userAccountTypes);
        _adapterClinics = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userClinicNames);
        _adapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userStatusNames);

        // set adapters
        _spinUserAccountType.setAdapter(_adapterAccountTypes);
        _spinUserAssignedClinic.setAdapter(_adapterClinics);
        _spinUserStatus.setAdapter(_adapterStatus);

        // listeners
        _btnUserSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // set views to variables
                String etUserFirstName = _etUserFirstName.getText().toString();
                String etUserLastName = _etUserLastName.getText().toString();
                String etUserEmail = _etUserEmail.getText().toString();
                String etUserPhone = _etUserPhone.getText().toString();
                String etUserPassword = _etUserPassword.getText().toString();
                String etUserPasswordConfirm = _etUserPasswordConfirm.getText().toString();

                // add logic to confirm that password is the same

                // update current user with any changes
                _currentUser.setFirstName(etUserFirstName);
                _currentUser.setLastName(etUserLastName);
                _currentUser.setEmail(etUserEmail);
                _currentUser.setPhone(etUserPhone);
                _currentUser.setPassword(etUserPassword);

                // add logic to set the account type, location, and status
                // first need to display those values
                // see: https://android--code.blogspot.com/2015/08/android-spinner-set-selected-item.html

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
