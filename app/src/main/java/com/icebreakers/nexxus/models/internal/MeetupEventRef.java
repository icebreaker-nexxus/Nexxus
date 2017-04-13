package com.icebreakers.nexxus.models.internal;

import org.parceler.Parcel;

/**
 * Created by radhikak on 4/12/17.
 */

@Parcel
public class MeetupEventRef {

    private String eventId;

    private Long groupId;

    private String groupUrlname;

    public MeetupEventRef(String eventId, Long groupId, String groupUrlname) {
        this.eventId = eventId;
        this.groupId = groupId;
        this.groupUrlname = groupUrlname;
    }

    public MeetupEventRef() {}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupUrlname() {
        return groupUrlname;
    }

    public void setGroupUrlname(String groupUrlname) {
        this.groupUrlname = groupUrlname;
    }
}
