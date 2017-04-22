package com.icebreakers.nexxus.fragments;

import android.graphics.drawable.Drawable;

/**
 * Created by radhikak on 4/21/17.
 */

public class CheckedInEventListFragment extends BaseEventListFragment {

    @Override
    public int getTabPosition() {
        return 1;
    }

    @Override
    public Drawable getTabDrawable() {
        return null;
    }

    @Override
    public String getTabTitle() {
        return "Checked In";
    }

    public static CheckedInEventListFragment newInstance() {
        return new CheckedInEventListFragment();
    }
}
