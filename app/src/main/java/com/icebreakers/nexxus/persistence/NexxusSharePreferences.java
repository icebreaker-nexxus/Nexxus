package com.icebreakers.nexxus.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.icebreakers.nexxus.models.Profile;
import com.linkedin.platform.AccessToken;

/**
 * Created by amodi on 4/4/17.
 */

public class NexxusSharePreferences {

    private static final String NAME = "Nexxus";
    private static final String ACCESS_TOKEN_LI = "access_token_li";
    private static final String PROFILE_ID = "profile_id";
    private static final String LOGGEDIN_PROFILE = "logged_in_profile";

    public static void putLIAccessToken(Context context, AccessToken accessToken) {
        Gson gson = new Gson();
        String accessTokenJson = gson.toJson(accessToken);
        getSharedPrefernces(context).edit().putString(ACCESS_TOKEN_LI, accessTokenJson).apply();
    }

    @Nullable
    public static AccessToken getLIAccessToken(Context context) {
        Gson gson = new Gson();
        String accessTokenString = getSharedPrefernces(context).getString(ACCESS_TOKEN_LI, null);
        if (accessTokenString == null) {
            return null;
        } else {
            return gson.fromJson(accessTokenString, AccessToken.class);
        }
    }

    public static SharedPreferences getSharedPrefernces(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    @Nullable
    public static String getProfileId(Context context) {
        return getSharedPrefernces(context).getString(PROFILE_ID, null);
    }

    public static void putProfileId(Context context, String profileId) {
        getSharedPrefernces(context).edit().putString(PROFILE_ID, profileId).apply();
    }

    public static void putLoggedInMemberProfile(Context context, Profile profile) {
        Gson gson = new Gson();
        String profileGson = gson.toJson(profile);
        getSharedPrefernces(context).edit().putString(LOGGEDIN_PROFILE, profileGson).apply();
    }

    public static Profile getLoggedInMemberProfile(Context context) {
        Gson gson = new Gson();
        String profileString = getSharedPrefernces(context).getString(LOGGEDIN_PROFILE, null);
        if (profileString == null) {
            return null;
        } else {
            return gson.fromJson(profileString, Profile.class);
        }
    }
}
