package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.OfferListActivity;
import com.ebabu.tooreest.beans.Offer;
import com.ebabu.tooreest.beans.Quote;
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
public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.OfferViewHolder> {

    private final static String TAG = QuoteAdapter.class.getSimpleName();
    private Context context;
    private List<Quote> listQuote;

    public QuoteAdapter(Context context, List<Quote> listQuote) {
        this.context = context;
        this.listQuote = listQuote;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_quote, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, final int position) {
        final Quote quote = listQuote.get(position);
        if (quote != null) {
            if (quote.getName() != null) {
                holder.tvName.setText(quote.getName());
            } else {
                holder.tvName.setText(IKeyConstants.NA);
            }

            if (quote.getEmail() != null) {
                holder.tvEmail.setText(quote.getEmail());
            } else {
                holder.tvEmail.setText(IKeyConstants.NA);
            }

            if (quote.getMobile() != null) {
                holder.tvMobile.setText(quote.getMobile());
            } else {
                holder.tvMobile.setText(IKeyConstants.NA);
            }

            if (quote.getDescription() != null) {
                holder.tvDescription.setText(quote.getDescription());
            } else {
                holder.tvDescription.setText(IKeyConstants.NA);
            }

            if (quote.getBudget() != null) {
                holder.tvBudget.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + quote.getBudget());
            } else {
                holder.tvBudget.setText(IKeyConstants.NA);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listQuote.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvName, tvDescription, tvMobile, tvBudget, tvEmail;

        public OfferViewHolder(View itemView) {
            super(itemView);
            tvName = (CustomTextView) itemView.findViewById(R.id.tv_name);
            tvDescription = (CustomTextView) itemView.findViewById(R.id.tv_description);
            tvMobile = (CustomTextView) itemView.findViewById(R.id.tv_mobile);
            tvBudget = (CustomTextView) itemView.findViewById(R.id.tv_budget);
            tvEmail = (CustomTextView) itemView.findViewById(R.id.tv_email);
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
