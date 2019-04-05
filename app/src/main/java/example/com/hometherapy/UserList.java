package example.com.hometherapy;

import java.util.ArrayList;
import java.util.List;

/**
 *  This is used for instantiating an object that stores a list of
 *  users, or literally, a List <User> object. It is necessary
 *  to do it this way so that GSON can serialize and deserialize
 *  into and out of a list of users. The list of users
 *  comprises the user database.
 *  @author Team06
 *  @version 1.0
 *  @since 2019-03-19
 *  {@link User} Model / data structure for a user object
 *  {@link Users} Activity/View for admin view of list of users
 */
public class UserList {

    // private member variable.
    private List<User> _userList;

    // constructor
    public UserList() {
        _userList = new ArrayList<>();
    }

    /**
     * Add a user to the List <User> member variable.
     * @param user
     */
    public void addUser(User user) {
        _userList.add(user);
    }

    // getter
    public List<User> getUserList() {
        return _userList;
    }

    // output current list of users
    @Override
    public String toString() {
        return "UserList{" +
                "_userList=" + _userList +
                '}';
    }
}
