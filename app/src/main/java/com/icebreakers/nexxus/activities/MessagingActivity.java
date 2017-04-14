package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.MessagingFragment;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/12/17.
 */

public class MessagingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, MessagingFragment.newInstance(profile)).commit();
    }
}
