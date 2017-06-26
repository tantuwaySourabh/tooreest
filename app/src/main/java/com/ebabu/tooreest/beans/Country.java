package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hp on 07/04/2017.
 */
public class Country implements Parcelable{
    private int countryId;
    private String countryName;
    private String mobileCode;
    private String currency;
    private String currencySymbol;
    private List<City> listCities;

    public Country(){}
    protected Country(Parcel in) {
        countryId = in.readInt();
        countryName = in.readString();
        mobileCode = in.readString();
        currency = in.readString();
        currencySymbol = in.readString();
        listCities = in.createTypedArrayList(City.CREATOR);
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public List<City> getListCities() {
        return listCities;
    }

    public void setListCities(List<City> listCities) {
        this.listCities = listCities;
    }

    @Override
    public String toString() {
        return countryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(countryId);
        parcel.writeString(countryName);
        parcel.writeString(mobileCode);
        parcel.writeString(currency);
        parcel.writeString(currencySymbol);
        parcel.writeTypedList(listCities);
    }

    @Override
    public boolean equals(Object obj) {
        Country country = (Country) obj;
        if (this.countryName.equalsIgnoreCase(country.getCountryName())) {
            return true;
        }
        return false;
    }
}
