package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.ProfileFragment;
import com.icebreakers.nexxus.models.Profile;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by amodi on 4/5/17.
 */

public class ProfileActivity extends AppCompatActivity {

    public static final String PROFILE_EXTRA = "profile_extra";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));
        getSupportActionBar().setTitle(profile.firstName + " " + profile.lastName);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, ProfileFragment.newInstance(profile)).commit();
    }
}
