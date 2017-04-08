package com.icebreakers.nexxus.api;

import com.icebreakers.nexxus.models.MeetupEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by radhikak on 4/4/17.
 */

public interface MeetupAPI {

    @GET("/find/events")
    Call<List<MeetupEvent>> findEvents(@Query("key") String key,
                                       @Query("lat") Double lat,
                                       @Query("lon") Double lon,
                                       @Query("radius") Integer radius,
                                       @Query("sign") Boolean sign);

    @GET("/find/events")
    Observable<List<MeetupEvent>> rxFindEvents(@Query("key") String key,
                                               @Query("lat") Double lat,
                                               @Query("lon") Double lon,
                                               @Query("radius") Integer radius,
                                               @Query("fields") String fields,
                                               @Query("sign") Boolean sign);
}
