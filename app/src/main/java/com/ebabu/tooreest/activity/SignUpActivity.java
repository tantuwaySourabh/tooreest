package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.SignupPagerAdapter;
import com.ebabu.tooreest.beans.City;
import com.ebabu.tooreest.beans.Country;
import com.ebabu.tooreest.beans.DataHandler;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = SignUpActivity.class.getSimpleName();
    private Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SignupPagerAdapter pagerAdapter;
    private int currentFragment;
    private AppCompatCheckBox cbTermsConditions;
    public String language;
    public City city;
    public Country country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = SignUpActivity.this;
        initView();
        initTabLayout();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        cbTermsConditions = (AppCompatCheckBox) findViewById(R.id.cb_terms_condition);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_terms_n_conditions).setOnClickListener(this);

        Intent intent = getIntent();
        language = intent.getStringExtra(IKeyConstants.KEY_LANGUAGE);
        country = intent.getParcelableExtra(IKeyConstants.KEY_COUNTRY);
        city = intent.getParcelableExtra(IKeyConstants.KEY_CITY);
    }

    private void initTabLayout() {
        currentFragment = 0;
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("CUSTOMER"));
        tabLayout.addTab(tabLayout.newTab().setText("PROVIDER"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        pagerAdapter = new SignupPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragment = tab.getPosition();
                viewPager.setCurrentItem(currentFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        currentFragment = 1;
        viewPager.setCurrentItem(currentFragment);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_login:
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.btn_signup:
                registerUser();
                break;

            case R.id.btn_terms_n_conditions:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_TERMS_CONDITIONS);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.terms_n_conditions));
                startActivity(intent);
                break;
        }
    }

    public String getUserType() {
        return String.valueOf(currentFragment + 1);
    }

    private void registerUser() {
        JSONObject jsonObject = null;
        if (getUserType().equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
            if (pagerAdapter.getCustomerSignUpFragment().areFieldsValid()) {
                jsonObject = pagerAdapter.getCustomerSignUpFragment().getCustomerJson();
            } else {
                return;
            }
        } else if (getUserType().equals(IKeyConstants.USER_TYPE_PROVIDER)) {
            if (pagerAdapter.getProviderSignUpFragment().areFieldsValid()) {
                jsonObject = pagerAdapter.getProviderSignUpFragment().getProviderJson();
            } else {
                return;
            }
        }

        if (!cbTermsConditions.isChecked()) {
            Toast.makeText(context, getString(R.string.accept_terms_conditions), Toast.LENGTH_LONG).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.registering_user));


        Log.d(TAG, "registerUser(): jsonObject=" + jsonObject);

        final JSONObject finalJsonObject = jsonObject;
        new AQuery(context).post(IUrlConstants.USER_REGISTRATION, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "registerUser(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            String userId = json.getString("data");
                            Intent intent = new Intent(context, OtpActivity.class);
                            intent.putExtra(IKeyConstants.USER_TYPE, finalJsonObject.getString(IKeyConstants.KEY_USER_TYPE));
                            intent.putExtra(IKeyConstants.USER_ID, userId);
                            intent.putExtra(IKeyConstants.KEY_MOBILE, finalJsonObject.getString(IKeyConstants.KEY_MOBILE));
                            DataHandler.getInstance().setJsonObject(finalJsonObject);
                            startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
        if(resultCode==RESULT_OK){
            if(requestCode== Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP){
                pagerAdapter.getProviderSignUpFragment().setLocationInView(data);
            }
        }
    }
}
