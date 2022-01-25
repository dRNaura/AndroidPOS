package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.customerData.PartialCustomerList;

import java.util.ArrayList;

public class PartialProductsAdapter extends RecyclerView.Adapter<PartialProductsAdapter.ViewHolder> {
    Context context;
    ArrayList<PartialCustomerList> partialList;
    String strCurrency = "";

    public PartialProductsAdapter(Context context, ArrayList<PartialCustomerList> partialList, String strCurrency) {
        this.context = context;
        this.partialList = partialList;
        this.strCurrency = strCurrency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_partialproducts, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        float quantityF = Float.parseFloat(partialList.get(i).getQuantity_ordered());
        int quantityInt = (int) quantityF;

        viewHolder.txtProductPrice1.setText("$"+" "+partialList.get(i).getUnit_price_before_discount());
        viewHolder.txtProductName1.setText(partialList.get(i).getProduct_name());
        viewHolder.txtProductSubtoralPrice1.setText("$"+partialList.get(i).getSell_price_inc_tax());

//        if(partialList.get(i).getSell_details().get(i).getLine_discount_amount().equals("null")){
//            viewHolder.edtDiscountAmt1.setText("Discount Amount: ");
//        }
//        else {
            viewHolder.edtDiscountAmt1.setText("Discount Amount: "+partialList.get(i).getLine_discount_amount());

     //   }
        viewHolder.spnDiscountType1.setText("Discount Type: "+partialList.get(i).getLine_discount_type());
        viewHolder.editQuantity1.setText(String.valueOf(quantityInt));
        /*viewHolder.tv_productId.setText(partialList.get(i).getSell_details().get(i).getProduct_id());
        viewHolder.tv_quant.setText(quantityInt + " * ");
        viewHolder.tv_unit.setText(partialList.get(i).getSell_details().get(i).getUnit_price_before_discount());
        viewHolder.tv_subtotal.setText(partialList.get(i).getTransaction().getFinal_total());*/
        //Toast.makeText(context, "" + partialList.get(i).getTransaction_details().get(i).getCurrency() + " " + partialList.get(i).getTransaction_details().get(i).getFinal_total(), Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return partialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductPrice1, txtProductName1, txtProductSubtoralPrice1, tv_quant, tv_unit;
TextView editQuantity1,edtDiscountAmt1,spnDiscountType1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductPrice1 = itemView.findViewById(R.id.txtProductPrice1);
            txtProductName1 = itemView.findViewById(R.id.txtProductName1);
            txtProductSubtoralPrice1 = itemView.findViewById(R.id.txtProductSubtoralPrice1);
            editQuantity1 = itemView.findViewById(R.id.editQuantity1);
            edtDiscountAmt1 = itemView.findViewById(R.id.edtDiscountAmt1);
            spnDiscountType1 = itemView.findViewById(R.id.spnDiscountType1);
           /* tv_productId = itemView.findViewById(R.id.tv_productId);
            tv_subtotal = itemView.findViewById(R.id.tv_subtotal);
            tv_quant = itemView.findViewById(R.id.tv_quant);
            tv_unit = itemView.findViewById(R.id.tv_unit);*/
        }
    }
}
