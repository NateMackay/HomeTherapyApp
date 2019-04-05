package example.com.hometherapy;

import org.junit.Test;

import example.com.hometherapy.validator.PhoneNumberValidator;

import static org.junit.Assert.*;

/**
 * Class used to test PhoneNumberValidator.java.
 * @author Team06
 * @version 1.0
 * @since 2019-03-23
 */
public class PhoneNumberValidatorTest {

    @Test
    public void phoneNumberValidator_CorrectPhoneNumberSimple_ReturnsTrue() {
        // matches 1234567890
        assertTrue(PhoneNumberValidator.isValidPhoneNumber("1234567890"));
    }

    @Test
    public void phoneNumberValidator_CorrectPhoneNumberIncludesDashes_ReturnsTrue() {
        // matches 123-456-7890
        assertTrue(PhoneNumberValidator.isValidPhoneNumber("123-456-7890"));
    }

    @Test
    public void phoneNumberValidator_CorrectPhoneNumberIncludesDashesAndBrackets_ReturnsTrue() {
        // matches (123)456-7890
        assertTrue(PhoneNumberValidator.isValidPhoneNumber("(123)456-7890"));
    }

    @Test
    public void phoneNumberValidator_CorrectPhoneNumberIncludesBrackets_ReturnsTrue() {
        // matches (123)4567890
        assertTrue(PhoneNumberValidator.isValidPhoneNumber("(123)4567890"));
    }

    @Test
    public void phoneNumberValidator_EnteredNonDigit_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("123p456798"));
    }

    @Test
    public void phoneNumberValidator_EnteredNoDigits_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("qasdfghbny"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsSimple_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("567890435"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsDashesAfterLastDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("123-456-789"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsDashesBetweenDashes_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("123-46-7893"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsDashesBeforeFirstDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("13-467-7893"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsBracketsDashAfterDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)456-895"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsBracketsDashBeforeDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)56-8959"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsBracketsDashInBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(13)456-8995"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsBracketsAfterBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)456789"));
    }

    @Test
    public void phoneNumberValidator_NotEnoughDigitsBracketsInBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(12)4567896"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsSimple_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("56789043597"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsDashesAfterLastDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("123-456-78995"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsDashesBetweenDashes_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("123-4596-7995"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsDashesBeforeFirstDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("1234-459-7995"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsBracketsDashAfterDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)456-89787"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsBracketsDashBeforeDash_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)4536-8787"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsBracketsDashInBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(1253)456-8787"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsBracketsAfterBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(123)45678920"));
    }

    @Test
    public void phoneNumberValidator_TooManyDigitsBracketsInBrackets_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber("(1233)4567892"));
    }

    @Test
    public void phoneNumberValidator_EmptyString_ReturnsFalse() {
        assertFalse(PhoneNumberValidator.isValidPhoneNumber(""));
    }

}
