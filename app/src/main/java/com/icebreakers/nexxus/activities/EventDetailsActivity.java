package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
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
import com.icebreakers.nexxus.databinding.ActivityEventDetailsBinding;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Venue;
import com.icebreakers.nexxus.utils.MapUtils;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = NexxusApplication.BASE_TAG + EventDetailsActivity.class.getName();
    public static final String EVENT_EXTRA = "event";
    private MeetupEvent event;

    ActivityEventDetailsBinding binding;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent detailsIntent = getIntent();
        event = Parcels.unwrap(detailsIntent.getParcelableExtra(EVENT_EXTRA));

        getSupportActionBar().setTitle("");

//        String imageURL = null;
//        if (event.getGroup().getKeyPhoto() != null) {
//            imageURL = event.getGroup().getKeyPhoto().getHighresLink();
//        } else  if (event.getGroup().getPhoto() != null) {
//            imageURL = event.getGroup().getPhoto().getHighresLink();
//        }
//
//        if (imageURL != null) {
//
//            if (imageURL != null) {
//                ivBackdrop.setVisibility(View.VISIBLE);
//                Glide.with(this)
//                        .load(imageURL)
//                        .placeholder(R.drawable.loading)
//                        .error(R.drawable.loading)
//                        .into(ivBackdrop);
//            } else {
//                ivBackdrop.setVisibility(View.GONE);
//            }
//        } else {
//            ivBackdrop.setVisibility(View.GONE);
//       }

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

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO implement check in action
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
