package com.icebreakers.nexxus.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.icebreakers.nexxus.fragments.BaseEventListFragment;

import java.util.List;

/**
 * Created by radhikak on 3/29/17.
 */

public class EventFragmentPagerAdapater extends FragmentStatePagerAdapter {

    protected List<BaseEventListFragment> fragments;
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<BaseEventListFragment> registeredFragments = new SparseArray<BaseEventListFragment>();

    public interface EventPageTab {
        int getTabPosition();
        Drawable getTabDrawable();
        String getTabTitle();
    }

    public EventFragmentPagerAdapater(FragmentManager fm, List<BaseEventListFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseEventListFragment fragment = (BaseEventListFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public BaseEventListFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        BaseEventListFragment fragment = fragments.get(position);
        return fragment.getTabTitle();
    }
}
