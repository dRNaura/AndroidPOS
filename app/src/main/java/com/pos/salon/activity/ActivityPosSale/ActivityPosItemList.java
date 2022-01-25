package com.pos.salon.activity.ActivityPosSale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPayment.ActivityPayment;
import com.pos.salon.activity.ActivityPayment.ActivityPaymentFalse;
import com.pos.salon.activity.BookingSection.BookinDateTimeActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.ProductsAdapters.CalculationAdapter;
import com.pos.salon.adapter.ProductsAdapters.TagAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.payment.Payment;
import com.pos.salon.model.payment.PaymentDataSend;
import com.pos.salon.model.payment.ProductDataSend;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.posLocation.Taxes;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.model.searchData.SearchItemResponse;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivityPosItemList extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rlProductList;
    CalculationAdapter adapter;
    RelativeLayout rlAddPayment;
    TextView btnDraft, btnQuotation, btnSuspend;
    Spinner spnTotalTax, spnTotalDiscountType;
    private String[] discountType;
    ArrayList<SearchItem> cartData = new ArrayList<>();
    ArrayList<SearchItem> arrOrignalProductDetails = new ArrayList<SearchItem>();
    LinearLayout iv_emptycart;
    TextView txt_booking_date, txtTotalProduct, txtTotalProductPrice, txtTotalPayable, txtTotalDiscountPrice, txtTotalTax, txtTotalShipingCharge;
    int productCount = 0;
    double subTotal = 0.0f, finalPrice = 0.0f, discountAmount = 0.0f, taxAmtF = 0.0f, taxAmount = 0.0f, shipingChargeF = 0.0f, shipingCharge = 0.0f;
    ArrayList<Taxes> itemTax = new ArrayList<Taxes>();
    ArrayList<String> tax = new ArrayList<String>();
    EditText edtDiscountTotal, edtTaxTotal, edtShipingDetailTotal, edtShipingChargesTotal;
    String totalDiscountTypeItem = "Select Discount Type", totalDiscountAmt = "0", totalTaxType = "", totalShipingCharge = "";
    ImageView imgProductAdd, imgTotalItemClose, fb_barcode;
    CurriencyData currencyData;
    CustomerListData customerData;
    RelativeLayout rlcart, rl_lay_totalPaid;
    BusinessLocationData locationData;
    Taxes taxData;
    ArrayList<ProductDataSend> products = new ArrayList<ProductDataSend>();
    PaymentDataSend paymemtData = new PaymentDataSend();
    DecimalFormat df = new DecimalFormat("##.00");
    @SuppressLint("StaticFieldLeak")
    public static Activity posItemListActivity;
    ArrayList<Payment> payment = new ArrayList<Payment>();
    public String from, total_before_tax = "0.0";
    public static String comingFrom;
    SharedPreferences sp_countproduct, sp_modifiers, sp_cartSave, sp_saveTax;
    int count = 0;
    SharedPreferences.Editor ed_countproduct, ed_modifiers, ed_saveTax;
    SharedPreferences.Editor ed_cartSave;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    JSONObject getEditResponse;
    int productOrService = 1;
    double dProductsSubTotal = 0;
    String orderr_type = "", tablee_id = "", waiterr_id = "", repair_product_id = "", service_staff_id = "", editOrExchnage = "";
    int transaction = 0, location_id = 0;
    LinearLayout llay_payment, lay_draft_quatation_suspend, lay_cart, lay_options;
    TextView bottomBtnPayment, txtTotalPaid;
    Dialog suspendDialog;
    public String shipping_details = "", shipping_charges = "0.0", parentComingFrom = "";
    BusinessDetails objBusiness = new BusinessDetails();
    Double totalPaid = 0.0;
    RelativeLayout rl_total_items, paynowNext;
    // for new cart design
    LinearLayout lay_update_discount, lay_update_Tax, lay_update_shipping;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();
    private final Calendar myCalendar = Calendar.getInstance();
    private ArrayList<CustomerListData> searchCustomerList = new ArrayList<CustomerListData>();
    DelayAutoCompleteTextView et_employee_name;
    CustomerSearchAdapter cutoCompleteCustomerAapter;
    ArrayList<String> nameEmployeelist = new ArrayList<>();
    ArrayList<String> nameEmployeeIDlist = new ArrayList<>();
    TagAdapter tagAdapter;
    public String booking_time = "", booking_date = "";
    private ArrayList<SearchItem> arrOrignalProdList = new ArrayList<SearchItem>();
    private final ArrayList sell_linesList = new ArrayList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_page_layout);

//        setBackNavgation();

        posItemListActivity = this;

        comingFrom = getIntent().getStringExtra("comingFrom");
        Log.e("comingFrom : ", comingFrom);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sp_modifiers = getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE);
        ed_modifiers = sp_modifiers.edit();

        //get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();


        Gson gson = new Gson();
        Type type = new TypeToken<BusinessDetails>() {
        }.getType();
        final String myBusinessDetail = sharedPreferences.getString("myBusinessDetail", "");
        if (myBusinessDetail != null) {
            objBusiness = (BusinessDetails) gson.fromJson(myBusinessDetail, type);
        }
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {
        }.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        rlAddPayment = findViewById(R.id.rlAddPayment);
        lay_update_discount = findViewById(R.id.lay_update_discount);
        lay_update_Tax = findViewById(R.id.lay_update_Tax);
        lay_update_shipping = findViewById(R.id.lay_update_shipping);
        btnDraft = findViewById(R.id.btnDraft);
        btnQuotation = findViewById(R.id.btnQuotation);
        btnSuspend = findViewById(R.id.btnSuspend);
        fb_barcode = findViewById(R.id.fb_barcode);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        llay_payment = (LinearLayout) findViewById(R.id.llay_payment);
        bottomBtnPayment = (TextView) findViewById(R.id.payment);
        txtTotalPaid = (TextView) findViewById(R.id.txtTotalPaid);

        setSupportActionBar(toolbar);
        rlProductList = (RecyclerView) findViewById(R.id.rlProductList);

        imgProductAdd = (ImageView) findViewById(R.id.imgProductAdd);
        txtTotalProductPrice = (TextView) findViewById(R.id.txtTotalProductPrice);
        edtShipingDetailTotal = (EditText) findViewById(R.id.edtShipingDetailTotal);
        edtShipingChargesTotal = (EditText) findViewById(R.id.edtShipingChargesTotal);
        txtTotalProduct = (TextView) findViewById(R.id.txtTotalProduct);
        txtTotalPayable = (TextView) findViewById(R.id.txtTotalPayable);
        rl_total_items = (RelativeLayout) findViewById(R.id.rl_total_items);

        rlcart = (RelativeLayout) findViewById(R.id.rlcart);
        rl_lay_totalPaid = (RelativeLayout) findViewById(R.id.rl_lay_totalPaid);
        paynowNext = (RelativeLayout) findViewById(R.id.paynowNext);
        iv_emptycart = (LinearLayout) findViewById(R.id.iv_emptycart);

        txtTotalDiscountPrice = (TextView) findViewById(R.id.txtTotalDiscountPrice);
        txtTotalTax = (TextView) findViewById(R.id.txtTotalTax);
        txtTotalShipingCharge = (TextView) findViewById(R.id.txtTotalShipingCharge);
        edtTaxTotal = (EditText) findViewById(R.id.edtTaxTotal);
        lay_draft_quatation_suspend = (LinearLayout) findViewById(R.id.lay_draft_quatation_suspend);
        lay_cart = (LinearLayout) findViewById(R.id.lay_cart);
        lay_options = (LinearLayout) findViewById(R.id.lay_options);

        spnTotalDiscountType = (Spinner) findViewById(R.id.spnTotalDiscountType);
        edtDiscountTotal = (EditText) findViewById(R.id.edtDiscountTotal);
        imgTotalItemClose = (ImageView) findViewById(R.id.imgTotalItemClose);
        spnTotalTax = (Spinner) findViewById(R.id.spnTotalTax);

        locationData = (BusinessLocationData) getIntent().getSerializableExtra("location");
        currencyData = (CurriencyData) getIntent().getSerializableExtra("currency");
        customerData = (CustomerListData) getIntent().getSerializableExtra("customer");

        if (getIntent().hasExtra("tax")) {
            itemTax = (ArrayList<Taxes>) getIntent().getSerializableExtra("tax");
        }

        imgProductAdd.setVisibility(View.VISIBLE);

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();


        if (itemTax == null) {
            itemTax = new ArrayList<Taxes>();
        }

        Taxes taxObj = new Taxes();
        taxObj.setName("Select Tax Type");
        taxObj.setAmount("");
        taxObj.setId("");
        itemTax.add(0, taxObj);

        if (tax.size() > 0) {
            tax.clear();
        }


        sp_saveTax = getSharedPreferences("defaultTax", Context.MODE_PRIVATE);
        ed_saveTax = sp_saveTax.edit();

        if (!sp_saveTax.getString("default_tax", "").equalsIgnoreCase("")) {

            ArrayList<Taxes> arrDefaultTaxes = new Gson().fromJson(sp_saveTax.getString("default_tax", "")
                    , new TypeToken<ArrayList<Taxes>>() {
                    }.getType());

            for (int i = 0; i < arrDefaultTaxes.size(); i++) {
                Taxes objNew = arrDefaultTaxes.get(i);

                boolean isNew = true;
                for (int j = 0; j < itemTax.size(); j++) {
                    if (objNew.getName().equalsIgnoreCase(itemTax.get(j).getName())) {
                        isNew = false;
                        break;
                    }
                }

                if (isNew) {
                    itemTax.add(objNew);
                }
            }
        }
        //end - fetch tax from shared preference and add to item tax.


        if (itemTax != null) {
            for (Taxes taxSelect : itemTax) {
                tax.add(taxSelect.getName());
            }
        }


        if (null != getIntent().getStringExtra("selected_ordertype")) {
            orderr_type = getIntent().getStringExtra("selected_ordertype");
        }

        if (null != getIntent().getStringExtra("tablee_id")) {
            tablee_id = getIntent().getStringExtra("tablee_id");
        }

        if (null != getIntent().getStringExtra("waiterr_id")) {
            waiterr_id = getIntent().getStringExtra("waiterr_id");
        }
        productOrService = getIntent().getIntExtra("productOrService", 1);

        if (sp_countproduct.getString("countt", "").equals("")) {
        } else {
            count = Integer.parseInt(sp_countproduct.getString("countt", ""));
        }

        if (null != getIntent().getStringExtra("parentfrom")) {
            parentComingFrom = getIntent().getStringExtra("parentfrom"); //sub type show from where parent screen coming from.
        }

        if (productOrService == 0) {
            fb_barcode.setVisibility(View.GONE);
        } else {
            fb_barcode.setVisibility(View.VISIBLE);
        }

        if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {

            // to check if coming to edit or exchange sale
            editOrExchnage = getIntent().getStringExtra("editOrExchnage");

            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                rl_lay_totalPaid.setVisibility(View.VISIBLE);
            } else {
                rl_lay_totalPaid.setVisibility(View.GONE);
            }
            if (productOrService == 0) {
                bottomBtnPayment.setText("Next");
            } else {
                bottomBtnPayment.setText("Payment");
            }
            if (AppConstant.isNetworkAvailable(ActivityPosItemList.this)) {
                editSaleDetail(editOrExchnage); // edit the sale detail
            } else {
                AppConstant.openInternetDialog(ActivityPosItemList.this);
            }

            if (!parentComingFrom.equalsIgnoreCase("") && (parentComingFrom.equalsIgnoreCase("fromListDraftFragment") || parentComingFrom.equalsIgnoreCase("fromQuatationListFragment"))) {//when comes from draft list and quotation list

                bottomBtnPayment.setText("Payment");

//                lay_draft_quatation.setVisibility(View.VISIBLE);
            } else {//when come from list pos list.
                if (productOrService == 0) {
                    bottomBtnPayment.setText("Next");
                } else {
                    bottomBtnPayment.setText("Update Sale");
                }

//                lay_draft_quatation.setVisibility(View.GONE);
            }

        } else if (comingFrom.equalsIgnoreCase("fromSearchItem")) {

            if (productOrService == 0 && arrdepartmentsList.contains("4")) {
                bottomBtnPayment.setText("Next");
            } else {
                bottomBtnPayment.setText("Payment");
            }


//            lay_draft_quatation.setVisibility(View.VISIBLE);

            //end show modifier.
        } else if (comingFrom.equalsIgnoreCase("fromRepairDetail")) {
            repair_product_id = String.valueOf(getIntent().getIntExtra("repair_product_id", 0));
            service_staff_id = String.valueOf(getIntent().getIntExtra("service_staff_id", 0));
            bottomBtnPayment.setText("Payment");

            imgProductAdd.setVisibility(View.GONE);

            lay_draft_quatation_suspend.setVisibility(View.GONE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.GONE);
            }

        }

        // hide draft view when come from delivery orders
        if (comingFrom.equalsIgnoreCase("fromSaleDetail") && parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            lay_draft_quatation_suspend.setVisibility(View.GONE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.GONE);
            }

//            rl_total_items.setVisibility(View.GONE);
            imgProductAdd.setVisibility(View.GONE);

            spnTotalDiscountType.setEnabled(false);
            edtDiscountTotal.setEnabled(false);
            spnTotalTax.setEnabled(false);
            edtShipingDetailTotal.setEnabled(false);
            edtShipingChargesTotal.setEnabled(false);


        }

        if (lay_update_discount != null) {
            lay_update_discount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDiscountDialog();
                }
            });
        }
        if (lay_update_Tax != null) {
            lay_update_Tax.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTaxDialog();
                }
            });
        }
        if (lay_update_shipping != null) {
            lay_update_shipping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateShippingDialog();
                }
            });
        }


        rlAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double dFinalTotalPayable = 0.0;

                if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                    dFinalTotalPayable = subTotal - totalPaid;
                } else {
                    dFinalTotalPayable = subTotal;
                }
                if (dFinalTotalPayable > 0) {

                    if (productOrService == 0 && arrdepartmentsList.contains("4")) {
                        openDateTimeEmployeeDialog();
                    } else {
                        for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
                            SearchItem objItem = cartData.get(cartCount);

                            ProductDataSend product = new ProductDataSend();

                            ArrayList<String> sendvariation_id = new ArrayList<>();
                            ArrayList<String> sendvariation_price = new ArrayList<>();
                            ArrayList<String> sendmodifier_id = new ArrayList<>();


                            product.setUnit_price(objItem.getSelling_price());
                            if (objItem.getDiscountType().equalsIgnoreCase("Select Discount Type")) {
                                product.setLine_discount_type("");
                            } else {
                                product.setLine_discount_type(objItem.getDiscountType());
                                product.setLine_discount_amount(objItem.getDiscountAmt());
                                // product.setLine_discount_amount(objItem.getDiscount()); sending calculated amount.

                            }
                            // only input discount value.


                            //onhold  product.setItem_tax(cartData.get(cartCount).getTaxCalculationAmt());
                            //onhold  product.setTax_id(cartData.get(cartCount).getTaxSelectId());
                            product.setProduct_id(objItem.getProduct_id());
                            product.setVariation_id(objItem.getVariation_id());
                            product.setEnable_stock(objItem.getEnable_stock());
                            product.setQuantity(String.valueOf(objItem.getQuantity()));
                            product.setUnit_price_inc_tax(objItem.getUnitFinalPrice());
                            product.setProduct_type(objItem.getType());
//                    product.setQuantityAvailable(objItem.getQty_available());

                            products.add(product);

                        }

                        paymemtData.setProducts(products);

                        if (totalDiscountTypeItem.equalsIgnoreCase("Select Discount Type"))
                            paymemtData.setDiscount_type("");
                        else {

                            if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                                paymemtData.setDiscount_type("fixed");

                            } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                                paymemtData.setDiscount_type("percentage");
                            }
                        }

                        // paymemtData.setDiscount_amount(discountAmount); //sending calculated amount
                        if (totalDiscountAmt.equalsIgnoreCase("")) {
                            paymemtData.setDiscount_amount(0.0); //only input value send.

                        } else {
                            paymemtData.setDiscount_amount(Double.valueOf(totalDiscountAmt)); //only input value send.
//                    paymemtData.setDiscount_amount(Double.valueOf(txtTotalDiscountPrice.getText().toString())); //only input value send.
                        }


                        Double dTaxAmount = 0.0;
                        Double dShippingCharges = 0.0;

                        if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                            if (taxData != null) {
                                dTaxAmount = Double.valueOf(taxData.getAmount());
                                paymemtData.setTax_rate_id(taxData.getId());
                                paymemtData.setTax_calculation_amount(dTaxAmount + "");
                            }
                        }


//                    String shipingDetail = edtShipingDetailTotal.getText().toString().trim();
                        String shipingDetail = shipping_details;

                        if (!shipingDetail.equalsIgnoreCase("")) {
//                        paymemtData.setShipping_details(edtShipingDetailTotal.getText().toString());
                            paymemtData.setShipping_details(shipping_details);
                        } else {

                        }

//                    String shippintTotal = edtShipingChargesTotal.getText().toString().trim();
                        String shippintTotal = totalShipingCharge;

                        if (!shippintTotal.equalsIgnoreCase("")) {
//                        dShippingCharges = Double.valueOf(edtShipingChargesTotal.getText().toString().trim());
                            dShippingCharges = Double.valueOf(totalShipingCharge);
                            paymemtData.setShipping_charges(dShippingCharges + "");
                        }

                        if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                            Double totalForExchange = subTotal - totalPaid;
                            paymemtData.setFinal_total(String.valueOf(totalForExchange));
                        } else {
                            paymemtData.setFinal_total(String.valueOf(subTotal));
                        }

                        paymemtData.setChange_return(paymemtData.getChange_return());
                        paymemtData.setStatus("final");

                        paymemtData.setOrder_type(orderr_type);

                        Intent intent = new Intent();
                        intent = new Intent(ActivityPosItemList.this, ActivityPayment.class);
//                    } else {
//                        intent = new Intent(ActivityPosItemList.this, ActivityPayment.class);
//                    }
                        if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                            Double totalForExchange = subTotal - totalPaid;
                            intent.putExtra("subtotal", String.valueOf(totalForExchange));
                        } else {
                            intent.putExtra("subtotal", String.valueOf(subTotal));
                        }
                        intent.putExtra("paymentObject", paymemtData);
                        intent.putExtra("paidamount", String.valueOf(totalPaid));
                        intent.putExtra("comingFrom", comingFrom);
                        intent.putExtra("editOrExchnage", editOrExchnage);

                        if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                            intent.putExtra("discounttype", "fixed");

                        } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                            intent.putExtra("discounttype", "percentage");
                        }

                        //intent.putExtra("discountamount", discountAmount); //sending calculated amount.
                        if (totalDiscountAmt.equalsIgnoreCase("")) {
                            intent.putExtra("discountamount", 0.0); // sending text value only.
                        } else {
                            intent.putExtra("discountamount", Double.valueOf(totalDiscountAmt)); // sending text value only.
//                    intent.putExtra("discountamount", Double.valueOf(txtTotalDiscountPrice.getText().toString())); // sending text value only.
                        }


                        if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                            if (taxData != null) {
                                intent.putExtra("taxrate", taxData.getId());
                            }
                        }
                        intent.putExtra("taxcalculation", dTaxAmount + "");
//                    intent.putExtra("shippingdetail", edtShipingDetailTotal.getText().toString());
                        intent.putExtra("shippingdetail", shipping_details);
                        intent.putExtra("shippingcharges", dShippingCharges + "");
                        intent.putExtra("subTotal", df.format(subTotal) + "");
                        intent.putExtra("changereturn", paymemtData.getChange_return());
                        intent.putExtra("customerId", customerData.getId());
                        intent.putExtra("transaction_id", transaction);
                        intent.putExtra("locationId", locationData.getId());
                        intent.putExtra("productOrService", productOrService);
                        intent.putExtra("booking_date", booking_date);
                        intent.putExtra("booking_time", booking_time);
                        intent.putExtra("currency", currencyData);
                        intent.putExtra("selected_ordertype", orderr_type);
                        intent.putExtra("tablee_id", tablee_id);
                        intent.putExtra("waiterr_id", service_staff_id);
                        intent.putExtra("employee_id", "");
                        intent.putExtra("repair_product_id", repair_product_id);
                        //update edit sale.
                        JSONObject objMain = createUpdateSaleDetailMainObject(parentComingFrom);

                        if (null != objMain) {
                            // Gson objGson = new Gson();
                            String jsonString = objMain.toString();
                            intent.putExtra("updateSaleDetailObject", jsonString);
                        }


                        if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                            if (!parentComingFrom.equalsIgnoreCase("") && (parentComingFrom.equalsIgnoreCase("fromListDraftFragment") || parentComingFrom.equalsIgnoreCase("fromQuatationListFragment"))) {//when comes from draft list and quotation list
                                intent.putExtra("comingFrom", "fromSearchItem");

                            } else {
                                //when come from list pos list.
                                intent.putExtra("comingFrom", comingFrom);
                            }
                        }

                        intent.putExtra("parentfrom", parentComingFrom);

                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
//                    finish();


                } else {
                    AppConstant.showToast(ActivityPosItemList.this, "Total Payable Amount Should be greater than 0");
                }
            }
        });

        if (locationData.getId() == null) {
            paymemtData.setLocation_id("");
        } else {
            paymemtData.setLocation_id(locationData.getId());
        }


        if (currencyData.getName() == null) {
            paymemtData.setCurrency("");
        } else {
            paymemtData.setCurrency(currencyData.getName());
        }


        paymemtData.setContact_id(customerData.getId());


        imgProductAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Gson gson = new Gson();
                        //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                        ed_cartSave.putString("myCart", gson.toJson(cartData));

                        ed_cartSave.commit();

                        if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                            Intent intent = new Intent(ActivityPosItemList.this, ActivitySearchItemList.class);

                            intent.putExtra("comingFrom", comingFrom);

                            intent.putExtra("location", locationData);
                            intent.putExtra("currency", currencyData);

                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            //if already come from search then simple finish. otherwise need to intent.
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        }
                    }
                });


        fb_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                ed_cartSave.putString("myCart", gson.toJson(cartData));

                ed_cartSave.commit();

//                if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                Intent intent = new Intent(ActivityPosItemList.this, ScannerActivity.class);

                intent.putExtra("comingFrom", comingFrom);
                intent.putExtra("from", "searchActivity");
                intent.putExtra("location", locationData);
                intent.putExtra("currency", currencyData);
                intent.putExtra("customer", customerData);
                intent.putExtra("selected_ordertype", orderr_type);
                intent.putExtra("tablee_id", tablee_id);
                intent.putExtra("waiterr_id", waiterr_id);

                startActivity(intent);

//                } else {
                //if already come from search then simple finish. otherwise need to intent.
                finish();
//                }
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        imgTotalItemClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // productDeleteAlertShow(null,0);
            }
        });


        edtDiscountTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (totalDiscountTypeItem.equalsIgnoreCase("Fixed") || totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                    String disEntered = s.toString();
                    totalDiscountAmt = disEntered;
                    String currency_code = objBusiness.getCurrency_code();
                    if (!disEntered.isEmpty()) {
                        if (!currencyData.getName().equals(currency_code)) {
                            Double exchngeRate = 0.0;
                            exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                            if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                                totalDiscountAmt = disEntered;
                            } else {
                                totalDiscountAmt = String.valueOf(exchngeRate * Double.valueOf(disEntered));
                            }

                        }
                    }

                    totalShow();
                    discountAmountShow();
                    totalTaxCalculate();
                    addShippingCharges();
                }
            }
        });


        edtShipingChargesTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                totalShipingCharge = s.toString();

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();

            }
        });


        spnTotalDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                totalDiscountTypeItem = discountType[position];

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spnTotalTax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //subTotal = subTotal + Double.parseDouble(String.valueOf(totalvariation));
                totalTaxType = tax.get(position);
                taxData = itemTax.get(position);

                if (position == 0) {
                    taxData = null;
                }

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        discountType = getResources().getStringArray(R.array.discount_array);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, discountType);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnTotalDiscountType.setAdapter(spinnerAdapter);

        ArrayAdapter<String> taxAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, tax);
        taxAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnTotalTax.setAdapter(taxAdapter);


        totalShow();
        discountAmountShow();
        totalTaxCalculate();
        addShippingCharges();


        btnDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAllProducts();
                paymemtData.setOrder_type(orderr_type);
                paymemtData.setRes_table_id(tablee_id);
                paymemtData.setRes_waiter_id(waiterr_id);
                paymemtData.setProducts(products);

                paymemtData.setPayment(payment);
                paymemtData.setFinal_total(String.valueOf(subTotal));
                paymemtData.setStatus("draft");
                paymemtData.setChange_return("0");

                if (currencyData.getName().equals(objBusiness.getCurrency_code())) {
                    paymemtData.setExchange_rate("1");
                } else {
                    paymemtData.setExchange_rate(objBusiness.getExchange_rate());
                }
                if (totalDiscountTypeItem.equalsIgnoreCase("Select Discount Type")) {
                    paymemtData.setDiscount_type("");
                    paymemtData.setDiscount_amount(0.0);
                } else {

                    if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                        paymemtData.setDiscount_type("fixed");

                    } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                        paymemtData.setDiscount_type("percentage");
                    }

                    paymemtData.setDiscount_amount(Double.valueOf(totalDiscountAmt));
//                    paymemtData.setDiscount_amount(Double.valueOf(txtTotalDiscountPrice.getText().toString()));
                }


                Double dTaxAmount = 0.0;
                Double dShippingCharges = 0.0;

                if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                    if (taxData != null) {
                        dTaxAmount = Double.valueOf(taxData.getAmount());
                        paymemtData.setTax_rate_id(taxData.getId());
                        paymemtData.setTax_calculation_amount(dTaxAmount + "");
                    }
                }


//                String shipingDetail = edtShipingDetailTotal.getText().toString().trim();
                String shipingDetail = shipping_details;
                if (!shipingDetail.equalsIgnoreCase("")) {
//                    paymemtData.setShipping_details(edtShipingDetailTotal.getText().toString());
                    paymemtData.setShipping_details(shipping_details);
                }
//                String shippintTotal = edtShipingChargesTotal.getText().toString().trim();
                String shippintTotal = totalShipingCharge;

                if (!shippintTotal.equalsIgnoreCase("")) {
//                    dShippingCharges = Double.valueOf(edtShipingChargesTotal.getText().toString().trim());
                    dShippingCharges = Double.valueOf(totalShipingCharge);
                    paymemtData.setShipping_charges(dShippingCharges + "");
                }


                if (AppConstant.isNetworkAvailable(ActivityPosItemList.this)) {
                    callPaymentAPI(paymemtData);
                } else {
                    AppConstant.openInternetDialog(ActivityPosItemList.this);
                }
            }
        });

        btnQuotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllProducts();
                paymemtData.setOrder_type(orderr_type);
                paymemtData.setRes_table_id(tablee_id);
                paymemtData.setRes_waiter_id(waiterr_id);
                paymemtData.setProducts(products);
                //    paymemtData.setPayment(payment);
                paymemtData.setFinal_total(String.valueOf(subTotal));
                paymemtData.setStatus("quotation");
                paymemtData.setChange_return("0");

                if (currencyData.getName().equals(objBusiness.getCurrency_code())) {
                    paymemtData.setExchange_rate("1");
                } else {
                    paymemtData.setExchange_rate(objBusiness.getExchange_rate());
                }

                if (totalDiscountTypeItem.equalsIgnoreCase("Select Discount Type")) {
                    paymemtData.setDiscount_type("");
                    paymemtData.setDiscount_amount(0.0);
                } else {

                    if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                        paymemtData.setDiscount_type("fixed");

                    } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                        paymemtData.setDiscount_type("percentage");
                    }
                    paymemtData.setDiscount_amount(Double.valueOf(totalDiscountAmt));
//                    paymemtData.setDiscount_amount(Double.valueOf(txtTotalDiscountPrice.getText().toString()));
                }


                Double dTaxAmount = 0.0;
                Double dShippingCharges = 0.0;

                if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                    if (taxData != null) {
                        dTaxAmount = Double.valueOf(taxData.getAmount());
                        paymemtData.setTax_rate_id(taxData.getId());
                        paymemtData.setTax_calculation_amount(dTaxAmount + "");
                    }
                }


//                String shipingDetail = edtShipingDetailTotal.getText().toString().trim();
                String shipingDetail = shipping_details;
                if (!shipingDetail.equalsIgnoreCase("")) {
//                    paymemtData.setShipping_details(edtShipingDetailTotal.getText().toString());
                    paymemtData.setShipping_details(shipping_details);
                }
//                String shippintTotal = edtShipingChargesTotal.getText().toString().trim();
                String shippintTotal = totalShipingCharge;

                if (!shippintTotal.equalsIgnoreCase("")) {
//                    dShippingCharges = Double.valueOf(edtShipingChargesTotal.getText().toString().trim());
                    dShippingCharges = Double.valueOf(totalShipingCharge);
                    paymemtData.setShipping_charges(dShippingCharges + "");
                }

                if (AppConstant.isNetworkAvailable(ActivityPosItemList.this)) {
                    callPaymentAPI(paymemtData);
                } else {
                    AppConstant.openInternetDialog(ActivityPosItemList.this);
                }


            }
        });

        btnSuspend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                suspendDialog = new Dialog(ActivityPosItemList.this);
                suspendDialog.setContentView(R.layout.suspend_sale_dialog);
                suspendDialog.setCancelable(false);
                Window window = suspendDialog.getWindow();
                window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
//                window.setGravity(Gravity.CENTER);


                TextView txt_suspend_sale = suspendDialog.findViewById(R.id.txt_suspend_sale);
                TextView txt_cancel_suspend = suspendDialog.findViewById(R.id.txt_cancel_suspend);
                final EditText ed_suspend_reason = suspendDialog.findViewById(R.id.ed_suspend_reason);

                suspendDialog.show();

                txt_cancel_suspend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        suspendDialog.dismiss();
                    }
                });
                txt_suspend_sale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ed_suspend_reason.getText().toString().isEmpty()) {

                            AppConstant.showToast(ActivityPosItemList.this, "Please Mention a reason to Suspend this sale");
                        } else {
                            addAllProducts();
                            paymemtData.setOrder_type(orderr_type);
                            paymemtData.setRes_table_id(tablee_id);
                            paymemtData.setRes_waiter_id(waiterr_id);
                            paymemtData.setProducts(products);
                            //    paymemtData.setPayment(payment);
                            paymemtData.setFinal_total(String.valueOf(subTotal));
                            paymemtData.setStatus("final");
                            paymemtData.setChange_return("0");
                            paymemtData.setIs_suspend("1");
                            paymemtData.setAdditional_notes(ed_suspend_reason.getText().toString().trim());

                            if (currencyData.getName().equals(objBusiness.getCurrency_code())) {
                                paymemtData.setExchange_rate("1");
                            } else {
                                paymemtData.setExchange_rate(objBusiness.getExchange_rate());
                            }

                            if (totalDiscountTypeItem.equalsIgnoreCase("Select Discount Type")) {
                                paymemtData.setDiscount_type("");

                                paymemtData.setDiscount_amount(0.0);
                            } else {

                                if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                                    paymemtData.setDiscount_type("fixed");

                                } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                                    paymemtData.setDiscount_type("percentage");


                                }
                                paymemtData.setDiscount_amount(Double.valueOf(totalDiscountAmt));
//                                paymemtData.setDiscount_amount(Double.valueOf(txtTotalDiscountPrice.getText().toString()));
                            }


                            Double dTaxAmount = 0.0;
                            Double dShippingCharges = 0.0;

                            if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                                if (taxData != null) {
                                    dTaxAmount = Double.valueOf(taxData.getAmount());
                                    paymemtData.setTax_rate_id(taxData.getId());
                                    paymemtData.setTax_calculation_amount(dTaxAmount + "");
                                }
                            }

//                            String shipingDetail = edtShipingDetailTotal.getText().toString().trim();
                            String shipingDetail = shipping_details;
                            if (!shipingDetail.equalsIgnoreCase("")) {
//                                paymemtData.setShipping_details(edtShipingDetailTotal.getText().toString());
                                paymemtData.setShipping_details(shipping_details);
                            }
//                            String shippintTotal = edtShipingChargesTotal.getText().toString().trim();
                            String shippintTotal = totalShipingCharge;

                            if (!shippintTotal.equalsIgnoreCase("")) {
//                                dShippingCharges = Double.valueOf(edtShipingChargesTotal.getText().toString().trim());
                                dShippingCharges = Double.valueOf(totalShipingCharge);
                                paymemtData.setShipping_charges(dShippingCharges + "");
                            }
                            suspendDialog.dismiss();

                            if (AppConstant.isNetworkAvailable(ActivityPosItemList.this)) {
                                callPaymentAPI(paymemtData);
                            } else {
                                AppConstant.openInternetDialog(ActivityPosItemList.this);
                            }

                        }
                    }
                });


            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm a");
        booking_time = mdformat.format(calendar.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        booking_date = sdf.format(new Date());

        setBackNavgation();
    }

    public void onServiceList() {
//        cartData.clear();
//        arrOrignalProductDetails.clear();

        //get orignal product detail (api) , coming from on search products. only for update modifiers.
        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<SearchItem>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("myCart", "");

            Log.e("strCart", strMyCart);

            cartData = (ArrayList<SearchItem>) gson.fromJson(strMyCart, type);
        }
        ArrayList<SearchItem> arrNew = new ArrayList<SearchItem>(cartData);
        for (int j = 0; j < arrNew.size(); j++) {
            SearchItem objAlready = arrNew.get(j);
            arrOrignalProductDetails.add(new SearchItem(objAlready));
        }

        for (int j = 0; j < arrOrignalProductDetails.size(); j++) {
            SearchItem obj = arrOrignalProductDetails.get(j);
            obj.setVariationPrice(0.0);
            obj.setVariation_name("");
        }

        // added cart badge here
        ed_countproduct.putString("countt", String.valueOf(cartData.size()));
        ed_countproduct.commit();

        setAdapterMethod(cartData, arrOrignalProductDetails, itemTax);
        //end cart adapter set code.

        if (cartData.size() == 0) {
            iv_emptycart.setVisibility(View.VISIBLE);
            rlcart.setVisibility(View.GONE);
            rlAddPayment.setVisibility(View.GONE);
            lay_draft_quatation_suspend.setVisibility(View.GONE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.GONE);
            }

            if (lay_cart != null) {
                lay_cart.setVisibility(View.GONE);
            }
            if (lay_options != null) {
                lay_options.setVisibility(View.GONE);
            }
        } else {
            iv_emptycart.setVisibility(View.GONE);
            rlcart.setVisibility(View.VISIBLE);
            rlAddPayment.setVisibility(View.VISIBLE);
            lay_draft_quatation_suspend.setVisibility(View.VISIBLE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.VISIBLE);
            }

            if (lay_cart != null) {
                lay_cart.setVisibility(View.VISIBLE);
            }
            if (lay_options != null) {
                lay_options.setVisibility(View.VISIBLE);
            }
        }


        totalShow();
        discountAmountShow();
        totalTaxCalculate();
        addShippingCharges();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        cartData.clear();
//        arrOrignalProductDetails.clear();

        //get orignal product detail (api) , coming from on search products. only for update modifiers.
        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<SearchItem>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("myCart", "");

            Log.e("strCart", strMyCart);

            cartData = (ArrayList<SearchItem>) gson.fromJson(strMyCart, type);
        }
        ArrayList<SearchItem> arrNew = new ArrayList<SearchItem>(cartData);
        for (int j = 0; j < arrNew.size(); j++) {
            SearchItem objAlready = arrNew.get(j);
            arrOrignalProductDetails.add(new SearchItem(objAlready));
        }

        for (int j = 0; j < arrOrignalProductDetails.size(); j++) {
            SearchItem obj = arrOrignalProductDetails.get(j);
            obj.setVariationPrice(0.0);
            obj.setVariation_name("");
        }

        // added cart badge here
        ed_countproduct.putString("countt", String.valueOf(cartData.size()));
        ed_countproduct.commit();

        setAdapterMethod(cartData, arrOrignalProductDetails, itemTax);
        //end cart adapter set code.

        if (cartData.size() == 0) {
            iv_emptycart.setVisibility(View.VISIBLE);
            rlcart.setVisibility(View.GONE);
            rlAddPayment.setVisibility(View.GONE);
            lay_draft_quatation_suspend.setVisibility(View.GONE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.GONE);
            }

            if (lay_cart != null) {
                lay_cart.setVisibility(View.GONE);
            }
            if (lay_options != null) {
                lay_options.setVisibility(View.GONE);
            }
        } else {
            iv_emptycart.setVisibility(View.GONE);
            rlcart.setVisibility(View.VISIBLE);
            rlAddPayment.setVisibility(View.VISIBLE);
            lay_draft_quatation_suspend.setVisibility(View.VISIBLE);
            if (paynowNext != null) {
                paynowNext.setVisibility(View.VISIBLE);
            }

            if (lay_cart != null) {
                lay_cart.setVisibility(View.VISIBLE);
            }
            if (lay_options != null) {
                lay_options.setVisibility(View.VISIBLE);
            }
        }


        totalShow();
        discountAmountShow();
        totalTaxCalculate();
        addShippingCharges();

    }

    void addAllProducts() {

        products = new ArrayList<>();

        for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
            SearchItem objItem = cartData.get(cartCount);

            ProductDataSend product = new ProductDataSend();

            ArrayList<String> sendvariation_id = new ArrayList<>();
            ArrayList<String> sendvariation_price = new ArrayList<>();
            ArrayList<String> sendmodifier_id = new ArrayList<>();


            product.setUnit_price(objItem.getSelling_price());
            if (objItem.getDiscountType().equalsIgnoreCase("Select Discount Type"))
                product.setLine_discount_type("");
            else
                product.setLine_discount_type(objItem.getDiscountType());

            // product.setLine_discount_amount(objItem.getDiscount()); sending calculated amount.
            product.setLine_discount_amount(objItem.getDiscountAmt()); // only input discount value.

            product.setProduct_id(objItem.getProduct_id());
            product.setVariation_id(objItem.getVariation_id());
            product.setEnable_stock(String.valueOf(productOrService));
            product.setQuantity(objItem.getQuantity() + "");
            product.setUnit_price_inc_tax(objItem.getUnitFinalPrice());
            products.add(product);

        }


    }


    private void setAdapterMethod(final ArrayList<SearchItem> cartData, final ArrayList<SearchItem> arrOrignalProductDetails, final ArrayList<Taxes> itemTax) {


        adapter = new CalculationAdapter(this, parentComingFrom, comingFrom, productOrService, cartData, arrOrignalProductDetails, itemTax,
                new CalculationAdapter.ClickDeleteItem() {

                    @Override
                    public void onDiscountCalculation(String discountType, String discountAmt, double unitFinalPrice, int position) {

                        SearchItem itemObj = ActivityPosItemList.this.cartData.get(position);

                        finalPrice = (unitFinalPrice + itemObj.getVariationPrice()) * itemObj.getQuantity();

                        if (!discountAmt.equalsIgnoreCase("0") || !discountAmt.equalsIgnoreCase("")) {
//                    double discountAmtF = Double.parseDouble(discountAmt);
//
//                    switch (discountType)
//                    {
//
//                        case "Fixed":
//                            finalPrice = totalPrice - discountAmtF;
//
//                            break;
//
//                        case "Percentage":
//                            discountAmtF = totalPrice * discountAmtF / 100;
//                            finalPrice = totalPrice - discountAmtF;
//                            break;
//
//                    }


                            if (discountAmt.equalsIgnoreCase("")) {
                                discountAmt = "0";
                            }


                            if (!itemObj.isCalculationDone())
                                itemObj.setCalculationDone(true);

                        } else {


                            if (itemObj.isCalculationDone())
                                itemObj.setCalculationDone(false);

                            itemObj.setDiscountAmt("");
                            itemObj.setDiscountt("0");
                            itemObj.setDiscountType(discountType);
                        }

                        itemObj.setFinalPrice(df.format(finalPrice) + "");
                        ActivityPosItemList.this.cartData.set(position, itemObj);
                        // Toast.makeText(ActivityPosItemList.this, ""+cartData.get(position).getQuantity(), Toast.LENGTH_LONG).show();

                        adapter.setNewItem(ActivityPosItemList.this.cartData);

                        totalShow();
                        discountAmountShow();
                        totalTaxCalculate();
                        addShippingCharges();
                    }


                    @Override
                    public void onTaxCalculation(String taxType, double taxAmt, double unitFinalPrice, int position) {

                        SearchItem itemObj = ActivityPosItemList.this.cartData.get(position);

                        finalPrice = (unitFinalPrice + itemObj.getVariationPrice()) * itemObj.getQuantity(); // now take this outside of if else for common work.

                        if (taxAmt != 0) {
                            if (!itemObj.isTaxCalculationDone())
                                itemObj.setTaxCalculationDone(true);


                            itemObj.setTaxCalculateAmt(taxAmt);
                            itemObj.setTaxType(taxType);
                        } else {

                            if (itemObj.isTaxCalculationDone())
                                itemObj.setTaxCalculationDone(false);


                            itemObj.setTaxCalculateAmt(0.0f);
                            itemObj.setTaxType(taxType);
                        }

                        itemObj.setFinalPrice(df.format(finalPrice) + "");
                        ActivityPosItemList.this.cartData.set(position, itemObj);
                        //  Toast.makeText(ActivityPosItemList.this, "Tax"+cartData.get(position).getQuantity(), Toast.LENGTH_LONG).show();
                        adapter.setNewItem(ActivityPosItemList.this.cartData);

                        totalShow();
                        discountAmountShow();
                        totalTaxCalculate();
                        addShippingCharges();
                    }


                    @Override
                    public void onSellPriceUpdate(double newSellPrice, int position) {
                        SearchItem itemObj = ActivityPosItemList.this.cartData.get(position);

//                itemObj.setSelling_price(String.valueOf(newSellPrice));

                        finalPrice = (newSellPrice + itemObj.getVariationPrice()) * itemObj.getQuantity(); // now take this outside of if else for common work.

//                if (taxAmt != 0) {
//                    if (!itemObj.isTaxCalculationDone())
//                        itemObj.setTaxCalculationDone(true);
//
//
//                    itemObj.setTaxCalculateAmt(taxAmt);
//                    itemObj.setTaxType(taxType);
//                } else {
//
//                    if (itemObj.isTaxCalculationDone())
//                        itemObj.setTaxCalculationDone(false);
//
//
//                    itemObj.setTaxCalculateAmt(0.0f);
//                    itemObj.setTaxType(taxType);
//                }

                        itemObj.setFinalPrice(df.format(finalPrice) + "");
                        ActivityPosItemList.this.cartData.set(position, itemObj);
                        //  Toast.makeText(ActivityPosItemList.this, "Tax"+cartData.get(position).getQuantity(), Toast.LENGTH_LONG).show();
                        adapter.setNewItem(ActivityPosItemList.this.cartData);

                        totalShow();
                        discountAmountShow();
                        totalTaxCalculate();
                        addShippingCharges();
                    }

                    @Override
                    public void onItemClick(int position, SearchItem item) {

                        productDeleteAlertShow(item, position);
                    }


                    @Override
                    public void onClick(int value, SearchItem item, int position, double Variations_total, String addValue) {
                        productCount = 0;
                        subTotal = 0;
                        //  totalvariation = Variations_total;
                        //  Toast.makeText(ActivityPosItemList.this, "Hiii"+y, Toast.LENGTH_LONG).show();
                        // getting quantity here..............
                        System.out.println("Product count: " + value);

                        if (addValue.equalsIgnoreCase("minus_modifier")) {

                        }

                        // Toast.makeText(ActivityPosItemList.this, "selected quantity: "+value, Toast.LENGTH_LONG).show();
                        if (value == 0) {
                            item.setQuantity(1);
                            finalPrice = Double.valueOf(item.getUnitFinalPrice()) + item.getVariationPrice();

                        } else {
                            item.setQuantity(value);
                            if (item.getUnitFinalPrice() == null) {
                                finalPrice = Float.parseFloat(item.getSelling_price()) + item.getVariationPrice();
                                if (!addValue.equals("add_with_different")) {
                                    finalPrice = finalPrice * item.getQuantity();

                                }
                            } else {
                                finalPrice = ((Double.valueOf(item.getUnitFinalPrice()) + item.getVariationPrice()) * item.getQuantity());
                            }
                        }


                        // Toast.makeText(ActivityPosItemList.this, "final: "+finalPrice, Toast.LENGTH_LONG).show();
                        item.setFinalPrice(finalPrice + "");

                        double singleUnitDiscountAmount = 0;

                        if (!item.getDiscountAmt().equalsIgnoreCase("")) {
                            double discountAmtF = Double.parseDouble(item.getDiscountAmt());
                            String strDiscountType = item.getDiscountType();
                            String strUpdateDiscountType = strDiscountType.substring(0, 1).toUpperCase() + strDiscountType.substring(1);
                            switch (strUpdateDiscountType) {

                                case "Fixed":

                                    singleUnitDiscountAmount = discountAmtF;


                                    if (!item.isCalculationDone())
                                        item.setCalculationDone(true);
                                    break;
                                case "Percentage":

                                    singleUnitDiscountAmount = (Double.parseDouble(item.getSelling_price()) * discountAmtF) / 100;

                                    if (!item.isCalculationDone())
                                        item.setCalculationDone(true);
                                    break;
                            }

                        }
                        item.setDiscountt(String.valueOf(singleUnitDiscountAmount));


                        double singlUnitTaxAmount = item.getTaxCalculateAmt();

                        if (!item.isCalculationDone() && !item.getTaxAmount().equalsIgnoreCase("")) {

                            // double taxAmtF = Double.parseDouble(item.getTaxAmount());

                            singlUnitTaxAmount = Double.valueOf(item.getTaxCalculateAmt());//(Double.valueOf(item.getSelling_price()) * taxAmtF) / 100;

                            if (!item.isTaxCalculationDone())
                                item.setTaxCalculationDone(true);
                        }

                        item.setUnitFinalPrice(String.valueOf((Double.valueOf(item.getSelling_price()) + singlUnitTaxAmount) - singleUnitDiscountAmount));

                        finalPrice = (Double.valueOf(item.getUnitFinalPrice()) + item.getVariationPrice()) * item.getQuantity();


                        item.setFinalPrice(df.format(finalPrice) + "");
                        item.setTaxCalculationAmt(df.format(singlUnitTaxAmount) + "");
                        ActivityPosItemList.this.cartData.set(position, item);
                        adapter.setNewItem(ActivityPosItemList.this.cartData);
                        //    Toast.makeText(ActivityPosItemList.this, "Click"+cartData.get(position).getQuantity(), Toast.LENGTH_LONG).show();


                        totalShow();
                        discountAmountShow();
                        totalTaxCalculate();
                        addShippingCharges();
                    }

                });

        rlProductList.setAdapter(adapter);
        rlProductList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rlProductList.setItemAnimator(new DefaultItemAnimator());

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle(R.string.posterminal);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Gson gson = new Gson();
                    //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                    ed_cartSave.putString("myCart", gson.toJson(cartData));

                    ed_cartSave.commit();

                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
    }


    AlertDialog alertDialogDelete;

    private void productDeleteAlertShow(final SearchItem item, final int position) {
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
                        if (item.getProduct_id().equalsIgnoreCase(arrOrignalProductDetails.get(i).getProduct_id())) {
                            arrOrignalProductDetails.remove(i);
                            break;
                        }
                    }

                    cartData.remove(position);

                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, cartData.size());
                    count = count - 1;
                    ed_countproduct.putString("countt", String.valueOf(count));
                    ed_countproduct.commit();

                    if (cartData.size() == 0) {
                        iv_emptycart.setVisibility(View.VISIBLE);
                        rlcart.setVisibility(View.GONE);
                        rlAddPayment.setVisibility(View.GONE);
                        lay_draft_quatation_suspend.setVisibility(View.GONE);
                        if (paynowNext != null) {
                            paynowNext.setVisibility(View.GONE);
                        }

                        if (lay_cart != null) {
                            lay_cart.setVisibility(View.GONE);
                        }
                        if (lay_options != null) {
                            lay_options.setVisibility(View.GONE);
                        }
                    } else {
                        iv_emptycart.setVisibility(View.GONE);
                        rlcart.setVisibility(View.VISIBLE);
                        rlAddPayment.setVisibility(View.VISIBLE);
                        lay_draft_quatation_suspend.setVisibility(View.VISIBLE);
                        if (paynowNext != null) {
                            paynowNext.setVisibility(View.VISIBLE);
                        }
                        if (lay_cart != null) {
                            lay_cart.setVisibility(View.VISIBLE);
                        }
                        if (lay_options != null) {
                            lay_options.setVisibility(View.VISIBLE);
                        }
                    }

                    //Toast.makeText(ActivityPosItemList.this, "" + sp_countproduct.getString("countt", ""), Toast.LENGTH_LONG).show();
                   /* adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged (position, cartData.size());*/

                    alertDialogDelete.dismiss();
                    totalShow();
                    discountAmountShow();
                    totalTaxCalculate();
                    addShippingCharges();
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

    private void totalShow() {
        discountAmount = 0;
        productCount = 0;
        subTotal = 0;
        //subTotal=subTotal+totalvariation;
        for (int x = 0; x < cartData.size(); x++) {

            SearchItem singleItem = cartData.get(x);
//
            double dTotalVariationPrice = 0.0;
//
            if (singleItem.getUnitFinalPrice() != null) {
                subTotal = subTotal + ((Double.valueOf(singleItem.getUnitFinalPrice()) + dTotalVariationPrice) * singleItem.getQuantity());
                productCount = productCount + singleItem.getQuantity();

            } else {
                //Log.e("b4 getFinalPrice-111"+x, String.valueOf(variationAtLastItem));
                productCount = productCount + singleItem.getQuantity();
                subTotal = subTotal + (Double.valueOf(singleItem.getSelling_price()) + dTotalVariationPrice) * singleItem.getQuantity();

            }

        }


        // Toast.makeText(posItemListActivity, "2: " + subTotal, Toast.LENGTH_LONG).show();
        txtTotalProduct.setText(productCount + "");

        String currency_code = objBusiness.getCurrency_code();
        if (!currencyData.getName().equals(currency_code)) {
            Double exchngeRate = 0.0;
            exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
            subTotal = exchngeRate * subTotal;
        }

//        txtTotalProductPrice.setText(currencyData.getName()+(df.format(subTotal)));
        txtTotalProductPrice.setText((df.format(subTotal)));

        dProductsSubTotal = subTotal; //store all product subtotal only.

        if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//            txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
            txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
        } else {
//            txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
            txtTotalPayable.setText("" + df.format(subTotal));
        }

    }

    private void discountAmountShow() {

        if (!totalDiscountAmt.equalsIgnoreCase("")) {
            float discountAmtF = Float.parseFloat(totalDiscountAmt);
            String strDiscountType = totalDiscountTypeItem;
            String strUpdateDiscountType = strDiscountType.substring(0, 1).toUpperCase() + strDiscountType.substring(1);
            switch (strUpdateDiscountType) {
                case "Fixed":
                    discountAmount = discountAmtF;
                    subTotal = dProductsSubTotal - discountAmount;
                    break;
                case "Percentage":
                    discountAmount = (dProductsSubTotal * discountAmtF / 100);
                    subTotal = dProductsSubTotal - discountAmount;
                    break;
                case "Select Discount Type":
                    discountAmount = 0;
                    discountAmount = 0;
                    totalDiscountAmt = "";
                    edtDiscountTotal.setText("");
                    break;
            }

            dProductsSubTotal = dProductsSubTotal - discountAmount;
            Log.e("D SUB TOTAL : ", df.format(subTotal));
            txtTotalDiscountPrice.setText(df.format(discountAmount));
            //  txtTotalProductPrice.setText(currencyData.getName() + " " + df.format(subTotal));

            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
            } else {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
                txtTotalPayable.setText("" + df.format(subTotal));
            }
        } else {

            txtTotalDiscountPrice.setText("0.00");
            // totalShow();
        }
    }

    private void totalTaxCalculate() {
        if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
            if (taxData == null) {
                return;
            }

            taxAmtF = Float.parseFloat(taxData.getAmount());

            taxAmount = (dProductsSubTotal * taxAmtF / 100);

            Log.e("subTotal", String.valueOf(subTotal));
            Log.e("taxAmtF", String.valueOf(taxAmtF));
            Log.e("taxAmount", String.valueOf(taxAmount));

            subTotal = subTotal + taxAmount;
            edtTaxTotal.setText(df.format(taxAmount));
            txtTotalTax.setText(df.format(taxAmount));
            //  txtTotalProductPrice.setText(currencyData.getName() + " " + df.format(subTotal));

            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
            } else {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
                txtTotalPayable.setText("" + df.format(subTotal));
            }
//            txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));

        } else {
            edtTaxTotal.setText("");
            taxAmount = 0;
            subTotal = subTotal - taxAmount;
            txtTotalTax.setText("0.00");
            //  txtTotalProductPrice.setText(currencyData.getName() + " " + df.format(subTotal));
            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
            } else {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
                txtTotalPayable.setText("" + df.format(subTotal));
            }
//            txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
        }
    }

    private void addShippingCharges() {
        if (!totalShipingCharge.equalsIgnoreCase("")) {
            shipingChargeF = Float.parseFloat(totalShipingCharge);
            // subTotal = subTotal - shipingCharge;
            shipingCharge = shipingChargeF;
            subTotal = subTotal + shipingChargeF;
            txtTotalShipingCharge.setText(shipingChargeF + "");
            // txtTotalProductPrice.setText(currencyData.getName() + " " + df.format(subTotal));
            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
            } else {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
                txtTotalPayable.setText("" + df.format(subTotal));
            }
//            txtTotalPayable.setText("Total Payable: " + currencyData.getName() + " " + df.format(subTotal));

        } else {

            edtTaxTotal.setText("");
            // subTotal = subTotal - shipingCharge;
            shipingCharge = 0;
            txtTotalShipingCharge.setText("0.00");
            // txtTotalProductPrice.setText(currencyData.getName() + " " + df.format(subTotal));
            if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
            } else {
//                txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
                txtTotalPayable.setText("" + df.format(subTotal));
            }
//            txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal));
        }
    }


    @Override
    public void onBackPressed() {


        Gson gson = new Gson();
        //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
        ed_cartSave.putString("myCart", gson.toJson(cartData));

        ed_cartSave.commit();


        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void openDateTimeEmployeeDialog() {
        Dialog filterDialog = new Dialog(this);
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.booking_date_time_dialog);
        filterDialog.setCancelable(false);


        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ImageView img_cancel_dialog = filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_apply_changes = filterDialog.findViewById(R.id.linear_apply_changes);
        LinearLayout lay_bookingDate = filterDialog.findViewById(R.id.lay_bookingDate);
        LinearLayout lay_bookingTime = filterDialog.findViewById(R.id.lay_bookingTime);
        TextView txt_bookingTime = filterDialog.findViewById(R.id.txt_bookingTime);
        RecyclerView recycler_employee = filterDialog.findViewById(R.id.recycler_employee);
        et_employee_name = filterDialog.findViewById(R.id.et_employee_name);
        txt_booking_date = filterDialog.findViewById(R.id.txt_booking_date);

        txt_booking_date.setText(booking_date);
        txt_bookingTime.setText(booking_time);

        filterDialog.show();

        GridLayoutManager layoutManagerTag = new GridLayoutManager(ActivityPosItemList.this, 2);
        recycler_employee.setLayoutManager(layoutManagerTag);
        tagAdapter = new TagAdapter(ActivityPosItemList.this, nameEmployeelist, "");
        recycler_employee.setAdapter(tagAdapter);

        nameEmployeelist.clear();
        cutoCompleteCustomerAapter = new CustomerSearchAdapter(ActivityPosItemList.this, searchCustomerList);
        et_employee_name.setAdapter(cutoCompleteCustomerAapter);
        et_employee_name.setThreshold(1);

        for (int a = 0; a < searchCustomerList.size(); a++) {
            String employee_id = nameEmployeeIDlist.toString().replace("[", "").replace("]", "");
            if(employee_id.equalsIgnoreCase(String.valueOf(searchCustomerList.get(a).getId()))) {
                nameEmployeelist.add(searchCustomerList.get(a).getText());
                break;
            }
        }
        tagAdapter.notifyDataSetChanged();
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        tagAdapter.setOnRemoveClicked(new TagAdapter.RemoveTag() {
            @Override
            public void setOnClickedItem(int position) {

                deleteEmployee(position);

            }
        });


        lay_bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityPosItemList.this, datee, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        lay_bookingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize a new time picker dialog fragment
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ActivityPosItemList.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String status = "AM";
                        if (selectedHour > 11) {
                            status = "PM";
                        }
                        if (selectedHour == 13) {
                            selectedHour = 1;
                        }
                        if (selectedHour == 14) {
                            selectedHour = 2;
                        }
                        if (selectedHour == 15) {
                            selectedHour = 3;
                        }
                        if (selectedHour == 16) {
                            selectedHour = 4;
                        }
                        if (selectedHour == 17) {
                            selectedHour = 5;
                        }
                        if (selectedHour == 18) {
                            selectedHour = 6;
                        }
                        if (selectedHour == 19) {
                            selectedHour = 7;
                        }
                        if (selectedHour == 20) {
                            selectedHour = 8;
                        }
                        if (selectedHour == 21) {
                            selectedHour = 9;
                        }
                        if (selectedHour == 22) {
                            selectedHour = 10;
                        }
                        if (selectedHour == 23) {
                            selectedHour = 11;
                        }
                        if (selectedHour == 24) {
                            selectedHour = 12;
                        }
                        String str = "";
                        if (selectedHour < 10) {
                            str = String.format("%02d", selectedHour);
                        } else {
                            str = String.valueOf(selectedHour);
                        }
                        if (selectedMinute < 10) {
                            booking_time = str + ":0" + selectedMinute + " " + status;
                            txt_bookingTime.setText(str + ":0" + selectedMinute + " " + status);
                        } else {
                            booking_time = str + ":" + selectedMinute + " " + status;
                            txt_bookingTime.setText(str + ":" + selectedMinute + " " + status);
                        }


                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        et_employee_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

//        et_employee_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
//                Object item = parent.getItemAtPosition(position);
//                if (item instanceof CustomerListData) {
//                    customerData = (CustomerListData) item;
//                }
//                et_employee_name.setText("");
//
//                boolean newItem=true;
//                for(int a=0;a < nameEmployeeIDlist.size();a++){
//                    if(customerData.getId()==Integer.parseInt(nameEmployeeIDlist.get(a))){
//                        newItem=false;
//                        break;
//                    }
//                }
//                if(newItem){
//                    nameEmployeelist.add(customerData.getText());
//                    nameEmployeeIDlist.add(String.valueOf(customerData.getId()));
//                    tagAdapter.notifyDataSetChanged();
//                }
//
////                contactID = String.valueOf(customerData.getId());
//
//            }
//        });


        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
                    SearchItem objItem = cartData.get(cartCount);

                    ProductDataSend product = new ProductDataSend();

                    ArrayList<String> sendvariation_id = new ArrayList<>();
                    ArrayList<String> sendvariation_price = new ArrayList<>();
                    ArrayList<String> sendmodifier_id = new ArrayList<>();


                    product.setUnit_price(objItem.getSelling_price());
                    if (objItem.getDiscountType().equalsIgnoreCase("Select Discount Type")) {
                        product.setLine_discount_type("");
                    } else {
                        product.setLine_discount_type(objItem.getDiscountType());
                        product.setLine_discount_amount(objItem.getDiscountAmt());
                        // product.setLine_discount_amount(objItem.getDiscount()); sending calculated amount.

                    }
                    // only input discount value.


                    //onhold  product.setItem_tax(cartData.get(cartCount).getTaxCalculationAmt());
                    //onhold  product.setTax_id(cartData.get(cartCount).getTaxSelectId());
                    product.setProduct_id(objItem.getProduct_id());
                    product.setVariation_id(objItem.getVariation_id());
                    product.setEnable_stock(objItem.getEnable_stock());
                    product.setQuantity(String.valueOf(objItem.getQuantity()));
                    product.setUnit_price_inc_tax(objItem.getUnitFinalPrice());
                    product.setProduct_type(objItem.getType());
//                    product.setQuantityAvailable(objItem.getQty_available());

                    products.add(product);

                }

                paymemtData.setProducts(products);

                if (totalDiscountTypeItem.equalsIgnoreCase("Select Discount Type"))
                    paymemtData.setDiscount_type("");
                else {

                    if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                        paymemtData.setDiscount_type("fixed");

                    } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                        paymemtData.setDiscount_type("percentage");
                    }
                }

                // paymemtData.setDiscount_amount(discountAmount); //sending calculated amount
                if (totalDiscountAmt.equalsIgnoreCase("")) {
                    paymemtData.setDiscount_amount(0.0); //only input value send.

                } else {
                    paymemtData.setDiscount_amount(Double.valueOf(totalDiscountAmt)); //only input value send.
//                    paymemtData.setDiscount_amount(Double.valueOf(txtTotalDiscountPrice.getText().toString())); //only input value send.
                }


                Double dTaxAmount = 0.0;
                Double dShippingCharges = 0.0;

                if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                    if (taxData != null) {
                        dTaxAmount = Double.valueOf(taxData.getAmount());
                        paymemtData.setTax_rate_id(taxData.getId());
                        paymemtData.setTax_calculation_amount(dTaxAmount + "");
                    }
                }


//                    String shipingDetail = edtShipingDetailTotal.getText().toString().trim();
                String shipingDetail = shipping_details;

                if (!shipingDetail.equalsIgnoreCase("")) {
//                        paymemtData.setShipping_details(edtShipingDetailTotal.getText().toString());
                    paymemtData.setShipping_details(shipping_details);
                } else {

                }

//                    String shippintTotal = edtShipingChargesTotal.getText().toString().trim();
                String shippintTotal = totalShipingCharge;

                if (!shippintTotal.equalsIgnoreCase("")) {
//                        dShippingCharges = Double.valueOf(edtShipingChargesTotal.getText().toString().trim());
                    dShippingCharges = Double.valueOf(totalShipingCharge);
                    paymemtData.setShipping_charges(dShippingCharges + "");
                }

                if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                    Double totalForExchange = subTotal - totalPaid;
                    paymemtData.setFinal_total(String.valueOf(totalForExchange));
                } else {
                    paymemtData.setFinal_total(String.valueOf(subTotal));
                }

                paymemtData.setChange_return(paymemtData.getChange_return());
                paymemtData.setStatus("final");

                paymemtData.setOrder_type(orderr_type);

                Intent intent = new Intent();
                intent = new Intent(ActivityPosItemList.this, ActivityPayment.class);
//                    } else {
//                        intent = new Intent(ActivityPosItemList.this, ActivityPayment.class);
//                    }
                if (editOrExchnage.equalsIgnoreCase("forExchange")) {
                    Double totalForExchange = subTotal - totalPaid;
                    intent.putExtra("subtotal", String.valueOf(totalForExchange));
                } else {
                    intent.putExtra("subtotal", String.valueOf(subTotal));
                }
                intent.putExtra("paymentObject", paymemtData);
                intent.putExtra("paidamount", String.valueOf(totalPaid));
                intent.putExtra("comingFrom", comingFrom);
                intent.putExtra("editOrExchnage", editOrExchnage);

                if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                    intent.putExtra("discounttype", "fixed");

                } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                    intent.putExtra("discounttype", "percentage");
                }

                //intent.putExtra("discountamount", discountAmount); //sending calculated amount.
                if (totalDiscountAmt.equalsIgnoreCase("")) {
                    intent.putExtra("discountamount", 0.0); // sending text value only.
                } else {
                    intent.putExtra("discountamount", Double.valueOf(totalDiscountAmt)); // sending text value only.
//                    intent.putExtra("discountamount", Double.valueOf(txtTotalDiscountPrice.getText().toString())); // sending text value only.
                }


                if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                    if (taxData != null) {
                        intent.putExtra("taxrate", taxData.getId());
                    }
                }
                intent.putExtra("taxcalculation", dTaxAmount + "");
//                    intent.putExtra("shippingdetail", edtShipingDetailTotal.getText().toString());
                intent.putExtra("shippingdetail", shipping_details);
                intent.putExtra("shippingcharges", dShippingCharges + "");
                intent.putExtra("subTotal", df.format(subTotal) + "");
                intent.putExtra("changereturn", paymemtData.getChange_return());
                intent.putExtra("customerId", customerData.getId());
                intent.putExtra("transaction_id", transaction);
                intent.putExtra("locationId", locationData.getId());
                intent.putExtra("productOrService", productOrService);
                intent.putExtra("booking_date", booking_date);
                intent.putExtra("booking_time", booking_time);
                intent.putExtra("currency", currencyData);
                intent.putExtra("selected_ordertype", orderr_type);
                intent.putExtra("tablee_id", tablee_id);
                intent.putExtra("waiterr_id", service_staff_id);
                String employee_id = nameEmployeeIDlist.toString().replace("[", "").replace("]", "");
                intent.putExtra("employee_id", employee_id);
                intent.putExtra("repair_product_id", repair_product_id);
                //update edit sale.
                JSONObject objMain = createUpdateSaleDetailMainObject(parentComingFrom);

                if (null != objMain) {
                    // Gson objGson = new Gson();
                    String jsonString = objMain.toString();
                    intent.putExtra("updateSaleDetailObject", jsonString);
                }


                if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                    if (!parentComingFrom.equalsIgnoreCase("") && (parentComingFrom.equalsIgnoreCase("fromListDraftFragment") || parentComingFrom.equalsIgnoreCase("fromQuatationListFragment"))) {//when comes from draft list and quotation list
                        intent.putExtra("comingFrom", "fromSearchItem");

                    } else {
                        //when come from list pos list.
                        intent.putExtra("comingFrom", comingFrom);
                    }
                }

                intent.putExtra("parentfrom", parentComingFrom);

                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });


    }

    final DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        booking_date = sdf.format(myCalendar.getTime());
        txt_booking_date.setText(sdf.format(myCalendar.getTime()));

    }

    public void filter(String text) {

        if (!sp_cartSave.getString("searchCustomerList", "").equalsIgnoreCase("")) {//if not cart empty.
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CustomerListData>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("searchCustomerList", "");
            searchCustomerList = (ArrayList<CustomerListData>) gson.fromJson(strMyCart, type);

        }

        ArrayList<CustomerListData> temp = new ArrayList();
        for (CustomerListData d : searchCustomerList) {
            if (d.getText().toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        cutoCompleteCustomerAapter.updateList(temp);
        et_employee_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                CustomerListData customerData = new CustomerListData();
                nameEmployeelist.clear();
                nameEmployeeIDlist.clear();
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }
                et_employee_name.setText("");

                boolean newItem = true;
                for (int a = 0; a < nameEmployeeIDlist.size(); a++) {
                    if (customerData.getId() == Integer.parseInt(nameEmployeeIDlist.get(a))) {
                        newItem = false;
                        break;
                    }
                }
                if (newItem) {
                    nameEmployeelist.add(customerData.getText());
                    nameEmployeeIDlist.add(String.valueOf(customerData.getId()));
                    tagAdapter.notifyDataSetChanged();
                }

//                contactID = String.valueOf(customerData.getId());

            }
        });
    }

    public void deleteEmployee(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityPosItemList.this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Employee");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                nameEmployeelist.remove(position);
                nameEmployeeIDlist.remove(position);
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


    private void callPaymentAPI(PaymentDataSend paymentData) {
        AppConstant.showProgress(this, false);

        Gson gson = new Gson();
        String json = gson.toJson(paymentData);

        Log.e("Request Suspend Json : ", json);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService =
                    retrofit.create(APIInterface.class);

            Call<ResponseBody> call = apiService.sendPayment("pos", paymentData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);


                            //  Toast.makeText( this, ""+respo, Toast.LENGTH_LONG).show();
                            String successstatus = responseObject.optString("success");
                            String message = responseObject.optString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("false")) {
                                System.out.println("Response :" + respo);
                            } else {
                                if (ActivitySearchItemList.posSearchItemListActivity != null) {
                                    ActivitySearchItemList.posSearchItemListActivity.finish();
                                }

//                                if(ActivityPosItemList.posItemListActivity != null)
//                                {
//                                    ActivityPosItemList.posItemListActivity.finish();
//                                }

                                if (ActivityPosTerminalDropdown.dropdownActivity != null) {
                                    ActivityPosTerminalDropdown.dropdownActivity.finish();
                                }


                                //clear cart product data if comes on this screen.
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                    ed_cartSave.remove("myCart");
                                    ed_cartSave.commit();

                                }

                                if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true")) {

                                    cartData.clear();
                                    arrOrignalProductDetails.clear();


                                    finish();
                                    overridePendingTransition(R.anim.enter, R.anim.exit);

                                } else {
                                    // dbHelper.deleteAllItem();
                                    cartData.clear();
                                    arrOrignalProductDetails.clear();

                                    Intent intent = new Intent(getBaseContext(), ActivityPaymentFalse.class);

                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }
                            }

                            AppConstant.hideProgress();
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ActivityPosItemList.this, "pos API", "(ActivityPosItemList Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getBaseContext(), "Could Not Make Order Successfull. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                        Toast.makeText(getBaseContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getBaseContext(), "Could Not Make Order Successfull. Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void editSaleDetail(final String editOrExchnage) {

        AppConstant.showProgress(this, false);
//          progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = APIClient.getClientToken(this);
//        Log.e("transactionId", String.valueOf(transactionId));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<ResponseBody> call = apiService.editsalesDetail("pos/" + getIntent().getStringExtra("transactionId") + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        try {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);

                            Log.e("Edit APi Response", respo.toString());
                            getEditResponse = responseObject;
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {


                                if (productOrService == 0) {
                                    //clear cart product data if comes on this screen.
//                                    if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
//
//                                        ed_cartSave.remove("myCart");
//                                        ed_cartSave.commit();
//
//                                    }
//                                    // added cart badge here
//                                    ed_countproduct.remove("countt");
//                                    ed_countproduct.commit();
//
//                                    ed_modifiers.clear();
//                                    ed_modifiers.commit();
                                    if (responseObject.has("booking_details") && !responseObject.isNull("booking_details")) {
                                        JSONObject dataBooking_details = responseObject.getJSONObject("booking_details");

                                        if (dataBooking_details.has("date_time") && !dataBooking_details.isNull("date_time")) {
                                            String date_time = dataBooking_details.getString("date_time");
                                            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date newDate = null;
                                            try {
                                                newDate = spf.parse(date_time);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            spf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                                            String strDate1 = spf.format(newDate);


                                            String[] separated = strDate1.split("\\ ");
                                            booking_date = separated[0];
                                            booking_time = separated[1] + " " + separated[2];
                                        }

                                    }


                                    JSONArray dataObj = responseObject.getJSONArray("sell_details");
                                    for (int i = 0; i < dataObj.length(); i++) {
                                        sell_linesList.add(dataObj.getJSONObject(i));

                                    }


                                    if (responseObject.has("employees") && !responseObject.isNull("employees")) {
                                        JSONArray employeesObj = responseObject.getJSONArray("employees");
                                        for (int i = 0; i < employeesObj.length(); i++) {
                                            JSONObject data = employeesObj.getJSONObject(i);
                                            CustomerListData customerListData = new CustomerListData();
                                            customerListData.setId(data.getInt("id"));
                                            customerListData.setText(data.getString("first_name") + " " + data.getString("last_name"));
//                                            if (data.has("email") && !data.isNull("email")) {
//                                                customerListData.setEmail(data.getString("email"));
//                                            }
                                            searchCustomerList.add(customerListData);
                                        }


                                    }
                                    Gson gson = new Gson();
                                    ed_cartSave.putString("searchCustomerList", gson.toJson(searchCustomerList));
                                    ed_cartSave.commit();

                                    if (responseObject.has("selected_booking_user") && !responseObject.isNull("selected_booking_user")) {
                                        String tags = "";
                                        try {
                                            tags = responseObject.getString("selected_booking_user");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (!tags.isEmpty()) {
                                            String[] elements = tags.split(",");

                                            for (int i = 0; i < elements.length; i++) {
                                                nameEmployeeIDlist.add(elements[i]);
                                            }

                                        }
                                    }

                                    productDetailFromIds();
                                }

                                JSONObject jsonObject = new JSONObject();
                                jsonObject = responseObject.getJSONObject("transaction");

                                shipping_details = jsonObject.getString("shipping_details");

                                if (jsonObject.has("total_before_tax") && !jsonObject.isNull("total_before_tax")) {

                                    total_before_tax = jsonObject.getString("total_before_tax");

                                    if (!total_before_tax.isEmpty()) {

                                        total_before_tax = jsonObject.getString("total_before_tax");
                                    } else {
                                        total_before_tax = "0";
                                    }
                                }

                                if (jsonObject.has("shipping_charges") && !jsonObject.isNull("shipping_charges")) {

                                    shipping_charges = jsonObject.getString("shipping_charges");

                                    if (!shipping_charges.isEmpty()) {
                                        shipping_charges = jsonObject.getString("shipping_charges");
                                    } else {
                                        shipping_charges = "0";
                                    }
                                }

                                Double final_total = 0.0;
                                final_total = Double.valueOf(jsonObject.getString("final_total"));
                                transaction = jsonObject.getInt("id");
                                location_id = jsonObject.getInt("location_id");


                                if (cartData.size() == 0) {
                                    iv_emptycart.setVisibility(View.VISIBLE);
                                    rlcart.setVisibility(View.GONE);
                                    rlAddPayment.setVisibility(View.GONE);
                                    lay_draft_quatation_suspend.setVisibility(View.GONE);
                                    if (paynowNext != null) {
                                        paynowNext.setVisibility(View.GONE);
                                    }
                                    if (lay_cart != null) {
                                        lay_cart.setVisibility(View.GONE);
                                    }
                                    if (lay_options != null) {
                                        lay_options.setVisibility(View.GONE);
                                    }
                                } else {
                                    iv_emptycart.setVisibility(View.GONE);
                                    rlcart.setVisibility(View.VISIBLE);
                                    rlAddPayment.setVisibility(View.VISIBLE);
                                    lay_draft_quatation_suspend.setVisibility(View.VISIBLE);
                                    if (paynowNext != null) {
                                        paynowNext.setVisibility(View.VISIBLE);
                                    }
                                    if (lay_cart != null) {
                                        lay_cart.setVisibility(View.VISIBLE);
                                    }
                                    if (lay_options != null) {
                                        lay_options.setVisibility(View.VISIBLE);
                                    }
                                }


                                String strSelectedDiscountType = "Select Discount Type";

                                if (jsonObject.has("discount_type") && !jsonObject.isNull("discount_type")) {
                                    if (jsonObject.getString("discount_type").equalsIgnoreCase("fixed")) {
                                        spnTotalDiscountType.setSelection(1);
                                        strSelectedDiscountType = "Fixed";

                                        if (jsonObject.has("discount_amount") && !jsonObject.isNull("discount_amount")) {
                                            if (!jsonObject.getString("discount_amount").isEmpty()) {
                                                String disEntered = jsonObject.getString("discount_amount");
                                                String currency_code = objBusiness.getCurrency_code();
                                                if (!currencyData.getName().equals(currency_code)) {
                                                    Double exchngeRate = 0.0;
                                                    exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                                                    totalDiscountAmt = String.valueOf(exchngeRate * Double.valueOf(disEntered));
                                                    edtDiscountTotal.setText(disEntered);

                                                } else {
                                                    totalDiscountAmt = disEntered;
                                                    edtDiscountTotal.setText(disEntered);
                                                }

                                            } else {
                                                edtDiscountTotal.setText("0.0");
                                            }
                                        } else {
                                            edtDiscountTotal.setText("0.0");
                                        }


                                    } else if (jsonObject.getString("discount_type").equalsIgnoreCase("percentage")) {
                                        spnTotalDiscountType.setSelection(2);
                                        strSelectedDiscountType = "Percentage";

                                        if (jsonObject.has("discount_amount") && !jsonObject.isNull("discount_amount")) {
                                            if (!jsonObject.getString("discount_amount").isEmpty()) {
                                                String disEntered = jsonObject.getString("discount_amount");
                                                String currency_code = objBusiness.getCurrency_code();
                                                totalDiscountAmt = disEntered;

                                                edtDiscountTotal.setText(disEntered);

//                                                    Double dTotalDiscountAmt = Double.valueOf(jsonObject.getString("discount_amount"));
////                                                    Double dTotalPercentageDis = (dTotalDiscountAmt/100)*Double.valueOf(total_before_tax);
//                                                    Double dTotalPercentageDis = ((dTotalDiscountAmt/Double.valueOf(total_before_tax))*100);
//                                                    edtDiscountTotal.setText(String.format("%.2f",dTotalPercentageDis));
                                            } else {

                                                Double dTotalPercentageDis = 0.0;

                                                edtDiscountTotal.setText(String.valueOf(dTotalPercentageDis));
                                            }
                                        } else {

                                            Double dTotalPercentageDis = 0.0;

                                            edtDiscountTotal.setText(String.valueOf(dTotalPercentageDis));
                                        }
                                    }
                                }

                                totalDiscountTypeItem = strSelectedDiscountType;
                                totalDiscountAmt = totalDiscountAmt;


                                //set tax value from edit sale api.
                                String strSelectedTaxType = "";

                                if (jsonObject.has("tax_id") && !jsonObject.isNull("tax_id")) {
                                    if (jsonObject.getInt("tax_id") != 0 && tax.size() > 1) {
                                        for (int i = 0; i < itemTax.size(); i++) {
                                            if (itemTax.get(i).getId() != "") {
                                                if (jsonObject.getInt("tax_id") == Integer.parseInt(itemTax.get(i).getId())) {
                                                    totalTaxType = tax.get(i);
                                                    taxData = itemTax.get(i);
                                                    spnTotalTax.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (!jsonObject.getString("tax_amount").equalsIgnoreCase("null")) {

                                        if (!jsonObject.getString("tax_amount").isEmpty()) {
                                            taxAmount = Double.valueOf(jsonObject.getString("tax_amount"));

                                        } else {
                                            taxAmount = 0.0;
                                        }
                                    } else {
                                        taxAmount = 0.0;
                                    }


                                } else {
                                    totalTaxType = tax.get(0);
                                    taxData = itemTax.get(0);
                                    taxAmount = 0.0;

                                }
                                edtTaxTotal.setText(df.format(taxAmount));
                                txtTotalTax.setText(df.format(taxAmount));

                                if (shipping_details != null && !shipping_details.equalsIgnoreCase("null")) {
                                    edtShipingDetailTotal.setText(shipping_details);
                                } else {
                                    edtShipingDetailTotal.setText("");
                                }

                                totalShipingCharge = shipping_charges;

                                edtShipingChargesTotal.setText(totalShipingCharge);

                                String currency_code = objBusiness.getCurrency_code();
                                if (!currencyData.getName().equals(currency_code)) {
                                    Double exchngeRate = 0.0;
                                    exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                                    total_before_tax = String.valueOf(exchngeRate * Double.parseDouble(total_before_tax));
                                }
//                                txtTotalProductPrice.setText(currencyData.getName()+String.format("%.2f",Double.valueOf(total_before_tax)));
                                txtTotalProductPrice.setText(String.format("%.2f", Double.valueOf(total_before_tax)));
                                txtTotalShipingCharge.setText(shipping_charges);
                                txtTotalProduct.setText(count + "");

                                if (getEditResponse.has("paid_amount") && !getEditResponse.isNull("paid_amount")) {
                                    totalPaid = Double.valueOf(getEditResponse.getString("paid_amount"));
                                    String currency_code1 = objBusiness.getCurrency_code();
                                    if (!currencyData.getName().equals(currency_code1)) {
                                        Double exchngeRate = 0.0;
                                        exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                                        totalPaid = exchngeRate * totalPaid;
                                    }
                                }
                                txtTotalPaid.setText("Total Paid: " + currencyData.getName() + " " + String.format("%.2f", totalPaid));

                                if (editOrExchnage.equalsIgnoreCase("forExchange")) {
//                                    txtTotalPayable.setText("Total Payable:  " + currencyData.getName() + " " + df.format(subTotal - totalPaid));
                                    txtTotalPayable.setText("" + df.format(subTotal - totalPaid));
                                } else {

                                    String currency_code1 = objBusiness.getCurrency_code();
                                    if (!currencyData.getName().equals(currency_code1)) {
                                        Double exchngeRate = 0.0;
                                        exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                                        final_total = exchngeRate * final_total;
                                    }
//                                    txtTotalPayable.setText("Total Payable: " +currencyData.getName()+" "+ String.format("%.2f",final_total));
                                    txtTotalPayable.setText("" + String.format("%.2f", final_total));
                                }


//                                if(editOrExchnage.equalsIgnoreCase("forEdit")){
//
//                                }else{
//                                    txtTotalPayable.setText("Total Payable: " +currencyData.getName()+" "+ String.valueOf(total_payable));


                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                            finish();

                        }
                    } else {//response body coming null.
                        AppConstant.sendEmailNotification(ActivityPosItemList.this, "pos/edit API", "(ActivityPosItemList Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(ActivityPosItemList.this, "Load Incomplete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ActivityPosItemList.this, "Load Incomplete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    finish();

                }

            });

        }
    }

    public JSONObject createUpdateSaleDetailMainObject(String parentComingFrom) {

        // update sale in case of due payement from list pos and delivery order
        JSONObject mainObject = new JSONObject();

        if (parentComingFrom.equalsIgnoreCase("fromListPosFragment") || parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            try {

                JSONObject jsonObject = getEditResponse.getJSONObject("transaction");
                mainObject.put("transaction_id", jsonObject.getInt("id"));
                mainObject.put("location_id", String.valueOf(location_id));
                mainObject.put("contact_id", String.valueOf(jsonObject.getInt("contact_id")));
                mainObject.put("booking_date", booking_date);
                mainObject.put("booking_time", booking_time);
                mainObject.put("order_type", orderr_type);
                String employee_id = nameEmployeeIDlist.toString().replace("[", "").replace("]", "");
                mainObject.put("service_staff_id", employee_id);
                JSONArray productArray = new JSONArray();

                for (int i = 0; i < cartData.size(); i++) {
                    SearchItem item = cartData.get(i);

                    JSONObject product = new JSONObject();

                    product.put("unit_price", String.valueOf(item.getSelling_price()));
                    product.put("line_discount_type", item.getDiscountType());

                    // product.put("line_discount_amount",item.getDiscount()); //sending here calculated amount.
                    product.put("line_discount_amount", item.getDiscountAmt()); // only input discount value.
                    product.put("sell_line_note", item.getSellLineNote());
                    product.put("item_tax", item.getTaxAmount());
                    product.put("tax_id", item.getTaxSelectId());

                    if (!item.getSellLineId().equalsIgnoreCase("")) {
                        product.put("transaction_sell_lines_id", String.valueOf(item.getSellLineId()));
                    } else {
                        product.put("transaction_sell_lines_id", "");
                    }


                    product.put("product_id", String.valueOf(item.getProduct_id()));
                    product.put("variation_id", String.valueOf(item.getVariation_id()));
                    product.put("quantity", String.valueOf(item.getQuantity()));
                    product.put("enable_stock", String.valueOf(item.getEnable_stock()));
                    product.put("unit_price_inc_tax", String.valueOf(item.getUnitFinalPrice()));
                    product.put("product_unit_id", "");

                    productArray.put(product);

                }

                mainObject.put("products", productArray);

                String strDiscountType = "";
                if (totalDiscountTypeItem.equalsIgnoreCase("Fixed")) {
                    strDiscountType = "fixed";

                } else if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                    strDiscountType = "percentage";
                }


                mainObject.put("discount_type", strDiscountType);
                mainObject.put("discount_amount", totalDiscountAmt);
//                mainObject.put("discount_amount", txtTotalDiscountPrice.getText().toString());
//                mainObject.put("shipping_details", edtShipingDetailTotal.getText().toString());
                mainObject.put("shipping_details", shipping_details);

//                mainObject.put("shipping_charges", edtShipingChargesTotal.getText().toString());
                mainObject.put("shipping_charges", totalShipingCharge);

                if (!totalTaxType.equalsIgnoreCase("Select Tax Type")) {
                    if (taxData != null) {
                        mainObject.put("tax_rate_id", taxData.getId());
                        mainObject.put("tax_calculation_amount", taxData.getAmount());
                    } else {
                        mainObject.put("tax_rate_id", "");
                        mainObject.put("tax_calculation_amount", "0.0");
                    }
                } else {
                    mainObject.put("tax_rate_id", "");
                    mainObject.put("tax_calculation_amount", "0.0");
                }
                mainObject.put("final_total", df.format(subTotal));
                mainObject.put("show_final_total", df.format(subTotal - totalPaid));

                JSONObject changeReturnObject = getEditResponse.getJSONObject("change_return");
                mainObject.put("change_return", String.valueOf(changeReturnObject.getDouble("amount")));
                mainObject.put("status", "final");
                mainObject.put("is_suspend", "0");
                mainObject.put("is_quotation", String.valueOf(jsonObject.getInt("is_quotation")));

                if (currencyData.getName().equals(objBusiness.getCurrency_code())) {
                    mainObject.put("exchange_rate", "1");
                } else {
                    mainObject.put("exchange_rate", objBusiness.getExchange_rate());
                }
                JSONArray paymenttLinesArray = getEditResponse.getJSONArray("payment_lines");
                String strPaymentId = "";

                for (int i = 0; i < paymenttLinesArray.length(); i++) {
                    JSONObject data = paymenttLinesArray.getJSONObject(i);
                    if (data.has("id") && !data.isNull("id")) {
                        strPaymentId = String.valueOf(data.getInt("id"));
                        break;
                    }

                }

                mainObject.put("paymentId", strPaymentId);

//            jsonObject.put("contact_id", location_id);

            } catch (JSONException e) {
                e.printStackTrace();

                return null;
            }

        } else {
            return null;
        }

        return mainObject;
    }

    public void updateDiscountDialog() {
        final Dialog discountDialog = new Dialog(ActivityPosItemList.this);
        discountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        discountDialog.setContentView(R.layout.discount_update);
        discountDialog.setCancelable(false);
        Window window = discountDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        ImageView img_cancel_dialog = discountDialog.findViewById(R.id.img_cancel_dialog);
        TextView txt_update_discount = discountDialog.findViewById(R.id.txt_update_discount);
        Spinner spnDiscountType = discountDialog.findViewById(R.id.spnTotalDiscountType);
        EditText edtDiscountAmt = discountDialog.findViewById(R.id.edtDiscountTotal);

        if (comingFrom.equalsIgnoreCase("fromSaleDetail") && parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            spnDiscountType.setEnabled(false);
            edtDiscountAmt.setEnabled(false);
        }

        txt_update_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountDialog.dismiss();
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountDialog.dismiss();
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, discountType);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnDiscountType.setAdapter(spinnerAdapter);


        for (int i = 0; i < discountType.length; i++) {
            if (totalDiscountTypeItem.equalsIgnoreCase(discountType[i])) {
                spnDiscountType.setSelection(i);
            }
        }
        edtDiscountAmt.setText(totalDiscountAmt);
//        spnDiscountType.setSelection();
        spnDiscountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                totalDiscountTypeItem = discountType[position];

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtDiscountAmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (totalDiscountTypeItem.equalsIgnoreCase("Fixed") || totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                    String disEntered = s.toString();
                    totalDiscountAmt = disEntered;
                    String currency_code = objBusiness.getCurrency_code();
                    if (!disEntered.isEmpty()) {
                        if (!currencyData.getName().equals(currency_code)) {
                            Double exchngeRate = 0.0;
                            exchngeRate = Double.valueOf(objBusiness.getExchange_rate());
                            if (totalDiscountTypeItem.equalsIgnoreCase("Percentage")) {
                                totalDiscountAmt = disEntered;
                            } else {
                                totalDiscountAmt = String.valueOf(exchngeRate * Double.valueOf(disEntered));
                            }

                        }
                    }

                    totalShow();
                    discountAmountShow();
                    totalTaxCalculate();
                    addShippingCharges();
                }
            }
        });
        discountDialog.show();
    }

    public void updateTaxDialog() {

        final Dialog taxDialog = new Dialog(ActivityPosItemList.this);
        taxDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        taxDialog.setContentView(R.layout.update_order_tax);
        taxDialog.setCancelable(false);
        Window window = taxDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        Spinner spnTaxType = taxDialog.findViewById(R.id.spnTotalTax);
        TextView txt_update_tax = taxDialog.findViewById(R.id.txt_update_tax);
        ImageView img_cancel_dialog = taxDialog.findViewById(R.id.img_cancel_dialog);

        ArrayAdapter<String> taxAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, tax);
        taxAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnTaxType.setAdapter(taxAdapter);

        for (int i = 0; i < tax.size(); i++) {
            if (totalTaxType.equalsIgnoreCase(tax.get(i))) {
                spnTaxType.setSelection(i);
            }
        }
//        edtDiscountAmt.setText(totalDiscountAmt);
        if (comingFrom.equalsIgnoreCase("fromSaleDetail") && parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            spnTaxType.setEnabled(false);
        }

        txt_update_tax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taxDialog.dismiss();
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taxDialog.dismiss();
            }
        });

        spnTaxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //subTotal = subTotal + Double.parseDouble(String.valueOf(totalvariation));
                totalTaxType = tax.get(position);
                taxData = itemTax.get(position);

                if (position == 0) {
                    taxData = null;
                }

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        taxDialog.show();
    }

    public void updateShippingDialog() {

        final Dialog shippingDialog = new Dialog(ActivityPosItemList.this);
        shippingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        shippingDialog.setContentView(R.layout.update_shipping_order);
        shippingDialog.setCancelable(false);
        Window window = shippingDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        EditText edtShipingDetailTotal = shippingDialog.findViewById(R.id.edtShipingDetailTotal);
        EditText edtShipingChargesTotal = shippingDialog.findViewById(R.id.edtShipingChargesTotal);
        TextView txt_update_shipping = shippingDialog.findViewById(R.id.txt_update_shipping);
        ImageView img_cancel_dialog = shippingDialog.findViewById(R.id.img_cancel_dialog);

        if (comingFrom.equalsIgnoreCase("fromSaleDetail") && parentComingFrom.equalsIgnoreCase("fromShippingFragment")) {
            edtShipingDetailTotal.setEnabled(false);
            edtShipingChargesTotal.setEnabled(false);
        }

        edtShipingDetailTotal.setText(shipping_details);
        edtShipingChargesTotal.setText(totalShipingCharge);

        txt_update_shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippingDialog.dismiss();
            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippingDialog.dismiss();
            }
        });

        edtShipingDetailTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                shipping_details = s.toString();

            }
        });
        edtShipingChargesTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                totalShipingCharge = s.toString();

                totalShow();
                discountAmountShow();
                totalTaxCalculate();
                addShippingCharges();

            }
        });

        shippingDialog.show();
    }

    public void productDetailFromIds() {


        Map<String, Object> obj = new HashMap<String, Object>();

        JSONObject requestJsonParameters = new JSONObject();

        String json = "";
        try {
            ArrayList<String> arrProducts = new ArrayList<String>();
            for (int i = 0; i < sell_linesList.size(); i++) {
                JSONObject objSellLine = (JSONObject) sell_linesList.get(i);

                if (objSellLine.has("product_id") && !objSellLine.isNull("product_id")) {

                    if (!objSellLine.getString("product_id").equalsIgnoreCase("")) {
                        arrProducts.add(objSellLine.getString("product_id"));
                    }
                }
            }
            obj.put("products", arrProducts);
            obj.put("location_id", location_id);

            Gson gson = new Gson();
            json = gson.toJson(obj);
            System.out.printf("JSON: %s", json);

            // requestJsonParameters.put("products",arrProducts);
            // requestJsonParameters.put("location_id",String.valueOf(idLocationId));

        } catch (JSONException e) {
            e.printStackTrace();
        }


//          AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(ActivityPosItemList.this);

        if (retrofit != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<SearchItemResponse> call = apiService.getProductDetailFromId("get-products-modifiers", body);
            call.enqueue(new Callback<SearchItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<SearchItemResponse> call, @NonNull Response<SearchItemResponse> response) {

                    if (response.body() != null) {
                        AppConstant.hideProgress();

                        Log.e("Product Search : ", "" + response.body());

                        if (response.body().isSuccess()) {
                            AppConstant.hideProgress();

                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();

                            try {

                                arrOrignalProdList = response.body().getProduct_list();
                                ArrayList<SearchItem> arrCartProducs = new ArrayList<SearchItem>();
                                if (sell_linesList.size() > 0) {
                                    for (int n = 0; n < sell_linesList.size(); n++) {
                                        JSONObject objSellLine = (JSONObject) sell_linesList.get(n);

                                        int proID = objSellLine.getInt("product_id");

                                        SearchItem objNewSearch = null;
                                        for (int j = 0; j < arrOrignalProdList.size(); j++) {
                                            SearchItem objTemp = arrOrignalProdList.get(j);

                                            if (String.valueOf(proID).equalsIgnoreCase(objTemp.getProduct_id())) {
                                                objNewSearch = objTemp;
                                                break;
                                            }
                                        }

                                        if (objNewSearch == null) {
                                            Toast.makeText(ActivityPosItemList.this, "Unable To Edit, Because of Incomplete Sale Detail", Toast.LENGTH_LONG).show();
                                        }

                                        objNewSearch.setActiveProduct(true);
                                        objNewSearch.setCalculationDone(false);

                                        //now fetching selling price from sell line object
                                        if (objSellLine.has("unit_price_before_discount") && !objSellLine.isNull("unit_price_before_discount")) {
                                            objNewSearch.setSelling_price(objSellLine.getString("unit_price_before_discount"));
                                        }

//                                        objNewSearch.setSellLineId(String.valueOf(objSellLine.getInt("id")));
//                                        objNewSearch.setSellLineNote(objSellLine.getString("sell_line_note"));
                                        if (objSellLine.has("line_discount_type") && !objSellLine.isNull("line_discount_type")) {
                                            objNewSearch.setDiscountType(objSellLine.getString("line_discount_type"));
                                            if (objSellLine.getString("line_discount_type").equalsIgnoreCase("fixed")) {
                                                objNewSearch.setDiscountt(objSellLine.getString("line_discount_amount"));

                                                objNewSearch.setDiscountAmt(objNewSearch.getDiscountt());

                                            } else if (objSellLine.getString("line_discount_type").equalsIgnoreCase("percentage")) {
                                                objNewSearch.setDiscountAmt(objSellLine.getString("line_discount_amount"));
//                        Double dDiscountAmt = Double.valueOf(objSellLine.getString("line_discount_amount"));
//                        Double dPercentageDis = (dDiscountAmt * 100) / Double.valueOf(objSellLine.getString("unit_price_before_discount"));
//                        objNewSearch.setDiscountAmt(String.valueOf(dPercentageDis));
                                            }
                                        }


                                        if (objSellLine != null) {
                                            objNewSearch.setEnable_stock(String.valueOf(objSellLine.getInt("enable_stock")));
                                        }

                                        objNewSearch.setFinalPrice(String.valueOf(objSellLine.getInt("quantity_ordered") * Double.valueOf(objSellLine.getString("default_sell_price"))));
                                        objNewSearch.setQuantity(objSellLine.getInt("quantity_ordered"));


                                        if (objSellLine.has("item_tax") && !objSellLine.isNull("item_tax")) {
                                            //temp set to Zero , when use tax then change.
                                            objNewSearch.setTaxAmount("0.0");
                                            objNewSearch.setTaxCalculateAmt(0.0);
                                            objNewSearch.setTaxCalculationAmt("0.0");
                                            objNewSearch.setTaxType("Select Tax Type");
                                        } else {
                                            objNewSearch.setTaxAmount("0.0");
                                            objNewSearch.setTaxCalculateAmt(0.0);
                                            objNewSearch.setTaxCalculationAmt("0.0");
                                            objNewSearch.setTaxType("Select Tax Type");
                                        }

                                        objNewSearch.setTaxCalculationDone(false);
                                        objNewSearch.setUnitFinalPrice(objSellLine.getString("sell_price_inc_tax"));

                                        Double dTotalVariationPrice = 0.0;
                                        String strSelectedVariationName = "";

                                        objNewSearch.setVariationPrice(dTotalVariationPrice);

//                                        if (objSellLine.has("variations") && !objSellLine.isNull("variations")) {
//                                            JSONObject objVaria = objSellLine.getJSONObject("variations");
//
////                        if (arrSelectedProductModifier.size() == 0) {
//                                            objNewSearch.setVariation(objVaria.getString("name"));
//
//                                            if (objVaria.has("color") && !objVaria.isNull("color")) {
//                                                JSONObject objColor = objVaria.getJSONObject("color");
//
//                                                objNewSearch.setColor(objColor.getString("name"));
////                            }
//                                            }
//
//                                            if (objVaria.has("variation_location_details") && !objVaria.isNull("variation_location_details")) {
//                                                JSONArray arrVariationLocDetails = objVaria.getJSONArray("variation_location_details");
//
//                                                for (int j = 0; j < arrVariationLocDetails.length(); j++) {
//                                                    JSONObject obj = arrVariationLocDetails.getJSONObject(j);
//
//                                                    if (obj.has("location_id") && !obj.isNull("location_id")) {
//                                                        if (obj.getInt("location_id") == location_id) {
//                                                            objNewSearch.setQty_available(obj.getString("qty_available"));
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//
//                                                if (arrVariationLocDetails.length() == 0) {
//                                                    objNewSearch.setQty_available("1.0");
//                                                }
//
//                                            } else {
//                                                objNewSearch.setQty_available("1.0");
//                                            }
//                                        }

                                        objNewSearch.setQty_available("1");
                                        objNewSearch.setVariation_name(strSelectedVariationName);

                                        arrCartProducs.add(objNewSearch);

                                    }

                                } else {
                                    Toast.makeText(ActivityPosItemList.this, "Incomplete Sale Detail. Can't Go For Edit", Toast.LENGTH_LONG).show();

                                }


                                //set cart count.
                                ed_countproduct.putString("countt", String.valueOf(arrCartProducs.size()));
                                ed_countproduct.commit();

                                Gson gson = new Gson();
                                //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
                                ed_cartSave.putString("myCart", gson.toJson(arrCartProducs));
                                ed_cartSave.commit();
                                onServiceList();

                            } catch (Exception e) {

                            }


                        } else {
                            Toast.makeText(ActivityPosItemList.this, "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(ActivityPosItemList.this, "get-products-modifiers API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(ActivityPosItemList.this, "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onFailure(Call<SearchItemResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ActivityPosItemList.this, "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }


//        if (retrofit != null)
//        {
//            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJsonParameters.toString());
//            APIInterface apiService = retrofit.create(APIInterface.class);
//            Call<ResponseBody> call = apiService.rewardAmount("get-products-modifiers", body);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                    if (response.body() != null) {
//                        AppConstant.hideProgress();
//
//                        try {
//                            if (response.body() != null) {
//                                AppConstant.hideProgress();
//                                String respo = response.body().string();
//                                JSONObject responseObject = new JSONObject(respo);
//
//
//                                String successstatus = responseObject.optString("success");
//                                //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
//                                if (successstatus.equalsIgnoreCase("true")) {
//
//                                    Toast.makeText(getContext(), "Success Response", Toast.LENGTH_LONG).show();
////
//                                    if(responseObject.has("product_list"))
//                                    {
//                                        JSONArray arrProducts = responseObject.getJSONArray("product_list");
//
//
//
//                                    }
//
//                                }
//                            } else {
//                                AppConstant.hideProgress();
//                                Toast.makeText(getContext(), "Web API Service Error", Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (Exception e) {
//
//                            AppConstant.hideProgress();
//                            Log.e("Exception", e.toString());
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    AppConstant.hideProgress();
//                }
//
//            });
//
//        }

    }

}