package example.com.hometherapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This class contains user information. Users are of 3 types, clients, therapists, and
 * administrators.
 */
public class Users extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
    }

    //
    private static final int USER_ADMIN = 1;
    private static final int USER_THERAPIST = 2;
    private static final int USER_CLIENT = 3;

    //
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int phone;
    private int userType;       // Admin, Therapist or Client
    private int userID;         // Unique ID to each user. I'm not sure if we need this?

    // constructors
    Users(String email, String password, String firstName, String lastName,
          int phone, int userType, int userID) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userType = userType;
        this.userID = userID;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPhone() {
        return phone;
    }

    public int getUserType() {
        return userType;
    }

    public int getUserID() {
        return userID;
    }


    // setters
    public void setUserName(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
