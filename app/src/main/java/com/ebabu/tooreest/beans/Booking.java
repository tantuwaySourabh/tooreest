package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.ebabu.tooreest.constant.IKeyConstants;

/**
 * Created by hp on 10/01/2017.
 */
public class Booking implements Parcelable {
    private String service_id;
    private String user_id;
    private String full_name;
    private String company_name;
    private String email;
    private String mobile_no;
    private String address;
    private String image;
    private String service_image;
    private String country;
    private String city;
    private String order_id;
    private String sub_category;
    private int service_amt;
    private int total_amount;
    private String booking_status;
    private String booking_date_time;
    private String provider_id;
    private String sub_cat_id;
    private int service_type;
    private String service_description;
    private String create_at;
    private int rating;
    private String review;
    private String booking_code;
    private String coupon_code;

    protected Booking(Parcel in) {
        service_id = in.readString();
        user_id = in.readString();
        full_name = in.readString();
        company_name = in.readString();
        email = in.readString();
        mobile_no = in.readString();
        address = in.readString();
        image = in.readString();
        service_image = in.readString();
        country = in.readString();
        city = in.readString();
        order_id = in.readString();
        sub_category = in.readString();
        service_amt = in.readInt();
        total_amount = in.readInt();
        booking_status = in.readString();
        booking_date_time = in.readString();
        provider_id = in.readString();
        sub_cat_id = in.readString();
        service_type = in.readInt();
        service_description = in.readString();
        create_at = in.readString();
        rating = in.readInt();
        review = in.readString();
        booking_code = in.readString();
        coupon_code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_id);
        dest.writeString(user_id);
        dest.writeString(full_name);
        dest.writeString(company_name);
        dest.writeString(email);
        dest.writeString(mobile_no);
        dest.writeString(address);
        dest.writeString(image);
        dest.writeString(service_image);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(order_id);
        dest.writeString(sub_category);
        dest.writeInt(service_amt);
        dest.writeInt(total_amount);
        dest.writeString(booking_status);
        dest.writeString(booking_date_time);
        dest.writeString(provider_id);
        dest.writeString(sub_cat_id);
        dest.writeInt(service_type);
        dest.writeString(service_description);
        dest.writeString(create_at);
        dest.writeInt(rating);
        dest.writeString(review);
        dest.writeString(booking_code);
        dest.writeString(coupon_code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getService_image() {
        return service_image;
    }

    public void setService_image(String service_image) {
        this.service_image = service_image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public int getService_amt() {
        return service_amt;
    }

    public void setService_amt(int service_amt) {
        this.service_amt = service_amt;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getBooking_status() {
        if (booking_status != null) {
            return (booking_status.charAt(0) + IKeyConstants.EMPTY).toUpperCase() + booking_status.toLowerCase().substring(1);
        }
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getBooking_date_time() {
        return booking_date_time;
    }

    public void setBooking_date_time(String booking_date_time) {
        this.booking_date_time = booking_date_time;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public int getService_type() {
        return service_type;
    }

    public void setService_type(int service_type) {
        this.service_type = service_type;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getBooking_code() {
        return booking_code;
    }

    public void setBooking_code(String booking_code) {
        this.booking_code = booking_code;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "service_id='" + service_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", full_name='" + full_name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", email='" + email + '\'' +
                ", mobile_no='" + mobile_no + '\'' +
                ", address='" + address + '\'' +
                ", image='" + image + '\'' +
                ", service_image='" + service_image + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", order_id='" + order_id + '\'' +
                ", sub_category='" + sub_category + '\'' +
                ", service_amt=" + service_amt +
                ", booking_status='" + booking_status + '\'' +
                ", booking_date_time='" + booking_date_time + '\'' +
                ", provider_id='" + provider_id + '\'' +
                ", sub_cat_id='" + sub_cat_id + '\'' +
                ", service_type=" + service_type +
                ", service_description='" + service_description + '\'' +
                ", create_at='" + create_at + '\'' +
                '}';
    }
}
