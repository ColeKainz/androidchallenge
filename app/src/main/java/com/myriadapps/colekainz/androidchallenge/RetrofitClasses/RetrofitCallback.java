package com.myriadapps.colekainz.androidchallenge.RetrofitClasses;

import android.content.Context;
import android.widget.Toast;

import com.myriadapps.colekainz.androidchallenge.InformationBoard.KingdomInfoActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Simplifies retrofit callbacks.
 *
 * More often than not, the error message that handled onFailure
 * was the same as the error message the was shown when the response
 * wasn't successful.
 */
public abstract class RetrofitCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            handleSuccess(response);
        } else {
            handleFailure();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        handleFailure();
    }

    public void handleUnsuccessfulResponse(Response<T> response) {
        handleFailure();
    }

    public abstract void handleFailure();
    public abstract void handleSuccess(Response<T> response);
}
