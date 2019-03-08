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

public class AddExerciseToLibrary extends AppCompatActivity {

    // for log
    private static final String TAG = "Add_Exercise_Activity";

    // Views
    private TextView _tvATLLabel;
    private EditText _etATLExerciseTitle;
    private Spinner _spinATLDiscipline;
    private Spinner _spinATLModality;
    private EditText _etATLAssignment;
    private Button _btnATLSave;

    // String arrays to display different options for discipline and modality.
    String _discipline[] = {"Speech Therapy", "Physical Therapy", "Occupational Therapy"};
    String _modality[] = {"Expressive Language", "Receptive Language", "Swallow", "Range of Motion", "Sensory Integration"};
    ArrayAdapter<String> _adapterDiscipline;
    ArrayAdapter<String> _adapterModality;

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_library);

        // Register Views
        _tvATLLabel = (TextView) findViewById(R.id.tvATLLabel);
        _etATLExerciseTitle = (EditText) findViewById(R.id.etATLExerciseTitle);
        _spinATLDiscipline = (Spinner) findViewById(R.id.spinATLDiscipline);
        _spinATLModality = (Spinner) findViewById(R.id.spinATLModality);
        _etATLAssignment = (EditText) findViewById(R.id.etATLAssignment);
        _btnATLSave = (Button) findViewById(R.id.btnATLSave);

        // Set up adapters
        _adapterDiscipline = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _discipline);
        _adapterModality = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _modality);

        _spinATLDiscipline.setAdapter(_adapterDiscipline);
        _spinATLModality.setAdapter(_adapterModality);

        _btnATLSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // register gson object
                Gson gson = new Gson();

                // Any validation?

                // set values for new exercise object
                String exerciseTitle = _etATLExerciseTitle.getText().toString();
                String discipline = _spinATLDiscipline.getSelectedItem().toString();
                String modality = _spinATLModality.getSelectedItem().toString();
                String assignment = _etATLAssignment.getText().toString();
                String videoLink = "video link"; // temp variable for now.

                // create temporary exercise object
                Exercise newExercise = new Exercise(exerciseTitle, discipline, modality, assignment, videoLink);

                // pull existing exercise list from shared preferences into a JSON format "List" of
                // "Exercise" or rather a ExerciseList object
                // if nothing is in SHARED_PREFS then default value will be empty string
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String jsonExerciseList = sharedPreferences.getString(EXERCISE_DATA, "");

                // for adding exercises
                ExerciseList currentExercises = new ExerciseList();
                boolean exerciseExists = false;

                // if there are existing exercises, convert JSON formatted string of existing
                // exercises into a ExerciseList object and store in currentExercises
                // and check if exercise title is already in database
                if (jsonExerciseList.length() > 0) {
                    currentExercises = gson.fromJson(jsonExerciseList, ExerciseList.class);

                    // get reference to list of exercises
                    List<Exercise> tempExerciseList = currentExercises.getExerciseList();

                    // loop through list of current users to see if new Exercise name already exists
                    for (int i = 0; i < tempExerciseList.size(); i++) {
                        // if exercise name entered by user is in the database, set flag to true
                        if (tempExerciseList.get(i).get_exerciseName().equals(exerciseTitle)) {
                            exerciseExists = true;
                        }
                    }
                }

                // add new exercise to database only if exercise does not already exist
                if (!exerciseExists) {

                    // add new exercise account to list of existing exercises
                    currentExercises.addExercise(newExercise);

                    // convert existing exercise list back to JSON format
                    String updatedList = gson.toJson(currentExercises);

                    // update Shared Prefs with updated data
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(EXERCISE_DATA, updatedList);
                    editor.apply();

                    // display contents for testing purposes
                    String fromSharedPrefs = sharedPreferences.getString(EXERCISE_DATA, "");
                    Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);
                } else {
                    _etATLExerciseTitle.setError("Exercise already exists");
                    _etATLExerciseTitle.requestFocus();
                    return;
                }


                 Intent intentExercises = new Intent(AddExerciseToLibrary.this, Exercises.class);
                 startActivity(intentExercises);
            }
        });

    }
}
