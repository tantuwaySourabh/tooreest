package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.androidquery.AQuery;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.ProviderDetailsForCustomerActivity;
import com.ebabu.tooreest.activity.ProviderListActivity;
import com.ebabu.tooreest.beans.Provider;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ServiceViewHolder> {

    private Context context;
    private List<Provider> listServices;
    private Resources resources;
    private static int colorDarkYellow, colorStartGray;
    private int listType;

    public ProviderAdapter(Context context, List<Provider> listServices, int listType) {
        this.context = context;
        this.listServices = listServices;
        resources = context.getResources();
        colorDarkYellow = resources.getColor(R.color.yellow);
        colorStartGray = resources.getColor(R.color.gray);
        this.listType = listType;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_my_service, parent, false);
        return new ServiceViewHolder(view, listType);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        final Provider provider = listServices.get(position);

        if (provider != null) {

            if (provider.getImage() != null && provider.getImage().startsWith(IKeyConstants.HTTP)) {
                new AQuery(context).id(holder.ivImage).image(provider.getImage());
            } else {
                holder.ivImage.setImageResource(R.mipmap.default_icon);
            }

            if (provider.getCompany_name() != null) {
                holder.tvCompanyName.setText(provider.getCompany_name());
            } else {
                holder.tvCompanyName.setText(IKeyConstants.NA);
            }

            holder.tvNumProjects.setText(provider.getProjectdone() + IKeyConstants.EMPTY);

            if (provider.getCharges() != null) {
                holder.tvCharge.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + provider.getCharges());
            } else {
                holder.tvCharge.setText(IKeyConstants.NA);
            }

            holder.ratingBar.setRating(provider.getRating());

            holder.mainItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProviderDetailsForCustomerActivity.class);
                    intent.putExtra(IKeyConstants.PROVIDER, provider);
                    if (listType == IKeyConstants.PROVIDER_FAVORITES || listType == IKeyConstants.PROVIDER_FEATURED) {
                        intent.putExtra(IKeyConstants.TO_SELECT_SERVICE, true);
                    }
                    if (context instanceof ProviderListActivity) {
                        intent.putExtra(IKeyConstants.SUBCAT_ID, ((ProviderListActivity) context).subcatId);
                        intent.putExtra(IKeyConstants.SUBCAT_NAME, ((ProviderListActivity) context).subcatName);
                    }
                    intent.putExtra(IKeyConstants.PROVIDER_LIST_TYPE, listType);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listServices.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        RatingBar ratingBar;
        CustomTextView tvCompanyName, tvFullname, tvNumProjects, tvCharge;
        CardView mainItem;

        public ServiceViewHolder(View itemView, int listType) {
            super(itemView);
            mainItem = (CardView) itemView.findViewById(R.id.layout_main_item);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
            tvCompanyName = (CustomTextView) itemView.findViewById(R.id.tv_company_name);
            tvNumProjects = (CustomTextView) itemView.findViewById(R.id.tv_num_projects);
            tvCharge = (CustomTextView) itemView.findViewById(R.id.tv_charges);

            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(colorStartGray, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(colorDarkYellow, PorterDuff.Mode.SRC_ATOP);

            if (listType == IKeyConstants.PROVIDER_FEATURED || listType == IKeyConstants.PROVIDER_FAVORITES) {
                itemView.findViewById(R.id.layout_charges).setVisibility(View.GONE);
            }
        }
    }
}
