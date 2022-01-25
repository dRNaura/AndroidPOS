package com.pos.salon.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import com.pos.salon.R;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.PrinterController;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CloseRegister extends AppCompatActivity {

    RelativeLayout lay_close_register, lay_print_register;
    Toolbar toolbar;
    TextView txt_cash_inHand, txt_cash_Payment, txt_cheque_Payment, txt_card_Payment, txt_bank_transfer, txt_reward_payment, txt_store_credit_payment, txt_custom_payment, txt_other_payment, txt_total_sales, txt_total_refund, txt_total_cash;
    EditText et_total_cash, et_closing_notes;
    Double closingAmount = 0.00;
    JSONObject getResopnseRegisterDetail = new JSONObject();
    public String comingFrom="";
    public int user_id=0;
    LinearLayout lay_total;
    private PrinterController mPrinterController = null;
    private int flag,Language=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_register);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        lay_close_register = findViewById(R.id.lay_close_register);
        et_total_cash = findViewById(R.id.et_total_cash);
        txt_cash_inHand = findViewById(R.id.txt_cash_inHand);
        txt_cash_Payment = findViewById(R.id.txt_cash_Payment);
        txt_cheque_Payment = findViewById(R.id.txt_cheque_Payment);
        txt_card_Payment = findViewById(R.id.txt_card_Payment);
        txt_bank_transfer = findViewById(R.id.txt_bank_transfer);
        txt_reward_payment = findViewById(R.id.txt_reward_payment);
        txt_store_credit_payment = findViewById(R.id.txt_store_credit_payment);
        txt_custom_payment = findViewById(R.id.txt_custom_payment);
        txt_other_payment = findViewById(R.id.txt_other_payment);
        txt_total_sales = findViewById(R.id.txt_total_sales);
        txt_total_refund = findViewById(R.id.txt_total_refund);
        txt_total_cash = findViewById(R.id.txt_total_cash);
//        txt_closing_amount = findViewById(R.id.txt_closing_amount);
//        et_card_slips = findViewById(R.id.et_card_slips);
//        et_total_cheques = findViewById(R.id.et_total_cheques);
        et_closing_notes = findViewById(R.id.et_closing_notes);
        lay_print_register = findViewById(R.id.lay_print_register);
        lay_total = findViewById(R.id.lay_total);

        toolbar = (Toolbar) findViewById(R.id.toolbar_close_register);
        setSupportActionBar(toolbar);


        listeners();
        setBackNavgation();

        comingFrom=getIntent().getStringExtra("comingFrom");
        if(comingFrom.equalsIgnoreCase("user_Detail")){

            lay_total.setVisibility(View.GONE);

            user_id=getIntent().getIntExtra("user_id",0);
            if (AppConstant.isNetworkAvailable(CloseRegister.this)) {
                userRegisterDetail(); // check register detail
            } else {
                AppConstant.openInternetDialog(CloseRegister.this);
            }
        }else{

            lay_total.setVisibility(View.VISIBLE);
            if (AppConstant.isNetworkAvailable(CloseRegister.this)) {
                registerDetail(); // check register detail
            } else {
                AppConstant.openInternetDialog(CloseRegister.this);
            }
        }


    }

    public void listeners() {


        et_total_cash.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                String etText = et_total_cash.getText().toString();
                String temp = et_total_cash.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = et_total_cash.getSelectionStart();
                    if (temp.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = temp.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = temp.substring(0, dotPosition);
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

        lay_close_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_total_cash.getText().toString().isEmpty()) {
                    AppConstant.showToast(CloseRegister.this, "Please Enter Amount");
                } else {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(CloseRegister.this);
                    builder1.setMessage("Are you sure you want to Close Register?");
                    builder1.setTitle("Close Register");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CloseRegister.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            if (AppConstant.isNetworkAvailable(CloseRegister.this)) {
                                closeRegister();
                            } else {
                                AppConstant.openInternetDialog(CloseRegister.this);
                            }

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

            }
        });

        lay_print_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPrinterDialog();
            }
        });


    }

    public void openPrinterDialog() {
        final Dialog fromPrintDialog = new Dialog(CloseRegister.this);
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
                bluetoothPrint("forBluetooth");

            }
        });

        txt_print_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == mPrinterController) {
                    mPrinterController = PrinterController.getInstance(CloseRegister.this);
                }
                BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//               Toast.makeText(CloseRegister.this, "Battery " + battery, Toast.LENGTH_SHORT).show();

                try {
                    flag = mPrinterController.PrinterController_Open();
                }catch (Exception e){
                    Log.e("Exception",e.toString());
                }

                if (flag == 0) {
//            settrue();
//                            if (battery >= 30) {
                    fromPrintDialog.dismiss();
                    Toast.makeText(CloseRegister.this, "Connect Success", Toast.LENGTH_SHORT).show();

                    mPrinterController.PrinterController_PrinterLanguage(Language);
                    mPrinterController.PrinterController_Take_The_Paper(1);
                    bluetoothPrint("forNewDevice");
                    mPrinterController.PrinterController_Take_The_Paper(2);
//                if (mCut) {
//                    cut();
//                }
//                                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
//                            }
                } else {
                    Toast.makeText(CloseRegister.this, "Connect Failure", Toast.LENGTH_SHORT).show();
                }


//                final Dialog fromPrintDialog = new Dialog(CloseRegister.this);
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
//                        printRegisterForDevice5();
//
//                    }
//
//                });
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (null == mPrinterController) {
//                            mPrinterController = PrinterController.getInstance(CloseRegister.this);
//                        }
//                        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
//                        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//
////                        Toast.makeText(CloseRegister.this, "Battery " + battery, Toast.LENGTH_SHORT).show();
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
//                                fromPrintDialog.dismiss();
//                                Toast.makeText(CloseRegister.this, "Connect Success", Toast.LENGTH_SHORT).show();
//
//                                mPrinterController.PrinterController_PrinterLanguage(Language);
//                                mPrinterController.PrinterController_Take_The_Paper(1);
//                                bluetoothPrint("forNewDevice");
//                                mPrinterController.PrinterController_Take_The_Paper(2);
////                if (mCut) {
////                    cut();
////                }
////                                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
////                            }
//                        } else {
//                            Toast.makeText(CloseRegister.this, "Connect Failure", Toast.LENGTH_SHORT).show();
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
//                                printProductForNewPOS(newPrinter);
//
//                            }
//                        } catch (Exception e) {
//                            Log.e("Exception", e.toString());
//                        }
//
//
////                }
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


    public void registerDetail() {
        AppConstant.showProgress(CloseRegister.this, false);
        Retrofit retrofit = APIClient.getClientToken(CloseRegister.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.registerDetail("cash-register/close-register");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            Log.e("close-register(Get)", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            getResopnseRegisterDetail = responseObject;
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true")) {

                                if (responseObject.has("closing_amount") && !responseObject.isNull("closing_amount")) {
                                    closingAmount = Double.valueOf(responseObject.getString("closing_amount"));

                                }
                                JSONObject register_detailsObj = responseObject.getJSONObject("register_details");
                                if (register_detailsObj.has("cash_in_hand") && !register_detailsObj.isNull("cash_in_hand")) {
                                    txt_cash_inHand.setText("$ " + register_detailsObj.getString("cash_in_hand"));
                                }
                                if (register_detailsObj.has("total_cash") && !register_detailsObj.isNull("total_cash")) {
                                    txt_cash_Payment.setText("$ " + register_detailsObj.getString("total_cash"));
                                }
                                if (register_detailsObj.has("total_cheque") && !register_detailsObj.isNull("total_cheque")) {
                                    txt_cheque_Payment.setText("$ " + register_detailsObj.getString("total_cheque"));
                                }
                                if (register_detailsObj.has("total_card") && !register_detailsObj.isNull("total_card")) {
                                    txt_card_Payment.setText("$ " + register_detailsObj.getString("total_card"));
                                }
                                if (register_detailsObj.has("total_bank_transfer") && !register_detailsObj.isNull("total_bank_transfer")) {
                                    txt_bank_transfer.setText("$ " + register_detailsObj.getString("total_bank_transfer"));
                                }
                                if (register_detailsObj.has("total_reward") && !register_detailsObj.isNull("total_reward")) {
                                    txt_reward_payment.setText("$ " + register_detailsObj.getString("total_reward"));
                                }
                                if (register_detailsObj.has("total_sc") && !register_detailsObj.isNull("total_sc")) {
                                    txt_store_credit_payment.setText("$ " + register_detailsObj.getString("total_sc"));
                                }
                                if (register_detailsObj.has("total_custom_pay_1") && !register_detailsObj.isNull("total_custom_pay_1")) {
                                    txt_custom_payment.setText("$ " + register_detailsObj.getString("total_custom_pay_1"));
                                }
                                if (register_detailsObj.has("total_other") && !register_detailsObj.isNull("total_other")) {
                                    txt_other_payment.setText("$ " + register_detailsObj.getString("total_other"));
                                }
                                if (register_detailsObj.has("total_sale") && !register_detailsObj.isNull("total_sale")) {
                                    txt_total_sales.setText("$ " + register_detailsObj.getString("total_sale"));
                                }
                                if (register_detailsObj.has("total_cash") && !register_detailsObj.isNull("total_cash")) {
                                    txt_total_cash.setText("$ " + register_detailsObj.getString("total_cash"));
                                }
                                if (register_detailsObj.has("total_refund") && !register_detailsObj.isNull("total_refund")) {
                                    txt_total_refund.setText("$ " + register_detailsObj.getString("total_refund"));
                                }
                                et_total_cash.setText(String.format("%.2f", closingAmount));
                                et_total_cash.setSelection(et_total_cash.getText().length());
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(CloseRegister.this, "cash-register/close-register Detail API", "(CloseRegister Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(CloseRegister.this, "Could Not Get Register Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CloseRegister.this, "Could Not Get Register Detail. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public void closeRegister() {

        AppConstant.showProgress(CloseRegister.this, false);
        Retrofit retrofit = APIClient.getClientToken(CloseRegister.this);

        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("closing_amount", et_total_cash.getText().toString());
                jsonObject.put("closing_note", et_closing_notes.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.openRegister("cash-register/close-register", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("close-register(Post)", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true")) {
                                if(HomeActivity.homeActivity !=null){
                                    HomeActivity.homeActivity.finish();
                                }
                                AppConstant.showToast(CloseRegister.this, "Register Closed Successfully");
                                Intent intent = new Intent(CloseRegister.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(CloseRegister.this, "cash-register/close-register API", "(CloseRegister Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(CloseRegister.this, "Could Not Close Register. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CloseRegister.this, "Could Not Close Register. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle(R.string.registerDetail);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent i = new Intent(CloseRegister.this, HomeActivity.class);
//                    startActivity(i);
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

    public void userRegisterDetail() {
        AppConstant.showProgress(CloseRegister.this, false);
        Retrofit retrofit = APIClient.getClientToken(CloseRegister.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.registerDetail("cash-register/register-details-by-user/"+user_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            Log.e("close-registerUser", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            getResopnseRegisterDetail = responseObject;
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("true")) {

                                if (responseObject.has("closing_amount") && !responseObject.isNull("closing_amount")) {
                                    closingAmount = Double.valueOf(responseObject.getString("closing_amount"));

                                }
                                JSONObject register_detailsObj = responseObject.getJSONObject("register_details");
                                if (register_detailsObj.has("cash_in_hand") && !register_detailsObj.isNull("cash_in_hand")) {
                                    txt_cash_inHand.setText("$ " + register_detailsObj.getString("cash_in_hand"));
                                }
                                if (register_detailsObj.has("total_cash") && !register_detailsObj.isNull("total_cash")) {
                                    txt_cash_Payment.setText("$ " + register_detailsObj.getString("total_cash"));
                                }
                                if (register_detailsObj.has("total_cheque") && !register_detailsObj.isNull("total_cheque")) {
                                    txt_cheque_Payment.setText("$ " + register_detailsObj.getString("total_cheque"));
                                }
                                if (register_detailsObj.has("total_card") && !register_detailsObj.isNull("total_card")) {
                                    txt_card_Payment.setText("$ " + register_detailsObj.getString("total_card"));
                                }
                                if (register_detailsObj.has("total_bank_transfer") && !register_detailsObj.isNull("total_bank_transfer")) {
                                    txt_bank_transfer.setText("$ " + register_detailsObj.getString("total_bank_transfer"));
                                }
                                if (register_detailsObj.has("total_reward") && !register_detailsObj.isNull("total_reward")) {
                                    txt_reward_payment.setText("$ " + register_detailsObj.getString("total_reward"));
                                }
                                if (register_detailsObj.has("total_sc") && !register_detailsObj.isNull("total_sc")) {
                                    txt_store_credit_payment.setText("$ " + register_detailsObj.getString("total_sc"));
                                }
                                if (register_detailsObj.has("total_custom_pay_1") && !register_detailsObj.isNull("total_custom_pay_1")) {
                                    txt_custom_payment.setText("$ " + register_detailsObj.getString("total_custom_pay_1"));
                                }
                                if (register_detailsObj.has("total_other") && !register_detailsObj.isNull("total_other")) {
                                    txt_other_payment.setText("$ " + register_detailsObj.getString("total_other"));
                                }
                                if (register_detailsObj.has("total_sale") && !register_detailsObj.isNull("total_sale")) {
                                    txt_total_sales.setText("$ " + register_detailsObj.getString("total_sale"));
                                }
                                if (register_detailsObj.has("total_cash") && !register_detailsObj.isNull("total_cash")) {
                                    txt_total_cash.setText("$ " + register_detailsObj.getString("total_cash"));
                                }
                                if (register_detailsObj.has("total_refund") && !register_detailsObj.isNull("total_refund")) {
                                    txt_total_refund.setText("$ " + register_detailsObj.getString("total_refund"));
                                }
                                et_total_cash.setText(String.format("%.2f", closingAmount));
                                et_total_cash.setSelection(et_total_cash.getText().length());
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(CloseRegister.this, "cash-register/close-register Detail API", "(CloseRegister Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(CloseRegister.this, "Could Not Get Register Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CloseRegister.this, "Could Not Get Register Detail. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    protected void bluetoothPrint(String forDevice) {

        String BILL = "";
        BILL = " -----" + "Register" + " Copy ---" + "\n\n";
        BILL = BILL;
        try {
            JSONObject registerDetail = null;
            registerDetail = getResopnseRegisterDetail.getJSONObject("register_details");
            if (registerDetail.has("user_name") && !registerDetail.isNull("user_name")) {
                BILL = BILL + registerDetail.getString("user_name") + "\n";
            }
            if (registerDetail.has("email") && !registerDetail.isNull("email")) {
                BILL = BILL + registerDetail.getString("email") + "\n";
            }
            BILL = BILL + "------------------------------------" + "\n";
            BILL = BILL + "          REGISTER DETAIL          \n"
                    + "--------------------------------" + "\n";

            if (registerDetail.has("cash_in_hand") && !registerDetail.isNull("cash_in_hand")) {
                BILL = BILL + "Cash In Hand  :  $ " + registerDetail.getString("cash_in_hand")+ "\n";
            } else {
                BILL = BILL + "Cash In Hand  :  $ " +  "0.00"+ "\n";
            }
            if (registerDetail.has("total_cash") && !registerDetail.isNull("total_cash")) {
                BILL = BILL + "Cash Payment  :  $ " + registerDetail.getString("total_cash")+ "\n";
            } else {
                BILL = BILL + "Cash Payment  :  $ " + "0.00"+ "\n";
            }
            if (registerDetail.has("total_cheque") && !registerDetail.isNull("total_cheque")) {
                BILL = BILL + "Cheque Payment  :  $ " + registerDetail.getString("total_cheque")+ "\n";
            } else {
                BILL = BILL + "Cheque Payment  :  $ " + "0.00"+ "\n";
            }
            if (registerDetail.has("total_card") && !registerDetail.isNull("total_card")) {
                BILL = BILL + "Card Payment  :  $ " + registerDetail.getString("total_card")+ "\n";
            } else {
                BILL = BILL + "Card Payment  :  $ " + "0.00"+ "\n";
            }
            if (registerDetail.has("total_bank_transfer") && !registerDetail.isNull("total_bank_transfer")) {
                BILL = BILL + "Bank Transfer  :  $ " + registerDetail.getString("total_bank_transfer")+"\n";
            } else {
                BILL = BILL + "Bank Transfer  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_reward") && !registerDetail.isNull("total_reward")) {
                BILL = BILL + "Reward Payment  :  $ " + registerDetail.getString("total_reward")+"\n";
            } else {
                BILL = BILL + "Reward Payment  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_sc") && !registerDetail.isNull("total_sc")) {
                BILL = BILL +"Store Credit Payment  :  $ " + registerDetail.getString("total_sc")+"\n";
            } else {
                BILL = BILL +"Store Credit Payment  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_custom_pay_1") && !registerDetail.isNull("total_custom_pay_1")) {
                BILL = BILL +"Custom Payment 1  :  $ " + registerDetail.getString("total_custom_pay_1")+"\n";
            } else {
                BILL = BILL +"Custom Payment 1  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_other") && !registerDetail.isNull("total_other")) {
                BILL = BILL +"Other Payments  :  $ " + registerDetail.getString("total_other")+"\n";
            } else {
                BILL = BILL +"Other Payments  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_sale") && !registerDetail.isNull("total_sale")) {
                BILL = BILL +"Total Sales  :  $ " + registerDetail.getString("total_sale")+"\n";
            } else {
                BILL = BILL +"Total Sales  :  $ " + "0.00"+"\n";
            }

            BILL = BILL + "------------------------------------" + "\n";

            if (registerDetail.has("total_refund") && !registerDetail.isNull("total_refund")) {
                BILL = BILL +"Total Refund  :  $ " + registerDetail.getString("total_refund")+"\n";
            } else {
                BILL = BILL +"Total Refund  :  $ " + "0.00"+"\n";
            }
            if (registerDetail.has("total_cash") && !registerDetail.isNull("total_cash")) {
                BILL = BILL +"Total Cash  :  $ " + registerDetail.getString("total_cash")+"\n";
            } else {
                BILL = BILL +"Total Cash  :  $ " + "0.00"+"\n";
            }

            BILL = BILL + "------------------------------------" + "\n";


        } catch (JSONException e) {
            Log.e("Exception", e.toString());
        }

        BILL = BILL + "\n\n\n";
        BILL = BILL + "     ***** THANK YOU *****";
        BILL = BILL + "\n\n\n\n\n ";

        Log.e("bill", "" + BILL.toString());

        if(forDevice.equalsIgnoreCase("forBluetooth")){
            Intent intent = new Intent(CloseRegister.this, BluetoothPrintActivity.class);
            intent.putExtra("bill_receipt", BILL);
            startActivity(intent);
        }else{
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


    public void queState_print(int status) {
        switch (status) {
            case -2:
                AppConstant.showToast(CloseRegister.this, "Time Out");
                break;
            case -1:
                AppConstant.showToast(CloseRegister.this, "Query Fail");
                break;
            case 0:
                AppConstant.showToast(CloseRegister.this, "Normal");
                break;
            case 1:
                AppConstant.showToast(CloseRegister.this, "No Paper");
                break;
            case 2:
                AppConstant.showToast(CloseRegister.this, "Hot");
                break;
            case 3:
                AppConstant.showToast(CloseRegister.this, "Hot and No Paper");
                break;
            default:
                AppConstant.showToast(CloseRegister.this, "Invalid");
                break;

        }
    }
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPrinterController)
            mPrinterController.PrinterController_Close();
    }


}
