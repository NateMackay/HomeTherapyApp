package example.com.hometherapy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * This is a custom array adapter for displaying the list of exercises
 * in a pretty format.
 * It is used by both the Exercises activity {@link Exercises} and the
 * Client Exercise Library activity {@link ClientExerciseLibrary}.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class ExerciseListAdapter extends ArrayAdapter<Exercise> {

    // for log
    private static final String TAG = "ExerciseListAdapter";

    private Context _context;
    private List<Exercise> _exerciseList;

    public ExerciseListAdapter(@NonNull Context context, @NonNull List<Exercise> exerciseList) {
        super(context, 0, exerciseList);
        _context = context;
        _exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(_context).inflate(R.layout.listview_row_design_exercise_1, parent, false);
        }

        Exercise currentExercise = _exerciseList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.tvRowELExerciseName);
        name.setText(String.format("Name: %s",currentExercise.get_exerciseName()));

        TextView discipline = (TextView) listItem.findViewById(R.id.tvRowELDiscipline);
        discipline.setText(String.format("Discipline: %s",currentExercise.get_discipline()));

        TextView modality = (TextView) listItem.findViewById(R.id.tvRowELModality);
        modality.setText(String.format("Modality: %s",currentExercise.get_modality()));

        return listItem;
    }
}
