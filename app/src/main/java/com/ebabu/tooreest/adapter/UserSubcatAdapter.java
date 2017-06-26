package com.ebabu.tooreest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.Subcategory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomEditText;
import com.ebabu.tooreest.customview.CustomTextView;

import java.util.ArrayList;

/**
 * Created by hp on 07/01/2017.
 */
public class UserSubcatAdapter extends RecyclerView.Adapter<UserSubcatAdapter.SubcatViewHolder> {

    private Context context;
    private ArrayList<Subcategory> listSubcategories;

    public UserSubcatAdapter(Context context, ArrayList<Subcategory> listSubcategories) {
        this.context = context;
        this.listSubcategories = listSubcategories;
    }

    @Override
    public SubcatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_user_subcat, parent, false);
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

        holder.tvPrice.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + subcategory.getFees());

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listSubcategories.remove(position);
                    notifyItemRemoved(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogToUpdateFees(subcategory, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSubcategories.size();
    }

    static class SubcatViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvSubcat, tvPrice;
        ImageView btnRemove;

        public SubcatViewHolder(View itemView) {
            super(itemView);
            tvSubcat = (CustomTextView) itemView.findViewById(R.id.tv_subcat);
            tvPrice = (CustomTextView) itemView.findViewById(R.id.tv_price);
            btnRemove = (ImageView) itemView.findViewById(R.id.btn_remove);
        }
    }

    private void openDialogToUpdateFees(final Subcategory subcategory, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.enter_amount));

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_dialog_place_bid, null);
        builder.setView(view);
        final CustomEditText etBidAmount = (CustomEditText) view.findViewById(R.id.et_bid_amount);
        etBidAmount.setHint(context.getString(R.string.enter_amount));

        builder.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String strFees = etBidAmount.getText().toString();
                int fees;
                if (strFees.isEmpty()) {
                    fees = 100;
                } else {
                    fees = Integer.parseInt(strFees);
                }
                subcategory.setFees(fees);
                notifyItemChanged(position);
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = ((Activity) context).getCurrentFocus();
                inputMethodManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
            }
        });

        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
