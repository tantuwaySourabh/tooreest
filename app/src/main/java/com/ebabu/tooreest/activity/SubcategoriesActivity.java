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
import com.ebabu.tooreest.adapter.SubcatGridAdapter;
import com.ebabu.tooreest.beans.Subcategory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SubcategoriesActivity extends AppCompatActivity {

    private final static String TAG = SubcategoriesActivity.class.getSimpleName();
    private Context context;
    private RecyclerView rvSubcategories;
    private ArrayList<Subcategory> mListSubcategories;
    private SubcatGridAdapter subcatGridAdapter;
    private String catId, catName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);
        context = SubcategoriesActivity.this;
        initView();
        Utils.setUpToolbar(context, catName);
    }

    private void initView() {
        Intent intent = getIntent();
        catId = intent.getStringExtra(IKeyConstants.CATEGORY);
        catName = intent.getStringExtra(IKeyConstants.CATEGORY_NAME);
        mListSubcategories = new ArrayList<>();
        rvSubcategories = (RecyclerView) findViewById(R.id.rv_subcategories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        subcatGridAdapter = new SubcatGridAdapter(context, mListSubcategories);
        rvSubcategories.setLayoutManager(layoutManager);
        rvSubcategories.setAdapter(subcatGridAdapter);

        getSubcategories();

    }


    private void getSubcategories() {


        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cat_id", catId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "getSubcategories(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.GET_SUBCATEGORY, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "getSubcategories(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Subcategory>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Subcategory> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    mListSubcategories.addAll(listSubcategories);
                                    subcatGridAdapter.notifyDataSetChanged();
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
        });
    }


}