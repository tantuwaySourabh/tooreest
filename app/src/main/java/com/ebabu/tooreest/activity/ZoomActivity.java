package com.ebabu.tooreest.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.TouchImageView;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.utils.Utils;


public class ZoomActivity extends Activity {
    private Context context;
    private TouchImageView img;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        context = this;
        img = (TouchImageView) findViewById(R.id.iv_zoomed_image);
        imageUrl = getIntent().getStringExtra(IKeyConstants.IMAGE);
        try {
            if (Utils.isNetworkConnected(context)) {
                new AQuery(context).id(img).image(imageUrl, true, true, 500, 0);
                img.setMaxZoom(15f);
            } else {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



}
