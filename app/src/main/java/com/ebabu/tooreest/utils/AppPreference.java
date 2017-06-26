package com.ebabu.tooreest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ebabu.tooreest.constant.IKeyConstants;

/**
 * Created by hp on 16/09/2016.
 */
public class AppPreference {
    private static AppPreference appPreference;
    private Context context;
    private SharedPreferences sharedPreferences;

    private final static String PREFERENCE_NAME = "AppPreference";

    private final static String USER_TYPE = "USER_TYPE";
    private final static String SECRET_KEY = "SECRET_KEY";
    private final static String IS_OTP_SCREEN = "IS_OTP_SCREEN";

    private final static String USER_ID = "USER_ID";
    private final static String COMPANY_NAME = "COMPANY_NAME";
    private final static String FULL_NAME = "FULL_NAME";
    private final static String MOBILE_NUM = "MOBILE_NUM";
    private final static String EMAIL = "EMAIL";
    private final static String PASSWORD = "PASSWORD";
    private final static String ADDRESS = "ADDRESS";
    private final static String POSTAL_CODE = "POSTAL_CODE";
    private final static String SUB_CAT = "SUB_CAT";
    private final static String LANGUAGE = "LANGUAGE";
    private final static String COUNTRY = "COUNTRY";
    private final static String CITY = "CITY";
    private final static String ABOUT_ME = "ABOUT_ME";
    private final static String PROFILE_IMAGE = "PROFILE_IMAGE";

    private final static String FB_LINK = "FB_LINK";
    private final static String TWITTER_LINK = "TWITTER_LINK";
    private final static String GPLUS_LINK = "GPLUS_LINK";
    private final static String LINKEDIN_LINK = "LINKEDIN_LINK";
    private final static String ID_PROOF_URL = "ID_PROOF_URL";
    private final static String BANK_ID_URL = "BANK_ID_URL";
    private final static String IS_BANK_VERIFIED = "IS_BANK_VERIFIED";
    private final static String IS_ID_VERIFIED = "IS_ID_VERIFIED";
    private final static String FCM_TOKEN = "FCM_TOKEN";
    private final static String GUIDE_TYPE = "GUIDE_TYPE";
    private final static String IS_CHAT_NOTI_ON = "IS_CHAT_NOTI_ON";
    private final static String IS_OTHER_NOTI_ON = "IS_OTHER_NOTI_ON";

    private final static String ADDRESS_PROOF_URL = "ADDRESS_PROOF_URL";
    private final static String BANK_NAME = "BANK_NAME";
    private final static String BANK_ACCOUNT_NUM = "BANK_ACCOUNT_NUM";
    private final static String BIC_CODE = "BIC_CODE";
    private final static String IS_LICENSED_GUIDE = "IS_LICENSED_GUIDE";
    private final static String CERTIFICATE_URL = "CERTIFICATE_URL";
    private final static String LICENSE_NUMBER = "LICENSE_NUMBER";
    private final static String AVAILABILITY_JSON = "AVAILABILITY_JSON";
    private final static String PROVIDER_LATITUDE = "PROVIDER_LATITUDE";
    private final static String PROVIDER_LONGITUDE = "PROVIDER_LONGITUDE";

    private final static String BRANCH_CODE = "BRANCH_CODE";
    private final static String PAYPAL_ID = "PAYPAL_ID";
    private final static String HAS_PAYPAL_ID = "HAS_PAYPAL_ID";

    private AppPreference(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static AppPreference getInstance(Context context) {
        return new AppPreference(context);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getUserType() {
        String userType = sharedPreferences.getString(USER_TYPE, null);
        return userType;
    }

    public void setUserType(String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TYPE, userType);
        editor.commit();
    }

    public String getSecretKey() {
        String userType = sharedPreferences.getString(SECRET_KEY, null);
        return userType;
    }

    public void setSecretKey(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SECRET_KEY, secretKey);
        editor.commit();
    }

    public boolean isOtpScreen() {
        boolean isOtpScreen = sharedPreferences.getBoolean(IS_OTP_SCREEN, false);
        return isOtpScreen;
    }

    public void setIsOtpScreen(boolean isOtpScreen) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_OTP_SCREEN, isOtpScreen);
        editor.commit();
    }

    public String getUserId() {
        String userType = sharedPreferences.getString(USER_ID, null);
        return userType;
    }

    public void setUserId(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, secretKey);
        editor.commit();
    }

    public String getCompanyName() {
        String userType = sharedPreferences.getString(COMPANY_NAME, null);
        return userType;
    }

    public void setCompanyName(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMPANY_NAME, secretKey);
        editor.commit();
    }

    public String getFullName() {
        String userType = sharedPreferences.getString(FULL_NAME, null);
        return userType;
    }

    public void setFullName(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FULL_NAME, secretKey);
        editor.commit();
    }

    public String getMobileNum() {
        String userType = sharedPreferences.getString(MOBILE_NUM, null);
        return userType;
    }

    public void setMobileNum(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_NUM, secretKey);
        editor.commit();
    }

    public String getEmail() {
        String userType = sharedPreferences.getString(EMAIL, null);
        return userType;
    }

    public void setEmail(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, secretKey);
        editor.commit();
    }

    public String getAboutMe() {
        String userType = sharedPreferences.getString(ABOUT_ME, null);
        return userType;
    }

    public void setAboutMe(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ABOUT_ME, secretKey);
        editor.commit();
    }

    public String getPassword() {
        String userType = sharedPreferences.getString(PASSWORD, null);
        return userType;
    }

    public void setPassword(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD, secretKey);
        editor.commit();
    }

    public String getAddress() {
        String userType = sharedPreferences.getString(ADDRESS, IKeyConstants.EMPTY);
        return userType;
    }

    public void setAddress(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS, secretKey);
        editor.commit();
    }

    public String getPostalCode() {
        String userType = sharedPreferences.getString(POSTAL_CODE, IKeyConstants.EMPTY);
        return userType;
    }

    public void setPostalCode(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(POSTAL_CODE, secretKey);
        editor.commit();
    }

    public String getLanguage() {
        String userType = sharedPreferences.getString(LANGUAGE, null);
        return userType;
    }

    public void setLanguage(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE, secretKey);
        editor.commit();
    }

    public String getCountry() {
        String userType = sharedPreferences.getString(COUNTRY, null);
        return userType;
    }

    public void setCountry(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COUNTRY, secretKey);
        editor.commit();
    }

    public String getCity() {
        String userType = sharedPreferences.getString(CITY, null);
        return userType;
    }

    public void setCity(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, secretKey);
        editor.commit();
    }

    public String getProfileImage() {
        String userType = sharedPreferences.getString(PROFILE_IMAGE, null);
        return userType;
    }

    public void setProfileImage(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE, secretKey);
        editor.commit();
    }

    public String getFbLink() {
        String userType = sharedPreferences.getString(FB_LINK, null);
        return userType;
    }

    public void setFbLink(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FB_LINK, secretKey);
        editor.commit();
    }

    public String getGplusLink() {
        String userType = sharedPreferences.getString(GPLUS_LINK, null);
        return userType;
    }

    public void setGplusLink(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GPLUS_LINK, secretKey);
        editor.commit();
    }

    public String getTwitterLink() {
        String userType = sharedPreferences.getString(TWITTER_LINK, null);
        return userType;
    }

    public void setTwitterLink(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TWITTER_LINK, secretKey);
        editor.commit();
    }

    public String getLinkedinLink() {
        String userType = sharedPreferences.getString(LINKEDIN_LINK, null);
        return userType;
    }

    public void setLinkedinLink(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LINKEDIN_LINK, secretKey);
        editor.commit();
    }

    public String getIdProofUrl() {
        String userType = sharedPreferences.getString(ID_PROOF_URL, IKeyConstants.EMPTY);
        return userType;
    }

    public void setIdProofUrl(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ID_PROOF_URL, secretKey);
        editor.commit();
    }

    public String getBankIdUrl() {
        String userType = sharedPreferences.getString(BANK_ID_URL, IKeyConstants.EMPTY);
        return userType;
    }

    public void setBankIdUrl(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_ID_URL, secretKey);
        editor.commit();
    }

    public int getIsIdVerified() {
        return sharedPreferences.getInt(IS_ID_VERIFIED, 0);
    }

    public void setIsIdVerified(int secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IS_ID_VERIFIED, secretKey);
        editor.commit();
    }

    public int getIsBankVerified() {
        return sharedPreferences.getInt(IS_BANK_VERIFIED, 0);
    }

    public void setIsBankVerified(int secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IS_BANK_VERIFIED, secretKey);
        editor.commit();
    }

    public String getFcmToken() {
        String userType = sharedPreferences.getString(FCM_TOKEN, null);
        return userType;
    }

    public void setFcmToken(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FCM_TOKEN, secretKey);
        editor.commit();
    }

    public String getGuideType() {
        String userType = sharedPreferences.getString(GUIDE_TYPE, null);
        return userType;
    }

    public void setGuideType(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GUIDE_TYPE, secretKey);
        editor.commit();
    }

    public String getAddressProofUrl() {
        String userType = sharedPreferences.getString(ADDRESS_PROOF_URL, IKeyConstants.EMPTY);
        return userType;
    }

    public void setAddressProofUrl(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADDRESS_PROOF_URL, secretKey);
        editor.commit();
    }

    public String getBankName() {
        String userType = sharedPreferences.getString(BANK_NAME, IKeyConstants.EMPTY);
        return userType;
    }

    public void setBankName(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_NAME, secretKey);
        editor.commit();
    }

    public String getBankAccountNum() {
        String userType = sharedPreferences.getString(BANK_ACCOUNT_NUM, IKeyConstants.EMPTY);
        return userType;
    }

    public void setBankAccountNum(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BANK_ACCOUNT_NUM, secretKey);
        editor.commit();
    }

    public String getBicCode() {
        String userType = sharedPreferences.getString(BIC_CODE, IKeyConstants.EMPTY);
        return userType;
    }

    public void setBicCode(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BIC_CODE, secretKey);
        editor.commit();
    }

    public String getAvailabilityJson() {
        String userType = sharedPreferences.getString(AVAILABILITY_JSON, IKeyConstants.JSON_ARRAY.toString());
        return userType;
    }

    public void setAvailabilityJson(String availabilityJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AVAILABILITY_JSON, availabilityJson);
        editor.commit();
    }

    public int isLicensedGuide() {
        if (getLicenseNumber() != null && !getLicenseNumber().isEmpty()) {
            return 1;
        }
        return 0;
    }


    public String getCertificateUrl() {
        String userType = sharedPreferences.getString(CERTIFICATE_URL, IKeyConstants.EMPTY);
        return userType;
    }

    public void setCertificateUrl(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CERTIFICATE_URL, secretKey);
        editor.commit();
    }

    public String getLicenseNumber() {
        String userType = sharedPreferences.getString(LICENSE_NUMBER, IKeyConstants.EMPTY);
        return userType;
    }

    public void setLicenseNumber(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LICENSE_NUMBER, secretKey);
        editor.commit();
    }

    public boolean isChatNotiOn() {
        boolean isOtpScreen = sharedPreferences.getBoolean(IS_CHAT_NOTI_ON, true);
        return isOtpScreen;
    }

    public void setChatNotiOn(boolean isOtpScreen) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_CHAT_NOTI_ON, isOtpScreen);
        editor.commit();
    }

    public boolean isOtherNotiOn() {
        boolean isOtpScreen = sharedPreferences.getBoolean(IS_OTHER_NOTI_ON, true);
        return isOtpScreen;
    }

    public void setOtherNotiOn(boolean isOtpScreen) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_OTHER_NOTI_ON, isOtpScreen);
        editor.commit();
    }

    public double getProviderLatitude() {
        return sharedPreferences.getFloat(PROVIDER_LATITUDE, 0);
    }

    public void setProviderLatitude(double latitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PROVIDER_LATITUDE, (float) latitude);
        editor.commit();
    }

    public double getProviderLongitude() {
        return sharedPreferences.getFloat(PROVIDER_LONGITUDE, 0);
    }

    public void setProviderLongitude(double longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PROVIDER_LONGITUDE, (float) longitude);
        editor.commit();
    }

    public String getBranchCode() {
        String userType = sharedPreferences.getString(BRANCH_CODE, IKeyConstants.EMPTY);
        return userType;
    }

    public void setBranchCode(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BRANCH_CODE, secretKey);
        editor.commit();
    }

    public String getPaypalId() {
        String userType = sharedPreferences.getString(PAYPAL_ID, IKeyConstants.EMPTY);
        return userType;
    }

    public void setPaypalId(String secretKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PAYPAL_ID, secretKey);
        editor.commit();
    }

    public int hasPayPalId() {
        return sharedPreferences.getInt(HAS_PAYPAL_ID, 0);
    }

    public void setHasPaypalId(int hasPaypalId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HAS_PAYPAL_ID, hasPaypalId);
        editor.commit();
    }

    public boolean isLoggedIn() {
        if (sharedPreferences.contains(SECRET_KEY)) {
            return true;
        }

        return false;
    }
}
