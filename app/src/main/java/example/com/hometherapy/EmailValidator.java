package example.com.hometherapy;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.regex.Pattern;

/**
 * Class used to validate email format.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class EmailValidator implements TextWatcher {

    // Pattern.compile() compiles the given regular expression into a pattern.
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    // private member variable
    private boolean _isValid = false;

    /**
     * returns true if string meets the pattern regex for an email.
     * @return
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * Takes an email as a parameter, and compares the email against the regex pattern
     * stored in EMAIL_PATTERN.
     * @param email
     * @return
     */
    public static boolean isValidEmail (CharSequence email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        _isValid = isValidEmail(s);
    }


}
