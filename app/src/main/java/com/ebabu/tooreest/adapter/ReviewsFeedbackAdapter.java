package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.ReviewsFeedback;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.List;

/**
 * Created by Sahitya on 2/6/2017.
 */

public class ReviewsFeedbackAdapter extends RecyclerView.Adapter<ReviewsFeedbackAdapter.ReviewViewHolder> {
    private Context context;
    private List<ReviewsFeedback> reviewList;

    public ReviewsFeedbackAdapter(Context context, List<ReviewsFeedback> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_review_n_feedback, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        ReviewsFeedback reviewsFeedback = reviewList.get(position);
        if (reviewsFeedback != null) {
            holder.tvRating.setText(String.format("%.1f", reviewsFeedback.getRating()));
            if (reviewsFeedback.getRating() <= 1) {
                holder.tvRating.setBackgroundResource(R.drawable.red_rounded_solid_bg);
            } else if (reviewsFeedback.getRating() >= 4) {
                holder.tvRating.setBackgroundResource(R.drawable.green_rounded_solid_bg);
            } else {
                holder.tvRating.setBackgroundResource(R.drawable.yellow_rounded_solid_bg);
            }

            if (reviewsFeedback.getUsername() != null) {
                holder.tvUsername.setText(reviewsFeedback.getUsername());
            } else {
                holder.tvUsername.setText(IKeyConstants.NA);
            }

            if (reviewsFeedback.getReview() != null) {
                holder.tvReview.setText(reviewsFeedback.getReview());
            } else {
                holder.tvReview.setText(IKeyConstants.NA);
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvRating, tvUsername, tvReview;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvRating = (CustomTextView) itemView.findViewById(R.id.tv_rating);
            tvUsername = (CustomTextView) itemView.findViewById(R.id.tv_username);
            tvReview = (CustomTextView) itemView.findViewById(R.id.tv_review);
        }
    }
}
