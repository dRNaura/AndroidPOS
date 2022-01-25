package com.pos.salon.activity.ActivityPurchases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.PurchaseSectionAdapters.PurchaseListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.PurchaseModel.PurchaseListModel;
import com.pos.salon.utilConstant.AppConstant;
import com.squareup.timessquare.CalendarPickerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PurchasesListActivity extends AppCompatActivity {
    Toolbar toolbar;
    private boolean isScrolling = true;
    int currentItems, totalItems, scrollItems;
    ProgressBar progressBar;
    RecyclerView list_purchase_recycler;
    LinearLayoutManager layoutManager;
    ArrayList<PurchaseListModel> arrPurchaseList = new ArrayList<>();
    TextView txt_no_resut,txt_list_date_range,txt_customRange;
    PurchaseListAdapter purchaseListAdapter;
    String startDate = "", endDate = "",firstDate="",lastDate="";
    ImageView close_filters,open_filters;
    LinearLayout lay_filters,lay_search,linear_apply_changes;
    RelativeLayout rl_date_range;
    public boolean customdateSelected;
    @SuppressLint("StaticFieldLeak")
    public static Activity purchaseListActivity;
    public boolean isPurchaseView = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_list);

        purchaseListActivity=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list_purchase_recycler=findViewById(R.id.list_purchase_recycler);
        progressBar=findViewById(R.id.progressBar);
        txt_no_resut=findViewById(R.id.txt_no_resut);
        close_filters=findViewById(R.id.close_filter);
        open_filters=findViewById(R.id.open_filters);
        rl_date_range=findViewById(R.id.rl_date_range);
        lay_filters=findViewById(R.id.lay_filters);
        lay_search=findViewById(R.id.lay_search);
        txt_list_date_range = findViewById(R.id.txt_list_date_range);
        linear_apply_changes=findViewById(R.id.linear_apply_changes);

        layoutManager = new LinearLayoutManager(this);
        list_purchase_recycler.setLayoutManager(layoutManager);
        purchaseListAdapter = new PurchaseListAdapter(this, arrPurchaseList);
        list_purchase_recycler.setAdapter(purchaseListAdapter);

        listeners();
        setBackNavgation();
    }

    public void listeners(){

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

            isPurchaseView = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {

                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("purchase.view")) {
                    isPurchaseView = true;
                }

            }
        }

        purchaseListAdapter.setOnItmeClicked(new PurchaseListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                if(isPurchaseView){
                    PurchaseListModel model = arrPurchaseList.get(position);
                    Intent i=new Intent(PurchasesListActivity.this, PurchaseDetailActivity.class);
                    i.putExtra("purchase_id", model.id);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(PurchasesListActivity.this,"You Don't Have Permission To View Detail");
                }

            }
        });

        open_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.VISIBLE);
                open_filters.setVisibility(View.GONE);
                lay_search.setVisibility(View.GONE);
            }
        });
        close_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.GONE);
                open_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.GONE);
                lay_search.setVisibility(View.VISIBLE);
                txt_list_date_range.setText("");
                startDate="";
                endDate="";
                arrPurchaseList.clear();
                isScrolling = true;
                purchaseList();
            }
        });
        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lay_filters.setVisibility(View.GONE);
                open_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.GONE);
                lay_search.setVisibility(View.VISIBLE);
//                txt_list_date_range.setText("");
                isScrolling = true;
                arrPurchaseList.clear();

                purchaseList();


            }
        });


        rl_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateRangeDialog();
            }
        });


        list_purchase_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            purchaseList();
                        }
                    }
                }
            }
        });

        if(AppConstant.isNetworkAvailable(PurchasesListActivity.this)){
            purchaseList();
        }else{
            AppConstant.openInternetDialog(PurchasesListActivity.this);
        }

    }

    public void openDateRangeDialog() {

        final Dialog dateRangeDialog;
        final TextView txt_apply_dates, txt_cancel_dialog, txt_todaysDate, txt_date_yesterday, txt_week_date, txt_lastThiDays, txt_this_month, txt_lastMonth, txt_currentFinacial;
        dateRangeDialog = new Dialog(PurchasesListActivity.this);
        dateRangeDialog.setContentView(R.layout.date_range);
        Window window = dateRangeDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        txt_cancel_dialog = dateRangeDialog.findViewById(R.id.txt_cancel_dialog);
        txt_todaysDate = dateRangeDialog.findViewById(R.id.txt_todaysDate);
        txt_date_yesterday = dateRangeDialog.findViewById(R.id.txt_date_yesterday);
        txt_week_date = dateRangeDialog.findViewById(R.id.txt_week_date);
        txt_lastThiDays = dateRangeDialog.findViewById(R.id.txt_lastThiDays);
        txt_this_month = dateRangeDialog.findViewById(R.id.txt_this_month);
        txt_lastMonth = dateRangeDialog.findViewById(R.id.txt_lastMonth);
        txt_currentFinacial = dateRangeDialog.findViewById(R.id.txt_currentFinacial);
        txt_customRange = dateRangeDialog.findViewById(R.id.txt_customRange);
        txt_apply_dates = dateRangeDialog.findViewById(R.id.txt_apply_dates);

        txt_todaysDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date today = Calendar.getInstance().getTime();//getting date


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat todayformatter = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                startDate = formatter.format(today);
                firstDate = todayformatter.format(today);
                txt_list_date_range.setText(firstDate);
                dateRangeDialog.dismiss();
            }
        });

        txt_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangeDialog.dismiss();
                txt_list_date_range.setText("");
                startDate = "";
                endDate = "";
            }
        });
        txt_date_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date mydate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat yesterdayformatter = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(mydate);
                startDate = formatter.format(mydate);

                firstDate = yesterdayformatter.format(mydate);
                txt_list_date_range.setText(firstDate);
                dateRangeDialog.dismiss();
            }
        });
        txt_week_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                lastDate = formatter2.format(today);

                Date newDate = new Date(today.getTime() - 604800000L);

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);

                txt_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();


//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(myDate);
//                calendar.add(Calendar.DAY_OF_YEAR, -7);
//                Date newDate = calendar.getTime();


            }
        });
        txt_lastThiDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                lastDate=formatter2.format(today);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                Date newDate = calendar.getTime();

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);
//
                txt_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();
            }
        });
        txt_this_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                lastDate = formatter2.format(today);

                Calendar c = Calendar.getInstance();   // this takes current date
                c.set(Calendar.DAY_OF_MONTH, 1);
                Date newDate = c.getTime();

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);

                txt_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();


            }
        });
        txt_lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                lastDate = formatter2.format(today);

                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date newDate = aCalendar.getTime();

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);

                txt_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();


            }
        });
        txt_currentFinacial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");

                endDate = formatter.format(today);
                lastDate = formatter2.format(today);

                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.YEAR, 0);
                aCalendar.set(Calendar.DAY_OF_YEAR, 1);
                Date newDate = aCalendar.getTime();

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);

                txt_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();
            }
        });

        txt_customRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDateRange();

            }
        });
        txt_apply_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_apply_dates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txt_customRange.getText().toString().equalsIgnoreCase("Custom Range")) {

                            Toast.makeText(PurchasesListActivity.this, "Select A Custom Range First", Toast.LENGTH_LONG).show();

                        } else {
                            String range = txt_customRange.getText().toString();

                            txt_list_date_range.setText(range);

                            dateRangeDialog.dismiss();

                        }
                    }
                });
            }
        });

        dateRangeDialog.show();
    }

    public void customDateRange() {

        final Dialog customDialog;
        Calendar myCalendar = Calendar.getInstance();

        customDialog = new Dialog(PurchasesListActivity.this);
        customDialog.setContentView(R.layout.dates_dialog);
        Window window = customDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        final CalendarPickerView calendar_view = customDialog.findViewById(R.id.calendar_view);

//getting min date
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.YEAR, -1);
        aCalendar.set(Calendar.DAY_OF_YEAR, 1);
        Date newDate = aCalendar.getTime();


        calendar_view.init(newDate, myCalendar.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE);

        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                customdateSelected = true;
//                Toast.makeText(getContext(),"Selected Date is : " +date.toString(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onDateUnselected(Date date) {

//                Toast.makeText(getContext(),"UnSelected Date is : " +date.toString(),Toast.LENGTH_LONG).show();
            }
        });
        Button btn_show_dates = (Button) customDialog.findViewById(R.id.btn_show_dates);
        btn_show_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customdateSelected) {
                    String strtDate = calendar_view.getSelectedDates().get(0).toString();

                    SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat spff = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    Date newDate = null;
                    Date newDatee = null;
                    try {
                        newDate = spf.parse(strtDate);
                        newDatee = spff.parse(strtDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf = new SimpleDateFormat("yyyy-MM-dd");
                    spff = new SimpleDateFormat("dd/MM/yyyy");
                    startDate = spf.format(newDate);
                    firstDate = spff.format(newDatee);


                    String enDate = calendar_view.getSelectedDates().get(calendar_view.getSelectedDates().size() - 1).toString();
                    SimpleDateFormat spf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat spff2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    Date newDate1 = null;
                    Date newDate11 = null;
                    try {
                        newDate1 = spf2.parse(enDate);
                        newDate11 = spff2.parse(enDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    spf2 = new SimpleDateFormat("yyyy-MM-dd");
                    spff2 = new SimpleDateFormat("dd/MM/yyyy");
                    endDate = spf2.format(newDate1);
                    lastDate = spff2.format(newDate11);

                    txt_customRange.setText(firstDate + "  - " + lastDate);

                    customDialog.dismiss();

                    customdateSelected = false;

                } else {
                    Toast.makeText(PurchasesListActivity.this, "Please Select A Date First", Toast.LENGTH_LONG).show();
                }
            }
        });

        customDialog.show();



    }


    public void purchaseList(){

        if (isScrolling) {
            arrPurchaseList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("purchases?limit="+arrPurchaseList.size()+"&start_date="+startDate+"&end_date="+endDate);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e(" purchase List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray dataObj = responseObject.getJSONArray("purchases");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    PurchaseListModel purchaseListModel = new PurchaseListModel();
                                    purchaseListModel.id=data.getInt("id");
                                    purchaseListModel.transaction_date=data.getString("transaction_date");

                                    if(data.has("ref_no") && !data.isNull("ref_no")){
                                        purchaseListModel.ref_no=data.getString("ref_no");
                                    }
                                    if(data.has("name") && !data.isNull("name")){
                                        purchaseListModel.name=data.getString("name");
                                    }

                                    if(data.has("status") && !data.isNull("status")){
                                        purchaseListModel.status=data.getString("status");
                                    }
                                    if(data.has("payment_status") && !data.isNull("payment_status")){
                                        purchaseListModel.payment_status=data.getString("payment_status");
                                    }
                                    if(data.has("final_total") && !data.isNull("final_total")){
                                        purchaseListModel.final_total=data.getString("final_total");
                                    }
                                    if(data.has("location_name") && !data.isNull("location_name")){
                                        purchaseListModel.location_name=data.getString("location_name");
                                    }
                                    if(data.has("amount_paid") && !data.isNull("amount_paid")){
                                        purchaseListModel.amount_paid=data.getString("amount_paid");
                                    }
                                    if(data.has("amount_return") && !data.isNull("amount_return")){
                                        purchaseListModel.amount_return=data.getString("amount_return");
                                    }

                                    arrPurchaseList.add(purchaseListModel);

                                }

                                if(arrPurchaseList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                purchaseListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(PurchasesListActivity.this,"purchases list API","(PurchaseList)","Web API Error : API Response Is Null");
                            Toast.makeText(PurchasesListActivity.this, "Could Not Load Purchase list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PurchasesListActivity.this, "Could Not Load Purchase List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("PURCHASES LIST");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
