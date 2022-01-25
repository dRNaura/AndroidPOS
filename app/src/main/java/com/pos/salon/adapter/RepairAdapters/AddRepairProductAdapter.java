package com.pos.salon.adapter.RepairAdapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.model.searchData.SearchItem;

import java.util.ArrayList;

public class AddRepairProductAdapter extends RecyclerView.Adapter<AddRepairProductAdapter.MyViewHolder> {

    public Context context;
    OnClicked onClicked;

    ArrayList<SearchItem> addrepairDetailList = new ArrayList<>();


    public AddRepairProductAdapter(Context context, ArrayList<SearchItem> addrepairDetailList) {

        this.context = context;
        this.addrepairDetailList = addrepairDetailList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_repair_proName, txt_repair_proSellingPrice;
        ImageView imgRepairProductDelete;
        EditText et_repair_proQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_repair_proName = itemView.findViewById(R.id.txt_repair_proName);
            et_repair_proQuantity = itemView.findViewById(R.id.et_repair_proQuantity);
            txt_repair_proSellingPrice = itemView.findViewById(R.id.txt_repair_proSellingPrice);
            imgRepairProductDelete = itemView.findViewById(R.id.imgRepairProductDelete);

        }

    }

    @NonNull
    @Override
    public AddRepairProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_repair_part_items, parent, false);
        return new AddRepairProductAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final AddRepairProductAdapter.MyViewHolder viewHolder, final int i) {

      final SearchItem model = addrepairDetailList.get(i);
        viewHolder.txt_repair_proName.setText(model.getName());
        viewHolder.txt_repair_proSellingPrice.setText(model.getSelling_price());
        viewHolder.et_repair_proQuantity.setText(String.valueOf(model.getQuantity()));
        viewHolder.imgRepairProductDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClicked != null) {
                    onClicked.setOnClickedItem(i, model);

                }
            }
        });


        viewHolder.et_repair_proQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                String repairQuantity = s.toString();

                if(repairQuantity.isEmpty())
                {
                    repairQuantity="0";

//                    viewHolder.txt_repair_proSellingPrice.setText(model.getSelling_price());
                    model.setQuantity(1);
//                    model.setSelling_price(model.getSelling_price());
//                    viewHolder.et_repair_proQuantity.setText("1");
//                    addrepairDetailList.set(i,model);
                    if (onClicked != null){
                        onClicked.setOnEditQuantity(i);

                    }
                    return;
                }

                String strQuant = model.getQty_available();

                int quantityAvail;

                if(strQuant.contains("."))
                {
                    String[] separated = strQuant.split("\\.");
                    String part1 = separated[0];
                    String part2 = separated[1];
                   quantityAvail=Integer.parseInt(part1);
                }
                else{

                  quantityAvail=Integer.parseInt(model.getQty_available());
                }
                int repairQunt=Integer.parseInt(repairQuantity);

                if(repairQunt<=quantityAvail) {

                    model.setQuantity(repairQunt);

                }
                else{
//                    viewHolder.txt_repair_proQuantity.setText("1");
                    viewHolder.et_repair_proQuantity.setText("");
                    viewHolder.et_repair_proQuantity.setError("Invalid Quantity");
                    model.setQuantity(1);

                }
                if (onClicked != null){
                    onClicked.setOnEditQuantity(i);

                }

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return addrepairDetailList.size();
    }

    public void setOnItmeClicked(AddRepairProductAdapter.OnClicked onClicked) {
        this.onClicked = (AddRepairProductAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {

        void setOnClickedItem(int position,SearchItem item);

        void setOnEditQuantity(int position);

    }



}

