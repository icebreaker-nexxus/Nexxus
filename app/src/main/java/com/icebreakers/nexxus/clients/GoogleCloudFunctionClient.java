package com.icebreakers.nexxus.clients;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by amodi on 4/23/17.
 */

public class GoogleCloudFunctionClient {

    private static final String TAG = GoogleCloudFunctionClient.class.getSimpleName();

    // fix this to be less hacky if time permits
    public static void sendPushNotification(String fromName, String toid, String fromId, String body) {
        sendPush(String.format("https://us-central1-nexxus-42eaf.cloudfunctions.net/sendPushNotificationForMessageWithBody?fromName=%s&toId=%s&fromId=%s&body=%s", fromName, toid, fromId, body));
    }

    public static void sendEventCheckInNotification(String fromName, String fromId, String eventId) {
        String body = fromName + " who also studied at UCLA checked-in";
        sendPush(String.format("https://us-central1-nexxus-42eaf.cloudfunctions.net/sendPushNotificationForEventCheckIn?fromName=%s&toId=%s&fromId=%s&body=%s&eventId=%s", fromName, "global", fromId, body, eventId));
    }

    public static void sendPush(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url(url)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Call to cloud failed with error  " + e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d(TAG, "Call to cloud completed with response code " + response.code());
            }
        });
    }

}
