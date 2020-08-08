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
import android.widget.RadioGroup;

import com.facebook.login.LoginManager;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;


public class Account extends AppCompatActivity {

    public static PlacesClient placesClient;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragUserProfile();
                case 1:
                    return new FragMatcha();
                case 2:
                    return new FragSettings();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void sortByClick(View view) {
        RadioGroup radioSortBy = (RadioGroup) findViewById(R.id.radioSortBy);
        int sortByID = radioSortBy.getCheckedRadioButtonId();

        FragSettings.buttonSortBy = findViewById(sortByID);
    }

    public static User fetchData(DataSnapshot data) {
        User newUser = new User();

        newUser.setGender(data.child("gender").getValue(String.class));
        newUser.setBirthDate(data.child("birthDate").getValue(String.class));
        newUser.setUsername(data.child("username").getValue(String.class));
        newUser.setProfPic(data.child("profPic").getValue(String.class));
        newUser.setFirstName(data.child("firstName").getValue(String.class));
        newUser.setEmail(data.child("email").getValue(String.class));
        newUser.setBio(data.child("bio").getValue(String.class));
        newUser.setBirthDate(data.child("birthDate").getValue(String.class));
        newUser.setInterests(data.child("interests").getValue(String.class));
        newUser.setLastName(data.child("lastName").getValue(String.class));
        newUser.setLocation(data.child("location").getValue(String.class));
        newUser.setGeoHash(data.child("geoHash").getValue(String.class));
        newUser.setPic2(data.child("pic2").getValue(String.class));
        newUser.setPic3(data.child("pic3").getValue(String.class));
        newUser.setPic4(data.child("pic4").getValue(String.class));
        newUser.setPic5(data.child("pic5").getValue(String.class));
        newUser.setSexPref(data.child("sexPref").getValue(String.class));
        newUser.setNotifications(data.child("notifications").getValue(String.class));
        newUser.setSortBy(data.child("sortBy").getValue(String.class));
        newUser.setFilterDistance(data.child("filterDistance").getValue(String.class));
        newUser.setFilterInterests(data.child("filterInterests").getValue(String.class));
        newUser.setFilterLocation(data.child("filterLocation").getValue(String.class));
        newUser.setFilterAgeMax(data.child("filterAgeMax").getValue(String.class));
        newUser.setFilterAgeMin(data.child("filterAgeMin").getValue(String.class));
        newUser.setPopularity(data.child("popularity").getValue(String.class));
        newUser.setAge(data.child("age").getValue(String.class));
        return newUser;
    }
}
