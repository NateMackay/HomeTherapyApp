package example.com.hometherapy;

import android.support.annotation.NonNull;

public class Therapist {

    // private member variables
    private String _userID;
    private String _firstName;
    private String _lastName;

    // constructors
    public Therapist() {
    }

    public Therapist(String _userID, String _firstName, String _lastName) {
        this._userID = _userID;
        this._firstName = _firstName;
        this._lastName = _lastName;
    }

    // getters
    public String get_userID() { return _userID; }
    public String get_firstName() { return _firstName; }
    public String get_lastName() { return _lastName; }

    // setters
    public void set_userID(String _userID) { this._userID = _userID; }
    public void set_firstName(String _firstName) { this._firstName = _firstName; }
    public void set_lastName(String _lastName) { this._lastName = _lastName; }

    @NonNull
    @Override
    public String toString() {
        return _firstName + " " + _lastName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj)) {
            return true;
        } else return this._userID.equals(obj); // if assigned therapist ID equals user ID
    }
}
