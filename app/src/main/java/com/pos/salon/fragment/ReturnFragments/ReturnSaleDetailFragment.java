package com.pos.salon.fragment.ReturnFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pos.salon.R;
import com.pos.salon.activity.BluetoothPrintActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.PrinterController;
import com.pos.salon.adapter.ReturnAdapters.ReturnProductAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
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
import static android.content.Context.BATTERY_SERVICE;
import static com.pos.salon.activity.HomeActivity.tool_title;


public class ReturnSaleDetailFragment extends Fragment {
    private int retrun_parent_sale_id=0, return_sell_id=0;
    private JSONObject getReturnSalesDetailResponse, objReceipt;
    private TextView txt_return_total, txt_return_ordertax, txt_return_discount, txt_return_finalTotal;
    private TextView txt_DetailInvoice, txt_returnDate, txt_returnCus_name, txt_return_location, txt_returnPaymentStatus, txt_sell_invoice_no, txt_sell_date;
    private ReturnProductAdapter returnProductAdapter;
    private final ArrayList return_sell_linesList = new ArrayList();
    private RecyclerView recyc_ProductDetail;
    private String currencyType;
    private ImageView open_action;
    private ProgressDialog progressDialog;
    private String part1 = "", part2 = "", part3, part4;
    private String return_invoice_no="", return_date="", customer_name="", sell_Invoice_no="", sell_date="";
    public boolean isSaleUpdate = false;
    public boolean isSaleDelete = false;
    private PrinterController mPrinterController = null;
    private int flag,Language=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_return_detail, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tool_title.setText("RETURN SALE DETAIL");
//        if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.N) {
//
//            DriverManager mDriverManager = DriverManager.getInstance();
////        mDriverManager = MyApp.sDriverManager;
//            mPrinter = mDriverManager.getPrinter();
//
//            int printerStatus = mPrinter.getPrinterStatus();
//            Log.e("getPrinterStatus: ", String.valueOf(printerStatus));
//            if (printerStatus != SdkResult.SDK_OK) {
//                mPrintStatus = true;
//            } else {
//                mPrintStatus = false;
//            }
//            //end print
//        }else{

//        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getArguments();
        return_sell_id = bundle.getInt("return_sell_id");
        retrun_parent_sale_id = bundle.getInt("retrun_parent_sale_id");

        txt_DetailInvoice = view.findViewById(R.id.txt_DetailInvoice);
        txt_returnDate = view.findViewById(R.id.txt_returnDate);
        txt_returnCus_name = view.findViewById(R.id.txt_returnCus_name);
        txt_return_location = view.findViewById(R.id.txt_return_location);
        txt_returnPaymentStatus = view.findViewById(R.id.txt_returnPaymentStatus);
        txt_sell_invoice_no = view.findViewById(R.id.txt_sell_invoice_no);
        txt_sell_date = view.findViewById(R.id.txt_sell_date);
        recyc_ProductDetail = view.findViewById(R.id.recyc_ProductDetail);
        txt_return_total = view.findViewById(R.id.txt_return_total);
        txt_return_ordertax = view.findViewById(R.id.txt_return_ordertax);
        txt_return_discount = view.findViewById(R.id.txt_return_discount);
        txt_return_finalTotal = view.findViewById(R.id.txt_return_finalTotal);
        open_action = view.findViewById(R.id.open_action);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyc_ProductDetail.setLayoutManager(layoutManager);

        listeners();

        returnDetail();

    }

    public void listeners() {

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isSaleDelete = true;
            isSaleUpdate=true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.update")) {
                    isSaleUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.delete")) {
                    isSaleDelete = true;
                }


            }
        }

        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop_up(v);
            }
        });
    }

    public void showPop_up(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
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
        popup.getMenuInflater().inflate(R.menu.return_menu_items, popup.getMenu());

        if (isSaleUpdate) {
            popup.getMenu().findItem(R.id.return_invoice_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.return_invoice_edit).setVisible(false);
        }
        if (isSaleDelete) {
            popup.getMenu().findItem(R.id.return_invoice_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.return_invoice_delete).setVisible(false);
        }
//        if (HisSaleView) {
//            popup.getMenu().findItem(R.id.return_invoice_Print).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.return_invoice_Print).setVisible(false);
//        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.return_invoice_edit:

                        ReturnSaleFragment nextFrag = new ReturnSaleFragment();
                        Bundle args = new Bundle();
                        args.putInt("transactionId", retrun_parent_sale_id);
                        args.putString("from", "fromReturnSaleDetailFragment");
//                            String gson = new Gson().toJson(return_sell_linesList );
                        args.putSerializable("productArraylist", return_sell_linesList);
//                            args.putSerializable("productArraylist", return_sell_linesList);
                        nextFrag.setArguments(args);
                        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.lay_return_detail, nextFrag).addToBackStack(null).commit();
                        fragmentManager.beginTransaction().addToBackStack(null);
                        tool_title.setText("RETURN SALE");
                        break;

                    case R.id.return_invoice_delete:

                        deleteConfirmation();

                        break;

                    case R.id.return_invoice_Print:

                        openPrinterDialog();

                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    public void openPrinterDialog() {

        final Dialog fromPrintDialog = new Dialog(getContext());
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
                    mPrinterController = PrinterController.getInstance(getContext());
                }
                BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(BATTERY_SERVICE);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//                 Toast.makeText(getContext(), "Battery " + battery, Toast.LENGTH_SHORT).show();

                try {
                    flag = mPrinterController.PrinterController_Open();
                }catch (Exception e){
                    Log.e("Exception",e.toString());
                }

                if (flag == 0) {
//            settrue();
//                            if (battery >= 30) {
                    fromPrintDialog.dismiss();
                    Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();

                    mPrinterController.PrinterController_PrinterLanguage(Language);
                    mPrinterController.PrinterController_Take_The_Paper(1);
                    bluetoothPrint("forNewDevice");
                    mPrinterController.PrinterController_Take_The_Paper(2);
//                if (mCut) {
//                    cut();
//                }
//                                Toast.makeText(getContext(), "Print", Toast.LENGTH_SHORT).show();
//                            }
                }
                else {
                    Toast.makeText(getContext(), "Connect Failure", Toast.LENGTH_SHORT).show();
                }

//                final Dialog fromPrintDialog = new Dialog(getContext());
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
//                        com.zcs.sdk.Printer mPrinter;
//                        DriverManager mDriverManager = DriverManager.getInstance();
////        mDriverManager = MyApp.sDriverManager;
//                        mPrinter = mDriverManager.getPrinter();
//
//                        int printerStatus = mPrinter.getPrinterStatus();
//                        Log.e("getPrinterStatus: ", String.valueOf(printerStatus));
//                        if (printerStatus != SdkResult.SDK_OK) {
//                            mPrintStatus = true;
//                        } else {
//                            mPrintStatus = false;
//                        }
////            //end print
//                        int printStatus = mPrinter.getPrinterStatus();
//                        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));
//
//                                }
//                            });
//                        } else {
//                            printReturnSaleCopy("Merchant Copy", mPrinter);
//
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
//                            builder1.setMessage("Do you want Customer copy ?");
//                            builder1.setTitle("Print");
//                            builder1.setCancelable(true);
//
//                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    printReturnSaleCopy("Customer Copy", mPrinter);
//                                }
//                            });
//
//                            builder1.setNegativeButton(
//                                    "No", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog alert11 = builder1.create();
//                            alert11.show();
//
//
//                        }
//                    }
//                });
//
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (null == mPrinterController) {
//                            mPrinterController = PrinterController.getInstance(getContext());
//                        }
//                        BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(BATTERY_SERVICE);
//                        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//
////                        Toast.makeText(getContext(), "Battery " + battery, Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(getContext(), "Connect Failure", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//                txt_pt_printer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        newPrinter = new Printer();
//                        newPrinter.open();
//                        int printStatus = newPrinter.queState();
//                        if (printStatus != 0) {
//                            queState_print(printStatus);
//                            return;
//
//                        }
//                        if (newPrinter.open() == 0) {
////                        openflg = true;
//                            printReturnNewPOSCopy("Merchant Copy");
//
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
//                            builder1.setMessage("Do you want Customer copy ?");
//                            builder1.setTitle("Print");
//                            builder1.setCancelable(true);
//
//                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    newPrinter = new Printer();
//                                    newPrinter.open();
//                                    printReturnNewPOSCopy("Customer Copy");
//                                }
//                            });
//
//                            builder1.setNegativeButton(
//                                    "No", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            AlertDialog alert11 = builder1.create();
//                            alert11.show();
//
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


    public void deleteConfirmation() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Are you sure you want to Cancel this Sale ?");
        builder1.setTitle("Cancel Return Sale");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteReturnSaleDetail();
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

    public void deleteReturnSaleDetail() {
        AppConstant.showProgress(getContext(), false);
//          progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("deletetransactionId", String.valueOf(return_sell_id));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteRetrunSaleDetail("sell-return/cancel-return-sale", return_sell_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.body() != null) {
                        AppConstant.hideProgress();

                        try {
                            if (response.body() != null) {
                                AppConstant.hideProgress();
                                String respo = response.body().string();
                                JSONObject responseObject = new JSONObject(respo);
                                Log.e("Cancel Return Response", respo.toString());

                                String successstatus = responseObject.optString("success");
                                //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                                if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();
                                    String msg = "";
                                    if (responseObject.has("msg") && !responseObject.isNull("msg")) {
                                        msg = responseObject.getString("msg");
                                    }

                                    Log.e("message", "" + msg);
                                    Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
                                    ReturnSaleListFragment ldf = new ReturnSaleListFragment();
                                    FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.lay_return_detail, ldf).commit();
                                    tool_title.setText("RETURN SALE LIST");

//                                    if (activeCenterFragments.size() > 0) {
//                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                                        for (Fragment activeFragment : activeCenterFragments) {
//                                            transaction.remove(activeFragment);
//                                        }
//                                        activeCenterFragments.clear();
//                                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                                        transaction.commit();
//                                    }
                                }
                            } else {
                                AppConstant.hideProgress();
                                AppConstant.sendEmailNotification(getContext(), "sell-return/cancel-return-sale API", "(ReturnSaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                                Toast.makeText(getContext(), "Unable To Delete Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(), "Unable To Delete Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void returnDetail() {
        AppConstant.showProgress(getContext(), false);
//          progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(retrun_parent_sale_id));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getReturnSaleDetail("sell-return/view", retrun_parent_sale_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);

                            getReturnSalesDetailResponse = responseObject;
                            Log.e("Retrun Sales Detail", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONObject sell = null;

                                if (getReturnSalesDetailResponse.has("sell") && !getReturnSalesDetailResponse.isNull("sell")) {
                                    sell = responseObject.getJSONObject("sell");

                                } else {
                                    return;
                                }

                                if (getReturnSalesDetailResponse.has("receipt") && !getReturnSalesDetailResponse.isNull("receipt")) {
                                    objReceipt = getReturnSalesDetailResponse.getJSONObject("receipt");

                                }
                                if (sell.has("invoice_no") && !sell.isNull("invoice_no")) {
                                    sell_Invoice_no = sell.getString("invoice_no");
                                }

                                if (sell.has("created_at") && !sell.isNull("created_at")) {
                                    sell_date = sell.getString("created_at");
                                }

                                if (sell.has("currency") && !sell.isNull("currency")) {
                                    currencyType = sell.getString("currency");
                                }


                                String orderDate = sell_date;
                                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date newDate = null;
                                try {
                                    newDate = spf.parse(orderDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                spf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String strDate1 = spf.format(newDate);


                                String[] separated = strDate1.split("\\ ");
                                part1 = separated[0];
                                part2 = separated[1];

                                txt_sell_date.setText(part1 + " | " + part2);

//                                String final_total=sell.getString("final_total");
//                                String discount_amount=sell.getString("discount_amount");
//                                String tax_amount=sell.getString("tax_amount");

                                txt_sell_invoice_no.setText(sell_Invoice_no);

                                JSONObject return_parent = sell.getJSONObject("return_parent");


                                return_invoice_no = return_parent.getString("invoice_no");
                                return_date = return_parent.getString("created_at");

                                String orderDate1 = return_date;
                                SimpleDateFormat spf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date newDate1 = null;
                                try {
                                    newDate1 = spf1.parse(orderDate1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                spf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String strDate = spf1.format(newDate1);


                                String[] separated1 = strDate.split("\\ ");
                                part3 = separated1[0];
                                part4 = separated1[1];

                                txt_returnDate.setText(part3 + " | " + part4);


                                txt_DetailInvoice.setText("Sale Return Detail(Invoice No.: " + return_invoice_no + ")");

                                String payment_status = "";
                                if (return_parent.has("payment_status") && !return_parent.isNull("payment_status")) {
                                    return_parent.getString("payment_status");
                                }
                                txt_returnPaymentStatus.setText(payment_status);

                                if (sell.has("contact") && !sell.isNull("contact")) {
                                    JSONObject contact = sell.getJSONObject("contact");
                                    customer_name = contact.getString("name");
                                }

                                txt_returnCus_name.setText(customer_name);

                                JSONObject location = sell.getJSONObject("location");

                                String location_name = location.getString("name");
                                txt_return_location.setText(location_name);

                                JSONArray sell_lines = sell.getJSONArray("sell_lines");

                                for (int i = 0; i < sell_lines.length(); i++) {
                                    return_sell_linesList.add(sell_lines.getJSONObject(i));
//                                    JSONObject data = sell_lines.getJSONObject(i);
                                }

                                returnProductAdapter = new ReturnProductAdapter(getContext(), return_sell_linesList, currencyType);
                                recyc_ProductDetail.setAdapter(returnProductAdapter);

//calculate total prices
                                double total = 0.0;

                                JSONObject objProduct = new JSONObject();
                                for (int i = 0; i < return_sell_linesList.size(); i++) {
                                    objProduct = (JSONObject) return_sell_linesList.get(i);
                                    String unit_price = objProduct.getString("unit_price");
                                    String quantity_returned = objProduct.getString("quantity_returned");

                                    double subTotalReturn = Double.parseDouble(unit_price) * Double.parseDouble(quantity_returned);

                                    total = total + subTotalReturn;

                                    Log.e("total", String.valueOf(String.format("%.2f", total)));
//                                    return_sell_linesList.add(sell_lines.getJSONObject(i));
//                                    JSONObject data = sell_lines.getJSONObject(i);
                                }

                                txt_return_total.setText(currencyType + String.valueOf(String.format("%.2f", total)));

                                String strDiscount = "0.00";
                                if (sell.has("discount_type") && !sell.isNull("discount_type")) {

                                    if (sell.getString("discount_type").equalsIgnoreCase("percentage")) {

                                        if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {

                                            strDiscount = sell.getString("discount_amount");

                                            if (!strDiscount.isEmpty()) {

                                                strDiscount = sell.getString("discount_amount");

                                            } else {

                                                strDiscount = "0";
                                            }
                                            Double fDiscountAmount = Double.parseDouble(strDiscount);

                                            Double finalDiscount = (fDiscountAmount / 100) * total;

                                            strDiscount = String.valueOf(finalDiscount);
                                        }

                                    } else if (sell.getString("discount_type").equalsIgnoreCase("fixed")) {


                                        if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {

                                            strDiscount = sell.getString("discount_amount");

                                            if (!strDiscount.isEmpty()) {

                                                strDiscount = sell.getString("discount_amount");
                                            } else {
                                                strDiscount = "0";
                                            }

                                        }

                                    }
                                }

                                txt_return_discount.setText(currencyType + String.format("%.2f", Double.valueOf(strDiscount)));

                                if (sell.has("tax_amount") && !sell.isNull("tax_amount")) {
                                    txt_return_ordertax.setText(currencyType + sell.getString("tax_amount"));
                                } else {
                                    txt_return_ordertax.setText(currencyType + "0.00");
                                }

//                                String totalReturn = String.valueOf(total - Float.valueOf(line_discount_amount) + Float.valueOf(item_tax));
//                                double totl_return=Double.parseDouble(totalReturn);
                                txt_return_finalTotal.setText(currencyType + sell.getString("final_total"));
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "sell-return/view API", "(ReturnSaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Return Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }


    }


    // print return sale


    protected void bluetoothPrint(String forDevice) {

        String BILL = "";

        if (objReceipt == null) {
            Toast.makeText(getContext(), "Unable to print , Incomplete sale receipt data.", Toast.LENGTH_LONG).show();

            return;
        }

        String address = "", locationname = "", mobile = "", customerName = "", returnInvoiceDate = "";
        JSONArray arrReturnProducts = new JSONArray();

        if (objReceipt.has("address") && !objReceipt.isNull("address")) {
            try {
                address = objReceipt.getString("address");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (objReceipt.has("location_name") && !objReceipt.isNull("location_name")) {
            try {
                locationname = objReceipt.getString("location_name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (objReceipt.has("contact") && !objReceipt.isNull("contact")) {
            try {
                mobile = objReceipt.getString("contact");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!address.isEmpty()) {

            BILL = BILL + locationname + address + "\n";
            if (!mobile.isEmpty()) {
                BILL = BILL + mobile + "\n\n";
            }
        }


        if (objReceipt.has("customer_name") && !objReceipt.isNull("customer_name")) {
            try {
                customerName = objReceipt.getString("customer_name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (objReceipt.has("invoice_date") && !objReceipt.isNull("invoice_date")) {
            try {
                returnInvoiceDate = objReceipt.getString("invoice_date");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        BILL = BILL + "Customer  :  " + customerName + "\n";
        BILL = BILL + "Date  :  " + returnInvoiceDate + "\n\n";
        BILL = BILL + "------------------------------------" + "\n\n";
        BILL = BILL + "            SALE RETURN           \n";
        BILL = BILL + "------------------------------------" + "\n\n";


        if (objReceipt.has("lines") && !objReceipt.isNull("lines")) {
            try {
                arrReturnProducts = objReceipt.getJSONArray("lines");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        double total = 0.0;
        JSONObject objProduct = new JSONObject();

        JSONObject objReturnProduct = new JSONObject();

        for (int i = 0; i < arrReturnProducts.length(); i++) {

            try {
                objProduct = arrReturnProducts.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (return_sell_linesList.get(i) != null) {
                objReturnProduct = (JSONObject) return_sell_linesList.get(i);
            }

            try {


                String productNAme = "";

                if (objProduct.has("name") && !objProduct.isNull("name")) {

                    productNAme = productNAme + objProduct.getString("name");

                }

                if (objProduct.has("variation") && !objProduct.isNull("variation")) {

                    productNAme = productNAme + objProduct.getString("variation") + " , ";

                }

                if (objProduct.has("sub_sku") && !objProduct.isNull("sub_sku")) {

                    productNAme = productNAme + objProduct.getString("sub_sku") + " ";

                }


                BILL = BILL + "\n";


                String quantity = "0";
                if (objReturnProduct.length() > 0) {
                    quantity = objReturnProduct.getString("quantity_returned");
                    BILL = BILL + "Quantity  :  " + Integer.parseInt(objReturnProduct.getString("quantity_returned")) + " Pc(s)" + "\n";

                }
                BILL = BILL + "Unit Price  :  " + currencyType + " " + objProduct.getString("unit_price") + "\n";


                String unit_price = objProduct.getString("unit_price");

                String quantyty1 = String.valueOf(quantity);
                String subtotal = String.valueOf(Float.valueOf(quantyty1) * Float.valueOf(unit_price));

                BILL = BILL + "Subtotal  :  " + currencyType + " " + subtotal + "\n";

                double subTotalReturn = Double.parseDouble(unit_price) * Double.parseDouble(quantity);

                total = total + subTotalReturn;


            } catch (Exception e) {

            }
        }
        BILL = BILL + "------------------------------------" + "\n";

        BILL = BILL + "Subtotal  :  " + currencyType + " " + total + "\n";

        BILL = BILL + "Grand Total  :  " + currencyType + " " + total + "\n\n";
        BILL = BILL + "------------------------------------" + "\n";


        try {
            if (objReceipt.has("reward_earned") && !objReceipt.isNull("reward_earned")) {
                String strRewardEarned = objReceipt.getString("reward_earned");
                BILL = BILL + "Reward Earned   :  " + currencyType + " " + strRewardEarned.trim() + "\n";

            } else {
                BILL = BILL + "Reward Earned   :  " + currencyType + " 0" + "\n";
            }


            if (objReceipt.has("total_reward") && !objReceipt.isNull("total_reward")) {
                String strTotalReward = objReceipt.getString("total_reward");
                BILL = BILL + "Total Reward   :  " + currencyType + " " + strTotalReward.trim() + "\n";

            } else {
                BILL = BILL + "Total Reward   :  " + currencyType + " 0" + "\n";
            }


            if (objReceipt.has("total_sc") && !objReceipt.isNull("total_sc")) {
                String strStoreCredit = objReceipt.getString("total_sc");
                BILL = BILL + "Store Credit   :  " + currencyType + " " + strStoreCredit.trim() + "\n";

            } else {
                BILL = BILL + "Store Credit   :  " + currencyType + " 0" + "\n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        BILL = BILL + "\n\n\n";
        BILL = BILL + "     ***** THANK YOU *****";
        BILL = BILL + "\n\n\n\n\n ";

        Log.e("bill", "" + BILL.toString());

        if(forDevice.equalsIgnoreCase("forBluetooth")){
            Intent intent = new Intent(getActivity(), BluetoothPrintActivity.class);
            intent.putExtra("bill_receipt", BILL);
            startActivity(intent);
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

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(String.valueOf(retrun_parent_sale_id), BarcodeFormat.CODE_128,250, 70);
                    Bitmap bitmap = Bitmap.createBitmap(250, 70, Bitmap.Config.RGB_565);
                    for (int i = 0; i<250; i++){
                        for (int j = 0; j<70; j++){
                            bitmap.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
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

}
