package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import retrofit2.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subscribe {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("message")
    @Expose
    private String message;

    public Subscribe(String email) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
