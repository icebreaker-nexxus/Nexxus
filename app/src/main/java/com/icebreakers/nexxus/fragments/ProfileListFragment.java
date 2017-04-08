package com.icebreakers.nexxus.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.ProfileAdapter;
import com.icebreakers.nexxus.helpers.SimilaritiesFinder;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amodi on 4/8/17.
 */

public class ProfileListFragment extends Fragment {

    @BindView(R.id.rv_profile) RecyclerView recyclerView;

    ProfileAdapter profileAdapter;
    RecyclerView.LayoutManager layoutManager;
    Profile profile;

    public static ProfileListFragment newInstance(Profile profile) {

        Bundle args = new Bundle();
        args.putParcelable("x", Parcels.wrap(profile));
        ProfileListFragment fragment = new ProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = Parcels.unwrap(getArguments().getParcelable("x"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        ButterKnife.bind(this, view);

        // test data, to be replaced by real calls from db
        List<Profile> b = new ArrayList<>();
        b.add(profile);

        Profile asd = new Profile();
        asd.educationList = new ArrayList<>();
        Profile.Education education = new Profile.Education();
        education.schoolName = profile.educationList.get(0).schoolName;
        asd.educationList.add(education);
        b.add(asd);

        Map<String, Similarities> similaritiesMap = SimilaritiesFinder.findSimilarities(profile, b);
        layoutManager = new LinearLayoutManager(getContext());
        profileAdapter = new ProfileAdapter(getContext(), b, similaritiesMap);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(profileAdapter);

        return view;
    }
}