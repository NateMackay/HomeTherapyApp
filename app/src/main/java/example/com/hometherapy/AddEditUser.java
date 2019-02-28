package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddEditUser extends AppCompatActivity {

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

    private String _userAccountTypes[] = {"Account Type", "Therapist", "Admin"};
    private String _userClinicNames[] = {"Location", "Wenatchee", "Spokane", "Moses Lake", "Kennewick"};
    private String _userStatusNames[] = {"Status", "Pending", "Active", "Inactive"};
    private ArrayAdapter<String> _adapterAccountTypes;
    private ArrayAdapter<String> _adapterClinics;
    private ArrayAdapter<String> _adapterStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_user);

        _spinUserAccountType = (Spinner) findViewById(R.id.spinUserAccountType);
        _spinUserAssignedClinic = (Spinner) findViewById(R.id.spinUserAssignedClinic);
        _spinUserStatus = (Spinner) findViewById(R.id.spinUserStatus);

        _adapterAccountTypes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userAccountTypes);
        _adapterClinics = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userClinicNames);
        _adapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _userStatusNames);

        _spinUserAccountType.setAdapter(_adapterAccountTypes);
        _spinUserAssignedClinic.setAdapter(_adapterClinics);
        _spinUserStatus.setAdapter(_adapterStatus);

        _btnUserSave = (Button) findViewById(R.id.btnUserSave);

        _btnUserSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentUsers = new Intent(AddEditUser.this, Users.class);
                startActivity(intentUsers);
            }
        });

    }

}
