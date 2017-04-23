package com.icebreakers.nexxus.fragments;

import android.graphics.drawable.Drawable;

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
}
