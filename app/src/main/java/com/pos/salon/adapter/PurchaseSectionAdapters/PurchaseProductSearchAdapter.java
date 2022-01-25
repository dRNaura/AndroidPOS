package com.pos.salon.adapter.PurchaseSectionAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PurchaseProductSearchAdapter extends RecyclerView.Adapter<PurchaseProductSearchAdapter.ViewHolder> {

    Context mContext;
    ArrayList<PurchaseProductSearchModel> itemArray = new ArrayList<>();
    private int lastExpandedPosition = -1;
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();

    public interface CilckListener {

        public void onItemClickPurchase(int position, PurchaseProductSearchModel item);

        // public void addProductCart(int position,)
    }

    PurchaseProductSearchAdapter.CilckListener listener;

    public PurchaseProductSearchAdapter(Context context, ArrayList<PurchaseProductSearchModel> itemArray,
                                        PurchaseProductSearchAdapter.CilckListener listener) {
        mContext = context;
        this.itemArray = itemArray;
        this.listener = listener;

    }

    public void updateNewItem(ArrayList<PurchaseProductSearchModel> itemArray) {

        this.itemArray = itemArray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PurchaseProductSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_item_row, viewGroup, false);
        return new PurchaseProductSearchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PurchaseProductSearchAdapter.ViewHolder viewHolder, final int i) {

        sharedPreferences = mContext.getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }
        final PurchaseProductSearchModel data = itemArray.get(i);
        float quantityF=0;

        if (data.getQty_available() != null) {
            quantityF = Float.parseFloat(data.getQty_available());
        }

        Log.e("Quantity", String.valueOf(quantityF));
        int quantityInt = (int) quantityF;

//        if (quantityInt <= 0) {
//            viewHolder.imgAddProduct.setImageResource(R.drawable.outofstock);
//            viewHolder.txtquantity.setText("Out of stock");
//            viewHolder.txtquantity.setTextColor(Color.RED);
//            viewHolder.ll_searchproduct.setEnabled(false);
//            viewHolder.ll_productlayout.setBackgroundResource(R.color.colorGray);
//
//        }else
//        {
//            viewHolder.ll_searchproduct.setEnabled(true);
//            viewHolder.ll_productlayout.setBackgroundResource(R.color.black);
//            viewHolder.txtquantity.setTextColor(Color.parseColor("#5EB56E"));//grenn color
//        }
        String style_no="";
        if(data.style_no !=null){
            style_no=data.style_no;
        }
        viewHolder.txtProductName.setText(style_no+" "+data.getName());
        // Toast.makeText(mContext, ""+Color.M, Toast.LENGTH_LONG).show();

        if (data.isActiveProduct())
        {

            viewHolder.imgAddProduct.setImageResource(R.drawable.minus_active);

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
                PurchaseProductSearchModel data = (PurchaseProductSearchModel) v.getTag();
                listener.onItemClickPurchase(i, data);
            }
        });
        viewHolder.ll_searchproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseProductSearchModel data = (PurchaseProductSearchModel) v.getTag();
                listener.onItemClickPurchase(i, data);
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
