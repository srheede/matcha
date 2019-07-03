package wethinkcode.co.za.matcha;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.facebook.login.LoginManager;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.simple.parser.JSONParser;


public class MainActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    LoginButton loginButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar progress;
    TextView TVSignUp;
    TextView TVForgot;

    private boolean validateDetails() {
        EditText EmailEditText = findViewById(R.id.email);
        EditText PWEditText = findViewById(R.id.pw);
        String Email = EmailEditText.getText().toString();
        String PW = PWEditText.getText().toString();
        if (Email.isEmpty()) {
            EmailEditText.setError("Field can't be empty.");
            return false;
        } else if (PW.isEmpty()) {
            PWEditText.setError("Field can't be empty.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TVSignUp = (TextView) findViewById(R.id.textViewSignUp);
        TVForgot = (TextView) findViewById(R.id.textViewForgot);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.buttonFacebookLogin);
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

        TVSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(gotoRegister);
            }
        });

        TVForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoForgotPW = new Intent(getApplicationContext(), ForgotPW.class);
                startActivity(gotoForgotPW);
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

        Button loginSubmit = findViewById(R.id.loginSubmit);
        loginSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firebaseAuthWithEmail();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login failed.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
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



    private void firebaseAuthWithEmail() {


        if (validateDetails()) {
            progress.setVisibility(View.VISIBLE);

            EditText EmailEditText = findViewById(R.id.email);
            EditText PWEditText = findViewById(R.id.pw);

            String Email = EmailEditText.getText().toString();
            String PW = PWEditText.getText().toString();

            DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

            mAuth.signInWithEmailAndPassword(Email, PW)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String firebaseID = mAuth.getCurrentUser().getUid();
                                Query query = users.orderByKey().equalTo(firebaseID);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "User logged in.",
                                                Toast.LENGTH_SHORT).show();
                                            Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
                                            startActivity(gotoAccount);
                                        } else {
                                            progress.setVisibility(View.GONE);
                                            Toast.makeText(MainActivity.this, "Account doesn't exist. Please sign up first.",
                                                    Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(MainActivity.this, "User not found in firebaseAuthWithGoogle.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }








    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String firebaseID = mAuth.getCurrentUser().getUid();
                            Query query = users.orderByKey().equalTo(firebaseID);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "User logged in.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
                                        startActivity(gotoAccount);
                                    } else {
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Account doesn't exist. Please sign up first.",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "User not found in firebaseAuthWithGoogle.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String firebaseID = mAuth.getCurrentUser().getUid();
                            Query query = users.orderByKey().equalTo(firebaseID);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "User logged in.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
                                        startActivity(gotoAccount);
                                    } else {
                                        progress.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Account doesn't exist. Please sign up first.",
                                                Toast.LENGTH_SHORT).show();
                                        LoginManager.getInstance().logOut();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "User not found in handleFacebookAccessToken.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

