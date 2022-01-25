package com.pos.salon.activity.ActivityProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.ProductSectionAdapters.ProductDetaiLocationVarAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.ProductModel.ProductDetailModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView open_action;
    int product_id = 0;
    TextView txt_product_name, txt_sku, txt_product_brand, txt_product_unit, txt_barcode_type, txt_category, txt_Sub_category;
    TextView txt_Alert_quantity, txt_Manage_Stock, txt_Expires_in, txt_ApplicableTax, txt_Selling_Price_type, txt_Product_Type;
    ArrayList<ProductDetailModel> arrProductLocVarList=new ArrayList<>();
    ProductDetaiLocationVarAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static Activity productDetailActivity;
    RecyclerView recycler_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productDetailActivity=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        product_id = getIntent().getIntExtra("product_id", 0);

        open_action = findViewById(R.id.open_action);
        txt_product_name = findViewById(R.id.txt_product_name);
        txt_sku = findViewById(R.id.txt_sku);
        txt_product_brand = findViewById(R.id.txt_product_brand);
        txt_product_unit = findViewById(R.id.txt_product_unit);
        txt_barcode_type = findViewById(R.id.txt_barcode_type);
        txt_category = findViewById(R.id.txt_category);
        txt_Sub_category = findViewById(R.id.txt_Sub_category);
        txt_Alert_quantity = findViewById(R.id.txt_Alert_quantity);
        txt_Manage_Stock = findViewById(R.id.txt_Manage_Stock);
        txt_Expires_in = findViewById(R.id.txt_Expires_in);
        txt_ApplicableTax = findViewById(R.id.txt_ApplicableTax);
        txt_Selling_Price_type = findViewById(R.id.txt_Selling_Price_type);
        txt_Product_Type = findViewById(R.id.txt_Product_Type);
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setNestedScrollingEnabled(false);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        adapter = new ProductDetaiLocationVarAdapter(this, arrProductLocVarList);
        recycler_view.setAdapter(adapter);


        setBackNavgation();
        listeners();
    }

    public void listeners() {

        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        productDetail();
    }

    private void showPopup(View view) {


        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }
        boolean isProductUpdate=false;
        boolean isProductDelete=false;

        if (permissionsDataList.isEmpty()) {

            isProductUpdate = true;
            isProductDelete = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("product.update")) {
                    isProductUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("product.delete")) {
                    isProductDelete = true;
                }

            }
        }

        PopupMenu popup = new PopupMenu(ProductDetailActivity.this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.menu_product_items, popup.getMenu());

        if(isProductUpdate) {
            popup.getMenu().findItem(R.id.product_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.product_edit).setVisible(false);
        }
        if(isProductDelete) {
            popup.getMenu().findItem(R.id.product_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.product_delete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.product_edit:
                        Intent intent = new Intent(ProductDetailActivity.this, AddProduct.class);
                        intent.putExtra("comingFrom", "fromProductDetail");
                        intent.putExtra("product_id",product_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                        case R.id.product_delete:
                            deletePurchase();
                        break;

                }
                return true;
            }
        });
        popup.show();

    }
    public void deletePurchase() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductDetailActivity.this);
        builder1.setMessage("Are you sure you want to Delete this Product?");
        builder1.setTitle("Delete Product");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                delete();

            }
        });

        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }
    public void delete() {
        AppConstant.showProgress(ProductDetailActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteCategory("products/" + product_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("delete product", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                if (ProductList.productListActivity != null) {
                                    ProductList.productListActivity.finish();
                                }
                                if (ProductDetailActivity.productDetailActivity != null) {
                                    ProductDetailActivity.productDetailActivity.finish();
                                }

                                Intent i = new Intent(ProductDetailActivity.this, ProductList.class);
                                startActivity(i);

                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ProductDetailActivity.this, "products/id delete API", "(Product)", "Web API Error : API Response Is Null");
                            Toast.makeText(ProductDetailActivity.this, "Could Not Delete Product . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ProductDetailActivity.this, "Could Not Delete Product . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


    public void productDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        Log.e("product_id", String.valueOf(product_id));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getRepairDetail("products/view/" + product_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("respo",respo.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONObject productObj = responseObject.getJSONObject("product");
                                product_id = productObj.getInt("id");

                                if (productObj.has("name") && !productObj.isNull("name")) {
                                    txt_product_name.setText(productObj.getString("name"));
                                }

                                if (productObj.has("sku") && !productObj.isNull("sku")) {
                                    txt_sku.setText(productObj.getString("sku"));
                                }
                                if (productObj.has("tax_type") && !productObj.isNull("tax_type")) {
                                    txt_Selling_Price_type.setText(productObj.getString("tax_type"));
                                }
                                if (productObj.has("type") && !productObj.isNull("type")) {
                                    txt_Product_Type.setText(productObj.getString("type"));
                                }
                                if (productObj.has("alert_quantity") && !productObj.isNull("alert_quantity")) {
                                    txt_Alert_quantity.setText(String.valueOf(productObj.getInt("alert_quantity")));
                                }
                                if (productObj.has("barcode_type") && !productObj.isNull("barcode_type")) {
                                    txt_barcode_type.setText(productObj.getString("barcode_type"));
                                }
                                if (productObj.has("expiry_period") && !productObj.isNull("expiry_period")) {
                                    txt_Expires_in.setText(productObj.getString("expiry_period"));
                                }else{
                                    txt_Expires_in.setText("Not Applicable");
                                }
                                if (productObj.has("enable_stock") && !productObj.isNull("enable_stock")) {

                                    if(productObj.getInt("enable_stock") ==1){
                                        txt_Manage_Stock.setText("Yes");
                                    }else {
                                        txt_Manage_Stock.setText("No");
                                    }

                                }

                                if(productObj.has("brand") && !productObj.isNull("brand")){
                                    JSONObject brandObj = productObj.getJSONObject("brand");
                                    if (brandObj.has("name") && !brandObj.isNull("name")) {
                                        txt_product_brand.setText(brandObj.getString("name"));
                                    }
                                }
                                if(productObj.has("unit") && !productObj.isNull("unit")){
                                    JSONObject unitObj = productObj.getJSONObject("unit");
                                    if (unitObj.has("short_name") && !unitObj.isNull("short_name")) {
                                        txt_product_unit.setText(unitObj.getString("short_name"));
                                    }
                                }
                                if(productObj.has("category") && !productObj.isNull("category")){
                                    JSONObject categoryObj = productObj.getJSONObject("category");
                                    if (categoryObj.has("name") && !categoryObj.isNull("name")) {
                                        txt_category.setText(categoryObj.getString("name"));
                                    }
                                }

                                if(productObj.has("sub_category") && !productObj.isNull("sub_category")){
                                JSONObject sub_categoryObj = productObj.getJSONObject("sub_category");
                                if (sub_categoryObj.has("name") && !sub_categoryObj.isNull("name")) {
                                    txt_Sub_category.setText(sub_categoryObj.getString("name"));
                                }
                                }

                                JSONArray locationsObj=responseObject.getJSONArray("locations");
                                    for(int i=0 ; i<locationsObj.length();i++){
                                        ProductDetailModel model=new ProductDetailModel();
                                        JSONObject data = locationsObj.getJSONObject(i);
//                                        model.location_name=data.getString("name");
                                        JSONArray variationsObj=productObj.getJSONArray("variations");
                                        for(int j=0 ; j<variationsObj.length();j++) {
                                            JSONObject dataVar = variationsObj.getJSONObject(j);
                                            if (data.has("name") && !data.isNull("name")) {
                                                model.location_name = data.getString("name");
                                            }
                                            if (dataVar.has("name") && !dataVar.isNull("name")) {
                                                model.variation_name = dataVar.getString("name");
                                            }
                                            if (dataVar.has("default_purchase_price") && !dataVar.isNull("default_purchase_price")) {
                                                model.default_purchase_price = dataVar.getString("default_purchase_price");
                                            }
                                            if (dataVar.has("dpp_inc_tax") && !dataVar.isNull("dpp_inc_tax")) {
                                                model.dpp_inc_tax = dataVar.getString("dpp_inc_tax");
                                            }
                                            if (dataVar.has("profit_percent") && !dataVar.isNull("profit_percent")) {
                                                model.profit_percent = dataVar.getString("profit_percent");
                                            }
                                            if (dataVar.has("default_sell_price") && !dataVar.isNull("default_sell_price")) {
                                                model.default_sell_price = dataVar.getString("default_sell_price");
                                            }
                                            if (dataVar.has("sell_price_inc_tax") && !dataVar.isNull("sell_price_inc_tax")) {
                                                model.sell_price_inc_tax = dataVar.getString("sell_price_inc_tax");
                                            }

                                            if (dataVar.has("product_variation") && !dataVar.isNull("product_variation")){
                                                JSONObject product_variationsObj = dataVar.getJSONObject("product_variation");
                                            if (product_variationsObj.has("name") && !product_variationsObj.isNull("name")) {
                                                model.product_variation_name = product_variationsObj.getString("name");
                                            }
                                        }
                                            if(dataVar.has("color") && !dataVar.isNull("color")){
                                                JSONObject colorObj=dataVar.getJSONObject("color");
                                                if(colorObj.has("name") && !colorObj.isNull("name")){
                                                    model.color_name=colorObj.getString("name");
                                                }
                                            }

                                            arrProductLocVarList.add(model);

                                            model=new ProductDetailModel();
                                        }

                                    }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ProductDetailActivity.this, "product/view API", "(ProductDetailActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ProductDetailActivity.this, "Could Not Load Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }


    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("PRODUCT DETAIL");
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
