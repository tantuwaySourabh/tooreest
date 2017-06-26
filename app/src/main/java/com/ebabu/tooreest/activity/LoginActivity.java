package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Provider;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppLozicUtils;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private Context context;
    private CustomEditText etEmail, etPassword;
    private String email, password, fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        initView();

    }

    private void initView() {
        etEmail = (CustomEditText) findViewById(R.id.et_email);
        etPassword = (CustomEditText) findViewById(R.id.et_password);
        findViewById(R.id.btn_register_now).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_forgot_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_register_now:
                intent = new Intent(context, LanguageSelectionActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_login:
                if (areFieldsValid()) {
                    new FcmTokenFetchingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;

            case R.id.btn_forgot_password:
                openForgetPasswordDialog();
                break;
        }
    }

    private class FcmTokenFetchingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (fcmToken == null || fcmToken.isEmpty()) {
                    fcmToken = FirebaseInstanceId.getInstance().getToken();
                }
                Log.d(TAG, "deviceToken=" + fcmToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (!fcmToken.isEmpty()) {
                    login();
                } else {
                    new FcmTokenFetchingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } catch (Exception e) {
                DialogUtils.openAlertToShowMessage(getResources().getString(R.string.unexpected_error), context);
            }
            super.onPostExecute(aVoid);
        }

    }

    public boolean areFieldsValid() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();


        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.enter_email));
            return false;
        }

        if (!Utils.isValidEmail(email)) {
            etEmail.setError(getString(R.string.enter_a_valid_email));
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.enter_password));
            return false;
        }

        if (password.length() < IKeyConstants.MIN_PASSWORD_LENGTH) {
            etPassword.setError(getString(R.string.password_length_at_least_n_digits, IKeyConstants.MIN_PASSWORD_LENGTH));
            return false;
        }

        return true;
    }

    private void login() {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("device_token", fcmToken);
            jsonObject.put("device_id", IKeyConstants.EMPTY);
            jsonObject.put("device_type", "Android");
            jsonObject.put("user_type", IKeyConstants.USER_TYPE_PROVIDER + IKeyConstants.EMPTY);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "login(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.LOGIN, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "login(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            json = json.getJSONObject("data");
                            String userType = json.getString("user_type");
                            if (IKeyConstants.USER_TYPE_CUSTOMER.equalsIgnoreCase(userType)) {
                                myLoading.dismiss();
                                Toast.makeText(context, "You're the customer of Tooreest and trying to login with guide app.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            AppPreference.getInstance(context).setFullName(json.getString("full_name"));
                            AppPreference.getInstance(context).setEmail(json.getString("email"));
                            AppPreference.getInstance(context).setMobileNum(json.getString("mobile_no"));
                            AppPreference.getInstance(context).setPassword(password);
                            AppPreference.getInstance(context).setUserType(userType);
                            AppPreference.getInstance(context).setLanguage(json.getString("language"));
                            AppPreference.getInstance(context).setCountry(json.getString("country"));
                            AppPreference.getInstance(context).setCity(json.getString("city"));
                            AppPreference.getInstance(context).setProfileImage(json.getString("image"));
                            AppPreference.getInstance(context).setFcmToken(fcmToken);

                            Utils.setAppLanguage(context, AppPreference.getInstance(context).getLanguage());
                            if (json.has("user_id")) {
                                AppPreference.getInstance(context).setUserId(json.getString("user_id"));
                            }

                            if (json.has("about_me")) {
                                AppPreference.getInstance(context).setAboutMe(json.getString("about_me"));
                            }
//                            if (json.has(IKeyConstants.KEY_GUIDE_TYPE)) {
//                                AppPreference.getInstance(context).setGuideType(json.getString(IKeyConstants.KEY_GUIDE_TYPE));
//                            }
                            if (json.has(IKeyConstants.KEY_FACEBOOK)) {
                                AppPreference.getInstance(context).setFbLink(json.getString(IKeyConstants.KEY_FACEBOOK));
                            }
                            if (json.has(IKeyConstants.KEY_GPLUS)) {
                                AppPreference.getInstance(context).setGplusLink(json.getString(IKeyConstants.KEY_GPLUS));
                            }
                            if (json.has(IKeyConstants.KEY_TWITTER)) {
                                AppPreference.getInstance(context).setTwitterLink(json.getString(IKeyConstants.KEY_TWITTER));
                            }
                            if (json.has(IKeyConstants.KEY_LINKEDIN)) {
                                AppPreference.getInstance(context).setLinkedinLink(json.getString(IKeyConstants.KEY_LINKEDIN));
                            }
                            if (json.has(IKeyConstants.BANK_DOC_URL)) {
                                AppPreference.getInstance(context).setBankIdUrl(json.getString(IKeyConstants.BANK_DOC_URL));
                            }
                            if (json.has(IKeyConstants.ID_PROOF_URL)) {
                                AppPreference.getInstance(context).setIdProofUrl(json.getString(IKeyConstants.ID_PROOF_URL));
                            }
                            if (json.has(IKeyConstants.IS_BANK_VERIFIED)) {
                                AppPreference.getInstance(context).setIsBankVerified(json.getInt(IKeyConstants.IS_BANK_VERIFIED));
                            }
                            if (json.has(IKeyConstants.IS_ID_VERIFIED)) {
                                AppPreference.getInstance(context).setIsIdVerified(json.getInt(IKeyConstants.IS_ID_VERIFIED));
                            }
                            if (json.has(IKeyConstants.KEY_BANK_NAME))
                                AppPreference.getInstance(context).setBankName(json.getString(IKeyConstants.KEY_BANK_NAME));

                            if (json.has(IKeyConstants.KEY_ACCOUNT_NUM))
                                AppPreference.getInstance(context).setBankAccountNum(json.getString(IKeyConstants.KEY_ACCOUNT_NUM));

                            if (json.has(IKeyConstants.KEY_BIC_CODE))
                                AppPreference.getInstance(context).setBicCode(json.getString(IKeyConstants.KEY_BIC_CODE));

                            if (json.has(IKeyConstants.KEY_LICENSE_NUM))
                                AppPreference.getInstance(context).setLicenseNumber(json.getString(IKeyConstants.KEY_LICENSE_NUM));

                            if (json.has(IKeyConstants.KEY_ID_IMG))
                                AppPreference.getInstance(context).setIdProofUrl(json.getString(IKeyConstants.KEY_ID_IMG));

                            if (json.has(IKeyConstants.KEY_ADDRESS_IMG))
                                AppPreference.getInstance(context).setAddressProofUrl(json.getString(IKeyConstants.KEY_ADDRESS_IMG));

                            if (json.has(IKeyConstants.KEY_LICENSE_IMG))
                                AppPreference.getInstance(context).setCertificateUrl(json.getString(IKeyConstants.KEY_LICENSE_IMG));

                            if (json.has(IKeyConstants.KEY_BANK_IMG))
                                AppPreference.getInstance(context).setBankIdUrl(json.getString(IKeyConstants.KEY_BANK_IMG));

                            if (json.has(IKeyConstants.LAT))
                                AppPreference.getInstance(context).setProviderLatitude(json.getDouble(IKeyConstants.LAT));

                            if (json.has(IKeyConstants.LNG))
                                AppPreference.getInstance(context).setProviderLongitude(json.getDouble(IKeyConstants.LNG));

                            if (json.has(IKeyConstants.KEY_HAS_PAYPAL_ACCOUNT))
                                AppPreference.getInstance(context).setHasPaypalId(json.getInt(IKeyConstants.KEY_HAS_PAYPAL_ACCOUNT));

                            if (json.has(IKeyConstants.KEY_PAYPAL_EMAIL_ID))
                                AppPreference.getInstance(context).setPaypalId(json.getString(IKeyConstants.KEY_PAYPAL_EMAIL_ID));

                            if (json.has(IKeyConstants.KEY_BANK_CODE))
                                AppPreference.getInstance(context).setBranchCode(json.getString(IKeyConstants.KEY_BANK_CODE));

                            if (json.has("availability")) {
                                try {
                                    JSONObject availabilityJsonObject = new JSONObject(json.getString("availability").isEmpty() ? "{}" : json.getString("availability"));
                                    if (availabilityJsonObject.has(IKeyConstants.KEY_TIMINGS)) {
                                        AppPreference.getInstance(context).setAvailabilityJson(availabilityJsonObject.getJSONArray(IKeyConstants.KEY_TIMINGS).toString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            AppLozicUtils.getInstance(context).loginInAppLozic(context);
                            Intent intent = null;
                            if (userType.equals(IKeyConstants.USER_TYPE_CUSTOMER)) {
                                intent = new Intent(context, CustomerHomeActivity.class);
                            } else {
                                AppPreference.getInstance(context).setCompanyName(json.getString("company_name"));
                                AppPreference.getInstance(context).setPostalCode(json.getString("postal_code"));
                                AppPreference.getInstance(context).setAddress(json.getString("address"));
                                intent = new Intent(context, ProviderHomeActivity.class);
                            }

                            AppPreference.getInstance(context).setSecretKey(json.getString("token"));
                            //Core.registerDeviceToken(context, fcmToken);
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
        });
    }

    private void openForgetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Forget Password");

        final View view = getLayoutInflater().inflate(R.layout.layout_dialog_forget_password, null);
        builder.setView(view);

        final CustomEditText etEmail = (CustomEditText) view.findViewById(R.id.et_email);

        builder.setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();

                if (email.isEmpty()) {
                    etEmail.setError(getString(R.string.enter_email));
                    return;
                }

                if (!Utils.isValidEmail(email)) {
                    etEmail.setError(getString(R.string.enter_a_valid_email));
                    return;
                }

                sendEmailOnForgotPassword(email, alertDialog);
            }
        });
    }

    private void sendEmailOnForgotPassword(String email, final AlertDialog alertDialog) {
        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "sendEmailOnForgotPassword(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.FORGOT_PASSWORD, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "sendEmailOnForgotPassword(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            alertDialog.dismiss();
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
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
}
