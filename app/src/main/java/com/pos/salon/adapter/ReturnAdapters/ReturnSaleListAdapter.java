package com.pos.salon.adapter.ReturnAdapters;

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
import com.pos.salon.model.listModel.ReturnSaleListModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReturnSaleListAdapter extends RecyclerView.Adapter<ReturnSaleListAdapter.MyViewHolder> {

    public Context context;
    OnClicked onClicked;
    ArrayList<ReturnSaleListModel> returnSalesList = new ArrayList<>();

    public ReturnSaleListAdapter(Context context, ArrayList<ReturnSaleListModel> returnSalesList) {
        this.context = context;
        this.returnSalesList = returnSalesList;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_forward;
        LinearLayout linear_layout_id;
        TextView txt_return_list_location, txt_return_list_order_date, txt_return_list_invoice_no, txt_return_list_cus_name;
        TextView txt_return_list_pay_pars, txt_return_list_pay_paid, txt_return_list_pay_due;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_forward = itemView.findViewById(R.id.img_forward);
            txt_return_list_location = itemView.findViewById(R.id.txt_return_list_location);
            txt_return_list_order_date = itemView.findViewById(R.id.txt_return_list_order_date);
            txt_return_list_invoice_no = itemView.findViewById(R.id.txt_return_list_invoice_no);
            txt_return_list_cus_name = itemView.findViewById(R.id.txt_return_list_cus_name);
            txt_return_list_pay_pars = itemView.findViewById(R.id.txt_return_list_pay_pars);
            txt_return_list_pay_paid = itemView.findViewById(R.id.txt_return_list_pay_paid);
            txt_return_list_pay_due = itemView.findViewById(R.id.txt_return_list_pay_due);
            linear_layout_id = itemView.findViewById(R.id.linear_layout_id);

        }
    }

    @NonNull
    @Override
    public ReturnSaleListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.return_sale_list_items, parent, false);
        return new ReturnSaleListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ReturnSaleListAdapter.MyViewHolder viewHolder, final int i) {

        ReturnSaleListModel model = returnSalesList.get(i);

        String dateArray = model.return_transaction_date;

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = null;
        try {
            newDate = spf.parse(dateArray);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate1 = spf.format(newDate);


        String[] separated = strDate1.split("\\ ");
        String part1 = separated[0];
        String part2 = separated[1];

        viewHolder.txt_return_list_order_date.setText(part1 + " | " + part2);
        viewHolder.txt_return_list_location.setText(model.return_business_location);

        viewHolder.txt_return_list_invoice_no.setText(model.return_invoice_no);
        viewHolder.txt_return_list_cus_name.setText(model.return_customer_name);

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null) {
                    onClicked.setOnClickedItem(i);
                }
            }
        });


        if (model.retun_payment_status.equalsIgnoreCase("Paid")) {
            viewHolder.txt_return_list_pay_paid.setVisibility(View.VISIBLE);
            viewHolder.txt_return_list_pay_pars.setVisibility(View.GONE);
            viewHolder.txt_return_list_pay_due.setVisibility(View.GONE);

        } else if (model.retun_payment_status.equalsIgnoreCase("Partial")) {
            viewHolder.txt_return_list_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_return_list_pay_pars.setVisibility(View.VISIBLE);
            viewHolder.txt_return_list_pay_due.setVisibility(View.GONE);

        } else if (model.retun_payment_status.equalsIgnoreCase("Due")) {
            viewHolder.txt_return_list_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_return_list_pay_pars.setVisibility(View.GONE);
            viewHolder.txt_return_list_pay_due.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public int getItemCount() {
        return returnSalesList.size();
    }

    public void setOnItmeClicked(ReturnSaleListAdapter.OnClicked onClicked) {
        this.onClicked = (ReturnSaleListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }

}
