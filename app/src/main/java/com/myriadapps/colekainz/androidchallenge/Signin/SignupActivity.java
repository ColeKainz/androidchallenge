package com.myriadapps.colekainz.androidchallenge.Signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.InformationBoard.KingdomsActivity;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitCallback;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitSingleton;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.ServerResponse;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Signs the user up for our service.
 */
public class SignupActivity extends AppCompatActivity {

    //UI elements.
    @BindView(R.id.nameSignupText) EditText nameSignupText;
    @BindView(R.id.emailSignupText) EditText emailSignupText;
    @BindView(R.id.signupButton) Button signupButton;
    @BindView(R.id.signupProgressBar) ProgressBar signupProgressBar;

    //Error messages.
    @BindString(R.string.name_text_invalid) String nameTextInvalid;
    @BindString(R.string.email_text_invalid) String emailTextInvalid;
    @BindString(R.string.save_user_failed) String saveUserFailedMSG;
    @BindString(R.string.server_resp_failed) String serverRespFailedMSG;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;
    @BindString(R.string.signin_account) String signinAccount;

    private ServerResponse response = RetrofitSingleton.getInstance().create(ServerResponse.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signupButton)
    protected void onSignupClick() {
        String name = nameSignupText.getText().toString();
        String email = emailSignupText.getText().toString();

        boolean validName = validateName(name);
        boolean validEmail = validateEmail(email);

        //If the name and email is valid, send a subscription request to the server.
        //If not, notify the user.
        if(validName && validEmail) {
            toggleLoadingState(true);
            Call<Subscribe> call = response.postSubscription(email);
            call.enqueue(new SubscribeCallback(new Account(name, email)));
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

    //Notifies the user that the app is loading.
    //Disable the UI so the user can't submit multiple requests.
    protected void toggleLoadingState(boolean loading) {
        if(loading) {
            signupProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            signupProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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

    /**
     * Handles the subscribe attempt.
     * If successful, persist the account and set it as the default.
     * If unsuccessful, notify the user.
    */
    protected class SubscribeCallback extends RetrofitCallback<Subscribe> {


        Context context = getApplicationContext();

        private Account account;

        SubscribeCallback(Account account) {
            this.account = account;
        }

        @Override
        public void handleFailure() {
            //An issue connecting to the server occurred.
            Toast errMsg = Toast.makeText(getApplicationContext(), serverRespFailedMSG,
                    Toast.LENGTH_LONG);
            errMsg.show();

            toggleLoadingState(false);
        }

        @Override
        public void handleUnsuccessfulResponse(Response<Subscribe> response) {
            //Display the server response to the user.
            if(response.body().getMessage() != null) {
                Toast errMsg = Toast.makeText(context, response.body().getMessage(),
                        Toast.LENGTH_LONG);
                errMsg.show();
            } else {
                Toast errMsg = Toast.makeText(context, response.message(),
                        Toast.LENGTH_LONG);
                errMsg.show();
            }

            toggleLoadingState(false);
        }

        @Override
        public void handleSuccess(Response<Subscribe> response) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());

            List<Account> accounts;
            Gson gson = new Gson();

            //Check if there are saved accounts.
            //If not, create a new list.
            String accountsJSON = prefs.getString(savedAccountsString, null);
            if(accountsJSON != null) {
                Account[] accountsArray = gson.fromJson(accountsJSON, Account[].class);
                accounts = new ArrayList(Arrays.asList(accountsArray));
            } else {
                accounts = new ArrayList<Account>();
            }

            accounts.add(account);

            String accountJSON = gson.toJson(account);
            accountsJSON = gson.toJson(accounts);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(savedAccountsString, accountsJSON);
            editor.putString(defaultAccountString, accountJSON);

            editor.apply();

            //Load the Kingdoms screen and terminate.
            Intent kingdoms = new Intent(context, KingdomsActivity.class);
            startActivity(kingdoms);

            finish();
        }
    }
}
