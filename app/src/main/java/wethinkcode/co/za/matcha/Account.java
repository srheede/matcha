package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;


public class Account extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView date;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Button buttonYes;
    private Button buttonNo;
    private String placeId;
    private AutocompleteSupportFragment locationFilterFragment;
    private PlacesClient placesClient;
    private String placeName;
    private RadioGroup radioSortBy;
    private RadioButton buttonSortBy;

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            if (firebaseAuth.getCurrentUser() == null) {
                Intent gotoLogout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotoLogout);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

        radioSortBy = findViewById(R.id.radioSortBy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        updateUI();
        nextMatch();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_changePW) {
            Intent gotoLogin = new Intent(getApplicationContext(), ChangePassword.class);
            startActivity(gotoLogin);
            return true;
        } else if (id == R.id.action_editProfile) {
            Intent gotoLogin = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(gotoLogin);
            return true;
        } else if (id == R.id.action_settings) {
            Intent gotoLogin = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(gotoLogin);
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortByClick(View view) {
        int sortByID = radioSortBy.getCheckedRadioButtonId();

        buttonSortBy = findViewById(sortByID);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FragUserProfile fragUserProfile = new FragUserProfile();
                    return fragUserProfile;
                case 1:
                    FragMatcha fragMatcha = new FragMatcha();
                    return fragMatcha;
                case 2:
                    FragSettings fragSettings = new FragSettings();
                    return fragSettings;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void updateUI() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        User user = fetchData(dataSnapshot);
                        fillForm(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(Account.this, "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void nextMatch() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = selectNextMatch(mAuth.getCurrentUser().getUid());
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        User user = fetchData(dataSnapshot);
                        fillFormMatcha(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(Account.this, "No new matches in your area.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String selectNextMatch(String uid) {
        return uid;
    }

    private User fetchData(DataSnapshot data) {
        User user = new User();

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
        user.setSortBy(data.child("sortBy").getValue(String.class));
        user.setFilterDistance(data.child("filterDistance").getValue(String.class));
        user.setFilterInterests(data.child("filterInterests").getValue(String.class));
        user.setFilterLocation(data.child("filterLocation").getValue(String.class));
        return (user);
    }

    private void fillForm(User user) {
        date = findViewById(R.id.textViewDate);
        TextView firstName = findViewById(R.id.textViewFirstName);
        TextView bio = findViewById(R.id.textViewBio);
        TextView interests = findViewById(R.id.textViewInterests);
        TextView lastName = findViewById(R.id.textViewLastName);
        TextView username = findViewById(R.id.textViewUsername);
        TextView email = findViewById(R.id.textViewEmail);
        TextView gender = findViewById(R.id.textViewGender);
        TextView location = findViewById(R.id.textViewLocation);
        TextView interestedIn = findViewById(R.id.textViewInterestedIn);
        ImageView profPic = findViewById(R.id.ImageViewProf);
        ImageView pic2 = findViewById(R.id.imageView2);
        ImageView pic3 = findViewById(R.id.imageView3);
        ImageView pic4 = findViewById(R.id.imageView4);
        ImageView pic5 = findViewById(R.id.imageView5);

        placeId = user.getLocation();
        placeName = "";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeName = place.getName();
            location.setText(placeName);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                System.out.println(apiException.getMessage());
            }
        });

        username.setText(user.getUsername());
        email.setText(user.getEmail());
        date.setText(user.getBirthDate());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        bio.setText(user.getBio());
        interests.setText(user.getInterests());
        gender.setText(user.getGender());
        interestedIn.setText(user.getSexPref());
        location.setText(user.getLocation());

        if (!user.getProfPic().isEmpty()) {
            Picasso.with(this).load(user.getProfPic()).into(profPic);
        } else {
            profPic.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic2().isEmpty()) {
            Picasso.with(this).load(user.getPic2()).into(pic2);
        } else {
            pic2.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic3().isEmpty()) {
            Picasso.with(this).load(user.getPic3()).into(pic3);
        } else {
            pic3.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic4().isEmpty()) {
            Picasso.with(this).load(user.getPic4()).into(pic4);
        } else {
            pic4.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic5().isEmpty()) {
            Picasso.with(this).load(user.getPic5()).into(pic5);
        } else {
            pic5.setVisibility(View.INVISIBLE);
        }
    }

    private void fillFormSettings(User user) {

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        // Create a new Places client instance
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        locationFilterFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_filter_fragment);

        // Specify the types of place data to return.
        locationFilterFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        locationFilterFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                placeId = place.getId();
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                System.out.println(status);
            }
        });

        RadioButton sortByLocation = findViewById(R.id.radioLocation);
        RadioButton sortByPopularity = findViewById(R.id.radioPopularity);
        RadioButton sortByBoth = findViewById(R.id.radioSortBoth);
        EditText filterInterests = findViewById(R.id.editTextFilterInterests);

        placeId = user.getFilterLocation();
        placeName = "";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeName = place.getName();
            locationFilterFragment.setText(placeName);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                System.out.println(apiException.getMessage());
            }
        });

        filterInterests.setText(user.getFilterInterests());

        if (user.getSortBy().equalsIgnoreCase("location")) {
            sortByLocation.toggle();
        }
        if (user.getSortBy().equalsIgnoreCase("popularity")) {
            sortByPopularity.toggle();
        }
        if (user.getSortBy().equalsIgnoreCase("both")) {
            sortByBoth.toggle();
        }
    }

    private void fillFormMatcha(User user) {

        buttonYes = findViewById(R.id.buttonYes);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMatch();
            }
        });

        buttonNo = findViewById(R.id.buttonNo);

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMatch();
            }
        });

        date = findViewById(R.id.textViewDate2);
        TextView firstName = findViewById(R.id.textViewFirstName2);
        TextView bio = findViewById(R.id.textViewBio2);
        TextView interests = findViewById(R.id.textViewInterests2);
        TextView lastName = findViewById(R.id.textViewLastName2);
        TextView username = findViewById(R.id.textViewUsername2);
        TextView email = findViewById(R.id.textViewEmail2);
        TextView gender = findViewById(R.id.textViewGender2);
        TextView location = findViewById(R.id.textViewLocation2);
        TextView interestedIn = findViewById(R.id.textViewInterestedIn2);
        ImageView profPic = findViewById(R.id.ImageViewProf2);
        ImageView pic2 = findViewById(R.id.imageView22);
        ImageView pic3 = findViewById(R.id.imageView32);
        ImageView pic4 = findViewById(R.id.imageView42);
        ImageView pic5 = findViewById(R.id.imageView52);

        placeId = user.getLocation();
        placeName = "";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeName = place.getName();
            location.setText(placeName);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                System.out.println(apiException.getMessage());
            }
        });


        username.setText(user.getUsername());
        email.setText(user.getEmail());
        date.setText(user.getBirthDate());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        bio.setText(user.getBio());
        interests.setText(user.getInterests());
        gender.setText(user.getGender());
        interestedIn.setText(user.getSexPref());
        location.setText(user.getLocation());

        if (!user.getProfPic().isEmpty()) {
            Picasso.with(this).load(user.getProfPic()).into(profPic);
        } else {
            profPic.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic2().isEmpty()) {
            Picasso.with(this).load(user.getPic2()).into(pic2);
        } else {
            pic2.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic3().isEmpty()) {
            Picasso.with(this).load(user.getPic3()).into(pic3);
        } else {
            pic3.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic4().isEmpty()) {
            Picasso.with(this).load(user.getPic4()).into(pic4);
        } else {
            pic4.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic5().isEmpty()) {
            Picasso.with(this).load(user.getPic5()).into(pic5);
        } else {
            pic5.setVisibility(View.INVISIBLE);
        }
    }
}
