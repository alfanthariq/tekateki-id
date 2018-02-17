package com.alfanthariq.tts.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.alfanthariq.tts.fragment.BottomNavFragment;
import java.util.ArrayList;

/**
 * Created by alfanthariq on 12/01/2018.
 */

public class BottomNavAdapter extends FragmentPagerAdapter {

    private ArrayList<BottomNavFragment> fragments = new ArrayList<>();
    private BottomNavFragment currentFragment;

    public BottomNavAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(BottomNavFragment.newInstance(0));
        fragments.add(BottomNavFragment.newInstance(1));
        fragments.add(BottomNavFragment.newInstance(2));
        fragments.add(BottomNavFragment.newInstance(3));
        fragments.add(BottomNavFragment.newInstance(4));
    }

    @Override
    public BottomNavFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((BottomNavFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public BottomNavFragment getCurrentFragment() {
        return currentFragment;
    }
}
