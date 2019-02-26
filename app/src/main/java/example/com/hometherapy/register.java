package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class register extends AppCompatActivity {

    private Button _btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);

        _btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create intent to move to exercises activity
                Intent intentExercises = new Intent(register.this, exercises.class);

                // go to exercises activity after registration
                startActivity(intentExercises);
            }
        });
    }
}
