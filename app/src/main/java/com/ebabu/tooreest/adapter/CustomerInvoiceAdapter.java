package com.ebabu.tooreest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.activity.PaymentSelectionActivity;
import com.ebabu.tooreest.beans.CustomerInvoice;

import java.util.List;

/**
 * Created by hp on 10/01/2017.
 */
public class CustomerInvoiceAdapter extends RecyclerView.Adapter<CustomerInvoiceAdapter.InvoiceViewHolder>{

    private Context context;
    private List<CustomerInvoice> listCustomerInvoice;

    public CustomerInvoiceAdapter(Context context, List<CustomerInvoice> listCustomerInvoice) {
        this.context = context;
        this.listCustomerInvoice = listCustomerInvoice;
    }

    @Override
    public InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_customer_invoice, parent, false);
        return new InvoiceViewHolder(view);

    }

    @Override
    public void onBindViewHolder(InvoiceViewHolder holder, int position) {
        holder.itemView.findViewById(R.id.btn_reschedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i=new Intent(context,PaymentSelectionActivity.class);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCustomerInvoice.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder{

        public InvoiceViewHolder(View itemView)  {
            super(itemView);
        }

    }
}
