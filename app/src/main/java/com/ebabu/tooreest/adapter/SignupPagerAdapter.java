package com.ebabu.tooreest.adapter;

/**
 * Created by hp on 18/02/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ebabu.tooreest.fragment.CustomerSignUpFragment;
import com.ebabu.tooreest.fragment.ProviderSignUpFragment;


public class SignupPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private CustomerSignUpFragment customerSignUpFragment;
    private ProviderSignUpFragment providerSignUpFragment;

    public SignupPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                customerSignUpFragment = CustomerSignUpFragment.getInstance();
                return customerSignUpFragment;

            case 1:
                providerSignUpFragment = ProviderSignUpFragment.getInstance();
                return providerSignUpFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public CustomerSignUpFragment getCustomerSignUpFragment() {
        return customerSignUpFragment;
    }

    public ProviderSignUpFragment getProviderSignUpFragment() {
        return providerSignUpFragment;
    }
}
