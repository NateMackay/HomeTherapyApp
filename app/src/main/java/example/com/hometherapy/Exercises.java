package example.com.hometherapy;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Exercises extends AppCompatActivity {

    // for log
    private static final String TAG = "ExercisesActivity";

    // member variables
    private List<String> _ExercisesList;
    private ArrayAdapter<String> _adapter;
    private ListView _listView;
    private Button _btnAddExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // instantiate a new list of Exercises
        _ExercisesList = new ArrayList<String>();

        // initialize array adapter and bind exercise list to it
        _adapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, _ExercisesList);

        // initialize view widgets
        _listView = (ListView) findViewById(R.id.lvExerciseList);
        _btnAddExercise = (Button) findViewById(R.id.btnAddExercise);

        // execute task in background
        new addDataToListTask().execute();

        // set the adapter to the list view
        _listView.setAdapter(_adapter);

        _btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExercise = new Intent(Exercises.this, AddExerciseToLibrary.class);
                startActivity(intentAddExercise);
            }
        });


    }

    private class addDataToListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // add some Exercises to the list
            _ExercisesList.add("TH Sound");
            _ExercisesList.add("R Sound");
            _ExercisesList.add("S Sound");
            _ExercisesList.add("Basic Swallow");
            _ExercisesList.add("3 Step Directions");

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


