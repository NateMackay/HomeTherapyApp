package example.com.hometherapy;

import org.junit.Test;

import java.util.Map;

import example.com.hometherapy.model.AssignedExercise;

import static org.junit.Assert.assertEquals;

/**
 * Class used to test the toMap() method in AssignedExercise.java.
 * @author Team06
 * @version 1.0
 * @since 2019-03-29
 */
public class AssignedExerciseTest {

    // Exercise variables.
    private String exerciseID = "ID_5678";
    private String exerciseName = "Breathing deeply";
    private String discipline = "Breathing Testing";
    private String modality = "Modal Test";
    private String assignment = "Testing how to breath slow and deep.";
    private String videoLink = "Link";

    // Specific to Assigned Exercise.
    private String assignedExerciseID = "AE_5678";
    private String assignedUserID = "My_ID_9445839";
    private Integer pointValue = 20;
    private String status = "active";
    private Boolean completedToday = false;

    // Created AssignedExercise variable with above variable values.
    private AssignedExercise assignedExercise = new AssignedExercise(exerciseID, exerciseName,
            discipline, modality, assignment, videoLink, assignedExerciseID,
            assignedUserID, pointValue, status, completedToday);

    // Create Map object and initialize with a call to the AssignedExercise toMap() method.
    private Map<String, Object> mapTest = assignedExercise.toMap();

    // Test that each variable is mapped correctly from assignedExercise to mapTest.

    @Test
    public void assignedExercise_ExerciseIdMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_exerciseID(), mapTest.get("_exerciseID"));
    }

    @Test
    public void assignedExercise_ExerciseNameMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_exerciseName(), mapTest.get("_exerciseName"));
    }

    @Test
    public void assignedExercise_ExerciseDisciplineMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_discipline(), mapTest.get("_discipline"));
    }

    @Test
    public void assignedExercise_ExerciseModalityMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_modality(), mapTest.get("_modality"));
    }

    @Test
    public void assignedExercise_ExerciseAssignmentMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_assignment(), mapTest.get("_assignment"));
    }

    @Test
    public void assignedExercise_ExerciseVideoLinkMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_videoLink(), mapTest.get("_videoLink"));
    }

    @Test
    public void assignedExercise_AssignedExerciseIdMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_assignedExerciseID(), mapTest.get("_assignedExerciseID"));
    }

    @Test
    public void assignedExercise_AssignedExerciseUserIdMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_assignedUserID(), mapTest.get("_assignedUserID"));
    }

    @Test
    public void assignedExercise_AssignedExercisePointValueMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_pointValue(), mapTest.get("_pointValue"));
    }

    @Test
    public void assignedExercise_AssignedExerciseStatusMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_status(), mapTest.get("_status"));
    }

    @Test
    public void assignedExercise_AssignedExerciseCompletedTodayMapsCorrectly_ReturnsEqual() {
        assertEquals(assignedExercise.get_completedToday(), mapTest.get("_completedToday"));
    }
}