package com.icebreakers.nexxus.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.activities.EventDetailsActivity;
import com.icebreakers.nexxus.activities.MessagingActivity;
import com.icebreakers.nexxus.helpers.ProfileHolder;
import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.models.Profile;
import com.icebreakers.nexxus.persistence.Database;
import org.parceler.Parcels;

import static com.icebreakers.nexxus.activities.EventDetailsActivity.EVENT_EXTRA;
import static com.icebreakers.nexxus.activities.ProfileActivity.PROFILE_EXTRA;
import static com.icebreakers.nexxus.persistence.Database.PROFILE_TABLE;

/**
 * Created by amodi on 4/23/17.
 */

public class NexxusFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = NexxusFirebaseMessagingService.class.getSimpleName();
    private static final String MESSAGE_TYPE = "message";
    private static final String CHECKIN_TYPE = "checkin";
    private static final String KEY_REPLY = "reply";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Received message " + remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }


        // Check if message contains a notification payload.

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String fromId = remoteMessage.getData().get("fromId");
            String toId = remoteMessage.getData().get("toId");
            String type = remoteMessage.getData().get("type");

            // make sure logged in user profile is present
            Profile profile = ProfileHolder.getInstance(getApplicationContext()).getProfile();
            if (profile == null) {
                // messed up, return;
                return;
            }
            if (!profile.id.equals(toId) && !toId.equals("global")) {
                // not my notification
                return;
            }

            if (profile.id.equals(fromId)) {
                // not my notification
                return;
            }


            if (MESSAGE_TYPE.equalsIgnoreCase(type)) {
                Database.instance().databaseReference.child(PROFILE_TABLE).child(fromId)
                                                     .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profileToMessage = dataSnapshot.getValue(Profile.class);
                        if (profileToMessage == null) {
                            return;
                        }
                        Log.d(TAG, "Got the profile for push notification...");
                        sendMessageNotification(remoteMessage.getNotification().getBody(),
                                                remoteMessage.getNotification().getTitle(),
                                                profileToMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            if (CHECKIN_TYPE.equalsIgnoreCase(type)) {
                String eventId = remoteMessage.getData().get("eventId");
                // hard coding to get
                sendEventNotification(remoteMessage.getNotification().getBody(),
                                      remoteMessage.getNotification().getTitle(),
                                      MeetupEvent.getCodePathEvent());

//                Database.instance().databaseReference.child(MEETUP_EVENT_TABLE).child(eventId)
//                     .addListenerForSingleValueEvent(new ValueEventListener() {
//                         @Override
//                         public void onDataChange(DataSnapshot dataSnapshot) {
//                             MeetupEvent meetupEvent = dataSnapshot.getValue(MeetupEvent.class);
//                             if (meetupEvent == null) {
//                                 return;
//                             }
//                             Log.d(TAG, "Got the meetupEvent for push notification...");
//
//                         }
//
//                         @Override
//                         public void onCancelled(DatabaseError databaseError) {
//
//                         }
//                                                     });
            }
        }
    }

    private void sendMessageNotification(String messageBody, String title, Profile profile) {
        String replyLabel = getString(R.string.reply);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
            .setLabel(replyLabel)
            .build();
        Intent intent = new Intent(NexxusFirebaseMessagingService.this, MessagingActivity.class);
        intent.putExtra(PROFILE_EXTRA, Parcels.wrap(profile));
        sendNotification(intent, title, messageBody);
    }

    private void sendEventNotification(String messageBody, String title, MeetupEvent meetupEvent) {
        Intent intent = new Intent(NexxusFirebaseMessagingService.this, EventDetailsActivity.class);
        intent.putExtra(EVENT_EXTRA, Parcels.wrap(meetupEvent));
        sendNotification(intent, title, messageBody);
    }

    private void sendNotification(Intent intent, String title, String messageBody) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,

                                                                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setContentTitle(title != null ? title : "New Notification")
            .setSmallIcon(R.drawable.ic_nexxus)
            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
