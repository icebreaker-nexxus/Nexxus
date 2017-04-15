package com.icebreakers.nexxus.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by amodi on 4/7/17.
 */

@Parcel
public class Event {
    public String id;
    public String name;

    public List<Profile> attendees;

    public Event() {}

    public Event(String eventId, String eventName, List<Profile> attendeeList) {
        id = eventId;
        name = eventName;
        attendees = attendeeList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Profile> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Profile> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(Profile profile) {
        attendees.add(profile);
    }
}
