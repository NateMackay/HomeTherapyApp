package example.com.hometherapy;

public class User {

    // private member variables
    private String _userID;         // Unique ID to each user. Probably replaced by Firebase "User" but keep for now
    private String _email;
    private String _password;
    private String _firstName;
    private String _lastName;
    private String _phone;
    private String _status;
    private String _assignedClinic;
    private String _accountType;
    private String _assignedTherapist;
    private Integer _myPoints;

    // constructors
    public User(String email, String password, String firstName, String lastName, String phone,
                String userID, String status, String assignedClinic, String accountType,
                String assignedTherapist) {
        this._email = email;
        this._password = password;
        this._firstName = firstName;
        this._lastName = lastName;
        this._phone = phone;
        this._userID = userID;
        this._status = status;
        this._assignedClinic = assignedClinic;
        this._accountType = accountType;
        this._assignedTherapist = assignedTherapist;
        this._myPoints = 0;
    }

    public User(String userID) {
        this._email = "";
        this._password = "";
        this._firstName = "";
        this._lastName = "";
        this._phone = "";
        this._userID = userID;
        this._status = "pending";
        this._assignedClinic = "pending";
        this._accountType = "pending";
        this._assignedTherapist = "pending";
        this._myPoints = 0;
    }

    // getters
    public String getEmail() { return _email; }
    public String getPassword() { return _password; }
    public String getFirstName() { return _firstName; }
    public String getLastName() { return _lastName; }
    public String getPhone() { return _phone; }
    public String getUserID() { return _userID; }
    public String get_status() { return _status; }
    public String get_assignedClinic() { return _assignedClinic; }
    public String get_accountType() { return _accountType; }
    public String get_assignedTherapist() { return _assignedTherapist; }
    public Integer get_myPoints() { return _myPoints; }

    // setters
    public void setEmail(String email) {
        this._email = email;
    }
    public void setPassword(String password) {
        this._password = password;
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
    public void setUserID(String userID) {
        this._userID = userID;
    }
    public void set_status(String _status) { this._status = _status; }
    public void set_assignedClinic(String _assignedClinic) { this._assignedClinic = _assignedClinic; }
    public void set_accountType(String _accountType) { this._accountType = _accountType; }
    public void set_assignedTherapist (String _assignedTherapist) {
        this._assignedTherapist = _assignedTherapist;
    }
    public void set_myPoints (Integer _myPoints) { this._myPoints = _myPoints; }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "_userID='" + _userID + '\'' +
                ", _email='" + _email + '\'' +
                ", _password='" + _password + '\'' +
                ", _firstName='" + _firstName + '\'' +
                ", _lastName='" + _lastName + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _status='" + _status + '\'' +
                ", _assignedClinic='" + _assignedClinic + '\'' +
                ", _accountType='" + _accountType + '\'' +
                ", _assignedTherapist='" + _assignedTherapist + '\'' +
                ", _myPoints=" + _myPoints +
                '}';
    }
}
