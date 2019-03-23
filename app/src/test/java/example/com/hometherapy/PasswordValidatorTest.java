package example.com.hometherapy;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordValidatorTest {

    @Test
    public void passwordValidator_CorrectEightCharacterPassword_ReturnsTrue() {
        assertTrue(PasswordValidator.isValidPassword("5&yhM?q4"));
    }

    @Test
    public void passwordValidator_CorrectTwentyCharacterPassword_ReturnsTrue() {
        assertTrue(PasswordValidator.isValidPassword("gR5&ml[aM1kL?oP)98dR"));
    }

    @Test
    public void passwordValidator_NoDigit_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("qQ@werTg?"));
    }

    @Test
    public void passwordValidator_NoLowerCaseLetter_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("1@#4QSX&K"));
    }

    @Test
    public void passwordValidator_NoUpperCaseLetter_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("1q@d%56b"));
    }

    @Test
    public void passwordValidator_NoSpecialCharacter_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("1qQW4cV9kL2"));
    }

    @Test
    public void passwordValidator_LessThanEightCharacters_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("1@fG^l8"));
    }

    @Test
    public void passwordValidator_MoreThanTwentyCharacters_ReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("1@fG^l8mN&htYzsd3$bgn"));
    }

    @Test
    public void phoneNumberValidator_EmptyString_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber(""));
    }

}
