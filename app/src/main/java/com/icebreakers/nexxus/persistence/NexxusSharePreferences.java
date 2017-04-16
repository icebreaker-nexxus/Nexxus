package com.icebreakers.nexxus.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.linkedin.platform.AccessToken;

/**
 * Created by amodi on 4/4/17.
 */

public class NexxusSharePreferences {

    private static final String NAME = "Nexxus";
    private static final String ACCESS_TOKEN_LI = "access_token_li";
    private static final String PROFILE_ID = "profile_id";

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

    public static void clearSharedPreferences(Context context) {
        getSharedPrefernces(context).edit().clear().commit();
    }
}
