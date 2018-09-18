package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Kingdom;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Quest;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitCallback;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitSingleton;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.ServerResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * This activity contains the information for the quests and kingdom selected from
 * the kingdoms activity.
 */
public class KingdomInfoActivity extends AppCompatActivity implements KingdomFragment.QuestSelector{

    @BindString(R.string.kingdom_id) String kingdomIDString;

    //Error messages.
    @BindString(R.string.server_resp_failed) String serverRespFailedMSG;

    //UI Elements
    @BindView(R.id.kingdomInfoPager) ViewPager kingdomInfoPager;
    @BindView(R.id.kingdomInfoToolbar) Toolbar kingdomInfoToolbar;

    private ServerResponse response = RetrofitSingleton.getInstance().create(ServerResponse.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kingdom_info);
        ButterKnife.bind(this);

        //Try to load selected kingdom.
        //If failed, exit activity.
        int id = 0;
        if (getIntent().hasExtra(kingdomIDString)) {
            Bundle bundle = getIntent().getExtras();
            id = bundle.getInt(kingdomIDString, -1);
        } else {
            finish();
        }

        //Setup the toolbars back button.
        setSupportActionBar(kingdomInfoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Call<Kingdom> call = response.getKingdom(id);
        call.enqueue(new KingdomCallback());
    }

    @Override
    public void selectQuestPage(int index) {
        //Since the kingdom page is at index 0,
        //the quest pages start at index 1.
        kingdomInfoPager.setCurrentItem(index+1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    protected class KingdomCallback extends RetrofitCallback<Kingdom> {

        Context context = getApplicationContext();

        @Override
        public void handleFailure() {
            //An issue connecting to the server occurred.
            Toast errMsg = Toast.makeText(context, serverRespFailedMSG,
                    Toast.LENGTH_LONG);
            errMsg.show();

            finish();
        }

        @Override
        public void handleSuccess(Response<Kingdom> response) {
            final KingdomInfoPagerAdapter adapter = new KingdomInfoPagerAdapter(getSupportFragmentManager(),
                    response.body());

            kingdomInfoPager.setAdapter(adapter);
            kingdomInfoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                    getSupportActionBar().setTitle(adapter.getPageTitle(i));
                }

                @Override public void onPageSelected(int i) {}
                @Override public void onPageScrollStateChanged(int i) {}
            });
        }
    }

    public static class KingdomInfoPagerAdapter extends FragmentPagerAdapter {

        private KingdomFragment kingdomFragment;
        private List<QuestFragment> questFragments;

        public KingdomInfoPagerAdapter(FragmentManager fragmentManager, Kingdom kingdom) {
            super(fragmentManager);

            /*
             * Usually these fragments are loaded on getItem().
             * That is useful for when the fragment data changes periodically,
             * but it uses more processing power. The data in this case does not
             * change and there isn't much, so we'll just load the fragments here.
             */
            kingdomFragment = KingdomFragment.newInstance(kingdom);

            questFragments = new ArrayList<QuestFragment>();
            for (Quest quest: kingdom.getQuests()) {
                QuestFragment questFragment = QuestFragment.newInstance(quest);
                questFragments.add(questFragment);
            }
        }

        @Override
        public InfoFragment getItem(int i) {
            if(i == 0) {
                return kingdomFragment;
            } else {
                //The size of the questFragments list is one less than
                //the amount of pages.
                return questFragments.get(i-1);
            }
        }

        @Override
        public int getCount() {
            //Include kingdom in page count.
            return questFragments.size() + 1;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int index) {
            InfoFragment frag;

            if(index == 0) {
                frag = kingdomFragment;
            } else {
                //The size of the questFragments list is one less than
                //the amount of pages.
                frag = questFragments.get(index-1);
            }

            return frag.getTitle();
        }
    }
}
