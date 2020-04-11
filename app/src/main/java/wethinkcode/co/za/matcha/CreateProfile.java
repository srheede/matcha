package wethinkcode.co.za.matcha;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CreateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String GPS;
    final static int Gallery_Pick = 1;
    final static int Gallery_Pick2 = 2;
    final static int Gallery_Pick3 = 3;
    final static int Gallery_Pick4 = 4;
    final static int Gallery_Pick5 = 5;
    private StorageReference UserProfileImageRef;
    private ImageView profPic;
    private String profPicUri;
    private ImageView pic2;
    private String pic2Uri;
    private ImageView pic3;
    private String pic3Uri;
    private ImageView pic4;
    private String pic4Uri;
    private ImageView pic5;
    private String pic5Uri;
    private RadioGroup radioGender;
    private RadioButton buttonGender;
    private RadioGroup radioInterestedIn;
    private RadioButton buttonInterestedIn;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView date;
    private String birthDate;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private String placeId;
    private String geoHash;
    private AutocompleteSupportFragment autocompleteFragment;

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() == null) {
                Intent gotoLogout = new Intent(getApplicationContext(), Register.class);
                startActivity(gotoLogout);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);
        Button buttonSave;

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBgF0JZGgJDOVfDJrIapQRidnvIWcs7pfU");

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                placeId = place.getId();
                place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                System.out.println(status);
            }
        });

        date = findViewById(R.id.textViewDate);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(CreateProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow(); //.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                birthDate = dayOfMonth + "/" + month + "/" + year;
                try {
                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd MMMM yyyy");
                    Date date = format1.parse(birthDate);
                    birthDate = format2.format(date);
                }
                catch (Exception e) {
                    Toast.makeText(CreateProfile.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

               date.setText(birthDate);
            }
        };

        radioGender = findViewById(R.id.radioGender);
        radioInterestedIn = findViewById(R.id.radioInterestedIn);

        mAuth = FirebaseAuth.getInstance();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profPic = findViewById(R.id.ImageViewProf);
        pic2 = findViewById(R.id.imageView2);
        pic3 = findViewById(R.id.imageView3);
        pic4 = findViewById(R.id.imageView4);
        pic5 = findViewById(R.id.imageView5);

        int interestedInID = radioInterestedIn.getCheckedRadioButtonId();
        int genderID = radioGender.getCheckedRadioButtonId();

        buttonInterestedIn = findViewById(interestedInID);
        buttonGender = findViewById(genderID);

        buttonSave = findViewById(R.id.buttonSave);

        TextView Cancel = findViewById(R.id.TextViewCancel);

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick);
            }
        });
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick2);
            }
        });
        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick3);
            }
        });
        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick4);
            }
        });
        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadImage = new Intent();
                uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                uploadImage.setType("image/*");
                startActivityForResult(uploadImage, Gallery_Pick5);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUI();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().getCurrentUser().delete();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates("gps", 900000, 10000, locationListener);
                }
        }
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
                                    Picasso.with(CreateProfile.this).load(o.toString()).into(profPic);
                                    profPicUri = o.toString();
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == Gallery_Pick2 && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            String firebaseID = mAuth.getCurrentUser().getUid();

            StorageReference filePath = UserProfileImageRef.child(firebaseID + "2.jpg");

            try {
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task getDownloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                            getDownloadUrl.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    pic2Uri = o.toString();
                                    Picasso.with(CreateProfile.this).load(o.toString()).into(pic2);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == Gallery_Pick3 && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            String firebaseID = mAuth.getCurrentUser().getUid();

            StorageReference filePath = UserProfileImageRef.child(firebaseID + "3.jpg");

            try {
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task getDownloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                            getDownloadUrl.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    pic3Uri = o.toString();
                                    Picasso.with(CreateProfile.this).load(o.toString()).into(pic3);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == Gallery_Pick4 && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            String firebaseID = mAuth.getCurrentUser().getUid();

            StorageReference filePath = UserProfileImageRef.child(firebaseID + "4.jpg");

            try {
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task getDownloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                            getDownloadUrl.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    pic4Uri = o.toString();
                                    Picasso.with(CreateProfile.this).load(o.toString()).into(pic4);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == Gallery_Pick5 && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            String firebaseID = mAuth.getCurrentUser().getUid();

            StorageReference filePath = UserProfileImageRef.child(firebaseID + "5.jpg");

            try {
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task getDownloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl();
                            getDownloadUrl.addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    pic5Uri = o.toString();
                                    Picasso.with(CreateProfile.this).load(o.toString()).into(pic5);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CreateProfile.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        updateDB();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GPS = location.getLatitude() + " " + location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET
            }, 10);
            return;
        }
    }

    public void interestedInClick(View view) {
        int interestedInID = radioInterestedIn.getCheckedRadioButtonId();

        buttonInterestedIn = findViewById(interestedInID);
    }

    public void genderClick(View view) {
        int genderID = radioGender.getCheckedRadioButtonId();

        buttonGender = findViewById(genderID);

    }

    private void saveUI() {

        EditText editTextFirstName = findViewById(R.id.editTextFirstName);
        EditText editTextLastName = findViewById(R.id.editTextLastName);
        EditText editTextBio = findViewById(R.id.editTextBio);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextInterests = findViewById(R.id.editTextInterests);
        Switch switchNotifications = findViewById(R.id.switchNotifications);
        TextView location = findViewById(R.id.textViewLocation);

        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String bio = editTextBio.getText().toString();
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        String interests = editTextInterests.getText().toString();

        if (username.isEmpty()) {
            editTextUsername.setError("Field can't be empty.");
        } else if (firstName.isEmpty()) {
            editTextFirstName.setError("Field can't be empty.");
        } else if (lastName.isEmpty()) {
            editTextLastName.setError("Field can't be empty.");
        } else if (email.isEmpty()) {
            editTextEmail.setError("Field can't be empty.");
        } else if (placeId == null) {
            location.setError("Field can't be empty.");
        } else if (birthDate == null){
            date.setError("Birth date must be selected.");
        } else {

                String gender = buttonGender.getText().toString();
                String sexPref = buttonInterestedIn.getText().toString();
                String notifications;

                if (switchNotifications.isChecked()) {
                    notifications = "yes";
                } else {
                    notifications = "no";
                }

                User user = new User();

                user.setFirstName(firstName);
                user.setSexPref(sexPref);
                user.setLastName(lastName);
                user.setInterests(interests);
                user.setBio(bio);
                user.setEmail(email);
                user.setGender(gender);
                user.setUsername(username);
                user.setBirthDate(birthDate);
                user.setProfPic(profPicUri);
                user.setPic2(pic2Uri);
                user.setPic3(pic3Uri);
                user.setPic4(pic4Uri);
                user.setPic5(pic5Uri);
                user.setNotifications(notifications);
                user.setLocation(placeId);

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String firebaseID = mAuth.getCurrentUser().getUid();
                    try {
                        users.child(firebaseID).setValue(user);
                        Toast.makeText(CreateProfile.this, "User account created.",
                                Toast.LENGTH_SHORT).show();
                        Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
                        startActivity(gotoAccount);
                    } catch (Exception e){
                        Toast.makeText(CreateProfile.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }



    private void updateDB() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        User user = fetchData(dataSnapshot);
                        query.removeEventListener(this);
                        users.child(firebaseID).removeValue();
                        fillForm(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(CreateProfile.this, "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fillForm(User user) {

        EditText firstName = findViewById(R.id.editTextFirstName);
        EditText bio = findViewById(R.id.editTextBio);
        EditText interests = findViewById(R.id.editTextInterests);
        EditText lastName = findViewById(R.id.editTextLastName);
        EditText username = findViewById(R.id.editTextUsername);
        EditText email = findViewById(R.id.editTextEmail);
        ImageView profPic = findViewById(R.id.ImageViewProf);
        RadioButton male = findViewById(R.id.radioMale);
        RadioButton female = findViewById(R.id.radioFemale);
        RadioButton men = findViewById(R.id.radioMen);
        RadioButton women = findViewById(R.id.radioWomen);
        RadioButton both = findViewById(R.id.radioBoth);
        Switch notifications = findViewById(R.id.switchNotifications);
        ImageView pic2 = findViewById(R.id.imageView2);
        ImageView pic3 = findViewById(R.id.imageView3);
        ImageView pic4 = findViewById(R.id.imageView4);
        ImageView pic5 = findViewById(R.id.imageView5);

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        username.setText(user.getUsername());
        bio.setText(user.getBio());
        email.setText(user.getEmail());
        interests.setText(user.getInterests());


        if (!user.getBirthDate().isEmpty()) {
            date.setText(user.getBirthDate());
        }
        if (!user.getProfPic().isEmpty()) {
            Picasso.with(this).load(user.getProfPic()).into(profPic);
            profPicUri = user.getProfPic();
        } else {
            profPicUri = "";
        }
        if (!user.getPic2().isEmpty()) {
            Picasso.with(this).load(user.getPic2()).into(pic2);
            pic2Uri = user.getPic2();
        } else {
            pic2Uri = "";
        }
        if (!user.getPic3().isEmpty()) {
            Picasso.with(this).load(user.getPic3()).into(pic3);
            pic3Uri = user.getPic3();
        } else {
            pic3Uri = "";
        }
        if (!user.getPic4().isEmpty()) {
            Picasso.with(this).load(user.getPic4()).into(pic4);
            pic4Uri = user.getPic4();
        } else {
            pic4Uri = "";
        }
        if (!user.getPic5().isEmpty()) {
            Picasso.with(this).load(user.getPic5()).into(pic5);
            pic5Uri = user.getPic5();
        } else {
            pic5Uri = "";
        }
        if (user.getGender().equalsIgnoreCase("male")) {
            male.toggle();
        }
        if (user.getGender().equalsIgnoreCase("female")) {
            female.toggle();
        }
        if (user.getSexPref().equalsIgnoreCase("men")) {
            men.toggle();
        }
        if (user.getSexPref().equalsIgnoreCase("women")) {
            women.toggle();
        }
        if (user.getSexPref().equalsIgnoreCase("both")) {
            both.toggle();
        }
        if (user.getNotifications().equals("yes")) {
            notifications.setChecked(true);
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
                user.setPic2("");
                user.setPic3("");
                user.setPic4("");
                user.setPic5("");
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
                user.setPic2("");
                user.setPic3("");
                user.setPic4("");
                user.setPic5("");
                user.setSexPref("");
                break;
            case "facebook":
                user.setEmail(data.child("email").getValue(String.class));
                user.setFirstName(data.child("first_name").getValue(String.class));
                user.setGender(data.child("gender").getValue(String.class));
                user.setLastName(data.child("last_name").getValue(String.class));
                user.setUsername("");
                user.setProfPic("");
                user.setBio("");
                user.setInterests("");
                user.setLocation("");
                user.setPic2("");
                user.setPic3("");
                user.setPic4("");
                user.setPic5("");
                user.setSexPref("");
                try {
                    SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd MMMM yyyy");
                    Date date = format1.parse(data.child("birthday").getValue(String.class));
                    birthDate = format2.format(date);
                }
                catch (Exception e) {
                    Toast.makeText(CreateProfile.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                date.setText(birthDate);
                user.setBirthDate(birthDate);
                break;
            case "matcha":
                user.setGender(data.child("gender").getValue(String.class));
                user.setBirthDate(data.child("birthDate").getValue(String.class));
                user.setUsername(data.child("username").getValue(String.class));
                user.setProfPic(data.child("profPic").getValue(String.class));
                user.setFirstName(data.child("firstName").getValue(String.class));
                user.setEmail(data.child("email").getValue(String.class));
                user.setBio(data.child("bio").getValue(String.class));
                user.setBirthDate(data.child("birthDate").getValue(String.class));
                user.setInterests(data.child("interests").getValue(String.class));
                user.setLastName(data.child("lastName").getValue(String.class));
                user.setLocation(data.child("location").getValue(String.class));
                user.setPic2(data.child("pic2").getValue(String.class));
                user.setPic3(data.child("pic3").getValue(String.class));
                user.setPic4(data.child("pic4").getValue(String.class));
                user.setPic5(data.child("pic5").getValue(String.class));
                user.setSexPref(data.child("sexPref").getValue(String.class));
                user.setNotifications(data.child("notifications").getValue(String.class));
                break;
        }
        return (user);
    }
}
