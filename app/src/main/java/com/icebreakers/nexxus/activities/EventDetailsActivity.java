package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.ProfileImageAdapter;
import com.icebreakers.nexxus.databinding.ActivityEventDetailsBinding;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.Venue;
import com.icebreakers.nexxus.models.internal.MeetupEventRef;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import com.icebreakers.nexxus.utils.MapUtils;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.design.widget.Snackbar.make;

public class EventDetailsActivity extends BaseActivity {

    private static final String TAG = NexxusApplication.BASE_TAG + EventDetailsActivity.class.getName();

    private MeetupEvent event;
    private MeetupEventRef eventRef;

    ActivityEventDetailsBinding binding;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEEE, MMM dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");

    ProfileHolder profileHolder;
    Profile currentUser;
    List<Profile> attendees;
    ProfileImageAdapter adapter;

    OnMapReadyCallback mapReadyCallback =  new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Venue venue =  event.getVenue();
            LatLng point = new LatLng(venue.getLat(), venue.getLon());
            BitmapDescriptor icon = MapUtils.createBubble(EventDetailsActivity.this, IconGenerator.STYLE_GREEN, venue.getName());
            // Creates and adds marker to the map
            Marker marker = MapUtils.addMarker(googleMap, point, venue.getName(), icon, true);
            marker.setPosition(point);

            // Zoom in to Event location
            CameraUpdate center = CameraUpdateFactory.newLatLng(point);
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(11);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
        }
    };
    private ItemClickSupport.OnItemClickListener profileImageClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            if (profileHolder.isUserCheckedIn(eventRef)) {
                // start ProfileListActivity
                startActivity(new Intent(EventDetailsActivity.this, ProfileListActivity.class));
            } else {
                final Snackbar snackbar = Snackbar.make(recyclerView, getString(R.string.before_checkin), Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.checkin), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                handleCheckIn();
                            }
                        }).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        Intent detailsIntent = getIntent();
        event = Parcels.unwrap(detailsIntent.getParcelableExtra("event"));
        eventRef = event.getEventRef();

        profileHolder = ProfileHolder.getInstance(this);
        currentUser = profileHolder.getProfile();

        String imageURL = null;
        if (event.getGroup().getKeyPhoto() != null) {
            imageURL = event.getGroup().getKeyPhoto().getHighresLink();
        } else  if (event.getGroup().getPhoto() != null) {
            imageURL = event.getGroup().getPhoto().getHighresLink();
        }

        if (imageURL != null) {

            if (imageURL != null) {
                binding.ivBackdrop.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageURL)
                        .placeholder(R.drawable.layer_list_event)
                        .error(R.drawable.layer_list_event)
                        .into(binding.ivBackdrop);
            } else {
                binding.ivBackdrop.setVisibility(View.GONE);
            }
        } else {
            binding.ivBackdrop.setVisibility(View.GONE);
       }

        binding.header.tvEventName.setText(event.getName());
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            binding.header.tvDescription.setText(Html.fromHtml(event.getDescription()));
        } else {
            binding.header.tvDescription.setVisibility(View.GONE);
        }

        binding.header.ivTime.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        binding.header.tvDate.setText(DATE_FORMAT.format(new Date(event.getTime())));

        // TODO Set relative time or start time - end time if available.
        binding.header.tvTime.setText(TIME_FORMAT.format(new Date(event.getTime())));

        // We make sure that venue is never null
        binding.header.tvLocationTitle.setText(event.getVenue().getName());
        String address = String.format("%s, %s", event.getVenue().getAddress1(), event.getVenue().getCity());
        binding.header.tvLocationAddress.setText(address);
        binding.header.ivLocation.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        // setup map view
        binding.mapview.onCreate(savedInstanceState);
        binding.mapview.getMapAsync(mapReadyCallback);

        /*
        Section to be removed after using checking Button
        if (profileHolder.isUserCheckedIn(eventRef)) {
            Log.d(TAG, "User has checked in to " + event.getName());
            binding.fab.setVisibility(View.GONE);
            binding.header.linearLayoutCheckInSection.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "User has NOT checked in to " + event.getName());
            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleCheckIn();
                    binding.fab.setVisibility(View.GONE);
                }
            });
        }
         **/

        setupCheckInSection();

        // Set up recyclerView for profileimages
        attendees = profileHolder.getAttendees(event);
        Log.d(TAG, "Profiles # " + attendees.size());
        adapter = new ProfileImageAdapter(attendees);
        binding.rvProfileImages.setAdapter(adapter);
        binding.rvProfileImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Add item click listener
        ItemClickSupport.addTo(binding.rvProfileImages).setOnItemClickListener(profileImageClickListener);
    }

    private void setupCheckInSection() {
        if (profileHolder.isUserCheckedIn(eventRef)) {
            binding.header.linearLayoutCheckInSection.setVisibility(View.GONE);
        }
        binding.header.btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckIn();
            }
        });
    }

    private void handleCheckIn() {
        profileHolder.checkIn(eventRef);
        attendees.add(0, currentUser);
        adapter.notifyItemInserted(0);
        binding.header.linearLayoutCheckInSection.setVisibility(View.GONE);
        EventBus.getDefault().post(event);

        make(binding.toolbar, getString(R.string.after_checkin), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.show_me), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(EventDetailsActivity.this, ProfileListActivity.class));
                    }
                }).show();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapview.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapview.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        binding.mapview.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapview.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapview.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapview.onDestroy();
    }
}
