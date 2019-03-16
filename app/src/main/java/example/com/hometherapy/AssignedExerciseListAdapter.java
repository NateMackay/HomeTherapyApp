package example.com.hometherapy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.view.LayoutInflater.from;
import static example.com.hometherapy.R.id;
import static example.com.hometherapy.R.id.tvRowStatus;
import static example.com.hometherapy.R.id.tvRowAssignedExerciseName;
//import static example.com.hometherapy.R.id.tvRowRewards;
import static example.com.hometherapy.R.layout;
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

        TextView name = (TextView) listItem.findViewById(tvRowAssignedExerciseName);
        name.setText(format("%s", currentExercise.get_exerciseName()));

        TextView discipline = (TextView) listItem.findViewById(tvRowStatus);
        discipline.setText(format("%s", currentExercise.get_status()));

        //TextView modality = (TextView) listItem.findViewById(tvRowRewards);
        //modality.setText(format("Mod: %s", currentExercise.get_rewards()));

        return listItem;
    }
}