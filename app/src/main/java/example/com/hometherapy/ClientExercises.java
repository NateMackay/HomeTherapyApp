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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * example.com.hometherapy.Therapist view of client's assigned exercises. Intent only comes
 * from MyClients {@link MyClients}, which is the therapist's view
 * of their clients. When therapist selects a client from MyClients,
 * it takes you to this view.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link MyClients}
 */
public class ClientExercises extends AppCompatActivity {

    // for log
    private static final String TAG = "Client_Exercises_Activity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAssignedExercisesRef;
    private DatabaseReference mUsersRef;

    // Key for extra message for user email address to pass to activity
    public static final String MSG_ASSIGNED_EXERCISE_ID = "example.com.hometherapy.ASSIGNED_EXERCISE_ID";
    public static final String MSG_CLIENT_UID = "example.com.hometherapy.CLIENT_UID";
    public static final String MSG_EXERCISE_ID = "example.com.hometherapy.EXERCISE_ID";

    // member variables
    private String _clientUID;
    private List<AssignedExercise> _tempAssignedExerciseList;

    // adapter for assigned exercise list
    private AssignedExerciseListAdapter _adapterAssignedExercises;

    // views
    private ListView _lvCEAssignedExercises;
    private Button _btnCEAddExercise;
    private TextView _tvCELabel;
    private Button _btnCEUserLogOut;
    private Button _btnCEReturnToMyClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_exercises);

        // register views
        _lvCEAssignedExercises = (ListView) findViewById(R.id.lvCEAssignedExercises);
        _btnCEAddExercise = (Button) findViewById(R.id.btnCEAddExercise);
        _tvCELabel = (TextView) findViewById(R.id.tvCELabel);
        _btnCEUserLogOut = (Button) findViewById(R.id.btnCEUsersLogOut);
        _btnCEReturnToMyClients = (Button) findViewById(R.id.btnCEReturnToMyClients);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // initialize array adapter and bind assigned exercise list to it, then set the adapter
        _tempAssignedExerciseList = new ArrayList<>();
        _adapterAssignedExercises = new AssignedExerciseListAdapter(this, _tempAssignedExerciseList);
        _lvCEAssignedExercises.setAdapter(_adapterAssignedExercises);

        // get client ID from extra message
        Intent thisIntent = getIntent();
        _clientUID = thisIntent.getStringExtra(MSG_CLIENT_UID);
        Log.d(TAG, "verify client ID: " + _clientUID);

        // set up firebase references from database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAssignedExercisesRef = mFirebaseDatabase.getReference().child("assignedExercises");
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        // Add listener for assigned exercise data to pull from firebase and display in listview
        // query so that we are only pulling those exercises assigned to the client
        Query queryAE = mAssignedExercisesRef.orderByChild("_assignedUserID").equalTo(_clientUID);
        queryAE.addListenerForSingleValueEvent(assignedExerciseEventListener);

        // Add listener for client UID so that we can get display name to update label
        Query queryUser = mUsersRef.orderByChild("userID").equalTo(_clientUID);
        queryUser.addListenerForSingleValueEvent(clientUserEventListener);

        // if a client clicks on an exercise, then he/she will be taken to the my exercise view
        _lvCEAssignedExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get current assigned exercise from position in the list
                AssignedExercise selectedExercise = _tempAssignedExerciseList.get(position);

                // the following is for therapists only
                // intent to go to Add(Edit) Exercise to Client
                // need to pass an intent that has the user ID as well as the assigned exercise ID
                Intent intentAETC = new Intent(ClientExercises.this, AddExerciseToClient.class);
                intentAETC.putExtra(MSG_ASSIGNED_EXERCISE_ID, selectedExercise.get_assignedExerciseID());
                intentAETC.putExtra(MSG_CLIENT_UID, _clientUID);
                intentAETC.putExtra(MSG_EXERCISE_ID, selectedExercise.get_exerciseID());
                startActivity(intentAETC);
            }
        });

        // add NEW exercise to client button - goes first to client exercise library
        // and then from there on to Add Exercise To Client AETC
        _btnCEAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Exercises screen, passing user and blank assigned exercise ID
                Intent intentClientExerciseLibrary = new Intent(ClientExercises.this, ClientExerciseLibrary.class);
                intentClientExerciseLibrary.putExtra(MSG_ASSIGNED_EXERCISE_ID, "");
                intentClientExerciseLibrary.putExtra(MSG_CLIENT_UID, _clientUID);
                startActivity(intentClientExerciseLibrary);
            }
        });

        // return to list of my clients button
        _btnCEReturnToMyClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentMyClients = new Intent(ClientExercises.this, MyClients.class);
                startActivity(intentMyClients);
            }
        });

        // on click, just go back to signIn screen
        // for testing purposes
        _btnCEUserLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = new Intent(ClientExercises.this, SignIn.class);
                startActivity(intentSignIn);
            }
        });

    } // END onCreate()

    // listener for list of assigned exercises
    ValueEventListener assignedExerciseEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            _tempAssignedExerciseList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AssignedExercise assignedExercise = snapshot.getValue(AssignedExercise.class);
                    _tempAssignedExerciseList.add(assignedExercise);
                }
            }
            _adapterAssignedExercises.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END assigned exercises listener

    // listener for to get specific user object associated with passed in client UID
    ValueEventListener clientUserEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                }

                // set label to client's first name + "'s Exercises"
                if (user != null) {
                    _tvCELabel.setText(String.format("%s's Exercises", user.getFirstName()));
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END client user listener


} // END Client Exercises class
