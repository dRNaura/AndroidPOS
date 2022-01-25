package com.pos.salon.adapter.PurchaseSectionAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import org.json.JSONObject;
import java.util.ArrayList;

public class PurchaseProductDetailAdapter extends RecyclerView.Adapter<PurchaseProductDetailAdapter.MyViewHolder> {

    Context context;
    ArrayList list=new ArrayList<>();
    public JSONObject purchaseItem;

    public PurchaseProductDetailAdapter(Context context,  ArrayList list) {
        this.context = context;
        this.list = list;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txt_subTotal,txtProductName, txt_unitCostBeforeDiscount, txt_DiscountPercentage, txt_tax, txt_unitCostBeforeTax, txt_SubtotalBeforeTax,txt_UnitSellingPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_ProductName);
            txt_unitCostBeforeDiscount = itemView.findViewById(R.id.txt_unitCostBeforeDiscount);
            txt_DiscountPercentage = itemView.findViewById(R.id.txt_DiscountPercentage);
            txt_tax = itemView.findViewById(R.id.txt_tax);
            txt_unitCostBeforeTax = itemView.findViewById(R.id.txt_unitCostBeforeTax);
            txt_UnitSellingPrice = itemView.findViewById(R.id.txt_UnitSellingPrice);
            txt_subTotal = itemView.findViewById(R.id.txt_subTotal);

        }
    }

    @NonNull
    @Override
    public PurchaseProductDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_product_items, parent, false);
        return new PurchaseProductDetailAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final PurchaseProductDetailAdapter.MyViewHolder viewHolder, final int i) {

        purchaseItem=(JSONObject) list.get(i);
        try {
            String strProductName = ""; // combine all products name , modifiers name etc.
            JSONObject sellProduct = null;
            if (purchaseItem.has("product") && !purchaseItem.isNull("product")) {
                sellProduct = (JSONObject) purchaseItem.getJSONObject("product");
                String strName = "";
                if (sellProduct.has("name") && !sellProduct.isNull("name")) {
                    strName = sellProduct.getString("name");
                }
                strProductName = strProductName + sellProduct.getInt("id") + " " + strName;


//                JSONObject objvariations = sellProduct.getJSONObject("variations");
//                JSONObject product_variation = objvariations.getJSONObject("product_variation");

                if (purchaseItem.has("pp_without_discount") && !purchaseItem.isNull("pp_without_discount")) {
                    viewHolder.txt_unitCostBeforeDiscount.setText(purchaseItem.getString("pp_without_discount"));
                }
                if (purchaseItem.has("pp_without_discount") && !purchaseItem.isNull("pp_without_discount")) {
                    viewHolder.txt_unitCostBeforeTax.setText(purchaseItem.getString("pp_without_discount"));
                }
                if (purchaseItem.has("discount_percent") && !purchaseItem.isNull("discount_percent")) {
                    viewHolder.txt_DiscountPercentage.setText(purchaseItem.getString("discount_percent"));
                }
                if (purchaseItem.has("item_tax") && !purchaseItem.isNull("item_tax")) {
                    viewHolder.txt_tax.setText(purchaseItem.getString("item_tax"));
                }
            }
            JSONObject variationProduct = null;
            String variation="";
            if(purchaseItem.has("variations") && !purchaseItem.isNull("variations")){
                variationProduct = (JSONObject) purchaseItem.getJSONObject("variations");
                if(variationProduct.has("product_variation") && !variationProduct.isNull("product_variation")){
                   JSONObject product_variation=(JSONObject) variationProduct.getJSONObject("product_variation");
                    variation=product_variation.getString("name") +" - ";
                }
                variation=variation+variationProduct.getString("name");
                strProductName=strProductName+" - "+variation;

                if (variationProduct.has("default_sell_price") && !variationProduct.isNull("default_sell_price")) {
                    viewHolder.txt_UnitSellingPrice.setText(variationProduct.getString("default_sell_price"));
                }


            }
            strProductName = strProductName + " X " + purchaseItem.getInt("quantity") + " (Quantity)";

            viewHolder.txtProductName.setText(strProductName);

            double subtotal=0.0;
              double price_inc_tax=0.0;
              int quantity=0;
            if (purchaseItem.has("purchase_price_inc_tax") && !purchaseItem.isNull("purchase_price_inc_tax")) {
                price_inc_tax= Double.parseDouble(purchaseItem.getString("purchase_price_inc_tax"));
            }
            if (purchaseItem.has("quantity") && !purchaseItem.isNull("quantity")) {
                quantity=purchaseItem.getInt("quantity");
            }
            subtotal=price_inc_tax * quantity ;

            viewHolder.txt_subTotal.setText("$"+String.format("%.2f",subtotal));


        }catch (Exception e){
            Log.e("Exception",e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

