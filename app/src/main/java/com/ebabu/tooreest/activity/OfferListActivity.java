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
import com.ebabu.tooreest.adapter.OffersAdapter;
import com.ebabu.tooreest.beans.Offer;
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

public class OfferListActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = OfferListActivity.class.getSimpleName();
    private Context context;
    private FloatingActionButton fabAddOffer;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvOffers;
    private List<Offer> listOffers;
    private OffersAdapter serviceAdapter;
    private final static int REQUEST_CODE_ADD_OFFER = 1;
    private String providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        context = OfferListActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.offers));
        fetchOffersList();
    }

    private void initView() {
        Intent intent = getIntent();
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER);
        rvOffers = (RecyclerView) findViewById(R.id.rv_offer);
        fabAddOffer = (FloatingActionButton) findViewById(R.id.btn_add_offer);
        fabAddOffer.setOnClickListener(this);
        initRecyclerView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_offer:
                Intent intent = new Intent(context, AddOfferActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_OFFER);
                break;
        }
    }

    private void initRecyclerView() {
        listOffers = new ArrayList<>();

        if (providerId == null) {
            serviceAdapter = new OffersAdapter(context, listOffers, true);
            fabAddOffer.setOnClickListener(this);
            rvOffers.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fabAddOffer.isShown())
                        fabAddOffer.hide();
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fabAddOffer.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            fabAddOffer.setVisibility(View.VISIBLE);
        } else {
            serviceAdapter = new OffersAdapter(context, listOffers, false);
            fabAddOffer.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvOffers.setLayoutManager(layoutManager);
        rvOffers.setAdapter(serviceAdapter);
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

        new AQuery(context).post(IUrlConstants.OFFER_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchDocumentList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Offer>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                listOffers.clear();
                                ArrayList<Offer> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listOffers.addAll(listSubcategories);
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
            if (requestCode == REQUEST_CODE_ADD_OFFER) {
                fetchOffersList();
            }
        }
    }
}
