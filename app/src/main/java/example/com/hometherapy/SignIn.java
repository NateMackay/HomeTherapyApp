package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn_Activity";

    private EditText _etSignInEmail;
    private EditText _etSignInPassword;
    private Button _btnLogin;
    private Button _btnRegister;

    String _therapistEmails[] = {"adam@ac.net", "bonnie@ac.net", "charlie@ac.net"};

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _btnRegister = (Button) findViewById(R.id.btnRegister);
        _etSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
        _etSignInPassword = (EditText) findViewById(R.id.etSignInPassword);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etSignInEmail.addTextChangedListener(_emailValidator);

        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!_emailValidator.isValid()) {
                    _etSignInEmail.setError("Invalid Email");
                    _etSignInEmail.requestFocus();
                    return;
                }

                String SignInEmail = _etSignInEmail.getText().toString();

                boolean isTherapist = false;

                for (String value : _therapistEmails) {

                    Log.d(TAG, "onClick: SignInEmail - " + SignInEmail);
                    Log.d(TAG, "onClick: Therapist Email - " + value);

                    if (SignInEmail.equals(value)) {
                        isTherapist = true;
                    }
                }

                Log.d(TAG, "onClick: isTherpapist - " + isTherapist);

                if (isTherapist == true) {
                    // intent to go to Clients screen
                    Intent intentClients = new Intent(SignIn.this, Clients.class);
                    startActivity(intentClients);
                } else {
                    // intent to go to Exercises screen
                    Intent intentExercises = new Intent(SignIn.this, TestNavigation.class);
                    startActivity(intentExercises);
                }
            }
        });

        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Register activity
                Intent intentRegister = new Intent(SignIn.this, Register.class);

                // go to Register activity
                startActivity(intentRegister);
            }
        });
    }
}
