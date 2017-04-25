package com.icebreakers.nexxus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.activities.EventDetailsActivity;
import com.icebreakers.nexxus.activities.EventListActivity;
import com.icebreakers.nexxus.adapters.EventFragmentPagerAdapater;
import com.icebreakers.nexxus.adapters.EventListAdapter;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.utils.EndlessRecyclerViewScrollListener;
import com.icebreakers.nexxus.utils.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.icebreakers.nexxus.activities.EventDetailsActivity.EVENT_EXTRA;

/**
 * Created by radhikak on 4/21/17.
 */

public abstract class BaseEventListFragment extends Fragment implements EventFragmentPagerAdapater.EventPageTab, EventListActivity.EventListUpdate {

    private static final String TAG = NexxusApplication.BASE_TAG + BaseEventListFragment.class.getSimpleName();

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rvEvents)
    RecyclerView rvEvents;

    @BindView(R.id.progressBar)
    MaterialProgressBar progressBar;

    EndlessRecyclerViewScrollListener scrollListener;

    List<MeetupEvent> events = new ArrayList<>();
    EventListAdapter eventListAdapter;

    ItemClickSupport.OnItemClickListener eventClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            // launch Event details activity
            MeetupEvent event = events.get(position);

            Intent detailsActivityIntent = new Intent(getActivity(), EventDetailsActivity.class);
            detailsActivityIntent.putExtra(EVENT_EXTRA, Parcels.wrap(event));

            Pair<View, String> p1 = Pair.create(v.findViewById(R.id.ivImage), "eventImage");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    p1);
            startActivity(detailsActivityIntent, options.toBundle());
        }
    };


    public BaseEventListFragment() {
        // Required empty public constructor
        events = new ArrayList<>();
        eventListAdapter = new EventListAdapter(events);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.bind(this, view);

        Log.d(TAG, "Inside onViewCreated");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventListAdapter = new EventListAdapter(events);
        rvEvents.setAdapter(eventListAdapter);
        rvEvents.setLayoutManager(layoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
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
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
                // fetchMeetupEvents(lastKnownLocation);
            }
        });
    }

    @Override
    public void update(List<MeetupEvent> newEvents) {
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        events.addAll(newEvents);
        eventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void add(MeetupEvent newEvent) {
        events.add(newEvent);
        eventListAdapter.notifyItemInserted(events.size());
    }
}
