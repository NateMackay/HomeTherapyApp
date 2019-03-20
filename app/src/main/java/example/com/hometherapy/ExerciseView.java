package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

/**
 * A read-only version of an exercise.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class ExerciseView extends AppCompatActivity {

    // for log
    private static final String TAG = "ExerciseView";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";

    // Key for extra message for exercise name to pass to activity
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISENAME";
    private String _currentExerciseName;

    // private member variables
    private ExerciseList _currentExercises;
    private Exercise _currentExercise;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    // views
    private TextView tv_Discipline;
    private TextView tv_Modality;
    private TextView tv_VideoLink;
    private TextView tv_Assignment;
    private TextView tv_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);

        // register views
        tv_Discipline = (TextView) findViewById(R.id.tvEVDiscipline);
        tv_Modality = (TextView) findViewById(R.id.tvEVModality);
        tv_VideoLink = (TextView) findViewById(R.id.tvEV_VideoLink);
        tv_Assignment = (TextView) findViewById(R.id.tvEVAssignment);
        tv_Name = (TextView) findViewById(R.id.tvEVNameLabel);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonExerciseList = _sharedPreferences.getString(EXERCISE_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get exercise name from extra message
        Intent thisIntent = getIntent();
        _currentExerciseName = thisIntent.getStringExtra(MSG_EXERCISE_NAME);
        Log.d(TAG, "verify current exercise: " + _currentExerciseName);

        // deserialize sharedPrefs JSON user database into List of Users
        _currentExercises = _gson.fromJson(jsonExerciseList, ExerciseList.class);

        if (_currentExercises != null) {
            List<Exercise> tempExerciseList = _currentExercises.getExerciseList();

            // find matching exercise by name
            // reference: section 3.5 on this page https://www.baeldung.com/find-list-element-java
            _currentExercise = tempExerciseList.stream()
                    .filter(exercise -> _currentExerciseName.equals(exercise.get_exerciseName()))
                    .findAny()
                    .orElse(null);
        } else {
            Log.e(TAG, "_currentExercises is empty");
        }

        // check current user
        Log.d(TAG, "currentExercise: " + _currentExerciseName);

        // set values of Text Views based on current exercise
        // only if _currentExercise is not null
        if (_currentExercise != null) {
            tv_Name.setText(_currentExercise.get_exerciseName());
            tv_Discipline.setText(_currentExercise.get_discipline());
            tv_Discipline.setText(_currentExercise.get_discipline());
            tv_Modality.setText(_currentExercise.get_modality());
            tv_VideoLink.setText(_currentExercise.get_videoLink());
            tv_Assignment.setText(_currentExercise.get_assignment());
        }

    }

}
