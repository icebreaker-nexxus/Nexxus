package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.databinding.ActivityDetailsBinding;
import com.icebreakers.nexxus.models.MeetupEvent;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.icebreakers.nexxus.R.id.tvEventName;

public class EventDetailsActivity extends AppCompatActivity {

    private MeetupEvent event;

    ActivityDetailsBinding binding;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd");
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent detailsIntent = getIntent();
        event = Parcels.unwrap(detailsIntent.getParcelableExtra("event"));

        getSupportActionBar().setTitle("");

        // TODO Is image really needed?

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
        binding.header.tvDate.setText(dateFormat.format(new Date(event.getTime())));

        // TODO Set relative time or start time - end time if available.
        binding.header.tvTime.setText(timeFormat.format(new Date(event.getTime())));

        if (event.getVenue() != null) {
            binding.header.tvLocationTitle.setText(event.getVenue().getName());
            String address = String.format("%s, %s", event.getVenue().getAddress1(), event.getVenue().getCity());
            binding.header.tvLocationAddress.setText(address);
            binding.header.ivLocation.setColorFilter(getResources().getColor(android.R.color.darker_gray));
        } else {
            binding.header.ivLocation.setVisibility(View.GONE);
        }

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

}
