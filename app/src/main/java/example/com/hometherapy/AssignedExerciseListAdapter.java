package example.com.hometherapy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static android.view.LayoutInflater.from;
import static example.com.hometherapy.R.id.tvRowAE1ExerciseName;
import static example.com.hometherapy.R.id.tvRowAE1PointValue;
import static example.com.hometherapy.R.id.tvRowAE1Status;
import static example.com.hometherapy.R.layout.listview_row_design_assigned_exercise_1;
import static java.lang.String.format;

/**
 * This is a custom array adapter for displaying the list of exercises assigned
 * to a given client.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class AssignedExerciseListAdapter extends ArrayAdapter<AssignedExercise> {

    // private member variables
    private Context _context;
    private List<AssignedExercise> _aExerciseList;

    // constructor
    public AssignedExerciseListAdapter(@NonNull Context context, @NonNull List<AssignedExercise> aExerciseList) {
        super(context, 0, aExerciseList);
        _context = context;
        _aExerciseList = aExerciseList;
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
        if (listItem == null) {
            listItem = from(_context).inflate(listview_row_design_assigned_exercise_1, parent, false);
        }

        AssignedExercise currentExercise = _aExerciseList.get(position);

        TextView name = (TextView) listItem.findViewById(tvRowAE1ExerciseName);
        name.setText(format("%s", currentExercise.get_exerciseName()));

        TextView discipline = (TextView) listItem.findViewById(tvRowAE1Status);
        discipline.setText(format("Status: %s", currentExercise.get_status()));

        TextView pointValue = (TextView) listItem.findViewById(tvRowAE1PointValue);
        pointValue.setText(format("Point Value: %s", currentExercise.get_pointValue()));

        return listItem;
    }
}