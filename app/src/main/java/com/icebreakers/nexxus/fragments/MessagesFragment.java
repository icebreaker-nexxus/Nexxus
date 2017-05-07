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
import com.icebreakers.nexxus.adapters.MessagesAdapter;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.models.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by amodi on 5/6/17.
 */

public class MessagesFragment extends Fragment {
    @BindView(R.id.rv_profile) RecyclerView recyclerView;

    private Profile loggedInProfile;
    private MessagesAdapter messagesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        ButterKnife.bind(this, view);
        Set<String> whiteListedProfilesIds = new HashSet<>();
        whiteListedProfilesIds.add("-GKTP4lCqZ");
        whiteListedProfilesIds.add("PtewWOGlGt");
        whiteListedProfilesIds.add("cBolp_P8Oz");
        whiteListedProfilesIds.add("ezuAnXLTZM");
        whiteListedProfilesIds.add("hj8uHrci7d");


        // fix this shit
        ProfileHolder profileHolder = ProfileHolder.getInstance(getContext());
        List<Profile> allProfiles = profileHolder.getAllProfiles();
        List<Profile> whitelistedProfiles = new ArrayList<>();
        for (Profile profile : allProfiles) {
            if (whiteListedProfilesIds.contains(profile.id)) {
                whitelistedProfiles.add(profile);
            }
        }
        messagesAdapter = new MessagesAdapter(getContext(), whitelistedProfiles);
        // end of shit

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(messagesAdapter);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }
}
