package com.ebabu.tooreest.adapter;

/**
 * Created by hp on 18/02/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.fragment.BookingFragment;


public class BookingPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String userType;
    private BookingFragment upcomingFragment, confirmedFragment, pastFragment;

    public BookingPagerAdapter(FragmentManager fm, int NumOfTabs, String userType) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.userType = userType;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                upcomingFragment = BookingFragment.getInstance(IKeyConstants.UPCOMING_BOOKINGS);
                return upcomingFragment;

            case 1:
                confirmedFragment = BookingFragment.getInstance(IKeyConstants.CONFIRMED_BOOKINGS);
                return confirmedFragment;

            case 2:
                pastFragment = BookingFragment.getInstance(IKeyConstants.PAST_BOOKINGS);
                return pastFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public BookingFragment getUpcomingFragment() {
        return upcomingFragment;
    }

    public BookingFragment getConfirmedFragment() {
        return confirmedFragment;
    }

    public BookingFragment getPastFragment() {
        return pastFragment;
    }
}
