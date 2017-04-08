package com.icebreakers.nexxus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amodi on 4/7/17.
 */

public class FakeEvent {
    public String id;
    public String name;

    public List<Profile> attendees;

    public FakeEvent() {
        attendees = new ArrayList<>();
    }

}
