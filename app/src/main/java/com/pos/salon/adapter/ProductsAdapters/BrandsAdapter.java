package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.CategoryModel;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    Context mContext;
    ArrayList<CategoryModel> list;
    BrandsAdapter.OnEditClicked onEDitClicked;
    BrandsAdapter.OnDeleteCLicked onDeleteCLicked;
    int userBusinessId = 0;
    private SharedPreferences sharedPreferences;

    public BrandsAdapter(Context context, ArrayList<CategoryModel> list) {
        this.mContext = context;
        this.list = list;
//        this.userBusinessId = userBusinessId;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_brands;
        ImageView img_brand_edit, img_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_brands = itemView.findViewById(R.id.txt_brands);
            img_brand_edit = itemView.findViewById(R.id.img_brand_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }

    @NonNull
    @Override
    public BrandsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brands_items, viewGroup, false);
        return new BrandsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandsAdapter.ViewHolder viewHolder, int i) {

        sharedPreferences = mContext.getSharedPreferences("login", MODE_PRIVATE);
        userBusinessId=sharedPreferences.getInt("business_id",0);

        if(userBusinessId==1){
            viewHolder.img_brand_edit.setVisibility(View.VISIBLE);
            viewHolder.img_delete.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img_brand_edit.setVisibility(View.GONE);
            viewHolder.img_delete.setVisibility(View.GONE);
        }
        CategoryModel model = list.get(i);
        viewHolder.txt_brands.setText(model.name);


        viewHolder.img_brand_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEDitClicked != null) {
                    onEDitClicked.setOnEditItem(list.get(i).id);
                }
            }
        });
        viewHolder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteCLicked != null) {
                    onDeleteCLicked.setOnDeleteItem(list.get(i).id);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnEditClicked(BrandsAdapter.OnEditClicked onEDitClicked) {
        this.onEDitClicked = (BrandsAdapter.OnEditClicked) onEDitClicked;
    }

    public interface OnEditClicked {
        void setOnEditItem(int cateID);
    }

    public void setOnDeleteClicked(BrandsAdapter.OnDeleteCLicked onDeleteCLicked) {
        this.onDeleteCLicked = (BrandsAdapter.OnDeleteCLicked) onDeleteCLicked;
    }

    public interface OnDeleteCLicked {
        void setOnDeleteItem(int id);
    }
    public void updateList(ArrayList<CategoryModel> list){
        this.list = list;

        notifyDataSetChanged();
    }
}
