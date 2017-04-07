package com.icebreakers.nexxus.models.internal;

import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * This is an internal Profile class
 * Used for GSON deserialization from LI API response data
 */

@Parcel
public class Profile {

    public String id;

    public String firstName;

    public String lastName;

    public String headline;

    public String publicProfileUrl;

    public String pictureUrl;

    public String emailAddress;

    @SerializedName("educations")
    public EducationInfo educationInfo;

    @SerializedName("threeCurrentPositions")
    public PositionInfo currentCompanyInfo;

    @SerializedName("threePastPositions")
    public PositionInfo pastCompanyInfo;

    public static Profile fromJSON(JSONObject jsonObject) {
        Profile profile = new Profile();
        try {
            profile.id = jsonObject.getString("id");
            profile.firstName = jsonObject.getString("firstName");
            profile.lastName = jsonObject.getString("lastName");
            profile.headline = jsonObject.getString("headline");
            profile.publicProfileUrl = jsonObject.getString("publicProfileUrl");
            profile.pictureUrl = jsonObject.getString("pictureUrl");
            profile.emailAddress = jsonObject.getString("emailAddress");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profile;
    }


    @Parcel
    public static class Education {
        public String schoolName;
        public com.icebreakers.nexxus.models.Profile.LIDate startDate;
        public com.icebreakers.nexxus.models.Profile.LIDate endDate;
    }

    @Parcel
    public static class EducationInfo {
        @SerializedName("values")
        public List<Education> educationList;
    }

    @Parcel
    public static class PositionInfo {
        @SerializedName("values")
        public List<Position> positionInfos;
    }

    @Parcel
    public static class Position {
        public boolean isCurrent;
        public String title;
        public com.icebreakers.nexxus.models.Profile.LIDate startDate;
        public com.icebreakers.nexxus.models.Profile.LIDate endDate;
        public Company company;
    }

    @Parcel
    public static class Company {
        public Long id;
        public String name;
    }


}
