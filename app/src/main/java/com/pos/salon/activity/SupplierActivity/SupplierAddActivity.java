package com.pos.salon.activity.SupplierActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.AddPurchaseActivity;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.posLocation.CountryData;
import com.pos.salon.newkotlin.AddSupplierModel;
import com.pos.salon.newkotlin.Response.AddResponseSupplier;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.SupplierActivity.SupplierDetailActivity.supplierDetailActivity;
import static com.pos.salon.activity.SupplierActivity.SupplierSection.supplierSectionActivity;

public class SupplierAddActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<CountryData> countriesList = new ArrayList();
    private Spinner spinner_country, spinner_register_by, spinner_state, spinner_city;
    private EditText et_suppier_first_name, et_suppier_last_name, et_supplier_business_name, et_supplier_mobile, et_supplier_landmark;
    private TextView txt_save_supplier;
    private String registeredMethod = "", getCountryId = "", state_id = "182", city_id = "29739", isComing = "";
    List<String> registered = new ArrayList<>();
    List<String> arrStateList = new ArrayList<>();
    List<String> arrCityList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AddSupplierModel userModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_add);

        SupplierAddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CountryData>>() {
        }.getType();

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String strcountryList = sharedPreferences.getString("country_list", "");

        if (strcountryList != null) {
            countriesList = (ArrayList<CountryData>) gson.fromJson(strcountryList, type);
        }

        fetchIds();

    }

    public void fetchIds() {

        userModel = new AddSupplierModel();
        spinner_country = findViewById(R.id.spinner_country);
        spinner_register_by = findViewById(R.id.spinner_register_by);
        spinner_state = findViewById(R.id.spinner_state);
        spinner_city = findViewById(R.id.spinner_city);
        txt_save_supplier = findViewById(R.id.txt_save_supplier);

        et_suppier_first_name = findViewById(R.id.et_suppier_first_name);
        et_suppier_last_name = findViewById(R.id.et_suppier_last_name);
        et_supplier_business_name = findViewById(R.id.et_supplier_business_name);
        et_supplier_mobile = findViewById(R.id.et_supplier_mobile);
        et_supplier_landmark = findViewById(R.id.et_supplier_landmark);

        registered = new ArrayList<String>();
        registered.add("Select Registration Method");
        registered.add("mobile");
        registered.add("email");

        arrStateList = new ArrayList<>();
        arrStateList.add("Cayman Island");

        arrCityList = new ArrayList<>();
        arrCityList.add("George Town");

        isComing = getIntent().getStringExtra("isComing");

        if (isComing.equalsIgnoreCase("toUpdate")) {

            txt_save_supplier.setText("Update");

            editContactDetail();
        } else {
            txt_save_supplier.setText("Save");

        }
        setBackNavgation();

        clickListeners();
    }

    public void clickListeners() {


        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        Log.e("countryCode", countryCode);
        String[] cArray = new String[countriesList.size() + 1];
        cArray[0] = "Select Country";
        ArrayAdapter<String> spnCountryAdapter = new ArrayAdapter<String>(SupplierAddActivity.this, android.R.layout.simple_spinner_item, cArray);
        spnCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_country.setAdapter(spnCountryAdapter);

            for (int i = 0; i < countriesList.size(); i++) {
                cArray[i + 1] = AppConstant.notNull(countriesList.get(i).getCountryName());
                if (countryCode.equalsIgnoreCase(countriesList.get(i).getcountry_code())) {
                    spinner_country.setSelection(i + 1);
                    break;
                }
        }


        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getCountryId = "";
                } else {
                    String getName = AppConstant.notNull(countriesList.get(position - 1).getId());
                    getCountryId = countriesList.get(position - 1).getId();

                    if (getName.isEmpty()) {

                        AppConstant.showToast(SupplierAddActivity.this, "Please select proper country.");
                        getCountryId = "";
                    }
//                        spinnerCountry.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SupplierAddActivity.this, android.R.layout.simple_spinner_item, registered);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_register_by.setAdapter(dataAdapter);

        spinner_register_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    registeredMethod = "";
                }else{
                    registeredMethod = spinner_register_by.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(SupplierAddActivity.this, android.R.layout.simple_spinner_item, arrStateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(stateAdapter);

        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(SupplierAddActivity.this, android.R.layout.simple_spinner_item, arrCityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(cityAdapter);

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        txt_save_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // openOtpDialog();
                String getFirstName = et_suppier_first_name.getText().toString().trim();
                String getLastName = et_suppier_last_name.getText().toString().trim();
                String getMobile = et_supplier_mobile.getText().toString().trim();
                String getBusiness = et_supplier_business_name.getText().toString().trim();
                String getLocation = et_supplier_landmark.getText().toString().trim();

                if (getFirstName.isEmpty()) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please enter first name");
                } else if (getLastName.isEmpty()) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please enter last name");
                } else if (registeredMethod.equalsIgnoreCase("")) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please Select Registration Method");
                } else if (getCountryId.equalsIgnoreCase("")) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please Select Country");
                } else if (getMobile.equalsIgnoreCase("")) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please Enter Mobile Number");
                } else if (state_id.equalsIgnoreCase("")) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please Select State");
                } else if (city_id.isEmpty()) {
                    AppConstant.showToast(SupplierAddActivity.this, "Please Select City");
                } else {
                    AppConstant.hideKeyboardFrom(SupplierAddActivity.this);

                    SupplierAddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    userModel = new AddSupplierModel();
                    userModel.setFirst_name(getFirstName);
                    userModel.setLast_name(getLastName);
                    userModel.setMobile(getMobile);
                    userModel.setState_id(state_id);
                    userModel.setCountry_id(getCountryId);
                    userModel.setCity_id(city_id);
                    userModel.setRegister_by(registeredMethod);
                    userModel.setType("supplier");
                    userModel.setSupplier_business_name(getBusiness);
                    userModel.setLandmark(getLocation);

                    if (isComing.equalsIgnoreCase("toUpdate")) {
//                        userModel.setReferral_contact_id(String.valueOf(getReferralContactId));
                        if (AppConstant.isNetworkAvailable(SupplierAddActivity.this)) {
                            callSupplierUpdateApi(); // update user detail for edit.
                        } else {
                            AppConstant.openInternetDialog(SupplierAddActivity.this);
                        }

                    } else {
                        callSaveSupplierApi();
                    }

                }
            }

        });

    }

    void callSaveSupplierApi() {

        AppConstant.showProgress(SupplierAddActivity.this, false);

        Retrofit retrofit = APIClient.getClientToken(SupplierAddActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<AddResponseSupplier> call = apiService.saveNewSupplierUser("contacts", userModel);
            call.enqueue(new Callback<AddResponseSupplier>() {
                @Override
                public void onResponse(@NonNull Call<AddResponseSupplier> call, @NonNull Response<AddResponseSupplier> response) {
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        registeredMethod = "";
                        AddResponseSupplier userResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(userResponse);
                        String modle = gson.toJson(userModel);
                        Log.e("model", modle.toString());
                        Log.e("json", json.toString());

                        Log.e("User Response : ", userResponse.getData() + "");

                        if (userResponse.getSuccess()) {
                            if (isComing.equalsIgnoreCase("AddPurchase")) {
                                if (AddPurchaseActivity.addPurchaseActivity != null) {
                                    AddPurchaseActivity.addPurchaseActivity.finish();
                                }

                                Intent i = new Intent(SupplierAddActivity.this, AddPurchaseActivity.class);
                                i.putExtra("comingFrom", "AddSupplier");
                                i.putExtra("supplierID", userResponse.getData().getId());
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                if(supplierSectionActivity !=null){
                                    supplierSectionActivity.finish();
                                }
                                AppConstant.showToast(SupplierAddActivity.this, userResponse.getMsg());
                                Intent i = new Intent(SupplierAddActivity.this, SupplierSection.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            AppConstant.showToast(SupplierAddActivity.this, userResponse.getMsg());
                        }

                    } else {
                        AppConstant.sendEmailNotification(SupplierAddActivity.this, "contacts API", "(SupplierAddActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(SupplierAddActivity.this, "Could Not Save Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddResponseSupplier> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierAddActivity.this, "Could Not Save Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void editContactDetail() {
       int contactId =getIntent().getIntExtra("contactId",0);
       Log.e("contactId", String.valueOf(contactId));
        AppConstant.showProgress(SupplierAddActivity.this, false);

        Retrofit retrofit = APIClient.getClientToken(SupplierAddActivity.this);
        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = apiService.getCustomersList("contacts/"+contactId +"/edit");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                AppConstant.hideProgress();

                try {
                    if (response.body() != null) {
                        AppConstant.hideProgress();
                        String respo = response.body().string();
                        JSONObject responseObject = new JSONObject(respo);
                        Log.e("Edit Response", response.toString());

                        String successstatus = responseObject.optString("success");
                        if (successstatus.equalsIgnoreCase("true")) {

                            if (responseObject.has("contact") && !responseObject.isNull("contact")) {
                                JSONObject contacts = responseObject.getJSONObject("contact");

                                et_suppier_first_name.setText(contacts.getString("first_name"));

                                if (contacts.has("last_name") && !contacts.isNull("last_name")) {
                                    et_suppier_last_name.setText(contacts.getString("last_name"));
                                }

                                if (contacts.has("country_id") && !contacts.isNull("country_id")) {
                                    getCountryId = contacts.getString("country_id");

                                    for (int i = 0; i < countriesList.size(); i++) {
                                        CountryData obj = countriesList.get(i);

                                        if (obj.getId().equalsIgnoreCase(getCountryId)) {
                                            spinner_country.setSelection(i + 1);
                                            break;
                                        }
                                    }
                                }

                                if (contacts.has("mobile") && !contacts.isNull("mobile")) {
                                    et_supplier_mobile.setText(contacts.getString("mobile"));
                                }
                                if (contacts.has("supplier_business_name") && !contacts.isNull("supplier_business_name")) {
                                    et_supplier_business_name.setText(contacts.getString("supplier_business_name"));
                                }
                                if (contacts.has("landmark") && !contacts.isNull("landmark")) {
                                    et_supplier_landmark.setText(contacts.getString("landmark"));
                                }

//                                if (contacts.has("email") && !contacts.isNull("email")) {
//                                    et.setText(contacts.getString("email"));
//                                }

//                                if (contacts.has("state") && !contacts.isNull("state")) {
//                                    etState.setText(contacts.getString("state"));
//                                }

//                                if (contacts.has("city") && !contacts.isNull("city")) {
//                                    etCityName.setText(contacts.getString("city"));
//                                }


                                if (contacts.has("register_by") && !contacts.isNull("register_by")) {
                                    registeredMethod = contacts.getString("register_by");

                                    for (int i = 0; i < registered.size(); i++) {
                                        if (registeredMethod.equalsIgnoreCase(registered.get(i).toString())) {
                                            spinner_register_by.setSelection(i);
                                            break;
                                        }

                                    }

                                }

                            }

                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(SupplierAddActivity.this, "contacts/edit API", "(SupplierAddActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(SupplierAddActivity.this, "Could Not Update Supplier Data. Please Try Again", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    AppConstant.hideProgress();
                    Log.e("Exception", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppConstant.hideProgress();
                Toast.makeText(SupplierAddActivity.this, "Could Not Update Supplier Data. Please Try Again", Toast.LENGTH_LONG).show();
            }

        });
    }

    void callSupplierUpdateApi() {

        AppConstant.showProgress(this, false);

        Retrofit retrofit = APIClient.getClientToken(SupplierAddActivity.this);

        if (retrofit != null) {
//            AppConstant.hideProgress();
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddResponseSupplier> call = apiService.updateSupplierData("contacts/" + getIntent().getIntExtra("contactId", 0), userModel);
            call.enqueue(new Callback<AddResponseSupplier>() {
                @Override
                public void onResponse(@NonNull Call<AddResponseSupplier> call, @NonNull Response<AddResponseSupplier> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();
                    if (response.body() != null) {

                        AddResponseSupplier userResponse = response.body();

                        if (userResponse.getData() != null) {

                            if (userResponse.getSuccess()) {
                                if (supplierSectionActivity != null) {
                                    supplierSectionActivity.finish();
                                }
                                if (supplierDetailActivity != null) {
                                    supplierDetailActivity.finish();
                                }
                                AppConstant.showToast(SupplierAddActivity.this, "" + userResponse.getMsg());
                                Intent i = new Intent(SupplierAddActivity.this, SupplierSection.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                AppConstant.showToast(SupplierAddActivity.this, "" + userResponse.getMsg());
                            }

                        }

                    } else {
                        AppConstant.sendEmailNotification(SupplierAddActivity.this, "contacts Edit API", "(SupplierAddActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(SupplierAddActivity.this, "Could Not Save Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddResponseSupplier> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierAddActivity.this, "Could Not Save Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (isComing.equalsIgnoreCase("toUpdate")) {
                getSupportActionBar().setTitle("EDIT SUPPLIER");
            } else {
                getSupportActionBar().setTitle("ADD SUPPLIER");
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    Intent i=new Intent(AddRepairActivity.this, HomeActivity.class);
//                    startActivity(i);
//                    finish();
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
}
