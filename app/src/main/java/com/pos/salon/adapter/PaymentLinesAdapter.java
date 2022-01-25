package com.pos.salon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentLinesAdapter extends RecyclerView.Adapter<PaymentLinesAdapter.MyViewHolder> {

    Context context;
    ArrayList paymentLinesList=new ArrayList();

    public PaymentLinesAdapter(Context context,ArrayList  paymentLinesList) {
        this.context = context;
        this.paymentLinesList=paymentLinesList;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_paymentLineDate,txt_PaymentLineReferenceNo,txt_PaymentAmount,txt_PaymentMode,txt_PaymnetNote;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_paymentLineDate=itemView.findViewById(R.id.txt_paymentLineDate);
            txt_PaymentLineReferenceNo=itemView.findViewById(R.id.txt_PaymentLineReferenceNo);
            txt_PaymentAmount=itemView.findViewById(R.id.txt_paymentLineAmount);
            txt_PaymentMode=itemView.findViewById(R.id.txt_payment_mode);
            txt_PaymnetNote=itemView.findViewById(R.id.txt_payment_note);
        }
    }

    @NonNull
    @Override
    public PaymentLinesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_lines_items, parent, false);
        return new PaymentLinesAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final PaymentLinesAdapter.MyViewHolder viewHolder, final int i) {


        JSONObject paymentLines= (JSONObject) paymentLinesList.get(i);


        try {

            String date= "",part1="",part2="";

            if(paymentLines.has("paid_on") && !paymentLines.isNull("paid_on"))
            {
                date =  paymentLines.getString("paid_on");

                String orderDate = date;
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
                part1 = separated[0];
                part2 = separated[1];

            }

            viewHolder.txt_paymentLineDate.setText(part1);

            if(paymentLines.has("payment_ref_no") && !paymentLines.isNull("payment_ref_no"))
            {
                viewHolder.txt_PaymentLineReferenceNo.setText(paymentLines.getString("payment_ref_no"));
            }

            viewHolder.txt_PaymentAmount.setText(paymentLines.getString("amount"));
            viewHolder.txt_PaymentMode.setText(paymentLines.getString("method"));

            String note=paymentLines.getString("note");
            if(note!=null && !note.equalsIgnoreCase("null")) {
                viewHolder.txt_PaymnetNote.setText(paymentLines.getString("note"));
            }else{
                viewHolder.txt_PaymnetNote.setText("");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {

        return paymentLinesList.size();
    }
}

