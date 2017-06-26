package com.ebabu.tooreest.ccavenue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class CCAvenueWebViewActivity extends Activity {

    private final static String TAG = CCAvenueWebViewActivity.class.getSimpleName();
    private Context context;
    private MyLoading dialog;
    private Intent mainIntent;
    private String html, encVal, orderId;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ccavenue_webview);
        context = CCAvenueWebViewActivity.this;
        mainIntent = getIntent();
        orderId = Utils.generateOrderId(context);
        new GetRSATask().execute();
    }

    public void postExecute() {

        class MyJavaScriptInterface {
            @JavascriptInterface
            public void processHTML(String html) {
                // process the html as needed by the app
                String status = null;
                if (html.indexOf("Failure") != -1) {
                    status = "Transaction Declined!";
                } else if (html.indexOf("Success") != -1) {
                    status = IKeyConstants.TRANSACTION_SUCCESSFUL;
                } else if (html.indexOf("Aborted") != -1) {
                    status = "Transaction Cancelled!";
                } else {
                    status = "Status Not Known!";
                }
                Log.d("processHTML", status);
                Intent intent = new Intent();
                intent.putExtra(AvenuesParams.TRANSACTION_STATUS, status);
                intent.putExtra(AvenuesParams.ORDER_ID, orderId);
                intent.putExtra(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT));
                intent.putExtra(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY));
                setResult(RESULT_OK, intent);
                finish();

            }
        }

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(webview, url);

                if (url.indexOf("/ccavResponseHandler.php") != -1) {
                    webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                } else {
//                    Toast.makeText(CCAvenueWebViewActivity.this, "Unable to redirect in CCAvenue. Please try again", Toast.LENGTH_SHORT).show();
//                    finish();
                }
                dialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

			/* An instance of this class will be registered as a JavaScript interface */
        StringBuffer params = new StringBuffer();
        params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE, Constants.ACCESS_CODE));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID, Constants.MERCHANT_ID));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID, orderId));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL, Constants.GET_REDIRECT_URL));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL, Constants.GET_CANCEL_URL));

        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME, AppPreference.getInstance(context).getFullName()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS, AppPreference.getInstance(context).getAddress()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY, AppPreference.getInstance(context).getCity()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE, IKeyConstants.NA));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP, AppPreference.getInstance(context).getPostalCode()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY, AppPreference.getInstance(context).getCountry()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL, AppPreference.getInstance(context).getMobileNum()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_EMAIL, AppPreference.getInstance(context).getEmail()));

        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_NAME, AppPreference.getInstance(context).getFullName()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ADDRESS, AppPreference.getInstance(context).getAddress()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_CITY, AppPreference.getInstance(context).getCity()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_STATE, IKeyConstants.NA));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_ZIP, AppPreference.getInstance(context).getPostalCode()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_COUNTRY, AppPreference.getInstance(context).getCountry()));
        params.append(ServiceUtility.addToPostParams(AvenuesParams.DELIVERY_TEL, AppPreference.getInstance(context).getMobileNum()));

        try {
            if (encVal != null) {
                params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL, URLEncoder.encode(encVal)));

                String vPostParams = params.substring(0, params.length() - 1);
                try {
                    webview.postUrl(Constants.TRANS_URL, vPostParams.getBytes("UTF-8"));
                } catch (Exception e) {
                    showToast("Exception occurred while opening webview.");
                }
            } else {
                Toast.makeText(context, "Couldn't encode key", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }


    public class GetRSATask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            dialog = new MyLoading(context);
            dialog.show(context.getString(R.string.please_wait));
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Constants.GET_RSA_URL); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.putOpt(AvenuesParams.ACCESS_CODE, Constants.ACCESS_CODE);
                postDataParams.putOpt(AvenuesParams.ORDER_ID, orderId);

                Log.d(TAG, "GetRSATask-> doInBackground()-> jsonBody= " + postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "responseCode=" + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    String rsaKey = "";
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        //Log.d(TAG, "in while loop=" + line);
                        rsaKey = rsaKey + line;
                    }

                    in.close();

                    return rsaKey;
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "GetRSATask-> onPostExecute()-> result=" + result);
//            Toast.makeText(getApplicationContext(), "Result: " + result,
//                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
            if (!ServiceUtility.chkNull(result).equals("")
                    && ServiceUtility.chkNull(result).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), result);
                postExecute();
            } else {
                Toast.makeText(context, context.getString(R.string.problem_in_payment_gateway), Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
} 