package wethinkcode.co.za.matcha;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPW extends AppCompatActivity {

    private TextView Email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);

        Email = findViewById(R.id.email);
        Button reset = findViewById(R.id.buttonReset);
        mAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(Email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ForgotPW.this, "Check your email account to reset your password.",
                                Toast.LENGTH_SHORT).show();
                        Intent gotoAccount = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(gotoAccount);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPW.this, "This account does not exist.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
