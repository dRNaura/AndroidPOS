package com.pos.salon.adapter.BookingAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.searchData.SearchItem;

import java.util.ArrayList;

public class BookingEditAdapter extends RecyclerView.Adapter<BookingEditAdapter.MyViewHolder> {

    private final ArrayList<SearchItem> itemArray;
    public Context context;
    BookingEditAdapter.ClickDeleteItem clickItem;
    private final Context mContext;


    public BookingEditAdapter(Context context, ArrayList<SearchItem> list, BookingEditAdapter.ClickDeleteItem clickItem) {
        mContext = context;
        this.itemArray = list;
        this.clickItem = clickItem;
    }

    public interface ClickDeleteItem {
        void onItemClick(SearchItem searchItem , int position,View v);

        void onClick(int value, SearchItem data, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button addButton, minusButton;
        TextView editQuantity, txtProductSubtoralPrice, txt_unitPrice, txtProductName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            addButton = itemView.findViewById(R.id.addButton);
            minusButton = itemView.findViewById(R.id.minusButton);
            editQuantity = itemView.findViewById(R.id.editQuantity);
            txtProductSubtoralPrice = itemView.findViewById(R.id.txtProductSubtoralPrice);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txt_unitPrice = itemView.findViewById(R.id.txt_unitPrice);
            txtProductName = itemView.findViewById(R.id.txtProductName);
        }
    }

    @NonNull
    @Override
    public BookingEditAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_edit_items, parent, false);
        return new BookingEditAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookingEditAdapter.MyViewHolder viewHolder, final int i) {

        SearchItem data = itemArray.get(i);

        viewHolder.txtProductName.setText("" + data.getName());
        viewHolder.editQuantity.setText("" + data.getQuantity());
        viewHolder.txt_unitPrice.setText("" + Double.valueOf(data.getUnitFinalPrice()));
        viewHolder.txtProductSubtoralPrice.setText("" + Double.valueOf(data.getFinalPrice()));

        viewHolder.addButton.setTag(data);
        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SearchItem data = (SearchItem) view.getTag();
                int adapterPosition = viewHolder.getAdapterPosition();
                itemArray.get(adapterPosition).setQuantity(itemArray.get(adapterPosition).getQuantity() + 1);
                SearchItem getItem = itemArray.get(adapterPosition);
                clickItem.onClick(getItem.getQuantity(), data, i);
                Double add = (Double.valueOf(data.getUnitFinalPrice())) * data.getQuantity();
                viewHolder.txtProductSubtoralPrice.setText(String.format("%.2f", add));
                viewHolder.editQuantity.setText(getItem.getQuantity() + "");

            }

        });

        viewHolder.minusButton.setTag(data);
        viewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchItem data = (SearchItem) v.getTag();

                int getCurrentCount = data.getQuantity();


                if (getCurrentCount > 1) {
                    int getPosition = viewHolder.getAdapterPosition();

                    itemArray.get(getPosition).setQuantity(getCurrentCount - 1);

                    SearchItem getItem = itemArray.get(getPosition);
                    clickItem.onClick(getItem.getQuantity(), data, i);
                    //data.setQuantity(String.valueOf(currentNos));
                    viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                    Double add = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();
                    viewHolder.txtProductSubtoralPrice.setText(String.format("%.2f", add));
                } else {
//                        Toast.makeText(mContext, "Enter valid item", Toast.LENGTH_LONG).show();
                }
            }
        });
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(ActivityPosItemList.comingFrom.equalsIgnoreCase("fromSaleDetail")){
//
//                }else{

                SearchItem data = (SearchItem) v.getTag();
                clickItem.onItemClick(data, i,v);

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemArray.size();
    }

}

