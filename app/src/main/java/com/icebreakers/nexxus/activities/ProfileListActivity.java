package com.icebreakers.nexxus.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.ProfileAdapter;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.helpers.SimilaritiesFinder;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Similarities;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by amodi on 4/8/17.
 */

public class ProfileListActivity extends BaseActivity {

    private static final String TAG = NexxusApplication.BASE_TAG + ProfileListActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.rvProfiles)
    RecyclerView rvProfiles;

    ProfileAdapter profileAdapter;
    RecyclerView.LayoutManager layoutManager;
    Profile currentProfile;
    List<Profile> profiles;
    List<Profile> allAttendees;
    ProfileHolder profileHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.attendees));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileHolder = ProfileHolder.getInstance(this);
        currentProfile = profileHolder.getProfile();
        allAttendees = profileHolder.getAllProfiles();

        profiles = new ArrayList<>();
        profiles.addAll(allAttendees);

        // find similarities
        Map<String, Similarities> similaritiesMap = SimilaritiesFinder.findSimilarities(currentProfile, profiles);

        // set up recyclerview
        profileAdapter = new ProfileAdapter(profiles, similaritiesMap);
        rvProfiles.setAdapter(new SlideInBottomAnimationAdapter(profileAdapter));
        rvProfiles.setLayoutManager(new LinearLayoutManager(this));
        // Add item click listener
        ItemClickSupport.addTo(rvProfiles).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Profile profile = profiles.get(position);
                Pair<View, String> p1 = Pair.create(v.findViewById(R.id.profile_image), "profileImage");
                Router.startProfileActivity(ProfileListActivity.this, profile, p1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profiles, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(Color.WHITE);
        et.setHint(R.string.search_by);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (profileAdapter.getItemCount() != allAttendees.size() && newText.isEmpty()) {
                    Log.d(TAG, "Search query is empty, calling updateProfileList");
                    updateProfileList(allAttendees);
                }
                return true;
            }
        });


        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // SearchView is closing
                updateProfileList(allAttendees);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    private void performSearch(String query) {
        Set<Profile> matchedProfileSet = new HashSet<>();
        List<Profile> matchedProfiles = new ArrayList<>();
        List<Profile> allAttendees = profileHolder.getAllProfiles();
        query = query.toLowerCase();

        for (Profile profile: allAttendees) {
            // check for name match
            if (profile.firstName.toLowerCase().contains(query) || profile.lastName.toLowerCase().contains(query)) {
                // name match
                matchedProfileSet.add(profile);
                Log.d(TAG, "Matched NAME adding " + profile);
            } else if (profile.headline.toLowerCase().contains(query)) {
                matchedProfileSet.add(profile);
            } else {
                // check for education / school
                if (profile.educationList != null) {
                    for (Profile.Education education : profile.educationList) {
                        if (education.schoolName.toLowerCase().contains(query)) {
                            matchedProfileSet.add(profile);
                            Log.d(TAG, "Matched Education SchoolName adding " + profile);
                            break;
                        }
                    }
                }

                // check for company name / title
                if (profile.positionList != null) {
                    for (Profile.Position position : profile.positionList) {
                        if (position.companyName.toLowerCase().contains(query) || position.title.toLowerCase().contains(query)) {
                            matchedProfileSet.add(profile);
                            Log.d(TAG, "Matched Position adding " + profile);
                            break;
                        }
                    }
                }
            }
        }
        matchedProfiles.addAll(matchedProfileSet);
        updateProfileList(matchedProfiles);
    }

    private void updateProfileList(List<Profile> matchedProfiles) {
        profiles.clear();
        profiles.addAll(matchedProfiles);
        profileAdapter.updateSimilaritiesMap(SimilaritiesFinder.findSimilarities(currentProfile, matchedProfiles));
        profileAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                item.expandActionView();
                searchView.requestFocus();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}