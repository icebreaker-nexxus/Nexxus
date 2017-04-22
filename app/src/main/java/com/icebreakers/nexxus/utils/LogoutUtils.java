package com.icebreakers.nexxus.utils;

import android.content.Intent;
import com.icebreakers.nexxus.activities.BaseActivity;
import com.icebreakers.nexxus.activities.LoginActivity;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.LISessionManager;

/**
 * Created by amodi on 4/14/17.
 */

public class LogoutUtils {

    public static void logout(BaseActivity activity) {
        LISessionManager.getInstance(activity).clearSession();
        NexxusSharePreferences.clearSharedPreferences(activity);
        activity.startActivity(new Intent(activity, LoginActivity.class));
        ProfileHolder.logout();
        activity.finish();
    }
}
