package com.icebreakers.nexxus.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.EventListAdapter;
import com.icebreakers.nexxus.clients.MeetupClient;
import com.icebreakers.nexxus.listeners.EventClickListener;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.utils.EndlessRecyclerViewScrollListener;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import com.icebreakers.nexxus.utils.LocationProvider;
import retrofit2.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 4/12/17.
 */

public class EventListFragment extends Fragment implements LocationProvider.LocationCallback {
    private static final String TAG = NexxusApplication.BASE_TAG + EventListFragment.class.getName();
    /**
     * Id to identify a location permission request.
     */
    private static final int REQUEST_LOCATION_PERMISSION = 0;

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvEvents) RecyclerView rvEvents;

    List<MeetupEvent> events = new ArrayList<>();
    EventListAdapter eventListAdapter;
    LinearLayoutManager layoutManager;
    LocationProvider mLocationProvider = null;
    CompositeSubscription compositeSubscription;
    EndlessRecyclerViewScrollListener scrollListener;
    Location lastKnownLocation;
    EventClickListener onEventClickListener;
    ItemClickSupport.OnItemClickListener eventClickListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onEventClickListener = (EventClickListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.bind(this, view);

        compositeSubscription = new CompositeSubscription();

        // Set up recyclerView
        layoutManager = new LinearLayoutManager(getContext());
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

        eventClickListener = new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                // launch Event details activity
                MeetupEvent event = events.get(position);
                EventListFragment.this.onEventClickListener.onEventClick(event);

            }
        };

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLocationProvider == null) {
            // check permissions
            Log.i(TAG, "Checking if the app has necessary LOCATION permissions");
            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // Location permissions have not been granted.
                Log.i(TAG, "Location permissions has NOT been granted. Requesting permissions.");
                requestLocationPermissions();

            } else {

                // location permissions have been granted. Connect location provider
                Log.i(TAG, "Location permissions have already been granted. Creating LocationProvider instance.");
                mLocationProvider = new LocationProvider(getContext(), this);
                mLocationProvider.connect();
            }
        }

    }

    private void requestLocationPermissions() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Log.i(TAG,
                  "Displaying location permission rationale to provide additional context.");
            Snackbar.make(swipeRefreshLayout, R.string.location_permission_rationale,
                          Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),
                                                              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                              REQUEST_LOCATION_PERMISSION);
                        }
                    })
                    .show();

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
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
                        mLocationProvider = new LocationProvider(getContext(), this);
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
    public void onPause() {
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

        // TODO Filter events based on categories with ids - with map
        // = 2 (Career and Business)
        // 6 Education and Learning
        // 34 Tech


        // TODO Sort this list with shortest distance - with map

        CompositeSubscription compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(MeetupClient.getInstance()
                                              .rxfindEvents(location.getLatitude(), location.getLongitude())
                                              .map(meetupEvents -> { return filterEvents(meetupEvents);})
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
                                                      events.addAll(meetupEvents);
                                                      eventListAdapter.notifyDataSetChanged();

                                                  }
                                              })
        );
    }

    private List<MeetupEvent> filterEvents(List<MeetupEvent> allEvents)
    {
        List<MeetupEvent> interestingEvents = new ArrayList<>();

        for (MeetupEvent event: allEvents) {
            if (event.getVenue() != null
                && (event.getGroup().getCategory().getId() == 6
                || event.getGroup().getCategory().getId() == 34
                || event.getGroup().getCategory().getId() == 2)) {
                interestingEvents.add(event);
                Log.d(TAG, "Event added: " + event.toString());
            }
        }

        return interestingEvents;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
