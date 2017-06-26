package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.Utils;

public class LegalActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        context = LegalActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.legal_v1));
    }

    private void initView() {
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_privacy_policy).setOnClickListener(this);
        findViewById(R.id.btn_terms_n_conditions).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {

            case R.id.btn_about:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_ABOUT_US);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.about));
                startActivity(intent);
                break;

            case R.id.btn_terms_n_conditions:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_TERMS_CONDITIONS);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.terms_n_conditions));
                startActivity(intent);
                break;

            case R.id.btn_privacy_policy:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_PRIVACY_POLICY);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.privacy_policy));
                startActivity(intent);
                break;
        }
    }
}
