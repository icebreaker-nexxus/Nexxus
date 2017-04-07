package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.clients.LinkedInClient;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

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
        if (session != null && session.isValid()) {
            fetchProfileAndStartActivity();
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
        linkedInClient.fetchBasicProfileInformation(new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Profile profile = Profile.fromJSON(apiResponse.getResponseDataAsJson());
                startMainActivity(profile);
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.e(TAG, "Error fetching profile information " + error);
            }
        });
    }

    private void startMainActivity(Profile profile) {
//        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
//        startActivity(intent);
        Log.d(TAG, "Starting EventListActivity");
        startActivity(new Intent(this, EventListActivity.class));
    }
}