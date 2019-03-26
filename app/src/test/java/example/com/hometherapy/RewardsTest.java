package example.com.hometherapy;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class used to test different reward methods from the MyRewards and MyExercise classes.
 *
 * Testing the addition of points to a users rewards total, subtraction of points from a user's
 * rewards total, and testing of the <= operator when...
 *
 * @author Team06
 * @version 1.0
 * @since 2019-03-26
 */
public class RewardsTest {

    private User userOne;
    private User userTwo;
    private User userThree;


    private Integer pointValueFive;
    private Integer pointValueTen;
    private Integer pointValueFifteen;
    private Integer pointValueTwenty;
    private Integer pointValueTwentyFive;

    public RewardsTest() {
        userOne = new User("testOne@test.com", "#tEstinG1","Harry", "Potter", "9659741352",
                "ID_1234", "Active", "TestClinic", "client", "TestTherapist");

        userTwo = new User("testTwo@test.com", "#tEstinG2","Bumble", "Bee", "8659741352",
                "ID_6314", "Active", "TestClinic", "client", "TestTherapist");

        userThree = new User ("testThree@test.com", "#tEstinG3","Ben", "Ten", "7659741352",
                "ID_5843", "Active", "TestClinic", "client", "TestTherapist");

        pointValueFive = 5;
        pointValueTen = 10;
        pointValueFifteen = 15;
        pointValueTwenty = 20;
        pointValueTwentyFive = 25;
    }

    @Test
    public void addToRewardsTotal_AddFive_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userOne, pointValueFive));

        // userOne _myPoints should = 5.
    }

    @Test
    public void redeemFromRewards_SubtractFive_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyRewards.doesSubtractCorrectly(userOne, pointValueFive));

        // userOne _myPoints should = 0.
    }

    @Test
    public void addToRewardsTotal_AddTen_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userTwo, pointValueTen));

        // userTwo _myPoints should = 10.
    }

    @Test
    public void redeemFromRewards_SubtractTen_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyRewards.doesSubtractCorrectly(userTwo, pointValueTen));

        // userThree _myPoints should = 0.
    }

    @Test
    public void addToRewardsTotal_AddFifteen_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userThree, pointValueFifteen));

        // userThree _myPoints should = 15.
    }

    @Test
    public void redeemFromRewards_SubtractFifteen_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyRewards.doesSubtractCorrectly(userThree, pointValueFifteen));

        // userThree _myPoints should = 0.
    }

    @Test
    public void addToRewardsTotal_AddTwenty_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userOne, pointValueTwenty));

        // userOne _myPoints should = 20.
    }

     @Test
    public void redeemFromRewards_SubtractTwenty_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyRewards.doesSubtractCorrectly(userOne, pointValueTwenty));

        // userOne _myPoints should = 0.
    }

    @Test
    public void addToRewardsTotal_AddTwentyFive_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userTwo, pointValueTwentyFive));

        // userTwo _myPoints should = 25.
    }

    @Test
    public void subtractFromRewards_SubtractTwentyFive_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyRewards.doesSubtractCorrectly(userTwo, pointValueTwentyFive));

        // userTwo _myPoints should = 0.
    }

    @Test
    public void addToRewardsTotalAgain_AddTwenty_ReturnsTrue() {

        // Check addition logic in MyExercise.addToRewardsTotal() works correctly.
        assertTrue(MyExercise.doesAddCorrectly(userOne, pointValueTwenty));

        // userOne _myPoints should = 20.
    }

    @Test
    public void enoughRewards_ReturnsTrue() {

        userOne.set_myPoints(20);

        // Check MyRewards.enoughRewards() logic works correctly.
        assertTrue(MyRewards.enoughRewards(userOne, pointValueFive));

        // 25 <= 45 is correct so should return true.
    }

    @Test
    public void enoughRewards_BothAreEqual_ReturnsTrue() {

        userOne.set_myPoints(20);

        // Check MyRewards.enoughRewards() logic works correctly.
        assertTrue(MyRewards.enoughRewards(userOne, pointValueTwenty));

        // 20 <= 20 is correct so should return true.
    }

    @Test
    public void enoughRewards_ReturnsFalse() {

        userOne.set_myPoints(10);

        // Check MyRewards.enoughRewards() logic works correctly.
        assertFalse(MyRewards.enoughRewards(userOne, pointValueFifteen));

        // 10 <= 15 is not correct so should return false.
    }
}
