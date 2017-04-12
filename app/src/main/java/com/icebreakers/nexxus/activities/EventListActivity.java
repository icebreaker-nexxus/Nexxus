package com.icebreakers.nexxus.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.EventListFragment;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.listeners.EventClickListener;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;

public class EventListActivity extends BaseActivity implements EventClickListener {

    private static final String TAG = NexxusApplication.BASE_TAG + EventListActivity.class.getName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.navigationView) NavigationView navigationView;

    ActionBarDrawerToggle drawerToggle;
    ImageView navHeaderProfileImage;
    TextView navHeaderProfileName;
    Profile profile;

    @Override
    public void onEventClick(MeetupEvent meetupEvent) {
        Router.startEventDetailActivity(this, meetupEvent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ButterKnife.bind(this);

        profile = NexxusSharePreferences.getLoggedInMemberProfile(this);

        setSupportActionBar(toolbar);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, new EventListFragment()).commit();

        // navigation bar
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(navigationView);
        View v = navigationView.getHeaderView(0);
        navHeaderProfileImage = (ImageView) v.findViewById(R.id.ivNavprofileImage);
        navHeaderProfileName = (TextView) v.findViewById(R.id.tvNavProfileName);
        setupNavigationHeader();
    }

    // Navigation header
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    selectDrawerItem(menuItem);
                    return true;
                }
            });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch(menuItem.getItemId()) {
            case R.id.event_list_nav:
                //Router.startEventListActivity(this, profile);
                break;
            case R.id.profile_nav:
                Router.startProfileActivity(this, profile);
                break;
            case R.id.attendees_nav:
                Router.startProfileListActivity(this, profile);
                break;
            default:
                break;
        }

        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private void setupNavigationHeader() {
        Glide.with(this).load(profile.pictureUrl).into(navHeaderProfileImage);
        navHeaderProfileName.setText(profile.firstName + " " + profile.lastName);
    }
}
