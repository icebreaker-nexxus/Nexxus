package com.icebreakers.nexxus.utils;

import android.content.Context;
import android.content.Intent;
import com.icebreakers.nexxus.activities.BaseActivity;
import com.icebreakers.nexxus.activities.LoginActivity;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.LISessionManager;

/**
 * Created by amodi on 4/14/17.
 */

public class LogoutUtils {

    public static void logout(BaseActivity activity, Context context) {
        LISessionManager.getInstance(context).clearSession();
        NexxusSharePreferences.clearSharedPreferences(context);
        context.startActivity(new Intent(activity, LoginActivity.class));
    }
}
