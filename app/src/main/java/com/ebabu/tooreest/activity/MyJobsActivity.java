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
import com.ebabu.tooreest.adapter.JobsAdapter;
import com.ebabu.tooreest.beans.Job;
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

public class MyJobsActivity extends AppCompatActivity {

    private final static String TAG = MyJobsActivity.class.getSimpleName();
    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvJobs;
    private List<Job> mListJobs;
    private JobsAdapter jobsAdapter;
    private String createAt = "0";
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        context = MyJobsActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.my_jobs));
        if (Utils.isNetworkConnected(context)) {
            fetchJobsList();
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        rvJobs = (RecyclerView) findViewById(R.id.rv_jobs);

        mListJobs = new ArrayList<>();
        jobsAdapter = new JobsAdapter(context, mListJobs);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvJobs.setLayoutManager(layoutManager);
        rvJobs.setAdapter(jobsAdapter);

        rvJobs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchJobsList();
                        }
                    }
                }
            }

        });
    }

    private void fetchJobsList() {

        final MyLoading myLoading = new MyLoading(context);
        if (mListJobs.size() == 0) {
            myLoading.show(getString(R.string.please_wait));
        }

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("create_at", createAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading = true;
        Log.d(TAG, "fetchJobsList(): jsonObject=" + jsonObject);
        new AQuery(context).post(IUrlConstants.JOB_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchJobsList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Job>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Job> tempListJobs = new Gson().fromJson(data.toString(), type);
                                if (tempListJobs.size() > 0) {
                                    int previousSize = mListJobs.size();
                                    mListJobs.addAll(previousSize, tempListJobs);
                                    jobsAdapter.notifyItemRangeInserted(previousSize, tempListJobs.size());
                                    createAt = tempListJobs.get(tempListJobs.size() - 1).getCreate_at();
                                }
                            }
                        } else {
                            if (mListJobs.size() == 0) {
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
                loading = false;
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
