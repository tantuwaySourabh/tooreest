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
import com.ebabu.tooreest.adapter.BidsAdapter;
import com.ebabu.tooreest.beans.Bid;
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

public class BidsListActivity extends AppCompatActivity {

    private final static String TAG = BidsListActivity.class.getSimpleName();
    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvBids;
    private List<Bid> mListBids;
    private BidsAdapter bidAdapter;
    private String createAt = "0";
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    //private Spinner spinnerCategories, spinnerSubcategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bids_list);
        context = BidsListActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.invite_for_job));
        if (Utils.isNetworkConnected(context)) {
            fetchBidsList();
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
//        spinnerCategories = (Spinner) findViewById(R.id.spinner_category);
//        spinnerSubcategories = (Spinner) findViewById(R.id.spinner_subcategory);
        rvBids = (RecyclerView) findViewById(R.id.rv_bids);

        mListBids = new ArrayList<>();

        bidAdapter = new BidsAdapter(context, mListBids);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvBids.setLayoutManager(layoutManager);
        rvBids.setAdapter(bidAdapter);
        rvBids.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchBidsList();
                        }
                    }
                }
            }

        });
//        final HashMap<String, ArrayList<Subcategory>> mapSubcategories = Utils.getServiceCategories(context);
//        ArrayList<String> listCategories = new ArrayList<>();
//        listCategories.addAll(mapSubcategories.keySet());
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_2, listCategories);
//        spinnerCategories.setAdapter(arrayAdapter);
//
//        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//
//                String selectedCat = (String) adapterView.getItemAtPosition(pos);
//                ArrayList<Subcategory> listSubcategories = mapSubcategories.get(selectedCat);
//                ArrayAdapter<Subcategory> arrayAdapter = new ArrayAdapter<Subcategory>(context, R.layout.spinner_item_2, listSubcategories);
//                spinnerSubcategories.setAdapter(arrayAdapter);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        spinnerSubcategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void fetchBidsList() {

        final MyLoading myLoading = new MyLoading(context);
        if (mListBids.size() == 0) {
            myLoading.show(getString(R.string.please_wait));
        }

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("create_at", createAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading = true;
        Log.d(TAG, "fetchBidsList(): jsonObject=" + jsonObject);
        new AQuery(context).post(IUrlConstants.BIDDING_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchBidsList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Bid>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Bid> tempListJobs = new Gson().fromJson(data.toString(), type);
                                if (tempListJobs.size() > 0) {
                                    int previousSize = mListBids.size();
                                    mListBids.addAll(previousSize, tempListJobs);
                                    bidAdapter.notifyItemRangeInserted(previousSize, tempListJobs.size());
                                    createAt = tempListJobs.get(tempListJobs.size() - 1).getCreate_at();
                                }
                            }
                        } else {
                            if (mListBids.size() == 0) {
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
