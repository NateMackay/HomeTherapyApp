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

public class AddEditClient extends AppCompatActivity {

    private TextView _tvAddEditClient;
    private EditText _etFirstName;
    private EditText _etLastName;
    private EditText _etEmail;
    private EditText _etPhone;
    private EditText _etPassword;
    private EditText _etPasswordConfirm;
    private Spinner _spinTherapist;
    private Spinner _spinClinic;
    private Spinner _spinStatus;
    private Button _btnSave;

    String _therapistNames[] = {"Therapist", "Adam Therapist", "Bonnie Therapist", "Charlie Therpaist"};
    String _clinicNames[] = {"Location", "Wenatchee", "Spokane", "Moses Lake", "Kennewick"};
    String _statusNames[] = {"Status", "Pending", "Active", "Inactive"};
    ArrayAdapter<String> _adapterTherapists;
    ArrayAdapter<String> _adapterClinics;
    ArrayAdapter<String> _adapterStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_client);

        _spinTherapist = (Spinner) findViewById(R.id.spinTherapist);
        _spinClinic = (Spinner) findViewById(R.id.spinClinic);
        _spinStatus = (Spinner) findViewById(R.id.spinStatus);

        _adapterTherapists = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _therapistNames);
        _adapterClinics = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _clinicNames);
        _adapterStatus = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _statusNames);

        _spinTherapist.setAdapter(_adapterTherapists);
        _spinClinic.setAdapter(_adapterClinics);
        _spinStatus.setAdapter(_adapterStatus);

        _btnSave = (Button) findViewById(R.id.btnSave);

        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClients = new Intent(AddEditClient.this, Clients.class);
                startActivity(intentClients);
            }
        });
    }
}
