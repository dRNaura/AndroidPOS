package com.pos.salon.adapter.BookingAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.model.BookingModels.BookingDetailModel;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.searchData.SearchItem;

import java.util.ArrayList;

public class BookigProductServiceAdapter extends RecyclerView.Adapter<BookigProductServiceAdapter.MyViewHolder> {

    public Context context;
    ArrayList<SearchItem> list = new ArrayList<>();

    public BookigProductServiceAdapter(Context context, ArrayList<SearchItem> list) {
        this.context = context;
        this.list = list;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_no,txt_productName,txt_UnitPrice,txt_quantity,txtFInal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_no=itemView.findViewById(R.id.txt_no);
            txt_productName=itemView.findViewById(R.id.txt_productName);
            txt_UnitPrice=itemView.findViewById(R.id.txt_UnitPrice);
            txt_quantity=itemView.findViewById(R.id.txt_quantity);
            txtFInal=itemView.findViewById(R.id.txtFInal);
        }
    }


    @NonNull
    @Override
    public BookigProductServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_service_items, parent, false);
        return new BookigProductServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookigProductServiceAdapter.MyViewHolder viewHolder, final int i) {

        SearchItem model =list.get(i);
        viewHolder.txt_no.setText(String.valueOf(i+1)+".");
        viewHolder.txt_UnitPrice.setText(""+Double.valueOf(model.getUnitFinalPrice()));
        viewHolder.txt_quantity.setText(""+Double.valueOf(model.getQuantity()));
        viewHolder.txt_productName.setText(""+model.getName());
        viewHolder.txtFInal.setText(""+Double.valueOf(model.getFinalPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}


