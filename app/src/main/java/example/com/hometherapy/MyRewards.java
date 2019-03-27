package example.com.hometherapy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    // name shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_DATA = "userData";

    // key for extra message for user email address to pass to activity
    public static final String MSG_USER_EMAIL = "example.com.hometherapy.USEREMAIL";

    // views
    // TextView _tvRewardsLabel;
    TextView _tvRewardsPoints;
    Spinner _spinRewardsPointValue;
    Button _btnRedeemPoints;

    // member variables
    private String _currentUserEmail;
    private UserList _userDatabase;
    private User _currentUser;
    private Gson _gson;
    private SharedPreferences _sharedPreferences;
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
        // _tvRewardsLabel = (TextView) findViewById(R.id.tvRewardsLabel);
        _tvRewardsPoints = (TextView) findViewById(R.id.tvRewardsPoints);
        _spinRewardsPointValue = (Spinner) findViewById(R.id.spinRewardsPointValue);
        _btnRedeemPoints = (Button) findViewById(R.id.btnRedeemPoints);

        // register and set point value adapter
        _adapterPointValue = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _pointValueOptions);
        _spinRewardsPointValue.setAdapter(_adapterPointValue);

        // open up User database (shared preferences)
        // JSON formatted string stored in shared preferences storing a UserList object
        _sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String jsonUserList = _sharedPreferences.getString(USER_DATA, "");

        // initialize GSON object
        _gson = new Gson();

        // get user email (i.e. account/ID) from extra message in intent
        Intent thisIntent = getIntent();
        _currentUserEmail = thisIntent.getStringExtra(MSG_USER_EMAIL);
        Log.d(TAG, "verify current user: " + _currentUserEmail);

        // deserialize sharedPrefs from JSON user database into List of Users
        _userDatabase = _gson.fromJson(jsonUserList, UserList.class);

        if (_userDatabase != null) {

            List<User> tempUserList = _userDatabase.getUserList();

            // find matching user account by email
            _currentUser = tempUserList.stream()
                    .filter(user -> _currentUserEmail.equals(user.getEmail()))
                    .findAny()
                    .orElse(null);

            Log.d(TAG, "verify current user: " + _currentUser);
        }

        // set current points in view and get current point value from user account
        if (_currentUser != null) {
            _myPoints = _currentUser.get_myPoints();
            _tvRewardsPoints.setText(_myPoints.toString());
        }

        // redeem points button listener
        // whatever value the user selects in the spinner is to be deducted from the
        // point balance stored in the user account, so long as it does not go below zero
        // if it would take it below zero, refocus back to spinner with an error message
        _btnRedeemPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (_currentUser != null) {

                    // get point value to redeem as selected by user
                    Integer selectedRewardAmt = Integer.valueOf(_pointValueOptions[_spinRewardsPointValue.getSelectedItemPosition()]);

                    Log.d(TAG, "selectedAmt: " + selectedRewardAmt + ", myPoints: " + _myPoints);

                    // check if myPoints is sufficient (by calling enoughRewards). If not
                    // sufficient, show error message.
                    if (enoughRewards(_currentUser, selectedRewardAmt)) {
                        // redeem points and update user info by calling redeemRewards()
                        //_myPoints -= selectedRewardAmt;
                        redeemRewards(_currentUser, selectedRewardAmt);
                        _myPoints = _currentUser.get_myPoints();
                        _tvRewardsPoints.setText(_myPoints.toString());
                        //_currentUser.set_myPoints(_myPoints);

                        // update database
                        // convert updated UserList object back to JSON format
                        String updatedUserList = _gson.toJson(_userDatabase);

                        // update Shared Prefs with updated data
                        SharedPreferences.Editor editor = _sharedPreferences.edit();
                        editor.putString(USER_DATA, updatedUserList);
                        editor.apply();  // considering using commit instead

                    } else {
                        // toast message not enough points
                        Toast.makeText(getApplicationContext(),
                                "Not enough points available. Please choose another amount to redeem",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * This is a helper method, which checks to make sure that there are enough rewards before
     * proceeding with any transaction.
     *
     * If the user has enough rewards, this method will return true, which will then allow
     * the redeemRewards() method to be called, and a transaction to take place. If the user
     * does not have enough rewards, then this method returns false, which will prevent
     * redeemRewards() from being called, and instead the user will see
     * an error message.
     *
     * @param pointsSelected - Point value selected by current user.
     * @param currentUser - Current user. Will get reward points from currentUser.
     * @return - True if points is less than or equal to the point value selected by
     * current user, false if otherwise.
     */
    public static boolean enoughRewards(User currentUser, Integer pointsSelected) {
        // return true if points is less than or equal to the current user's reward
        // points, false if otherwise.
        return pointsSelected <= currentUser.get_myPoints();
    }

    /**
     * This method will redeem the points from the user reward points (_myPoints), by
     * subtracting points from the user reward points.
     * @param points - Point value selected by current user.
     * @param currentUser - The current user. Will get reward points from currentUser.
     */
    private static void redeemRewards(User currentUser, Integer points) {
        // store the difference between the current user's reward points and the redeem points
        // selected by the user in an Integer variable.
        Integer newMyPoints = currentUser.get_myPoints() - points;

        // Set the user's reward points to the value of the the above integer variable.
        currentUser.set_myPoints(newMyPoints);
    }

    /**
     * When called, this method will test if the subtraction logic in redeemRewards()
     * works as intended.
     *
     * @param currentUser - The current user.
     * @param points - Point value selected by current user.
     * @return - Returns true if subtraction logic in redeemRewards() works correctly.
     */
    public static boolean doesSubtractCorrectly(User currentUser, Integer points) {
        // Calculate rewards before call to redeemRewards() and store in Integer.
        Integer testEquals = currentUser.get_myPoints() - points;

        // Call addToRewards to update user's rewards
        redeemRewards(currentUser, points);

        // Should return true, to indicate that logic in addToRewardsTotal works as intended.
        return testEquals.equals(currentUser.get_myPoints());
    }

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
            intentMessage.putExtra(MSG_USER_EMAIL, _currentUserEmail);
            startActivity(intentMessage);
        /* } else if (id == R.id.nav_myRewards) {
            Intent intentRewards = new Intent(MyExercises.this, MyRewards.class);
            startActivity(intentRewards); */
        } else if (id == R.id.nav_myMessages) {
            Intent intentRewards = new Intent(MyRewards.this, MyMessages.class);
            startActivity(intentRewards);
        } else if (id == R.id.nav_myProfile) {
            Intent intentProfile = new Intent(MyRewards.this, MyProfile.class);
            intentProfile.putExtra(MSG_USER_EMAIL, _currentUserEmail);
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
