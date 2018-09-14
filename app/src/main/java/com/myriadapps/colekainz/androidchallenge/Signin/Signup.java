package com.myriadapps.colekainz.androidchallenge.Signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myriadapps.colekainz.androidchallenge.InformationBoard.Kingdoms;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitSingleton;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.ServerResponse;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    //UI elements.
    @BindView(R.id.nameSignupText) EditText nameSignupText;
    @BindView(R.id.emailSignupText) EditText emailSignupText;
    @BindView(R.id.signupButton) Button signupButton;

    //Error messages.
    @BindString(R.string.name_text_invalid) String nameTextInvalid;
    @BindString(R.string.email_text_invalid) String emailTextInvalid;
    @BindString(R.string.save_user_failed) String saveUserFailedMSG;
    @BindString(R.string.server_resp_failed) String serverRespFailedMSG;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;

    ServerResponse response = RetrofitSingleton.getInstance().create(ServerResponse.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameSignupText.getText().toString();
                String email = emailSignupText.getText().toString();

                boolean validName = validateName(name);
                boolean validEmail = validateEmail(email);

                //If the name and email is valid, send a subscription request to the server.
                //If not, notify the user.
                if(validName && validEmail) {
                    Call<Subscribe> call = response.postSubscription(email);
                    call.enqueue(new SubscribeCallback(name, email));
                } else {
                    //Determine whether the name or email field is invalid and alert the user.
                    if(!validName) {
                        nameSignupText.setError(nameTextInvalid);
                    }

                    if(!validEmail) {
                        emailSignupText.setError(emailTextInvalid);
                    }
                }
            }
        });
    }

    protected boolean validateName(String name) {
        //Only allow letters and spaces in name field.
        Pattern pattern = Pattern.compile("^[A-Za-z\\s]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    protected boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    protected class SubscribeCallback implements  Callback<Subscribe> {
        /*
        Handles the server response the subscribe attempt.
        If successful, persist the account and set it as the default.
        If unsuccessful, notify the user.
        */

        Context context = getApplicationContext();

        private String name;
        private String email;

        SubscribeCallback(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public void onResponse(Call<Subscribe> call, Response<Subscribe> response) {

            //Check if the subscription attempt was successful.
            //If not, notify the user.
            if(response.isSuccessful()) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String savedAccounts = prefs.getString(savedAccountsString, null);
                SharedPreferences.Editor editor = prefs.edit();

                //Try to load the array of accounts and add on a new one.
                //Then set the account as the default account.
                try {
                    //Check if there is an array of saved accounts.
                    //If not, create one.
                    JSONArray accountsArray;
                    if(savedAccounts != null && !savedAccounts.isEmpty()) {
                        accountsArray = new JSONArray(savedAccounts);
                    } else {
                        accountsArray = new JSONArray();
                    }

                    JSONObject account = new JSONObject();
                    account.put("name", name);
                    account.put("email", email);

                    accountsArray.put(account);

                    editor.putString(savedAccountsString, accountsArray.toString());
                    editor.putString(defaultAccountString, account.toString());

                    editor.apply();

                } catch (JSONException e) {
                    //Failed to save the user.
                    //The attempt to subscribe was successful, so load kingdoms anyway.
                    Toast errMsg = Toast.makeText(context, saveUserFailedMSG,
                            Toast.LENGTH_LONG);
                    errMsg.show();
                }

                //Load the Kingdoms screen and terminate.
                Intent kingdoms = new Intent(context, Kingdoms.class);
                kingdoms.putExtra(name, name);
                kingdoms.putExtra(email, email);
                startActivity(kingdoms);

                finish();
            } else {
                //Display the server response method to the user.
                Toast errMsg = Toast.makeText(context, response.body().getMessage(),
                        Toast.LENGTH_LONG);
                errMsg.show();
            }
        }

        @Override
        public void onFailure(Call<Subscribe> call, Throwable t) {
            //An issue connecting to the server occurred.
            Toast errMsg = Toast.makeText(context, serverRespFailedMSG,
                    Toast.LENGTH_LONG);
            errMsg.show();
        }
    }
}
