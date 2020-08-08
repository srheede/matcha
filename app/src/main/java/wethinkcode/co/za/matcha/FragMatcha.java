package wethinkcode.co.za.matcha;

import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.GsonBuildConfig;
import com.squareup.picasso.Picasso;

import org.conscrypt.Conscrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import static com.facebook.FacebookSdk.getApplicationContext;
import static wethinkcode.co.za.matcha.GeoHash.decodeHash;

public class FragMatcha extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    private String placeName;
    private User matcha;
    private View rootView;
    private Boolean matchFound = false;
    private int radius = 10;
    private int maxRadius = 0;
    private User user;
    private ArrayList<String> matched = new ArrayList<String>();
    private String matchKey;
    private ArrayList<String> popular = new ArrayList<String>();
    private String firebaseID;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.8.101:3000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_matcha, container, false);

        mAuth = FirebaseAuth.getInstance();

        updateUI();
        getData();
        return rootView;
    }

    private void postData(String data) {
        Post post = new Post(firebaseID, data);
        Call<Post> call = jsonPlaceHolderApi.createPost(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void getData() {

        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, retrofit2.Response<List<Post>> response) {
                if (!response.isSuccessful()){
                    System.out.println(response.code());
                    return;
                }
                System.out.println(response.code());
                List<Post> posts = response.body();
                for (Post post : posts){
                    System.out.println(post.getFirebaseID());
                    System.out.println(post.getData());
                }
                System.out.println("here");
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void updateUI() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseID = mAuth.getCurrentUser().getUid();
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        user = Account.fetchData(dataSnapshot);
                        showForm();
                        String data = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                        postData(data);
                        int radius = Integer.parseInt(user.getFilterDistance());
                        maxRadius = radius * radius;
                        selectNextMatch();
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

    private void nextMatch(String firebaseID) {
        if (firebaseID != null) {
            Query query = users.child(firebaseID);

            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("email").getValue() != null) {
                        matcha = Account.fetchData(dataSnapshot);
                        fillFormMatcha();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            hideForm();
        }
    }

    private void hideForm() {
        rootView.findViewById(R.id.scrollView2).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.imageViewNo).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.textViewNo).setVisibility(View.VISIBLE);
    }

    private void showForm() {
        rootView.findViewById(R.id.scrollView2).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.imageViewNo).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.textViewNo).setVisibility(View.INVISIBLE);
    }

    private void fillFormMatcha() {

        Button buttonYes = rootView.findViewById(R.id.buttonYes);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int popularity = Integer.parseInt(matcha.getPopularity());
                    matcha.setPopularity(Integer.toString(popularity + 1));
                    users.child(matchKey).setValue(matcha);
                    if (user.getSortBy().equals("Location")) {
                        matchFound = false;
                        selectNextMatch();
                    } else {
                        popular.remove(0);
                        if (popular.isEmpty()){
                            nextMatch(null);
                        } else {
                            nextMatch(popular.get(0));
                        }
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        });

        Button buttonNo = rootView.findViewById(R.id.buttonNo);

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getSortBy().equals("Location")) {
                    matchFound = false;
                    selectNextMatch();
                } else {
                    popular.remove(0);
                    if (popular.isEmpty()){
                        nextMatch(null);
                    } else {
                        nextMatch(popular.get(0));
                    }
                }
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
        TextView rating = rootView.findViewById(R.id.rating2);


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
        rating.setText(setRating());

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

    private void selectNextMatch() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String firebaseID = mAuth.getCurrentUser().getUid();
            if (user.getSortBy().equals("Popularity")) {
                popularMatch(firebaseID);
            } else {
                Query query = users.child(firebaseID);

                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("email").getValue() != null) {

                            Object geoHash = dataSnapshot.child("geoHash").getValue();

                            assert geoHash != null;
                            final LatLong latLong = decodeHash(geoHash.toString());

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
                            GeoFire geoFire = new GeoFire(ref);

                            nearestMatch(geoFire, latLong, firebaseID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "User not found in updateUI.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private String setRating() {

        String rating = matcha.getPopularity();
        String gender = matcha.getGender();
        if (gender.equals("Male")){
            gender = "BOY";
        } else {
            gender = "GIRL";
        }

        int popularity = Integer.parseInt(rating);

        if (popularity <= 2){
            rating = "NOBODY (" + rating + ")";
        } else if (popularity <= 4) {
            rating = "SOMEBODY (" + rating + ")";
        } else if (popularity <= 6) {
            rating = "EEEEY! (" + rating + ")";
        } else if (popularity <= 8) {
            rating = "LOOK AT YOU! (" + rating + ")";
        } else if (popularity <= 100) {
            rating = " YOU GO " + gender + "! (" + rating + ")";
        } else {
            rating = "THE BEST! (" + rating + ")";
        }

        return rating;
    }

    private void popularMatch(String firebaseID) {
        users.orderByChild("popularity").limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String age = snapshot.child("age").getValue().toString();
                    String location = snapshot.child("filterLocation").getValue().toString();
                    int Age = Integer.parseInt(age);
                    int filterMinAge = Integer.parseInt(user.getFilterAgeMin());
                    int filterMaxAge = Integer.parseInt(user.getFilterAgeMax());
                    if (filterMinAge <= Age && Age <= filterMaxAge) {
                        if (!snapshot.getKey().equals(firebaseID)) {
                            if (user.getFilterLocation().isEmpty()) {
                                popular.add(snapshot.getKey());
                            } else if (user.getFilterLocation().equals(location)) {
                                popular.add(snapshot.getKey());
                            }
                        }
                    }
                }
                if (popular.isEmpty()){
                    nextMatch(null);
                } else {
                    nextMatch(popular.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nearestMatch(GeoFire geoFire, LatLong latLong, String firebaseID) {
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLong.getLat(), latLong.getLon()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!matchFound & !key.equals(firebaseID) & !matched.contains(key)) {
                    filterMatch(key);
                } else if (!matchFound & radius < maxRadius) {
                    radius = radius + 10;
                    nearestMatch(geoFire, latLong, firebaseID);
                } else if (!matchFound){
                    matchFound = true;
                    nextMatch(null);
                }
            }

            private void filterMatch(String key) {
                if (key != null) {
                    Query query = users.child(key);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("email").getValue() != null) {
                                matcha = Account.fetchData(dataSnapshot);

                                String age = matcha.getAge();
                                String location = matcha.getLocation();
                                int Age = Integer.parseInt(age);
                                int filterMinAge = Integer.parseInt(user.getFilterAgeMin());
                                int filterMaxAge = Integer.parseInt(user.getFilterAgeMax());
                                if (Age >= filterMinAge && Age <= filterMaxAge) {
                                    if (!matchFound & user.getFilterLocation().isEmpty()) {
                                        matchKey = key;
                                        matchFound = true;
                                        matched.add(matchKey);
                                        nextMatch(matchKey);
                                    } else if (!matchFound & user.getFilterLocation().equals(location)) {
                                        matchKey = key;
                                        matchFound = true;
                                        matched.add(matchKey);
                                        nextMatch(matchKey);
                                    } else if (!matchFound & radius < maxRadius) {
                                        radius = radius + 10;
                                        nearestMatch(geoFire, latLong, firebaseID);
                                    } else if (!matchFound){
                                        matchFound = true;
                                        nextMatch(null);
                                    }
                                } else if (!matchFound & radius < maxRadius) {
                                    radius = radius + 10;
                                    nearestMatch(geoFire, latLong, firebaseID);
                                } else if (!matchFound) {
                                    matchFound = true;
                                    nextMatch(null);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}