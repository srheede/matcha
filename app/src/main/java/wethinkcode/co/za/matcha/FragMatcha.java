package wethinkcode.co.za.matcha;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class FragMatcha extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private String placeName;
    private User matcha;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_matcha, container, false);

        mAuth = FirebaseAuth.getInstance();

        nextMatch();

        return rootView;
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
                        matcha = Account.fetchData(dataSnapshot);
                        fillFormMatcha(matcha);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getActivity(), "No new matches in your area.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fillFormMatcha(User matcha) {

        Button buttonYes = rootView.findViewById(R.id.buttonYes);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMatch();
            }
        });

        Button buttonNo = rootView.findViewById(R.id.buttonNo);

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMatch();
            }
        });

        TextView date = rootView.findViewById(R.id.textViewDate2);
        TextView firstName = rootView.findViewById(R.id.textViewFirstName2);
        TextView bio = rootView.findViewById(R.id.textViewBio2);
        TextView interests = rootView.findViewById(R.id.textViewInterests2);
        TextView lastName = rootView.findViewById(R.id.textViewLastName2);
        TextView username = rootView.findViewById(R.id.textViewUsername2);
        TextView email = rootView.findViewById(R.id.textViewEmail2);
        TextView gender = rootView.findViewById(R.id.textViewGender2);
        TextView location = rootView.findViewById(R.id.textViewLocation2);
        TextView interestedIn = rootView.findViewById(R.id.textViewInterestedIn2);
        ImageView profPic = rootView.findViewById(R.id.ImageViewProf2);
        ImageView pic2 = rootView.findViewById(R.id.imageView22);
        ImageView pic3 = rootView.findViewById(R.id.imageView32);
        ImageView pic4 = rootView.findViewById(R.id.imageView42);
        ImageView pic5 = rootView.findViewById(R.id.imageView52);

        String placeId = matcha.getLocation();
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

        username.setText(matcha.getUsername());
        email.setText(matcha.getEmail());
        date.setText(matcha.getBirthDate());
        firstName.setText(matcha.getFirstName());
        lastName.setText(matcha.getLastName());
        bio.setText(matcha.getBio());
        interests.setText(matcha.getInterests());
        gender.setText(matcha.getGender());
        interestedIn.setText(matcha.getSexPref());
        location.setText(matcha.getLocation());

        if (!matcha.getProfPic().isEmpty()) {
            Picasso.with(getActivity()).load(matcha.getProfPic()).into(profPic);
        } else {
            profPic.setVisibility(View.INVISIBLE);
        }
        if (!matcha.getPic2().isEmpty()) {
            Picasso.with(getActivity()).load(matcha.getPic2()).into(pic2);
        } else {
            pic2.setVisibility(View.INVISIBLE);
        }
        if (!matcha.getPic3().isEmpty()) {
            Picasso.with(getActivity()).load(matcha.getPic3()).into(pic3);
        } else {
            pic3.setVisibility(View.INVISIBLE);
        }
        if (!matcha.getPic4().isEmpty()) {
            Picasso.with(getActivity()).load(matcha.getPic4()).into(pic4);
        } else {
            pic4.setVisibility(View.INVISIBLE);
        }
        if (!matcha.getPic5().isEmpty()) {
            Picasso.with(getActivity()).load(matcha.getPic5()).into(pic5);
        } else {
            pic5.setVisibility(View.INVISIBLE);
        }
    }

    private String selectNextMatch(String uid) {
        return uid;
    }
}
