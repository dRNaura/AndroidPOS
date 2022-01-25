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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context mContext;
    public boolean isOpen = false;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    ArrayList<CategoryModel> catlist;
    ArrayList<CategoryModel> subcatlist;
    ArrayList<CategoryModel> subsubcatlist;
    OnEditClicked onEDitClicked;
    OnDeleteCLicked onDeleteCLicked;
    int userBusinessId=0;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> catlist, ArrayList<CategoryModel> subCatlist, ArrayList<CategoryModel> subsublist,int userBusinessId) {
        this.mContext = context;
        this.catlist = catlist;
        this.subcatlist = subCatlist;
        this.subsubcatlist = subsublist;
        this.userBusinessId = userBusinessId;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recycler_sub_cat;
        TextView txt_cate;
        ImageView imgOpenCLose, img_category_edit, img_delete;
        LinearLayout tab_expandable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler_sub_cat = itemView.findViewById(R.id.recycler_sub_cat);
            txt_cate = itemView.findViewById(R.id.txt_cate);
            imgOpenCLose = itemView.findViewById(R.id.imgOpenCLose);
            tab_expandable = itemView.findViewById(R.id.tab_expandable);
            img_category_edit = itemView.findViewById(R.id.img_category_edit);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_items, viewGroup, false);
        return new CategoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, int i) {

        if(userBusinessId==1){
            viewHolder.img_category_edit.setVisibility(View.VISIBLE);
            viewHolder.img_delete.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img_category_edit.setVisibility(View.GONE);
            viewHolder.img_delete.setVisibility(View.GONE);
        }
        CategoryModel model = catlist.get(i);
        CategoryModel model1 = new CategoryModel();
        viewHolder.txt_cate.setText(model.name);

        ArrayList<CategoryModel> arrSubCat = new ArrayList<>();
        for (int a = 0; a < subcatlist.size(); a++) {
            model1 = subcatlist.get(a);
            if (model.id == model1.parent_id) {
                CategoryModel submodel = new CategoryModel();
                submodel.sub_id = model1.sub_id;
                submodel.sub_name = model1.sub_name;
                arrSubCat.add(submodel);
            }
        }


        if (arrSubCat == null) {
            arrSubCat = new ArrayList<>();
        }
        if (arrSubCat.size() == 0) {
            viewHolder.imgOpenCLose.setVisibility(View.GONE);
        } else {
            viewHolder.imgOpenCLose.setVisibility(View.VISIBLE);
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(viewHolder.recycler_sub_cat.getContext(),
                LinearLayoutManager.VERTICAL, false);

        layoutManager.setInitialPrefetchItemCount(arrSubCat.size());


        SubCategoryAdapter subItemAdapter = new SubCategoryAdapter(viewHolder.recycler_sub_cat.getContext(), arrSubCat, subsubcatlist,userBusinessId);
        viewHolder.recycler_sub_cat.setLayoutManager(layoutManager);
        viewHolder.recycler_sub_cat.setAdapter(subItemAdapter);
        viewHolder.recycler_sub_cat.setRecycledViewPool(viewPool);

        subItemAdapter.setOnEditClicked(new SubCategoryAdapter.OnEditClicked() {
            @Override
            public void setOnEditItem(int subCateID) {
                if (onEDitClicked != null) {
                    onEDitClicked.setOnEditItem(subCateID);
                }
            }
        });
        subItemAdapter.setOnDeleteClicked(new SubCategoryAdapter.OnDeleteCLicked() {
            @Override
            public void setOnDeleteItem(int id) {
                if (onDeleteCLicked != null) {
                    onDeleteCLicked.setOnDeleteItem(id);
                }
            }
        });

        viewHolder.imgOpenCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.tab_expandable.getVisibility() == View.VISIBLE) {
                    isOpen = true;
                    viewHolder.tab_expandable.setVisibility(View.GONE);
                    viewHolder.imgOpenCLose.setImageResource(R.mipmap.add_black);
                } else {
                    isOpen = false;
                    viewHolder.tab_expandable.setVisibility(View.VISIBLE);
                    viewHolder.imgOpenCLose.setImageResource(R.mipmap.minus_black);
                }
            }
        });
        viewHolder.img_category_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEDitClicked != null) {
                    onEDitClicked.setOnEditItem(catlist.get(i).id);
                }
            }
        });
        viewHolder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteCLicked != null) {
                    onDeleteCLicked.setOnDeleteItem(catlist.get(i).id);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return catlist.size();
    }

    public void setOnEditClicked(CategoryAdapter.OnEditClicked onEDitClicked) {
        this.onEDitClicked = (CategoryAdapter.OnEditClicked) onEDitClicked;
    }

    public interface OnEditClicked {
        void setOnEditItem(int cateID);
    }

    public void setOnDeleteClicked(CategoryAdapter.OnDeleteCLicked onDeleteCLicked) {
        this.onDeleteCLicked = (CategoryAdapter.OnDeleteCLicked) onDeleteCLicked;
    }

    public interface OnDeleteCLicked {
        void setOnDeleteItem(int id);
    }

}