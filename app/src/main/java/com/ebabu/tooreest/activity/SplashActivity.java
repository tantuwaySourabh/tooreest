package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private Intent notificationIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;
        Utils.setAppLanguage(context, AppPreference.getInstance(context).getLanguage());
        notificationIntent = getIntent();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4 * 1000);
                    startNextActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }

    private void startNextActivity() {
        Intent intent = null;
        if (AppPreference.getInstance(context).isLoggedIn()) {
            if (AppPreference.getInstance(context).getUserType().equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
                intent = new Intent(context, CustomerHomeActivity.class);
            } else {
                intent = new Intent(context, ProviderHomeActivity.class);
            }
            intent.putExtra(IKeyConstants.NOTIFICATION_TYPE, notificationIntent.getStringExtra(IKeyConstants.NOTIFICATION_TYPE));
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
