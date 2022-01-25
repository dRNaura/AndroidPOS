package com.pos.salon.adapter.DeliveryOrderAdapter;

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
import com.pos.salon.model.deliveryOrdersModel.Shipping_list_model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShippingListAdapter extends RecyclerView.Adapter<ShippingListAdapter.MyViewHolder> {

    public Context context;
    ShippingListAdapter.OnClicked onClicked;
    ArrayList<Shipping_list_model> list = new ArrayList<>();
    public ShippingListAdapter(Context context, ArrayList<Shipping_list_model> list) {
        this.context = context;
        this.list = list;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_forward, img_return_exists;
        LinearLayout linear_layout_id;
        TextView txt_order_date, txt_invoice_no, txt_cus_name, txt_pay_parse, txt_pay_paid, txt_location, txt_pay_due,txt_delivery_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_forward = itemView.findViewById(R.id.img_forward);
            txt_order_date = itemView.findViewById(R.id.order_date);
            txt_invoice_no = itemView.findViewById(R.id.txt_invoice_no);
            txt_cus_name = itemView.findViewById(R.id.txt_cus_name);
            txt_pay_parse = itemView.findViewById(R.id.txt_pay_pars);
            txt_pay_paid = itemView.findViewById(R.id.txt_pay_paid);
            txt_location = itemView.findViewById(R.id.txt_location);
            txt_pay_due = itemView.findViewById(R.id.txt_pay_due);
            img_return_exists = itemView.findViewById(R.id.img_return_exists);
            linear_layout_id = itemView.findViewById(R.id.linear_layout_id);
            txt_delivery_status = itemView.findViewById(R.id.txt_delivery_status);

        }
    }

    @NonNull
    @Override
    public ShippingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_order_items, parent, false);
        return new ShippingListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ShippingListAdapter.MyViewHolder viewHolder, final int i) {


        Shipping_list_model model = list.get(i);

        String dateArray = model.transaction_date;

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


        String[] separated = strDate1.split("\\ ");
        String part1 = separated[0];
        String part2 = separated[1];

        viewHolder.txt_order_date.setText(part1 + " | " + part2);
        viewHolder.txt_invoice_no.setText(model.invoice_no);

        if (model.name == null) {
            model.name = "";
        }

        viewHolder.txt_cus_name.setText(model.name);
        viewHolder.txt_location.setText(model.business_location);

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }

        });
        if (model.payment_status.equalsIgnoreCase("Paid")) {
            viewHolder.txt_pay_paid.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_parse.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        } else if (model.payment_status.equalsIgnoreCase("Partial")) {
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_parse.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        } else if (model.payment_status.equalsIgnoreCase("Due")) {
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_parse.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.VISIBLE);

        }
        if (model.return_exists == 0) {
            viewHolder.img_return_exists.setVisibility(View.GONE);
        } else {

            viewHolder.img_return_exists.setVisibility(View.VISIBLE);
        }

        if(model.delivery_order_status.equalsIgnoreCase("picked_up")){
            viewHolder.txt_delivery_status.setText("Picked Up");
        }else{
            viewHolder.txt_delivery_status.setText(model.delivery_order_status);
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

    public void setOnItmeClicked(ShippingListAdapter.OnClicked onClicked) {
        this.onClicked = (ShippingListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
