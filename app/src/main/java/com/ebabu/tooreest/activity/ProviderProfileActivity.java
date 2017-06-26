package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.City;
import com.ebabu.tooreest.beans.Country;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.ImageUtils;
import com.ebabu.tooreest.utils.Utils;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ProviderProfileActivity.class.getSimpleName();
    private Context context;
    private CustomTextView tvProfilePicBg, btnEditSave, tvAddress;
    private CustomEditText etLanguages, tvName, tvAboutMe, tvMobileNum, tvEmail, tvPostalCode, tvCompanyName, etFbLink, etGplusLink, etTwitterLink, etLinkedinLink;
    private CircleImageView ivProfilePic;
    private ImageUtils imageUtils;
    private String fullname, aboutMe, mobileNum, email, address, companyName, postalCode, guideType, languages;
    private Spinner spinnerLanguage, spinnerCountry, spinnerCity;
    private Resources resources;
    private ArrayAdapter<String> arrayAdapterLanguage;
    private ArrayAdapter<Country> arrayAdapterCountry;
    //private ArrayAdapter<City> arrayAdapterCity;
    private List<Country> listCountries;
    private RadioGroup rgGuideType;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_profile);
        context = ProviderProfileActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.my_profile_small));
    }

    private void initView() {
        listCountries = Utils.getCountriesList(context);
        resources = getResources();
        rgGuideType = (RadioGroup) findViewById(R.id.rg_guide_type);
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);
        ivProfilePic = (CircleImageView) findViewById(R.id.tv_profile_pic);
        ivProfilePic.setEnabled(false);
        tvName = (CustomEditText) findViewById(R.id.tv_name);
        tvPostalCode = (CustomEditText) findViewById(R.id.et_postal_code);
        tvCompanyName = (CustomEditText) findViewById(R.id.et_company_name);
        tvProfilePicBg = (CustomTextView) findViewById(R.id.tv_profile_pic_bg);
        etLanguages = (CustomEditText) findViewById(R.id.et_languages);
        tvAboutMe = (CustomEditText) findViewById(R.id.et_about_me);
        tvMobileNum = (CustomEditText) findViewById(R.id.et_phone);
        tvEmail = (CustomEditText) findViewById(R.id.et_email);
        tvAddress = (CustomTextView) findViewById(R.id.et_location);

        etFbLink = (CustomEditText) findViewById(R.id.et_fb_link);
        etGplusLink = (CustomEditText) findViewById(R.id.et_gplus_link);
        etTwitterLink = (CustomEditText) findViewById(R.id.et_twitter_link);
        etLinkedinLink = (CustomEditText) findViewById(R.id.et_linkedin_link);

        btnEditSave = (CustomTextView) findViewById(R.id.btn_edit_profile);

        btnEditSave.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);
        rgGuideType.setEnabled(false);
        tvAddress.setOnClickListener(this);
        imageUtils = new ImageUtils(context, ivProfilePic);
        initSpinners();
        setProfileDataInView();
    }

    private void initSpinners() {

        arrayAdapterLanguage = new ArrayAdapter<String>(context, R.layout.spinner_item, resources.getStringArray(R.array.array_language));
        spinnerLanguage.setAdapter(arrayAdapterLanguage);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomTextView textView = (CustomTextView) view;
                if (pos == 0) {
                    textView.setTextColor(resources.getColor(R.color.divider_color));
                } else {
                    textView.setTextColor(resources.getColor(R.color.primary_text_color));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<City> arrayAdapterCity = new ArrayAdapter<City>(context, R.layout.spinner_item, listCountries.get(0).getListCities());
        spinnerCity.setAdapter(arrayAdapterCity);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomTextView textView = (CustomTextView) view;
                if (textView != null) {
                    if (pos == 0) {
                        textView.setTextColor(resources.getColor(R.color.divider_color));
                    } else {
                        textView.setTextColor(resources.getColor(R.color.primary_text_color));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        arrayAdapterCountry = new ArrayAdapter<Country>(context, R.layout.spinner_item, listCountries);
        spinnerCountry.setAdapter(arrayAdapterCountry);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomTextView textView = (CustomTextView) view;
                if (pos == 0) {
                    textView.setTextColor(resources.getColor(R.color.divider_color));
                } else {
                    textView.setTextColor(resources.getColor(R.color.primary_text_color));
                }
                Log.d(TAG, "selectedCountryPos=" + listCountries.get(pos).getListCities());
                ArrayAdapter<City> arrayAdapterCity = new ArrayAdapter<City>(context, R.layout.spinner_item, listCountries.get(pos).getListCities());
                spinnerCity.setAdapter(arrayAdapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerLanguage.setEnabled(false);
        spinnerCity.setEnabled(false);
        spinnerCountry.setEnabled(false);

        rgGuideType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                guideType = (String) radioGroup.findViewById(i).getTag();
            }
        });
    }

    private void setProfileDataInView() {
        String imageUrl = AppPreference.getInstance(context).getProfileImage();
        String name = AppPreference.getInstance(context).getFullName();
        String companyName = AppPreference.getInstance(context).getCompanyName();
        String postalCode = AppPreference.getInstance(context).getPostalCode();
        guideType = AppPreference.getInstance(context).getGuideType();
        latitude = AppPreference.getInstance(context).getProviderLatitude();
        longitude = AppPreference.getInstance(context).getProviderLongitude();
        languages = AppPreference.getInstance(context).getLanguage();

        if (name != null && !name.isEmpty()) {
            tvName.setText(name);
        } else {
            tvName.setText(IKeyConstants.EMPTY);
        }

//        if (companyName != null && !companyName.isEmpty()) {
//            tvCompanyName.setText(companyName);
//        } else {
//            tvCompanyName.setText(IKeyConstants.EMPTY);
//        }

        if (postalCode != null && !postalCode.isEmpty()) {
            tvPostalCode.setText(postalCode);
        } else {
            tvPostalCode.setText(IKeyConstants.EMPTY);
        }

        if (imageUrl != null && imageUrl.startsWith("http")) {
            new AQuery(context).id(ivProfilePic).image(imageUrl);
        } else {
            tvProfilePicBg.setText(name.charAt(0) + IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getAboutMe() != null && !AppPreference.getInstance(context).getAboutMe().isEmpty()) {
            tvAboutMe.setText(AppPreference.getInstance(context).getAboutMe());
        } else {
            tvAboutMe.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getEmail() != null && !AppPreference.getInstance(context).getEmail().isEmpty()) {
            tvEmail.setText(AppPreference.getInstance(context).getEmail());
        } else {
            tvEmail.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getMobileNum() != null && !AppPreference.getInstance(context).getMobileNum().isEmpty()) {
            tvMobileNum.setText(IKeyConstants.PLUS_SIGN + AppPreference.getInstance(context).getMobileNum());
        } else {
            tvMobileNum.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getAddress() != null && !AppPreference.getInstance(context).getAddress().isEmpty()) {
            tvAddress.setText(AppPreference.getInstance(context).getAddress());
        } else {
            tvAddress.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getFbLink() != null && !AppPreference.getInstance(context).getFbLink().isEmpty()) {
            etFbLink.setText(AppPreference.getInstance(context).getFbLink());
        } else {
            etFbLink.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getGplusLink() != null && !AppPreference.getInstance(context).getGplusLink().isEmpty()) {
            etGplusLink.setText(AppPreference.getInstance(context).getGplusLink());
        } else {
            etGplusLink.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getTwitterLink() != null && !AppPreference.getInstance(context).getTwitterLink().isEmpty()) {
            etTwitterLink.setText(AppPreference.getInstance(context).getTwitterLink());
        } else {
            etTwitterLink.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getLinkedinLink() != null && !AppPreference.getInstance(context).getLinkedinLink().isEmpty()) {
            etLinkedinLink.setText(AppPreference.getInstance(context).getLinkedinLink());
        } else {
            etLinkedinLink.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getLanguage() != null && !AppPreference.getInstance(context).getLanguage().isEmpty()) {
            etLanguages.setText(AppPreference.getInstance(context).getLanguage());
        } else {
            etLanguages.setText(IKeyConstants.EMPTY);
        }

        if (AppPreference.getInstance(context).getLanguage() != null && !AppPreference.getInstance(context).getLanguage().isEmpty()) {
            spinnerLanguage.setSelection(arrayAdapterLanguage.getPosition(AppPreference.getInstance(context).getLanguage()));
        }

        if (AppPreference.getInstance(context).getCountry() != null && !AppPreference.getInstance(context).getCountry().isEmpty()) {
            Country country = new Country();
            country.setCountryName(AppPreference.getInstance(context).getCountry());
            spinnerCountry.setSelection(arrayAdapterCountry.getPosition(country));
        }

        if (AppPreference.getInstance(context).getCity() != null && !AppPreference.getInstance(context).getCity().isEmpty()) {
            City city = new City();
            city.setCityName(AppPreference.getInstance(context).getCity());
            spinnerCity.setSelection(((ArrayAdapter<City>) spinnerCity.getAdapter()).getPosition(city));
        }

        Log.d(TAG, "guideType=" + guideType);
        if (guideType != null) {
            ((AppCompatRadioButton) rgGuideType.findViewWithTag(guideType)).setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_profile:
                onEditSaveBtnClicked();
                break;

            case R.id.tv_profile_pic:
                imageUtils.openDialogToChosePic();
                break;

            case R.id.et_location:
                Utils.startAddressActivity(context, Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP);
                break;
        }
    }

    private void onEditSaveBtnClicked() {
        if (btnEditSave.getText().toString().equalsIgnoreCase(getString(R.string.edit_profile))) {
            enableDisableFields(true);
            btnEditSave.setText(getString(R.string.update_profile));
        } else {
            if (areFieldsValid()) {
                updateProfile();
            }
//            enableDisableFields(false);
//            btnEditSave.setText(getString(R.string.edit_profile));
        }
    }

    private void enableDisableFields(boolean enabled) {
        if (enabled) {
            tvName.setBackgroundResource(R.drawable.bottom_line_gray);
            //tvAboutMe.setBackgroundResource(R.drawable.bottom_line_gray);
            //tvMobileNum.setBackgroundResource(R.drawable.bottom_line_gray);
            //tvEmail.setBackgroundResource(R.drawable.bottom_line_gray);
        } else {
            tvName.setBackgroundResource(android.R.color.transparent);
            //tvAboutMe.setBackgroundResource(android.R.color.transparent);
            //tvMobileNum.setBackgroundResource(android.R.color.transparent);
            //tvEmail.setBackgroundResource(android.R.color.transparent);
        }
        rgGuideType.setEnabled(enabled);
        ivProfilePic.setEnabled(enabled);
        tvName.setEnabled(enabled);
        tvAboutMe.setEnabled(enabled);
        tvPostalCode.setEnabled(enabled);
        tvCompanyName.setEnabled(enabled);
        tvAddress.setEnabled(enabled);
        etLanguages.setEnabled(enabled);
        etFbLink.setEnabled(enabled);
        etGplusLink.setEnabled(enabled);
        etTwitterLink.setEnabled(enabled);
        etLinkedinLink.setEnabled(enabled);
        //((Spinner) spinnerLanguage).getSelectedView().setEnabled(enabled);
        spinnerLanguage.setEnabled(enabled);

        //((Spinner) spinnerCity).getSelectedView().setEnabled(enabled);
        spinnerCity.setEnabled(enabled);

        //((Spinner) spinnerCountry).getSelectedView().setEnabled(enabled);
        spinnerCountry.setEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE_PICKUP) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                address = place.getAddress().toString();
                tvAddress.setText(address);
            } else {
                imageUtils.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    public boolean areFieldsValid() {
        fullname = tvName.getText().toString();
        mobileNum = tvMobileNum.getText().toString();
        email = tvEmail.getText().toString();
        aboutMe = tvAboutMe.getText().toString();
        address = tvAddress.getText().toString();
        postalCode = tvPostalCode.getText().toString();
        companyName = tvCompanyName.getText().toString();
        languages = etLanguages.getText().toString();

        if (fullname.isEmpty()) {
            tvName.setError(getString(R.string.enter_name));
            return false;
        }

        if (!Utils.isNameValid(fullname)) {
            tvName.setError(getString(R.string.enter_a_valid_name));
            return false;
        }

//        if (companyName.isEmpty()) {
//            tvCompanyName.setError(getString(R.string.enter_company_name));
//            return false;
//        }

//        if (!Utils.isNameValid(companyName)) {
//            tvCompanyName.setError(getString(R.string.enter_a_valid_company));
//            return false;
//        }

//        if (postalCode.isEmpty()) {
//            tvPostalCode.setError(getString(R.string.enter_pincode));
//            return false;
//        }

//        if (postalCode.length() < IKeyConstants.MIN_PINCODE_LENGTH) {
//            tvPostalCode.setError(getString(R.string.pincode_must_be_n_digits, IKeyConstants.MIN_PINCODE_LENGTH));
//            return false;
//        }

        if (address.isEmpty()) {
            tvAddress.setError(getString(R.string.enter_address));
            return false;
        }

        if (aboutMe.isEmpty()) {
            tvAboutMe.setError(getString(R.string.write_about_you));
            return false;
        }

        if (languages.isEmpty()) {
            etLanguages.setError(getString(R.string.enter_languages));
            return false;
        }

//        if (spinnerLanguage.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, getString(R.string.select_language), Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (spinnerCountry.getSelectedItemPosition() == 0) {
            Toast.makeText(context, getString(R.string.select_country), Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (spinnerCity.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, getString(R.string.select_city), Toast.LENGTH_SHORT).show();
//            return false;
//        }

        return true;
    }

    private void updateProfile() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        Map<String, Object> params = new HashMap<>();
        params.put(IKeyConstants.KEY_LANGUAGE, languages);
        params.put(IKeyConstants.KEY_COUNTRY, ((Country) spinnerCountry.getSelectedItem()).getCountryName());
        params.put(IKeyConstants.KEY_CITY, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_COMPANY_NAME, fullname);
        params.put(IKeyConstants.KEY_FULL_NAME, fullname);
        params.put(IKeyConstants.KEY_USERNAME, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_MOBILE, mobileNum);
        params.put(IKeyConstants.KEY_EMAIL, email);
        params.put(IKeyConstants.KEY_ADDRESS, address);
        params.put(IKeyConstants.KEY_POSTAL_CODE, postalCode);
        if (imageUtils.getByteArray() != null) {
            params.put(IKeyConstants.KEY_IMAGE, imageUtils.getByteArray());
        } else {
            params.put(IKeyConstants.KEY_IMAGE, IKeyConstants.EMPTY);
        }
        params.put(IKeyConstants.KEY_ABOUT_ME, aboutMe);

        params.put(IKeyConstants.KEY_FACEBOOK, etFbLink.getText().toString());
        params.put(IKeyConstants.KEY_GPLUS, etGplusLink.getText().toString());
        params.put(IKeyConstants.KEY_TWITTER, etTwitterLink.getText().toString());
        params.put(IKeyConstants.KEY_LINKEDIN, etLinkedinLink.getText().toString());
        params.put(IKeyConstants.LAT, latitude);
        params.put(IKeyConstants.LNG, longitude);
        //params.put(IKeyConstants.KEY_GUIDE_TYPE, guideType);

        Log.d(TAG, "updateProfile(): params=" + params);

        new AQuery(context).ajax(IUrlConstants.UPDATE_PROFILE, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "updateProfile(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONObject jsonObject = json.getJSONObject("data");

                            AppPreference.getInstance(context).setFullName(fullname);
                            AppPreference.getInstance(context).setAddress(address);
                            AppPreference.getInstance(context).setAboutMe(aboutMe);
                            AppPreference.getInstance(context).setCompanyName(fullname);
                            AppPreference.getInstance(context).setPostalCode(postalCode);
                            AppPreference.getInstance(context).setProfileImage(jsonObject.getString("image"));
                            //AppPreference.getInstance(context).setGuideType(guideType);
                            AppPreference.getInstance(context).setFbLink(etFbLink.getText().toString());
                            AppPreference.getInstance(context).setGplusLink(etGplusLink.getText().toString());
                            AppPreference.getInstance(context).setTwitterLink(etTwitterLink.getText().toString());
                            AppPreference.getInstance(context).setLinkedinLink(etLinkedinLink.getText().toString());

                            AppPreference.getInstance(context).setLanguage(languages);
                            AppPreference.getInstance(context).setCountry(((Country) spinnerCountry.getSelectedItem()).getCountryName());
                            AppPreference.getInstance(context).setCity(((City) spinnerCity.getSelectedItem()).getCityName());
                            AppPreference.getInstance(context).setProviderLatitude(latitude);
                            AppPreference.getInstance(context).setProviderLongitude(longitude);
                            Utils.setAppLanguage(context, AppPreference.getInstance(context).getLanguage());
//                            setResult(RESULT_OK);
//                            finish();
                            Intent intent = new Intent(context, ProviderHomeActivity.class);
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
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
