package com.pos.salon.activity.ActivityPurchases.AddPurchaseSection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pos.salon.R;
import com.pos.salon.activity.SupplierActivity.SupplierAddActivity;
import com.pos.salon.adapter.SpinnerAdapter;
import com.pos.salon.adapter.SpinnerSelectionAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.repairModel.SpinnerSelectionModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddPurchaseActivity extends AppCompatActivity {
    public String comingFrom = "", ref_no = "", purchaseStatusID = "";
    Toolbar toolbar;
    private final ArrayList<SpinnerSelectionModel> arrLocationList = new ArrayList<>();
    private final ArrayList<SpinnerSelectionModel> arrOrderStatusesList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arSupplierList = new ArrayList<>();
    private Spinner dropdownPurchaseStatus, dropdownlocation;
    private final SpinnerSelectionModel spinnerModel = new SpinnerSelectionModel();
    private LinearLayout lay_selectdate_calender;
    private TextView txt_date, txt_supplier;
    private final Calendar myCalendar = Calendar.getInstance();
    private RelativeLayout lay_next, lay_supplier;
    private int supplierID = 0, purchase_id = 0, location_id = 0;
    private String supplier_name = "", purchaseDate = "";
    private EditText et_Reference_No;
    @SuppressLint("StaticFieldLeak")
    public static Activity addPurchaseActivity;
    private final ArrayList arrProductList = new ArrayList();
    private SharedPreferences.Editor ed_cartSave, ed_countproduct;
    private ImageView img_add_supplier;
    SharedPreferences sp_cartSave,sp_countproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);

        addPurchaseActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dropdownPurchaseStatus = findViewById(R.id.dropdownPurchaseStatus);
        dropdownlocation = findViewById(R.id.dropdownlocation);
        lay_supplier = findViewById(R.id.lay_supplier);
        lay_selectdate_calender = findViewById(R.id.lay_selectdate_calender);
        txt_date = findViewById(R.id.txt_date);
        txt_supplier = findViewById(R.id.txt_supplier);
        lay_next = findViewById(R.id.lay_next);
        et_Reference_No = findViewById(R.id.et_Reference_No);
        img_add_supplier = findViewById(R.id.img_add_supplier);

        comingFrom = getIntent().getStringExtra("comingFrom");

        if (comingFrom.equalsIgnoreCase("fromDetail")) {
            purchase_id = getIntent().getIntExtra("purchase_id", 0);

             sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
              ed_cartSave = sp_cartSave.edit();

             sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
            ed_countproduct = sp_countproduct.edit();

            et_Reference_No.setEnabled(false);

        }

        setBackNavgation();
        listeners();

    }

    public void listeners() {

        img_add_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AddPurchaseActivity.this, SupplierAddActivity.class);
                i.putExtra("isComing", "AddPurchase");
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        lay_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierID == 0) {
                    AppConstant.showToast(AddPurchaseActivity.this, "Please Select Supplier");
                } else if (purchaseDate.equalsIgnoreCase("")) {
                    AppConstant.showToast(AddPurchaseActivity.this, "Please Select Purchase Date");
                } else if (spinnerModel.order_status_id.equalsIgnoreCase("0")) {
                    AppConstant.showToast(AddPurchaseActivity.this, "Please Select Purchase Status");
                } else if (spinnerModel.id == 0) {
                    AppConstant.showToast(AddPurchaseActivity.this, "Please Select Location");
                } else {

                    if (comingFrom.equalsIgnoreCase("fromDetail")) {
                        boolean bCheck = goToCartScreen();
                    } else {
                        if(sp_cartSave != null){
                            if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                ed_cartSave.remove("myCart");
                                ed_cartSave.commit();

                            }
                            // added cart badge here
                            ed_countproduct.remove("countt");
                            ed_countproduct.commit();
                        }

                        ref_no = et_Reference_No.getText().toString();
                        Intent i = new Intent(AddPurchaseActivity.this, PurchaseProductSearchActivity.class);
                        i.putExtra("location_id", spinnerModel.id);
                        i.putExtra("reference_no", ref_no);
                        i.putExtra("purchaseDate", purchaseDate);
                        i.putExtra("purchaseStatus", spinnerModel.order_status_id);
                        i.putExtra("supplierID", supplierID);
                        i.putExtra("comingFrom", "AddPurchaseActivity");

                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                }

            }
        });

        lay_selectdate_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPurchaseActivity.this, datee, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dropdownlocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.id = arrLocationList.get(position).id;
                } else {
                    spinnerModel.id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownPurchaseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.order_status_id = arrOrderStatusesList.get(position).order_status_id;
                } else {
                    spinnerModel.order_status_id = "0";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lay_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierID == 0) {
                    AppConstant.showToast(AddPurchaseActivity.this, "Please Select Supplier");
                } else {
                    openSupplierDialog();
                }
            }
        });


        if (comingFrom.equalsIgnoreCase("fromDetail")) {
            if (AppConstant.isNetworkAvailable(AddPurchaseActivity.this)) {
                editDetail();
            } else {
                AppConstant.openInternetDialog(AddPurchaseActivity.this);
            }
        } else {
            Date todayDate = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            purchaseDate = formatter.format(todayDate);
            txt_date.setText(purchaseDate);

            if (AppConstant.isNetworkAvailable(AddPurchaseActivity.this)) {
                salePurchaseCreateDetail();
            } else {
                AppConstant.openInternetDialog(AddPurchaseActivity.this);
            }
        }


    }

    public void openSupplierDialog() {

        final Dialog subCategoryDialog = new Dialog(this);
        subCategoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        subCategoryDialog.setContentView(R.layout.new_list_view);

        final ListView listView = subCategoryDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = subCategoryDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = subCategoryDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Supplier");

        Window window = subCategoryDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        SpinnerAdapter adapter = new SpinnerAdapter(this, arSupplierList);
        listView.setAdapter(adapter);

        adapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {
                supplierID = arSupplierList.get(position).id;
//                str_sub_category = arrDropSubCategoryList.get(position).name;
                txt_supplier.setText(arSupplierList.get(position).name);
                subCategoryDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryDialog.dismiss();
            }
        });

        subCategoryDialog.show();

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
        purchaseDate = sdf.format(myCalendar.getTime());
        txt_date.setText(sdf.format(myCalendar.getTime()));

    }


    public void salePurchaseCreateDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("purchases/create");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("create purchase ", respo);
                            String status = responseObject.optString("success");
                            if (status.equalsIgnoreCase("true")) {
                                arSupplierList.clear();
                                arrLocationList.clear();
                                arrOrderStatusesList.clear();

                                setFisrtItems();

                                JSONArray dataSupplier = responseObject.getJSONArray("suppliers");
                                for (int i = 0; i < dataSupplier.length(); i++) {
                                    JSONObject object = dataSupplier.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arSupplierList.add(model);

                                }
                                if(comingFrom.equalsIgnoreCase("AddSupplier")){
                                    supplierID = getIntent().getIntExtra("supplierID",0);
                                    for(int a=0;a<arSupplierList.size();a++){
                                        if(supplierID ==arSupplierList.get(a).id){
                                            supplier_name = arSupplierList.get(a).name;
                                            txt_supplier.setText(supplier_name);
                                        }
                                    }

                                }else{
                                    JSONObject defaultSupplier = responseObject.getJSONObject("default_supplier");
                                    supplierID = defaultSupplier.getInt("id");
                                    supplier_name = defaultSupplier.getString("name");
                                    txt_supplier.setText(supplier_name);

                                }
                                JSONArray data = responseObject.getJSONArray("business_locations");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrLocationList.add(model);

                                }

                                JSONArray orderStatusesdata = responseObject.getJSONArray("orderStatuses");
                                for (int i = 0; i < orderStatusesdata.length(); i++) {
                                    JSONObject object = orderStatusesdata.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.order_status_id = object.getString("id");
                                    model.name = object.getString("name");
                                    arrOrderStatusesList.add(model);

                                }

                                for(int i = 0; i < arrOrderStatusesList.size(); i++) {
                                    dropdownPurchaseStatus.setSelection(1);
                                    break;
                                }


//                                jobSpinnerAdapter.notifyDataSetChanged();
                            }

                        } else {

                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddPurchaseActivity.this, "purchases/create API", "(AddPurchaseActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddPurchaseActivity.this, "Could Not Create Purchase. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddPurchaseActivity.this, "Could Not Create purchases. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void editDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("purchases/" + purchase_id + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("edit purchase ", respo);
                            String status = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            if (status.equalsIgnoreCase("true")) {

                                arSupplierList.clear();
                                arrLocationList.clear();
                                arrOrderStatusesList.clear();
                                arrProductList.clear();

                                JSONObject purchaseObj = responseObject.getJSONObject("purchase");
                                ref_no = purchaseObj.getString("ref_no");
                                purchaseStatusID = purchaseObj.getString("status");
                                location_id = purchaseObj.getInt("location_id");

                                et_Reference_No.setText(ref_no);
                                purchaseDate = purchaseObj.getString("transaction_date");

                                JSONObject contact = purchaseObj.getJSONObject("contact");
                                supplierID = contact.getInt("id");
                                supplier_name = contact.getString("name");

                                txt_supplier.setText(supplier_name);

                                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date newDate = null;
                                try {
                                    newDate = spf.parse(purchaseDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                spf = new SimpleDateFormat("MM/dd/yyyy");
                                purchaseDate = spf.format(newDate);

                                txt_date.setText(purchaseDate);

                                if (purchaseObj.has("purchase_lines") && !purchaseObj.isNull("purchase_lines")) {
                                    JSONArray sell_lines = purchaseObj.getJSONArray("purchase_lines");
                                    for (int i = 0; i < sell_lines.length(); i++) {
                                        arrProductList.add(sell_lines.getJSONObject(i));
                                    }
                                }

                                setFisrtItems();

                                JSONArray orderStatusesdata = responseObject.getJSONArray("orderStatuses");
                                for (int i = 0; i < orderStatusesdata.length(); i++) {
                                    JSONObject object = orderStatusesdata.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.order_status_id = object.getString("id");
                                    model.name = object.getString("name");
                                    arrOrderStatusesList.add(model);

                                }
                                JSONArray data = responseObject.getJSONArray("business_locations");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrLocationList.add(model);

                                }
                                JSONArray dataSupplier = responseObject.getJSONArray("suppliers");
                                for (int i = 0; i < dataSupplier.length(); i++) {
                                    JSONObject object = dataSupplier.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arSupplierList.add(model);

                                }
//                                JSONObject defaultSupplier = responseObject.getJSONObject("default_supplier");
//                                SpinnerSelectionModel supplierModel = new SpinnerSelectionModel();
//                                supplierID=defaultSupplier.getInt("id");
//                                supplier_name=defaultSupplier.getString("name");
//
//
                            }else {
                                onBackPressed();
                                finish();
                                AppConstant.showToast(AddPurchaseActivity.this,""+msg);
                            }

                            for (int i = 0; i < arrLocationList.size(); i++) {
                                if (location_id == arrLocationList.get(i).id) {
                                    dropdownlocation.setSelection(i);
                                    break;
                                }
                            }

                            for (int i = 0; i < arrOrderStatusesList.size(); i++) {
                                if (purchaseStatusID.equalsIgnoreCase(arrOrderStatusesList.get(i).order_status_id)) {
                                    dropdownPurchaseStatus.setSelection(i);
                                    break;
                                }
                            }


                        } else {

                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddPurchaseActivity.this, "purchases/create API", "(AddPurchaseActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddPurchaseActivity.this, "Could Not Create Purchase. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddPurchaseActivity.this, "Could Not Create purchases. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }


    public void setFisrtItems() {

        SpinnerSelectionModel spinnerSelectionModel = new SpinnerSelectionModel();
        spinnerSelectionModel.id = 0;
        spinnerSelectionModel.setName("Select Location");
        arrLocationList.add(0, spinnerSelectionModel);

        SpinnerSelectionModel spinnerUnitModel = new SpinnerSelectionModel();
        spinnerUnitModel.order_status_id = "0";
        spinnerUnitModel.setName("Select Purchase Status");
        arrOrderStatusesList.add(0, spinnerUnitModel);



        setAdpters();

    }

    public void setAdpters() {

        SpinnerSelectionAdapter locationAdapter = new SpinnerSelectionAdapter(this, arrLocationList);
        dropdownlocation.setAdapter(locationAdapter);

        SpinnerSelectionAdapter purchaseAdapter = new SpinnerSelectionAdapter(this, arrOrderStatusesList);
        dropdownPurchaseStatus.setAdapter(purchaseAdapter);


    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (comingFrom.equalsIgnoreCase("fromDetail")) {
                getSupportActionBar().setTitle("EDIT PURCHASE");
            } else {
                getSupportActionBar().setTitle("ADD PURCHASE");
            }


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
                        PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
                    }
                    if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
                        PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
                    }
                    if (PurchaseProductSearchList.purchaseProductSearchList != null) {
                        PurchaseProductSearchList.purchaseProductSearchList.finish();
                    }

                   onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
            PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
        }
        if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
            PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
        }
        if (PurchaseProductSearchList.purchaseProductSearchList != null) {
            PurchaseProductSearchList.purchaseProductSearchList.finish();
        }

        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public boolean goToCartScreen() {
        try {
            //create cart products array.
            ArrayList<PurchaseProductSearchModel> arrCartProducs = new ArrayList<>();
            if (arrProductList.size() > 0) {
                for (int n = 0; n < arrProductList.size(); n++) {
                    JSONObject objSellLine = (JSONObject) arrProductList.get(n);

                    JSONObject objProduct = objSellLine.getJSONObject("product");
                    JSONObject objvariations = objSellLine.getJSONObject("variations");

                    JSONObject product_variation = objvariations.getJSONObject("product_variation");

                    PurchaseProductSearchModel objNewSearch = new PurchaseProductSearchModel();
                    objNewSearch.setActiveProduct(true);
                    objNewSearch.setProduct_id(objSellLine.getInt("product_id"));
                    objNewSearch.setVariation_id(objSellLine.getInt("variation_id"));
                    objNewSearch.setQuantity(objSellLine.getInt("quantity"));
                    objNewSearch.setDiscount_percent((objSellLine.getString("discount_percent")));
                    objNewSearch.setName(objProduct.getString("name"));
                    objNewSearch.setProfit_percent(objvariations.getString("profit_percent"));
                    objNewSearch.setDefault_purchase_price(objSellLine.getString("pp_without_discount"));
                    objNewSearch.setUnitFinalPrice(objvariations.getString("default_sell_price"));
                    objNewSearch.setUnitCostBeforTax(objSellLine.getString("purchase_price_inc_tax"));
                    objNewSearch.setVariation_name(product_variation.getString("name"));
                    objNewSearch.setSub_sku(objvariations.getString("sub_sku"));

                    if(objvariations.has("color") && !objvariations.isNull("color")){
                        JSONObject objColor = objvariations.getJSONObject("color");
                        if (objColor.has("name") && !objColor.isNull("name")) {
                            objNewSearch.setColor(objColor.getString("name"));
                        }
                    }

                    if (objProduct.has("style_no") && !objProduct.isNull("style_no")) {
                        objNewSearch.setStyle_no(objProduct.getString("style_no"));
                    }
                    if (objProduct.has("type") && !objProduct.isNull("type")) {
                        objNewSearch.setType(objProduct.getString("type"));
                    }
                    if (objvariations.has("name") && !objvariations.isNull("name")) {
                        objNewSearch.setVariation(objvariations.getString("name"));
                    }


                    double linetotal2 = Double.parseDouble(objNewSearch.getUnitCostBeforTax()) * objNewSearch.getQuantity();
                    objNewSearch.setLineTotal(String.valueOf(linetotal2));


//                    product.purchase_price = objItem.getUnitCostBeforTax();
//                    product.purchase_line_tax_id ="";
//                    product.purchase_price_inc_tax =objItem.getUnitCostBeforTax();
//                    product.item_tax ="0.00";
//                    product.default_sell_price = objItem.getUnitFinalPrice();
//                    objNewSearch.setSellLineNote(objSellLine.getString("sell_line_note"));
//
//                        objNewSearch.setDiscountt(objSellLine.getString("line_discount_amount"));
//                        Double dDiscountAmt = Double.valueOf(objSellLine.getString("line_discount_amount"));
//                        Double dPercentageDis = (dDiscountAmt * 100) / Double.valueOf(objSellLine.getString("unit_price_before_discount"));
//                        objNewSearch.setDiscountAmt(String.valueOf(dPercentageDis));
//
//
//
//                    objNewSearch.setFinalPrice(String.valueOf(objSellLine.getInt("quantity") * Double.valueOf(objSellLine.getString("unit_price"))));
//                    objNewSearch.setQuantity(objSellLine.getInt("quantity"));
//
//
//
//                    objNewSearch.setTaxCalculationDone(false);
//                    objNewSearch.setUnitFinalPrice(objSellLine.getString("unit_price_inc_tax"));
//
//                    Double dTotalVariationPrice = 0.0;
//                    String strSelectedVariationName = "";
//
//                    objNewSearch.setVariationPrice(dTotalVariationPrice);
//
//
//                    objNewSearch.setVariation_name(strSelectedVariationName);
//
//
                    arrCartProducs.add(objNewSearch);

                }

            } else {
                Toast.makeText(AddPurchaseActivity.this, "Incomplete Sale Detail. Can't Go For Edit", Toast.LENGTH_LONG).show();
                return true;
            }

            //set cart count.
            ed_countproduct.putString("countt", String.valueOf(arrCartProducs.size()));
            ed_countproduct.commit();

//            JSONObject sell = getPurchaseDetailResponse.getJSONObject("");
//                        showEditDialog();
            Intent intent = new Intent(AddPurchaseActivity.this, PurchaseProductSearchList.class);

            intent.putExtra("location_id", spinnerModel.id);
            intent.putExtra("reference_no", et_Reference_No.getText().toString());
            intent.putExtra("purchaseDate", purchaseDate);
            intent.putExtra("purchaseStatus", spinnerModel.order_status_id);
            intent.putExtra("supplierID", supplierID);
            intent.putExtra("comingFrom", "fromDetail");
            intent.putExtra("purchase_id", purchase_id);
//            intent.putExtra("transactionId", String.valueOf(transactionId));


            Gson gson = new Gson();
            //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
            ed_cartSave.putString("myCart", gson.toJson(arrCartProducs));
            ed_cartSave.commit();

            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }


}
