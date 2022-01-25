package com.pos.salon.activity.ActivityPurchases.AddPurchaseSection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pos.salon.R;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.adapter.PurchaseSectionAdapters.PurchaseProductSearchAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;
import com.pos.salon.model.searchData.PurchaseItemResponse;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList.strScannedBarCode;
import static com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.AddPurchaseActivity.addPurchaseActivity;

public class PurchaseProductSearchActivity extends AppCompatActivity {
    ImageView fb_barcode,home_icon;
    Toolbar toolbar;
    RecyclerView rlProductList;
    PurchaseProductSearchAdapter adapter;
    ArrayList<PurchaseProductSearchModel> itemArray = new ArrayList<>();
    ArrayList<PurchaseProductSearchModel> arrCartProducts = new ArrayList<>();
    LinearLayout llAlert;
    ImageView imgNotFount, imgCart, scanbarcode, img_clear_search_product;
    RelativeLayout rlNext;
    EditText searchEditBox;

    LinearLayout llParent;
    String quaryText = "";
    ProgressBar progressBar;
    private boolean isScrolling = true;
    int count = 0;
    int currentItems, totalItems, scrollItems;
    TextView tv_productcount;
//    public static String strScannedBarCode = "";
    LinearLayoutManager mLayoutManager;
    SharedPreferences sp_countproduct, sp_modifiers;
    SharedPreferences.Editor ed_countproduct, ed_modifiers;
    SharedPreferences sp_cartSave; // for fetch data from preference
    SharedPreferences.Editor ed_cartSave; //for save data in preference.
    int location_id = 0, supplierID = 0;
    String comingFrom = "", reference_no = "", purchaseDate = "", purchaseStatus = "";
    public static Activity purchaseProductSearchActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posterminal_search);

        purchaseProductSearchActivity = this;

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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
        home_icon = findViewById(R.id.home_icon);
        scanbarcode = (ImageView) findViewById(R.id.scanbarcode);
        imgNotFount = (ImageView) findViewById(R.id.imgNotFount);
        imgCart = (ImageView) findViewById(R.id.imgCart);
        tv_productcount = (TextView) findViewById(R.id.tv_productcount);
        searchEditBox = (EditText) findViewById(R.id.searchEditBox);
        llAlert = (LinearLayout) findViewById(R.id.llAlert);
        rlNext = (RelativeLayout) findViewById(R.id.rlNext);
        img_clear_search_product = (ImageView) findViewById(R.id.img_clear_search_product);

        progressBar = findViewById(R.id.progressBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this);

        location_id = getIntent().getIntExtra("location_id", 0);
        reference_no = getIntent().getStringExtra("reference_no");
        purchaseDate = getIntent().getStringExtra("purchaseDate");
        purchaseStatus = getIntent().getStringExtra("purchaseStatus");
        supplierID = getIntent().getIntExtra("supplierID", 0);


        setWidget();
        setBackNavgation();

        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(addPurchaseActivity !=null){
                    addPurchaseActivity.finish();
                }
                if(HomeActivity.homeActivity !=null){
                    HomeActivity.homeActivity.finish();
                }
                Intent i = new Intent(PurchaseProductSearchActivity.this, HomeActivity.class);
                startActivity(i);

                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });


        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                ed_cartSave.commit();

                // added cart badge here
                ed_countproduct.putString("countt", String.valueOf(arrCartProducts.size()));
                ed_countproduct.commit();

                if (comingFrom.equalsIgnoreCase("fromDetail")) //if coming from  detail.
                {
                    finish();

                } else {
                    Intent i = new Intent(PurchaseProductSearchActivity.this, PurchaseProductSearchList.class);
                    i.putExtra("location_id", location_id);
                    i.putExtra("reference_no", reference_no);
                    i.putExtra("purchaseDate", purchaseDate);
                    i.putExtra("purchaseStatus", purchaseStatus);
                    i.putExtra("supplierID", supplierID);
                    i.putExtra("comingFrom", "fromSearchItem");

                    startActivity(i);
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

                if (comingFrom.equalsIgnoreCase("fromDetail")) //if coming from sale detail.
                {
                    finish();

                } else {
                    Intent i = new Intent(PurchaseProductSearchActivity.this, PurchaseProductSearchList.class);
                    i.putExtra("location_id", location_id);
                    i.putExtra("reference_no", reference_no);
                    i.putExtra("purchaseDate", purchaseDate);
                    i.putExtra("purchaseStatus", purchaseStatus);
                    i.putExtra("supplierID", supplierID);
                    i.putExtra("comingFrom", "fromSearchItem");

                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

                searchEditBox.setText("");
                if (itemArray.size() > 0)
                    itemArray.clear();
                adapter.updateNewItem(itemArray);
            }
        });

        rlProductList = (RecyclerView) findViewById(R.id.rlProductList);

        adapter = new PurchaseProductSearchAdapter(this, itemArray, new PurchaseProductSearchAdapter.CilckListener() {
            @Override
            public void onItemClickPurchase(int position, PurchaseProductSearchModel item1) {

                if (!item1.isActiveProduct()) {
                    if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
                        item1.setQty_available("0");
                    }

                    double dStockQuantityAvail = Double.valueOf(item1.getQty_available());  // 3
                    int iStockQuantityAvail = (int) dStockQuantityAvail;
//                    if (iStockQuantityAvail > 0) {

                    int iProductUpdatedQuantity = 1;

                    item1.setActiveProduct(true);

                    // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                    item1.setUnitFinalPrice(item1.getSelling_price());
                    double linetotal = Double.parseDouble(item1.getDefault_purchase_price());
                    item1.setUnitCostBeforTax(item1.getDefault_purchase_price());
                    item1.setLineTotal(String.valueOf(linetotal));

                    boolean isAlreadyAdded = false;
                    for (int i = 0; i < arrCartProducts.size(); i++) {
                        PurchaseProductSearchModel objItem = arrCartProducts.get(i);

                        if (objItem.getProduct_id() == item1.getProduct_id() && objItem.getVariation_id() == item1.getVariation_id()) {
//                                if (objItem.getQuantity() < iStockQuantityAvail) {
                            objItem.setQuantity(objItem.getQuantity() + 1);

//                                }
//                                else {
//                                    Toast.makeText(PurchaseProductSearchActivity.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                            double linetotal2 = Double.parseDouble(item1.getDefault_purchase_price()) * objItem.getQuantity();
                            objItem.setLineTotal(String.valueOf(linetotal2));
                            isAlreadyAdded = true;

//                                break;
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

//                    }
//                    else {
//                        Toast.makeText(PurchaseProductSearchActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

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
                itemArray.set(position, item1);
                adapter.updateNewItem(itemArray);


//------------- open keyboard on click of add product from product list
                //AppConstant.hideKeyboardFrom(ActivitySearchItemList.this);
            }
        });
        rlProductList.setHasFixedSize(true);
        rlProductList.setLayoutManager(mLayoutManager);
        rlProductList.setItemAnimator(new DefaultItemAnimator());
        rlProductList.setAdapter(adapter);

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
                    if (AppConstant.isNetworkAvailable(PurchaseProductSearchActivity.this)) {
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
//                Intent intent = new Intent(PurchaseProductSearchActivity.this, ScannerActivity.class);
//
//                intent.putExtra("location_id", location_id);
//                startActivity(intent);

                Intent i = new Intent(PurchaseProductSearchActivity.this, ScannerActivity.class);
                i.putExtra("location_id", location_id);
                i.putExtra("reference_no", reference_no);
                i.putExtra("purchaseDate", purchaseDate);
                i.putExtra("purchaseStatus", purchaseStatus);
                i.putExtra("supplierID", supplierID);
                i.putExtra("from", "purchaseActivity");
                i.putExtra("comingFrom", "fromSearchItem");

                startActivity(i);
                searchEditBox.setText("");
                overridePendingTransition(R.anim.enter, R.anim.exit);

//                final Dialog fromPrintDialog = new Dialog(PurchaseProductSearchActivity.this);
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
//                        Intent intent = new Intent(PurchaseProductSearchActivity.this, ScanActivityDevice5.class);
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
////                        Intent intent = new Intent(PurchaseProductSearchActivity.this, ScanActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
////                    }
////                });
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        fromPrintDialog.dismiss();
////                        strScannedBarCode = "";
////                        Intent intent = new Intent(PurchaseProductSearchActivity.this, CaptureActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
//                        strScannedBarCode = "";
//                        Intent intent = new Intent(PurchaseProductSearchActivity.this, ScannerActivity.class);
//                        intent.putExtra("comingFrom", "purchaseActivity");
//                        intent.putExtra("location", location_id);
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
            Toast.makeText(PurchaseProductSearchActivity.this, "Content-" + contents + " Format-" + format, Toast.LENGTH_LONG).show();

        }
    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            if (comingFrom.equalsIgnoreCase("fromDetail")) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setTitle("PURCHASE PRODUCT");
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setTitle("PURCHASE PRODUCT");
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (comingFrom.equalsIgnoreCase("AddPurchaseActivity")) {
//
//                        arrCartProducts.clear();
//                        //clear cart product data if comes on this screen.
//                            if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
//
//                                ed_cartSave.remove("myCart");
//                                ed_cartSave.commit();
//
//                            }
//                            // added cart badge here
//                            ed_countproduct.remove("countt");
//                            ed_countproduct.commit();
//
//                            ed_modifiers.clear();
//                            ed_modifiers.commit();
//
//                        itemArray.clear();
//
//                        adapter.updateNewItem(itemArray);
//
//                        Intent i = new Intent(PurchaseProductSearchActivity.this, AddPurchaseActivity.class);
//                        i.putExtra("comingFrom",comingFrom);
//                        startActivity(i);
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    }
//                    else {
//
//                    }


                }
            });
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

        Retrofit retrofit = APIClient.getClientToken(PurchaseProductSearchActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<PurchaseItemResponse> call = apiService.getPurchaseSearchItem("get-product-suggestion?location_id=" + location_id + "&term=" + quaryText + "&limit=" + itemArray.size());
            call.enqueue(new Callback<PurchaseItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<PurchaseItemResponse> call, @NonNull Response<PurchaseItemResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        strScannedBarCode =  "";
                        Log.e("Product Search : ", "" + response.body());
                        PurchaseItemResponse searchResponse = response.body();
                        if (searchResponse.isSuccess()) {
                            if (strFromWhere.equalsIgnoreCase("textChanged")) {
                                itemArray.clear();
//                                strScannedBarCode="";
                            } else if (strFromWhere.equalsIgnoreCase("scanBarCode")) {//direct add to cart product.

                                ArrayList<PurchaseProductSearchModel> arrScannedProducts = searchResponse.getProduct_list();


                                if (arrScannedProducts.size() > 0) {
                                    PurchaseProductSearchModel item1 = arrScannedProducts.get(0);

//                                    if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
//                                        item1.setQty_available("0");
//
//                                        Toast.makeText(PurchaseProductSearchActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_LONG).show();
//
//                                        return;
//                                    }

                                    double dStockQuantityAvail = Double.valueOf(item1.getQty_available());
                                    int iStockQuantityAvail = (int) dStockQuantityAvail;

//                                    if (iStockQuantityAvail > 0) {


                                        item1.setActiveProduct(true);

                                        // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                                    item1.setUnitFinalPrice(item1.getSelling_price());
                                    double linetotal = Double.parseDouble(item1.getDefault_purchase_price());
                                    item1.setUnitCostBeforTax(item1.getDefault_purchase_price());
                                    item1.setLineTotal(String.valueOf(linetotal));

                                    boolean isAlreadyAdded = false;
                                    for (int i = 0; i < arrCartProducts.size(); i++) {
                                        PurchaseProductSearchModel objItem = arrCartProducts.get(i);

                                        if (objItem.getProduct_id() == item1.getProduct_id() && objItem.getVariation_id() == item1.getVariation_id()) {
//                                if (objItem.getQuantity() < iStockQuantityAvail) {
                                            objItem.setQuantity(objItem.getQuantity() + 1);

//                                }
//                                else {
//                                    Toast.makeText(PurchaseProductSearchActivity.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                            double linetotal2 = Double.parseDouble(item1.getDefault_purchase_price()) * objItem.getQuantity();
                                            objItem.setLineTotal(String.valueOf(linetotal2));
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

//                                    }
//                                    else {
//
//                                        Toast.makeText(PurchaseProductSearchActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_LONG).show();
//                                        return;
//                                    }
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

                    } else {
                        //AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(PurchaseProductSearchActivity.this, "get-product-suggestion API", "(PurchaseProductSearchActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(PurchaseProductSearchActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<PurchaseItemResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    Toast.makeText(PurchaseProductSearchActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (comingFrom.equalsIgnoreCase("AddPurchaseActivity")) {

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

            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (comingFrom.equalsIgnoreCase("fromDetail")) {
            Gson gson = new Gson();
            ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
            ed_cartSave.commit();

            // added cart badge here
            ed_countproduct.putString("countt", String.valueOf(arrCartProducts.size()));
            ed_countproduct.commit();

            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        itemArray.clear();
        arrCartProducts.clear();

        if (sp_countproduct.getString("countt", "").equals("")) {
            tv_productcount.setText("");
            tv_productcount.setBackgroundResource(0);
            count = 0;

        } else {
            count = Integer.parseInt(sp_countproduct.getString("countt", ""));
            tv_productcount.setText(sp_countproduct.getString("countt", ""));
            tv_productcount.setBackgroundResource(R.drawable.circle_back);

            if (count > 0) {//reset cart products if comes from PosItemList(cart)
                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<PurchaseProductSearchModel>>() {
                    }.getType();

                    String strMyCart = sp_cartSave.getString("myCart", "");
                    arrCartProducts = (ArrayList<PurchaseProductSearchModel>) gson.fromJson(strMyCart, type);

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


        if (! strScannedBarCode.equalsIgnoreCase("")) {
            itemArray.clear();

            if (AppConstant.isNetworkAvailable(PurchaseProductSearchActivity.this)) {
                searchItemCollection(strScannedBarCode, false, "scanBarCode");
            } else {
                AppConstant.openInternetDialog(PurchaseProductSearchActivity.this);
            }
        }
//151411350-7097282 sku bar code.

    }

//    @Override
//    public void add(SearchItem data, ArrayList<ProductModifier> arrNewModifiers) {
//
//        double totalvariationprice = 0.0;
//
//        ArrayList<ProductModifier> arrOld = data.getArrSelectedModifiers();
//
//        if (arrOld == null) {
//            data.setArrSelectedModifiers(arrNewModifiers);
//        } else {
//            for (int i = 0; i < arrNewModifiers.size(); i++) {
//                arrOld.add(arrNewModifiers.get(i));
//            }
//
//            data.setArrSelectedModifiers(arrOld);
//        }
//
//        String strVariation = "";
//        for (int j = 0; j < arrOld.size(); j++) {
//            ProductModifier obj = arrOld.get(j);
//
//            totalvariationprice = totalvariationprice + Double.valueOf(obj.getVariation_price());
//
//            if (j == (arrOld.size() - 1)) {
//                strVariation = strVariation + obj.getName() + "-" + obj.getVariation_name() + "(" + obj.getVariation_price() + ")";
//            } else {
//                strVariation = strVariation + obj.getName() + "-" + obj.getVariation_name() + "(" + obj.getVariation_price() + ")" + ", ";
//            }
//        }
//
//
//        for (int l = 0; l < arrCartProducts.size(); l++) {
//            if (data.getProduct_id().equalsIgnoreCase(arrCartProducts.get(l).getProduct_id())) {
//                PurchaseProductSearchModel objCartItem = arrCartProducts.get(l);
//                objCartItem.setArrSelectedModifiers(arrOld);
//                objCartItem.setVariationPrice(totalvariationprice);
//                objCartItem.setVariation_name(strVariation);
//
//                break;
//            }
//        }
//
//        ed_modifiers.putString("totalvarprice", String.valueOf(totalvariationprice));
//        ed_modifiers.commit();
//
//    }

//    @Override
//    public void add(int i, SearchItem data1) {
//        dbHelper.inserModifier(data1.Modifierrlist,data1.Variations);
//}
//
//    @Override
//    public void add(List<String> expandableListTitle, List<String> expandedListText, List<String> varprice, double totalvariationprice, List<String> mid, List<String> varid) {
//        for(int i=0;i<expandableListTitle.size();i++){
//            Log.d("msg", "adddd: "+expandableListTitle.get(i)+","+expandedListText.get(i)+","+varprice.get(i));
//        }
////        intent.putStringArrayListExtra("modtitle", (ArrayList<String>) expandableListTitle);
////        intent.putStringArrayListExtra("variationname", (ArrayList<String>) expandedListText);
////        intent.putStringArrayListExtra("variationprice", (ArrayList<String>) varprice);
////        intent.putExtra("totalvarprice", String.valueOf(totalvariationprice));
//
//        Gson gson = new Gson();
//        String s1 = gson.toJson(expandableListTitle);
//        String s2 = gson.toJson(expandedListText);
//        String s3 = gson.toJson(varprice);
//        ed_modifiers.putString("modtitle",s1 );
//        ed_modifiers.putString("variationname",s2 );
//        ed_modifiers.putString("variationprice",s3 );
//        ed_modifiers.putString("totalvarprice",String.valueOf(totalvariationprice) );
//        ed_modifiers.commit();
//
//
//
//
//
//
//    }
}


