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
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.utils.AnimationUtils;

import java.util.List;

/**
 * Created by amodi on 5/6/17.
 */

public class MessagesAdapter extends  RecyclerView.Adapter<MessagesAdapter.ProfileViewHolder> {
    private boolean isInitialLoad = true;
    private int lastAnimatedPosition = -1;
    private List<Profile> profiles;
    private Context context;

    public MessagesAdapter(Context context, List<Profile> profileList) {
        this.profiles = profileList;
        this.context = context;
    }

    @Override
    public MessagesAdapter.ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new MessagesAdapter.ProfileViewHolder(view);
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
                     profile.profileColor = new Palette.Builder(bitmap).generate()
                                                                       .getMutedColor(ContextCompat.getColor(context,
                                                                                                             R.color.colorPrimary));
                 }
             });

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }


    private void runEnterAnimation(View view, int position) {

        if (!isInitialLoad && position >= 4) {
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

    public void notifyAdapter() {
        isInitialLoad = false;
        notifyDataSetChanged();
    }

    public void resetLastAnimationItem() {
        lastAnimatedPosition = -1;
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_name_text) TextView profileNameText;
        @BindView(R.id.profile_headline_text) TextView profileHeadlineText;
        @BindView(R.id.profile_image) ImageView profileImage;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

