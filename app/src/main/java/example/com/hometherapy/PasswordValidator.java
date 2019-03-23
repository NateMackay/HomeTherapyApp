package example.com.hometherapy;

import android.text.Editable;
import android.text.TextWatcher;
import java.util.regex.Pattern;

/**
 * Class used to validate password format.
 * @author Team06
 * @version 1.0
 * @since 2019-03-23
 */
public class PasswordValidator implements TextWatcher {

    public static final Pattern PASSWORD_PATTERN = Pattern.compile(
            // a lower case letter must occur at least once
            "((?=.*[a-z])"  +
            // at least one digit.
            "(?=.*\\d)"     +
            // an upper case letter must occur at least once
            "(?=.*[A-Z])"   +
            // a special character must occur at least once
            "(?=.*[@#$%!^&,<>/;:'\"\\.\\*\\(\\)\\[\\[\\]\\{\\}\\?])" +
            // Be between 8 and 20 characters long
            ".{8,20})"
    );

    private boolean _isValid = false;

    public boolean isValid() {
        return _isValid;
    }

    public static boolean isValidPassword (CharSequence password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        _isValid = isValidPassword(s);
    }

}

