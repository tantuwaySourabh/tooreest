package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.UserSubcatAdapter;
import com.ebabu.tooreest.beans.Subcategory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class UserCategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = UserCategoriesActivity.class.getSimpleName();
    private Context context;
    private RecyclerView rvSubcategories;
    private ArrayList<Subcategory> listSelectedSubcat;
    private UserSubcatAdapter userSubcatAdapter;
    private Spinner spinnerCategories, spinnerSubcategories;
    private HashMap<String, ArrayList<Subcategory>> mapSubcategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_categories);
        context = UserCategoriesActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.my_skills));
    }

    private void initView() {
        mapSubcategories = Utils.getServiceCategories(context);
        listSelectedSubcat = new ArrayList<>();
        rvSubcategories = (RecyclerView) findViewById(R.id.rv_subcategories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        userSubcatAdapter = new UserSubcatAdapter(context, listSelectedSubcat);
        rvSubcategories.setLayoutManager(layoutManager);
        rvSubcategories.setAdapter(userSubcatAdapter);

        spinnerCategories = (Spinner) findViewById(R.id.spinner_category);
        spinnerSubcategories = (Spinner) findViewById(R.id.spinner_subcategories);

        final ArrayList<String> listCategories = new ArrayList<>();
        listCategories.addAll(mapSubcategories.keySet());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, listCategories);
        spinnerCategories.setAdapter(arrayAdapter);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedCat = (String) adapterView.getItemAtPosition(pos);
                ArrayList<Subcategory> listSubcategories = mapSubcategories.get(selectedCat);
                if (listSubcategories.size() > 0) {
                    ArrayAdapter<Subcategory> arrayAdapter = new ArrayAdapter<Subcategory>(context, R.layout.spinner_item, listSubcategories);
                    spinnerSubcategories.setAdapter(arrayAdapter);
                } else {
                    getSubcategories();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCategories.setSelection(0);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        getSubcategories();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                onAddBtnClicked();
                break;

            case R.id.btn_save:
                onSaveBtnClicked();
                break;
        }
    }

    private void onSaveBtnClicked() {
        if (listSelectedSubcat.size() > 0) {
            Intent intent = new Intent();
            Subcategory firstSubcategory = listSelectedSubcat.get(0);
            String subCatCsv = firstSubcategory.getSubcategory_id();
            String subCatNameCsv = firstSubcategory.getSubcategory_name();
            for (int i = 1; i < listSelectedSubcat.size(); i++) {
                subCatCsv = subCatCsv + IKeyConstants.COMMA + listSelectedSubcat.get(i).getSubcategory_id();
                subCatNameCsv = subCatNameCsv + IKeyConstants.COMMA + listSelectedSubcat.get(i).getSubcategory_name();
            }
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            intent.putExtra(IKeyConstants.SUB_CAT_JSON, gson.toJson(listSelectedSubcat));
            intent.putExtra(IKeyConstants.SUB_CAT_CSV, subCatCsv.trim());
            intent.putExtra(IKeyConstants.SUBCAT_NAME_CSV, subCatNameCsv.trim());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(context, getString(R.string.add_category), Toast.LENGTH_SHORT).show();
        }
    }

    private void onAddBtnClicked() {
        Subcategory selectedSubcategory = (Subcategory) spinnerSubcategories.getSelectedItem();
        if (!listSelectedSubcat.contains(selectedSubcategory)) {
            listSelectedSubcat.add(0, selectedSubcategory);
            userSubcatAdapter.notifyItemInserted(0);
        }
    }

    private void getSubcategories() {


        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cat_id", (spinnerCategories.getSelectedItemPosition() + 1));
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
                                    mapSubcategories.put(spinnerCategories.getSelectedItem().toString(), listSubcategories);
                                    ArrayAdapter<Subcategory> arrayAdapter = new ArrayAdapter<Subcategory>(context, R.layout.spinner_item, listSubcategories);
                                    spinnerSubcategories.setAdapter(arrayAdapter);
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


    @Override
    public void onBackPressed() {
        onSaveBtnClicked();
    }
}