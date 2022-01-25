package com.pos.salon.adapter.RepairAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.repairModel.RepairModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RepairListAdapter extends RecyclerView.Adapter<RepairListAdapter.MyViewHolder>{

    public Context context;
    RepairListAdapter.OnClicked onClicked;
    ArrayList<RepairModel> repairItemList = new ArrayList<>();


    public RepairListAdapter(Context context, ArrayList<RepairModel> repairItemList){

        this.context=context;
        this.repairItemList=repairItemList;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_created_date,txt_repair_id,txt_customer_repair,txt_repair_serial_no,txt_repair_contact,txt_repair_brand,txt_repair_model,txt_repair_status;
        ImageView img_forward;
        LinearLayout lay_repair_forward;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_repair_id=itemView.findViewById(R.id.txt_repair_id);
            txt_customer_repair=itemView.findViewById(R.id.txt_customer_repair);
            txt_repair_serial_no=itemView.findViewById(R.id.txt_repair_serial_no);
            txt_repair_contact=itemView.findViewById(R.id.txt_repair_contact);
            txt_repair_brand=itemView.findViewById(R.id.txt_repair_brand);
            txt_repair_model=itemView.findViewById(R.id.txt_repair_model);
            txt_repair_status=itemView.findViewById(R.id.txt_repair_status);
            img_forward=itemView.findViewById(R.id.img_forward);
            lay_repair_forward=itemView.findViewById(R.id.lay_repair_forward);
            txt_created_date=itemView.findViewById(R.id.txt_created_date);

        }

    }

    @NonNull
    @Override
    public RepairListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repair_list_items, parent, false);
        return new RepairListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final RepairListAdapter.MyViewHolder viewHolder, final int i) {

        RepairModel model = repairItemList.get(i);

        viewHolder.txt_repair_id.setText(String.valueOf(model.id));
        viewHolder.txt_customer_repair.setText(model.customer);
        viewHolder.txt_repair_serial_no.setText(model.product);
        viewHolder.txt_repair_contact.setText(model.contact_no);
        viewHolder.txt_repair_brand.setText(model.brand);
        viewHolder.txt_repair_model.setText(model.products_model);


        String dateArray=model.created_at;

//        Log.e("datetime",model.order_Date);

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(dateArray);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate1 = spf.format(newDate);


        String[]  separated = strDate1.split("\\ ");
        String part1 = separated[0];
        String part2 = separated[1];

        viewHolder.txt_created_date.setText(part1 + " | " + part2);



        String status=model.repair_status;

        viewHolder.txt_repair_status.setText(status.toUpperCase());



        viewHolder.lay_repair_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }

        });
    }

    @Override
    public int getItemCount() {
        return repairItemList.size();
    }

    public void setOnItmeClicked(RepairListAdapter.OnClicked onClicked) {
        this.onClicked = (RepairListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}