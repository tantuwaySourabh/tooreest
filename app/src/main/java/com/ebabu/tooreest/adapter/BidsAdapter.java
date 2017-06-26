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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.ProviderDetailsForCustomerActivity;
import com.ebabu.tooreest.beans.Bid;
import com.ebabu.tooreest.beans.Job;
import com.ebabu.tooreest.beans.Provider;
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
public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.JobViewHolder> {

    private final static String TAG = BidsAdapter.class.getSimpleName();
    private Context context;
    private List<Bid> listJobs;
    private Resources resources;

    public BidsAdapter(Context context, List<Bid> listJobs) {
        this.context = context;
        this.listJobs = listJobs;
        resources = context.getResources();
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_bids, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, final int position) {
        final Bid bid = listJobs.get(position);
        if (bid != null) {
            if (bid.getProvider_image() != null && bid.getProvider_image().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(holder.ivProviderImage).image(bid.getProvider_image());
            } else {
                holder.ivProviderImage.setImageResource(R.mipmap.personal);
            }

            if (bid.getCreate_at() != null && !bid.getCreate_at().isEmpty()) {
                holder.tvDate.setText(Utils.onlyDateMonth.format(Long.parseLong(bid.getCreate_at())));
            } else {
                holder.tvDate.setText(IKeyConstants.NA);
            }

            if (bid.getFull_name() != null) {
                holder.tvProviderName.setText(bid.getFull_name());
            } else {
                holder.tvProviderName.setText(IKeyConstants.NA);
            }

            if (bid.getIs_urgent() == 1) {
                holder.tvIsUrgent.setVisibility(View.VISIBLE);
            } else {
                holder.tvIsUrgent.setVisibility(View.GONE);
            }

            if (bid.getDescription() != null) {
                holder.tvDescription.setText(bid.getDescription());
            } else {
                holder.tvDescription.setText(IKeyConstants.EMPTY);
            }

            if (bid.getCategory_name() != null) {
                holder.tvCategory.setText(bid.getCategory_name());
            } else {
                holder.tvCategory.setText(IKeyConstants.NA);
            }

            if (bid.getService_name() != null) {
                holder.tvSubcategory.setText(bid.getService_name());
            } else {
                holder.tvSubcategory.setText(IKeyConstants.NA);
            }

            if (bid.getCity() != null && !bid.getCity().isEmpty()) {
                holder.tvCity.setText(bid.getCity());
            } else {
                holder.tvCity.setText(IKeyConstants.NA);
            }

            if (bid.getBudget() != null && !bid.getBudget().isEmpty()) {
                holder.tvBudget.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + bid.getBudget());
            } else {
                holder.tvBudget.setText(IKeyConstants.NA);
            }

            if (bid.getBid_amount() != null && !bid.getBid_amount().isEmpty()) {
                holder.tvBidAmount.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + bid.getBid_amount());
            } else {
                holder.tvBidAmount.setText(IKeyConstants.NA);
            }

            holder.ivProviderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProviderProfile(bid, false);
                }
            });

            holder.tvProviderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProviderProfile(bid, false);
                }
            });

            holder.btnHire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProviderProfile(bid, true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listJobs.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvProviderName, tvIsUrgent, tvDate, tvDescription, tvCategory, tvSubcategory, tvCity, btnHire, tvBudget, tvBidAmount;
        CircleImageView ivProviderImage;

        public JobViewHolder(View itemView) {
            super(itemView);
            tvProviderName = (CustomTextView) itemView.findViewById(R.id.tv_customer_name);
            tvBudget = (CustomTextView) itemView.findViewById(R.id.tv_budget);
            tvBidAmount = (CustomTextView) itemView.findViewById(R.id.tv_bid_amount);
            tvDate = (CustomTextView) itemView.findViewById(R.id.tv_date);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.tv_description);
            tvIsUrgent = (CustomTextView) itemView.findViewById(R.id.tv_urgent);
            tvCategory = (CustomTextView) itemView.findViewById(R.id.tv_category);
            tvSubcategory = (CustomTextView) itemView.findViewById(R.id.tv_subcategory);
            tvCity = (CustomTextView) itemView.findViewById(R.id.tv_city);
            btnHire = (CustomTextView) itemView.findViewById(R.id.btn_hire);
            ivProviderImage = (CircleImageView) itemView.findViewById(R.id.iv_profile_pic);
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

    private void openProviderProfile(Bid bid, boolean openDialog) {
        Provider provider = new Provider();
        provider.setUser_id(bid.getUser_id());
        provider.setAbout_me(bid.getAbout_me());
        provider.setCity(bid.getCity());
        provider.setRating(bid.getAve_rating());
        provider.setFull_name(bid.getFull_name());
        provider.setCompany_name(bid.getCompany_name());
        provider.setFacebook(bid.getFacebook());
        provider.setTwitter(bid.getTwitter());
        provider.setLinkedin(bid.getLinkedin());
        provider.setGoogle_plus(bid.getGoogle_plus());
        provider.setImage(bid.getProvider_image());
        provider.setIs_bank_verify(bid.getIs_bank_verify());
        provider.setIs_id_verify(bid.getIs_id_verify());
        provider.setIs_favorite(bid.getIs_favorite());
        provider.setCharges(bid.getBid_amount());


        Intent intent = new Intent(context, ProviderDetailsForCustomerActivity.class);
        intent.putExtra(IKeyConstants.PROVIDER, provider);
        intent.putExtra(IKeyConstants.FROM_BIDDING, openDialog);
        intent.putExtra(IKeyConstants.SUBCAT_ID, bid.getSubcategory_id());
        intent.putExtra(IKeyConstants.SUBCAT_NAME, bid.getService_name());
        intent.putExtra(IKeyConstants.PROVIDER_LIST_TYPE, IKeyConstants.PROVIDER_FEATURED);
        context.startActivity(intent);
    }
}
