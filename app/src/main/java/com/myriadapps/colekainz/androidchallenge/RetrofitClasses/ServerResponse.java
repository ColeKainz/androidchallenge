package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServerResponse {

    @POST("/api/v1/subscribe")
    @FormUrlEncoded
    Call<Subscribe> postSubscription(@Field("email") String email);


}
