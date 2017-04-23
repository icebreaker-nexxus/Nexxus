package com.icebreakers.nexxus.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.EventFragmentPagerAdapater;
import com.icebreakers.nexxus.clients.MeetupClient;
import com.icebreakers.nexxus.databinding.ActivityEventListBinding;
import com.icebreakers.nexxus.fragments.BaseEventListFragment;
import com.icebreakers.nexxus.fragments.CheckedInEventListFragment;
import com.icebreakers.nexxus.fragments.NearbyEventListFragment;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.utils.LocationProvider;
import com.icebreakers.nexxus.utils.LogoutUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends BaseActivity
        implements LocationProvider.LocationCallback {

    private static final String TAG = NexxusApplication.BASE_TAG + EventListActivity.class.getSimpleName();

    public interface EventListUpdate {
        public void update(List<MeetupEvent> events);
        public void add(MeetupEvent newEvent);
        public void setRefreshing(boolean refreshing);
    }

    /**
     * Id to identify a location permission request.
     */
    private static final int REQUEST_LOCATION_PERMISSION = 0;

    private LocationProvider mLocationProvider = null;

    NearbyEventListFragment nearbyEventListFragment;
    CheckedInEventListFragment checkedInEventListFragment;

    ImageView navHeaderProfileImage;
    TextView navHeaderProfileName;
    ActionBarDrawerToggle drawerToggle;
    Profile profile;

    private CompositeSubscription compositeSubscription;

    Location lastKnownLocation;

    ActivityEventListBinding binding;

    EventFragmentPagerAdapater adapter;

    private class EventList {
        List<MeetupEvent> nearbyEvents;
        List<MeetupEvent> checkedInEvents;

        public EventList() {
            nearbyEvents = new ArrayList<>();
            checkedInEvents = new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_list);

        setSupportActionBar(binding.toolbar);

        profile = ProfileHolder.getInstance(this).getProfile();

        compositeSubscription = new CompositeSubscription();

        // Set up fragments and view pager
        List<BaseEventListFragment> fragments = new ArrayList<>();
        nearbyEventListFragment = NearbyEventListFragment.newInstance();
        checkedInEventListFragment = CheckedInEventListFragment.newInstance();

        fragments.add(nearbyEventListFragment.getTabPosition(), nearbyEventListFragment);
        fragments.add(checkedInEventListFragment.getTabPosition(), checkedInEventListFragment);

        adapter = new EventFragmentPagerAdapater(getSupportFragmentManager(), fragments);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setPageTransformer(true, new AccordionTransformer());
        binding.tabs.setupWithViewPager(binding.viewpager);

        // navigation bar
        drawerToggle = setupDrawerToggle();
        setupDrawerContent(binding.navigationView);
        View v = binding.navigationView.getHeaderView(0);
        navHeaderProfileImage = (ImageView) v.findViewById(R.id.ivNavprofileImage);
        navHeaderProfileName = (TextView) v.findViewById(R.id.tvNavProfileName);
        setupNavigationHeader();

        EventBus.getDefault().register(this);
        Log.d(TAG, "EVENTBUG REGISTERED");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mLocationProvider == null) {
            // check permissions
            Log.i(TAG, "Checking if the app has necessary LOCATION permissions");
            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Location permissions have not been granted.
                Log.i(TAG, "Location permissions has NOT been granted. Requesting permissions.");
                requestLocationPermissions();

            } else {

                // location permissions have been granted. Connect location provider
                Log.i(TAG, "Location permissions have already been granted. Creating LocationProvider instance.");
                mLocationProvider = new LocationProvider(this, this);
                mLocationProvider.connect();
            }
        }

    }

    private void requestLocationPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
                Log.i(TAG,
                        "Displaying location permission rationale to provide additional context.");
                Snackbar.make(binding.tabs, R.string.location_permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(EventListActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION_PERMISSION);
                            }
                        })
                        .show();

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (mLocationProvider == null) {
                        mLocationProvider = new LocationProvider(this, this);
                        mLocationProvider.connect();
                    }

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    // TODO how do we handle this?
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
    }

    @Override
    public void onLocationUpdated(Location location) {
        lastKnownLocation = location;
        Log.d(TAG, "onLocationUpdated: Location received: " + location.toString());
        fetchMeetupEvents(location);
    }

    private void fetchMeetupEvents(final Location location) {
        // For debug
        Call<List<MeetupEvent>> eventsCall = MeetupClient.getInstance().findEvents(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "Meetup find events request: " + eventsCall.request().url().toString());

        CompositeSubscription compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(MeetupClient.getInstance()
                .rxfindEvents(location.getLatitude(), location.getLongitude())
                .map(meetupEvents -> { return filterAndSeparate(meetupEvents);})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<EventList>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "received error", e);
                        BaseEventListFragment currentFragment = adapter.getRegisteredFragment(binding.viewpager.getCurrentItem());
                        currentFragment.setRefreshing(false);
                    }

                    @Override
                    public void onNext(EventList eventList) {
                        BaseEventListFragment currentFragment = adapter.getRegisteredFragment(binding.viewpager.getCurrentItem());
                        currentFragment.setRefreshing(false);

                        Log.d(TAG, "onNext events #" + eventList.nearbyEvents.size());
                        nearbyEventListFragment.update(eventList.nearbyEvents);
                        checkedInEventListFragment.update(eventList.checkedInEvents);
                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        compositeSubscription.unsubscribe();
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

    private EventList filterAndSeparate(List<MeetupEvent> allEvents)
    {
        EventList eventList = new EventList();
        // = 2 (Career and Business)
        // 6 Education and Learning
        // 34 Tech

        ProfileHolder profileHolder = ProfileHolder.getInstance(this);

        for (MeetupEvent event: allEvents) {
            if (event.getVenue() != null
                    && (event.getGroup().getCategory().getId() == 6
                    || event.getGroup().getCategory().getId() == 34
                    || event.getGroup().getCategory().getId() == 2)
                    && (event.getGroup().getKeyPhoto() != null
                    || event.getGroup().getPhoto() != null)) {

                if (profileHolder.isUserCheckedIn(event.getEventRef())) {
                    eventList.checkedInEvents.add(event);
                }

                eventList.nearbyEvents.add(event);
            }
        }

        return eventList;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.drawer_open,  R.string.drawer_close);
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
                Router.startProfileActivity(this, profile, null);
                break;
            case R.id.logout_nav:
                LogoutUtils.logout(this);
                break;
            case R.id.nearby_nav:
                startActivity(new Intent(this, NearbyActivity.class));
                break;
            default:
                break;
        }

        // Close the navigation drawer
        binding.drawer.closeDrawers();
    }

    private void setupNavigationHeader() {
        Glide.with(this).load(profile.pictureUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(navHeaderProfileImage);
        navHeaderProfileName.setText(profile.firstName + " " + profile.lastName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MeetupEvent event) {
        Log.d(TAG, "Checkin-event received " + event);
        checkedInEventListFragment.add(event);
    }
}
