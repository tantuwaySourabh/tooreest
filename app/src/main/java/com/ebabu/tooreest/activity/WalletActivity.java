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
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = WalletActivity.class.getSimpleName();
    private Context context;
    private Intent intent;
    private int bookingAmnt, amountInWallet, serviceCharge, serviceTax, discount, offerId, totalAmount;
    private boolean forPayment;
    private CustomTextView btnTransactions, btnWithdraw, btnAddMoney, btnPayNow, tvWalletAmnt;
    private String timestamp, subcatId, providerId, bookingId;
    private int apiType;
    private final static int RQ_ADD_MONEY = 1, RQ_WITHDRAW_MONEY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_provider);
        context = WalletActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.wallet));
        getWalletAmount();
    }

    public void initView() {
        intent = getIntent();
        forPayment = intent.getBooleanExtra(IKeyConstants.FOR_PAYMENT, false);
        timestamp = intent.getStringExtra(IKeyConstants.TIMESTAMP);
        subcatId = intent.getStringExtra(IKeyConstants.SUBCAT_ID);
        bookingId = intent.getStringExtra(IKeyConstants.BOOKING);
        providerId = intent.getStringExtra(IKeyConstants.PROVIDER_ID);
        apiType = intent.getIntExtra(IKeyConstants.API_TYPE, 0);
        bookingAmnt = intent.getIntExtra(IKeyConstants.BOOKING_AMOUNT, 0);
        serviceCharge = intent.getIntExtra(IKeyConstants.SERVICE_CHARGE, 0);
        serviceTax = intent.getIntExtra(IKeyConstants.SERVICE_TAX, 0);
        totalAmount = intent.getIntExtra(IKeyConstants.TOTAL_AMOUNT, 0);
        discount = intent.getIntExtra(IKeyConstants.DISCOUNT, 0);
        offerId = intent.getIntExtra(IKeyConstants.OFFER_ID, 0);

        tvWalletAmnt = (CustomTextView) findViewById(R.id.tv_wallet_amount);
        btnTransactions = (CustomTextView) findViewById(R.id.btn_transaction);
        btnWithdraw = (CustomTextView) findViewById(R.id.btn_withdraw);
        btnAddMoney = (CustomTextView) findViewById(R.id.btn_add_money);
        btnPayNow = (CustomTextView) findViewById(R.id.btn_pay_now);
        if (forPayment) {
            btnWithdraw.setVisibility(View.GONE);
            btnPayNow.setVisibility(View.VISIBLE);
            btnPayNow.setText(getString(R.string.pay_now, bookingAmnt));
        } else {
            btnWithdraw.setVisibility(View.VISIBLE);
            btnPayNow.setVisibility(View.GONE);
        }
        btnTransactions.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
        btnAddMoney.setOnClickListener(this);
        btnPayNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_transaction:
                intent = new Intent(context, TransactionActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_add_money:
                intent = new Intent(context, AddMoneyActivity.class);
                startActivityForResult(intent, RQ_ADD_MONEY);
                break;

            case R.id.btn_withdraw:
                intent = new Intent(context, WithdrawMoneyActivity.class);
                intent.putExtra(IKeyConstants.WALLET_AMOUNT, amountInWallet);
                startActivityForResult(intent, RQ_WITHDRAW_MONEY);
                break;

            case R.id.btn_pay_now:
                if (amountInWallet < bookingAmnt) {
                    DialogUtils.openAlertToShowMessage(getString(R.string.you_dont_have_sufficient_amount), context);
                } else {
                    if (apiType == IKeyConstants.PAY_NOW) {
                        payNow();
                    } else {
                        bookService();
                    }
                }
                break;
        }
    }

    private void getWalletAmount() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();

        new AQuery(context).post(IUrlConstants.WALLET, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "getWalletAmount(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONObject jsonObject = json.getJSONObject("data");
                            amountInWallet = jsonObject.getInt("wallet_amt");
                            tvWalletAmnt.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + amountInWallet);
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();

                    finish();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void bookService() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("booking_date", timestamp);
            jsonObject.put("subcategory_id", subcatId);
            jsonObject.put("provider_id", providerId);
            jsonObject.put("service_amt", serviceCharge);
            jsonObject.put("tax", serviceTax);
            jsonObject.put("offer_amt", discount);
            jsonObject.put("total_amount", totalAmount);
            jsonObject.put("advance_paid", bookingAmnt);
            jsonObject.put("offer_id", offerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "bookService(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.BOOK_SERVICE, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "bookService(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, CustomerHomeActivity.class);
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
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void payNow() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("booking_id", bookingId);
            jsonObject.put("amount", bookingAmnt);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "payNow(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.PAY_NOW, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "payNow(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RQ_ADD_MONEY) {
                getWalletAmount();
            } else if (requestCode == RQ_WITHDRAW_MONEY) {
                DialogUtils.openAlertToShowMessage(getString(R.string.withdraw_request_success), context);
                getWalletAmount();
            }
        }
    }
}
