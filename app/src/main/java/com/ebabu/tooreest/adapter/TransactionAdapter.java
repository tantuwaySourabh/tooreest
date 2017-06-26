package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.beans.TransactionHistory;
import com.ebabu.tooreest.constant.IKeyConstants;
import com.ebabu.tooreest.customview.CustomTextView;
import com.ebabu.tooreest.utils.Utils;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<TransactionHistory> listTransaction;
    private Resources resources;

    public TransactionAdapter(Context context, List<TransactionHistory> listTransaction) {
        this.context = context;
        this.listTransaction = listTransaction;
        this.resources = context.getResources();
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        TransactionHistory transactionHistory = listTransaction.get(position);
        if (transactionHistory != null) {
            if (transactionHistory.getCreate_at() != null && !transactionHistory.getCreate_at().isEmpty()) {
                holder.tvDate.setText(Utils.displayDateFormat.format(Long.parseLong(transactionHistory.getCreate_at())));
            } else {
                holder.tvDate.setText(IKeyConstants.EMPTY);
            }

            if (transactionHistory.getAmount() != null && !transactionHistory.getAmount().isEmpty()) {
                holder.tvAmount.setText(IKeyConstants.DOLLAR_SIGN + IKeyConstants.SPACE + transactionHistory.getAmount());
            } else {
                holder.tvAmount.setText(IKeyConstants.NA);
            }

            if (transactionHistory.getAdd_or_deduct() != null && !transactionHistory.getAdd_or_deduct().isEmpty()) {
                if (IKeyConstants.TRANSACTION_ADD.equalsIgnoreCase(transactionHistory.getAdd_or_deduct())) {
                    holder.tvPlusMinus.setText(IKeyConstants.PLUS_SIGN);
                    holder.tvAmount.setTextColor(resources.getColor(R.color.parrot_green));
                } else {
                    holder.tvPlusMinus.setText(IKeyConstants.MINUS_SIGN);
                    holder.tvAmount.setTextColor(resources.getColor(R.color.medium_red));
                }
            } else {
                holder.tvPlusMinus.setText(IKeyConstants.EMPTY);
            }

            if (transactionHistory.getTitle() != null && !transactionHistory.getTitle().isEmpty()) {
                holder.tvTitle.setText(Utils.capitalizeString(transactionHistory.getTitle()));
            } else {
                holder.tvTitle.setText(IKeyConstants.NA);
            }

            if (transactionHistory.getOrder_id() != null && !transactionHistory.getOrder_id().isEmpty()) {
                holder.tvOrderId.setText(context.getString(R.string.order_id_n, transactionHistory.getOrder_id()));
            } else {
                holder.tvOrderId.setText(IKeyConstants.EMPTY);
            }

            if (transactionHistory.getOrder_id() != null && !transactionHistory.getOrder_id().isEmpty()) {
                holder.tvTransactionId.setText(context.getString(R.string.transaction_id_n, transactionHistory.getOrder_id()));
            } else {
                holder.tvTransactionId.setText(IKeyConstants.EMPTY);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listTransaction.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        CustomTextView tvPlusMinus, tvOrderId, tvTransactionId, tvAmount, tvTitle, tvDate;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            tvPlusMinus = (CustomTextView) itemView.findViewById(R.id.tv_plus_minus);
            tvOrderId = (CustomTextView) itemView.findViewById(R.id.tv_order_id);
            tvTransactionId = (CustomTextView) itemView.findViewById(R.id.tv_transaction_number);
            tvAmount = (CustomTextView) itemView.findViewById(R.id.tv_amount);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.tv_title);
            tvDate = (CustomTextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
