package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.clients.LinkedInClient;
import com.icebreakers.nexxus.models.internal.Profile;
import com.icebreakers.nexxus.persistence.Database;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.MainActivity.PROFILE_EXTRA;
import static com.icebreakers.nexxus.persistence.Database.PROFILE_TABLE;

/**
 * Created by amodi on 4/4/17.
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = NexxusApplication.BASE_TAG + SplashActivity.class.getSimpleName();
    // splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AccessToken accessToken = NexxusSharePreferences.getLIAccessToken(this);
        LISessionManager.getInstance(getApplicationContext()).init(accessToken);
        LISession session = LISessionManager.getInstance(getApplicationContext()).getSession();
        String profileId = NexxusSharePreferences.getProfileId(this);
        if (session != null && session.isValid() && profileId != null) {

            Database.instance().databaseReference.child(PROFILE_TABLE).child(profileId)
                                                 .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    com.icebreakers.nexxus.models.Profile profile = dataSnapshot.getValue(com.icebreakers.nexxus.models.Profile.class);
                    if (profile == null) {
                        Log.e(TAG, "Cannot find profile for profileId " + dataSnapshot.getKey());
                        fetchProfileAndStartActivity();
                    } else {
                        startMainActivity(profile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Cannot find object in the database " + databaseError);
                }
            });
        } else {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void fetchProfileAndStartActivity() {
        LinkedInClient linkedInClient = new LinkedInClient(getApplicationContext());
        linkedInClient.fetchFullProfileInformation(new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Gson gson = new GsonBuilder().create();
                Profile internalProfile = gson.fromJson(apiResponse.getResponseDataAsString(), Profile.class);
                com.icebreakers.nexxus.models.Profile profile = com.icebreakers.nexxus.models.Profile.convertFromInternalProfile(internalProfile);
                Database.instance().insertProfileValue(profile);
                startMainActivity(profile);
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.e(TAG, "Error fetching profile information " + error);
            }
        });
    }

    private void startMainActivity(com.icebreakers.nexxus.models.Profile profile) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        startActivity(intent);
    }
}