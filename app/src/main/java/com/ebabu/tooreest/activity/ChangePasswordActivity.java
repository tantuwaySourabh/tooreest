package com.ebabu.tooreest.activity;

import android.content.Context;
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
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ChangePasswordActivity.class.getSimpleName();
    private Context context;
    private CustomEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private String currentPassword, newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        context = ChangePasswordActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.change_password));
    }

    private void initView() {
        etCurrentPassword = (CustomEditText) findViewById(R.id.et_current_password);
        etNewPassword = (CustomEditText) findViewById(R.id.et_new_password);
        etConfirmPassword = (CustomEditText) findViewById(R.id.et_confirm_password);
        findViewById(R.id.btn_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                changePassword();
                break;
        }
    }

    private boolean areFieldsValid() {
        currentPassword = etCurrentPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError(getString(R.string.enter_current_password));
            return false;
        }

        if (!currentPassword.equals(AppPreference.getInstance(context).getPassword())) {
            etCurrentPassword.setError(getString(R.string.incorrect_password));
            return false;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError(getString(R.string.enter_new_password));
            return false;
        }

        if (newPassword.length() < IKeyConstants.MIN_PASSWORD_LENGTH) {
            etNewPassword.setError(getString(R.string.password_length_at_least_n_digits, IKeyConstants.MIN_PASSWORD_LENGTH));
            return false;
        }

        if (newPassword.equals(currentPassword)) {
            etNewPassword.setError(getString(R.string.new_old_password_are_same));
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError(getString(R.string.enter_password));
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.confirm_password_is_different));
            return false;
        }

        return true;
    }

    private void changePassword() {
        if (!areFieldsValid()) {
            return;
        }

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();


        Log.d(TAG, "fetchProviderList(): jsonObject=" + jsonObject);

        try {
            jsonObject.put("c_password", currentPassword);
            jsonObject.put("n_password", newPassword);
            jsonObject.put("con_password", confirmPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "changePassword(): jsonObject=" + jsonObject);
        new AQuery(context).post(IUrlConstants.CHANGE_PASSWORD, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "changePassword(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            AppPreference.getInstance(context).setPassword(newPassword);
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
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
