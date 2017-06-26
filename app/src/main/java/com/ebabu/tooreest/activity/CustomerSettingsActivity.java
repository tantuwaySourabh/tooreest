package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;
import com.rey.material.widget.Switch;

public class CustomerSettingsActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {

    private Context context;
    private Switch switchChatNoti, switchOtherNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings);
        context = CustomerSettingsActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.settings));
    }

    private void initView() {
        switchChatNoti = (Switch) findViewById(R.id.switch_chat_noti);
        switchOtherNoti = (Switch) findViewById(R.id.switch_other_noti);
        findViewById(R.id.btn_edit_profile).setOnClickListener(this);
        findViewById(R.id.btn_change_password).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_help).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);

        switchChatNoti.setChecked(AppPreference.getInstance(context).isChatNotiOn());
        switchOtherNoti.setChecked(AppPreference.getInstance(context).isOtherNotiOn());

        switchChatNoti.setOnCheckedChangeListener(this);
        switchOtherNoti.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_edit_profile:
                if (AppPreference.getInstance(context).getUserType().equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
                    intent = new Intent(context, CustomerProfileActivity.class);
                } else {
                    intent = new Intent(context, ProviderProfileActivity.class);
                }
                startActivity(intent);
                break;

            case R.id.btn_change_password:
                intent = new Intent(context, ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_about:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_ABOUT_US);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.about));
                startActivity(intent);
                break;

            case R.id.btn_help:
                Utils.startHelpshiftFaq(context);
                break;

            case R.id.btn_logout:
                DialogUtils.openDialogToLogout(context);
                break;
        }
    }

    @Override
    public void onCheckedChanged(Switch aSwitch, boolean isChecked) {
        switch (aSwitch.getId()) {
            case R.id.switch_chat_noti:
                AppPreference.getInstance(context).setChatNotiOn(isChecked);
                break;

            case R.id.switch_other_noti:
                AppPreference.getInstance(context).setOtherNotiOn(isChecked);
                break;
        }
    }
}
