package com.myriadapps.colekainz.androidchallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.myriadapps.colekainz.androidchallenge.InformationBoard.Kingdoms;
import com.myriadapps.colekainz.androidchallenge.Signin.Signin;
import com.myriadapps.colekainz.androidchallenge.Signin.Signup;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;

public class MainActivity extends AppCompatActivity {

    @BindString(R.string.load_user_failed) String loadUserFailedMSG;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;
    @BindString(R.string.sigin_name) String signinName;
    @BindString(R.string.sigin_email) String signinEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String savedAccounts = prefs.getString(savedAccountsString, null);
        String defaultAccount = prefs.getString(defaultAccountString, null);

        //Check if there are saved accounts.
        //If not, default to signup.
        if(savedAccounts != null && !savedAccounts.isEmpty()) {
            if(defaultAccount != null && !defaultAccount.isEmpty()) {
                //Try to load default user.
                try {
                    JSONObject defaultSignin = new JSONObject(defaultAccount);

                    String name = defaultSignin.getString("name");
                    String email = defaultSignin.getString("email");

                    Intent kingdoms = new Intent(this, Kingdoms.class);
                    kingdoms.putExtra(signinName, name);
                    kingdoms.putExtra(signinEmail, email);
                    startActivity(kingdoms);
                } catch(JSONException e) {
                    Toast errMsg = Toast.makeText(this, loadUserFailedMSG,
                            Toast.LENGTH_LONG);
                    errMsg.show();

                    //Cannot load user, so default to sign in screen.
                    Intent signin = new Intent(this, Signin.class);
                    startActivity(signin);
                }
            } else {
                //No default account, load signin.
                Intent signin = new Intent(this, Signin.class);
                startActivity(signin);
            }
        } else {
            Intent signup = new Intent(this, Signup.class);
            startActivity(signup);
        }
    }
}
