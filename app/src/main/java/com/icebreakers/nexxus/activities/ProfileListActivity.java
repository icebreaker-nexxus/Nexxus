package com.icebreakers.nexxus.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Comparator;
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
    @BindView(R.id.searchtoolbar) Toolbar searchToolbar;
    @BindView(R.id.rvProfiles) RecyclerView rvProfiles;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;

    MenuItem searchItem;
    Menu searchMenu;
    Menu menu;
    ProfileAdapter profileAdapter;
    RecyclerView.LayoutManager layoutManager;
    Profile currentProfile;
    List<Profile> profiles;
    List<Profile> allAttendees;
    ProfileHolder profileHolder;
    Map<String, Similarities> similaritiesMap;

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
        similaritiesMap = SimilaritiesFinder.findSimilarities(currentProfile, profiles);
        sortAttendees();

        // set up recyclerview
        profileAdapter = new ProfileAdapter(coordinatorLayout, this, profiles, similaritiesMap);
        rvProfiles.setAdapter(profileAdapter);
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
        setupSearchBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profiles, menu);

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
        sortAttendees();
        profileAdapter.updateSimilaritiesMap(SimilaritiesFinder.findSimilarities(currentProfile, matchedProfiles));
        profileAdapter.resetLastAnimationItem();
        profileAdapter.notifyAdapter();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(R.id.searchtoolbar, true);
                }
                else {
                    searchToolbar.setVisibility(View.VISIBLE);
                }

                searchItem.expandActionView();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupSearchBar() {
        searchToolbar.inflateMenu(R.menu.menu_search);
        searchMenu = searchToolbar.getMenu();

        searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar, false);
                else
                    searchToolbar.setVisibility(View.GONE);
            }
        });

        searchItem = searchMenu.findItem(R.id.action_filter_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(R.id.searchtoolbar, false);
                }
                else {
                    searchToolbar.setVisibility(View.GONE);
                }
                updateProfileList(allAttendees);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;
            }
        });

        initSearchView();
    }

    public void initSearchView() {
        final SearchView searchView = (SearchView) searchMenu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);


        // set hint and the text colors

        int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, final boolean shouldMakeVisible) {
        final View myView = findViewById(viewID);

        int width = toolbar.getWidth();

        width -= (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) -
            (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2) -
            getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = toolbar.getHeight()/2;

        Animator anim;
        if(shouldMakeVisible) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        }
        else {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        }

        anim.setDuration(1000);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(!shouldMakeVisible) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.GONE);
                }
            }
        });

        // make the view visible and start the animation
        if (shouldMakeVisible) {
            myView.setVisibility(View.VISIBLE);
        }
        // start the animation
        anim.start();
    }

    private void sortAttendees() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            profiles.sort(new Comparator<Profile>() {
                @Override
                public int compare(Profile profile, Profile t1) {
                    Similarities similarities1 = similaritiesMap.get(profile.id);
                    Similarities similarities2 = similaritiesMap.get(t1.id);
                    if ((similarities1 == null) && (similarities2 == null)) {
                        return 0;
                    }
                    if (similarities1 == null && similarities2.numOfSimilarities > 0) {
                        return 1;
                    }
                    if (similarities2 == null && similarities1.numOfSimilarities > 0) {
                        return -1;
                    }
                    if (similarities1 == null || similarities2 == null) {
                        return 0;
                    }
                    if (similarities1.numOfSimilarities > similarities2.numOfSimilarities) {
                        return -1;
                    } else if (similarities1.numOfSimilarities < similarities2.numOfSimilarities) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }
}