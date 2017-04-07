package com.icebreakers.nexxus.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.EventListAdapter;
import com.icebreakers.nexxus.clients.MeetupClient;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.utils.EndlessRecyclerViewScrollListener;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import com.icebreakers.nexxus.utils.LocationProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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

    List<MeetupEvent> events = new ArrayList<>();
    EventListAdapter eventListAdapter;

    private CompositeSubscription compositeSubscription;

    EndlessRecyclerViewScrollListener scrollListener;

    Location lastKnownLocation;

    ItemClickSupport.OnItemClickListener eventClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {

           // launch Event details activity
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

        // TODO add tech categories

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
                        }

                        events.addAll(meetupEvents);
                        eventListAdapter.notifyDataSetChanged();

                    }
                })
        );
    }
}
