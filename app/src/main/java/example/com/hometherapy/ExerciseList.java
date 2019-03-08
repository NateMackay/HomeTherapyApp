package example.com.hometherapy;

import java.util.ArrayList;
import java.util.List;

public class ExerciseList {

    private List<Exercise> _exerciseList;

    public ExerciseList() {
        _exerciseList = new ArrayList<>();
    }

    public void addExercise(Exercise exercise) {
        _exerciseList.add(exercise);
    }

    // getter
    public List<Exercise> getExerciseList() {
        return _exerciseList;
    }

    // output current list of users
    @Override
    public String toString() {
        return "ExerciseList{" +
                "_exerciseList=" + _exerciseList +
                '}';
    }
}
