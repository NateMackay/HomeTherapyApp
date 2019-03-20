package example.com.hometherapy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom array adapter for displaying list of clients.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link MyClients}
 */
public class MyClientsAdapter extends ArrayAdapter<User> {

    private Context _context;
    private List<User> _userList;

    public MyClientsAdapter(@NonNull Context context, @NonNull List<User> userList) {
        super(context, 0, userList);
        _context = context;
        _userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(_context).inflate(R.layout.listview_row_design_myclients_1, parent, false);
        }

        User currentUser = _userList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.tvRowMCName);
        name.setText(String.format("%s, %s", currentUser.getLastName(), currentUser.getFirstName()));

        TextView email = (TextView) listItem.findViewById(R.id.tvRowMCEmail);
        email.setText(currentUser.getEmail());

        TextView phone = (TextView) listItem.findViewById(R.id.tvRowMCPhone);
        phone.setText(currentUser.getPhone());

        TextView assignedClinic = (TextView) listItem.findViewById(R.id.tvRowMCAssignedClinic);
        assignedClinic.setText(String.format("Clinic: %s", currentUser.get_assignedClinic()));

        TextView status = (TextView) listItem.findViewById(R.id.tvRowMCStatus);
        status.setText(String.format("Status: %s", currentUser.get_status()));

        String pointValue = "0"; // temporary place holder for points until set in User class
        TextView points = (TextView) listItem.findViewById(R.id.tvRowMCPoints);
        points.setText(String.format("Points: %s",pointValue));

        return listItem;
    }
}
