package com.icebreakers.nexxus.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Message;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.icebreakers.nexxus.persistence.Database.PROFILE_TABLE;

/**
 * Created by radhikak on 4/11/17.
 */

public class ProfileHolder {

    private static final String TAG = NexxusApplication.BASE_TAG + ProfileHolder.class.getSimpleName();

    private static AccessToken accessToken = null;
    private static String profileId;
    private static LISession session;

    private static Profile profile = null;

    private static ProfileHolder instance = null;

    public interface OnProfileReadyCallback {
        public void onSuccess(Profile profile);
        public void onError(LIApiError error);
    }

    private static final String PROFILE_ID_RK = "-GKTP4lCqZ";
    private static final String PROFILE_ID_AM = "4qHi9-qdlA";
    private static final String PROFILE_ID_SV = "usFWMyY-h7";

    private static List<Profile> allProfiles = new ArrayList<>();
    private static HashMap<String, Profile> profilesMap = new HashMap<>();

    private static List<Message> allMessages = new ArrayList<>();

    private OnProfileReadyCallback callback = null;

    private ValueEventListener currentProfileListener = new ValueEventListener() {
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
                EventBus.getDefault().post(profile);
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

    private ValueEventListener allProfilesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            allProfiles.clear();

            for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                Profile profile = dataSnapshotChild.getValue(Profile.class);
                allProfiles.add(profile);
                profilesMap.put(profile.id, profile);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "allProfilesListener: Cannot find object in the database " + databaseError);
        }
    };

    private ChildEventListener incomingMessageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Message message = dataSnapshot.getValue(Message.class);
            EventBus.getDefault().postSticky(message);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // do nothing
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // do nothing
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // do nothing
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // do nothing
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

        fetchAllProfiles(allProfilesListener);
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

        if (profileId != null) {
            fetchProfileFromDb(currentProfileListener);
        } else {
            fetchProfileFromServer();
        }
    }


    // USE only when profiel database schema changes
    public void forceFetchFromDB(final OnProfileReadyCallback listener) {
        callback = listener;
        fetchProfileFromDb(currentProfileListener);
    }
    public void saveAceessToken(Context context) {
        LISessionManager sessionManager = LISessionManager.getInstance(context);
        session = sessionManager.getSession();
        accessToken = session.getAccessToken();
        NexxusSharePreferences.putLIAccessToken(context, accessToken);
    }

    public Profile getProfile() {
        return profile;
    }

    public Profile getProfile(String id) {
        return profilesMap.get(id);
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

    // ideally this should have a callback as an argument
    public List<Profile> getAllProfiles() {
        return allProfiles;
    }

    public List<Profile> getAttendees(MeetupEvent event) {
        ArrayList<Profile> attendees = new ArrayList<>();
        attendees.addAll(allProfiles);

        // remove current user if not checked-in
        if (!isUserCheckedIn(event.getEventRef())) {
            Log.d(TAG, "user has not checked in");
            for (Profile attendee : allProfiles) {
                if (attendee.id.equals(profile.id)) {
                    attendees.remove(attendee);
                }
            }
        }

        return attendees;
    }

    public void setMessagesListener() {

        // TODO set this only for active profiles
        // also set Incomimg message listener
        String messagesRowId = MessagesHelper.getMessageRowId(profile.id, PROFILE_ID_SV);
        setupIncomingMessageListener(messagesRowId);
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

    private void fetchAllProfiles(ValueEventListener listener) {
        Database.instance().databaseReference.child(Database.PROFILE_TABLE).addValueEventListener(listener);
    }

    private void setupIncomingMessageListener(String messagesRowId) {
        Database.instance().messagesTableReference().child(messagesRowId).addChildEventListener(incomingMessageListener);
    }

    public static void logout() {
        instance = null;
        accessToken = null;
        session = null;
    }

}
