package com.ebabu.tooreest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ebabu.tooreest.R;
import com.ebabu.tooreest.adapter.CustomerInvoiceAdapter;
import com.ebabu.tooreest.beans.CustomerInvoice;
import com.ebabu.tooreest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomerInvoiceActivity extends AppCompatActivity {

    private Context context;
    private LinearLayoutManager layoutManager;
    private RecyclerView rvInvoice;
    private List<CustomerInvoice> listInvoice;
    private CustomerInvoiceAdapter bookingInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_invoice);
        context = CustomerInvoiceActivity.this;
        initView();
        Utils.setUpToolbar(context, getString(R.string.invoice));
    }

    private void initView() {
        rvInvoice = (RecyclerView) findViewById(R.id.rv_invoice);

        listInvoice = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            listInvoice.add(new CustomerInvoice());
        }
        bookingInvoice = new CustomerInvoiceAdapter(context, listInvoice);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvInvoice.setLayoutManager(layoutManager);
        rvInvoice.setAdapter(bookingInvoice);
    }
}
