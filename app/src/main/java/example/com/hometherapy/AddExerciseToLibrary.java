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

public class AddExerciseToLibrary extends AppCompatActivity {

    private TextView _tvATLLabel;
    private EditText _etATLExerciseTitle;
    private Spinner _spinATLDiscipline;
    private Spinner _spinATLModality;
    private EditText _etATLAssignment;
    private Button _btnATLSave;

    String _discipline[] = {"Speech Therapy", "Physical Therapy", "Occupational Therapy"};
    String _modality[] = {"Expressive Language", "Receptive Language", "Swallow", "Range of Motion", "Sensory Integration"};
    ArrayAdapter<String> _adapterDiscipline;
    ArrayAdapter<String> _adapterModality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_library);

        _tvATLLabel = (TextView) findViewById(R.id.tvATLLabel);
        _etATLExerciseTitle = (EditText) findViewById(R.id.etATLExerciseTitle);
        _spinATLDiscipline = (Spinner) findViewById(R.id.spinATLDiscipline);
        _spinATLModality = (Spinner) findViewById(R.id.spinATLModality);
        _etATLAssignment = (EditText) findViewById(R.id.etATLAssignment);
        _btnATLSave = (Button) findViewById(R.id.btnATLSave);

        _adapterDiscipline = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _discipline);
        _adapterModality = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _modality);

        _spinATLDiscipline.setAdapter(_adapterDiscipline);
        _spinATLModality.setAdapter(_adapterModality);

        _btnATLSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentExercises = new Intent(AddExerciseToLibrary.this, Exercises.class);
                startActivity(intentExercises);
            }
        });

    }
}
