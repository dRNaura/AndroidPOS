package com.pos.salon.utilConstant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.PosSettingDefault;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppConstant {
//    public static String BASE_URL = "https://dev.shopplusglobal.com/beta/pos/public/api/"; //production Development
    //    public static String BASE_URL = "https://shopplusglobal.com/pos/public/api/"; // production live /// not used now


    public static String BASE_URL = "https://devpos.shopplusglobal.com/api/"; //production Development
//        public static String BASE_URL = "https://pos.shopplusglobal.com/api/"; //updated live url
    public static ProgressDialog progressDialog;
    public static String TEMP_AUTH;

    public static PosSettingDefault objPosSetting;
    //Shop Plus department id
//    public class MyDepartmentId {
//        public final static int ELECTRONICS = 1;
//        public final static int FASHION = 2;
//        public final static int AUTOMOTIVE = 3;
//        public final static int HEALTH_BEAUTY  = 4;
//        public final static int GROCERY  = 5;
//        public final static int ENTERTAINMENT  = 6;
//        public final static int RECREATIONAL  = 7;
//        public final static int FOOD_BEVERAGES  = 8;
//        public final static int BUSINESS  = 9;
//        public final static int CAR_RENTAL  = 10;
//
//    }

    // network check
    public static boolean isNetworkAvailable(Context context) {

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_LONG).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }


    public static void openInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You Are Not Connected To Internet");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
//            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//
//                }
//            });

        builder.show();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void showProgress(Context context, Boolean isCancelable) {
        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }

    public static void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static void showSnakeBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String notNull(String value) {
        if (value == null)
            return "";
        else
            return value;
    }

    // send email if any error accurs in api
    public static void sendEmailNotification(final Context context, String errorMessage, String email_body, String email_subject) {

        AppConstant.showProgress(context, false);
        Retrofit retrofit = APIClient.getClientToken(context);

        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email_body", errorMessage + " " + email_body);
                jsonObject.put("email_subject", email_subject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Main", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.sendEmailCopy("notification/sendApiErrorNotification", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
//                                String respo = response.body().string();
//                                JSONObject responseObject = new JSONObject(respo);
//                                Log.e(" Error Email Response", responseObject.toString());
//                                String successstatus = responseObject.optString("success");
//                                //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
//                                if (successstatus.equalsIgnoreCase("1") || successstatus.equalsIgnoreCase("success")) {
//
////                                    Toast.makeText(context, "Error Notification Sent Successfully", Toast.LENGTH_LONG).show();
//                                }

                        } else {

//                                Toast.makeText(context, "Could Not Send Error Email. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
//                            Toast.makeText(context, "Exception : "+e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                        Toast.makeText(context, "Could Not Send Error Email. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
