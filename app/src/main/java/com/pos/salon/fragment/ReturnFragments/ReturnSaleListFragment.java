package com.pos.salon.fragment.ReturnFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.adapter.ReturnAdapters.ReturnSaleListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.listModel.ReturnSaleListModel;
import com.pos.salon.utilConstant.AppConstant;
import com.squareup.timessquare.CalendarPickerView;
import org.json.JSONArray;
import org.json.JSONObject;
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

import static com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList.strScannedBarCode;
import static com.pos.salon.activity.HomeActivity.tool_title;

public class ReturnSaleListFragment extends Fragment {

    RecyclerView list_return_SaleRecycler;
    ReturnSaleListAdapter returnSaleListAdapter;
    TextView txt_customRange,txt_reurn_list_date_range,txt_no_resut;
    String  startDate = "", endDate = "",firstDate="",lastDate="";
    public boolean customdateSelected;
    RelativeLayout rl_date_range,rl_find_return;
    ImageView open_filters,close_filters;
    LinearLayout lay_filters,lay_search,linear_apply_changes;
    ArrayList<ReturnSaleListModel> returnSalesList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    int currentItems, totalItems, scrollItems;
    private boolean isScrolling = true;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_return_sale_list, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tool_title.setText("RETURN SALE LIST");

        list_return_SaleRecycler=view.findViewById(R.id.list_return_SaleRecycler);
        rl_date_range=view.findViewById(R.id.rl_date_range);
        rl_find_return=view.findViewById(R.id.rl_find_return);
        txt_reurn_list_date_range=view.findViewById(R.id.txt_reurn_list_date_range);
        open_filters=view.findViewById(R.id.open_filter);
        close_filters=view.findViewById(R.id.close_filter);
        lay_filters=view.findViewById(R.id.lay_filters);
        lay_search=view.findViewById(R.id.lay_search);
        linear_apply_changes=view.findViewById(R.id.linear_apply_changes);
        progressBar=view.findViewById(R.id.progressBar);
        txt_no_resut=view.findViewById(R.id.txt_no_resut);


        mLayoutManager = new LinearLayoutManager(getContext());
        list_return_SaleRecycler.setLayoutManager(mLayoutManager);
        returnSaleListAdapter = new ReturnSaleListAdapter(getContext(),returnSalesList);
        list_return_SaleRecycler.setAdapter(returnSaleListAdapter);

        listeners();

    }

    public void listeners(){

        rl_find_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openScanDialog();

//                FindReturnSale ldf = new FindReturnSale();
//                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
//                transaction.replace(R.id.lay_return_layout, ldf);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });

        open_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.VISIBLE);
                open_filters.setVisibility(View.GONE);
//                lay_search.setVisibility(View.GONE);
            }
        });

        close_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.GONE);
                open_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.GONE);
//                lay_search.setVisibility(View.VISIBLE);
                txt_reurn_list_date_range.setText("");
                startDate = "";
                endDate = "";

                returnSaleList();
            }
        });


        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lay_filters.setVisibility(View.GONE);
                open_filters.setVisibility(View.VISIBLE);
                close_filters.setVisibility(View.GONE);
//                lay_search.setVisibility(View.VISIBLE);
//                txt_reurn_list_date_range.setText("");

//                startDate = "";
//                endDate = "";
                returnSalesList.clear();
                returnSaleList();


            }
        });

        rl_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateRangeDialog();
            }
        });

        returnSaleListAdapter.setOnItmeClicked(new ReturnSaleListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {

                ReturnSaleListModel model = returnSalesList.get(position);
                ReturnSaleDetailFragment ldf = new ReturnSaleDetailFragment();
                Bundle args = new Bundle();
                args.putInt("retrun_parent_sale_id", model.retrun_parent_sale_id);
                ldf.setArguments(args);
                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                transaction.replace(R.id.lay_return_layout, ldf);
                transaction.addToBackStack(null);
                transaction.commit();

                tool_title.setText("RETURN SALE DETAIL");
            }
        });
        list_return_SaleRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    currentItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    scrollItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;

                            returnSaleList();
                        }
                    }
                }
            }
        });

        returnSaleList();
    }

    public void returnSaleList(){


        if (isScrolling) {
            returnSalesList.clear();
            AppConstant.showProgress(getContext(), false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        Log.e("startDrate",startDate);
        Log.e("endDate",endDate);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.returnSaleList("sell-return",startDate,endDate,returnSalesList.size());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Return Sales List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
//                                salesList();
                                JSONArray dataObj = responseObject.getJSONArray("sells");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    ReturnSaleListModel listPosModel = new ReturnSaleListModel();
                                    listPosModel.return_sell_id=data.getInt("id");
                                    listPosModel.return_transaction_date = data.getString("transaction_date");
                                    listPosModel.return_invoice_no = data.getString("invoice_no");

                                    if(data.has("name") && !data.isNull("name")){
                                        listPosModel.return_customer_name = data.getString("name");
                                    }
                                    listPosModel.return_final_total = data.getString("final_total");
                                    listPosModel.return_business_location = data.getString("business_location");
                                    listPosModel.return_parent_sale = data.getString("parent_sale");
                                    listPosModel.retrun_parent_sale_id = data.getInt("parent_sale_id");
                                    listPosModel.return_amount_paid = data.getString("amount_paid");
                                    listPosModel.retun_payment_status = data.getString("payment_status");

                                    returnSalesList.add(listPosModel);

                                }

                                if(returnSalesList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                returnSaleListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            progressBar.setVisibility(View.GONE);
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(),"sell-return API","(ReturnSaleListFragment Screen)","Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Return Sale List Detail. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Return Sale List Detail. Please Try Again.", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


    public void openDateRangeDialog() {
        final Dialog dateRangeDialog;
        final TextView txt_apply_dates, txt_cancel_dialog, txt_todaysDate, txt_date_yesterday, txt_week_date, txt_lastThiDays, txt_this_month, txt_lastMonth, txt_currentFinacial;
        dateRangeDialog = new Dialog(getContext());
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
                txt_reurn_list_date_range.setText(firstDate);
                dateRangeDialog.dismiss();
            }
        });

        txt_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangeDialog.dismiss();
                txt_reurn_list_date_range.setText("");
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
                txt_reurn_list_date_range.setText(firstDate);
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

                txt_reurn_list_date_range.setText(firstDate + "  - " + lastDate);
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
                txt_reurn_list_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_reurn_list_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_reurn_list_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_reurn_list_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();
            }
        });

        txt_customRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customDateRange();
//                new DatePickerDialog(getContext(), date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        txt_apply_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_apply_dates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txt_customRange.getText().toString().equalsIgnoreCase("Custom Range")) {

                            Toast.makeText(getContext(), "Select a Custom Range First", Toast.LENGTH_LONG).show();

                        } else {
                            String range = txt_customRange.getText().toString();

                            txt_reurn_list_date_range.setText(range);

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

        customDialog = new Dialog(getContext());
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
                    Toast.makeText(getContext(), "Please Select a Date First", Toast.LENGTH_LONG).show();
                }
            }
        });

        customDialog.show();

    }
    public void openScanDialog() {
        final Dialog fromPrintDialog = new Dialog(getContext());
        fromPrintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        fromPrintDialog.setContentView(R.layout.layout_scan_dialog);
        fromPrintDialog.setCancelable(true);
        Window window = fromPrintDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.CENTER);
        fromPrintDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        EditText et_searchInvoice = fromPrintDialog.findViewById(R.id.et_searchInvoice);
        RelativeLayout lay_search_invoice = fromPrintDialog.findViewById(R.id.lay_search_invoice);
        RelativeLayout lay_scan_invoice = fromPrintDialog.findViewById(R.id.lay_scan_invoice);
        ImageView img_cancel_dialog = fromPrintDialog.findViewById(R.id.img_cancel_dialog);

        et_searchInvoice.setHint("ENTER RETURN SALE ID");
        fromPrintDialog.show();

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromPrintDialog.dismiss();
            }
        });
        lay_search_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_searchInvoice.getText().toString().isEmpty()) {
                    AppConstant.showToast(getContext(), "Please Enter Sale ID");
                } else {
                    fromPrintDialog.dismiss();
                    String invoice = et_searchInvoice.getText().toString();
                    ReturnSaleDetailFragment ldf = new ReturnSaleDetailFragment();
                    Bundle args = new Bundle();
                    args.putInt("retrun_parent_sale_id", Integer.parseInt(invoice));
                    ldf.setArguments(args);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                    transaction.replace(R.id.lay_return_layout, ldf);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    et_searchInvoice.setText("");
                    tool_title.setText("RETURN SALE DETAIL");
                }

            }
        });
        lay_scan_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromPrintDialog.dismiss();
                et_searchInvoice.setText("");
                strScannedBarCode = "";
                Intent intent = new Intent(getContext(), ScannerActivity.class);
                intent.putExtra("from", "fromReturnSale");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }

        });

    }
    @Override
    public void onResume() {
        super.onResume();
        if(!strScannedBarCode.isEmpty()){
            ReturnSaleDetailFragment ldf = new ReturnSaleDetailFragment();
            Bundle args = new Bundle();
            args.putInt("retrun_parent_sale_id", Integer.parseInt(strScannedBarCode));
            ldf.setArguments(args);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
            transaction.replace(R.id.lay_return_layout, ldf);
            transaction.addToBackStack(null);
            transaction.commit();
            strScannedBarCode = "";
            tool_title.setText("RETURN SALE DETAIL");
        }
    }


}
