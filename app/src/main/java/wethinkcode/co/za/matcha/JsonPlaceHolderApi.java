package wethinkcode.co.za.matcha;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {
    @GET("get")
    Call<List<Post>> getPosts();

    @POST("post")
    Call<Post>createPost(@Body Post post);

    @FormUrlEncoded
    @POST("post")
    Call<Post> createPost (
            @Field("firebaseID") String firebaseID,
            @Field("data") String data
    );
}
