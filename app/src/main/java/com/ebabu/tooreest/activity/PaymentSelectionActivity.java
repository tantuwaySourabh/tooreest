package com.ebabu.tooreest.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.utils.Utils;

public class PaymentSelectionActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_selection);
        initView();
        Utils.setUpToolbar(context, getString(R.string.payment));
    }
    public void initView(){
        context=PaymentSelectionActivity.this;
    }
}
