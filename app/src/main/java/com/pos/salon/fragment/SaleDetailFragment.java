package com.pos.salon.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.pos.salon.activity.ActivityPosSale.ActivityPosItemList;
import com.pos.salon.activity.BluetoothPrintActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.PrinterController;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.PaymentLinesAdapter;
import com.pos.salon.adapter.ProductsAdapters.ProductDetailAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.posLocation.Taxes;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.model.searchData.SearchItemResponse;
import com.pos.salon.utilConstant.AppConstant;

import org.jetbrains.annotations.NotNull;
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
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.pos.salon.activity.HomeActivity.isRegisterOpen;
import static com.pos.salon.activity.HomeActivity.tool_title;
import static com.pos.salon.adapter.ProductsAdapters.ProductDetailAdapter.sellItem;

public class SaleDetailFragment extends Fragment {

    private JSONObject getSalesDetailResponse, objReceipt;
    private RecyclerView recyc_ProductDetail, recycler_PaymentLines;
    private ProductDetailAdapter productDetailAdapter;
    private PaymentLinesAdapter paymentLinesAdapter;
    private final ArrayList sell_linesList = new ArrayList();
    private ArrayList<SearchItem> arrOrignalProdList = new ArrayList<SearchItem>();
    private final ArrayList paymentLinesList = new ArrayList();
    private TextView txt_DeliveryStatus, txt_ShippingAddress, txt_DetailAddress, txt_total, txt_totaldiscount, txt_OrderTax, txt_ShippingCharges, txt_FinalTotal, txt_TotalPaid, txt_TotalRemaining, txt_staff_note, txt_sale_note;
    public static String currencyType = "$";
    private String str_Paid = "";
    private String remaining="";
    private String transactionId="";
    private ImageView open_action;
    private String payable = "", total_paid;
    private String strLocationMobile = "", strLocationAddress = "", strOrderTaxName = "", strOrderTaxAmount = "", part1 = "", part2 = "", dis = "0", strDiscountAmount = "0.0", strGrandSubtotal = "", strDiscountType = "";
    private String id_type="";
    private String invoiceId = "";
    private String str_shipping = "";
    private String invoice_no = "", customer_name = "", transaction_date = "", payment_status = "",customerNameAddress="",business_name="";
    private TextView txt_DetailCustomerName, txt_DetailLocation, txt_DetailInvoice, txt_PaymentStatus, txt_DetailDates;
    private String comingFrom = "", subFrom = "", sendNotificationType = "", strDeliveryStatus = "";
    //    private Printer mPrinter;
    int idLocationId = 0, return_exists = 0;
    private SharedPreferences.Editor ed_cartSave, ed_countproduct, ed_selectedcustomer;
    private Dialog openRegisterDialog, newSaleNotificationDialog;
    private EditText et_register_amount;
    private CheckBox ch_send_email, ch_send_sms, ch_send_both;
    private CardView card_shipping_address;
    ArrayList<String> arrdepartmentsList = new ArrayList<String>();
    public boolean isSaleUpdate = false;
    public boolean isSaleDelete = false;
    public boolean isSendNotification = false;
    public boolean isSaleCreate = false;

    private PrinterController mPrinterController = null;
    private int flag,Language=0;
    private int productOrService=0;
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_detail, container, false);

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        tool_title.setText("SALE DETAIL");
        // print code
        recyc_ProductDetail = view.findViewById(R.id.recyc_ProductDetail);
        recycler_PaymentLines = view.findViewById(R.id.recycler_PaymentLines);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyc_ProductDetail.setLayoutManager(layoutManager);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_PaymentLines.setLayoutManager(mLayoutManager);

        ///get cart shared preference
        SharedPreferences sp_cartSave = getContext().getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        //cart product count set.
        SharedPreferences sp_countproduct = getContext().getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();


        SharedPreferences sp_selectedcustomer = getContext().getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
        ed_selectedcustomer = sp_selectedcustomer.edit();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", MODE_PRIVATE);
        //check for departments
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {
        }.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }

        Bundle bundle = getArguments();
//        listPosModel = (ListPosModel) bundle.getSerializable("saleModel");
        transactionId = bundle.getString("transactionId"); // your selected transaction id.
        comingFrom = bundle.getString("from");//shows from which screen you come.
        subFrom = bundle.getString("subFrom");//to check either coming from list pos or Repair Sales
        id_type = bundle.getString("id_type");

        Gson gson = new Gson();
        Type type = new TypeToken<BusinessDetails>() {
        }.getType();
        final String myBusinessDetail = sharedPreferences.getString("myBusinessDetail", "");

        if (myBusinessDetail != null) {
            BusinessDetails objMyBusiness = (BusinessDetails) gson.fromJson(myBusinessDetail, type);
            currencyType = objMyBusiness.getCurrency_code();
        }

        if (comingFrom.equalsIgnoreCase("fromListPosFragment")) {
            return_exists = bundle.getInt("return_exists");
        }
        txt_total = view.findViewById(R.id.txt_total);
        txt_totaldiscount = view.findViewById(R.id.txt_discount);
        txt_OrderTax = view.findViewById(R.id.txt_ordertax);
        txt_ShippingCharges = view.findViewById(R.id.txt_shipping);
        txt_FinalTotal = view.findViewById(R.id.txt_finalTotal);
        txt_TotalPaid = view.findViewById(R.id.txt_total_paid);
        txt_TotalRemaining = view.findViewById(R.id.txt_total_remaining);
        txt_staff_note = view.findViewById(R.id.txt_staff_note);
        txt_sale_note = view.findViewById(R.id.txt_sale_note);
        open_action = view.findViewById(R.id.open_action);
        txt_DetailAddress = view.findViewById(R.id.txt_DetailAddress);
        txt_ShippingAddress = view.findViewById(R.id.txt_ShippingAddress);
        card_shipping_address = view.findViewById(R.id.card_shipping_address);
        txt_DeliveryStatus = view.findViewById(R.id.txt_DeliveryStatus);


        txt_DetailCustomerName = view.findViewById(R.id.txt_DetailCustomerName);
        txt_DetailLocation = view.findViewById(R.id.txt_DetailLocation);
        txt_DetailInvoice = view.findViewById(R.id.txt_DetailInvoice);
        txt_PaymentStatus = view.findViewById(R.id.txt_DetailPaymentStatus);
        txt_DetailDates = view.findViewById(R.id.txt_DetailDates);


        listeners();
        salesDetail();


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
            isSendNotification = true;
            isSaleUpdate=true;
            isSaleCreate=true;
        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.update")) {
                    isSaleUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.delete")) {
                    isSaleDelete = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("send_notification")) {
                    isSendNotification = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.create")) {
                    isSaleCreate = true;
                }

            }
        }


        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (comingFrom.equalsIgnoreCase("fromQuatationListFragment")) {
                    //when coming from Qautation List
                    showQuatationPopUp(v, "Quotation");
                } else if (comingFrom.equalsIgnoreCase("fromListDraftFragment")) {

                    //when coming from Draft List
                    showQuatationPopUp(v, "Draft");

                } else if (comingFrom.equalsIgnoreCase("fromShippingFragment")) {

                    showDeliveryPopup(v);
                } else {
                    // when coming from POS LIS
                    showPopup(v);
                }
//
            }
        });
    }

    private void showDeliveryPopup(View view) {
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
        popup.getMenuInflater().inflate(R.menu.delivery_order_items, popup.getMenu());

        if (payment_status.equalsIgnoreCase("paid")) {
            popup.getMenu().findItem(R.id.delivery_order_add_payment).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.delivery_order_add_payment).setVisible(true);
        }
//
//        if (HomeActivity.isSaleView == true) {
//            popup.getMenu().findItem(R.id.delivery_order_print).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.delivery_order_print).setVisible(false);
//        }

        if (strDeliveryStatus.equalsIgnoreCase("received")) {
            popup.getMenu().findItem(R.id.delivery_order_accepted).setVisible(true);
            popup.getMenu().findItem(R.id.delivery_order_pickedup).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_returned).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_delivered).setVisible(false);

        } else if (strDeliveryStatus.equalsIgnoreCase("assigned")) {
            popup.getMenu().findItem(R.id.delivery_order_accepted).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_pickedup).setVisible(true);
            popup.getMenu().findItem(R.id.delivery_order_returned).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_delivered).setVisible(false);
        } else if (strDeliveryStatus.equalsIgnoreCase("picked_up")) {
            popup.getMenu().findItem(R.id.delivery_order_accepted).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_pickedup).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_returned).setVisible(true);
            popup.getMenu().findItem(R.id.delivery_order_delivered).setVisible(true);
        } else if (strDeliveryStatus.equalsIgnoreCase("delivered")) {
            popup.getMenu().findItem(R.id.delivery_order_accepted).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_pickedup).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_returned).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_delivered).setVisible(false);
        } else if (strDeliveryStatus.equalsIgnoreCase("returned")) {
            popup.getMenu().findItem(R.id.delivery_order_accepted).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_pickedup).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_returned).setVisible(false);
            popup.getMenu().findItem(R.id.delivery_order_delivered).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.delivery_order_add_payment:
                        if (isRegisterOpen == true) {
                            boolean bCheck = goToCartScreen("forEdit");// here next go to positem list screen
                        } else {
                            openRegisterDialog();
                        }
                        break;

                    case R.id.delivery_order_accepted:

                        deliveryAccepted();

                        break;

                    case R.id.delivery_order_pickedup:
                        deliveryPickedUp();

                        break;
                    case R.id.delivery_order_returned:
                        deliveryReturned();

                        break;
                    case R.id.delivery_order_delivered:

                        deliveryDelivered();
                        break;

                    case R.id.delivery_order_print:

                        openPrinterDialog("Delivery Order");
                        break;
                }
                return true;
            }
        });
        popup.show();

    }


    private void showQuatationPopUp(View view, final String strFor) {
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
        popup.getMenuInflater().inflate(R.menu.quatation_menu, popup.getMenu());

        if (isSaleUpdate) {
            popup.getMenu().findItem(R.id.quatation_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.quatation_edit).setVisible(false);
        }

        if (isSaleDelete) {
            popup.getMenu().findItem(R.id.quatation_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.quatation_delete).setVisible(false);
        }
//        if (HomeActivity.isSaleView == true) {
//            popup.getMenu().findItem(R.id.quatation_Print).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.quatation_Print).setVisible(false);
//        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.quatation_edit:

                        if (isRegisterOpen == true) {
                            boolean bCheck = goToCartScreen("forEdit");// here next go to positem list screen
                        } else {
                            openRegisterDialog();
                        }

                        break;

                    case R.id.quatation_delete:

                        deleteSaleDetail();

                        break;

                    case R.id.quatation_Print:
//                        if(android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.N){
//                            int printStatus = mPrinter.getPrinterStatus();
//                            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));
//
//                                    }
//                                });
//                            } else {
//                                openPrinterDialog(strFor);
//                            }
//                        }

//                        else{
                        openPrinterDialog(strFor);
//                    }
                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    private void showPopup(View view) {

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
        popup.getMenuInflater().inflate(R.menu.perform_action_items, popup.getMenu());

        //if sale already return or  paymnet status is due than sale cannot be return
//        if (return_exists == 0 && !payment_status.equalsIgnoreCase("due") && isSaleCreate) {
        if (isSaleCreate) {
            popup.getMenu().findItem(R.id.invoice_returnSale).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.invoice_returnSale).setVisible(false);
        }
        if (isSaleUpdate) {
            popup.getMenu().findItem(R.id.invoice_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.invoice_edit).setVisible(false);
        }

//        if (arrdepartmentsList.contains("1") /*|| APIClient.getMyDepartmentId(getContext()) == AppConstant.MyDepartmentId.FASHION*/) {
//            popup.getMenu().findItem(R.id.invoice_exchangeSale).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.invoice_exchangeSale).setVisible(false);
//        }

        if (isSaleCreate) {
            popup.getMenu().findItem(R.id.invoice_exchangeSale).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.invoice_exchangeSale).setVisible(false);
        }

        if (isSaleDelete) {
            popup.getMenu().findItem(R.id.invoice_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.invoice_delete).setVisible(false);
        }
//        if (HomeActivity.isSaleView == true) {
//            popup.getMenu().findItem(R.id.invoice_Print).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.invoice_Print).setVisible(false);
//        }
        if (isSendNotification) {
            popup.getMenu().findItem(R.id.invoice_new_sale_notification).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.invoice_new_sale_notification).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.invoice_edit:

                        if (isRegisterOpen == true) {
                            boolean bCheck = goToCartScreen("forEdit");// here next go to positem list screen
                        } else {
                            openRegisterDialog();
                        }

                        break;

                    case R.id.invoice_exchangeSale:
                        boolean bCheck = goToCartScreen("forExchange");// here next go to positem list screen
                        break;

                    case R.id.invoice_returnSale:

                        //   //if partial payment then cannot return sale.
                        //  if (!payment_status.equalsIgnoreCase("partial")) {
                        ReturnSaleFragment nextFrag = new ReturnSaleFragment();
                        Bundle args = new Bundle();
                        args.putInt("transactionId", Integer.parseInt(transactionId));
                        args.putString("from", "fromSaleDetailFragment");
                        args.putString("subFrom", subFrom);
//                        String gson = new Gson().toJson(sell_linesList );
                        args.putSerializable("productArraylist", sell_linesList);
                        nextFrag.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.rl_sale_layout, nextFrag).addToBackStack(null).commit();
//                        activeCenterFragments.add(nextFrag);

                        if (subFrom.equalsIgnoreCase("fromRepairSaleActivity")) {
                        } else {
                            HomeActivity.tool_title.setText("RETURN SALE");
                        }
//                        openFragment(new ReturnSaleFragment());
                        //   } else {
                        //       Toast.makeText(getContext(), "Partial payment sale cannot be return. Please complete your payment.", Toast.LENGTH_LONG).show();
                        //   }


//
                        break;

                    case R.id.invoice_delete:

                        deleteSaleDetail();

                        break;

                    case R.id.invoice_new_sale_notification:

                        sendNewSaleNotification();

                        break;
                    case R.id.invoice_Print:

                        openPrinterDialog("Sale");

                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    // choose which machine to use for printer

    public void openPrinterDialog(final String strFor) {

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
                bluetoothPrint(strFor,"forBluetooth");

            }
        });

        txt_print_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                bluetoothPrint(strFor,"forNewDevice");

                if (null == mPrinterController) {
                    mPrinterController = PrinterController.getInstance(getContext());
                }
                BatteryManager batteryManager = (BatteryManager) getContext().getSystemService(BATTERY_SERVICE);
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//                        Toast.makeText(getContext(), "Battery " + battery, Toast.LENGTH_SHORT).show();

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
                    bluetoothPrint(strFor,"forNewDevice");
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
//                        printProductCopy(strFor);
//                    }
//                });
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
//
//                                Toast.makeText(getContext(), "Connect Success", Toast.LENGTH_SHORT).show();
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
//                        }
//                        else {
//                            Toast.makeText(getContext(), "Connect Failure", Toast.LENGTH_SHORT).show();
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
////                        openflg = true;
//                                printProductForNewPOS(strFor, newPrinter);
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


    public boolean goToCartScreen(String editOrExchange) {
        try {
            //create cart products array.
            ArrayList<SearchItem> arrCartProducs = new ArrayList<SearchItem>();
            if (sell_linesList.size() > 0) {
                for (int n = 0; n < sell_linesList.size(); n++) {
                    JSONObject objSellLine = (JSONObject) sell_linesList.get(n);

                    JSONObject objProduct = objSellLine.getJSONObject("product");
                    if(objProduct.has("enable_stock") && !objProduct.isNull("enable_stock")){
                        productOrService=objProduct.getInt("enable_stock");
                    }


                    SearchItem objNewSearch = null;
                    for (int j = 0; j < arrOrignalProdList.size(); j++) {
                        SearchItem objTemp = arrOrignalProdList.get(j);

                        if (String.valueOf(objProduct.getInt("id")).equalsIgnoreCase(objTemp.getProduct_id())) {
                            objNewSearch = objTemp;
                            break;
                        }
                    }

                    if (objNewSearch == null) {
                        Toast.makeText(getContext(), "Unable To Edit, Because of Incomplete Sale Detail", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    objNewSearch.setActiveProduct(true);
                    objNewSearch.setCalculationDone(false);

                    //now fetching selling price from sell line object
                    if (objSellLine.has("unit_price_before_discount") && !objSellLine.isNull("unit_price_before_discount")) {
                        objNewSearch.setSelling_price(objSellLine.getString("unit_price_before_discount"));
                    }

                    objNewSearch.setSellLineId(String.valueOf(objSellLine.getInt("id")));
                    objNewSearch.setSellLineNote(objSellLine.getString("sell_line_note"));


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


                    if (objProduct != null) {
                        objNewSearch.setEnable_stock(String.valueOf(objProduct.getInt("enable_stock")));
                    }

                    objNewSearch.setFinalPrice(String.valueOf(objSellLine.getInt("quantity") * Double.valueOf(objSellLine.getString("unit_price"))));
                    objNewSearch.setQuantity(objSellLine.getInt("quantity"));


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
                    objNewSearch.setUnitFinalPrice(objSellLine.getString("unit_price_inc_tax"));

                    Double dTotalVariationPrice = 0.0;
                    String strSelectedVariationName = "";

                    objNewSearch.setVariationPrice(dTotalVariationPrice);

                    if (objSellLine.has("variations") && !objSellLine.isNull("variations")) {
                        JSONObject objVaria = objSellLine.getJSONObject("variations");

//                        if (arrSelectedProductModifier.size() == 0) {
                            objNewSearch.setVariation(objVaria.getString("name"));

                            if (objVaria.has("color") && !objVaria.isNull("color")) {
                                JSONObject objColor = objVaria.getJSONObject("color");

                                objNewSearch.setColor(objColor.getString("name"));
//                            }
                        }

                        if (objVaria.has("variation_location_details") && !objVaria.isNull("variation_location_details")) {
                            JSONArray arrVariationLocDetails = objVaria.getJSONArray("variation_location_details");

                            for (int j = 0; j < arrVariationLocDetails.length(); j++) {
                                JSONObject obj = arrVariationLocDetails.getJSONObject(j);

                                if (obj.has("location_id") && !obj.isNull("location_id")) {
                                    if (obj.getInt("location_id") == idLocationId) {
                                        objNewSearch.setQty_available(obj.getString("qty_available"));
                                        break;
                                    }
                                }
                            }

                            if (arrVariationLocDetails.length() == 0) {
                                objNewSearch.setQty_available("1.0");
                            }

                        } else {
                            objNewSearch.setQty_available("1.0");
                        }
                    }


                    objNewSearch.setVariation_name(strSelectedVariationName);

                    arrCartProducs.add(objNewSearch);

                }

            } else {
                Toast.makeText(getContext(), "Incomplete Sale Detail. Can't Go For Edit", Toast.LENGTH_LONG).show();
                return true;
            }

            //set cart count.
            ed_countproduct.putString("countt", String.valueOf(arrCartProducs.size()));
            ed_countproduct.commit();

            JSONObject sell = getSalesDetailResponse.getJSONObject("sell");
//                        showEditDialog();
            Intent intent = new Intent(getActivity(), ActivityPosItemList.class);


            BusinessLocationData businessLocationData = new BusinessLocationData();
            businessLocationData.setId(String.valueOf(sell.getInt("location_id")));
            if (sell.has("location") && !sell.isNull("location")) {
                JSONObject objLocation = sell.getJSONObject("location");
                if (objLocation.getString("name") != null) {
                    businessLocationData.setName(objLocation.getString("name"));
                }
            }
            intent.putExtra("location", businessLocationData);
            if (editOrExchange.equalsIgnoreCase("forEdit")) {
                intent.putExtra("editOrExchnage", "forEdit");
            } else {
                intent.putExtra("editOrExchnage", "forExchange");
            }

            ArrayList<Taxes> taxes = new ArrayList();
//
//                            intent.putExtra("tax", taxes);

            if (getSalesDetailResponse.has("order_taxes") && !getSalesDetailResponse.isNull("order_taxes")) {
                if (sell.has("tax") && !sell.isNull("tax")) {

                    JSONObject tax = sell.getJSONObject("tax");

                    Taxes taxObj = new Taxes();
                    taxObj.setId(String.valueOf(tax.getInt("id")));
                    taxObj.setName(tax.getString("name"));
                    taxObj.setAmount(String.valueOf(tax.getInt("amount")));

                    taxes.add(taxObj);
                }
            }
            intent.putExtra("tax", taxes);


            CurriencyData currencyData = new CurriencyData();
            currencyData.setId(sell.getString("currency"));
            currencyData.setName(sell.getString("currency"));
            intent.putExtra("currency", currencyData);

            CustomerListData customerListData = new CustomerListData();
            if (sell.has("contact") && !sell.isNull("contact")) {
                JSONObject contact = sell.getJSONObject("contact");

                customerListData.setId(contact.getInt("id"));
                customerListData.setName(contact.getString("name"));
                customerListData.setMobile(contact.getString("mobile"));
                customerListData.setLandmark(contact.getString("landmark"));
                customerListData.setCity(contact.getString("city"));
                customerListData.setState(contact.getString("state"));

                ed_selectedcustomer.putString("customernamee", customerListData.getName());
                ed_selectedcustomer.commit();
            } else {

                ed_selectedcustomer.putString("customernamee", "Cash Customer");
                ed_selectedcustomer.commit();
            }
            intent.putExtra("customer", customerListData);

            intent.putExtra("comingFrom", "fromSaleDetail");

            // check for the sale is suspended sale or not
            if (sell.getString("is_suspend").equalsIgnoreCase("1")) {
                intent.putExtra("parentfrom", "fromListDraftFragment");
            } else {
                intent.putExtra("parentfrom", comingFrom);
            }

            intent.putExtra("from", "fromFragment");
            intent.putExtra("transactionId", String.valueOf(transactionId));

            if (sell.getString("order_type") != null) {
                intent.putExtra("selected_ordertype", sell.getString("order_type"));
            } else {
                intent.putExtra("selected_ordertype", "");
            }

            //fetch table id from sell object.
            if (sell.getString("res_table_id") != null) {
                intent.putExtra("tablee_id", sell.getString("res_table_id"));
            } else {
                intent.putExtra("tablee_id", "");
            }

            //fetch waiter id from sell object.
            if (sell.getString("res_waiter_id") != null) {
                intent.putExtra("waiterr_id", sell.getString("res_waiter_id"));
            } else {
                intent.putExtra("waiterr_id", "");
            }

            //need tax data , location data from sandi.

            intent.putExtra("from", "fromSaleDetailAddCardItem");
            intent.putExtra("productOrService", productOrService);


            Gson gson = new Gson();
            //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
            ed_cartSave.putString("myCart", gson.toJson(arrCartProducs));
            ed_cartSave.commit();

            startActivity(intent);

            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }


    public void deleteSaleDetail() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Are you sure you want to Delete this Sale?");
        builder1.setTitle("Delete Sale Detail");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteSale();

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

    public void deleteSale() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deletesalesDetail("pos/delete-sale", transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("delete sale ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);


                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();

                                if (comingFrom.equalsIgnoreCase("fromListDraftFragment")) {

//                                        if (getFragmentManager().getBackStackEntryCount() != 0) {
//                                            getFragmentManager().popBackStack();
//                                            HomeActivity.tool_title.setText("LIST DRAFTS");
//                                        }
                                    ListDraftFragment ldf = new ListDraftFragment();
                                    FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.rl_sale_layout, ldf).commit();
                                    HomeActivity.tool_title.setText("LIST DRAFTS");
                                    Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
//                                    activeCenterFragments.add(ldf);

                                } else if (comingFrom.equalsIgnoreCase("fromQuatationListFragment")) {

//                                        if (getFragmentManager().getBackStackEntryCount() != 0) {
//                                            getFragmentManager().popBackStack();
//                                            HomeActivity.tool_title.setText("LIST QUATATION");
//                                        }

                                    QuatationListFragment ldf = new QuatationListFragment();
                                    FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left).replace(R.id.rl_sale_layout, ldf).commit();
                                    HomeActivity.tool_title.setText("LIST QUATATION");
                                    Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
//                                    activeCenterFragments.add(ldf);

                                } else {

                                    if (subFrom.equalsIgnoreCase("fromRepairSaleActivity")) {

                                    } else {
                                        if (getActivity().getFragmentManager().getBackStackEntryCount() != 0) {
                                            getActivity().getFragmentManager().popBackStack();
                                            HomeActivity.tool_title.setText("LIST POS SALE");
                                        }
                                        Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();
//                                        ListPosFragment ldf = new ListPosFragment();
//                                        getFragmentManager().beginTransaction().replace(R.id.rl_sale_layout, ldf).commit();
//                                        HomeActivity.tool_title.setText("LIST POS SALE");
                                    }

                                }
                            } else {
                                AppConstant.showToast(getContext(), "" + msg);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "pos/delete-sale API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void salesDetail() {

        AppConstant.showProgress(getContext(), false);
//          progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("SaleDetailtransactionId", String.valueOf(transactionId));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getsalesDetail("sales/" + transactionId+"?id_type="+id_type);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();

                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);

                            getSalesDetailResponse = responseObject;
                            Log.e("Sales Detail", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();
                                JSONObject sell = getSalesDetailResponse.getJSONObject("sell");

                                if (getSalesDetailResponse.has("sell") && !getSalesDetailResponse.isNull("sell")) {
                                    idLocationId = sell.getInt("location_id");
                                }
                                if(sell.has("id") && !sell.isNull("id")){
                                    transactionId= String.valueOf(sell.getInt("id"));
                                }

                                if (sell.has("delivery_order_status") && !sell.isNull("delivery_order_status")) {
                                    strDeliveryStatus = sell.getString("delivery_order_status");
                                    if (strDeliveryStatus.equalsIgnoreCase("picked_up")) {
                                        txt_DeliveryStatus.setText("Picked Up");
                                    } else {
                                        txt_DeliveryStatus.setText(strDeliveryStatus);
                                    }

                                }

                                invoice_no = sell.getString("invoice_no");
                                transaction_date = sell.getString("transaction_date");
                                payment_status = sell.getString("payment_status");

                                JSONObject objLocation = new JSONObject();
                                if (sell.has("location") && !sell.isNull("location")) {
                                    objLocation = sell.getJSONObject("location");

                                    String strAddress = "";

                                    if (objLocation.has("city") && !objLocation.isNull("city")) {
                                        strAddress = strAddress + objLocation.getString("city") + ",";
                                    }

                                    if (objLocation.has("state") && !objLocation.isNull("state")) {
                                        strAddress = strAddress + objLocation.getString("state") + ",\n";
                                    }
                                    if (objLocation.has("country") && !objLocation.isNull("country")) {
                                        strAddress = strAddress + objLocation.getString("country") +",";

                                    }
                                    if (objLocation.has("zip_code") && !objLocation.isNull("zip_code")) {
                                        strAddress = strAddress + objLocation.getString("zip_code");
                                    }
                                    strLocationAddress = strAddress;
                                    txt_DetailAddress.setText(strLocationAddress);

                                    if (objLocation.has("mobile") && !objLocation.isNull("mobile")) {
                                        strLocationMobile = objLocation.getString("mobile");

                                    }


                                }

                                txt_DetailLocation.setText(objLocation.getString("name"));

                                if (getSalesDetailResponse.has("receipt") && !getSalesDetailResponse.isNull("receipt")) {
                                    objReceipt = getSalesDetailResponse.getJSONObject("receipt");
                                    if(objReceipt.has("customer_info") && !objReceipt.isNull("customer_info")){
                                        customerNameAddress=objReceipt.getString("customer_info");
                                    }
                                    if(objReceipt.has("business_name") && !objReceipt.isNull("business_name")){
                                        business_name=objReceipt.getString("business_name");
                                    }
                                    if(objReceipt.has("payments") && !objReceipt.isNull("payments")){
                                        JSONArray payment_lines = objReceipt.getJSONArray("payments");
                                        for (int j = 0; j < payment_lines.length(); j++) {
                                            paymentLinesList.add(payment_lines.getJSONObject(j));

                                        }

                                    }

                                    if(objReceipt.has("change_payments") && !objReceipt.isNull("change_payments")){
                                        JSONArray payment_lines = objReceipt.getJSONArray("change_payments");
                                        for (int j = 0; j < payment_lines.length(); j++) {
                                            paymentLinesList.add(payment_lines.getJSONObject(j));

                                        }

                                    }
                                }

                                //set order tax
                                if (sell.has("tax") && !sell.isNull("tax")) {
                                    JSONObject objSellTex = sell.getJSONObject("tax");

                                    if (objSellTex.getString("name") != null) {
                                        strOrderTaxName = objSellTex.getString("name");

                                        if (getSalesDetailResponse.has("order_taxes") && !getSalesDetailResponse.isNull("order_taxes")) {
                                            JSONObject objOrderTex = getSalesDetailResponse.getJSONObject("order_taxes");

                                            if (objOrderTex.getString(strOrderTaxName) != null) {
                                                strOrderTaxAmount = objOrderTex.getString(strOrderTaxName);
                                            }
                                        }
                                    }
                                }
                                //end order tax


                                if (sell.has("contact") && !sell.isNull("contact")) {
                                    JSONObject contact = sell.getJSONObject("contact");
                                    customer_name = contact.getString("name");


//
                                    String strAddress = "";

                                    if (contact.has("city") && !contact.isNull("city")) {
                                        strAddress = strAddress + contact.getString("city") + ",";
                                    }

                                    if (contact.has("state") && !contact.isNull("state")) {
                                        strAddress = strAddress + contact.getString("state") + ",";
                                    }
                                    if (contact.has("country") && !contact.isNull("country")) {

                                        strAddress = strAddress + contact.getString("country");

                                    }
//                                    strLocationAddress = strAddress;
                                    txt_DetailAddress.setText(strAddress);
                                }

                                JSONArray sell_lines = sell.getJSONArray("sell_lines");
//                                JSONArray payment_lines = sell.getJSONArray("payment_lines");

                                String staff_note = sell.getString("staff_note");
                                invoiceId = sell.getString("invoice_no");

                                if (staff_note != null && !staff_note.equalsIgnoreCase("null")) {
                                    txt_staff_note.setText(staff_note);
                                } else {
                                    txt_staff_note.setText("None");
                                }

                                for (int i = 0; i < sell_lines.length(); i++) {
                                    sell_linesList.add(sell_lines.getJSONObject(i));
                                    JSONObject data = sell_lines.getJSONObject(i);

                                }

                                productDetailFromIds();//fetch product details as same as in search product.

//                                for (int j = 0; j < payment_lines.length(); j++) {
//                                    paymentLinesList.add(payment_lines.getJSONObject(j));
//
//                                }
                                String sale_note = "";

                                if (sell.has("additional_notes") && !sell.isNull("additional_notes")) {

                                    sale_note = sell.getString("additional_notes");
                                    if (!sale_note.isEmpty()) {
                                        sale_note = sell.getString("additional_notes");
                                        txt_sale_note.setText(sale_note);
                                    } else {
                                        txt_sale_note.setText("None");
                                    }

                                } else {

                                    txt_sale_note.setText("None");
                                }

//                                currencyType=arrexchangeRateList.get(0).getCurrency_code();
//                                currencyType = sell.getString("currency");
                                productDetailAdapter = new ProductDetailAdapter(getContext(), sell_linesList);
                                recyc_ProductDetail.setAdapter(productDetailAdapter);

                                paymentLinesAdapter = new PaymentLinesAdapter(getContext(), paymentLinesList);
                                recycler_PaymentLines.setAdapter(paymentLinesAdapter);


                                if (sell.has("total_before_tax") && !sell.isNull("total_before_tax")) {

                                    strGrandSubtotal = sell.getString("total_before_tax");

                                    if (!strGrandSubtotal.isEmpty()) {

                                        strGrandSubtotal = sell.getString("total_before_tax");

                                    } else {
                                        strGrandSubtotal = "0.0";

                                    }
                                } else {

                                    strGrandSubtotal = "0.0";
                                }


                                if (sell.has("discount_type") && !sell.isNull("discount_type")) {

                                    if (sell.getString("discount_type").equalsIgnoreCase("percentage")) {


                                        if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {

                                            strDiscountAmount = sell.getString("discount_amount");

                                            if (!strDiscountAmount.isEmpty()) {

                                                strDiscountAmount = sell.getString("discount_amount");

                                            } else {

                                                strDiscountAmount = "0";
                                            }
                                            Float fDiscountAmount = Float.parseFloat(strDiscountAmount);
                                            Float fGrandSubTotal = Float.parseFloat(strGrandSubtotal);

                                            Float finalDiscount = (fDiscountAmount / 100) * fGrandSubTotal;

                                            dis = String.valueOf(finalDiscount);

                                            strDiscountType = "Percentage";
                                        }

                                    } else if (sell.getString("discount_type").equalsIgnoreCase("fixed")) {

                                        if (sell.has("discount_amount") && !sell.isNull("discount_amount")) {

                                            dis = sell.getString("discount_amount");
                                            strDiscountAmount = dis;

                                            if (!dis.isEmpty()) {
                                                dis = sell.getString("discount_amount");
                                            } else {
                                                dis = "0";
                                            }

                                        }

                                        strDiscountType = "Fixed";

                                    } else {
                                        strDiscountType = "";
                                    }
                                }

                                txt_totaldiscount.setText("- " + currencyType + String.format("%.2f", Double.valueOf(dis)));


                                txt_total.setText(currencyType + sell.getString("total_before_tax"));
                                str_shipping = sell.getString("shipping_charges");
                                txt_OrderTax.setText("+ " + currencyType + sell.getString("tax_amount"));
                                txt_ShippingCharges.setText("+ " + currencyType + sell.getString("shipping_charges"));
                                txt_FinalTotal.setText(currencyType + sell.getString("final_total"));

                                if (objReceipt != null) {
                                    if (objReceipt.has("total_paid") && !objReceipt.isNull("total_paid")) {
                                        total_paid = objReceipt.getString("total_paid").trim();
                                    }

                                } else if (sell.has("total_amount_recovered") && !sell.isNull("total_amount_recovered")) {
                                    total_paid = sell.getString("total_amount_recovered").trim();
                                }

                                if (total_paid != null && !total_paid.equalsIgnoreCase("null") && !total_paid.equalsIgnoreCase("")) {
                                    txt_TotalPaid.setText(currencyType + String.format("%.2f", Double.valueOf(total_paid)));
                                    str_Paid = total_paid;
                                } else {
                                    txt_TotalPaid.setText(currencyType + "0.00");
                                    str_Paid = "0.00";
                                }
                                payable = sell.getString("final_total");
                                remaining = String.format("%.2f", Float.valueOf(payable) - Float.valueOf(str_Paid));
                                txt_TotalRemaining.setText(currencyType + remaining);

                                txt_DetailCustomerName.setText(customer_name);
                                txt_DetailInvoice.setText("Sale Details(Invoice No.: " + invoiceId + ")");

                                if (payment_status != null && !payment_status.equalsIgnoreCase("null")) {
                                    if (payment_status.equalsIgnoreCase("picked_up")) {
                                        txt_PaymentStatus.setText("Picked Up");
                                    } else {
                                        txt_PaymentStatus.setText(payment_status);
                                    }

                                } else {
                                    txt_PaymentStatus.setText("");
                                }
                                String orderDate = transaction_date;
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

                                txt_DetailDates.setText(part1 + "  |  " + part2);

                                if (getSalesDetailResponse.has("shipping_address") && !getSalesDetailResponse.isNull("shipping_address")) {
                                    String str_ShippingAddress = "";
//                                    if(getSalesDetailResponse.getJSONArray("shipping_address").length() ==0){
//                                        AppConstant.showToast(getContext(),"Empty Array");
//                                    }
//                                    else{
                                    JSONObject objShippingAddres = null;
                                    if (getSalesDetailResponse.has("objShippingAddres") && !getSalesDetailResponse.isNull("objShippingAddres")) {
                                        objShippingAddres = getSalesDetailResponse.getJSONObject("shipping_address");

                                    }
                                    if (objShippingAddres.has("delivery_name") && !objShippingAddres.isNull("delivery_name")) {
                                        str_ShippingAddress = objShippingAddres.getString("delivery_name");
                                    }
                                    if (objShippingAddres.has("delivery_street_address") && !objShippingAddres.isNull("delivery_street_address")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_street_address");
                                    }
                                    if (objShippingAddres.has("delivery_area") && !objShippingAddres.isNull("delivery_area")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_area");
                                    }
                                    if (objShippingAddres.has("delivery_city") && !objShippingAddres.isNull("delivery_city")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_city");
                                    }
                                    if (objShippingAddres.has("delivery_state") && !objShippingAddres.isNull("delivery_state")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_state");
                                    }
                                    if (objShippingAddres.has("delivery_country") && !objShippingAddres.isNull("delivery_country")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_country");
                                    }
                                    if (objShippingAddres.has("delivery_postcode") && !objShippingAddres.isNull("delivery_postcode")) {
                                        str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_postcode");
                                    }
//                                    }

//                                str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getInt("delivery_area_id");
//                                str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getInt("delivery_state_id");
//                                str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getInt("delivery_country_id");
//                                str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getInt("delivery_city_id");
//                                str_ShippingAddress = str_ShippingAddress + "," + objShippingAddres.getString("delivery_suburb");

                                    if (str_ShippingAddress.isEmpty()) {
                                        card_shipping_address.setVisibility(View.GONE);
                                    } else {
                                        card_shipping_address.setVisibility(View.VISIBLE);
                                        txt_ShippingAddress.setText(str_ShippingAddress);
                                    }
                                }
                            }

                        } else {
                            AppConstant.sendEmailNotification(getContext(), "sales/view API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }


    }

    //get product sale detail api response

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
            obj.put("location_id", idLocationId);

            Gson gson = new Gson();
            json = gson.toJson(obj);
            System.out.printf("JSON: %s", json);

            // requestJsonParameters.put("products",arrProducts);
            // requestJsonParameters.put("location_id",String.valueOf(idLocationId));

        } catch (JSONException e) {
            e.printStackTrace();
        }


//          AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

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


                            arrOrignalProdList = response.body().getProduct_list();

                        } else {
                            Toast.makeText(getContext(), "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(getContext(), "get-products-modifiers API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(getContext(), "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }


                @Override
                public void onFailure(Call<SearchItemResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Unable To Fetch Product Detail. Please Try Again", Toast.LENGTH_LONG).show();
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



    public void sendNewSaleNotification() {
        newSaleNotificationDialog = new Dialog(getContext());
        newSaleNotificationDialog.setContentView(R.layout.new_sale_notification_dialog);
        newSaleNotificationDialog.setCancelable(false);
        Window window = newSaleNotificationDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView txt_send_notification = newSaleNotificationDialog.findViewById(R.id.txt_send);
        TextView txt_cancel_dialog = newSaleNotificationDialog.findViewById(R.id.txt_cancel_send);

        //Checbox

        ch_send_both = newSaleNotificationDialog.findViewById(R.id.ch_send_both);
        ch_send_sms = newSaleNotificationDialog.findViewById(R.id.ch_send_sms);
        ch_send_email = newSaleNotificationDialog.findViewById(R.id.ch_send_email);

        sendNotificationType = "Send Email Only";
        ch_send_email.setChecked(true);


        txt_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSaleNotificationDialog.dismiss();
                sendNotificationType = "";
            }
        });

        txt_send_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppConstant.isNetworkAvailable(getContext())) {
                    sendNotification(sendNotificationType);
                } else {
                    AppConstant.openInternetDialog(getContext());
                }
                newSaleNotificationDialog.dismiss();

            }
        });

        ch_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationType = "Send Email Only";
                ch_send_email.setChecked(true);
                ch_send_sms.setChecked(false);
                ch_send_both.setChecked(false);
            }
        });

        ch_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationType = "Send SMS Only";
                ch_send_email.setChecked(false);
                ch_send_sms.setChecked(true);
                ch_send_both.setChecked(false);
            }
        });
        ch_send_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationType = "Send Both Email & SMS";
                ch_send_email.setChecked(false);
                ch_send_sms.setChecked(false);
                ch_send_both.setChecked(true);
            }
        });
        newSaleNotificationDialog.show();


    }


    public void openRegisterDialog() {


        openRegisterDialog = new Dialog(getContext());
        openRegisterDialog.setContentView(R.layout.open_register_items);
        openRegisterDialog.setCancelable(false);
        Window window = openRegisterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
//                window.setGravity(Gravity.CENTER);

        TextView txt_open_register = openRegisterDialog.findViewById(R.id.txt_open_register);
        TextView txt_cancel_register = openRegisterDialog.findViewById(R.id.txt_cancel_register);
        et_register_amount = openRegisterDialog.findViewById(R.id.et_register_amount);

        openRegisterDialog.show();

        txt_cancel_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterDialog.dismiss();
            }
        });


        et_register_amount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            int beforeDecimal = 7;
            int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = et_register_amount.getText().toString();
                String temp = et_register_amount.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = et_register_amount.getSelectionStart();
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
        txt_open_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_register_amount.getText().toString().isEmpty()) {
                    AppConstant.showToast(getContext(), "Please Enter Amount");
                } else {

                    openRegisterApiCall(); //here we are calling the api to open register
                }

            }
        });

    }

    public void openRegisterApiCall() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("amount", et_register_amount.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.openRegister("cash-register", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            Log.e("open Register", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                AppConstant.hideKeyboardFrom(getContext());
                                openRegisterDialog.dismiss();
                                AppConstant.showToast(getContext(), "Register Opened Successfully");
                                boolean bCheck = goToCartScreen("forEdit");// here next go to positem list screen
//                                img_close_register.setVisibility(View.VISIBLE);

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "cash-register API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getActivity(), "Could Not Open Register. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Open Register. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    public void sendNotification(String notification_type) {

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("template_for", "new_sale");
                jsonObject.put("notification_type", notification_type);

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
                            Log.e("Email Send Notification", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {

                                Toast.makeText(getContext(), "Notification Sent Successfully", Toast.LENGTH_LONG).show();
                            }
                        } else {

                            AppConstant.sendEmailNotification(getContext(), "notification/send API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Send Notification. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Send Email. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected void bluetoothPrint(String strFor,String forDevice) {

        String BILL = "";

        BILL = " ----- " + strFor + " Copy ----\n" +
                "\n " +
                " " +business_name+"\n\n"+
                " " +strLocationAddress + "\n" +
                " " + "Mobile :" + strLocationMobile + "\n\n";
        BILL = BILL
                + " " + "Invoice No : " + invoice_no + " \n"
                + " " +customerNameAddress + " \n"
                + " " + "Date  : " + part1 + " , " + part2 + " \n\n"

                + "--------------------------------\n";
        //set currency type from receipt object.
        String strCurrencyType = "";
        if (objReceipt != null) {
            if (objReceipt.has("currency") && !objReceipt.isNull("currency")) {
                try {
                    JSONObject objCurrency = objReceipt.getJSONObject("currency");

                    strCurrencyType = objCurrency.getString("symbol");

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppConstant.showToast(getContext(), e.toString());
                }
            }
        }
        //end.

        BILL = BILL + "            Product           \n"
                + "--------------------------------";

        for (int i = 0; i < sell_linesList.size(); i++) {
            sellItem = (JSONObject) sell_linesList.get(i);

            try {

                try {
                    if (sellItem.has("product") && !sellItem.isNull("product")) {

                        JSONObject sellProduct = (JSONObject) sellItem.getJSONObject("product");
                        JSONObject sellVariation = (JSONObject) sellItem.getJSONObject("variations");
                        String productNAme = sellProduct.getString("name");

                        String strProductName = ""; // combine all products name , modifiers name etc.
                        Float fModifierTotalPrice = 0.0f; // get item total additional modifiers price and then add to product sub total.

                        strProductName = strProductName + sellProduct.getString("name");


                        if (sellItem.getJSONArray("modifiers") != null) {
                            JSONArray arrModifiers = sellItem.getJSONArray("modifiers");
                            for (int j = 0; j < arrModifiers.length(); j++) {
                                JSONObject objModifier = arrModifiers.getJSONObject(j);

                                if (objModifier.getJSONObject("product") != null) {
                                    JSONObject objProduct = objModifier.getJSONObject("product");

                                    if (objProduct.getString("name") != null) {
                                        if (j == 0) {
                                            strProductName = strProductName + " - " + objProduct.getString("name");
                                        } else {
                                            strProductName = strProductName + objProduct.getString("name");
                                        }

                                    }
                                }

                                if (objModifier.getJSONObject("variations") != null) {
                                    JSONObject objVariation = objModifier.getJSONObject("variations");

                                    if (objVariation.getString("name") != null) {
                                        strProductName = strProductName + "(" + objVariation.getString("name");

                                        if (objVariation.getString("sell_price_inc_tax") != null) {
                                            fModifierTotalPrice = fModifierTotalPrice + Float.valueOf(objVariation.getString("sell_price_inc_tax"));

                                            strProductName = strProductName + "-" + SaleDetailFragment.currencyType + objVariation.getString("sell_price_inc_tax");
                                        }

                                        strProductName = strProductName + ")";
                                    }
                                }

                                if (j == arrModifiers.length() - 1) {

                                } else {
                                    strProductName = strProductName + ", ";
                                }
                            }


                            //if user not logged with Food & Beverage.
                            if (arrdepartmentsList.contains("1")) {//electronics

                                if (!sellProduct.getString("type").equalsIgnoreCase("single")) //using negation , type = single means this product have no variant.
                                {
                                    if (arrModifiers.length() == 0) {
                                        if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                                            JSONObject objVariation = sellItem.getJSONObject("variations");

                                            //if name not equal to dummy only then show variation.
                                            if (!objVariation.getString("name").equalsIgnoreCase("DUMMY")) {

                                                JSONObject objProductVariation = objVariation.getJSONObject("product_variation");

                                                strProductName = strProductName + " (" + objProductVariation.getString("name") + ":" + objVariation.getString("name") + ")";
                                            }
                                        }
                                    }
                                }

                            } else if (arrdepartmentsList.contains("2")) {//fashions
                                if (arrModifiers.length() == 0) {
                                    if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                                        JSONObject objVariation = sellItem.getJSONObject("variations");

                                        //if name not equal to dummy only then show variation.
                                        if (!objVariation.getString("name").equalsIgnoreCase("DUMMY")) {

                                            JSONObject objProductVariation = objVariation.getJSONObject("product_variation");

                                            strProductName = strProductName + " (" + objProductVariation.getString("name") + ":" + objVariation.getString("name");

                                            if (objVariation.has("color") && !objVariation.isNull("color")) {
                                                JSONObject objColor = objVariation.getJSONObject("color");

                                                if (objColor.has("name") && !objColor.isNull("name")) {
                                                    strProductName = strProductName + ", Color:" + objColor.getString("name") + ")";
                                                }
                                            } else {
                                                strProductName = strProductName + ")";
                                            }
                                        }
                                    }
                                }
                            } else {

                            }
                        }


                        if (sellItem.has("variations") && !sellItem.isNull("variations")) {
                            BILL = BILL + "\n" + strProductName + " , " + sellVariation.getString("sub_sku");
                        } else {
                            BILL = BILL + "\n" + strProductName;
                        }

                    }
                } catch (Exception e) {
                }
                BILL = BILL + "\n\n" + "Quantity  :  " + sellItem.getString("quantity") + " Pc(s)";
                BILL = BILL + "\n\n" + "Unit Price  :  " + sellItem.getString("unit_price_before_discount");

                double qQuantity = Double.valueOf(sellItem.getString("quantity"));
                double qUnityPrice = Double.valueOf(sellItem.getString("unit_price_before_discount"));
                double dProductTotal = qQuantity * qUnityPrice;

                //discount show if come
                String strProductDisountAmount = "0.0";
                String strDiscountFinalAmount = "0.0";
                if (sellItem.has("line_discount_amount") && !sellItem.isNull("line_discount_amount")) {
                    strProductDisountAmount = sellItem.getString("line_discount_amount");
                }
                Float fDiscountAMount = Float.valueOf(strProductDisountAmount);

                if (fDiscountAMount > 0) {
                    if (sellItem.getString("line_discount_type").equalsIgnoreCase("percentage")) {

                        Double dDiscountAmount = Double.valueOf(strProductDisountAmount);

                        Double finalDiscount = (dDiscountAmount / 100) * dProductTotal;

                        strDiscountFinalAmount = String.valueOf(finalDiscount);

                        BILL = BILL + "\n\n" + "Discount (" + sellItem.getString("line_discount_type") + " - " + strProductDisountAmount + "%)  :  (-) " + strCurrencyType + " " + strDiscountFinalAmount;

                    } else if (sellItem.getString("line_discount_type").equalsIgnoreCase("fixed")) {

                        strDiscountFinalAmount = strProductDisountAmount;
                        BILL = BILL + "\n\n" + "Discount (" + sellItem.getString("line_discount_type") + ")  :  (-) " + strCurrencyType + " " + strDiscountFinalAmount;
                    }

                }

//tax show if comes
                String strProductTaxAmount = "0.0";
                if (sellItem.has("item_tax") && !sellItem.isNull("item_tax")) {
                    strProductTaxAmount = sellItem.getString("item_tax");
                }
                Float fTaxAMount = Float.valueOf(strProductTaxAmount);

                if (fTaxAMount > 0) {
                    BILL = BILL + "\n\n" + "Tax   :  (+) " + strCurrencyType + " " + sellItem.getString("item_tax");
                }

                int quantity = sellItem.getInt("quantity");
                String price_inc_tax = sellItem.getString("unit_price_inc_tax");

                String quantyty1 = String.valueOf(quantity);
                String subtotal = String.valueOf(Float.valueOf(quantyty1) * (Float.valueOf(price_inc_tax)));
                BILL = BILL + "\n\n" + "Subtotal  :   " + String.format("%.2f", Double.valueOf(subtotal));

                BILL = BILL + "\n\n" + "-------------------------------";
//                if (i != sell_linesList.size() - 1) {
//                    BILL = BILL + "\n\n"+ "-------------------------------";
//                }


            } catch (Exception e) {

            }
        }


        BILL = BILL + "\n" + "Subtotal :" + strCurrencyType + " " + strGrandSubtotal;

        if (strDiscountType.equalsIgnoreCase("Percentage")) {
            if (strDiscountAmount.isEmpty()) {
                BILL = BILL + "\n\n" + "Discount (%) :  (-) " + strCurrencyType + " 0.0 \n";
            } else {
                BILL = BILL + "\n\n" + "Discount (" + strDiscountAmount + "%" + ")  :  (-) " + strCurrencyType + " " + String.format("%.2f", Double.valueOf(dis));
            }

        } else if (strDiscountType.equalsIgnoreCase("Fixed")) {
            BILL = BILL + "\n\n" + "Discount (Fixed)  :  (-) " + strCurrencyType + " " + String.format("%.2f", Double.valueOf(dis));
        } else {
            BILL = BILL + "\n\n" + "Discount  :  (-) " + strCurrencyType + " " + String.format("%.2f", Double.valueOf(dis));
        }

        if (!strOrderTaxAmount.isEmpty()) {
            BILL = BILL + "\n\n" + "Tax (" + strOrderTaxName + ")  :  (+) " + strCurrencyType + " " + strOrderTaxAmount;
        }

        if (!str_shipping.isEmpty() && Float.valueOf(str_shipping) > 0) {
            BILL = BILL + "\n\n" + "Shipping Charges  :  (+) " + strCurrencyType + " " + str_shipping;
        }
        BILL = BILL + "\n\n" + "-------------------------------";
        BILL = BILL + "\n\n" + "Grand Total   :  " + strCurrencyType + " " + payable;
        BILL = BILL + "\n\n" + "-------------------------------";
        BILL = BILL + "\n\n" + "Paid By   :";


        try {

            for (int i = 0; i < paymentLinesList.size(); i++) {

                JSONObject paymentLines = (JSONObject) paymentLinesList.get(i);

                if (paymentLines.getString("method") != null && paymentLines.getString("amount") != null) {
                    String date = paymentLines.getString("date");

                    SimpleDateFormat spf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
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
//                    BILL = BILL + "\n\n" + strMethodCapital + " " + strDate + "    :  " + strCurrencyType + " " + paymentLines.getString("amount");
                    BILL = BILL + "\n\n" + strMethodCapital  + "    :  " + strCurrencyType + " " + paymentLines.getString("amount");
                }
            }

        } catch (Exception e) {
            AppConstant.showToast(getContext(), e.toString());
        }
        if (objReceipt != null) {
            try {
                if (objReceipt.getString("total_paid_label") != null && objReceipt.getString("total_paid_amount") != null) {
                    BILL = BILL + "\n\n" + objReceipt.getString("total_paid_label") + "   :  " + strCurrencyType + " " + objReceipt.getString("total_paid_amount") + " (" + currencyType + ")";

                } else {
                    BILL = BILL + "\n\n" + objReceipt.getString("total_paid_label") + "   :  " + strCurrencyType + " 0 (" + currencyType + ")";
                }

                if (objReceipt.getString("total_due_amount") != null) {

                    BILL = BILL + "\n\n" + objReceipt.getString("total_due_label") + "   :  " + strCurrencyType + " " + objReceipt.getDouble("total_due_amount");

                } else {
                    BILL = BILL + "\n\n" + objReceipt.getString("total_due_label") + "   :  " + strCurrencyType + " 0 ";
                }


                if (objReceipt.getString("reward_earned") != null) {
                    String strRewardEarned = objReceipt.getString("reward_earned");
                    BILL = BILL + "\n\n" + "Reward Earned   :  " + strCurrencyType + " " + strRewardEarned.trim();

                } else {
                    BILL = BILL + "\n\n" + "Reward Earned   :  " + strCurrencyType + " 0";
                }


                if (objReceipt.getString("total_reward") != null) {
                    String strTotalReward = objReceipt.getString("total_reward");
                    BILL = BILL + "\n\n" + "Total Reward   :  " + strCurrencyType + " " + strTotalReward.trim();

                } else {
                    BILL = BILL + "\n\n" + "Total Reward   :  " + strCurrencyType + " 0";
                }


                if (objReceipt.getString("total_sc") != null) {
                    String strStoreCredit = objReceipt.getString("total_sc");
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
                    BitMatrix bitMatrix = multiFormatWriter.encode(invoice_no, BarcodeFormat.CODE_128,250, 70);
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

    public void deliveryAccepted() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deliveryOrder("orders/mark-as-accepted/" + transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("accept sale ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            if (successstatus.equalsIgnoreCase("1")) {
                                sell_linesList.clear();
                                salesDetail();
                            }
                            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();


                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "orders/mark-as-accepted API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void deliveryPickedUp() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deliveryOrder("orders/mark-as-picked-up/" + transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("accept sale ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            if (successstatus.equalsIgnoreCase("1")) {
                                sell_linesList.clear();
                                salesDetail();
                            }
                            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "orders/mark-as-picked-up API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void deliveryReturned() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deliveryOrder("orders/mark-as-returned/" + transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("return sale ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            if (successstatus.equalsIgnoreCase("1")) {
                                sell_linesList.clear();
                                salesDetail();
                            }
                            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();


                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "orders/mark-as-returned API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void deliveryDelivered() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("transactionId", String.valueOf(transactionId));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deliveryOrder("orders/mark-as-delivered/" + transactionId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("delivered sale ", response.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.getString("msg");
                            if (successstatus.equalsIgnoreCase("1")) {
                                sell_linesList.clear();
                                salesDetail();
                            }
                            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "orders/mark-as-delivered API", "(SaleDetailFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Delete Sale Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }

    public void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }


}