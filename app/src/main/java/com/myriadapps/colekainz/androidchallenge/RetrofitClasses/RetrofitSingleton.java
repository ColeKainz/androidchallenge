package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * There should only be one retrofit object per url.
 * Otherwise were just taking up memory.
 *
 * This class was sampled from
 * https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
 * Courtesy of Parkash Pun
 */

public class RetrofitSingleton {

    private static Retrofit retrofit;
    private static final String URL = "https://challenge2015.myriadapps.com/";

    public static Retrofit getInstance() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return retrofit;
    }
}
