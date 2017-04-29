package com.icebreakers.nexxus.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Message;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.internal.MeetupEventRef;
import com.icebreakers.nexxus.models.messaging.MessageRef;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
        void onSuccess(Profile profile);
        void onError(LIApiError error);
    }

    private static List<Profile> allProfiles = new ArrayList<>();

    private static HashMap<String, Profile> profilesMap = new HashMap<>();

    private static Set<String> messagesRowIds = new HashSet<>();

    private OnProfileReadyCallback callback = null;

    private ValueEventListener currentProfileListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Profile currentProfile = dataSnapshot.getValue(Profile.class);
            if (currentProfile == null) {
                Log.e(TAG, "Cannot find profile for profileId " + dataSnapshot.getKey());
                // fetch from server
                fetchProfileFromServer();
            } else {
                Log.d(TAG, "Profile fetched successfully " + currentProfile.firstName);
                profile = currentProfile;

                // post updated profile
                EventBus.getDefault().postSticky(profile);

                Log.d(TAG, "Registering for push notification " + profile.id);
                FirebaseMessaging.getInstance().subscribeToTopic(profile.id);
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
            Log.d(TAG, "incomingMessageListener: onChildAdded " + dataSnapshot.getKey());
            messagesRowIds.add(dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "incomingMessageListener: onChildChanged " + dataSnapshot.getKey());

            for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                Message message = dataSnapshotChild.getValue(Message.class);
                handleIncomingMessage(dataSnapshot.getKey(), message);
                break;
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

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
        setupIncomingMessageListener(incomingMessageListener);
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

    private void fetchAllProfiles(ValueEventListener listener) {
        Database.instance().databaseReference.child(Database.PROFILE_TABLE).addValueEventListener(listener);
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

    public void checkIn(Context context, MeetupEventRef eventRef) {
        profile.addMeetupEventRef(eventRef);
        Database.instance().saveProfile(profile);
        NexxusSharePreferences.putLoggedInMemberProfile(context, profile);
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

    // USE only when profile  schema changes
    public void forceFetchFromDB(final OnProfileReadyCallback listener) {
        callback = listener;
        fetchProfileFromDb(currentProfileListener);
    }

    public void saveMessage(String messagesRowId, Message message) {
        // save message reference in profile
        saveMessageRef(messagesRowId, message.receiverId);
        Database.instance().saveMessage(messagesRowId, message);
    }

    private void saveMessageRef(String messagesRowId, String receiverId) {
        // save only for new conversation
        if (!profile.messageRefHashMap.containsKey(messagesRowId)) {
            profile.messageRefHashMap.put(messagesRowId, new MessageRef(messagesRowId, receiverId));
            profile.messageIds.add(messagesRowId);
            Database.instance().saveProfile(profile);
        }
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
                Log.d(TAG, "Registering for push notification " + profileId);
                FirebaseMessaging.getInstance().subscribeToTopic(profileId);

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

    private void setupIncomingMessageListener(ChildEventListener messageListener) {
        Database.instance().messagesTableReference().addChildEventListener(messageListener);
    }


    private void handleIncomingMessage(String key, Message message) {
        StringTokenizer tokenizer = new StringTokenizer(key, "?");
        String id1 = tokenizer.nextToken();
        String id2 = tokenizer.nextToken();

        Log.d(TAG, "incomingMessageListener: onChildChanged id 1 " + id1);
        Log.d(TAG, "incomingMessageListener: onChildChanged id 2 " + id2);

        Profile otherProfile = null;
        if (id1.equals(profileId)) {
            otherProfile = profilesMap.get(id2);
        } else if (id2.equals(profileId)) {
            otherProfile = profilesMap.get(id1);
        }
        if (otherProfile != null && message.receiverId.equals(profile.id)) {
            Log.d(TAG, "Received incoming message from " + otherProfile.firstName);

            // save this reference in profile
            saveMessageRef(key, otherProfile.id);

            // TODO make sure senderId and receiverId logic is correct
            // notify registered listeners
            // EventBus.getDefault().postSticky(otherProfile.id);
        }
    }

    public static void logout() {
        instance = null;
        accessToken = null;
        session = null;
    }

}
