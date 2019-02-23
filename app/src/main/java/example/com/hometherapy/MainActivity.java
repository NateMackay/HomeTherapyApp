package example.com.hometherapy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // This is a comment by Nate
    // This is a comment by Emile (fingers crossed)
    // Nate's next comment
    // Emile comment
    // Eric new comment

    // create a new message
    public void createNewMessage(View view) {
        Messaging myMessage = new Messaging();
    }
}
