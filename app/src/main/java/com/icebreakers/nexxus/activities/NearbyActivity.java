package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.adapters.ProfileAdapter;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.helpers.SimilaritiesFinder;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.utils.ItemClickSupport;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by amodi on 4/18/17.
 */

public class NearbyActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = NearbyActivity.class.getSimpleName();
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @BindView(R.id.pulse) PulsatorLayout pulsatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvProfiles) RecyclerView recyclerView;

    private GoogleApiClient googleApiClient;
    private Message activeMessage;
    private MessageListener messageListener;
    private boolean retried = false;
    private Profile loggedInProfile;
    private ProfileAdapter profileAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Set<String> profileIds;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "connected!");
        subscribe();
        publish();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "GoogleApiClient disconnected with cause: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "GoogleApiClient connection failed");
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nearby_attendees);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profileIds = new HashSet<>();
        layoutManager = new LinearLayoutManager(this);
        profileAdapter = new ProfileAdapter(new ArrayList<>(), new HashMap<>());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(profileAdapter);

        pulsatorLayout.start();
        loggedInProfile = ProfileHolder.getInstance(this).getProfile();
        googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Nearby.MESSAGES_API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
        setupMessageListener();
        setupItemClickListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                googleApiClient.connect();
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void setupMessageListener() {
        messageListener = new MessageListener() {

            @Override
            public void onFound(Message message) {
                String modelString = new String(message.getContent());
                Gson gson = new Gson();
                Profile profile = gson.fromJson(modelString, Profile.class);
                if (profile.id.equals(loggedInProfile.id)) {
                    Log.v(TAG, "Received a signal from the same profile, ignoring!");
                    return;
                }
                if (!profileIds.contains(profile.id)) {
                    profileIds.add(profile.id);
                    profileAdapter.addProfile(profile, SimilaritiesFinder.findSimilarities(loggedInProfile, profile));
                    profileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost message: " + messageAsString);
            }
        };
    }

    private void setupItemClickListener() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Profile profile = profileAdapter.getItemAtAdapterPosition(position);
                Pair<View, String> p1 = Pair.create(v.findViewById(R.id.profile_image), "profileImage");
                Router.startProfileActivity(NearbyActivity.this, profile, p1);
            }
        });
    }

    private void subscribe() {
        Log.v(TAG, "subscribing");
        PendingResult<Status> subscriptionStatus = Nearby.Messages.subscribe(googleApiClient, messageListener);
        subscriptionStatus.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.v(TAG, "Subscription result is " + status);
                if (status.getStatusCode() == 2806) {
                    Log.e(TAG, "Forbidden, should not retry");
                    return;
                }
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(NearbyActivity.this, 1001);
                        Log.v(TAG, "retrying..");
                        if (!retried) {
                            retried = true;
                            subscribe();
                        }
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "resolution failed", e);
                    }
                }
            }
        });
    }

    private void unsubscribe() {
        Log.v(TAG, "unsubscribing");
        PendingResult<Status> subscriptionStatus = Nearby.Messages.unsubscribe(googleApiClient, messageListener);
        subscriptionStatus.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.v(TAG, "Unsubscription result is " + status);
            }
        });
    }

    private void publish() {
        Log.v(TAG, "publishing");
        activeMessage = new Message(new Gson().toJson(loggedInProfile).getBytes());
        Nearby.Messages.publish(googleApiClient, activeMessage);
    }

    private void unpublish() {
        Log.v(TAG, "unpublishing");
        if (activeMessage != null) {
            Nearby.Messages.unpublish(googleApiClient, activeMessage);
            activeMessage = null;
        }
    }




}

