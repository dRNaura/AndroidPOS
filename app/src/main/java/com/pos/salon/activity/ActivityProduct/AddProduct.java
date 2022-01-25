package com.pos.salon.activity.ActivityProduct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.AddVariableProduct.VariableProductAdapter;
import com.pos.salon.adapter.CategorySearchAdapter;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.ProductsAdapters.ImageAdapter;
import com.pos.salon.adapter.ProductsAdapters.TagAdapter;
import com.pos.salon.adapter.SpinnerSelectionAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.CategoryModel;
import com.pos.salon.model.ProductModel.ImagesModel;
import com.pos.salon.model.VariableModel.AllVariationModel;
import com.pos.salon.model.VariableModel.VariationValueModel;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.repairModel.SpinnerSelectionModel;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.HomeActivity.CAMERA_REQUEST;
import static com.pos.salon.activity.HomeActivity.MY_CAMERA_PERMISSION_CODE;


public class AddProduct extends AppCompatActivity {

    Toolbar toolbar;
    EditText et_time_Required,et_product_name, et_alert_quantity, et_product_sku, et_product_description, et_selling_exc, et_margin, et_purchase_inc, et_purchase_exc;
    EditText et_product_odometer, et_product_torque, et_product_engine, et_product_doors, et_product_top_speed, et_product_horse_power, et_product_Towing;
    EditText et_min_order,et_max_order,et_style_no;
    CheckBox ch_manage_stock,ch_Enable_Restock,ch_Show_in_Website,ch_cart_button;
    Spinner  dropdownTimeWait,dropdownUnits, dropdownBarTypes, dropdownProductModel;
    Spinner dropdownProductCondition, dropdownProductTransamission, dropdownProductFuel, dropdownIntColor, dropdownExtColor, dropDown_productType;
    SpinnerSelectionModel spinnerModel = new SpinnerSelectionModel();
    private ArrayList<CategoryModel> arrbrandsList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> productModelList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrUnitList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrTimeLeftList = new ArrayList<>();
    private ArrayList<CategoryModel> arrCategoryList = new ArrayList<>();
    private ArrayList<CategoryModel> arrSubCategoryList = new ArrayList<>();
    private ArrayList<CategoryModel> arrSubSubCategoryList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrBarcodeTypeList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrCarConditionList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrTransmissionList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrFuelTypeList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrColorList = new ArrayList<>();
    private ArrayList<String> arrProductTypeList = new ArrayList<>();
    TextView txt_save_product, txt_product_image, txt_select_gallery_image, txt_select_img_product,txt_add_tags;
    LinearLayout lay_alert_quantity,lay_UsersSearch;
    double exc_inc_tax = 0.0, total_exc_tax = 0.0, margin_value = 0.0,defaultMargin=0.0;
    int inImagePosition=0,product_detail_id = 0, enable_stock = 1, product_id = 0, single_variation_id = 0, variationNO = 1, variation_temp_id = 0, variation_color_id = 0;
    int enable_restock=1,show_website=1,show_cart_button=1;
    String time_type="",alert_quantity = "0", comingFrom = "", product_type = "single", productImagePath = "", productImageExtenion = "", galleryfilename = "", galleryImagePath = "", galleryImageExtension = "";
    String variantImagePath="",variantImageExtension="",variantFileName="";
    LinearLayout lay_car_vendor, lay_variable_product, lay_product_single,lay_time_required;
    SharedPreferences sharedPreferences;
    ArrayList<String> arrdepartmentsList = new ArrayList<>();
    RecyclerView recycler_variable_product, recycler_images,recycler_tags,recycler_User;
    ImageView add_product_variation, image_product;
    VariableProductAdapter variableValueAdapter;
    ArrayList<Integer> arrVariationList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrProductTempList = new ArrayList<>();
    private ArrayList<SpinnerSelectionModel> arrProductColorList = new ArrayList<>();
    SharedPreferences sp_cartSave;
    SharedPreferences.Editor ed_cartSave;
    ArrayList<VariationValueModel> arrAllVariationValueList = new ArrayList<>();
    ArrayList<Integer> arrIntIDList = new ArrayList<>();
    ArrayList<String> arrStringNameList = new ArrayList<>();
    ArrayList<ImagesModel> arrImagesList = new ArrayList<>();
    ArrayList<ImagesModel> arrVariantImagesList = new ArrayList<>();
    ArrayList<String> arrStringTagsList=new ArrayList<>();
    ImageAdapter imageAdapter;
    TextView txtVariantImg;
    TagAdapter tagAdapter ;
    TagAdapter usersAdapter ;
    int categoryID=0,subCatID=0,subsubCatID=0,brand_id=0,checkProductType=0;
    RelativeLayout lay_product_category,lay_product_sub_category,lay_product_sub_sub_category,lay_product_brand;
    TextView txt_product_category,txt_product_sub_category,txt_product_sub_sub_category,txt_product_brand;
    CategorySearchAdapter catSearchAdapter,subCategoryAdapter,subSubCategoryAdapter,brandsAdapter;
    boolean excTaxSingle, incTaxSingle,marginSingle,sellingExcPrice;
    DelayAutoCompleteTextView et_SearchUser;
    ArrayList<CustomerListData> searchUserList = new ArrayList();
    CustomerSearchAdapter cutoCompleteCustomerAapter;
    ArrayList<String> nameUserlist = new ArrayList<>();
    ArrayList<String> nameUserIDlist = new ArrayList<>();
    private CustomerListData customerData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        comingFrom = getIntent().getStringExtra("comingFrom");
        setids();

        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();


        setBackNavgation();
    }

    public void setids() {

        ch_manage_stock = findViewById(R.id.ch_manage_stock);
        txt_save_product = findViewById(R.id.txt_save_product);
        lay_alert_quantity = findViewById(R.id.lay_alert_quantity);
        lay_car_vendor = findViewById(R.id.lay_car_vendor);

        // editetxt
        et_product_name = findViewById(R.id.et_product_name);
        et_alert_quantity = findViewById(R.id.et_alert_quantity);
        et_product_sku = findViewById(R.id.et_product_sku);
        et_product_description = findViewById(R.id.et_product_description);
        et_selling_exc = findViewById(R.id.et_selling_exc);
        et_margin = findViewById(R.id.et_margin);
        et_purchase_inc = findViewById(R.id.et_purchase_inc);
        et_purchase_exc = findViewById(R.id.et_purchase_exc);
        et_product_odometer = findViewById(R.id.et_product_odometer);
        et_product_torque = findViewById(R.id.et_product_torque);
        et_product_engine = findViewById(R.id.et_product_engine);
        et_product_doors = findViewById(R.id.et_product_doors);
        et_product_top_speed = findViewById(R.id.et_product_top_speed);
        et_product_horse_power = findViewById(R.id.et_product_horse_power);
        et_product_Towing = findViewById(R.id.et_product_Towing);
        et_min_order = findViewById(R.id.et_min_order);
        et_max_order = findViewById(R.id.et_max_order);
        et_style_no = findViewById(R.id.et_style_no);
        txt_add_tags = findViewById(R.id.txt_add_tags);

        ch_Enable_Restock = findViewById(R.id.ch_Enable_Restock);
        ch_Show_in_Website = findViewById(R.id.ch_Show_in_Website);
        ch_cart_button = findViewById(R.id.ch_cart_button);

        recycler_variable_product = findViewById(R.id.recycler_variable_product);
        recycler_tags = findViewById(R.id.recycler_tags);

        // spinners
//        dropdownBrands = findViewById(R.id.dropdownBrands);
        dropdownUnits = findViewById(R.id.dropdownUnits);
        dropdownTimeWait = findViewById(R.id.dropdownTimeWait);
//        dropdownCategory = findViewById(R.id.dropdownCategory);
//        dropdownSubCategory = findViewById(R.id.dropdownSubCategory);
//        dropdownSubSubCategory = findViewById(R.id.dropdownSubSubCategory);
        dropdownBarTypes = findViewById(R.id.dropdownBarTypes);
        dropdownProductModel = findViewById(R.id.dropdownProductModel);
        dropdownProductCondition = findViewById(R.id.dropdownProductCondition);
        dropdownProductTransamission = findViewById(R.id.dropdownProductTransamission);
        dropdownProductFuel = findViewById(R.id.dropdownProductFuel);
        dropdownIntColor = findViewById(R.id.dropdownIntColor);
        dropdownExtColor = findViewById(R.id.dropdownExtColor);
        dropDown_productType = findViewById(R.id.dropDown_productType);

        lay_variable_product = findViewById(R.id.lay_variable_product);
        lay_product_single = findViewById(R.id.lay_product_single);
        add_product_variation = findViewById(R.id.add_product_variation);

        lay_time_required=findViewById(R.id.lay_time_required);
        lay_product_category=findViewById(R.id.lay_product_category);
        lay_product_sub_category=findViewById(R.id.lay_product_sub_category);
        lay_product_sub_sub_category=findViewById(R.id.lay_product_sub_sub_category);
        lay_product_brand=findViewById(R.id.lay_product_brand);
        txt_product_brand=findViewById(R.id.txt_product_brand);

        txt_product_category=findViewById(R.id.txt_product_category);
        txt_product_sub_category=findViewById(R.id.txt_product_sub_category);
        txt_product_sub_sub_category=findViewById(R.id.txt_product_sub_sub_category);

        txt_product_image = findViewById(R.id.txt_product_image);
        txt_select_gallery_image = findViewById(R.id.txt_select_gallery_image);
        txt_select_img_product = findViewById(R.id.txt_select_img_product);
        image_product = findViewById(R.id.image_product);
        recycler_images = findViewById(R.id.recycler_images);
        recycler_User = findViewById(R.id.recycler_User);
        et_SearchUser = findViewById(R.id.et_SearchUser);
        lay_UsersSearch = findViewById(R.id.lay_UsersSearch);
        et_time_Required = findViewById(R.id.et_time_Required);


        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");
        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }
        if (arrdepartmentsList.contains("12")) {
            lay_car_vendor.setVisibility(View.VISIBLE);
        } else {
            lay_car_vendor.setVisibility(View.GONE);
        }

        arrProductTypeList = new ArrayList<String>();
        arrProductTypeList.add("Single");
        arrProductTypeList.add("Variable");

        arrVariationList.add(1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_variable_product.setLayoutManager(layoutManager);
        variableValueAdapter = new VariableProductAdapter(this, arrVariationList, arrProductTempList, arrProductColorList,arrVariantImagesList);
        recycler_variable_product.setAdapter(variableValueAdapter);


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        recycler_images.setLayoutManager(layoutManager2);
        imageAdapter = new ImageAdapter(this, arrImagesList);
        recycler_images.setAdapter(imageAdapter);

        GridLayoutManager layoutManagerTag = new GridLayoutManager(this,2);
        recycler_tags.setLayoutManager(layoutManagerTag);
        tagAdapter = new TagAdapter(this, arrStringTagsList,"");
        recycler_tags.setAdapter(tagAdapter);

        GridLayoutManager layoutManagerUser = new GridLayoutManager(this, 3);
        recycler_User.setLayoutManager(layoutManagerUser);
        usersAdapter = new TagAdapter(this, nameUserlist,"");
        recycler_User.setAdapter(usersAdapter);


        cutoCompleteCustomerAapter = new CustomerSearchAdapter(this, searchUserList);
        et_SearchUser.setAdapter(cutoCompleteCustomerAapter);
        et_SearchUser.setThreshold(1);


        et_SearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filterUser(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddProduct.this, android.R.layout.simple_spinner_item, arrProductTypeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDown_productType.setAdapter(dataAdapter);

        dropDown_productType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                if(++checkProductType > 1) {
                    if (position == 0) {
                        lay_product_single.setVisibility(View.VISIBLE);
                        lay_variable_product.setVisibility(View.GONE);

                        product_type = "single";

                        if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {//if not cart empty.

                            ed_cartSave.remove("myVariation");
                            ed_cartSave.commit();

                        }
                        arrAllVariationValueList.clear();
                        arrVariationList.clear();
                        arrVariationList.add(1);
                        variableValueAdapter.notifyDataSetChanged();
//                    variableValueAdapter.notifyItemInserted(arrVariationList.size());

//                    getProductType("single");
                    } else {

                        margin_value = defaultMargin;
                        et_margin.setText(String.format("%.2f", defaultMargin));

                        et_purchase_exc.setText("");
                        et_purchase_inc.setText("");
                        exc_inc_tax = 0.0;

                        et_selling_exc.setText("");
                        total_exc_tax = 0.0;

                        lay_product_single.setVisibility(View.GONE);
                        lay_variable_product.setVisibility(View.VISIBLE);
                        product_type = "variable";
                        getProductType();
                    }
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


//        dropdownCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if(++checkCategory > 1){
//                    if (position > 0) {
//                        spinnerModel.category_id = arrCategoryList.get(position).category_id;
//                        spinnerModel.sub_cat_id=0;
//                        spinnerModel.sub_sub_cat_id=0;
//                        getSubcategoriesList();
//                        getTags();
//
//                    } else {
//                        spinnerModel.category_id = 0;
//                    }
//                }else{
//                    spinnerModel.category_id = arrCategoryList.get(position).category_id;
//                }
//
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        dropdownSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if(++checkSubCategory > 1){
//                    if (position > 0) {
//                        spinnerModel.sub_cat_id = arrSubCategoryList.get(position).sub_cat_id;
//                        spinnerModel.sub_sub_cat_id=0;
//                        getSubSubcategoriesList();
//
//                    } else {
//                        spinnerModel.sub_cat_id = 0;
//                    }
//                }else{
//                    spinnerModel.sub_cat_id = arrSubCategoryList.get(position).sub_cat_id;
//                }
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        dropdownSubSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//              if(++checkSubSubCategory > 1){
//                if (position > 0) {
//                    spinnerModel.sub_sub_cat_id = arrSubSubCategoryList.get(position).sub_sub_cat_id;
//                    getTags();
//                } else {
//                    spinnerModel.sub_sub_cat_id = 0;
//                }
//            }else{
//                spinnerModel.sub_sub_cat_id = arrSubSubCategoryList.get(position).sub_sub_cat_id;
//            }
//
//        }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        listeners();


    }

    public void listeners() {

        if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
            product_id = getIntent().getIntExtra("product_id", 0);
            txt_save_product.setText("UPDATE");
            if (AppConstant.isNetworkAvailable(AddProduct.this)) {
                editProductDetail();
            } else {
                AppConstant.openInternetDialog(AddProduct.this);
            }

        } else {
            txt_save_product.setText("SAVE");

            if (AppConstant.isNetworkAvailable(AddProduct.this)) {

                saleProductCreateDetail();

            } else {
                AppConstant.openInternetDialog(AddProduct.this);
            }

        }

        tagAdapter.setOnRemoveClicked(new TagAdapter.RemoveTag() {
            @Override
            public void setOnClickedItem(int position) {

                deleteTags(position);

            }
        });
        usersAdapter.setOnRemoveClicked(new TagAdapter.RemoveTag() {
            @Override
            public void setOnClickedItem(int position) {

                deleteStaff(position);

            }
        });

        lay_product_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrandDialog();
            }
        });
        lay_product_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductCatDialog();
            }
        });

        lay_product_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryID == 0) {
                    AppConstant.showToast(AddProduct.this, "Please Select Category");
                } else {
                    openProductSubCategoryDialog();
                }
            }
        });
        lay_product_sub_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subCatID == 0) {
                    AppConstant.showToast(AddProduct.this, "Please Select Sub-Category");
                } else {
                    openProductSubSubCategoryDialog();
                }
            }
        });

        txt_add_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAddTagsDialog();
            }
        });

        variableValueAdapter.setOnItmeClicked(new VariableProductAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position, Map<Integer, Integer> mSpinnerSelectedItem, Map<Integer, Integer> mSelectedColorItem) {

                int temp_id = 0, color_id = 0;
                String temp_name = "", color_name = "";
                for (Map.Entry m : mSpinnerSelectedItem.entrySet()) {
                    if (m.getKey().equals(position)) {
                        temp_id = (int) m.getValue();
                        for (int a = 0; a < arrProductTempList.size(); a++) {
                            if (temp_id == arrProductTempList.get(a).id) {
                                temp_name = arrProductTempList.get(a).name;
                                break;
                            }
                        }
                        break;
                    }
                }

                for (Map.Entry m : mSelectedColorItem.entrySet()) {
                    if (m.getKey().equals(position)) {
                        color_id = (int) m.getValue();
                        for (int a = 0; a < arrProductColorList.size(); a++) {
                            if (color_id == arrProductColorList.get(a).id) {
                                color_name = arrProductColorList.get(a).name;
                                break;
                            }
                        }
                        break;
                    }
                }


                if (temp_id == 0) {
                    AppConstant.showToast(AddProduct.this, "Please Select Template");
                } else {
//
//                    VariationValueModel model = new VariationValueModel();
//                    model.variation_template_id = temp_id;
//                    model.color_id = color_id;
//                    arrAllVariationValueList.add(model);
//
//                    Gson gson = new Gson();
//                    ed_cartSave.putString("myVariation", gson.toJson(arrAllVariationValueList));
//                    ed_cartSave.commit();


                    Intent intent = new Intent(AddProduct.this, VariationValueActivity.class);
                    intent.putExtra("template_id", temp_id);
                    intent.putExtra("temp_name", temp_name);
                    if (color_id == 0) {
                        intent.putExtra("color_name", "");
                    } else {
                        intent.putExtra("color_name", color_name);
                    }
                    intent.putExtra("color_id", color_id);
                    intent.putExtra("position", position);
                    intent.putExtra("defaultMargin", defaultMargin);
                    intent.putExtra("comingTo", comingFrom);
                    startActivity(intent);

                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });

        variableValueAdapter.setOnTempClicked(new VariableProductAdapter.OnTempVariable() {
            @Override
            public void setOnClickedItem(int position, int spinnerPOstion) {
                for (int a = 0; a < arrVariationList.size(); a++) {
                    if (position == a) {
                        variation_temp_id = arrProductTempList.get(spinnerPOstion).id;
                    }
                }

            }
        });


        add_product_variation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int IntegerFormat = Integer.valueOf(1);
                arrVariationList.add(IntegerFormat);
                variableValueAdapter.notifyItemInserted(arrVariationList.size());

            }
        });

//        dropdownBrands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position > 0) {
//                    spinnerModel.brand_id = arrbrandsList.get(position).brand_id;
//                } else {
//                    spinnerModel.brand_id = 0;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        dropdownProductCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.condition_id = arrCarConditionList.get(position).condition_id;
                } else {
                    spinnerModel.condition_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownProductModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.model_id = productModelList.get(position).model_id;
                } else {
                    spinnerModel.model_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dropdownUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.unit_id = arrUnitList.get(position).unit_id;
                } else {
                    spinnerModel.unit_id = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdownTimeWait.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time_type=arrTimeLeftList.get(position).getName();
//                if (position > 0) {
//                    spinnerModel.id = arrTimeLeftList.get(position).id;
//                } else {
//                    spinnerModel.id = 0;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dropdownBarTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.barcode_id = arrBarcodeTypeList.get(position).barcode_id;
                } else {
                    spinnerModel.barcode_id = "0";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdownProductTransamission.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.transmission_id = arrTransmissionList.get(position).transmission_id;
                } else {
                    spinnerModel.transmission_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownProductFuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.fuel_id = arrFuelTypeList.get(position).fuel_id;
                } else {
                    spinnerModel.fuel_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownIntColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.inteColor_id = arrColorList.get(position).color_id;
                } else {
                    spinnerModel.inteColor_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownExtColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    spinnerModel.extColor_id = arrColorList.get(position).color_id;
                } else {
                    spinnerModel.extColor_id = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ch_manage_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ch_manage_stock.isChecked()) {
                    enable_stock = 1;
                    lay_alert_quantity.setVisibility(View.VISIBLE);
                    lay_time_required.setVisibility(View.GONE);
                    lay_UsersSearch.setVisibility(View.GONE);
                    nameUserIDlist.clear();
                    nameUserlist.clear();
                    time_type="";
                    et_time_Required.setText("");
                } else {
                    enable_stock = 0;
                    lay_alert_quantity.setVisibility(View.GONE);
                    lay_time_required.setVisibility(View.VISIBLE);
                    lay_UsersSearch.setVisibility(View.VISIBLE);
                    time_type="minutes";
                }
            }
        });
        ch_Enable_Restock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ch_Enable_Restock.isChecked()) {
                    enable_restock = 1;
                } else {
                    enable_restock = 0;
                }
            }
        });
        ch_cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ch_cart_button.isChecked()) {
                    show_cart_button = 1;
                } else {
                    show_cart_button = 0;
                }
            }
        });
        ch_Show_in_Website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ch_Show_in_Website.isChecked()) {
                    show_website = 1;
                } else {
                    show_website = 0;
                }
            }
        });


        et_purchase_exc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                excTaxSingle = b;
            }
        });
        et_purchase_exc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                hideSoftKeyboard(AddProduct.this);
                if(excTaxSingle) {
//                    et_margin.setText("");
//                    margin_value = 0.0;
                    double searchText = 0.0;
                    if (et_purchase_exc.getText().toString().isEmpty()) {
                        searchText = 0.0;
                        exc_inc_tax = 0.0;
                    } else {
                        searchText = Double.parseDouble(et_purchase_exc.getText().toString());
                        exc_inc_tax = Double.parseDouble(et_purchase_exc.getText().toString());
                    }

                    et_purchase_inc.setText(String.format("%.2f", searchText));
                    total_exc_tax = ((margin_value / 100) * exc_inc_tax) + Double.valueOf(searchText);
                    if (total_exc_tax == 0.0) {
                        et_selling_exc.setText(String.format("%.2f", total_exc_tax));
                    } else {
                        et_selling_exc.setText(String.format("%.2f",total_exc_tax));
                    }
                    et_purchase_exc.setSelection(et_purchase_exc.getText().toString().length());

                }
            }
        });

//        et_purchase_exc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if (event == null || !event.isShiftPressed()) {
//                        // the user is done typing.
//
//                        hideSoftKeyboard(AddProduct.this);
//                        et_margin.setText("");
//                        margin_value = 0.0;
//                        double searchText = 0.0;
//                        if (et_purchase_exc.getText().toString().isEmpty()) {
//                            searchText = 0.0;
//                            exc_inc_tax = 0.0;
//                        } else {
//                            searchText = Double.parseDouble(et_purchase_exc.getText().toString());
//                            exc_inc_tax = Double.parseDouble(et_purchase_exc.getText().toString());
//                        }
//
//                        et_purchase_inc.setText(String.format("%.2f", searchText));
//
//                        if (total_exc_tax == 0.0) {
//                            et_selling_exc.setText(String.format("%.2f", searchText));
//                        } else {
//                            total_exc_tax = ((margin_value / 100) * exc_inc_tax) + Double.valueOf(searchText);
//                            et_selling_exc.setText(String.valueOf(total_exc_tax));
//                        }
//                        return true; // consume.
//                    }
//
//                }
//                return false; // pass on to other listeners.
//
//            }
//        });


        et_purchase_inc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                incTaxSingle = b;
            }
        });
        et_purchase_inc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (incTaxSingle) {
//                    et_margin.setText("");
//                    margin_value = 0.0;
//                hideSoftKeyboard(AddProduct.this);
                    double searchText = 0.0;
                    if (et_purchase_inc.getText().toString().isEmpty()) {
                        searchText = 0.0;
                        exc_inc_tax = 0.0;
                    } else {
                        searchText = Double.parseDouble(et_purchase_inc.getText().toString());
                        exc_inc_tax = Double.parseDouble(et_purchase_inc.getText().toString());
                    }
                    et_purchase_exc.setText(String.format("%.2f", searchText));
                    total_exc_tax = ((margin_value / 100) * exc_inc_tax) + Double.valueOf(searchText);
                    if (total_exc_tax == 0.0) {
                        et_selling_exc.setText(String.format("%.2f", total_exc_tax));
                    } else {
//                        total_exc_tax = ((margin_value / 100) * exc_inc_tax) + Double.valueOf(searchText);
                        et_selling_exc.setText(String.format("%.2f",total_exc_tax));
                    }
                    et_purchase_inc.setSelection(et_purchase_inc.getText().toString().length());

                }
            }
        });

//        et_purchase_inc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if (event == null || !event.isShiftPressed()) {
//                        // the user is done typing.
//                        et_margin.setText("");
//                        margin_value = 0.0;
//                        hideSoftKeyboard(AddProduct.this);
//                        double searchText = 0.0;
//                        if (et_purchase_inc.getText().toString().isEmpty()) {
//                            searchText = 0.0;
//                            exc_inc_tax = 0.0;
//                        } else {
//                            searchText = Double.parseDouble(et_purchase_inc.getText().toString());
//                            exc_inc_tax = Double.parseDouble(et_purchase_inc.getText().toString());
//                        }
//                        et_purchase_exc.setText(String.format("%.2f", searchText));
//
//                        if (total_exc_tax == 0.0) {
//                            et_selling_exc.setText(String.format("%.2f", searchText));
//                        } else {
//                            total_exc_tax = ((margin_value / 100) * exc_inc_tax) + Double.valueOf(searchText);
//                            et_selling_exc.setText(String.valueOf(total_exc_tax));
//                        }
//
//                        return true; // consume.
//                    }
//
//                }
//                return false; // pass on to other listeners.
//
//            }
//        });

        et_margin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                marginSingle = b;
            }
        });

        et_margin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(marginSingle){
                    if (et_margin.getText().toString().isEmpty()) {
                        margin_value = 0.0;
                    } else {
                        try {
                            margin_value = Double.parseDouble(et_margin.getText().toString());
                        }catch (NumberFormatException  e){
                            margin_value=0.0;
                        }

                    }
                    if (margin_value == 0.0) {
                        total_exc_tax = exc_inc_tax;
                    } else {
                        total_exc_tax = ((margin_value / 100) * exc_inc_tax) + exc_inc_tax;
                    }
                    et_selling_exc.setText(String.format("%.2f", total_exc_tax));
                }

            }
        });

//        et_margin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if (event == null || !event.isShiftPressed()) {
//                        // the user is done typing.
//
//                        hideSoftKeyboard(AddProduct.this);
//                        if (et_margin.getText().toString().isEmpty()) {
//                            margin_value = 0.0;
//                        } else {
//                            margin_value = Double.parseDouble(et_margin.getText().toString());
//                        }
//
//                        if (margin_value == 0.0) {
//                            total_exc_tax = exc_inc_tax;
//                        } else {
//                            total_exc_tax = ((margin_value / 100) * exc_inc_tax) + exc_inc_tax;
//
//                        }
//                        et_selling_exc.setText(String.format("%.2f", total_exc_tax));
//                        return true; // consume.
//                    }
//
//                }
//                return false; // pass on to other listeners.
//
//            }
//        });
        et_selling_exc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                sellingExcPrice = b;
            }
        });

        et_selling_exc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_selling_exc.setEnabled(true);
                if(sellingExcPrice){
                    if(exc_inc_tax > 0){
                        if (et_selling_exc.getText().toString().isEmpty()) {
                            total_exc_tax = 0.0;
                        } else {
                            total_exc_tax = Double.parseDouble(et_selling_exc.getText().toString());
                        }
                        double profit=  total_exc_tax - exc_inc_tax;
                        margin_value= (profit/exc_inc_tax) * 100;

//                        if(margin_value > 0){
//                            et_margin.setText(String.format("%.2f",margin_value));
//                        }else {
//                            margin_value=0.0;
                        et_margin.setText(String.format("%.2f",margin_value));
//                        }


                    }else{
                        et_selling_exc.setEnabled(false);
                        AppConstant.showToast(AddProduct.this,"Please Enter Valid Purchase Price");
                    }

                }

            }
        });


        txt_select_img_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, 0);
            }
        });

        txt_select_gallery_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, 1);
            }
        });

        imageAdapter.setOnItmeClicked(new ImageAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                arrImagesList.remove(position);
                imageAdapter.notifyDataSetChanged();
            }
        });
        variableValueAdapter.setOnImageSelect(new VariableProductAdapter.OnImageSelect() {
            @Override
            public void setOnImageSelect(int position,TextView txtVariantIm) {
                inImagePosition=position;
                txtVariantImg=txtVariantIm;
                Intent pictureActionIntent = null;
                pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, 2);
            }
        });

        txt_save_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ch_manage_stock.isChecked() && !et_alert_quantity.getText().toString().isEmpty()) {
                    alert_quantity = "0";
                }
                else if (ch_manage_stock.isChecked() && !et_alert_quantity.getText().toString().isEmpty()) {
                    alert_quantity = et_alert_quantity.getText().toString();
                }
                else if (ch_manage_stock.isChecked()) {
                    alert_quantity = "0";
                }
                else if (!ch_manage_stock.isChecked() ) {
                    alert_quantity = "0";
                }
                if (et_product_name.getText().toString().isEmpty()) {
                    AppConstant.showToast(AddProduct.this, "Please Enter Product Name");
                } else if (spinnerModel.unit_id == 0) {
                    AppConstant.showToast(AddProduct.this, "Please Select Units");
                } else if (et_style_no.getText().toString().isEmpty()){
                    AppConstant.showToast(AddProduct.this, "Please Enter Style/Model No.");
                }
                else if (et_min_order.getText().toString().equalsIgnoreCase("0")){
                    AppConstant.showToast(AddProduct.this, "Min Order Cannot be less than 1");
                }
//                else if(alert_quantity.equalsIgnoreCase("0")){
//                    AppConstant.showToast(AddProduct.this, "Alert Quantity Cannot be less than 1");
//                }
                else if (spinnerModel.barcode_id.equalsIgnoreCase("0")) {
                    AppConstant.showToast(AddProduct.this, "Please Select Barcode Type");
                } else if (ch_manage_stock.isChecked() && et_alert_quantity.getText().toString().isEmpty()) {
                    AppConstant.showToast(AddProduct.this, "Please Enter Alert Quantity");
                }else if(!ch_manage_stock.isChecked() && nameUserIDlist.size()==0){
                    AppConstant.showToast(AddProduct.this, "Please Assign Staff");
                }else  if(!ch_manage_stock.isChecked() && et_time_Required.getText().toString().isEmpty()){
                    AppConstant.showToast(AddProduct.this, "Please Enter Time Required");
                }
                else if (product_type.equalsIgnoreCase("single")) {
                    if (et_purchase_exc.getText().toString().isEmpty() || exc_inc_tax == 0.0) {
                        AppConstant.showToast(AddProduct.this, "Please Enter Purchase Exc. Tax");
                    } else if (et_purchase_inc.getText().toString().isEmpty() || exc_inc_tax == 0.0) {
                        AppConstant.showToast(AddProduct.this, "Please Enter Purchase Inc. Tax");
                    } else if (et_margin.getText().toString().isEmpty() || margin_value == 0.0) {
                        AppConstant.showToast(AddProduct.this, "Please Enter Margin %");
                    } else if (et_selling_exc.getText().toString().isEmpty() || et_selling_exc.getText().toString().equalsIgnoreCase("0.00")) {
                        AppConstant.showToast(AddProduct.this, "Enter Purchase Exc. & Inc. Price");
                    }
                    else {
                        if (arrdepartmentsList.contains("12")) {
                            if (et_product_torque.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.torque");
                            } else if (et_product_engine.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.engine");
                            } else if (et_product_doors.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.doors");
                            } else if (et_product_top_speed.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.top_speed");
                            } else if (et_product_horse_power.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.horsepower");
                            } else if (et_product_Towing.getText().toString().isEmpty()) {
                                AppConstant.showToast(AddProduct.this, "Please Enter product.towing_capacity");
                            } else {
                                if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
                                    if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                                   hideSoftKeyboard(AddProduct.this);
                                        updateProductDetail();
                                    } else {
                                        AppConstant.openInternetDialog(AddProduct.this);
                                    }

                                } else {
                                    if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                                  hideSoftKeyboard(AddProduct.this);
                                        addProduct();
                                    } else {
                                        AppConstant.openInternetDialog(AddProduct.this);
                                    }

                                }
                            }
                        } else {
                            if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
                                if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                    updateProductDetail();
                                } else {
                                    AppConstant.openInternetDialog(AddProduct.this);
                                }

                            } else {
                                if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                    addProduct();
                                } else {
                                    AppConstant.openInternetDialog(AddProduct.this);
                                }

                            }

                        }

                    }

                } else if (product_type.equalsIgnoreCase("variable") && arrAllVariationValueList.size() == 0) {
                    AppConstant.showToast(AddProduct.this, "Please Add Variations");

                } else {
                    if (arrdepartmentsList.contains("12")) {
                        if (et_product_torque.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.torque");
                        } else if (et_product_engine.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.engine");
                        } else if (et_product_doors.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.doors");
                        } else if (et_product_top_speed.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.top_speed");
                        } else if (et_product_horse_power.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.horsepower");
                        } else if (et_product_Towing.getText().toString().isEmpty()) {
                            AppConstant.showToast(AddProduct.this, "Please Enter product.towing_capacity");
                        } else {
                            if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
                                if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                    updateProductDetail();
                                } else {
                                    AppConstant.openInternetDialog(AddProduct.this);
                                }

                            } else {
                                if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                    addProduct();
                                } else {
                                    AppConstant.openInternetDialog(AddProduct.this);
                                }

                            }
                        }
                    } else {
                        if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
                            if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                updateProductDetail();
                            } else {
                                AppConstant.openInternetDialog(AddProduct.this);
                            }

                        } else {
                            if (AppConstant.isNetworkAvailable(AddProduct.this)) {
//                            hideSoftKeyboard(AddProduct.this);
                                addProduct();
                            } else {
                                AppConstant.openInternetDialog(AddProduct.this);
                            }

                        }

                    }

                }
            }
        });

    }
    public void deleteStaff(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                nameUserIDlist.remove(position);
                nameUserlist.remove(position);
                usersAdapter.notifyDataSetChanged();

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

    public void filterUser(String text) {
        if (!sp_cartSave.getString("searchCustomerList", "").equalsIgnoreCase("")) {//if not cart empty.
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CustomerListData>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("searchCustomerList", "");
            searchUserList = (ArrayList<CustomerListData>) gson.fromJson(strMyCart, type);

        }
        ArrayList<CustomerListData> temp = new ArrayList();
        for (CustomerListData d : searchUserList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getText().toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        cutoCompleteCustomerAapter.updateList(temp);

        et_SearchUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }
                et_SearchUser.setText("");
                nameUserlist.add(customerData.getText());
                nameUserIDlist.add(String.valueOf(customerData.getId()));
                usersAdapter.notifyDataSetChanged();
//                contactID = String.valueOf(customerData.getId());

            }
        });
    }


    public void deleteTags(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddProduct.this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Tag");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                arrStringTagsList.remove(position);
                tagAdapter.notifyDataSetChanged();

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
    public void openBrandDialog() {
        final Dialog dialog = new Dialog(AddProduct.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.brand_filter_dialog);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);
        TextView txt_no_resut = dialog.findViewById(R.id.txt_no_resut);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddProduct.this);
        recycler_category.setLayoutManager(mLayoutManager);
        brandsAdapter = new CategorySearchAdapter(AddProduct.this, arrbrandsList);
        recycler_category.setAdapter(brandsAdapter);

        if(arrbrandsList.size() > 0) {
            txt_no_resut.setVisibility(View.GONE);
        }else{
            txt_no_resut.setVisibility(View.VISIBLE);
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        brandsAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                CategoryModel model = list.get(position);
                brand_id = model.id;
                txt_product_brand.setText(model.name);
                dialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filterbrand(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }



    public void openProductCatDialog() {
        final Dialog dialog = new Dialog(AddProduct.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.search_filter_list_items);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);
        TextView txt_no_resut = dialog.findViewById(R.id.txt_no_resut);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddProduct.this);
        recycler_category.setLayoutManager(mLayoutManager);
         catSearchAdapter = new CategorySearchAdapter(AddProduct.this, arrCategoryList);
        recycler_category.setAdapter(catSearchAdapter);

        if(arrCategoryList.size() > 0) {
            txt_no_resut.setVisibility(View.GONE);
        }else{
            txt_no_resut.setVisibility(View.VISIBLE);
        }
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        catSearchAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                CategoryModel model = list.get(position);
                categoryID = model.id;
                txt_product_category.setText(model.name);
                dialog.dismiss();

                arrSubCategoryList.clear();
                arrSubSubCategoryList.clear();

                subCatID = 0;
                txt_product_sub_category.setText("Select Sub Category");

                subsubCatID = 0;
                txt_product_sub_sub_category.setText("Select Sub Sub Category");

                if (categoryID == 0) {
                    arrStringTagsList.clear();
                    tagAdapter.notifyDataSetChanged();
                } else {
                    getSubcategoriesList();

                }
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }
    void filter(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        catSearchAdapter.updateList(temp);

    }
    public void openProductSubCategoryDialog() {
        final Dialog dialog = new Dialog(AddProduct.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.sub_category_filter_dialog);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);
        TextView txt_no_resut = dialog.findViewById(R.id.txt_no_resut);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddProduct.this);
        recycler_category.setLayoutManager(mLayoutManager);
        subCategoryAdapter = new CategorySearchAdapter(AddProduct.this, arrSubCategoryList);
        recycler_category.setAdapter(subCategoryAdapter);

        if(arrSubCategoryList.size() > 0) {
            txt_no_resut.setVisibility(View.GONE);
        }else{
            txt_no_resut.setVisibility(View.VISIBLE);
        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        subCategoryAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                CategoryModel model = list.get(position);
                subCatID = model.id;
                txt_product_sub_category.setText(model.name);
                dialog.dismiss();

                arrSubSubCategoryList.clear();
                subsubCatID = 0;
                txt_product_sub_sub_category.setText("Select Sub Sub Category");

//                if (subCatID == 0) {
//
//                } else {
                    getSubSubcategoriesList();

//                }


            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                subCategoryfilter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }
    void subCategoryfilter(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrSubCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        subCategoryAdapter.updateList(temp);

    }


    public void openProductSubSubCategoryDialog() {
        final Dialog dialog = new Dialog(AddProduct.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.sub_sub_category_filter_dialog);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);
        TextView txt_no_resut = dialog.findViewById(R.id.txt_no_resut);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddProduct.this);
        recycler_category.setLayoutManager(mLayoutManager);
        subSubCategoryAdapter = new CategorySearchAdapter(AddProduct.this, arrSubSubCategoryList);
        recycler_category.setAdapter(subSubCategoryAdapter);

        if(arrSubSubCategoryList.size() > 0) {
            txt_no_resut.setVisibility(View.GONE);
        }else{
            txt_no_resut.setVisibility(View.VISIBLE);
        }

        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        subSubCategoryAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                CategoryModel model = list.get(position);
                subsubCatID = model.id;
                txt_product_sub_sub_category.setText(model.name);
                dialog.dismiss();

                getTags();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                subSubCategoryfilter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }
    void subSubCategoryfilter(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrSubSubCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        subSubCategoryAdapter.updateList(temp);

    }
    void filterbrand(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrbrandsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        brandsAdapter.updateList(temp);

    }


    public void openAddTagsDialog() {
        Dialog addTagDialog = new Dialog(AddProduct.this);
        addTagDialog.setContentView(R.layout.add_tag_dialog);
        addTagDialog.setCancelable(false);
        Window window = addTagDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);

        TextView txt_add_tag = addTagDialog.findViewById(R.id.txt_add_tag);
        TextView txt_cancel_tag = addTagDialog.findViewById(R.id.txt_cancel_tag);
        EditText et_tag_name  = addTagDialog.findViewById(R.id.et_tag_name);

        addTagDialog.show();

        txt_cancel_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagDialog.dismiss();
            }
        });

        txt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_tag_name.getText().toString().isEmpty()) {
                    AppConstant.showToast(AddProduct.this, "Please Enter Tag Name");
                } else {
                   String  newTagName=et_tag_name.getText().toString();
                    arrStringTagsList.add(newTagName);
                    tagAdapter.notifyDataSetChanged();
                    addTagDialog.dismiss();
//                   getTags();
                }

            }
        });

    }



    // details for product
    public void saleProductCreateDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("products/create");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("create response", respo);
                            searchUserList.clear();
                            String status = responseObject.optString("success");
                            if (status.equalsIgnoreCase("true")) {

                                if(responseObject.has("default_profit_percent") && !responseObject.isNull("default_profit_percent")){
                                    defaultMargin = responseObject.getInt("default_profit_percent");
                                    margin_value=defaultMargin;
                                    et_margin.setText(String.format("%.2f",margin_value));
                                }
                                JSONArray data = responseObject.getJSONArray("categories");
                                CategoryModel modell = new CategoryModel();
                                modell.id = 0;
                                modell.name ="Select Category";
                                arrCategoryList.add(modell);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrCategoryList.add(model);

                                }

                                JSONArray sub_categoriesdata = responseObject.getJSONArray("sub_categories");
                                for (int i = 0; i < sub_categoriesdata.length(); i++) {
                                    JSONObject object = sub_categoriesdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubCategoryList.add(model);
                                }
                                JSONArray sub_sub_categoriesdata = responseObject.getJSONArray("sub_sub_categories");
                                for (int i = 0; i < sub_sub_categoriesdata.length(); i++) {
                                    JSONObject object = sub_sub_categoriesdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubSubCategoryList.add(model);

                                }

                                JSONArray brandsdata = responseObject.getJSONArray("brands");
                                CategoryModel modelbrand = new CategoryModel();
                                modelbrand.id = 0;
                                modelbrand.name ="Select Brand";
                                arrbrandsList.add(modelbrand);
                                for (int i = 0; i < brandsdata.length(); i++) {
                                    JSONObject object = brandsdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrbrandsList.add(model);

                                }
                                if (responseObject.has("models") && !responseObject.isNull("models")) {
                                    JSONArray models = responseObject.getJSONArray("models");
                                    for (int i = 0; i < models.length(); i++) {
                                        JSONObject object = models.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.model_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        productModelList.add(model);
                                    }
                                }
                                if (responseObject.has("barcode_types") && !responseObject.isNull("barcode_types")) {
                                    JSONArray barcode_typesdata = responseObject.getJSONArray("barcode_types");
                                    for (int i = 0; i < barcode_typesdata.length(); i++) {
                                        JSONObject object = barcode_typesdata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.barcode_id = object.getString("id");
                                        model.name = object.getString("name");
                                        arrBarcodeTypeList.add(model);
                                    }
                                }
                                if (responseObject.has("units") && !responseObject.isNull("units")) {
                                    JSONArray unitsdata = responseObject.getJSONArray("units");
                                    for (int i = 0; i < unitsdata.length(); i++) {
                                        JSONObject object = unitsdata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.unit_id = object.getInt("id");
                                        model.name = object.getString("short_name");
                                        arrUnitList.add(model);
                                    }
                                }
                                if (responseObject.has("condition_types") && !responseObject.isNull("condition_types")) {
                                    JSONArray conditiondata = responseObject.getJSONArray("condition_types");
                                    for (int i = 0; i < conditiondata.length(); i++) {
                                        JSONObject object = conditiondata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.condition_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrCarConditionList.add(model);
                                    }
                                }
                                if (responseObject.has("transmission_types") && !responseObject.isNull("transmission_types")) {
                                    JSONArray transmissiondata = responseObject.getJSONArray("transmission_types");
                                    for (int i = 0; i < transmissiondata.length(); i++) {
                                        JSONObject object = transmissiondata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.transmission_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrTransmissionList.add(model);
                                    }
                                }

                                if (responseObject.has("fuel_types") && !responseObject.isNull("fuel_types")) {
                                    JSONArray fueldata = responseObject.getJSONArray("fuel_types");
                                    for (int i = 0; i < fueldata.length(); i++) {
                                        JSONObject object = fueldata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.fuel_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrFuelTypeList.add(model);
                                    }
                                }
                                if (responseObject.has("colors") && !responseObject.isNull("colors")) {
                                    JSONArray colordata = responseObject.getJSONArray("colors");
                                    for (int i = 0; i < colordata.length(); i++) {
                                        JSONObject object = colordata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.color_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrColorList.add(model);
                                    }
                                }

                                if (responseObject.has("service_staffs") && !responseObject.isNull("service_staffs")) {
                                    JSONArray employeeObj = responseObject.getJSONArray("service_staffs");
                                    for (int i = 0; i < employeeObj.length(); i++) {
                                        JSONObject empdata = employeeObj.getJSONObject(i);
                                        CustomerListData customerListData = new CustomerListData();
                                        customerListData.setId(empdata.getInt("id"));
                                        customerListData.setText(empdata.getString("name"));
                                        searchUserList.add(customerListData);
                                    }
                                }
                                Gson gson = new Gson();
                                ed_cartSave.putString("searchCustomerList", gson.toJson(searchUserList));
                                ed_cartSave.commit();
                                cutoCompleteCustomerAapter.notifyDataSetChanged();


                                setFisrtItems();
//                                jobSpinnerAdapter.notifyDataSetChanged();

                                    for (int i = 0; i < arrUnitList.size(); i++) {
                                        if (arrUnitList.get(i).name.equalsIgnoreCase("Pc(s)")) {
                                            dropdownUnits.setSelection(i);
                                        }
                                    }

                                        for (int i = 0; i < arrBarcodeTypeList.size(); i++) {
                                        if (arrBarcodeTypeList.get(i).barcode_id.equalsIgnoreCase("C128")) {
                                            dropdownBarTypes.setSelection(i);
                                        }

                                }
                            }

                        } else {

                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "product/create API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddProduct.this, "Could Not Create Product. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Create Product. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void setFisrtItems() {

//        SpinnerSelectionModel spinnerSelectionModel = new SpinnerSelectionModel();
//        spinnerSelectionModel.brand_id = 0;
//        spinnerSelectionModel.setName("Select Brand");
//        arrbrandsList.add(0, spinnerSelectionModel);

        SpinnerSelectionModel spinnerUnitModel = new SpinnerSelectionModel();
        spinnerUnitModel.unit_id = 0;
        spinnerUnitModel.setName("Select Unit");
        arrUnitList.add(0, spinnerUnitModel);

        for(int i=0;i<3;i++){
            SpinnerSelectionModel spinnerTimeModel = new SpinnerSelectionModel();
            if(i==0){
                spinnerTimeModel.setName("minutes");
            }else if(i==1){
                spinnerTimeModel.setName("hours");
            }else if(i==2){
                spinnerTimeModel.setName("days");
            }
            arrTimeLeftList.add(0, spinnerTimeModel);
        }

//        SpinnerSelectionModel categoryModel = new SpinnerSelectionModel();
//        categoryModel.category_id = 0;
//        categoryModel.setName("Select Category");
//        arrCategoryList.add(0, categoryModel);

//        SpinnerSelectionModel subCategoryModel = new SpinnerSelectionModel();
//        subCategoryModel.sub_cat_id = 0;
//        subCategoryModel.setName("Select Sub Category");
//        arrSubCategoryList.add(0, subCategoryModel);
//
//        SpinnerSelectionModel subsubCategoryModel = new SpinnerSelectionModel();
//        subsubCategoryModel.sub_sub_cat_id = 0;
//        subsubCategoryModel.setName("Select Sub Sub Category");
//        arrSubSubCategoryList.add(0, subsubCategoryModel);

        SpinnerSelectionModel barcodeModel = new SpinnerSelectionModel();
        barcodeModel.barcode_id = "0";
        barcodeModel.setName("Select Barcode Type");
        arrBarcodeTypeList.add(0, barcodeModel);

        SpinnerSelectionModel conditionModel = new SpinnerSelectionModel();
        conditionModel.condition_id = 0;
        conditionModel.setName("Select Car Condition");
        arrCarConditionList.add(0, conditionModel);

        SpinnerSelectionModel fuelModel = new SpinnerSelectionModel();
        fuelModel.fuel_id = 0;
        fuelModel.setName("Select Fuel Type");
        arrFuelTypeList.add(0, fuelModel);

        SpinnerSelectionModel colorModel = new SpinnerSelectionModel();
        colorModel.color_id = 0;
        colorModel.setName("Select Color");
        arrColorList.add(0, colorModel);

        SpinnerSelectionModel productModel = new SpinnerSelectionModel();
        productModel.model_id = 0;
        productModel.setName("Select Model");
        productModelList.add(0, productModel);

        SpinnerSelectionModel transmissionModel = new SpinnerSelectionModel();
        transmissionModel.transmission_id = 0;
        transmissionModel.setName("Select Transmission");
        arrTransmissionList.add(0, transmissionModel);

        setAdpters();


    }

    public void setAdpters() {

//        SpinnerSelectionAdapter projectTypeAdapter = new SpinnerSelectionAdapter(this, arrbrandsList);
//        dropdownBrands.setAdapter(projectTypeAdapter);

        SpinnerSelectionAdapter productModel = new SpinnerSelectionAdapter(this, productModelList);
        dropdownProductModel.setAdapter(productModel);

        SpinnerSelectionAdapter unitAdapter = new SpinnerSelectionAdapter(this, arrUnitList);
        dropdownUnits.setAdapter(unitAdapter);

        SpinnerSelectionAdapter timeAdapter = new SpinnerSelectionAdapter(this, arrTimeLeftList);
        dropdownTimeWait.setAdapter(timeAdapter);

//        SpinnerSelectionAdapter categoryAdapter = new SpinnerSelectionAdapter(this, arrCategoryList);
//        dropdownCategory.setAdapter(categoryAdapter);
//
//        SpinnerSelectionAdapter subCategoryAdapter = new SpinnerSelectionAdapter(this, arrSubCategoryList);
//        dropdownSubCategory.setAdapter(subCategoryAdapter);
//
//        SpinnerSelectionAdapter subsubCategoryAdapter = new SpinnerSelectionAdapter(this, arrSubSubCategoryList);
//        dropdownSubSubCategory.setAdapter(subsubCategoryAdapter);

        SpinnerSelectionAdapter barcodeAdapter = new SpinnerSelectionAdapter(this, arrBarcodeTypeList);
        dropdownBarTypes.setAdapter(barcodeAdapter);

        SpinnerSelectionAdapter conditionAdapter = new SpinnerSelectionAdapter(this, arrCarConditionList);
        dropdownProductCondition.setAdapter(conditionAdapter);

        SpinnerSelectionAdapter transmissioncAdapter = new SpinnerSelectionAdapter(this, arrTransmissionList);
        dropdownProductTransamission.setAdapter(transmissioncAdapter);

        SpinnerSelectionAdapter fuelTypeAdapter = new SpinnerSelectionAdapter(this, arrFuelTypeList);
        dropdownProductFuel.setAdapter(fuelTypeAdapter);

        SpinnerSelectionAdapter colorAdapter = new SpinnerSelectionAdapter(this, arrColorList);
        dropdownIntColor.setAdapter(colorAdapter);

        SpinnerSelectionAdapter colorExtAdapter = new SpinnerSelectionAdapter(this, arrColorList);
        dropdownExtColor.setAdapter(colorExtAdapter);

    }

    public void addProduct() {
        AppConstant.showProgress(AddProduct.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("name", et_product_name.getText().toString());
            mainObject.put("brand_id", spinnerModel.brand_id);
            mainObject.put("unit_id", spinnerModel.unit_id);
            mainObject.put("category_id", categoryID);
            mainObject.put("sub_category_id", subCatID);
            mainObject.put("sub_sub_category_id", subsubCatID);
            mainObject.put("sku", et_product_sku.getText().toString());
            mainObject.put("alert_quantity", alert_quantity);
            mainObject.put("barcode_type", spinnerModel.barcode_id);
            mainObject.put("enable_stock", enable_stock);
            mainObject.put("single_dpp", exc_inc_tax);
            mainObject.put("single_dpp_inc_tax", exc_inc_tax);
            mainObject.put("profit_percent", margin_value);
            mainObject.put("single_dsp_inc_tax", et_selling_exc.getText().toString());
            mainObject.put("single_dsp", et_selling_exc.getText().toString());
            mainObject.put("model_id", spinnerModel.model_id);
            mainObject.put("condition_type", spinnerModel.condition_id);
            mainObject.put("odometer", et_product_odometer.getText().toString());
            mainObject.put("torque", et_product_torque.getText().toString());
            mainObject.put("engine", et_product_engine.getText().toString());
            mainObject.put("doors", et_product_doors.getText().toString());
            mainObject.put("transmission_type", spinnerModel.transmission_id);
            mainObject.put("top_speed", et_product_top_speed.getText().toString());
            mainObject.put("fuel_type", spinnerModel.fuel_id);
            mainObject.put("horsepower", et_product_horse_power.getText().toString());
            mainObject.put("towing_capacity", et_product_Towing.getText().toString());
            mainObject.put("interior_color_id", spinnerModel.inteColor_id);
            mainObject.put("exterior_color_id", spinnerModel.extColor_id);
            mainObject.put("type", product_type);
            mainObject.put("image", productImagePath);
            mainObject.put("image_type", productImageExtenion);
            mainObject.put("enable_restock", enable_restock);
            mainObject.put("show_website", show_website);
            mainObject.put("show_cart_button", show_cart_button);
            mainObject.put("products_min_order", et_min_order.getText().toString());
            mainObject.put("products_max_stock", et_max_order.getText().toString());
            mainObject.put("style_no", et_style_no.getText().toString());
            mainObject.put("time_type",time_type);
            mainObject.put("time",et_time_Required.getText().toString());
            String tag= arrStringTagsList.toString().replace("[", "").replace("]", "");
            mainObject.put("tags",tag);
            Log.e("tags", tag);

            String staff= nameUserIDlist.toString().replace("[", "").replace("]", "");
            mainObject.put("service_staffs",staff);
            Log.e("service_staffs", staff);

            JSONArray galleryImage = new JSONArray();
            for (int i = 0; i < arrImagesList.size(); i++) {
                ImagesModel model = arrImagesList.get(i);
                JSONObject image = new JSONObject();
                image.put("image", model.data);
                image.put("image_type", model.type);
                galleryImage.put(image);
            }
            mainObject.put("gallery_images", galleryImage);
            Log.e("galleryImage", galleryImage.toString());

            JSONArray productVariation = new JSONArray();
            for (int i = 0; i < arrAllVariationValueList.size(); i++) {
                VariationValueModel products = arrAllVariationValueList.get(i);
                JSONObject product = new JSONObject();
                product.put("variation_template_id", products.variation_template_id);
                product.put("row_index", "");
                product.put("color_id", products.color_id);
                String imageData="",imgType="";
                if(arrVariantImagesList.size() > i){
                    imageData=arrVariantImagesList.get(i).data;
                    imgType=arrVariantImagesList.get(i).type;
                }
                product.put("image",imageData);
                product.put("image_type",imgType);
                productVariation.put(product);

                JSONArray variations = new JSONArray();

                for (int a = 0; a < products.getVarialtionValues().size(); a++) {
                    AllVariationModel productsVariation = products.getVarialtionValues().get(a);
                    JSONObject objVariation = new JSONObject();
                    objVariation.put("sub_sku", productsVariation.variation_sku);
                    objVariation.put("value", productsVariation.name);
                    objVariation.put("variation_value_id", productsVariation.variation_value_id);
                    objVariation.put("default_purchase_price", productsVariation.dpp_exc_tax);
                    objVariation.put("dpp_inc_tax", productsVariation.dpp_inc_tax);
                    objVariation.put("profit_percent", productsVariation.var_exc_margin);
                    objVariation.put("default_sell_price", productsVariation.var_dsp_exc_tax);
                    objVariation.put("sell_price_inc_tax", productsVariation.var_dsp_exc_tax);
                    variations.put(objVariation);
                }
                product.put("variations", variations);

            }
            mainObject.put("product_variation", productVariation);

            Log.e("product_variation", productVariation.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.addRepairs("products", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("Add Product Response", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {

                                if (ProductList.productListActivity != null) {
                                    ProductList.productListActivity.finish();
                                }
                                if (ProductDetailActivity.productDetailActivity != null) {
                                    ProductDetailActivity.productDetailActivity.finish();
                                }
                                String msg = responseObject.optString("msg");
                                Toast.makeText(AddProduct.this, "" + msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(AddProduct.this, ProductList.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                AppConstant.hideProgress();
                                Toast.makeText(AddProduct.this, "Could Not Add Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "products API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Add Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // edit product detail
    public void editProductDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("products/" + product_id + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("edit response", respo);

                            String status = responseObject.optString("success");
                            if (status.equalsIgnoreCase("true")) {
                                JSONObject data = responseObject.getJSONObject("product");

                                if(responseObject.has("default_profit_percent") && !responseObject.isNull("default_profit_percent")){
                                    defaultMargin = responseObject.getInt("default_profit_percent");
                                    margin_value=defaultMargin;
                                    et_margin.setText(String.format("%.2f", margin_value));
                                }
                                JSONArray categoriesdata = responseObject.getJSONArray("categories");
                                CategoryModel modell = new CategoryModel();
                                modell.id = 0;
                                modell.name = "Select Category";
                                arrCategoryList.add(modell);
                                for (int i = 0; i < categoriesdata.length(); i++) {
                                    JSONObject object = categoriesdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrCategoryList.add(model);

                                }

                                JSONArray sub_categoriesdata = responseObject.getJSONArray("sub_categories");
                                for (int i = 0; i < sub_categoriesdata.length(); i++) {
                                    JSONObject object = sub_categoriesdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubCategoryList.add(model);

                                }

                                JSONArray sub_sub_categoriesdata = responseObject.getJSONArray("sub_sub_categories");
                                for (int i = 0; i < sub_sub_categoriesdata.length(); i++) {
                                    JSONObject object = sub_sub_categoriesdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubSubCategoryList.add(model);

                                }

                                JSONArray brandsdata = responseObject.getJSONArray("brands");
                                for (int i = 0; i < brandsdata.length(); i++) {
                                    JSONObject object = brandsdata.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrbrandsList.add(model);

                                }

                                JSONArray barcode_typesdata = responseObject.getJSONArray("barcode_types");
                                for (int i = 0; i < barcode_typesdata.length(); i++) {
                                    JSONObject object = barcode_typesdata.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.barcode_id = object.getString("id");
                                    model.name = object.getString("name");
                                    arrBarcodeTypeList.add(model);


                                }
                                JSONArray unitsdata = responseObject.getJSONArray("units");
                                for (int i = 0; i < unitsdata.length(); i++) {
                                    JSONObject object = unitsdata.getJSONObject(i);
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.unit_id = object.getInt("id");
                                    model.name = object.getString("short_name");
                                    arrUnitList.add(model);
                                }
                                if (responseObject.has("models") && !responseObject.isNull("models")) {
                                    JSONArray modeldata = responseObject.getJSONArray("models");
                                    for (int i = 0; i < modeldata.length(); i++) {
                                        JSONObject object = modeldata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.model_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        productModelList.add(model);
                                    }
                                }
                                if (responseObject.has("condition_types") && !responseObject.isNull("condition_types")) {
                                    JSONArray conditiondata = responseObject.getJSONArray("condition_types");
                                    for (int i = 0; i < conditiondata.length(); i++) {
                                        JSONObject object = conditiondata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.condition_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrCarConditionList.add(model);
                                    }
                                }
                                if (responseObject.has("transmission_types") && !responseObject.isNull("transmission_types")) {
                                    JSONArray transmissiondata = responseObject.getJSONArray("transmission_types");
                                    for (int i = 0; i < transmissiondata.length(); i++) {
                                        JSONObject object = transmissiondata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.transmission_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrTransmissionList.add(model);
                                    }
                                }
                                if (responseObject.has("fuel_types") && !responseObject.isNull("fuel_types")) {
                                    JSONArray fueldata = responseObject.getJSONArray("fuel_types");
                                    for (int i = 0; i < fueldata.length(); i++) {
                                        JSONObject object = fueldata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.fuel_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrFuelTypeList.add(model);
                                    }
                                }
                                if (responseObject.has("colors") && !responseObject.isNull("colors")) {
                                    JSONArray intColordata = responseObject.getJSONArray("colors");
                                    for (int i = 0; i < intColordata.length(); i++) {
                                        JSONObject object = intColordata.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.color_id = object.getInt("id");
                                        model.name = object.getString("name");
                                        arrColorList.add(model);
                                    }
                                }

                                if (responseObject.has("service_staffs") && !responseObject.isNull("service_staffs")) {
                                    JSONArray employeeObj = responseObject.getJSONArray("service_staffs");
                                    for (int i = 0; i < employeeObj.length(); i++) {
                                        JSONObject empdata = employeeObj.getJSONObject(i);
                                        CustomerListData customerListData = new CustomerListData();
                                        customerListData.setId(empdata.getInt("id"));
                                        customerListData.setText(empdata.getString("name"));
                                        searchUserList.add(customerListData);
                                    }
                                }
                                Gson gson = new Gson();
                                ed_cartSave.putString("searchCustomerList", gson.toJson(searchUserList));
                                ed_cartSave.commit();
                                cutoCompleteCustomerAapter.notifyDataSetChanged();

                                if (responseObject.has("selected_service_staffs") && !responseObject.isNull("selected_service_staffs")) {
                                    JSONArray servicesObj = responseObject.getJSONArray("selected_service_staffs");
                                    for (int i = 0; i < servicesObj.length(); i++) {
                                        JSONObject staffdata = servicesObj.getJSONObject(i);
                                        if (staffdata.has("id") && !staffdata.isNull("id")) {
                                            nameUserIDlist.add(String.valueOf(staffdata.getInt("id")));
                                        }
                                        if (staffdata.has("name") && !staffdata.isNull("name")) {
                                            nameUserlist.add(staffdata.getString("name"));
                                        }
                                    }
                                }
                                usersAdapter.notifyDataSetChanged();

                                if (responseObject.has("business_service") && !responseObject.isNull("business_service")) {
                                    JSONObject servicesObj = responseObject.getJSONObject("business_service");

                                    if(servicesObj.has("time") && !servicesObj.isNull("time")){
                                        et_time_Required.setText(""+servicesObj.getInt("time"));
                                    }
                                    if(servicesObj.has("time_type") && !servicesObj.isNull("time_type")){
                                        time_type=servicesObj.getString("time_type");
                                    }
                                }

                                setFisrtItems();
                                    for (int i = 0; i < arrTimeLeftList.size(); i++) {
                                        if (time_type.equalsIgnoreCase(arrTimeLeftList.get(i).getName())) {
                                            dropdownTimeWait.setSelection(i);
                                        }
                                    }

                                String product_name = data.getString("name");

                                et_product_name.setText(product_name);
                                if(data.has("image") && !data.isNull("image")){
                                    txt_product_image.setText(data.getString("image"));
                                }
                                if (data.has("type") && !data.isNull("type")) {
                                    product_type = data.getString("type");
                                    for (int i = 0; i < arrProductTypeList.size(); i++) {
                                        if (product_type.equalsIgnoreCase(arrProductTypeList.get(i))) {
                                            dropDown_productType.setSelection(i);
                                            dropDown_productType.setEnabled(false);
                                        }
                                    }

                                    if (product_type.equalsIgnoreCase("single")) {
                                        lay_product_single.setVisibility(View.VISIBLE);
                                        lay_variable_product.setVisibility(View.GONE);

                                        product_type = "single";
                                        if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {//if not cart empty.
                                            ed_cartSave.remove("myVariation");
                                            ed_cartSave.commit();

                                        }
                                        arrAllVariationValueList.clear();
//                                        arrVariationList.clear();
//                                        arrVariationList.add(1);
                                        variableValueAdapter.notifyDataSetChanged();
//                    variableValueAdapter.notifyItemInserted(arrVariationList.size());

//                    getProductType("single");
                                    } else {
                                        lay_product_single.setVisibility(View.GONE);
                                        lay_variable_product.setVisibility(View.VISIBLE);
                                        product_type = "variable";
                                        getProductTypeEdit();
                                    }
                                }
                                if (data.has("unit_id") && !data.isNull("unit_id")) {
                                    int unit_id = data.getInt("unit_id");
                                    for (int i = 0; i < arrUnitList.size(); i++) {
                                        if (unit_id == arrUnitList.get(i).unit_id) {
                                            dropdownUnits.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("brand_id") && !data.isNull("brand_id")) {
                                     brand_id = data.getInt("brand_id");
                                    for (int i = 0; i < arrbrandsList.size(); i++) {
                                        if (brand_id == arrbrandsList.get(i).id) {
                                            txt_product_brand.setText(arrbrandsList.get(i).name);
                                        }
                                    }
                                }

                                if (data.has("category_id") && !data.isNull("category_id")) {
                                     categoryID = data.getInt("category_id");
                                    for (int i = 0; i < arrCategoryList.size(); i++) {
                                        if (categoryID == arrCategoryList.get(i).id) {
                                            txt_product_category.setText(arrCategoryList.get(i).name);
                                            break;
                                        }
                                    }
                                }
                                if (data.has("sub_category_id") && !data.isNull("sub_category_id")) {
                                     subCatID=data.getInt("sub_category_id");
                                    for (int i = 0; i < arrSubCategoryList.size(); i++) {
                                        if (subCatID == arrSubCategoryList.get(i).id) {
                                            txt_product_sub_category.setText(arrSubCategoryList.get(i).name);
                                            break;
                                        }
                                    }
                                }
                                if (data.has("sub_sub_category_id") && !data.isNull("sub_sub_category_id")) {
                                     subsubCatID = data.getInt("sub_sub_category_id");
                                    for (int i = 0; i < arrSubSubCategoryList.size(); i++) {
                                        if (subsubCatID == arrSubSubCategoryList.get(i).id) {
                                            txt_product_sub_category.setText(arrSubSubCategoryList.get(i).name);
                                        }
                                    }
                                }
                                if (data.has("interior_color_id") && !data.isNull("interior_color_id")) {
                                    int color_id = data.getInt("interior_color_id");
                                    for (int i = 0; i < arrColorList.size(); i++) {
                                        if (color_id == arrColorList.get(i).color_id) {
                                            dropdownIntColor.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("exterior_color_id") && !data.isNull("exterior_color_id")) {
                                    int color_id = data.getInt("exterior_color_id");
                                    for (int i = 0; i < arrColorList.size(); i++) {
                                        if (color_id == arrColorList.get(i).color_id) {
                                            dropdownExtColor.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("model_id") && !data.isNull("model_id")) {
                                    int model_id = data.getInt("model_id");
                                    for (int i = 0; i < productModelList.size(); i++) {
                                        if (model_id == productModelList.get(i).model_id) {
                                            dropdownProductModel.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("condition_type") && !data.isNull("condition_type")) {
                                    int condition_type = data.getInt("condition_type");
                                    for (int i = 0; i < arrCarConditionList.size(); i++) {
                                        if (condition_type == arrCarConditionList.get(i).condition_id) {
                                            dropdownProductCondition.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("transmission_type") && !data.isNull("transmission_type")) {
                                    int transmission_type = data.getInt("transmission_type");
                                    for (int i = 0; i < arrTransmissionList.size(); i++) {
                                        if (transmission_type == arrTransmissionList.get(i).transmission_id) {
                                            dropdownProductTransamission.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("fuel_type") && !data.isNull("fuel_type")) {
                                    int fuel_type = data.getInt("fuel_type");
                                    for (int i = 0; i < arrFuelTypeList.size(); i++) {
                                        if (fuel_type == arrFuelTypeList.get(i).fuel_id) {
                                            dropdownProductFuel.setSelection(i);
                                        }
                                    }
                                }
                                if (data.has("towing_capacity") && !data.isNull("towing_capacity")) {
                                    String towing_capacity = data.getString("towing_capacity");
                                    et_product_Towing.setText(towing_capacity);
                                }
                                if (data.has("horsepower") && !data.isNull("horsepower")) {
                                    String horsepower = data.getString("horsepower");
                                    et_product_horse_power.setText(horsepower);
                                }
                                if (data.has("top_speed") && !data.isNull("top_speed")) {
                                    String top_speed = data.getString("top_speed");
                                    et_product_top_speed.setText(top_speed);
                                }
                                if (data.has("doors") && !data.isNull("doors")) {
                                    String doors = data.getString("doors");
                                    et_product_doors.setText(doors);
                                }
                                if (data.has("engine") && !data.isNull("engine")) {
                                    String engine = data.getString("engine");
                                    et_product_engine.setText(engine);
                                }
                                if (data.has("torque") && !data.isNull("torque")) {
                                    String torque = data.getString("torque");
                                    et_product_torque.setText(torque);
                                }
                                if (data.has("odometer") && !data.isNull("odometer")) {
                                    String odometer = data.getString("odometer");
                                    et_product_odometer.setText(odometer);
                                }

                                if (data.has("alert_quantity") && !data.isNull("alert_quantity")) {
                                    int alert_quantity = data.getInt("alert_quantity");
                                    et_alert_quantity.setText(String.valueOf(alert_quantity));
                                }
                                if (data.has("enable_restock") && !data.isNull("enable_restock")) {
                                    enable_restock= data.getInt("enable_restock");
                                    if(enable_restock==1){
                                        ch_Enable_Restock.setChecked(true);
                                    }else{
                                        ch_Enable_Restock.setChecked(false);
                                    }
                                }
                                if (data.has("show_website") && !data.isNull("show_website")) {
                                    show_website= data.getInt("show_website");
                                    if(show_website==1){
                                        ch_Show_in_Website.setChecked(true);
                                    }else{
                                        ch_Show_in_Website.setChecked(false);
                                    }
                                }
                                if (data.has("show_cart_button") && !data.isNull("show_cart_button")) {
                                    show_cart_button= data.getInt("show_cart_button");
                                    if(show_cart_button==1){
                                        ch_cart_button.setChecked(true);
                                    }else{
                                        ch_cart_button.setChecked(false);
                                    }
                                }
                             if (data.has("tags") && !data.isNull("tags")) {
                                 String tags=data.getString("tags");
                                 if(!tags.isEmpty()){
                                     String[] elements = tags.split(",");

                                     for(int i=0 ; i <elements.length ; i++){
                                         arrStringTagsList.add(elements[i]);
                                     }
                                     tagAdapter.notifyDataSetChanged();
                                 }

//                                 List<String> fixedLenghtList = Arrays.asList(elements);
//                                 arrStringTagsList = new ArrayList<String>(fixedLenghtList);
//                                 tagAdapter = new TagAdapter(AddProduct.this, arrStringTagsList);
//                                 recycler_tags.setAdapter(tagAdapter);
                                }
                             if (data.has("products_min_order") && !data.isNull("products_min_order")) {
                                 String min_order=String.valueOf(data.getInt("products_min_order"));
                                   et_min_order.setText(min_order);
                                }
                             if(data.has("products_max_stock") && !data.isNull("products_max_stock")) {
                                 String max_order=String.valueOf(data.getInt("products_max_stock"));
                                   et_max_order.setText(max_order);
                                }
                             if (data.has("style_no") && !data.isNull("style_no")) {
                                 String style =data.getString("style_no");
                                   et_style_no.setText(style);
                                 checkStyleNo(style);
                                }
                             enable_stock = data.getInt("enable_stock");
                                if (enable_stock == 1) {
                                    ch_manage_stock.setChecked(true);
                                    lay_alert_quantity.setVisibility(View.VISIBLE);
                                    lay_time_required.setVisibility(View.GONE);
                                    lay_UsersSearch.setVisibility(View.GONE);
                                } else {
                                    ch_manage_stock.setChecked(false);
                                    lay_alert_quantity.setVisibility(View.GONE);
                                    lay_time_required.setVisibility(View.VISIBLE);
                                    lay_UsersSearch.setVisibility(View.VISIBLE);
                                }

                                String barcode_type = data.getString("barcode_type");
                                for (int i = 0; i < arrBarcodeTypeList.size(); i++) {
                                    if (barcode_type.equalsIgnoreCase(arrBarcodeTypeList.get(i).barcode_id)) {
                                        dropdownBarTypes.setSelection(i);
                                    }
                                }
                                String product_description = "";
//                                if(data.has("product_description") && !data.isNull("product_description")){
//                                    product_description=data.getString("product_description");
//                                }

//                                et_product_description.setText(product_description);
                                if (data.has("sku") && !data.isNull("sku")) {
                                    String sku = data.getString("sku");
                                    et_product_sku.setText(sku);
                                    et_product_sku.setEnabled(false);

                                }
                                arrIntIDList.clear();
                                arrStringNameList.clear();


                                if(responseObject.has("gallaryImages") && !responseObject.isNull("gallaryImages")){
                                    JSONArray product_detailsdata = responseObject.getJSONArray("gallaryImages");
                                    arrImagesList.clear();
                                    for(int b=0 ; b<product_detailsdata.length();b++){
                                        JSONObject jsonObject = product_detailsdata.getJSONObject(b);
                                        ImagesModel model = new ImagesModel();
                                        model.name =jsonObject.getString("image");
//                                        model.type = galleryImageExtension;
//                                        model.data = galleryImagePath;
                                        arrImagesList.add(model);
                                        imageAdapter.notifyDataSetChanged();

                                    }
                                }
                                if (responseObject.has("product_details") && !responseObject.isNull("product_details")) {
                                    JSONArray product_detailsdata = responseObject.getJSONArray("product_details");
                                    arrVariantImagesList.clear();
                                    for (int a = 0; a < product_detailsdata.length(); a++) {
                                        JSONObject jsonObject = product_detailsdata.getJSONObject(a);
//                                        product_detail_id=jsonObject.getInt("id");
                                        arrIntIDList.add(jsonObject.getInt("id"));
                                        arrStringNameList.add(jsonObject.getString("name"));
//                                        product_detail_Name=jsonObject.getString("name");

                                        JSONArray variationsdata = jsonObject.getJSONArray("variations");
                                        if (variationsdata.length() > 0) {
                                            for (int i = 0; i < 1; i++) {
                                                JSONObject object = variationsdata.getJSONObject(i);
                                                et_purchase_exc.setText(object.getString("default_purchase_price"));
                                                et_purchase_inc.setText(object.getString("dpp_inc_tax"));
                                                exc_inc_tax = Double.valueOf(object.getString("dpp_inc_tax"));
                                                et_margin.setText(object.getString("profit_percent"));
                                                margin_value = Double.valueOf(object.getString("profit_percent"));

                                                et_selling_exc.setText(object.getString("default_sell_price"));
                                                single_variation_id = object.getInt("id");

                                                if(i==0){
                                                    if(object.has("image") && !object.isNull("image")){
                                                        ImagesModel model = new ImagesModel();
                                                        model.name =object.getString("image");

//                                                    txtVariantImg.setText(variantFileName + variantImageExtension);

//                                                        for(int b=0 ;b<arrVariantImagesList.size();b++){
//                                                            if(arrVariantImagesList.get(a).img_Position==inImagePosition){
//                                                                arrVariantImagesList.remove(inImagePosition);
//                                                                break;
//                                                            }
//                                                        }
                                                        arrVariantImagesList.add(model);
                                                        variableValueAdapter.notifyDataSetChanged();
                                                    }

                                                }



                                            }

                                        }

                                    }


                                }


//                                jobSpinnerAdapter.notifyDataSetChanged();
                            }

                        } else {

                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "product/create API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddProduct.this, "Could Not Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void updateProductDetail() {
        AppConstant.showProgress(AddProduct.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("name", et_product_name.getText().toString());
            mainObject.put("brand_id", spinnerModel.brand_id);
            mainObject.put("unit_id", spinnerModel.unit_id);
            mainObject.put("category_id",categoryID);
            mainObject.put("sub_category_id", subCatID);
            mainObject.put("sub_sub_category_id",subsubCatID);
            mainObject.put("sku", et_product_sku.getText().toString());
            mainObject.put("alert_quantity", alert_quantity);
            mainObject.put("barcode_type", spinnerModel.barcode_id);
            mainObject.put("enable_stock", enable_stock);
//            mainObject.put("product_description",et_product_description.getText().toString());
            mainObject.put("single_dpp", exc_inc_tax);
            mainObject.put("single_dpp_inc_tax", exc_inc_tax);
            mainObject.put("profit_percent", margin_value);
            mainObject.put("single_dsp_inc_tax", et_selling_exc.getText().toString());
            mainObject.put("single_dsp", et_selling_exc.getText().toString());
            mainObject.put("single_variation_id", single_variation_id);
//            mainObject.put("model_id", spinnerModel.id);
//            mainObject.put("condition_type", spinnerModel.id);
//            mainObject.put("odometer", et_product_odometer.getText().toString());
//            mainObject.put("torque", et_product_torque.getText().toString());
//            mainObject.put("engine", et_product_engine.getText().toString());
//            mainObject.put("doors", et_product_doors.getText().toString());
//            mainObject.put("transmission_type", spinnerModel.id);
//            mainObject.put("top_speed", et_product_top_speed.getText().toString());
//            mainObject.put("fuel_type", spinnerModel.id);
//            mainObject.put("horsepower", et_product_horse_power.getText().toString());
//            mainObject.put("towing_capacity", et_product_Towing.getText().toString());
//            mainObject.put("interior_color_id", spinnerModel.inteColor_id);
//            mainObject.put("exterior_color_id", spinnerModel.extColor_id);
            mainObject.put("type", product_type);
            mainObject.put("product_description", "");

            mainObject.put("enable_restock", enable_restock);
            mainObject.put("show_website", show_website);
            mainObject.put("show_cart_button", show_cart_button);
            mainObject.put("products_min_order", et_min_order.getText().toString());
            mainObject.put("products_max_stock", et_max_order.getText().toString());
            mainObject.put("style_no", et_style_no.getText().toString());
            mainObject.put("time_type",time_type);
            mainObject.put("time",et_time_Required.getText().toString());
            mainObject.put("tags",arrStringTagsList.toString().replace("[", "").replace("]", ""));
            Log.e("tags", arrStringTagsList.toString());
            String staff= nameUserIDlist.toString().replace("[", "").replace("]", "");
            mainObject.put("service_staffs",staff);
            Log.e("service_staffs", staff);

            mainObject.put("image", productImagePath);
            mainObject.put("image_type", productImageExtenion);

            JSONArray galleryImage = new JSONArray();

            for (int i = 0; i < arrImagesList.size(); i++) {
                ImagesModel model = arrImagesList.get(i);
                JSONObject image = new JSONObject();
                image.put("image", model.data);
                image.put("image_type", model.type);
                galleryImage.put(image);
            }
            mainObject.put("gallery_images", galleryImage);
            Log.e("galleryImage", galleryImage.toString());

            JSONObject productVariation = new JSONObject();
            JSONArray productVariationNew = new JSONArray();

            for (int i = 0; i < arrAllVariationValueList.size(); i++) {

                if (arrStringNameList.size() > i) {
                    VariationValueModel products = arrAllVariationValueList.get(i);
                    JSONObject product = new JSONObject();
                    product.put("name", arrStringNameList.get(i));
                    product.put("variation_template_id", products.variation_template_id);
                    product.put("row_index", product_detail_id);
                    product.put("color_id", products.color_id);

                    String imageData="",imgType="";
                    if(arrVariantImagesList.size() > i){
                        imageData=arrVariantImagesList.get(i).data;
                        imgType=arrVariantImagesList.get(i).type;
                    }
                    product.put("image",imageData);
                    product.put("image_type",imgType);
//                    productVariation.put(product);

                    JSONObject objId = new JSONObject();
                    for (int a = 0; a < products.getVarialtionValues().size(); a++) {
                        AllVariationModel productsVariation = products.getVarialtionValues().get(a);
                        JSONObject objVariation = new JSONObject();
                        objVariation.put("sub_sku", productsVariation.variation_sku);
                        objVariation.put("value", productsVariation.name);
                        objVariation.put("variation_value_id", productsVariation.variation_value_id);
                        objVariation.put("default_purchase_price", productsVariation.dpp_exc_tax);
                        objVariation.put("dpp_inc_tax", productsVariation.dpp_inc_tax);
                        objVariation.put("profit_percent", "");
                        objVariation.put("default_sell_price", productsVariation.var_dsp_exc_tax);
                        objVariation.put("sell_price_inc_tax", productsVariation.var_dsp_exc_tax);
                        objId.put(String.valueOf(productsVariation.id), objVariation);
//                    variations.put("",objId);

                    }
                    product.put("variations_edit", objId);
                    productVariation.put(String.valueOf(arrIntIDList.get(i)), product);

                } else {
                    VariationValueModel products = arrAllVariationValueList.get(i);
                    JSONObject productNew = new JSONObject();
                    productNew.put("variation_template_id", products.variation_template_id);
                    productNew.put("row_index", "");
                    productNew.put("color_id", products.color_id);
                    productVariationNew.put(productNew);

                    JSONArray variations = new JSONArray();

                    for (int a = 0; a < products.getVarialtionValues().size(); a++) {
                        AllVariationModel productsVariation = products.getVarialtionValues().get(a);
                        JSONObject objVariation = new JSONObject();
                        objVariation.put("sub_sku", productsVariation.variation_sku);
                        objVariation.put("value", productsVariation.name);
                        objVariation.put("variation_value_id", productsVariation.variation_value_id);
                        objVariation.put("default_purchase_price", productsVariation.dpp_exc_tax);
                        objVariation.put("dpp_inc_tax", productsVariation.dpp_inc_tax);
                        objVariation.put("profit_percent", "");
                        objVariation.put("default_sell_price", productsVariation.var_dsp_exc_tax);
                        objVariation.put("sell_price_inc_tax", productsVariation.var_dsp_exc_tax);
                        variations.put(objVariation);
                    }
                    productNew.put("variations", variations);

                }

            }
            mainObject.put("product_variation", productVariationNew);
            Log.e("product_variation_edit", productVariation.toString());
            mainObject.put("product_variation_edit", productVariation);
            mainObject.put("submit_type", "submit");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.updateProductDetail("products/" + product_id, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("update Product Response", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {
                                String msg = responseObject.optString("msg");
                                Toast.makeText(AddProduct.this, "" + msg, Toast.LENGTH_LONG).show();

                                if (ProductList.productListActivity != null) {
                                    ProductList.productListActivity.finish();
                                }
                                if (ProductDetailActivity.productDetailActivity != null) {
                                    ProductDetailActivity.productDetailActivity.finish();
                                }
                                Intent i = new Intent(AddProduct.this, ProductList.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                AppConstant.hideProgress();
                                Toast.makeText(AddProduct.this, "Could Not Update Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "products API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getProductTypeEdit() {
        Retrofit retrofit = APIClient.getClientToken(AddProduct.this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("action", "edit");
            mainObject.put("type", "variable");
            mainObject.put("product_id", product_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("products/product_form_part", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("product_form_partEdit", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            arrProductColorList.clear();
                            arrProductTempList.clear();

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                if (responseObject.has("colors") && !responseObject.isNull("colors")) {
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.id = 0;
                                    model.name = "Please Select";
                                    arrProductColorList.add(model);
                                    JSONArray dataObj = responseObject.getJSONArray("colors");
                                    for (int i = 0; i < dataObj.length(); i++) {
                                        JSONObject data = dataObj.getJSONObject(i);
                                        SpinnerSelectionModel model2 = new SpinnerSelectionModel();
                                        model2.id = data.getInt("id");
                                        model2.name = data.getString("name");
                                        arrProductColorList.add(model2);
                                    }
                                }


                                if (responseObject.has("product_variations") && !responseObject.isNull("product_variations")) {
                                    JSONArray dataObj = responseObject.getJSONArray("product_variations");


                                    for (int i = 0; i < dataObj.length(); i++) {
                                        JSONObject variations = dataObj.getJSONObject(i);
                                        VariationValueModel productVariationDataSend = new VariationValueModel();
                                        JSONArray var = variations.getJSONArray("variations");
                                        int color_id = 0;
                                        ArrayList<AllVariationModel> productsVariation = new ArrayList<>();
                                        for (int a = 0; a < var.length(); a++) {
                                            JSONObject variation = var.getJSONObject(a);
                                            AllVariationModel product = new AllVariationModel();
                                            product.id = variation.getInt("id");
                                            product.name = variation.getString("name");
                                            product.variation_sku = variation.getString("sub_sku");
                                            product.dpp_exc_tax = variation.getString("dpp_inc_tax");
                                            product.dpp_inc_tax = variation.getString("dpp_inc_tax");
                                            product.var_exc_margin = variation.getString("profit_percent");
                                            product.var_dsp_exc_tax = variation.getString("sell_price_inc_tax");
                                            product.variation_value_id = variation.getInt("variation_value_id");
                                            if (variation.has("color_id") && !variation.isNull("color_id")) {
                                                color_id = variation.getInt("color_id");
                                            }
                                            productsVariation.add(product);
                                        }
                                        productVariationDataSend.setVarialtionValues(productsVariation);
                                        productVariationDataSend.variation_template_id = variations.getInt("variation_template_name_id");
                                        productVariationDataSend.color_id = color_id;
                                        productVariationDataSend.position = i;
                                        arrAllVariationValueList.add(productVariationDataSend);

//                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
//                                        model.id = 0;
//                                        model.name = "Please Select";
//                                        arrProductTempList.add(model);
//                                        for (int a = 0; a < dataObj.length(); a++) {
//                                            JSONObject data = dataObj.getJSONObject(a);
//                                            SpinnerSelectionModel model2 = new SpinnerSelectionModel();
//                                            model2.id = data.getInt("id");
//                                            model2.name = data.getString("name");
//                                            arrProductTempList.add(model2);
//                                        }
                                    }

                                    Gson gson = new Gson();
                                    ed_cartSave.putString("myVariation", gson.toJson(arrAllVariationValueList));
                                    ed_cartSave.commit();
                                    arrVariationList.clear();

                                    if (arrAllVariationValueList.size() == 1) {
                                        arrVariationList.add(arrAllVariationValueList.size());
                                    } else {
                                        arrVariationList.clear();
                                        arrVariationList.add(1);
                                        arrVariationList.add(arrAllVariationValueList.size());
                                    }


//                                    variableValueAdapter = new VariableProductAdapter(AddProduct.this, arrVariationList, arrProductTempList, arrProductColorList);
//                                    recycler_variable_product.setAdapter(variableValueAdapter);


                                    variableValueAdapter.notifyDataSetChanged();

                                }

                            } else {
                                AppConstant.hideProgress();
//                                Toast.makeText(AddProduct.this, "Could Not Update Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "product_form_part API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getProductType() {
        Retrofit retrofit = APIClient.getClientToken(AddProduct.this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("action", "add");
            mainObject.put("type", "variable");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("products/product_form_part", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("product_form_part", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            arrProductColorList.clear();
                            arrProductTempList.clear();

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                if (responseObject.has("colors") && !responseObject.isNull("colors")) {
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.id = 0;
                                    model.name = "Please Select";
                                    arrProductColorList.add(model);
                                    JSONArray dataObj = responseObject.getJSONArray("colors");
                                    for (int i = 0; i < dataObj.length(); i++) {
                                        JSONObject data = dataObj.getJSONObject(i);
                                        SpinnerSelectionModel model2 = new SpinnerSelectionModel();
                                        model2.id = data.getInt("id");
                                        model2.name = data.getString("name");
                                        arrProductColorList.add(model2);
                                    }
                                }
                                if (responseObject.has("variation_templates") && !responseObject.isNull("variation_templates")) {
                                    JSONArray dataObj = responseObject.getJSONArray("variation_templates");
                                    SpinnerSelectionModel model = new SpinnerSelectionModel();
                                    model.id = 0;
                                    model.name = "Please Select";
                                    arrProductTempList.add(model);
                                    for (int i = 0; i < dataObj.length(); i++) {
                                        JSONObject data = dataObj.getJSONObject(i);
                                        SpinnerSelectionModel model2 = new SpinnerSelectionModel();
                                        model2.id = data.getInt("id");
                                        model2.name = data.getString("name");
                                        arrProductTempList.add(model2);
                                    }
                                }

                                variableValueAdapter.notifyDataSetChanged();

                            } else {
                                AppConstant.hideProgress();
//                                Toast.makeText(AddProduct.this, "Could Not Update Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "product_form_part API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void getTags() {
        Retrofit retrofit = APIClient.getClientToken(AddProduct.this);
        JSONObject mainObject = new JSONObject();
        try {
            if(categoryID==0){
                mainObject.put("category_id", "");
            }
            else{
                mainObject.put("category_id", categoryID);
            }
            if(subCatID==0){
                mainObject.put("sub_category_id","");
            }
            else{
                mainObject.put("sub_category_id",subCatID);
            }
            if(subsubCatID==0){
                mainObject.put("sub_sub_category_id","");
            }else{
                mainObject.put("sub_sub_category_id", subsubCatID);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("getTags", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            arrStringTagsList.clear();
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("products/getTags", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                String tags = responseObject.optString("tags");
//                                arrStringTagsList = Arrays.asList(tags.split("\\s*,\\s*"));
//                                tagAdapter = new TagAdapter(AddProduct.this, arrStringTagsList);
//                                recycler_tags.setAdapter(tagAdapter);
//                                for(int i = 0; i < mStringArray.length ; i++){
//                                    Log.e("string is",(String)mStringArray[i]);
//                                }
//                                List<String> myList = new ArrayList<String>(tags.length());
//                                for (int i = 0; i < tags.length(); i++) {
//                                    myList.add(String.valueOf(tags.charAt(i)));
//                                }

//                                String[] stringArray = arrStringTagsList.toArray(new String[0]);
                                Log.e("tags", arrStringTagsList.toString());

                                String[] elements = tags.split(",");

                                for(int i=0 ; i <elements.length ; i++){
                                    arrStringTagsList.add(elements[i]);
                                }

                                tagAdapter.notifyDataSetChanged();
                                Log.e("arrStringTagsList",arrStringTagsList.toString());

                            } else {
                                AppConstant.hideProgress();
//                                Toast.makeText(AddProduct.this, "Could Not Update Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "getTags API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

//        arrAllVariationValueList.clear();

        if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<VariationValueModel>>() {
            }.getType();
            String strMyCart = sp_cartSave.getString("myVariation", "");
            arrAllVariationValueList = (ArrayList<VariationValueModel>) gson.fromJson(strMyCart, type);

            variableValueAdapter.notifyDataSetChanged();

        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (comingFrom.equalsIgnoreCase("fromProductDetail")) {
                getSupportActionBar().setTitle("EDIT PRODUCT");
            } else {

                if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {//if not cart empty.
                    ed_cartSave.remove("myVariation");
                    ed_cartSave.commit();

                }
                arrAllVariationValueList.clear();
                getSupportActionBar().setTitle("ADD PRODUCT");
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void getSubcategoriesList() {
        AppConstant.showProgress(AddProduct.this,false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("getSubCategories?category_id=" +categoryID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            arrSubCategoryList.clear();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("subCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray catObj = responseObject.getJSONArray("sub_categories");
//                                CategoryModel catmodel = new CategoryModel();
//                                catmodel.id = 0;
//                                catmodel.name = "Select Sub Category";
//                                arrSubCategoryList.add(catmodel);
                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubCategoryList.add(model);
                                }

//                                for(int b=0;b<arrSubCategoryList.size();b++){
//                                    if(subCatID==arrSubCategoryList.get(b).sub_cat_id){
//                                        spinnerModel.sub_cat_id = arrCategoryList.get(b).sub_cat_id;
//                                        dropdownSubCategory.setSelection(b);
//                                        break;
//                                    }
//                                }
                                getTags();

                            }

//                            SpinnerSelectionAdapter subCategoryAdapter = new SpinnerSelectionAdapter(AddProduct.this, arrSubCategoryList);
//                            dropdownSubCategory.setAdapter(subCategoryAdapter);


                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "sub - category list API", "(getSubCategories)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddProduct.this, "Could Not Load SubCategory list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Load SubCategory List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void getSubSubcategoriesList() {
        AppConstant.showProgress(AddProduct.this,false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("getSubCategories?sub_category_id=" +subCatID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            arrSubSubCategoryList.clear();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("subCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray catObj = responseObject.getJSONArray("sub_sub_categories");

//                                CategoryModel catmodel = new CategoryModel();
//                                catmodel.id = 0;
//                                catmodel.name = "Select Sub Sub Category";
//                                arrSubSubCategoryList.add(catmodel);
                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrSubSubCategoryList.add(model);

                                }

//                                for(int b=0;b<arrSubSubCategoryList.size();b++){
//                                    if(subsubCatID==arrSubSubCategoryList.get(b).sub_sub_cat_id){
//                                        spinnerModel.sub_sub_cat_id = arrSubSubCategoryList.get(b).sub_sub_cat_id;
//                                        dropdownSubSubCategory.setSelection(b);
//                                    }
//                                }

//                                SpinnerSelectionAdapter subCategoryAdapter = new SpinnerSelectionAdapter(AddProduct.this, arrSubSubCategoryList);
//                                dropdownSubSubCategory.setAdapter(subCategoryAdapter);


                            }

                            getTags();
//                            setAdpters();
//                            setFisrtItems();

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(AddProduct.this, "sub sub category list API", "(getSubSubCategories)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddProduct.this, "Could Not Load SubSubCategory list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddProduct.this, "Could Not Load SubSubCategory List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


    public void checkStyleNo(String check_no) {
        Retrofit retrofit = APIClient.getClientToken(AddProduct.this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("style_no",check_no);
            mainObject.put("product_id", product_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("products/check-style-no", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("check-style-no", respo);
                            if(respo.equalsIgnoreCase("true")){

                            }else{
                                et_style_no.setText("");
                            }
//                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddProduct.this, "check-style-no API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            AppConstant.showProgress(AddProduct.this, false);
            if (requestCode == 0) {
                final Uri imageUri = data.getData();
                String imagepath = getRealPathFromURI(imageUri, 0);
                doCompress(new File(imagepath), 0);

            } else if (requestCode == 1) {
                final Uri imageUri = data.getData();
                String imagepath = getRealPathFromURI(imageUri, 1);
                doCompress(new File(imagepath), 1);

            }else if (requestCode == 2) {
                final Uri imageUri = data.getData();
                String imagepath = getRealPathFromURI(imageUri, 2);
                doCompress(new File(imagepath), 2);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri, int type) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();

        if (type == 0) {
            image_product.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(cursor.getString(column_index))
                    .thumbnail(0.5f)
                    .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher))
                    .into(image_product);
        }

        return cursor.getString(column_index);
    }

    public void doCompress(File actualImage, int type) {
        AppConstant.hideProgress();
        new Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        // compressedImage = file;
                        if (type == 0) {
                            productImagePath = file.getAbsolutePath();
                            String newPAth = productImagePath.replace(file.getName(), "");
                            File Remname = new File(newPAth, UUID.randomUUID().toString() + file.getName().substring(file.getName().lastIndexOf(".")));

                            if (file.exists()) {
                                file.renameTo(Remname);
                            }
                            productImagePath = Remname.getAbsolutePath();
                            Bitmap bitmap = BitmapFactory.decodeFile(productImagePath);
                            productImagePath = ConvertBitmapToString(bitmap, type);

//                            Log.e("productImagePath", productImagePath);
                        } else if(type == 1) {
                            galleryImagePath = file.getAbsolutePath();
                            String newPAth = galleryImagePath.replace(file.getName(), "");
                            File Remname = new File(newPAth, UUID.randomUUID().toString() + file.getName().substring(file.getName().lastIndexOf(".")));

                            if (file.exists()) {
                                file.renameTo(Remname);
                            }
                            galleryImagePath = Remname.getAbsolutePath();
                            Bitmap bitmap = BitmapFactory.decodeFile(galleryImagePath);
                            galleryImagePath = ConvertBitmapToString(bitmap, type);

                            ImagesModel model = new ImagesModel();
                            model.name = galleryfilename + galleryImageExtension;
                            model.type = galleryImageExtension;
                            model.data = galleryImagePath;
                            arrImagesList.add(model);
                            imageAdapter.notifyDataSetChanged();

//                            Log.e("galleryImagePath", galleryImagePath);
                        }else{

                            variantImagePath = file.getAbsolutePath();
                            String newPAth = variantImagePath.replace(file.getName(), "");
                            File Remname = new File(newPAth, UUID.randomUUID().toString() + file.getName().substring(file.getName().lastIndexOf(".")));

                            if (file.exists()) {
                                file.renameTo(Remname);
                            }
                            variantImagePath = Remname.getAbsolutePath();
                            Bitmap bitmap = BitmapFactory.decodeFile(variantImagePath);
                            variantImagePath = ConvertBitmapToString(bitmap, type);

                            ImagesModel model = new ImagesModel();
                            model.name = variantFileName + variantImageExtension;
                            model.type = variantImageExtension;
                            model.data = variantImagePath;
                            model.img_Position =inImagePosition ;

                            txtVariantImg.setText(variantFileName + variantImageExtension);

                            for(int a=0 ;a<arrVariantImagesList.size();a++){
                                if(arrVariantImagesList.get(a).img_Position==inImagePosition){
                                    arrVariantImagesList.remove(inImagePosition);
                                    break;
                                }
                            }
                            arrVariantImagesList.add(inImagePosition,model);
//                            variableValueAdapter.notifyItemChanged(inImagePosition);

//                            boolean entered=false;
//                            for(int a=0 ; a<arrVariantImagesList.size();a++){
//                                if(arrVariantImagesList.get(a).img_Position != inImagePosition){
//                                    model.img_Position=inImagePosition;
//                                    entered=true;
//                                }
//                            }
//                            if(entered){
//                                arrVariantImagesList.add(inImagePosition,model);
//                                variableValueAdapter.notifyItemChanged(inImagePosition);
//                            }else{
//                                arrVariantImagesList.add(model);
//                                variableValueAdapter.notifyDataSetChanged();
//                            }



//                            Log.e("galleryImagePath", galleryImagePath);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        Log.e("errror", throwable.toString());
                        //showError(throwable.getMessage());
                    }
                });
    }

    public String ConvertBitmapToString(Bitmap bitmap, int type) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        try {
            if (type == 0) {
                String encodedString = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
                String filename = productImagePath.substring(productImagePath.lastIndexOf("/") + 1);
                filename = filename.substring(0, filename.lastIndexOf("."));
                productImageExtenion = productImagePath.substring(productImagePath.lastIndexOf("."));
                Log.e("extension", productImageExtenion);
                txt_product_image.setText(filename + productImageExtenion);

            } else if(type==1){
                galleryfilename = galleryImagePath.substring(galleryImagePath.lastIndexOf("/") + 1);
                galleryfilename = galleryfilename.substring(0, galleryfilename.lastIndexOf("."));
                galleryImageExtension = galleryImagePath.substring(galleryImagePath.lastIndexOf("."));

            }else if(type==2){

                variantFileName = variantImagePath.substring(variantImagePath.lastIndexOf("/") + 1);
                variantFileName = variantFileName.substring(0, variantFileName.lastIndexOf("."));
                variantImageExtension = variantImagePath.substring(variantImagePath.lastIndexOf("."));

            }

//            Log.e("Base 64", Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

//    public static String ConvertBitmapToString(Bitmap bitmap) {
//        String encodedImage = "";
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        try {
//            encodedImage = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
//
//            Log.e("Base 64",Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
//            extensionName = path.substring(path.lastIndexOf("."));
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//    }

}
