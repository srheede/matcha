package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class UserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    

    private FirebaseAuth.AuthStateListener mAuthlistener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() == null){
                Intent gotoLogout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotoLogout);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        mAuth = FirebaseAuth.getInstance();

        TextView Logout = findViewById(R.id.TextViewLogout);



        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthlistener);
        updateUI();
    }

    private void updateUI(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UID;

        if (currentUser != null) {
            UID = currentUser.getUid();
        }
        else{
            UID = null;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = database.child("users");

        Query query = users.orderByChild("firebaseID").equalTo(UID).limitToFirst(1);
        query.addValueEventListener (new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = fetchData(dataSnapshot);
                    fillForm(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fillForm(User user)
    {
        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText bio = findViewById(R.id.editTextBio);
       // EditText interests = findViewById(R.id.editTextInterests);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText username = findViewById(R.id.editTextUsername);
     //   EditText email = findViewById(R.id.email);
       /* ImageView profPic = findViewById(R.id.ImageViewProf);
        ImageView pic2 = findViewById(R.id.imageView2);
        ImageView pic3 = findViewById(R.id.imageView3);
        ImageView pic4 = findViewById(R.id.imageView4);
        ImageView pic5 = findViewById(R.id.imageView5);*/

       if (!user.getFirstName().isEmpty()) {
           firstName.setText(user.getFirstName());
       }
/*       if (!user.getBio().isEmpty()) {
           bio.setText(user.getBio());
       }
       if (!user.getLastName().isEmpty()) {
           lastName.setText(user.getLastName());
       }
       if (!user.getUsername().isEmpty()) {
           username.setText(user.getUsername());
       }*/
      //  email.setText(user.getEmail());

    }

    private User fetchData(DataSnapshot dataSnapshot)
    {
        User user = new User();

        for(DataSnapshot data : dataSnapshot.getChildren()) {
            if(data.child("platform").getValue().equals("email")) {
                user.setEmail(data.child("email").getValue(String.class));
                user.setFirebaseID(data.child("firebaseID").getValue(String.class));
            } else if (data.child("platform").getValue().equals("google")){
                user.setEmail(data.child("email").getValue(String.class));
                user.setFirebaseID(data.child("firebaseID").getValue(String.class));
                user.setFirstName(data.child("first_name").getValue(String.class));
                user.setProfPic(data.child("photo").getValue(String.class));
                user.setUsername(data.child("username").getValue(String.class));
            } else if (data.child("platform").getValue().equals("facebook")){
                user.setEmail(data.child("email").getValue(String.class));
                user.setFirebaseID(data.child("firebaseID").getValue(String.class));
                user.setBirthDate(data.child("birthday").getValue(String.class));
                user.setFirstName(data.child("first_name").getValue(String.class));
                user.setGender(data.child("gender").getValue(String.class));
                user.setLastName(data.child("last_name").getValue(String.class));
            } else if (data.child("platform").getValue().equals("matcha")){
                user.setGender(data.child("gender").getValue(String.class));
                user.setUsername(data.child("username").getValue(String.class));
                user.setProfPic(data.child("profPic").getValue(String.class));
                user.setFirstName(data.child("firstName").getValue(String.class));
                user.setFirebaseID(data.child("firebaseID").getValue(String.class));
                user.setEmail(data.child("email").getValue(String.class));
                user.setBio(data.child("bio").getValue(String.class));
                user.setBirthDate(data.child("birthDate").getValue(String.class));
                user.setInterests(data.child("interests").getValue(String[].class));
                user.setLastName(data.child("lastName").getValue(String.class));
                user.setLocation(data.child("location").getValue(String.class));
                user.setPics(data.child("pics").getValue(String[].class));
                user.setSexPref(data.child("sexPref").getValue(String.class));
            }
        }
        return (user);
    }

    /*    private boolean validateOther() {

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

}