package com.pos.salon.adapter.PurchaseSectionAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.PurchaseModel.PurchaseListModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.MyViewHolder> {

    public Context context;
    PurchaseListAdapter.OnClicked onClicked;
    ArrayList<PurchaseListModel> list = new ArrayList<>();


    public PurchaseListAdapter(Context context, ArrayList<PurchaseListModel> list) {

        this.context = context;
        this.list = list;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linear_layout_id;
        TextView order_date,txt_reference_no,txt_location,txt_pay_pars,txt_pay_paid,txt_pay_due,txt_supplier,txt_product_name;
        TextView txt_grand_total,txt_payment_due,txt_payment_status,txt_amount_paid;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            linear_layout_id = itemView.findViewById(R.id.linear_layout_id);
            order_date = itemView.findViewById(R.id.order_date);
            txt_reference_no = itemView.findViewById(R.id.txt_reference_no);
            txt_location = itemView.findViewById(R.id.txt_location);
            txt_pay_pars = itemView.findViewById(R.id.txt_pay_pars);
            txt_pay_paid = itemView.findViewById(R.id.txt_pay_paid);
            txt_pay_due = itemView.findViewById(R.id.txt_pay_due);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_supplier = itemView.findViewById(R.id.txt_supplier);
            txt_grand_total = itemView.findViewById(R.id.txt_grand_total);
            txt_payment_due = itemView.findViewById(R.id.txt_payment_due);
            txt_payment_status = itemView.findViewById(R.id.txt_payment_status);
            txt_amount_paid = itemView.findViewById(R.id.txt_amount_paid);

        }

    }

    @NonNull
    @Override
    public PurchaseListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_list_items, parent, false);
        return new PurchaseListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final PurchaseListAdapter.MyViewHolder viewHolder, final int i) {

        PurchaseListModel model=list.get(i);
        viewHolder.txt_reference_no.setText(model.ref_no);
        viewHolder.txt_supplier.setText(model.name);
        viewHolder.txt_payment_status.setText(model.status);
        viewHolder.txt_location.setText(model.location_name);
//        viewHolder.txt_product_name.setText(model.txt_product_name);
        viewHolder.txt_grand_total.setText("$"+model.final_total);
        viewHolder.txt_amount_paid.setText("$"+model.amount_paid);

        double pay_due= Double.valueOf(model.final_total) -Double.valueOf(model.amount_paid);
        viewHolder.txt_payment_due.setText("$"+(String.format("%.2f",pay_due)));

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(model.transaction_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate1 = spf.format(newDate);
        viewHolder.order_date.setText(strDate1);

        if (model.payment_status.equalsIgnoreCase("Paid")){
            viewHolder.txt_pay_paid.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_pars.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        }else if(model.payment_status.equalsIgnoreCase("Partial")){
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_pars.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        }else if(model.payment_status.equalsIgnoreCase("Due")){
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_pars.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.VISIBLE);

        }


        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(PurchaseListAdapter.OnClicked onClicked) {
        this.onClicked = (PurchaseListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}