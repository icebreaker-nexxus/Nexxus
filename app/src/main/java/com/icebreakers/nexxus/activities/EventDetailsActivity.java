package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.models.MeetupEvent;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailsActivity extends AppCompatActivity {

    private MeetupEvent event;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop)
    ImageView ivBackdrop;

    @BindView(R.id.tvEventName)
    TextView tvEventName;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvTime)
    TextView tvTime;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent detailsIntent = getIntent();
        event = Parcels.unwrap(detailsIntent.getParcelableExtra("event"));

        getSupportActionBar().setTitle(event.getName());

        String imageURL = null;
        if (event.getGroup().getKeyPhoto() != null) {
            imageURL = event.getGroup().getKeyPhoto().getHighresLink();
        } else  if (event.getGroup().getPhoto() != null) {
            imageURL = event.getGroup().getPhoto().getHighresLink();
        }

        if (imageURL != null) {

            if (imageURL != null) {
                ivBackdrop.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageURL)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading)
                        .into(ivBackdrop);
            } else {
                ivBackdrop.setVisibility(View.GONE);
            }
        } else {
            ivBackdrop.setVisibility(View.GONE);
        }

        tvEventName.setText(event.getName());
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            tvDescription.setText(Html.fromHtml(event.getDescription()));
        } else {
            tvDescription.setVisibility(View.GONE);
        }
        tvTime.setText(dateFormat.format(new Date(event.getTime())));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO implement check in action
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
