package com.pos.salon.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.HomeActivity.txt_notification_count;

//
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    // This function is called whenever a push notification is *received*.
    @Override
    public void onReceive(Context context, Intent pushNotif) {

        if (pushNotif != null) {

//            Gson gsons = new Gson();
//            String json = gsons.toJson(pushNotif);
//            Log.e("intent",json);
//            String text = "", title = "",notification_id="",transaction_id="",business_id="",unread_count="0",badge="";
//            try {
//                JSONObject obj=null;
//                if(pushNotif.getStringExtra("_wp") !=null){
//                     obj = new JSONObject(pushNotif.getStringExtra("_wp"));
//                    Log.e("obj", obj.toString());
//                }
//                JSONObject object = obj.getJSONObject("alert");
//                if(object.has("title") && !object.isNull("title")){
//                    text = object.getString("title");
//                    Log.e("text", text.toString());
//                }
//
//                if(pushNotif.hasExtra("transaction_id")){
//                    transaction_id =pushNotif.getStringExtra("transaction_id");
//                }
//                if(pushNotif.hasExtra("business_id")){
//                    business_id =pushNotif.getStringExtra("business_id");
//                }
//                if(pushNotif.hasExtra("unread_count")) {
//                    unread_count = pushNotif.getStringExtra("unread_count");
//                }
//
////                    ShortcutBadger.applyCount(context, Integer.parseInt(unread_count)); //for 1.1.4+
////                    ShortcutBadger.with(context).count(unread_count); //for 1.1.3
////                }
//                Log.e("transaction_id",transaction_id);
//                Log.e("business_id",business_id);
//                Log.e("unread_count",unread_count);
//
//
//                if(ShortcutBadger.isBadgeCounterSupported(context)){
//                    ShortcutBadger.applyCount(context.getApplicationContext(), Integer.parseInt(unread_count)); //for 1.1.4+
//                }


//            } catch (Exception e) {
//                Log.e("Exception",e.toString());
//            }
            checkNotificationCount(context);
        }
    }
            public void checkNotificationCount(Context context) {
//        AppConstant.showProgress(HomeActivity.this, false);
                Retrofit retrofit = APIClient.getClientToken(context);
                APIInterface apiService = retrofit.create(APIInterface.class);
                Call<ResponseBody> call = apiService.getList("fetch-unread-notifications");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.body() != null) {
                                AppConstant.hideProgress();
                                String respo = response.body().string();
                                Log.e("check push notification", respo.toString());
                                JSONObject responseObject = new JSONObject(respo);
                                String successstatus = responseObject.optString("success");
                                if (successstatus.equalsIgnoreCase("true")) {

                                    int unseen_notification = responseObject.getInt("unseen_notification");
                                    if (unseen_notification == 0) {
                                        txt_notification_count.setVisibility(View.GONE);
                                        txt_notification_count.setText(String.valueOf(unseen_notification));
                                    } else {
                                        txt_notification_count.setVisibility(View.VISIBLE);
                                        txt_notification_count.setText(String.valueOf(unseen_notification));
                                    }
                                    if(ShortcutBadger.isBadgeCounterSupported(context)){
                    ShortcutBadger.applyCount(context.getApplicationContext(), unseen_notification); //for 1.1.4+
                }

//                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.zxing_beep);
//                            mp.start();

                                }

                            } else {
                                AppConstant.hideProgress();
                                AppConstant.sendEmailNotification(context, "fetch-unread-notifications API", "(HomeActivity Screen)", "Web API Error : API Response Is Null");
                                Toast.makeText(context, "Could Not Check Notification Count. Please Try Again", Toast.LENGTH_LONG).show();

                            }

                        } catch (Exception e) {
                            AppConstant.hideProgress();
                            Log.e("Exception", e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppConstant.hideProgress();
                        Toast.makeText(context, "Could Not Check Notification Count. Please Try Again", Toast.LENGTH_LONG).show();

                    }
                });
            }

}
