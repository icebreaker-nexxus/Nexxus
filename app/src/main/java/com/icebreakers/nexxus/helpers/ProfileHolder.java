package com.icebreakers.nexxus.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.internal.MeetupEventRef;
import com.icebreakers.nexxus.persistence.Database;
import com.icebreakers.nexxus.persistence.NexxusSharePreferences;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import static com.icebreakers.nexxus.persistence.Database.PROFILE_TABLE;

/**
 * Created by radhikak on 4/11/17.
 */

public class ProfileHolder {

    private static final String TAG = NexxusApplication.BASE_TAG + ProfileHolder.class.getName();

    private static AccessToken accessToken = null;
    private static String profileId;
    private static LISession session;

    private static Profile profile = null;

    private static ProfileHolder instance = null;

    public interface OnProfileReadyCallback {
        public void onSuccess(Profile profile);
        public void onError(LIApiError error);
    }

    private OnProfileReadyCallback callback = null;

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Profile currentProfile = dataSnapshot.getValue(com.icebreakers.nexxus.models.Profile.class);
            if (currentProfile == null) {
                Log.e(TAG, "Cannot find profile for profileId " + dataSnapshot.getKey());
                // fetch from server
                fetchProfileFromServer();
            } else {
                Log.d(TAG, "Profile fetched successfully " + currentProfile.firstName);
                profile = currentProfile;
                if (callback != null) {
                    callback.onSuccess(profile);
                    callback = null;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Cannot find object in the database " + databaseError);
        }
    };

    public static ProfileHolder getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "Creating new ProfileHolder instance");
            instance = new ProfileHolder(context);
        }

        return instance;
    }

    private ProfileHolder(Context context) {
        accessToken = NexxusSharePreferences.getLIAccessToken(context);
        LISessionManager.getInstance(context).init(accessToken);
        session = LISessionManager.getInstance(context).getSession();
        profileId = NexxusSharePreferences.getProfileId(context);
    }

    public boolean hasUserLoggedIn() {
        return session != null && session.isValid() && profileId != null;
    }

    public void fetchProfle(final OnProfileReadyCallback listener) {
        if (profile != null && listener != null) {
            listener.onSuccess(profile);
            return;
        }

        if (listener != null) callback = listener;

        fetchProfileFromDb(valueEventListener);
    }

    public Profile getProfile() {
        return profile;
    }

    public void checkIn(MeetupEventRef eventRef) {
        profile.addMeetupEventRef(eventRef);
        Database.instance().saveProfile(profile);
    }

    public boolean isUserCheckedIn(MeetupEventRef eventRef) {
        for (MeetupEventRef meetupEventRef : profile.meetupEventRefs) {
            if (eventRef.getEventId().equals(meetupEventRef.getEventId())) return true;
        }

        return false;
    }


    private void fetchProfileFromDb(final ValueEventListener listener) {
        Database.instance().databaseReference.child(PROFILE_TABLE).child(profileId)
                .addValueEventListener(listener);
    }

    private void fetchProfileFromServer() {
        Log.d(TAG, "Fetching profile from server");
        NexxusApplication.getLinkedInClient().fetchFullProfileInformation(new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Gson gson = new GsonBuilder().create();
                com.icebreakers.nexxus.models.internal.Profile internalProfile = gson.fromJson(apiResponse.getResponseDataAsString(), com.icebreakers.nexxus.models.internal.Profile.class);
                profile = com.icebreakers.nexxus.models.Profile.convertFromInternalProfile(internalProfile);

                profileId = profile.id;
                NexxusSharePreferences.putProfileId(NexxusApplication.getInstance().getApplicationContext(), profile.id);

                Database.instance().saveProfile(profile);

                Log.d(TAG, "Profile fetched successfully");

                if (callback != null) {
                    callback.onSuccess(profile);
                }
            }

            @Override
            public void onApiError(LIApiError error) {
                Log.e(TAG, "Error fetching profile information " + error);

                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }

}
