package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.linkedin.platform.errors.LIApiError;

/**
 * Created by amodi on 4/4/17.
 */

public class SplashActivity extends BaseActivity {
    private static final String TAG = NexxusApplication.BASE_TAG + SplashActivity.class.getSimpleName();
    // splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProfileHolder profileHolder = ProfileHolder.getInstance(this);

        if (profileHolder.hasUserLoggedIn()) {
            profileHolder.fetchProfle(new ProfileHolder.OnProfileReadyCallback() {
                @Override
                public void onSuccess(com.icebreakers.nexxus.models.Profile profile) {
                    Log.d(TAG, "Calling EventListActivity");
                    startActivity(new Intent(SplashActivity.this, EventListActivity.class));
                    finish();
                }

                @Override
                public void onError(LIApiError error) {

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
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}