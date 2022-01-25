package com.pos.salon.adapter.ProductsAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.CustomerAdapters.CustomExpandableListAdapter;
import com.pos.salon.interfacesection.Selectedinterface;
import com.pos.salon.model.searchData.SearchItem;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class SearchItemListAdapter extends RecyclerView.Adapter<SearchItemListAdapter.ViewHolder> {

    Context mContext;
    ArrayList<SearchItem> itemArray = new ArrayList<SearchItem>();
    private int lastExpandedPosition = -1;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListAdapter expandableListAdapter;
    Selectedinterface selectedinterface;
    static SQLiteDatabase sqLiteDatabase;
    String prodid;
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();

    public interface CilckListener {

        public void onItemClick(int position, SearchItem item);

        // public void addProductCart(int position,)
    }

    CilckListener listener;

    public SearchItemListAdapter(Context context, ArrayList<SearchItem> itemArray, CilckListener listener) {
        mContext = context;
        this.itemArray = itemArray;
        this.listener = listener;
        this.selectedinterface = selectedinterface;

    }

    public void updateNewItem(ArrayList<SearchItem> itemArray) {

        this.itemArray = itemArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        sharedPreferences = mContext.getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }
        final SearchItem data = itemArray.get(i);
        float quantityF=0;

        if (data.getQty_available() != null) {
            quantityF = Float.parseFloat(data.getQty_available());
        }

        Log.e("Quantity", String.valueOf(quantityF));
        int quantityInt = (int) quantityF;

        if (quantityInt <= 0) {
            viewHolder.imgAddProduct.setImageResource(R.drawable.outofstock);
            viewHolder.txtquantity.setText("Out of stock");
            viewHolder.txtquantity.setTextColor(Color.RED);
            viewHolder.ll_searchproduct.setEnabled(false);
            viewHolder.ll_productlayout.setBackgroundResource(R.color.colorGray);

        }else
        {
            viewHolder.ll_searchproduct.setEnabled(true);
            viewHolder.ll_productlayout.setBackgroundResource(R.color.black);
            viewHolder.txtquantity.setTextColor(Color.parseColor("#5EB56E"));//grenn color
        }
        String style_no="";
        if(data.style_no !=null){
            style_no=data.style_no;
        }
        viewHolder.txtProductName.setText(style_no+" "+data.getName());
        // Toast.makeText(mContext, ""+Color.M, Toast.LENGTH_LONG).show();

        if (data.isActiveProduct())
        {

            viewHolder.imgAddProduct.setImageResource(R.drawable.minus_active);
            try {
                if (data.getModifier_sets().size() > 0)
                {
                    viewHolder.dialog.show();
                    prodid = data.getProduct_id();
                    expandableListAdapter = new CustomExpandableListAdapter(mContext,data);
                    viewHolder.expandableListView.setAdapter(expandableListAdapter);

                } else {

                }
            } catch (Exception e) {
                //Toast.makeText(mContext, ""+e.toString(), Toast.LENGTH_LONG).show();
            }


        } else {
            viewHolder.imgAddProduct.setImageResource(R.drawable.inactiveadd_product);

        }

        viewHolder.expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    viewHolder.expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        viewHolder.imgAddProduct.setTag(data);
        viewHolder.ll_searchproduct.setTag(data);

        viewHolder.imgAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchItem data = (SearchItem) v.getTag();
                listener.onItemClick(i, data);
            }
        });

        viewHolder.ll_searchproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchItem data = (SearchItem) v.getTag();
                listener.onItemClick(i, data);
            }
        });
        Glide.with(mContext)
                .load("https://shopplusglobal.com/beta/pos/public/uploads/img/product_images/" + data.getImage())
                .into(viewHolder.imgItem);


        viewHolder.txtProductPrice.setText(/*currencyData.getName()*/"$ "+ data.getSelling_price() + ", ");
        viewHolder.txtProductId.setText("(" + data.getProduct_id() + ")");

        if (arrdepartmentsList.contains("2"))
        {
            if(data.getType().equalsIgnoreCase("single"))
            {
                viewHolder.txtVariety.setText("");
            }else
            {
                viewHolder.txtVariety.setText(data.getVariation_name()+" : "+data.getVariation() + "   Color :"+data.getColor());
            }


        }else if (arrdepartmentsList.contains("1"))
        {
            viewHolder.txtVariety.setText("");
        }


        viewHolder.txtquantity.setText(String.valueOf(quantityInt) + "Pc(s)");

        viewHolder.close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.dialog.dismiss();
            }
        });

    }


    public static boolean CheckIsDataAlreadyInDBorNot(String TableName, String tableproductid, String prodid) {
        String Query = "Select * from " + TableName + " where " + tableproductid + " = " + prodid;
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void prepareListData(ArrayList<SearchItem.Modifierrlist> modifier_sets, ArrayList<SearchItem.Variations> variations) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> top250 = new ArrayList<String>();


        for (int i = 0; i < modifier_sets.size(); i++) {
            listDataHeader.add(modifier_sets.get(i).getName());
            top250.add(variations.get(i).getName());
            listDataChild.put(listDataHeader.get(i), top250);
        }

//        expandableListAdapter = new CustomExpandableListAdapter(mContext, expandableListTitle, expandableListDetail);
//        viewHolder.expandableListView.setAdapter(expandableListAdapter);

        //  expandableListAdapter = new CustomExpandableListAdapter(mContext, listDataHeader, listDataChild);
        // viewHolder.expandableListView.setAdapter(expandableListAdapter);


    }

    @Override
    public int getItemCount() {
        return itemArray.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem, imgAddProduct;
        TextView txtProductName, txtProductPrice, txtquantity,txtVariety, txtProductId;
        LinearLayout ll_searchproduct, ll_productlayout;
        Dialog dialog;
        Button btn_addmodifiers, close_dialog;
        ExpandableListView expandableListView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            imgAddProduct = (ImageView) itemView.findViewById(R.id.imgAddProduct);
            txtProductName = (TextView) itemView.findViewById(R.id.txtProductName);
            txtProductPrice = (TextView) itemView.findViewById(R.id.txtProductPrice);
            txtquantity = (TextView) itemView.findViewById(R.id.txtquantity);

            txtProductId = (TextView) itemView.findViewById(R.id.txtProductId);
            txtVariety = (TextView) itemView.findViewById(R.id.txtVariety);
            ll_searchproduct = (LinearLayout) itemView.findViewById(R.id.ll_searchproduct);
            ll_productlayout = (LinearLayout) itemView.findViewById(R.id.ll_productlayout);

            dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_modifierset);
            //   rv_modifiers = dialog.findViewById(R.id.rv_modifiers);
            expandableListView = dialog.findViewById(R.id.expandableListView);
            btn_addmodifiers = dialog.findViewById(R.id.btn_addmodifiers);
            close_dialog = dialog.findViewById(R.id.close_dialog);

        }

    }
}
