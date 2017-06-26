package com.ebabu.tooreest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.BookingPagerAdapter;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.Utils;

public class BookingsActivity extends AppCompatActivity {

    private Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BookingPagerAdapter pagerAdapter;
    private int currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bookings);
        context = BookingsActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.bookings));
        initTabLayout();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    }

    private void initTabLayout() {
        currentFragment = 0;
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("OPEN"));
        tabLayout.addTab(tabLayout.newTab().setText("CONFIRMED"));
        tabLayout.addTab(tabLayout.newTab().setText("COMPLETED"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new BookingPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), IKeyConstants.USER_TYPE_CUSTOMER);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setHovered(true);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragment = tab.getPosition();
                viewPager.setCurrentItem(currentFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void refreshAllFragments() {
        pagerAdapter.getUpcomingFragment().refreshList();
        pagerAdapter.getConfirmedFragment().refreshList();
        pagerAdapter.getPastFragment().refreshList();
    }

}
