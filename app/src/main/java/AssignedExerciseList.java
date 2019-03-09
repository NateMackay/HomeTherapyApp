import java.util.ArrayList;
import java.util.List;

import example.com.hometherapy.Exercise;

public class AssignedExerciseList {

    private List<AssignedExercise> _assignedExerciseList;

    public AssignedExerciseList() { _assignedExerciseList = new ArrayList<>(); }

    public void addAssignedExercise(AssignedExercise assignedExercise) {
        _assignedExerciseList.add(assignedExercise);
    }

    // getter
    public List<AssignedExercise> getAssignedExerciseList() {
        return _assignedExerciseList;
    }

    // output current list of assigned exercises
    @Override
    public String toString() {
        return "AssignedExerciseList{" +
                "_assignedExerciseList=" + _assignedExerciseList +
                '}';
    }
}
