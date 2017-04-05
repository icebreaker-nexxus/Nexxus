package com.icebreakers.nexxus.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by amodi on 4/4/17.
 */

@Parcel
public class Profile {

    public String id;

    public String firstName;

    public String lastName;

    public String headline;

    public String publicProfileUrl;

    public String pictureUrl;

    public static Profile fromJSON(JSONObject jsonObject) {
        Profile profile = new Profile();
        try {
            profile.id = jsonObject.getString("id");
            profile.firstName = jsonObject.getString("firstName");
            profile.lastName = jsonObject.getString("lastName");
            profile.headline = jsonObject.getString("headline");
            profile.publicProfileUrl = jsonObject.getString("publicProfileUrl");
            profile.pictureUrl = jsonObject.getString("pictureUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;
    }
}
