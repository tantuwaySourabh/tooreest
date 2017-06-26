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
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.customview.SquareImageView;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.ImageUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UploadIdAndBankActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = UploadIdAndBankActivity.class.getSimpleName();
    private Context context;
    private SquareImageView ivBank, ivId;
    private ImageUtils imageUtilsId, imageUtilsBank;
    private int isBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_id_bank);
        context = UploadIdAndBankActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.upload_doc1));
    }

    private void initView() {
        ivBank = (SquareImageView) findViewById(R.id.iv_bank_image);
        ivId = (SquareImageView) findViewById(R.id.iv_id_image);
        ivBank.setOnClickListener(this);
        ivId.setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        imageUtilsId = new ImageUtils(context, ivId);
        imageUtilsBank = new ImageUtils(context, ivBank);

        String idProofUrl = AppPreference.getInstance(context).getIdProofUrl();
        String bankIdUrl = AppPreference.getInstance(context).getBankIdUrl();
        AQuery aQuery = new AQuery(context);
        if (idProofUrl != null && idProofUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivId).image(idProofUrl);
        }
        if (bankIdUrl != null && bankIdUrl.startsWith(IKeyConstants.HTTP)) {
            aQuery.id(ivBank).image(bankIdUrl);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bank_image:
                isBank = 1;
                imageUtilsBank.openDialogToChosePic();
                break;

            case R.id.iv_id_image:
                isBank = 0;
                imageUtilsId.openDialogToChosePic();
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
        if (isBank == 1) {
            imageUtilsBank.onActivityResult(requestCode, resultCode, data);
        } else {
            imageUtilsId.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean areFieldsValid() {

        if (imageUtilsId.getByteArray() == null) {
            Toast.makeText(context, getString(R.string.upload_id_title), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageUtilsBank.getByteArray() == null) {
            Toast.makeText(context, getString(R.string.upload_bank_title), Toast.LENGTH_SHORT).show();
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


        params.put("id_img", imageUtilsId.getByteArray());
        params.put("bank_img", imageUtilsBank.getByteArray());


        Log.d(TAG, "uploadDocument(): params=" + params);

        new AQuery(context).ajax(IUrlConstants.UPLOAD_BANK_AND_ID, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "uploadDocument(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            JSONObject data = json.getJSONObject("data");
                            if (data.has(IKeyConstants.BANK_DOC_URL)) {
                                AppPreference.getInstance(context).setBankIdUrl(data.getString(IKeyConstants.BANK_DOC_URL));
                            }
                            if (data.has(IKeyConstants.ID_PROOF_URL)) {
                                AppPreference.getInstance(context).setIdProofUrl(data.getString(IKeyConstants.ID_PROOF_URL));
                            }
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
