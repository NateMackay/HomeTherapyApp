package example.com.hometherapy;

import java.util.ArrayList;
import java.util.List;

/**
 *  This is used for instantiating an object that stores a list of
 *  exercises, or literally, a List <Exercise> object. It is necessary
 *  to do it this way so that GSON can serialize and deserialize
 *  into and out of a list of exercises. The list of exercises
 *  comprises the general library of exercises.
 *  @author Team06
 *  @version 1.0
 *  @since 2019-03-19
 *  {@link Exercise} Model / data structure for an exericse object
 *  {@link Exercises} Activity/View representing exercise library
 */
public class ExerciseList {

    // private member variable.
    private List<Exercise> _exerciseList;

    // constructor
    public ExerciseList() {
        _exerciseList = new ArrayList<>();
    }

    /**
     * Add an exercise to the List <Exercise> member variable.
     * @param exercise
     */
    public void addExercise(Exercise exercise) {
        _exerciseList.add(exercise);
    }

    // getter
    public List<Exercise> getExerciseList() {
        return _exerciseList;
    }

    // output current list of exercises
    @Override
    public String toString() {
        return "ExerciseList{" +
                "_exerciseList=" + _exerciseList +
                '}';
    }
}
