package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.ProfileFragment;
import com.icebreakers.nexxus.helpers.Router;
import com.icebreakers.nexxus.listeners.MessageClickEvent;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;


/**
 * Created by amodi on 4/5/17.
 */

public class ProfileActivity extends BaseActivity implements MessageClickEvent {

    public static final String PROFILE_EXTRA = "profile_extra";

    @Override
    public void onMessageClickEvent(Profile profile) {
        Router.startMessaginActivity(this, profile);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, ProfileFragment.newInstance(profile)).commit();
    }
}
