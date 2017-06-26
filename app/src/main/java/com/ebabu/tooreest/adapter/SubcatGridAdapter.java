package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.ProviderListActivity;
import com.ebabu.tooreest.beans.Subcategory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.ArrayList;

/**
 * Created by hp on 07/01/2017.
 */
public class SubcatGridAdapter extends RecyclerView.Adapter<SubcatGridAdapter.SubcatViewHolder> {

    private Context context;
    private ArrayList<Subcategory> listSubcategories;

    public SubcatGridAdapter(Context context, ArrayList<Subcategory> listSubcategories) {
        this.context = context;
        this.listSubcategories = listSubcategories;
    }

    @Override
    public SubcatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_subcat_grid, parent, false);
        return new SubcatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubcatViewHolder holder, final int position) {
        final Subcategory subcategory = listSubcategories.get(position);

        if (subcategory.getSubcategory_name() != null) {
            holder.tvSubcat.setText(subcategory.getSubcategory_name());
        } else {
            holder.tvSubcat.setText(IKeyConstants.NA);
        }

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProviderListActivity.class);
                intent.putExtra(IKeyConstants.SUBCAT_ID, subcategory.getSubcategory_id());
                intent.putExtra(IKeyConstants.SUBCAT_NAME, subcategory.getSubcategory_name());
                intent.putExtra(IKeyConstants.PROVIDER_LIST_TYPE, IKeyConstants.PROVIDER_BY_CATEGORY);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSubcategories.size();
    }

    static class SubcatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMain;
        CustomTextView tvSubcat;

        public SubcatViewHolder(View itemView) {
            super(itemView);
            layoutMain = (LinearLayout) itemView.findViewById(R.id.layout_main);
            tvSubcat = (CustomTextView) itemView.findViewById(R.id.tv_cat_name);
        }
    }
}
