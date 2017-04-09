package com.icebreakers.nexxus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.ProfileListFragment;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.icebreakers.nexxus.MainActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/8/17.
 */

public class ProfileListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.attendees));

        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.profileListContainer, ProfileListFragment.newInstance(profile)).commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
