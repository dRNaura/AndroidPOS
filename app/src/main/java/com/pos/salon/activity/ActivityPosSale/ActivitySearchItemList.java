package com.pos.salon.activity.ActivityPosSale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pos.salon.R;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.adapter.ProductsAdapters.SearchItemListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.model.searchData.SearchItemResponse;
import com.pos.salon.utilConstant.AppConstant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.ActivityPosSale.ActivityPosTerminalDropdown.dropdownActivity;

public class ActivitySearchItemList extends AppCompatActivity {
    ImageView fb_barcode;
    Toolbar toolbar;
    RecyclerView rlProductList;
    SearchItemListAdapter adapter;
    ArrayList<SearchItem> itemArray = new ArrayList<SearchItem>();
    ArrayList<SearchItem> arrCartProducts = new ArrayList<SearchItem>();
    LinearLayout llAlert;
    ImageView imgNotFount, imgCart, scanbarcode, img_clear_search_product, home_icon;
    RelativeLayout rlNext;
    EditText searchEditBox;
    BusinessLocationData locationData;
    CurriencyData currencyData;
    CustomerListData customerData;
    //    DatabaseHelper dbHelper;
    LinearLayout llParent;
    String quaryText = "";
    String orderr_type = "", tablee_id = "", waiterr_id = "";
    ProgressBar progressBar;
    private boolean isScrolling = true;
    int count = 0;
    int currentItems, totalItems, scrollItems;
    public static Activity posSearchItemListActivity;
    TextView tv_productcount;
    public static String strScannedBarCode = "";
    LinearLayoutManager mLayoutManager;
    SharedPreferences sp_countproduct, sp_modifiers;
    SharedPreferences.Editor ed_countproduct, ed_modifiers;
    SharedPreferences sp_cartSave; // for fetch data from preference
    SharedPreferences.Editor ed_cartSave; //for save data in preference.
    String comingFrom;
    public boolean isKeyBoardOpen;
    int productOrService = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posterminal_search);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        posSearchItemListActivity = this;
//        dbHelper = new DatabaseHelper(ActivitySearchItemList.this);
        fb_barcode = findViewById(R.id.fb_barcode);

        //cart product count set.
        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();


        sp_modifiers = getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE);
        ed_modifiers = sp_modifiers.edit();

        comingFrom = getIntent().getStringExtra("comingFrom");

        llParent = (LinearLayout) findViewById(R.id.llParent);
        scanbarcode = (ImageView) findViewById(R.id.scanbarcode);
        imgNotFount = (ImageView) findViewById(R.id.imgNotFount);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        tv_productcount = (TextView) findViewById(R.id.tv_productcount);
        searchEditBox = (EditText) findViewById(R.id.searchEditBox);
        llAlert = (LinearLayout) findViewById(R.id.llAlert);
        rlNext = (RelativeLayout) findViewById(R.id.rlNext);
        home_icon = findViewById(R.id.home_icon);
        img_clear_search_product = (ImageView) findViewById(R.id.img_clear_search_product);

        progressBar = findViewById(R.id.progressBar);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this);
        locationData = (BusinessLocationData) getIntent().getSerializableExtra("location");
        currencyData = (CurriencyData) getIntent().getSerializableExtra("currency");
        productOrService = getIntent().getIntExtra("productOrService", 0);

        if (!comingFrom.equalsIgnoreCase("fromSaleDetail")) //if not coming from sale detail.
        {
            customerData = (CustomerListData) getIntent().getSerializableExtra("customer");
            orderr_type = getIntent().getStringExtra("selected_ordertype");
            tablee_id = getIntent().getStringExtra("tablee_id");
            waiterr_id = getIntent().getStringExtra("waiterr_id");
            home_icon.setVisibility(View.VISIBLE);
        } else if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {

            home_icon.setVisibility(View.GONE);
        }


        setWidget();
        setBackNavgation();
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (comingFrom.equalsIgnoreCase("ActivityPosTerminalDropDown")) {
                arrCartProducts.clear();

                //clear cart product data if comes on this screen.
                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                    ed_cartSave.remove("myCart");
                    ed_cartSave.commit();

                }
                // added cart badge here
                ed_countproduct.remove("countt");
                ed_countproduct.commit();

                ed_modifiers.clear();
                ed_modifiers.commit();

                itemArray.clear();

                adapter.updateNewItem(itemArray);
                if (dropdownActivity != null) {
                    dropdownActivity.finish();
                }
                if (HomeActivity.homeActivity != null) {
                    HomeActivity.homeActivity.finish();
                }
                Intent i = new Intent(ActivitySearchItemList.this, HomeActivity.class);

                startActivity(i);

                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
//                } else {
//
//                }
            }
        });

        if (productOrService == 0) {
            searchEditBox.setHint("Search Service");
            fb_barcode.setVisibility(View.GONE);
        } else {
            searchEditBox.setHint("Enter Product Name/SKU/Brand");
            fb_barcode.setVisibility(View.VISIBLE);
        }

//        List<String> arraySpinner=new ArrayList<>();
//        arraySpinner.add("Product");
//       arraySpinner.add("Service");
//        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
//        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdownProductService.setAdapter(adapterType);

//        String aa="Product";
//        if(comingFrom.equalsIgnoreCase("fromSaleDetail")) {
////            for (int i = 0; i < arraySpinner.size(); i++) {
////                if (aa.equalsIgnoreCase(arraySpinner.get(i))) {
////                    dropdownProductService.setSelection(i);
////                }
////
////            }
//            dropdownProductService.setEnabled(false);
//        }
//            dropdownProductService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if(dropdownProductService.isEnabled()){
//                    //clear cart product data if comes on this screen.
//                    if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
//
//                        ed_cartSave.remove("myCart");
//                        ed_cartSave.commit();
//
//                    }
//                    // added cart badge here
//                    ed_countproduct.remove("countt");
//                    ed_countproduct.commit();
//
//                    ed_modifiers.clear();
//                    ed_modifiers.commit();
//
//                    itemArray.clear();
//
//                    adapter.updateNewItem(itemArray);
//
//                    count=0;
//                    tv_productcount.setBackgroundResource(0);
//                    tv_productcount.setText("");
//
//                    if(position==0){
//                        productOrService = 1;
//                    }else{
//                        productOrService = 0;
//                    }
//                    searchEditBox.setText("");
//                    if(productOrService==0){
//                        searchEditBox.setHint("Search Service");
//                        fb_barcode.setVisibility(View.GONE);
//                    }else{
//                        searchEditBox.setHint("Enter Product Name/SKU/Brand");
//                        fb_barcode.setVisibility(View.VISIBLE);
//                    }
//                    arrCartProducts.clear();
//
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                ed_cartSave.commit();

                // added cart badge here
                ed_countproduct.putString("countt", String.valueOf(arrCartProducts.size()));
                ed_countproduct.commit();

                if (comingFrom.equalsIgnoreCase("fromSaleDetail")) //if coming from sale detail.
                {
                    finish();

                } else {
                    Intent intent = new Intent(ActivitySearchItemList.this, ActivityPosItemList.class);

                    intent.putExtra("location", locationData);
                    intent.putExtra("currency", currencyData);
                    intent.putExtra("customer", customerData);
                    intent.putExtra("selected_ordertype", orderr_type);
                    intent.putExtra("tablee_id", tablee_id);
                    intent.putExtra("waiterr_id", waiterr_id);
                    intent.putExtra("comingFrom", "fromSearchItem");
                    intent.putExtra("parentfrom", "fromPosTerminalDrop");
                    intent.putExtra("productOrService", productOrService);

                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

                searchEditBox.setText("");
                if (itemArray.size() > 0)
                    itemArray.clear();
                adapter.updateNewItem(itemArray);
            }
        });

    }

    private void setWidget() {

        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // data pass on click bottom cart btn or top right cart btn.

                Gson gson = new Gson();
                ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                ed_cartSave.commit();

                // added cart badge here
                ed_countproduct.putString("countt", String.valueOf(arrCartProducts.size()));
                ed_countproduct.commit();

                if (comingFrom.equalsIgnoreCase("fromSaleDetail")) //if coming from sale detail.
                {
                    finish();

                } else {

                    Intent intent = new Intent(ActivitySearchItemList.this, ActivityPosItemList.class);

                    intent.putExtra("location", locationData);
                    intent.putExtra("currency", currencyData);
                    intent.putExtra("customer", customerData);
                    intent.putExtra("selected_ordertype", orderr_type);
                    intent.putExtra("tablee_id", tablee_id);
                    intent.putExtra("waiterr_id", waiterr_id);
                    intent.putExtra("comingFrom", "fromSearchItem");
                    intent.putExtra("parentfrom", "fromPosTerminalDrop");
                    intent.putExtra("productOrService", productOrService);

                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

                searchEditBox.setText("");
                if (itemArray.size() > 0)
                    itemArray.clear();
                adapter.updateNewItem(itemArray);
            }
        });

        rlProductList = (RecyclerView) findViewById(R.id.rlProductList);

        adapter = new SearchItemListAdapter(this, itemArray, new SearchItemListAdapter.CilckListener() {
            @Override
            public void onItemClick(int position, SearchItem item1) {

                if (!item1.isActiveProduct()) {
                    if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
                        item1.setQty_available("0");
                    }
                    if (productOrService == 0) {
                        item1.setQty_available("1");
                    }
                    double dStockQuantityAvail = Double.valueOf(item1.getQty_available());  // 3
                    int iStockQuantityAvail = (int) dStockQuantityAvail;

                    if (iStockQuantityAvail > 0) {

                        int iProductUpdatedQuantity = 1;

                        item1.setActiveProduct(true);

                        // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                        item1.setUnitFinalPrice(item1.getSelling_price());

                        boolean isAlreadyAdded = false;
                        for (int i = 0; i < arrCartProducts.size(); i++) {
                            SearchItem objItem = arrCartProducts.get(i);

                            if (objItem.getProduct_id().equalsIgnoreCase(item1.getProduct_id()) && objItem.getVariation_id().equalsIgnoreCase(item1.getVariation_id())) {
                                if (productOrService == 0) {
                                    objItem.setQuantity(objItem.getQuantity() + 1);
                                } else {
                                    if (objItem.getQuantity() < iStockQuantityAvail) {
                                        objItem.setQuantity(objItem.getQuantity() + 1);

                                    } else {
                                        Toast.makeText(ActivitySearchItemList.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                isAlreadyAdded = true;

                                break;
                            }
                        }

                        if (isAlreadyAdded) {
//                            if(iProductUpdatedQuantity > iStockQuantityAvail)
//                            {
//                                Toast.makeText(ActivitySearchItemList.this, "This Product has no more stock available For Now", Toast.LENGTH_LONG).show();
//
//                                return;
//                            }
                        } else {
//                            AppConstant.showToast(ActivitySearchItemList.this,"Item Added To Cart");
                            arrCartProducts.add(item1);
                            // long insert = dbHelper.insertItem(item1);

                            count = count + 1;
                        }

                        // added cart badge here
                        ed_countproduct.putString("countt", String.valueOf(count));
                        ed_countproduct.commit();
                        tv_productcount.setText(sp_countproduct.getString("countt", ""));
                        // Toast.makeText(ActivitySearchItemList.this, "" +count, Toast.LENGTH_LONG).show();

                        Gson gson = new Gson();
                        ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                        ed_cartSave.commit();

                        tv_productcount.setBackgroundResource(R.drawable.circle_back);

                    } else {
                        Toast.makeText(ActivitySearchItemList.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {
                    item1.setActiveProduct(false);


                    // dbHelper.deleteItem(item1);
                    arrCartProducts.remove(item1); // remove the item.

                    //  arrCartOrignalProducts.remove(item1); //remove selected cart products.


                    count = count - 1;

                    if (count == 0) {
                        tv_productcount.setText("");
                        tv_productcount.setBackgroundResource(0);

                    } else {
                        ed_countproduct.putString("countt", String.valueOf(count));
                        ed_countproduct.commit();
                        tv_productcount.setText(sp_countproduct.getString("countt", ""));
                        //     Toast.makeText(ActivitySearchItemList.this, "" +count, Toast.LENGTH_LONG).show();

                        tv_productcount.setBackgroundResource(R.drawable.circle_back);
                    }

                }
                searchEditBox.setText("");
                try {
                    itemArray.set(position, item1);
                    adapter.updateNewItem(itemArray);


                } catch (Exception e) {
                    Log.e("exception", e.toString());
                }


//------------- open keyboard on click of add product from product list
                //AppConstant.hideKeyboardFrom(ActivitySearchItemList.this);
            }
        });
        rlProductList.setHasFixedSize(true);
        rlProductList.setLayoutManager(mLayoutManager);
        rlProductList.setItemAnimator(new DefaultItemAnimator());
        rlProductList.setAdapter(adapter);

        searchEditBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isKeyBoardOpen = true;
                return false;
            }
        });


        searchEditBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (AppConstant.isNetworkAvailable(ActivitySearchItemList.this)) {
                        img_clear_search_product.setVisibility(View.VISIBLE);
                        quaryText = s.toString();
                        itemArray.clear();
                        searchItemCollection(quaryText, false, "textChanged");
                    }
                } else {
                    img_clear_search_product.setVisibility(View.GONE);
//                    searchItemCollection("", false, "textChanged");
                    itemArray.clear();
                    adapter.updateNewItem(itemArray);
                    imgNotFount.setVisibility(View.VISIBLE);
                    llAlert.setVisibility(View.VISIBLE);
                }
            }
        });
        img_clear_search_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditBox.setText("");
                itemArray.clear();
                adapter.updateNewItem(itemArray);
                imgNotFount.setVisibility(View.VISIBLE);
                llAlert.setVisibility(View.VISIBLE);
            }
        });


        rlProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            searchItemCollection(quaryText, true, "onScroll");
                        }
                    }
                }
            }
        });


        fb_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strScannedBarCode = "";
                Intent intent = new Intent(ActivitySearchItemList.this, ScannerActivity.class);
                intent.putExtra("location", locationData);
                intent.putExtra("currency", currencyData);
                intent.putExtra("customer", customerData);
                intent.putExtra("selected_ordertype", orderr_type);
                intent.putExtra("tablee_id", tablee_id);
                intent.putExtra("waiterr_id", waiterr_id);
                intent.putExtra("comingFrom", "fromSearchItem");
                intent.putExtra("from", "searchActivity");
                startActivity(intent);
//                intent.putExtra("from", "searchActivity");
//                intent.putExtra("location", locationData);
//                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                searchEditBox.setText("");

//                final Dialog fromPrintDialog = new Dialog(ActivitySearchItemList.this);
//                fromPrintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                fromPrintDialog.setContentView(R.layout.select_printer_dialog);
//                fromPrintDialog.setCancelable(true);
//
//                Window window = fromPrintDialog.getWindow();
//                window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
////                window.setGravity(Gravity.CENTER);
//
//                TextView txt_device5 = fromPrintDialog.findViewById(R.id.txt_zcs_printer);
//                TextView txt_device7 = fromPrintDialog.findViewById(R.id.txt_pt_printer);
//                TextView txt_printer_jeoPower = fromPrintDialog.findViewById(R.id.txt_printer_jeoPower);
//                TextView txt_cancel = fromPrintDialog.findViewById(R.id.txt_cancel);
//
//                fromPrintDialog.show();
//
//                txt_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fromPrintDialog.dismiss();
//                    }
//                });
//
//                txt_device5.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fromPrintDialog.dismiss();
//                        strScannedBarCode = "";
//                        Intent intent = new Intent(ActivitySearchItemList.this, ScanActivityDevice5.class);
//                        intent.putExtra("comingFrom", "searchActivity");
//                        startActivity(intent);
//                        searchEditBox.setText("");
//                    }
//                });
////                txt_device7.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        fromPrintDialog.dismiss();
////                        strScannedBarCode = "";
////                        Intent intent = new Intent(ActivitySearchItemList.this, ScanActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
////                    }
////                });
//
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        fromPrintDialog.dismiss();
////                        strScannedBarCode = "";
////                        Intent intent = new Intent(ActivitySearchItemList.this, CaptureActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
//                        strScannedBarCode = "";
//                        Intent intent = new Intent(ActivitySearchItemList.this, ScannerActivity.class);
//                        intent.putExtra("comingFrom", "searchActivity");
//                        intent.putExtra("location", locationData);
//                        startActivity(intent);
//                        searchEditBox.setText("");
//                    }
//                });

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent in) {
        super.onActivityResult(requestCode, resultCode, in);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, in);
        if (scanningResult != null) {
            String contents = in.getStringExtra("SCAN_RESULT");
            String format = in.getStringExtra("SCAN_RESULT_FORMAT");
            Toast.makeText(ActivitySearchItemList.this, "Content-" + contents + " Format-" + format, Toast.LENGTH_SHORT).show();

        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setTitle(R.string.posterminal);

            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle(R.string.posterminal);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (comingFrom.equalsIgnoreCase("ActivityPosTerminalDropDown")) {
                            arrCartProducts.clear();

                            //clear cart product data if comes on this screen.
                            if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                ed_cartSave.remove("myCart");
                                ed_cartSave.commit();

                            }
                            // added cart badge here
                            ed_countproduct.remove("countt");
                            ed_countproduct.commit();

                            ed_modifiers.clear();
                            ed_modifiers.commit();

                            itemArray.clear();

                            adapter.updateNewItem(itemArray);
                            if (dropdownActivity != null) {
                                dropdownActivity.finish();
                            }
                            Intent i = new Intent(ActivitySearchItemList.this, ActivityPosTerminalDropdown.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        } else {

                        }
                    }
                });
            }
        }
    }


    private void searchItemCollection(final String quaryText, boolean isScrollDOwn, final String strFromWhere) {

//        AppConstant.showProgress(this,false);

        if (isScrollDOwn) {
            progressBar.setVisibility(View.VISIBLE);

        } else {

            //    AppConstant.showProgress(this, false);
        }

        if (strFromWhere.equalsIgnoreCase("scanBarCode")) {
            AppConstant.showProgress(this, false);
        }

        Retrofit retrofit = APIClient.getClientToken(ActivitySearchItemList.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<SearchItemResponse> call = apiService.getSearchItem("get-product-suggestion?location_id=" + locationData.getId() + "&term=" + quaryText + "&limit=" + itemArray.size() + "&enable_stock=" + productOrService);
            call.enqueue(new Callback<SearchItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<SearchItemResponse> call, @NonNull Response<SearchItemResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        strScannedBarCode = "";
                        Log.e("Product Search : ", "" + response.body());
                        SearchItemResponse searchResponse = response.body();
                        if (searchResponse.isSuccess()) {
                            if (strFromWhere.equalsIgnoreCase("textChanged")) {
                                itemArray.clear();
//                                strScannedBarCode="";
                            } else if (strFromWhere.equalsIgnoreCase("scanBarCode")) {//direct add to cart product.

                                ArrayList<SearchItem> arrScannedProducts = searchResponse.getProduct_list();


                                if (arrScannedProducts.size() > 0) {
                                    SearchItem item1 = arrScannedProducts.get(0);

                                    if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
                                        item1.setQty_available("0");
                                        Toast.makeText(ActivitySearchItemList.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();

                                        return;
                                    }

                                    double dStockQuantityAvail = Double.valueOf(item1.getQty_available());
                                    int iStockQuantityAvail = (int) dStockQuantityAvail;

                                    if (iStockQuantityAvail > 0) {


                                        item1.setActiveProduct(true);

                                        // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                                        item1.setUnitFinalPrice(item1.getSelling_price());

                                        boolean isAlreadyAdded = false;
                                        for (int i = 0; i < arrCartProducts.size(); i++) {
                                            SearchItem objItem = arrCartProducts.get(i);


                                            if (objItem.getProduct_id().equalsIgnoreCase(item1.getProduct_id()) && objItem.getVariation_id().equalsIgnoreCase(item1.getVariation_id())) {
                                                if (objItem.getQuantity() < iStockQuantityAvail) {
                                                    AppConstant.showToast(ActivitySearchItemList.this, "Item Added To Cart");
                                                    objItem.setQuantity(objItem.getQuantity() + 1);

                                                } else {
                                                    Toast.makeText(ActivitySearchItemList.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                isAlreadyAdded = true;

                                                break;
                                            }
                                        }

                                        if (isAlreadyAdded) {
//                                            if(iProductUpdatedQuantity > iStockQuantityAvail)
//                                            {
//                                                Toast.makeText(ActivitySearchItemList.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_LONG).show();
//                                                return;
//                                            }

                                        } else {
                                            AppConstant.showToast(ActivitySearchItemList.this, "Item Added To Cart");
                                            arrCartProducts.add(item1);
                                            // long insert = dbHelper.insertItem(item1);

                                            count = count + 1;
                                        }


                                        // added cart badge here
                                        ed_countproduct.putString("countt", String.valueOf(count));
                                        ed_countproduct.commit();
                                        tv_productcount.setText(sp_countproduct.getString("countt", ""));
                                        // Toast.makeText(ActivitySearchItemList.this, "" +count, Toast.LENGTH_LONG).show();

                                        Gson gson = new Gson();
                                        ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                                        ed_cartSave.commit();

                                        tv_productcount.setBackgroundResource(R.drawable.circle_back);

                                    } else {

                                        Toast.makeText(ActivitySearchItemList.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                            }

                            if (!quaryText.equalsIgnoreCase("") && !strFromWhere.equals("scanBarCode")) {
                                itemArray.addAll(searchResponse.getProduct_list());
                            }
                            // itemArray = searchResponse.getProduct_list();

                            if (searchEditBox.getText().toString().isEmpty() && !strFromWhere.equals("scanBarCode")) {
                                itemArray.clear();
                            }

                        }

                        if (itemArray.size() > 0 || strFromWhere.equalsIgnoreCase("scanBarCode")) {
                            if (strFromWhere.equalsIgnoreCase("scanBarCode")) {
                                img_clear_search_product.setVisibility(View.GONE);
                            } else {
                                img_clear_search_product.setVisibility(View.VISIBLE);
                            }
                            imgNotFount.setVisibility(View.GONE);
                            llAlert.setVisibility(View.GONE);
                        } else {
                            img_clear_search_product.setVisibility(View.GONE);
                            imgNotFount.setVisibility(View.VISIBLE);
                            llAlert.setVisibility(View.VISIBLE);
                        }

                        adapter.updateNewItem(itemArray);

//                        if(itemArray.size()>0 && isKeyBoardOpen){
//                            addProductDialog();
//                        }


                    } else {
                        //AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(ActivitySearchItemList.this, "get-product-suggestion API", "(ActivitySearchItemList Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(ActivitySearchItemList.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<SearchItemResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    Log.e("Throwable ", t.toString());
                    Toast.makeText(ActivitySearchItemList.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (comingFrom.equalsIgnoreCase("ActivityPosTerminalDropDown")) {

            // arrCartOrignalProducts.clear();
            arrCartProducts.clear();

            ed_cartSave.remove("myCart");
            ed_cartSave.commit();

            ed_modifiers.clear();
            ed_modifiers.commit();

            ed_countproduct.clear();
            ed_countproduct.commit();

            itemArray.clear(); //for barocde scanning

            adapter.updateNewItem(itemArray);
            if (dropdownActivity != null) {
                dropdownActivity.finish();
            }
            Intent i = new Intent(ActivitySearchItemList.this, ActivityPosTerminalDropdown.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
            Gson gson = new Gson();
            ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
            ed_cartSave.commit();

            // added cart badge here
            ed_countproduct.putString("countt", String.valueOf(arrCartProducts.size()));
            ed_countproduct.commit();
            if (dropdownActivity != null) {
                dropdownActivity.finish();
            }
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        itemArray.clear();
        arrCartProducts.clear();

        if (sp_countproduct.getString("countt", "").equals("")) {
            tv_productcount.setText("");
            tv_productcount.setBackgroundResource(0);
            count = 0;

        } else {
            count = Integer.parseInt(sp_countproduct.getString("countt", ""));
            if (count == 0) {
                tv_productcount.setText("");
                tv_productcount.setBackgroundResource(0);
            } else {
                tv_productcount.setText(sp_countproduct.getString("countt", ""));
                tv_productcount.setBackgroundResource(R.drawable.circle_back);
            }

            if (count > 0) {//reset cart products if comes from PosItemList(cart)
                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<SearchItem>>() {
                    }.getType();

                    String strMyCart = sp_cartSave.getString("myCart", "");
                    arrCartProducts = (ArrayList<SearchItem>) gson.fromJson(strMyCart, type);

                }

            }

            //Toast.makeText(ActivitySearchItemList.this, "" + sp_countproduct.getString("countt", "") + ",Count:" + count, Toast.LENGTH_LONG).show();

        }
        searchEditBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });


        if (!strScannedBarCode.equalsIgnoreCase("")) {
            itemArray.clear();

            if (AppConstant.isNetworkAvailable(ActivitySearchItemList.this)) {
                searchItemCollection(strScannedBarCode, false, "scanBarCode");
            } else {
                AppConstant.openInternetDialog(ActivitySearchItemList.this);
            }
        }
    }

}
