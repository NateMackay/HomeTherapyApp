package example.com.hometherapy;

public abstract class User {

    // move to enums - and use
    // private static final String USER_ADMIN = "admin";
    private static final String CLIENT_USER = "CLIENT";
    private static final String NON_CLIENT_USER = "NON_CLIENT";

    //
    private int userID;         // Unique ID to each user. Probably replaced by Firebase "User" but keep for now
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int _phone;
    private String status;
    private String userType;

    // constructors
    public User(String email, String password, String firstName, String lastName, int phone, int userID, String userType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this._phone = phone;
        this.userID = userID;
        this.userType = userType;
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
        return _phone;
    }

    public int getUserID() {
        return userID;
    }

    public String get_userType() {
        return userType;
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
        this._phone = phone;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void set_userType (String userType) {
        this.userType = userType;
    }

}
