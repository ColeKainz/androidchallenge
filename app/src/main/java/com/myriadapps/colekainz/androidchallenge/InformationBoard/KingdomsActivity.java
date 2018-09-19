package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.MainActivity;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Kingdom;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitCallback;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitSingleton;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.ServerResponse;
import com.myriadapps.colekainz.androidchallenge.Signin.Account;
import com.myriadapps.colekainz.androidchallenge.Signin.SigninActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This activity displays all the of kingdoms to the user in a recycler view.
 */
public class KingdomsActivity extends AppCompatActivity {

    @BindString(R.string.kingdom_id) String kingdomIDString;

    //Signin info.
    @BindString(R.string.saved_accounts) String savedAccountsString;
    @BindString(R.string.default_account) String defaultAccountString;
    @BindString(R.string.signin_account) String signinAccount;

    //Error messages.
    @BindString(R.string.server_resp_failed) String serverRespFailedMSG;
    @BindString(R.string.delete_user_failed) String deleteUserFailedMSG;

    //UI Elements
    @BindView(R.id.kingdomsToolbar) Toolbar toolbar;
    @BindView(R.id.kingdomsRecyclerView) RecyclerView kingdomsView;

    private SharedPreferences prefs;
    private ServerResponse response = RetrofitSingleton.getInstance().create(ServerResponse.class);

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kingdoms);
        ButterKnife.bind(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Load the account.
        //If an account wasn't specified, load the default account.
        //If that fails, go to main activity.
        String accountJSON = "";
        if (getIntent().hasExtra(signinAccount)) {
            Bundle bundle = getIntent().getExtras();
            accountJSON = bundle.getString(signinAccount, null);
        } else if(prefs.contains(defaultAccountString)){
            accountJSON = prefs.getString(defaultAccountString, null);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }

        account = new Gson().fromJson(accountJSON, Account.class);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(account.getEmail());

        Call<List<Kingdom>> call = response.getKingdoms();
        call.enqueue(new RetrofitCallback<List<Kingdom>>() {

            Context context = getApplicationContext();

            @Override
            public void handleFailure() {
                //An issue connecting to the server occurred.
                Toast errMsg = Toast.makeText(context, serverRespFailedMSG,
                        Toast.LENGTH_LONG);
                errMsg.show();

                String accountJSON = new Gson().toJson(account);

                Intent intent = new Intent(context, NotRespondingActivity.class);
                intent.putExtra(signinAccount, accountJSON);
                startActivity(intent);

                finish();
            }

            @Override
            public void handleSuccess(Response<List<Kingdom>> response) {
                kingdomsView.setLayoutManager( new LinearLayoutManager(context));
                kingdomsView.setAdapter(new KingdomListAdapter(response.body()));
            }
        });
    }

    //The user may have made a new account. If they press the back button, their account
    //has been persisted, so navigate to the signin activity.
    @Override
    public void onBackPressed() {
        //If back pressed, log out.
        onLogoutClick();
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

    public class KingdomListAdapter extends
            RecyclerView.Adapter<KingdomListAdapter.KingdomViewHolder> {

        public class KingdomViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.kingdomTitle) TextView titleTextView;
            @BindView(R.id.kindgomImage) ImageView imageImageView;

            public KingdomViewHolder(View itemView){
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public TextView getTitleTextView() {
                return titleTextView;
            }

            public ImageView getImageImageView() {
                return  imageImageView;
            }
        }

        private List<Kingdom> kingdoms;

        public KingdomListAdapter(List<Kingdom> kingdoms){
            this.kingdoms = kingdoms;
        }

        @Override
        public KingdomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.kingdom_list_item, viewGroup, false);

            KingdomViewHolder viewHolder = new KingdomViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final KingdomViewHolder viewHolder, final int index) {
            final Kingdom kingdom = kingdoms.get(index);

            final String title = kingdom.getName();
            final String imageURL = kingdom.getImageURL();

            TextView titleTextView = viewHolder.getTitleTextView();
            ImageView imageImageView = viewHolder.getImageImageView();

            RequestOptions options = new RequestOptions();
            options.circleCrop();

            titleTextView.setText(title);
            Glide.with(viewHolder.itemView)
                    .load(imageURL)
                    .apply(options)
                    .into(imageImageView);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kingdomInfo = new Intent(viewHolder.itemView.getContext(),
                            KingdomInfoActivity.class);
                    kingdomInfo.putExtra(kingdomIDString, kingdom.getId());
                    startActivity(kingdomInfo);
                }
            });
        }

        @Override
        public int getItemCount() {
            return kingdoms.size();
        }
    }
}
