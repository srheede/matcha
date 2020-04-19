package wethinkcode.co.za.matcha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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


public class FragUserProfile extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private String placeName;
    private User user;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_userprofile, container, false);

        mAuth = FirebaseAuth.getInstance();

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
                        fillForm();
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

    private void fillForm() {
        TextView date = rootView.findViewById(R.id.textViewDate);
        TextView firstName = rootView.findViewById(R.id.textViewFirstName);
        TextView bio = rootView.findViewById(R.id.textViewBio);
        TextView interests = rootView.findViewById(R.id.textViewInterests);
        TextView lastName = rootView.findViewById(R.id.textViewLastName);
        TextView username = rootView.findViewById(R.id.textViewUsername);
        TextView email = rootView.findViewById(R.id.textViewEmail);
        TextView gender = rootView.findViewById(R.id.textViewGender);
        TextView location = rootView.findViewById(R.id.textViewLocation);
        TextView interestedIn = rootView.findViewById(R.id.textViewInterestedIn);
        ImageView profPic = rootView.findViewById(R.id.ImageViewProf);
        ImageView pic2 = rootView.findViewById(R.id.imageView2);
        ImageView pic3 = rootView.findViewById(R.id.imageView3);
        ImageView pic4 = rootView.findViewById(R.id.imageView4);
        ImageView pic5 = rootView.findViewById(R.id.imageView5);

        String placeId = user.getLocation();
        placeName = "";

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        Account.placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
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
            Picasso.with(getActivity()).load(user.getProfPic()).into(profPic);
        } else {
            profPic.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic2().isEmpty()) {
            Picasso.with(getActivity()).load(user.getPic2()).into(pic2);
        } else {
            pic2.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic3().isEmpty()) {
            Picasso.with(getActivity()).load(user.getPic3()).into(pic3);
        } else {
            pic3.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic4().isEmpty()) {
            Picasso.with(getActivity()).load(user.getPic4()).into(pic4);
        } else {
            pic4.setVisibility(View.INVISIBLE);
        }
        if (!user.getPic5().isEmpty()) {
            Picasso.with(getActivity()).load(user.getPic5()).into(pic5);
        } else {
            pic5.setVisibility(View.INVISIBLE);
        }
    }
}