package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FragSettings extends Fragment {

    private String placeName;
    private RadioButton sortByLocation;
    private RadioButton sortByPopularity;
    private EditText filterInterests;
    public static RadioButton buttonSortBy;
    private String filterPlaceId;
    private FirebaseAuth mAuth;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private User user;
    private SeekBar seekBar;
    private SeekBar seekBarMax;
    private SeekBar seekBarMin;
    private TextView distanceResult;
    private TextView resultMin;
    private TextView resultMax;
    private int maxRadius = 0;
    private int maxAge = 55;
    private int minAge = 18;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);


        mAuth = FirebaseAuth.getInstance();

        RadioGroup radioSortBy = rootView.findViewById(R.id.radioSortBy);
        sortByLocation = rootView.findViewById(R.id.radioLocation);
        sortByPopularity = rootView.findViewById(R.id.radioPopularity);
        distanceResult = rootView.findViewById(R.id.textViewResult);
        resultMin = rootView.findViewById(R.id.textViewResultMinAge);
        resultMax = rootView.findViewById(R.id.textViewResultMaxAge);
        seekBar = rootView.findViewById(R.id.seekBar);
        seekBarMax = rootView.findViewById(R.id.seekBarMaxAge);
        seekBarMin = rootView.findViewById(R.id.seekBarMinAge);
        filterInterests = rootView.findViewById(R.id.editTextFilterInterests);
        Button buttonSettings = rootView.findViewById(R.id.buttonSettings);
        int sortByID = radioSortBy.getCheckedRadioButtonId();
        buttonSortBy = rootView.findViewById(sortByID);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int effectiveProgress = progress * progress;
                String result;
                if (progress == 100) {
                    result = "max";
                } else {
                    result = effectiveProgress + "km";
                }
                distanceResult.setText(result);
                maxRadius = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double ratio = 100.0/37.0;
                int effectiveProgress = (int) (18 + progress/ratio);
                if (effectiveProgress < minAge){
                    effectiveProgress = minAge;
                }
                String result = String.valueOf(effectiveProgress);
                resultMax.setText(result);
                maxAge = effectiveProgress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double ratio = 100.0/37.0;
                int effectiveProgress = (int) (18 + progress/ratio);
                if (effectiveProgress > maxAge){
                    effectiveProgress = maxAge;
                }
                String result = String.valueOf(effectiveProgress);
                resultMin.setText(result);
                minAge = effectiveProgress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        updateUI();

        return rootView;
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
                        user = Account.fetchData(dataSnapshot);
                        fillFormSettings(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getActivity(), "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void fillFormSettings(User user) {

            filterInterests.setText(user.getFilterInterests());

            if (user.getSortBy().equalsIgnoreCase("location")) {
                sortByLocation.toggle();
            }
            if (user.getSortBy().equalsIgnoreCase("popularity")) {
                sortByPopularity.toggle();
            }

            String filterDistance = user.getFilterDistance();
            maxRadius = Integer.parseInt(filterDistance);
            seekBar.setProgress(maxRadius);

            String ageMax = user.getFilterAgeMax();
            String ageMin = user.getFilterAgeMin();

            double ratio = 100.0/37.0;
            int progressMaxAge = (int) ((Integer.parseInt(ageMax) - 18) * ratio) + 1;
            int progressMinAge = (int) ((Integer.parseInt(ageMin) - 18) * ratio) + 1;

            maxAge = progressMaxAge;
            seekBarMax.setProgress(maxAge);

            minAge = progressMinAge;
            seekBarMin.setProgress(minAge);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment locationFilterFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_filter_fragment);

            if (locationFilterFragment != null) {
                // Specify the types of place data to return.
                locationFilterFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

                // Set up a PlaceSelectionListener to handle the response.
                locationFilterFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        // TODO: Get info about the selected place.
                        filterPlaceId = place.getId();
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                        // TODO: Handle the error.
                        System.out.println(status);
                    }
                });
            }
        String placeId = user.getFilterLocation();
        placeName = "";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        Account.placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
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
    }

    void saveSettings() {

        String interests = filterInterests.getText().toString();

        String sortBy = buttonSortBy.getText().toString();

        if (!interests.isEmpty()) {
            interests = filterTags(interests);
            user.setFilterInterests(interests);
        }

        user.setSortBy(sortBy);
        user.setFilterDistance(Integer.toString(maxRadius));
        user.setFilterAgeMax(Integer.toString(maxAge));
        user.setFilterAgeMin(Integer.toString(minAge));

        if (filterPlaceId != null) {
            user.setFilterLocation(filterPlaceId);}

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            try {
                users.child(firebaseID).setValue(user);
                Toast.makeText(getActivity(), "Settings saved.",
                        Toast.LENGTH_SHORT).show();
                Intent gotoAccount = new Intent(getApplicationContext(), Account.class);
                startActivity(gotoAccount);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String filterTags(String interests) {

        Pattern pattern = Pattern.compile("[#][A-Za-z0-9-_]+");
        Matcher tags = pattern.matcher(interests);
        interests = "";
        while (tags.find()){
            interests = interests.concat(tags.group() + " ");
        }
        return interests;
    }
}
