package com.pos.salon.activity.ActivityPurchases;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.AddPurchaseActivity;
import com.pos.salon.activity.BluetoothPrintActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.PrinterController;
import com.pos.salon.adapter.PaymentLinesAdapter;
import com.pos.salon.adapter.PurchaseSectionAdapters.PurchaseProductDetailAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PurchaseDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    int purchase_id = 0;
    RecyclerView recycler_PaymentLines, recyc_ProductDetail;
    private JSONObject getPurchaseDetailResponse;
    private PaymentLinesAdapter paymentLinesAdapter;
    private final ArrayList paymentLinesList = new ArrayList();
    private final ArrayList arrProductList = new ArrayList();
    PurchaseProductDetailAdapter purchaseProductDetailAdapter;
    TextView txt_additional_notes, txt_shipping_detail, txt_DetailPurchase, txt_SupplierName, txt_DetailAddress, txt_Mobile, txt_paymemt_status, txt_purchaseStatus;
    TextView txt_trans_Date, txt_SupplierBusinessName, txt_net_total, txt_Purchasetax, txt_discount, txt_shipping, txt_finalTotal;
    ImageView open_action;
    @SuppressLint("StaticFieldLeak")
    public static Activity purchaseDetailActivity;
    String strAddress = "", strMobile = "", subtotal = "", refrenceNo = "", supplierName = "";
    private PrinterController mPrinterController = null;
    private int flag;
    private final int Language=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_detail);

        purchaseDetailActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        purchase_id = getIntent().getIntExtra("purchase_id", 0);

        setBackNavgation();
        fetchIds();
    }

    public void fetchIds() {

        recycler_PaymentLines = findViewById(R.id.recycler_PaymentLines);
        recyc_ProductDetail = findViewById(R.id.recyc_ProductDetail);
        txt_additional_notes = findViewById(R.id.txt_additional_notes);
        txt_shipping_detail = findViewById(R.id.txt_shipping_detail);
        txt_DetailPurchase = findViewById(R.id.txt_DetailPurchase);
        txt_SupplierName = findViewById(R.id.txt_SupplierName);
        txt_DetailAddress = findViewById(R.id.txt_DetailAddress);
        txt_Mobile = findViewById(R.id.txt_Mobile);
        txt_paymemt_status = findViewById(R.id.txt_paymemt_status);
        txt_purchaseStatus = findViewById(R.id.txt_purchaseStatus);
        txt_trans_Date = findViewById(R.id.txt_trans_Date);
        txt_SupplierBusinessName = findViewById(R.id.txt_SupplierBusinessName);
        txt_net_total = findViewById(R.id.txt_net_total);
        txt_Purchasetax = findViewById(R.id.txt_Purchasetax);
        txt_discount = findViewById(R.id.txt_discount);
        txt_shipping = findViewById(R.id.txt_shipping);
        txt_finalTotal = findViewById(R.id.txt_finalTotal);
        open_action = findViewById(R.id.open_action);

        LinearLayoutManager layoutManager = new LinearLayoutManager(PurchaseDetailActivity.this);
        recyc_ProductDetail.setLayoutManager(layoutManager);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PurchaseDetailActivity.this);
        recycler_PaymentLines.setLayoutManager(mLayoutManager);


        listeners();

    }

    public void listeners() {

        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        salesDetail();
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
        boolean isPurchaseUpdate = false;
        boolean isPurchaseDelete = false;

        if (permissionsDataList.isEmpty()) {

            isPurchaseUpdate = true;
            isPurchaseDelete = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {

                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("purchase.update")) {
                    isPurchaseUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("purchase.delete")) {
                    isPurchaseDelete = true;
                }
            }
        }
        PopupMenu popup = new PopupMenu(PurchaseDetailActivity.this, view);
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
        popup.getMenuInflater().inflate(R.menu.purchase_menu_items, popup.getMenu());

        if (isPurchaseUpdate) {
            popup.getMenu().findItem(R.id.purchase_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.purchase_edit).setVisible(false);
        }
        if (isPurchaseDelete) {
            popup.getMenu().findItem(R.id.purchase_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.purchase_edit).setVisible(false);
        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.purchase_edit:

                        Intent intent = new Intent(PurchaseDetailActivity.this, AddPurchaseActivity.class);
                        intent.putExtra("comingFrom", "fromDetail");
                        intent.putExtra("purchase_id", purchase_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                           boolean bCheck = goToCartScreen("forEdit");// here next go to positem list scree
                        break;
                    case R.id.delete_purchase:

                        deletePurchase();

                        break;

                    case R.id.purchase_Print:

                        openPrinterDialog("Purchase");

                        break;

                }
                return true;
            }
        });
        popup.show();

    }

    public void openPrinterDialog(final String strFor) {

        final Dialog fromPrintDialog = new Dialog(PurchaseDetailActivity.this);
        fromPrintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        fromPrintDialog.setContentView(R.layout.print_from_dilaog);
        fromPrintDialog.setCancelable(true);

        Window window = fromPrintDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
//                window.setGravity(Gravity.CENTER);

        TextView txt_print_pos = fromPrintDialog.findViewById(R.id.txt_print_pos);
        TextView txt_print_bluetooth = fromPrintDialog.findViewById(R.id.txt_print_bluetooth);
        TextView txt_wifi_printer = fromPrintDialog.findViewById(R.id.txt_wifi_printer);
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
                bluetoothPrint(strFor,"forBluetooth");

            }
        });

        txt_print_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == mPrinterController) {
                    mPrinterController = PrinterController.getInstance(PurchaseDetailActivity.this);
                }
                BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//                Toast.makeText(PurchaseDetailActivity.this, "Battery " + battery, Toast.LENGTH_SHORT).show();

                try {
                    flag = mPrinterController.PrinterController_Open();
                }catch (Exception e){
                    Log.e("Exception",e.toString());
                }

                if (flag == 0) {
//            settrue();
//                            if (battery >= 30) {

                    fromPrintDialog.dismiss();
                    Toast.makeText(PurchaseDetailActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();

                    mPrinterController.PrinterController_PrinterLanguage(Language);
                    mPrinterController.PrinterController_Take_The_Paper(1);
                    bluetoothPrint(strFor,"forNewDevice");
                    mPrinterController.PrinterController_Take_The_Paper(2);
//                if (mCut) {
//                    cut();
//                }
//                                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
//                            }
                } else {
                    Toast.makeText(PurchaseDetailActivity.this, "Connect Failure", Toast.LENGTH_SHORT).show();
                }

//                final Dialog fromPrintDialog = new Dialog(PurchaseDetailActivity.this);
//                fromPrintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                fromPrintDialog.setContentView(R.layout.select_printer_dialog);
//                fromPrintDialog.setCancelable(true);
//
//                Window window = fromPrintDialog.getWindow();
//                window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
////                window.setGravity(Gravity.CENTER);
//
//                TextView txt_zcs_printer = fromPrintDialog.findViewById(R.id.txt_zcs_printer);
//                TextView txt_pt_printer = fromPrintDialog.findViewById(R.id.txt_pt_printer);
//                TextView txt_printer_jeoPower = fromPrintDialog.findViewById(R.id.txt_printer_jeoPower);
////                TextView txt_wifi_printer = fromPrintDialog.findViewById(R.id.txt_wifi_printer);
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
//                txt_zcs_printer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fromPrintDialog.dismiss();
//                        printProductCopy(strFor);
//                    }
//                });
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (null == mPrinterController) {
//                            mPrinterController = PrinterController.getInstance(PurchaseDetailActivity.this);
//                        }
//                        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
//                        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//
////                        Toast.makeText(PurchaseDetailActivity.this, "Battery " + battery, Toast.LENGTH_SHORT).show();
//
//                        try {
//                            flag = mPrinterController.PrinterController_Open();
//                        }catch (Exception e){
//                            Log.e("Exception",e.toString());
//                        }
//
//                        if (flag == 0) {
////            settrue();
////                            if (battery >= 30) {
//
//                                fromPrintDialog.dismiss();
//                                Toast.makeText(PurchaseDetailActivity.this, "Connect Success", Toast.LENGTH_SHORT).show();
//
//                                mPrinterController.PrinterController_PrinterLanguage(Language);
//                                mPrinterController.PrinterController_Take_The_Paper(1);
//                                bluetoothPrint(strFor,"forNewDevice");
//                                mPrinterController.PrinterController_Take_The_Paper(2);
////                if (mCut) {
////                    cut();
////                }
////                                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
////                            }
//                        } else {
//                            Toast.makeText(PurchaseDetailActivity.this, "Connect Failure", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//
//                txt_pt_printer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //                if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.N) {
////                    printProductCopy(strFor);
////                }else{
//                        fromPrintDialog.dismiss();
//                        try {
//                            Printer newPrinter = null;
//                            newPrinter = new Printer();
//                            newPrinter.open();
//                            int printStatus = newPrinter.queState();
//                            if (printStatus != 0) {
//                                queState_print(printStatus);
//                                return;
//
//                            }
//                            if (newPrinter.open() == 0) {
//
//                                printProductForNewPOS(strFor, newPrinter);
//
//                            }
//                        } catch (Exception e) {
//                            Log.e("Exception", e.toString());
//                        }
//

//                }
//                    }
//                });


            }
        });
//        txt_wifi_printer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                        && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUESTS);
////                }
//                }
//                fromPrintDialog.dismiss();
//                printItem();
//            }
//        });
    }


    public void salesDetail() {

        AppConstant.showProgress(PurchaseDetailActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(PurchaseDetailActivity.this);
        Log.e("id", String.valueOf(purchase_id));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("purchases/" + purchase_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);

                            getPurchaseDetailResponse = responseObject;

                            Log.e("Purchase Detail", responseObject.toString());

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();

                                JSONObject sell = getPurchaseDetailResponse.getJSONObject("purchase");

                                if (sell.has("shipping_details") && !sell.isNull("shipping_details")) {
                                    txt_shipping_detail.setText(sell.getString("shipping_details"));
                                } else {
                                    txt_shipping_detail.setText("None");
                                }

                                if (sell.has("additional_notes") && !sell.isNull("additional_notes")) {
                                    txt_additional_notes.setText(sell.getString("additional_notes"));
                                } else {
                                    txt_additional_notes.setText("None");
                                }

                                if (sell.has("status") && !sell.isNull("status")) {
                                    txt_purchaseStatus.setText(sell.getString("status"));
                                }
                                if (sell.has("payment_status") && !sell.isNull("payment_status")) {
                                    txt_paymemt_status.setText(sell.getString("payment_status"));
                                }
                                if (sell.has("ref_no") && !sell.isNull("ref_no")) {
                                    refrenceNo = sell.getString("ref_no");
                                    txt_DetailPurchase.setText("Purchase Detals (Ref. No: #" + sell.getString("ref_no") + ")");
                                }
                                if (sell.has("transaction_date") && !sell.isNull("transaction_date")) {
                                    String transaction_date = sell.getString("transaction_date");
                                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date newDate = null;
                                    try {
                                        newDate = spf.parse(transaction_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    spf = new SimpleDateFormat("dd-MM-yyyy");
                                    String strDate1 = spf.format(newDate);

                                    txt_trans_Date.setText(strDate1);

                                }
                                String currency = "$";

//                                if (sell.has("currency") && !sell.isNull("currency")) {
//                                    currency=sell.getString("currency");
//                                }

                                if (sell.has("tax_amount") && !sell.isNull("tax_amount")) {
                                    txt_Purchasetax.setText("( + ) " + currency + " " + sell.getString("tax_amount"));
                                } else {
                                    txt_Purchasetax.setText("( + ) " + currency + " " + "0.00");
                                }
                                if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {
                                    txt_discount.setText("( - ) " + currency + " " + sell.getString("discount_amount"));
                                } else {
                                    txt_discount.setText("( - ) " + currency + " " + "0.00");
                                }
                                if (sell.has("shipping_charges") && !sell.isNull("shipping_charges")) {
                                    txt_shipping.setText("( + ) " + currency + " " + sell.getString("shipping_charges"));
                                } else {
                                    txt_shipping.setText("( + ) " + currency + " " + "0.00");
                                }
                                if (sell.has("total_before_tax") && !sell.isNull("total_before_tax")) {
                                    txt_net_total.setText(currency + " " + sell.getString("total_before_tax"));
                                } else {
                                    txt_net_total.setText(currency + " " + "0.00");
                                }

                                if (sell.has("final_total") && !sell.isNull("final_total")) {
                                    txt_finalTotal.setText(currency + " " + sell.getString("final_total"));
                                } else {
                                    txt_finalTotal.setText(currency + " " + "0.00");
                                }

                                if (sell.has("contact") && !sell.isNull("contact")) {
                                    JSONObject contact = sell.getJSONObject("contact");

                                    if (contact.has("name") && !contact.isNull("name")) {
                                        txt_SupplierName.setText(contact.getString("name"));
                                    }
                                    if (contact.has("supplier_business_name") && !contact.isNull("supplier_business_name")) {
                                        supplierName = contact.getString("supplier_business_name");
                                        txt_SupplierBusinessName.setText(contact.getString("supplier_business_name"));
                                    }

                                    strAddress = "";

                                    if (contact.has("city") && !contact.isNull("city")) {
                                        strAddress = strAddress + contact.getString("city") + ",";
                                    }

                                    if (contact.has("state") && !contact.isNull("state")) {
                                        strAddress = strAddress + contact.getString("state") + ",";
                                    }
                                    if (contact.has("country") && !contact.isNull("country")) {

                                        strAddress = strAddress + contact.getString("country");

                                    }
                                    txt_DetailAddress.setText(strAddress);

                                    if (contact.has("mobile") && !contact.isNull("mobile")) {
                                        strMobile = contact.getString("mobile");
                                        txt_Mobile.setText(contact.getString("mobile"));
                                    }

                                }

                                if (sell.has("purchase_lines") && !sell.isNull("purchase_lines")) {
                                    JSONArray sell_lines = sell.getJSONArray("purchase_lines");
                                    for (int i = 0; i < sell_lines.length(); i++) {
                                        arrProductList.add(sell_lines.getJSONObject(i));
                                    }
                                }


//                                productDetailFromIds();//fetch product details as same as in search product.

//                                if(sell.has("delivery_order_status") && !sell.isNull("delivery_order_status")){
//                                    strDeliveryStatus=sell.getString("delivery_order_status");
//                                    if(strDeliveryStatus.equalsIgnoreCase("picked_up")){
//                                        txt_DeliveryStatus.setText("Picked Up");
//                                    }else{
//                                        txt_DeliveryStatus.setText(strDeliveryStatus);
//                                    }
//
//                                }

//                                invoice_no = sell.getString("invoice_no");
//                                transaction_date = sell.getString("transaction_date");
//                                payment_status = sell.getString("payment_status");

//                                JSONObject objLocation = new JSONObject();
//                                if (sell.has("location") && !sell.isNull("location")) {
//                                    objLocation = sell.getJSONObject("location");
//
//
//                                }

//                                txt_DetailLocation.setText(objLocation.getString("name"));
//
//                                if (getSalesDetailResponse.has("receipt") && !getSalesDetailResponse.isNull("receipt")) {
//                                    objReceipt = getSalesDetailResponse.getJSONObject("receipt");
//                                }


                                //set order tax
//                                if (sell.has("tax") && !sell.isNull("tax")) {
//                                    JSONObject objSellTex = sell.getJSONObject("tax");
//
//                                    if (objSellTex.getString("name") != null) {
//                                        strOrderTaxName = objSellTex.getString("name");
//
//                                        if (getSalesDetailResponse.has("order_taxes") && !getSalesDetailResponse.isNull("order_taxes")) {
//                                            JSONObject objOrderTex = getSalesDetailResponse.getJSONObject("order_taxes");
//
//                                            if (objOrderTex.getString(strOrderTaxName) != null) {
//                                                strOrderTaxAmount = objOrderTex.getString(strOrderTaxName);
//                                            }
//                                        }
//                                    }
//                                }
                                //end order tax

//
                                JSONArray payment_lines = sell.getJSONArray("payment_lines");
                                for (int j = 0; j < payment_lines.length(); j++) {
                                    paymentLinesList.add(payment_lines.getJSONObject(j));
                                }

                                paymentLinesAdapter = new PaymentLinesAdapter(PurchaseDetailActivity.this, paymentLinesList);
                                recycler_PaymentLines.setAdapter(paymentLinesAdapter);

                                purchaseProductDetailAdapter = new PurchaseProductDetailAdapter(PurchaseDetailActivity.this, arrProductList);
                                recyc_ProductDetail.setAdapter(purchaseProductDetailAdapter);

                            }

                        } else {
                            AppConstant.sendEmailNotification(PurchaseDetailActivity.this, "purchase/id API", "(PurchaseDetailACtivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(PurchaseDetailActivity.this, "Could Not Load Purchase Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(PurchaseDetailActivity.this, "Could Not Load Purchase Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }


    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("PURCHASE DETAIL");
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

    public void deletePurchase() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PurchaseDetailActivity.this);
        builder1.setMessage("Are you sure you want to Delete this Purchase?");
        builder1.setTitle("Delete Purchase");
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
        AppConstant.showProgress(PurchaseDetailActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteCategory("purchases/" + purchase_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("delete purchase", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                if (PurchasesListActivity.purchaseListActivity != null) {
                                    PurchasesListActivity.purchaseListActivity.finish();
                                }
                                if (PurchaseDetailActivity.purchaseDetailActivity != null) {
                                    PurchaseDetailActivity.purchaseDetailActivity.finish();
                                }

                                Intent i = new Intent(PurchaseDetailActivity.this, PurchasesListActivity.class);
                                startActivity(i);

                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(PurchaseDetailActivity.this, "purchases/id delete API", "(Purchases)", "Web API Error : API Response Is Null");
                            Toast.makeText(PurchaseDetailActivity.this, "Could Not Delete Purchase . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(PurchaseDetailActivity.this, "Could Not Delete Purchase . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


    public void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    protected void bluetoothPrint(String strFor,String forDevice) {

        String BILL = "";

        BILL = " ----- " + strFor + " Copy ----\n" +
                "\n " +
                " " + strAddress + "\n\n" +
                " " + "Mobile :" + strMobile + "\n\n" +
                " \n";

        try {

            JSONObject sell = getPurchaseDetailResponse.getJSONObject("purchase");


            BILL = BILL
                    + " " + "Reference No. : " + refrenceNo + " \n\n"
                    + " " + "Supplier  : " + sell.getString("status") + " \n\n"
                    + " " + "Purchase Status  : " + sell.getString("payment_status") + " \n\n"
                    + " " + "Payment Status  : " + sell.getString("payment_status") + " \n\n"
                    + " " + "Date  : " + sell.getString("transaction_date") + " \n\n"

                    + "--------------------------------\n";

            String strCurrencyType = "";

            BILL = BILL + "            Product           \n"
                    + "--------------------------------";

            for (int i = 0; i < arrProductList.size(); i++) {
                JSONObject sellItem = (JSONObject) arrProductList.get(i);

                try {

                    try {
                        BILL = BILL + "\n";
                        String strProductName = ""; // combine all products name , modifiers name etc.
                        JSONObject sellProduct = null;
                        if (sellItem.has("product") && !sellItem.isNull("product")) {
                            sellProduct = (JSONObject) sellItem.getJSONObject("product");
                            String strName = "";
                            if (sellProduct.has("name") && !sellProduct.isNull("name")) {
                                strName = sellProduct.getString("name");
                            }
                            strProductName = strProductName + sellProduct.getInt("id") + " " + strName;

                        }
                        JSONObject variationProduct = null;
                        String variation = "";
                        if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                            variationProduct = (JSONObject) sellItem.getJSONObject("variations");
                            if (variationProduct.has("product_variation") && !variationProduct.isNull("product_variation")) {
                                JSONObject product_variation = (JSONObject) variationProduct.getJSONObject("product_variation");
                                variation = product_variation.getString("name") + " - ";
                            }
                            variation = variation + variationProduct.getString("name");
                            strProductName = strProductName + " - " + variation;
                        }
                        BILL = BILL + "\n" + strProductName;

                    } catch (Exception e) {

                    }

                    BILL = BILL + "\n\n" + "Quantity  :  " + sellItem.getString("quantity") + " Pc(s)";
                    BILL = BILL + "\n\n" + "Unit Cost  :  " + sellItem.getString("pp_without_discount");
                    BILL = BILL + "\n\n" + "Discount Percent  :  " + sellItem.getString("discount_percent");

                    if (sellItem.has("item_tax") && !sellItem.isNull("item_tax")) {
                        BILL = BILL + "\n\n" + "Tax  :  " + sellItem.getString("item_tax");
                    }
                    if (sellItem.has("default_sell_price") && !sellItem.isNull("default_sell_price")) {
                        BILL = BILL + "\n\n" + "Unit Selling Price  : " + sellItem.getString("default_sell_price");
                    }

                    double subtotal = 0.0;
                    double price_inc_tax = 0.0;
                    int quantity = 0;
                    if (sellItem.has("purchase_price_inc_tax") && !sellItem.isNull("purchase_price_inc_tax")) {
                        price_inc_tax = Double.parseDouble(sellItem.getString("purchase_price_inc_tax"));
                    }
                    if (sellItem.has("quantity") && !sellItem.isNull("quantity")) {
                        quantity = sellItem.getInt("quantity");
                    }
                    subtotal = price_inc_tax * quantity;
                    BILL = BILL + "\n\n" + "Subtotal  : " + subtotal;


//                    if (i != arrProductList.size() - 1) {
//                        newPrinter.printString("--------------------------------");
//                    }

                } catch (Exception e) {

                }
            }
            BILL = BILL + "\n\n" + "--------------------------------" + subtotal;
            BILL = BILL + "\n\n" + "Net Total Amount   : " + sell.getString("total_before_tax");
            BILL = BILL + "\n\n" + "Discount (" + "%" + ")   : (-)" + String.format("%.2f", Double.valueOf(sell.getString("discount_amount")));
            BILL = BILL + "\n\n" + "Purchase Tax   :  (+) " + strCurrencyType + " " + sell.getString("tax_amount");
            BILL = BILL + "\n\n" + "Shipping charges  :  (+) " + strCurrencyType + " " + sell.getString("shipping_charges");
            BILL = BILL + "\n\n" + "--------------------------------";

            BILL = BILL + "\n\n" + "Purchase Total   :  " + strCurrencyType + " " + sell.getString("final_total");
            BILL = BILL + "\n\n" + "--------------------------------";

            BILL = BILL + "\n\n" + "Paid By   :";

            for (int i = 0; i < paymentLinesList.size(); i++) {

                JSONObject paymentLines = (JSONObject) paymentLinesList.get(i);

                if (paymentLines.getString("method") != null && paymentLines.getString("amount") != null) {
                    String date = paymentLines.getString("paid_on");


                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date newDate = null;
                    try {
                        newDate = spf.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    spf = new SimpleDateFormat("dd-MM-yyyy");
                    String strDate = spf.format(newDate);

                    String strMethod = paymentLines.getString("method");
                    String strMethodCapital = strMethod.substring(0, 1).toUpperCase() + strMethod.substring(1);
                    BILL = BILL + "\n\n" + strMethodCapital + " " + strDate + "    :  " + strCurrencyType + " " + paymentLines.getString("amount");
                }
            }

            if (sell.has("shipping_details") && !sell.isNull("shipping_details")) {
                BILL = BILL + "\n\n" + "Shipping Details   :  " + strCurrencyType + " " + sell.getString("shipping_details");
            }
            if (sell.has("additional_notes") && !sell.isNull("additional_notes")) {
                BILL = BILL + "\n\n" + "Additional Notes   :  " + sell.getString("additional_notes");
            }


        } catch (Exception e) {

        }
        BILL = BILL + "\n\n\n";
        BILL = BILL + "     ***** THANK YOU *****";
        BILL = BILL + "\n\n\n\n\n ";

        Log.e("bill", "" + BILL.toString());

        if(forDevice.equalsIgnoreCase("forBluetooth")){
            Intent intent = new Intent(PurchaseDetailActivity.this, BluetoothPrintActivity.class);
            intent.putExtra("bill_receipt", BILL);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
        else{
            if (Language == 2) {
                try {
                    mPrinterController.PrinterController_Print(BILL.getBytes("GB2312"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                mPrinterController.PrinterController_Print(BILL.getBytes());
            }
        }

    }
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPrinterController)
            mPrinterController.PrinterController_Close();
    }

}