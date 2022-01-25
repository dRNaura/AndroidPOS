package com.pos.salon.activity.SupplierActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.listModel.ListPosModel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.pos.salon.R;
import com.pos.salon.adapter.SupplierAdapter.SupplierDetailAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import java.lang.reflect.Field;
import android.content.Intent;
import android.view.MenuItem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.lang.reflect.Method;
import static com.pos.salon.activity.SupplierActivity.SupplierSection.supplierSectionActivity;

public class SupplierDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    RecyclerView recycler_view_supplier;
    LinearLayoutManager layoutManager;
    SupplierDetailAdapter supplierDetailAdapter;
    int contact_id = 0;
    private TextView txt_Nodata,txt_total_reward, txt_store_credit, txt_default_supplier, txt_address, txt_business_name, txt_registration_date, txt_mobile, txt_tax_number, txt_purchase_payment, txt_purchase_due, txt_purchase_return, txt_purchase_return_due, txt_total_purchase;
    ArrayList<ListPosModel> contactSalesList = new ArrayList<>();
    ImageView open_action;
    @SuppressLint("StaticFieldLeak")
    public static Activity supplierDetailActivity;
    public boolean isSupplierUpdate=false;
    public boolean isSupplierDelete=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail_page);

        supplierDetailActivity=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contact_id = getIntent().getIntExtra("contact_id", 0);

        findViews();
        setBackNavgation();
    }

    public void findViews() {
        recycler_view_supplier = findViewById(R.id.recycler_view_supplier);
        txt_default_supplier = findViewById(R.id.txt_default_supplier);
        txt_address = findViewById(R.id.txt_address);
        txt_registration_date = findViewById(R.id.txt_registration_date);
        txt_mobile = findViewById(R.id.txt_mobile);
        txt_tax_number = findViewById(R.id.txt_tax_number);
        txt_purchase_payment = findViewById(R.id.txt_purchase_payment);
        txt_purchase_due = findViewById(R.id.txt_purchase_due);
        txt_purchase_return = findViewById(R.id.txt_purchase_return);
        txt_purchase_return_due = findViewById(R.id.txt_purchase_return_due);
        txt_total_purchase = findViewById(R.id.txt_total_purchase);
        open_action = findViewById(R.id.open_action);
        txt_business_name = findViewById(R.id.txt_business_name);
        txt_store_credit = findViewById(R.id.txt_store_credit);
        txt_total_reward = findViewById(R.id.txt_total_reward);
        txt_Nodata = findViewById(R.id.txt_Nodata);

        layoutManager = new LinearLayoutManager(SupplierDetailActivity.this);
        recycler_view_supplier.setLayoutManager(layoutManager);
        supplierDetailAdapter = new SupplierDetailAdapter(SupplierDetailActivity.this,contactSalesList);
        recycler_view_supplier.setAdapter(supplierDetailAdapter);

        clickListeners();

        supplierDetail();
        salesRelatedToContact();
    }

    public void clickListeners() {

        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customerViewPopUp(v);

            }
        });

    }

    private void customerViewPopUp(View v) {

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isSupplierUpdate = true;
            isSupplierDelete = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {

                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("supplier.update")) {
                    isSupplierUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("supplier.delete")) {
                    isSupplierDelete = true;
                }

            }
        }

        PopupMenu popup = new PopupMenu(SupplierDetailActivity.this, v);
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
        popup.getMenuInflater().inflate(R.menu.supplier_view, popup.getMenu());

        if (isSupplierUpdate) {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(false);
        }
        if (isSupplierDelete) {
            popup.getMenu().findItem(R.id.supplier_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.supplier_delete).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.customer_edit:


                        Intent i = new Intent(SupplierDetailActivity.this, SupplierAddActivity.class);
                        i.putExtra("isComing", "toUpdate");
                        i.putExtra("contactId", contact_id);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                    case R.id.supplier_delete:

                        deleteSupplierDetail();

                        break;

                }
                return true;
            }
        });
        popup.show();

    }

    public void deleteSupplierDetail() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SupplierDetailActivity.this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Detail");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deletedetail();

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
    public void deletedetail() {
        AppConstant.showProgress(SupplierDetailActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(SupplierDetailActivity.this);
        Log.e("contact_id", String.valueOf(contact_id));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteDetail("contacts/deleteContact/"+contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    AppConstant.hideProgress();
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

                                if (supplierSectionActivity != null) {
                                    supplierSectionActivity.finish();
                                }
                                Intent i =new Intent(SupplierDetailActivity.this,SupplierSection.class);
                                startActivity(i);
                                AppConstant.showToast(SupplierDetailActivity.this, "" + msg);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                            else {
                                AppConstant.showToast(SupplierDetailActivity.this, "" + msg);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(SupplierDetailActivity.this, "contacts/deleteContact", "(SupplierDetail Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(SupplierDetailActivity.this, "Could Not Delete Contact Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierDetailActivity.this, "Could Not Delete Contact Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }



    public void supplierDetail() {
        AppConstant.showProgress(SupplierDetailActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(SupplierDetailActivity.this);
        Log.e("ContactId", String.valueOf(contact_id));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomerDetail("contacts/" + contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("supplier Detail", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                if (responseObject.has("contact") && !responseObject.isNull("contact")) {

                                    JSONObject contacts = responseObject.getJSONObject("contact");

                                    if (contacts.has("total_purchase") && !contacts.isNull("total_purchase")) {
                                        txt_total_purchase.setText("$" + contacts.getString("total_purchase"));
                                    }
                                    double purchase_return_due = 0.0;
                                    double total_purchase = 0.0, return_received = 0.0;
                                    if (contacts.has("total_purchase_return") && !contacts.isNull("total_purchase_return")) {
                                        txt_purchase_return.setText("$" + contacts.getString("total_purchase_return"));
                                        total_purchase = Double.parseDouble(contacts.getString("total_purchase_return"));
                                    }
                                    if (contacts.has("purchase_return_received") && !contacts.isNull("purchase_return_received")) {
                                        return_received = Double.parseDouble(contacts.getString("purchase_return_received"));
                                    }
                                    purchase_return_due = (total_purchase - return_received);
                                    txt_purchase_return_due.setText("$" + String.valueOf(purchase_return_due));

                                    if (contacts.has("invoice_received") && !contacts.isNull("invoice_received")) {
                                    }
                                    if (contacts.has("opening_balance") && !contacts.isNull("opening_balance")) {
                                        String opening_balance = contacts.getString("opening_balance");
                                    }
                                    if (contacts.has("opening_balance_paid") && !contacts.isNull("opening_balance_paid")) {
                                        String opening_balance_paid = contacts.getString("opening_balance_paid");
                                    }
                                    if (contacts.has("id") && !contacts.isNull("id")) {
                                        String id = contacts.getString("id");
                                    }

                                    if (contacts.has("business_id") && !contacts.isNull("business_id")) {
                                        String business_id = contacts.getString("business_id");
                                    }
                                    if (contacts.has("default_address_id") && !contacts.isNull("default_address_id")) {
                                        String default_address_id = contacts.getString("default_address_id");
                                    }
                                    if (contacts.has("type") && !contacts.isNull("type")) {
                                        String type = contacts.getString("type");
                                    }
                                    if (contacts.has("supplier_business_name") && !contacts.isNull("supplier_business_name")) {
                                        txt_business_name.setText(contacts.getString("supplier_business_name"));

                                    }
                                    if (contacts.has("name") && !contacts.isNull("name")) {
                                        txt_default_supplier.setText(contacts.getString("name"));
                                    }
                                    if (contacts.has("username") && !contacts.isNull("username")) {
                                        String username = contacts.getString("username");
                                    }
                                    if (contacts.has("first_name") && !contacts.isNull("first_name")) {
                                        String first_name = contacts.getString("first_name");
                                    }
                                    if (contacts.has("last_name") && !contacts.isNull("last_name")) {
                                        String last_name = contacts.getString("last_name");
                                    }
                                    if (contacts.has("last_name") && !contacts.isNull("last_name")) {
                                        String last_name = contacts.getString("last_name");
                                    }
                                    if (contacts.has("mobile") && !contacts.isNull("mobile")) {
                                        txt_mobile.setText(contacts.getString("mobile"));
                                    }
                                    if (contacts.has("tax_number") && !contacts.isNull("tax_number")) {
                                        txt_tax_number.setText(contacts.getString("tax_number"));
                                    }
                                    String address = "";
                                    if (contacts.has("city") && !contacts.isNull("city")) {
                                        address = contacts.getString("city") + ",";

                                    }
                                    if (contacts.has("state") && !contacts.isNull("state")) {
                                        address = address + contacts.getString("state") + ",";
                                    }
                                    if (contacts.has("country") && !contacts.isNull("country")) {
                                        address = address + contacts.getString("country");

                                    }
                                    txt_address.setText(address);
                                    if (contacts.has("created_at") && !contacts.isNull("created_at")) {
                                        txt_registration_date.setText(contacts.getString("created_at"));
                                    }
                                    if (contacts.has("purchase_paid") && !contacts.isNull("purchase_paid")) {
                                        txt_purchase_payment.setText("$" + contacts.getString("purchase_paid"));
                                    }
                                    if (contacts.has("total_sell_return") && !contacts.isNull("total_sell_return")) {
                                        txt_purchase_due.setText("$" + contacts.getString("total_sell_return"));
                                    }

                                    if (contacts.has("total_reward") && !contacts.isNull("total_reward")) {
                                        txt_purchase_due.setText("$" + contacts.getString("total_reward"));
                                    }

                                }
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(SupplierDetailActivity.this, "contacts/ API", "(SupplierDetail Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(SupplierDetailActivity.this, "Could Not Load Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierDetailActivity.this, "Could Not Load Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

    public void salesRelatedToContact() {
        Retrofit retrofit = APIClient.getClientToken(SupplierDetailActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getCustomerDetail("sales?contact_id=" + contact_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {

                            AppConstant.hideProgress();
                            contactSalesList.clear();
                            String respo = response.body().string();

                            Log.e("Customer Sales", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray dataObj = responseObject.getJSONArray("sells");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    ListPosModel listPosModel = new ListPosModel();

                                    listPosModel.contactInvoice = data.getString("invoice_no");
                                    listPosModel.contactName = data.getString("name");
                                    listPosModel.contactTtansactionDate = data.getString("transaction_date");
                                    listPosModel.contactTotalAmount = data.getString("final_total");

                                    if (data.has("total_paid") && !data.isNull("total_paid")) {
                                        listPosModel.contactTotalPaid = data.getString("total_paid");
                                    } else {
                                        listPosModel.contactTotalPaid = "0.0";
                                    }
                                    listPosModel.contactPaymentstatus = data.getString("payment_status");
                                    contactSalesList.add(listPosModel);
                                }

                                if(contactSalesList.isEmpty()){
                                    txt_Nodata.setVisibility(View.VISIBLE);
                                }else{
                                    txt_Nodata.setVisibility(View.GONE );
                                }
                                supplierDetailAdapter.notifyDataSetChanged();
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(SupplierDetailActivity.this, "sales?contact_id API", "(CustmerViewDetail Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(SupplierDetailActivity.this, "Could Not Load Supplier Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(SupplierDetailActivity.this, "Could Not Load Customer Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    Intent i = new Intent(RepairListActivity.this, RepairSection.class);
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