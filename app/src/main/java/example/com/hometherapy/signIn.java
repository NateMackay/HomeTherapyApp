package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class signIn extends AppCompatActivity {

    private EditText _etEmail;
    private EditText _etPassword;
    private Button _btnLogin;
    private Button _btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        _btnLogin = (Button) findViewById(R.id.btnLogin);
        _btnRegister = (Button) findViewById(R.id.btnRegister);

        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Exercises screen
                Intent intentExercises = new Intent(signIn.this, exercises.class);

                // go to Exercises activity after login
                startActivity(intentExercises);
            }
        });

        _btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // intent to go to Register activity
                Intent intentRegister = new Intent(signIn.this, register.class);

                // go to Register activity
                startActivity(intentRegister);
            }
        });     

    }
}
