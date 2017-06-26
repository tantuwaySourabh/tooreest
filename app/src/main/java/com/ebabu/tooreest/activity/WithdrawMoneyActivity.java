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
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawMoneyActivity extends AppCompatActivity {

    private final static String TAG = WithdrawMoneyActivity.class.getSimpleName();
    private Context context;
    private int amountInWallet;
    private CustomEditText etAmount;
    private final static int MIN_WALLET_AMOUNT = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);
        context = WithdrawMoneyActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.withdraw));
    }

    private void initView() {
        Intent intent = getIntent();
        amountInWallet = intent.getIntExtra(IKeyConstants.WALLET_AMOUNT, 0);
        etAmount = (CustomEditText) findViewById(R.id.et_amount);
        findViewById(R.id.btn_withdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountToWithdrawStr = etAmount.getText().toString();
                int amountToWithdraw = 0;
                if (!amountToWithdrawStr.isEmpty()) {
                    amountToWithdraw = Integer.parseInt(amountToWithdrawStr);
                    if ((amountInWallet - amountToWithdraw) >= MIN_WALLET_AMOUNT) {
                        withdrawMoney(amountToWithdrawStr);
                    } else {
                        etAmount.setError(getString(R.string.withdraw_condition));
                    }
                } else {
                    etAmount.setError(getString(R.string.enter_amount));
                }

            }
        });
    }

    private void withdrawMoney(String amount) {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "withdrawMoney(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.WITHDRAW_MONEY, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "withdrawMoney(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
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
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

}
