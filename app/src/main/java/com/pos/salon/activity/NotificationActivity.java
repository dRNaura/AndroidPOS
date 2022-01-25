package com.pos.salon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.adapter.NotificationAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.NotificationModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recycler_notification;
    NotificationAdapter notificationAdapter;
    ProgressBar progressBar;
    private boolean isScrolling = true;
    private int page_number=0;
    private final ArrayList<NotificationModel> arrNotificationList=new ArrayList<>();
    private TextView txt_no_resut;
    SharedPreferences sharedPreferences;
    LinearLayoutManager layoutManager;
    int currentItems, totalItems, scrollItems;
    String user_id="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "");

        findIds();
        clickListeners();

        setBackNavgation();
    }
    public void findIds(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler_notification = (RecyclerView) findViewById(R.id.recycler_notification);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_no_resut = (TextView) findViewById(R.id.txt_no_resut);
        setSupportActionBar(toolbar);

         layoutManager = new LinearLayoutManager(this);
        recycler_notification.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this,arrNotificationList,user_id);
        recycler_notification.setAdapter(notificationAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();

        getNotificationList();
    }

    public void clickListeners(){

        notificationAdapter.setOnItmeClicked(new NotificationAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
//                NotificationModel model=arrNotificationList.get(position);
//
//                String all_vals = model.viewed_by;
//                List<String> list = Arrays.asList(all_vals.split(","));
//                if (!list.contains(user_id)) {
//                    changeStatusNotification(model.comment_id);
//                }else {
//
//                }
//
//                SaleDetailFragment ldf = new SaleDetailFragment();
//                Bundle args = new Bundle();
//                args.putInt("transactionId", model.transaction_id);
//                args.putString("from", "fromNotificationActivity");
//                args.putString("subFrom", "fromNotificationActivity");
//                args.putInt("return_exists",0);
//                ldf.setArguments(args);
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.lay_notification_activity, ldf);
////                activeCenterFragments.add(ldf);
////                transaction.addToBackStack(null);
//                transaction.commit();
////                Intent i= new Intent(NotificationActivity.this,NotificationDetailActivity.class);
////                i.putExtra("comment_subject",model.comment_subject);
////                startActivity(i);
//
//                finish();
            }
        });

        recycler_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollItems = layoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            getNotificationList();
                        }
                    }
                }
            }
        });
    }



    public void changeStatusNotification(int id ){
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getNotificationDetail("change-notification-status",id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("change status", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");

                            if (successstatus.equalsIgnoreCase("true")) {

//                                getNotificationList();
                            }
                            AppConstant.showToast(NotificationActivity.this,""+msg);
                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(NotificationActivity.this,"change-notification-status API","(NotificationListActivity)","Web API Error : API Response Is Null");
                            Toast.makeText(NotificationActivity.this, "Could Not Change Status. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NotificationActivity.this, "Could Not Load Repair Sale List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void getNotificationList(){
        if (isScrolling) {
            page_number=0;
            arrNotificationList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("fetch-notifications?limit=10"+"&page_number="+page_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("notification List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                page_number++;

                                JSONArray dataObj = responseObject.getJSONArray("notifications_data");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    NotificationModel notificationModel = new NotificationModel();
                                    notificationModel.comment_id=data.getInt("comment_id");
                                    notificationModel.transaction_id=data.getInt("transaction_id");
                                    notificationModel.comment_status=data.getInt("comment_status");
                                    notificationModel.comment_text=data.getString("comment_text");
                                    notificationModel.comment_subject=data.getString("comment_subject");
                                    notificationModel.icon_class=data.getString("icon_class");
                                    notificationModel.link=data.getString("link");
                                    notificationModel.viewed_by=data.getString("viewed_by");

                                    arrNotificationList.add(notificationModel);

                                }

                                if(arrNotificationList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                notificationAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(NotificationActivity.this,"notification API","(NotificationListActivity)","Web API Error : API Response Is Null");
                            Toast.makeText(NotificationActivity.this, "Could Not Load Notification list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NotificationActivity.this, "Could Not Load Repair Sale List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notification");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i=new Intent(NotificationActivity.this, HomeActivity.class);
//                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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
