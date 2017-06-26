package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.ZoomActivity;
import com.ebabu.tooreest.beans.Documents;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.customview.SquareImageView;

import java.util.List;

/**
 * Created by Sahitya on 2/7/2017.
 */

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private Context context;
    private List<Documents> documentsList;
    private boolean isProvider;

    public DocumentAdapter(Context context, List<Documents> documentsList, boolean isProvider) {
        this.context = context;
        this.documentsList = documentsList;
        this.isProvider = isProvider;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item_my_document, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Documents documents = documentsList.get(position);

        if (documents != null) {
            if (documents.getImage() != null && documents.getImage().startsWith("http")) {
                new AQuery(context).id(holder.ivImage).image(documents.getImage());
            } else {
                holder.ivImage.setImageResource(R.mipmap.default_icon);
            }

            if (documents.getTitle() != null) {
                holder.tvTitle.setText(documents.getTitle());
            } else {
                holder.tvTitle.setText(IKeyConstants.EMPTY);
            }

            holder.mainItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ZoomActivity.class);
                    intent.putExtra(IKeyConstants.IMAGE, documents.getImage());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView ivImage;
        CustomTextView tvTitle;
        CardView mainItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (SquareImageView) itemView.findViewById(R.id.iv_image);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.tv_title);
            mainItem = (CardView) itemView.findViewById(R.id.layout_main_item);
        }
    }
}
