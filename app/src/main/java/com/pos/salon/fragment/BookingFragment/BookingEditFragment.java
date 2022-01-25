package com.pos.salon.fragment.BookingFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityProduct.AddProduct;
import com.pos.salon.activity.ActivityProduct.CategoryActivity;
import com.pos.salon.activity.ActivityProduct.ProductDetailActivity;
import com.pos.salon.activity.ActivityProduct.ProductList;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.BookingAdapter.BookingEditAdapter;
import com.pos.salon.adapter.CategorySearchAdapter;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.ProductsAdapters.TagAdapter;
import com.pos.salon.adapter.SpinerItemAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.fragment.QuatationListFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.CategoryModel;
import com.pos.salon.model.ProductModel.ImagesModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.VariableModel.AllVariationModel;
import com.pos.salon.model.VariableModel.VariationValueModel;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.searchData.SearchItem;
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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.pos.salon.activity.HomeActivity.tool_title;


public class BookingEditFragment extends Fragment {

    BookingEditAdapter bookingServiceAdapter;
    BookingEditAdapter bookingProductAdapter;
    ArrayList<SearchItem> arrServiceDataList = new ArrayList<>();
    ArrayList<SearchItem> arrProductDataList = new ArrayList<>();
    double subTotal = 0.0f, finalPrice = 0.0f, discountAmount = 0.0f, serviceSubTotal = 0.0f, productTotal = 0.0f;
    DecimalFormat df = new DecimalFormat("##.00");
    RecyclerView rlProductList, rlServiceList;
    int productCount = 0, booking_id = 0;
    double tax_amount = 0.0;
    TextView btnSubmit, txt_customerName, txt_customerMobile, txt_customerEmail, txt_booking_date, txt_bookingTime;
    TextView txt_noServices,txt_noProduct,txtServiceSubTotal, txtTotal, txt_tax_amount, txtProductTotal, txt_ServiceAmount, txt_paymentMethod;
    String totalDiscountAmt = "0.0";
    EditText edtDiscountTotal;
    LinearLayout lay_submit, lay_bookingDate, lay_bookingTime, lay_serviceAdd, lay_productAdd;
    private final Calendar myCalendar = Calendar.getInstance();
    private ArrayList<CustomerListData> searchCustomerList = new ArrayList<CustomerListData>();
    DelayAutoCompleteTextView et_customer_name;
    private CustomerListData customerData;
    CustomerSearchAdapter cutoCompleteCustomerAapter;
    RecyclerView recycler_employee;
    ArrayList<String> nameEmployeelist = new ArrayList<>();
    ArrayList<String> nameEmployeeIDlist = new ArrayList<>();
    TagAdapter tagAdapter;
    SharedPreferences.Editor ed_cartSave;
    SharedPreferences sp_cartSave;
    private ArrayList<CategoryModel> arrServicesList = new ArrayList<>();
    private ArrayList<CategoryModel> arrProductList = new ArrayList<>();
    private CategorySearchAdapter catSearchAdapter;
    public String booking_date = "", bookingTime = "", booking_status = "", payment_status = "";
    Spinner spn_paymntStatus, spn_bookingStatus;
    ArrayList<SpinnerModel> paymentSTatusList = new ArrayList<>();
    ArrayList<SpinnerModel> bookingSTatusList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_edit, container, false);
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

        tool_title.setText("EDIT BOOKING");
        Bundle bundle = getArguments();
        booking_id = bundle.getInt("booking_id");
        findViewIds(view);
    }

    public void findViewIds(View view) {
        rlProductList = view.findViewById(R.id.rlProductList);
        rlServiceList = view.findViewById(R.id.rlServiceList);
        txt_customerName = view.findViewById(R.id.txt_customerName);
        txt_customerMobile = view.findViewById(R.id.txt_customerMobile);
        txt_customerEmail = view.findViewById(R.id.txt_customerEmail);
        txt_booking_date = view.findViewById(R.id.txt_booking_date);
        txt_bookingTime = view.findViewById(R.id.txt_bookingTime);
        txtServiceSubTotal = view.findViewById(R.id.txtServiceSubTotal);
        edtDiscountTotal = view.findViewById(R.id.edtDiscountTotal);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtProductTotal = view.findViewById(R.id.txtProductTotal);
        txt_ServiceAmount = view.findViewById(R.id.txt_ServiceAmount);
        txt_tax_amount = view.findViewById(R.id.txt_tax_amount);
        lay_bookingDate = view.findViewById(R.id.lay_bookingDate);
        lay_bookingTime = view.findViewById(R.id.lay_bookingTime);
        et_customer_name = view.findViewById(R.id.et_customer_name);
        recycler_employee = view.findViewById(R.id.recycler_employee);
        lay_serviceAdd = view.findViewById(R.id.lay_serviceAdd);
        lay_productAdd = view.findViewById(R.id.lay_productAdd);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        spn_paymntStatus = view.findViewById(R.id.spn_paymntStatus);
        spn_bookingStatus = view.findViewById(R.id.spn_bookingStatus);
        txt_paymentMethod = view.findViewById(R.id.txt_paymentMethod);
        lay_submit = view.findViewById(R.id.lay_submit);
        txt_noServices = view.findViewById(R.id.txt_noServices);
        txt_noProduct = view.findViewById(R.id.txt_noProduct);


        GridLayoutManager layoutManagerTag = new GridLayoutManager(getContext(), 3);
        recycler_employee.setLayoutManager(layoutManagerTag);
        tagAdapter = new TagAdapter(getContext(), nameEmployeelist,"");
        recycler_employee.setAdapter(tagAdapter);

        cutoCompleteCustomerAapter = new CustomerSearchAdapter(getActivity(), searchCustomerList);
        et_customer_name.setAdapter(cutoCompleteCustomerAapter);
        et_customer_name.setThreshold(1);

        paymentSTatusList.clear();
        SpinnerModel model = new SpinnerModel();
        model.name = "Completed";
        paymentSTatusList.add(model);
        SpinnerModel newmodel = new SpinnerModel();
        newmodel.name = "Pending";
        paymentSTatusList.add(newmodel);


//        sp_cartSave = getContext().getSharedPreferences("myCartPreference", MODE_PRIVATE);
//        ed_cartSave = sp_cartSave.edit();

        clicklisteners();
        ///get cart shared preference
        sp_cartSave = getContext().getSharedPreferences("searchCustomerList", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();
        getEditDetail();

    }

    public void clicklisteners() {

        SpinerItemAdapter spinerItemAdapter = new SpinerItemAdapter(getContext(), paymentSTatusList);
        spn_paymntStatus.setAdapter(spinerItemAdapter);

        spn_paymntStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    payment_status = "completed";
                } else if (position == 1) {
                    payment_status = "pending";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spn_bookingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                booking_status = bookingSTatusList.get(position).spinner_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tagAdapter.setOnRemoveClicked(new TagAdapter.RemoveTag() {
            @Override
            public void setOnClickedItem(int position) {

                deleteEmployee(position);

            }
        });

        lay_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrServiceDataList.size() == 0 && arrProductDataList.size() == 0) {
                    AppConstant.showToast(getContext(), "No Items Added");
                } else {
                    updateBooking();
                }

            }
        });

        et_customer_name.addTextChangedListener(new TextWatcher() {
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

        lay_serviceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServcieDialog("service");
            }
        });
        lay_productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServcieDialog("product");
            }
        });

        lay_bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), datee, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
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
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String status = "AM";
                        if (selectedHour > 11) {
                            status = "PM";
                        }
                        if (selectedHour == 13) {
                            selectedHour = 01;
                        }
                        if (selectedHour == 14) {
                            selectedHour = 02;
                        }
                        if (selectedHour == 15) {
                            selectedHour = 03;
                        }
                        if (selectedHour == 16) {
                            selectedHour = 04;
                        }
                        if (selectedHour == 17) {
                            selectedHour = 05;
                        }
                        if (selectedHour == 18) {
                            selectedHour = 06;
                        }
                        if (selectedHour == 19) {
                            selectedHour = 07;
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
                        if (selectedMinute < 10) {
                            bookingTime = selectedHour + ":0" + selectedMinute + " " + status;
                            txt_bookingTime.setText(selectedHour + ":0" + selectedMinute + " " + status);
                        } else {
                            bookingTime = selectedHour + ":" + selectedMinute + " " + status;
                            txt_bookingTime.setText(selectedHour + ":" + selectedMinute + " " + status);
                        }
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
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
                String disEntered = s.toString();
                if (!disEntered.isEmpty()) {
                    totalDiscountAmt = String.valueOf(Double.valueOf(disEntered));
                } else {
                    totalDiscountAmt = "0.0";
                }
                totalShow();
                discountAmountShow();

            }
        });


        bookingServiceAdapter = new BookingEditAdapter(getContext(), arrServiceDataList, new BookingEditAdapter.ClickDeleteItem() {
            @Override
            public void onItemClick(SearchItem searchItem, int position, View v) {

                productDeleteAlertShow(searchItem, position, v, "service");
            }

            @Override
            public void onClick(int quantity, SearchItem item, int position) {
                subTotal = 0;

                item.setUnitFinalPrice(String.valueOf((Double.valueOf(item.getUnitFinalPrice()))));

                finalPrice = (Double.valueOf(item.getUnitFinalPrice()) * item.getQuantity());

                item.setFinalPrice(df.format(finalPrice) + "");
                arrServiceDataList.set(position, item);

                totalShow();
                discountAmountShow();
            }

        });

        rlServiceList.setAdapter(bookingServiceAdapter);
        rlServiceList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rlServiceList.setItemAnimator(new DefaultItemAnimator());

        bookingProductAdapter = new BookingEditAdapter(getContext(), arrProductDataList, new BookingEditAdapter.ClickDeleteItem() {

            @Override
            public void onItemClick(SearchItem searchItem, int position, View v) {

                productDeleteAlertShow(searchItem, position, v, "product");
            }

            @Override
            public void onClick(int quantity, SearchItem item, int position) {
                subTotal = 0;

                item.setUnitFinalPrice(String.valueOf((Double.valueOf(item.getUnitFinalPrice()))));

                finalPrice = (Double.valueOf(item.getUnitFinalPrice()) * item.getQuantity());

                item.setFinalPrice(df.format(finalPrice) + "");
                arrProductDataList.set(position, item);

                totalShow();
                discountAmountShow();
            }

        });
        rlProductList.setAdapter(bookingProductAdapter);
        rlProductList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rlProductList.setItemAnimator(new DefaultItemAnimator());


    }

    public void openServcieDialog(String addTo) {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.search_filter_list_items);

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
        TextView txt_title = dialog.findViewById(R.id.txt_title);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);

        txt_title.setText("Services");

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        if (addTo.equalsIgnoreCase("service")) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recycler_category.setLayoutManager(mLayoutManager);
            catSearchAdapter = new CategorySearchAdapter(getContext(), arrServicesList);
            recycler_category.setAdapter(catSearchAdapter);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recycler_category.setLayoutManager(mLayoutManager);
            catSearchAdapter = new CategorySearchAdapter(getContext(), arrProductList);
            recycler_category.setAdapter(catSearchAdapter);
        }

        catSearchAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                dialog.dismiss();
                if (addTo.equalsIgnoreCase("service")) {
                    CategoryModel model = list.get(position);
                    SearchItem searchItem = new SearchItem();
                    searchItem.setName(model.name);
                    searchItem.setUnitFinalPrice(String.valueOf(model.price));
                    searchItem.setQuantity(1);
                    searchItem.setProduct_id(String.valueOf(model.id));
                    searchItem.setFinalPrice(String.valueOf(model.net_price));
                    boolean isAlreadyAdded = false;
                    for (int a = 0; a < arrServiceDataList.size(); a++) {
                        SearchItem searchItem1 = arrServiceDataList.get(a);
                        if (searchItem.getProduct_id() == arrServiceDataList.get(a).getProduct_id()) {
                            searchItem.setQuantity(searchItem1.getQuantity() + 1);
                            searchItem.setFinalPrice(String.valueOf(Double.valueOf(model.price) * searchItem.getQuantity()));
                            isAlreadyAdded = true;
                            arrServiceDataList.set(a, searchItem);
                            break;
                        }
                    }
                    if (isAlreadyAdded) {

                    } else {
                        arrServiceDataList.add(searchItem);

                    }
                    bookingServiceAdapter.notifyDataSetChanged();
                } else {
                    CategoryModel model = list.get(position);
                    SearchItem searchItem = new SearchItem();
                    searchItem.setName(model.name);
                    searchItem.setUnitFinalPrice(String.valueOf(model.price));
                    searchItem.setQuantity(1);
                    searchItem.setProduct_id(String.valueOf(model.id));
                    searchItem.setFinalPrice(String.valueOf(model.net_price));
                    boolean isAlreadyAdded = false;
                    for (int a = 0; a < arrProductDataList.size(); a++) {
                        SearchItem searchItem1 = arrProductDataList.get(a);
                        if (searchItem.getProduct_id().equalsIgnoreCase(arrProductDataList.get(a).getProduct_id())) {
                            searchItem.setQuantity(searchItem1.getQuantity() + 1);
                            searchItem.setFinalPrice(String.valueOf(Double.valueOf(model.price) * searchItem.getQuantity()));
                            isAlreadyAdded = true;
                            arrProductDataList.set(a, searchItem);
                            break;
                        }
                    }
                    if (isAlreadyAdded) {

                    } else {
                        arrProductDataList.add(searchItem);

                    }
                    bookingProductAdapter.notifyDataSetChanged();
                }

                if(arrProductDataList.size()==0){
                    txt_noProduct.setVisibility(View.VISIBLE);
                }else{
                    txt_noProduct.setVisibility(View.GONE);
                }
                if(arrServiceDataList.size()==0){
                    txt_noServices.setVisibility(View.VISIBLE);
                }else{
                    txt_noServices.setVisibility(View.GONE);
                }

                totalShow();
                discountAmountShow();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filterItem(s.toString(), addTo);

                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }

    public void deleteEmployee(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
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

    void filterItem(String text, String addTo) {
        ArrayList<CategoryModel> temp = new ArrayList();
        if (addTo.equalsIgnoreCase("service")) {
            for (CategoryModel d : arrServicesList) {
                if (d.name.toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        } else {
            for (CategoryModel d : arrProductList) {
                if (d.name.toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        }

        //update recyclerview
        catSearchAdapter.updateList(temp);

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
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.getText().toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        cutoCompleteCustomerAapter.updateList(temp);

        et_customer_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }
                et_customer_name.setText("");

                boolean newItem=true;
                for(int a=0;a < nameEmployeeIDlist.size();a++){
                    if(customerData.getId()==Integer.parseInt(nameEmployeeIDlist.get(a))){
                        newItem=false;
                        break;
                    }
                }
                if(newItem){
                    nameEmployeelist.add(customerData.getText());
                    nameEmployeeIDlist.add(String.valueOf(customerData.getId()));
                    tagAdapter.notifyDataSetChanged();
                }

//                contactID = String.valueOf(customerData.getId());

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


    public void getEditDetail() {

        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<ResponseBody> call = apiService.editsalesDetail("bookings/" + booking_id + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        try {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            searchCustomerList.clear();
                            arrServicesList.clear();
                            arrProductList.clear();
                            bookingSTatusList.clear();
                            Log.e("Response", respo.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONObject dataObj = responseObject.getJSONObject("booking_details");

                                if (dataObj.has("tax_amount") && !dataObj.isNull("tax_amount")) {
                                    tax_amount = dataObj.getDouble("tax_amount");
                                    txt_tax_amount.setText(String.valueOf(tax_amount));
                                }
                                if (dataObj.has("amount_to_pay") && !dataObj.isNull("amount_to_pay")) {
                                    txtTotal.setText("" + dataObj.getDouble("amount_to_pay"));
                                }
                                if (dataObj.has("original_amount") && !dataObj.isNull("original_amount")) {
                                    txtServiceSubTotal.setText("" + dataObj.getDouble("original_amount"));
                                }
                                if (dataObj.has("product_amount") && !dataObj.isNull("product_amount")) {
                                    txtProductTotal.setText("" + dataObj.getDouble("product_amount"));
                                }
                                if (dataObj.has("original_amount") && !dataObj.isNull("original_amount")) {
                                    txt_ServiceAmount.setText("" + dataObj.getDouble("original_amount"));
                                }
                                if (dataObj.has("payment_gateway") && !dataObj.isNull("payment_gateway")) {
                                    txt_paymentMethod.setText("" + dataObj.getString("payment_gateway"));
                                }
                                if (dataObj.has("payment_status") && !dataObj.isNull("payment_status")) {
                                    payment_status = dataObj.getString("payment_status");
                                    for (int a = 0; a < paymentSTatusList.size(); a++) {
                                        if (paymentSTatusList.get(a).name.equalsIgnoreCase(payment_status)) {
                                            spn_paymntStatus.setSelection(a);
                                            break;
                                        }
                                    }
                                }
                                if (dataObj.has("status") && !dataObj.isNull("status")) {
                                    booking_status = dataObj.getString("status");
                                }
                                if (responseObject.has("status") && !responseObject.isNull("status")) {
                                    JSONArray arrayStatus = responseObject.getJSONArray("status");

                                    for (int a = 0; a < arrayStatus.length(); a++) {
                                        JSONObject statusdata = arrayStatus.getJSONObject(a);
                                        SpinnerModel statusMOdel2 = new SpinnerModel();
                                        if (statusdata.has("id") && !statusdata.isNull("id")) {
                                            statusMOdel2.spinner_id = statusdata.getString("id");
                                        }
                                        if (statusdata.has("name") && !statusdata.isNull("name")) {
                                            statusMOdel2.name = statusdata.getString("name");
                                        }
                                        bookingSTatusList.add(statusMOdel2);
                                    }
                                    SpinerItemAdapter statusAdapter = new SpinerItemAdapter(getContext(), bookingSTatusList);
                                    spn_bookingStatus.setAdapter(statusAdapter);

                                    for (int a = 0; a < bookingSTatusList.size(); a++) {
                                        if (bookingSTatusList.get(a).name.equalsIgnoreCase(booking_status)) {
                                            spn_bookingStatus.setSelection(a);
                                            break;
                                        }
                                    }
                                }

                                if (dataObj.has("date_time") && !dataObj.isNull("date_time")) {
                                    String part1 = "";
                                    String part2 = "";
                                    if (dataObj.getString("date_time") != null && !dataObj.getString("date_time").equalsIgnoreCase("")) {
                                        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date newDate = null;
                                        try {
                                            newDate = spf.parse(dataObj.getString("date_time"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        spf = new SimpleDateFormat("yyyy-MM-dd");
                                        part1 = spf.format(newDate);
                                        spf = new SimpleDateFormat("hh:mm a");
                                        part2 = spf.format(newDate);

                                    }
                                    booking_date = part1;
                                    bookingTime = part2;
                                    txt_booking_date.setText(part1);
                                    txt_bookingTime.setText(part2);
                                }

                                if (dataObj.has("contact") && !dataObj.isNull("contact")) {
                                    JSONObject contactObj = dataObj.getJSONObject("contact");
                                    if (contactObj.has("name") && !contactObj.isNull("name")) {
                                        txt_customerName.setText(contactObj.getString("name"));
                                    }
                                    if (contactObj.has("mobile") && !contactObj.isNull("mobile")) {
                                        txt_customerMobile.setText(contactObj.getString("mobile"));
                                    }
                                    if (contactObj.has("email") && !contactObj.isNull("email")) {
                                        txt_customerEmail.setText(contactObj.getString("email"));
                                    }
                                }
                                if (dataObj.has("items") && !dataObj.isNull("items")) {
                                    JSONArray itemsObj = dataObj.getJSONArray("items");
                                    for (int i = 0; i < itemsObj.length(); i++) {
                                        JSONObject data = itemsObj.getJSONObject(i);
                                        SearchItem searchItem = new SearchItem();
                                        if (data.has("quantity") && !data.isNull("quantity")) {
                                            searchItem.setQuantity(data.getInt("quantity"));
                                        }
                                        if (data.has("unit_price") && !data.isNull("unit_price")) {
                                            searchItem.setUnitFinalPrice(String.valueOf(data.getDouble("unit_price")));
                                        }
                                        if (data.has("amount") && !data.isNull("amount")) {
                                            searchItem.setFinalPrice(String.valueOf(data.getDouble("amount")));
                                        }
                                        if (data.has("business_service_id") && !data.isNull("business_service_id")) {
                                            searchItem.setProduct_id(String.valueOf(data.getInt("business_service_id")));
                                        }
                                        if (data.has("product") && !data.isNull("product")) {
                                            JSONObject product = data.getJSONObject("product");
                                            searchItem.setName(product.getString("name"));
                                            searchItem.setProduct_id(String.valueOf(product.getInt("id")));
                                            arrProductDataList.add(searchItem);
                                        } else {
                                            arrServiceDataList.add(searchItem);

                                        }
                                    }
                                    if(arrProductDataList.size()==0){
                                        txt_noProduct.setVisibility(View.VISIBLE);
                                    }else{
                                        txt_noProduct.setVisibility(View.GONE);
                                    }
                                    if(arrServiceDataList.size()==0){
                                        txt_noServices.setVisibility(View.VISIBLE);
                                    }else{
                                        txt_noServices.setVisibility(View.GONE);
                                    }

                                    bookingServiceAdapter.notifyDataSetChanged();
                                    bookingProductAdapter.notifyDataSetChanged();
                                }

                                if (responseObject.has("employees") && !responseObject.isNull("employees")) {
                                    JSONArray employeesObj = responseObject.getJSONArray("employees");
                                    for (int i = 0; i < employeesObj.length(); i++) {
                                        JSONObject data = employeesObj.getJSONObject(i);
                                        CustomerListData customerListData = new CustomerListData();
                                        customerListData.setId(data.getInt("id"));
                                        customerListData.setText(data.getString("first_name") + " " + data.getString("last_name"));
                                        if (data.has("email") && !data.isNull("email")) {
                                            customerListData.setEmail(data.getString("email"));
                                        }
                                        searchCustomerList.add(customerListData);
                                    }


                                }
                                Gson gson = new Gson();
                                ed_cartSave.putString("searchCustomerList", gson.toJson(searchCustomerList));
                                ed_cartSave.commit();
                                cutoCompleteCustomerAapter.notifyDataSetChanged();


//                                if (responseObject.has("businessServices") && !responseObject.isNull("businessServices")) {
//                                    JSONArray itemsObj = responseObject.getJSONArray("businessServices");
//                                    for (int i = 0; i < itemsObj.length(); i++) {
//                                        JSONObject data = itemsObj.getJSONObject(i);
//                                        if (data.has("name") && !data.isNull("name")) {
//                                            searchItem.name = data.getString("name");
//                                        }
//                                    }
//                                }
//                                    if(!tags.isEmpty()){
//                                        String[] elements = tags.split(",");
//
//                                        for(int i=0 ; i <elements.length ; i++){
//                                            arrStringTagsList.add(elements[i]);
//                                        }
//                                        tagAdapter.notifyDataSetChanged();
//                                    }

                                if (responseObject.has("businessServices") && !responseObject.isNull("businessServices")) {
                                    JSONArray itemsObj = responseObject.getJSONArray("businessServices");
                                    for (int i = 0; i < itemsObj.length(); i++) {
                                        JSONObject data = itemsObj.getJSONObject(i);
                                        CategoryModel searchItem = new CategoryModel();
                                        searchItem.quantity = 1;
                                        if (data.has("name") && !data.isNull("name")) {
                                            searchItem.name = data.getString("name");
                                        }
                                        if (data.has("price") && !data.isNull("price")) {
                                            searchItem.price = data.getDouble("price");
                                        }
                                        if (data.has("net_price") && !data.isNull("net_price")) {
                                            searchItem.net_price = data.getDouble("net_price");
                                        }
                                        if (data.has("id") && !data.isNull("id")) {
                                            searchItem.id = data.getInt("id");
                                        }
                                        arrServicesList.add(searchItem);
                                    }
                                }
//                                if (responseObject.has("selected_booking_user") && !responseObject.isNull("selected_booking_user")) {
//                                    JSONArray itemsObj = responseObject.getJSONArray("selected_booking_user");
//                                    for (int i = 0; i < itemsObj.length(); i++) {
//                                        JSONObject data = itemsObj.getJSONObject(i);
//                                        CategoryModel searchItem = new CategoryModel();
//                                        searchItem.quantity = 1;
//                                        if (data.has("name") && !data.isNull("name")) {
//                                            searchItem.name = data.getString("name");
//                                        }
//                                        if (data.has("price") && !data.isNull("price")) {
//                                            searchItem.price = data.getDouble("price");
//                                        }
//                                        if (data.has("net_price") && !data.isNull("net_price")) {
//                                            searchItem.net_price = data.getDouble("net_price");
//                                        }
//                                        if (data.has("id") && !data.isNull("id")) {
//                                            searchItem.id = data.getInt("id");
//                                        }
//                                        arrServicesList.add(searchItem);
//                                    }
//                                }
                                if (responseObject.has("products") && !responseObject.isNull("products")) {
                                    JSONArray itemsObj = responseObject.getJSONArray("products");
                                    for (int i = 0; i < itemsObj.length(); i++) {
                                        JSONObject data = itemsObj.getJSONObject(i);
                                        CategoryModel searchItem = new CategoryModel();
                                        if (data.has("name") && !data.isNull("name")) {
                                            searchItem.name = data.getString("name");
                                        }
                                        if (data.has("product_id") && !data.isNull("product_id")) {
                                            searchItem.id = data.getInt("product_id");
                                        }
                                        if (data.has("selling_price") && !data.isNull("selling_price")) {
                                            searchItem.price = data.getDouble("selling_price");
                                        }
                                        if (data.has("selling_price") && !data.isNull("selling_price")) {
                                            searchItem.net_price = data.getDouble("selling_price");
                                        }
                                        arrProductList.add(searchItem);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.toString());
                        }
                    } else {//response body coming null.
                        AppConstant.sendEmailNotification(getContext(), "bookings/edit API", "(edit Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(getContext(), "Load Incomplete Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Load Incomplete Detail. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });

        }


    }

    AlertDialog alertDialogDelete;

    private void productDeleteAlertShow(SearchItem item, final int position, View v, String from) {
        ViewGroup viewGroup = v.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.product_delete_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        Button buttonYes = (Button) dialogView.findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("product")) {
                    for (int i = 0; i < arrProductDataList.size(); i++) {
                        if (arrProductDataList.get(position).getProduct_id().equalsIgnoreCase(arrProductDataList.get(i).getProduct_id())) {
                            arrProductDataList.remove(i);
                            break;
                        }
                    }
                    bookingProductAdapter.notifyItemRemoved(position);
                    bookingProductAdapter.notifyItemRangeChanged(position, arrProductDataList.size());

                } else {
                    for (int i = 0; i < arrServiceDataList.size(); i++) {
                        if (arrServiceDataList.get(position).getProduct_id().equalsIgnoreCase(arrServiceDataList.get(i).getProduct_id())) {
                            arrServiceDataList.remove(i);
                            break;
                        }
                    }
                    bookingServiceAdapter.notifyItemRemoved(position);
                    bookingServiceAdapter.notifyItemRangeChanged(position, arrServiceDataList.size());
                }
                if(arrProductDataList.size()==0){
                    txt_noProduct.setVisibility(View.VISIBLE);
                }else{
                    txt_noProduct.setVisibility(View.GONE);
                }
                if(arrServiceDataList.size()==0){
                    txt_noServices.setVisibility(View.VISIBLE);
                }else{
                    txt_noServices.setVisibility(View.GONE);
                }
                alertDialogDelete.dismiss();
                totalShow();
                discountAmountShow();
            }
//
//            }
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

        productCount = 0;
        subTotal = 0;
        serviceSubTotal = 0;
        productTotal = 0;
        //subTotal=subTotal+totalvariation;
        for (int x = 0; x < arrServiceDataList.size(); x++) {

            SearchItem singleItem = arrServiceDataList.get(x);

            if (singleItem.getUnitFinalPrice() != null) {
                subTotal = subTotal + ((Double.valueOf(singleItem.getUnitFinalPrice())) * singleItem.getQuantity());
                productCount = productCount + singleItem.getQuantity();
                serviceSubTotal = serviceSubTotal + Double.valueOf(singleItem.getUnitFinalPrice()) * singleItem.getQuantity();

            } else {
                productCount = productCount + singleItem.getQuantity();
                subTotal = subTotal + (Double.valueOf(singleItem.getUnitFinalPrice())) * singleItem.getQuantity();
                serviceSubTotal = serviceSubTotal + Double.valueOf(singleItem.getUnitFinalPrice()) * singleItem.getQuantity();

            }

        }

        for (int x = 0; x < arrProductDataList.size(); x++) {

            SearchItem singleItem = arrProductDataList.get(x);

            if (singleItem.getUnitFinalPrice() != null) {
                subTotal = subTotal + ((Double.valueOf(singleItem.getUnitFinalPrice())) * singleItem.getQuantity());
                productCount = productCount + singleItem.getQuantity();
                productTotal = productTotal + Double.valueOf(singleItem.getUnitFinalPrice()) * singleItem.getQuantity();
            } else {
                productCount = productCount + singleItem.getQuantity();
                subTotal = subTotal + (Double.valueOf(singleItem.getUnitFinalPrice())) * singleItem.getQuantity();
                productTotal = productTotal + Double.valueOf(singleItem.getUnitFinalPrice()) * singleItem.getQuantity();

            }

        }


        txtServiceSubTotal.setText((df.format(serviceSubTotal)));
        txt_tax_amount.setText("0.0");
        txt_ServiceAmount.setText((df.format(serviceSubTotal)));
        txtProductTotal.setText((df.format(productTotal)));

    }

    private void discountAmountShow() {
        double productTotalAfterDis = 0.0;
        double discountAmountDIs = 0.0;
        if (!totalDiscountAmt.equalsIgnoreCase("")) {
            float discountAmtF = Float.parseFloat(totalDiscountAmt);
            discountAmount = discountAmtF;

            discountAmountDIs = (serviceSubTotal * discountAmtF / 100);

            productTotalAfterDis = serviceSubTotal - discountAmountDIs;

            subTotal = subTotal - discountAmountDIs;
        }
        Log.e("D SUB TOTAL : ", df.format(subTotal));
        txt_ServiceAmount.setText((df.format(productTotalAfterDis)));
//        subTotal = subTotal + productTotal;
        txt_tax_amount.setText("0.0");
        txtTotal.setText("" + df.format(subTotal));

    }

    public void updateBooking() {
        AppConstant.showProgress(getContext(), false);
        Retrofit retrofit = APIClient.getClientToken(getContext());

        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("booking_date", booking_date);
            mainObject.put("booking_time", bookingTime);
            mainObject.put("status", booking_status);

            JSONArray employee_id = new JSONArray();
            for (int i = 0; i < nameEmployeeIDlist.size(); i++) {
                employee_id.put(String.valueOf(nameEmployeeIDlist.get(i)));
            }
            mainObject.put("employee_id", employee_id);
            JSONArray types = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                types.put("service");
            }
            mainObject.put("types", types);

            JSONArray item_ids = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                item_ids.put(arrServiceDataList.get(i).getProduct_id());
            }
            mainObject.put("item_ids", item_ids);

            JSONArray item_prices = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                item_prices.put(String.valueOf(Double.valueOf(arrServiceDataList.get(i).getUnitFinalPrice())));
            }
            mainObject.put("item_prices", item_prices);

            JSONArray tax_percent = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                tax_percent.put("0");
            }
            mainObject.put("tax_percent", tax_percent);

            JSONArray tax_amount = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                tax_amount.put("0");
            }
            mainObject.put("tax_amount", tax_amount);

            JSONArray cart_quantity = new JSONArray();
            for (int i = 0; i < arrServiceDataList.size(); i++) {
                cart_quantity.put(String.valueOf(arrServiceDataList.get(i).getQuantity()));
            }
            mainObject.put("cart_quantity", cart_quantity);

            JSONArray cart_products = new JSONArray();
            for (int i = 0; i < arrProductDataList.size(); i++) {
                cart_products.put(arrProductDataList.get(i).getProduct_id());
            }
            mainObject.put("cart_products", cart_products);

            JSONArray product_prices = new JSONArray();
            for (int i = 0; i < arrProductDataList.size(); i++) {
                product_prices.put(String.valueOf(Double.valueOf(arrProductDataList.get(i).getUnitFinalPrice())));
            }
            mainObject.put("product_prices", product_prices);

            JSONArray product_percent = new JSONArray();
            for (int i = 0; i < arrProductDataList.size(); i++) {
                product_percent.put("0");
            }
            mainObject.put("product_percent", product_percent);

            JSONArray product_quantity = new JSONArray();
            for (int i = 0; i < arrProductDataList.size(); i++) {
                product_quantity.put(String.valueOf(arrProductDataList.get(i).getQuantity()));
            }
            mainObject.put("product_quantity", product_quantity);
            mainObject.put("payment_status", payment_status);
            mainObject.put("cart_discount", "0");
            mainObject.put("cart_tax", "0");
            mainObject.put("current_url", "");
            mainObject.put("hidden_booking_time", bookingTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.updateBooking("bookings/" + booking_id, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("update Response", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            if (successstatus.equalsIgnoreCase("true")) {


//                                fragmentTransaction.remove(fm.findFragmentByTag("bookingListFragment")).commit();
//                                fragmentTransaction.remove(fm.findFragmentByTag("bookingDetailFragment")).commit();
//                                fragmentTransaction.remove(fm.findFragmentByTag("bookingEditFragment")).commit();
//                                fragmentTransaction.remove(fm.findFragmentByTag("bookingEditFragment")).commit();
//                                fragmentTransaction.remove(fm.findFragmentByTag("home")).commit();
//
//                                BookingListFragment ldf = new BookingListFragment();
//                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left).replace(R.id.lay_Bookingedit, ldf).commit();
                                HomeActivity.tool_title.setText("BOOKING");
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack("bookingListFragment", 0);
                                fm.popBackStack("bookingDetailFragment", 0);
                                fm.popBackStack("bookingEditFragment", 0);
                                fm.popBackStack("home", 0);
                            }
                            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_LONG).show();

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "edit API", "(EditBooking)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Add Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}

