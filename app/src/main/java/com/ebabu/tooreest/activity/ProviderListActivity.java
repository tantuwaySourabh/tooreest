package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.ProviderAdapter;
import com.ebabu.tooreest.beans.Provider;
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

public class ProviderListActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ProviderListActivity.class.getSimpleName();
    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvServices;
    private List<Provider> mListProviders;
    private ProviderAdapter providerAdapter;
    private FloatingActionButton fabAddService;
    public String createAt = "0", subcatId, subcatName;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        context = ProviderListActivity.this;
        initView();
        Utils.setUpToolbar(context, subcatName);
    }

    private void initView() {
        Intent intent = getIntent();
        listType = intent.getIntExtra(IKeyConstants.PROVIDER_LIST_TYPE, 0);
        subcatId = intent.getStringExtra(IKeyConstants.SUBCAT_ID);
        subcatName = intent.getStringExtra(IKeyConstants.SUBCAT_NAME);
        rvServices = (RecyclerView) findViewById(R.id.rv_services);
        fabAddService = (FloatingActionButton) findViewById(R.id.btn_add_service);
        fabAddService.setOnClickListener(this);
        fabAddService.setVisibility(View.GONE);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mListProviders = new ArrayList<>();
        providerAdapter = new ProviderAdapter(context, mListProviders, listType);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(providerAdapter);

        rvServices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                            fetchProviderList();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        fetchProviderList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_service:
                onAddServiceBtnClicked();
                break;
        }
    }

    private void onAddServiceBtnClicked() {
        Intent intent = new Intent(context, AddNewServiceActivity.class);
        startActivity(intent);
    }

    private void fetchProviderList() {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        String serviceUrl = null;
        try {
            jsonObject.put("create_at", createAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (listType == IKeyConstants.PROVIDER_FAVORITES) {
            serviceUrl = IUrlConstants.FAVORITE_PROVIDER_LIST;
        } else if (listType == IKeyConstants.PROVIDER_FEATURED) {
            serviceUrl = IUrlConstants.FEATURED_PROVIDERS_LIST;
        } else {
            try {
                jsonObject.put("sub_category", subcatId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            serviceUrl = IUrlConstants.PROVIDER_LIST;
        }
        Log.d(TAG, "fetchProviderList(): jsonObject=" + jsonObject);
        new AQuery(context).post(serviceUrl, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchProviderList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Provider>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Provider> tempListProviders = new Gson().fromJson(data.toString(), type);
                                if (tempListProviders.size() > 0) {
                                    int previousSize = mListProviders.size();
                                    mListProviders.addAll(previousSize, tempListProviders);
                                    providerAdapter.notifyItemRangeInserted(previousSize, tempListProviders.size());
                                    createAt = tempListProviders.get(tempListProviders.size() - 1).getCreate_at();
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
