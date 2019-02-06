package wethinkcode.co.za.matcha;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class submitRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_register);

        if (getIntent().hasExtra("wethinkcode.co.za.matcha.FirstName")) {
            TextView FirstNameTV = findViewById(R.id.FirstNameTV);
            String FirstName = getIntent().getExtras().getString("wethinkcode.co.za.matcha.FirstName");
            FirstNameTV.setText(FirstName);
        }
    }
}
