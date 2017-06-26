package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.ebabu.tooreest.constant.IKeyConstants;


/**
 * Created by hp on 13/10/2016.
 */
public class Timing implements Parcelable {
    private String visit_id = IKeyConstants.EMPTY;
    private String from = IKeyConstants.EMPTY;
    private String to = IKeyConstants.EMPTY;

    public Timing(){}

    protected Timing(Parcel in) {
        visit_id = in.readString();
        from = in.readString();
        to = in.readString();
    }

    public static final Creator<Timing> CREATOR = new Creator<Timing>() {
        @Override
        public Timing createFromParcel(Parcel in) {
            return new Timing(in);
        }

        @Override
        public Timing[] newArray(int size) {
            return new Timing[size];
        }
    };

    public String getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(visit_id);
        parcel.writeString(from);
        parcel.writeString(to);
    }
}
