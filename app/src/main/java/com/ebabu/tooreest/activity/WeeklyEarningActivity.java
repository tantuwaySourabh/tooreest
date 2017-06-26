package com.ebabu.tooreest.activity;

import android.content.Context;
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
import com.ebabu.tooreest.adapter.WeeklyEarningAdapter;
import com.ebabu.tooreest.beans.Earning;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WeeklyEarningActivity extends AppCompatActivity {

    private final static String TAG = WeeklyEarningActivity.class.getSimpleName();
    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvWeeklyEarning;
    private List<Earning> mListWeeklyEarning;
    private WeeklyEarningAdapter weeklyEarningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_earning);
        context = WeeklyEarningActivity.this;
        initView();
        Utils.setUpToolbar(context, "Weekly Earning");
    }

    private void initView() {
        rvWeeklyEarning = (RecyclerView) findViewById(R.id.rv_weekly_earning);

        mListWeeklyEarning = new ArrayList<>();
        weeklyEarningAdapter = new WeeklyEarningAdapter(context, mListWeeklyEarning);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvWeeklyEarning.setLayoutManager(layoutManager);
        rvWeeklyEarning.setAdapter(weeklyEarningAdapter);

        fetchWeeklyEarning();
    }

    private void fetchWeeklyEarning() {

        final MyLoading myLoading = new MyLoading(context);

        final JSONObject jsonObject = new JSONObject();

        new AQuery(context).post(IUrlConstants.GET_WEEKLY_EARNING, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchWeeklyEarning(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Earning>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Earning> tempListJobs = new Gson().fromJson(data.toString(), type);
                                if (tempListJobs.size() > 0) {
                                    mListWeeklyEarning.clear();
                                    mListWeeklyEarning.addAll(tempListJobs);
                                    weeklyEarningAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (mListWeeklyEarning.size() == 0) {
                                Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            }
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
