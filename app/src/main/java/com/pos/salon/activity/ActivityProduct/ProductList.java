package com.pos.salon.activity.ActivityProduct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.ProductSectionAdapters.ProductListAdapter;
import com.pos.salon.adapter.SpinnerAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.ProductModel.ProductListModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView list_product_recycler;
    ProgressBar progressBar;
    private boolean isScrolling = true;
    LinearLayoutManager layoutManager;
    int currentItems, totalItems, scrollItems;
    TextView txt_no_resut,title_product;
    ArrayList<ProductListModel> arrProductList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arrGetCategoryList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arrGetbrandsList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arrGetunitsList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arrGetProductTypeList = new ArrayList<>();
    ProductListAdapter productListAdapter;
    ImageView img_filter_product,img_clear_po_search;
    int category_id=0,unit_id=0,brand_id=0;
    String product_type="",category_name="ALL",type_name="ALL",brand_name="ALL",unit_name="ALL",searchText="";
    TextView txt_dialog_category,txt_dialog_type,txt_dialog_brand,txt_dialog_units;
    EditText et_search_product;
    @SuppressLint("StaticFieldLeak")
    public static Activity productListActivity;
    public boolean isProductView=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        productListActivity=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list_product_recycler=findViewById(R.id.list_product_recycler);
        progressBar=findViewById(R.id.progressBar);
        txt_no_resut=findViewById(R.id.txt_no_resut);
        title_product=findViewById(R.id.title_product);
        img_filter_product=findViewById(R.id.img_filter_product);
        et_search_product=findViewById(R.id.et_search_product);
        img_clear_po_search=findViewById(R.id.img_clear_po_search);

        title_product.setText("PRODUCT LIST");


        layoutManager = new LinearLayoutManager(this);
        list_product_recycler.setLayoutManager(layoutManager);
        productListAdapter = new ProductListAdapter(this, arrProductList);
        list_product_recycler.setAdapter(productListAdapter);

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

            isProductView = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("product.view")) {
                    isProductView = true;
                }

            }
        }


        productListAdapter.setOnItmeClicked(new ProductListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                if(isProductView){
                    ProductListModel productModel = arrProductList.get(position);
                    Intent i=new Intent(ProductList.this, ProductDetailActivity.class);
                    i.putExtra("product_id", productModel.id);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(ProductList.this,"You Dont Have Permission To View Detail");
                }
            }
        });


        list_product_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollItems = layoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            productList();
                        }
                    }
                }
            }
        });


        et_search_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().isEmpty()){
                    img_clear_po_search.setVisibility(View.GONE);
                }else{
                    img_clear_po_search.setVisibility(View.VISIBLE);
                }
            }
        });

        img_clear_po_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(ProductList.this);

//                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                et_search_product.setText("");
                arrProductList.clear();
                searchText=et_search_product.getText().toString();
                productList();
            }
        });

        et_search_product.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        hideSoftKeyboard(ProductList.this);

                        arrProductList.clear();
//                    getReceiptSummary();
                        searchText=et_search_product.getText().toString();

                        productList();

                        return true; // consume.
                    }

                }
                return false; // pass on to other listeners.

            }
        });


        img_filter_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForFilters();
            }
        });
        if(AppConstant.isNetworkAvailable(ProductList.this)){
            productList();
        }else{
            AppConstant.openInternetDialog(ProductList.this);
        }

    }

    public void openDialogForFilters() {

     Dialog filterDialog = new Dialog(this);
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.product_filter_dialog);
        filterDialog.setCancelable(false);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        RelativeLayout lay_category=filterDialog.findViewById(R.id.lay_category);
        RelativeLayout lay_product_types=filterDialog.findViewById(R.id.lay_product_types);
        RelativeLayout lay_brand=filterDialog.findViewById(R.id.lay_brand);
        RelativeLayout lay_units=filterDialog.findViewById(R.id.lay_units);
        ImageView img_cancel_dialog=filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_apply_changes=filterDialog.findViewById(R.id.linear_apply_changes);

         txt_dialog_category=filterDialog.findViewById(R.id.txt_dialog_category);
         txt_dialog_type=filterDialog.findViewById(R.id.txt_dialog_type);
         txt_dialog_brand=filterDialog.findViewById(R.id.txt_dialog_brand);
        txt_dialog_units=filterDialog.findViewById(R.id.txt_dialog_units);

        txt_dialog_category.setText(category_name);
        txt_dialog_type.setText(type_name);
        txt_dialog_brand.setText(brand_name);
        txt_dialog_units.setText(unit_name);

        lay_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCategoryDialog();
            }
        });


        lay_product_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openProductTypeDialog();
            }
        });

        lay_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openBrandDialog();
            }
        });
        lay_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUnitDialog();
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                isScrolling=true;
                productList();
            }
        });

        filterDialog.show();
    }

    public void openCategoryDialog() {

        final Dialog companyDialog = new Dialog(this);
        companyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        companyDialog.setContentView(R.layout.new_list_view);

        final ListView listView = companyDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = companyDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = companyDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Category");

        Window window = companyDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        companyDialog.show();
        SpinnerAdapter projectTypeAdapter = new SpinnerAdapter(this, arrGetCategoryList);
        listView.setAdapter(projectTypeAdapter);

        projectTypeAdapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {

                category_id=arrGetCategoryList.get(position).id;
                category_name=arrGetCategoryList.get(position).name;
                txt_dialog_category.setText(arrGetCategoryList.get(position).name);
                companyDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyDialog.dismiss();
            }
        });

        companyDialog.show();

    }

    public void openProductTypeDialog() {

        final Dialog companyDialog = new Dialog(this);
        companyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        companyDialog.setContentView(R.layout.new_list_view);

        final ListView listView = companyDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = companyDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = companyDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Product Type");


        Window window = companyDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        companyDialog.show();
        arrGetProductTypeList.clear();
        SpinnerModel spinnerModel=new SpinnerModel();
        spinnerModel.name="ALL";
        arrGetProductTypeList.add(spinnerModel);
        SpinnerModel spinnerModel2=new SpinnerModel();
        spinnerModel2.name="Single";
        arrGetProductTypeList.add(spinnerModel2);
        SpinnerModel spinnerModel3=new SpinnerModel();
        spinnerModel3.name="Variable";
        arrGetProductTypeList.add(spinnerModel3);



        SpinnerAdapter projectTypeAdapter = new SpinnerAdapter(this, arrGetProductTypeList);
        listView.setAdapter(projectTypeAdapter);

        projectTypeAdapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {
                if(arrGetProductTypeList.get(position).name.equalsIgnoreCase("ALL")){
                    product_type="";
                }else{
                    product_type=arrGetProductTypeList.get(position).name;
                }
                type_name=arrGetProductTypeList.get(position).name;
                txt_dialog_type.setText(arrGetProductTypeList.get(position).name);
                companyDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyDialog.dismiss();
            }
        });

        companyDialog.show();

    }
    public void openUnitDialog() {

        final Dialog companyDialog = new Dialog(this);
        companyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        companyDialog.setContentView(R.layout.new_list_view);

        final ListView listView = companyDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = companyDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = companyDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Unit");

        Window window = companyDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        companyDialog.show();
        SpinnerAdapter projectTypeAdapter = new SpinnerAdapter(this, arrGetunitsList);
        listView.setAdapter(projectTypeAdapter);

        projectTypeAdapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {

                unit_id=arrGetunitsList.get(position).id;
                unit_name=arrGetunitsList.get(position).name;
                txt_dialog_units.setText(arrGetunitsList.get(position).name);
                companyDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyDialog.dismiss();
            }
        });

        companyDialog.show();

    }

    public void openBrandDialog() {

        final Dialog companyDialog = new Dialog(this);
        companyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        companyDialog.setContentView(R.layout.new_list_view);

        final ListView listView = companyDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = companyDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = companyDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Brand");

        Window window = companyDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        companyDialog.show();
        SpinnerAdapter projectTypeAdapter = new SpinnerAdapter(this, arrGetbrandsList);
        listView.setAdapter(projectTypeAdapter);

        projectTypeAdapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {

                brand_id=arrGetbrandsList.get(position).id;
                brand_name=arrGetbrandsList.get(position).name;
                txt_dialog_brand.setText(arrGetbrandsList.get(position).name);
                companyDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyDialog.dismiss();
            }
        });

        companyDialog.show();

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

    public void productList(){

        if (isScrolling) {
            arrProductList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("products?limit="+arrProductList.size()+"&type="+product_type+"&category_id="+category_id+"&brand_id="+brand_id+"&unit_id="+unit_id+"&term="+searchText);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            arrGetCategoryList.clear();
                            arrGetbrandsList.clear();
                            arrGetunitsList.clear();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e(" product List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();
                                if(responseObject.has("categories") && !responseObject.isNull("categories")){
                                    JSONArray categories = responseObject.getJSONArray("categories");
                                    SpinnerModel categoryModel = new SpinnerModel();
                                    categoryModel.id=0;
                                    categoryModel.name="ALL";
                                    arrGetCategoryList.add(categoryModel);

                                    for (int i = 0; i < categories.length(); i++) {
                                        JSONObject data = categories.getJSONObject(i);
                                        SpinnerModel categoryModel2 = new SpinnerModel();
                                        categoryModel2.id=data.getInt("id");
                                        categoryModel2.name=data.getString("name");
                                        arrGetCategoryList.add(categoryModel2);

                                    }
                                }
                                if(responseObject.has("brands") && !responseObject.isNull("brands")) {
                                    JSONArray brands = responseObject.getJSONArray("brands");
                                    SpinnerModel brandsModel = new SpinnerModel();
                                    brandsModel.id = 0;
                                    brandsModel.name = "ALL";
                                    arrGetbrandsList.add(brandsModel);
                                    for (int i = 0; i < brands.length(); i++) {
                                        JSONObject data = brands.getJSONObject(i);
                                        SpinnerModel brandsModel2 = new SpinnerModel();
                                        brandsModel2.id = data.getInt("id");
                                        brandsModel2.name = data.getString("name");
                                        arrGetbrandsList.add(brandsModel2);

                                    }
                                }
                                if(responseObject.has("units") && !responseObject.isNull("units")) {
                                    JSONArray units = responseObject.getJSONArray("units");
                                    SpinnerModel unitsModel = new SpinnerModel();
                                    unitsModel.id = 0;
                                    unitsModel.name = "ALL";
                                    arrGetunitsList.add(unitsModel);
                                    for (int i = 0; i < units.length(); i++) {
                                        JSONObject data = units.getJSONObject(i);
                                        SpinnerModel unitsModel2 = new SpinnerModel();
                                        unitsModel2.id = data.getInt("id");
                                        unitsModel2.name = data.getString("short_name");
                                        arrGetunitsList.add(unitsModel2);

                                    }
                                }
                                if(responseObject.has("products") && !responseObject.isNull("products")) {
                                    JSONArray dataObj = responseObject.getJSONArray("products");
                                    for (int i = 0; i < dataObj.length(); i++) {

                                        JSONObject data = dataObj.getJSONObject(i);
                                        ProductListModel productModel = new ProductListModel();
                                        productModel.id = data.getInt("id");
                                        productModel.product = data.getString("product");
                                        productModel.type = data.getString("type");


                                        if (data.has("category") && !data.isNull("category")) {
                                            productModel.category = data.getString("category");
                                        }

                                        if (data.has("sub_category") && !data.isNull("sub_category")) {
                                            productModel.sub_category = data.getString("sub_category");
                                        }
                                        if (data.has("unit") && !data.isNull("unit")) {
                                            productModel.unit = data.getString("unit");
                                        }
                                        if (data.has("enable_stock") && !data.isNull("enable_stock")) {
                                            productModel.enable_stock = data.getInt("enable_stock");
                                        }
                                        if (data.has("is_inactive") && !data.isNull("is_inactive")) {
                                            productModel.is_inactive = data.getInt("is_inactive");
                                        }
                                        if (data.has("current_stock") && !data.isNull("current_stock")) {
                                            productModel.current_stock = data.getString("current_stock");
                                        }
                                        if (data.has("max_price") && !data.isNull("max_price")) {
                                            productModel.max_price = data.getString("max_price");
                                        }

                                        if (data.has("min_price") && !data.isNull("min_price")) {
                                            productModel.min_price = data.getString("min_price");
                                        }
                                        if (data.has("sku") && !data.isNull("sku")) {
                                            productModel.sku = data.getString("sku");
                                        }
                                        if (data.has("brand") && !data.isNull("brand")) {
                                            productModel.brand = data.getString("brand");
                                        }

                                        arrProductList.add(productModel);

                                    }
                                }

                                if(arrProductList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                productListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(ProductList.this,"product list API","(ProductList)","Web API Error : API Response Is Null");
                            Toast.makeText(ProductList.this, "Could Not Load Product list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductList.this, "Could Not Load Product List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
