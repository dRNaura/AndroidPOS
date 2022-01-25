package com.pos.salon.activity.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.CloseRegister;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserDetailViewActivity extends AppCompatActivity {

    Toolbar toolbar;
    int user_id=0;
    ImageView open_action;
    TextView txt_name,txt_user_email,txt_user_Username,txt_user_role,txt_user_status;
    @SuppressLint("StaticFieldLeak")
    public static Activity userDetailViewActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_view);

        userDetailViewActivity=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user_id=getIntent().getIntExtra("user_id",0);

        setBackNavgation();

        findViews();
    }
    public void findViews(){

        open_action=findViewById(R.id.open_action);
        txt_name=findViewById(R.id.txt_name);
        txt_user_email=findViewById(R.id.txt_user_email);
        txt_user_Username=findViewById(R.id.txt_user_Username);
        txt_user_role=findViewById(R.id.txt_user_role);
//        txt_sale_commission=findViewById(R.id.txt_sale_commission);
//        txt_sale_commission1=findViewById(R.id.txt_sale_commission1);
        txt_user_status=findViewById(R.id.txt_user_status);
//        txt_permanent_address=findViewById(R.id.txt_permanent_address);
//        txt_current_address=findViewById(R.id.txt_current_address);
//        txt_dob=findViewById(R.id.txt_dob);
//        txt_martial_status=findViewById(R.id.txt_martial_status);
//        txt_blood_group=findViewById(R.id.txt_blood_group);
//        txt_contact_number=findViewById(R.id.txt_contact_number);
//        txt_id_proof=findViewById(R.id.txt_id_proof);
//        txt_holder_name=findViewById(R.id.txt_holder_name);

        listeners();

    }
    public void listeners(){


        open_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userViewPopUp(v);
            }
        });



        userDetail();

    }

    private void userViewPopUp(View v) {

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }
        boolean isUserUpdate =false;
        boolean isUserDelete =false;

        if (permissionsDataList.isEmpty()) {
            isUserUpdate = true;
            isUserDelete = true;
        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("user.update")) {
                    isUserUpdate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("user.delete")) {
                    isUserDelete = true;
                }

            }
        }
        PopupMenu popup = new PopupMenu(UserDetailViewActivity.this, v);
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
        popup.getMenuInflater().inflate(R.menu.user_view, popup.getMenu());

        if(isUserUpdate) {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.customer_edit).setVisible(false);
        }
        if(isUserDelete) {
            popup.getMenu().findItem(R.id.supplier_delete).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.supplier_delete).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.customer_edit:

                        Intent i = new Intent(UserDetailViewActivity.this, AddUserManageActivity.class);
                        i.putExtra("isComing", "toUpdate");
                        i.putExtra("user_id", user_id);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                    case R.id.supplier_delete:

                        deleteUserDetail();

                        break;

                    case R.id.register_detail:
                        Intent intent = new Intent(UserDetailViewActivity.this, CloseRegister.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("comingFrom","user_Detail");
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;

                }
                return true;
            }
        });
        popup.show();

    }
    public void deleteUserDetail() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UserDetailViewActivity.this);
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
        AppConstant.showProgress(UserDetailViewActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(UserDetailViewActivity.this);
        Log.e("user_id", String.valueOf(user_id));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteCategory("users/"+user_id);
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

                                if (UserManageActivity.userManageActivityActivity != null) {
                                    UserManageActivity.userManageActivityActivity.finish();
                                }
                                Intent i =new Intent(UserDetailViewActivity.this, UserManageActivity.class);
                                startActivity(i);
                                AppConstant.showToast(UserDetailViewActivity.this, "" + msg);
                                finish();
                                overridePendingTransition(R.anim.enter, R.anim.exit);

                            }
                            else {
                                AppConstant.showToast(UserDetailViewActivity.this, "" + msg);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(UserDetailViewActivity.this, "users/delete", "(UserDetailViewActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(UserDetailViewActivity.this, "Could Not Delete User Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {

                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(UserDetailViewActivity.this, "Could Not Delete User Detail. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });
        }

    }



    public void userDetail() {
        AppConstant.showProgress(UserDetailViewActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(UserDetailViewActivity.this);
        Log.e("ContactId", String.valueOf(user_id));
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("users/" + user_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("user Detail", responseObject.toString());
                            int successstatus = responseObject.optInt("success");

                            if (successstatus==1) {

                                if (responseObject.has("user") && !responseObject.isNull("user")) {

                                    JSONObject user = responseObject.getJSONObject("user");

                                    String userName="";
                                    if (user.has("surname") && !user.isNull("surname")) {
                                        userName=user.getString("surname");
                                    }
                                    if (user.has("first_name") && !user.isNull("first_name")) {
                                        userName=userName+" "+user.getString("first_name");
                                    }
                                    if (user.has("last_name") && !user.isNull("last_name")) {
                                        userName=userName+" "+user.getString("last_name");
                                    }
                                    txt_name.setText(userName);

                                    if (user.has("email") && !user.isNull("email")) {
                                        txt_user_email.setText(user.getString("email"));
                                    }
                                    if (user.has("username") && !user.isNull("username")) {
                                        txt_user_Username.setText(user.getString("username"));
                                    }
                                    if (user.has("user_type") && !user.isNull("user_type")) {
                                        txt_user_role.setText(user.getString("user_type"));
                                    }
//                                    if (user.has("cmmsn_percent") && !user.isNull("cmmsn_percent")) {
//                                        txt_sale_commission.setText(user.getString("cmmsn_percent"));
//                                    }
//                                    if (user.has("cmmsn_percent") && !user.isNull("cmmsn_percent")) {
//                                        txt_sale_commission1.setText(user.getString("cmmsn_percent"));
//                                    }
                                    if (user.has("status") && !user.isNull("status")) {
                                        txt_user_status.setText(user.getString("status"));
                                    }
//                                    if (user.has("permanent_address") && !user.isNull("permanent_address")) {
//                                        txt_permanent_address.setText(user.getString("permanent_address"));
//                                    }
//                                    if (user.has("current_address") && !user.isNull("current_address")) {
//                                        txt_current_address.setText(user.getString("current_address"));
//                                    }
//                                    if (user.has("dob") && !user.isNull("dob")) {
//                                        txt_dob.setText(user.getString("dob"));
//                                    }
//                                    if (user.has("marital_status") && !user.isNull("marital_status")) {
//                                        txt_martial_status.setText(user.getString("marital_status"));
//                                    }
//                                    if (user.has("blood_group") && !user.isNull("blood_group")) {
//                                        txt_blood_group.setText(user.getString("blood_group"));
//                                    }
//                                    if (user.has("contact_number") && !user.isNull("contact_number")) {
//                                        txt_contact_number.setText(user.getString("contact_number"));
//                                    }
//                                    if (user.has("id_proof_name") && !user.isNull("id_proof_name")) {
//                                        txt_id_proof.setText(user.getString("id_proof_name"));
//                                    }
//                                    if (user.has("id_proof_name") && !user.isNull("id_proof_name")) {
//                                        txt_holder_name.setText(user.getString("id_proof_name"));
//                                    }


                                }
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(UserDetailViewActivity.this, "users/ API", "(UserDetail Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(UserDetailViewActivity.this, "Could Not Load User Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(UserDetailViewActivity.this, "Could Not Load User Detail. Please Try Again", Toast.LENGTH_LONG).show();
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