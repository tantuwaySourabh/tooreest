package com.ebabu.tooreest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class AddOfferActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = AddOfferActivity.class.getSimpleName();
    private Context context;
    private CustomEditText etTitle, etDescription, etAmount;
    private CustomTextView tvStartDate, tvEndDate;
    private String title, description, amount, startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);
        context = AddOfferActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.add_offer));
    }

    private void initView() {
        etTitle = (CustomEditText) findViewById(R.id.et_title);
        etDescription = (CustomEditText) findViewById(R.id.et_description);
        etAmount = (CustomEditText) findViewById(R.id.et_amount);
        tvStartDate = (CustomTextView) findViewById(R.id.tv_start_date);
        tvEndDate = (CustomTextView) findViewById(R.id.tv_end_date);

        findViewById(R.id.btn_start_date).setOnClickListener(this);
        findViewById(R.id.btn_end_date).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_date:
                Utils.openCalendarDialog(context, tvStartDate, Utils.dateDashFormat);
                break;

            case R.id.btn_end_date:
                Utils.openCalendarDialog(context, tvEndDate, Utils.dateDashFormat);
                break;

            case R.id.btn_save:
                if (areFieldsValid()) {
                    addOffer();
                }
                break;
        }
    }

    private boolean areFieldsValid() {
        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
        amount = etAmount.getText().toString();
        startDate = tvStartDate.getText().toString();
        endDate = tvEndDate.getText().toString();

        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.enter_title));
            return false;
        }

        if (description.isEmpty()) {
            etDescription.setError(getString(R.string.enter_description));
            return false;
        }

        if (amount.isEmpty()) {
            etAmount.setError(getString(R.string.enter_amount));
            return false;
        }

        if (startDate.isEmpty()) {
            Toast.makeText(context, getString(R.string.select_start_date), Toast.LENGTH_SHORT).show();
            return false;
        }

        startDate = startDate + " 12:00:00 AM";
        if (endDate.isEmpty()) {
            Toast.makeText(context, getString(R.string.select_end_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        endDate = endDate + " 11:59:00 PM";

        try {
            long startTimestamp = Utils.completeTimestampFormat.parse(startDate).getTime();
            long endTimestamp = Utils.completeTimestampFormat.parse(endDate).getTime();

            if (startTimestamp > endTimestamp) {
                Toast.makeText(context, getString(R.string.start_date_is_lower), Toast.LENGTH_SHORT).show();
                return false;
            }

            startDate = Utils.fullTimestamp24hours.format(startTimestamp);
            endDate = Utils.fullTimestamp24hours.format(endTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void addOffer() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("description", description);
            jsonObject.put("amount", amount);
            jsonObject.put("start_date", startDate);
            jsonObject.put("end_date", endDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "addOffer(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.ADD_OFFER, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "addOffer(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
