package example.com.hometherapy;

public class NonClientUser extends User {

    // private member variables
    private String _accountType;

    // constructor
    public NonClientUser(String email, String password, String firstName, String lastName, int phone, int userID, String accountType) {
        super(email, password, firstName, lastName, phone, userID, "NON_CLIENT");
        _accountType = accountType;
    }

    public String get_accountType() {
        return _accountType;
    }

    public void set_accountType(String _accountType) {
        this._accountType = _accountType;
    }
}
