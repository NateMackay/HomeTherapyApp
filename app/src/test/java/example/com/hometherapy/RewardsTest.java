package example.com.hometherapy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Class used to test Integer addition and subtraction performed in RewardsTest.java
 * and MyExercise.java. Redesigned to be more independent of modularization efforts.
 *
 * @author Team06
 * @version 2.0
 * @since 2019-03-26
 */
public class RewardsTest {

    private User userOne;
    private User userTwo;

    private Integer pointsResult;
    private Integer pointsAddUpdate;
    private Integer pointsMinusUpdate;

    public RewardsTest() {
        // userOne reward points, _myPoints, set to 0.
        userOne   = new User("testOne@test.com", "#tEstinG1","Harry", "Potter", "9659741352",
                "ID_1234", "Active", "TestClinic", "client", "TestTherapist");

        // userTwo reward points, _myPoints, set to 0.
        userTwo  = new User("testTwo@test.com", "#tEstinG2","Bumble", "Bee", "8659741352",
                "ID_6314", "Active", "TestClinic", "client", "TestTherapist");

        // set user reward points variables.
        userOne.set_myPoints(10);
        userTwo.set_myPoints(25);

        // Use addition operator to store in pointsAddUpdate the sum of userOne
        // reward points + 15. the sum should be 35. The logic is similar to
        // line 225 of MyExercise.java
        pointsAddUpdate = userOne.get_myPoints() + 5;

        // Use subtraction operator to store in pointsMinusUpdate the difference
        // between userTwo reward points and 10. The difference should be 15.
        // The logic is similar to line 155 of MyRewards.java
        pointsMinusUpdate = userTwo.get_myPoints() - 10;

        // Will be used to test the above transactions performed correctly.
        pointsResult = 15;
    }
    
    @Test
    public void testUserIntegerAddition_ReturnsEqual() {
        // Check Integer addition logic performed on line 44.
        assertEquals(pointsAddUpdate, pointsResult);
    }

    @Test
    public void testUserIntegerSubtraction_ReturnsEqual() {
        // Check Integer subtraction logic performed on line 49.
        assertEquals(pointsMinusUpdate, pointsResult);
    }
}
