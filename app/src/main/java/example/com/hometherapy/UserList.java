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

}
