package com.ebabu.tooreest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.SignUpActivity;
import com.ebabu.tooreest.activity.UserCategoriesActivity;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 05/01/2017.
 */
public class ProviderSignUpFragment extends Fragment {
    private final static String TAG = ProviderSignUpFragment.class.getSimpleName();
    private CustomTextView tvCountryCode;
    private SignUpActivity signUpActivity;
    private CustomEditText etCompanyName, etFullname, etUsername, etMobileNum, etEmail, etPassword, etPostalCode;
    private CustomTextView btnAddCategories, tvCatNames, etAddress;
    private String companyName, address, postalCode, subCatCsv, fullname, mobileNum, email, password, guideType;
    private double latitude, longitude;
    private JSONObject jsonObject = new JSONObject();
    private final static int REQUEST_CODE_SELECTED_CAT = 1;
    private JSONArray subcatJson;
    private RadioGroup rgGuideType;

    public static ProviderSignUpFragment getInstance() {
        ProviderSignUpFragment customerSignUpFragment = new ProviderSignUpFragment();
        return customerSignUpFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_signup, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rgGuideType = (RadioGroup) view.findViewById(R.id.rg_guide_type);
        tvCountryCode = (CustomTextView) view.findViewById(R.id.tv_country_code);
        etCompanyName = (CustomEditText) view.findViewById(R.id.et_company_name);
        etFullname = (CustomEditText) view.findViewById(R.id.et_fullname);
        etUsername = (CustomEditText) view.findViewById(R.id.et_username);
        etMobileNum = (CustomEditText) view.findViewById(R.id.et_mobile);
        etEmail = (CustomEditText) view.findViewById(R.id.et_email);
        etPassword = (CustomEditText) view.findViewById(R.id.et_password);
        etAddress = (CustomTextView) view.findViewById(R.id.et_address);
        etPostalCode = (CustomEditText) view.findViewById(R.id.et_postal_code);
        btnAddCategories = (CustomTextView) view.findViewById(R.id.et_categories);
        tvCatNames = (CustomTextView) view.findViewById(R.id.et_category_names);

        btnAddCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkConnected(getContext())) {
                    Intent intent = new Intent(signUpActivity, UserCategoriesActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SELECTED_CAT);
                } else {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        rgGuideType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                guideType = (String) radioGroup.findViewById(i).getTag();
            }
        });
        tvCountryCode.setText(IKeyConstants.PLUS_SIGN + signUpActivity.country.getMobileCode());

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.startAddressActivity(signUpActivity, Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP);
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpActivity = (SignUpActivity) getActivity();
    }

    public boolean areFieldsValid() {
        companyName = etFullname.getText().toString();
        fullname = etFullname.getText().toString();
        mobileNum = etMobileNum.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        address = etAddress.getText().toString();
        postalCode = etPostalCode.getText().toString();

//        if (companyName.isEmpty()) {
//            etCompanyName.setError(getString(R.string.enter_company_name));
//            return false;
//        }
//
//        if (!Utils.isNameValid(companyName)) {
//            etCompanyName.setError(getString(R.string.enter_a_valid_company));
//            return false;
//        }

        if (fullname.isEmpty()) {
            etFullname.setError(getString(R.string.enter_name));
            return false;
        }

        if (!Utils.isNameValid(fullname)) {
            etFullname.setError(getString(R.string.enter_a_valid_name));
            return false;
        }

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

        if (address.isEmpty()) {
            etAddress.setError(getString(R.string.enter_address));
            return false;
        }

        if (postalCode.isEmpty()) {
            etPostalCode.setError(getString(R.string.enter_pincode));
            return false;
        }

//        if (postalCode.length() < IKeyConstants.MIN_PINCODE_LENGTH) {
//            etPostalCode.setError(getString(R.string.pincode_must_be_n_digits, IKeyConstants.MIN_PINCODE_LENGTH));
//            return false;
//        }

//        if (subcatJson == null || subcatJson.length() == 0) {
//            Toast.makeText(signUpActivity, getString(R.string.add_category), Toast.LENGTH_SHORT).show();
//            return false;
//        }

//        if (guideType == null || guideType.isEmpty()) {
//            Toast.makeText(signUpActivity, getString(R.string.select_guide_type), Toast.LENGTH_SHORT).show();
//            return false;
//        }
        try {
            jsonObject.put(IKeyConstants.KEY_LANGUAGE, signUpActivity.language);
            jsonObject.put(IKeyConstants.KEY_COUNTRY, signUpActivity.country);
            jsonObject.put(IKeyConstants.KEY_CITY, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_COMPANY_NAME, companyName);
            jsonObject.put(IKeyConstants.KEY_FULL_NAME, fullname);
            jsonObject.put(IKeyConstants.KEY_USERNAME, IKeyConstants.EMPTY);
            jsonObject.put(IKeyConstants.KEY_PASSWORD, password);
            jsonObject.put(IKeyConstants.KEY_MOBILE, signUpActivity.country.getMobileCode() + mobileNum);
            jsonObject.put(IKeyConstants.KEY_EMAIL, email);
            jsonObject.put(IKeyConstants.KEY_ADDRESS, address);
            jsonObject.put(IKeyConstants.KEY_POSTAL_CODE, postalCode);
            //jsonObject.put(IKeyConstants.KEY_SUB_CAT, subcatJson);
            jsonObject.put(IKeyConstants.KEY_USER_TYPE, IKeyConstants.USER_TYPE_PROVIDER);
//            jsonObject.put(IKeyConstants.KEY_GUIDE_TYPE, guideType);
            jsonObject.put(IKeyConstants.LAT, latitude);
            jsonObject.put(IKeyConstants.LNG, longitude
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public JSONObject getProviderJson() {
        return jsonObject;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode + ", data=" + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECTED_CAT) {
                subCatCsv = data.getStringExtra(IKeyConstants.SUB_CAT_CSV);
                try {
                    subcatJson = new JSONArray(data.getStringExtra(IKeyConstants.SUB_CAT_JSON));
                    Log.d("ProviderSignUp", "onActivityResult(): subcatJson=" + subcatJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String subCatNameCsv = data.getStringExtra(IKeyConstants.SUBCAT_NAME_CSV);
                if (subCatNameCsv != null) {
                    tvCatNames.setVisibility(View.VISIBLE);
                    tvCatNames.setText("(" + subCatNameCsv.replaceAll(IKeyConstants.COMMA, IKeyConstants.COMMA + " ") + ")");
                }
            }
        }
    }

    public void setLocationInView(Intent data) {
        Place place = PlaceAutocomplete.getPlace(signUpActivity, data);
        LatLng latLng = place.getLatLng();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        address = place.getAddress().toString();
        etAddress.setText(address);
    }
}
