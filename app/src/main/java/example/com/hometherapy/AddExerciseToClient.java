package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * From the therapist dashboard (ClientExercises.Java/client_exercises.xml), the therapist
 * can click a button to assign an exercise to a client. This takes the user to
 * ClientExerciseLibrary.java/client_exercise_library.xml, where the therapist can select
 * from a list of exercises to assign to the client. Upon selection of an exercise,
 * the AddExerciseToClient.java/add_exercise_to_client.xml (this) opens up, allowing the
 * therapist to select additional details specific to the activity, including the
 * point value the therapist wants to assign to the exercise and the status.
 * @author Team06
 * @version 1.0
 * @since   2019-03-19
 */
public class AddExerciseToClient extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_add_exercise_to_client";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAssignedExercisesRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mExerciseLibrary;

    // Key for extra message for user email address to pass to activity
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // member variables
    private String _clientUID;
    private String _assignedExerciseID;
    private String _exerciseID;

    // views
    private TextView _tvAETCAssignment;
    private TextView _tvAETCExercise;
    private TextView _tvAETCDiscipline;
    private TextView _tvAETCModality;
    private TextView _tvLinkToVideo;
    private Button _btnAETCSave;
    private CheckBox _cbAETCClientCompleted;
    private Spinner _spinAETCPointValue;
    private Spinner _spinAETCStatus;

    // array adapter for spinner views
    private String _exerciseStatusNames[] = {"Status", "not started", "started", "complete", "on hold", "inactive"};
    private String _pointValueOptions[] = {"5", "10", "15", "20", "25"};
    private ArrayAdapter<String> _adapterStatus;
    private ArrayAdapter<String> _adapterPointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_client);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // register views
        _tvAETCAssignment = (TextView) findViewById(R.id.tvAETCAssignment);
        _tvAETCExercise = (TextView) findViewById(R.id.tvAETCExercise);
        _tvAETCDiscipline = (TextView) findViewById(R.id.tvAETCDiscipline);
        _tvAETCModality = (TextView) findViewById(R.id.tvAETCModality);
        _tvLinkToVideo = (TextView) findViewById(R.id.tvLinkToVideo);
        _btnAETCSave = (Button) findViewById(R.id.btnAETCSave);
        _spinAETCPointValue = (Spinner) findViewById(R.id.spinAETCPointValue);
        _spinAETCStatus = (Spinner) findViewById(R.id.spinAETCStatus);
        _cbAETCClientCompleted = (CheckBox) findViewById(R.id.cbAETCClientCompleted);

        // register adapters
        _adapterPointValue = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _pointValueOptions);
        _adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _exerciseStatusNames);

        // set adapters
        _spinAETCPointValue.setAdapter(_adapterPointValue);
        _spinAETCStatus.setAdapter(_adapterStatus);

        // get exercise data passed from client exercise library
        Intent thisIntent = getIntent();
        _assignedExerciseID = thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID);
        _clientUID = thisIntent.getStringExtra(MSG_CLIENT_UID);
        _exerciseID = thisIntent.getStringExtra(MSG_EXERCISE_ID);

        Log.d(TAG, "Intent: _assignedExerciseID: " + _assignedExerciseID);
        Log.d(TAG, "Intent: _clientUID: " + _clientUID);
        Log.d(TAG, "Intent: _exerciseID: " + _exerciseID);

        // set up firebase references from database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAssignedExercisesRef = mFirebaseDatabase.getReference().child("assignedExercises");
        mUsersRef = mFirebaseDatabase.getReference().child("users");
        mExerciseLibrary = mFirebaseDatabase.getReference().child("library");

        // Populate views with either existing assigned exercise data or data from exercise library
        // For existing exercises, coming from ClientExercises, query existing exercise and set listener
        // Otherwise we are assigning a new exercise, which is coming from ClientExerciseLibrary
        if (!_assignedExerciseID.equals("")) {

            // Query assigned exercise data for the specific assigned exercise we're looking for and set listener
            Query queryAE = mAssignedExercisesRef.orderByChild("_assignedExerciseID").equalTo(_assignedExerciseID);
            queryAE.addListenerForSingleValueEvent(assignedExerciseListener);

        } else {
            // For new exercises, we are coming from Client Exercise Library, so we will have
            // an Exercise ID in our intent, but confirm for sure that Exercise ID is not empty
            if (!_exerciseID.equals("")) {

                Log.d(TAG, "exerciseID Not Empty - New Exercise");

                // Query exercise data for specified exercise ID we're looking for and set listener
                Query queryExercise = mExerciseLibrary.orderByChild("_exerciseID").equalTo(_exerciseID);
                queryExercise.addListenerForSingleValueEvent(exerciseListener);

            } else {
                Log.e(TAG, "error: exerciseID should not be empty");
            }
        }

        // save changes button
        _btnAETCSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set values for new assigned exercise object from view objects
                String exerciseName = _tvAETCExercise.getText().toString();
                String discipline = _tvAETCDiscipline.getText().toString();
                String modality = _tvAETCModality.getText().toString();
                String assignment = _tvAETCAssignment.getText().toString();
                String videoLink = _tvLinkToVideo.getText().toString();
                Boolean isExerciseCompleted = _cbAETCClientCompleted.isChecked();
                String status = _spinAETCStatus.getSelectedItem().toString();
                Integer pointValue = Integer.parseInt(_spinAETCPointValue.getSelectedItem().toString());

                Map<String, Object> assignedExerciseValues; // temporary map to store values
                String assignedExerciseID = _assignedExerciseID; // empty if new, or existing value

                // if adding a new exercise, create a new assigned exercise
                if (assignedExerciseID.isEmpty()) {

                    // write new assigned exercise to library
                    // push() creates a new random key, which will be stored in assigned exercise
                    assignedExerciseID = mAssignedExercisesRef.push().getKey();

                    // create a new assigned exercise from values in form
                    // if this is a new exercise, we should have received an exercise ID from
                    // client exercise library
                    // we also received the client ID from the intent
                    // all other values besides the generated assigned exercise ID are recieved from view
                    AssignedExercise tempAssignedExercise = new AssignedExercise(_exerciseID, exerciseName,
                            discipline, modality, assignment, videoLink, assignedExerciseID,
                            _clientUID, pointValue, status, false );

                    // create a map from the tempAssignedExercise object using toMap()
                    // in Assigned Exercise class
                    assignedExerciseValues = tempAssignedExercise.toMap();

                } else {
                    // update the existing assigned exercise
                    // only need to update point value, status
                    assignedExerciseValues = new HashMap<>();
                    assignedExerciseValues.put("_pointValue", pointValue);
                    assignedExerciseValues.put("_status", status);
                    assignedExerciseValues.put("_completedToday", isExerciseCompleted);
                }

                // update values to firebase
                if (assignedExerciseID != null) {
                    mAssignedExercisesRef.child(assignedExerciseID).updateChildren(assignedExerciseValues)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Update to firebase was successful");
                            } else {
                                Log.d(TAG, "Update to firebase failed");
                            }
                        }
                    });
                }

                // navigate back to Client Exercises
                Intent intentClientExercises = new Intent(AddExerciseToClient.this, ClientExercises.class);
                intentClientExercises.putExtra(MSG_CLIENT_UID, _clientUID);
                startActivity(intentClientExercises);

            }
        }); // END save changes button

    } // END onCreate()

    // event listener for assigned exercise ID when there is an existing exercise ID passed in
    ValueEventListener assignedExerciseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                AssignedExercise assignedExercise = new AssignedExercise();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    assignedExercise = snapshot.getValue(AssignedExercise.class);
                }

                if (assignedExercise != null) {

                    // populate text views with data from assigned exercise
                    _tvAETCAssignment.setText(assignedExercise.get_assignment());
                    _tvAETCExercise.setText(assignedExercise.get_exerciseName());
                    _tvAETCDiscipline.setText(assignedExercise.get_discipline());
                    _tvAETCModality.setText(assignedExercise.get_modality());
                    _tvLinkToVideo.setText(assignedExercise.get_videoLink());
                    _cbAETCClientCompleted.setChecked(assignedExercise.get_completedToday());

                    // populate spinners with data from assigned exercise
                    // set status and point value spinners based on what is in assigned exercise
                    // first, get indices of these values
                    int iSpinAETCPointValue = Arrays.asList(_pointValueOptions).indexOf(assignedExercise.get_pointValue().toString());
                    int iSpinAETCStatus = Arrays.asList(_exerciseStatusNames).indexOf(assignedExercise.get_status());

                    // set spinner values based on current assigned exercise
                    // note that a value of -1 means indexOf() did not find value searching for
                    if (iSpinAETCPointValue >= 0) {
                        _spinAETCPointValue.setSelection(iSpinAETCPointValue);
                    }

                    if (iSpinAETCStatus >= 0) {
                        _spinAETCStatus.setSelection(iSpinAETCStatus);
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END assigned exercise listener

    // event listener for exercise ID
    ValueEventListener exerciseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Exercise exercise = new Exercise();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    exercise = snapshot.getValue(Exercise.class);
                }

                if (exercise != null) {

                    // populate text views with data passed in exercise ID
                    _tvAETCAssignment.setText(exercise.get_assignment());
                    _tvAETCExercise.setText(exercise.get_exerciseName());
                    _tvAETCDiscipline.setText(exercise.get_discipline());
                    _tvAETCModality.setText(exercise.get_modality());
                    _tvLinkToVideo.setText(exercise.get_videoLink());

                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };


} // END Add Exercise to Client class
