package example.com.hometherapy;

import android.text.Editable;
import android.text.TextWatcher;
import java.util.regex.Pattern;

/**
 * Class used to validate phone number format.
 * @author Team06
 * @version 1.0
 * @since 2019-03-23
 */
public class PhoneNumberValidator implements TextWatcher  {


    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(
            // matches 1234567890
            "\\d{10}" +
                    // or
                    "|" +
                    // matches 123-456-7890
                    "(?:\\d{3}-){2}\\d{4}" +
                    // or
                    "|" +
                    // matches (123)456-7890 or (123)4567890
                    "\\(\\d{3}\\)\\d{3}-?\\d{4}"
    );

    private boolean _isValid = false;

    public boolean isValid() {
        return _isValid;
    }

    public static boolean isValidPhoneNumber (CharSequence phoneNumber) {
        return phoneNumber != null && PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        _isValid = isValidPhoneNumber(s);
    }

}
