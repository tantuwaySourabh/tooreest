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
import com.ebabu.tooreest.adapter.TransactionAdapter;
import com.ebabu.tooreest.beans.TransactionHistory;
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

public class TransactionActivity extends AppCompatActivity {

    private final static String TAG = TransactionActivity.class.getSimpleName();
    private Context context;
    private RecyclerView rvTransaction;
    private LinearLayoutManager layoutManager;
    private List<TransactionHistory> mListTransaction;
    private TransactionAdapter adapterTransaction;
    private String createAt = "0";
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasaction);
        context = TransactionActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.transactions));
        if (Utils.isNetworkConnected(context)) {
            fetchTransactionsList();
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        rvTransaction = (RecyclerView) findViewById(R.id.rv_transaction);

        mListTransaction = new ArrayList<>();
        adapterTransaction = new TransactionAdapter(context, mListTransaction);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvTransaction.setLayoutManager(layoutManager);
        rvTransaction.setAdapter(adapterTransaction);

        rvTransaction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchTransactionsList();
                        }
                    }
                }
            }


        });
    }

    private void fetchTransactionsList() {

        final MyLoading myLoading = new MyLoading(context);
        if (mListTransaction.size() == 0) {
            myLoading.show(getString(R.string.please_wait));
        }

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("create_at", createAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading = true;
        Log.d(TAG, "fetchTransactionsList(): jsonObject=" + jsonObject);
        new AQuery(context).post(IUrlConstants.TRANSACTION_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchTransactionsList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<TransactionHistory>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<TransactionHistory> tempListNotifications = new Gson().fromJson(data.toString(), type);
                                if (tempListNotifications.size() > 0) {
                                    int previousSize = mListTransaction.size();
                                    mListTransaction.addAll(previousSize, tempListNotifications);
                                    adapterTransaction.notifyItemRangeInserted(previousSize, tempListNotifications.size());
                                    createAt = tempListNotifications.get(tempListNotifications.size() - 1).getCreate_at();
                                }
                            }
                        } else {
                            if (mListTransaction.size() == 0) {
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