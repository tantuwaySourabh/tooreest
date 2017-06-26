package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Subcategory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.ImageUtils;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostJobActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = PostJobActivity.class.getSimpleName();
    private Context context;
    private Spinner spinnerCategories, spinnerSubcategories;
    private Resources resources;
    private HashMap<String, ArrayList<Subcategory>> mapSubcategories;
    private Switch switchWantLocal, switchIsUrgent;
    private CustomEditText etDescription, etBudget;
    private CustomTextView btnUploadFiles, tvUploadText;
    private ImageView ivImage;
    private String description, budget;
    private ImageUtils imageUtils;
    private final static int MIN_DESCRIPTION_LENGTH = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        context = PostJobActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.post_a_job));
    }

    private void initView() {
        resources = getResources();
        mapSubcategories = Utils.getServiceCategories(context);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        switchWantLocal = (Switch) findViewById(R.id.switch_require_local);
        switchIsUrgent = (Switch) findViewById(R.id.switch_urgent_work);
        etDescription = (CustomEditText) findViewById(R.id.et_work_description);
        etBudget = (CustomEditText) findViewById(R.id.et_budget);
        spinnerCategories = (Spinner) findViewById(R.id.spinner_project_category);
        spinnerSubcategories = (Spinner) findViewById(R.id.spinner_subcategories);
        btnUploadFiles = (CustomTextView) findViewById(R.id.btn_view_uploaded_files);
        tvUploadText = (CustomTextView) findViewById(R.id.tv_upload_message);

        btnUploadFiles.setOnClickListener(this);
        findViewById(R.id.btn_post_a_job).setOnClickListener(this);

        final ArrayList<String> listCategories = new ArrayList<>();
        listCategories.addAll(mapSubcategories.keySet());

        ArrayAdapter<String> arrayAdapterLanguage = new ArrayAdapter<String>(context, R.layout.spinner_item_padded, listCategories);
        spinnerCategories.setAdapter(arrayAdapterLanguage);
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedCat = (String) adapterView.getItemAtPosition(pos);
                ArrayList<Subcategory> listSubcategories = mapSubcategories.get(selectedCat);
                if (listSubcategories.size() > 0) {
                    ArrayAdapter<Subcategory> arrayAdapter = new ArrayAdapter<Subcategory>(context, R.layout.spinner_item_padded, listSubcategories);
                    spinnerSubcategories.setAdapter(arrayAdapter);
                } else {
                    getSubcategories();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imageUtils = new ImageUtils(context, ivImage);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_view_uploaded_files:
                imageUtils.openDialogToChosePic();
                break;

            case R.id.btn_post_a_job:
                postJob();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUtils.onActivityResult(requestCode, resultCode, data);
            btnUploadFiles.setText(getString(R.string.change_file));
            tvUploadText.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
        }
    }

    private boolean areFieldsValid() {
        description = etDescription.getText().toString();
        budget = etBudget.getText().toString();

        if (description.isEmpty()) {
            etDescription.setError(getString(R.string.enter_description));
            return false;
        }

        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            etDescription.setError(getString(R.string.min_description_length_is_n, MIN_DESCRIPTION_LENGTH));
            return false;
        }


        if (budget.isEmpty()) {
            etBudget.setError(getString(R.string.enter_your_budget));
            return false;
        }

        if (Integer.parseInt(budget) < 100) {
            etBudget.setError(getString(R.string.minimum_budget_is_100));
            return false;
        }

        return true;
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
                                    ArrayAdapter<Subcategory> arrayAdapter = new ArrayAdapter<Subcategory>(context, R.layout.spinner_item_padded, listSubcategories);
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

    private void postJob() {

        if (!areFieldsValid()) {
            return;
        }

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final Map<String, Object> params = new HashMap<String, Object>();

        params.put("category_id", (spinnerCategories.getSelectedItemPosition() + 1));
        params.put("subcat_id", ((Subcategory)spinnerSubcategories.getSelectedItem()).getSubcategory_id());
        params.put("need_local_provider", switchWantLocal.isChecked() ? 1 : 0);
        params.put("description", description);
        params.put("budget", budget);
        params.put("city", AppPreference.getInstance(context).getCity());
        params.put("country", AppPreference.getInstance(context).getCountry());
        params.put("is_urgent", switchIsUrgent.isChecked() ? 1 : 0);
        if (imageUtils.getByteArray() != null) {
            params.put("image", imageUtils.getByteArray());
        } else {
            params.put("image", IKeyConstants.EMPTY);
        }


        Log.d(TAG, "postJob(): jsonObject=" + params);

        new AQuery(context).ajax(IUrlConstants.POST_A_JOB, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "postJob(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            finish();
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
        }.method(AQuery.METHOD_POST).header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
