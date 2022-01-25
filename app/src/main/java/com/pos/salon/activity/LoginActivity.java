package com.pos.salon.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.pos.salon.R;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.login.LoginParse;
import com.pos.salon.model.login.LoginSendData;
import com.pos.salon.model.posLocation.CountryData;
import com.pos.salon.utilConstant.AppConstant;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText edtUserName, edtPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<CountryData> countryList;
    ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<>();
    ArrayList<LoginPermissionsData> arrRolesPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setView();
    }

    private void setView() {

        // bib.infinityfashions.ky@gmail.com
        // infinity@123

        //User name: jmelectronicsky@gmail.com, Pass: 123456

        //token  eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImZlYTVlOTM4ZDM1NzI5MGIyOTk0MmY1MmNiNWQ3NTQxOTM5ZTRkYTYyNmFjMDIzODE0ZGFiMjZkYTU0NTZlMjRlNWMxMzk3MzZlNWFiZjAzIn0.eyJhdWQiOiIxIiwianRpIjoiZmVhNWU5MzhkMzU3MjkwYjI5OTQyZjUyY2I1ZDc1NDE5MzllNGRhNjI2YWMwMjM4MTRkYWIyNmRhNTQ1NmUyNGU1YzEzOTczNmU1YWJmMDMiLCJpYXQiOjE1NzU0NDk1ODQsIm5iZiI6MTU3NTQ0OTU4NCwiZXhwIjoxNjA3MDcxOTgzLCJzdWIiOiIxOCIsInNjb3BlcyI6W119.icUVMh1haijXFVPWKA_PI3Q6cx9TDoXYjyqkSBt6Na1YUrPChJ4rJbFyAIxHTMThP-zihME8i4Eqfh-LXROG3dktBLLEOWYkb8jnPBKAV-2gorN72SvEvR6DBDo64G9uM9e2sTJyKEJi-v8R9p94Zs4obHQu_6KSwyL_0xp-xWX-JoxWTCdZTpWfzMHA-OwxsxfgOGVPCCrdMEotDfIGqMLbmupUL9an4IR6tmdYl-y4gPgI4FHs4gON7riSZ45IeNSpZK-CO_3XPjzntvw1_0Kzyeov16lM3_1jisG_yfdRtlgeAkc5lpfB8DCPuW-JUhE4QCbDIVRY_qPhWMilMwiYWXkQLLgNhVJw846nUHNhvGq6PDjA3DPycO2-bqtsWEBQOhfbGDqIOD-gO9KuNpnyScGxBbThEqYdkOj0zuaWWTrrlBXECsPPRpXuhpcxbcsAQxQq8_AWMHkhkcf0Z7RaBjQ2XbUvpj3ISN8mOCtpsU1fhTXQYcFvjPtmiaIMa-Wl4AnWXF_b5IKdQQ6aoxz6UHJqNO2WtB5KQpXWBScE8XpUs_qAnSZhazxFPpE3c7tDLZkLfv75zm1PZwFlHaMg4gCKvI0zmTDGqp3MUY83D6DqsQmBeQi5D9RyVpyuZ0agmqD_kFCOQy1YMF78waCiZv0_LC-S9OKZRT2GmAs
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        editor = sharedPreferences.edit();

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSendData sendData = new LoginSendData();
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();

                if (userName.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Enter Username", Toast.LENGTH_LONG).show();
                }
                else if (password.equalsIgnoreCase(""))
                {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                }
                else {
                    sendData.setUsername(userName);
                    sendData.setPassword(password);
                    if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                        callLoginAPI(sendData);
                    } else {
                        AppConstant.openInternetDialog(LoginActivity.this);
                    }

                }
            }
        });
    }


    private void callLoginAPI(final LoginSendData sendData) {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClient(LoginActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<LoginParse> call = apiService.userLogin("login", sendData);
            call.enqueue(new Callback<LoginParse>() {
                @Override
                public void onResponse(@NonNull Call<LoginParse> call, @NonNull Response<LoginParse> response) {
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        AppConstant.hideProgress();
                        LoginParse loginData = response.body();

                        Gson gsons = new Gson();
                        String json = gsons.toJson(loginData);

                        Log.e("login Json : ", json);
                        if (loginData.getStatus().equalsIgnoreCase("200")) {
                            AppConstant.TEMP_AUTH = loginData.getAccess_token();
                            // Toast.makeText(LoginActivity.this, ""+AppConstant.TEMP_AUTH, Toast.LENGTH_LONG).show();
                            editor.putString("user", sendData.getUsername());
//                            editor.putInt("department_id", Integer.parseInt(loginData.getUser().getDepartment_id()));
//                            Log.e("deptttt id:", "" + loginData.getUser().getDepartment_id());
                            editor.putString("password", sendData.getPassword());
                            editor.putString("token", loginData.getAccess_token());
                            editor.putString("user_id",loginData.getUser().getId());
                            editor.putInt("business_id",loginData.getUser().getBusiness_id());
                            countryList = loginData.getCountryLists();

                            //   String currencyCode=loginData.getBusiness_details().getCurrency_code();
                            //   String exchangeRate=loginData.getBusiness_details().getExchange_rate();

                            String gson3 = new Gson().toJson(loginData.getBusiness_details());
                            editor.putString("myBusinessDetail", gson3);
                            editor.commit();

                            String gson = new Gson().toJson(countryList);
                            editor.putString("country_list", gson);
                            editor.commit();

                            permissionsDataList = loginData.getPermissionsDataLists();
                            String gson1 = new Gson().toJson(permissionsDataList);
                            editor.putString("permissionDataList", gson1);
                            editor.commit();

                            Log.e("permisionListData", gson1);
                            for(int i=0 ; i < loginData.getUser().getRoles().size() ; i++){
                                arrRolesPermissionList=loginData.getUser().getRoles().get(i).getPermissions();
                            }
                            String gson01 = new Gson().toJson(arrRolesPermissionList);
                            editor.putString("rolesPermission", gson01);
                            editor.commit();
                            Log.e("rolesPermission", gson01);


                            ArrayList<String> repairModuleList = loginData.getenabled_modules();
                            String gson2 = new Gson().toJson(repairModuleList);
                            editor.putString("enableList", gson2);
                            editor.commit();

                            Log.e("enableList", gson2);

                            ArrayList<String> departmentList = loginData.getDepartments();
                            String gsonDept = new Gson().toJson(departmentList);
                            editor.putString("departments", gsonDept);
                            editor.commit();

                            Log.e("gsonDept", ""+gsonDept);
                            //save default pos setting in APP Constant
//                            if (!loginData.getBusiness_details().getPos_settings().equals("")) {
//                                String json = loginData.getBusiness_details().getPos_settings();
//
//                                AppConstant.objPosSetting = fetchedDettingFromLoginResponse(json);
//                            }

                            Log.e("Token", loginData.getAccess_token());
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }


                        Toast.makeText(LoginActivity.this, loginData.getStatus_text(), Toast.LENGTH_LONG).show();

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(LoginActivity.this, "login API", "(LoginActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(LoginActivity.this, "Could Not Logged In. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginParse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(LoginActivity.this, "Could Not Logged In. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
