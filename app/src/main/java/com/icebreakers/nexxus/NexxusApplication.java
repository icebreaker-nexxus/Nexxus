package com.icebreakers.nexxus;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.icebreakers.nexxus.persistence.Database;

/**
 * Created by radhikak on 4/6/17.
 */

public class NexxusApplication extends Application {

    public static final String BASE_TAG = "NX: ";

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Database.instance().databaseReference.keepSynced(true);
    }
}
