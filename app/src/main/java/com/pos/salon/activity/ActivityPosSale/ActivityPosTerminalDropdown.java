package com.pos.salon.activity.ActivityPosSale;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.AddCustomerActivity;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.activity.ActivityPayment.PartialCustomerProducts;
import com.pos.salon.adapter.ProductsAdapters.CurrencyAdapter;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.LocationAdapter.LocationAdapter;
import com.pos.salon.adapter.ListAdapters.ServiceStaffAdapter;
import com.pos.salon.adapter.TableListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.interfacesection.Selectedtable;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.customerData.FetchPartialCustomer;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CountryData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.posLocation.PosLocationResponse;
import com.pos.salon.model.posLocation.ResTables;
import com.pos.salon.model.posLocation.ServiceStaff;
import com.pos.salon.model.posLocation.Taxes;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ActivityPosTerminalDropdown extends AppCompatActivity implements Selectedtable {

    private Toolbar toolbar;
    private Spinner dropdownLocation;
    private Spinner dropdownCurrency;
    private Spinner dropdownStaff;
    private Spinner dropdownTable;
    private ArrayList<BusinessLocationData> locationList;
    private ArrayList<CurriencyData> currencyList;
    private ArrayList<CustomerListData> customerList;
    private ArrayList<ServiceStaff> staffList;
    private ArrayList<ResTables> tables_List;
    LocationAdapter locationAdapter;
    CurrencyAdapter currencyAdapter;
    Dialog dialog;
    String quaryText="";
    ServiceStaffAdapter staffAdapter;
    TableListAdapter tableListAdapter;
    BusinessLocationData locationData;
    LinearLayout dropDownParent;
    ArrayList<Taxes> itemTax;
    ArrayList<CountryData> countryList;
    CurriencyData currencyData;
    CustomerListData customerData;
    CustomerSearchAdapter customerSearchAdapter;
    ArrayList<CustomerListData> searchCustomerList=new ArrayList<>() ;
    DelayAutoCompleteTextView ac_searchcustomer;
    TextView customerSearchBox;
    @SuppressLint("StaticFieldLeak")
    public static Activity dropdownActivity;
    private String defaultCurrency = "";
    SharedPreferences sp_selectedcustomer, sharedPreferences,sp_saveTax;
    SharedPreferences.Editor ed_selectedcustomer, editor,ed_saveTax;
    SharedPreferences sp_cartSave; // for fetch data from preference
    SharedPreferences.Editor ed_cartSave; //for save data in preference.
    Spinner spnordertype;
    String orderr_type = "", selected_ordertype;
    String[] arraySpinner = new String[]{"Select Order Type", "Dine In", "Take Out", "Delivery"};
    String tablee_id="", waiterr_id="";
    ImageView ivAddUser;
    SharedPreferences sp_countproduct;
    SharedPreferences.Editor ed_countproduct;
    ArrayList<String> departmentList = new ArrayList<String>();
    Spinner dropdownProductService;
    int productOrService=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posterminal_dropdown);

        dropdownActivity = this;
        spnordertype = findViewById(R.id.spnordertype);
        dropdownProductService = findViewById(R.id.dropdownProductService);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sp_saveTax = getSharedPreferences("defaultTax", Context.MODE_PRIVATE);
        ed_saveTax = sp_saveTax.edit();

        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {}.getType();

        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            departmentList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
        }
        if(departmentList.contains("8")){
            spnordertype.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnordertype.setAdapter(adapter);
        }

        dialog = new Dialog(ActivityPosTerminalDropdown.this);
        dialog.setContentView(R.layout.dialog_searchcustomer);
        ac_searchcustomer = (DelayAutoCompleteTextView) dialog.findViewById(R.id.ac_searchcustomer);
        sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
        ed_selectedcustomer = sp_selectedcustomer.edit();

        ed_selectedcustomer.putString("customernamee", "Cash Customer");
        ed_selectedcustomer.commit();

        //Toast.makeText(dropdownActivity, ""+sp_selectedcustomer.getString("customernamee",""), Toast.LENGTH_LONG).show();


        setWidget();
        setBackNavgation();
        dropdownMenuCollection();

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        ed_countproduct.remove("countt");
        ed_countproduct.commit();

        // added cart badge here


        ivAddUser = findViewById(R.id.ivAddUser);
        customerSearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (locationData == null) {
                    AppConstant.showSnakeBar(dropDownParent, "Select location");
                } else  if (orderr_type.equals("Select Order Type")) {
                    AppConstant.showSnakeBar(dropDownParent, "Select order type");
                } else {
                    dialog.show();
                }

            }
        });

        spnordertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                orderr_type = String.valueOf(adapterView.getItemAtPosition(i));
                if (adapterView.getItemAtPosition(i).equals("Dine In")) {
                    dropdownStaff.setVisibility(View.VISIBLE);
                    dropdownTable.setVisibility(View.VISIBLE);
                } else {
                    dropdownStaff.setVisibility(View.GONE);
                    dropdownTable.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ivAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String gson = new Gson().toJson(countryList );
                Intent intent=new Intent(getBaseContext(), AddCustomerActivity.class);
                intent.putExtra("country_list",gson);
                intent.putExtra("isComing","toAdd");
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        List<String> arraySpinner=new ArrayList<>();
        arraySpinner.add("Product");
        arraySpinner.add("Service");
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownProductService.setAdapter(adapterType);

//        String aa="Product";
//        if(comingFrom.equalsIgnoreCase("fromSaleDetail")) {
////            for (int i = 0; i < arraySpinner.size(); i++) {
////                if (aa.equalsIgnoreCase(arraySpinner.get(i))) {
////                    dropdownProductService.setSelection(i);
////                }
////
////            }
//            dropdownProductService.setEnabled(false);
//        }

        dropdownProductService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(dropdownProductService.isEnabled()){
                    //clear cart product data if comes on this screen.
//                    if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
//
//                        ed_cartSave.remove("myCart");
//                        ed_cartSave.commit();
//
//                    }
                    // added cart badge here
//                    ed_countproduct.remove("countt");
//                    ed_countproduct.commit();

//                    ed_modifiers.clear();
//                    ed_modifiers.commit();
//
//                    itemArray.clear();
//
//                    adapter.updateNewItem(itemArray);

//                    count=0;
//                    tv_productcount.setBackgroundResource(0);
//                    tv_productcount.setText("");

                    if(position==0){
                        productOrService = 1;
                    }else{
                        productOrService = 0;
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

            ed_cartSave.remove("myCart");
            ed_cartSave.commit();

        }
    }

    private void setWidget() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dropDownParent = (LinearLayout) findViewById(R.id.dropDownParent);

        itemTax = new ArrayList<Taxes>();
        locationList = new ArrayList<BusinessLocationData>();

        dropdownLocation = (Spinner) findViewById(R.id.dropdownLocation);
        dropdownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0)
                    locationData = locationList.get(position);
                else
                    locationData = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencyList = new ArrayList<CurriencyData>();

        dropdownCurrency = (Spinner) findViewById(R.id.dropdownCurrency);
        dropdownCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currencyData = currencyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        customerList = new ArrayList<CustomerListData>();

        Spinner dropdownCustomer = (Spinner) findViewById(R.id.dropdownCustomer);
        dropdownCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                customerData = customerList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        customerSearchBox = (TextView) findViewById(R.id.et_customerSearchBox);
        ac_searchcustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                    if (s.length() > 0) {

                        ac_searchcustomer.setLoadingIndicator((android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));
                        quaryText = s.toString();
                        searchCollection(quaryText);

                    }


            }
        });
        staffList = new ArrayList<ServiceStaff>();
        dropdownStaff = (Spinner) findViewById(R.id.dropdownStaff);
        dropdownTable = (Spinner) findViewById(R.id.dropdownTable);

        RelativeLayout dropdownNext = (RelativeLayout) findViewById(R.id.dropdownNext);
        dropdownNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationData == null) {
                    AppConstant.showSnakeBar(dropDownParent, "Select location");
                } else {

                    if (orderr_type.equals("Select Order Type")) {
                        AppConstant.showSnakeBar(dropDownParent, "Select order type");
                    } else {
                        if (orderr_type.equals("Dine In")) {
                            selected_ordertype = "dine_in";
                        } else if (orderr_type.equals("Take Out")) {
                            selected_ordertype = "take_out";
                            tablee_id = "";
                            waiterr_id = "";
                        } else if (orderr_type.equals("Delivery")) {
                            selected_ordertype = "delivery";
                            tablee_id = "";
                            waiterr_id = "";
                        }
                        //Toast.makeText(ActivityPosTerminalDropdown.this, ""+currencyData.getName(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ActivityPosTerminalDropdown.this, ActivitySearchItemList.class);
                        intent.putExtra("location", locationData);
                        intent.putExtra("currency", currencyData);
                        intent.putExtra("customer", customerData);

                        if (customerData != null)
                        {
                            sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
                            ed_selectedcustomer = sp_selectedcustomer.edit();

                            if (customerData.getName().equalsIgnoreCase("Cash Customer") || customerData.getText().equalsIgnoreCase("Cash Customer"))
                            {
                                ed_selectedcustomer.putString("customernamee", "Cash Customer");
                            }else
                            {
                                ed_selectedcustomer.putString("customernamee", "Selected Customer");
                            }

                            ed_selectedcustomer.commit();
                        }

                        Log.e("CustomerData", String.valueOf(customerData));
                        intent.putExtra("selected_ordertype", selected_ordertype);
                        intent.putExtra("tablee_id", tablee_id);
                        intent.putExtra("waiterr_id", waiterr_id);
                        intent.putExtra("comingFrom", "ActivityPosTerminalDropDown");
                        intent.putExtra("productOrService", productOrService);
//                        intent.putExtra("customerId", customer);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                }
            }
        });
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
                    Intent i=new Intent(ActivityPosTerminalDropdown.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            });
        }
    }

    private void dropdownMenuCollection() {

        if(AppConstant.isNetworkAvailable(this)) {
            AppConstant.showProgress(ActivityPosTerminalDropdown.this, false);

            Retrofit retrofit = APIClient.getClientToken(ActivityPosTerminalDropdown.this);
            if (retrofit != null) {
                APIInterface apiService = retrofit.create(APIInterface.class);
                Call<PosLocationResponse> call = apiService.getPosData("pos");
                call.enqueue(new Callback<PosLocationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PosLocationResponse> call, @NonNull Response<PosLocationResponse> response) {

                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            PosLocationResponse posResponse = response.body();

                            Log.e("POS API DropDown", "" + posResponse);

                            // Log.e("msg", "onResponse: "+posResponse.getService_staff() );

                            if (posResponse.isSuccess()) {
                                itemTax = posResponse.getTaxes();

                                if (posResponse.getTaxes() == null) {
                                    itemTax = new ArrayList<Taxes>();
                                }

                                //save item tax in shared preference .
                                if(itemTax.size() > 0)
                                {
                                    String gson = new Gson().toJson(itemTax);
                                    ed_saveTax.putString("default_tax",gson);
                                    ed_saveTax.commit();
                                }



                                locationList = posResponse.getBusiness_locations();
                                currencyList = posResponse.getCurrencies();
                                customerList = posResponse.getWalk_in_customer();
                                staffList = posResponse.getService_staff();
                                tables_List = posResponse.getRes_tables();
                                customerData = customerList.get(0);
                                countryList = posResponse.getCountryLists();
                                defaultCurrency = posResponse.getDefault_currency();
                                customerSearchBox.setText(customerData.getName());
                                setAdapter();
                                ArrayList<CustomerListData> searchServiceList = new ArrayList();
                                for (int i = 0; i < staffList.size(); i++) {
                                    CustomerListData customerListData = new CustomerListData();
                                    customerListData.setId(Integer.parseInt(staffList.get(i).id));
                                    customerListData.setText(staffList.get(i).name);
                                    searchServiceList.add(customerListData);
                                }
                            Gson gson = new Gson();
                            ed_cartSave.putString("searchCustomerList", gson.toJson(searchServiceList));
                            ed_cartSave.commit();



                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(ActivityPosTerminalDropdown.this,"pos API","(ActivityPosTerminalDropdown Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(ActivityPosTerminalDropdown.this, "Unable To Fetch Default Menu Details , Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PosLocationResponse> call, Throwable t) {
                        AppConstant.hideProgress();
                        Toast.makeText(ActivityPosTerminalDropdown.this, "Unable To Fetch Default Menu Details , Please Try Again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else
        {
            AppConstant.openInternetDialog(this);
        }
    }

    private void setAdapter() {

        BusinessLocationData locationDumy = new BusinessLocationData();
        locationDumy.setId("-1");
        locationDumy.setName("Select Your Location");

        locationList.add(0, locationDumy);
        locationAdapter = new LocationAdapter(ActivityPosTerminalDropdown.this, locationList);
        dropdownLocation.setAdapter(locationAdapter);

        for (int i = 0; i < locationList.size(); i++) {
            locationData=locationList.get(1);
            dropdownLocation.setSelection(1);
        }
//
        currencyAdapter = new CurrencyAdapter(this, currencyList);
        dropdownCurrency.setAdapter(currencyAdapter);

        if (defaultCurrency != null) {

            int position = 0;
            for (position = 0; position < currencyList.size(); position++) {

                if (defaultCurrency != null) {

                    if (defaultCurrency.equalsIgnoreCase(currencyList.get(position).getName()))
                        break;
                }
            }
            dropdownCurrency.setSelection(position);
        }

        staffAdapter = new ServiceStaffAdapter(this, staffList, this);
        dropdownStaff.setAdapter(staffAdapter);

        tableListAdapter = new TableListAdapter(this, tables_List, this);
        dropdownTable.setAdapter(tableListAdapter);


        if (staffList.size() > 0) {
            // dropdownStaff.setVisibility(View.VISIBLE);
        } else {
            // dropdownStaff.setVisibility(View.GONE);
        }

        if (tables_List.size() > 0) {

            //dropdownTable.setVisibility(View.VISIBLE);
        } else {
            // dropdownTable.setVisibility(View.GONE);
        }

    }

    private void fetchPartialCustomer(final String customerid) {

        Retrofit retrofit = APIClient.getClientToken(ActivityPosTerminalDropdown.this);

        AppConstant.showProgress(this,false);

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<FetchPartialCustomer> call = apiService.getpartialCustomer("fetchPartialSale?contact_id=" + customerid);

            System.out.println("Partial Customer " + call.request().url());
            call.enqueue(new Callback<FetchPartialCustomer>() {
                @Override
                public void onResponse(@NonNull Call<FetchPartialCustomer> call, @NonNull Response<FetchPartialCustomer> response) {

                    AppConstant.hideProgress();
                    if (response.body() != null)
                    {
                        //AppConstant.hideProgress();
                        FetchPartialCustomer fp = response.body();

                        if (fp.isSuccess())
                        {
                                if (locationData == null) {
                                    AppConstant.showSnakeBar(dropDownParent, "Select location");
                                } else {

                                    //customer save type (selected or save.)
                                    if (customerData != null)
                                    {
                                        sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
                                        ed_selectedcustomer = sp_selectedcustomer.edit();

                                        if (customerData.getName().equalsIgnoreCase("Cash Customer") || customerData.getText().equalsIgnoreCase("Cash Customer"))
                                        {
                                            ed_selectedcustomer.putString("customernamee", "Cash Customer");
                                        }else
                                        {
                                            ed_selectedcustomer.putString("customernamee", "Selected Customer");
                                        }

                                        ed_selectedcustomer.commit();
                                    }

                                    Intent intent = new Intent(ActivityPosTerminalDropdown.this, PartialCustomerProducts.class);

                                    intent.putExtra("statuspartial", "true");
                                    intent.putExtra("partialcustomerid", customerid);

                                    if (orderr_type.equals("Dine In")) {
                                        selected_ordertype = "dine_in";
                                    } else if (orderr_type.equals("Take Out")) {
                                        selected_ordertype = "take_out";
                                        tablee_id = "";
                                        waiterr_id = "";
                                    } else if (orderr_type.equals("Delivery")) {
                                        selected_ordertype = "delivery";
                                        tablee_id = "";
                                        waiterr_id = "";
                                    }

                                    intent.putExtra("selected_ordertype", selected_ordertype);
                                    intent.putExtra("tablee_id", tablee_id);
                                    intent.putExtra("waiterr_id", waiterr_id);
                                    intent.putExtra("comingFrom", "ActivityPosTerminalDropDown");

                                    locationData.setId(fp.getTransaction().getLocation_id());
                                    intent.putExtra("location", locationData);
                                    intent.putExtra("currency", currencyData);
                                    intent.putExtra("customer", customerData);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);

                                }
                        } else {

                        }

                    } else {
                        //AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(ActivityPosTerminalDropdown.this,"fetchPartialSale API","(ActivityPosTerminalDropdown Screen)","Web API Error : API Response Is Null");
                       Toast.makeText(ActivityPosTerminalDropdown.this, "Failed Partial Customer : " + response.errorBody(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<FetchPartialCustomer> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(ActivityPosTerminalDropdown.this, "eror" + t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    public void tableselect(int position, String id, String name) {
        tablee_id = id;
    }

    @Override
    public void staffselect(int position, String id, String name) {
        waiterr_id = id;
        //  Toast.makeText(dropdownActivity, ""+waiterr_id, Toast.LENGTH_LONG).show();
    }


    private void searchCollection(final String quaryText) {

        Retrofit retrofit = APIClient.getClientToken(ActivityPosTerminalDropdown.this);
        if (retrofit != null) {

            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<ResponseBody> call = apiService.getCustomerList("get-customers?term=" + quaryText);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {

                            searchCustomerList.clear();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Search Customer Respon",respo.toString());

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                if(responseObject.has("contacts") && !responseObject.isNull("contacts"))
                                {
                                    JSONArray dataObj = responseObject.getJSONArray("contacts");
                                    for (int i = 0; i < dataObj.length(); i++) {

                                        JSONObject data = dataObj.getJSONObject(i);
                                        CustomerListData customerListData=new CustomerListData();
                                        customerListData.setId(data.getInt("id"));
                                        customerListData.setText(data.getString("text"));
                                        if(data.has("mobile") && !data.isNull("mobile")){
                                            customerListData.setMobile(data.getString("mobile"));
                                        }else{
                                            customerListData.setMobile("");
                                        }
                                        customerListData.setLandmark(data.getString("landmark"));
                                        customerListData.setCity(data.getString("city"));
                                        customerListData.setState(data.getString("state"));
                                        customerListData.setRegister_by(data.getString("register_by"));
                                        customerListData.setPay_term_number(data.getString("pay_term_number"));
                                        customerListData.setPay_term_type(data.getString("pay_term_type"));
                                        if(data.has("email") && !data.isNull("email")){
                                            customerListData.setEmail(data.getString("email"));
                                        }else{
                                            customerListData.setEmail("");
                                        }
                                        searchCustomerList.add(customerListData);

                                    }
                                }

                                customerSearchAdapter = new CustomerSearchAdapter(ActivityPosTerminalDropdown.this, searchCustomerList);
                                ac_searchcustomer.setAdapter(customerSearchAdapter);
                                ac_searchcustomer.setThreshold(1);

                            }

                        } else {
                            AppConstant.sendEmailNotification(ActivityPosTerminalDropdown.this,"get-customers API","(ActivityPosTerminalDropdown Screen)","Web API Error : API Response Is Null");
                            //AppConstant.hideProgress();
                            Toast.makeText(ActivityPosTerminalDropdown.this, "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    }
                }



                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //AppConstant.hideProgress();
                    Toast.makeText(ActivityPosTerminalDropdown.this, "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }



        ac_searchcustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;

                }

                ActivityPosTerminalDropdown.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                customerSearchBox.setText(quaryText);

                //-------store customer name
                ed_selectedcustomer.putString("customernamee",quaryText);
                ed_selectedcustomer.commit();


                fetchPartialCustomer(String.valueOf(customerData.getId())); //temp comment - partial customer work.
//                 Toast.makeText(ActivityPosTerminalDropdown.this, "Customer ID: "+customerData.getId(), Toast.LENGTH_LONG).show();
                Log.e("CustomeriD",""+customerData.getId());
                Log.e("Customer Name",""+customerData.getName());
                ac_searchcustomer.setText("");
                dialog.dismiss();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(HomeActivity.homeActivity !=null){
            HomeActivity.homeActivity.finish();
        }
        Intent i=new Intent(ActivityPosTerminalDropdown.this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
