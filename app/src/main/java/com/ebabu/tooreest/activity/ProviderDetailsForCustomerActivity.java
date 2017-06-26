package com.ebabu.tooreest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Provider;
import com.ebabu.tooreest.beans.Service;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ProviderDetailsForCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ProviderDetailsForCustomerActivity.class.getSimpleName();
    private Context context;
    private static int colorDarkYellow, colorStartGray;
    private RatingBar ratingBar;
    private CustomTextView tvName, tvCategory, tvAboutMe, btnAboutMe, btnAddRemoveFavorites, btnIdVerified, btnBankVerified, btnContact, btnBookNow;
    private ImageView ivProfilePic;
    private Provider provider;
    private String subcatId, subcatName;
    private int serviceCharge, serviceTax, offerDiscount, totalPayable, offerId;
    private final static int REQUEST_CODE_SELECT_OFFER = 1, REQUEST_CODE_SELECT_SERVICE = 2;
    private int isFavorite = 0;
    private int listType;
    private boolean fromBidding, selectService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_details_for_customer);
        context = ProviderDetailsForCustomerActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.provider_profile));
    }

    private void initView() {
        Intent intent = getIntent();
        selectService = intent.getBooleanExtra(IKeyConstants.TO_SELECT_SERVICE, false);
        fromBidding = intent.getBooleanExtra(IKeyConstants.FROM_BIDDING, false);
        listType = intent.getIntExtra(IKeyConstants.PROVIDER_LIST_TYPE, 0);
        subcatId = intent.getStringExtra(IKeyConstants.SUBCAT_ID);
        subcatName = intent.getStringExtra(IKeyConstants.SUBCAT_NAME);
        provider = intent.getParcelableExtra(IKeyConstants.PROVIDER);
        isFavorite = provider.getIs_favorite();
        ivProfilePic = (ImageView) findViewById(R.id.tv_profile_pic);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        tvCategory = (CustomTextView) findViewById(R.id.tv_category);
        tvAboutMe = (CustomTextView) findViewById(R.id.tv_about_me);
        btnAboutMe = (CustomTextView) findViewById(R.id.btn_aboutme);
        btnAddRemoveFavorites = (CustomTextView) findViewById(R.id.btn_add_remove_favorite);
        btnBookNow = (CustomTextView) findViewById(R.id.btn_book_now);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);

        btnIdVerified = (CustomTextView) findViewById(R.id.btn_id_verified);
        btnBankVerified = (CustomTextView) findViewById(R.id.btn_bank_verified);
        btnContact = (CustomTextView) findViewById(R.id.btn_contact);

        btnBookNow.setOnClickListener(this);
        findViewById(R.id.btn_my_services).setOnClickListener(this);
        findViewById(R.id.btn_offers).setOnClickListener(this);
        findViewById(R.id.btn_request_for_quote).setOnClickListener(this);
        findViewById(R.id.btn_my_document).setOnClickListener(this);
        findViewById(R.id.btn_review_n_feedback).setOnClickListener(this);

        findViewById(R.id.btn_fb).setOnClickListener(this);
        findViewById(R.id.btn_gplus).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
        findViewById(R.id.btn_linkedin).setOnClickListener(this);

        btnAddRemoveFavorites.setOnClickListener(this);

        Resources resources = context.getResources();
        colorDarkYellow = resources.getColor(R.color.yellow);
        colorStartGray = resources.getColor(R.color.divider_color);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);

        btnAboutMe.setOnClickListener(this);
        setProfileDataInView();
        updateVerifiedStatus();
        if (listType == IKeyConstants.PROVIDER_FEATURED || listType == IKeyConstants.PROVIDER_FAVORITES) {
            btnBookNow.setVisibility(View.GONE);
        }
        if (fromBidding) {
            openDialogToBookServices();
        }
    }

    private void setProfileDataInView() {

        if (isFavorite == 1) {
            btnAddRemoveFavorites.setText(getString(R.string.remove_from_favorites));
        } else {
            btnAddRemoveFavorites.setText(getString(R.string.add_to_favorites));
        }

        if (provider.getCompany_name() != null && !provider.getCompany_name().isEmpty()) {
            tvName.setText(provider.getCompany_name());
        } else {
            tvName.setText(IKeyConstants.NA);
        }

        if (provider.getCity() != null && !provider.getCity().isEmpty()) {
            tvCategory.setText(provider.getCity());
        } else {
            tvCategory.setText("City: " + IKeyConstants.NA);
        }

        if (provider.getAbout_me() != null && !provider.getAbout_me().isEmpty()) {
            tvAboutMe.setText(provider.getAbout_me());
        } else {
            tvAboutMe.setText(IKeyConstants.EMPTY);
        }

        if (provider.getImage() != null && provider.getImage().startsWith("http")) {
            new AQuery(context).id(ivProfilePic).image(provider.getImage());
        } else {
            //tvProfilePicBg.setText(provider.getCompany_name().charAt(0) + IKeyConstants.EMPTY);
        }

        ratingBar.setRating(provider.getRating());

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_book_now:
                openDialogToBookServices();
                break;

            case R.id.btn_my_services:
                intent = new Intent(context, MyServicesActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER, provider.getUser_id());
                intent.putExtra(IKeyConstants.TO_SELECT_SERVICE, selectService);
                startActivityForResult(intent, REQUEST_CODE_SELECT_SERVICE);
                break;

            case R.id.btn_aboutme:
                onAboutMeClicked();
                break;

            case R.id.btn_offers:
                intent = new Intent(context, OfferListActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER, provider.getUser_id());
                startActivityForResult(intent, REQUEST_CODE_SELECT_OFFER);
                break;

            case R.id.btn_request_for_quote:
                intent = new Intent(context, RequestQuoteActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER, provider.getUser_id());
                startActivity(intent);
                break;

            case R.id.btn_my_document:
                intent = new Intent(context, ProviderGalleryActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER, provider.getUser_id());
                startActivity(intent);
                break;

            case R.id.btn_review_n_feedback:
                intent = new Intent(context, ReviewFeedbackActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER, provider.getUser_id());
                startActivity(intent);
                break;

            case R.id.btn_add_remove_favorite:
                onAddRemoveFavoriteBtnClicked();
                break;

            case R.id.btn_fb:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.FACEBOOK);
                intent.putExtra(IKeyConstants.URL, provider.getFacebook());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_FACEBOOK);
                startActivity(intent);
                break;

            case R.id.btn_gplus:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.GPLUS);
                intent.putExtra(IKeyConstants.URL, provider.getGoogle_plus());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_GPLUS);
                startActivity(intent);
                break;

            case R.id.btn_twitter:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.TWITTER);
                intent.putExtra(IKeyConstants.URL, provider.getTwitter());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_TWITTER);
                startActivity(intent);
                break;

            case R.id.btn_linkedin:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.LINKED_IN);
                intent.putExtra(IKeyConstants.URL, provider.getLinkedin());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_LINKEDIN);
                startActivity(intent);
                break;
        }
    }

    private void openDialogToBookServices() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (fromBidding) {
            builder.setCancelable(false);
        }
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_book_now, null);
        builder.setView(view);

        final CustomTextView tvDate = (CustomTextView) view.findViewById(R.id.tv_date);
        final CustomTextView tvTime = (CustomTextView) view.findViewById(R.id.tv_time);
        final CustomTextView tvServiceCharge = (CustomTextView) view.findViewById(R.id.tv_service_charge);
        final CustomTextView tvServiceTax = (CustomTextView) view.findViewById(R.id.tv_service_tax);
        final CustomTextView tvOfferDiscount = (CustomTextView) view.findViewById(R.id.tv_discount);
        final CustomTextView tvTotalPayable = (CustomTextView) view.findViewById(R.id.tv_total_payable);
        final CustomEditText etBookingAmnt = (CustomEditText) view.findViewById(R.id.et_booking_amnt);

        tvDate.setText(Utils.displayDateFormat.format(System.currentTimeMillis()));
        tvTime.setText(Utils.displayTimeFormat.format(System.currentTimeMillis()));

        serviceCharge = Integer.parseInt(provider.getCharges());
        serviceTax = Utils.getPercentAmount(Integer.parseInt(provider.getCharges()));

        totalPayable = serviceCharge + serviceTax - offerDiscount;

        etBookingAmnt.setText(totalPayable + IKeyConstants.EMPTY);

        tvServiceCharge.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + serviceCharge);
        tvServiceTax.setText("+ " + IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + serviceTax);
        tvOfferDiscount.setText("- " + IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + offerDiscount);
        tvTotalPayable.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + totalPayable);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (fromBidding) {
                    finish();
                }
            }
        });

        view.findViewById(R.id.btn_edit_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openCalendarDialog(context, tvDate);
            }
        });

        view.findViewById(R.id.btn_edit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openTimerPickerDialog(context, tvTime);
            }
        });


        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strBookingAmnt = etBookingAmnt.getText().toString();
                String timestamp = tvDate.getText().toString() + IKeyConstants.SPACE + tvTime.getText().toString();
                try {
                    timestamp = Utils.fullTimestamp24hours.format(Utils.completeDisplayDateFormat.parse(timestamp));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (strBookingAmnt.isEmpty()) {
                    Toast.makeText(context, getString(R.string.minimum_booking_amnt), Toast.LENGTH_SHORT).show();
                } else {
                    int bookingAmnt = Integer.parseInt(strBookingAmnt);
                    if (bookingAmnt < 100) {
                        Toast.makeText(context, getString(R.string.minimum_booking_amnt), Toast.LENGTH_SHORT).show();
                    } else if (bookingAmnt > totalPayable) {
                        Toast.makeText(context, getString(R.string.you_are_paying_greater), Toast.LENGTH_SHORT).show();
                    } else {
                        //alertDialog.dismiss();
                        Intent intent = new Intent(context, WalletActivity.class);
                        intent.putExtra(IKeyConstants.FOR_PAYMENT, true);
                        intent.putExtra(IKeyConstants.SERVICE_CHARGE, serviceCharge);
                        intent.putExtra(IKeyConstants.SERVICE_TAX, serviceTax);
                        intent.putExtra(IKeyConstants.DISCOUNT, offerDiscount);
                        intent.putExtra(IKeyConstants.TOTAL_AMOUNT, totalPayable);
                        intent.putExtra(IKeyConstants.OFFER_ID, offerId);
                        intent.putExtra(IKeyConstants.TIMESTAMP, timestamp);
                        intent.putExtra(IKeyConstants.PROVIDER_ID, provider.getUser_id());
                        intent.putExtra(IKeyConstants.SUBCAT_ID, subcatId);
                        intent.putExtra(IKeyConstants.BOOKING_AMOUNT, bookingAmnt);

                        startActivity(intent);
                    }
                }
            }
        });
        alertDialog.show();
    }

    private void onAboutMeClicked() {
        if (tvAboutMe.getVisibility() == View.VISIBLE) {
            tvAboutMe.setVisibility(View.GONE);
            btnAboutMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.plus_yellow, 0);
        } else {
            tvAboutMe.setVisibility(View.VISIBLE);
            btnAboutMe.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.minus, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_OFFER) {
                offerId = data.getIntExtra(IKeyConstants.OFFER_ID, 0);
                offerDiscount = Integer.parseInt(data.getStringExtra(IKeyConstants.DISCOUNT));
            } else if (requestCode == REQUEST_CODE_SELECT_SERVICE) {
                Service service = data.getParcelableExtra(IKeyConstants.SERVICE);
                if (service != null) {
                    subcatId = service.getSub_cat_id();
                    subcatName = service.getSub_cat_name();
                    provider.setCharges(service.getPrice() + IKeyConstants.EMPTY);
                    btnBookNow.setVisibility(View.VISIBLE);
                    Toast.makeText(context, service.getSub_cat_name() + " has been selected", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void onAddRemoveFavoriteBtnClicked() {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("provider_id", provider.getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onAddRemoveFavoriteBtnClicked(): jsonObject=" + jsonObject);
        String serviceUrl = null;
        if (isFavorite == 1) {
            serviceUrl = IUrlConstants.REMOVE_FROM_FAVORITE;
        } else {
            serviceUrl = IUrlConstants.ADD_TO_FAVORITE;
        }

        new AQuery(context).post(serviceUrl, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "onAddRemoveFavoriteBtnClicked(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            if (isFavorite == 1) {
                                btnAddRemoveFavorites.setText(getString(R.string.add_to_favorites));
                                isFavorite = 0;
                            } else {
                                btnAddRemoveFavorites.setText(getString(R.string.remove_from_favorites));
                                isFavorite = 1;
                            }
                        } else {
                            DialogUtils.openAlertToShowMessage(json.getString(IKeyConstants.MESSAGE), context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void updateVerifiedStatus() {
        if (provider.getIs_id_verify() == 1) {
            btnIdVerified.setText(getString(R.string.id_verified));
        } else {
            btnIdVerified.setText(getString(R.string.id_not_verified));
        }

        if (provider.getIs_bank_verify() == 1) {
            btnBankVerified.setText(getString(R.string.bank_verified));
        } else {
            btnBankVerified.setText(getString(R.string.bank_not_verified));
        }
    }
}
