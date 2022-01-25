package com.pos.salon.adapter.CustomerAdapters;

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
import com.pos.salon.model.listModel.CustmerListModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.MyViewHolder>{

    public Context context;
    CustomerListAdapter.OnClicked onClicked;
    ArrayList<CustmerListModel> customerList;


    public CustomerListAdapter(Context context,ArrayList<CustmerListModel> customerList){

        this.context=context;
        this.customerList=customerList;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_contact_id,txt_customer_name,txt_registration_Date,txt_total_sale_due,txt_total_return_due,txt_mobile,txt_active,txt_in_active,txt_resend_otp;
        ImageView img_forward;
        LinearLayout lay_forward;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_contact_id=itemView.findViewById(R.id.txt_contact_id);
            txt_customer_name=itemView.findViewById(R.id.txt_customer_name);
            txt_registration_Date=itemView.findViewById(R.id.txt_registration_Date);
            txt_total_sale_due=itemView.findViewById(R.id.txt_total_sale_due);
            txt_total_return_due=itemView.findViewById(R.id.txt_total_return_due);
            txt_mobile=itemView.findViewById(R.id.txt_mobile);
            txt_active=itemView.findViewById(R.id.txt_active);
            txt_in_active=itemView.findViewById(R.id.txt_in_active);
            txt_resend_otp=itemView.findViewById(R.id.txt_resend_otp);
            img_forward=itemView.findViewById(R.id.img_forward);
            lay_forward=itemView.findViewById(R.id.lay_forward);



        }

    }

    @NonNull
    @Override
    public CustomerListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custmer_list_items, parent, false);
        return new CustomerListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final CustomerListAdapter.MyViewHolder viewHolder, final int i) {

        CustmerListModel model = customerList.get(i);

        viewHolder.txt_contact_id.setText(model.contact_id);
        viewHolder.txt_customer_name.setText(model.name);

        String orderDate =model.created_at;
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(orderDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate1 = spf.format(newDate);


        String[] separated = strDate1.split("\\ ");

        String part1="",part2="";

        part1 = separated[0];
        part2 = separated[1];

        viewHolder.txt_registration_Date.setText(part1 + "  |  " + part2);

        viewHolder.txt_total_sale_due.setText(model.total_sell_return);
        viewHolder.txt_mobile.setText(model.mobile);



        if(model.isActive==0){

            viewHolder.txt_resend_otp.setVisibility(View.GONE);
            viewHolder.txt_in_active.setVisibility(View.VISIBLE);
            viewHolder.txt_active.setVisibility(View.INVISIBLE);
        }else {

            viewHolder.txt_resend_otp.setVisibility(View.GONE);
            viewHolder.txt_in_active.setVisibility(View.INVISIBLE);
            viewHolder.txt_active.setVisibility(View.VISIBLE);
        }

        viewHolder.img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClicked != null)
                    onClicked.setOnClickedItem(i,v);
            }

        });



       Double dTotalInvoice = Double.valueOf(model.getTotal_invoice());
        Double dTotalInvoiceReceived = Double.valueOf(model.getInvoice_received());

        Double dTotalSellReturn = Double.valueOf(model.getTotal_sell_return());
        Double dSellReturnPaid = Double.valueOf(model.getSell_return_paid());

            viewHolder.txt_total_sale_due.setText(String.format("%.2f", (dTotalInvoice - dTotalInvoiceReceived)));
        viewHolder.txt_total_return_due.setText(String.format("%.2f", (dTotalSellReturn - dSellReturnPaid)));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void setOnItmeClicked(CustomerListAdapter.OnClicked onClicked) {
        this.onClicked = (CustomerListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position,View v);
    }
}
