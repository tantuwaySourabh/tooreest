package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
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

public class RequestQuoteActivity extends AppCompatActivity {

    private final static String TAG = RequestQuoteActivity.class.getSimpleName();
    private Context context;
    private CustomTextView tvName, tvEmail, tvMobile, btnSend;
    private CustomEditText etBudget, etDescription;
    private String budget, description, providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_qoute);
        context = RequestQuoteActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.request_for_quote));
    }

    private void initView() {
        Intent intent = getIntent();
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        tvEmail = (CustomTextView) findViewById(R.id.tv_email);
        tvMobile = (CustomTextView) findViewById(R.id.tv_phone_num);
        etBudget = (CustomEditText) findViewById(R.id.et_budget);
        etDescription = (CustomEditText) findViewById(R.id.et_description);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areFieldsValid()) {
                    postRequestQuote();
                }
            }
        });

        tvName.setText(AppPreference.getInstance(context).getFullName());
        tvEmail.setText(AppPreference.getInstance(context).getEmail());
        tvMobile.setText(AppPreference.getInstance(context).getMobileNum());
    }

    private boolean areFieldsValid() {
        budget = etBudget.getText().toString();
        description = etDescription.getText().toString();

        if (budget.isEmpty()) {
            etBudget.setError(getString(R.string.enter_your_budget));
            return false;
        }

        if (description.isEmpty()) {
            etDescription.setError(getString(R.string.enter_description));
            return false;
        }

        if (description.length() < 50) {
            etDescription.setError(getString(R.string.min_n_characters_needed, 50));
            return false;
        }

        return true;
    }

    private void postRequestQuote() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", AppPreference.getInstance(context).getFullName());
            jsonObject.put("email", AppPreference.getInstance(context).getEmail());
            jsonObject.put("mobile", AppPreference.getInstance(context).getMobileNum());
            jsonObject.put("budget", budget);
            jsonObject.put("description", description);
            jsonObject.put("provider_id", providerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "postRequestQuote(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.REQUEST_FOR_QUOTE, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "postRequestQuote(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            finish();
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
}
