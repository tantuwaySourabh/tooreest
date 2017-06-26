package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.OfferListActivity;
import com.ebabu.tooreest.beans.Offer;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.constant.IUrlConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.MyLoading;
import com.ebabu.tooreest.utils.AppPreference;
import com.ebabu.tooreest.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {

    private final static String TAG = OffersAdapter.class.getSimpleName();
    private Context context;
    private List<Offer> listOffers;
    private boolean isProvider;

    public OffersAdapter(Context context, List<Offer> listOffers, boolean isProvider) {
        this.context = context;
        this.listOffers = listOffers;
        this.isProvider = isProvider;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_offer, parent, false);
        return new OfferViewHolder(view, isProvider);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, final int position) {
        final Offer offer = listOffers.get(position);
        if (offer != null) {
            if (offer.getTitle() != null) {
                holder.tvTitle.setText(offer.getTitle());
            } else {
                holder.tvTitle.setText(IKeyConstants.NA);
            }

            if (offer.getDescription() != null) {
                holder.tvDescription.setText(offer.getDescription());
            } else {
                holder.tvDescription.setText(IKeyConstants.NA);
            }

            if (offer.getAmount() != null) {
                holder.tvAmount.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + offer.getAmount());
            } else {
                holder.tvAmount.setText(IKeyConstants.NA);
            }

            if (offer.getStart_date() != null && offer.getEnd_date() != null) {
                holder.tvDate.setText("Valid from " + offer.getStart_date() + " to " + offer.getEnd_date());
            } else {
                holder.tvDate.setText(IKeyConstants.NA);
            }

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialogToDeleteOffer(position, offer);
                }
            });

            if (!isProvider) {
                holder.mainItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        Intent intent = new Intent();
                        intent.putExtra(IKeyConstants.OFFER_ID, offer.getOffer_id());
                        intent.putExtra(IKeyConstants.DISCOUNT, offer.getAmount());
                        activity.setResult(activity.RESULT_OK, intent);
                        activity.finish();

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return listOffers.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvTitle, tvDescription, tvAmount, tvDate;
        ImageView btnDelete;
        CardView mainItem;

        public OfferViewHolder(View itemView, boolean isProvider) {
            super(itemView);
            mainItem = (CardView) itemView.findViewById(R.id.layout_main_item);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.tv_description);
            tvAmount = (CustomTextView) itemView.findViewById(R.id.tv_amount);
            tvDate = (CustomTextView) itemView.findViewById(R.id.tv_date);
            btnDelete = (ImageView) itemView.findViewById(R.id.btn_delete);
            if (isProvider) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }
    }

    private void openDialogToDeleteOffer(final int position, final Offer offer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this offer?");
        builder.setTitle("Confirmation");

        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteOffer(position, offer);
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

    private void deleteOffer(final int position, Offer offer) {

        if (!Utils.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        final MyLoading myLoading = new MyLoading(context);
        myLoading.show(context.getString(R.string.please_wait));

        final JSONObject jsonObject = new JSONObject();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        try {
            jsonObject.put("offer_id", offer.getOffer_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "deleteOffer(): jsonObject=" + jsonObject);

        new AQuery(context).post(IUrlConstants.DELETE_OFFER, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d(TAG, "deleteOffer(): url= " + url + ", json= " + json + ", errorCode=" + status.getCode() + ", error=" + status.getError());
                if (json != null) {
                    try {
                        String strStatus = json.getString(IKeyConstants.STATUS);
                        if (IKeyConstants.SUCCESS.equalsIgnoreCase(strStatus)) {
//                            listSubcategories.remove(position);
//                            notifyItemRemoved(position);
                            ((OfferListActivity) context).fetchOffersList();
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
