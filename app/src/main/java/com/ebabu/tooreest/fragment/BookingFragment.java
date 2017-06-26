package com.ebabu.tooreest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.BookingsActivity;
import com.ebabu.tooreest.adapter.BookingAdapter;
import com.ebabu.tooreest.beans.Booking;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class BookingFragment extends Fragment {

    private final static String TAG = BookingFragment.class.getSimpleName();
    private BookingsActivity activity;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvBookings;
    private List<Booking> listBookings;
    private BookingAdapter bookingAdapter;
    private String createAt = "0", orderType;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private CustomTextView tvMessage;

    public static BookingFragment getInstance(String orderType) {
        BookingFragment fragment = new BookingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IKeyConstants.ORDER_TYPE, orderType);
        fragment.setArguments(bundle);
        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_bookings, null);
        initView(view);
        fetchBookingsHistory();
        return view;
    }

    private void initView(View view) {
        tvMessage = (CustomTextView) view.findViewById(R.id.tv_message);
        rvBookings = (RecyclerView) view.findViewById(R.id.rv_bookings);
        listBookings = new ArrayList<>();
        bookingAdapter = new BookingAdapter(activity, listBookings, orderType);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvBookings.setLayoutManager(layoutManager);
        rvBookings.setAdapter(bookingAdapter);

        rvBookings.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchBookingsHistory();
                        }
                    }
                }
            }


        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BookingsActivity) getActivity();
        Bundle bundle = getArguments();
        orderType = bundle.getString(IKeyConstants.ORDER_TYPE);
    }

    public void fetchBookingsHistory() {

        if (!Utils.isNetworkConnected(activity)) {
            Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(activity);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_type", AppPreference.getInstance(activity).getUserType());
            jsonObject.put("order_type", orderType);
            jsonObject.put("create_at", createAt);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "fetchBookingsHistory(): jsonObject=" + jsonObject);
        loading = true;

        new AQuery(activity).post(IUrlConstants.BOOKING_HISTORY, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchBookingsHistory(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Booking>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                int previousSize = listBookings.size();
                                ArrayList<Booking> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listBookings.addAll(previousSize, listSubcategories);
                                    bookingAdapter.notifyDataSetChanged();
                                    createAt = listSubcategories.get(listSubcategories.size() - 1).getCreate_at();
                                }
                            }

                        } else {
                            //Toast.makeText(activity, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(activity, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(activity, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(activity, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
                loading = false;
                setViewOnListSize();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(activity).getSecretKey()));
    }

    private void setViewOnListSize(){
        if(listBookings.size()>0){
            rvBookings.setVisibility(View.VISIBLE);
            tvMessage.setVisibility(View.GONE);
        }else {
            rvBookings.setVisibility(View.GONE);
            tvMessage.setVisibility(View.VISIBLE);
        }
    }

    public void refreshList() {
        listBookings.clear();
        bookingAdapter.notifyDataSetChanged();
        createAt = "0";
        fetchBookingsHistory();
    }
}
