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
import com.ebabu.tooreest.utils.RazorPayUtil;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = AddMoneyActivity.class.getSimpleName();
    private Context context;
    private CustomEditText etAmount;
    private final static int RQ_ADD_MONEY = 1;
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        context = AddMoneyActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.add_money));
    }

    private void initView() {
        etAmount = (CustomEditText) findViewById(R.id.et_amount);
        findViewById(R.id.btn_100).setOnClickListener(this);
        findViewById(R.id.btn_500).setOnClickListener(this);
        findViewById(R.id.btn_1000).setOnClickListener(this);
        findViewById(R.id.btn_add_money).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_100:
                etAmount.setText("100");
                break;

            case R.id.btn_500:
                etAmount.setText("500");
                break;

            case R.id.btn_1000:
                etAmount.setText("1000");
                break;

            case R.id.btn_add_money:
                onAddMoneyBtnClicked();
                break;
        }
    }

    private void onAddMoneyBtnClicked() {
        String strAmount = etAmount.getText().toString().trim();
        amount = 0;
        if (!strAmount.isEmpty()) {
            amount = Integer.parseInt(strAmount);
        }
        if (amount > 0) {
            if (Utils.isNetworkConnected(context)) {
//                Intent intent = new Intent(context, CCAvenueWebViewActivity.class);
//                intent.putExtra(AvenuesParams.CURRENCY, "INR");
//                intent.putExtra(AvenuesParams.AMOUNT, amount + IKeyConstants.EMPTY);
//                startActivityForResult(intent, RQ_ADD_MONEY);
                //addMoney(Utils.generateOrderId(context), amount + IKeyConstants.EMPTY);
                new RazorPayUtil(context, "Order #" + Utils.generateOrderId(context), (int) amount * 100).startPayment();
            } else {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMoney(String orderId, String amount) {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("order_id", orderId);
            jsonObject.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "addMoney(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.ADD_MONEY, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "addMoney(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, getString(R.string.money_added_successfully), Toast.LENGTH_LONG).show();
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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == RQ_ADD_MONEY) {
//                String status = data.getStringExtra(AvenuesParams.TRANSACTION_STATUS);
//                if (status != null && status.equalsIgnoreCase(IKeyConstants.TRANSACTION_SUCCESSFUL)) {
//                    addMoney(data.getStringExtra(AvenuesParams.ORDER_ID), data.getStringExtra(AvenuesParams.AMOUNT));
//                } else {
//                    Toast.makeText(context, status, Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }

    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Log.d(TAG, "onPaymentSuccess(): razorpayPaymentID=" + razorpayPaymentID);
            addMoney(razorpayPaymentID, amount + IKeyConstants.EMPTY);
        } catch (Exception e) {
            Log.e(TAG, "onPaymentSuccess(): " + e.getMessage(), e);
        }
    }

    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(context, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "onPaymentError(): " + e.getMessage(), e);
        }
    }
}
