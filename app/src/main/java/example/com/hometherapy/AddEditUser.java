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

        // activity elements
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
                Intent intentUsers = new Intent(AddEditUser.this, Users.class);
                startActivity(intentUsers);
            }
        });

    }

}
