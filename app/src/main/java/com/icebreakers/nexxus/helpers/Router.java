package com.icebreakers.nexxus.helpers;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.activities.BaseActivity;
import com.icebreakers.nexxus.activities.MessagingActivity;
import com.icebreakers.nexxus.activities.ProfileActivity;
import com.icebreakers.nexxus.models.Profile;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;

/**
 * Created by amodi on 4/9/17.
 */

public class Router {

    private static final String TAG = NexxusApplication.BASE_TAG + Router.class.getSimpleName();

    public static void startProfileActivity(BaseActivity activity, Profile profile, Pair<View, String> p1) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        if (p1 != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1);
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }

    }

    public static void startMessaginActivity(BaseActivity baseActivity, Profile profile) {
        Intent intent = new Intent(baseActivity, MessagingActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        baseActivity.startActivity(intent);
    }
}
