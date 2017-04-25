package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static int SPLASH_TIME_OUT = 500;

    @BindView(R.id.logo) ImageView logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

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
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    startOnboardingActivity();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void startOnboardingActivity() {
        Animation zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        Animation zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out);

        logo.setAnimation(zoomin);
        logo.setAnimation(zoomout);
        logo.startAnimation(zoomout);
        zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.startAnimation(zoomin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
                // close this activity
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}