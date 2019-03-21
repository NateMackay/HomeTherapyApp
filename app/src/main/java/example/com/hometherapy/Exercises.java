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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

/**
 * This is the class that is for the exercise library view
 * linked to activity_exercises.xml.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class Exercises extends AppCompatActivity {

    // for log
    private static final String TAG = "ExercisesActivity";

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EXERCISE_DATA = "exerciseData";
    public static final String LOGIN_USER = "loginUser";

    // Key for extra message for exercise name to pass to activity
    public static final String MSG_EXERCISE_NAME = "example.com.hometherapy.EXERCISENAME";

    // member variables
    private ExerciseList _currentExercises;
    private Exercise _exercise;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
    private List<Exercise> tempExerciseList;
    private User _loginUser;

    // views
    private ListView _lvExerciseList;
    private Button _btnAddExercise;
    private Button _btnExercisesReturnDashboard;

    // array adapter for exercise list
    private ExerciseListAdapter _adapterExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // open up database for given user (shared preferences)
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonExerciseList = _sharedPreferences.getString(EXERCISE_DATA, "");
        String jsonLoginUser = _sharedPreferences.getString(LOGIN_USER, "");

        // initialize GSON object
        _gson = new Gson();

        // deserialize sharedPrefs JSON user database into List of Exercises
        _currentExercises = _gson.fromJson(jsonExerciseList, ExerciseList.class);
        _loginUser = _gson.fromJson(jsonLoginUser, User.class);

        // register view widgets
        _lvExerciseList = (ListView) findViewById(R.id.lvExerciseList);
        _btnAddExercise = (Button) findViewById(R.id.btnAddExercise);
        _btnExercisesReturnDashboard = (Button) findViewById(R.id.btnExercisesReturnDashboard);

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

                // intent to go to Add/Edit Exercise to Library screen, passing exercise via extra message
                Intent intentAddExerciseToLibrary = new Intent(Exercises.this, AddExerciseToLibrary.class);
                intentAddExerciseToLibrary.putExtra(MSG_EXERCISE_NAME, currentExercise.get_exerciseName());
                startActivity(intentAddExerciseToLibrary);
            }
        });

        _btnExercisesReturnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (_loginUser != null) {

                    if (_loginUser.get_accountType().equals("therapist")) {

                        Intent intentReturnTherapistDashboard = new Intent(Exercises.this, MyClients.class);
                        startActivity(intentReturnTherapistDashboard);

                    } else if (_loginUser.get_accountType().equals("admin")){
                        // go to users
                        Intent intentReturnAdminDashboard = new Intent(Exercises.this, Users.class);
                        startActivity(intentReturnAdminDashboard);
                    } else {
                        // only therapist and admin users should have had access to the exercise
                        // library, so if this is happening, then there is a problem with the
                        // login user SP or somehow a client was able to see this screen
                        Toast.makeText(Exercises.this, "Unable to return to dashboard: " +
                                "user not therapist or admin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Check Shared Prefs Login User logic if you get this message
                    Toast.makeText(Exercises.this, "Unable to return to dashboard: " +
                            "error with user login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        _btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddExercise = new Intent(Exercises.this, AddExerciseToLibrary.class);
                intentAddExercise.putExtra(MSG_EXERCISE_NAME, "***new_exercise***");
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


