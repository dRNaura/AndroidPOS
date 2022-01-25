package com.pos.salon.activity.ActivityPayment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivityPosItemList;
import com.pos.salon.activity.ActivityPosSale.ActivityPosTerminalDropdown;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.activity.ConfirmationActivity;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.payment.Payment;
import com.pos.salon.model.payment.PaymentDataSend;
import com.pos.salon.model.payment.ProductDataSend;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivityPayment extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ActivityPayment";
    Calendar myCalendar;
    EditText et_card_amount, cardHolderName, edtDateSet, cardNumber, edtCVV, cardAmount, cashAmount, et_awardAmount, et_storeCredit;
    Toolbar toolbar;
    RelativeLayout paynowNext;
    double storeDouble = 0, rewardDouble = 0;
    TextView txtTotalPay, reward_price, store_credit;
    LinearLayout llCashPay, llDebitCard, llCreditCard, layout_reward, layout_storeCredit;
    ImageView imgCreditCard, imgDebitCard, imgCashPay;
    TextView mTvLog, txtCreditcard, txtDebitcard, txtCashPay, tv_dueamount;
    PaymentDataSend paymemtData;
    ArrayList<Payment> payment = new ArrayList<Payment>();
    Payment paymentObj = new Payment();
    String paymentMethod = "card", cardType = "Visa";
    ArrayList<ProductDataSend> cartData;
    FrameLayout frameVisa, frameMaster, frameCredit, frameDebit;
    double totalbill = 0;
    ArrayList<ProductDataSend> products = new ArrayList<ProductDataSend>();
    TextView tv_balance,tv_duecurrency,tv_dueBalance;
    String discounttype="";
    String partialpayment="";
    Double discountamt = 0.0;
    String taxrate="";
    String taxcalculation="";
    String shippingdetail="";
    String shippingcharges="";
    String subtotal="";
    String changereturn="";
    int customerId=0, transactionId = 0,productOrService=1;
    String locationId="";
    CurriencyData currencydata;
    ArrayList<String> st = new ArrayList<>();
    SharedPreferences sp_selectedcustomer, sp_countproduct, sp_modifiers, sp_cartSave;
    SharedPreferences.Editor ed_selectedcustomer, ed_countproduct, ed_modifiers, ed_cartSave;
    TextView tv;
    String booking_time="",booking_date="",paidamount="",repair_product_id="",editOrExchange="";
    String orderr_type = "", tablee_id = "", waiterr_id = "", comingFrom = "", parentComingFrom = "",employee_id;
    DecimalFormat df = new DecimalFormat("##.00");
    Context getScreenContext;
    protected Dialog mSearchDialog;
    private StringBuffer sbLog = new StringBuffer();
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    //end card search.
    ImageView img_search_card;
    RelativeLayout rl_swipe, rl_tap, rl_insert;
    SharedPreferences sharedPreferences;
    BusinessDetails objBusiness=new BusinessDetails();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        layout_reward = (LinearLayout) findViewById(R.id.layout_reward);
        layout_storeCredit = (LinearLayout) findViewById(R.id.layout_storeCredit);

        getScreenContext = ActivityPayment.this;
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        tv = findViewById(R.id.tv);

        comingFrom = getIntent().getStringExtra("comingFrom");
        parentComingFrom = getIntent().getStringExtra("parentfrom");
        repair_product_id = getIntent().getStringExtra("repair_product_id");


        Gson gson = new Gson();Type type = new TypeToken<BusinessDetails>() {}.getType();
        final String myBusinessDetail = sharedPreferences.getString("myBusinessDetail", "");
        if (myBusinessDetail != null) {
            objBusiness = (BusinessDetails) gson.fromJson(myBusinessDetail, type);
        }


        if (comingFrom.equalsIgnoreCase("partialCustomer")) {
            //total amount to be payable
            totalbill = Double.parseDouble(getIntent().getStringExtra("totalPayable"));
        }

        else {
            paymemtData = (PaymentDataSend) getIntent().getSerializableExtra("paymentObject");
            cartData = paymemtData.getProducts();// dbHelper.getAllItem();
            paidamount = getIntent().getStringExtra("paidamount");
            taxrate = getIntent().getStringExtra("taxrate");
            taxcalculation = getIntent().getStringExtra("taxcalculation"); //in this tax id => tax amount value sent.
            subtotal = getIntent().getStringExtra("subtotal");
            changereturn = getIntent().getStringExtra("changereturn");
            orderr_type = getIntent().getStringExtra("selected_ordertype");
            tablee_id = getIntent().getStringExtra("tablee_id");
            waiterr_id = getIntent().getStringExtra("waiterr_id");
            employee_id = getIntent().getStringExtra("employee_id");

            //total amount to be payable
            totalbill = Double.parseDouble(paymemtData.getFinal_total());
        }

        locationId = getIntent().getStringExtra("locationId");
        productOrService = getIntent().getIntExtra("productOrService",1);
        booking_time = getIntent().getStringExtra("booking_time");
        booking_date = getIntent().getStringExtra("booking_date");

        customerId = getIntent().getIntExtra("customerId", 0);
        Log.e("CustomerId", "" + customerId);

        transactionId = getIntent().getIntExtra("transaction_id", 0);
        System.out.println("THE TRANSACTION ID IS " + transactionId);

        if(getIntent().hasExtra("editOrExchnage")){
            editOrExchange=getIntent().getStringExtra("editOrExchnage");
        }

        if (getIntent().hasExtra("partialpayment")) {
            partialpayment = getIntent().getStringExtra("partialpayment");
            //Toast.makeText(this, "NotPartial"+partialpayment, Toast.LENGTH_LONG).show();
        } else {
            partialpayment = "null";
            //Toast.makeText(this, "NotPartial", Toast.LENGTH_LONG).show();

        }

        if (getIntent().hasExtra("shippingdetail")) {
            shippingdetail = getIntent().getStringExtra("shippingdetail");
        } else {
            shippingdetail = "";
        }
        if (getIntent().hasExtra("shippingcharges")) {
            shippingcharges = getIntent().getStringExtra("shippingcharges");
        } else {
            shippingcharges = "0.00";
        }

        if (getIntent().hasExtra("discounttype")) {
            discounttype = getIntent().getStringExtra("discounttype");

        } else {
            discounttype = "";
        }
        // Toast.makeText(this, ""+discounttype, Toast.LENGTH_LONG).show();
        if (getIntent().hasExtra("discountamount")) {

            discountamt = getIntent().getDoubleExtra("discountamount", 0.0);
        } else {
            discountamt = 0.0;
        }

        currencydata = (CurriencyData) getIntent().getSerializableExtra("currency");


        // Fetched shared preference values
        sp_modifiers = getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE);
        ed_modifiers = sp_modifiers.edit();

        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
        ed_selectedcustomer = sp_selectedcustomer.edit();
        //end : Fetched shared prefernece values.


        if (sp_selectedcustomer.getString("customernamee", "").equalsIgnoreCase("Cash Customer") || comingFrom.equalsIgnoreCase("fromSaleDetail")) {// hide reward ,  hide store credit. when customer not select or if comes from sale detail for edit.
            layout_storeCredit.setVisibility(View.GONE);
            layout_reward.setVisibility(View.GONE);

        } else {//select customer : un hide reward ,un hide store credit ,

            if(AppConstant.isNetworkAvailable(ActivityPayment.this)){
                storeCredit();
                rewardAmount();
            }else{
                AppConstant.isNetworkAvailable(ActivityPayment.this);
            }

        }


        setWidget();
        setBackNavgation();
    }

    void storeCredit() {
        AppConstant.showProgress(getScreenContext, false);
        Retrofit retrofit = APIClient.getClientToken(getScreenContext);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contact_id", customerId);

            if (transactionId > 0) {
                jsonObject.put("transaction_id", transactionId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (retrofit != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.storeCredit("get-available-sc", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Store Credit Response", responseObject.toString());

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                String storeCredit = "0.0";

                                if (responseObject.has("total_sc") && !responseObject.isNull("total_sc")) {
                                    storeCredit = responseObject.getString("total_sc");
                                }

//                            double stoCredit=
                                storeDouble = Double.valueOf(storeCredit);


                                if (storeDouble > 0) {
                                    layout_storeCredit.setVisibility(View.VISIBLE);
                                    store_credit.setText(currencydata.getName() + " " + (df.format(storeDouble)));
//
                                } else {
                                    layout_storeCredit.setVisibility(View.GONE);
//
                                }
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ActivityPayment.this,"get-available-sc API","(ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(getScreenContext, "Could Not Fetch Customer Store Credit. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                    }

//                    AppConstant.hideProgress();
//                    AddUserResposne userResponse = response.body();
//                    if (userResponse.getSuccess() == true) {
////                        String respo = response.body().string();
////                        JSONObject responseObject = new JSONObject(respo);
////                        tv.setText(respo);
//
//                        storeCredit = userResponse.getData().getTotal_reward();
//                        reward_price.setText(store);
//
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getScreenContext, "Could Not Fetch Customer Store Credit. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void rewardAmount() {
//        AppConstant.showProgress(getScreenContext,false);
        Retrofit retrofit = APIClient.getClientToken(getScreenContext);

        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("contact_id", customerId);
                if (transactionId > 0) {
                    jsonObject.put("transaction_id", transactionId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.rewardAmount("get-available-reward", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Reward Response", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                String reward = "0.0";
                                if (responseObject.has("total_reward") && !responseObject.isNull("total_reward")) {

                                    reward = responseObject.getString("total_reward");
                                }
//                            double stoCredit=
                                rewardDouble = Double.valueOf(reward);

                                if (rewardDouble > 0) {
                                    layout_reward.setVisibility(View.VISIBLE);
                                    reward_price.setText(currencydata.getName() + " " + (df.format(rewardDouble)));

                                } else {
                                    layout_reward.setVisibility(View.GONE);
//
                                }

//                            Toast.makeText(getScreenContext, "" +respo, Toast.LENGTH_LONG).show();
                                //  Toast.makeText( this, ""+respo, Toast.LENGTH_LONG).show();
//                            String successstatus = responseObject.optString("success");
//                            String message = responseObject.optString("msg");
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ActivityPayment.this,"get-available-reward API","(ActivityPyament Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getScreenContext, "Could Not Load Customer Reward Amount. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                    }

//                    AppConstant.hideProgress();
//                    AddUserResposne userResponse = response.body();
//                    if (userResponse.getSuccess() == true) {
////                        String respo = response.body().string();
////                        JSONObject responseObject = new JSONObject(respo);
////                        tv.setText(respo);
//
//                        storeCredit = userResponse.getData().getTotal_reward();
//                        reward_price.setText(store);
//
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getScreenContext, "Could Not Load Customer Reward Amount. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void setWidget() {

        myCalendar = Calendar.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtDateSet = (EditText) findViewById(R.id.edtDateSet);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_duecurrency = (TextView) findViewById(R.id.tv_duecurrency);
        tv_dueBalance = (TextView) findViewById(R.id.tv_dueBalance);

        cashAmount = (EditText) findViewById(R.id.cashAmount);
        cardAmount = (EditText) findViewById(R.id.cardAmount);
        et_storeCredit = (EditText) findViewById(R.id.et_storeCredit);
        et_awardAmount = (EditText) findViewById(R.id.et_awardAmount);
        tv_dueamount = (TextView) findViewById(R.id.tv_dueamount);
        reward_price = (TextView) findViewById(R.id.reward_price);
        store_credit = (TextView) findViewById(R.id.store_credit);
        et_card_amount = (EditText) findViewById(R.id.et_card_Amount);
        mTvLog = (TextView) findViewById(R.id.tv_log);

        //Card Payment
        rl_insert = findViewById(R.id.rl_insert);
        rl_tap = findViewById(R.id.rl_tap);
        rl_swipe = findViewById(R.id.rl_swipe);

        img_search_card = findViewById(R.id.img_search_card);

        edtDateSet.setOnClickListener(this);

        cardHolderName = (EditText) findViewById(R.id.cardHolderName);
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        edtCVV = (EditText) findViewById(R.id.edtCVV);

        paynowNext = (RelativeLayout) findViewById(R.id.paynowNext);
        paynowNext.setOnClickListener(this);

        llCreditCard = (LinearLayout) findViewById(R.id.llCreditCard);
        llCreditCard.setOnClickListener(this);
        llDebitCard = (LinearLayout) findViewById(R.id.llDebitCard);
        llDebitCard.setOnClickListener(this);
        llCashPay = (LinearLayout) findViewById(R.id.llCashPay);
        llCashPay.setOnClickListener(this);

        frameVisa = (FrameLayout) findViewById(R.id.frameVisa);
        frameVisa.setOnClickListener(this);
        frameMaster = (FrameLayout) findViewById(R.id.frameMaster);
        frameMaster.setOnClickListener(this);
        frameCredit = (FrameLayout) findViewById(R.id.frameCredit);
        frameCredit.setOnClickListener(this);
        frameDebit = (FrameLayout) findViewById(R.id.frameDebit);
        frameDebit.setOnClickListener(this);

        imgCreditCard = (ImageView) findViewById(R.id.imgCreditCard);
        imgDebitCard = (ImageView) findViewById(R.id.imgDebitCard);
        imgCashPay = (ImageView) findViewById(R.id.imgCashPay);

        txtCreditcard = (TextView) findViewById(R.id.txtCreditcard);
        txtDebitcard = (TextView) findViewById(R.id.txtDebitcard);
        txtCashPay = (TextView) findViewById(R.id.txtCashPay);

        txtTotalPay = (TextView) findViewById(R.id.txtTotalPay);

        txtTotalPay.setText(currencydata.getName() + " " + df.format(totalbill));
        tv_duecurrency.setText("("+currencydata.getName()+ ")");
        tv_dueBalance.setText("("+currencydata.getName()+ ")");

        et_storeCredit.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = et_storeCredit.getText().toString();
                String temp = et_storeCredit.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = et_storeCredit.getSelectionStart();
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

        et_storeCredit.addTextChangedListener(new TextWatcher() {
            double cardd;
            double cashh;
            double store;
            double reward;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (Double.valueOf(et_storeCredit.getText().toString()) > storeDouble) {
                        et_storeCredit.setError("Enter a Valid Amount");
                        et_storeCredit.setText("");
//                       Toast.makeText(getScreenContext,"Plase Enetr a valid Amount",Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                if (s.toString().trim().length() > 0) {

                    store = Double.valueOf(String.valueOf(s));

                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());
                    } else {
                        cashh = 0.00;
                    }

                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());
                    } else {
                        reward = 0.00;
                    }


                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }
                } else {
                    store = 0.00;
                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());
                    } else {
                        cashh = 0.00;
                    }

                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());
                    } else {
                        reward = 0.00;
                    }

                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }

                }
            }
        });


        et_awardAmount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = et_awardAmount.getText().toString();
                String temp = et_awardAmount.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = et_awardAmount.getSelectionStart();
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

        et_awardAmount.addTextChangedListener(new TextWatcher() {
            double cardd = 0.0;
            double cashh = 0.0;
            double store = 0.0;
            double reward = 0.0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (Double.valueOf(et_awardAmount.getText().toString()) > rewardDouble) {

                        et_awardAmount.setError("Enter a valid Amount");
                        et_awardAmount.setText("");

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (s.toString().trim().length() > 0) {

                    reward = Double.valueOf(s.toString());

                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());
                    } else {
                        cashh = 0.00;
                    }
                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());
                    } else {
                        store = 0.00;
                    }

                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {

                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }

                } else {
                    reward = 0.00;
                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());
                    } else {
                        cashh = 0.00;
                    }

                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());
                    } else {
                        store = 0.00;
                    }
                    if (totalbill <= (cashh + reward + cashh + store)) {
                        double balance = Math.abs((cashh + reward + cardd + store) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + reward + cardd + store));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }
//
                }


            }
        });

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
            double cardd;
            double cashh;
            double store;
            double reward;

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
                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());
                    } else {
                        store = 0.00;
                    }

                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());

                    } else {
                        reward = 0.00;
                    }
                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }
                } else {
                    cashh = 0.00;
                    if (!cardAmount.getText().toString().isEmpty()) {
                        cardd = Double.valueOf(cardAmount.getText().toString());
                    } else {
                        cardd = 0.00;
                    }

                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());
                    } else {
                        store = 0.00;
                    }

                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());

                    } else {
                        reward = 0.00;
                    }

                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }

                }
            }
        });


        //for display amount in currency format #####.##
        cardAmount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = cardAmount.getText().toString();
                String temp = cardAmount.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = cardAmount.getSelectionStart();
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

        cardAmount.addTextChangedListener(new TextWatcher() {
            double cashh;
            double cardd;
            double store;
            double reward;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.toString().trim().length() > 0) {

                    cardd = Double.valueOf(editable.toString());
                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());

                    } else {
                        cashh = 0.00;
                    }
                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());

                    } else {
                        store = 0.00;
                    }
                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());
                    } else {
                        reward = 0.00;
                    }

                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cardd + cashh + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cardd + cashh + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }

                } else {
                    cardd = 0.00;
                    if (!cashAmount.getText().toString().isEmpty()) {
                        cashh = Double.valueOf(cashAmount.getText().toString());


                    } else {
                        cashh = 0.00;
                    }
                    if (!et_storeCredit.getText().toString().isEmpty()) {
                        store = Double.valueOf(et_storeCredit.getText().toString());

                    } else {
                        store = 0.00;
                    }
                    if (!et_awardAmount.getText().toString().isEmpty()) {
                        reward = Double.valueOf(et_awardAmount.getText().toString());
                    } else {
                        reward = 0.00;
                    }

                    if (totalbill <= (cashh + cardd + store + reward)) {
                        double balance = Math.abs((cashh + cardd + store + reward) - totalbill);
                        tv_balance.setText(String.format("%.2f", balance));
                        tv_dueamount.setText("0.00");
                    } else {
                        double duebalance = Math.abs(totalbill - (cashh + cardd + store + reward));
                        tv_dueamount.setText(String.format("%.2f", duebalance));
                        tv_balance.setText("0.00");
                    }
                }

            }
        });


        //click listener for insert,Tap,Swipe
//
//        rl_insert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                CardReaderTypeEnum cardType = CardReaderTypeEnum.IC_CARD;
//
//                searchCard(cardType);
//
//            }
//        });
//
//        rl_tap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CardReaderTypeEnum cardType= CardReaderTypeEnum.RF_CARD;
//                searchCard(cardType);
//            }
//        });
//
//        rl_swipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CardReaderTypeEnum cardType= CardReaderTypeEnum.MAG_CARD;
//                searchCard(cardType);
//            }
//        });
//
    }


    private void closeDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSearchDialog != null && mSearchDialog.isShowing()) {
                    mSearchDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }





    public void showLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, log);
                Date date = new Date();
                sbLog.append(dateFormat.format(date)).append(":");
                sbLog.append(log);
                String text = mTvLog.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String[] str = text.split("\r\n");
                    for (int i = 0; i < str.length; i++) {
                        sbLog.append("\r\n");
                        sbLog.append(str[i]);
                    }
                }
                mTvLog.setText(sbLog.toString());
                sbLog.setLength(0);
            }
        });
    }


    int[] tags = {
            0x9F26,
            0x9F27,
            0x9F10,
            0x9F37,
            0x9F36,
            0x95,
            0x9A,
            0x9C,
            0x9F02,
            0x5F2A,
            0x82,
            0x9F1A,
            0x9F03,
            0x9F33,
            0x9F34,
            0x9F35,
            0x9F1E,
            0x84,
            0x9F09,
            0x9F41,
            0x9F63,
            0x5F24
    };


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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
        String myFormat = "MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDateSet.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.edtDateSet:
                new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.paynowNext:

                if (sp_selectedcustomer.getString("customernamee", "").equalsIgnoreCase("Cash Customer") || comingFrom.equalsIgnoreCase("fromSaleDetail")) {// hide reward ,  hide store credit. when customer not select or if comes from sale detail for edit.

                    if (Double.valueOf(tv_dueamount.getText().toString()) > 0) {
                        Toast.makeText(this, "For Cash Customer , You need to pay total payable amount.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // bottom next button click for final payment api.
                double doawared = 0.0, dostro = 0.0, doawared2 = 0.0, dostro2 = 0.0;
                String award = et_awardAmount.getText().toString();
                String stro = et_storeCredit.getText().toString();

                try {
                    doawared = Double.parseDouble(award);
                    doawared2 = Double.valueOf(df.format(doawared));
                } catch (NumberFormatException e) {
                }
                try {
                    dostro = Double.parseDouble(stro);
                    dostro2 = Double.parseDouble(df.format(dostro));
                } catch (Exception e) {

                }

                if (cardAmount.getText().toString().isEmpty() && cashAmount.getText().toString().isEmpty() && et_storeCredit.getText().toString().isEmpty() && et_awardAmount.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Enter amount", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else if (!et_awardAmount.getText().toString().isEmpty() && doawared2 > rewardDouble) {
                    Toast.makeText(this, "Amount Should Not Be Greater Than Total Reward Amount", Toast.LENGTH_LONG).show();
                } else if (!et_storeCredit.getText().toString().isEmpty() && dostro2 > storeDouble) {
                    Toast.makeText(this, "Amount Should Not Be Greater Than Total Store Credit", Toast.LENGTH_LONG).show();
                } else {
//
                    //here we are coming from two sides.
                    if (comingFrom.equalsIgnoreCase("fromSaleDetail") && editOrExchange.equalsIgnoreCase("forEdit")) {

                        if(AppConstant.isNetworkAvailable(ActivityPayment.this)){
                            AppConstant.hideKeyboardFrom(ActivityPayment.this);
                            updateSaleDetail(); // update sale detail api called.
                        }else{
                            AppConstant.openInternetDialog(ActivityPayment.this);
                        }

                    } else if (comingFrom.equalsIgnoreCase("partialCustomer")) {
                        if(AppConstant.isNetworkAvailable(ActivityPayment.this)){
                            completePartialSale(); // hit api for pay pending custojmer payement.
                        }else{
                            AppConstant.openInternetDialog(ActivityPayment.this);
                        }
                    } else {

                        //when payment by card then need to check all fields values for card payment like card number , cvv , card holder name etc.

                        PaymentDataSend objPayData = new PaymentDataSend();
                        objPayData = paymemtData;

                        addCartProduct();
                        payment.clear();
                        if (!(et_storeCredit.getText().toString().isEmpty())) {
                            if (sp_selectedcustomer.getString("customernamee", "").equals("Cash Customer")) {
                                objPayData.setOrder_type(orderr_type);
                                objPayData.setRes_table_id(tablee_id);
                                objPayData.setRes_waiter_id(waiterr_id);
                                objPayData.setTransaction_id(String.valueOf(transactionId));
                                Payment storeCreditObj = new Payment();
                                storeCreditObj.setAmount(et_storeCredit.getText().toString() + "");
                                storeCreditObj.setMethod("store_credit");
                                payment.add(storeCreditObj);
                                Log.e("payementObj", String.valueOf(paymentObj));
                                objPayData.setPayment(payment);
                                objPayData.setFinal_total(subtotal);
                                objPayData.setChange_return(tv_balance.getText().toString());
                                objPayData.setRepair_product_id(repair_product_id);

                                if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                    objPayData.setExchange_rate("1");
                                }else {
                                    objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                }

                            } else {

                                if (partialpayment.equals("null")) {
                                    objPayData.setOrder_type(orderr_type);
                                    objPayData.setRes_table_id(tablee_id);
                                    objPayData.setRes_waiter_id(waiterr_id);
                                    objPayData.setRepair_product_id(repair_product_id);
                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    Payment storeCreditObj = new Payment();

                                    storeCreditObj.setAmount(et_storeCredit.getText().toString() + "");
                                    storeCreditObj.setMethod("store_credit");
                                    Log.e("payementObj1", String.valueOf(paymentObj));
                                    payment.add(storeCreditObj);

                                    objPayData.setPayment(payment);
                                    objPayData.setFinal_total(subtotal);
                                    objPayData.setShow_final_total(subtotal);
                                    objPayData.setIs_quotation("0");

                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }
                                    objPayData.setChange_return(tv_balance.getText().toString());

                                } else {
                                    Payment storeCreditObj = new Payment();
                                    storeCreditObj.setAmount(et_storeCredit.getText().toString() + "");
                                    storeCreditObj.setMethod("store_credit");
                                    storeCreditObj.setCard_number("");
                                    storeCreditObj.setCard_holder_name("");
                                    storeCreditObj.setCard_type("");
                                    storeCreditObj.setCard_month("");
                                    storeCreditObj.setCard_year("");
                                    storeCreditObj.setCard_security("");
                                    payment.add(storeCreditObj);

                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    objPayData.setLocation_id(locationId);
                                    objPayData.setCurrency(currencydata.getName());
                                    objPayData.setContact_id(customerId);
                                    objPayData.setDiscount_type("");
                                    objPayData.setDiscount_amount(0.0);
                                    objPayData.setTax_rate_id("");
                                    objPayData.setTax_calculation_amount("");
                                    objPayData.setShipping_details("");
                                    objPayData.setShipping_charges(shippingcharges);
                                    objPayData.setFinal_total(df.format(totalbill));
                                    objPayData.setPaid_amount(paidamount);
                                    objPayData.setPayment(payment);
                                    objPayData.setChange_return(tv_balance.getText().toString());
                                    objPayData.setRepair_product_id(repair_product_id);

                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }
                                }
                            }
                        }

                        if (!(et_awardAmount.getText().toString().isEmpty())) {

                            if (sp_selectedcustomer.getString("customernamee", "").equals("Cash Customer")) {
                                objPayData.setOrder_type(orderr_type);
                                objPayData.setRes_table_id(tablee_id);
                                objPayData.setRes_waiter_id(waiterr_id);
                                objPayData.setTransaction_id(String.valueOf(transactionId));
                                Payment awardObj = new Payment();
                                awardObj.setAmount(et_awardAmount.getText().toString() + "");
                                awardObj.setMethod("reward");
                                payment.add(awardObj);

                                objPayData.setPayment(payment);
                                objPayData.setFinal_total(subtotal);
                                objPayData.setChange_return(tv_balance.getText().toString());
                                objPayData.setRepair_product_id(repair_product_id);

                                if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                    objPayData.setExchange_rate("1");
                                }else{
                                    objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                }

                                Log.d("msg", "onClick: " + paymemtData);

                            } else {

                                if (partialpayment.equals("null")) {
                                    objPayData.setOrder_type(orderr_type);
                                    objPayData.setRes_table_id(tablee_id);
                                    objPayData.setRes_waiter_id(waiterr_id);
                                    objPayData.setRepair_product_id(repair_product_id);
                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }
                                    Payment awardObj = new Payment();
                                    awardObj.setAmount(et_awardAmount.getText().toString() + "");
                                    awardObj.setMethod("reward");
                                    payment.add(awardObj);

                                    paymemtData.setPayment(payment);
                                    paymemtData.setFinal_total(subtotal);
                                    objPayData.setShow_final_total(subtotal);
                                    objPayData.setIs_quotation("0");
                                    paymemtData.setChange_return(tv_balance.getText().toString());

                                } else {

                                    Payment awardObj = new Payment();
                                    awardObj.setAmount(et_awardAmount.getText().toString() + "");
                                    awardObj.setMethod("reward");
                                    awardObj.setCard_number("");
                                    awardObj.setCard_holder_name("");
                                    awardObj.setCard_type("");
                                    awardObj.setCard_month("");
                                    awardObj.setCard_year("");
                                    awardObj.setCard_security("");
                                    payment.add(awardObj);

                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    objPayData.setLocation_id(locationId);
                                    objPayData.setCurrency(currencydata.getName());
                                    objPayData.setContact_id(customerId);
                                    objPayData.setDiscount_type("");
                                    objPayData.setDiscount_amount(0.0);
                                    objPayData.setTax_rate_id("");
                                    objPayData.setTax_calculation_amount("");
                                    objPayData.setShipping_details("");
                                    objPayData.setShipping_charges(shippingcharges);
                                    objPayData.setFinal_total(df.format(totalbill));
                                    objPayData.setPaid_amount(paidamount);
                                    objPayData.setPayment(payment);
                                    objPayData.setShow_final_total(subtotal);
                                    objPayData.setIs_quotation("0");
                                    objPayData.setChange_return(tv_balance.getText().toString());
                                    objPayData.setRepair_product_id(repair_product_id);

                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }

                                    Log.d("msg", "onClick: " + partialpayment);
                                }
                            }
                        }

                        if (!(cashAmount.getText().toString().isEmpty())) {
                            if (sp_selectedcustomer.getString("customernamee", "").equals("Cash Customer")) {
                                objPayData.setProducts(products);

                                objPayData.setOrder_type(orderr_type);
                                objPayData.setRes_table_id(tablee_id);
                                objPayData.setRes_waiter_id(waiterr_id);
                                objPayData.setTransaction_id(String.valueOf(transactionId));
                                Payment cashObj = new Payment();
                                cashObj.setAmount(cashAmount.getText().toString() + "");
                                cashObj.setMethod("cash");
                                payment.add(cashObj);
                                objPayData.setPayment(payment);
                                objPayData.setFinal_total(subtotal);
                                objPayData.setShow_final_total(subtotal);
                                objPayData.setContact_id(customerId);
                                objPayData.setChange_return(tv_balance.getText().toString());
                                objPayData.setRepair_product_id(repair_product_id);
                                if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                    objPayData.setExchange_rate("1");
                                }else{
                                    objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                }

                                Log.e("msg", "onClick: " + paymemtData);

                            } else {

                                if (partialpayment.equals("null")) {

                                    Log.e("CarTDATA-partial-null", String.valueOf(cartData.size()));
                                    objPayData.setOrder_type(orderr_type);
                                    objPayData.setRes_table_id(tablee_id);
                                    objPayData.setRes_waiter_id(waiterr_id);
                                    objPayData.setRepair_product_id(repair_product_id);
                                    Payment cashObj = new Payment();
                                    cashObj.setAmount(cashAmount.getText().toString() + "");
                                    cashObj.setMethod("cash");
                                    objPayData.setPaid_amount(paidamount);
                                    payment.add(cashObj);
                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    objPayData.setPayment(payment);
                                    objPayData.setFinal_total(subtotal);
                                    objPayData.setShow_final_total(subtotal);
                                    objPayData.setIs_quotation("0");
                                    objPayData.setChange_return(tv_balance.getText().toString());
                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }

                                } else {
                                    Payment cashObj = new Payment();
                                    cashObj.setAmount(cashAmount.getText().toString() + "");
                                    cashObj.setMethod("cash");
                                    cashObj.setCard_number("");
                                    cashObj.setCard_holder_name("");
                                    cashObj.setCard_type("");
                                    cashObj.setCard_month("");
                                    cashObj.setCard_year("");
                                    cashObj.setCard_security("");
                                    payment.add(cashObj);

                                    objPayData.setTransaction_id(String.valueOf(transactionId));
                                    objPayData.setLocation_id(locationId);
                                    objPayData.setCurrency(currencydata.getName());
                                    objPayData.setContact_id(customerId);

                                    objPayData.setDiscount_type("");
                                    objPayData.setDiscount_amount(0.0);
                                    objPayData.setTax_rate_id("");
                                    objPayData.setTax_calculation_amount("");
                                    objPayData.setShipping_details("");
                                    objPayData.setShipping_charges(shippingcharges);
                                    objPayData.setFinal_total(df.format(totalbill));
                                    objPayData.setPaid_amount(paidamount);
                                    objPayData.setPayment(payment);
                                    objPayData.setChange_return(tv_balance.getText().toString());
                                    objPayData.setRepair_product_id(repair_product_id);
                                    if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                        objPayData.setExchange_rate("1");
                                    }else{
                                        objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                    }
                                    // Toast.makeText(this, ""+subtotal, Toast.LENGTH_LONG).show();
                                    //partialPaymentAPI(paymemtData);
                                }
                            }
                        }

                        if (!(cardAmount.getText().toString().isEmpty())) {

                            if (sp_selectedcustomer.getString("customernamee", "").equals("Cash Customer")) {

                                Double dCardAmount = 0.0;
                                if (!cardAmount.getText().toString().isEmpty()) {
                                    dCardAmount = Double.valueOf(cardAmount.getText().toString());
                                }

                                Double dCashAmount = 0.0;
                                if (!cashAmount.getText().toString().isEmpty()) {
                                    dCashAmount = Double.valueOf(cashAmount.getText().toString());
                                }


                                if (totalbill > (dCardAmount + dCashAmount)) {
                                    Toast.makeText(this, "Amount should not be less than total bill", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    String holderName = cardHolderName.getText().toString().trim();
                                    String cardNumberEnter = cardNumber.getText().toString().trim();
                                    String cvvNumber = edtCVV.getText().toString().trim();
                                    String expYear = edtDateSet.getText().toString().trim();

                                    if (cardNumberEnter.equalsIgnoreCase("")) {
                                        Toast.makeText(this, "Enter card number", Toast.LENGTH_LONG).show();
                                    }
//                                else if (cvvNumber.equalsIgnoreCase("")) {
////                                    Toast.makeText(this, "Enter cvv number", Toast.LENGTH_LONG).show();
//                                    cvvNumber ="";
//                                }
                                    else if (expYear.equalsIgnoreCase("")) {
                                        Toast.makeText(this, "Enter expire date", Toast.LENGTH_LONG).show();
                                    } else {

                                        String[] dateSplit = expYear.split("/");

                                        paymemtData.setOrder_type(orderr_type);
                                        paymemtData.setRes_table_id(tablee_id);
                                        paymemtData.setRes_waiter_id(waiterr_id);

                                        paymemtData.setDiscount_type(discounttype);
                                        paymemtData.setDiscount_amount(discountamt);
                                        paymemtData.setTax_rate_id(taxrate);
                                        paymemtData.setTax_calculation_amount(taxcalculation);

                                        paymemtData.setContact_id(customerId);
                                        paymemtData.setLocation_id(locationId);
                                        paymemtData.setCurrency(currencydata.getName());

                                        paymemtData.setShipping_details(shippingdetail);
                                        paymemtData.setShipping_charges(shippingcharges);

                                        paymemtData.setFinal_total(subtotal);
                                        objPayData.setShow_final_total(subtotal);
                                        objPayData.setIs_quotation("0");
                                        paymemtData.setChange_return(tv_balance.getText().toString());

                                        Payment cardObj = new Payment();
                                        cardObj.setAmount(cardAmount.getText().toString() + "");
                                        cardObj.setMethod(paymentMethod);
                                        cardObj.setCard_number(cardNumberEnter);
                                        cardObj.setCard_holder_name(holderName);
                                        cardObj.setCard_type(cardType);
                                        cardObj.setCard_month(dateSplit[0]);
                                        cardObj.setCard_year(dateSplit[1]);
                                        cardObj.setCard_security(cvvNumber);
                                        payment.add(cardObj);

                                        paymemtData.setPayment(payment);
                                        paymemtData.setFinal_total(subtotal);
                                        paymemtData.setChange_return(tv_balance.getText().toString());
                                        objPayData.setRepair_product_id(repair_product_id);
//
                                        if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                            objPayData.setExchange_rate("1");
                                        }else{
                                            objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                        }
                                    }
                                }
                            }

                            else {
                                String holderName = cardHolderName.getText().toString().trim();
                                String cardNumberEnter = cardNumber.getText().toString().trim();
                                String cvvNumber = edtCVV.getText().toString().trim();
                                String expYear = edtDateSet.getText().toString().trim();

                                String[] dateSplit1 = expYear.split("/");

                                if (cardNumberEnter.equalsIgnoreCase("")) {
                                    Toast.makeText(this, "Enter card number", Toast.LENGTH_LONG).show();
                                }
//                            else if (cvvNumber.equalsIgnoreCase(""))
//                            {
//                                Toast.makeText(this, "Enter cvv number", Toast.LENGTH_LONG).show();
//                                 cvvNumber = "";
//                            }
                                else if (expYear.equalsIgnoreCase("")) {
                                    Toast.makeText(this, "Enter expire date", Toast.LENGTH_LONG).show();
                                } else {

                                    String[] dateSplit = expYear.split("/");

                                    if (partialpayment.equals("null")) {
                                        objPayData.setOrder_type(orderr_type);
                                        objPayData.setRes_table_id(tablee_id);
                                        objPayData.setRes_waiter_id(waiterr_id);

                                        objPayData.setDiscount_type(discounttype);

                                        objPayData.setDiscount_amount(discountamt);
                                        objPayData.setTax_rate_id(taxrate);
                                        objPayData.setTax_calculation_amount(taxcalculation);

                                        objPayData.setContact_id(customerId);
                                        objPayData.setLocation_id(locationId);
                                        objPayData.setCurrency(currencydata.getName());
                                        String shipingDetail = paymemtData.getShipping_details().trim();

                                        if (!shipingDetail.equalsIgnoreCase("")) {
                                            objPayData.setShipping_details(shippingdetail);
                                        }

                                        objPayData.setShipping_charges(shippingcharges);

                                        objPayData.setFinal_total(subtotal);
                                        objPayData.setShow_final_total(subtotal);
                                        objPayData.setPaid_amount(paidamount);
                                        objPayData.setIs_quotation("0");
                                        objPayData.setChange_return(tv_balance.getText().toString());
                                        objPayData.setRepair_product_id(repair_product_id);

                                        Payment cardObj = new Payment();
                                        cardObj.setAmount(cardAmount.getText().toString() + "");
                                        cardObj.setMethod(paymentMethod);
                                        cardObj.setCard_number(cardNumberEnter);
                                        cardObj.setCard_holder_name(holderName);
                                        cardObj.setCard_type(cardType);
                                        cardObj.setCard_month(dateSplit[0]);
                                        cardObj.setCard_year(dateSplit[1]);
                                        cardObj.setCard_security(cvvNumber);
                                        payment.add(cardObj);

                                        objPayData.setPayment(payment);
                                        objPayData.setFinal_total(subtotal);
                                        objPayData.setChange_return(tv_balance.getText().toString());
                                        if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                            objPayData.setExchange_rate("1");
                                        }else{
                                            objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                        }
                                        objPayData.setTransaction_id(String.valueOf(transactionId));
                                        //callPaymentAPI(paymemtData);

                                    } else {
                                        Payment cardObj = new Payment();
                                        cardObj.setAmount(cardAmount.getText().toString());
                                        cardObj.setMethod("card");
                                        cardObj.setCard_number(cardNumberEnter);
                                        cardObj.setCard_holder_name(holderName);
                                        cardObj.setCard_type(cardType);
                                        cardObj.setCard_month(dateSplit1[0]);
                                        cardObj.setCard_year(dateSplit1[1]);
                                        cardObj.setCard_security(cvvNumber);

                                        payment.add(cardObj);


                                        objPayData.setTransaction_id(String.valueOf(transactionId));
                                        objPayData.setLocation_id(locationId);
                                        objPayData.setCurrency(currencydata.getName());
                                        objPayData.setContact_id(customerId);
                                        //  paymemtData.setProducts(products);
                                        objPayData.setDiscount_type("");
                                        objPayData.setDiscount_amount(0.0);
                                        objPayData.setTax_rate_id("");
                                        objPayData.setTax_calculation_amount("");
                                        objPayData.setShipping_details("");
                                        objPayData.setShipping_charges(shippingcharges);
                                        objPayData.setFinal_total(df.format(totalbill));
                                        objPayData.setPaid_amount(paidamount);
                                        objPayData.setPayment(payment);
                                        objPayData.setChange_return(tv_balance.getText().toString());
                                        objPayData.setRepair_product_id(repair_product_id);
                                        if(currencydata.getName().equals(objBusiness.getCurrency_code())){
                                            objPayData.setExchange_rate("1");
                                        }else{
                                            objPayData.setExchange_rate(objBusiness.getExchange_rate());
                                        }

                                    }
                                }
                            }
                        }

                        if (partialpayment.equals("null")) {
                            if(AppConstant.isNetworkAvailable(ActivityPayment.this)){
                                callPaymentAPI(objPayData);
                            }else{
                                AppConstant.openInternetDialog(ActivityPayment.this);
                            }

                        } else {
                            Log.e("methodpar", objPayData.getPayment().toString());
                            if(AppConstant.isNetworkAvailable(ActivityPayment.this)){
                                partialPaymentAPI(objPayData);
                            }else{
                                AppConstant.openInternetDialog(ActivityPayment.this);
                            }

                        }

                    }//end of coming from else part.
                }

                break;
            case R.id.llDebitCard:

                paymentMethod = "card";

                llDebitCard.setBackgroundColor(getResources().getColor(R.color.orange));
                llCreditCard.setBackgroundColor(getResources().getColor(R.color.white));
                llCashPay.setBackgroundColor(getResources().getColor(R.color.white));

                imgDebitCard.setImageResource(R.drawable.creditcard_active);
                imgCreditCard.setImageResource(R.drawable.creditcard);
                imgCashPay.setImageResource(R.drawable.cash_inactive);

                txtDebitcard.setTextColor(getResources().getColor(R.color.white));
                txtCreditcard.setTextColor(getResources().getColor(R.color.colorGray));
                txtCashPay.setTextColor(getResources().getColor(R.color.colorGray));
                break;
            case R.id.llCreditCard:
                paymentMethod = "card";
                llDebitCard.setBackgroundColor(getResources().getColor(R.color.white));
                llCreditCard.setBackgroundColor(getResources().getColor(R.color.orange));
                llCashPay.setBackgroundColor(getResources().getColor(R.color.white));

                imgDebitCard.setImageResource(R.drawable.creditcard);
                imgCreditCard.setImageResource(R.drawable.creditcard_active);
                imgCashPay.setImageResource(R.drawable.cash_inactive);

                txtDebitcard.setTextColor(getResources().getColor(R.color.colorGray));
                txtCreditcard.setTextColor(getResources().getColor(R.color.white));
                txtCashPay.setTextColor(getResources().getColor(R.color.colorGray));

                break;
            case R.id.llCashPay:
                paymentMethod = "cash";
                llDebitCard.setBackgroundColor(getResources().getColor(R.color.white));
                llCreditCard.setBackgroundColor(getResources().getColor(R.color.white));
                llCashPay.setBackgroundColor(getResources().getColor(R.color.orange));

                imgDebitCard.setImageResource(R.drawable.creditcard);
                imgCreditCard.setImageResource(R.drawable.creditcard);
                imgCashPay.setImageResource(R.drawable.cash_active);

                txtDebitcard.setTextColor(getResources().getColor(R.color.colorGray));
                txtCreditcard.setTextColor(getResources().getColor(R.color.colorGray));
                txtCashPay.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.frameVisa:
                cardType = "Visa";
                frameVisa.setBackgroundColor(getResources().getColor(R.color.colorgray2));
                frameMaster.setBackgroundColor(getResources().getColor(R.color.white));
                frameCredit.setBackgroundColor(getResources().getColor(R.color.white));
                frameDebit.setBackgroundColor(getResources().getColor(R.color.white));

                break;
            case R.id.frameMaster:
                cardType = "MasterCard";
                frameMaster.setBackgroundColor(getResources().getColor(R.color.colorgray2));
                frameVisa.setBackgroundColor(getResources().getColor(R.color.white));
                frameCredit.setBackgroundColor(getResources().getColor(R.color.white));
                frameDebit.setBackgroundColor(getResources().getColor(R.color.white));

                break;
            case R.id.frameCredit:
                cardType = "Credit Card";
                frameCredit.setBackgroundColor(getResources().getColor(R.color.colorgray2));
                frameVisa.setBackgroundColor(getResources().getColor(R.color.white));
                frameMaster.setBackgroundColor(getResources().getColor(R.color.white));
                frameDebit.setBackgroundColor(getResources().getColor(R.color.white));

                break;
            case R.id.frameDebit:
                cardType = "Debit Card";
                frameDebit.setBackgroundColor(getResources().getColor(R.color.colorgray2));
                frameVisa.setBackgroundColor(getResources().getColor(R.color.white));
                frameMaster.setBackgroundColor(getResources().getColor(R.color.white));
                frameCredit.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }
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
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
    }

    private void addCartProduct() {

        for (int cartCount = 0; cartCount < cartData.size(); cartCount++) {
            ProductDataSend objCartProduct = cartData.get(cartCount);

            ProductDataSend product = new ProductDataSend();

            product.setUnit_price(objCartProduct.getUnit_price());
            if (objCartProduct.getLine_discount_type().equalsIgnoreCase("Select Discount Type"))
                product.setLine_discount_type("");
            else
                product.setLine_discount_type(objCartProduct.getLine_discount_type());

            product.setLine_discount_amount(objCartProduct.getLine_discount_amount());
            product.setItem_tax(objCartProduct.getItem_tax());
            product.setTax_id(objCartProduct.getTax_id());
            product.setProduct_id(objCartProduct.getProduct_id());
            product.setVariation_id(objCartProduct.getVariation_id());
            product.setEnable_stock(objCartProduct.getEnable_stock());
            product.setProduct_type(objCartProduct.getProduct_type());
            product.setQuantity(paymemtData.getProducts().get(cartCount).getQuantity());
            product.setUnit_price_inc_tax(objCartProduct.getUnit_price_inc_tax());

            products.add(product);
        }

        paymemtData.setProducts(products);

        paymemtData.setDiscount_type(discounttype);

        paymemtData.setDiscount_amount(discountamt);
        paymemtData.setTax_rate_id(taxrate);
        paymemtData.setTax_calculation_amount(taxcalculation);

        paymemtData.setContact_id(customerId);
        paymemtData.setLocation_id(locationId);
        paymemtData.setCurrency(currencydata.getName());
        if(productOrService==0){
            paymemtData.setProduct_type("service");
        }else{
            paymemtData.setProduct_type("product");
        }
        paymemtData.setBooking_date(booking_date);
        paymemtData.setBooking_time(booking_time);
        paymemtData.setService_staff_id(employee_id);
        paymemtData.setShipping_details(shippingdetail);
        paymemtData.setShipping_charges(shippingcharges);
        paymemtData.setFinal_total(subtotal);
        paymemtData.setChange_return(tv_balance.getText().toString());
    }

    private void callPaymentAPI(PaymentDataSend paymentData) {
        AppConstant.showProgress(this, false);
        Gson gson = new Gson();
        String json = gson.toJson(paymentData);
        Log.e("Payment Json : ", json);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call=null;
            if(editOrExchange.equalsIgnoreCase("forExchange")){
                call = apiService.sendPayment("exchangeSaleTransaction", paymentData);
            }else {
                call = apiService.sendPayment("pos", paymentData);
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            if (respo == null) {
                                Toast.makeText(getScreenContext, "Unable To Make Your Order. Please Try Again.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Payment res", "" + respo);

                            tv.setText(respo);

                            String successstatus = responseObject.optString("success");
                            String message = "";

                            message = responseObject.optString("msg");

                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true"))
                            {

                                if (ActivitySearchItemList.posSearchItemListActivity != null) {
                                    ActivitySearchItemList.posSearchItemListActivity.finish();
                                }

                                if (ActivityPosItemList.posItemListActivity != null) {
                                    ActivityPosItemList.posItemListActivity.finish();
                                }

                                if (ActivityPosTerminalDropdown.dropdownActivity != null) {

                                    ActivityPosTerminalDropdown.dropdownActivity.finish();
                                }
                                cartData.clear();
                                //clear cart product data if comes on this screen.
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                    ed_cartSave.remove("myCart");
                                    ed_cartSave.commit();
                                }


                                ed_countproduct.clear();
                                ed_countproduct.commit();

                                ed_modifiers.clear();
                                ed_modifiers.commit();

//
//
                                if(editOrExchange.equalsIgnoreCase("forExchange")){



                                }

                                else{
                                    Intent intent = new Intent(getScreenContext, ConfirmationActivity.class);
                                    intent.putExtra("payResponse", respo);
                                    intent.putExtra("cashamount", cashAmount.getText().toString().trim());
                                    intent.putExtra("cardamount", cardAmount.getText().toString().trim());
                                    intent.putExtra("dueamount", tv_dueamount.getText().toString());
                                    intent.putExtra("balanceamount", tv_balance.getText().toString());

                                    startActivity(intent);

                                }

                                AppConstant.showToast(ActivityPayment.this, "" + message);

                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);

//
//                                }
                            }
                        } else {
                            AppConstant.sendEmailNotification(ActivityPayment.this,"pos API","(ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(getScreenContext, "Could Not Make Your Order. Please Try Again.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception",e.toString());
                        e.printStackTrace();
                        Toast.makeText(getScreenContext, "" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getScreenContext, "Could Not Make Your Order. Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void updateSaleDetail() {
        JSONObject mainObject = new JSONObject();

        if (null != getIntent().getStringExtra("updateSaleDetailObject")) {
            String strMainObject = getIntent().getStringExtra("updateSaleDetailObject");

            try {
                mainObject = new JSONObject(strMainObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //set up payment array object
            JSONArray paymentArray = new JSONArray();

            try {

                if (!(et_storeCredit.getText().toString().isEmpty())) {
                    JSONObject payment = new JSONObject();
                    payment.put("payment_id", mainObject.getString("paymentId"));
                    payment.put("amount", et_storeCredit.getText().toString());
                    payment.put("method", "store_credit");

                    paymentArray.put(payment);
                }

                if (!(et_awardAmount.getText().toString().isEmpty())) {
                    JSONObject payment = new JSONObject();
                    payment.put("payment_id", mainObject.getString("paymentId"));
                    payment.put("amount", et_awardAmount.getText().toString());
                    payment.put("method", "reward");

                    paymentArray.put(payment);
                }

                if (!cardAmount.getText().toString().isEmpty()) {

                    if (cardNumber.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Please input your valid card number.", Toast.LENGTH_LONG).show();
                        return;

                    } else if (edtDateSet.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Please Select your card expiry month year.", Toast.LENGTH_LONG).show();
                        return;

                    } else if (Double.valueOf(cardAmount.getText().toString()) < totalbill) {
                        Toast.makeText(this, "By card, need to pay complete payment.", Toast.LENGTH_LONG).show();
                        return;

                    } else {
                        JSONObject payment = new JSONObject();

                        payment.put("payment_id", mainObject.getString("paymentId"));
                        payment.put("amount", df.format(Double.valueOf(cardAmount.getText().toString())));
                        payment.put("method", "card");

                        payment.put("card_number", cardNumber.getText().toString());
                        payment.put("card_type", "credit");

                        String expYear = edtDateSet.getText().toString().trim();
                        String[] dateSplit = expYear.split("/");

                        if (dateSplit.length > 0)
                            payment.put("card_month", dateSplit[0]);

                        if (dateSplit.length > 1)
                            payment.put("card_year", dateSplit[1]);

                        paymentArray.put(payment);
                    }

                }

                if (!cashAmount.getText().toString().isEmpty()) {

                    JSONObject payment = new JSONObject();
                    payment.put("payment_id", mainObject.getString("paymentId"));
                    payment.put("amount", df.format(Double.valueOf(cashAmount.getText().toString())));
                    payment.put("method", "cash");

                    paymentArray.put(payment);
//                     }
                }

                mainObject.put("payment", paymentArray);
                if(productOrService==0){
                    mainObject.put("product_type","service");
                }else{
                    mainObject.put("product_type","product");

                }

                //end of payment Array


            } catch (Exception e) {

                Toast.makeText(this, "Failed Exception : " + e.toString(), Toast.LENGTH_LONG).show();
                return;

            }

            if (null == mainObject) {
                //show toast cant update.
                Toast.makeText(ActivityPayment.this, "Incomplete Cart data Details, Please Try Again", Toast.LENGTH_LONG).show();
                return;
            }

            String strTransactionId = "";
            try {
                strTransactionId = String.valueOf(mainObject.getString("transaction_id"));
            } catch (JSONException e) {
                e.printStackTrace();
                //empty transaction id , show toast cant update.
                return;
            }

            AppConstant.showProgress(this, false);

            Retrofit retrofit = APIClient.getClientToken(this);
            if (retrofit != null) {
                Log.e("MAIN", mainObject.toString());
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
                APIInterface apiService = retrofit.create(APIInterface.class);
                Call<ResponseBody> call = apiService.updateSalesDetail("pos/" + strTransactionId, body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        AppConstant.hideProgress();
                        if (response.body() != null) {
                            AppConstant.hideProgress();

                            try {
                                if (response.body() != null) {
                                    AppConstant.hideProgress();
                                    String respo = response.body().string();

                                    Log.e("UpdateAPIResponse", respo);
                                    JSONObject responseObject = new JSONObject(respo);


                                    String successstatus = responseObject.optString("success");
                                    String msg = responseObject.optString("msg");
                                    //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                                    if (successstatus.equalsIgnoreCase("true")) {

                                        if (ActivitySearchItemList.posSearchItemListActivity != null) {
                                            ActivitySearchItemList.posSearchItemListActivity.finish();
                                        }

                                        if (ActivityPosItemList.posItemListActivity != null) {
                                            ActivityPosItemList.posItemListActivity.finish();
                                        }

                                        if (ActivityPosTerminalDropdown.dropdownActivity != null) {

                                            ActivityPosTerminalDropdown.dropdownActivity.finish();
                                        }

                                        if(HomeActivity.homeActivity !=null){
                                            HomeActivity.homeActivity.finish();
                                        }

                                        Toast.makeText(ActivityPayment.this, "" + msg, Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(ActivityPayment.this, HomeActivity.class);

                                        startActivity(i);
                                        finish();
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                    } else {
                                        Toast.makeText(ActivityPayment.this, "" + msg, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    AppConstant.hideProgress();
                                    AppConstant.sendEmailNotification(ActivityPayment.this,"pos API For Update","(ActivityPayment Screen","Web API Error : API Response Is Null");
                                    Toast.makeText(ActivityPayment.this, "Could Not Update Sale, Please Try Again", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                AppConstant.hideProgress();
                                Log.e("Exception", e.toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppConstant.hideProgress();
                        Toast.makeText(ActivityPayment.this, "Could Not Update Sale, Please Try Again", Toast.LENGTH_LONG).show();
                    }

                });
            }
        }
    }


    public void completePartialSale() {
        JSONObject mainObject = new JSONObject();


        Double dFinalTOtal = 0.0;
        //set up payment array object
        JSONArray paymentArray = new JSONArray();

        try {

            if (!(et_storeCredit.getText().toString().isEmpty())) {
                JSONObject payment = new JSONObject();
                payment.put("amount", et_storeCredit.getText().toString());
                payment.put("method", "store_credit");
                paymentArray.put(payment);
                dFinalTOtal = dFinalTOtal + Double.valueOf(et_storeCredit.getText().toString());

            }

            if (!(et_awardAmount.getText().toString().isEmpty())) {
                JSONObject payment = new JSONObject();
                payment.put("amount", et_awardAmount.getText().toString());
                payment.put("method", "reward");
                paymentArray.put(payment);
                dFinalTOtal = dFinalTOtal + Double.valueOf(et_awardAmount.getText().toString());
            }

            if (!cardAmount.getText().toString().isEmpty()) {
                if (cardNumber.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please input your valid card number.", Toast.LENGTH_LONG).show();
                    return;

                } else if (edtDateSet.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please Select your card expiry month year.", Toast.LENGTH_LONG).show();
                    return;

                } else if (Double.valueOf(cardAmount.getText().toString()) < totalbill) {
                    Toast.makeText(this, "By card, need to pay complete payment.", Toast.LENGTH_LONG).show();
                    return;

                } else {
                    JSONObject payment = new JSONObject();


                    payment.put("amount", df.format(Double.valueOf(cardAmount.getText().toString())));
                    payment.put("method", "card");

                    payment.put("card_number", cardNumber.getText().toString());
                    payment.put("card_type", "credit");

                    String expYear = edtDateSet.getText().toString().trim();
                    String[] dateSplit = expYear.split("/");

                    if (dateSplit.length > 0)
                        payment.put("card_month", dateSplit[0]);

                    if (dateSplit.length > 1)
                        payment.put("card_year", dateSplit[1]);
                    paymentArray.put(payment);
                    dFinalTOtal = dFinalTOtal + Double.valueOf(cardAmount.getText().toString());
                }

            }

            if (!cashAmount.getText().toString().isEmpty()) {
                JSONObject payment = new JSONObject();
                payment.put("amount", df.format(Double.valueOf(cashAmount.getText().toString())));
                payment.put("method", "cash");

                paymentArray.put(payment);
                dFinalTOtal = dFinalTOtal + Double.valueOf(cashAmount.getText().toString());
            }

            mainObject.put("payment", paymentArray);
            mainObject.put("contact_id", customerId);
            mainObject.put("location_id", locationId);
            mainObject.put("transaction_id", transactionId);
            mainObject.put("change_return", tv_balance.getText().toString());

            if(currencydata.getName().equals(objBusiness.getCurrency_code())){

                mainObject.put("exchange_rate","1");
            }else{
                mainObject.put("exchange_rate",objBusiness.getExchange_rate());
            }
            mainObject.put("final_total", String.valueOf(dFinalTOtal));
            //end of payment Array

        } catch (Exception e) {

            Toast.makeText(this, "Failed Exception : " + e.toString(), Toast.LENGTH_LONG).show();
            return;

        }

        if (null == mainObject) {
            //show toast cant update.
            Toast.makeText(ActivityPayment.this, "Incomplete Partial Sale Details, Please Try Again.", Toast.LENGTH_LONG).show();
            return;
        }

        AppConstant.showProgress(this, false);

        Retrofit retrofit = APIClient.getClientToken(this);

        if (retrofit != null) {
            Log.e("partial sale update : ", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.completePartialSaleRequest("partialSaleTransaction", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    AppConstant.hideProgress();

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("partial response:", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");

                            if(ActivityPosTerminalDropdown.dropdownActivity !=null){
                                ActivityPosTerminalDropdown.dropdownActivity.finish();
                            }
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                if(HomeActivity.homeActivity !=null){
                                    HomeActivity.homeActivity.finish();
                                }
                                Toast.makeText(ActivityPayment.this, "" + msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ActivityPayment.this, HomeActivity.class);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(ActivityPayment.this, "" + msg, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ActivityPayment.this,"partialSaleTransaction API","ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(ActivityPayment.this, "Could Not Update Sale, Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ActivityPayment.this, "Could Not Update Sale, Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }


    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

    }

    private void partialPaymentAPI(PaymentDataSend paymemtData) {

        // partialclick="true";
        AppConstant.showProgress(this, false);

        Log.e(" Partial Payment Data", "" + paymemtData.toString());
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.sendPartialPayment("partialSaleTransaction", paymemtData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {


                    try {
                        if (response.body() != null) {
                            String respo1 = response.body().string();
                            // Toast.makeText( this, "Responsee1" + respo1, Toast.LENGTH_LONG).show();
                            JSONObject responseObject = new JSONObject(respo1);
                            String successstatus = responseObject.optString("success");
                            String message = responseObject.optString("msg");
                            Log.e("Partial res", "" + respo1);

                            tv.setText(respo1);

                            if (ActivitySearchItemList.posSearchItemListActivity != null) {
                                ActivitySearchItemList.posSearchItemListActivity.finish();
                            }

                            if (ActivityPosItemList.posItemListActivity != null) {
                                ActivityPosItemList.posItemListActivity.finish();
                            }

                            if (ActivityPosTerminalDropdown.dropdownActivity != null) {
                                ActivityPosTerminalDropdown.dropdownActivity.finish();
                            }


                            cartData.clear();

                            //clear cart product data if comes on this screen.
                            if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                ed_cartSave.remove("myCart");
                                ed_cartSave.commit();
                            }

                            ed_countproduct.clear();
                            ed_countproduct.commit();

                            ed_modifiers.clear();
                            ed_modifiers.commit();


                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true")) {

                                Toast.makeText(getScreenContext, "" + message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getScreenContext, ConfirmationActivity.class);
                                intent.putExtra("payResponse", respo1);
                                intent.putExtra("cashamount", cashAmount.getText().toString().trim());
                                intent.putExtra("cardamount", cardAmount.getText().toString().trim());
                                intent.putExtra("dueamount", tv_dueamount.getText().toString());
                                intent.putExtra("balanceamount", tv_balance.getText().toString());
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getScreenContext, "" + message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getScreenContext, ActivityPaymentFalse.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter, R.anim.exit);

                            }

                            finish();
                            AppConstant.hideProgress();

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ActivityPayment.this,"partialSaleTransaction API","(ActivityPayment Screen","Web API Error : API Response Is Null");
                            Toast.makeText(getScreenContext, "Failed Order : Please Try Again.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getScreenContext, "" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getScreenContext, "Failed Order : " + t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}