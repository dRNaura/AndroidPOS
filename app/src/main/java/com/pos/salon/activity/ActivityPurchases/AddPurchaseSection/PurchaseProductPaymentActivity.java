package com.pos.salon.activity.ActivityPurchases.AddPurchaseSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPurchases.PurchaseDetailActivity;
import com.pos.salon.activity.ActivityPurchases.PurchasesListActivity;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.PurchaseModel.ProductPurchaseDataSend;
import com.pos.salon.model.PurchaseModel.PurchaseProductDataSend;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PurchaseProductPaymentActivity extends AppCompatActivity {

    Toolbar toolbar;
    RelativeLayout paynowNext;
    PurchaseProductDataSend paymemtData;
    TextView txtTotalPay, tv_dueamount, tv_balance;
    EditText cashAmount;
    double totalbill = 0.0;
    ArrayList<ProductPurchaseDataSend> cartData;
    ArrayList<ProductPurchaseDataSend> products = new ArrayList<>();
    int location_id = 0, supplierID = 0,purchase_id=0;
    String comingFrom = "", reference_no = "", purchaseDate = "", purchaseStatus = "";
    SharedPreferences  sp_countproduct, sp_cartSave;
    SharedPreferences.Editor  ed_countproduct, ed_cartSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_product_payment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        paynowNext = findViewById(R.id.paynowNext);
        txtTotalPay = findViewById(R.id.txtTotalPay);
        cashAmount = findViewById(R.id.cashAmount);
        tv_dueamount = findViewById(R.id.tv_dueamount);
        tv_balance = findViewById(R.id.tv_balance);

        paymemtData = (PurchaseProductDataSend) getIntent().getSerializableExtra("purchaseProductDataSend");
        location_id = getIntent().getIntExtra("location_id", 0);
        reference_no = getIntent().getStringExtra("reference_no");
        purchaseDate = getIntent().getStringExtra("purchaseDate");
        purchaseStatus = getIntent().getStringExtra("purchaseStatus");
        supplierID = getIntent().getIntExtra("supplierID", 0);
        comingFrom = getIntent().getStringExtra("comingFrom");
        purchase_id = getIntent().getIntExtra("purchase_id",0);

        cartData = paymemtData.getPurchases();

        txtTotalPay.setText("$" + paymemtData.getFinal_total());
        totalbill = paymemtData.getFinal_total();

        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        listeners();
        setBackNavgation();
    }

    public void listeners() {

           cashAmount.setText(""+paymemtData.getFinal_total());

           cashAmount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = cashAmount.getText().toString();
                String temp = cashAmount.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = cashAmount.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }
                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }
                }
                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});
        cashAmount.addTextChangedListener(new TextWatcher() {
            double cashh;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().trim().length() > 0) {
                    cashh = Double.valueOf(editable.toString());

                    if (totalbill <= (cashh)) {
                        double balance = Math.abs((cashh) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }
                } else {
                    cashh = 0.00;
                    if (totalbill <= (cashh)) {
                        double balance = Math.abs((cashh) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }

                }
            }
        });


        paynowNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cashAmount.getText().toString().isEmpty()){
                    AppConstant.showToast(PurchaseProductPaymentActivity.this, "Please Enter Amount");
                }else {
                    double amount =Double.parseDouble(cashAmount.getText().toString());
                    if(amount > 0){

                        if (comingFrom.equalsIgnoreCase("fromDetail")) {

                            PurchaseProductDataSend objPayData = new PurchaseProductDataSend();
                            objPayData = paymemtData;
                            addCartProduct();
                            callUpdateAPI(objPayData);

                        }else{
                            PurchaseProductDataSend objPayData = new PurchaseProductDataSend();
                            objPayData = paymemtData;
                            addCartProduct();
                            callPaymentAPI(objPayData);
                        }

                    }else{
                        AppConstant.showToast(PurchaseProductPaymentActivity.this, "Please Enter Valid Amount");
                    }

                }

            }
        });
    }

    private void callPaymentAPI(PurchaseProductDataSend paymentData) {

        AppConstant.showProgress(this, false);
        Gson gson = new Gson();
        String json = gson.toJson(paymentData);
        Log.e("Payment Json : ", json);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call=null;
            call = apiService.sendPuchasePayment("purchases", paymentData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            if (respo == null) {
                                Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Payment res", "" + respo);

                            String successstatus = responseObject.optString("success");
                            String message = "";
                            message = responseObject.optString("msg");
                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true"))
                            {

                                if (AddPurchaseActivity.addPurchaseActivity != null) {
                                    AddPurchaseActivity.addPurchaseActivity.finish();
                                }

                                if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
                                    PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
                                }

                                if (PurchaseProductSearchList.purchaseProductSearchList != null) {
                                    PurchaseProductSearchList.purchaseProductSearchList.finish();
                                }
                                if (PurchasesListActivity.purchaseListActivity != null) {
                                    PurchasesListActivity.purchaseListActivity.finish();
                                }
                                if (PurchasesListActivity.purchaseListActivity != null) {
                                PurchasesListActivity.purchaseListActivity.finish();
                                }

                                cartData.clear();
                                //clear cart product data if comes on this screen.
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
                                    ed_cartSave.remove("myCart");
                                    ed_cartSave.commit();
                                }

                                ed_countproduct.clear();
                                ed_countproduct.commit();
                                AppConstant.showToast(PurchaseProductPaymentActivity.this, "" + message);

                                Intent intent = new Intent(PurchaseProductPaymentActivity.this, PurchasesListActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(PurchaseProductPaymentActivity.this,"pos API","(ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception",e.toString());
                        e.printStackTrace();
                        Toast.makeText(PurchaseProductPaymentActivity.this, "" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void addCartProduct() {

        for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
            ProductPurchaseDataSend objItem = cartData.get(cartCount);
            ProductPurchaseDataSend product = new ProductPurchaseDataSend();
            product.product_id = objItem.product_id;
            product.variation_id = objItem.variation_id;
            product.quantity = objItem.quantity;
            product.discount_percent = objItem.discount_percent;
            product.profit_percent = objItem.profit_percent;
            product.pp_without_discount = objItem.pp_without_discount;
            product.purchase_price = objItem.purchase_price;
            product.purchase_line_tax_id = objItem.purchase_line_tax_id;
            product.item_tax = objItem.item_tax;
            product.purchase_price_inc_tax = objItem.purchase_price_inc_tax;
            product.default_sell_price = objItem.default_sell_price;
            products.add(product);
        }
        paymemtData.setPurchases(products);
        paymemtData.setContact_id(supplierID);
        paymemtData.setLocation_id(location_id);
        paymemtData.setStatus(purchaseStatus);
        paymemtData.setRef_no(reference_no);
        paymemtData.setTransaction_date(purchaseDate);
        paymemtData.setFinal_total(paymemtData.getFinal_total());
        paymemtData.setPayment_amount(Double.parseDouble(cashAmount.getText().toString()));
        paymemtData.setPayment_method("cash");
        paymemtData.setTotal_before_tax(paymemtData.getFinal_total());

    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add Payment");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

    private void callUpdateAPI(PurchaseProductDataSend paymentData) {
        AppConstant.showProgress(this, false);
        Gson gson = new Gson();
        String json = gson.toJson(paymentData);
        Log.e("Update Json : ", json);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call=null;
            call = apiService.updatePuchasePayment("purchases/"+purchase_id, paymentData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            if (respo == null) {
                                Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Payment res", "" + respo);

                            String successstatus = responseObject.optString("success");
                            String message = "";
                            message = responseObject.optString("msg");
                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true"))
                            {

                                if (AddPurchaseActivity.addPurchaseActivity != null) {
                                    AddPurchaseActivity.addPurchaseActivity.finish();
                                }

                                if (PurchaseProductSearchActivity.purchaseProductSearchActivity != null) {
                                    PurchaseProductSearchActivity.purchaseProductSearchActivity.finish();
                                }

                                if (PurchaseProductSearchList.purchaseProductSearchList != null) {
                                    PurchaseProductSearchList.purchaseProductSearchList.finish();
                                }
                                if (PurchaseDetailActivity.purchaseDetailActivity != null) {
                                    PurchaseDetailActivity.purchaseDetailActivity.finish();
                                }
                                if (PurchasesListActivity.purchaseListActivity != null) {
                                    PurchasesListActivity.purchaseListActivity.finish();
                                }

                                cartData.clear();
                                //clear cart product data if comes on this screen.
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
                                    ed_cartSave.remove("myCart");
                                    ed_cartSave.commit();
                                }

                                ed_countproduct.clear();
                                ed_countproduct.commit();
                                AppConstant.showToast(PurchaseProductPaymentActivity.this, "" + message);

                                Intent intent = new Intent(PurchaseProductPaymentActivity.this, PurchasesListActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(PurchaseProductPaymentActivity.this,"pos API","(ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception",e.toString());
                        e.printStackTrace();
                        Toast.makeText(PurchaseProductPaymentActivity.this, "" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(PurchaseProductPaymentActivity.this, "Something Wrong!! Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


}
