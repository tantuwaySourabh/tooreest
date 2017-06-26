package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {

    private final static String TAG = OtpActivity.class.getSimpleName();
    private Context context;
    private static CustomEditText etOtp;
    private String userId, userType, mobileNumber;
    private CustomTextView tvMobileNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        context = OtpActivity.this;
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        tvMobileNum = (CustomTextView) findViewById(R.id.tv_mobile_num);
        etOtp = (CustomEditText) findViewById(R.id.et_otp);
        etOtp.addTextChangedListener(textWatcher);

        userId = intent.getStringExtra(IKeyConstants.USER_ID);
        userType = intent.getStringExtra(IKeyConstants.USER_TYPE);
        mobileNumber = intent.getStringExtra(IKeyConstants.KEY_MOBILE);

        tvMobileNum.setText(IKeyConstants.PLUS_SIGN + mobileNumber);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String otp = etOtp.getText().toString();
            if (otp.length() == 4) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                verifyOtp();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        AppPreference.getInstance(context).setIsOtpScreen(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPreference.getInstance(context).setIsOtpScreen(false);
    }

    public static void updateMessageBox(String otp) {
        etOtp.setText(otp);
    }

    private void verifyOtp() {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.verifying_otp));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("verify_code", etOtp.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "verifyOtp(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.OTP_VERIFICATION, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "verifyOtp(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, "Check your email and verify for login", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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
        });
    }
}
