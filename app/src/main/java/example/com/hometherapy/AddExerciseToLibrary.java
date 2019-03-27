package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to add an exercise to the general exercise library. This has
 * no connection with any client. It is a general reusable library.
 * This should be accessed via the Exercises.Java / content_exercises.xml screen,
 * which is the general exercise library. A link in the menu in the
 * Exercises.Java / content_exercises.xml screen should be to
 * “Add exercise to library” which takes the user to this
 * AddExerciseToLibrary.java / add_exercise_to_library screen.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class AddExerciseToLibrary extends AppCompatActivity {

    // for log
    private static final String TAG = "Add_Exercise_Activity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mExerciseLibraryRef; // for reference to library node

    // Key for extra message for exercise ID to pass to activity
    // if empty, then this is a new exercise
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // Views
    private TextView _tvATLLabel;
    private EditText _etATLExerciseTitle;
    private Spinner _spinATLDiscipline;
    private Spinner _spinATLModality;
    private EditText _etATLAssignment;
    private EditText _etATLVideoLink;
    private Button _btnATLSave;

    // private member variables
    String _exerciseID;
    Boolean _retrievedExerciseSuccess;
    String _currentExerciseName;
    String _currentDiscipline;
    String _currentModality;
    String _currentAssignment;
    String _currentVideoLink;

    // String arrays to display different options for discipline and modality.
    String _discipline[] = {"Speech Therapy", "Physical Therapy", "Occupational Therapy"};
    String _modality[] = {"Expressive Language", "Receptive Language", "Swallow", "Range of Motion", "Sensory Integration"};
    ArrayAdapter<String> _adapterDiscipline;
    ArrayAdapter<String> _adapterModality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_library);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // set reference to library node and add listener to populate form if existing exercise
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mExerciseLibraryRef = mFirebaseDatabase.getReference().child("library");

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

        // get exercise ID from intent
        // if value is empty then this is for a new exercise
        Intent thisIntent = getIntent();
        _exerciseID = thisIntent.getStringExtra(MSG_EXERCISE_ID);
        Log.d(TAG, "verify current exercise ID: " + _exerciseID);

        // if there is an exercise ID, then populate fields with existing exercise data based on exercise name
        if (!_exerciseID.equals("")) {

            // set up query for listener specific to exercise ID
            // event listener takes care of populating existing exercise values to view
            Query query = mExerciseLibraryRef.orderByChild("_exerciseID").equalTo(_exerciseID);
            query.addListenerForSingleValueEvent(valueEventListenerExerciseID);
        }

        // save changes button to save exercise to library
        _btnATLSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set values for new exercise object from view objects
                String exerciseTitle = _etATLExerciseTitle.getText().toString();
                String discipline = _spinATLDiscipline.getSelectedItem().toString();
                String modality = _spinATLModality.getSelectedItem().toString();
                String assignment = _etATLAssignment.getText().toString();
                String videoLink = _etATLVideoLink.getText().toString();

                Log.d(TAG, "onClick: _exerciseID = " + _exerciseID);
                String exerciseID = _exerciseID; // will be empty if new exercise or will be existing exercise
                Map<String, Object> exerciseValues; // temporary map to store exercise values

                // if new exercise, then create a new exercise and convert values to temporary map
                if (exerciseID.isEmpty()) {
                    // write new exercise to library - make this a separate method and method call here
                    // reference: https://firebase.google.com/docs/database/android/read-and-write
                    // push() creates a new random key, then we get the key to store it temporarily
                    exerciseID = mExerciseLibraryRef.push().getKey();

                    // create a new exercise from above
                    Exercise tempExercise = new Exercise(exerciseID, exerciseTitle, discipline, modality, assignment, videoLink);

                    // create a map from the tempExercise object using Exercise member function toMap()
                    exerciseValues = tempExercise.toMap();

                } else {
                    // if we are updating an existing exercise
                    // update values into a temporary map for uploading to Firebase db
                    exerciseValues = new HashMap<>();
                    exerciseValues.put("_exerciseID", exerciseID);
                    exerciseValues.put("_exerciseTitle", exerciseTitle);
                    exerciseValues.put("_discipline", discipline);
                    exerciseValues.put("_modality", modality);
                    exerciseValues.put("_assignment", assignment);
                    exerciseValues.put("_videoLink", videoLink);
                }

                // update values to firebase
                if (exerciseID != null) {
                    mExerciseLibraryRef.child(exerciseID).updateChildren(exerciseValues);
                }

                // go back to library view
                Intent intentExercises = new Intent(AddExerciseToLibrary.this, Exercises.class);
                startActivity(intentExercises);
            }

        }); // END save button

    } // END onCreate()

    ValueEventListener valueEventListenerExerciseID = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Exercise exercise = new Exercise();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    exercise = snapshot.getValue(Exercise.class);
                }
                if (exercise != null) {

                    // populate view with data from exercise
                    _etATLExerciseTitle.setText(exercise.get_exerciseName());
                    _etATLAssignment.setText(exercise.get_assignment());
                    _etATLVideoLink.setText(exercise.get_videoLink());

                    // get indices of discipline and modality for setting each value to the spinner
                    int iDiscipline = Arrays.asList(_discipline).indexOf(exercise.get_discipline());
                    int iModality = Arrays.asList(_modality).indexOf(exercise.get_modality());

                    // set spinner values - note a value of -1 means indexOf() did not find value searching for
                    if (iDiscipline >= 0) {
                        _spinATLDiscipline.setSelection(iDiscipline);
                    }

                    if (iModality >= 0) {
                        _spinATLModality.setSelection(iModality);
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };


} // END AddExerciseToLibrary.class
