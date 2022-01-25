package com.pos.salon.adapter.ProductSectionAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.ProductModel.ProductDetailModel;
import java.util.ArrayList;

public class ProductDetaiLocationVarAdapter extends RecyclerView.Adapter<ProductDetaiLocationVarAdapter.MyViewHolder> {

    public Context context;
    ArrayList<ProductDetailModel>list=new ArrayList();
    public ProductDetaiLocationVarAdapter(Context context,ArrayList<ProductDetailModel>list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_dpp_exc_tax,txt_dpp_inc_tax,txt_x_margin,txt_dsp_exc_tax,txt_dsp_inc_tax,txt_location_name,txt_product_variation;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_dpp_exc_tax=itemView.findViewById(R.id.txt_dpp_exc_tax);
            txt_dpp_inc_tax=itemView.findViewById(R.id.txt_dpp_inc_tax);
            txt_x_margin=itemView.findViewById(R.id.txt_x_margin);
            txt_dsp_exc_tax=itemView.findViewById(R.id.txt_dsp_exc_tax);
            txt_dsp_inc_tax=itemView.findViewById(R.id.txt_dsp_inc_tax);
            txt_location_name=itemView.findViewById(R.id.txt_location_name);
            txt_product_variation=itemView.findViewById(R.id.txt_product_variation);

        }

    }

    @NonNull
    @Override
    public ProductDetaiLocationVarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_variation_items, parent, false);
        return new ProductDetaiLocationVarAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ProductDetaiLocationVarAdapter.MyViewHolder viewHolder, final int i) {

        ProductDetailModel model =list.get(i);
        viewHolder.txt_dpp_exc_tax.setText("$"+model.default_purchase_price);
        viewHolder.txt_dpp_inc_tax.setText("$"+model.dpp_inc_tax);
        viewHolder.txt_x_margin.setText("$"+model.profit_percent);
        viewHolder.txt_dsp_exc_tax.setText("$"+model.default_sell_price);
        viewHolder.txt_dsp_inc_tax.setText("$"+model.sell_price_inc_tax);
        viewHolder.txt_location_name.setText(model.location_name);

        String variation_name="";
        if(!model.product_variation_name.equalsIgnoreCase("") && !model.product_variation_name.equalsIgnoreCase("DUMMY") ){
            variation_name=" ("+model.product_variation_name+": "+"";
        }
        if(!model.variation_name.equalsIgnoreCase("") && !model.variation_name.equalsIgnoreCase("DUMMY")){
            variation_name=variation_name+model.variation_name+" , "+"";
        }
        if(!model.color_name.equalsIgnoreCase("")){
            variation_name=variation_name+"Color: "+model.color_name+" ) "+"";
        }
        viewHolder.txt_product_variation.setText(variation_name);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}