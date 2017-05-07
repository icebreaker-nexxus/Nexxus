package com.icebreakers.nexxus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.fragments.MessagesFragment;

/**
 * Created by amodi on 5/6/17.
 */

public class MessagesActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        Intent intent = getIntent();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentContainer, new MessagesFragment()).commit();
    }
}
