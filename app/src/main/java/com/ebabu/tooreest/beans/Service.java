package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 27/02/2017.
 */
public class Service implements Parcelable{
    private String service_id;
    private String cat_id;
    private String sub_cat_id;
    private String sub_cat_name;
    private int price;
    private int projectdone;
    private float ave_rating;
    private String image;
    private int service_type;
    private String description;


    public Service(){}

    protected Service(Parcel in) {
        service_id = in.readString();
        cat_id = in.readString();
        sub_cat_id = in.readString();
        sub_cat_name = in.readString();
        price = in.readInt();
        projectdone = in.readInt();
        ave_rating = in.readFloat();
        image = in.readString();
        service_type = in.readInt();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_id);
        dest.writeString(cat_id);
        dest.writeString(sub_cat_id);
        dest.writeString(sub_cat_name);
        dest.writeInt(price);
        dest.writeInt(projectdone);
        dest.writeFloat(ave_rating);
        dest.writeString(image);
        dest.writeInt(service_type);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public void setSub_cat_name(String sub_cat_name) {
        this.sub_cat_name = sub_cat_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProjectdone() {
        return projectdone;
    }

    public void setProjectdone(int projectdone) {
        this.projectdone = projectdone;
    }

    public float getAve_rating() {
        return ave_rating;
    }

    public void setAve_rating(float ave_rating) {
        this.ave_rating = ave_rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        Service service = (Service) obj;
        if (service.getSub_cat_id().equals(this.sub_cat_id)) {
            return true;
        }
        return false;
    }

}
