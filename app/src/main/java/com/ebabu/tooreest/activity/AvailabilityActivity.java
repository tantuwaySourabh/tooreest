package com.ebabu.tooreest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.TimingsAdapter;
import com.ebabu.tooreest.beans.Timing;
import com.ebabu.tooreest.beans.VisitDays;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvailabilityActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = AvailabilityActivity.class.getSimpleName();
    private Context context;
    private AppCompatCheckBox cbDays[];
    private List<VisitDays> timings = new ArrayList<>();
    private List<VisitDays> myVisitDays = new ArrayList<>();
    private boolean isEdit = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hospital);
        context = AvailabilityActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.availability));
    }

    private void initView() {
        try {
            Type type = new TypeToken<List<VisitDays>>() {
            }.getType();
            myVisitDays = new Gson().fromJson(AppPreference.getInstance(context).getAvailabilityJson(), type);
            Log.d(TAG, "myVisitDays.size = " + myVisitDays.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbDays = new AppCompatCheckBox[7];

        cbDays[0] = (AppCompatCheckBox) findViewById(R.id.cb_monday);
        cbDays[1] = (AppCompatCheckBox) findViewById(R.id.cb_tuesday);
        cbDays[2] = (AppCompatCheckBox) findViewById(R.id.cb_wednesday);
        cbDays[3] = (AppCompatCheckBox) findViewById(R.id.cb_thursday);
        cbDays[4] = (AppCompatCheckBox) findViewById(R.id.cb_friday);
        cbDays[5] = (AppCompatCheckBox) findViewById(R.id.cb_saturday);
        cbDays[6] = (AppCompatCheckBox) findViewById(R.id.cb_sunday);

        findViewById(R.id.btn_add_time_monday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_tuesday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_wednesday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_thursday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_friday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_saturday).setOnClickListener(this);
        findViewById(R.id.btn_add_time_sunday).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        for (int i = 0; i < 7; i++) {
            timings.add(new VisitDays());
        }
        if (isEdit) {
            for (int i = 0; i < myVisitDays.size(); i++) {
                timings.set(i, myVisitDays.get(i));
                cbDays[i].setChecked(true);
            }
        }
    }

    private void openDialogToAddTiming(final int day) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Timings for " + Utils.ARRAY_DAYS[day]);

        View view = getLayoutInflater().inflate(R.layout.layout_dialog_add_timing, null);
        builder.setView(view);

        ImageView btnAddTime = (ImageView) view.findViewById(R.id.btn_add_time);
        final CustomTextView tvTo = (CustomTextView) view.findViewById(R.id.et_to);
        final CustomTextView tvFrom = (CustomTextView) view.findViewById(R.id.et_from);
        RecyclerView rvTimings = (RecyclerView) view.findViewById(R.id.rv_timings);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        final List<Timing> listTimings = timings.get(day).getTimearr();

        //if (timings.size() > 0) {
        //        } else {
//            listTimings = new ArrayList<>();
//        }
        final TimingsAdapter timingsAdapter = new TimingsAdapter(context, listTimings);
        rvTimings.setLayoutManager(layoutManager);
        rvTimings.setAdapter(timingsAdapter);

        tvTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openTimerPickerDialog(context, tvTo);
            }
        });

        tvFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openTimerPickerDialog(context, tvFrom);
            }
        });

        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = tvFrom.getText().toString();
                String to = tvTo.getText().toString();

                Date dateFrom, dateTo;
                try {
                    dateFrom = Utils.displayTimeFormat.parse(tvFrom.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Select start time", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    dateTo = Utils.displayTimeFormat.parse(tvTo.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Select end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dateFrom.compareTo(dateTo) >= 0) {
                    Toast.makeText(context, "Start time must be less than end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (from.isEmpty()) {
                    Toast.makeText(context, "Select start time", Toast.LENGTH_SHORT).show();
                } else if (to.isEmpty()) {
                    Toast.makeText(context, "Select end time", Toast.LENGTH_SHORT).show();
                } else {
                    Timing timing = new Timing();
                    timing.setTo(to);
                    timing.setFrom(from);
                    listTimings.add(0, timing);
                    timingsAdapter.notifyItemInserted(0);
                }
            }
        });

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "saving listTimings with size " + listTimings.size() + " for day " + day);
                        timings.get(day).setTimearr(listTimings);
                        alertDialog.dismiss();
                    }
                });
            }
        });


        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                onSaveBtnClicked();
                break;

            default:
                openDialogToAddTiming(Integer.parseInt(view.getTag().toString()));
                break;
        }
    }

    private void onSaveBtnClicked() {
        List<VisitDays> listVisitDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (cbDays[i].isChecked()) {
                VisitDays visitDays = new VisitDays();
                visitDays.setDay(i);
                if (timings.size() == 0) {
                    Toast.makeText(context, "Set timings", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    List<Timing> listDayTime = timings.get(i).getTimearr();
                    Log.d(TAG, "listDatTime: " + listDayTime.size());
                    if (listDayTime != null && listDayTime.size() > 0) {
                        visitDays.setTimearr(timings.get(i).getTimearr());
                        listVisitDays.add(visitDays);
                    } else {
                        Toast.makeText(context, "Set timings for " + Utils.ARRAY_DAYS[i], Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        }

        if (Utils.isNetworkConnected(context)) {
            updateAvailability(listVisitDays);
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAvailability(List<VisitDays> listVisitDays) {

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show("Saving availability timings");
        JSONObject jsonObject = new JSONObject();
        JSONArray availabilityJsonArray = null;
        try {
            availabilityJsonArray = new JSONArray(new Gson().toJson(listVisitDays));
            jsonObject.put("timings", availabilityJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "updateAvailability(): jsonObject=" + jsonObject);
        final JSONArray finalAvailabilityJsonArray = availabilityJsonArray;
        new AQuery(context).post(IUrlConstants.UPDATE_AVAILABILITY, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                try {
                    Log.d(TAG, "updateAvailability(): url=" + url + ", json=" + json);
                    if (json == null) {
                        if (status.getCode() == AjaxStatus.NETWORK_ERROR)
                            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    } else {
                        String strStatus = json.getString("status");
                        if (strStatus.equalsIgnoreCase(IKeyConstants.SUCCESS)) {
                            AppPreference.getInstance(context).setAvailabilityJson(finalAvailabilityJsonArray.toString());
                            Toast.makeText(context, "Availability timings updated successfully", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();

                }
                myLoading.dismiss();

            }
        }.header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }
}
