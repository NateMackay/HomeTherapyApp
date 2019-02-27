package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class register extends AppCompatActivity {

    private Button _btnCreateAccount;
    private EditText _etEmail;

    // validator for email input field
    private EmailValidator _emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        _etEmail = (EditText) findViewById(R.id.etEmail);

        // setup field validators
        _emailValidator = new EmailValidator();
        _etEmail.addTextChangedListener(_emailValidator);

        _btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!_emailValidator.isValid()) {
                    _etEmail.setError("Invalid Email");
                    _etEmail.requestFocus();
                    return;
                }

                // create intent to move to exercises activity
                Intent intentExercises = new Intent(register.this, exercises.class);

                // go to exercises activity after registration
                startActivity(intentExercises);
            }
        });

    }
}
