package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.Utils;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        context = ContactActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.contact));
    }

    private void initView() {
        findViewById(R.id.btn_fb).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
        findViewById(R.id.btn_gplus).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_fb:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.FACEBOOK);
                intent.putExtra(IKeyConstants.URL, IKeyConstants.FACEBOOK_LINK);
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_FACEBOOK);
                break;

            case R.id.btn_twitter:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.TWITTER);
                intent.putExtra(IKeyConstants.URL, IKeyConstants.TWITTER_LINK);
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_TWITTER);
                break;

            case R.id.btn_gplus:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.GPLUS);
                intent.putExtra(IKeyConstants.URL, IKeyConstants.GOOGLE_PLUS_LINK);
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_GPLUS);

                break;
        }

        startActivity(intent);
    }
}
