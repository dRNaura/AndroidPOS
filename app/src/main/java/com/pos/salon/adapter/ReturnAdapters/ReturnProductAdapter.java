package com.pos.salon.adapter.ReturnAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.fragment.SaleDetailFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ReturnProductAdapter extends RecyclerView.Adapter<ReturnProductAdapter.MyViewHolder> {

    public Context context;
    ArrayList returnProductList = new ArrayList<>();
   JSONObject sellItem;
   String currencyType;
   SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();

    public ReturnProductAdapter(Context context, ArrayList salesList,String currencyType) {
        this.context = context;
        this.returnProductList=salesList;
        this.currencyType=currencyType;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_ProductName,txt_unit_price,txt_return_quantity,txt_return_subTotal;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_ProductName=itemView.findViewById(R.id.txt_ProductName);
            txt_unit_price=itemView.findViewById(R.id.txt_unit_price);
            txt_return_quantity=itemView.findViewById(R.id.txt_return_quantity);
            txt_return_subTotal=itemView.findViewById(R.id.txt_return_subTotal);
        }
    }
    @NonNull
    @Override
    public ReturnProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.return_product_detail_items, parent, false);
        return new ReturnProductAdapter.MyViewHolder(itemView);

    }
    @Override
    public void onBindViewHolder(final ReturnProductAdapter.MyViewHolder viewHolder, final int i) {


        sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        sellItem= (JSONObject) returnProductList.get(i);


//            if(sellItem.has("product") && !sellItem.isNull("product"))
//            {
//
//                JSONObject sellProduct=  (JSONObject) sellItem.getJSONObject("product");
//
//
//                String productNAme= sellProduct.getString("name");
//                viewHolder.txt_ProductName.setText(productNAme);
//            }

            viewHolder.txt_ProductName.setText("");
            try {

                String strProductName = ""; // combine all products name , modifiers name etc.
                Float fModifierTotalPrice = 0.0f; // get item total additional modifiers price and then add to product sub total.
                JSONObject sellProduct = null;
                if (sellItem.has("product") && !sellItem.isNull("product")) {

                    sellProduct = (JSONObject) sellItem.getJSONObject("product");
                    String strName = "";
                    if (sellProduct.has("name") && !sellProduct.isNull("name")) {
                        strName = sellProduct.getString("name");
                    }
                    strProductName = strProductName + sellProduct.getInt("id") + " " + strName;
                }

                if (sellItem.has("modifiers") && !sellItem.isNull("modifiers")) {
                    JSONArray arrModifiers = sellItem.getJSONArray("modifiers");
                    for (int j = 0; j < arrModifiers.length(); j++) {
                        JSONObject objModifier = arrModifiers.getJSONObject(j);

                        if (objModifier.has("product") && !objModifier.isNull("product")) {
                            JSONObject objProduct = objModifier.getJSONObject("product");

                            if (objProduct.has("name") && !objProduct.isNull("name")) {
                                if (j == 0) {
                                    strProductName = strProductName + " - " + objProduct.getString("name");
                                } else {
                                    strProductName = strProductName + objProduct.getString("name");
                                }

                            }
                        }



                        if (objModifier.has("variations") && !objModifier.isNull("variations")) {
                            JSONObject objVariation = objModifier.getJSONObject("variations");

                            if (objVariation.has("name") && !objVariation.isNull("name")) {
                                strProductName = strProductName + "(" + objVariation.getString("name");

                                if (objVariation.has("sell_price_inc_tax") && !objVariation.isNull("sell_price_inc_tax")) {
                                    fModifierTotalPrice = fModifierTotalPrice + Float.valueOf(objVariation.getString("sell_price_inc_tax"));

                                    strProductName = strProductName + "-" + SaleDetailFragment.currencyType + objVariation.getString("sell_price_inc_tax");
                                }

                                strProductName = strProductName + ")";
                            }
                        }

                        if (j == arrModifiers.length() - 1) {

                        } else {
                            strProductName = strProductName + ", ";
                        }
                    }


                    //if user not logged with Food & Beverage.
                    if (arrdepartmentsList.contains("1")) {//electronics

                        if (!sellProduct.getString("type").equalsIgnoreCase("single")) //using negation , type = single means this product have no variant.
                        {
                            if (arrModifiers.length() == 0) {
                                if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                                    JSONObject objVariation = sellItem.getJSONObject("variations");

                                    //if name not equal to dummy only then show variation.
                                    if(!objVariation.getString("name").equalsIgnoreCase("DUMMY")) {

                                        if (objVariation.has("product_variation") && !objVariation.isNull("product_variation")) {
                                            JSONObject objProductVariation = objVariation.getJSONObject("product_variation");

                                            strProductName = strProductName + " (" + objProductVariation.getString("name") + ":" + objVariation.getString("name");
                                        }

                                        if (objVariation.has("color") && !objVariation.isNull("color")) {
                                            JSONObject objColor = objVariation.getJSONObject("color");

                                            if (objColor.has("name") && !objColor.isNull("name")) {
                                                strProductName = strProductName + ", Color:" + objColor.getString("name") + ")";
                                            }
                                        } else {
                                            strProductName = strProductName + ")";
                                        }
                                    }
                                }
                            }
                        }
                    } else if (arrdepartmentsList.contains("2")) {//fashions
                        if (arrModifiers.length() == 0)
                        {
                            if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                                JSONObject objVariation = sellItem.getJSONObject("variations");

                                if (objVariation.has("product_variation") && !objVariation.isNull("product_variation")) {
                                    JSONObject objProductVariation = objVariation.getJSONObject("product_variation");

                                    strProductName = strProductName + " (" + objProductVariation.getString("name") + ":" + objVariation.getString("name");
                                }

                                if (objVariation.has("color") && !objVariation.isNull("color")) {
                                    JSONObject objColor = objVariation.getJSONObject("color");

                                    if (objColor.has("name") && !objColor.isNull("name")) {
                                        strProductName = strProductName + ", Color:" + objColor.getString("name") + ")";
                                    }
                                } else {
                                    strProductName = strProductName + ")";
                                }
                            }
                        }
                    } else {

                    }

                }

                viewHolder.txt_ProductName.setText(strProductName);


            String unit_price="0.0";
            String return_quant="0";
            if(sellItem.has("unit_price_inc_tax") && !sellItem.isNull("unit_price_inc_tax"))
            {
                unit_price=sellItem.getString("unit_price_inc_tax");
                viewHolder.txt_unit_price.setText(currencyType+sellItem.getString("unit_price_inc_tax"));
            }

            if(sellItem.has("quantity_returned") && !sellItem.isNull("quantity_returned"))
            {
                return_quant=sellItem.getString("quantity_returned");
                viewHolder.txt_return_quantity.setText(sellItem.getString("quantity_returned"));
            }

            String subtotal = String.valueOf(Float.valueOf(unit_price) * Float.valueOf(return_quant));

            viewHolder.txt_return_subTotal.setText(currencyType+subtotal);
//                viewHolder.txt_Subtotal.setText(SaleDetailFragment.currencyType+(String.format("%.2f",subtotal)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return returnProductList.size();
    }
}
