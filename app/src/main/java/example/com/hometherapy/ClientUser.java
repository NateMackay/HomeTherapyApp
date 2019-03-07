package example.com.hometherapy;

public class ClientUser extends User {

    // private member variables
    private String _assignedTherapist;

    // constructor
    public ClientUser(String email, String password, String firstName, String lastName,
                      String phone, String userID, String status, String assignedClinic,
                      String accountType, String _assignedTherapist) {
        super(email, password, firstName, lastName, phone, userID, status,
                assignedClinic, accountType);
        this._assignedTherapist = _assignedTherapist;
    }

    public String get_assignedTherapist() {
        return _assignedTherapist;
    }

    public void set_assignedTherapist(String _assignedTherapist) {
        this._assignedTherapist = _assignedTherapist;
    }
}
