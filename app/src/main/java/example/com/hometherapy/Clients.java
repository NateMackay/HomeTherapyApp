package example.com.hometherapy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Clients extends AppCompatActivity {

    private TextView _tvClientViewTitle;
    private ListView _lvClientList;
    private Button _btnAddClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        _tvClientViewTitle = (TextView) findViewById(R.id.tvClientViewTitle);
        _lvClientList = (ListView) findViewById(R.id.lvClientList);
        _btnAddClient = (Button) findViewById(R.id.btnAddClient);

        _btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddClient = new Intent(Clients.this, AddEditClient.class);
                startActivity(intentAddClient);
            }
        });
    }
}
