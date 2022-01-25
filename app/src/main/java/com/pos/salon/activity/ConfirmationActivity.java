package com.pos.salon.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pos.salon.R;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.PrinterController;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.cashData.LineData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConfirmationActivity extends AppCompatActivity {

    Spinner spinnerPrint;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    String payResponse="";
    TextView txtOrderId, txtCustomerName;
    LinearLayout llNo;
    String transaction_id = "", orderId = "", invoiceId = "", publisherName = "", date = "", cardHolderName = "", cardNo = "", cardType = "", auth = "", totalPayment = "", customerName = "", currencyCode = "";
    String dateTime = "";
    ArrayList<LineData> lineData = new ArrayList<LineData>();
    ArrayList paymentLinesList = new ArrayList();
    String cashamount="", business_name="",final_total="", orderSubTotal="", balanceamount="", totaldue="", cardamount="", dueamount="", rewardearned="", totalreward="", storecredit="", mobile="", address="", locationname="", discountLabel="", taxLabel="", shippingLabel="", totalCardPayment="";
    double discountOrder = 0, taxOrder = 0, shippingOrder = 0;
    public String businesslogo = "";
    TextView responsepa;
    SharedPreferences sp_cartSave,sharedPreferences; // for fetch data from preference
    SharedPreferences.Editor ed_cartSave; //for save data in preference.
    JSONObject innerObj;
    //String date2="";
    ArrayList<String> departmentList = new ArrayList<String>();
    private PrinterController mPrinterController = null;
    private int flag,Language=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        responsepa = findViewById(R.id.responsepa);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            departmentList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }


        payResponse = getIntent().getStringExtra("payResponse");
        responsepa.setText(payResponse);

        dueamount = getIntent().getStringExtra("dueamount");
        cardamount = getIntent().getStringExtra("cardamount");
        cashamount = getIntent().getStringExtra("cashamount");


        if (cardamount.equals("")) {
            cardamount = "0";
        }

        if (cashamount.equals("")) {
            cashamount = "0";
        }


        balanceamount = getIntent().getStringExtra("balanceamount");

        Log.e("msg", "PayResponse: " + payResponse);

        try {

            JSONObject jsonObj = new JSONObject(payResponse);

            transaction_id = String.valueOf(jsonObj.optInt("transaction_id"));
            innerObj = jsonObj.optJSONObject("receipt");
            businesslogo = innerObj.optString("logo");
            locationname = innerObj.optString("location_name");
            totaldue = innerObj.optString("total_due");
            mobile = innerObj.optString("contact");
            address = innerObj.optString("address");
            business_name = innerObj.optString("business_name");
            orderId = innerObj.optString("bank_order_id");
            storecredit = innerObj.optString("total_sc");
            invoiceId = innerObj.optString("invoice_no");
            publisherName = innerObj.optString("publisher_name");
            date = innerObj.optString("auth_date");
            //time = innerObj.optString("bank_order_id");
            cardHolderName = innerObj.optString("card_holder_name");
            discountOrder = innerObj.optDouble("discount");
            discountLabel = innerObj.optString("discount_label");

            taxLabel = innerObj.optString("line_tax_label");
            taxOrder = innerObj.optDouble("tax");

            shippingLabel = innerObj.getString("shipping_charges_label");
            shippingOrder = innerObj.getDouble("shipping_charges");

            cardNo = innerObj.optString("card_number");
            totalCardPayment = innerObj.optString("total_card_payment");
            rewardearned = innerObj.optString("reward_earned");
            totalreward = innerObj.optString("total_reward");
            cardType = innerObj.optString("card_type");
            auth = innerObj.optString("auth_code");
            orderSubTotal = innerObj.optString("subtotal");
            final_total = innerObj.optString("final_total");
            totalPayment = innerObj.optString("total_paid_amount");
            customerName = innerObj.optString("customer_name");
            currencyCode = innerObj.optString("currency_code");
            dateTime = innerObj.optString("invoice_date");
//            transaction_id = innerObj.optString("transaction_id");
            //Log.d("msg", "PayResponse1: "+cardNo);
            JSONArray lineArray = innerObj.optJSONArray("lines");


            JSONArray payment_linesList = innerObj.getJSONArray("payments");

            for (int j = 0; j < payment_linesList.length(); j++) {

                paymentLinesList.add(payment_linesList.getJSONObject(j));

            }


            for (int i = 0; i < lineArray.length(); i++) {
                LineData dataObj = new LineData();

                JSONObject lineJsonObj = lineArray.optJSONObject(i);

                dataObj.setName(lineJsonObj.optString("name"));
                dataObj.setVariation(lineJsonObj.optString("variation"));
                dataObj.setQuantity(lineJsonObj.optString("quantity"));
                dataObj.setUnits(lineJsonObj.optString("units"));
                dataObj.setUnit_price(lineJsonObj.optString("unit_price"));
                dataObj.setTax(lineJsonObj.optString("tax"));
                dataObj.setTax_unformatted(lineJsonObj.optString("tax_unformatted"));
                dataObj.setTax_name(lineJsonObj.optString("tax_name"));
                dataObj.setTax_percent(lineJsonObj.optString("tax_percent"));
                dataObj.setUnit_price_inc_tax(lineJsonObj.optString("unit_price_inc_tax"));
                dataObj.setUnit_price_exc_tax(lineJsonObj.optString("unit_price_exc_tax"));
                dataObj.setPrice_exc_tax(lineJsonObj.optString("price_exc_tax"));
                dataObj.setUnit_price_before_discount(lineJsonObj.optString("unit_price_before_discount"));
                dataObj.setLine_total(lineJsonObj.optString("line_total"));
                dataObj.setLine_discount(lineJsonObj.optString("line_discount"));
                dataObj.setSub_sku(lineJsonObj.optString("sub_sku"));
                dataObj.setCat_code(lineJsonObj.optString("cat_code"));

                // Toast.makeText(this, "" + lineJsonObj.optString("quantity"), Toast.LENGTH_LONG).show();
                lineData.add(dataObj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        setWidget();
        setBackNavgation();


    }



    private void setWidget() {

        String[] printerType;
        if (!cardNo.equalsIgnoreCase("")) {
            printerType = getResources().getStringArray(R.array.print_array);
        } else {
            printerType = getResources().getStringArray(R.array.print_array_cash);
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtOrderId = (TextView) findViewById(R.id.txtOrderId);
        txtOrderId.setText("Order #" + invoiceId);

        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtCustomerName.setText(customerName);
//        storeAmount.setText(storecredit);


        llNo = (LinearLayout) findViewById(R.id.llNo);
        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HomeActivity.homeActivity !=null){
                    HomeActivity.homeActivity.finish();
                }
                Intent i = new Intent(ConfirmationActivity.this, HomeActivity.class);

                startActivity((i));
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        spinnerPrint = (Spinner) findViewById(R.id.spinnerPrint);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, printerType);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPrint.setAdapter(spinnerAdapter);


        spinnerPrint.setOnItemSelectedListener(listener);
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
                    if(HomeActivity.homeActivity !=null){
                        HomeActivity.homeActivity.finish();
                    }
                    Intent i = new Intent(ConfirmationActivity.this, HomeActivity.class);

                    startActivity((i));
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(HomeActivity.homeActivity !=null){
            HomeActivity.homeActivity.finish();
        }
        Intent i = new Intent(ConfirmationActivity.this, HomeActivity.class);

        startActivity((i));
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

            String select = String.valueOf(parent.getItemAtPosition(position));

            progressDialog = new ProgressDialog(ConfirmationActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");


            if (select.equals("Print")) {
                progressDialog.show();
//                printProductCopy();
                openPrinterDialog();

                progressDialog.dismiss();


            } else if (select.equals("Print Merchant Receipt")) {
                if (!cardNo.equalsIgnoreCase(""))
                {

//
                } else {
                    //show that card payment not done.
                }

            } else if (select.equals("Customer CC Receipt")) {

                if (!cardNo.equalsIgnoreCase(""))
                {
                }
            } else if (select.equals("Email Copy")) {

                sendEmailCopy();


            } else {
                //show that card payment not done.
            }
        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void takeScreenShot() {
        View u = this.findViewById(R.id.ll_dummy);

        LinearLayout z = this.findViewById(R.id.ll_dummy);
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(u, totalHeight, totalWidth);
        String extr = Environment.getExternalStorageDirectory() + "/SHOPPLUS/";
        File path = new File(extr);
        if (!path.exists()) {
            path.mkdirs();
        }
        //Save bitmap
        String fileName = "image.png";
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(this.getContentResolver(), b, "Screen", "screen");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintHelper photoPrinter = new PrintHelper(ConfirmationActivity.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        // Bitmap bitmap = ((BitmapDrawable) iv_print.getDrawable()).getBitmap();
        photoPrinter.printBitmap("DemoPrint", b);
    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public void openPrinterDialog() {

        final Dialog fromPrintDialog = new Dialog(ConfirmationActivity.this);
        fromPrintDialog.setContentView(R.layout.print_from_dilaog);
        fromPrintDialog.setCancelable(true);

        Window window = fromPrintDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
//                window.setGravity(Gravity.CENTER);

        TextView txt_print_pos = fromPrintDialog.findViewById(R.id.txt_print_pos);
        TextView txt_print_bluetooth = fromPrintDialog.findViewById(R.id.txt_print_bluetooth);
        TextView txt_cancel = fromPrintDialog.findViewById(R.id.txt_cancel);

        fromPrintDialog.show();

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromPrintDialog.dismiss();
            }
        });

        txt_print_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromPrintDialog.dismiss();
                bluetoothPrint("forBluetooth");

            }
        });

        txt_print_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == mPrinterController) {
                    mPrinterController = PrinterController.getInstance(ConfirmationActivity.this);
                }
                BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                try {
                    flag = mPrinterController.PrinterController_Open();
                }catch (Exception e){
                    Log.e("Exception",e.toString());
                }

                if (flag == 0) {

                    fromPrintDialog.dismiss();
                    Toast.makeText(ConfirmationActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();

                    mPrinterController.PrinterController_PrinterLanguage(Language);
                    mPrinterController.PrinterController_Take_The_Paper(1);
                    bluetoothPrint("forNewDevice");
                    mPrinterController.PrinterController_Take_The_Paper(2);

                } else {
                    Toast.makeText(ConfirmationActivity.this, "Connect Failure", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
            // Toast.makeText(this, ""+permissions[0], Toast.LENGTH_LONG).show();
            //resume tasks needing this permission
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        spinnerPrint.setSelection(0);

        //clear cart product data if comes on this screen.
        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
            ed_cartSave.remove("myCart");
            ed_cartSave.commit();
        }
    }


    //print smart pos machine product copy for merchant or customer.


    public void sendEmailCopy() {


        AppConstant.showProgress(ConfirmationActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(ConfirmationActivity.this);

        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("transaction_id", transaction_id);
                jsonObject.put("template_for", "new_sale");
                jsonObject.put("notification_type", "email_only");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Main", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.sendEmailCopy("notification/send", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Email Response", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {

                                Toast.makeText(ConfirmationActivity.this, "Notification Sent Successfully", Toast.LENGTH_LONG).show();

                            }

                        } else {
                            AppConstant.sendEmailNotification(ConfirmationActivity.this, "notification/send API", "(ConfirmationActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(ConfirmationActivity.this, "Could Not Send Email. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ConfirmationActivity.this, "Could Not Send Email. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected void bluetoothPrint(String forDevice) {

        String BILL = "";

        BILL = "\n\n";

        BILL = business_name+"\n\n";
        if (!address.isEmpty()) {

            BILL =BILL + locationname + " " + address;

            if (!mobile.isEmpty()) {
                BILL = BILL + "\n" + mobile;
            }
        }

        BILL = BILL + "\n\n" + "Invoice No  :  " + invoiceId
                + "\n" + "Customer  :  " + customerName
                + "\n" + "Date  : " + dateTime + "\n\n "

                + "--------------------------------";

        BILL = BILL + "            Product           \n"
                + "--------------------------------";


        //set currency type from receipt object.
        String strCurrencyType = "";

        //set values from receipt object response.
        if (innerObj != null) {
            if (innerObj.has("currency") && !innerObj.isNull("currency")) {
                try {
                    JSONObject objCurrency = innerObj.getJSONObject("currency");

                    strCurrencyType = objCurrency.getString("symbol");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        //end.

        for (int i = 0; i < lineData.size(); i++) {
            LineData objLine = lineData.get(i);

            double dSubTotal = 0.0;

            if (departmentList.contains("8")) {
                String strVariationName = "";
                objLine.setVariation(strVariationName);
// ProductDetailAdapter.sellItem = (JSONObject) objLine;
                BILL = BILL + "\n\n" + objLine.getName() + " - " + strVariationName + " , " + objLine.getSub_sku();
            } else //if(APIClient.getMyDepartmentId(this) == AppConstant.MyDepartmentId.FASHION || )
            {
// ProductDetailAdapter.sellItem = (JSONObject) objLine;
                BILL = BILL + "\n\n" + objLine.getName() + " " + objLine.getVariation() + " , " + objLine.getSub_sku();
            }

            BILL = BILL + "\n\n" + "Quantity  :  " + objLine.getQuantity() + " " + objLine.getUnits();

            BILL = BILL + "\n\n" + "Unit Price  :  " + objLine.getUnit_price_before_discount();

            try {

                //discount show if come
                String strProductDisountAmount = objLine.getLine_discount();

//                Float fDiscountAMount = Float.valueOf(strProductDisountAmount);

//                if (fDiscountAMount > 0) {
                BILL = BILL + "\n\n" + "Discount  :  (-) " + strCurrencyType + " " + strProductDisountAmount;
//                }

                //tax show if comes
                String strProductTaxAmount = objLine.getTax();
                Float fTaxAMount = Float.valueOf(strProductTaxAmount);

                if (fTaxAMount > 0) {
                    BILL = BILL + "\n\n" + "Tax  :  (+) " + strCurrencyType + " " + strProductTaxAmount;
                }

            } catch (Exception e) {
                Log.e("Exception : ", e.toString());
            }

//                quantity = objLine.getQuantity();
//                price_inc_tax = ProductDetailAdapter.sellItem.getString("unit_price_inc_tax");
//
//                String quantyty1 = String.valueOf(quantity);
//                subtotal = String.valueOf(Float.valueOf(quantyty1) * (Float.valueOf(price_inc_tax) - fDiscountAMount));

            if (!objLine.getLine_total().isEmpty()) {
                dSubTotal = dSubTotal + Double.valueOf(objLine.getLine_total());
            }
            BILL = BILL + "\n\n" + "Subtotal  :   " + String.format("%.2f", dSubTotal);

            if (i != lineData.size() - 1) {
                BILL = BILL + "\n\n" + "------------------------------";
            }
        }
        BILL = BILL + "\n\n" + "------------------------------";

        BILL = BILL + "\n\n" + "Subtotal  :  " + strCurrencyType + " " + orderSubTotal.trim();

        if (discountOrder > 0) {
            BILL = BILL + "\n\n" + discountLabel + " :  (-) " + strCurrencyType + " " + discountOrder;
        }


        if (taxOrder > 0) {
            BILL = BILL + "\n\n" + taxLabel + " :  (+) " + strCurrencyType + " " + taxOrder;
        }

        if (shippingOrder > 0) {
            BILL = BILL + "\n\n" + shippingLabel + " :  (+) " + strCurrencyType + " " + shippingOrder;
        }


        BILL = BILL + "\n\n" + "-------------------------------";
        BILL = BILL + "\n\n" + "Grand Total  :  " + strCurrencyType + " " + final_total;
        BILL = BILL + "\n\n" + "-------------------------------";
        BILL = BILL + "\n\n" + "Paid By   :";


        try {

            for (int i = 0; i < paymentLinesList.size(); i++) {

                JSONObject paymentLines = (JSONObject) paymentLinesList.get(i);

                if (paymentLines.getString("method") != null && paymentLines.getString("amount") != null) {

                    String strMethod = paymentLines.getString("method");
                    String strMethodCapital = strMethod.substring(0, 1).toUpperCase() + strMethod.substring(1);
                    BILL = BILL + "\n\n" + strMethodCapital + " ( " + paymentLines.getString("date") + " )  :  " + strCurrencyType + " " + paymentLines.getString("amount");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        BILL = BILL + "\n\n" + "Total Paid   :   $ " + totalPayment + " (" + currencyCode + ")";

        if (!balanceamount.isEmpty()) {

            BILL = BILL + "\n\n" + "Cash(Change Return) :  (-) " + strCurrencyType + " " + balanceamount;
        }


        if (innerObj != null) {
            try {

                if (innerObj.getString("total_due_label") != null) {
                    BILL = BILL + "\n\n" + innerObj.getString("total_due_label") + "   :  " + strCurrencyType + " " + String.format("%.2f", innerObj.getDouble("total_due_amount"));

                } else {
                    BILL = BILL + "\n\n" + innerObj.getString("total_due_label") + "   :  " + strCurrencyType + " 0 ";
                }


                if (innerObj.getString("reward_earned") != null) {
                    String strRewardEarned = innerObj.getString("reward_earned");
                    BILL = BILL + "\n\n" + "Reward Earned   :  " + strCurrencyType + " " + strRewardEarned.trim();

                } else {
                    BILL = BILL + "\n\n" + "Reward Earned   :  " + strCurrencyType + " 0";
                }


                if (innerObj.getString("total_reward") != null) {
                    String strTotalReward = innerObj.getString("total_reward");
                    BILL = BILL + "\n\n" + "Total Reward   :  " + strCurrencyType + " " + strTotalReward.trim();

                } else {
                    BILL = BILL + "\n\n" + "Total Reward   :  " + strCurrencyType + " 0";
                }


                if (innerObj.getString("total_sc") != null) {
                    String strStoreCredit = innerObj.getString("total_sc");
                    BILL = BILL + "\n\n" + "Store Credit   :  " + strCurrencyType + " " + strStoreCredit.trim();
                } else {
                    BILL = BILL + "\n\n" + "Store Credit   :  " + strCurrencyType + " 0";
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        BILL = BILL + "\n\n\n";
        BILL = BILL + "     ***** THANK YOU *****";
        BILL = BILL + "\n\n\n\n\n ";

        Log.e("bill", "" + BILL.toString());

        if(forDevice.equalsIgnoreCase("forBluetooth")){

            Intent intent = new Intent(ConfirmationActivity.this, BluetoothPrintActivity.class);
            intent.putExtra("bill_receipt", BILL);

            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else {
            if (Language == 2) {
                try {
                    mPrinterController.PrinterController_Print(BILL.getBytes("GB2312"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                mPrinterController.PrinterController_Print(BILL.getBytes());

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(invoiceId, BarcodeFormat.CODE_128, 250, 70);
                    Bitmap bitmap = Bitmap.createBitmap(250, 70, Bitmap.Config.RGB_565);
                    for (int i = 0; i < 250; i++) {
                        for (int j = 0; j < 70; j++) {
                            bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                        }
                    }
//                    imageView.setImageBitmap(bitmap);
                    mPrinterController.PrinterController_Bitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    protected void onDestroy() {
        super.onDestroy();
        if (null != mPrinterController)
            mPrinterController.PrinterController_Close();
    }
}
