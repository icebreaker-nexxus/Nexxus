package com.icebreakers.nexxus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.Profile;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icebreakers.nexxus.MainActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/5/17.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = NexxusApplication.BASE_TAG + ProfileFragment.class.getName();

    @BindView(R.id.tvName) TextView tvProfileName;
    @BindView(R.id.tvHeadline) TextView tvHeadline;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

    private Profile profile;

    public static ProfileFragment newInstance(Profile profile) {

        Bundle args = new Bundle();
        args.putParcelable(PROFILE_EXTRA, Parcels.wrap(profile));
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = Parcels.unwrap(getArguments().getParcelable(PROFILE_EXTRA));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        tvProfileName.setText(profile.firstName + " " + profile.lastName);
        tvHeadline.setText(profile.headline);
        Glide.with(getActivity()).load(profile.pictureUrl).into(ivProfileImage);

        return view;
    }
}
