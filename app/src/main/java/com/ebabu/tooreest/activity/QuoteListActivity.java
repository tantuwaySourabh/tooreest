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
import com.ebabu.tooreest.adapter.QuoteAdapter;
import com.ebabu.tooreest.beans.Quote;
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

public class QuoteListActivity extends AppCompatActivity {
    private final static String TAG = QuoteListActivity.class.getSimpleName();
    private Context context;
    private RecyclerView rvQuotes;
    private LinearLayoutManager layoutManager;
    private QuoteAdapter quoteAdapter;
    private List<Quote> listQuotes;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String createAt = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_list);
        context = QuoteListActivity.this;
        Utils.setUpToolbar(context, getString(R.string.quote_requests));
        initView();
        fetchQuoteList();
    }

    private void initView() {
        rvQuotes = (RecyclerView) findViewById(R.id.rv_quote);
        listQuotes = new ArrayList<>();
        quoteAdapter = new QuoteAdapter(context, listQuotes);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvQuotes.setLayoutManager(layoutManager);
        rvQuotes.setAdapter(quoteAdapter);
        addPaginationInRecyclerView();
    }

    private void addPaginationInRecyclerView() {

        rvQuotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchQuoteList();
                        }
                    }
                }
            }
        });
    }

    public void fetchQuoteList() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("create_at", createAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "fetchQuoteList(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.QUOTE_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchQuoteList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Quote>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                int previousSize = listQuotes.size();
                                ArrayList<Quote> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listQuotes.addAll(previousSize, listSubcategories);
                                    quoteAdapter.notifyDataSetChanged();
                                    createAt = listSubcategories.get(listSubcategories.size() - 1).getCreate_at();
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
