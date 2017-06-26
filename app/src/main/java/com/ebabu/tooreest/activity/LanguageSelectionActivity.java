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

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.City;
import com.ebabu.tooreest.beans.Country;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;

import java.util.List;

public class LanguageSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = LanguageSelectionActivity.class.getSimpleName();
    private Context context;
    private Spinner spinnerLanguage, spinnerCountry, spinnerCity;
    private Resources resources;
    private List<Country> listCountries;
    private CustomEditText etLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        context = LanguageSelectionActivity.this;
        resources = getResources();
        initView();
    }

    private void initView() {
        listCountries = Utils.getCountriesList(context);
        Log.d(TAG, "listCountries=" + listCountries);
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);
        etLanguages = (CustomEditText) findViewById(R.id.et_languages);
        findViewById(R.id.btn_next).setOnClickListener(this);

        ArrayAdapter<String> arrayAdapterLanguage = new ArrayAdapter<String>(context, R.layout.spinner_item, resources.getStringArray(R.array.array_language));
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

        ArrayAdapter<Country> arrayAdapterCountry = new ArrayAdapter<Country>(context, R.layout.spinner_item, listCountries);
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


//        ArrayAdapter<City> arrayAdapterCity = new ArrayAdapter<City>(context, R.layout.spinner_item, listCountries.get(0).getListCities());
//        spinnerCity.setAdapter(arrayAdapterCity);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                onNextBtnClicked();
                break;
        }
    }

    private void onNextBtnClicked() {
        String languages = etLanguages.getText().toString();
//        if (spinnerLanguage.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, getString(R.string.select_language), Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (spinnerCountry.getSelectedItemPosition() == 0) {
            Toast.makeText(context, getString(R.string.select_country), Toast.LENGTH_SHORT).show();
            return;
        }
        if (languages.isEmpty()) {
            etLanguages.setError(getString(R.string.enter_languages));
            return;
        }
//        if (spinnerCity.getSelectedItemPosition() == 0) {
//            Toast.makeText(context, getString(R.string.select_city), Toast.LENGTH_SHORT).show();
//            return;
//        }

        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra(IKeyConstants.KEY_LANGUAGE, languages);
        intent.putExtra(IKeyConstants.KEY_COUNTRY, (Country) spinnerCountry.getSelectedItem());
        intent.putExtra(IKeyConstants.KEY_CITY, (City) spinnerCity.getSelectedItem());
        startActivity(intent);
    }
}
