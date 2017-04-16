package com.icebreakers.nexxus.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amodi on 4/8/17.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profiles;
    private Map<String, Similarities> similaritiesMap;

    public ProfileAdapter(List<Profile> profileList, Map<String, Similarities> similaritiesMap) {
        this.profiles = profileList;
        this.similaritiesMap = similaritiesMap;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        final Profile profile = profiles.get(position);
        final Context context = holder.itemView.getContext();

        if (profile.firstName != null && profile.lastName != null) {
            holder.profileNameText.setText(profile.firstName + " " + profile.lastName);
        } else {
            holder.profileNameText.setText(profile.firstName != null ? profile.firstName : profile.lastName);
        }

        holder.profileHeadlineText.setText(profile.headline);

        Glide.with(context).load(profile.pictureUrl).into(holder.profileImage);

        Similarities similarities = similaritiesMap.get(profile.id);
        if (similarities != null && similarities.numOfSimilarities >= 1) {
            if (similarities.numOfSimilarities > 1) {
                holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_common));
                holder.profileSimilarityText.setText(String.format(context.getResources()
                                                                          .getString(R.string.similarities_in_common),
                                                                   similarities.numOfSimilarities));
            } else {
                if (similarities.similarEducations.size() != 0) {
                    holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_education));
                    holder.profileSimilarityText.setText(similarities.similarEducations.get(0).schoolName);
                } else {
                    holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_work));
                    holder.profileSimilarityText.setText(similarities.similarPositions.get(0).companyName);
                }
            }
        } else {
            holder.similaritySection.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void updateSimilaritiesMap(Map<String, Similarities> similaritiesMap) {
        this.similaritiesMap.putAll(similaritiesMap);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_name_text) TextView profileNameText;
        @BindView(R.id.profile_headline_text) TextView profileHeadlineText;
        @BindView(R.id.profile_image) ImageView profileImage;
        @BindView(R.id.profile_similarity_text) TextView profileSimilarityText;
        @BindView(R.id.similarity_image) ImageView profileSimilarityImage;
        @BindView(R.id.similarity_section) LinearLayout similaritySection;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
