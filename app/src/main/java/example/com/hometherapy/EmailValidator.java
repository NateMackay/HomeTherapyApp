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

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean _isValid = false;

    public boolean isValid() {
        return _isValid;
    }

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
