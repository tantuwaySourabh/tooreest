package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.ProviderOffer;

import java.util.List;

/**
 * Created by Sahitya on 2/7/2017.
 */

public class ProviderOfferAdapter extends RecyclerView.Adapter<ProviderOfferAdapter.ViewHolder> {
    private Context context;
    private List<ProviderOffer> offerList;

    public ProviderOfferAdapter(Context context, List<ProviderOffer> offerList) {
        this.context = context;
        this.offerList = offerList;
    }

    @Override
    public ProviderOfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item_provider_offer, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProviderOfferAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
