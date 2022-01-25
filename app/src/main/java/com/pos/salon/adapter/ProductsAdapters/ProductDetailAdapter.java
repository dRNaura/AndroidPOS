package com.pos.salon.adapter.ProductsAdapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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


public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.MyViewHolder> {
    Context context;
    ArrayList sell_lineList = new ArrayList();
    public static JSONObject sellItem;
    int quantity;
    String price_inc_tax="";
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();

    public ProductDetailAdapter(Context context, ArrayList salesList) {
        this.context = context;
        this.sell_lineList = salesList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txtProductName, txtUnitPrice, txt_Tax, txt_PriceTax, txt_Subtotal, txt_productDiscount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_ProductName);
            txtUnitPrice = itemView.findViewById(R.id.txt_unit_price);
            txt_Tax = itemView.findViewById(R.id.txt_Tax);
            txt_PriceTax = itemView.findViewById(R.id.txt_Price_txt);
            txt_Subtotal = itemView.findViewById(R.id.txt_subTotal);
            txt_productDiscount = itemView.findViewById(R.id.txt_productDiscount);

        }
    }

    @NonNull
    @Override
    public ProductDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_product_detail_item, parent, false);
        return new ProductDetailAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ProductDetailAdapter.MyViewHolder viewHolder, final int i) {

        sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        sellItem = (JSONObject) sell_lineList.get(i);

        viewHolder.txtProductName.setText("");
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

                quantity = sellItem.getInt("quantity");
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
                } else {

                }

            }

            strProductName = strProductName + " X " + quantity + " (Quantity)";

            viewHolder.txtProductName.setText(strProductName);

            if (sellItem.has("unit_price_before_discount") && !sellItem.isNull("unit_price_before_discount")) {
                viewHolder.txtUnitPrice.setText(SaleDetailFragment.currencyType + sellItem.getString("unit_price_before_discount"));
            }

            if (sellItem.has("unit_price_inc_tax") && !sellItem.isNull("unit_price_inc_tax")) {
                viewHolder.txt_PriceTax.setText(SaleDetailFragment.currencyType + sellItem.getString("unit_price_inc_tax"));
            }


            if (sellItem.has("unit_price_inc_tax") && !sellItem.isNull("unit_price_inc_tax")) {
                price_inc_tax = sellItem.getString("unit_price_inc_tax");
            }

            if (sellItem.has("item_tax") && !sellItem.isNull("item_tax")) {

                String itemTex = sellItem.getString("item_tax");

                if (!itemTex.equalsIgnoreCase("null") && itemTex != null) {
                    viewHolder.txt_Tax.setText(SaleDetailFragment.currencyType + sellItem.getString("item_tax"));
                } else {
                    viewHolder.txt_Tax.setText(SaleDetailFragment.currencyType + ("0.00"));
                }
            }

            if (sellItem.has("line_discount_amount") && !sellItem.isNull("line_discount_amount")) {

                if (sellItem.has("line_discount_type") && !sellItem.isNull("line_discount_type")) {

                    String discountType = sellItem.getString("line_discount_type");


                    if (discountType.equalsIgnoreCase("percentage")) {

                        Double unitProPrice = Double.valueOf(sellItem.getString("unit_price_before_discount"));
                        Double unitDisAmount = Double.valueOf(sellItem.getString("line_discount_amount"));
                        Double totolDiscount = unitDisAmount / 100 * unitProPrice;
                        viewHolder.txt_productDiscount.setText(SaleDetailFragment.currencyType+String.format("%.2f",totolDiscount));

                    } else if (discountType.equalsIgnoreCase("fixed")) {
                        viewHolder.txt_productDiscount.setText(SaleDetailFragment.currencyType + String.format("%.2f",Double.valueOf(sellItem.getString("line_discount_amount"))));
                    }else{
                        viewHolder.txt_productDiscount.setText(SaleDetailFragment.currencyType+"0.00");
                    }
                }
            }


            String quantyty1 = String.valueOf(quantity);
            String subtotal = String.valueOf(Float.valueOf(quantyty1) * ((Float.valueOf(price_inc_tax) + fModifierTotalPrice)));
            Log.e("subtotal", subtotal);

            viewHolder.txt_Subtotal.setText(SaleDetailFragment.currencyType +String.format("%.2f",Double.valueOf(subtotal)));
//                viewHolder.txt_Subtotal.setText(SaleDetailFragment.currencyType+(String.format("%.2f",subtotal)));


        } catch (JSONException e) {
            Log.e("Exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return sell_lineList.size();
    }
}
