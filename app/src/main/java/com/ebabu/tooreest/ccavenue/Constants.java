package com.ebabu.tooreest.ccavenue;

public class Constants {
    public static final String PARAMETER_SEP = "&";
    public static final String PARAMETER_EQUALS = "=";

    //URLs
    public static final String BASE_URL = " https://www.tooreest.com/marchant/";
    public static final String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";
    public static final String GET_RSA_URL = BASE_URL + "GetRSA.php";
    public static final String GET_CANCEL_URL = BASE_URL + "ccavResponseHandler.php";
    public static final String GET_REDIRECT_URL = BASE_URL + "ccavResponseHandler.php";

    //Configurations - TEST
//    public static final String ACCESS_CODE = "4YRUXLSRO20O8NIH";
//    public static final String WORKING_KEY = "EA2114800E037CE0F0313AD36CB57A2A";
//    public static final String MERCHANT_ID = "2";
//    public static final String CURRENCY_INR = "INR";

    //Configurations - LIVE
    public static final String ACCESS_CODE = "AVKF66DH70CC20FKCC";
    public static final String MERCHANT_ID = "107759";
    public static final String WORKING_KEY = "CAAFF09BA4182434C491DD1DE4F3DA2D";
    public static final String CURRENCY_INR = "INR";

}