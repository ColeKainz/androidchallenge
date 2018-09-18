package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Giver;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Quest;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitCallback;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.RetrofitSingleton;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.ServerResponse;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Fragment that displays the selected quest in the KingdomInfo activity.
 */
public class QuestFragment extends InfoFragment {

    private static final String QUESTSTRING = "quest";

    //UI Elements
    @BindView(R.id.questInfoImage) ImageView questImageView;
    @BindView(R.id.questInfoQuestDescription) TextView questDescTextView;
    @BindView(R.id.questInfoGiverDescription) TextView questGiverDescTextView;
    @BindView(R.id.questInfoGiverImage) ImageView questGiverImageView;
    @BindView(R.id.questInfoGiverName) TextView questGiverNameTextView;

    //Error messages.
    @BindString(R.string.load_kingdom_info_giver_failed_msg) String loadGiverFailedMsg;

    private ServerResponse response = RetrofitSingleton.getInstance().create(ServerResponse.class);

    private Unbinder unbinder;
    private Quest quest;
    private String title;

    public static QuestFragment newInstance(Quest quest) {
        QuestFragment questFragment = new QuestFragment();

        //Encode the receved kingdom object for later.
        Bundle args = new Bundle();
        args.putString(QUESTSTRING, quest.toJson());
        questFragment.setArguments(args);

        return questFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Decode the received kingdom object.
        String questJSON = getArguments().getString(QUESTSTRING);
        quest = new Gson().fromJson(questJSON, Quest.class);

        title = quest.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quest, container, false);
        unbinder = ButterKnife.bind(this, view);

        //Setup quest info.
        questDescTextView.setText(quest.getDescription());

        RequestOptions optionsQuest = new RequestOptions();
        optionsQuest.centerCrop();

        Glide.with(view)
                .load(quest.getImageURL())
                .apply(optionsQuest)
                .into(questImageView);

        questGiverNameTextView.setText(quest.getGiver().getName());

        //Setup giver info.
        RequestOptions optionsGiver = new RequestOptions();
        optionsGiver.circleCrop();

        Glide.with(view)
                .load(quest.getGiver().getImageURL())
                .apply(optionsGiver)
                .into(questGiverImageView);

        Call<Giver> call = response.getCharacter(quest.getGiver().getId());
        call.enqueue(new RetrofitCallback<Giver>() {
            @Override
            public void handleFailure() {
                questGiverDescTextView.setText(loadGiverFailedMsg);
            }

            @Override
            public void handleSuccess(Response<Giver> response) {
                questGiverDescTextView.setText(response.body().getDescription());
            }
        });

        return view;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
