package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * View of library of exercises for therapist to select from to assign
 * an exercise to the client. From therapist's view of client's assigned exercises
 * {@link ClientExercises}, if a therapist clicks on the assign new
 * exercise to client button, it takes the therapist to this view,
 * which shows all of the exercises in the library for the therapist
 * to select from. This then takes the therapist to the Add Exercise
 * To Client view {@link AddExerciseToClient}.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link ClientExercises } therapist view of assigned exercises
 * {@link AddExerciseToClient} add/edit view of assigned exercise to client
 */
public class ClientExerciseLibrary extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_client_exercise_library";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mExerciseLibraryRef; // for reference to library node

    // Key for extra message for user email address to pass to activity
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // member variables
    private List<Exercise> _tempExerciseList;
    private String _clientUID;
    private String _assignedExerciseID; // place holder to receive blank intent to pass on to AETC

    // views
    private ExerciseListAdapter _adapterExerciseList;
    private ListView _lvCELExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercise_library);

        // register views
        _lvCELExerciseList = (ListView) findViewById(R.id.lvCELExerciseList);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // get client ID (clientUID) and blank assigned exercise value from extra message
        // assignedExerciseID is intentionally blank because AETC needs to accept an intent
        // from either Client Exercises (if existing) or Client Exercise Library (if new)
        // if new, then there will not be any assignedExerciseID, but AETC still expects it
        Intent thisIntent = getIntent();
        _clientUID = thisIntent.getStringExtra(MSG_CLIENT_UID);
        _assignedExerciseID = thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID);
        Log.d(TAG, "verify client ID: " + _clientUID);
        Log.d(TAG, "verify assigned exercise ID (expect empty): " + _assignedExerciseID);

        // initialize array adapter and bind exercise list to it, then set adapter
        _tempExerciseList = new ArrayList<>();
        _adapterExerciseList = new ExerciseListAdapter(this, _tempExerciseList);
        _lvCELExerciseList.setAdapter(_adapterExerciseList);

        // add listener for display reference to pull data from firebase and display in listview
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mExerciseLibraryRef = mFirebaseDatabase.getReference().child("library");
        mExerciseLibraryRef.addListenerForSingleValueEvent(valueEventListener);

        // set on item click listener to select an exercise and move to Add Exercise to Client
        _lvCELExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current exercise that is clicked on by user by its position
                Exercise selectedExercise = _tempExerciseList.get(position);

                Log.d(TAG, "click on exercise in list: " + selectedExercise);
                Log.d(TAG, "exercise ID: " + selectedExercise.get_exerciseID());
                Log.d(TAG, "Intent clientID: " + _clientUID);
                Log.d(TAG, "Intent AssignedExerciseID: " + _assignedExerciseID );

                // intent to go to add exercise to client activity
                // note that _assignedExerciseID should be empty, but we pass it on to AETC
                // because AETC expects it from both this ClientExerciseLibrary as well
                // as ClientExercises; because it is empty, it means we are assigning a new exercise
                Intent intentAETC = new Intent(ClientExerciseLibrary.this, AddExerciseToClient.class);
                intentAETC.putExtra(MSG_ASSIGNED_EXERCISE_ID, _assignedExerciseID);
                intentAETC.putExtra(MSG_CLIENT_UID, _clientUID);
                intentAETC.putExtra(MSG_EXERCISE_ID, selectedExercise.get_exerciseID());
                startActivity(intentAETC);

                Log.d(TAG, "on ITEM CLICK listener 4");
            }
        }); // END on item click listener

    } // END onCreate()

    // value event listener for library of exercises
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
            _tempExerciseList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    _tempExerciseList.add(exercise);
                }
            }
            _adapterExerciseList.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END value event listener

}
