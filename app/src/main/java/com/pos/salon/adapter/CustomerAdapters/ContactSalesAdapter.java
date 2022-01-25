package com.pos.salon.adapter.CustomerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.listModel.ListPosModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContactSalesAdapter extends RecyclerView.Adapter<ContactSalesAdapter.MyViewHolder>{
    public Context context;
    ArrayList<ListPosModel> contactSalesList = new ArrayList<>();


    public ContactSalesAdapter(Context context,ArrayList<ListPosModel> contactSalesList){

        this.context=context;
        this.contactSalesList=contactSalesList;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_contactSaleInvoice,txt_contactSaleName,contactSaleDate,txt_contactSaleTotalAmount,txt_contactSaleTotalPaid,txt_contactSaleTotalRemain,txt_totalSalePaymentStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_contactSaleInvoice=itemView.findViewById(R.id.txt_contactSaleInvoice);
            txt_contactSaleName=itemView.findViewById(R.id.txt_contactSaleName);
            contactSaleDate=itemView.findViewById(R.id.contactSaleDate);
            txt_contactSaleTotalAmount=itemView.findViewById(R.id.txt_contactSaleTotalAmount);
            txt_contactSaleTotalPaid=itemView.findViewById(R.id.txt_contactSaleTotalPaid);
            txt_contactSaleTotalRemain=itemView.findViewById(R.id.txt_contactSaleTotalRemain);
            txt_totalSalePaymentStatus=itemView.findViewById(R.id.txt_totalSalePaymentStatus);
        }

    }

    @NonNull
    @Override
    public ContactSalesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_sales_item, parent, false);
        return new ContactSalesAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ContactSalesAdapter.MyViewHolder viewHolder, final int i) {

        ListPosModel model=contactSalesList.get(i);

        viewHolder.txt_contactSaleInvoice.setText("Sale Details(Invoice No.: " + model.contactInvoice + ")");
        viewHolder.txt_contactSaleName.setText(model.contactName);

        String orderDate = model.contactTtansactionDate;
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

        viewHolder.contactSaleDate.setText(part1 + "  |  " + part2);

        viewHolder.txt_contactSaleTotalAmount.setText(String.format("%.2f",Double.valueOf(model.contactTotalAmount)));
        viewHolder.txt_contactSaleTotalPaid.setText(String.format("%.2f",Double.valueOf(model.contactTotalPaid)));


        Double totalAmount=0.0;
        Double totalPaid=0.0;
        if(!model.contactTotalAmount.isEmpty() && !model.contactTotalAmount.equalsIgnoreCase("null")){
            totalAmount=Double.valueOf(model.contactTotalAmount);
        }
        if(!model.contactTotalPaid.isEmpty() && !model.contactTotalPaid.equalsIgnoreCase("null")) {
            totalPaid = Double.valueOf(model.contactTotalPaid);
        }
        Double totalRemain=totalAmount-totalPaid;

        viewHolder.txt_contactSaleTotalRemain.setText(String.format("%.2f",totalRemain));
        viewHolder.txt_totalSalePaymentStatus.setText(model.contactPaymentstatus);

    }

    @Override
    public int getItemCount() {
        return contactSalesList.size();
    }
}