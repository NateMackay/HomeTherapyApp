package example.com.hometherapy;

/**
 * not needed - to be safe deleted
 */
public class ClientUser extends User {

    // constructor
    public ClientUser(String email, String password, String firstName, String lastName,
                      String phone, String userID, String status, String assignedClinic,
                      String accountType, String assignedTherapist) {
        super(email, password, firstName, lastName, phone, userID, status,
                assignedClinic, accountType, assignedTherapist);

    }
}
