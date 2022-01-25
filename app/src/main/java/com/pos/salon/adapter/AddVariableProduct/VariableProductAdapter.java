package com.pos.salon.adapter.AddVariableProduct;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.SpinnerSelectionAdapter;
import com.pos.salon.model.ProductModel.ImagesModel;
import com.pos.salon.model.VariableModel.VariationValueModel;
import com.pos.salon.model.repairModel.SpinnerSelectionModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class VariableProductAdapter extends RecyclerView.Adapter<VariableProductAdapter.MyViewHolder> {

    public Context context;
    private ArrayList<SpinnerSelectionModel> arrProductTempList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrProductColorList = new ArrayList<>();
    OnClicked onClicked;
    OnTempVariable onTempVariable;
    OnImageSelect onImageSelect;
    int template_id = 0, color_id = 0;
    ArrayList<Integer> list;
    Map<Integer, Integer> mSelectedTempItem = new HashMap<Integer, Integer>();
    Map<Integer, Integer> mSelectedColorItem = new HashMap<Integer, Integer>();
    ArrayList<VariationValueModel> arrAllVariationValueList=new ArrayList();
    ArrayList<ImagesModel> arrVariantImagesList;
    SharedPreferences sp_cartSave;
    public VariableProductAdapter(Context context, ArrayList<Integer> list,ArrayList<SpinnerSelectionModel> arrProductTempList,ArrayList<SpinnerSelectionModel> arrProductColorList,ArrayList<ImagesModel> arrVariantImagesList) {
        this.context = context;
        this.list = list;
        this.arrProductTempList = arrProductTempList;
        this.arrProductColorList = arrProductColorList;
        this.arrVariantImagesList = arrVariantImagesList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner_template, spinner_Color;
        LinearLayout lay_variation_value;
        TextView txt_variant_image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            spinner_Color = itemView.findViewById(R.id.spinner_Color);
            spinner_template = itemView.findViewById(R.id.spinner_template);
            lay_variation_value = itemView.findViewById(R.id.lay_variation_value);
            txt_variant_image = itemView.findViewById(R.id.txt_variant_image);

        }
    }

    @NonNull
    @Override
    public VariableProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.varible_product_items, parent, false);
        return new VariableProductAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VariableProductAdapter.MyViewHolder viewHolder, final int i) {

//        getProductType();

        SpinnerSelectionAdapter colorAdapter = new SpinnerSelectionAdapter(context, arrProductColorList);
        viewHolder.spinner_Color.setAdapter(colorAdapter);

        SpinnerSelectionAdapter tempAdapter = new SpinnerSelectionAdapter(context, arrProductTempList);
        viewHolder.spinner_template.setAdapter(tempAdapter);

        if(arrVariantImagesList.size() >i){
            ImagesModel model=arrVariantImagesList.get(i);
            viewHolder.txt_variant_image.setText(model.name);
        }


//        if (mSpinnerSelectedItem.containsKey(i)) {
//            viewHolder.spinner_template(mSpinnerSelectedItem.get(i));
//        }

//        if(arrProductTempList.size() > 0){
//            for(int a=0;a<arrProductTempList.size();a++){
//                if(arrProductTempList.get(a).isDisable){
//                    viewHolder.spinner_template.setEnabled(false);
//                }
//            }
//        }
        sp_cartSave = context.getSharedPreferences("myCartPreference", MODE_PRIVATE);

        arrAllVariationValueList=new ArrayList<>();
        if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<VariationValueModel>>() {}.getType();
            String strMyCart = sp_cartSave.getString("myVariation", "");
            arrAllVariationValueList = (ArrayList<VariationValueModel>) gson.fromJson(strMyCart, type);

        }

        if(arrAllVariationValueList.size()> i){
            int colorId=arrAllVariationValueList.get(i).color_id;
            int id =arrAllVariationValueList.get(i).variation_template_id;

            for(int a=0; a<arrProductTempList.size();a++){
                if(id==arrProductTempList.get(a).id){
                    viewHolder.spinner_template.setSelection(a);
                    viewHolder.spinner_template.setEnabled(false);
                    break;
                }
            }
            for(int a=0; a<arrProductColorList.size();a++){
                if(colorId==arrProductColorList.get(a).id){
                    viewHolder.spinner_Color.setSelection(a);
                    viewHolder.spinner_template.setEnabled(false);
                    break;
                }
            }

        }

        viewHolder.spinner_template.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try{
                    SpinnerSelectionModel itemSelected = (SpinnerSelectionModel) parent.getItemAtPosition(position);
                    template_id=itemSelected.id;
                    mSelectedTempItem.put(i, template_id);

                }catch (Exception e){
                    Log.e("Exception",e.toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHolder.spinner_Color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerSelectionModel itemSelected = (SpinnerSelectionModel) parent.getItemAtPosition(position);
                color_id=itemSelected.id;
                mSelectedColorItem.put(i, color_id);

//                if(onColorVariable !=null){
//                    onColorVariable.setOnClickedItem(i,position);
//                }

//                if(position > 0){
//                    color_id=arrProductColorList.get(position).id;
//                }else{
//
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.lay_variation_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onClicked !=null){
                    onClicked.setOnClickedItem(i,mSelectedTempItem,mSelectedColorItem);
                }
            }
        });

        viewHolder.txt_variant_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onImageSelect !=null){
                    onImageSelect.setOnImageSelect(i,viewHolder.txt_variant_image);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItmeClicked(VariableProductAdapter.OnClicked onClicked) {
        this.onClicked = (VariableProductAdapter.OnClicked) onClicked;
    }

    public interface OnClicked {
        void setOnClickedItem(int position, Map<Integer, Integer> mSelectedTempItem,Map<Integer, Integer> mSelectedColorItem);
    }

    public void setOnTempClicked(VariableProductAdapter.OnTempVariable onTempVariable) {
        this.onTempVariable = (VariableProductAdapter.OnTempVariable) onTempVariable;
    }

    public interface OnTempVariable {
        void setOnClickedItem(int position,int spinnerPOstion);
    }
    public void setOnImageSelect(VariableProductAdapter.OnImageSelect onImageSelect) {
        this.onImageSelect = (VariableProductAdapter.OnImageSelect) onImageSelect;
    }

    public interface OnImageSelect {
        void setOnImageSelect(int position,TextView txtVariantImg);
    }

}

