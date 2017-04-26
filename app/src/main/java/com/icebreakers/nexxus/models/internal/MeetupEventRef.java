package com.icebreakers.nexxus.models.internal;

import org.parceler.Parcel;

/**
 * Created by radhikak on 4/12/17.
 */

@Parcel
public class MeetupEventRef {

    private String eventId;

    public MeetupEventRef(String eventId) {
        this.eventId = eventId;
    }

    public MeetupEventRef() {}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
