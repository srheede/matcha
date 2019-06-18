package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class UserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    ImageView profPic;
    String profPicUri;
    RadioGroup radioGender;
    RadioGroup radioInterestedIn;
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
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
        Button buttonSave;

        radioGender = findViewById(R.id.radioGender);
        radioInterestedIn = findViewById(R.id.radioInterestedIn);

        mAuth = FirebaseAuth.getInstance();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profPic = findViewById(R.id.ImageViewProf);

        buttonSave = findViewById(R.id.buttonSave);

        TextView Logout = findViewById(R.id.TextViewLogout);

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUI();
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            String firebaseID = mAuth.getCurrentUser().getUid();

            StorageReference filePath = UserProfileImageRef.child(firebaseID + ".jpg");

            try {
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task getDownloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                            getDownloadUrl.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Picasso.with(UserProfile.this).load(o.toString()).into(profPic);
                                    profPicUri = o.toString();
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(UserProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        updateDB();
    }

    private void saveUI(){

        EditText editTextFirstName = findViewById(R.id.editTextFirstName);
        EditText editTextLastName = findViewById(R.id.editTextLastName);
        EditText editTextBio = findViewById(R.id.editTextBio);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextUsername = findViewById(R.id.editTextUsername);

        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String bio = editTextBio.getText().toString();
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        int gender = radioGender.getCheckedRadioButtonId();
        int interestedIn = radioInterestedIn.getCheckedRadioButtonId();
        System.out.println("test");
        System.out.println(interestedIn);
        String Gender;
        if (gender == 2131230902) {
            Gender = "male";
        } else {
            Gender = "female";
        }
        String sexPref;
        switch (interestedIn) {
            case 2131230902 :
                sexPref = "men";
                break;
            case 2131230900 :
                sexPref = "women";
                break;
            case 2131230899 :
                sexPref = "both";
                break;
            default :
                sexPref = "";
                break;
        }



        User user = new User();

        user.setFirstName(firstName);
        user.setSexPref(sexPref);
        user.setLocation("");
        user.setLastName(lastName);
        user.setInterests("");
        user.setBirthDate("");
        user.setBio(bio);
        user.setEmail(email);
        user.setGender(Gender);
        user.setUsername(username);
        if (profPicUri.isEmpty())
        {
            user.setProfPic("");
        }
        else {
            user.setProfPic(profPicUri);
        }
        user.setPics("");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            users.child(firebaseID).setValue(user);
        }
    }

    private void updateDB(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            Query query = users.child(firebaseID);

            query.addValueEventListener (new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        User user = fetchData(dataSnapshot);
                        query.removeEventListener(this);
                        users.child(firebaseID).setValue(user);
                        fillForm(user);
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(UserProfile.this, "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

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
        else {
            Toast.makeText(UserProfile.this, "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fillForm(User user)
    {
        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText bio = findViewById(R.id.editTextBio);
       // EditText interests = findViewById(R.id.editTextInterests);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText username = findViewById(R.id.editTextUsername);
        EditText email = findViewById(R.id.editTextEmail);
        ImageView profPic = findViewById(R.id.ImageViewProf);
        RadioButton male = findViewById(R.id.radioMale);
        RadioButton female = findViewById(R.id.radioFemale);
        RadioButton men = findViewById(R.id.radioMen);
        RadioButton women = findViewById(R.id.radioWomen);
        RadioButton both = findViewById(R.id.radioBoth);
       /* ImageView pic2 = findViewById(R.id.imageView2);
        ImageView pic3 = findViewById(R.id.imageView3);
        ImageView pic4 = findViewById(R.id.imageView4);
        ImageView pic5 = findViewById(R.id.imageView5);*/

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        username.setText(user.getUsername());
        bio.setText(user.getBio());
        email.setText(user.getEmail());
        if (!user.getProfPic().isEmpty()) {
            Picasso.with(this).load(user.getProfPic()).into(profPic);
        }
        if (user.getGender().equals("male")){
            male.toggle();
        }
        if (user.getGender().equals("female")){
            female.toggle();
        }
        if (user.getSexPref().equals("men")){
            men.toggle();
        }
        if (user.getSexPref().equals("women")){
            women.toggle();
        }
        if (user.getSexPref().equals("both")){
            both.toggle();
        }
    }

    private User fetchData(DataSnapshot data) {
        User user = new User();
            String platform = data.child("platform").getValue(String.class);
                switch (platform) {
                    case "email":
                        user.setEmail(data.child("email").getValue(String.class));
                        user.setGender("");
                        user.setUsername("");
                        user.setProfPic("");
                        user.setFirstName("");
                        user.setBio("");
                        user.setBirthDate("");
                        user.setInterests("");
                        user.setLastName("");
                        user.setLocation("");
                        user.setPics("");
                        user.setSexPref("");
                        break;
                    case "google":
                        user.setEmail(data.child("email").getValue(String.class));
                        user.setFirstName(data.child("first_name").getValue(String.class));
                        user.setProfPic(data.child("photo").getValue(String.class));
                        user.setUsername(data.child("username").getValue(String.class));
                        user.setGender("");
                        user.setBio("");
                        user.setBirthDate("");
                        user.setInterests("");
                        user.setLastName("");
                        user.setLocation("");
                        user.setPics("");
                        user.setSexPref("");
                        break;
                    case "facebook":
                        user.setEmail(data.child("email").getValue(String.class));
                        user.setBirthDate(data.child("birthday").getValue(String.class));
                        user.setFirstName(data.child("first_name").getValue(String.class));
                        user.setGender(data.child("gender").getValue(String.class));
                        user.setLastName(data.child("last_name").getValue(String.class));
                        user.setUsername("");
                        user.setProfPic("");
                        user.setBio("");
                        user.setInterests("");
                        user.setLocation("");
                        user.setPics("");
                        user.setSexPref("");
                        break;
                    case "matcha":
                        user.setGender(data.child("gender").getValue(String.class));
                        user.setUsername(data.child("username").getValue(String.class));
                        user.setProfPic(data.child("profPic").getValue(String.class));
                        user.setFirstName(data.child("firstName").getValue(String.class));
                        user.setEmail(data.child("email").getValue(String.class));
                        user.setBio(data.child("bio").getValue(String.class));
                        user.setBirthDate(data.child("birthDate").getValue(String.class));
                        user.setInterests(data.child("interests").getValue(String.class));
                        user.setLastName(data.child("lastName").getValue(String.class));
                        user.setLocation(data.child("location").getValue(String.class));
                        user.setPics(data.child("pics").getValue(String.class));
                        user.setSexPref(data.child("sexPref").getValue(String.class));
                        break;
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