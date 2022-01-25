package com.pos.salon.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>{

    public Context context;
    NotificationAdapter.OnClicked onClicked;
    ArrayList<NotificationModel> list = new ArrayList<>();
    String  user_id;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> list,String user_id){
        this.context=context;
        this.list=list;
        this.user_id=user_id;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_content_text,txt_view,txt_notification_customer,txt_notification_date;
        LinearLayout ly_notification;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_content_text=itemView.findViewById(R.id.txt_content_text);
            txt_notification_customer=itemView.findViewById(R.id.txt_notification_customer);
            txt_notification_date=itemView.findViewById(R.id.txt_notification_date);
            txt_view=itemView.findViewById(R.id.txt_view);
            ly_notification=itemView.findViewById(R.id.ly_notification);

        }

    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_items, parent, false);
        return new NotificationAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.MyViewHolder viewHolder, final int i) {

        NotificationModel model=list.get(i);
        viewHolder.txt_content_text.setText(model.comment_subject);
        if(!model.notification_customer.equalsIgnoreCase("")){
            viewHolder.txt_notification_customer.setVisibility(View.VISIBLE);
            viewHolder.txt_notification_customer.setText("Customer : "+model.notification_customer);
        }else{
            viewHolder.txt_notification_customer.setVisibility(View.GONE);
        }

        if(!model.notification_date.equalsIgnoreCase("")){
            viewHolder.txt_notification_date.setVisibility(View.VISIBLE);
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = spf.parse(model.notification_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            spf = new SimpleDateFormat("MMM dd,yyyy");
            String strDate1 = spf.format(newDate);
            viewHolder.txt_notification_date.setText("Date : "+strDate1);
        }else{
            viewHolder.txt_notification_date.setVisibility(View.GONE);
        }


        String all_vals = model.viewed_by;
        List<String> list = Arrays.asList(all_vals.split(","));
        if (list.contains(user_id)) {
            viewHolder.txt_view.setVisibility(View.VISIBLE);
            viewHolder.ly_notification.setBackgroundColor(Color.parseColor("#ffffff"));
        }else {
            viewHolder.txt_view.setVisibility(View.VISIBLE);
            viewHolder.ly_notification.setBackgroundColor(Color.parseColor("#dd4b39"));
        }

        viewHolder.txt_view.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItmeClicked(NotificationAdapter.OnClicked onClicked) {
        this.onClicked = (NotificationAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
