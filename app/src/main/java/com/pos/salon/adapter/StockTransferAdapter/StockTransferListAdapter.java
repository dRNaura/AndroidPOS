package com.pos.salon.adapter.StockTransferAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.UserManageModel;
import java.util.ArrayList;

public class StockTransferListAdapter extends RecyclerView.Adapter<StockTransferListAdapter.MyViewHolder> {

    public Context context;
    StockTransferListAdapter.OnClicked onClicked;
    ArrayList<UserManageModel> list = new ArrayList<>();
    public StockTransferListAdapter(Context context, ArrayList<UserManageModel> list) {

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
    public StockTransferListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_transfer_items, parent, false);
        return new StockTransferListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final StockTransferListAdapter.MyViewHolder viewHolder, final int i) {



//        viewHolder.lay_user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onClicked != null)
//                    onClicked.setOnClickedItem(i);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setOnItmeClicked(StockTransferListAdapter.OnClicked onClicked) {
        this.onClicked = (StockTransferListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }

    public void updateList(ArrayList<UserManageModel> list){
        this.list = list;

        notifyDataSetChanged();
    }
}

