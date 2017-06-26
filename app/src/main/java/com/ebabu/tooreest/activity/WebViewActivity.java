package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.utils.Utils;
import com.rey.material.widget.ProgressView;

public class WebViewActivity extends AppCompatActivity {

    private final static String TAG = WebViewActivity.class.getSimpleName();
    private Context context;
    private WebView webView;
    private String header, url = IKeyConstants.EMPTY;
    private int pageId;
    private ProgressView progressView;
    private final static String SAMPLE_HTML_CONTENT = "<div id=\"Content\">\n" +
            "<div id=\"Panes\"><div>\n" +
            "<h2>What is Lorem Ipsum?</h2>\n" +
            "<p><strong>Lorem Ipsum</strong> is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.</p>\n" +
            "</div><div><h2>Why do we use it?</h2>\n" +
            "<p>It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).</p>\n" +
            "</div><br />";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        context = WebViewActivity.this;
        initView();
        Utils.setUpToolbar(context, header.toUpperCase().replaceAll("_", " "));
    }

    private void initView() {
        progressView = (ProgressView) findViewById(R.id.progress_pv_linear_colors);
        Intent intent = getIntent();
        pageId = intent.getIntExtra(IKeyConstants.PAGE_ID, 0);
        header = intent.getStringExtra(IKeyConstants.HEADER);

        if (pageId == IKeyConstants.ID_TERMS_CONDITIONS) {
            url = IUrlConstants.TERMS_CONDITIONS_PAGE;
        } else if (pageId == IKeyConstants.ID_ABOUT_US) {
            url = IUrlConstants.ABOUT_US_PAGE;
        } else if (pageId == IKeyConstants.ID_PRIVACY_POLICY) {
            url = IUrlConstants.PRIVACY_POLICY_PAGE;
        } else if (pageId == IKeyConstants.FACEBOOK) {
            url = intent.getStringExtra(IKeyConstants.URL);
        } else if (pageId == IKeyConstants.GPLUS) {
            url = intent.getStringExtra(IKeyConstants.URL);
        } else if (pageId == IKeyConstants.TWITTER) {
            url = intent.getStringExtra(IKeyConstants.URL);
        } else if (pageId == IKeyConstants.LINKED_IN) {
            url = intent.getStringExtra(IKeyConstants.URL);
        }


        webView = (WebView) findViewById(R.id.webview);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (Utils.isNetworkConnected(context)) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
        if (url != null && !url.isEmpty()) {
            if (!url.startsWith(IKeyConstants.HTTP)) {
                url = "http://" + url;
            }
            Log.d(TAG, "url= " + url);
            webView.loadUrl(url);
        } else {
            Toast.makeText(context, getString(R.string.url_not_found), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressView.start();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressView.stop();
        }


    }
}
