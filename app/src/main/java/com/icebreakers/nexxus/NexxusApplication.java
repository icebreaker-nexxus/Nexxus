package com.icebreakers.nexxus;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.persistence.Database;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by radhikak on 4/6/17.
 */

public class NexxusApplication extends Application {

    public static final String BASE_TAG = "NX: ";

    private static Profile profile = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                          .setDefaultFontPath("fonts/SourceSansPro-Regular.ttf")
                                          .setFontAttrId(R.attr.fontPath)
                                          .build()
        );
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Database.instance().databaseReference.keepSynced(true);
    }

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile currentProfile) {
        profile = currentProfile;
    }
}
