package com.icebreakers.nexxus.persistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.icebreakers.nexxus.models.Event;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Message;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.models.messaging.MessageRef;

/**
 * Created by amodi on 4/6/17.
 */

public class Database {

    private static Database instance;
    public DatabaseReference databaseReference;

    public static final String PROFILE_TABLE = "Profiles";
    public static final String MESSAGES_TABLE = "Messages";

    public static final String EVENT_TABLE = "Events";
    public static final String MEETUP_EVENT_TABLE = "MeetupEvent";

    private Database() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static Database instance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void saveProfile(Profile profile) {
        databaseReference.child(PROFILE_TABLE).child(profile.id).setValue(profile);
    }

    public DatabaseReference messagesTableReference() {
        return databaseReference.child(MESSAGES_TABLE);
    }

    public void saveMessage(String messageRowId, Message message) {
        messagesTableReference().child(messageRowId).push().setValue(message);
    }

    public void saveMeetupEvent(MeetupEvent event) {
        databaseReference.child(MEETUP_EVENT_TABLE).child(event.getId()).setValue(event);
    }
  
    public void saveEvent(Event event) {
        databaseReference.child(EVENT_TABLE).child(event.id).setValue(event);

    }
}
