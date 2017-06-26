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
import com.ebabu.tooreest.adapter.ServiceAdapter;
import com.ebabu.tooreest.beans.Service;
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

public class MyServicesActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = MyServicesActivity.class.getSimpleName();
    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvServices;
    private List<Service> listServices;
    private ServiceAdapter serviceAdapter;
    private FloatingActionButton fabAddService;
    private String providerId;
    public final static int REQUEST_CODE_ADD_SERVICE = 1;
    private boolean selectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        context = MyServicesActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.my_services));
    }

    private void initView() {
        Intent intent = getIntent();
        selectService = intent.getBooleanExtra(IKeyConstants.TO_SELECT_SERVICE, false);
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER);
        rvServices = (RecyclerView) findViewById(R.id.rv_services);
        fabAddService = (FloatingActionButton) findViewById(R.id.btn_add_service);

        initRecyclerView();
        fetchServiceList();
    }

    private void initRecyclerView() {
        listServices = new ArrayList<>();

        if (providerId == null) {
            serviceAdapter = new ServiceAdapter(context, listServices, true, selectService);
            fabAddService.setOnClickListener(this);
            rvServices.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fabAddService.isShown())
                        fabAddService.hide();
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fabAddService.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            fabAddService.setVisibility(View.VISIBLE);
        } else {
            serviceAdapter = new ServiceAdapter(context, listServices, false, selectService);
            fabAddService.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(serviceAdapter);
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
        //intent.putParcelableArrayListExtra(IKeyConstants.SERVICE, (ArrayList<? extends Parcelable>) listServices);
        startActivityForResult(intent, REQUEST_CODE_ADD_SERVICE);
    }

    public void fetchServiceList() {

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

        new AQuery(context).post(IUrlConstants.GET_SERVICES, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchDocumentList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Service>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                listServices.clear();
                                ArrayList<Service> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listServices.addAll(listSubcategories);
                                    serviceAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_SERVICE) {
                fetchServiceList();
            }
        }
    }

    public void onServiceSelected(Service service) {
        Intent intent = new Intent();
        intent.putExtra(IKeyConstants.SERVICE, service);
        setResult(RESULT_OK, intent);
        finish();
    }
}
