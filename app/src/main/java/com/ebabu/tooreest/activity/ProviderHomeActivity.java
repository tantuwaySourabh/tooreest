package com.ebabu.tooreest.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ProviderHomeActivity.class.getSimpleName();
    private Context context;
    private Toolbar actionBarToolbar;
    private DrawerLayout drawerLayout;
    private RatingBar ratingBar;
    private CustomTextView aboutmeBtn, tvName, tvCompanyName, tvProfilePicBg, tvCity, btnIdVerified, btnBankVerified, tvTotalEarning, tvWeeklyEarning;
    private CircleImageView ivProfilePic;
    private LinearLayout aboutmeText;
    private CustomEditText etAboutMe;
    private ImageView btnEditAbtMe, ivBadge;
    private int colorDarkYellow, colorStartGray;
    private boolean isAbtEditTextEnabled = false;
    private Intent notificationIntent;
    private final static int REQUEST_CODE_PROFILE_UPDATE = 1;
    private int totalEarning, weeklyEarning, totalBookings, bookingsCompleted, acceptedBookings, newBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_provider);
        context = ProviderHomeActivity.this;
        notificationIntent = getIntent();
        initView();
        setUpToolbar(context, IKeyConstants.EMPTY);
        String notiType = notificationIntent.getStringExtra(IKeyConstants.NOTIFICATION_TYPE);
        if (notiType != null && notiType.equalsIgnoreCase(IKeyConstants.NOTI_TYPE_BOOKING)) {
            Intent intent = new Intent(context, BookingsActivity.class);
            startActivity(intent);
        }
        fetchTotalEarning();
        //Log.d(TAG, Utils.getCountryJson());
    }

    private void initView() {
        tvTotalEarning = (CustomTextView) findViewById(R.id.tv_total_earning);
        tvWeeklyEarning = (CustomTextView) findViewById(R.id.tv_weekly_earning);
        ivProfilePic = (CircleImageView) findViewById(R.id.tv_profile_pic);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        tvCompanyName = (CustomTextView) findViewById(R.id.tv_company_name);
        tvProfilePicBg = (CustomTextView) findViewById(R.id.tv_profile_pic_bg);
        tvCity = (CustomTextView) findViewById(R.id.tv_city);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        aboutmeBtn = (CustomTextView) findViewById(R.id.btn_aboutme);
        aboutmeText = (LinearLayout) findViewById(R.id.text_aboutme);
        etAboutMe = (CustomEditText) findViewById(R.id.et_about_me);
        btnEditAbtMe = (ImageView) findViewById(R.id.btn_edit_aboutme);
        ivBadge = (ImageView) findViewById(R.id.iv_badge);

        btnIdVerified = (CustomTextView) findViewById(R.id.btn_id_verified);
        btnBankVerified = (CustomTextView) findViewById(R.id.btn_bank_verified);


        Resources resources = context.getResources();
        colorDarkYellow = resources.getColor(R.color.yellow);
        colorStartGray = resources.getColor(R.color.gray);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);

        findViewById(R.id.layout_profile).setOnClickListener(this);
        findViewById(R.id.btn_my_profile).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_my_jobs).setOnClickListener(this);
        findViewById(R.id.btn_bookings).setOnClickListener(this);
        findViewById(R.id.btn_my_services).setOnClickListener(this);
//        findViewById(R.id.btn_about).setOnClickListener(this);
//        findViewById(R.id.btn_privacy_policy).setOnClickListener(this);
//        findViewById(R.id.btn_terms_n_conditions).setOnClickListener(this);
        aboutmeBtn.setOnClickListener(this);
        findViewById(R.id.btn_review_n_feedback).setOnClickListener(this);
        findViewById(R.id.btn_provider_offer).setOnClickListener(this);
        findViewById(R.id.btn_edit_aboutme).setOnClickListener(this);
        findViewById(R.id.btn_my_document).setOnClickListener(this);
        findViewById(R.id.btn_quote_request).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);

        findViewById(R.id.btn_fb).setOnClickListener(this);
        findViewById(R.id.btn_gplus).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
        findViewById(R.id.btn_linkedin).setOnClickListener(this);
        findViewById(R.id.btn_availability).setOnClickListener(this);
        findViewById(R.id.btn_documents_detail).setOnClickListener(this);
        findViewById(R.id.btn_help).setOnClickListener(this);
        findViewById(R.id.btn_legal).setOnClickListener(this);
        findViewById(R.id.btn_view_total).setOnClickListener(this);
        findViewById(R.id.btn_view_weekly).setOnClickListener(this);

        btnBankVerified.setOnClickListener(this);
        btnIdVerified.setOnClickListener(this);
        findViewById(R.id.btn_drawer_contact).setOnClickListener(this);
        btnEditAbtMe.setOnClickListener(this);

        etAboutMe.setText(AppPreference.getInstance(context).getAboutMe());
        updateVerifiedStatus();
    }

    private Toolbar setUpToolbar(final Context context, String title) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        actionBarToolbar = (Toolbar) activity.findViewById(R.id.toolbar_actionbar);
        activity.setSupportActionBar(actionBarToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        //actionBarToolbar.setBackgroundResource(backgroundResource);
        actionBarToolbar.setNavigationIcon(R.mipmap.navigation);
        actionBarToolbar.setNavigationContentDescription(title);
        actionBarToolbar.setTitle("");
        actionBarToolbar.setSubtitle("");
        CustomTextView tvTitle = (CustomTextView) actionBarToolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        actionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        actionBarToolbar.inflateMenu(R.menu.menu_home_customer);
        actionBarToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {

                    case R.id.action_notifications:
                        intent = new Intent(context, NotificationsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_wallet:
                        intent = new Intent(context, WalletActivity.class);
                        //startActivity(intent);
                        break;

//                    case R.id.action_chat:
//                        intent = new Intent(context, ConversationActivity.class);
//                        startActivity(intent);
//                        break;
                }
                return false;
            }
        });
        return actionBarToolbar;
    }

    private void setProfileDataInView() {
        String imageUrl = AppPreference.getInstance(context).getProfileImage();
        String name = AppPreference.getInstance(context).getFullName();
        String companyName = AppPreference.getInstance(context).getCompanyName();
        String address = AppPreference.getInstance(context).getCountry();
        int isLicensedGuide = AppPreference.getInstance(context).isLicensedGuide();

        if (isLicensedGuide == 1) {
            ivBadge.setVisibility(View.VISIBLE);
        } else {
            ivBadge.setVisibility(View.GONE);
        }

        if (name != null && !name.isEmpty()) {
            tvName.setText(name);
        } else {
            tvName.setText(IKeyConstants.NA);
        }

        if (companyName != null && !companyName.isEmpty()) {
            tvCompanyName.setText(companyName);
        } else {
            tvCompanyName.setText(IKeyConstants.NA);
        }

        if (address != null && !address.isEmpty()) {
            tvCity.setText(address);
        } else {
            tvCity.setText(IKeyConstants.NA);
        }

        if (imageUrl != null && imageUrl.startsWith(IKeyConstants.HTTP)) {
            new AQuery(context).id(ivProfilePic).image(imageUrl);
        } else {
            ivProfilePic.setImageResource(R.mipmap.profile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileDataInView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_customer, menu);
        return true;
    }

    private void openDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void showDialogToEditAboutme() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dailog_aboutme_edit, null);
        builder.setView(view);
        final CustomEditText etAboutMe = (CustomEditText) view.findViewById(R.id.et_feedback);
        etAboutMe.setText(AppPreference.getInstance(context).getAboutMe());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aboutMe = etAboutMe.getText().toString();
                if (aboutMe.isEmpty()) {
                    Toast.makeText(context, getString(R.string.write_something_abt_u), Toast.LENGTH_SHORT).show();
                } else {
                    updateAboutMe(aboutMe, alertDialog);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        closeDrawer();
        Intent intent = null;
        switch (view.getId()) {
            case R.id.layout_profile:
            case R.id.btn_my_profile:
                intent = new Intent(context, ProviderProfileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PROFILE_UPDATE);
                break;

            case R.id.btn_edit_aboutme:
                showDialogToEditAboutme();
                break;
            case R.id.btn_logout:
                DialogUtils.openDialogToLogout(context);
                break;

//            case R.id.btn_about:
//                intent = new Intent(context, WebViewActivity.class);
//                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_ABOUT_US);
//                intent.putExtra(IKeyConstants.HEADER, getString(R.string.about));
//                startActivity(intent);
//                break;

            case R.id.btn_my_services:
                intent = new Intent(context, MyServicesActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_view_total:
                intent = new Intent(context, TotalEarningActivity.class);
                intent.putExtra(IKeyConstants.KEY_TOTAL_EARNING, totalEarning);
                intent.putExtra(IKeyConstants.KEY_TOTAL_BOOKINGS, totalBookings);
                intent.putExtra(IKeyConstants.KEY_BOOKINGS_COMPLETED, bookingsCompleted);
                intent.putExtra(IKeyConstants.KEY_ACCEPTED_BOOKINGS, acceptedBookings);
                intent.putExtra(IKeyConstants.KEY_NEW_BOOKINGS, newBookings);
                startActivity(intent);
                break;

            case R.id.btn_view_weekly:
                if (Utils.isNetworkConnected(context)) {
                    intent = new Intent(context, WeeklyEarningActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_my_jobs:
                intent = new Intent(context, MyJobsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_bookings:
                intent = new Intent(context, BookingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_help:
                //Utils.startHelpshiftFaq(context);
                break;

            case R.id.btn_legal:
                intent = new Intent(context, LegalActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_documents_detail:
                intent = new Intent(context, DocumentsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_availability:
                intent = new Intent(context, AvailabilityActivity.class);
                startActivity(intent);
                break;

//            case R.id.btn_terms_n_conditions:
//                intent = new Intent(context, WebViewActivity.class);
//                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_TERMS_CONDITIONS);
//                intent.putExtra(IKeyConstants.HEADER, getString(R.string.terms_n_conditions));
//                startActivity(intent);
//                break;
//
//            case R.id.btn_privacy_policy:
//                intent = new Intent(context, WebViewActivity.class);
//                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_PRIVACY_POLICY);
//                intent.putExtra(IKeyConstants.HEADER, getString(R.string.privacy_policy));
//                startActivity(intent);
//                break;

            case R.id.btn_aboutme:
                if (aboutmeText.getVisibility() == View.VISIBLE) {
                    aboutmeText.setVisibility(View.GONE);
                    aboutmeBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.plus_yellow, 0);
                    break;
                } else {
                    aboutmeText.setVisibility(View.VISIBLE);
                    aboutmeBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.minus, 0);
                }
                break;

            case R.id.btn_review_n_feedback:
                intent = new Intent(context, ReviewFeedbackActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_provider_offer:
                intent = new Intent(context, OfferListActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_my_document:
                intent = new Intent(context, ProviderGalleryActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_quote_request:
                intent = new Intent(context, QuoteListActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_bank_verified:
//                intent = new Intent(context, UploadIdAndBankActivity.class);
//                startActivity(intent);
                break;

            case R.id.btn_id_verified:
//                intent = new Intent(context, UploadIdAndBankActivity.class);
//                startActivity(intent);
                break;

            case R.id.btn_drawer_contact:
                intent = new Intent(context, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_settings:
                intent = new Intent(context, CustomerSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_fb:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.FACEBOOK);
                intent.putExtra(IKeyConstants.URL, AppPreference.getInstance(context).getFbLink());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_FACEBOOK);
                startActivity(intent);
                break;

            case R.id.btn_gplus:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.GPLUS);
                intent.putExtra(IKeyConstants.URL, AppPreference.getInstance(context).getGplusLink());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_GPLUS);
                startActivity(intent);
                break;

            case R.id.btn_twitter:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.TWITTER);
                intent.putExtra(IKeyConstants.URL, AppPreference.getInstance(context).getTwitterLink());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_TWITTER);
                startActivity(intent);
                break;

            case R.id.btn_linkedin:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.LINKED_IN);
                intent.putExtra(IKeyConstants.URL, AppPreference.getInstance(context).getLinkedinLink());
                intent.putExtra(IKeyConstants.HEADER, IKeyConstants.KEY_LINKEDIN);
                startActivity(intent);
                break;
        }
    }

    private void updateAboutMe(final String aboutMe, final AlertDialog alertDialog) {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(IKeyConstants.KEY_ABOUT_ME, aboutMe);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "updateAboutMe(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.UPDATE_ABOUT_ME, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "updateAboutMe(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            AppPreference.getInstance(context).setAboutMe(aboutMe);
                            etAboutMe.setText(aboutMe);
                            alertDialog.dismiss();
                            Toast.makeText(context, getString(R.string.about_me_updated), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    private void onEditAbtMeBtnClicked() {
        if (isAbtEditTextEnabled) {
            btnEditAbtMe.setImageResource(R.mipmap.edit_yellow);
        } else {
            btnEditAbtMe.setImageResource(R.mipmap.success);
        }
    }

    private void updateVerifiedStatus() {
        if (AppPreference.getInstance(context).getIsIdVerified() == 1) {
            btnIdVerified.setText(getString(R.string.id_verified));
        } else {
            btnIdVerified.setText(getString(R.string.id_not_verified));
        }

        if (AppPreference.getInstance(context).getIsBankVerified() == 1) {
            btnBankVerified.setText(getString(R.string.bank_verified));
        } else {
            btnBankVerified.setText(getString(R.string.bank_not_verified));
        }
    }

    private void fetchTotalEarning() {
        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        //myLoading.show(getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();

        new AQuery(context).post(IUrlConstants.GET_TOTAL_EARNING, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchTotalEarning(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray jsonArray = json.getJSONArray("data");
                            JSONObject data = jsonArray.getJSONObject(0);
                            totalEarning = data.getInt("total_earning");
                            weeklyEarning = data.getInt("weekly_amount");
                            bookingsCompleted = data.getInt("completed_count");
                            acceptedBookings = data.getInt("accepted_count");
                            newBookings = data.getInt("booked_count");
                            totalBookings = bookingsCompleted + acceptedBookings + newBookings;

                            tvTotalEarning.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + totalEarning);
                            tvWeeklyEarning.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + weeklyEarning);
                        } else {
                            if (IKeyConstants.INVALID_TOKEN.equalsIgnoreCase(json.getString(IKeyConstants.MESSAGE))) {
                                DialogUtils.openAlertOnInvalidToken(context);
                            } else {
                                Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                }
                myLoading.dismiss();
            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PROFILE_UPDATE) {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        }
    }
}
