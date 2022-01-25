package com.pos.salon.adapter.BookingAdapter;

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
import com.pos.salon.model.BookingModels.BookingListModel;
import com.pos.salon.model.ContentModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.MyViewHolder> {

    public Context context;
    BookingListAdapter.OnClicked onClicked;
    ArrayList<BookingListModel> list = new ArrayList<>();

    public BookingListAdapter(Context context, ArrayList<BookingListModel> list) {
        this.context = context;
        this.list = list;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout lay_forwrd,lay_dateTime,linear_layout_id;
        TextView txt_status,txt_email,txt_mobile,txt_date,txt_time,txt_bookingid,txt_username,txt_service;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            lay_forwrd=itemView.findViewById(R.id.lay_forwrd);
            txt_status=itemView.findViewById(R.id.txt_status);
            txt_email=itemView.findViewById(R.id.txt_email);
            txt_mobile=itemView.findViewById(R.id.txt_mobile);
            txt_date=itemView.findViewById(R.id.txt_date);
            txt_time=itemView.findViewById(R.id.txt_time);
            txt_bookingid=itemView.findViewById(R.id.txt_bookingid);
            txt_username=itemView.findViewById(R.id.txt_username);
            lay_dateTime=itemView.findViewById(R.id.lay_dateTime);
            linear_layout_id=itemView.findViewById(R.id.linear_layout_id);
            txt_service=itemView.findViewById(R.id.txt_service);


        }
    }

    @NonNull
    @Override
    public BookingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_items, parent, false);
        return new BookingListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final BookingListAdapter.MyViewHolder viewHolder, final int i) {

        BookingListModel model=list.get(i);
        viewHolder.txt_status.setText(model.status);
        viewHolder.txt_bookingid.setText(String.valueOf(model.id));
        viewHolder.txt_username.setText(model.name);
        viewHolder.txt_email.setText(model.email);
        viewHolder.txt_mobile.setText(model.mobile);
        viewHolder.txt_service.setText(model.service_name);

        String part1 = "";
        String part2="";
        if(model.date_time !=null &&  !model.date_time.equalsIgnoreCase("")){
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = null;
            try {
                newDate = spf.parse(model.date_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            spf = new SimpleDateFormat("dd MMM");
            part1= spf.format(newDate);
            spf = new SimpleDateFormat("hh:mm a");
            part2= spf.format(newDate);

        }
        viewHolder.txt_date.setText(part1);
        viewHolder.txt_time.setText(part2);

        if(model.status.equalsIgnoreCase("canceled")){
            viewHolder.lay_dateTime.setBackgroundColor(Color.parseColor("#dc3545"));
        }
        else if(model.status.equalsIgnoreCase("approved")){
            viewHolder.lay_dateTime.setBackgroundColor(Color.parseColor("#00c0ef"));
        }
        else if(model.status.equalsIgnoreCase("in progress")){
            viewHolder.lay_dateTime.setBackgroundColor(Color.parseColor("#337ab7"));
        }
        else if(model.status.equalsIgnoreCase("pending")){
            viewHolder.lay_dateTime.setBackgroundColor(Color.parseColor("#ffc107"));
        }
        else if(model.status.equalsIgnoreCase("completed")){
            viewHolder.lay_dateTime.setBackgroundColor(Color.parseColor("#dff0d8"));
        }

        viewHolder.linear_layout_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onClicked !=null){
                    onClicked.setOnClickedItem(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(BookingListAdapter.OnClicked onClicked) {
        this.onClicked = (BookingListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}

