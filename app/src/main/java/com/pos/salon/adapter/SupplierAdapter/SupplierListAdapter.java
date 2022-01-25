package com.pos.salon.adapter.SupplierAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.listModel.SupplierListModel;
import java.util.ArrayList;

public class SupplierListAdapter extends RecyclerView.Adapter<SupplierListAdapter.MyViewHolder> {

    public Context context;
    SupplierListAdapter.OnClicked onClicked;
    ArrayList<SupplierListModel> list = new ArrayList<>();


    public SupplierListAdapter(Context context, ArrayList<SupplierListModel> list) {

        this.context = context;
        this.list = list;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_supplier_contactID,txt_supplier_BusinessName,txt_supplier_Name,txt_supplier_Contact,txt_supplier_Email,txt_supplier_PurchaseDue,txt_supplier_ReturnPurchase;
        LinearLayout lay_supplier_items;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_supplier_contactID=itemView.findViewById(R.id.txt_supplier_contactID);
            txt_supplier_BusinessName=itemView.findViewById(R.id.txt_supplier_BusinessName);
            txt_supplier_Name=itemView.findViewById(R.id.txt_supplier_Name);
            txt_supplier_Contact=itemView.findViewById(R.id.txt_supplier_Contact);
            txt_supplier_Email=itemView.findViewById(R.id.txt_supplier_Email);
            txt_supplier_PurchaseDue=itemView.findViewById(R.id.txt_supplier_PurchaseDue);
            txt_supplier_ReturnPurchase=itemView.findViewById(R.id.txt_supplier_ReturnPurchase);
            lay_supplier_items=itemView.findViewById(R.id.lay_supplier_items);
        }

    }

    @NonNull
    @Override
    public SupplierListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_list_items, parent, false);
        return new SupplierListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final SupplierListAdapter.MyViewHolder viewHolder, final int i) {

        SupplierListModel model=list.get(i);
        viewHolder.txt_supplier_contactID.setText(model.contact_id);
        viewHolder.txt_supplier_BusinessName.setText(model.supplier_business_name);
        viewHolder.txt_supplier_Name.setText(model.name);
        viewHolder.txt_supplier_Contact.setText(model.mobile);
        viewHolder.txt_supplier_Email.setText(model.email);
        Double totalDuePurchase = (Double.parseDouble(model.total_purchase )- Double.parseDouble(model.purchase_paid));
        viewHolder.txt_supplier_PurchaseDue.setText("$"+totalDuePurchase);
        Double totalReturnPurchase = (Double.parseDouble(model.total_purchase_return )- Double.parseDouble(model.purchase_return_paid));
        viewHolder.txt_supplier_ReturnPurchase.setText("$"+totalReturnPurchase);

        viewHolder.lay_supplier_items.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItmeClicked(SupplierListAdapter.OnClicked onClicked) {
        this.onClicked = (SupplierListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
