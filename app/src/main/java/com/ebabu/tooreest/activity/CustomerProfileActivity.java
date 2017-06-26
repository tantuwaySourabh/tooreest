package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = CustomerProfileActivity.class.getSimpleName();
    private Context context;
    private CustomTextView tvProfilePicBg, btnEditSave;
    private CustomEditText tvName, tvAboutMe, tvMobileNum, tvEmail, tvAddress;
    private CircleImageView ivProfilePic;
    private ImageUtils imageUtils;
    private String fullname, aboutMe, mobileNum, email, address;
    private Spinner spinnerLanguage, spinnerCountry, spinnerCity;
    private Resources resources;
    private ArrayAdapter<String> arrayAdapterLanguage;
    private ArrayAdapter<Country> arrayAdapterCountry;
    private List<Country> listCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        context = CustomerProfileActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.my_profile_small));
    }

    private void initView() {
        listCountries = Utils.getCountriesList(context);
        resources = getResources();
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);

        ivProfilePic = (CircleImageView) findViewById(R.id.tv_profile_pic);
        ivProfilePic.setEnabled(false);
        tvName = (CustomEditText) findViewById(R.id.tv_name);
        tvProfilePicBg = (CustomTextView) findViewById(R.id.tv_profile_pic_bg);
        tvAboutMe = (CustomEditText) findViewById(R.id.et_about_me);
        tvMobileNum = (CustomEditText) findViewById(R.id.et_phone);
        tvEmail = (CustomEditText) findViewById(R.id.et_email);
        tvAddress = (CustomEditText) findViewById(R.id.et_location);
        btnEditSave = (CustomTextView) findViewById(R.id.btn_edit_profile);

        btnEditSave.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);

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

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                ArrayAdapter<City> arrayAdapterCity = new ArrayAdapter<City>(context, R.layout.spinner_item, listCountries.get(pos).getListCities());
                spinnerCity.setAdapter(arrayAdapterCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        arrayAdapterCity = new ArrayAdapter<String>(context, R.layout.spinner_item, resources.getStringArray(R.array.array_city));
//        spinnerCity.setAdapter(arrayAdapterCity);

        spinnerLanguage.setEnabled(false);

        spinnerCity.setEnabled(false);

        spinnerCountry.setEnabled(false);
    }

    private void setProfileDataInView() {
        String imageUrl = AppPreference.getInstance(context).getProfileImage();
        String name = AppPreference.getInstance(context).getFullName();

        if (name != null && !name.isEmpty()) {
            tvName.setText(name);
        } else {
            tvName.setText(IKeyConstants.EMPTY);
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

        if (AppPreference.getInstance(context).getLanguage() != null && !AppPreference.getInstance(context).getLanguage().isEmpty()) {
            spinnerLanguage.setSelection(arrayAdapterLanguage.getPosition(AppPreference.getInstance(context).getLanguage()));
        }

        if (AppPreference.getInstance(context).getCountry() != null && !AppPreference.getInstance(context).getCountry().isEmpty()) {
            Country country = new Country();
            country.setCountryName(AppPreference.getInstance(context).getCountry());
            spinnerCountry.setSelection(arrayAdapterCountry.getPosition(country));

            if (AppPreference.getInstance(context).getCity() != null && !AppPreference.getInstance(context).getCity().isEmpty()) {
                City city = new City();
                city.setCityName(AppPreference.getInstance(context).getCity());
                List<City> listCities = listCountries.get(arrayAdapterCountry.getPosition(country)).getListCities();
                for (int i = 0; i < listCities.size(); i++) {
                    if (listCities.get(i).getCityName().equalsIgnoreCase(AppPreference.getInstance(context).getCity())) {
                        spinnerCity.setSelection(i);
                        break;
                    }
                }
            }
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

        ivProfilePic.setEnabled(enabled);
        tvName.setEnabled(enabled);
        tvAboutMe.setEnabled(enabled);
        //tvMobileNum.setEnabled(enabled);
        //tvEmail.setEnabled(enabled);
        tvAddress.setEnabled(enabled);

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
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }

    public boolean areFieldsValid() {
        fullname = tvName.getText().toString();
        mobileNum = tvMobileNum.getText().toString();
        email = tvEmail.getText().toString();
        aboutMe = tvAboutMe.getText().toString();
        address = tvAddress.getText().toString();

        if (fullname.isEmpty()) {
            tvName.setError(getString(R.string.enter_name));
            return false;
        }

        if (!Utils.isNameValid(fullname)) {
            tvName.setError(getString(R.string.enter_a_valid_name));
            return false;
        }

        if (address.isEmpty()) {
            tvAddress.setError(getString(R.string.enter_address));
            return false;
        }

        if (aboutMe.isEmpty()) {
            tvAboutMe.setError(getString(R.string.write_about_you));
            return false;
        }

        if (spinnerLanguage.getSelectedItemPosition() == 0) {
            Toast.makeText(context, getString(R.string.select_language), Toast.LENGTH_SHORT).show();
            return false;
        }

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
        params.put(IKeyConstants.KEY_LANGUAGE, (String) spinnerLanguage.getSelectedItem());
        params.put(IKeyConstants.KEY_COUNTRY, ((Country) spinnerCountry.getSelectedItem()).getCountryName());
        params.put(IKeyConstants.KEY_CITY, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_COMPANY_NAME, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_FULL_NAME, fullname);
        params.put(IKeyConstants.KEY_USERNAME, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_MOBILE, mobileNum);
        params.put(IKeyConstants.KEY_EMAIL, email);
        params.put(IKeyConstants.KEY_ADDRESS, address);
        params.put(IKeyConstants.KEY_POSTAL_CODE, IKeyConstants.EMPTY);
        params.put(IKeyConstants.KEY_IMAGE, imageUtils.getByteArray());
        params.put(IKeyConstants.KEY_ABOUT_ME, aboutMe);

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

                            AppPreference.getInstance(context).setLanguage((String) spinnerLanguage.getSelectedItem());
                            AppPreference.getInstance(context).setCountry(((Country) spinnerCountry.getSelectedItem()).getCountryName());
                            AppPreference.getInstance(context).setCity(((City) spinnerCity.getSelectedItem()).getCityName());
                            AppPreference.getInstance(context).setProfileImage(jsonObject.getString("image"));

                            Utils.setAppLanguage(context, AppPreference.getInstance(context).getLanguage());
//                            setResult(RESULT_OK);
//                            finish();
                            Intent intent = new Intent(context, CustomerHomeActivity.class);
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
