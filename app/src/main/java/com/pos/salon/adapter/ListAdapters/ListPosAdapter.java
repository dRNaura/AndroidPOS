package com.pos.salon.adapter.ListAdapters;

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
import com.pos.salon.model.listModel.ListPosModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ListPosAdapter extends RecyclerView.Adapter<ListPosAdapter.MyViewHolder> {

    public Context context;
    OnClicked onClicked;
    ArrayList<ListPosModel> salesList = new ArrayList<>();

    public ListPosAdapter(Context context, ArrayList<ListPosModel> salesList) {
        this.context = context;
        this.salesList=salesList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_forward,img_return_exists;
        LinearLayout linear_layout_id;
        TextView txt_order_date,txt_invoice_no,txt_cus_name,txt_pay_parse,txt_pay_paid,txt_location,txt_pay_due;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_forward=itemView.findViewById(R.id.img_forward);
            txt_order_date=itemView.findViewById(R.id.order_date);
            txt_invoice_no=itemView.findViewById(R.id.txt_invoice_no);
            txt_cus_name=itemView.findViewById(R.id.txt_cus_name);
            txt_pay_parse=itemView.findViewById(R.id.txt_pay_pars);
            txt_pay_paid=itemView.findViewById(R.id.txt_pay_paid);
            txt_location=itemView.findViewById(R.id.txt_location);
            txt_pay_due=itemView.findViewById(R.id.txt_pay_due);
            img_return_exists=itemView.findViewById(R.id.img_return_exists);
            linear_layout_id=itemView.findViewById(R.id.linear_layout_id);

        }
    }
    @NonNull
    @Override
    public ListPosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pos_item, parent, false);
        return new ListPosAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ListPosAdapter.MyViewHolder viewHolder, final int i) {


        ListPosModel model = salesList.get(i);

        if(!model.order_Date.equalsIgnoreCase("")){
            String dateArray=model.order_Date;

//        Log.e("datetime",model.order_Date);
//     MM/dd/yyyy HH:mm
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = spf.parse(dateArray);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            spf = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
            String strDate1 = spf.format(newDate);


            String[]  separated = strDate1.split("\\ ");
            String part1 = separated[0];
            String part2 = separated[1];

            viewHolder.txt_order_date.setText(part1 + " | " + part2);
        }

        viewHolder.txt_invoice_no.setText(model.invoice_no);

        if(model.customer_name == null)
        {
            model.customer_name = "";
        }

        viewHolder.txt_cus_name.setText(model.customer_name);
        viewHolder.txt_location.setText(model.location);

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }

        });
        if (model.payemnt_method.equalsIgnoreCase("Paid")){
            viewHolder.txt_pay_paid.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_parse.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        }else if(model.payemnt_method.equalsIgnoreCase("Partial")){
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_parse.setVisibility(View.VISIBLE);
            viewHolder.txt_pay_due.setVisibility(View.GONE);

        }else if(model.payemnt_method.equalsIgnoreCase("Due")){
            viewHolder.txt_pay_paid.setVisibility(View.GONE);
            viewHolder.txt_pay_parse.setVisibility(View.GONE);
            viewHolder.txt_pay_due.setVisibility(View.VISIBLE);

        }
        if(model.return_exists==0){
            viewHolder.img_return_exists.setVisibility(View.GONE);
        }else{
            viewHolder.img_return_exists.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }
    public void setOnItmeClicked(ListPosAdapter.OnClicked onClicked) {
        this.onClicked = (ListPosAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
