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
import java.util.List;

/**
 * Used to add an exercise to the general exercise library. This has
 * no connection with any client. It is a general reusable library.
 * This should be accessed via the Exercises.Java / exercises.xml screen,
 * which is the general exercise library. A link in the menu in the
 * Exercises.Java / exercises.xml screen should be to
 * “Add exercise to library” which takes the user to this
 * AddExerciseToLibrary.java / add_exercise_to_library screen.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class AddExerciseToLibrary extends AppCompatActivity {

    // for log
    private static final String TAG = "Add_Exercise_Activity";

    // Key for extra message for exercise name to pass to activity
    // this is needed only if needing to edit an existing exercise
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISENAME";

    // Views
    private TextView _tvATLLabel;
    private EditText _etATLExerciseTitle;
    private Spinner _spinATLDiscipline;
    private Spinner _spinATLModality;
    private EditText _etATLAssignment;
    private EditText _etATLVideoLink;
    private Button _btnATLSave;

    // private member variables
    SharedPreferences _sharedPreferences;
    Gson _gson;
    String _exerciseName;
    ExerciseList _currentExerciseLibrary;
    List <Exercise> _listOfExercisesFromLibrary;
    Exercise _currentExercise;

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
        _etATLVideoLink = (EditText) findViewById(R.id.etATLVideoLink);
        _btnATLSave = (Button) findViewById(R.id.btnATLSave);

        // Set up adapters
        _adapterDiscipline = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _discipline);
        _adapterModality = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _modality);

        _spinATLDiscipline.setAdapter(_adapterDiscipline);
        _spinATLModality.setAdapter(_adapterModality);

        // initialize GSON object
        _gson = new Gson();

        // pull existing exercise list from shared preferences into a JSON format "List" of
        // "Exercise" or rather a ExerciseList object
        // if nothing is in SHARED_PREFS then default value will be empty string
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonExerciseList = _sharedPreferences.getString(EXERCISE_DATA, "");

        // deserialize sharedPrefs JSON exercise library into ExerciseList class
        _currentExerciseLibrary = _gson.fromJson(jsonExerciseList, ExerciseList.class);

        // get name of exercise from intent
        // if name value in intent msg is ***new_exercise*** then this is for a new exercise
        Intent thisIntent = getIntent();
        _exerciseName = thisIntent.getStringExtra(MSG_EXERCISE_NAME);
        Log.d(TAG, "verify current exercise name: " + _exerciseName);

        if (_currentExerciseLibrary != null) {

            // if not new exercise, then populate fields with existing exercise data based on exercise name
            if (!_exerciseName.equals("***new_exercise***")) {

                // get list of exercises and filter exercise list by exercise name received in intent
                _listOfExercisesFromLibrary = _currentExerciseLibrary.getExerciseList();
                _currentExercise = _listOfExercisesFromLibrary.stream()
                        .filter(name -> _exerciseName.equals(name.get_exerciseName()))
                        .findAny()
                        .orElse(null);

                if (_currentExercise != null) {

                    // set views to exercise data
                    String discipline = _currentExercise.get_discipline();
                    String modality = _currentExercise.get_modality();
                    String assignment = _currentExercise.get_assignment();
                    String videoLink = _currentExercise.get_videoLink();

                    _etATLExerciseTitle.setText(_exerciseName);
                    _etATLAssignment.setText(assignment);
                    _etATLVideoLink.setText(videoLink);

                    // get indices of discipline and modality for setting each value to the spinner
                    int iDiscipline = Arrays.asList(_discipline).indexOf(discipline);
                    int iModality = Arrays.asList(_modality).indexOf(modality);

                    // set spinner values - note a value of -1 means indexOf() did not find value searching for
                    if (iDiscipline >= 0) {
                        _spinATLDiscipline.setSelection(iDiscipline);
                    }

                    if (iModality >= 0) {
                        _spinATLModality.setSelection(iModality);
                    }
                }  else {
                    Log.e(TAG, "error: _currentExercise is NULL");
                }
            }

        } else {
            // instantiate a new exercise library (i.e. ExerciseList class object)
            _currentExerciseLibrary = new ExerciseList();
        }

        // save exercise to exercise database in shared preferences after click of button
        _btnATLSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check to see if exercise title already exists in database
                // if list of exercises from library is null, then database is new and empty
                String exerciseTitle = _etATLExerciseTitle.getText().toString();
                boolean exerciseExists = false;
                if (_listOfExercisesFromLibrary != null) {
                    // loop through list of current users to see if new Exercise name already exists
                    for (int i = 0; i < _listOfExercisesFromLibrary.size(); i++) {
                        // if exercise name entered by user is in the database, set flag to true
                        if (_listOfExercisesFromLibrary.get(i).get_exerciseName().equals(exerciseTitle)) {
                            exerciseExists = true;
                        }
                    }
                    // if exercise exists in library, return focus to the exercise name edit text
                    // however, if we simply matched our own exercise in the library, no need
                    // to go back as we can save/update our existing exercise
                    if (exerciseExists && !exerciseTitle.equals(_exerciseName)) {
                        _etATLExerciseTitle.setError("Exercise name already exists. Try a different name.");
                        _etATLExerciseTitle.requestFocus();
                        return;
                    }
                }

                // set values for new exercise object from view objects
                String discipline = _spinATLDiscipline.getSelectedItem().toString();
                String modality = _spinATLModality.getSelectedItem().toString();
                String assignment = _etATLAssignment.getText().toString();
                String videoLink = _etATLVideoLink.getText().toString();

                // check if this is a new exercise or not
                // if not new, update exercise with data from fields selected
                // if new exercise, create new exercise object and add it to the library
                if (_currentExercise != null) {
                    // update values
                    _currentExercise.set_exerciseName(exerciseTitle);
                    _currentExercise.set_discipline(discipline);
                    _currentExercise.set_modality(modality);
                    _currentExercise.set_assignment(assignment);
                    _currentExercise.set_videoLink(videoLink);

                } else {
                    // this is a new exercise, create new exercise and add to the library
                    _currentExerciseLibrary.addExercise(new Exercise(exerciseTitle, discipline, modality, assignment, videoLink));
                }

                // put exercise list back to JSON format
                String updatedList = _gson.toJson(_currentExerciseLibrary);

                // update Shared Prefs with updated data
                SharedPreferences.Editor editor = _sharedPreferences.edit();
                editor.putString(EXERCISE_DATA, updatedList);
                editor.apply();

                // display contents for testing purposes
                String fromSharedPrefs = _sharedPreferences.getString(EXERCISE_DATA, "");
                Log.d(TAG, "fromSharedPrefs: " + fromSharedPrefs);

                // go back to library view
                Intent intentExercises = new Intent(AddExerciseToLibrary.this, Exercises.class);
                startActivity(intentExercises);
            }

        });

    }
}
