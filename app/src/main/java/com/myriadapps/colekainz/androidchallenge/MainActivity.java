package com.myriadapps.colekainz.androidchallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.InformationBoard.KingdomsActivity;
import com.myriadapps.colekainz.androidchallenge.Signin.Account;
import com.myriadapps.colekainz.androidchallenge.Signin.SigninActivity;
import com.myriadapps.colekainz.androidchallenge.Signin.SignupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Checks whether there are saved accounts and a default user.
 */

public class MainActivity extends AppCompatActivity {

    //Error messages.
    @BindString(R.string.load_user_failed) String loadUserFailedMSG;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;
    @BindString(R.string.signin_account) String signinName;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String savedAccountsJSON = prefs.getString(savedAccountsString, "");
        String defaultAccountJSON = prefs.getString(defaultAccountString, "");

        Gson gson = new Gson();
        Account[] accounts = gson.fromJson(savedAccountsJSON, Account[].class);
        Account defaultAccount = gson.fromJson(defaultAccountJSON, Account.class);

        //Check if there are saved accounts.
        //If not, default to signup.
        if(accounts.length > 0) {
            //Check if there is a default account.
            //If not go to signin.
            if(defaultAccount != null) {
                //Load default account.
                Intent kingdoms = new Intent(this, KingdomsActivity.class);
                startActivity(kingdoms);
            } else {
                Intent signin = new Intent(this, SigninActivity.class);
                startActivity(signin);
            }
        } else {
            Intent signup = new Intent(this, SignupActivity.class);
            startActivity(signup);
        }

        //Kill main activity.
        finish();
    }
}
