package com.icebreakers.nexxus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;
import com.icebreakers.nexxus.utils.AnimationUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by amodi on 4/8/17.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private int lastAnimatedPosition = -1;
    private List<Profile> profiles;
    private Map<String, Similarities> similaritiesMap;
    private Context context;
    private View view;

    public ProfileAdapter(View view, Context context, List<Profile> profileList, Map<String, Similarities> similaritiesMap) {
        this.profiles = profileList;
        this.similaritiesMap = similaritiesMap;
        this.context = context;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        final Profile profile = profiles.get(position);
        final Context context = holder.itemView.getContext();

        if (profile.firstName != null && profile.lastName != null) {
            holder.profileNameText.setText(profile.firstName + " " + profile.lastName);
        } else {
            holder.profileNameText.setText(profile.firstName != null ? profile.firstName : profile.lastName);
        }

        holder.profileHeadlineText.setText(profile.headline);

        Glide.with(context)
             .load(profile.pictureUrl)
             .asBitmap()
             .into(new BitmapImageViewTarget(holder.profileImage) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                profile.profileColor = new Palette.Builder(bitmap).generate().getMutedColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });

        Similarities similarities = similaritiesMap.get(profile.id);
        if (similarities != null && similarities.numOfSimilarities >= 1) {
            holder.similaritySection.setVisibility(View.VISIBLE);
            if (similarities.numOfSimilarities > 1) {
                holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_common));
                holder.profileSimilarityText.setText(String.format(context.getResources()
                                                                          .getString(R.string.similarities_in_common),
                                                                   similarities.numOfSimilarities));
            } else {
                if (similarities.similarEducations.size() != 0) {
                    holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_education));
                    holder.profileSimilarityText.setText(String.format(context.getResources().getString(R.string.also_studied_at), similarities.similarEducations.get(0).schoolName));
                } else {
                    holder.profileSimilarityImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_work));
                    holder.profileSimilarityText.setText(String.format(context.getResources().getString(R.string.also_worked_at), similarities.similarPositions.get(0).companyName));
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

    public void addProfile(Profile profile, Similarities similarities) {
        this.profiles.add(profile);
        this.similaritiesMap.put(profile.id, similarities);
    }

    public Profile getItemAtAdapterPosition(int position) {
        return profiles.get(position);
    }

    private void runEnterAnimation(View view, int position) {
        if (position >= 4) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(AnimationUtils.getScreenHeight(context));
            view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(5.f))
                .setDuration(2000)
                .start();
        }
    }

    public void resetLastAnimationItem() {
        lastAnimatedPosition = -1;
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
