package com.ebabu.tooreest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.SignUpActivity;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 05/01/2017.
 */
public class CustomerSignUpFragment extends Fragment {
    private SignUpActivity signUpActivity;
    private CustomEditText etFullname, etUsername, etMobileNum, etEmail, etPassword;
    private CustomTextView tvCountryCode;
    private String fullname, mobileNum, email, password;
    private JSONObject jsonObject = new JSONObject();

    public static CustomerSignUpFragment getInstance() {
        CustomerSignUpFragment customerSignUpFragment = new CustomerSignUpFragment();
        return customerSignUpFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_signup, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvCountryCode = (CustomTextView) view.findViewById(R.id.tv_country_code);
        etFullname = (CustomEditText) view.findViewById(R.id.et_fullname);
        etUsername = (CustomEditText) view.findViewById(R.id.et_username);
        etMobileNum = (CustomEditText) view.findViewById(R.id.et_mobile);
        etEmail = (CustomEditText) view.findViewById(R.id.et_email);
        etPassword = (CustomEditText) view.findViewById(R.id.et_password);

        tvCountryCode.setText(IKeyConstants.PLUS_SIGN + signUpActivity.country.getMobileCode());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpActivity = (SignUpActivity) getActivity();
    }

    public boolean areFieldsValid() {
        fullname = etFullname.getText().toString();
        mobileNum = etMobileNum.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (fullname.isEmpty()) {
            etFullname.setError(getString(R.string.enter_name));
            return false;
        }

        if (!Utils.isNameValid(fullname)) {
            etFullname.setError(getString(R.string.enter_a_valid_name));
            return false;
        }

        if (mobileNum.isEmpty()) {
            etMobileNum.setError(getString(R.string.enter_mobile_number));
            return false;
        }

        if (mobileNum.length() < IKeyConstants.MIN_MOBILE_NUM_LENGTH) {
            etMobileNum.setError(getString(R.string.mobile_number_must_be_n_digits, IKeyConstants.MIN_MOBILE_NUM_LENGTH));
            return false;
        }

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

        try {
            jsonObject.put(IKeyConstants.KEY_LANGUAGE, signUpActivity.language);
            jsonObject.put(IKeyConstants.KEY_COUNTRY, signUpActivity.country.getCountryName());
            jsonObject.put(IKeyConstants.KEY_CITY, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_COMPANY_NAME, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_FULL_NAME, fullname);
            jsonObject.put(IKeyConstants.KEY_USERNAME, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_PASSWORD, password);
            jsonObject.put(IKeyConstants.KEY_MOBILE, signUpActivity.country.getMobileCode() + mobileNum);
            jsonObject.put(IKeyConstants.KEY_EMAIL, email);
            jsonObject.put(IKeyConstants.KEY_ADDRESS, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_POSTAL_CODE, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_SUB_CAT, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_USER_TYPE, IKeyConstants.USER_TYPE_CUSTOMER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public JSONObject getCustomerJson() {
        return jsonObject;
    }
}
