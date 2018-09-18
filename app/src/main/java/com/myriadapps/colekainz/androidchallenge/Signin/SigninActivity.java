package com.myriadapps.colekainz.androidchallenge.Signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.InformationBoard.KingdomsActivity;
import com.myriadapps.colekainz.androidchallenge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

/**
 * This activity displays a list of saved accounts to the user,
 * as well as a sign up button.
 */

public class SigninActivity extends AppCompatActivity {

    //Error messages
    @BindString(R.string.load_user_failed) String loadUserFailedMSG;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;
    @BindString(R.string.signin_account) String signinAccount;

    //UI Elements
    @BindView(R.id.accountList) RecyclerView accountList;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Load the saved accounts from the preferences.
        //If fail, notify the user.
        String savedAccountsJSON = prefs.getString(savedAccountsString, null);
        if(savedAccountsJSON != null) {
            Account[] accountArray = new Gson().fromJson(savedAccountsJSON, Account[].class);
            List<Account> accounts = Arrays.asList(accountArray);

            //Check if the list is populated
            if (!accounts.isEmpty()) {
                AccountListAdapter adapter = new AccountListAdapter(accounts);
                accountList.setAdapter(adapter);
                accountList.setLayoutManager(new LinearLayoutManager(this));
            } else {
                loadUserFailed();
            }
        } else {
            loadUserFailed();
        }
    }

    @OnClick(R.id.signupButton)
    protected void onSignupClick(View v) {
        //Notice this activity is not exited after Signup is started.
        //This is so the user can click back and come back to this activity.
        Intent signup = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(signup);
    }

    //If the user list failed to load,
    //take the user to signup.
    protected void loadUserFailed() {
        Toast errMsg = Toast.makeText(this, loadUserFailedMSG,
                Toast.LENGTH_LONG);
        errMsg.show();

        Intent signup = new Intent(this, SignupActivity.class);
        startActivity(signup);

        finish();
    }

    public class AccountListAdapter extends
            RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

        public class AccountViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.nameAccItemText) TextView nameTextView;
            @BindView(R.id.emailAccItemText) TextView emailTextView;

            public AccountViewHolder(View itemView){
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public TextView getNameTextView() {
                return nameTextView;
            }

            public TextView getEmailTextView() {
                return emailTextView;
            }
        }

        private List<Account> accounts;

        public AccountListAdapter(List<Account> accounts){
            this.accounts = accounts;
        }

        @Override
        public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.account_list_item, viewGroup, false);

            AccountViewHolder viewHolder = new AccountViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final AccountViewHolder viewHolder, int index) {
            final Account account = accounts.get(index);;

            TextView nameTextView = viewHolder.getNameTextView();
            TextView emailTextView = viewHolder.getEmailTextView();

            nameTextView.setText(account.getName());
            emailTextView.setText(account.getEmail());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Load the Kingdoms screen and terminate.
                    String accountJSON = new Gson().toJson(account);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(defaultAccountString, accountJSON);

                    editor.apply();

                    Intent kingdoms = new Intent(getApplicationContext(), KingdomsActivity.class);
                    kingdoms.putExtra(signinAccount, accountJSON);
                    startActivity(kingdoms);

                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }
    }
}
