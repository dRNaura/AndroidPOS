package com.pos.salon.adapter.AddVariableProduct;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pos.salon.R;
import com.pos.salon.model.VariableModel.VariationValueModel;
import com.pos.salon.utilConstant.AppConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariationValueAdapter extends RecyclerView.Adapter<VariationValueAdapter.MyViewHolder> {

    public Context context;
    private ArrayList<VariationValueModel> list = new ArrayList<>();

    double exc_inc_tax = 0.0, total_exc_tax = 0.0, margin_value = 0.0;
    Map<Integer, Double> arrExc_inc_tax = new HashMap<Integer, Double>();
    Map<Integer, Double> arrtotal_exc_tax = new HashMap<Integer, Double>();
    Map<Integer, Double> arrmargin_value = new HashMap<Integer, Double>();
    String color_name = "", temp_name = "";
    boolean excTaxSingle, incTaxSingle, marginSingle, sellingExcPrice;
    VariationValueAdapter.ClickItem clickItem;
    ArrayList<VariationValueModel> arrVariationValueList;

    public interface ClickItem {
        public void onDeleteClick(int position);

        public void setOnCopyItem(int position, Map<Integer, Double> arrExc_tax, Map<Integer, Double> arrMarginValue, Map<Integer, Double> arrtotal_exc_tax);

    }

    public VariationValueAdapter(Context context, ArrayList<VariationValueModel> arrVariationValueList, ArrayList<VariationValueModel> list, String temp_name, String color_name, VariationValueAdapter.ClickItem clickItem) {
        this.context = context;
        this.list = list;
        this.color_name = color_name;
        this.temp_name = temp_name;
        this.clickItem = clickItem;
        this.arrVariationValueList = arrVariationValueList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText et_value, et_variation_sku;
        ImageView imgDelete, img_copy_value;
        EditText et_purchase_exc, et_purchase_inc, et_margin, et_selling_exc;
        TextView txt_variations;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            et_value = itemView.findViewById(R.id.et_value);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            img_copy_value = itemView.findViewById(R.id.img_copy_value);
            et_purchase_exc = itemView.findViewById(R.id.et_purchase_exc);
            et_purchase_inc = itemView.findViewById(R.id.et_purchase_inc);
            et_margin = itemView.findViewById(R.id.et_margin);
            et_selling_exc = itemView.findViewById(R.id.et_selling_exc);
            et_variation_sku = itemView.findViewById(R.id.et_variation_sku);
            txt_variations = itemView.findViewById(R.id.txt_variations);


        }
    }

    @NonNull
    @Override
    public VariationValueAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.variation_value_items, parent, false);
        return new VariationValueAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final VariationValueAdapter.MyViewHolder viewHolder, final int i) {
//
//        viewHolder.setIsRecyclable(false);
       VariationValueModel model = list.get(i);
       Log.e("post",""+i);


        if (arrVariationValueList.size() == 0) {
            viewHolder.img_copy_value.setVisibility(View.GONE);
        } else {
            viewHolder.img_copy_value.setVisibility(View.VISIBLE);
        }
        if (i == 0) {
            viewHolder.img_copy_value.setVisibility(View.VISIBLE);
        } else {
            viewHolder.img_copy_value.setVisibility(View.GONE);
        }

        if (color_name.equalsIgnoreCase("")) {
            viewHolder.txt_variations.setText(temp_name);
        } else {
            viewHolder.txt_variations.setText(temp_name + " , Color : " + color_name);
        }

        viewHolder.et_value.setText(model.name);

        if(viewHolder.et_value.getText().toString().isEmpty()) {
            viewHolder.et_value.setEnabled(true);
        } else {
            viewHolder.et_value.setEnabled(false);
        }
        viewHolder.et_variation_sku.setText(model.variation_sku);

        if (!model.dpp_exc_tax.equalsIgnoreCase("0.0")) {
            viewHolder.et_purchase_exc.setText(model.dpp_exc_tax);
        } else {
            viewHolder.et_purchase_exc.setText("");
        }
        if (!model.dpp_inc_tax.equalsIgnoreCase("0.0")) {
            viewHolder.et_purchase_inc.setText(model.dpp_inc_tax);
        } else {
            viewHolder.et_purchase_inc.setText("");
        }

        if (!model.var_exc_margin.equalsIgnoreCase("0.0")) {
            viewHolder.et_margin.setText(String.format("%.2f", Double.parseDouble(model.var_exc_margin)));
        } else {
            viewHolder.et_margin.setText("0.0");
        }
        if (!model.var_dsp_exc_tax.equalsIgnoreCase("0.0")) {
            viewHolder.et_selling_exc.setText(String.format("%.2f", Double.parseDouble(model.var_dsp_exc_tax)));
        } else {
            viewHolder.et_selling_exc.setText("");
        }

        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickItem != null) {
                    clickItem.onDeleteClick(i);
                }
            }
        });
        viewHolder.img_copy_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickItem != null) {
                    clickItem.setOnCopyItem(i, arrExc_inc_tax, arrmargin_value, arrtotal_exc_tax);
                }

            }
        });
        viewHolder.et_purchase_exc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                excTaxSingle = b;
            }
        });


        viewHolder.et_purchase_exc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(excTaxSingle){
//                    viewHolder.et_margin.setText("");
//                    margin_value = 0.0;
                    double searchText = 0.0;
                    if (viewHolder.et_purchase_exc.getText().toString().isEmpty()) {
                        searchText = 0.0;
                        exc_inc_tax = 0.0;
                        arrExc_inc_tax.put(i,exc_inc_tax);
                        arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i, Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = list.get(i).var_dsp_exc_tax;
                    } else {
                        searchText = Double.parseDouble(viewHolder.et_purchase_exc.getText().toString());
                        exc_inc_tax = Double.parseDouble(viewHolder.et_purchase_exc.getText().toString());
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i,  Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax =list.get(i).var_dsp_exc_tax;
                    }

                    viewHolder.et_purchase_inc.setText(String.format("%.2f", searchText));
                    total_exc_tax = ((Double.parseDouble(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + exc_inc_tax;
                    if (total_exc_tax == 0.0) {
                        total_exc_tax = ((Double.parseDouble(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + Double.valueOf(searchText);
                        viewHolder.et_selling_exc.setText(String.format("%.2f", total_exc_tax));
//                        viewHolder.et_selling_exc.setText(String.format("%.2f", searchText));
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, (Double.parseDouble(list.get(i).var_exc_margin)));
                        arrtotal_exc_tax.put(i,total_exc_tax);

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                    } else {
                        total_exc_tax = ((Double.parseDouble(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + Double.valueOf(searchText);
                        viewHolder.et_selling_exc.setText(String.format("%.2f",total_exc_tax));
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, Double.parseDouble(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i, total_exc_tax);

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                    }

                    list.set(i,model);
                }
            }
        });

        viewHolder.et_purchase_inc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                incTaxSingle = b;
            }
        });

        viewHolder.et_purchase_inc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (incTaxSingle) {
                    double searchText = 0.0;

                    if (viewHolder.et_purchase_inc.getText().toString().isEmpty()) {
                        searchText = 0.0;
                        exc_inc_tax = 0.0;
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i, Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = list.get(i).var_dsp_exc_tax;
                    } else {
                        searchText = Double.parseDouble(viewHolder.et_purchase_inc.getText().toString());
                        exc_inc_tax = Double.parseDouble(viewHolder.et_purchase_inc.getText().toString());

                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i,  Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax =  list.get(i).var_dsp_exc_tax;
                    }
                    viewHolder.et_purchase_exc.setText(String.format("%.2f", searchText));

                    total_exc_tax = ((Double.valueOf(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + exc_inc_tax;

                    if (total_exc_tax == 0.0) {
                        total_exc_tax = ((Double.parseDouble(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + Double.valueOf(searchText);
                        viewHolder.et_selling_exc.setText(String.format("%.2f", total_exc_tax));
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i,Double.parseDouble(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i, total_exc_tax);

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                    } else {
                        total_exc_tax = ((Double.valueOf(list.get(i).var_exc_margin) / 100) * exc_inc_tax) + Double.valueOf(searchText);
                        viewHolder.et_selling_exc.setText(String.format("%.2f", total_exc_tax));
                        arrExc_inc_tax.put(i, exc_inc_tax);
                        arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                        arrtotal_exc_tax.put(i, total_exc_tax);

                        model.dpp_inc_tax = String.valueOf(exc_inc_tax);
                        model.dpp_exc_tax = String.valueOf(exc_inc_tax);
                        model.var_exc_margin = list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                    }
                    list.set(i,model);
                }
            }
        });
        viewHolder.et_margin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                marginSingle = b;
            }
        });
        viewHolder.et_margin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (marginSingle) {
                    if (viewHolder.et_margin.getText().toString().isEmpty()) {
                        margin_value = 0.0;
                        arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                        arrmargin_value.put(i, margin_value);
                        arrtotal_exc_tax.put(i,  Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax =list.get(i).dpp_exc_tax;
                        model.dpp_exc_tax = list.get(i).dpp_exc_tax;
                        model.var_exc_margin = String.valueOf(margin_value);
                        model.var_dsp_exc_tax = list.get(i).var_dsp_exc_tax;
                    } else {
                        try {
                            margin_value = Double.parseDouble(viewHolder.et_margin.getText().toString());
                        } catch (NumberFormatException e) {
                            margin_value = 0.0;
                        }
                        arrmargin_value.put(i, margin_value);
                        arrtotal_exc_tax.put(i, Double.valueOf(list.get(i).var_dsp_exc_tax));

                        model.dpp_inc_tax = list.get(i).dpp_exc_tax;
                        model.dpp_exc_tax = list.get(i).dpp_exc_tax;
                        model.var_exc_margin = String.valueOf(margin_value);
                        model.var_dsp_exc_tax = list.get(i).var_dsp_exc_tax;
                    }

                    if (margin_value == 0.0) {
                        total_exc_tax = Double.parseDouble(list.get(i).dpp_exc_tax);
                        arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                        arrmargin_value.put(i, margin_value);
                        arrtotal_exc_tax.put(i, total_exc_tax);

                        model.dpp_inc_tax = list.get(i).dpp_exc_tax;
                        model.dpp_exc_tax =list.get(i).dpp_exc_tax;
                        model.var_exc_margin = String.valueOf(margin_value);
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                    } else {
                        total_exc_tax = ((margin_value / 100) * Double.valueOf(list.get(i).dpp_exc_tax)) + Double.valueOf(list.get(i).dpp_exc_tax);
                        arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                        arrmargin_value.put(i, margin_value);
                        arrtotal_exc_tax.put(i, total_exc_tax);

                        model.dpp_inc_tax = list.get(i).dpp_exc_tax;
                        model.dpp_exc_tax =list.get(i).dpp_exc_tax;
                        model.var_exc_margin = String.valueOf(margin_value);
                        model.var_dsp_exc_tax = String.valueOf(total_exc_tax);

                    }
                    viewHolder.et_selling_exc.setText(String.format("%.2f", total_exc_tax));

                    list.set(i,model);
                }


            }
        });

        viewHolder.et_selling_exc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                sellingExcPrice = b;
            }
        });

        viewHolder.et_selling_exc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewHolder.et_selling_exc.setEnabled(true);
                if (sellingExcPrice) {

                    if (Double.valueOf(list.get(i).dpp_exc_tax) > 0) {
                        if (viewHolder.et_selling_exc.getText().toString().isEmpty()) {
                            total_exc_tax = 0.0;
                            arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                            arrmargin_value.put(i, Double.valueOf(list.get(i).var_exc_margin));
                            arrtotal_exc_tax.put(i, total_exc_tax);

                            model.dpp_inc_tax =list.get(i).dpp_exc_tax;
                            model.dpp_exc_tax =list.get(i).dpp_exc_tax;
                            model.var_exc_margin = list.get(i).var_exc_margin;
                            model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                        } else {
                            total_exc_tax = Double.parseDouble(viewHolder.et_selling_exc.getText().toString());
                            arrmargin_value.put(i,  Double.valueOf(list.get(i).var_exc_margin));
                            arrtotal_exc_tax.put(i, total_exc_tax);

                            model.dpp_inc_tax =list.get(i).dpp_exc_tax;
                            model.dpp_exc_tax = list.get(i).dpp_exc_tax;
                            model.var_exc_margin =list.get(i).var_exc_margin;
                            model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                        }

                        if (total_exc_tax == 0.0) {
                            double profit = total_exc_tax - Double.valueOf(list.get(i).dpp_exc_tax);
                            margin_value = (profit / Double.valueOf(list.get(i).dpp_exc_tax)) * 100;

                            total_exc_tax = Double.valueOf(list.get(i).dpp_exc_tax);
                            arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                            arrmargin_value.put(i, margin_value);
                            arrtotal_exc_tax.put(i, total_exc_tax);

                            model.dpp_inc_tax =list.get(i).dpp_exc_tax;
                            model.dpp_exc_tax =list.get(i).dpp_exc_tax;
                            model.var_exc_margin = String.valueOf(margin_value);
                            model.var_dsp_exc_tax = String.valueOf(total_exc_tax);
                        } else {
                            double profit = total_exc_tax - Double.valueOf(list.get(i).dpp_exc_tax);
                            margin_value = (profit / Double.valueOf(list.get(i).dpp_exc_tax)) * 100;

                            arrExc_inc_tax.put(i, Double.valueOf(list.get(i).dpp_exc_tax));
                            arrmargin_value.put(i, margin_value);
                            arrtotal_exc_tax.put(i, total_exc_tax);

                            model.dpp_inc_tax =list.get(i).dpp_exc_tax;
                            model.dpp_exc_tax = list.get(i).dpp_exc_tax;
                            model.var_exc_margin = String.valueOf(margin_value);
                            model.var_dsp_exc_tax = String.valueOf(total_exc_tax);

                        }
                        list.set(i,model);
                        viewHolder.et_margin.setText(String.format("%.2f", margin_value));

                    } else {
                        viewHolder.et_selling_exc.setEnabled(false);
                        AppConstant.showToast(context, "Please Enter Valid Purchase Price");
                    }

                }
            }
        });


        viewHolder.et_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                model.name = s.toString();

//                value=s.toString();
            }
        });

        viewHolder.et_variation_sku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                model.variation_sku = s.toString();

//                value=s.toString();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

