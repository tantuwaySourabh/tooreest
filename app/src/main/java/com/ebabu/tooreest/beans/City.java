package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by hp on 07/04/2017.
 */
public class City implements Parcelable {
    private int countryId;
    private int cityId;
    private String cityName;

    public City() {
    }

    protected City(Parcel in) {
        countryId = in.readInt();
        cityId = in.readInt();
        cityName = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return cityName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(countryId);
        parcel.writeInt(cityId);
        parcel.writeString(cityName);
    }

    @Override
    public boolean equals(Object obj) {
        City city = (City) obj;
        Log.d("City", "city=" + city.cityName + ", this.city=" + this.cityName);
        if (this.cityName.equalsIgnoreCase(city.getCityName().trim())) {
            return true;
        }
        return false;
    }
}
