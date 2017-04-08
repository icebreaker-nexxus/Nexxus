package com.icebreakers.nexxus;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by radhikak on 4/6/17.
 */

public class NexxusApplication extends Application {

    public static final String BASE_TAG = "NX: ";

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                          .setDefaultFontPath("fonts/SourceSansPro-Regular.ttf")
                                          .setFontAttrId(R.attr.fontPath)
                                          .build()
        );
    }
}
