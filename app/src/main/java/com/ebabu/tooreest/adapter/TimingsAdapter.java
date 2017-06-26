package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Timing;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.List;

/**
 * Created by hp on 16/09/2016.
 */
public class TimingsAdapter extends RecyclerView.Adapter<TimingsAdapter.TimingViewHolder> {

    private Context context;
    private List<Timing> listTimings;

    public TimingsAdapter(Context context, List<Timing> listTimings) {
        this.context = context;
        this.listTimings = listTimings;
    }

    @Override
    public TimingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_timing, parent, false);
        return new TimingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimingViewHolder holder, final int position) {
        Timing timing = listTimings.get(position);

        if (timing.getFrom() != null && !timing.getFrom().isEmpty()) {
            holder.tvFrom.setText(timing.getFrom());
        } else {
            holder.tvFrom.setText(IKeyConstants.NA);
        }

        if (timing.getTo() != null && !timing.getTo().isEmpty()) {
            holder.tvTo.setText(timing.getTo());
        } else {
            holder.tvTo.setText(IKeyConstants.NA);
        }

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (listTimings.size() == 1) {
                        listTimings.clear();
                        notifyDataSetChanged();
                    } else {
                        listTimings.remove(position);
                        notifyItemRemoved(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTimings.size();
    }


    static class TimingViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvFrom, tvTo;
        ImageView btnRemove;

        public TimingViewHolder(View itemView) {
            super(itemView);
            tvFrom = (CustomTextView) itemView.findViewById(R.id.tv_from);
            tvTo = (CustomTextView) itemView.findViewById(R.id.tv_to);
            btnRemove = (ImageView) itemView.findViewById(R.id.btn_remove);
        }
    }


}
