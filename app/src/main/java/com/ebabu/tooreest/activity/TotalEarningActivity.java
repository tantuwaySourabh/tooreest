package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;

public class TotalEarningActivity extends AppCompatActivity {

    private Context context;
    private CustomTextView tvTotalEarning, tvTotalBookings, tvBookingsCompleted, tvAcceptedBookings, tvNewBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_earning);
        context = TotalEarningActivity.this;
        initView();
        Utils.setUpToolbar(context, "Total Stats");
    }

    private void initView() {
        tvTotalEarning = (CustomTextView) findViewById(R.id.tv_total_earning);
        tvTotalBookings = (CustomTextView) findViewById(R.id.tv_total_booking);
        tvBookingsCompleted = (CustomTextView) findViewById(R.id.tv_completed_booking);
        tvAcceptedBookings = (CustomTextView) findViewById(R.id.tv_accepted_booking);
        tvNewBookings = (CustomTextView) findViewById(R.id.tv_new_booking);

        Intent intent = getIntent();
        tvTotalEarning.setText(intent.getIntExtra(IKeyConstants.KEY_TOTAL_EARNING, 0) + IKeyConstants.EMPTY);
        tvTotalBookings.setText(intent.getIntExtra(IKeyConstants.KEY_TOTAL_BOOKINGS, 0) + IKeyConstants.EMPTY);
        tvBookingsCompleted.setText(intent.getIntExtra(IKeyConstants.KEY_BOOKINGS_COMPLETED, 0) + IKeyConstants.EMPTY);
        tvAcceptedBookings.setText(intent.getIntExtra(IKeyConstants.KEY_ACCEPTED_BOOKINGS, 0) + IKeyConstants.EMPTY);
        tvNewBookings.setText(intent.getIntExtra(IKeyConstants.KEY_NEW_BOOKINGS, 0) + IKeyConstants.EMPTY);
    }
}
