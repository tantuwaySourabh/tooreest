package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 15/10/2016.
 */
public class VisitDays implements Parcelable {
    private int day;
    private List<Timing> timearr = new ArrayList<>();

    public VisitDays(){}

    protected VisitDays(Parcel in) {
        day = in.readInt();
        timearr = in.createTypedArrayList(Timing.CREATOR);
    }

    public static final Creator<VisitDays> CREATOR = new Creator<VisitDays>() {
        @Override
        public VisitDays createFromParcel(Parcel in) {
            return new VisitDays(in);
        }

        @Override
        public VisitDays[] newArray(int size) {
            return new VisitDays[size];
        }
    };

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Timing> getTimearr() {
        return timearr;
    }

    public void setTimearr(List<Timing> timearr) {
        this.timearr = timearr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(day);
        parcel.writeTypedList(timearr);
    }
}
