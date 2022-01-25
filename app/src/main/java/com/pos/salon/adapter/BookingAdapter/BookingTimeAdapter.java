package com.pos.salon.adapter.BookingAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.model.ContentModel;

import java.util.ArrayList;

public class BookingTimeAdapter extends RecyclerView.Adapter<BookingTimeAdapter.MyViewHolder> {

    public Context context;
    BookingTimeAdapter.OnClicked onClicked;
    ArrayList<ContentModel> list = new ArrayList<>();

    public BookingTimeAdapter(Context context, ArrayList<ContentModel> list) {
        this.context = context;
        this.list = list;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

        }

    }


    @NonNull
    @Override
    public BookingTimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_time_items, parent, false);
        return new BookingTimeAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final BookingTimeAdapter.MyViewHolder viewHolder, final int i) {


    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public void setOnItmeClicked(BookingTimeAdapter.OnClicked onClicked) {
        this.onClicked = (BookingTimeAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
