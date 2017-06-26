package com.ebabu.tooreest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.ZoomActivity;
import com.ebabu.tooreest.beans.Job;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 10/01/2017.
 */
public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobViewHolder> {

    private final static String TAG = JobsAdapter.class.getSimpleName();
    private Context context;
    private List<Job> listJobs;
    private Resources resources;

    public JobsAdapter(Context context, List<Job> listJobs) {
        this.context = context;
        this.listJobs = listJobs;
        resources = context.getResources();
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, final int position) {
        final Job job = listJobs.get(position);
        if (job != null) {
            if (job.getImage() != null && job.getImage().startsWith(IKeyConstants.HTTP)) {
                holder.ivImage.setVisibility(View.VISIBLE);
                new AQuery(context).id(holder.ivImage).image(job.getImage());

            } else {
                holder.ivImage.setVisibility(View.GONE);
            }

            if (job.getCustomer_image() != null && job.getCustomer_image().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(holder.ivCustomerImg).image(job.getCustomer_image());
            } else {
                holder.ivCustomerImg.setImageResource(R.mipmap.personal);
            }

            if (job.getCreate_at() != null && !job.getCreate_at().isEmpty()) {
                holder.tvDate.setText(Utils.onlyDateMonth.format(Long.parseLong(job.getCreate_at())));
            } else {
                holder.tvDate.setText(IKeyConstants.NA);
            }

            if (job.getCustomer_name() != null) {
                holder.tvCustomerName.setText(job.getCustomer_name());
            } else {
                holder.tvCustomerName.setText(IKeyConstants.NA);
            }

            if (job.getIs_urgent() == 1) {
                holder.tvIsUrgent.setVisibility(View.VISIBLE);
            } else {
                holder.tvIsUrgent.setVisibility(View.GONE);
            }

            if (job.getDescription() != null) {
                holder.tvDescription.setText(job.getDescription());
            } else {
                holder.tvDescription.setText(IKeyConstants.EMPTY);
            }

            if (job.getJob_category() != null) {
                holder.tvCategory.setText(job.getJob_category());
            } else {
                holder.tvCategory.setText(IKeyConstants.NA);
            }

            if (job.getSubcatgory() != null) {
                holder.tvSubcategory.setText(job.getSubcatgory());
            } else {
                holder.tvSubcategory.setText(IKeyConstants.NA);
            }

            if (job.getCity() != null && !job.getCity().isEmpty()) {
                holder.tvCity.setText(job.getCity());
            } else {
                holder.tvCity.setText(IKeyConstants.NA);
            }

            if (job.getBudget() != null) {
                holder.btnPlaceBid.setText(context.getString(R.string.place_bid_budget, job.getBudget()));
            } else {
                holder.btnPlaceBid.setText(IKeyConstants.NA);
            }
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ZoomActivity.class);
                    intent.putExtra(IKeyConstants.IMAGE, job.getImage());
                    context.startActivity(intent);
                }
            });

            holder.btnPlaceBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToPlaceBid(job, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listJobs.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvCustomerName, tvIsUrgent, tvDate, tvDescription, tvCategory, tvSubcategory, tvCity, btnPlaceBid;
        CircleImageView ivCustomerImg;
        ImageView ivImage;

        public JobViewHolder(View itemView) {
            super(itemView);

            tvCustomerName = (CustomTextView) itemView.findViewById(R.id.tv_customer_name);
            tvDate = (CustomTextView) itemView.findViewById(R.id.tv_date);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.tv_description);
            tvIsUrgent = (CustomTextView) itemView.findViewById(R.id.tv_urgent);
            tvCategory = (CustomTextView) itemView.findViewById(R.id.tv_category);
            tvSubcategory = (CustomTextView) itemView.findViewById(R.id.tv_subcategory);
            tvCity = (CustomTextView) itemView.findViewById(R.id.tv_city);
            btnPlaceBid = (CustomTextView) itemView.findViewById(R.id.btn_place_bid);
            ivCustomerImg = (CircleImageView) itemView.findViewById(R.id.iv_profile_pic);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }

    private void openDialogToPlaceBid(final Job job, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.enter_bid_amount));

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_place_bid, null);
        builder.setView(view);
        final CustomEditText etBidAmount = (CustomEditText) view.findViewById(R.id.et_bid_amount);
        builder.setPositiveButton(context.getString(R.string.place_bid), null);


        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String biddingAmount = etBidAmount.getText().toString().trim();

                        if (biddingAmount.isEmpty()) {
                            etBidAmount.setError(context.getString(R.string.enter_bidding_amount));
                            return;
                        }

                        Integer biddingAmountInt = Integer.parseInt(biddingAmount);
                        if (biddingAmountInt > Integer.parseInt(job.getBudget())) {
                            etBidAmount.setError(context.getString(R.string.maximum_bidding_amount_is_n, job.getBudget()));
                            return;
                        }

                        if (biddingAmountInt < 100) {
                            etBidAmount.setError(context.getString(R.string.minimum_budget_is_100));
                            return;
                        }

                        placeABid(biddingAmount, job, position, alertDialog);
                    }
                });
            }
        });


        alertDialog.show();
    }

    private void placeABid(String biddingAmount, Job job, int position, final AlertDialog alertDialog) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("post_id", job.getJob_id());
            jsonObject.put("budget", biddingAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "placeABid(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.BID_A_JOB, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "placeABid(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
                            Toast.makeText(context, json.getString(IKeyConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                            ((Activity) context).finish();
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
        }.method(AQuery.METHOD_POST).header(IKeyConstants.SECRET_KEY, AppPreference.getInstance(context).getSecretKey()));
    }

}
