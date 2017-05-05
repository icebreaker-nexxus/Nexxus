package com.icebreakers.nexxus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;
import com.icebreakers.nexxus.R;
import com.icebreakers.nexxus.services.NexxusFirebaseMessagingService;

import static com.icebreakers.nexxus.services.NexxusFirebaseMessagingService.REPLY_ACTION;

/**
 * Created by amodi on 5/4/17.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String KEY_SENDER_PROFILE_ID = "KEY_SENDER_PROFILEID";
    private static final String KEY_RECEIVER_PROFILE_ID = "KEY_RECEIVER_PROFILEID";
    private static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";

    public static Intent getReplyMessageIntent(Context context, String receiverProfileId,
                                               String senderProfileId,
                                               int notificationId) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(KEY_SENDER_PROFILE_ID, senderProfileId);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_RECEIVER_PROFILE_ID, receiverProfileId);
        return intent;
    }
    public NotificationBroadcastReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = NexxusFirebaseMessagingService.getReplyMessage(intent);
            int receivedId = intent.getIntExtra(KEY_RECEIVER_PROFILE_ID, 0);

            Toast.makeText(context, "Message ID: " + receivedId + "\nMessage: " + message,
                           Toast.LENGTH_SHORT).show();

            // update notification
            int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
            updateNotification(context, notifyId);
        }
    }

    private void updateNotification(Context context, int notifyId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_nexxus)
            .setContentText(context.getString(R.string.notif_content_sent));

        notificationManager.notify(notifyId, builder.build());
    }

}
