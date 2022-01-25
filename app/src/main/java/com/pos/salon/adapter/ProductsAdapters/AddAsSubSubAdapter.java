package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.CategoryModel;
import java.util.ArrayList;

public class AddAsSubSubAdapter extends RecyclerView.Adapter<AddAsSubSubAdapter.ViewHolder> {

    Context mContext;
    ArrayList<CategoryModel> list;
    AddAsSubSubAdapter.OnClicked onClicked;

    public AddAsSubSubAdapter(Context context, ArrayList<CategoryModel> list) {
        mContext = context;
        this.list = list;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_subsubcate_name,txt_cate_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_subsubcate_name = itemView.findViewById(R.id.txt_subsubcate_name);
            txt_cate_name = itemView.findViewById(R.id.txt_cate_name);

        }
    }

    @NonNull
    @Override
    public AddAsSubSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_sub_cat_filter_items, viewGroup, false);
        return new AddAsSubSubAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAsSubSubAdapter.ViewHolder viewHolder, int i) {
        CategoryModel model = list.get(i);
        viewHolder.txt_subsubcate_name.setText(model.name);
        if(model.catName.equalsIgnoreCase("")){
            viewHolder.txt_cate_name.setVisibility(View.GONE);
        }else{
            viewHolder.txt_cate_name.setVisibility(View.VISIBLE);
            viewHolder.txt_cate_name.setText(model.catName);
        }


        viewHolder.txt_subsubcate_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClicked !=null){
                    onClicked.setOnItem(list,i);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClicked(AddAsSubSubAdapter.OnClicked onClicked) {
        this.onClicked = (AddAsSubSubAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnItem(ArrayList<CategoryModel> list,int position);
    }
    public void updateList(ArrayList<CategoryModel> list2){
        list = list2;
        notifyDataSetChanged();
    }


}
