package com.ebabu.tooreest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.BookingDetailsActivity;
import com.ebabu.tooreest.activity.BookingsActivity;
import com.ebabu.tooreest.beans.Booking;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final static String TAG = BookingAdapter.class.getSimpleName();
    private Context context;
    private List<Booking> listBookings;
    private String bookingType;
    private Resources resources;


    public BookingAdapter(Context context, List<Booking> listBookings, String bookingType) {
        this.context = context;
        this.listBookings = listBookings;
        this.bookingType = bookingType;
        this.resources = context.getResources();
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_customer_booking, parent, false);
        return new BookingViewHolder(view, bookingType, context);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Booking booking = listBookings.get(position);

        if (booking != null) {
            if (booking.getService_image() != null && booking.getService_image().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(holder.ivImage).image(booking.getService_image());
            } else {
                holder.ivImage.setImageResource(R.mipmap.default_icon);
            }

            if (IKeyConstants.USER_TYPE_CUSTOMER.equals(AppPreference.getInstance(context).getUserId())) {
                if (booking.getCompany_name() != null) {
                    holder.tvName.setText(booking.getCompany_name());
                } else {
                    holder.tvName.setText(IKeyConstants.NA);
                }
            } else {
                if (booking.getFull_name() != null) {
                    holder.tvName.setText(booking.getFull_name());
                } else {
                    holder.tvName.setText(IKeyConstants.NA);
                }
            }

            if (booking.getOrder_id() != null) {
                holder.tvBookingNum.setText(context.getString(R.string.booking_num_X, booking.getOrder_id()));
            } else {
                holder.tvBookingNum.setText(IKeyConstants.NA);
            }

            if (booking.getCoupon_code() != null && !booking.getCoupon_code().isEmpty()) {
                holder.ivCoupon.setVisibility(View.VISIBLE);
            } else {
                holder.ivCoupon.setVisibility(View.GONE);
            }

            if (booking.getSub_category() != null) {
                holder.tvService.setText(context.getString(R.string.service_name, booking.getSub_category()));
            } else {
                holder.tvService.setText(context.getString(R.string.service_name, IKeyConstants.NA));
            }

            if (booking.getService_type() != 0) {
                if (booking.getService_type() == 1) {
                    holder.ivServiceImg.setImageResource(R.mipmap.physical_guide);
                } else {
                    holder.ivServiceImg.setImageResource(R.mipmap.virtual_guide);
                }
                holder.tvServiceType.setText(Utils.ARRAY_SERVICE_TYPE[booking.getService_type() - 1]);
            } else {
                holder.tvServiceType.setText(IKeyConstants.NA);
            }

            holder.tvCharge.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + (booking.getService_amt() + Utils.getPercentAmount(booking.getService_amt())));


            if (booking.getBooking_date_time() != null) {
                try {
                    Date fullTimestamp = Utils.fullTimestamp24hours.parse(booking.getBooking_date_time());

                    holder.tvDate.setText(context.getString(R.string.date_value, Utils.displayDateFormat.format(fullTimestamp)));
                    //holder.tvTime.setText(Utils.displayTimeFormat.format(fullTimestamp).toUpperCase());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                holder.tvDate.setText(context.getString(R.string.date_value, IKeyConstants.NA));
                //holder.tvTime.setText(IKeyConstants.NA);
            }

            if (booking.getBooking_status() != null) {
                if (booking.getBooking_status().equalsIgnoreCase(IKeyConstants.BOOKING_STATUS_CANCELLED)) {
                    holder.tvStatus.setTextColor(resources.getColor(R.color.medium_red));
                } else if (booking.getBooking_status().equalsIgnoreCase(IKeyConstants.BOOKING_STATUS_ACCEPTED)) {
                    holder.tvStatus.setTextColor(resources.getColor(R.color.dark_parrot_green));
                } else if (booking.getBooking_status().equalsIgnoreCase(IKeyConstants.BOOKING_STATUS_COMPLETED)) {
                    holder.tvStatus.setTextColor(resources.getColor(R.color.dark_parrot_green));
                    if (booking.getReview() != null && !booking.getReview().isEmpty()) {
                        holder.layoutReview.setVisibility(View.VISIBLE);
                        holder.tvReview.setText(booking.getReview());
                        holder.ratingBar.setRating(booking.getRating());
                    } else {
                        holder.layoutReview.setVisibility(View.GONE);
                    }
                } else {
                    holder.tvStatus.setTextColor(resources.getColor(R.color.drawer_background));
                }
                holder.tvStatus.setText(booking.getBooking_status());
            } else {
                holder.tvStatus.setText(IKeyConstants.NA);
            }
        }

        setListenerOnButtons(holder, booking, position);
    }

    private void setListenerOnButtons(BookingViewHolder viewHolder, final Booking booking, final int position) {
        viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectDialog();
                /*openDialogToAcceptOrReject(position, booking, IKeyConstants.REJECT_BOOKING);*/
            }
        });

        viewHolder.layoutMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookingDetailsActivity.class);
                intent.putExtra(IKeyConstants.BOOKING, booking);
                context.startActivity(intent);
            }
        });

        if (AppPreference.getInstance(context).getUserType().equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
            viewHolder.btnReschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToReschedule(position, booking);
                }
            });

            viewHolder.btnRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (booking.getBooking_status().equalsIgnoreCase(IKeyConstants.BOOKING_STATUS_CANCELLED)) {
                        DialogUtils.openAlertToShowMessage(context.getString(R.string.cannot_repeat_booking), context);
                    } else {
                        openDialogToRepeat(position, booking);
                    }
                }
            });

            viewHolder.btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToRateService(position, booking);
                }
            });
        } else {
            viewHolder.btnCompleteJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToCompleteJob(position, booking);
                }
            });
//            if (AppPreference.getInstance(context).getUserType().equalsIgnoreCase(IKeyConstants.USER_TYPE_PROVIDER)) {
//                if (IKeyConstants.BOOKING_STATUS_BOOKED.equalsIgnoreCase(booking.getBooking_status()) || IKeyConstants.BOOKING_STATUS_RESCHEDULE.equalsIgnoreCase(booking.getBooking_status()) || IKeyConstants.BOOKING_STATUS_REPEAT.equalsIgnoreCase(booking.getBooking_status())) {
//                    viewHolder.btnAccept.setVisibility(View.VISIBLE);
//                } else {
//                    viewHolder.btnAccept.setVisibility(View.GONE);
//                }
//            }
            viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToAcceptOrReject(position, booking, IKeyConstants.ACCEPT_BOOKING);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listBookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        LinearLayout layoutMainItem, layoutReview;
        CustomTextView tvReview, btnReschedule, tvServiceType, btnReview, btnCompleteJob, btnAccept, btnRepeat, btnCancel, tvStatus, tvBookingNum, tvName, tvService, tvCharge, tvDate, tvTime;
        ImageView ivImage, ivServiceImg, ivCoupon;
        private int colorDarkYellow, colorStartGray;

        public BookingViewHolder(View itemView, String bookingType, Context context) {
            super(itemView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
            Resources resources = context.getResources();
            colorDarkYellow = resources.getColor(R.color.yellow);
            colorStartGray = resources.getColor(R.color.gray);

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);

            layoutReview = (LinearLayout) itemView.findViewById(R.id.layout_review);

            tvReview = (CustomTextView) itemView.findViewById(R.id.tv_review);
            layoutMainItem = (LinearLayout) itemView.findViewById(R.id.layout_main_item);
            btnReschedule = (CustomTextView) itemView.findViewById(R.id.btn_reschedule);
            btnReview = (CustomTextView) itemView.findViewById(R.id.btn_review);
            btnCancel = (CustomTextView) itemView.findViewById(R.id.btn_cancel);
            btnRepeat = (CustomTextView) itemView.findViewById(R.id.btn_repeat);
            btnCompleteJob = (CustomTextView) itemView.findViewById(R.id.btn_start_job);
            btnAccept = (CustomTextView) itemView.findViewById(R.id.btn_complete_job);
            tvStatus = (CustomTextView) itemView.findViewById(R.id.tv_status);
            tvBookingNum = (CustomTextView) itemView.findViewById(R.id.tv_booking_num);
            tvName = (CustomTextView) itemView.findViewById(R.id.tv_name);
            tvService = (CustomTextView) itemView.findViewById(R.id.tv_service);
            tvCharge = (CustomTextView) itemView.findViewById(R.id.tv_charges);
            tvDate = (CustomTextView) itemView.findViewById(R.id.tv_date);
            tvTime = (CustomTextView) itemView.findViewById(R.id.tv_time);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            ivCoupon = (ImageView) itemView.findViewById(R.id.iv_coupon);
            ivServiceImg = (ImageView) itemView.findViewById(R.id.iv_service_type);
            tvServiceType = (CustomTextView) itemView.findViewById(R.id.tv_service_type);

            if (AppPreference.getInstance(context).getUserType().equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
                btnCompleteJob.setVisibility(View.GONE);
                btnAccept.setVisibility(View.GONE);

                if (bookingType.equals(IKeyConstants.UPCOMING_BOOKINGS)) {
                    btnReschedule.setVisibility(View.GONE);
                    btnReview.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnRepeat.setVisibility(View.GONE);
                } else {
                    btnReschedule.setVisibility(View.GONE);
                    btnReview.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    btnRepeat.setVisibility(View.GONE);
                }
            } else {
                btnReschedule.setVisibility(View.GONE);
                btnReview.setVisibility(View.GONE);
                btnRepeat.setVisibility(View.GONE);

                if (bookingType.equals(IKeyConstants.UPCOMING_BOOKINGS)) {
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCompleteJob.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                } else if (bookingType.equals(IKeyConstants.CONFIRMED_BOOKINGS)) {
                    btnCancel.setVisibility(View.GONE);
                    btnCompleteJob.setVisibility(View.VISIBLE);
                    btnAccept.setVisibility(View.GONE);
                } else {
                    btnCancel.setVisibility(View.GONE);
                    btnCompleteJob.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                }
            }
        }
    }

    private void openDialogToReschedule(final int position, final Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_reschedule, null);
        builder.setView(view);

        final CustomTextView tvDate = (CustomTextView) view.findViewById(R.id.tv_date);
        final CustomTextView tvTime = (CustomTextView) view.findViewById(R.id.tv_time);

        tvDate.setText(Utils.displayDateFormat.format(System.currentTimeMillis()));
        tvTime.setText(Utils.displayTimeFormat.format(System.currentTimeMillis()));
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_edit_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openCalendarDialog(context, tvDate);
            }
        });

        view.findViewById(R.id.btn_edit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openTimerPickerDialog(context, tvTime);
            }
        });

        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timestamp = tvDate.getText().toString() + IKeyConstants.SPACE + tvTime.getText().toString();
                try {
                    timestamp = Utils.fullTimestamp24hours.format(Utils.completeDisplayDateFormat.parse(timestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                rescheduleService(position, booking, timestamp, alertDialog);
            }
        });
        alertDialog.show();
    }

    private void openDialogToRepeat(final int position, final Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_reschedule, null);
        builder.setView(view);

        final CustomTextView tvDate = (CustomTextView) view.findViewById(R.id.tv_date);
        final CustomTextView tvTime = (CustomTextView) view.findViewById(R.id.tv_time);
        final CustomEditText etComment = (CustomEditText) view.findViewById(R.id.et_reason);
        etComment.setHint(context.getString(R.string.describe_in_n_chars, 30));
        view.findViewById(R.id.layout_text_box).setVisibility(View.VISIBLE);
        tvDate.setText(Utils.displayDateFormat.format(System.currentTimeMillis()));
        tvTime.setText(Utils.displayTimeFormat.format(System.currentTimeMillis()));
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_edit_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openCalendarDialog(context, tvDate);
            }
        });

        view.findViewById(R.id.btn_edit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openTimerPickerDialog(context, tvTime);
            }
        });

        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timestamp = tvDate.getText().toString() + IKeyConstants.SPACE + tvTime.getText().toString();
                try {
                    timestamp = Utils.fullTimestamp24hours.format(Utils.completeDisplayDateFormat.parse(timestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (Utils.isNetworkConnected(context)) {
                    String comment = etComment.getText().toString().trim();
                    if (comment.isEmpty()) {
                        etComment.setError(context.getString(R.string.describe_in_n_chars, 30));
                    } else {
                        repeatService(position, booking, timestamp, comment, alertDialog);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.show();
    }

    private int ratingInt = 0;

    private void openDialogToRateService(final int position, final Booking booking) {
        Resources resources = context.getResources();
        int colorDarkYellow = resources.getColor(R.color.yellow);
        int colorStartGray = resources.getColor(R.color.gray);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout view = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_review, null);
        builder.setCancelable(true);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        final TextView tvRatingText = (TextView) view.findViewById(R.id.tv_rating_text);
        TextView btnSubmit = (TextView) view.findViewById(R.id.btn_submit);
        final EditText etFeedback = (EditText) view.findViewById(R.id.et_feedback);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar_default);
        ratingInt = (int) ratingBar.getRating();
        tvRatingText.setText(Utils.RATING_TEXT[ratingInt - 1]);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingInt = (int) rating;
                tvRatingText.setText(Utils.RATING_TEXT[ratingInt - 1]);
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context)) {
                    String review = etFeedback.getText().toString().trim();
                    if (review.isEmpty()) {
                        etFeedback.setError("Write some review about product");
                    } else {
                        postRatingReview(position, booking, ratingInt, review, alertDialog);
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    private void openDialogToCompleteJob(final int position, final Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_complete_booking, null);
        builder.setTitle(context.getString(R.string.complete_job));
        builder.setView(view);
        final CustomEditText etBookingCode = (CustomEditText) view.findViewById(R.id.et_booking_code);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_complete_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookingCode = etBookingCode.getText().toString();
                if (bookingCode.isEmpty()) {
                    etBookingCode.setError("Enter booking code");
                } else if (bookingCode.length() != 6) {
                    etBookingCode.setError("Booking code must be of 6 characters");
                } else {
                    completeJob(position, booking, alertDialog, bookingCode);
                }

            }
        });
        alertDialog.show();
    }

    private void completeJob(final int position, Booking booking, final AlertDialog alertDialog, String bookingCode) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("booking_id", booking.getOrder_id());
            jsonObject.put("booking_code", bookingCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "completeJob(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.COMPLETE_JOB, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "completeJob(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            alertDialog.dismiss();
                            ((BookingsActivity) context).refreshAllFragments();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void rescheduleService(final int position, final Booking booking, final String timestamp, final AlertDialog alertDialog) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("booking_id", booking.getOrder_id());
            jsonObject.put("reschedule_date", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "rescheduleService(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.RESCHEDULE_SERVICE, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "rescheduleService(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            booking.setBooking_date_time(timestamp);
                            notifyItemChanged(position);
                            alertDialog.dismiss();
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        } else {
                            DialogUtils.openAlertToShowMessage(json.getString(IKeyConstants.MESSAGE), context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }


    private void repeatService(final int position, final Booking booking, final String timestamp, String comment, final AlertDialog alertDialog) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("booking_id", booking.getOrder_id());
            jsonObject.put("repeat_date", timestamp);
            jsonObject.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "repeatService(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.REPEAT, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "repeatService(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            listBookings.remove(position);
                            notifyItemRemoved(position);
                            alertDialog.dismiss();
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        } else {
                            DialogUtils.openAlertToShowMessage(json.getString(IKeyConstants.MESSAGE), context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void postRatingReview(final int position, Booking booking, int rating, String review, final AlertDialog alertDialog) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("order_id", booking.getOrder_id());
            jsonObject.put("provider_id", booking.getProvider_id());
            jsonObject.put("sub_cat_id", booking.getSub_cat_id());
            jsonObject.put("rating", rating);
            jsonObject.put("review", review);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "postRatingReview(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.POST_REVIEW, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "postRatingReview(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            notifyItemChanged(position);
                            alertDialog.dismiss();
                            DialogUtils.openAlertToShowMessage(json.getString(IKeyConstants.MESSAGE), context);
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void openDialogToAcceptOrReject(final int position, final Booking booking, final int acceptOrReject) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (acceptOrReject == IKeyConstants.ACCEPT_BOOKING) {
            builder.setMessage("Are you sure you want to accept this booking?");
        } else {
            builder.setMessage("Are you sure you want to reject this booking?");
        }
        builder.setTitle(context.getString(R.string.confirmation));

        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                acceptOrRejectBooking(position, booking, acceptOrReject);
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

   private void rejectDialog() {
        final View commentLayout,radiogroup;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_reject_dialog, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        commentLayout = view.findViewById(R.id.comment_layout);
        radiogroup = view.findViewById(R.id.rg_reject);

        view.findViewById(R.id.rb3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentLayout.setVisibility(View.VISIBLE);
            }

        });

       view.findViewById(R.id.rb2).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               commentLayout.setVisibility(View.GONE);
           }

       });
       view.findViewById(R.id.rb1).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               commentLayout.setVisibility(View.GONE);
           }

       });

        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                }
        });
    }




    private void acceptOrRejectBooking(final int position, final Booking booking, final int acceptOrReject) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("order_id", booking.getOrder_id());
            jsonObject.put("ac_type", acceptOrReject + IKeyConstants.EMPTY);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "acceptOrRejectBooking(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.ACCEPT_REJECT_BOOKING, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "acceptOrRejectBooking(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
//                            listBookings.remove(position);
//                            notifyItemRemoved(position);
                            ((BookingsActivity) context).refreshAllFragments();
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        } else {
                            DialogUtils.openAlertToShowMessage(json.getString(IKeyConstants.MESSAGE), context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
