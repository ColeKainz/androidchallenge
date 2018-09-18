package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.myriadapps.colekainz.androidchallenge.R;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Kingdom;
import com.myriadapps.colekainz.androidchallenge.RetrofitClasses.Quest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment that displays the selected kingdom in the Kingdoms activity.
 */
public class KingdomFragment extends InfoFragment {

    private static final String KINGDOMSTRING = "kingdom";

    //UI Elements
    @BindView(R.id.kingdomInfoImage) ImageView kingdomImage;
    @BindView(R.id.kingdomInfoRecyclerView) RecyclerView questListView;
    @BindView(R.id.kingdomInfoClimate) TextView climateTextView;
    @BindView(R.id.kingdomInfoPopulation) TextView populationTextView;

    private Unbinder unbinder;
    private Kingdom kingdom;
    private QuestSelector questSelector;
    private String title;

    public static KingdomFragment newInstance(Kingdom kingdom) {
        KingdomFragment kingdomFragment = new KingdomFragment();

        //Encode the receved kingdom object for later.
        Bundle args = new Bundle();
        args.putString(KINGDOMSTRING, kingdom.toJson());
        kingdomFragment.setArguments(args);

        return kingdomFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Decode the received kingdom object.
        String kingdomJSON = getArguments().getString(KINGDOMSTRING);
        kingdom = new Gson().fromJson(kingdomJSON, Kingdom.class);

        title = kingdom.getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kingdom, container, false);
        unbinder = ButterKnife.bind(this, view);

        climateTextView.setText(kingdom.getClimate());
        populationTextView.setText(Integer.toString(kingdom.getPopulation()));

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        Glide.with(view)
                .load(kingdom.getImageURL())
                .apply(options)
                .into(kingdomImage);

        questListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        questListView.setAdapter(new KingdomInfoListAdapter(kingdom.getQuests()));

        return view;
    }

    //Need to attach Quest Selecter so the KingdomInfo activity
    //can display the quest name when selected.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            questSelector = (QuestSelector) getActivity();
        } catch(ClassCastException e) {
            Toast errMSG = Toast.makeText(context, "Could not instantiate quest selector",
                Toast.LENGTH_LONG);
            errMSG.show();

            getActivity().finish();
        }
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

    public class KingdomInfoListAdapter extends
            RecyclerView.Adapter<KingdomInfoListAdapter.KingdomInfoViewHolder> {

        public class KingdomInfoViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.kingdomInfoQuestTitle) TextView questTitleTextView;
            @BindView(R.id.kingdomInfoQuestImage) ImageView questImageImageView;

            public KingdomInfoViewHolder(View itemView){
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public ImageView getQuestImageImageView() {
                return questImageImageView;
            }

            public TextView getQuestTitleTextView() {
                return questTitleTextView;
            }

        }

        private List<Quest> quests;

        public KingdomInfoListAdapter(List<Quest> quests){
            this.quests = quests;
        }

        @Override
        public KingdomInfoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.kingdom_info_list_item, viewGroup, false);

            KingdomInfoViewHolder viewHolder = new KingdomInfoViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final KingdomInfoViewHolder viewHolder, final int index) {
            Quest quest = quests.get(index);

            final String title = quest.getName();
            final String imageURL = quest.getImageURL();

            TextView titleTextView = viewHolder.getQuestTitleTextView();
            ImageView questImageView = viewHolder.getQuestImageImageView();

            titleTextView.setText(title);

            RequestOptions options = new RequestOptions();
            options.circleCrop();

            Glide.with(viewHolder.itemView)
                    .load(imageURL)
                    .apply(options)
                    .into(questImageView);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questSelector.selectQuestPage(index);
                }
            });
        }

        @Override
        public int getItemCount() {
            return quests.size();
        }
    }

    interface QuestSelector {
        void selectQuestPage(int index);
    }
}
