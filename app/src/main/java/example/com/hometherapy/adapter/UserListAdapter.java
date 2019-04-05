package example.com.hometherapy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import example.com.hometherapy.R;
import example.com.hometherapy.Users;
import example.com.hometherapy.model.User;

/**
 * Custom array adapter for displaying list of users.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link Users}
 */
public class UserListAdapter extends ArrayAdapter<User> {

    // private member variables
    private Context _context;
    private List<User> _userList;

    // constructor
    public UserListAdapter(@NonNull Context context, @NonNull List<User> userList) {
        super(context, 0, userList);
        _context = context;
        _userList = userList;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(_context).inflate(R.layout.listview_row_design_user_1, parent, false);
        }

        User currentUser = _userList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.tvRowUserName);
        name.setText(String.format("%s, %s", currentUser.getLastName(), currentUser.getFirstName()));

        TextView email = (TextView) listItem.findViewById(R.id.tvRowUserEmail);
        email.setText(currentUser.getEmail());

        TextView accountType = (TextView) listItem.findViewById(R.id.tvRowUserAccountType);
        accountType.setText(String.format("Acct Type: %s", currentUser.get_accountType()));

        TextView status = (TextView) listItem.findViewById(R.id.tvRowUserStatus);
        status.setText(String.format("Status: %s", currentUser.get_status()));

        return listItem;
    }
}

