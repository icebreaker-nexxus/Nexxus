package com.icebreakers.nexxus.clients;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icebreakers.nexxus.BuildConfig;
import com.icebreakers.nexxus.NexxusApplication;
import com.icebreakers.nexxus.api.MeetupAPI;
import com.icebreakers.nexxus.models.MeetupEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by radhikak on 4/4/17.
 */

public class MeetupClient {

    private static final String TAG = NexxusApplication.BASE_TAG + MeetupClient.class.getName();

    public static final String BASE_URL = "https://api.meetup.com";

    public static final String API_KEY = BuildConfig.meetupApiKey;

    private static MeetupClient instance;
    private final MeetupAPI meetupAPI;

    private MeetupClient() {

        final Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        meetupAPI = retrofit.create(MeetupAPI.class);
    }

    public static MeetupClient getInstance() {
        if (instance == null) {
            instance = new MeetupClient();
        }
        return instance;
    }

    public Call<List<MeetupEvent>> findEvents(Double lat, Double lon) {
        Integer radius = 3; //miles

        return meetupAPI.findEvents(API_KEY, lat, lon, radius, true);
    }

    public Observable<List<MeetupEvent>> rxfindEvents(Double lat, Double lon) {
        Integer radius = 3; //miles

        return meetupAPI.rxFindEvents(API_KEY, lat, lon, radius, true);
    }
    
    private void findMeetupEvents() {

        Double lat = 37.3691027;
        Double lon = -121.9984824;

        // For debug
        Call<List<MeetupEvent>> eventsCall = MeetupClient.getInstance().findEvents(lat, lon);
        Log.d(TAG, "Meetup find events request: " + eventsCall.request().url().toString());

        CompositeSubscription compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(MeetupClient.getInstance()
                .rxfindEvents(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<MeetupEvent>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "received error", e);
                    }

                    @Override
                    public void onNext(List<MeetupEvent> meetupEvents) {
                        Log.d(TAG, "onNext events #" + meetupEvents.size());
                        for (MeetupEvent event: meetupEvents) {
                            Log.d(TAG, event.toString());
                        }
                    }
                })
        );

    }
}


