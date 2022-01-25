package com.pos.salon.adapter.SupplierAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pos.salon.R;
import com.pos.salon.model.listModel.ListPosModel;

import java.util.ArrayList;

public class SupplierDetailAdapter extends RecyclerView.Adapter<SupplierDetailAdapter.MyViewHolder> {

    public Context context;
    ArrayList<ListPosModel> contactSalesList;
    public SupplierDetailAdapter(Context context, ArrayList<ListPosModel> contactSalesList) {
        this.context = context;
        this.contactSalesList = contactSalesList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

        }

    }
//
    @NonNull
    @Override
    public SupplierDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_detail_items, parent, false);
        return new SupplierDetailAdapter.MyViewHolder(itemView);

    }
//
    @Override
    public void onBindViewHolder(final SupplierDetailAdapter.MyViewHolder viewHolder, final int i) {


    }
//
    @Override
    public int getItemCount() {
        return contactSalesList.size();
    }
}
