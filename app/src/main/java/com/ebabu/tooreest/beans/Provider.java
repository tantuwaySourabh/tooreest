package com.ebabu.tooreest.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hp on 16/01/2017.
 */
public class Provider implements Parcelable {

    private String user_id;
    private String company_name;
    private String full_name;
    private String mobile_no;
    private String email;
    private String address;
    private String postal_code;
    private String city;
    private String language;
    private String country;
    private String about_me;
    private String image;
    private float ave_rating;
    private String price;
    private int projectdone;
    private String facebook;
    private String google_plus;
    private String twitter;
    private String linkedin;
    private int is_id_verify;
    private int is_bank_verify;
    private int is_favorite;
    private String create_at;


    public Provider() {
    }

    protected Provider(Parcel in) {
        user_id = in.readString();
        company_name = in.readString();
        full_name = in.readString();
        mobile_no = in.readString();
        email = in.readString();
        address = in.readString();
        postal_code = in.readString();
        city = in.readString();
        language = in.readString();
        country = in.readString();
        about_me = in.readString();
        image = in.readString();
        ave_rating = in.readFloat();
        price = in.readString();
        projectdone = in.readInt();
        facebook = in.readString();
        google_plus = in.readString();
        twitter = in.readString();
        linkedin = in.readString();
        is_id_verify = in.readInt();
        is_bank_verify = in.readInt();
        is_favorite = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(company_name);
        dest.writeString(full_name);
        dest.writeString(mobile_no);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(postal_code);
        dest.writeString(city);
        dest.writeString(language);
        dest.writeString(country);
        dest.writeString(about_me);
        dest.writeString(image);
        dest.writeFloat(ave_rating);
        dest.writeString(price);
        dest.writeInt(projectdone);
        dest.writeString(facebook);
        dest.writeString(google_plus);
        dest.writeString(twitter);
        dest.writeString(linkedin);
        dest.writeInt(is_id_verify);
        dest.writeInt(is_bank_verify);
        dest.writeInt(is_favorite);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getRating() {
        return ave_rating;
    }

    public void setRating(float ave_rating) {
        this.ave_rating = ave_rating;
    }

    public String getCharges() {
        return price;
    }

    public void setCharges(String price) {
        this.price = price;
    }

    public int getProjectdone() {
        return projectdone;
    }

    public void setProjectdone(int projectdone) {
        this.projectdone = projectdone;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGoogle_plus() {
        return google_plus;
    }

    public void setGoogle_plus(String google_plus) {
        this.google_plus = google_plus;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public int getIs_id_verify() {
        return is_id_verify;
    }

    public void setIs_id_verify(int is_id_verify) {
        this.is_id_verify = is_id_verify;
    }

    public int getIs_bank_verify() {
        return is_bank_verify;
    }

    public void setIs_bank_verify(int is_bank_verify) {
        this.is_bank_verify = is_bank_verify;
    }

    public int getIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(int is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
