package com.pos.salon.fragment.BookingFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivityPosItemList;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.BookingAdapter.BookigProductServiceAdapter;
import com.pos.salon.adapter.BookingAdapter.BookingListAdapter;
import com.pos.salon.adapter.LocationAdapter.LocationAdapter;
import com.pos.salon.adapter.ProductsAdapters.TagAdapter;
import com.pos.salon.adapter.SpinerItemAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BookingModels.BookingDetailModel;
import com.pos.salon.model.BookingModels.BookingListModel;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.posLocation.Taxes;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.pos.salon.activity.HomeActivity.isRegisterOpen;


public class BookingDetailFragment extends Fragment {

    RecyclerView recyc_ProductServiceDetail, recyc_Employee;
    BookigProductServiceAdapter bookigProductServiceAdapter;
    private final ArrayList<SearchItem> bookingProductList = new ArrayList<>();
    ImageView open_action;
    int booking_id = 0,transaction_id=0;
    TextView txt_booking_date, txt_booking_time, txt_booking_Status, txt_customerName, txt_CustomerEmail, txt_mobile;
    TextView txt_paymentMethod, txt_paymentStatus, txt_service_subtotal, txt_tax, txt_serviceTotal, txt_productTotal, txt_finalTotal;
    public String booking_status = "";
    private ArrayList<String> nameEmployeelist = new ArrayList<>();
    TagAdapter tagAdapter;
    private ArrayList<SpinnerModel> arrStatusList = new ArrayList<>();
    private JSONObject getSalesDetailResponse;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_detail, container, false);
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

        HomeActivity.tool_title.setText("BOOKING DETAIL");

        Bundle bundle = getArguments();
//        listPosModel = (ListPosModel) bundle.getSerializable("saleModel");
        if (bundle != null) {
            booking_id = bundle.getInt("booking_id", 0); // your selected transaction id.
        }

        findViewIds(view);
    }

    public void findViewIds(View view) {

        recyc_ProductServiceDetail = view.findViewById(R.id.recyc_ProductServiceDetail);
        open_action = view.findViewById(R.id.open_action);
        txt_booking_date = view.findViewById(R.id.txt_booking_date);
        txt_booking_time = view.findViewById(R.id.txt_booking_time);
        txt_booking_Status = view.findViewById(R.id.txt_booking_Status);
        txt_customerName = view.findViewById(R.id.txt_customerName);
        txt_CustomerEmail = view.findViewById(R.id.txt_CustomerEmail);
        txt_mobile = view.findViewById(R.id.txt_mobile);
        txt_paymentMethod = view.findViewById(R.id.txt_paymentMethod);
        txt_paymentStatus = view.findViewById(R.id.txt_paymentStatus);
        txt_service_subtotal = view.findViewById(R.id.txt_service_subtotal);
        txt_tax = view.findViewById(R.id.txt_tax);
        txt_serviceTotal = view.findViewById(R.id.txt_serviceTotal);
        txt_productTotal = view.findViewById(R.id.txt_productTotal);
        txt_finalTotal = view.findViewById(R.id.txt_finalTotal);
        recyc_Employee = view.findViewById(R.id.recyc_Employee);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyc_ProductServiceDetail.setLayoutManager(mLayoutManager);
        bookigProductServiceAdapter = new BookigProductServiceAdapter(getContext(), bookingProductList);
        recyc_ProductServiceDetail.setAdapter(bookigProductServiceAdapter);

        GridLayoutManager layoutManagerTag = new GridLayoutManager(getContext(), 2);
        recyc_Employee.setLayoutManager(layoutManagerTag);
        tagAdapter = new TagAdapter(getContext(), nameEmployeelist, "bookingDetail");
        recyc_Employee.setAdapter(tagAdapter);

        clickListeners();

    }

    public void clickListeners() {

        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        bookingDetail();

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
        popup.getMenuInflater().inflate(R.menu.menu_booking_items, popup.getMenu());
//        if (booking_status.equalsIgnoreCase("pending")) {
//            popup.getMenu().findItem(R.id.booking_cancel).setVisible(true);
//        } else {
//            popup.getMenu().findItem(R.id.booking_cancel).setVisible(false);
//        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.booking_edit:
                        goToCartScreen();
//                        BookingEditFragment ldf = new BookingEditFragment();
//                        Bundle args = new Bundle();
//                        args.putInt("booking_id", booking_id);
//                        ldf.setArguments(args);
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
//                        transaction.replace(R.id.rl_booking_detail_layout, ldf, "bookingEditFragment");
////                       activeCenterFragments.add(ldf);
//                        transaction.addToBackStack("bookingEditFragment");
//                        transaction.commit();
                        break;

                    case R.id.booking_change_status:

                        bookingStatusDialog();

                        break;

                }
                return true;
            }
        });
        popup.show();

    }

    public void bookingStatusDialog() {
        Dialog filterDialog = new Dialog(getContext());
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.booking_status_change_items);
        filterDialog.setCancelable(false);
        filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.CENTER);

        ImageView img_cancel_dialog = filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_change_status = filterDialog.findViewById(R.id.linear_change_status);
        Spinner dropdown_BookingStatus = filterDialog.findViewById(R.id.dropdown_BookingStatus);

        SpinerItemAdapter statusAdapter = new SpinerItemAdapter(getContext(), arrStatusList);
        dropdown_BookingStatus.setAdapter(statusAdapter);

        dropdown_BookingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                booking_status = arrStatusList.get(position).spinner_id;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int a = 0; a < arrStatusList.size(); a++) {
            if (booking_status.equalsIgnoreCase(arrStatusList.get(a).spinner_id)) {
                dropdown_BookingStatus.setSelection(a);
                break;
            }
        }
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        linear_change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                bookingStatusUpdate();
            }
        });

        filterDialog.show();
    }


    public void bookingDetail() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("bookings/" + booking_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            getSalesDetailResponse = responseObject;
                            nameEmployeelist.clear();
                            bookingProductList.clear();
                            arrStatusList.clear();
                            Log.e("booking detail", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                if (responseObject.has("status") && !responseObject.isNull("status")) {
                                    JSONArray arrayStatus = responseObject.getJSONArray("status");

                                    for (int a = 0; a < arrayStatus.length(); a++) {
                                        JSONObject locationdata = arrayStatus.getJSONObject(a);
                                        SpinnerModel statusMOdel2 = new SpinnerModel();
                                        if (locationdata.has("id") && !locationdata.isNull("id")) {
                                            statusMOdel2.spinner_id = locationdata.getString("id");
                                        }
                                        if (locationdata.has("name") && !locationdata.isNull("name")) {
                                            statusMOdel2.name = locationdata.getString("name");
                                        }
                                        arrStatusList.add(statusMOdel2);
                                    }
                                }

                                JSONObject transaction_detailsObj = responseObject.getJSONObject("transaction_details");
                                if(transaction_detailsObj.has("id") && !transaction_detailsObj.isNull("id")){
                                    transaction_id=transaction_detailsObj.getInt("id");
                                }

                                JSONObject dataObj = responseObject.getJSONObject("booking_details");

                                if (dataObj.has("date_time") && !dataObj.isNull("date_time")) {
                                    String date_time = dataObj.getString("date_time");
                                    String part1 = "";
                                    String part2 = "";
                                    if (date_time != null && !date_time.equalsIgnoreCase("")) {
                                        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date newDate = null;
                                        try {
                                            newDate = spf.parse(date_time);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        spf = new SimpleDateFormat("dd MMM");
                                        part1 = spf.format(newDate);
                                        spf = new SimpleDateFormat("hh:mm a");
                                        part2 = spf.format(newDate);

                                    }
                                    txt_booking_date.setText(part1);
                                    txt_booking_time.setText(part2);
                                }
                                if (dataObj.has("status") && !dataObj.isNull("status")) {
                                    booking_status = dataObj.getString("status");
                                    txt_booking_Status.setText(dataObj.getString("status"));
                                }
                                if (dataObj.has("payment_gateway") && !dataObj.isNull("payment_gateway")) {
                                    txt_paymentMethod.setText(dataObj.getString("payment_gateway"));
                                }
                                if (dataObj.has("payment_status") && !dataObj.isNull("payment_status")) {
                                    txt_paymentStatus.setText(dataObj.getString("payment_status"));
                                }
                                if (dataObj.has("original_amount") && !dataObj.isNull("original_amount")) {
                                    Double serviceSub = dataObj.getDouble("original_amount");
                                    txt_service_subtotal.setText(String.valueOf(serviceSub));
                                }
                                if (dataObj.has("tax_amount") && !dataObj.isNull("tax_amount")) {
                                    Double tax = dataObj.getDouble("tax_amount");
                                    txt_tax.setText(String.valueOf(tax));
                                }
                                if (dataObj.has("original_amount") && !dataObj.isNull("original_amount")) {
                                    Double tax = dataObj.getDouble("original_amount");
                                    txt_serviceTotal.setText(String.valueOf(tax));
                                }
                                if (dataObj.has("product_amount") && !dataObj.isNull("product_amount")) {
                                    Double tax = dataObj.getDouble("product_amount");
                                    txt_productTotal.setText(String.valueOf(tax));
                                } else {
                                    txt_productTotal.setText("0.0");
                                }
                                if (dataObj.has("amount_to_pay") && !dataObj.isNull("amount_to_pay")) {
                                    Double tax = dataObj.getDouble("amount_to_pay");
                                    txt_finalTotal.setText(String.valueOf(tax));
                                }

                                if (dataObj.has("users") && !dataObj.isNull("users")) {
                                    JSONArray obj = dataObj.getJSONArray("users");
                                    for (int a = 0; a < obj.length(); a++) {
                                        JSONObject data = obj.getJSONObject(a);
                                        if (data.has("id") && !data.isNull("id")) {

                                        }
                                    }
                                }
//                                if (dataObj.has("user") && !dataObj.isNull("user")) {
//                                    JSONObject obj = dataObj.getJSONObject("user");
//                                    txt_customerName.setText(obj.getString("first_name") + " " + obj.getString("last_name"));
//
//                                    if (obj.has("email") && !obj.isNull("email")) {
//                                        txt_CustomerEmail.setText(obj.getString("email"));
//                                    }
//                                    if (obj.has("contact_no") && !obj.isNull("contact_no")) {
//                                        txt_mobile.setText(obj.getString("contact_no"));
//                                    }
//
//                                }
                                if (dataObj.has("items") && !dataObj.isNull("items")) {
                                    JSONArray obj = dataObj.getJSONArray("items");

                                    for (int a = 0; a < obj.length(); a++) {
                                        JSONObject data = obj.getJSONObject(a);
                                        SearchItem bookingListModel = new SearchItem();
                                        if (data.has("unit_price") && !data.isNull("unit_price")) {
                                            bookingListModel.setUnitFinalPrice(String.valueOf(data.getDouble("unit_price")));
                                        }
                                        if (data.has("quantity") && !data.isNull("quantity")) {
                                            bookingListModel.setQuantity(data.getInt("quantity"));
                                        }
                                        if (data.has("amount") && !data.isNull("amount")) {
                                            bookingListModel.setFinalPrice(String.valueOf(data.getDouble("amount")));
                                        }
                                        if (data.has("id") && !data.isNull("id")) {
                                            bookingListModel.setProduct_id(String.valueOf(data.getInt("id")));
                                        }
                                        if (data.has("business_service") && !data.isNull("business_service")) {
                                            JSONObject object = data.getJSONObject("business_service");
                                            bookingListModel.setName(object.getString("name"));
                                        }
                                        bookingProductList.add(bookingListModel);
                                    }
                                    bookigProductServiceAdapter.notifyDataSetChanged();
                                }
                                if (dataObj.has("users") && !dataObj.isNull("users")) {
                                    JSONArray obj = dataObj.getJSONArray("users");
                                    for (int a = 0; a < obj.length(); a++) {
                                        JSONObject data = obj.getJSONObject(a);
                                        String employe_Name = "";
                                        if (data.has("first_name") && !data.isNull("first_name")) {
                                            employe_Name = data.getString("first_name");
                                        }
                                        if (data.has("last_name") && !data.isNull("last_name")) {
                                            employe_Name = employe_Name + " " + data.getString("last_name");
                                        }

                                        nameEmployeelist.add(employe_Name);
                                    }
                                    tagAdapter.notifyDataSetChanged();
                                }

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "bookings detail API", "(BookingDetail Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Booking Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Booking Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void bookingStatusUpdate() {
        Retrofit retrofit = APIClient.getClientToken(getContext());
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("id", booking_id);
            mainObject.put("status", booking_status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("change-booking-status", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("booking status ", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            AppConstant.showToast(getContext(), "" + msg);

                            if (successstatus.equalsIgnoreCase("true")) {
                                bookingDetail();
                            }

                        } else {
                            AppConstant.hideProgress();
                            Toast.makeText(getContext(), "Could Not Load Booking Status List. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Booking Status List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }
   public void goToCartScreen() {
       try {
           SharedPreferences sp_cartSave, sp_countproduct, sp_modifiers;
           SharedPreferences.Editor ed_cartSave, ed_countproduct, ed_modifiers;
           sp_cartSave = getContext().getSharedPreferences("myCartPreference", MODE_PRIVATE);
           ed_cartSave = sp_cartSave.edit();

           sp_countproduct = getContext().getSharedPreferences("CountProduct", MODE_PRIVATE);
           ed_countproduct = sp_countproduct.edit();

           sp_modifiers = getContext().getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE);
           ed_modifiers = sp_modifiers.edit();

           if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

               ed_cartSave.remove("myCart");
               ed_cartSave.apply();

           }
           // added cart badge here
           ed_countproduct.remove("countt");
           ed_countproduct.apply();

           ed_modifiers.clear();
           ed_modifiers.apply();

           Intent intent = new Intent(getActivity(), ActivityPosItemList.class);
           JSONObject sell = getSalesDetailResponse.getJSONObject("transaction_details");
           JSONObject booking_details = getSalesDetailResponse.getJSONObject("booking_details");

           BusinessLocationData businessLocationData = new BusinessLocationData();
           businessLocationData.setId(String.valueOf(sell.getInt("location_id")));
           intent.putExtra("location", businessLocationData);

           CurriencyData currencyData = new CurriencyData();
           currencyData.setId(sell.getString("currency"));
           currencyData.setName(sell.getString("currency"));
           intent.putExtra("currency", currencyData);

           CustomerListData customerListData = new CustomerListData();
           intent.putExtra("customer", customerListData);
           intent.putExtra("editOrExchnage", "forEdit");
           intent.putExtra("comingFrom", "fromSaleDetail");
           intent.putExtra("parentfrom", "fromListPosFragment");
           intent.putExtra("from", "fromFragment");
           intent.putExtra("productOrService", 0);
           intent.putExtra("transactionId", String.valueOf(transaction_id));
//            intent.putExtra("transactionId", String.valueOf(transactionId));

           //need tax data , location data from sandi.

//
//            Gson gson = new Gson();
//            //      intent.putExtra("orignalProductsList", gson.toJson(arrCartOrignalProducts));
//            ed_cartSave.putString("myCart", gson.toJson(arrCartProducs));
//            ed_cartSave.commit();

           startActivity(intent);

           getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}