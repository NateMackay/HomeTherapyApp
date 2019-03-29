package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Client's view of their current number of accumulated points. It
 * is also where the client goes to redeem points for a reward.
 * The client navigates to this view from the menu in the client
 * dashboard {@link MyExercises}.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class MyRewards extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // for log
    private static final String TAG = "MyRewards_Activity";

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersRef;

    // views
    TextView _tvRewardsPoints;
    Spinner _spinRewardsPointValue;
    Button _btnRedeemPoints;

    // member variables
    private String _currentUserID;
    private Integer _myPoints;

    // array adapter for spinner view for points to redeem
    private String _pointValueOptions[] = {"5", "10", "15", "20", "25"};
    private ArrayAdapter<String> _adapterPointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rewards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // register views
        _tvRewardsPoints = (TextView) findViewById(R.id.tvRewardsPoints);
        _spinRewardsPointValue = (Spinner) findViewById(R.id.spinRewardsPointValue);
        _btnRedeemPoints = (Button) findViewById(R.id.btnRedeemPoints);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        _currentUserID = mAuth.getUid();

        // setup firebase references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mFirebaseDatabase.getReference().child("users");

        // register and set point value adapter
        _adapterPointValue = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _pointValueOptions);
        _spinRewardsPointValue.setAdapter(_adapterPointValue);

        // query firebase for user data to get existing points
        Query queryUser = mUsersRef.orderByChild("userID").equalTo(_currentUserID);
        queryUser.addListenerForSingleValueEvent(valueEventListenerUser);

        // navigation
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    } // END onCreate()

    // event listener for user data so we can get the current user's points
    ValueEventListener valueEventListenerUser = new ValueEventListener() {
        @Override
        public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user = snapshot.getValue(User.class);
                }

                Log.d(TAG, "onDataChange: user = " + user);

                if (user != null) {
                    // get current points
                    _myPoints = user.get_myPoints();
                } else {
                    _myPoints = 0;
                }

                // set text views
                _tvRewardsPoints.setText(String.format("%s", _myPoints.toString()));

                // add button listener to complete button
                _btnRedeemPoints.setOnClickListener(redeemPointsBtnListener);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    }; // END event listener for user query

    View.OnClickListener redeemPointsBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // get point value to redeem as selected by user
            Integer selectedRewardAmt = Integer.valueOf(_pointValueOptions[_spinRewardsPointValue.getSelectedItemPosition()]);

            // verify selected points and current myPoints
            Log.d(TAG, "selectedRewardsAmt: " + selectedRewardAmt);
            Log.d(TAG, "_myPoints: " + _myPoints);

            // update points by redemption amount if sufficient points available
            if (selectedRewardAmt <= _myPoints) {

                // calculate updated points
                Integer updatedPoints = _myPoints - selectedRewardAmt;

                // update Firebase
                mUsersRef.child(_currentUserID).child("_myPoints").setValue(updatedPoints);

                // update view
                _tvRewardsPoints.setText(String.format("%s", updatedPoints.toString()));

            } else {
                // toast message not enough points
                Toast.makeText(getApplicationContext(),
                        "Not enough points available. Please choose another amount to redeem",
                        Toast.LENGTH_SHORT).show();
            }

        } // END onClick()

    }; // END redeem points button listener

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myExercises) {
            Intent intentMessage = new Intent(MyRewards.this, MyExercises.class);
            startActivity(intentMessage);
        /* } else if (id == R.id.nav_myRewards) {
            Intent intentRewards = new Intent(MyExercises.this, MyRewards.class);
            startActivity(intentRewards); */
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyRewards.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myProfile) {
            Intent intentProfile = new Intent(MyRewards.this, MyProfile.class);
            startActivity(intentProfile);
        } else if (id == R.id.nav_LogOut) {
            Intent intentLogIn = new Intent(MyRewards.this, SignIn.class);
            startActivity(intentLogIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
