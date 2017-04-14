package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.MessagingFragment;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/12/17.
 */

public class MessagingActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Profile profile = Parcels.unwrap(intent.getParcelableExtra(PROFILE_EXTRA));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(String.format(getString(R.string.full_name), profile.firstName, profile.lastName));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, MessagingFragment.newInstance(profile)).commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
