package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.customview.SquareImageView;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.ImageUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UploadDocActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = UploadDocActivity.class.getSimpleName();
    private Context context;
    private SquareImageView ivImage;
    private ImageUtils imageUtils;
    private CustomEditText etTitle;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_doc);
        context = UploadDocActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.upload_doc1));
    }

    private void initView() {
        etTitle = (CustomEditText) findViewById(R.id.et_title);
        ivImage = (SquareImageView) findViewById(R.id.iv_image);
        ivImage.setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        imageUtils = new ImageUtils(context, ivImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image:
                imageUtils.openDialogToChosePic();
                break;

            case R.id.btn_submit:
                if (areFieldsValid()) {
                    uploadDocument();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }

    private boolean areFieldsValid() {
        title = etTitle.getText().toString();

        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.enter_title));
            return false;
        }

        if (imageUtils.getByteArray() == null) {
            Toast.makeText(context, getString(R.string.upload_doc1), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadDocument() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final Map<String, Object> params = new HashMap<String, Object>();


        params.put("title", title);
        params.put("image", imageUtils.getByteArray());


        Log.d(TAG, "deleteOffer(): params=" + params);

        new AQuery(context).ajax(IUrlConstants.UPLOAD_DOC, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "deleteOffer(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.method(AQuery.METHOD_POST).header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
