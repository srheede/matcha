package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerSubmit = findViewById(R.id.registerSubmit);
        registerSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText FirstNameEditText = findViewById(R.id.firstname);
                EditText SurnameEditText = findViewById(R.id.surname);
                EditText UsernameEditText = findViewById(R.id.username);
                EditText EmailEditText = findViewById(R.id.email);
                EditText PWEditText = findViewById(R.id.pw);
                EditText ConfirmPWEditText = findViewById(R.id.confirmpw);

                String FirstName = FirstNameEditText.getText().toString();
                String Surname = SurnameEditText.getText().toString();
                String Username = UsernameEditText.getText().toString();
                String Email = EmailEditText.getText().toString();
                String PW = PWEditText.getText().toString();
                String ConfirmPW = ConfirmPWEditText.getText().toString();

                Intent submitRegister = new Intent(getApplicationContext(), submitRegister.class);
                submitRegister.putExtra("wethinkcode.co.za.matcha.FirstName", FirstName);
                startActivity(submitRegister);

            }
        });
    }
}
