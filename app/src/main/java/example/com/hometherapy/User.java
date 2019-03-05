package example.com.hometherapy;

public abstract class User {

    // move to enums - and use
    // private static final String USER_ADMIN = "admin";
    private static final int CLIENT_USER_ = 1;
    private static final int NON_CLIENT_USER = 2;

    //
    private int _userID;         // Unique ID to each user. Probably replaced by Firebase "User" but keep for now
    private String _email;
    private String _password;
    private String _firstName;
    private String _lastName;
    private int _phone;
    private String status;
    private int _userType;

    // constructors
    public User(String email, String password, String firstName, String lastName, int phone, int userID, int _userType) {
        this._email = email;
        this._password = password;
        this._firstName = firstName;
        this._lastName = lastName;
        this._phone = phone;
        this._userID = userID;
        this._userType = _userType;
    }

    // getters
    public String getEmail() {
        return _email;
    }

    public String getPassword() {
        return _password;
    }

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public int getPhone() {
        return _phone;
    }

    public int getUserID() {
        return _userID;
    }

    public int get_userType() {
        return _userType;
    }

    // setters
    public void setUserName(String email) {
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

    public void setPhone(int phone) {
        this._phone = phone;
    }

    public void setUserID(int userID) {
        this._userID = userID;
    }

    public void set_userType (int userType) {
        this._userType = userType;
    }

}
