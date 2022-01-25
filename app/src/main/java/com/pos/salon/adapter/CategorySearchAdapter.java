package com.pos.salon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.CategoryModel;

import java.util.ArrayList;

public class CategorySearchAdapter extends RecyclerView.Adapter<CategorySearchAdapter.MyViewHolder> {

    public Context context;
    private OnClicked onCLicked;
    private ArrayList<CategoryModel> list;

    public CategorySearchAdapter(Context context,ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cate_name;
        LinearLayout lay_cat;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cate_name=itemView.findViewById(R.id.txt_cate_name);
            lay_cat=itemView.findViewById(R.id.lay_cat);
        }
    }

    @NonNull
    @Override
    public CategorySearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cate_search_items, parent, false);
        return new CategorySearchAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final CategorySearchAdapter.MyViewHolder viewHolder, final int i) {

        CategoryModel model= list.get(i);
        viewHolder.txt_cate_name.setText(model.name);
        viewHolder.lay_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(onCLicked !=null){
                    onCLicked.setOnItem(list,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClicked(CategorySearchAdapter.OnClicked onClicked) {
        this.onCLicked = (CategorySearchAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnItem(ArrayList<CategoryModel> list,int position);
    }
    public void updateList(ArrayList<CategoryModel> list2){
        list = list2;
        notifyDataSetChanged();
    }

}
