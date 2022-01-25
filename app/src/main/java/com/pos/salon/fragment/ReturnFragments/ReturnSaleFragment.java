package com.pos.salon.fragment.ReturnFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.ReturnAdapters.ReturnSaleAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.fragment.SaleDetailFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.listModel.ListPosModel;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;



public class ReturnSaleFragment extends Fragment {

    private ReturnSaleAdapter adapter;
    private int iTaxPercentage = 0;
    private TextView label_returnTax, txt__return_subtotal, txt_discount_type, txt_update_sale_return, invoice_no, date, location_name, customer, et_return_Date, txt_total_return_discount, txt_total_return_tax, txt_ordertax, txt_return_Total;
    private ImageView img_date;
    private String currentDate = "", discount_type = "",transactionId="";
    private Spinner spnr_discount_type;
    private EditText et_discount, et_invoice_no;
    private double total = 0.0, dDiscountTotal = 0.0;
    public static ArrayList<ListPosModel> productList = new ArrayList<>();
    private final Calendar myCalendar = Calendar.getInstance();
    private JSONObject getRetrunUpdateResponse;
    private String currency, from = "",subFrom = "";
    private final ArrayList<String> arrdiscountType = new ArrayList<String>();
    private ListPosModel objListMOdel;
    private ArrayList<JSONObject> arrayProductlist=new ArrayList<>();
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_return_sale, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getContext().getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ReturnSaleAdapter(getContext());
        recyclerView.setAdapter(adapter);

        invoice_no = view.findViewById(R.id.txt_return_invoice_no);
        customer = view.findViewById(R.id.txt_returnCustomer_name);
        date = view.findViewById(R.id.txt_return_Date);
        location_name = view.findViewById(R.id.txt_returnLocation);
        img_date = view.findViewById(R.id.img_date);
        et_return_Date = view.findViewById(R.id.et_return_Date);
        et_discount = view.findViewById(R.id.et_discount_amount);
        txt_total_return_discount = view.findViewById(R.id.txt_total_return_discount);
        txt_total_return_tax = view.findViewById(R.id.txt_total_return_tax);
        txt_ordertax = view.findViewById(R.id.txt_ordertax);
        txt_return_Total = view.findViewById(R.id.txt_return_Total);
        txt_update_sale_return = view.findViewById(R.id.txt_update_sale_return);
        et_invoice_no = view.findViewById(R.id.et_invoice_no);
        spnr_discount_type = view.findViewById(R.id.spnr_discount_type);
        txt__return_subtotal = view.findViewById(R.id.txt__return_subtotal);
        label_returnTax = view.findViewById(R.id.label_returnTax);

        Bundle bundle = getArguments();
        transactionId = String.valueOf(bundle.getInt("transactionId"));
        from = bundle.getString("from");
        subFrom = bundle.getString("subFrom");


        arrayProductlist.clear();
        productList.clear();



        if (!bundle.get("productArraylist").toString().isEmpty() && bundle.get("productArraylist") != null) {

//            Gson gson = new Gson();
//            Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
//
//            String strMyCart = getArguments().getString("productArraylist");
//
//            arrayProductlist = (ArrayList<JSONObject>) gson.fromJson(strMyCart, type);

//            arrayProductlist = new Gson().fromJson(getArguments().getString("productArraylist"), new TypeToken<ArrayList>() {
//            }.getType());
            arrayProductlist = (ArrayList)getArguments().getSerializable("productArraylist");
//            arrayProductlist = bundle.getParcelableArrayList("productArraylist");

            for (int i = 0; i < arrayProductlist.size(); i++) {

                try {

                    JSONObject sellItem = (JSONObject) arrayProductlist.get(i);
                    ListPosModel listPosModel = new ListPosModel();
                    listPosModel.sell_line_id = sellItem.getInt("id");

                    //  listPosModel.product_name = data.getString("product_name");

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

                        //   quantity = sellItem.getInt("quantity");
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
                                        if (!objVariation.getString("name").equalsIgnoreCase("DUMMY")) {

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
                                    if (!objVariation.getString("name").equalsIgnoreCase("DUMMY")) {

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
                    listPosModel.product_name = strProductName;

                    listPosModel.unit_price = sellItem.getString("unit_price_before_discount");
                    listPosModel.quantity = sellItem.getInt("quantity");
                    listPosModel.unit_price_inc_tax = sellItem.getString("unit_price_inc_tax");
                    listPosModel.returnQuantity = "0";
                    listPosModel.returnSubtotal = "0";
                    productList.add(listPosModel);
                } catch (Exception e) {
                    Log.e("Exception : ", e.toString());
                }
//

            }
            adapter.notifyDataSetChanged();
        }


        arrdiscountType.add("Select Discount Type");

        returnSaleDetail();

        listeners();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentDate = df.format(c);
        et_return_Date.setText(currentDate);

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        arrayProductlist.clear();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void listeners() {


        et_discount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                String discountAmount = s.toString();

                if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Fixed")) {
                    if (discountAmount.isEmpty()) {
                        discountAmount = "0";
                        dDiscountTotal = 0.0;
                        txt_total_return_discount.setText(currency + "0.00");
                        calculateAllTotal();
                        return;

                    } else {
                        txt_total_return_discount.setText("-" + currency + discountAmount);
                        dDiscountTotal = Double.valueOf(discountAmount);
                        calculateAllTotal();
                    }
                } else if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Percentage")) {

                    if (discountAmount.isEmpty()) {
                        discountAmount = "0";
                        txt_total_return_discount.setText(currency + "0.00");
                        dDiscountTotal = 0.0;
                        calculateAllTotal();
                        return;
                    } else {

                        double disc = total * (Double.parseDouble(discountAmount) / 100);
                        dDiscountTotal = disc;
                        txt_total_return_discount.setText("-" + currency + disc);
                        calculateAllTotal();
                        return;

                    }
                }
            }
        });

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(getContext(), datee, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        adapter.setOnItmeClicked(new ReturnSaleAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {

                total = 0.0;
                if (productList.size() > 0) {

                    for (int i = 0; i < productList.size(); i++) {
                        objListMOdel = productList.get(i);

                        total = total + Double.parseDouble(objListMOdel.returnSubtotal);
                    }

                    dDiscountTotal = calculateDiscount();

                } else {

                    dDiscountTotal = 0.0;

                    txt_ordertax.setText("+" + currency + "0.00");
                    txt_total_return_discount.setText("-" + currency + "0.00");
                    txt_total_return_tax.setText("-" + currency + "0.00");
                    txt_return_Total.setText(currency + "0.00");
                }

                calculateAllTotal();
            }
        });

        txt_update_sale_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String returnTotal = txt__return_subtotal.getText().toString();

                if (returnTotal.isEmpty() || returnTotal.equalsIgnoreCase(currency + "0.00")) {
                    AppConstant.showToast(getContext(), "Please Select Return Quantity");
                } else {
                    returnSaleUpdateApi();
                }
            }
        });

        spnr_discount_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String discountAmount = et_discount.getText().toString();

                if (arrdiscountType.get(position).equalsIgnoreCase("Fixed")) {
                    if (discountAmount.isEmpty()) {
                        discountAmount = "0";
                        dDiscountTotal = 0.0;
                        txt_total_return_discount.setText(currency + "0.00");
                        calculateAllTotal();

                        return;

                    } else {
                        dDiscountTotal = Double.parseDouble(discountAmount);
                        txt_total_return_discount.setText("-" + currency + discountAmount);
                        calculateAllTotal();
                    }
                } else if (arrdiscountType.get(position).equalsIgnoreCase("Percentage")) {

                    if (discountAmount.isEmpty()) {
                        discountAmount = "0";
                        dDiscountTotal = 0.0;
                        txt_total_return_discount.setText(currency + "0.00");
                        calculateAllTotal();
                        return;
                    } else {

                        double disc = total * (Double.parseDouble(discountAmount) / 100);
                        dDiscountTotal = disc;

                        txt_total_return_discount.setText("-" + currency + disc);
                        calculateAllTotal();
                        return;
                    }
                } else {

                    et_discount.setText("");
                    dDiscountTotal = 0.0;

                    calculateAllTotal();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void calculateAllTotal() {

        txt__return_subtotal.setText(currency + "" + String.format("%.2f", total));

        double disamnt = total - dDiscountTotal;

        double dTotalReturnTax = (Double.valueOf(iTaxPercentage) / 100) * disamnt;

        disamnt = disamnt + dTotalReturnTax;

        txt_total_return_tax.setText("+" + currency + String.format("%.2f", dTotalReturnTax));

        txt_return_Total.setText(currency + String.format("%.2f", disamnt));
        txt_total_return_discount.setText("-" + currency + String.format("%.2f", dDiscountTotal));

    }

    public double calculateDiscount() {
        String discountAmount = et_discount.getText().toString();

        if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Fixed")) {
            if (discountAmount.isEmpty()) {
                return 0.0;
            } else {
                return Double.valueOf(discountAmount);
            }
        } else if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Percentage")) {

            if (discountAmount.isEmpty()) {
                return 0;
            } else {

                double disc = total * (Double.parseDouble(discountAmount) / 100);
                return disc;
            }
        }

        return 0.0;
    }

    public void returnSaleUpdateApi() {

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

        JSONObject mainObject = new JSONObject();
        try {

            JSONObject sell = getRetrunUpdateResponse.getJSONObject("sell");

            mainObject.put("transaction_id", String.valueOf(sell.getInt("id")));
            mainObject.put("transaction_date", et_return_Date.getText().toString());
            mainObject.put("invoice_no", et_invoice_no.getText().toString());

            JSONArray productArray = new JSONArray();

            for (int i = 0; i < productList.size(); i++) {

                ListPosModel products = productList.get(i);

                JSONObject product = new JSONObject();

                String quantityReturn = products.returnQuantity;

                if (Integer.parseInt(quantityReturn) > 0) {
                    double quantity = Double.parseDouble(quantityReturn);
                    int quanINt = (int) quantity;
                    product.put("quantity", String.valueOf(quanINt));
                    product.put("unit_price_inc_tax", products.unit_price_inc_tax);
                    product.put("sell_line_id", String.valueOf(products.sell_line_id));
                    product.put("is_defective",products.isDefective);
                    productArray.put(product);

                }
            }

            mainObject.put("products", productArray);
            mainObject.put("total_paid", "0");
            mainObject.put("return_total", txt_return_Total.getText().toString());
            mainObject.put("amount", txt_return_Total.getText().toString());
            mainObject.put("method", "cash");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.returnSaleUpdate("sell-return", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();

                            Log.e("Return Update Response", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {

                                String msg = responseObject.optString("msg");


//                                Intent i=new Intent(getContext(),HomeActivity.class);
//                                startActivity(i);
                                if (from.equalsIgnoreCase("fromReturnSaleDetailFragment")) {
                                    ReturnSaleListFragment fragment = new ReturnSaleListFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.lay_return_detail, fragment).commit();
                                    HomeActivity.tool_title.setText("RETURN SALE LIST");

                                } else {

                                    if (subFrom.equalsIgnoreCase("fromRepairSaleActivity")) {

                                    } else {

                                        ReturnSaleListFragment fragment = new ReturnSaleListFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.rl_sale_layout, fragment).commit();
                                        HomeActivity.tool_title.setText("RETURN SALE LIST");
                                    }
                                }
                                Toast.makeText(getContext(), "Sale Updated Successfully", Toast.LENGTH_LONG).show();


//                                if (activeCenterFragments.size() > 0) {
//                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                                    for (Fragment activeFragment : activeCenterFragments) {
//                                        transaction.remove(activeFragment);
//                                    }
//                                    activeCenterFragments.clear();
//                                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                    transaction.commit();
//                                }


                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(),"sell-return Update API","(ReturnSaleFragment Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Update Your Return Sale Detial. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Update Your Return Sale Detial. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    final DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        et_return_Date.setText(sdf.format(myCalendar.getTime()));
        currentDate = sdf.format(myCalendar.getTime());
    }


    public void returnSaleDetail() {

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.returnSale("sell-return/add/" + transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {

                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("Sale Detail Response", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            getRetrunUpdateResponse = responseObject;

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONObject sell = responseObject.getJSONObject("sell");

                                String invoice = sell.getString("invoice_no");
                                currency = sell.getString("currency");


                                if (sell.has("discount_type") && !sell.isNull("discount_type")) {

                                    discount_type = sell.getString("discount_type");

                                    if (discount_type.equalsIgnoreCase("fixed")) {
                                        arrdiscountType.add("Fixed");

                                    } else if (discount_type.equalsIgnoreCase("percentage")) {
                                        arrdiscountType.add("Percentage");
                                    }
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrdiscountType);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnr_discount_type.setAdapter(dataAdapter);

                                if (discount_type.equalsIgnoreCase("fixed")) {

                                    if (arrdiscountType.size() > 1) {
                                        spnr_discount_type.setSelection(1);
                                    }


                                } else if (discount_type.equalsIgnoreCase("percentage")) {

                                    if (arrdiscountType.size() > 1) {
                                        spnr_discount_type.setSelection(1);
                                    }

                                } else {
                                    spnr_discount_type.setSelection(0);
                                    dDiscountTotal = 0.0;
                                    et_discount.setText("");

                                }


                                if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {
                                    String discount_amount = sell.getString("discount_amount");
                                    if (!discount_amount.isEmpty()) {
                                        et_discount.setText(discount_amount);
                                        dDiscountTotal = Double.parseDouble(discount_amount);
                                    } else {
                                        dDiscountTotal = 0.0;
                                    }
                                }

                                if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Fixed")) {
                                    if (dDiscountTotal == 0.0) {

                                        txt_total_return_discount.setText(currency + "0.00");

                                    } else {
                                        txt_total_return_discount.setText("-" + currency + dDiscountTotal);
                                    }

                                } else if (spnr_discount_type.getSelectedItem().toString().equalsIgnoreCase("Percentage")) {

                                    if (dDiscountTotal == 0.0) {

                                        txt_total_return_discount.setText(currency + "0.00");

                                    } else {
                                        double disc = total * (dDiscountTotal / 100);
                                        dDiscountTotal = disc;
                                        txt_total_return_discount.setText("-" + currency + disc);
                                    }

                                }


                                if (sell.has("tax") && !sell.isNull("tax")) {
                                    JSONObject objTax = sell.getJSONObject("tax");

                                    label_returnTax.setText("Total Return Tax - (" + objTax.getString("name") + " - " + objTax.getInt("amount") + "%)");

                                    iTaxPercentage = objTax.getInt("amount");
                                }


                                calculateAllTotal();

                                String name="";
                                if(sell.has("contact") && !sell.isNull("contact")){
                                    JSONObject contact = sell.getJSONObject("contact");
                                    if(contact.has("name") && !contact.isNull("name")){
                                        name = contact.getString("name");
                                    }

                                }
                                String indate="";
                                if(sell.has("transaction_date") && !sell.isNull("transaction_date")){
                                     indate = sell.getString("transaction_date");
                                }
                                String location_nam="";
                                if(sell.has("location") && !sell.isNull("location")){
                                    JSONObject location = sell.getJSONObject("location");
                                    if(location.has("name") && !location.isNull("name")){
                                        location_nam = location.getString("name");
                                    }
                                }

//                                JSONArray sellLines = sell.getJSONArray("sell_lines");

//                                for (int i = 0; i < sellLines.length(); i++) {
//
//                                    JSONObject data = sellLines.getJSONObject(i);
//                                    ListPosModel listPosModel = new ListPosModel();
//                                    listPosModel.sell_line_id = data.getInt("id");
//                                    listPosModel.product_name = data.getString("product_name");
//                                    listPosModel.unit_price = data.getString("unit_price");
//                                    listPosModel.quantity = data.getInt("quantity");
//                                    listPosModel.unit_price_inc_tax = data.getString("unit_price_inc_tax");
//                                    listPosModel.returnQuantity = "0";
//                                    listPosModel.returnSubtotal = "0";
//                                    productList.add(listPosModel);
////
////                                }
//                                adapter.notifyDataSetChanged();
                                invoice_no.setText(invoice);
                                customer.setText(name);
                                if(!indate.equalsIgnoreCase("")){
                                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date newDate = null;
                                    try {
                                        newDate = spf.parse(indate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    spf = new SimpleDateFormat("dd/MM/yyyy");
                                    indate = spf.format(newDate);
                                }


                                date.setText(indate);
                                location_name.setText(location_nam);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(),"sell-return/add/  API","(ReturnSaleFragment Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}

