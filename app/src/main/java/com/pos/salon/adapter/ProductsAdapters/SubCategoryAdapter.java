package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.CategoryModel;
import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    Context mContext;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    public boolean isOpen=false;
    ArrayList<CategoryModel> sublist;
    ArrayList<CategoryModel> subsublist;
    SubCategoryAdapter.OnEditClicked onEDitClicked;
    SubCategoryAdapter.OnDeleteCLicked onDeleteCLicked;
    private int userBusinessId=0;

    public SubCategoryAdapter(Context context, ArrayList<CategoryModel> sublist,ArrayList<CategoryModel> subsublist,int userBusinessId) {
        this.mContext = context;
        this.sublist = sublist;
        this.subsublist = subsublist;
        this.userBusinessId = userBusinessId;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recycler_sub_cat;
        TextView txt_cate;
        LinearLayout tab_expandable;
        ImageView imgOpenCLose,img_subCat_edit,img_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler_sub_cat=itemView.findViewById(R.id.recycler_sub_cat);
            txt_cate=itemView.findViewById(R.id.txt_cate);
            tab_expandable=itemView.findViewById(R.id.tab_expandable);
            imgOpenCLose=itemView.findViewById(R.id.imgOpenCLose);
            img_subCat_edit=itemView.findViewById(R.id.img_subCat_edit);
            img_delete=itemView.findViewById(R.id.img_delete);
        }
    }
    @NonNull
    @Override
    public SubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sub_category_items, viewGroup, false);
        return new SubCategoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.ViewHolder viewHolder, int i) {

        if(userBusinessId==1){
            viewHolder.img_subCat_edit.setVisibility(View.VISIBLE);
            viewHolder.img_delete.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img_subCat_edit.setVisibility(View.GONE);
            viewHolder.img_delete.setVisibility(View.GONE);
        }
        CategoryModel model=sublist.get(i);
        CategoryModel subSUbCat=new CategoryModel();

        viewHolder.txt_cate.setText(model.sub_name);


        viewHolder.imgOpenCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.tab_expandable.getVisibility() == View.VISIBLE) {
                    isOpen=true;
                    viewHolder.tab_expandable.setVisibility(View.GONE);
                    viewHolder.imgOpenCLose.setImageResource(R.mipmap.add_black);
                }
                else {
                    isOpen=false;
                    viewHolder.tab_expandable.setVisibility(View.VISIBLE);
                    viewHolder.imgOpenCLose.setImageResource(R.mipmap.minus_black);

                }
            }
        });

        viewHolder.img_subCat_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onEDitClicked != null){
                    onEDitClicked.setOnEditItem(sublist.get(i).sub_id);
                }
            }
        });

        viewHolder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteCLicked != null){
                    onDeleteCLicked.setOnDeleteItem(sublist.get(i).sub_id);
                }
            }
        });

        ArrayList<CategoryModel> arrSubSubCat = new ArrayList<>();
        for (int a = 0; a < subsublist.size(); a++) {
            subSUbCat = subsublist.get(a);
            if (model.sub_id == subSUbCat.parent_id) {
                CategoryModel submodel=new CategoryModel();
                submodel.sub_sub_id=subSUbCat.sub_sub_id;
                submodel.sub_sub_name=subSUbCat.sub_sub_name;
                arrSubSubCat.add(submodel);
            }
        }

        if(arrSubSubCat.size()==0){
            viewHolder.imgOpenCLose.setVisibility(View.GONE);
        }else{
            viewHolder.imgOpenCLose.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(viewHolder.recycler_sub_cat.getContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(arrSubSubCat.size());

        SubSubCatAdapter subsubAdapter = new SubSubCatAdapter(viewHolder.recycler_sub_cat.getContext(),arrSubSubCat);
        viewHolder.recycler_sub_cat.setLayoutManager(layoutManager);
        viewHolder.recycler_sub_cat.setAdapter(subsubAdapter);
        viewHolder.recycler_sub_cat.setRecycledViewPool(viewPool);

        subsubAdapter.setOnEditClicked(new SubSubCatAdapter.OnEditClicked() {
            @Override
            public void setOnEditItem(int subCateID) {
                if(onEDitClicked != null){
                    onEDitClicked.setOnEditItem(subCateID);
                }
            }
        });
        subsubAdapter.setOnDeleteClicked(new SubSubCatAdapter.OnDeleteCLicked() {
            @Override
            public void setOnDeleteItem(int id) {
                if(onEDitClicked != null){
                    onEDitClicked.setOnEditItem(id);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return sublist.size();
    }
    public void setOnEditClicked(SubCategoryAdapter.OnEditClicked onEDitClicked) {
        this.onEDitClicked = (SubCategoryAdapter.OnEditClicked) onEDitClicked;
    }

    public interface OnEditClicked {
        void setOnEditItem(int subCateID);
    }
    public void setOnDeleteClicked(SubCategoryAdapter.OnDeleteCLicked onDeleteCLicked) {
        this.onDeleteCLicked = (SubCategoryAdapter.OnDeleteCLicked) onDeleteCLicked;
    }

    public interface OnDeleteCLicked {
        void setOnDeleteItem(int id);
    }

}