package example.com.hometherapy;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an object that stores a list of assigned exercises,
 * or literally, a List <AssignedExercise> object. It is necessary
 * to do it this way so that the GSON can serialize and deserialize
 * into and out of a list of assigned exercises.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link AssignedExercise}
 */
public class AssignedExerciseList {

    // private member variable.
    private List<AssignedExercise> _assignedExerciseList;

    // constructor
    public AssignedExerciseList() { _assignedExerciseList = new ArrayList<>(); }

    /**
     * Add an assignedExercise to the List <AssignedExercise> member variable.
     * @param assignedExercise
     */
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
