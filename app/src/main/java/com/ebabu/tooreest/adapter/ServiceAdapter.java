package com.ebabu.tooreest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.AddNewServiceActivity;
import com.ebabu.tooreest.activity.MyServicesActivity;
import com.ebabu.tooreest.beans.Service;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final static String TAG = ServiceAdapter.class.getSimpleName();
    private Context context;
    private List<Service> listServices;
    private Resources resources;
    private static int colorDarkYellow, colorStartGray;
    private boolean isProvider, selectService;


    public ServiceAdapter(Context context, List<Service> listServices, boolean isProvider, boolean selectService) {
        this.context = context;
        this.listServices = listServices;
        resources = context.getResources();
        colorDarkYellow = resources.getColor(R.color.yellow);
        colorStartGray = resources.getColor(R.color.gray);
        this.isProvider = isProvider;
        this.selectService = selectService;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_service, parent, false);
        return new ServiceViewHolder(view, isProvider, selectService);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, final int position) {
        final Service service = listServices.get(position);

        if (service != null) {

            if (service.getImage() != null && service.getImage().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(holder.ivImage).image(service.getImage(), true, true, 80, AQuery.FADE_IN);
            } else {
                holder.ivImage.setImageResource(R.mipmap.ic_launcher);
            }

            if (listServices.size() == 1) {
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                holder.btnDelete.setVisibility(View.VISIBLE);
            }

//            if (isProvider) {
//                holder.btnDelete.setVisibility(View.VISIBLE);
//            } else {
//                holder.btnDelete.setVisibility(View.GONE);
//            }

            if (service.getSub_cat_name() != null) {
                holder.tvCompanyName.setText(service.getSub_cat_name());
            } else {
                holder.tvCompanyName.setText(IKeyConstants.NA);
            }

            if (service.getDescription() != null) {
                holder.tvDescription.setText(service.getDescription());
            } else {
                holder.tvDescription.setText(IKeyConstants.NA);
            }

            if (service.getService_type() != 0) {
                holder.tvServiceType.setText(Utils.ARRAY_SERVICE_TYPE[service.getService_type() - 1]);
            } else {
                holder.tvServiceType.setText(IKeyConstants.NA);
            }

            holder.tvNumProjects.setText(service.getProjectdone() + IKeyConstants.EMPTY);

            if (service.getPrice() != 0) {
                holder.tvCharge.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + service.getPrice());
            } else {
                holder.tvCharge.setText(IKeyConstants.NA);
            }

            holder.ratingBar.setRating(service.getAve_rating());

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToDeleteService(position);
                }
            });

            if (isProvider) {
                holder.mainItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, AddNewServiceActivity.class);
                        intent.putExtra(IKeyConstants.SERVICE, service);
                        ((Activity) context).startActivityForResult(intent, MyServicesActivity.REQUEST_CODE_ADD_SERVICE);
                    }
                });
            } else {
                holder.radioService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (context instanceof MyServicesActivity) {
                                ((MyServicesActivity) context).onServiceSelected(service);
                            }
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return listServices.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, btnDelete;
        RatingBar ratingBar;
        CustomTextView tvCompanyName, tvFullname, tvNumProjects, tvCharge, tvDescription, tvServiceType;
        CardView mainItem;
        AppCompatRadioButton radioService;

        public ServiceViewHolder(View itemView, boolean isProvider, boolean selectService) {
            super(itemView);
            mainItem = (CardView) itemView.findViewById(R.id.layout_main_item);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            btnDelete = (ImageView) itemView.findViewById(R.id.btn_delete);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
            tvCompanyName = (CustomTextView) itemView.findViewById(R.id.tv_company_name);
            tvNumProjects = (CustomTextView) itemView.findViewById(R.id.tv_num_projects);
            tvCharge = (CustomTextView) itemView.findViewById(R.id.tv_charges);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.tv_description);
            tvServiceType = (CustomTextView) itemView.findViewById(R.id.tv_service_type);
            radioService = (AppCompatRadioButton) itemView.findViewById(R.id.radio_select_service);

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);
            if (isProvider) {
                radioService.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                if (selectService) {
                    radioService.setVisibility(View.VISIBLE);
                }
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    private void openDialogToDeleteService(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this service?");
        builder.setTitle("Confirmation");

        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateSubcategory(position);
            }
        });

        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateSubcategory(final int position) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));


        final JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("service_id", listServices.get(position).getService_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "updateSubcategory(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.DELETE_SERVICE, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "updateSubcategory(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
//                            listSubcategories.remove(position);
//                            notifyItemRemoved(position);
                            ((MyServicesActivity) context).fetchServiceList();
                        } else {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
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
}
