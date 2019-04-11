package wethinkcode.co.za.matcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.regex.Pattern;


public class  Register extends AppCompatActivity
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

/*    private boolean validateOther() {
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
    }*/

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
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar progress;
    private TextView TVLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                    Intent gotoAccount = new Intent(getApplicationContext(), UserProfile.class);
                    startActivity(gotoAccount);
                }
            }
        });

        TVLogin = (TextView) findViewById(R.id.textViewLogin);

        progress = (ProgressBar)findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = database.child("users");

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile", "user_gender", "user_birthday", "user_location" , "user_photos");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.setVisibility(View.VISIBLE);
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

        TVLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLogin = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotoLogin);
            }
        });

        SignInButton signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        Button registerSubmit = findViewById(R.id.loginSubmit);
        registerSubmit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                progress.setVisibility(View.VISIBLE);

                if (validateEmail() && validatePW() && confirmPW()) {
                    EditText EmailEditText = findViewById(R.id.email);
                    EditText PWEditText = findViewById(R.id.pw);

                    String Email = EmailEditText.getText().toString();
                    String PW = PWEditText.getText().toString();

                    mAuth.createUserWithEmailAndPassword(Email, PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "User account created.",
                                        Toast.LENGTH_SHORT).show();
                               // FirebaseUser user = task.getResult().getUser();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "User already exists.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                progress.setVisibility(View.GONE);
                // Google Sign In failed, update UI appropriately
                Toast.makeText(Register.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                // ...
            }
        }
        else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser currentUser){


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = database.child("users");
       // String UID;
       // String username;
        //String email;
        User user;

       // UID = currentUser.getUid();
       // username = currentUser.getDisplayName();
      //  email = currentUser.getEmail();
        user = new User("UID", "username", "email");
       // users.push().setValue(user);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(Register.this, "User account created.",
                                Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Register.this, "User account created.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthUserCollisionException){
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "User already exists.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progress.setVisibility(View.GONE);
                        }
                    });
        }
    }
