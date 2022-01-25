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
import com.pos.salon.model.BookingModels.BookingListModel;
import com.pos.salon.model.NotificationModel;
import java.util.ArrayList;

public class EventCalenderAdapter extends RecyclerView.Adapter<EventCalenderAdapter.MyViewHolder>{

    public Context context;
    EventCalenderAdapter.OnClicked onClicked;
    ArrayList<BookingListModel> list = new ArrayList<>();

    public EventCalenderAdapter(Context context, ArrayList<BookingListModel> list){
        this.context = context;
        this.list = list;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lay_linear ;
        TextView txtUser_event,txt_time_event;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lay_linear=itemView.findViewById(R.id.lay_linear);
            txtUser_event=itemView.findViewById(R.id.txtUser_event);
            txt_time_event=itemView.findViewById(R.id.txt_time_event);
        }
    }

    @NonNull
    @Override
    public EventCalenderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_time_user_items, parent, false);
        return new EventCalenderAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final EventCalenderAdapter.MyViewHolder viewHolder, final int i) {
        BookingListModel model =list.get(i);
        viewHolder.txt_time_event.setText(model.time);
        viewHolder.txtUser_event.setText(model.name);

        viewHolder.lay_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClicked != null)
                    onClicked.setOnClickedItem(i);
            }
        });
        if(model.status.equalsIgnoreCase("canceled")){
            viewHolder.lay_linear.setBackgroundColor(Color.parseColor("#dc3545"));
        }
        else if(model.status.equalsIgnoreCase("approved")){
            viewHolder.lay_linear.setBackgroundColor(Color.parseColor("#00c0ef"));
        }
        else if(model.status.equalsIgnoreCase("in progress")){
            viewHolder.lay_linear.setBackgroundColor(Color.parseColor("#337ab7"));
        }
        else if(model.status.equalsIgnoreCase("pending")){
            viewHolder.lay_linear.setBackgroundColor(Color.parseColor("#ffc107"));
        }
        else if(model.status.equalsIgnoreCase("completed")){
            viewHolder.lay_linear.setBackgroundColor(Color.parseColor("#dff0d8"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(EventCalenderAdapter.OnClicked onClicked) {
        this.onClicked = (EventCalenderAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
