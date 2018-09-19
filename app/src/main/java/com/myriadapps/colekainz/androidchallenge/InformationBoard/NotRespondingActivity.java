package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.MainActivity;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.Signin.Account;
import com.myriadapps.colekainz.androidchallenge.Signin.SigninActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Handles the kingdom activity not reaching the server.
 * The kingdom activity passes its account into the activity.
 *
 */

public class NotRespondingActivity extends AppCompatActivity {

    //Signin info.
    @BindString(R.string.signin_account) String signinAccount;
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;

    //UI Elements
    @BindView(R.id.notRespondingToolbar) Toolbar toolbar;
    @BindView(R.id.notRespondingRefresh) ConstraintLayout refresh;

    //Error messages.
    @BindString(R.string.delete_user_failed) String deleteUserFailedMSG;

    private String accountJSON;
    private Account account;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_responding);
        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        accountJSON = bundle.getString(signinAccount, null);
        account = new Gson().fromJson(accountJSON, Account.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(account.getEmail());
    }

    @OnClick(R.id.notRespondingRefresh)
    public void onRefresh() {
        Intent kingdoms = new Intent(getApplicationContext(), KingdomsActivity.class);
        kingdoms.putExtra(signinAccount, accountJSON);
        startActivity(kingdoms);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_kingdoms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                onLogoutClick();
                break;
            case R.id.action_delete:
                onDeleteClick();
                break;
            default:
                break;
        }

        return true;
    }

    public void onLogoutClick() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(defaultAccountString);
        editor.apply();

        Intent signin = new Intent(this, SigninActivity.class);
        startActivity(signin);

        finish();
    }

    public void onDeleteClick() {
        //Get the saved accounts, find the one that matches the current account
        //and remove it. Then persist the accounts list.
        String accountsJSON = prefs.getString(savedAccountsString, null);
        if(accountsJSON != null) {
            Gson gson = new Gson();
            Account[] accountArray = gson.fromJson(accountsJSON, Account[].class);

            //Arrays.asList returns a fixed size List.
            //Needs to be converted to a normal list.
            List<Account> accounts = new ArrayList<Account>(Arrays.asList(accountArray));

            //Accounts.remove was not working.
            //Quick work around.
            for(int i = 0; i < accounts.size(); i++) {
                if(accounts.get(i).equals(account)){
                    accounts.remove(i);
                }
            }

            accountsJSON = gson.toJson(accounts);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(savedAccountsString, accountsJSON);
            editor.remove(defaultAccountString);

            editor.apply();

            //Go back to MainActivity, rather than Signin.
            //All of the accounts might have been removed.
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);

            finish();
        } else {
            //An issue connecting to the server occurred.
            Toast errMsg = Toast.makeText(getApplicationContext(), deleteUserFailedMSG,
                    Toast.LENGTH_LONG);
            errMsg.show();
        }
    }

    @Override
    public void onBackPressed() {
        //If back pressed, log out.
        onLogoutClick();
    }
}
