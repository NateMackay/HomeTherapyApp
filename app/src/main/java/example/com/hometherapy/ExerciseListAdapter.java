package example.com.hometherapy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {
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

        TextView name = (TextView) listItem.findViewById(R.id.tvRowExerciseName);
        name.setText(String.format("Name:, %s",currentExercise.get_exerciseName()));

        TextView discipline = (TextView) listItem.findViewById(R.id.tvRowDiscipline);
        discipline.setText(String.format("Dsc: %s", currentExercise.get_discipline()));

        TextView modality = (TextView) listItem.findViewById(R.id.tvRowModality);
        modality.setText(String.format("Mod: %s", currentExercise.get_modality()));

        return listItem;
    }
}
