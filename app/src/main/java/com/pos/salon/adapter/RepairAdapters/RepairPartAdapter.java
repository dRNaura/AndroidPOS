package com.pos.salon.adapter.RepairAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.repairModel.RepairModel;

import java.util.ArrayList;

public class RepairPartAdapter extends RecyclerView.Adapter<RepairPartAdapter.MyViewHolder> {

    public Context context;

    ArrayList<RepairModel> repairDetailList = new ArrayList<>();


    public RepairPartAdapter(Context context, ArrayList<RepairModel> repairDetailList) {

        this.context = context;
        this.repairDetailList = repairDetailList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_repair_detail_name, txt_repair_detail_pricee, txt_repair_detail_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_repair_detail_name = itemView.findViewById(R.id.txt_repair_detail_name);
            txt_repair_detail_pricee = itemView.findViewById(R.id.txt_repair_detail_pricee);
            txt_repair_detail_status = itemView.findViewById(R.id.txt_repair_detail_status);

        }

    }

    @NonNull
    @Override
    public RepairPartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repair_parts_items, parent, false);
        return new RepairPartAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final RepairPartAdapter.MyViewHolder viewHolder, final int i) {

        RepairModel model = repairDetailList.get(i);
        viewHolder.txt_repair_detail_name.setText(model.detailRepairName);
        viewHolder.txt_repair_detail_pricee.setText(model.detailRepairPrice);
        viewHolder.txt_repair_detail_status.setText(model.detailRepairStatus);


    }

    @Override
    public int getItemCount() {
        return repairDetailList.size();
    }

}
