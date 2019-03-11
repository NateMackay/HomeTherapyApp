package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.List;

/**
 * This is the class that is for the exercise library view
 * linked to activity_exercises.xml *
 */
public class Exercises extends AppCompatActivity {

    // for log
    private static final String TAG = "ExercisesActivity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";

    // Key for extra message for exercise name to pass to activity
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISENAME";

    // member variables
    private ExerciseList _currentExercises;
    private Exercise _exercise;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<Exercise> tempExerciseList;

    // views
    private ListView _lvExerciseList;
    private Button _btnAddExercise;

    // array adapter for exercise list
    private ExerciseListAdapter _adapterExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonExerciseList = _sharedPreferences.getString(EXERCISE_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON user database into List of Exercises
        _currentExercises = _gson.fromJson(jsonExerciseList, ExerciseList.class);

        // register view widgets
        _lvExerciseList = (ListView) findViewById(R.id.lvExerciseList);
        _btnAddExercise = (Button) findViewById(R.id.btnAddExercise);

        // if current exercise database is not empty, then proceed with getting list of exercises
        // and binding to array adapter
        if (_currentExercises != null) {

            tempExerciseList = _currentExercises.getExerciseList();

            Log.d(TAG, "tempExerciseList: " + tempExerciseList);

            // initialize array adapter and bind exercise list to it
            _adapterExerciseList = new ExerciseListAdapter(this, tempExerciseList);

            // set the adapter to the list view
            _lvExerciseList.setAdapter(_adapterExerciseList);
        }

        // add an on item click listener that will open up the existing exercise for editing
        _lvExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current exercise from position in list
                Exercise currentExercise = tempExerciseList.get(position);

                // intent to go to Exercise View screen, passing exercise via extra message
                Intent intentEV = new Intent(Exercises.this, ExerciseView.class);
                intentEV.putExtra(MSG_EXERCISE_NAME, currentExercise.get_exerciseName());
                startActivity(intentEV);
            }
        });

        _btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExercise = new Intent(Exercises.this, AddExerciseToLibrary.class);
                startActivity(intentAddExercise);
            }
        });
    }





    // future logic - add async tasks
    // execute task in background
//        new AddDataToListTask().execute();

//    private class AddDataToListTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            // add some Exercises to the list
//            //_ExercisesList.add("TH Sound");
//            //_ExercisesList.add("R Sound");
//            //_ExercisesList.add("S Sound");
//            //_ExercisesList.add("Basic Swallow");
//            //_ExercisesList.add("3 Step Directions");
//
//
//
//            // simulate time delay
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }

}


