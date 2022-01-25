package com.pos.salon.activity.UserManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.ProductsAdapters.TagAdapter;
import com.pos.salon.adapter.ServiceSearchAdapter;
import com.pos.salon.adapter.SpinnerSelectionAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.repairModel.SpinnerSelectionModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import static com.pos.salon.activity.UserManagement.UserDetailViewActivity.userDetailViewActivity;
import static com.pos.salon.activity.UserManagement.UserManageActivity.userManageActivityActivity;

public class AddUserManageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public String isComing = "", userTypeID = "", is_active = "";
    public int roleId = 0;
    public TextView txtConfirmPass,txtPassword,txt_save_user;
    public ArrayList<SpinnerSelectionModel> arrRoleList = new ArrayList<>();
    public ArrayList<SpinnerSelectionModel> arrUserTypeList = new ArrayList<>();
    public Spinner dropDown_UserType, dropDown_Role;
    public EditText et_user_prefix, et_user_first_name, et_user_last_name, et_user_email, et_user_Username, et_user_password, et_user_confirmPassword;
    public CheckBox ch_isActive;
    DelayAutoCompleteTextView et_service;
    TagAdapter tagAdapter;
    RecyclerView recycler_Service;
    ArrayList<String> nameServicelist = new ArrayList<>();
    ArrayList<String> serviceIDlist = new ArrayList<>();
    ServiceSearchAdapter cutoCompleteCustomerAapter;
    ArrayList<CustomerListData> searchServiceList = new ArrayList();
    private CustomerListData customerData;
    SharedPreferences.Editor ed_cartSave;
    SharedPreferences sp_cartSave;
    LinearLayout lay_UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_manage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();
        sp_cartSave = this.getSharedPreferences("searchCustomerList", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

    }

    public void findViews() {
        txt_save_user = findViewById(R.id.txt_save_user);
        dropDown_UserType = findViewById(R.id.dropDown_UserType);
        dropDown_Role = findViewById(R.id.dropDown_Role);
        et_user_prefix = findViewById(R.id.et_user_prefix);
        et_user_first_name = findViewById(R.id.et_user_first_name);
        et_user_last_name = findViewById(R.id.et_user_last_name);
        et_user_email = findViewById(R.id.et_user_email);
        et_user_Username = findViewById(R.id.et_user_Username);
        et_user_password = findViewById(R.id.et_user_password);
        et_user_confirmPassword = findViewById(R.id.et_user_confirmPassword);
        ch_isActive = findViewById(R.id.ch_isActive);
        et_service = findViewById(R.id.et_service);
        recycler_Service = findViewById(R.id.recycler_Service);
        lay_UserName = findViewById(R.id.lay_UserName);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);


        isComing = getIntent().getStringExtra("isComing");

        if (isComing.equalsIgnoreCase("toUpdate")) {
            txt_save_user.setText("Update");
            txtPassword.setText("Password:");
            txtConfirmPass.setText("Confirm Password:");
            lay_UserName.setVisibility(View.GONE);

//            editContactDetail();
        } else {
            txt_save_user.setText("Save");
            txtPassword.setText("Password:*");
            txtConfirmPass.setText("Confirm Password:*");
            lay_UserName.setVisibility(View.VISIBLE);
        }


        GridLayoutManager layoutManagerTag = new GridLayoutManager(this, 3);
        recycler_Service.setLayoutManager(layoutManagerTag);
        tagAdapter = new TagAdapter(this, nameServicelist, "");
        recycler_Service.setAdapter(tagAdapter);

        cutoCompleteCustomerAapter = new ServiceSearchAdapter(this, searchServiceList);
        et_service.setAdapter(cutoCompleteCustomerAapter);
        et_service.setThreshold(1);


        clickListeners();
        setBackNavgation();
    }

    public void clickListeners() {

        if (isComing.equalsIgnoreCase("toUpdate")) {
            if (AppConstant.isNetworkAvailable(AddUserManageActivity.this)) {
                editDetail();
            } else {
                AppConstant.openInternetDialog(AddUserManageActivity.this);
            }
        } else {
            if (AppConstant.isNetworkAvailable(AddUserManageActivity.this)) {
                saleCreateDetail();
            } else {
                AppConstant.openInternetDialog(AddUserManageActivity.this);
            }
        }

        tagAdapter.setOnRemoveClicked(new TagAdapter.RemoveTag() {
            @Override
            public void setOnClickedItem(int position) {

                deleteEmployee(position);

            }
        });

        dropDown_Role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    roleId = arrRoleList.get(position).id;
                } else {
                    roleId = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropDown_UserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    userTypeID = arrUserTypeList.get(position).idString;
                } else {
                    userTypeID = "0";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ch_isActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch_isActive.isChecked()) {
                    is_active = "active";
                } else {
                    is_active = "";
                }
            }
        });
        txt_save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_user_first_name.getText().toString().isEmpty()) {
                    AppConstant.showToast(AddUserManageActivity.this, "Please Enter First Name");
                }
                else if(et_user_email.getText().toString().isEmpty()){
                    AppConstant.showToast(AddUserManageActivity.this, "Please Enter Email Address");
                }
                else if (!et_user_email.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(et_user_email.getText().toString()).matches()){
                    AppConstant.showToast(AddUserManageActivity.this, "Please enter valid email address");
                }else if (userTypeID.equalsIgnoreCase("0")) {
                    AppConstant.showToast(AddUserManageActivity.this, "Please Select User Type");
                } else if (roleId == 0) {
                    AppConstant.showToast(AddUserManageActivity.this, "Please Select User Role");
                }else if(serviceIDlist.size()==0){
                    AppConstant.showToast(AddUserManageActivity.this, "Please Assign Services");
                }
                else if (isComing.equalsIgnoreCase("toUpdate")) {
                     if (!et_user_password.getText().toString().isEmpty() && et_user_confirmPassword.getText().toString().isEmpty()) {
                        AppConstant.showToast(AddUserManageActivity.this, "Please Confirm Password");
                    } else if (!et_user_confirmPassword.getText().toString().isEmpty() && et_user_password.getText().toString().isEmpty()) {
                        AppConstant.showToast(AddUserManageActivity.this, "Please Enter Password");
                    } else if (!et_user_password.getText().toString().matches(et_user_confirmPassword.getText().toString())) {
                        AppConstant.showToast(AddUserManageActivity.this, "Password Not Matching");
                    } else {
                            updateUser();
                    }
                } else {
                    if (et_user_password.getText().toString().isEmpty()) {
                        AppConstant.showToast(AddUserManageActivity.this, "Please Enter Password");
                    } else if (et_user_confirmPassword.getText().toString().isEmpty()) {
                        AppConstant.showToast(AddUserManageActivity.this, "Please Confirm Password");
                    } else if (!et_user_password.getText().toString().matches(et_user_confirmPassword.getText().toString())) {
                        AppConstant.showToast(AddUserManageActivity.this, "Password Not Matching");
                    } else {
                        saveUser();
                    }
                }

            }
        });

        et_service.addTextChangedListener(new TextWatcher() {
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

    }

    public void filter(String text) {

        if (!sp_cartSave.getString("searchCustomerList", "").equalsIgnoreCase("")) {//if not cart empty.
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CustomerListData>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("searchCustomerList", "");
            searchServiceList = (ArrayList<CustomerListData>) gson.fromJson(strMyCart, type);

        }
        ArrayList<CustomerListData> temp = new ArrayList();
        for (CustomerListData d : searchServiceList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getText().toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        cutoCompleteCustomerAapter.updateList(temp);

        et_service.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }

                et_service.setText("");
                boolean newItem = true;
                for (int a = 0; a < serviceIDlist.size(); a++) {
                    if (customerData.getId() == Integer.parseInt(serviceIDlist.get(a))) {
                        newItem = false;
                        break;
                    }
                }
                if (newItem) {
                    nameServicelist.add(customerData.getText());
                    serviceIDlist.add(String.valueOf(customerData.getId()));
                    tagAdapter.notifyDataSetChanged();
                }

//                contactID = String.valueOf(customerData.getId());

            }
        });
    }


    public void deleteEmployee(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                nameServicelist.remove(position);
                serviceIDlist.remove(position);
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


    public void saleCreateDetail() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("users/create");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            arrRoleList.clear();
                            arrUserTypeList.clear();
                            searchServiceList.clear();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("user detail ", respo);
                            String status = responseObject.optString("success");
                            if (status.equalsIgnoreCase("true")) {

                                setFisrtItems();

                                if (responseObject.has("roles") && !responseObject.isNull("roles")) {
                                    JSONArray jsonObjectRole = responseObject.getJSONArray("roles");
                                    for (int i = 0; i < jsonObjectRole.length(); i++) {
                                        JSONObject data = jsonObjectRole.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.id = data.getInt("id");
                                        model.name = data.getString("name");
                                        arrRoleList.add(model);
                                    }
                                }
                                if (responseObject.has("user_types") && !responseObject.isNull("user_types")) {
                                    JSONArray jsonArrayUserType = responseObject.getJSONArray("user_types");
                                    for (int i = 0; i < jsonArrayUserType.length(); i++) {
                                        JSONObject data = jsonArrayUserType.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        model.idString = data.getString("id");
                                        model.name = data.getString("name");
                                        arrUserTypeList.add(model);

                                    }
                                }
                                if (responseObject.has("business_services") && !responseObject.isNull("business_services")) {
                                    JSONArray servicesObj = responseObject.getJSONArray("business_services");
                                    for (int i = 0; i < servicesObj.length(); i++) {
                                        JSONObject data = servicesObj.getJSONObject(i);
                                        CustomerListData customerListData = new CustomerListData();
                                        customerListData.setId(data.getInt("id"));
                                        customerListData.setText(data.getString("name"));
                                        searchServiceList.add(customerListData);
                                    }
                                }
                                Gson gson = new Gson();
                                ed_cartSave.putString("searchCustomerList", gson.toJson(searchServiceList));
                                ed_cartSave.commit();
                                cutoCompleteCustomerAapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddUserManageActivity.this, "user/create API", "(AddUserManageActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(AddUserManageActivity.this, "Could Not Get Detail . Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddUserManageActivity.this, "Could Not Get Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void editDetail() {
        AppConstant.showProgress(AddUserManageActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(AddUserManageActivity.this);
        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = apiService.getList("users/" + getIntent().getIntExtra("user_id", 0) + "/edit");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                AppConstant.hideProgress();
                try {
                    if (response.body() != null) {
                        AppConstant.hideProgress();
                        String respo = response.body().string();
                        JSONObject responseObject = new JSONObject(respo);
                        Log.e("Edit Response", respo);

                        int successstatus = responseObject.optInt("success");
                        if (successstatus == 1) {
                            arrRoleList.clear();
                            arrUserTypeList.clear();

                            if (responseObject.has("user") && !responseObject.isNull("user")) {
                                JSONObject user = responseObject.getJSONObject("user");

                                if (user.has("surname") && !user.isNull("surname")) {
                                    et_user_prefix.setText(user.getString("surname"));
                                }
                                if (user.has("first_name") && !user.isNull("first_name")) {
                                    et_user_first_name.setText(user.getString("first_name"));
                                }
                                if (user.has("last_name") && !user.isNull("last_name")) {
                                    et_user_last_name.setText(user.getString("last_name"));
                                }
                                if (user.has("email") && !user.isNull("email")) {
                                    et_user_email.setText(user.getString("email"));
                                }
                                if (user.has("username") && !user.isNull("username")) {
                                    et_user_Username.setText(user.getString("username"));
                                }

                                if (user.has("user_type") && !user.isNull("user_type")) {
                                    userTypeID = user.getString("user_type");
                                }

                                if (responseObject.has("is_checked_checkbox") && !responseObject.isNull("is_checked_checkbox")) {
                                    boolean is_checked_checkbox = responseObject.getBoolean("is_checked_checkbox");
                                    ch_isActive.setChecked(is_checked_checkbox);

                                }
                                setFisrtItems();

                                if (responseObject.has("roles") && !responseObject.isNull("roles")) {
                                    JSONArray jsonObjectRole = responseObject.getJSONArray("roles");
                                    for (int i = 0; i < jsonObjectRole.length(); i++) {
                                        JSONObject data = jsonObjectRole.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        if (data.has("id") && !data.isNull("id")) {
                                            roleId = data.getInt("id");
                                        }
                                        if (data.has("id") && !data.isNull("id")) {
                                            model.id = data.getInt("id");
                                        }
                                        if (data.has("name") && !data.isNull("name")) {
                                            model.name = data.getString("name");
                                        }
                                        arrRoleList.add(model);
                                    }

                                }
                                if (responseObject.has("user_types") && !responseObject.isNull("user_types")) {
                                    JSONArray jsonArrayUserType = responseObject.getJSONArray("user_types");
                                    for (int i = 0; i < jsonArrayUserType.length(); i++) {
                                        JSONObject data = jsonArrayUserType.getJSONObject(i);
                                        SpinnerSelectionModel model = new SpinnerSelectionModel();
                                        if (data.has("id") && !data.isNull("id")) {
                                            model.idString = data.getString("id");
                                        }
                                        if (data.has("name") && !data.isNull("name")) {
                                            model.name = data.getString("name");
                                        }
                                        arrUserTypeList.add(model);
                                    }
                                }

                                for (int a = 0; a < arrUserTypeList.size(); a++) {
                                    if (userTypeID.equalsIgnoreCase(arrUserTypeList.get(a).idString)) {
                                        dropDown_UserType.setSelection(a);
                                        break;
                                    }

                                }
                                for (int a = 0; a < arrRoleList.size(); a++) {
                                    if (roleId == arrRoleList.get(a).id) {
                                        dropDown_Role.setSelection(a);
                                        break;
                                    }

                                }

                                if (responseObject.has("business_services") && !responseObject.isNull("business_services")) {
                                    JSONArray servicesObj = responseObject.getJSONArray("business_services");
                                    for (int i = 0; i < servicesObj.length(); i++) {
                                        JSONObject data = servicesObj.getJSONObject(i);
                                        CustomerListData customerListData = new CustomerListData();
                                        customerListData.setId(data.getInt("id"));
                                        customerListData.setText(data.getString("name"));
                                        searchServiceList.add(customerListData);
                                    }
                                }
                                Gson gson = new Gson();
                                ed_cartSave.putString("searchCustomerList", gson.toJson(searchServiceList));
                                ed_cartSave.commit();
                                cutoCompleteCustomerAapter.notifyDataSetChanged();

                                if (responseObject.has("selected_business_services") && !responseObject.isNull("selected_business_services")) {
                                    JSONArray servicesObj = responseObject.getJSONArray("selected_business_services");
                                    for (int i = 0; i < servicesObj.length(); i++) {
                                        JSONObject data = servicesObj.getJSONObject(i);
                                        if (data.has("id") && !data.isNull("id")) {
                                            serviceIDlist.add(String.valueOf(data.getInt("id")));
                                        }
                                        if (data.has("name") && !data.isNull("name")) {
                                            nameServicelist.add(data.getString("name"));
                                        }
                                    }
                                }
                                tagAdapter.notifyDataSetChanged();
                            }

                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(AddUserManageActivity.this, "users/edit API", "(AddUserManageActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(AddUserManageActivity.this, "Could Not Edit User . Please Try Again", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    AppConstant.hideProgress();
                    Log.e("Exception", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppConstant.hideProgress();
                Toast.makeText(AddUserManageActivity.this, "Could Not Edit User. Please Try Again", Toast.LENGTH_LONG).show();
            }

        });
    }


    public void saveUser() {

        AppConstant.showProgress(AddUserManageActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("surname", et_user_prefix.getText().toString());
            mainObject.put("first_name", et_user_first_name.getText().toString());
            mainObject.put("last_name", et_user_last_name.getText().toString());
            mainObject.put("email", et_user_email.getText().toString());
            mainObject.put("user_type", userTypeID);
            mainObject.put("role", roleId);
            mainObject.put("username", et_user_Username.getText().toString());
            mainObject.put("password", et_user_password.getText().toString());
            mainObject.put("confirm_password", et_user_confirmPassword.getText().toString());
            String service_id = serviceIDlist.toString().replace("[", "").replace("]", "");
            mainObject.put("business_services", service_id);
            mainObject.put("is_active", is_active);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.addRepairs("users", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("Add user ", respo);
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {

                                if (userManageActivityActivity != null) {
                                    userManageActivityActivity.finish();
                                }
                                if (userDetailViewActivity != null) {
                                    userDetailViewActivity.finish();
                                }
                                String msg = responseObject.optString("msg");
                                Toast.makeText(AddUserManageActivity.this, "" + msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(AddUserManageActivity.this, UserManageActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);

                            } else {
                                AppConstant.hideProgress();
                                Toast.makeText(AddUserManageActivity.this, "Could Not Add User . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddUserManageActivity.this, "users API", "(AddUserManageActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddUserManageActivity.this, "Could Not Add User. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void updateUser() {

        AppConstant.showProgress(AddUserManageActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("surname", et_user_prefix.getText().toString());
            mainObject.put("first_name", et_user_first_name.getText().toString());
            mainObject.put("last_name", et_user_last_name.getText().toString());
            mainObject.put("email", et_user_email.getText().toString());
            mainObject.put("user_type", userTypeID);
            mainObject.put("role", roleId);
            mainObject.put("username", et_user_Username.getText().toString());
            mainObject.put("password", et_user_password.getText().toString());
            mainObject.put("confirm_password", et_user_confirmPassword.getText().toString());
            String service_id = serviceIDlist.toString().replace("[", "").replace("]", "");
            mainObject.put("business_services", service_id);
            mainObject.put("is_active", is_active);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.add("users/" + getIntent().getIntExtra("user_id", 0), body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("Add user ", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("1")) {
                                String msg = responseObject.optString("msg");

                                if (userManageActivityActivity != null) {
                                    userManageActivityActivity.finish();
                                }
                                if (userDetailViewActivity != null) {
                                    userDetailViewActivity.finish();
                                }
                                Toast.makeText(AddUserManageActivity.this, "" + msg, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(AddUserManageActivity.this, UserManageActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                AppConstant.hideProgress();
                                Toast.makeText(AddUserManageActivity.this, "Could Not Add User . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(AddUserManageActivity.this, "users API", "(AddUserManageActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(AddUserManageActivity.this, "Could Not Add User. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void setFisrtItems() {


        SpinnerSelectionModel spinnerModel = new SpinnerSelectionModel();
        spinnerModel.idString = "0";
        spinnerModel.name = "Select Type";
        arrUserTypeList.add(0, spinnerModel);

        SpinnerSelectionModel spinnerRole = new SpinnerSelectionModel();
        spinnerRole.id = 0;
        spinnerRole.name = "Select Role";
        arrRoleList.add(0, spinnerRole);


        setAdpters();

    }

    public void setAdpters() {

        SpinnerSelectionAdapter roleAdapter = new SpinnerSelectionAdapter(this, arrRoleList);
        dropDown_Role.setAdapter(roleAdapter);

        SpinnerSelectionAdapter usertypeAdapter = new SpinnerSelectionAdapter(this, arrUserTypeList);
        dropDown_UserType.setAdapter(usertypeAdapter);


    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (isComing.equalsIgnoreCase("toUpdate")) {
                getSupportActionBar().setTitle("EDIT USER");
            } else {
                getSupportActionBar().setTitle("ADD USER");
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