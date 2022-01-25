package com.pos.salon.activity.SupplierActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.SupplierAdapter.SupplierListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.listModel.SupplierListModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.ActivityProduct.ProductList.hideSoftKeyboard;

public class SupplierSection extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView txt_no_resut;
    private RecyclerView list_supplier_recycler;
    private ProgressBar progressBar;
    private ImageView img_add_supplier,img_clear_search;
    private EditText et_search_customer;
    private LinearLayoutManager mLayoutManager;
    private SupplierListAdapter supplierListAdapter;
    ArrayList<SupplierListModel> arrSupplierList = new ArrayList<>();
    private boolean isScrolling = true;
    private int currentItems, totalItems, scrollItems;
    private String quaryText = "";
    @SuppressLint("StaticFieldLeak")
    public static Activity supplierSectionActivity;
    public boolean isSupplierView=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_section);

        supplierSectionActivity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list_supplier_recycler=findViewById(R.id.list_supplier_recycler);
        progressBar=findViewById(R.id.progressBar);
        txt_no_resut=findViewById(R.id.txt_no_resut);
        TextView title_product = findViewById(R.id.title_supplier);
        img_add_supplier=findViewById(R.id.img_add_supplier);
        et_search_customer=findViewById(R.id.et_search_customer);
        img_clear_search=findViewById(R.id.img_clear_search);

        title_product.setText("SUPPLIER LIST");


        mLayoutManager = new LinearLayoutManager(this);
        list_supplier_recycler.setLayoutManager(mLayoutManager);
        supplierListAdapter = new SupplierListAdapter(this, arrSupplierList);
        list_supplier_recycler.setAdapter(supplierListAdapter);

        setBackNavgation();
        listeners();
    }
    public void listeners(){

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isSupplierView = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("supplier.view")) {
                    isSupplierView = true;
                }

            }
        }


//        productListAdapter.setOnItmeClicked(new ProductListAdapter.OnClicked() {
//            @Override
//            public void setOnClickedItem(int position) {
//                if(HomeActivity.isSaleView==true){
//                    ProductListModel productModel = arrProductList.get(position);
//
//                    Intent i=new Intent(ProductList.this, ProductDetailActivity.class);
//                    i.putExtra("product_id", productModel.id);
//                    startActivity(i);
//
//                }else{
//                    AppConstant.showToast(ProductList.this,"You Dont Have Permission To View Sale Detail");
//                }
//            }
//        });

        supplierListAdapter.setOnItmeClicked(new SupplierListAdapter.OnClicked() {
            @java.lang.Override
            public void setOnClickedItem(int position) {
                if(isSupplierView){
                    SupplierListModel model = arrSupplierList.get(position);
                    Intent i=new Intent(SupplierSection.this, SupplierDetailActivity.class);
                    i.putExtra("contact_id", model.id);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(SupplierSection.this,"You Dont Have Permission To View Detail");
                }
            }
        });

        img_add_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SupplierSection.this,SupplierAddActivity.class);
                i.putExtra("isComing", "");
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        list_supplier_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    currentItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    scrollItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            supplierList();
                        }
                    }
                }
            }
        });


        et_search_customer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().isEmpty()){
                    img_clear_search.setVisibility(View.GONE);
                }else{
                    img_clear_search.setVisibility(View.VISIBLE);
                }

            }
        });

        img_clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(SupplierSection.this);
                et_search_customer.setText("");
                arrSupplierList.clear();
                quaryText=et_search_customer.getText().toString();
                supplierList();
            }
        });

        et_search_customer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        hideSoftKeyboard(SupplierSection.this);

                        arrSupplierList.clear();
//                    getReceiptSummary();
                        quaryText=et_search_customer.getText().toString();

                        supplierList();

                        return true; // consume.
                    }

                }
                return false; // pass on to other listeners.

            }
        });

        if(AppConstant.isNetworkAvailable(SupplierSection.this)){
            supplierList();
        }else{
            AppConstant.openInternetDialog(SupplierSection.this);
        }


    }


   public void supplierList() {
       if (isScrolling) {
           arrSupplierList.clear();
           AppConstant.showProgress(this, false);
       } else {
           progressBar.setVisibility(View.VISIBLE);
       }
       Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomersList("contacts?term=" + quaryText + "&limit=" + arrSupplierList.size()+"&type="+"supplier");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("supplier List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray dataObj = responseObject.getJSONArray("contacts");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    SupplierListModel listPosModel = new SupplierListModel();
                                    if(data.has("contact_id") && !data.isNull("contact_id")){
                                        listPosModel.contact_id=data.getString("contact_id");
                                    }
                                    if(data.has("supplier_business_name") && !data.isNull("supplier_business_name")){
                                        listPosModel.supplier_business_name=data.getString("supplier_business_name");
                                    }
                                    if(data.has("name") && !data.isNull("name")){
                                        listPosModel.name=data.getString("name");
                                    }
                                    if(data.has("mobile") && !data.isNull("mobile")){
                                        listPosModel.mobile=data.getString("mobile");
                                    }
                                    if(data.has("email") && !data.isNull("email")){
                                        listPosModel.email=data.getString("email");
                                    }
                                    if(data.has("type") && !data.isNull("type")){
                                        listPosModel.type=data.getString("type");
                                    }
                                    if(data.has("id") && !data.isNull("id")){
                                        listPosModel.id=data.getInt("id");
                                    }
                                    if(data.has("total_purchase") && !data.isNull("total_purchase")){
                                        listPosModel.total_purchase=data.getString("total_purchase");
                                    }
                                    if(data.has("purchase_paid") && !data.isNull("purchase_paid")){
                                        listPosModel.purchase_paid=data.getString("purchase_paid");
                                    }
                                    if(data.has("total_purchase_return") && !data.isNull("total_purchase_return")){
                                        listPosModel.total_purchase_return=data.getString("total_purchase_return");
                                    }
                                    if(data.has("purchase_return_paid") && !data.isNull("purchase_return_paid")){
                                        listPosModel.purchase_return_paid=data.getString("purchase_return_paid");
                                    }
                                    if(data.has("opening_balance") && !data.isNull("opening_balance")){
                                        listPosModel.opening_balance=data.getString("opening_balance");
                                    }
                                    if(data.has("opening_balance_paid") && !data.isNull("opening_balance_paid")){
                                        listPosModel.opening_balance_paid=data.getString("opening_balance_paid");
                                    }
                                    arrSupplierList.add(listPosModel);
                                    Collections.reverse(arrSupplierList);

                                }

                                if(arrSupplierList.size() ==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else {
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                supplierListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(SupplierSection.this, "contacts?term API", "(Supplier Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(SupplierSection.this, "Could Not Load Supplier List. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Excetion", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierSection.this, "Could Not Load Supplier List. Please Try Again", Toast.LENGTH_LONG).show();
                }


            });

        }

    }



    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    Intent i = new Intent(RepairListActivity.this, RepairSection.class);
//                    startActivity(i);
//                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
