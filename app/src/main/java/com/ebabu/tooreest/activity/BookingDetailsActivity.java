package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Booking;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;

import java.text.ParseException;
import java.util.Date;

public class BookingDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = BookingDetailsActivity.class.getSimpleName();
    private Context context;
    private CustomTextView btnChat, tvEmail, tvPhoneNum, tvDiscount, tvUsername, tvDescription, tvServiceType, tvTotalCharge, tvStatus, tvBookingNum, tvName, tvService, tvDate;
    private ImageView ivImage, ivProfilePic;
    private Booking booking;
    private int totalAmount;
    private final static int REQUEST_CODE_PAY_NOW = 1;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        context = BookingDetailsActivity.this;
        initView();
        setDataInView();
        initToolbar();
    }

    private void initView() {
        Intent intent = getIntent();
        booking = intent.getParcelableExtra(IKeyConstants.BOOKING);
        Log.d(TAG, "booking=" + booking);
        btnChat = (CustomTextView) findViewById(R.id.btn_chat);
        tvEmail = (CustomTextView) findViewById(R.id.tv_email);
        tvDiscount = (CustomTextView) findViewById(R.id.tv_discount);
        tvPhoneNum = (CustomTextView) findViewById(R.id.tv_phone_num);
        tvStatus = (CustomTextView) findViewById(R.id.tv_status);
        tvBookingNum = (CustomTextView) findViewById(R.id.tv_booking_num);
        tvTotalCharge = (CustomTextView) findViewById(R.id.tv_charges);
        tvServiceType = (CustomTextView) findViewById(R.id.tv_service_type);
        tvDescription = (CustomTextView) findViewById(R.id.tv_description);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        tvUsername = (CustomTextView) findViewById(R.id.tv_user_name);
        tvService = (CustomTextView) findViewById(R.id.tv_service);
        tvDate = (CustomTextView) findViewById(R.id.tv_date);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        btnChat.setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar actionBarToolbar = Utils.setUpToolbar(context, getString(R.string.booking_details));
        actionBarToolbar.inflateMenu(R.menu.menu_home_customer);
        actionBarToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {

                    case R.id.action_chat:
                        intent = new Intent(context, ConversationActivity.class);
                        intent.putExtra(ConversationUIService.USER_ID, booking.getEmail());
                        intent.putExtra(ConversationUIService.DISPLAY_NAME, booking.getFull_name());
                        intent.putExtra(ConversationUIService.TAKE_ORDER, true);
                        //startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    private void setDataInView() {
        if (booking != null) {
            if (booking.getService_image() != null && booking.getService_image().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(ivImage).image(booking.getService_image());
            } else {
                ivImage.setImageResource(R.mipmap.default_icon);
            }

            if (booking.getImage() != null && booking.getImage().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(ivProfilePic).image(booking.getImage());
            } else {
                ivProfilePic.setImageResource(R.mipmap.profile);
            }

            if (booking.getFull_name() != null) {
                tvName.setText(booking.getFull_name());
                tvUsername.setText(booking.getFull_name());
            } else {
                tvName.setText(IKeyConstants.NA);
                tvUsername.setText(IKeyConstants.NA);
            }

            if (booking.getOrder_id() != null) {
                tvBookingNum.setText(context.getString(R.string.booking_num_X, booking.getOrder_id()));
            } else {
                tvBookingNum.setText(IKeyConstants.NA);
            }

            if (booking.getSub_category() != null) {
                tvService.setText(context.getString(R.string.service_name, booking.getSub_category()));
            } else {
                tvService.setText(context.getString(R.string.service_name, IKeyConstants.NA));
            }

            if (booking.getBooking_status() != null) {
                tvStatus.setText(booking.getBooking_status());
            } else {
                tvStatus.setText(IKeyConstants.NA);
            }

            if (booking.getService_description() != null) {
                tvDescription.setText(booking.getService_description());
            } else {
                tvDescription.setText(IKeyConstants.NA);
            }

            if (booking.getService_type() != 0) {
                tvServiceType.setText(Utils.ARRAY_SERVICE_TYPE[booking.getService_type() - 1]);
                if (booking.getService_type() == IKeyConstants.SERVICE_TYPE_PHYSICAL) {
                    if (IKeyConstants.BOOKING_STATUS_ACCEPTED.equalsIgnoreCase(booking.getBooking_status())) {
                        findViewById(R.id.layout_contact_details).setVisibility(View.VISIBLE);
                        tvEmail.setText(booking.getEmail());
                        tvPhoneNum.setText(IKeyConstants.PLUS_SIGN + booking.getMobile_no().replaceAll(IKeyConstants.SPACE, IKeyConstants.EMPTY));
                    }
                    btnChat.setVisibility(View.GONE);

                } else {
                    if (IKeyConstants.BOOKING_STATUS_ACCEPTED.equalsIgnoreCase(booking.getBooking_status())) {
                        btnChat.setVisibility(View.VISIBLE);
                    }
                    findViewById(R.id.layout_contact_details).setVisibility(View.GONE);
                }
            } else {
                tvServiceType.setText(IKeyConstants.NA);
            }


            totalAmount = booking.getService_amt() + Utils.getPercentAmount(booking.getService_amt());
            if (booking.getTotal_amount() > 0) {
                if (booking.getCoupon_code() != null && !booking.getCoupon_code().isEmpty()) {
                    tvDiscount.setVisibility(View.VISIBLE);
                    tvDiscount.setText("You got " + IKeyConstants.USD + IKeyConstants.SPACE + (totalAmount - booking.getTotal_amount()) + " discount by applying coupon code " + booking.getCoupon_code());
                }
                tvTotalCharge.setText(IKeyConstants.USD + IKeyConstants.SPACE + (booking.getTotal_amount()));
            } else {
                tvTotalCharge.setText(IKeyConstants.FREE);
            }

            if (booking.getBooking_date_time() != null) {
                try {
                    Date fullTimestamp = Utils.fullTimestamp24hours.parse(booking.getBooking_date_time());
                    tvDate.setText(Utils.displayDateFormat.format(fullTimestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                tvDate.setText(IKeyConstants.NA);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chat:
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationUIService.USER_ID, booking.getEmail());
                intent.putExtra(ConversationUIService.DISPLAY_NAME, booking.getFull_name());
                intent.putExtra(ConversationUIService.TAKE_ORDER, true);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //getMenuInflater().inflate(R.menu.menu_booking_details, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PAY_NOW) {
                finish();
            }
        }

    }
}
