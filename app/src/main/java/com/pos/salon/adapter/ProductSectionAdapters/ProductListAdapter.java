package com.pos.salon.adapter.ProductSectionAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.ProductModel.ProductListModel;
import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    public Context context;
    ProductListAdapter.OnClicked onClicked;
    ArrayList<ProductListModel> list = new ArrayList<>();


    public ProductListAdapter(Context context, ArrayList<ProductListModel> list) {

        this.context = context;
        this.list = list;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_product_name,txt_product_type,txt_current_stock,txt_product_brand,txt_product_category,txt_selling_price,txt_product_sub_category;
       LinearLayout lay_product_items;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_product_name=itemView.findViewById(R.id.txt_product_name);
            txt_product_type=itemView.findViewById(R.id.txt_product_type);
            txt_current_stock=itemView.findViewById(R.id.txt_current_stock);
            txt_product_brand=itemView.findViewById(R.id.txt_product_brand);
            txt_product_category=itemView.findViewById(R.id.txt_product_category);
            txt_selling_price=itemView.findViewById(R.id.txt_selling_price);
            txt_product_sub_category=itemView.findViewById(R.id.txt_product_sub_category);
            lay_product_items=itemView.findViewById(R.id.lay_product_items);

        }

    }

    @NonNull
    @Override
    public ProductListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_items, parent, false);
        return new ProductListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ProductListAdapter.MyViewHolder viewHolder, final int i) {

        ProductListModel model=list.get(i);
        viewHolder.txt_product_name.setText(model.product +" / "+model.sku);
        viewHolder.txt_product_type.setText(model.type);
        if(model.current_stock.equalsIgnoreCase("")){
            viewHolder.txt_current_stock.setText("0 Pieces");
        }else{
            String stock=model.current_stock;
            String[] separated = stock.split("\\.");
            viewHolder.txt_current_stock.setText(separated[0] +" "+"Pieces");
        }
        viewHolder.txt_product_brand.setText(model.brand);
        viewHolder.txt_product_category.setText(model.category);
        viewHolder.txt_selling_price.setText("$"+model.min_price);
        viewHolder.txt_product_sub_category.setText(model.sub_category);

        viewHolder.lay_product_items.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItmeClicked(ProductListAdapter.OnClicked onClicked) {
        this.onClicked = (ProductListAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position);
    }
}
