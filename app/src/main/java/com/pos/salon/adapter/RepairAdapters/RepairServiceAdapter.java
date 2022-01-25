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

public class RepairServiceAdapter extends RecyclerView.Adapter<RepairServiceAdapter.MyViewHolder> {

    public Context context;

    ArrayList<RepairModel> repairServiceList = new ArrayList<>();


    public RepairServiceAdapter(Context context, ArrayList<RepairModel> repairServiceList) {

        this.context = context;
        this.repairServiceList = repairServiceList;
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
    public RepairServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repair_parts_items, parent, false);
        return new RepairServiceAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final RepairServiceAdapter.MyViewHolder viewHolder, final int i) {

        RepairModel model = repairServiceList.get(i);
        viewHolder.txt_repair_detail_name.setText(model.detailRepairServiceName);
        viewHolder.txt_repair_detail_pricee.setText(model.detailRepairServicePrice);
        viewHolder.txt_repair_detail_status.setText(model.detailRepairServiceStatus);


    }

    @Override
    public int getItemCount() {
        return repairServiceList.size();
    }


}
