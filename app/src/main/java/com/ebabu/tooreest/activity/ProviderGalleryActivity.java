package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.DocumentAdapter;
import com.ebabu.tooreest.beans.Documents;
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

public class ProviderGalleryActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = OfferListActivity.class.getSimpleName();
    private Context context;
    private FloatingActionButton fabAddDocument;
    private GridLayoutManager layoutManager;
    private RecyclerView rvDocuments;
    private List<Documents> listDocuments;
    private DocumentAdapter documentAdapter;
    private final static int REQUEST_CODE_ADD_OFFER = 1;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String providerId, createAt = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_gallery);
        context = ProviderGalleryActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.documents));
        fetchDocumentList();
    }

    private void initView() {
        Intent intent = getIntent();
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER);
        rvDocuments = (RecyclerView) findViewById(R.id.rv_documents);
        fabAddDocument = (FloatingActionButton) findViewById(R.id.btn_add_doc);
        fabAddDocument.setOnClickListener(this);
        initRecyclerView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_doc:
                Intent intent = new Intent(context, UploadDocActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_OFFER);
                break;
        }
    }

    private void initRecyclerView() {
        listDocuments = new ArrayList<>();

        if (providerId == null) {
            documentAdapter = new DocumentAdapter(context, listDocuments, true);
            fabAddDocument.setOnClickListener(this);
            rvDocuments.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fabAddDocument.isShown())
                        fabAddDocument.hide();

                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if (!loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                Log.d(TAG, "visibleItemCount: " + visibleItemCount + ", totalItemCount: " + totalItemCount + ", pastVisiblesItems: " + pastVisiblesItems);
                                fetchDocumentList();
                            }
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fabAddDocument.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            fabAddDocument.setVisibility(View.VISIBLE);
        } else {
            documentAdapter = new DocumentAdapter(context, listDocuments, false);
            fabAddDocument.setVisibility(View.GONE);
        }
        layoutManager = new GridLayoutManager(context, 2);

        rvDocuments.setLayoutManager(layoutManager);
        rvDocuments.setAdapter(documentAdapter);
    }


    public void fetchDocumentList() {

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
            jsonObject.put("create_at", createAt);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "fetchDocumentList(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.DOCUMENT_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchDocumentList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Documents>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                int previousSize = listDocuments.size();
                                ArrayList<Documents> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listDocuments.addAll(previousSize, listSubcategories);
                                    createAt = listSubcategories.get(listDocuments.size() - 1).getCreate_at();
                                    documentAdapter.notifyDataSetChanged();
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
                fetchDocumentList();
            }
        }
    }

}
