package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Service;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AddNewServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = AddNewServiceActivity.class.getSimpleName();
    private Context context;
    private ArrayList<Subcategory> mListSubcategories;
    private Spinner spinnerCategories, spinnerSubcategories;
    private LinkedHashMap<String, ArrayList<Subcategory>> mapSubcategories;
    private AppCompatRadioButton rbPhysical, rbVirtual;
    private CustomEditText etDescription, etCharge;
    private CustomTextView btnUploadFiles, tvUploadText;
    private ImageView ivImage;
    private String cat_id, service_id, description, serviceCharge;
    private ImageUtils imageUtils;
    private final static int MIN_DESCRIPTION_LENGTH = 30;
    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_service);
        context = AddNewServiceActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.add_new_service));
    }

    private void initView() {
        mapSubcategories = Utils.getServiceCategories(context);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        etDescription = (CustomEditText) findViewById(R.id.et_work_description);
        spinnerCategories = (Spinner) findViewById(R.id.spinner_category);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        spinnerSubcategories = (Spinner) findViewById(R.id.spinner_subcategories);
        etCharge = (CustomEditText) findViewById(R.id.et_charge);
        btnUploadFiles = (CustomTextView) findViewById(R.id.btn_view_uploaded_files);
        tvUploadText = (CustomTextView) findViewById(R.id.tv_upload_message);
        rbPhysical = (AppCompatRadioButton) findViewById(R.id.rb_physical);
        rbVirtual = (AppCompatRadioButton) findViewById(R.id.rb_virtual);
        btnUploadFiles.setOnClickListener(this);
        ivImage.setOnClickListener(this);
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


        findViewById(R.id.btn_save).setOnClickListener(this);

        rbPhysical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    etCharge.setEnabled(true);
                    etCharge.setText(IKeyConstants.EMPTY);
                } else {
                    etCharge.setEnabled(false);
                    serviceCharge = IKeyConstants.VIRTUAL_GUIDE_PRICE_USD + IKeyConstants.EMPTY;
                    etCharge.setText(serviceCharge);
                }
            }
        });

        imageUtils = new ImageUtils(context, ivImage);

        handleIntent();
        getSubcategories();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        service = intent.getParcelableExtra(IKeyConstants.SERVICE);
        if (service != null && service.getSub_cat_id() != null) {
            cat_id = service.getCat_id();
            service_id = service.getService_id();
            etDescription.setText(service.getDescription());
            etCharge.setText(service.getPrice() + IKeyConstants.EMPTY);
            if (service.getService_type() == 1) {
                rbPhysical.setChecked(true);
            } else {
                rbVirtual.setChecked(true);
            }
            if (service.getImage() != null && service.getImage().startsWith(IKeyConstants.HTTP)) {
                btnUploadFiles.setText(getString(R.string.change_file));
                tvUploadText.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);
                new AQuery(context).id(ivImage).image(service.getImage(), true, true, 500, AQuery.FADE_IN);
            }
        } else {
            cat_id = IKeyConstants.EMPTY;
            service_id = IKeyConstants.EMPTY;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_save:
                onSaveBtnClicked();
                break;

            case R.id.btn_view_uploaded_files:
                imageUtils.openDialogToChosePic();
                break;

            case R.id.iv_image:
                if (service != null && service.getImage() != null && service.getImage().startsWith(IKeyConstants.HTTP)) {
                    Intent intent = new Intent(context, ZoomActivity.class);
                    intent.putExtra(IKeyConstants.IMAGE, service.getImage());
                    startActivity(intent);
                }
                break;
        }
    }

    private void onSaveBtnClicked() {
        serviceCharge = etCharge.getText().toString();
        description = etDescription.getText().toString();

        if (description.isEmpty()) {
            etDescription.setError(getString(R.string.enter_description));
            return;
        }

        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            etDescription.setError(getString(R.string.min_description_length_is_n, MIN_DESCRIPTION_LENGTH));
            return;
        }

        if (serviceCharge.isEmpty()) {
            etCharge.setError(getString(R.string.enter_service_charge));
            return;
        }

        if (Integer.parseInt(serviceCharge) < IKeyConstants.MIN_SERVICE_CHARGE) {
            etCharge.setError(getString(R.string.min_service_charge_is_n, IKeyConstants.MIN_SERVICE_CHARGE));
            return;
        }

        if (Utils.isNetworkConnected(context)) {
            updateSubcategory();
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
                                    if (service != null && service.getSub_cat_id() != null) {
                                        Subcategory subcategory = new Subcategory();
                                        subcategory.setSubcategory_id(service.getSub_cat_id());
                                        spinnerSubcategories.setSelection(arrayAdapter.getPosition(subcategory));
                                    }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUtils.onActivityResult(requestCode, resultCode, data);
            btnUploadFiles.setText(getString(R.string.change_file));
            tvUploadText.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
        }
    }

    private void updateSubcategory() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final Map<String, Object> params = new HashMap<>();
        params.put("category_id", spinnerCategories.getSelectedItemPosition() + 1);
        params.put("service_id", service_id);
        params.put("sub_cate_id", ((Subcategory) spinnerSubcategories.getSelectedItem()).getSubcategory_id());
        params.put("description", description);
        params.put("service_type", (rbPhysical.isChecked() ? IKeyConstants.SERVICE_TYPE_PHYSICAL : IKeyConstants.SERVICE_TYPE_VIRTUAL) + IKeyConstants.EMPTY);
        params.put("service_charge", serviceCharge);
        if (imageUtils.getByteArray() != null) {
            params.put("image", imageUtils.getByteArray());
        } else {
            params.put("image", IKeyConstants.EMPTY);
        }


        Log.d(TAG, "updateSubcategory(): params=" + params);

        new AQuery(context).ajax(IUrlConstants.ADD_UPDATE_SERVICE, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "updateSubcategory(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            setResult(RESULT_OK);
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