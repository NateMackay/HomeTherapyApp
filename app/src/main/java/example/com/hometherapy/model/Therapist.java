package example.com.hometherapy.model;

import android.support.annotation.NonNull;

/**
 * This class represents a therapist. This class was created to help facilitate the
 * assignment of a therapist to a client, and all the data retrievals that are possible with
 * a therapist/client relationship.
 * @author Team06
 * @version 1.0
 * @since 2019-04-05
 */
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

}
