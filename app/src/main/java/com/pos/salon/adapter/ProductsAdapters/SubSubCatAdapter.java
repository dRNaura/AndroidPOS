package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
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

public class SubSubCatAdapter extends RecyclerView.Adapter<SubSubCatAdapter.ViewHolder> {

    Context mContext;
    ArrayList<CategoryModel> list;
    SubSubCatAdapter.OnEditClicked onEDitClicked;
    SubSubCatAdapter.OnDeleteCLicked onDeleteCLicked;
    public SubSubCatAdapter(Context context,ArrayList<CategoryModel> list) {
        mContext = context;
        this.list = list;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cate;
        ImageView img_subSubEdit,img_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cate = itemView.findViewById(R.id.txt_cate);
            img_subSubEdit = itemView.findViewById(R.id.img_subSubEdit);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }

    @NonNull
    @Override
    public SubSubCatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_sub_category_items, viewGroup, false);
        return new SubSubCatAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubSubCatAdapter.ViewHolder viewHolder, int i) {
        CategoryModel model=list.get(i);
        viewHolder.txt_cate.setText(model.sub_sub_name);
        viewHolder.img_subSubEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onEDitClicked !=null){
                    onEDitClicked.setOnEditItem(list.get(i).sub_sub_id);
                }
            }
        });
        viewHolder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteCLicked !=null){
                    onDeleteCLicked.setOnDeleteItem(list.get(i).sub_sub_id);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setOnEditClicked(SubSubCatAdapter.OnEditClicked onEDitClicked) {
        this.onEDitClicked = (SubSubCatAdapter.OnEditClicked) onEDitClicked;
    }

    public interface OnEditClicked {
        void setOnEditItem(int subCateID);
    }

    public void setOnDeleteClicked(SubSubCatAdapter.OnDeleteCLicked onDeleteCLicked) {
        this.onDeleteCLicked = (SubSubCatAdapter.OnDeleteCLicked) onDeleteCLicked;
    }

    public interface OnDeleteCLicked {
        void setOnDeleteItem(int id);
    }
}

