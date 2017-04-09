package com.icebreakers.nexxus.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by amodi on 4/9/17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
