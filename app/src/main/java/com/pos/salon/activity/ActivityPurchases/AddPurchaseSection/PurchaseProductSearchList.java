package com.pos.salon.activity.ActivityPurchases.AddPurchaseSection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.PurchaseSectionAdapters.AddPurchaseProductAdapter;
import com.pos.salon.model.PurchaseModel.ProductPurchaseDataSend;
import com.pos.salon.model.PurchaseModel.PurchaseProductDataSend;
import com.pos.salon.model.PurchaseModel.PurchaseProductSearchModel;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PurchaseProductSearchList extends AppCompatActivity {
    RecyclerView recy_ProductList;
    AddPurchaseProductAdapter addPurchaseProductAdapter;
    Toolbar toolbar;
    private RelativeLayout rlAddPayment;
    SharedPreferences.Editor ed_countproduct;
    SharedPreferences.Editor ed_cartSave;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences sp_countproduct, sp_cartSave;
    ArrayList<PurchaseProductSearchModel> cartData = new ArrayList<>();
    ArrayList<PurchaseProductSearchModel> arrOrignalProductDetails = new ArrayList<>();
    ImageView imgProductAdd;
    int count = 0;
    TextView txtTotalPayable;
    DecimalFormat df = new DecimalFormat("##.00");
    public double subTotal = 0.0;
    PurchaseProductDataSend purchaseProductDataSend=new PurchaseProductDataSend();
    ArrayList<ProductPurchaseDataSend> products = new ArrayList<>();
    int location_id = 0, supplierID = 0,purchase_id=0;
    String reference_no = "", purchaseDate = "", purchaseStatus = "",comingFrom="";
    @SuppressLint("StaticFieldLeak")
    public static Activity purchaseProductSearchList;
    LinearLayout iv_emptycart,lay_payy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search_list);

        purchaseProductSearchList=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();


        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        recy_ProductList = findViewById(R.id.recy_ProductList);
        recy_ProductList.setNestedScrollingEnabled(false);

        rlAddPayment = findViewById(R.id.rlAddPayment);
        imgProductAdd = findViewById(R.id.imgProductAdd);
        txtTotalPayable = findViewById(R.id.txtTotalPayable);
        iv_emptycart = findViewById(R.id.iv_emptycart);
        lay_payy = findViewById(R.id.lay_payy);

        location_id = getIntent().getIntExtra("location_id", 0);
        reference_no = getIntent().getStringExtra("reference_no");
        purchaseDate = getIntent().getStringExtra("purchaseDate");
        purchaseStatus = getIntent().getStringExtra("purchaseStatus");
        supplierID = getIntent().getIntExtra("supplierID", 0);
        comingFrom = getIntent().getStringExtra("comingFrom");

        if (sp_countproduct.getString("countt", "").equals("")) {
        } else {
            count = Integer.parseInt(sp_countproduct.getString("countt", ""));
        }
        if(comingFrom.equalsIgnoreCase("fromDetail")){
            purchase_id=getIntent().getIntExtra("purchase_id",0);
        }

        setBackNavgation();
        listeners();
    }

    public void listeners() {

        rlAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cartData.size()==0){
                    AppConstant.showToast(PurchaseProductSearchList.this,"Empty Cart ! Please add Item in Cart");
                }else{
                    for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
                        PurchaseProductSearchModel objItem = cartData.get(cartCount);
                        ProductPurchaseDataSend product = new ProductPurchaseDataSend();
                        product.product_id = objItem.product_id;
                        product.variation_id = objItem.variation_id;
                        product.quantity = objItem.quantity;
                        product.discount_percent = objItem.discount_percent;
                        product.pp_without_discount = objItem.getDefault_purchase_price();
                        product.profit_percent = objItem.profit_percent;
                        product.purchase_price = objItem.getUnitCostBeforTax();
                        product.purchase_line_tax_id ="";
                        product.purchase_price_inc_tax =objItem.getUnitCostBeforTax();
                        product.item_tax ="0.00";
                        product.default_sell_price = objItem.getUnitFinalPrice();
                        products.add(product);

                    }
                    purchaseProductDataSend.setPurchases(products);
                    purchaseProductDataSend.setFinal_total(Double.parseDouble(df.format(subTotal)));

                    Intent i = new Intent(PurchaseProductSearchList.this, PurchaseProductPaymentActivity.class);
                    i.putExtra("purchaseProductDataSend", purchaseProductDataSend);
                    i.putExtra("location_id",location_id);
                    i.putExtra("reference_no", reference_no);
                    i.putExtra("purchaseDate", purchaseDate);
                    i.putExtra("purchaseStatus",purchaseStatus);
                    i.putExtra("supplierID", supplierID);
                    i.putExtra("comingFrom", comingFrom);
                    i.putExtra("purchase_id", purchase_id);
                    startActivity(i);
//                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }


            }
        });

        imgProductAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int x = 0; x < cartData.size(); x++) {
                            PurchaseProductSearchModel singleItem = cartData.get(x);
                            double linetotal =Double.parseDouble(singleItem.getUnitCostBeforTax()) * singleItem.getQuantity();
                             singleItem.setLineTotal(df.format(linetotal));
                        }

                        Gson gson = new Gson();
                        //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                        ed_cartSave.putString("myCart", gson.toJson(cartData));

                        ed_cartSave.commit();

                        if (comingFrom.equalsIgnoreCase("fromDetail")) {
                            Intent intent = new Intent(PurchaseProductSearchList.this, PurchaseProductSearchActivity.class);
                            intent.putExtra("comingFrom", comingFrom);
                            intent.putExtra("location", location_id);
//                            intent.putExtra("currency", currencyData);
                            startActivity(intent);

                        } else {
                            //if already come from search then simple finish. otherwise need to intent.
                            finish();
                        }
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                });

        totalShow();

    }

    private void setAdapterMethod(final ArrayList<PurchaseProductSearchModel> cartData, final ArrayList<PurchaseProductSearchModel> arrOrignalProductDetails) {


        addPurchaseProductAdapter = new AddPurchaseProductAdapter(this, cartData, arrOrignalProductDetails,
                new AddPurchaseProductAdapter.ClickDeleteItem() {

                    @Override
                    public void onDeleteClick(int position, PurchaseProductSearchModel item) {

                        productDeleteAlertShow(item, position);
                    }

                    @Override
                    public void onQuantityClick(int quantity, PurchaseProductSearchModel item, int position) {

                        item.setLineTotal(item.getLineTotal());

                        totalShow();

                    }

                    @Override
                    public void onDiscountCalculation(String discountAmt, double unitFinalPrice, int position) {

                        totalShow();
                    }

                    @Override
                    public void onUnitCostBeforeDisocunt(String ammnt, double unitFinalPrice, int position) {
                        totalShow();
                    }

                });
        recy_ProductList.setAdapter(addPurchaseProductAdapter);
        recy_ProductList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recy_ProductList.setItemAnimator(new DefaultItemAnimator());

    }

    AlertDialog alertDialogDelete;

    private void productDeleteAlertShow(final PurchaseProductSearchModel item, final int position) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.product_delete_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        Button buttonYes = (Button) dialogView.findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item == null) {
                    // dbHelper.deleteAllItem();
                    // sqLiteDatabase.execSQL("delete from " + TABLE_NAME);
                    cartData.clear();

                    Gson gson = new Gson();
                    //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                    ed_cartSave.putString("myCart", gson.toJson(cartData));
                    ed_cartSave.commit();
                    finish();
                } else {
                    // dbHelper.deleteItem(item);
                    //  sqLiteDatabase.execSQL("delete from " + TABLE_NAME);

                    for (int i = 0; i < arrOrignalProductDetails.size(); i++) {
                        if (item.getProduct_id() == arrOrignalProductDetails.get(i).getProduct_id()) {
                            arrOrignalProductDetails.remove(i);
                            break;
                        }
                    }

                    cartData.remove(position);

//                    for(int i=0;i<cartData.size();i++){
//                        double lineTotal =(Double.parseDouble(item.getLineTotal()) * item.getQuantity());
//                      item.setLineTotal(String.valueOf(lineTotal));
//                    }
//                    double lineTotal =(Double.parseDouble(cartData.get(position).lineTotal) * cartData.get(position).quantity);
//                    item.setLineTotal(String.valueOf(lineTotal));

                    addPurchaseProductAdapter.setNewItem(cartData);

                    addPurchaseProductAdapter.notifyDataSetChanged();
                    if (sp_countproduct.getString("countt", "").equals("")) {
                    } else {
                        count = Integer.parseInt(sp_countproduct.getString("countt", ""));
                    }

                    count = count - 1;
                    ed_countproduct.putString("countt", String.valueOf(count));
                    ed_countproduct.commit();

                    if (cartData.size() == 0) {
                        iv_emptycart.setVisibility(View.VISIBLE);
                        lay_payy.setVisibility(View.GONE);
                    } else {
                        iv_emptycart.setVisibility(View.GONE);
                        lay_payy.setVisibility(View.VISIBLE);
                    }

                    //Toast.makeText(ActivityPosItemList.this, "" + sp_countproduct.getString("countt", ""), Toast.LENGTH_LONG).show();
                   /* adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged (position, cartData.size());*/

                    alertDialogDelete.dismiss();

                    totalShow();
//                    discountAmountShow();
//                    totalTaxCalculate();
//                    addShippingCharges();
                }

            }
        });

        Button buttonNo = (Button) dialogView.findViewById(R.id.buttonNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete.dismiss();
            }
        });
        alertDialogDelete = builder.create();
        alertDialogDelete.show();
    }


    @Override
    public void onBackPressed() {

        //clear cart product data if comes on this screen.
        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

            ed_cartSave.remove("myCart");
            ed_cartSave.commit();
        }

        cartData.clear();
        arrOrignalProductDetails.clear();
        ed_countproduct.clear();
        ed_countproduct.commit();


        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }

    @Override
    protected void onResume() {
        super.onResume();

//        cartData.clear();
//        arrOrignalProductDetails.clear();

        //get orignal product detail (api) , coming from on search products. only for update modifiers.
        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<PurchaseProductSearchModel>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("myCart", "");

            Log.e("strCart", strMyCart);

            cartData = (ArrayList<PurchaseProductSearchModel>) gson.fromJson(strMyCart, type);
        }

//        ArrayList<PurchaseProductSearchModel> arrNew = new ArrayList<PurchaseProductSearchModel>(cartData);
//        for (int j = 0; j < arrNew.size(); j++) {
//            PurchaseProductSearchModel objAlready = arrNew.get(j);
//            arrOrignalProductDetails.add(new PurchaseProductSearchModel(objAlready));
//        }
//
//        for (int j = 0; j < arrOrignalProductDetails.size(); j++) {
//            PurchaseProductSearchModel obj = arrOrignalProductDetails.get(j);
//            obj.setArrSelectedModifiers(new ArrayList<ProductModifier>());
//            obj.setVariationPrice(0.0);
//            obj.setVariation_name("");
//        }

        // added cart badge here
        ed_countproduct.putString("countt", String.valueOf(cartData.size()));
        ed_countproduct.commit();

//        addPurchaseProductAdapter.notifyDataSetChanged();

        setAdapterMethod(cartData, arrOrignalProductDetails);
        //end cart adapter set code.
//
        if (cartData.size() == 0) {
            iv_emptycart.setVisibility(View.VISIBLE);
            lay_payy.setVisibility(View.GONE);
        } else {
            iv_emptycart.setVisibility(View.GONE);
            lay_payy.setVisibility(View.VISIBLE);
        }


        totalShow();
//        discountAmountShow();
//        totalTaxCalculate();
//        addShippingCharges();

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("PURCHASE PRODUCT");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //clear cart product data if comes on this screen.
                    if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                        ed_cartSave.remove("myCart");
                        ed_cartSave.commit();

                    }

                    cartData.clear();
                    arrOrignalProductDetails.clear();
                    ed_countproduct.clear();
                    ed_countproduct.commit();

                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

                }
            });
        }
    }

    private void totalShow() {

        subTotal = 0;
        //subTotal=subTotal+totalvariation;
        for (int x = 0; x < cartData.size(); x++) {
            PurchaseProductSearchModel singleItem = cartData.get(x);
            subTotal = subTotal + ((Double.valueOf(singleItem.getLineTotal())));

        }

        txtTotalPayable.setText("Total Payable:  " + "$" + " " + df.format(subTotal));
    }

}
