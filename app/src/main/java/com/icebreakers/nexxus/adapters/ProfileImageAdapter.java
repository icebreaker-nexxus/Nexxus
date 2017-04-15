package com.icebreakers.nexxus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.Profile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by radhikak on 4/13/17.
 */

public class ProfileImageAdapter extends RecyclerView.Adapter<ProfileImageAdapter.ProfileImageHolder> {

    private List<Profile> profiles;

    public static class ProfileImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;

        public ProfileImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ProfileImageAdapter(List<Profile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public ProfileImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_image, null);
        return new ProfileImageHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(ProfileImageHolder holder, int position) {

        Profile profile = profiles.get(position);
        Glide.with(holder.itemView.getContext()).load(profile.pictureUrl).into(holder.ivProfileImage);

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
