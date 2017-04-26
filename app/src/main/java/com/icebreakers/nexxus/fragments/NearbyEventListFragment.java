package com.icebreakers.nexxus.fragments;

import android.graphics.drawable.Drawable;

import com.icebreakers.nexxus.models.MeetupEvent;
import com.icebreakers.nexxus.persistence.Database;

import java.util.List;

/**
 * Created by amodi on 4/12/17.
 */

public class NearbyEventListFragment extends BaseEventListFragment {

    @Override
    public int getTabPosition() {
        return 0;
    }

    @Override
    public Drawable getTabDrawable() {
        return null;
    }

    @Override
    public String getTabTitle() {
        return "Nearby";
    }

    public static NearbyEventListFragment newInstance() {
        return new NearbyEventListFragment();
    }

    @Override
    public void update(List<MeetupEvent> newEvents) {
        if (events.isEmpty()) {
            MeetupEvent codePathEvent = MeetupEvent.getCodePathEvent();
            events.add(0, codePathEvent);
            Database.instance().saveMeetupEvent(codePathEvent);
        }

        super.update(newEvents);
    }
}
