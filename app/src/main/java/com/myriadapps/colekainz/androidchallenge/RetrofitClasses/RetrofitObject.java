package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import com.google.gson.Gson;

/**
 * Simplifies converting the retrofit objects to JSON.
 */

public abstract class RetrofitObject {

    public String toJson() {
        return new Gson().toJson(this);
    }
}
