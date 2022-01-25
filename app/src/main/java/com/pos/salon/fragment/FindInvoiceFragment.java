package com.pos.salon.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList.strScannedBarCode;
import static com.pos.salon.activity.HomeActivity.tool_title;


public class FindInvoiceFragment extends Fragment {

    RelativeLayout lay_search_invoice,lay_scan_invoice;
    public boolean isSaleView;
    EditText et_searchInvoice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_invoice, container, false);
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

        tool_title.setText("FIND INVOICE");

        findViews(view);
    }
    public void findViews(View view){

        lay_search_invoice=view.findViewById(R.id.lay_search_invoice);
        lay_scan_invoice=view.findViewById(R.id.lay_scan_invoice);
        et_searchInvoice=view.findViewById(R.id.et_searchInvoice);



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
            isSaleView = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.view")) {
                    isSaleView = true;
                }

            }
        }

        listeners();

    }

    public void listeners(){

        lay_search_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_searchInvoice.getText().toString().isEmpty()){
                    AppConstant.showToast(getContext(),"Please Enter Invoice No.");
                }else{
                    if (isSaleView) {
                        String invoice=et_searchInvoice.getText().toString();
                        SaleDetailFragment ldf = new SaleDetailFragment();
                        Bundle args = new Bundle();
                        args.putString("transactionId",invoice);
                        Log.e("id",invoice);
                        args.putString("from", "fromListPosFragment");
                        args.putString("subFrom", "fromListPosFragment");
                        args.putInt("return_exists", 0);
                        args.putString("id_type","invoice");
                        ldf.setArguments(args);
                        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                        transaction.replace(R.id.lay_findInvoice, ldf);
//                        activeCenterFragments.add(ldf);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        et_searchInvoice.setText("");
//                    tool_title.setText("SALE DETAIL");
                    } else {
                        AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
                    }
                }

            }
        });

        lay_scan_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                strScannedBarCode = "";
                Intent intent = new Intent(getContext(), ScannerActivity.class);
                intent.putExtra("from", "findInvoice");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

//                final Dialog fromPrintDialog = new Dialog(getContext());
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
//                        Intent intent = new Intent(getContext(), ScanActivityDevice5.class);
//                        intent.putExtra("comingFrom", "searchActivity");
//                        startActivity(intent);
//                        et_searchInvoice.setText("");
//                    }
//                });
////                txt_device7.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        fromPrintDialog.dismiss();
////                        strScannedBarCode = "";
////                        Intent intent = new Intent(ActivitySearchItemList.this, ScanActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
////                    }
////                });
//
//                txt_printer_jeoPower.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        fromPrintDialog.dismiss();
////                        strScannedBarCode = "";
////                        Intent intent = new Intent(ActivitySearchItemList.this, CaptureActivity.class);
////                        intent.putExtra("comingFrom", "searchActivity");
////                        startActivity(intent);
////                        searchEditBox.setText("");
//
//
//                        strScannedBarCode = "";
//                        Intent intent = new Intent(getContext(), ScannerActivity.class);
//                        intent.putExtra("comingFrom", "findInvoice");
//                        startActivity(intent);
//
//                    }
//                });

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

   if(!strScannedBarCode.isEmpty()){
       if (isSaleView) {
           SaleDetailFragment ldf = new SaleDetailFragment();
           Bundle args = new Bundle();
           args.putString("transactionId", strScannedBarCode);
           args.putString("from", "fromListPosFragment");
           args.putString("subFrom", "fromListPosFragment");
           args.putInt("return_exists", 0);
           args.putString("id_type","invoice");
           ldf.setArguments(args);

           FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
           transaction.add(R.id.lay_findInvoice, ldf);
//           activeCenterFragments.add(ldf);
           transaction.addToBackStack(null);
           transaction.commit();
//                    tool_title.setText("SALE DETAIL");
           strScannedBarCode = "";
           et_searchInvoice.setText("");
       } else {
           AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
       }
   }


    }
}