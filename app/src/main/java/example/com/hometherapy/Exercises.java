package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Exercises extends AppCompatActivity {

    // for log
    private static final String TAG = "ExercisesActivity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";

    // member variables
    private ExerciseList _currentExercies;
    private Exercise _exercise;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;

    // views
    private ArrayAdapter<Exercise> _adapter;
    private ListView _listView;
    private Button _btnAddExercise;

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
        _currentExercies = _gson.fromJson(jsonExerciseList, ExerciseList.class);

        Log.d(TAG, " _currentExercies = _gson.fromJson(jsonExerciseList, ExerciseList.class:  " + _currentExercies);
        
        List<Exercise> tempExerciseList = _currentExercies.getExerciseList();

        // initialize array adapter and bind exercise list to it
        _adapter = new ArrayAdapter<Exercise>(this, android.R.layout.simple_selectable_list_item, tempExerciseList);

        // initialize view widgets
        _listView = (ListView) findViewById(R.id.lvExerciseList);
        _btnAddExercise = (Button) findViewById(R.id.btnAddExercise);


        // set the adapter to the list view
        _listView.setAdapter(_adapter);

        // execute task in background
        new AddDataToListTask().execute();

        _btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExercise = new Intent(Exercises.this, AddExerciseToLibrary.class);
                startActivity(intentAddExercise);
            }
        });
    }

    private class AddDataToListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // add some Exercises to the list
            //_ExercisesList.add("TH Sound");
            //_ExercisesList.add("R Sound");
            //_ExercisesList.add("S Sound");
            //_ExercisesList.add("Basic Swallow");
            //_ExercisesList.add("3 Step Directions");



            // simulate time delay
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}


