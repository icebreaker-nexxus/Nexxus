package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.EventListAdapter;
import com.icebreakers.nexxus.clients.MeetupClient;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.utils.EndlessRecyclerViewScrollListener;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import com.icebreakers.nexxus.utils.LocationProvider;
import org.parceler.Parcels;
import retrofit2.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity
        implements LocationProvider.LocationCallback {

    private static final String TAG = NexxusApplication.BASE_TAG + EventListActivity.class.getName();

    private LocationProvider mLocationProvider;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rvEvents)
    RecyclerView rvEvents;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    ActionBarDrawerToggle drawerToggle;

    List<MeetupEvent> events = new ArrayList<>();
    EventListAdapter eventListAdapter;

    private CompositeSubscription compositeSubscription;

    EndlessRecyclerViewScrollListener scrollListener;

    Location lastKnownLocation;

    ItemClickSupport.OnItemClickListener eventClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {

           // launch Event details activity
            MeetupEvent event = events.get(position);

            Intent detailsActivityIntent = new Intent(EventListActivity.this, EventDetailsActivity.class);
            detailsActivityIntent.putExtra("event", Parcels.wrap(event));
            startActivity(detailsActivityIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mLocationProvider = new LocationProvider(this, this);

        compositeSubscription = new CompositeSubscription();

        // Set up recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        eventListAdapter = new EventListAdapter(events);
        rvEvents.setAdapter(eventListAdapter);
        rvEvents.setLayoutManager(layoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "inside onLoadMore");

                // TODO Paging will have to be handled by searching for more events by increasing radius. - P1
            }
        };
        // Adds the scroll listener to RecyclerView
        rvEvents.addOnScrollListener(scrollListener);

        // Add item click listener
        ItemClickSupport.addTo(rvEvents).setOnItemClickListener(eventClickListener);

        // setup swipe to refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.white,
                R.color.colorPrimary,
                android.R.color.white);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO search for more events
                 fetchMeetupEvents(lastKnownLocation);
            }
        });

        // navigation bar
        drawerToggle = setupDrawerToggle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

        // TODO Filter events based on categories with ids - with map
        // = 2 (Career and Business)
        // 6 Education and Learning
        // 34 Tech


        // TODO Sort this list with shortest distance - with map

        CompositeSubscription compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(MeetupClient.getInstance()
                .rxfindEvents(location.getLatitude(), location.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MeetupEvent>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "received error", e);
                        swipeRefreshLayout.setRefreshing(false);
                        // TODO handle this?
                    }

                    @Override
                    public void onNext(List<MeetupEvent> meetupEvents) {
                        swipeRefreshLayout.setRefreshing(false);

                        Log.d(TAG, "onNext events #" + meetupEvents.size());
                        for (MeetupEvent event: meetupEvents) {
                            Log.d(TAG, event.toString());
                            if (event.getGroup().getCategory().getId() == 6
                                    || event.getGroup().getCategory().getId() == 34
                                    || event.getGroup().getCategory().getId() == 2)
                                events.add(event);
                        }

                        eventListAdapter.notifyDataSetChanged();

                    }
                })
        );
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
}
