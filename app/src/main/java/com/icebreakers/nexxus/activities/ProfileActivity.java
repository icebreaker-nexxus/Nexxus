package com.icebreakers.nexxus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.ProfileFragment;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by amodi on 4/5/17.
 */

public class ProfileActivity extends BaseActivity {

    public static final String PROFILE_EXTRA = "profile_extra";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, ProfileFragment.newInstance(profile)).commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
