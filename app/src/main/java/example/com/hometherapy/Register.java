package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity {

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

                // create intent to move to Exercises activity
                Intent intentExercises = new Intent(Register.this, Exercises.class);

                // go to Exercises activity after registration
                startActivity(intentExercises);
            }
        });

    }
}
