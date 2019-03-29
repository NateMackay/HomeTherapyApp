package example.com.hometherapy;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Class used to test the toMap() method in Exercise.java.
 * @author Team06
 * @version 1.0
 * @since 2019-03-29
 */
public class ExerciseTest {

    // Exercise variables.
    private String exerciseID = "ID_1234";
    private String exerciseName = "Testing Speaking";
    private String discipline = "Speech Testing";
    private String modality = "Modal Test";
    private String assignment = "Testing how to speak slowly.";
    private String videoLink = "Link";

    // Created Exercise variable with above variable values.
    private Exercise exercise = new Exercise(exerciseID, exerciseName,
            discipline, modality, assignment, videoLink);

    // Create Map object and initialize with a call to the Exercise toMap() method.
    private Map<String, Object> mapTest = exercise.toMap();

    // Test that each variable is mapped correctly from exercise to mapTest.

    @Test
    public void exercise_ExerciseIdMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_exerciseID(), mapTest.get("_exerciseID"));
    }

    @Test
    public void exercise_ExerciseNameMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_exerciseName(), mapTest.get("_exerciseName"));
    }

    @Test
    public void exercise_ExerciseDisciplineMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_discipline(), mapTest.get("_discipline"));
    }

    @Test
    public void exercise_ExerciseModalityMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_modality(), mapTest.get("_modality"));
    }

    @Test
    public void exercise_ExerciseAssignmentMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_assignment(), mapTest.get("_assignment"));
    }

    @Test
    public void exercise_ExerciseVideoLinkMapsCorrectly_ReturnsEqual() {
        assertEquals(exercise.get_videoLink(), mapTest.get("_videoLink"));
    }
    
}