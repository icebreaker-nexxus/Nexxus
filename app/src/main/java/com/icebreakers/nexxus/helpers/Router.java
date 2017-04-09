package com.icebreakers.nexxus.helpers;

import android.content.Intent;
import android.util.Log;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.activities.BaseActivity;
import com.icebreakers.nexxus.activities.EventListActivity;
import com.icebreakers.nexxus.activities.ProfileActivity;
import com.icebreakers.nexxus.activities.ProfileListActivity;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/9/17.
 */

public class Router {

    private static final String TAG = NexxusApplication.BASE_TAG + Router.class.getSimpleName();

    public static void startEventListActivity(BaseActivity activity, Profile profile) {
        Log.d(TAG, "Starting EventListActivity");
        Intent intent = new Intent(activity, EventListActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        activity.startActivity(intent);
    }

    public static void startProfileActivity(BaseActivity activity, Profile profile) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        activity.startActivity(intent);
    }

    public static void startProfileListActivity(BaseActivity activity, Profile profile) {
        Intent intent = new Intent(activity, ProfileListActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        activity.startActivity(intent);
    }
}
