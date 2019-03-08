package example.com.hometherapy;

import java.util.ArrayList;
import java.util.List;

public class UserList {

    private List<User> _userList;

    public UserList() {
        _userList = new ArrayList<>();
    }

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
