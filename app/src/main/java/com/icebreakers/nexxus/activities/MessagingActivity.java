package com.icebreakers.nexxus.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.MessagingFragment;

/**
 * Created by amodi on 4/12/17.
 */

public class MessagingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new MessagingFragment()).commit();
    }
}
