package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client's view of a given assigned exercise. Clients navigate to this
 * view when they click on an assigned exercise from their dashboard,
 * MyExercises {@link MyExercises}.
 * This view is where the client can mark the exercise as complete.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MyExercise extends AppCompatActivity {

    // for log
    private static final String TAG = "activity_my_exercise";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAssignedExercisesRef;
    private DatabaseReference mUsersRef;

    // Keys for extra messages
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";

    // member variables
    private String _assignedExerciseID;
    private String _currentUserID;
    private Integer _pointValue;
    private Integer _myPoints;

    // views
    private TextView _tvMyAssignment;
    private TextView _tvMyExercise;
    private TextView _tvMyDiscipline;
    private TextView _tvMyModality;
    private TextView _tvLinkToVideo;
    private Button _btnMyComplete;
    private Button _btnMyExercises;
    private TextView _tvMyEPointValue;
    private TextView _tvMyEStatus;
    private TextView _tvWellDoneMSG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exercise);

        // register views
        _tvMyAssignment = (TextView) findViewById(R.id.tvMyEAssignment);
        _tvMyExercise = (TextView) findViewById(R.id.tvMyExercise);
        _tvMyDiscipline = (TextView) findViewById(R.id.tvMyDiscipline);
        _tvMyModality = (TextView) findViewById(R.id.tvMyModality);
        _tvLinkToVideo = (TextView) findViewById(R.id.tvLinkToVideo);
        _btnMyComplete = (Button) findViewById(R.id.btnComplete);
        _btnMyExercises = (Button) findViewById(R.id.btnGoToExercises);
        _tvMyEPointValue = (TextView) findViewById(R.id.tvMyEPointValue);
        _tvMyEStatus = (TextView) findViewById(R.id.tvMyEStatus);
        _tvWellDoneMSG = (TextView) findViewById(R.id.tvWellDoneMsg);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        _currentUserID = mAuth.getUid();

        Log.d(TAG, "onCreate - currentUserID: " + _currentUserID);

        // setup firebase references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAssignedExercisesRef = mFirebaseDatabase.getReference().child("assignedExercises");
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        // get assigned exercise ID passed from MyExercises.java
        Intent thisIntent = getIntent();
        _assignedExerciseID = thisIntent.getStringExtra(MSG_ASSIGNED_EXERCISE_ID);

        // verify assigned exercise from intent
        Log.d(TAG, "assigned exercise ID from Intent: " + _assignedExerciseID);

        // query firebase for assigned exercise data
        Query queryAE = mAssignedExercisesRef.orderByChild("_assignedExerciseID").equalTo(_assignedExerciseID);
        queryAE.addListenerForSingleValueEvent(assignedExerciseListener);

        Log.d(TAG, "onCreate: 1");

        // query firebase for user data to get existing points
        Query queryUser = mUsersRef.orderByChild("userID").equalTo(_currentUserID);
        queryUser.addListenerForSingleValueEvent(valueEventListenerUser);

        Log.d(TAG, "onCreate: 2");

        // listener for Back to My Exercises button
        _btnMyExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClientExercises = new Intent(MyExercise.this, MyExercises.class);
                startActivity(intentClientExercises);
            }
        });

        Log.d(TAG, "onCreate: 3");

    } // END onCreate()

    // event listener for assigned exercise ID
    ValueEventListener assignedExerciseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                Log.d(TAG, "onDataChange: 4");
                
                AssignedExercise assignedExercise = new AssignedExercise();

                Log.d(TAG, "onDataChange: 5");
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: 6");
                    
                    assignedExercise = snapshot.getValue(AssignedExercise.class);

                    Log.d(TAG, "onDataChange: 7");
                }

                if (assignedExercise != null) {

                    Log.d(TAG, "onDataChange: 8");

                    Log.d(TAG, "onDataChange: assignedExercise: " + assignedExercise);
                    
                    // populate text views with data passed in with assigned exercise ID
                    // set text view values
                    _tvMyAssignment.setText(assignedExercise.get_assignment());
                    _tvMyExercise.setText(assignedExercise.get_exerciseName());
                    _tvMyDiscipline.setText(assignedExercise.get_discipline());
                    _tvMyModality.setText(assignedExercise.get_modality());
                    _tvLinkToVideo.setText(assignedExercise.get_videoLink());
                    _tvMyEPointValue.setText(String.format("%s", assignedExercise.get_pointValue().toString()));
                    _tvMyEStatus.setText(assignedExercise.get_status());

                    Log.d(TAG, "onDataChange: 9");
                    
                    // if assignment has been completed already, mark complete
                    if (assignedExercise.get_completedToday().equals(true)) {

                        Log.d(TAG, "onDataChange: 10");
                        // set button view to completed status
                        hasCompleted();

                        Log.d(TAG, "onDataChange: 11");
                        
                    }

                    // set temporary point value of exercise
                    _pointValue = assignedExercise.get_pointValue();

                    Log.d(TAG, "onDataChange: 12");

                    Log.d(TAG, "onDataChange AE Point Value: " + _pointValue);
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END event listener for assigned exercise

    // event listener for user data so we can get the current user's points
    ValueEventListener valueEventListenerUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                }

                Log.d(TAG, "onDataChange: user = " + user);

                if (user != null) {
                    // get current points
                    _myPoints = user.get_myPoints();
                }

                // add button listener to complete button
                _btnMyComplete.setOnClickListener(completeExerciseBtnListener);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END event listener for user query

    // onClick listener for complete button
    View.OnClickListener completeExerciseBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // update view of button to show exercise is completed
            hasCompleted();

            // get new updated point value
            Integer pointsUpdate = _myPoints + _pointValue;

            // verify point values
            Log.d(TAG, "onClick: myPoints = " + _myPoints);
            Log.d(TAG, "onClick: exercise point Value = " + _pointValue);
            Log.d(TAG, "onClick: total new points = " + pointsUpdate);

            // note: it is possible to combine the two updates below by setting
            // the reference to the root node and then setting the path for each in the map
            // and then updating values
            // reference: https://firebase.google.com/docs/database/android/read-and-write
            // left as is for clarity

            // update completed flag in assigned exercise data
            Map<String, Object> updateAEValues = new HashMap<>();
            updateAEValues.put("_completedToday", true);
            if (_assignedExerciseID != null) {
                mAssignedExercisesRef.child(_assignedExerciseID).updateChildren(updateAEValues);
            }

            // update point value to user data
            Map<String, Object> updateUserValues = new HashMap<>();
            updateUserValues.put("_myPoints", pointsUpdate);
            if (_currentUserID != null) {
                mUsersRef.child(_currentUserID).updateChildren(updateUserValues);
            }

        } // END onClick()

    }; // END value event listener for complete button

    /**
     * If assigned exercise is already completed, then make the below changes to the view when called.
     */
    public void hasCompleted() {
        // set text and enabled status so that round_button.xml file, where shape properties are set
        // can be applied accordingly
        // references:
        // https://developer.android.com/guide/topics/ui/controls/button
        // https://www.journaldev.com/19850/android-button-design-custom-round-color (view in Chrome)
        // https://developer.android.com/guide/topics/resources/drawable-resource#Shape
        _btnMyComplete.setText("Completed");
        _btnMyComplete.setEnabled(false);
    } // END hasCompleted()

} // END MyExercise class

