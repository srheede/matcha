package wethinkcode.co.za.matcha;

import android.net.Uri;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.regex.Pattern;


public class  Register extends AppCompatActivity
{

    private static final Pattern PASSWORD_LENGTH = Pattern.compile("^.{8,}$");
    private static final Pattern PASSWORD_NUMBER = Pattern.compile("^(?=.*[0-9]).{2,}$");
    private static final Pattern PASSWORD_LETTER = Pattern.compile("^(?=.*[a-zA-Z]).{2,}$");
    private static final Pattern PASSWORD_SPACE = Pattern.compile("^(?=\\S+$).{2,}$");
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressBar progress;
    LoginButton loginButton;
    TextView TVLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress bar

        progress = (ProgressBar)findViewById(R.id.progressBar);

        // Link to login area

        TVLogin = (TextView) findViewById(R.id.textViewLogin);

        TVLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLogin = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotoLogin);
            }
        });

        // Firebase Authentication

        mAuth = FirebaseAuth.getInstance();

        // Google Sign Up

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;
                }
            }
        });

        // Facebook Sign Up

        loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends", "user_likes", "user_gender", "user_birthday", "user_location" , "user_photos"));
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.setVisibility(View.VISIBLE);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    try {
                                        object.put("platform", "facebook");
                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(Register.this, e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    handleFacebookAccessToken(loginResult.getAccessToken(), object);
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,location,photos,likes,friends");
                request.setParameters(parameters);
                request.executeAsync();




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

        // Email Sign Up

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
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("platform", "email");
                                    object.put("email", Email);
                                }
                                catch (Exception e){
                                    Toast.makeText(Register.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                    FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    updateUI(user, object);
                                }
                                else {
                                    Toast.makeText(Register.this, "User not found in registerSubmit.setOnClickListener.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progress.setVisibility(View.GONE);
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                mAuth.signInWithEmailAndPassword(Email, PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progress.setVisibility(View.GONE);
//                                            Toast.makeText(Register.this, "User logged in.",
//                                                    Toast.LENGTH_SHORT).show();
                                            Intent gotoAccount = new Intent(getApplicationContext(), CreateProfile.class);
                                            startActivity(gotoAccount);
                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            LoginManager.getInstance().logOut();
                                            progress.setVisibility(View.GONE);
                                            Toast.makeText(Register.this, "Account already exists. Please go to login.",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            } else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(Register.this, "Authentication Failed.",
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

    private void updateUI(FirebaseUser currentUser, JSONObject object){

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        String firebaseID = currentUser.getUid();
        String Object = object.toString();

        Query query = users.child(firebaseID);
        query.addListenerForSingleValueEvent (new ValueEventListener() {

            Object User;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    try {
                        User = new JSONParser().parse(Object);
                        users.child(firebaseID).setValue(User);
                        progress.setVisibility(View.GONE);
                    }
                    catch (Exception e)
                    {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(Register.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progress.setVisibility(View.GONE);
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(Register.this, "Account already exists. Please go to login.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Intent gotoAccount = new Intent(getApplicationContext(), CreateProfile.class);
        startActivity(gotoAccount);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sign in success, update UI with the signed-in user's information
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Register.this);
                        JSONObject object = new JSONObject();
                        if (acct != null) {
                            String Username = acct.getDisplayName();
                            String FirstName = acct.getGivenName();
                            String LastName = acct.getFamilyName();
                            String Email = acct.getEmail();
                            String Id = acct.getId();
                            Uri Photo = acct.getPhotoUrl();
                            try {
                                object.put("platform", "google");
                                object.put("username", Username);
                                object.put("first_name", FirstName);
                                object.put("last_name", LastName);
                                object.put("email", Email);
                                object.put("id", Id);
                                object.put("photo", Photo);
                            }
                            catch (Exception e){
                                Toast.makeText(Register.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else{
                            Toast.makeText(Register.this, "Could not create user.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUI(user, object);
                        }
                        else {
                            Toast.makeText(Register.this, "User not found in firebaseAuthWithGoogle.",
                                    Toast.LENGTH_SHORT).show();
                        }
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

    private void handleFacebookAccessToken(AccessToken token, JSONObject object) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                updateUI(user, object);
                            }
                            else {
                                Toast.makeText(Register.this, "User not found in handleFacebookAccessToken.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthUserCollisionException){
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
