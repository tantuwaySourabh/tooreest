package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Earning;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class WeeklyEarningAdapter extends RecyclerView.Adapter<WeeklyEarningAdapter.ServiceViewHolder> {

    private final static String TAG = WeeklyEarningAdapter.class.getSimpleName();
    private Context context;
    private List<Earning> listEarnings;


    public WeeklyEarningAdapter(Context context, List<Earning> listEarnings) {
        this.context = context;
        this.listEarnings = listEarnings;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_weekly_earning, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, final int position) {
        final Earning earning = listEarnings.get(position);

        if (earning != null) {

            if (earning.getDay() != null) {
                holder.tvDay.setText(earning.getDay());
            } else {
                holder.tvDay.setText(IKeyConstants.NA);
            }

            if (earning.getAmount() != null) {
                holder.tvEarning.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + earning.getAmount());
            } else {
                holder.tvEarning.setText(IKeyConstants.NA);
            }

        }
    }

    @Override
    public int getItemCount() {
        return listEarnings.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvDay, tvEarning;

        public ServiceViewHolder(View itemView) {
            super(itemView);

            tvDay = (CustomTextView) itemView.findViewById(R.id.tv_day);
            tvEarning = (CustomTextView) itemView.findViewById(R.id.tv_earning);

        }
    }


}
