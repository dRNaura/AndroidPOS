package com.pos.salon.adapter.ProductsAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.CustomerAdapters.CustomExpandableListAdapter;
import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.posLocation.Taxes;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<SearchItem> itemArray;
    private ArrayList<SearchItem> arrItemsOrignal = new ArrayList<SearchItem>();
    private SearchItem objOrignalItem = null;
    private final String[] discountType;
    private ArrayList<SearchItem> tempItem = new ArrayList<SearchItem>();
    private ArrayList<Taxes> itemTax = new ArrayList<Taxes>();
    private ArrayList<String> tax = new ArrayList<String>();
    private String discountAmt = "", discountTypeItem = "Select Discount Type", taxTypeItem = "Select Tax Type";
    private double subTotal = 0.0, taxAmount = 0.0f;
    private DecimalFormat df = new DecimalFormat("##.00");
//    CurriencyData currencyData
    double Variations_total = 0;
    String productid;
    CustomExpandableListAdapter expandableListAdapter;
    public String parentComingFrom="",from="";
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();
    BusinessDetails objBusiness = new BusinessDetails();
    int productOrService=1;

    public interface ClickDeleteItem {

        public void onItemClick(int position, SearchItem item);

        public void onClick(int value, SearchItem item, int position, double y, String addValue);

        public void onDiscountCalculation(String discountType, String discountAmt, double unitFinalPrice, int position);

        public void onTaxCalculation(String taxType, double taxAmt, double totalPrice, int position);

        public void onSellPriceUpdate(double newSellPrice, int position);

    }

    // SearchItem cartmodifiersdata;
    ClickDeleteItem clickItem;

    public void updateList(SearchItem item) {
        itemArray.add(itemArray.size(), item);

        //  notifyDataSetChanged();

    }


    public CalculationAdapter(Context context,String parentComingFrom,String from,int productOrService, ArrayList<SearchItem> itemArray, ArrayList<SearchItem> arrOrigProductItems,
                              ArrayList<Taxes> itemTax, ClickDeleteItem clickItem) {
        mContext = context;
        this.itemArray = itemArray;
        this.clickItem = clickItem;
        this.itemTax = itemTax;
        this.arrItemsOrignal = arrOrigProductItems;
        this.parentComingFrom = parentComingFrom;
        this.from = from;
        this.productOrService = productOrService;


//        Toast.makeText(context, ""+modtitle.get(0), Toast.LENGTH_LONG).show();
        discountType = mContext.getResources().getStringArray(R.array.discount_array);
        if (tax.size() > 0){
            tax.clear();
        }

        if (itemTax != null) {
            for (Taxes taxSelect : itemTax) {
                tax.add(taxSelect.getName());
            }
        }
    }


    public void setNewItem(ArrayList<SearchItem> tempItem) {
        this.tempItem = tempItem;

    }


    public ArrayList<SearchItem> getItemArray() {
        return itemArray;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selected_product_list, viewGroup, false);
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


        //-------get currency name
        Intent intent = ((Activity) mContext).getIntent();
//        currencyData = (CurriencyData) intent.getSerializableExtra("currency");
        //Toast.makeText(mContext, ""+currencyData.getName(), Toast.LENGTH_LONG).show();
        sharedPreferences = mContext.getSharedPreferences("login", MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<BusinessDetails>() {
        }.getType();
        final String myBusinessDetail = sharedPreferences.getString("myBusinessDetail", "");
        if (myBusinessDetail != null) {
            objBusiness = (BusinessDetails) gson.fromJson(myBusinessDetail, type);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, discountType);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        viewHolder.spnDiscountType.setAdapter(spinnerAdapter);

        String strExistDiscountType = "";

        //  check if a product already have a discount
        if (data.discount != null) {

            if (!data.discount.getDiscount_type().equalsIgnoreCase("")) {
                strExistDiscountType = data.discount.getDiscount_type();

                if (strExistDiscountType.equalsIgnoreCase("fixed")) {
                    strExistDiscountType = "Fixed";
                } else if (strExistDiscountType.equalsIgnoreCase("percentage")) {
                    strExistDiscountType = "Percentage";
                }

                viewHolder.edtDiscountAmt.setText(data.discount.getDiscount_amount());
            }
        } else {
            if (data.getDiscountType().equalsIgnoreCase("fixed")) {
                strExistDiscountType = "Fixed";

                if (!data.getDiscountt().equalsIgnoreCase("")) {
                    viewHolder.edtDiscountAmt.setText(data.getDiscountt());
                }


            } else if (data.getDiscountType().equalsIgnoreCase("percentage")) {
                strExistDiscountType = "Percentage";

                if(from.equalsIgnoreCase("fromSaleDetail")){
                    if (!data.getDiscountAmt().equalsIgnoreCase("")) {
//                viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                        viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                    }

                }else{
                    if (!data.getDiscountAmt().equalsIgnoreCase("")) {
//                viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                        viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                    }

                }
            }

        }

        data.setSelling_price(data.getSelling_price());
        final int disPosition = spinnerAdapter.getPosition(strExistDiscountType);
        viewHolder.spnDiscountType.setSelection(disPosition);
        // Toast.makeText(mContext, ""+data.getQty_available(), Toast.LENGTH_LONG).show();

        if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            viewHolder.spnDiscountType.setEnabled(false);
        }
            viewHolder.spnDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    double singlUnitTaxAmount = 0;
                    //calculate tax amount for create unit final price.
                    if (!taxTypeItem.equalsIgnoreCase("Select Tax Type")) {//already tax selected.

                        double taxAmtF = Double.parseDouble(data.getTaxAmount());

                        singlUnitTaxAmount = (Double.valueOf(data.getSelling_price()) * taxAmtF) / 100;

                    }

                    data.setTaxCalculationAmt(df.format(singlUnitTaxAmount) + "");
                    //end of tax calculate.


                    Log.e("dis iTem", "disacount Item");

                    discountTypeItem = discountType[position];

                    if (discountTypeItem.equalsIgnoreCase("Fixed")) {
                        data.setDiscountType("fixed");


                    } else if (discountTypeItem.equalsIgnoreCase("Percentage")) {
                        data.setDiscountType("percentage");
                    }


                    if (!discountTypeItem.equalsIgnoreCase("Select Discount Type")) {
                        if (disPosition != position) {
                            data.setCalculationDone(false);
                        }

                        discountAmt = viewHolder.edtDiscountAmt.getText().toString();


                        double singleUnitDiscountAmount = 0;

                        if (discountAmt != null && !discountAmt.equalsIgnoreCase("")) {
                            if (!data.isCalculationDone()) {

                                String strDiscountType = discountTypeItem;
                                String strUpdateDiscountType = strDiscountType.substring(0, 1).toUpperCase() + strDiscountType.substring(1);
                                switch (strUpdateDiscountType) {
                                    case "Fixed":
                                        singleUnitDiscountAmount = Double.valueOf(discountAmt);

                                        break;

                                    case "Percentage":
                                        singleUnitDiscountAmount = (Double.valueOf(data.getSelling_price()) * Double.valueOf(viewHolder.edtDiscountAmt.getText().toString())) / 100;

                                        break;
                                }

                                data.setDiscountt(String.valueOf(singleUnitDiscountAmount));
                                data.setDiscountAmt(viewHolder.edtDiscountAmt.getText().toString());


                                data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                                subTotal = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();

                                clickItem.onDiscountCalculation(discountTypeItem, String.valueOf(singleUnitDiscountAmount), Double.valueOf(data.getUnitFinalPrice()), i);
                                viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));
                            }
                        }
                    } else {
                        viewHolder.edtDiscountAmt.setText("");

                        discountAmt = "0";

                        data.setDiscountt(discountAmt);
                        data.setDiscountAmt(viewHolder.edtDiscountAmt.getText().toString());


                        data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - Double.valueOf(discountAmt)));

                        subTotal = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();

                        if (data.isCalculationDone()) {
                            clickItem.onDiscountCalculation(discountTypeItem, "0", Double.valueOf(data.getUnitFinalPrice()), i);
                        }

                        viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));
                    }




                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        //if user not logged with Food & Beverage.

        for(int a=0 ;a<arrdepartmentsList.size();a++) {
            if (arrdepartmentsList.get(a).contains("1")) {
                if (data.getType().equalsIgnoreCase("single")) {
                    data.setVariation_name("");
                }

            }
        }

        if (!data.getVariation_name().isEmpty()) {
            viewHolder.tv_mvariations.setText(data.getVariation_name());
        } else {

            viewHolder.tv_mvariations.setText("");
        }


//        try {
//
//            ArrayList<ProductModifier> arr = data.getArrSelectedModifiers();
//
//            Variations_total = 0;
//            String strVariation = "";
//            for (int z=0;z<arr.size();z++)
//            {
//                ProductModifier obj = arr.get(z);
//
//                if(z == (arr.size() - 1))
//                {
//                    strVariation = strVariation + obj.getName() + "-" + obj.getVariation_name() + "(" + obj.getVariation_price() + ")";
//
//                }else
//                {
//                    strVariation = strVariation + ", " +obj.getName() + "-" + obj.getVariation_name() + "(" + obj.getVariation_price() + ")";
//                }
//
//                Variations_total = Variations_total + Double.valueOf(obj.getVariation_price());
//            }
//
//            viewHolder.tv_mvariations.setText(strVariation);
//
//
//        } catch (Exception e) {
//
//        }


        viewHolder.edtDiscountAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    viewHolder.edtDiscountAmt.setSelection(viewHolder.edtDiscountAmt.getText().length());
//
//                }
            }
        });

        if(viewHolder.imgOptionsEdit !=null){
            viewHolder.imgOptionsEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openOptionsDialog(data,viewHolder.edtDiscountAmt,viewHolder.spnDiscountType);
                }
            });

        }

        if(parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            viewHolder.edtDiscountAmt.setEnabled(false);
        }
        else{
        viewHolder.edtDiscountAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                double singlUnitTaxAmount = 0;
                //calculate tax amount for create unit final price.
                if (!taxTypeItem.equalsIgnoreCase("Select Tax Type")) {//already tax selected.

                    double taxAmtF = Double.parseDouble(data.getTaxAmount());

                    singlUnitTaxAmount = (Double.valueOf(data.getSelling_price()) * taxAmtF) / 100;

                }

                data.setTaxCalculationAmt(df.format(singlUnitTaxAmount) + "");
                //end of tax calculate.


                if (!data.getDiscountType().equalsIgnoreCase("Select Discount Type")) {
                    discountAmt = viewHolder.edtDiscountAmt.getText().toString().trim();

                    //if discount value not entered.
                    double singleUnitDiscountAmount = 0;

                    if (discountAmt.equalsIgnoreCase("") || discountAmt.equalsIgnoreCase("0")) {
                        if (data.isCalculationDone()) {
                            discountAmt = "0";
                            singleUnitDiscountAmount = 0;
                            data.setDiscountt(discountAmt);
                            data.setDiscountAmt(viewHolder.edtDiscountAmt.getText().toString());


                            data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                            subTotal = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();


                            clickItem.onDiscountCalculation(data.getDiscountType(), discountAmt, Double.valueOf(data.getUnitFinalPrice()), i);

                            viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));

                        }
                        return;
                    }


                    if (discountAmt.equalsIgnoreCase("")) {
                        discountAmt = "0";
                    }


                    String strDiscountType = data.getDiscountType();
                    String strUpdateDiscountType = strDiscountType.substring(0, 1).toUpperCase() + strDiscountType.substring(1);
                    switch (strUpdateDiscountType) {
                        case "Fixed":
                            singleUnitDiscountAmount = Double.valueOf(discountAmt);

                            break;

                        case "Percentage":
                            singleUnitDiscountAmount = (Double.valueOf(data.getSelling_price()) * Double.valueOf(viewHolder.edtDiscountAmt.getText().toString())) / 100;

                            break;
                    }

                    data.setDiscountt(String.valueOf(singleUnitDiscountAmount));
                    data.setDiscountAmt(viewHolder.edtDiscountAmt.getText().toString());

                    data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                    subTotal = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();


                    data.setCalculationDone(false);
                    clickItem.onDiscountCalculation(data.getDiscountType(), String.valueOf(singleUnitDiscountAmount), Double.valueOf(data.getUnitFinalPrice()), i);

                    viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));

                } else {
                    // Toast.makeText(mContext,"Select Discount Type",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
        ArrayAdapter<String> taxAdapter = new ArrayAdapter<String>(mContext.getApplicationContext(), R.layout.spinner_item, tax);
        taxAdapter.setDropDownViewResource(R.layout.spinner_item);
        viewHolder.spnTax.setAdapter(taxAdapter);
        final int taxPosition = taxAdapter.getPosition(data.getTaxType());
        viewHolder.spnTax.setSelection(taxPosition);
        viewHolder.spnTax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                taxTypeItem = tax.get(position);

                viewHolder.edtTaxCalculation.setText("");


                //Calculate discount amount before tax.
                double singleUnitDiscountAmount = 0;

                if (!viewHolder.edtDiscountAmt.getText().toString().equalsIgnoreCase("")) {
                    double discountAmtF = Double.valueOf(viewHolder.edtDiscountAmt.getText().toString());//Double.parseDouble(data.getDiscountAmt());

                    String strDiscountType = data.getDiscountType();
                    String strUpdateDiscountType = strDiscountType.substring(0, 1).toUpperCase() + strDiscountType.substring(1);
                    switch (strUpdateDiscountType) {
                        case "Fixed":

                            singleUnitDiscountAmount = discountAmtF;

                            break;

                        case "Percentage":

                            singleUnitDiscountAmount = (Double.parseDouble(data.getSelling_price()) * discountAmtF) / 100;

                            if (!data.isCalculationDone())
                                data.setCalculationDone(true);
                            break;
                    }
                }

                data.setDiscountt(String.valueOf(singleUnitDiscountAmount));
                data.setDiscountAmt(viewHolder.edtDiscountAmt.getText().toString());
                //end of discount calculation.

                double singlUnitTaxAmount = 0;

                if (!taxTypeItem.equalsIgnoreCase("Select Tax Type")) {
//                    viewHolder.edtTaxCalculation.setText(itemTax.get(position).getAmount());
                    taxAmount = Float.parseFloat(itemTax.get(position).getAmount());  // show % in tax amount like 5% , 10% etc
                    data.setTaxAmount(String.valueOf(taxAmount));


                    singlUnitTaxAmount = ((Double.parseDouble(data.getSelling_price()) * taxAmount) / 100);
                    data.setTaxCalculateAmt(singlUnitTaxAmount);


                    data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                    subTotal = ((Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity());

                    data.setTaxSelectId(itemTax.get(position).getId());

                    if (!data.isTaxCalculationDone()) {
                        clickItem.onTaxCalculation(taxTypeItem, singlUnitTaxAmount, Double.valueOf(data.getUnitFinalPrice()), i);

                    }

                    viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));

                } else {

                    if (data.isTaxCalculationDone()) {

                        data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                        subTotal = ((Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity());


                        clickItem.onTaxCalculation(taxTypeItem, 0, Double.valueOf(data.getUnitFinalPrice()), i);

                        viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + (String.format("%.2f", subTotal)));
                        taxAmount = 0.0;

                        data.setTaxCalculateAmt(taxAmount);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHolder.txtProductName.setText(data.style_no+" "+data.getName());
        viewHolder.txt_CurrencyType.setText(objBusiness.getCurrency_code());
        viewHolder.ed_ProductPrice.setText(data.getSelling_price());
//        viewHolder.ed_ProductPrice.setSelection(viewHolder.ed_ProductPrice.getText().length());
//        if(AppConstant.objPosSetting.is_pos_subtotal_editable)
//        {
//            viewHolder.ed_ProductPrice.setEnabled(true);
//        }else
//        {
//            viewHolder.ed_ProductPrice.setEnabled(false);
//        }

        viewHolder.editQuantity.setText(data.getQuantity() + "");


        data.setUnitFinalPrice(String.valueOf(Double.parseDouble(data.getSelling_price()) + data.getTaxCalculateAmt() - Double.valueOf(data.getDiscountt())));
        subTotal = ((Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity());

        Log.e("Variations_total", "" + Variations_total);
        data.setFinalPrice(subTotal + "");
        viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));
        //  }
//        if (!data.getImage().equalsIgnoreCase("")) {
//            Glide.with(mContext)
//                    .load("https://shopplusglobal.com/beta/pos/public/uploads/img/product_images/" + data.getImage())
//                    .into(viewHolder.imgProduct);
//        }


            viewHolder.addButton.setTag(data);
            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
                        AppConstant.showToast(mContext, "Cannot Perform This Action");
                    } else {
                        final SearchItem data = (SearchItem) view.getTag();
                        productid = data.getProduct_id();

                        if (data.getQty_available() == null) {
                            data.setQty_available("0");
                        }
                        if(productOrService==0){
                            int qunt= Integer.parseInt(data.getQty_available());
                            int qntyAvail= qunt + 1;
                            data.setQty_available(String.valueOf(qntyAvail));
                        }
                        float quantityF = Float.parseFloat(data.getQty_available());
                        int quantityInt = (int) quantityF;
                        Log.e("QuantityInt", "" + quantityInt);
                        int getCurrentCount = data.getQuantity();
                        Log.e("Current", "" + getCurrentCount);

                        // if (currentNos >= 1) {
                        //Toast.makeText(mContext, ""+y, Toast.LENGTH_LONG).show();
                        if (getCurrentCount < quantityInt) {

                            for (int i = 0; i < arrItemsOrignal.size(); i++) {
                                if (arrItemsOrignal.get(i).getName().equalsIgnoreCase(data.getName())) {
                                    objOrignalItem = arrItemsOrignal.get(i);
                                    break;
                                }
                            }

                            try {
                                if (objOrignalItem.getModifier_sets() != null) {

                                    if (objOrignalItem.getModifier_sets().size() > 0) {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                        builder1.setMessage("Do you want to continue with same modifiers?");
                                        builder1.setCancelable(true);
                                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {
                                                int adapterPosition = viewHolder.getAdapterPosition();

                                                itemArray.get(adapterPosition).setQuantity(itemArray.get(adapterPosition).getQuantity() + 1);
                                                SearchItem getItem = itemArray.get(adapterPosition);
                                                clickItem.onClick(getItem.getQuantity(), data, i, getItem.getVariationPrice(), "add_same_modifier");

                                                Double add = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();
                                                viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", add));
                                                viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                                                dialog1.cancel();
                                            }
                                        });

                                        //adding variation to dialog on cart page
                                        builder1.setNegativeButton("Change", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogg, int id) {

                                                viewHolder.dialog.show();

                                                expandableListAdapter = new CustomExpandableListAdapter(mContext, objOrignalItem);
                                                viewHolder.expandableListView.setAdapter(expandableListAdapter);

                                            }

                                        });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                    } else {//if product has not modifiers
                                        int adapterPosition = viewHolder.getAdapterPosition();

                                        itemArray.get(adapterPosition).setQuantity(itemArray.get(adapterPosition).getQuantity() + 1);
                                        SearchItem getItem = itemArray.get(adapterPosition);
                                        clickItem.onClick(getItem.getQuantity(), data, i, getItem.getVariationPrice(), "add_same_modifier");

                                        Double add = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();
                                        viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", add));
                                        viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                                    }

                                } else {//if product has not modifiers
                                    int adapterPosition = viewHolder.getAdapterPosition();

                                    itemArray.get(adapterPosition).setQuantity(itemArray.get(adapterPosition).getQuantity() + 1);
                                    SearchItem getItem = itemArray.get(adapterPosition);
                                    clickItem.onClick(getItem.getQuantity(), data, i, getItem.getVariationPrice(), "add_same_modifier");

                                    Double add = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();
                                    viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", add));
                                    viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                                }


                            } catch (Exception e) {
                                Log.e("On add button cart : ", e.toString());
                            }


                        } else {
                            Toast.makeText(mContext, "You can sell only " + quantityInt, Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });


            viewHolder.minusButton.setTag(data);
            viewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
                        AppConstant.showToast(mContext,"Cannot Perform This Action");
                    } else {

                    SearchItem data = (SearchItem) v.getTag();

                    float quantityF = Float.parseFloat(data.getQty_available());
                    int quantityInt = (int) quantityF;

                    int getCurrentCount = data.getQuantity();


                    if (getCurrentCount > 1) {


                        int getPosition = viewHolder.getAdapterPosition();

                        itemArray.get(getPosition).setQuantity(getCurrentCount - 1);

                        SearchItem getItem = itemArray.get(getPosition);

                        clickItem.onClick(getItem.getQuantity(), data, i, getItem.getVariationPrice(), "minus_modifier");

                        //data.setQuantity(String.valueOf(currentNos));
                        viewHolder.editQuantity.setText(getItem.getQuantity() + "");
                        Double add = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();
                        viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", add));
                    } else {
                        Toast.makeText(mContext, "Enter valid item", Toast.LENGTH_LONG).show();
                    }

                }
            }
            });

        if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            viewHolder.imgDelete.setVisibility(View.GONE);
        } else {
            viewHolder.imgDelete.setTag(data);
            viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                if(ActivityPosItemList.comingFrom.equalsIgnoreCase("fromSaleDetail")){
//
//                }else{

                    SearchItem data = (SearchItem) v.getTag();
                    clickItem.onItemClick(i, data);

                }
            });
        }


// value upto 2 decimal points only


        viewHolder.ed_ProductPrice.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = viewHolder.ed_ProductPrice.getText().toString();
                String temp = viewHolder.ed_ProductPrice.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = viewHolder.ed_ProductPrice.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }
                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }
                }
                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});



// to make product price edittable
        viewHolder.ed_ProductPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {

                    data.setSelling_price(editable.toString().trim());

                    data.setUnitFinalPrice(String.valueOf((Double.valueOf(data.getSelling_price()) + data.getTaxCalculateAmt()) - Double.valueOf(data.getDiscountt())));

                    subTotal = (Double.valueOf(data.getUnitFinalPrice()) + data.getVariationPrice()) * data.getQuantity();


                    clickItem.onSellPriceUpdate(Double.valueOf(data.getUnitFinalPrice()), i);

                    viewHolder.txtProductSubtoralPrice.setText(objBusiness.getCurrency_code() + " " + String.format("%.2f", subTotal));
                }

            }
        });

    }


    @Override
    public int getItemCount() {

        return itemArray.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Dialog dialog;
        ImageView imgProduct, imgDelete,imgOptionsEdit;
        TextView txtProductName, txtProductSubtoralPrice, tv_mvariations, editQuantity, txt_CurrencyType;
        Spinner spnTax, spnDiscountType;
        EditText edtTaxCalculation, edtDescription, ed_ProductPrice;
        EditText edtDiscountAmt;
        Button addButton, minusButton;
        View view;
        ExpandableListView expandableListView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgProduct = (ImageView) itemView.findViewById(R.id.imgProduct);
            imgOptionsEdit = (ImageView) itemView.findViewById(R.id.imgOptionsEdit);
            imgDelete = (ImageView) itemView.findViewById(R.id.imgDelete);
            spnTax = (Spinner) itemView.findViewById(R.id.spnTax); //right now product tax is not needed , can add later.
            spnDiscountType = (Spinner) itemView.findViewById(R.id.spnDiscountType);
            ed_ProductPrice = (EditText) itemView.findViewById(R.id.ed_ProductPrice);
            txtProductName = (TextView) itemView.findViewById(R.id.txtProductName);
            txtProductSubtoralPrice = (TextView) itemView.findViewById(R.id.txtProductSubtoralPrice);
            editQuantity = (TextView) itemView.findViewById(R.id.editQuantity);
            edtTaxCalculation = (EditText) itemView.findViewById(R.id.edtTaxCalculation);
            edtDescription = (EditText) itemView.findViewById(R.id.edtDescription);
            edtDiscountAmt = (EditText) itemView.findViewById(R.id.edtDiscountAmt);
            addButton = (Button) itemView.findViewById(R.id.addButton);
            minusButton = (Button) itemView.findViewById(R.id.minusButton);
            tv_mvariations = (TextView) itemView.findViewById(R.id.tv_mvariations);
            txt_CurrencyType = (TextView) itemView.findViewById(R.id.txt_CurrencyType);

            dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_modifierset);

            expandableListView = dialog.findViewById(R.id.expandableListView);

//            sqLiteDatabase = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
//
//            String CREATE_TABLEE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALL_MODIFIERS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + MODIFIERID + " VARCHAR," + MODIFIERNAME + " VARCHAR," + VARIATIONID + " VARCHAR," +
//                    VARIATIONNAME + " VARCHAR," + VARIATIONPRICE + " VARCHAR," +
//                    PRODUCTID + " VARCHAR" + ")";
//            sqLiteDatabase.execSQL(CREATE_TABLEE);
//
//            String TABLE_MODIFIERNAME = "CREATE TABLE IF NOT EXISTS " + TABLE_MODIFIER_NAME +
//                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + MODIFIERNAME + " VARCHAR," + MODIFIERID + " VARCHAR," +
//                    PRODUCTID + " VARCHAR" + ")";
//            sqLiteDatabase.execSQL(TABLE_MODIFIERNAME);
//
//           /* int pos = edtDiscountAmt.getText().length();
//            edtDiscountAmt.setSelection(pos);*/
        }

    }


    // doalig for selecting discaount tax. etc.
    public void openOptionsDialog(SearchItem data,EditText edtDiscountAmt,Spinner spnDiscountType) {
        final Dialog optionDialog = new Dialog(mContext);
        optionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        optionDialog.setContentView(R.layout.options_select_discount);
        optionDialog.setCancelable(false);
        Window window = optionDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView txt_title = optionDialog.findViewById(R.id.txt_title);
        TextView txt_close = optionDialog.findViewById(R.id.txt_close);
        Spinner spnDiscountTypeDialog = optionDialog.findViewById(R.id.spnDiscountType);
        EditText edtDiscountAmtDialog = optionDialog.findViewById(R.id.edtDiscountAmt);
        ImageView img_cancel_dialog = optionDialog.findViewById(R.id.img_cancel_dialog);

        txt_title.setText(data.getName());
        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, discountType);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnDiscountTypeDialog.setAdapter(spinnerAdapter);


        String strExistDiscountType = "";

        //  check if a product already have a discount
        if (data.discount != null) {

            if (!data.discount.getDiscount_type().equalsIgnoreCase("")) {
                strExistDiscountType = data.discount.getDiscount_type();

                if (strExistDiscountType.equalsIgnoreCase("fixed")) {
                    strExistDiscountType = "Fixed";
                } else if (strExistDiscountType.equalsIgnoreCase("percentage")) {
                    strExistDiscountType = "Percentage";
                }

                edtDiscountAmtDialog.setText(data.discount.getDiscount_amount());
            }
        } else {
            if (data.getDiscountType().equalsIgnoreCase("fixed")) {
                strExistDiscountType = "Fixed";

                if (!data.getDiscountt().equalsIgnoreCase("")) {
                    edtDiscountAmtDialog.setText(data.getDiscountt());
                }


            } else if (data.getDiscountType().equalsIgnoreCase("percentage")) {
                strExistDiscountType = "Percentage";
                if(from.equalsIgnoreCase("fromSaleDetail")){
                    if (!data.getDiscountAmt().equalsIgnoreCase("")) {
//                viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                        edtDiscountAmtDialog.setText(data.getDiscountAmt());
                    }

                }else{
                    if (!data.getDiscountAmt().equalsIgnoreCase("")) {
//                viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
                        edtDiscountAmtDialog.setText(data.getDiscountAmt());
                    }

                }
//
//                if (!data.getDiscountAmt().equalsIgnoreCase("")) {
////                viewHolder.edtDiscountAmt.setText(data.getDiscountAmt());
//                    edtDiscountAmt.setText(data.getDiscountAmt());
//                }
            }

        }


        final int disPosition = spinnerAdapter.getPosition(strExistDiscountType);
        spnDiscountTypeDialog.setSelection(disPosition);
        if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            spnDiscountTypeDialog.setEnabled(false);
        }
        spnDiscountTypeDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spnDiscountType.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            edtDiscountAmtDialog.setEnabled(false);
        } else {
            edtDiscountAmtDialog.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    edtDiscountAmt.setText(editable.toString());

                }
            });


            optionDialog.show();

        }
    }


}
