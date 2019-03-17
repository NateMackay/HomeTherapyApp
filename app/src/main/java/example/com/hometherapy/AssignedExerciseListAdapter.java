package example.com.hometherapy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.view.LayoutInflater.from;
import static example.com.hometherapy.R.id.tvRowAE1ExerciseName;
import static example.com.hometherapy.R.id.tvRowAE1PointValue;
import static example.com.hometherapy.R.id.tvRowAE1Status;
import static example.com.hometherapy.R.layout.listview_row_design_assigned_exercise_1;
import static java.lang.String.format;

/**
 * An AssignedExerciseListAdapter class.
 */
public class AssignedExerciseListAdapter extends ArrayAdapter<AssignedExercise> {
    private Context _context;
    private List<AssignedExercise> _aExerciseList;

    public AssignedExerciseListAdapter(@NonNull Context context, @NonNull List<AssignedExercise> aExerciseList) {
        super(context, 0, aExerciseList);
        _context = context;
        _aExerciseList = aExerciseList;
    }

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
        pointValue.setText(format("Point Value: %s", "25")); // "25" value is dummy value until points are implemented

        return listItem;
    }
}