package example.com.hometherapy;

/**
 * This is the class / data structure for a user.
 * It applies to all user types. It includes a placeholder
 * for userID, which may be used by FireBase, potentially.
 * It contains the user email, name, password, phone, status,
 * assigned clinic, account type, assigned therapist (only
 * relevant to client user), and accumulated rewards points
 * (only relevant to a client user).
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class User {

    // private member variables
    private String _userID; // Firebase user UID
    private String _email;  // Firebase user email
    private String _firstName;
    private String _lastName;
    private String _phone;
    private String _status;
    private String _assignedClinic;
    private String _accountType;
    private String _assignedTherapistUID;
    private String _assignedTherapistName;
    private Integer _myPoints;

    // constructors
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID, String email, String firstName, String lastName, String phone,
                String status, String assignedClinic, String accountType,
                String assignedTherapistUID, String assignedTherapistName) {
        this._userID = userID;
        this._email = email;
        this._firstName = firstName;
        this._lastName = lastName;
        this._phone = phone;
        this._status = status;
        this._assignedClinic = assignedClinic;
        this._accountType = accountType;
        this._assignedTherapistUID = assignedTherapistUID;
        this._assignedTherapistName = assignedTherapistName;
        this._myPoints = 0;
    }

    // getters
    public String getUserID() { return _userID; }
    public String getEmail() { return _email; }
    public String getFirstName() { return _firstName; }
    public String getLastName() { return _lastName; }
    public String getPhone() { return _phone; }
    public String get_status() { return _status; }
    public String get_assignedClinic() { return _assignedClinic; }
    public String get_accountType() { return _accountType; }
    public String get_assignedTherapistUID() { return _assignedTherapistUID; }
    public String get_assignedTherapistName() { return _assignedTherapistName; }
    public Integer get_myPoints() { return _myPoints; }

    // setters
    public void setUserID(String userID) {
        this._userID = userID;
    }
    public void setEmail(String email) {
        this._email = email;
    }
    public void setFirstName(String firstName) {
        this._firstName = firstName;
    }
    public void setLastName(String lastName) {
        this._lastName = lastName;
    }
    public void setPhone(String phone) {
        this._phone = phone;
    }
    public void set_status(String _status) { this._status = _status; }
    public void set_assignedClinic(String _assignedClinic) { this._assignedClinic = _assignedClinic; }
    public void set_accountType(String _accountType) { this._accountType = _accountType; }
    public void set_assignedTherapistUID (String _assignedTherapistUID) {
        this._assignedTherapistUID = _assignedTherapistUID;
    }
    public void set_assignedTherapistName (String _assignedTherapistName) {
        this._assignedTherapistName = _assignedTherapistName;
    }
    public void set_myPoints (Integer _myPoints) { this._myPoints = _myPoints; }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "_userID='" + _userID + '\'' +
                ", _email='" + _email + '\'' +
                ", _firstName='" + _firstName + '\'' +
                ", _lastName='" + _lastName + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _status='" + _status + '\'' +
                ", _assignedClinic='" + _assignedClinic + '\'' +
                ", _accountType='" + _accountType + '\'' +
                ", _assignedTherapistUID='" + _assignedTherapistUID + '\'' +
                ", _assignedTherapistName='" + _assignedTherapistName + '\'' +
                ", _myPoints=" + _myPoints +
                '}';
    }
}
