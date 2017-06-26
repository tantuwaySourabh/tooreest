package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.ProviderAdapter;
import com.ebabu.tooreest.beans.Provider;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.DialogUtils;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = CustomerHomeActivity.class.getSimpleName();
    private Context context;
    private Toolbar actionBarToolbar;
    private DrawerLayout drawerLayout;
    private CustomTextView tvName, tvProfilePicBg, btnViewAll;
    private CircleImageView ivProfilePic;
    private Intent notificationIntent;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvServices;
    private List<Provider> listServices;
    private ProviderAdapter providerAdapter;
    private ProgressView progressView;
    private final static int REQUEST_CODE_PROFILE_UPDATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);
        context = CustomerHomeActivity.this;
        notificationIntent = getIntent();
        initView();
        setUpToolbar(context, IKeyConstants.EMPTY);
        String notiType = notificationIntent.getStringExtra(IKeyConstants.NOTIFICATION_TYPE);
        if (notiType != null && notiType.equalsIgnoreCase(IKeyConstants.NOTI_TYPE_BOOKING)) {
            Intent intent = new Intent(context, BookingsActivity.class);
            startActivity(intent);
        }
        fetchProviderList();
    }

    private void initView() {
        progressView = (ProgressView) findViewById(R.id.rey_loading);
        ivProfilePic = (CircleImageView) findViewById(R.id.tv_profile_pic);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        btnViewAll = (CustomTextView) findViewById(R.id.btn_view_all);
        tvProfilePicBg = (CustomTextView) findViewById(R.id.tv_profile_pic_bg);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rvServices = (RecyclerView) findViewById(R.id.rv_services);
        listServices = new ArrayList<>();
        providerAdapter = new ProviderAdapter(context, listServices, IKeyConstants.PROVIDER_FEATURED);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(providerAdapter);

        rvServices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.layout_profile).setOnClickListener(this);
        findViewById(R.id.btn_my_profile).setOnClickListener(this);
        findViewById(R.id.btn_bookings).setOnClickListener(this);
        findViewById(R.id.btn_invoice).setOnClickListener(this);
        findViewById(R.id.btn_my_favorites).setOnClickListener(this);
        findViewById(R.id.btn_post_a_job).setOnClickListener(this);
        findViewById(R.id.btn_about).setOnClickListener(this);
        findViewById(R.id.btn_contact).setOnClickListener(this);
        findViewById(R.id.btn_invite_for_job).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
        findViewById(R.id.btn_privacy_policy).setOnClickListener(this);
        findViewById(R.id.btn_terms_n_conditions).setOnClickListener(this);

        findViewById(R.id.btn_cat_business).setOnClickListener(this);
        findViewById(R.id.btn_cat_event_wedding).setOnClickListener(this);
        findViewById(R.id.btn_cat_personal_services).setOnClickListener(this);
        findViewById(R.id.btn_cat_health).setOnClickListener(this);
        findViewById(R.id.btn_cat_home_services).setOnClickListener(this);
        findViewById(R.id.btn_cat_lessons).setOnClickListener(this);
        findViewById(R.id.btn_cat_hobbies).setOnClickListener(this);
        findViewById(R.id.btn_cat_beauty).setOnClickListener(this);
        findViewById(R.id.btn_cat_repair).setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
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
                        startActivity(intent);
                        break;

                    case R.id.action_chat:
                        intent = new Intent(context, ConversationActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        return actionBarToolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileDataInView();
    }

    private void setProfileDataInView() {
        String imageUrl = AppPreference.getInstance(context).getProfileImage();
        String name = AppPreference.getInstance(context).getFullName();

        if (name != null && !name.isEmpty()) {
            tvName.setText(name);
        } else {
            tvName.setText(IKeyConstants.NA);
        }

        if (imageUrl != null && imageUrl.startsWith("http")) {
            new AQuery(context).id(ivProfilePic).image(imageUrl);
        } else {
            tvProfilePicBg.setText(name.charAt(0) + IKeyConstants.EMPTY);
        }
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

    @Override
    public void onClick(View view) {
        closeDrawer();
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_logout:
                DialogUtils.openDialogToLogout(context);
                break;

            case R.id.layout_profile:
            case R.id.btn_my_profile:
                intent = new Intent(context, CustomerProfileActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PROFILE_UPDATE);
                break;

            case R.id.btn_about:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_ABOUT_US);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.about));
                startActivity(intent);
                break;

            case R.id.btn_bookings:
                intent = new Intent(context, BookingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_invite_for_job:
                intent = new Intent(context, BidsListActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_invoice:
                intent = new Intent(context, CustomerInvoiceActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_my_favorites:
                intent = new Intent(context, ProviderListActivity.class);
                intent.putExtra(IKeyConstants.PROVIDER_LIST_TYPE, IKeyConstants.PROVIDER_FAVORITES);
                intent.putExtra(IKeyConstants.SUBCAT_NAME, getString(R.string.my_favorites));
                startActivity(intent);
                break;

            case R.id.btn_post_a_job:
                if (Utils.isNetworkConnected(context)) {
                    intent = new Intent(context, PostJobActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_settings:
                intent = new Intent(context, CustomerSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_contact:
                intent = new Intent(context, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_terms_n_conditions:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_TERMS_CONDITIONS);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.terms_n_conditions));
                startActivity(intent);
                break;

            case R.id.btn_privacy_policy:
                intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(IKeyConstants.PAGE_ID, IKeyConstants.ID_PRIVACY_POLICY);
                intent.putExtra(IKeyConstants.HEADER, getString(R.string.privacy_policy));
                startActivity(intent);
                break;

            case R.id.btn_view_all:
                intent = new Intent(context, ProviderListActivity.class);
                intent.putExtra(IKeyConstants.SUBCAT_NAME, getString(R.string.featured_providers));
                intent.putExtra(IKeyConstants.PROVIDER_LIST_TYPE, IKeyConstants.PROVIDER_FEATURED);
                startActivity(intent);
                break;

            default:
                String catId = (String) view.getTag();
                String catName = ((CustomTextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                intent = new Intent(context, SubcategoriesActivity.class);
                intent.putExtra(IKeyConstants.CATEGORY, catId);
                intent.putExtra(IKeyConstants.CATEGORY_NAME, catName);
                startActivity(intent);
                break;
        }
    }

    private void fetchProviderList() {


        progressView.setVisibility(View.VISIBLE);
        progressView.start();

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("create_at", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "fetchProviderList(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.FEATURED_PROVIDERS_LIST, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "fetchProviderList(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            JSONArray data = json.getJSONArray("data");
                            Type type = new TypeToken<ArrayList<Provider>>() {
                            }.getType();

                            if (data != null && data.length() > 0) {
                                ArrayList<Provider> listSubcategories = new Gson().fromJson(data.toString(), type);
                                if (listSubcategories.size() > 0) {
                                    listServices.addAll(listSubcategories);
                                    providerAdapter.notifyDataSetChanged();
                                }
                            }
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
                progressView.setVisibility(View.GONE);
                progressView.stop();
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
