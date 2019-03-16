package example.com.hometherapy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private Context _context;
    private List<User> _userList;

    public UserListAdapter(@NonNull Context context, @NonNull List<User> userList) {
        super(context, 0, userList);
        _context = context;
        _userList = userList;
    }

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

