package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Handles the response from the server.
 */

public interface ServerResponse {

    @POST("/api/v1/subscribe")
    @FormUrlEncoded
    Call<Subscribe> postSubscription(@Field("email") String email);

    @GET("/api/v1/kingdoms")
    Call<List<Kingdom>> getKingdoms();

    @GET("/api/v1/kingdoms/{id}")
    Call<Kingdom> getKingdom(@Path("id") int id);

    @GET("/api/v1/characters/{id}")
    Call<Giver> getCharacter(@Path("id") int id);
}
