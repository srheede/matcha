package wethinkcode.co.za.matcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.regex.Pattern;

public class Register extends AppCompatActivity
{
    private static final Pattern PASSWORD_LENGTH =
        Pattern.compile("^.{8,}$");
    private static final Pattern PASSWORD_NUMBER =
            Pattern.compile("^(?=.*[0-9]).{2,}$");
    private static final Pattern PASSWORD_LETTER =
            Pattern.compile("^(?=.*[a-zA-Z]).{2,}$");
    private static final Pattern PASSWORD_SPACE =
            Pattern.compile("^(?=\\S+$).{2,}$");

    private boolean validateEmail() {
        EditText EmailEditText = findViewById(R.id.email);
        String Email = EmailEditText.getText().toString();
        if (Email.isEmpty()) {
            EmailEditText.setError("Field can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            EmailEditText.setError("Please enter a valid email address.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePW() {
        EditText PWEditText = findViewById(R.id.pw);
        String PW = PWEditText.getText().toString();
        if (PW.isEmpty()) {
            PWEditText.setError("Field can't be empty.");
            return false;
        } else if (!PASSWORD_LETTER.matcher(PW).matches()) {
            PWEditText.setError("Password must contain at least one letter.");
            return false;
        } else if (!PASSWORD_NUMBER.matcher(PW).matches()) {
            PWEditText.setError("Password must contain at least one digit.");
            return false;
        } else if (!PASSWORD_SPACE.matcher(PW).matches()) {
            PWEditText.setError("Password can't have any spaces.");
            return false;
        } else if (!PASSWORD_LENGTH.matcher(PW).matches()) {
            PWEditText.setError("Password must be at least 8 characters long.");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateOther() {
        EditText UsernameEditText = findViewById(R.id.username);
        EditText FirstNameEditText = findViewById(R.id.firstname);
        EditText SurnameEditText = findViewById(R.id.surname);
        String Username = UsernameEditText.getText().toString();
        String FirstName = FirstNameEditText.getText().toString();
        String Surname = SurnameEditText.getText().toString();
        if (FirstName.isEmpty()) {
            FirstNameEditText.setError("Field can't be empty.");
            return false;
        } else if (Surname.isEmpty()) {
            SurnameEditText.setError("Field can't be empty.");
            return false;
        } else if (Username.isEmpty()) {
            UsernameEditText.setError("Field can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    private boolean confirmPW() {
        EditText PWEditText = findViewById(R.id.pw);
        EditText ConfirmPWEditText = findViewById(R.id.confirmpw);
        String PW = PWEditText.getText().toString();
        String ConfirmPW = ConfirmPWEditText.getText().toString();
        if (!PW.equals(ConfirmPW)) {
            PWEditText.setError("Passwords don't match.");
            return false;
        } else {
            return true;
        }
    }

    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = database.child("users");

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile", "user_gender", "user_birthday", "user_location" , "user_photos");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        Button registerSubmit = findViewById(R.id.registerSubmit);
        registerSubmit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (validateOther() && validateEmail() && validatePW() && confirmPW()) {
                    User user;
                    EditText FirstNameEditText = findViewById(R.id.firstname);
                    EditText SurnameEditText = findViewById(R.id.surname);
                    EditText UsernameEditText = findViewById(R.id.username);
                    EditText EmailEditText = findViewById(R.id.email);
                    EditText PWEditText = findViewById(R.id.pw);

                    String FirstName = FirstNameEditText.getText().toString();
                    String Surname = SurnameEditText.getText().toString();
                    String Username = UsernameEditText.getText().toString();
                    String Email = EmailEditText.getText().toString();
                    String PW = PWEditText.getText().toString();

                    user = new User(Username, Email, FirstName, Surname, PW, "", "", "");
                    users.push().setValue(user);
                }
            }
        });
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser){

        Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
        startActivity(gotoAccount);

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
