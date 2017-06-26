package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.ReviewsFeedbackAdapter;
import com.ebabu.tooreest.beans.ReviewsFeedback;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
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

public class ReviewFeedbackActivity extends AppCompatActivity {
    private final static String TAG = ReviewFeedbackActivity.class.getSimpleName();
    private Context context;
    private RecyclerView rvReviewsFeedback;
    private LinearLayoutManager reviewsLayoutManager;
    private ReviewsFeedbackAdapter reviewsAdapter;
    private List<ReviewsFeedback> reviewsList;
    private String providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_feedback);
        context = ReviewFeedbackActivity.this;
        Utils.setUpToolbar(context, getString(R.string.review_and_feedback));
        initView();
        fetchOffersList();
    }

    private void initView() {
        Intent intent = getIntent();
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER);
        rvReviewsFeedback = (RecyclerView) findViewById(R.id.rv_review_feedback);
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsFeedbackAdapter(context, reviewsList);
        reviewsLayoutManager = new LinearLayoutManager(context);
        reviewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvReviewsFeedback.setLayoutManager(reviewsLayoutManager);
        rvReviewsFeedback.setAdapter(reviewsAdapter);
    }

    public void fetchOffersList() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            if (providerId == null) {
                jsonObject.put("provider_id", "0");
            } else {
                jsonObject.put("provider_id", providerId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "fetchDocumentList(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.REVIEW_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchDocumentList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<ReviewsFeedback>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                reviewsList.clear();
                                ArrayList<ReviewsFeedback> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    reviewsList.addAll(listSubcategories);
                                    reviewsAdapter.notifyDataSetChanged();
                                }
                            }
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
