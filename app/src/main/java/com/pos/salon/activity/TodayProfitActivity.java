package com.pos.salon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TodayProfitActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txt_opening_stock,txt_closing_stock,txt_total_purchase,txt_total_sales,txt_stock_adjustment,txt_stock_recovered;
    TextView txt_total_expense,txt_shipping_charges,txt_gross_profit,txt_net_profit;
    RelativeLayout lay_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_profit);

        toolbar = (Toolbar) findViewById(R.id.toolbar_close_register);
        setSupportActionBar(toolbar);

        fetchIDs();
        setBackNavgation();
    }
    public void fetchIDs(){

        txt_opening_stock=findViewById(R.id.txt_opening_stock);
        txt_closing_stock=findViewById(R.id.txt_closing_stock);
        txt_total_purchase=findViewById(R.id.txt_total_purchase);
        txt_total_sales=findViewById(R.id.txt_total_sales);
        txt_stock_adjustment=findViewById(R.id.txt_stock_adjustment);
        txt_stock_recovered=findViewById(R.id.txt_stock_recovered);
        txt_total_expense=findViewById(R.id.txt_total_expense);
        txt_shipping_charges=findViewById(R.id.txt_shipping_charges);
        txt_gross_profit=findViewById(R.id.txt_gross_profit);
        txt_net_profit=findViewById(R.id.txt_net_profit);
        lay_close=findViewById(R.id.lay_close);

        clicklisteners();

    }
    public void clicklisteners(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = df.format(c);

        if (AppConstant.isNetworkAvailable(TodayProfitActivity.this)) {
            profitLossDetail(date); // check register detail
        } else {
            AppConstant.openInternetDialog(TodayProfitActivity.this);
        }

        lay_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void profitLossDetail(String date) {
//        https://pos.shopplusglobal.com/api/profit-loss?start_date=2021-01-18&end_date=2021-01-18&location_id=
        AppConstant.showProgress(TodayProfitActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(TodayProfitActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.registerDetail("profit-loss?start_date="+date+"&end_date="+date+"&location_id=");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();

                            Log.e("profit-loss", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if(successstatus.equalsIgnoreCase("true")){

                                JSONObject profitLossObj = responseObject.getJSONObject("profit_details");
                                if (profitLossObj.has("opening_stock") && !profitLossObj.isNull("opening_stock")) {
                                    txt_opening_stock.setText("$ " + String.format("%.2f", Double.valueOf(profitLossObj.getString("opening_stock"))));
                                }
                                if (profitLossObj.has("closing_stock") && !profitLossObj.isNull("closing_stock")) {
                                    txt_closing_stock.setText("$ " + String.format("%.2f", Double.valueOf(profitLossObj.getInt("closing_stock"))));
                                }
                                if (profitLossObj.has("total_purchase") && !profitLossObj.isNull("total_purchase")) {
                                    txt_total_purchase.setText("$ " +  String.format("%.2f", Double.valueOf( profitLossObj.getInt("total_purchase"))));
                                }
                                if (profitLossObj.has("total_sell") && !profitLossObj.isNull("total_sell")) {
                                    txt_total_sales.setText("$ " +String.format("%.2f", Double.valueOf( profitLossObj.getInt("total_sell"))));
                                }
                                if (profitLossObj.has("total_expense") && !profitLossObj.isNull("total_expense")) {
                                    txt_total_expense.setText("$ " + String.format("%.2f", Double.valueOf(profitLossObj.getString("total_expense"))));
                                }
                                if (profitLossObj.has("total_adjustment") && !profitLossObj.isNull("total_adjustment")) {
                                    txt_stock_adjustment.setText("$ " +String.format("%.2f", Double.valueOf(profitLossObj.getString("total_adjustment"))));
                                }
                                if (profitLossObj.has("total_recovered") && !profitLossObj.isNull("total_recovered")) {
                                    txt_stock_recovered.setText("$ " +String.format("%.2f", Double.valueOf(profitLossObj.getString("total_recovered"))));
                                }
                                if (profitLossObj.has("total_transfer_shipping_charges") && !profitLossObj.isNull("total_transfer_shipping_charges")) {
                                    txt_shipping_charges.setText("$ " + String.format("%.2f", Double.valueOf(profitLossObj.getString("total_transfer_shipping_charges"))));
                                }
                                if (profitLossObj.has("total_purchase_discount") && !profitLossObj.isNull("total_purchase_discount")) {
                                    txt_total_purchase.setText("$ " +String.format("%.2f", Double.valueOf(profitLossObj.getString("total_purchase_discount"))));
                                }
                                if (profitLossObj.has("total_sell_discount") && !profitLossObj.isNull("total_sell_discount")) {
//                                    txt.setText("$ " + profitLossObj.getInt("total_sell_discount"));
                                }
                                if (profitLossObj.has("total_purchase_return") && !profitLossObj.isNull("total_purchase_return")) {
//                                    txt_cash_inHand.setText("$ " + profitLossObj.getString("total_purchase_return"));
                                }
                                if (profitLossObj.has("total_sell_return") && !profitLossObj.isNull("total_sell_return")) {
//                                    txt_cash_inHand.setText("$ " + profitLossObj.getString("total_sell_return"));
                                }
                                if (profitLossObj.has("net_profit") && !profitLossObj.isNull("net_profit")) {
                                    txt_net_profit.setText("$ " +String.format("%.2f", Double.valueOf(profitLossObj.getInt("net_profit"))));
                                }
                                if (profitLossObj.has("gross_profit") && !profitLossObj.isNull("gross_profit")) {
                                    txt_gross_profit.setText("$ " + String.format("%.2f", Double.valueOf(profitLossObj.getString("gross_profit"))));
                                }

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(TodayProfitActivity.this, "profit-loss Detail API", "(TodayProfitActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(TodayProfitActivity.this, "Could Not Get Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(TodayProfitActivity.this, "Could Not Get Detail. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle(R.string.todayProfit);
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
