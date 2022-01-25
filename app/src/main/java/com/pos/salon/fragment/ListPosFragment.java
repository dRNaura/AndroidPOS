package com.pos.salon.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.JeoPowerDeviceSDK.Scanner.ScannerActivity;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.LocationAdapter.FIlteredLocationAdapter;
import com.pos.salon.adapter.ListAdapters.ListPosAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.listModel.ListPosModel;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.PosLocationResponse;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList.strScannedBarCode;
import static com.pos.salon.activity.HomeActivity.tool_title;

public class ListPosFragment extends Fragment {

    private final ArrayList<ListPosModel> salesList = new ArrayList<>();
    private ListPosAdapter listPosAdapter;
    private LinearLayout lay_filters;
    private LinearLayout lay_search;
    private final Calendar myCalendar = Calendar.getInstance();
    private ImageView open_filter, close_filter;
    private TextView txt_date_range, txt_customRange, txt_no_resut;
    private FrameLayout lay_pos_layout;
    private Spinner dropdownLocation, dropdownPayementStatus, dropdownSaleFrom;
    private ArrayList<BusinessLocationData> locationList;
    private PosLocationResponse posResponse;
    private String quaryText, locationId = "", contactID = "";
    private DelayAutoCompleteTextView et_customer_name;
    private CustomerSearchAdapter cutoCompleteCustomerAapter;
    private final ArrayList<CustomerListData> searchCustomerList = new ArrayList<CustomerListData>();
    private boolean isScrolling = true;
    private BusinessLocationData locationData;
    private CustomerListData customerData;
    private int currentItems, totalItems, scrollItems;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private boolean customdateSelected;
    private RelativeLayout rl_find_return;
    private final String[] paymnetStatuspos = {"00", "01", "02", "03"};
    private String paymentmethod = "", startDate = "", endDate = "", firstDate = "", lastDate = "", saleFrom = "POS";
    public boolean isSaleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_pos_fragmnet, container, false);
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

        tool_title.setText("LIST POS SALE");

        RecyclerView listposRecycler = view.findViewById(R.id.listposRecycler);
        open_filter = view.findViewById(R.id.open_filter);
        close_filter = view.findViewById(R.id.close_filter);
        lay_filters = view.findViewById(R.id.lay_filters);
        lay_search = view.findViewById(R.id.lay_search);
        LinearLayout rl_select_location = view.findViewById(R.id.rl_select_location);
        RelativeLayout rl_date_range = view.findViewById(R.id.rl_date_range);
//        txt_select_location = view.findViewById(R.id.txt_select_location);
        dropdownLocation = view.findViewById(R.id.dropdownLocation);
        txt_date_range = view.findViewById(R.id.txt_date_range);
        et_customer_name = view.findViewById(R.id.et_customer_name);
        LinearLayout linear_apply_changes = view.findViewById(R.id.linear_apply_changes);
        lay_pos_layout = view.findViewById(R.id.lay_pos_layout);
        progressBar = view.findViewById(R.id.progressBar);
        dropdownPayementStatus = view.findViewById(R.id.dropdown_paymentStatus);
        dropdownSaleFrom = view.findViewById(R.id.dropdownSaleFrom);
        txt_no_resut = view.findViewById(R.id.txt_no_resut);
        rl_find_return = view.findViewById(R.id.rl_find_return);

        mLayoutManager = new LinearLayoutManager(getContext());
        listposRecycler.setLayoutManager(mLayoutManager);
        listPosAdapter = new ListPosAdapter(getContext(), salesList);
        listposRecycler.setAdapter(listPosAdapter);


//        listeners();
//
//    }
//
//    public void listeners() {

//add payment methods to list

        final List<String> paymnetStatus = new ArrayList<String>();
        paymnetStatus.add("Select Payment Status");
        paymnetStatus.add("Paid");
        paymnetStatus.add("Due");
        paymnetStatus.add("Partial");

        final List<String> saleFromList = new ArrayList<String>();
        saleFromList.add("Sale From");
        saleFromList.add("POS");
        saleFromList.add("ONLINE");


        listposRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            salesList();
                        }
                    }
                }
            }
        });


        dropdownMenuCollection();

        rl_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateRangeDialog();
            }
        });

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isSaleView = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.view")) {
                    isSaleView = true;
                }

            }
        }
        rl_find_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanDialog();
            }
        });

        listPosAdapter.setOnItmeClicked(new ListPosAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {

                if (isSaleView) {
                    ListPosModel listPosModel = salesList.get(position);
                    SaleDetailFragment ldf = new SaleDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("transactionId", listPosModel.transactionId);
                    args.putString("from", "fromListPosFragment");
                    args.putString("subFrom", "fromListPosFragment");
                    args.putInt("return_exists", listPosModel.return_exists);
                    args.putString("id_type", "transaction");
                    ldf.setArguments(args);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                    transaction.replace(R.id.lay_pos_layout, ldf);
                    transaction.addToBackStack(null);
                    transaction.commit();
//                    tool_title.setText("SALE DETAIL");
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
                }
            }
        });

        open_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.VISIBLE);
                close_filter.setVisibility(View.VISIBLE);
                open_filter.setVisibility(View.GONE);
//                lay_search.setVisibility(View.GONE);
            }
        });

        close_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.GONE);
                open_filter.setVisibility(View.VISIBLE);
                close_filter.setVisibility(View.GONE);
//                lay_search.setVisibility(View.VISIBLE);
                locationId = "";
                startDate = "";
                endDate = "";
                paymentmethod = "";
                contactID = "";
                saleFrom = "";
                txt_date_range.setText("");
                et_customer_name.setText("");
                dropdownPayementStatus.setSelection(0);
                dropdownSaleFrom.setSelection(0);
                salesList.clear();
                salesList();
            }
        });
        final DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        rl_select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, paymnetStatus);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownPayementStatus.setAdapter(dataAdapter);

        dropdownPayementStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                paymentmethod = paymnetStatuspos[position];

//                paymentmethod=dropdownPayementStatus.getSelectedItem().toString();

                if (!paymentmethod.equalsIgnoreCase("00")) {
                    paymentmethod = dropdownPayementStatus.getSelectedItem().toString();
                } else {
                    paymentmethod = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> saleFromAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, saleFromList);
        saleFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSaleFrom.setAdapter(saleFromAdapter);
        int posi = saleFromAdapter.getPosition(saleFromList.get(1));
        dropdownSaleFrom.setSelection(posi);
        dropdownSaleFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    saleFrom = dropdownSaleFrom.getSelectedItem().toString();
                } else {
                    saleFrom = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    locationData = locationList.get(position);
                    locationId = locationData.getId();

                } else {
                    locationData = null;
                    locationId = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationId.isEmpty() && et_customer_name.getText().toString().isEmpty() && txt_date_range.getText().toString().isEmpty() && paymentmethod.isEmpty()) {
                    AppConstant.showSnakeBar(lay_pos_layout, "Select Any Field To Filter Search");
                } else {
                    lay_filters.setVisibility(View.GONE);
                    open_filter.setVisibility(View.VISIBLE);
                    close_filter.setVisibility(View.GONE);
//                    lay_search.setVisibility(View.VISIBLE);
                    salesList.clear();
                    salesList();
                }
            }
        });

        et_customer_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    quaryText = s.toString();
                    searchCollection(quaryText);
                }
            }
        });

        if (AppConstant.isNetworkAvailable(getContext())) {
            salesList();
        } else {
            AppConstant.openInternetDialog(getContext());
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        salesList();
//    }

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
                txt_date_range.setText(firstDate);
                dateRangeDialog.dismiss();
            }
        });

        txt_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateRangeDialog.dismiss();
                txt_date_range.setText("");
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
                txt_date_range.setText(firstDate);
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

                txt_date_range.setText(firstDate + "  - " + lastDate);
                dateRangeDialog.dismiss();


            }
        });
        txt_lastThiDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = Calendar.getInstance().getTime();//getting date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
                endDate = formatter.format(today);
                lastDate = formatter2.format(today);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                Date newDate = calendar.getTime();

                startDate = formatter.format(newDate);
                firstDate = formatter2.format(newDate);
//
                txt_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_date_range.setText(firstDate + "  - " + lastDate);
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

                txt_date_range.setText(firstDate + "  - " + lastDate);
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

                            Toast.makeText(getContext(), "Select A Custom Range First", Toast.LENGTH_LONG).show();

                        } else {
                            String range = txt_customRange.getText().toString();

                            txt_date_range.setText(range);

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
                    Toast.makeText(getContext(), "Please Select A Date First", Toast.LENGTH_LONG).show();
                }
            }
        });

        customDialog.show();


    }

    private void dropdownMenuCollection() {
//        AppConstant.showProgress(getContext(), false);

        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<PosLocationResponse> call = apiService.getPosData("pos");
            call.enqueue(new Callback<PosLocationResponse>() {
                @Override
                public void onResponse(@NonNull Call<PosLocationResponse> call, @NonNull Response<PosLocationResponse> response) {
                    if (response.body() != null) {

                        AppConstant.hideProgress();
                        posResponse = response.body();
                        if (posResponse.isSuccess()) {
                            locationList = posResponse.getBusiness_locations();
                            setAdapter();
                        }
                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(getContext(), "pos API", "(ListPosFragment Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(getContext(), "Could Not Load Locations List. Please Try Again", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<PosLocationResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Locations List. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        txt_customRange.setText(sdf.format(myCalendar.getTime()));
    }

    private void setAdapter() {
        BusinessLocationData locationDumy = new BusinessLocationData();
        locationDumy.setId("-1");
        locationDumy.setName("Select Location");
        locationList.add(0, locationDumy);
        FIlteredLocationAdapter locationAdapter = new FIlteredLocationAdapter(getActivity(), locationList);
        dropdownLocation.setAdapter(locationAdapter);

        for (int i = 0; i < locationList.size(); i++) {
            locationData = locationList.get(1);
            locationId = locationData.getId();
            dropdownLocation.setSelection(1);
        }
    }

    public void salesList() {

        if (isScrolling) {
            salesList.clear();
            AppConstant.showProgress(getContext(), false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(getContext());
        Log.e("paymentMethod", paymentmethod);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getSalesFilters("sales?limit=" + salesList.size() + "&payment_status=" + paymentmethod + "&location_id=" + locationId + "&contact_id=" + contactID + "&start_date=" + startDate + "&end_date=" + endDate + "&sale_from=" + saleFrom);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("Sales List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray dataObj = responseObject.getJSONArray("sells");
                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject data = dataObj.getJSONObject(i);
                                    ListPosModel listPosModel = new ListPosModel();
                                    if (data.has("transaction_date") && !data.isNull("transaction_date")) {
                                        listPosModel.order_Date = data.getString("transaction_date");
                                    }
                                    if (data.has("invoice_no") && !data.isNull("invoice_no")) {
                                        listPosModel.invoice_no = data.getString("invoice_no");
                                    }
                                    if (data.has("name") && !data.isNull("name")) {
                                        listPosModel.customer_name = data.getString("name");
                                    } else {
                                        listPosModel.customer_name = "";
                                    }
                                    if (data.has("payment_status") && !data.isNull("payment_status")) {
                                        listPosModel.payemnt_method = data.getString("payment_status");
                                    }
                                    if (data.has("business_location") && !data.isNull("business_location")) {
                                        listPosModel.location = data.getString("business_location");
                                    }
                                    if (data.has("id") && !data.isNull("id")) {
                                        listPosModel.transactionId = String.valueOf(data.getInt("id"));
                                    }
                                    if (data.has("return_exist") && !data.isNull("return_exist")) {
                                        listPosModel.return_exists = data.getInt("return_exist");
                                    }
                                    if (data.has("is_direct_sale") && !data.isNull("is_direct_sale")) {
                                        listPosModel.is_direct_sale = data.getInt("is_direct_sale");
                                    }
                                    if (data.has("final_total") && !data.isNull("final_total")) {
                                        listPosModel.final_total = data.getString("final_total");
                                    }
                                    if (data.has("final_total") && !data.isNull("final_total")) {
                                        listPosModel.tax_amount = data.getString("tax_amount");
                                    }
                                    if (data.has("discount_amount") && !data.isNull("discount_amount")) {
                                        listPosModel.discount_amount = data.getString("discount_amount");
                                    }
                                    if (data.has("discount_type") && !data.isNull("discount_type")) {
                                        listPosModel.discount_type = data.getString("discount_type");
                                    }
                                    if (data.has("total_before_tax") && !data.isNull("total_before_tax")) {
                                        listPosModel.total_before_tax = data.getString("total_before_tax");
                                    }
                                    if (data.has("total_paid") && !data.isNull("total_paid")) {
                                        listPosModel.total_paid = data.getString("total_paid");
                                    }
                                    if (data.has("return_paid") && !data.isNull("return_paid")) {
                                        listPosModel.return_paid = data.getString("return_paid");
                                    }
                                    if (data.has("amount_return") && !data.isNull("amount_return")) {
                                        listPosModel.amount_return = data.getString("amount_return");
                                    }

                                    salesList.add(listPosModel);
                                }

                                if (salesList.size() == 0) {
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                } else {
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                listPosAdapter.notifyDataSetChanged();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "sales API", "(ListPosFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load POS Sale List. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Throwable", t.toString());
                    AppConstant.hideProgress();
                    //  Toast.makeText(getContext(), "Could Not Load POS Sale List. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void searchCollection(final String quaryText) {

        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {

            APIInterface apiService = retrofit.create(APIInterface.class);

            Call<ResponseBody> call = apiService.getCustomerList("get-customers?term=" + quaryText);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {

                            searchCustomerList.clear();
                            String respo = response.body().string();
                            Log.e("Search Product", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray dataObj = responseObject.getJSONArray("contacts");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    CustomerListData customerListData = new CustomerListData();
                                    customerListData.setId(data.getInt("id"));
                                    customerListData.setText(data.getString("text"));
                                    if (data.has("mobile") && !data.isNull("mobile")) {
                                        customerListData.setMobile(data.getString("mobile"));
                                    }
                                    customerListData.setLandmark(data.getString("landmark"));
                                    customerListData.setCity(data.getString("city"));
                                    customerListData.setState(data.getString("state"));
                                    customerListData.setPay_term_number(data.getString("pay_term_number"));
                                    customerListData.setPay_term_type(data.getString("pay_term_type"));
                                    if (data.has("email") && !data.isNull("email")) {
                                        customerListData.setEmail(data.getString("email"));
                                    }
                                    searchCustomerList.add(customerListData);
                                }
                                cutoCompleteCustomerAapter = new CustomerSearchAdapter(getActivity(), searchCustomerList);
                                et_customer_name.setAdapter(cutoCompleteCustomerAapter);
                                et_customer_name.setThreshold(1);
                            }

                        } else {
                            AppConstant.sendEmailNotification(getContext(), "get-customers API", "(ListPosFragment Screen)", "Web API Error : APi Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Could Not Load Customer List. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }

        et_customer_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof CustomerListData) {
                    customerData = (CustomerListData) item;
                }
                et_customer_name.setText(quaryText);
                contactID = String.valueOf(customerData.getId());

            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            salesList();
        }
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
        et_searchInvoice.setHint("ENTER INVOICE NO.");
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
                    AppConstant.showToast(getContext(), "Please Enter Invoice No.");
                } else {
                    if (isSaleView) {
                        fromPrintDialog.dismiss();
                        String invoice = et_searchInvoice.getText().toString();
                        SaleDetailFragment ldf = new SaleDetailFragment();
                        Bundle args = new Bundle();
                        args.putString("transactionId", invoice);
                        Log.e("id", invoice);
                        args.putString("from", "fromListPosFragment");
                        args.putString("subFrom", "fromListPosFragment");
                        args.putInt("return_exists", 0);
                        args.putString("id_type", "invoice");
                        ldf.setArguments(args);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                        transaction.replace(R.id.lay_pos_layout, ldf);
//                        activeCenterFragments.add(ldf);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        et_searchInvoice.setText("");
//                    tool_title.setText("SALE DETAIL");
                    } else {
                        AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
                    }
                }

            }
        });
        lay_scan_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSaleView) {
                    fromPrintDialog.dismiss();
                    et_searchInvoice.setText("");
                    strScannedBarCode = "";
                    Intent intent = new Intent(getContext(), ScannerActivity.class);
                    intent.putExtra("from", "findInvoice");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
                }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!strScannedBarCode.isEmpty()) {
            if (isSaleView) {
                SaleDetailFragment ldf = new SaleDetailFragment();
                Bundle args = new Bundle();
                args.putString("transactionId", strScannedBarCode);
                args.putString("from", "fromListPosFragment");
                args.putString("subFrom", "fromListPosFragment");
                args.putInt("return_exists", 0);
                args.putString("id_type", "invoice");
                ldf.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                transaction.replace(R.id.lay_pos_layout, ldf);
//           activeCenterFragments.add(ldf);
                transaction.addToBackStack(null);
                transaction.commit();
//                    tool_title.setText("SALE DETAIL");
                strScannedBarCode = "";
            } else {
                AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
            }
        }
    }
}
