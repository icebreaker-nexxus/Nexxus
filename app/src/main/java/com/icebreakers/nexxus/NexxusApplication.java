package com.icebreakers.nexxus;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.icebreakers.nexxus.clients.LinkedInClient;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.persistence.Database;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by radhikak on 4/6/17.
 */

public class NexxusApplication extends Application {

    public static final String BASE_TAG = "NX: ";

    private static final String TAG = BASE_TAG + NexxusApplication.class.getName();

    private static NexxusApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Inside app onCreate()");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                          .setDefaultFontPath("fonts/SourceSansPro-Regular.ttf")
                                          .setFontAttrId(R.attr.fontPath)
                                          .build()
        );
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Database.instance().databaseReference.keepSynced(true);

        instance = this;

        ProfileHolder.getInstance(getApplicationContext());
    }

    public static LinkedInClient getLinkedInClient()
    {
        if (NexxusApplication.instance == null) {
            Log.e(TAG, "Instance is NULL!!!");
        }
        return new LinkedInClient(NexxusApplication.instance.getApplicationContext());
    }
}
