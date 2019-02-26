package example.com.hometherapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class exercises extends AppCompatActivity {

    // for log
    private static final String TAG = "ExercisesActivity";

    // member variables
    private List<String> _ExercisesList;
    private ArrayAdapter<String> _adapter;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // instantiate a new list of exercises
        _ExercisesList = new ArrayList<String>();

        // add some exercises to the list
        _ExercisesList.add("TH Sound");
        _ExercisesList.add("R Sound");
        _ExercisesList.add("S Sound");
        _ExercisesList.add("Basic Swallow");
        _ExercisesList.add("3 Step Directions");

        // initialize array adapter and bind exercise list to it
        _adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _ExercisesList);

        // set the adapter to the list view
        _listView.setAdapter(_adapter);


    }
}
