
package com.pos.salon.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.CountryData;
import com.pos.salon.newkotlin.ActivateUserModel;
import com.pos.salon.newkotlin.AddUserModel;
import com.pos.salon.newkotlin.CheckEmailModel;
import com.pos.salon.newkotlin.CheckMobileMode;
import com.pos.salon.newkotlin.ResendOtpModel;
import com.pos.salon.newkotlin.Response.AddUserResposne;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddCustomerActivity extends AppCompatActivity {

    Context currentContext;
    CustomerListData customerData;
    TextView btnSave, txt_title;
    Spinner spinnerCountry, spinner_register_by;
    EditText etFirstName, etLastName, etMobile, etEmail, etState, etCityName;
    ImageView ivBack;
    int getContactId = 0, iIsUserActive = 0;
    String getCountryId = "";
    String isComing = "", quaryText = "",getReferralContactId="";
    String[] registeredBy = {"00", "01", "02", "03"};
    String registeredMethod = "";
    AddUserModel userModel = null;
    DelayAutoCompleteTextView et_search_customer_name;
    ArrayList<CountryData> countriesList = new ArrayList();
    public boolean isScrolling = true;
    ArrayList<CustomerListData> searchCustomerList = new ArrayList<>();
    CustomerSearchAdapter customerSearchAdapter;
    List<String> registered=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addcustomer);

        currentContext = this;

        AddCustomerActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    // initialize all variables
    public void init() {

        userModel = new AddUserModel();

        btnSave = findViewById(R.id.btnSave);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);
        etState = findViewById(R.id.etState);
        etCityName = findViewById(R.id.etCityName);
        ivBack = findViewById(R.id.ivBack);
        txt_title = findViewById(R.id.txt_title);
//        spinner_referral_from = findViewById(R.id.spinner_referral_from);
        et_search_customer_name = findViewById(R.id.et_search_customer_name);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinner_register_by = findViewById(R.id.spinner_registered_by);

//        customerList();

        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();

        Log.e("countryCode", countryCode);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                finish();
            }
        });


        registered = new ArrayList<String>();
        registered.add("Select Registration Method");
        registered.add("mobile");
        registered.add("email");


        isComing = getIntent().getStringExtra("isComing");


        if (isComing.equalsIgnoreCase("toUpdate")) {

            btnSave.setText("Update");

            txt_title.setText("EDIT CUSTOMER");

            getContactId = getIntent().getIntExtra("contactId", 0); // current contact id.
        } else {
            btnSave.setText("Save");

            txt_title.setText("ADD CUSTOMER");
        }

        countriesList = new Gson().fromJson(getIntent().getStringExtra("country_list"), new TypeToken<ArrayList<CountryData>>() {
        }.getType());

//
//        spinner_referral_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    getCustomerName = "";
//
////                    spinnerCountry.getSelectedItem().toString();
//                } else {
//                    String getName = AppConstant.notNull(customerList.get(position - 1).getName());
//                    getCustomerName = customerList.get(position - 1).getName();
//
//                    if (getName.isEmpty()) {
//
//                        AppConstant.showToast(currentContext, "Please select Referal Customer.");
//                        getCustomerName = "";
//                    }
////                        spinnerCountry.getSelectedItem().toString();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//
//            }
//        });


        String[] cArray = new String[countriesList.size() + 1];

        cArray[0] = "Select Country";


        ArrayAdapter<String> spnCountryAdapter = new ArrayAdapter<String>(AddCustomerActivity.this, android.R.layout.simple_spinner_item, cArray);
        spnCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(spnCountryAdapter);

        for (int i = 0; i < countriesList.size(); i++) {

            cArray[i + 1] = AppConstant.notNull(countriesList.get(i).getCountryName());
            if (countryCode.equalsIgnoreCase(countriesList.get(i).getcountry_code())) {
                spinnerCountry.setSelection(i + 1);
            }
        }


        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getCountryId = "";
//                   spinnerCountry.getSelectedItem().toString();
                } else {
                    String getName = AppConstant.notNull(countriesList.get(position - 1).getId());
                    getCountryId = countriesList.get(position - 1).getId();

                    if (getName.isEmpty()) {

                        AppConstant.showToast(currentContext, "Please select proper country.");
                        getCountryId = "";
                    }
//                        spinnerCountry.getSelectedItem().toString();
                }

                System.out.println("The Selected Country id is -> " + getCountryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddCustomerActivity.this, android.R.layout.simple_spinner_item, registered);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_register_by.setAdapter(dataAdapter);

        spinner_register_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                registeredMethod = registeredBy[position];

                registeredMethod = spinner_register_by.getSelectedItem().toString();

                if (!registeredMethod.equalsIgnoreCase("00")) {
                    registeredMethod = spinner_register_by.getSelectedItem().toString();
                } else {
                    registeredMethod = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        et_search_customer_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if(AppConstant.isNetworkAvailable(AddCustomerActivity.this)){
                        quaryText = s.toString();
                        customerList(quaryText);
                    }else{
                        AppConstant.openInternetDialog(AddCustomerActivity.this);
                    }

                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openOtpDialog();
                String getFirstName = etFirstName.getText().toString().trim();
                String getLastName = etLastName.getText().toString().trim();
                String getMobile = etMobile.getText().toString().trim();
                String getEmail = etEmail.getText().toString().trim();
                String getState = etState.getText().toString().trim();
                String getCity = etCityName.getText().toString().trim();
                String getReferral = et_search_customer_name.getText().toString().trim();


                if (getFirstName.isEmpty()) {
                    AppConstant.showToast(currentContext, "Please enter first name");
                } else if (getLastName.isEmpty()) {
                    AppConstant.showToast(currentContext, "Please enter last name");
                }
//                else if () {
//                    AppConstant.showToast(currentContext, "Please enter mobile number");
//                }
                else if (registeredMethod.equalsIgnoreCase("Select Registration Method")) {
                    AppConstant.showToast(currentContext, "Please Select Registration Method");
                }
                else if(registeredMethod.equalsIgnoreCase("mobile") && getMobile.isEmpty()){
                        AppConstant.showToast(currentContext, "Please Enter Mobile Number");
                }

                else if(registeredMethod.equalsIgnoreCase("email") && getEmail.isEmpty()){
                        AppConstant.showToast(currentContext, "Please Enter Email Address");
                }
                else if (!getEmail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()){
                        AppConstant.showToast(currentContext, "Please enter valid email address");
                }
               else if (getState.isEmpty()) {
                    AppConstant.showToast(currentContext, "Please enter State name");
                }
               else if (getCity.isEmpty()) {
                    AppConstant.showToast(currentContext, "Please enter City name");
                }

//                else if (getReferral.isEmpty()) {
//                    AppConstant.showToast(currentContext, "Please Select Whom You get Referal From");
//                }
                else {
                    AppConstant.hideKeyboardFrom(currentContext);

                    AddCustomerActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    userModel = new AddUserModel();
                    userModel.setFirst_name(getFirstName);
                    userModel.setLast_name(getLastName);
                    userModel.setMobile(getMobile);
                    userModel.setEmail(getEmail);
                    userModel.setState(getState);
                    userModel.setState_id("182");
                    userModel.setCountry_id(getCountryId);
                    userModel.setCity(getCity);
                    userModel.setCity_id("29739");
                    userModel.setRegister_by(registeredMethod);
                    userModel.setReferral_contact_id(String.valueOf(getReferralContactId));
                    userModel.setType("customer");

                    if (isComing.equalsIgnoreCase("toUpdate")) {
//                        userModel.setReferral_contact_id(String.valueOf(getReferralContactId));
                        if(AppConstant.isNetworkAvailable(AddCustomerActivity.this)){
                            callEditUserDataUpdateApi(); // update user detail for edit.
                        }else{
                            AppConstant.openInternetDialog(AddCustomerActivity.this);
                        }

                    } else {
                        checkEmailExists();
                        userModel.setReferral_contact_id("");
                    }

                }
            }
        });

        // contact info load all details when edit
        if (isComing.equalsIgnoreCase("toUpdate")) {
            if(AppConstant.isNetworkAvailable(AddCustomerActivity.this)){
                editContactDetail();
            }else{
                AppConstant.openInternetDialog(AddCustomerActivity.this);
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    // Open Otp Dialog for matching otp
    void openOtpDialog(String contactNo) {

        final Dialog dialog = new Dialog(this);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_otp_verification);

        Button activateWithoutOtpBtn = dialog.findViewById(R.id.btnActivateWithoutOtp);
        Button activateBtn = dialog.findViewById(R.id.btnActivate);
        TextView resendBtn = dialog.findViewById(R.id.btnResendOtp);
        TextView msgText = dialog.findViewById(R.id.msg);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);

        msgText.setText("Please Enter Verification Code");

        final EditText editText = dialog.findViewById(R.id.etPin);

        dialog.show();

        activateWithoutOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userActiveWithoutOtp("", 0, dialog);
                dialog.dismiss();
//                finish();
            }
        });
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                dialog.dismiss();
                callResendCode();
                editText.setText("");
            }
        });


        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String getOtp = editText.getText().toString().trim();
                if (getOtp.isEmpty()) {
                    Toast.makeText(currentContext, "Please Enter OTP ", Toast.LENGTH_LONG).show();
                } else {

                    userActiveWithoutOtp(getOtp, 1, dialog);
                }

            }
        });

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    void callSaveUserDataApi() {

//        AppConstant.showProgress(this,false);

        System.out.println("Sending data to server-> FirstName " + userModel.getFirst_name() + " Last name " + userModel.getLast_name()
                + " Email " + userModel.getEmail() + " Phone " + userModel.getMobile() + " Country COde " + userModel.getCountry_id() + " Statte " + userModel.getState() +
                " City " + userModel.getCity() + " Referral_contact_id " + userModel.getReferral_contact_id());

        Log.e("Country Code", userModel.getCountry_id());

        System.out.println("JSOn->  " + new Gson().toJson(userModel));

        Log.e("userModel", String.valueOf(userModel));

        Retrofit retrofit = APIClient.getClientToken(currentContext);

        if (retrofit != null) {

//            AppConstant.hideProgress();
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddUserResposne> call = apiService.saveNewUser("contacts", userModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        registeredMethod = "";
                        AddUserResposne userResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(userResponse);
                        Log.e("json",json.toString());

                        Log.e("User Response : ", userResponse.getData() + "");
                        System.out.println("The Add user Response " + userResponse.getMsg());
                        Log.e("Adduser resopnse ", "" + userResponse.getData().getContact_id());

                        if(userResponse.getSuccess()){
                            if (!userResponse.getData().getContact_id().equalsIgnoreCase("0")) {
                                getContactId = userResponse.getData().getId();
                                Log.e("GetContact iD", String.valueOf(getContactId));
                                openOtpDialog(userResponse.getData().getCountry_id() + " " + userResponse.getData().getMobile());
                                AppConstant.showToast(currentContext, userResponse.getMsg());
                            }
//                            // Log.e("msg", "onResponse: "+posResponse.getService_staff() );
//                            AppConstant.showToast(currentContext, "Unable to add New User. Please Try Again");
                        }    else {
                            AppConstant.showToast(currentContext, userResponse.getMsg());
                        }


                    } else {
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"contacts API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Could Not Save Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Could Not Save Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void callEditUserDataUpdateApi() {

        AppConstant.showProgress(this, false);
//        https:
//dev.shopplusglobal.com/beta/pos/public/api/contacts/592922116
        System.out.println("Sending data to server-> FirstName " + userModel.getFirst_name() + " Last name " + userModel.getLast_name()
                + " Email " + userModel.getEmail() + " Phone " + userModel.getMobile() + " Country COde " + userModel.getCountry_id() + " State " + userModel.getState() +
                " City " + userModel.getCity() + " Referral_contact_id " + userModel.getReferral_contact_id());

        Log.e("Country Code", userModel.getCountry_id());

        System.out.println("JSOn->  " + new Gson().toJson(userModel));

        Log.e("userModel", String.valueOf(userModel));

        Retrofit retrofit = APIClient.getClientToken(currentContext);

        if (retrofit != null) {

//            AppConstant.hideProgress();
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddUserResposne> call = apiService.updateUserData("contacts/" + getContactId, userModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();
                    if (response.body() != null) {

                        AddUserResposne userResponse = response.body();
                        System.out.println("The update user Response " + userResponse.getMsg());
                        Log.e("update user resopnse ", "" + userResponse.getData().getContact_id());


                        if (userResponse.getData() != null) {
                            if(userResponse.getSuccess()){
//                                CustomerListFragment ldf = new CustomerListFragment();
//                                getSupportFragmentManager().beginTransaction().replace(R.id.layout_customer, ldf).commit();
//                                HomeActivity.tool_title.setText(" CUSTOMER LIST");
//                                // Log.e("msg", "onResponse: "+posResponse.getService_staff() );
                                if(HomeActivity.homeActivity !=null){
                                    HomeActivity.homeActivity.finish();
                                }
                                Intent intent=new Intent(AddCustomerActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }else{
                                AppConstant.showToast(currentContext, userResponse.getMsg());
                            }


                        }

                    } else {
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"contacts Edit API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Could Not Save Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Could Not Save Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // Call Resend Code if user not Received any code
    void callResendCode() {
        ResendOtpModel otpModel = new ResendOtpModel();
        otpModel.setContact_id(String.valueOf(getContactId));
        Log.e("Contact Id", String.valueOf(getContactId));

        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(currentContext);

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddUserResposne> call = apiService.callResendOtp("resendCode", otpModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();
                    if (response.body() != null) {

                        AddUserResposne userResponse = response.body();
                        Log.e("OTP Resend Response", userResponse.toString());

                        Log.e("Resend Otp Model", "" + userResponse.getData().getContact_id());
                        AppConstant.showToast(currentContext, userResponse.getMsg());
                        Log.e("Resend response", "" + userResponse.getMsg());
                        // Log.e("msg", "onResponse: "+posResponse.getService_staff() );
                    } else {
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"resendCode API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Unable To Resend Code Again. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Unable To Resend Code Again. Please Try Aagain", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // Call Resend Code if user not Received any code
    void userActiveWithoutOtp(final String otp, final int isActive, final Dialog objDialog) {

        final ActivateUserModel otpModel = new ActivateUserModel();
        otpModel.setContact_id(String.valueOf(getContactId));
        otpModel.set_active(isActive);
        otpModel.setOtp(otp);

        AppConstant.showProgress(this, false);

        AppConstant.hideKeyboardFrom(currentContext);

        Retrofit retrofit = APIClient.getClientToken(currentContext);

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<AddUserResposne> call = apiService.callActivateUser("activateContact", otpModel);
            call.enqueue(new Callback<AddUserResposne>() {
                @Override
                public void onResponse(@NonNull Call<AddUserResposne> call, @NonNull Response<AddUserResposne> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    AppConstant.hideProgress();

                    if (response.body() != null) {

                        Log.e("otp response", response.toString());

                        AddUserResposne userResponse = response.body();

                        if (isActive == 1) {
                            if (otpModel.getOtp().equalsIgnoreCase(otp)) {
                                Toast.makeText(currentContext, "User Added  Successfully", Toast.LENGTH_LONG).show();

                                objDialog.dismiss();
                                if(HomeActivity.homeActivity !=null){
                                    HomeActivity.homeActivity.finish();
                                }
                                Intent intent=new Intent(AddCustomerActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(currentContext, "Enter a valid OTP", Toast.LENGTH_LONG).show();
                            }


                        } else if (isActive == 0) {
                            Toast.makeText(currentContext, "User Added  Successfully", Toast.LENGTH_LONG).show();
                            objDialog.dismiss();

                            finish();
                        }
                        Log.e("Resend Otp Model ", "" + userResponse.getData().getContact_id());
                        // Log.e("msg", "onResponse: "+posResponse.getService_staff() );

                    } else {

                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"activateContact API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Some Error To Add User. Please Try Again", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<AddUserResposne> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Some Error To Add User. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Check Email and password//


    // Call Resend Code if user not Received any code
    void checkEmailExists() {

      final  CheckEmailModel model = new CheckEmailModel();
        model.setEmail(userModel.getEmail());

        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(currentContext);

        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<Boolean> call = apiService.callCheckEmail("contacts/check-email", model);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();

                    if (response.body() != null) {

                        Gson gson = new Gson();
                        String json = gson.toJson(model);
                        Log.e("json",json.toString());

                        Boolean boolResponse = response.body();

                        if (boolResponse) {
                            checkPhoneExists();
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.showToast(currentContext, "Email Exists. " + "Please Try With Different Email Address");
                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"contacts/check-email API","(AddCustomerActivity SCreen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Could Not Check Your Email. Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
//                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Could Not Check Your Email. Please Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // Call Resend Code if user not Received any code
    void checkPhoneExists() {

        CheckMobileMode model = new CheckMobileMode();
        model.setMobile(userModel.getMobile());

        Retrofit retrofit = APIClient.getClientToken(currentContext);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<Boolean> call = apiService.callCheckMobile("contacts/check-mobile", model);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    //Toast.makeText(ActivityPosTerminalDropdown.this, ""+response.body(), Toast.LENGTH_LONG).show();
                    if (response.body() != null) {

                        Boolean boolResponse = response.body();
                        if (boolResponse) {
                            if (isComing.equalsIgnoreCase("toUpdate")) {

                            } else {
                                if(AppConstant.isNetworkAvailable(AddCustomerActivity.this)){
                                    callSaveUserDataApi(); // save new customer details.
                                }else{
                                    AppConstant.openInternetDialog(AddCustomerActivity.this);
                                }


                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.showToast(currentContext, "Mobile number Exists. " + "Please try with different mobile number");
                        }

                    } else {
//                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"contacts/check-mobile API","(AddCustomerActivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(currentContext, "Could not check your Mobile Number. Please try again.\", Toast.LENGTH_LONG).show();\n" + "}", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(currentContext, "Could Not Check Your Mobile Number. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void editContactDetail() {

        AppConstant.showProgress(AddCustomerActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(AddCustomerActivity.this);

        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = apiService.getCustomersList("contacts/" + getIntent().getIntExtra("contactId", 0) + "/edit");
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


                                etFirstName.setText(contacts.getString("first_name"));

                                if (contacts.has("last_name") && !contacts.isNull("last_name")) {
                                    etLastName.setText(contacts.getString("last_name"));
                                }

                                if (contacts.has("country_id") && !contacts.isNull("country_id")) {
                                    getCountryId = contacts.getString("country_id");

                                    for (int i = 0; i < countriesList.size(); i++) {
                                        CountryData obj = countriesList.get(i);

                                        if (obj.getId().equalsIgnoreCase(getCountryId)) {
                                            spinnerCountry.setSelection(i+1);
                                            break;
                                        }
                                    }
                                }

                                if (contacts.has("mobile") && !contacts.isNull("mobile")) {
                                    etMobile.setText(contacts.getString("mobile"));
                                }

                                if (contacts.has("email") && !contacts.isNull("email")) {
                                    etEmail.setText(contacts.getString("email"));
                                }

                                if (contacts.has("state") && !contacts.isNull("state")) {
                                    etState.setText(contacts.getString("state"));
                                }

                                if (contacts.has("city") && !contacts.isNull("city")) {
                                    etCityName.setText(contacts.getString("city"));
                                }

                                if (contacts.has("referral") && !contacts.isNull("referral")) {

                                    JSONObject referralObj=contacts.getJSONObject("referral");

                                    if(referralObj.has("id") && !referralObj.isNull("id")){
                                        getReferralContactId=String.valueOf(referralObj.getInt("id"));
                                    }else{
                                        getReferralContactId="";
                                    }

                                    if(referralObj.has("name") && !referralObj.isNull("name")){
                                        et_search_customer_name.setText(referralObj.getString("name"));
                                    }else{
                                        et_search_customer_name.setText("Select Customer");
                                    }


                                }


                                if (contacts.has("isActive") && !contacts.isNull("isActive")) {
                                    if (contacts.getInt("isActive") == 1) {
                                        // etMobile.setEnabled(true);
                                        //  spinnerCountry.setEnabled(true);
                                        iIsUserActive = 1;
                                    } else {
                                        // etMobile.setEnabled(false);
                                        // spinnerCountry.setEnabled(false);
                                        iIsUserActive = 0;
                                    }
                                }

                                if(contacts.has("register_by") && !contacts.isNull("register_by")){
                                    registeredMethod=contacts.getString("register_by");
                                    for(int i=0; i<registered.size();i++){
                                        if(registeredMethod.equalsIgnoreCase(registered.get(i).toString())){
                                            spinner_register_by.setSelection(i);
                                        }

                                    }

                                }

                            }

                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"contacts/edit API","(AddCustomerACtivity Screen)","Web API Error : API Response Is Null");
                        Toast.makeText(AddCustomerActivity.this, "Could Not Update Customer Data. Please Try Again", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    AppConstant.hideProgress();
                    Log.e("Exception", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppConstant.hideProgress();
                Toast.makeText(AddCustomerActivity.this, "Could Not Update Customer Data. Please Try Again", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void customerList(final String quaryText) {

        Retrofit retrofit = APIClient.getClientToken(AddCustomerActivity.this);
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

                        String successstatus = responseObject.optString("success");
                        //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                        if (successstatus.equalsIgnoreCase("true")) {

                            if (responseObject.has("contacts") && !responseObject.isNull("contacts")) {
                                JSONArray dataObj = responseObject.getJSONArray("contacts");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    CustomerListData customerListData = new CustomerListData();
                                    customerListData.setId(data.getInt("id"));
                                    customerListData.setText(data.getString("text"));

                                    if (data.has("mobile") && !data.isNull("mobile")) {
                                        customerListData.setMobile(data.getString("mobile"));
                                    } else {
                                        customerListData.setMobile("");
                                    }
                                    customerListData.setLandmark(data.getString("landmark"));
                                    customerListData.setCity(data.getString("city"));
                                    customerListData.setState(data.getString("state"));
                                    customerListData.setRegister_by(data.getString("register_by"));
                                    customerListData.setPay_term_number(data.getString("pay_term_number"));
                                    customerListData.setPay_term_type(data.getString("pay_term_type"));

                                    if (data.has("email") && !data.isNull("email")) {
                                        customerListData.setEmail(data.getString("email"));
                                    } else {
                                        customerListData.setEmail("");
                                    }


                                    searchCustomerList.add(customerListData);

                                }
                            }

                            customerSearchAdapter = new CustomerSearchAdapter(AddCustomerActivity.this, searchCustomerList);
                            et_search_customer_name.setAdapter(customerSearchAdapter);
                            et_search_customer_name.setThreshold(1);

                        }

                    } else {
                        AppConstant.sendEmailNotification(AddCustomerActivity.this,"get-customers? API","(AddCustmerActivity Screen)","Web API Error : API Response Is Null");
                        //AppConstant.hideProgress();
                        Toast.makeText(AddCustomerActivity.this, "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //AppConstant.hideProgress();
                Toast.makeText(AddCustomerActivity.this, "Could Not Load Customer List. Please Try Again.", Toast.LENGTH_LONG).show();
            }
        });

        et_search_customer_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }
                et_search_customer_name.setText(quaryText);
                getReferralContactId = String.valueOf(customerData.getId());

                Log.e("getReferralContactId", getReferralContactId);


            }
        });
    }



//                try {
//                    if (response.body() != null) {
//                        AppConstant.hideProgress();
//                        progressBar.setVisibility(View.GONE);
//                        String respo = response.body().string();
//                        JSONObject responseObject = new JSONObject(respo);
//                        Log.e("CustomerList", response.toString());
//
//                        String successstatus = responseObject.optString("success");
//                        if (successstatus.equalsIgnoreCase("true")) {
//                            JSONArray dataObj = responseObject.getJSONArray("contacts");
//                            for (int i = 0; i < dataObj.length(); i++) {
//
//                                JSONObject data = dataObj.getJSONObject(i);
//                                CustmerListModel listPosModel = new CustmerListModel();
//                                listPosModel.setId(data.getInt("id"));
//                                listPosModel.setName(data.getString("name"));
//                                customerList.add(listPosModel);
//                                Log.e("Customermodel", "" + listPosModel.getName());
//
//                            }
//
//                            String[] custmerArray = new String[customerList.size() + 1];
//                            custmerArray[0] = "Select Customer";
//
//                            for (int i = 0; i < customerList.size(); i++) {
//
//                                custmerArray[i + 1] = AppConstant.notNull(customerList.get(i).getName());
//
//                            }
//
//                             spnCustomerAdapter = new ArrayAdapter<String>(AddCustomerActivity.this, android.R.layout.simple_list_item_1, custmerArray);
//                            list_view.setAdapter(spnCustomerAdapter);
//
//                            spnCustomerAdapter.notifyDataSetChanged();
//
////                                spnCustomerAdapter = new ArrayAdapter<String>(AddCustomerActivity.this, android.R.layout.simple_spinner_item, custmerArray);
////                                spnCustomerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                                spinner_referral_from.setAdapter(spnCustomerAdapter);
////
////                                int n = spnCustomerAdapter.getCount();
////                                Log.e("posi", String.valueOf(n));
////
////                                spnCustomerAdapter.notifyDataSetChanged();
//                        }
//
//                    } else {
//                        AppConstant.hideProgress();
//                        Toast.makeText(currentContext, "Failed : Could not load Customer list. Please try again.", Toast.LENGTH_LONG).show();
//                    }
//

//    }


//        EditText edt_searchCustomer;
//        customerDialog = new Dialog(AddCustomerActivity.this);
//        customerDialog.setContentView(R.layout.listview);
//        list_view = customerDialog.findViewById(R.id.list_view);
//        progressBar = customerDialog.findViewById(R.id.progressBar);
//        edt_searchCustomer = customerDialog.findViewById(R.id.edt_ref_searchCustomer);
//        Window window = customerDialog.getWindow();
//        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//
//        customerList("");
//
//        edt_searchCustomer.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//                if (s.length() > 0) {
//                    quaryText = s.toString();
//
//                    customerList(quaryText);
//
//                } else {
//
//                    customerList("");
//                }
//
//            }
//        });
//
//        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                getCustomerName = list_view.getItemAtPosition(position) + "";
//
//                Log.e("name", getCustomerName);
//
//                txt_referral_from.setText(getCustomerName);
//
//                if(getCustomerName.equalsIgnoreCase("Select Customer")){
//                    getReferralContactId = 0;
//                }else{
//                    getReferralContactId = customerList.get(position - 1).getId();
//                }
//
//                Log.e("contact", String.valueOf(getReferralContactId));
//                customerDialog.dismiss();
//            }
//        });
//
//        list_view.setOnScrollListener(new AbsListView.OnScrollListener(){
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {}
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if (isScrolling) {
//                    if (totalItemCount > previousTotal) {
//                        isScrolling = false;
//                        previousTotal = totalItemCount;
//                        currentPage++;
//                    }
//                }
//                if (!isScrolling && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//
//                   customerList(quaryText);
//                    isScrolling = true;
//                }

//
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if((lastInScreen == totalItemCount) && isScrolling){
//
//                    customerList();
//                }
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if((lastInScreen == totalItemCount) && !(isScrolling))
//                {
//                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
//                    isScrolling = false;
//
////                    scrollMyListViewToBottom();
//                    customerList();
//                }


//            }

//        });




}
