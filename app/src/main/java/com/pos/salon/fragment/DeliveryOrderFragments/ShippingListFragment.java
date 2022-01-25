package com.pos.salon.fragment.DeliveryOrderFragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.adapter.DeliveryOrderAdapter.ShippingListAdapter;
import com.pos.salon.adapter.ListAdapters.ServiceStaffAdapter;
import com.pos.salon.adapter.LocationAdapter.FIlteredLocationAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.fragment.SaleDetailFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.interfacesection.Selectedtable;
import com.pos.salon.model.deliveryOrdersModel.Shipping_list_model;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.ServiceStaff;
import com.pos.salon.utilConstant.AppConstant;
import com.squareup.timessquare.CalendarPickerView;
import org.json.JSONArray;
import org.json.JSONObject;
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

import static com.pos.salon.activity.HomeActivity.tool_title;


public class ShippingListFragment extends Fragment implements Selectedtable {

   private RecyclerView listDeliveryRecycler;
    private ProgressBar progressBar;
    private boolean isScrolling = true;
    private final ArrayList<Shipping_list_model> orderList = new ArrayList<>();
    private ShippingListAdapter shippingListAdapter;
    private LinearLayoutManager mLayoutManager;
    private ImageView open_filter, close_filter;
    private LinearLayout lay_filters, lay_search, linear_apply_changes;
    private RelativeLayout rl_date_range;
    private TextView txt_customRange, txt_date_range,txt_no_resut;
    private boolean customdateSelected;
    private String business_id ="", startDate = "", endDate = "", firstDate = "", lastDate = "", paymentStatus = "", delivery_staff_id="",customer_id="";
    private FIlteredLocationAdapter contactAdapter;
    private FIlteredLocationAdapter businessAdapter;
    private ServiceStaffAdapter serviceStaffAdapter;
    private final ArrayList<BusinessLocationData> contactList = new ArrayList<>();
    private final ArrayList<BusinessLocationData> businessList = new ArrayList<>();
    private Spinner dropdownContact, dropdown_paymentStatus, dropdownServiceStaff, dropdownBusiness;
    private BusinessLocationData locationData=new BusinessLocationData();
    private ServiceStaff serviceStaffModel;
    private final ArrayList<ServiceStaff> service_staffList = new ArrayList<>();
    private int currentItems, totalItems, scrollItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipping_list, container, false);
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

        tool_title.setText("DELIVERY ORDERS");

        findViewIds(view);
    }

    public void findViewIds(View view) {

        listDeliveryRecycler = view.findViewById(R.id.listDeliveryRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        open_filter = view.findViewById(R.id.open_filter);
        lay_filters = view.findViewById(R.id.lay_filters);
        lay_search = view.findViewById(R.id.lay_search);
        linear_apply_changes = view.findViewById(R.id.linear_apply_changes);
        close_filter = view.findViewById(R.id.close_filter);
        rl_date_range = view.findViewById(R.id.rl_date_range);
        txt_date_range = view.findViewById(R.id.txt_date_range);
        dropdownContact = view.findViewById(R.id.dropdownContact);
        dropdown_paymentStatus = view.findViewById(R.id.dropdown_paymentStatus);
        dropdownBusiness = view.findViewById(R.id.dropdownBusiness);
        dropdownServiceStaff = view.findViewById(R.id.dropdownServiceStaff);
        txt_no_resut = view.findViewById(R.id.txt_no_resut);


        mLayoutManager = new LinearLayoutManager(getContext());
        listDeliveryRecycler.setLayoutManager(mLayoutManager);
        shippingListAdapter = new ShippingListAdapter(getContext(), orderList);
        listDeliveryRecycler.setAdapter(shippingListAdapter);

        final List<String> paymnetStatus = new ArrayList<String>();
        paymnetStatus.add("Select Payment Status");
        paymnetStatus.add("Paid");
        paymnetStatus.add("Due");
        paymnetStatus.add("Partial");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, paymnetStatus);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown_paymentStatus.setAdapter(dataAdapter);


        listeners();
    }

    public void listeners() {

        open_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.VISIBLE);
                close_filter.setVisibility(View.VISIBLE);
                open_filter.setVisibility(View.GONE);
                lay_search.setVisibility(View.GONE);
            }
        });

        close_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_filters.setVisibility(View.GONE);
                open_filter.setVisibility(View.VISIBLE);
                close_filter.setVisibility(View.GONE);
                lay_search.setVisibility(View.VISIBLE);

                startDate = "";
                endDate = "";
                business_id="" ;
                customer_id="";
                delivery_staff_id="";
                paymentStatus="";

                dropdown_paymentStatus.setSelection(0);
                dropdownBusiness.setSelection(0);
                dropdownServiceStaff.setSelection(0);
                dropdownContact.setSelection(0);
                txt_date_range.setText("");

                orderList.clear();
                deliveryOrdersList();
            }
        });

        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (business_id.isEmpty() && delivery_staff_id.isEmpty() && txt_date_range.getText().toString().isEmpty() && paymentStatus.isEmpty() && customer_id.isEmpty()) {
                    AppConstant.showToast(getContext(), "Select Any Field To Filter Search");
                } else {
                    lay_filters.setVisibility(View.GONE);
                    open_filter.setVisibility(View.VISIBLE);
                    close_filter.setVisibility(View.GONE);
                    lay_search.setVisibility(View.VISIBLE);
                    orderList.clear();
                    deliveryOrdersList();
                }
            }
        });


        rl_date_range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateRangeDialog();
            }
        });


        dropdown_paymentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    paymentStatus = dropdown_paymentStatus.getSelectedItem().toString();
                } else {
                    paymentStatus = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownBusiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    locationData = businessList.get(position);
                    business_id = locationData.getId();

                } else {
                    locationData = null;
                    business_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdownServiceStaff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    serviceStaffModel = service_staffList.get(position);
                    delivery_staff_id = serviceStaffModel.id;
                } else {
                    serviceStaffModel = null;
                    delivery_staff_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    locationData = contactList.get(position);
                    customer_id = locationData.getId();

                } else {
                    locationData = null;
                    customer_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shippingListAdapter.setOnItmeClicked(new ShippingListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
//                if (HomeActivity.isSaleView == true) {
                    Shipping_list_model shippingModel = orderList.get(position);
                    SaleDetailFragment ldf = new SaleDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("transactionId", String.valueOf(shippingModel.id));
                    args.putString("from", "fromShippingFragment");
                    args.putString("subFrom", "fromshippingFragment");
                    args.putInt("return_exists", shippingModel.return_exists);
                    ldf.setArguments(args);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                    transaction.replace(R.id.lay_shipping_layout, ldf);
//                    activeCenterFragments.add(ldf);setCustomA
                    transaction.addToBackStack(null);
                    transaction.commit();

//                } else {
//                    AppConstant.showToast(getContext(), "You Don't Have Permission To View Sale Detail");
//                }
            }
        });

        listDeliveryRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            deliveryOrdersList();
                        }
                    }
                }
            }
        });


        deliveryOrdersList();
        setAdapter();


    }

    private void setAdapter() {

        contactAdapter = new FIlteredLocationAdapter(getActivity(), contactList);
        dropdownContact.setAdapter(contactAdapter);

//        for (int i = 0; i < contactList.size(); i++) {
//            locationData = contactList.get(0);
//            locationId = locationData.getId();
//            dropdownContact.setSelection(0);
//        }


        businessAdapter = new FIlteredLocationAdapter(getActivity(), businessList);
        dropdownBusiness.setAdapter(businessAdapter);


        serviceStaffAdapter = new ServiceStaffAdapter(getActivity(), service_staffList, this);
        dropdownServiceStaff.setAdapter(serviceStaffAdapter);
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


    public void deliveryOrdersList() {
        if (isScrolling) {
            orderList.clear();
            AppConstant.showProgress(getContext(), false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deliveryOrder("shipping?business_id=" + business_id + "&customer_id=" +customer_id + "&payment_status=" + paymentStatus + "&start_date=" + startDate + "&end_date=" + endDate + "&delivery_staff_id=" + delivery_staff_id+"&limit="+orderList.size());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            businessList.clear();
                            service_staffList.clear();
                            contactList.clear();

                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);

                            BusinessLocationData locationDumy = new BusinessLocationData();
                            locationDumy.setId("-1");
                            locationDumy.setName("Select Customer");
                            contactList.add(0, locationDumy);

                            BusinessLocationData business = new BusinessLocationData();
                            business.setId("-1");
                            business.setName("Select Business");
                            businessList.add(0, business);

                            ServiceStaff serviceStaff = new ServiceStaff();
                            serviceStaff.id = "0";
                            serviceStaff.first_name = ("Select Service Staff");
                            service_staffList.add(0, serviceStaff);

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("delivery List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray dataObj = responseObject.getJSONArray("sells");
                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject data = dataObj.getJSONObject(i);
                                    Shipping_list_model model = new Shipping_list_model();
                                    model.id = data.getInt("id");
                                    model.transaction_date = data.getString("transaction_date");
                                    model.is_direct_sale = data.getInt("is_direct_sale");
                                    model.invoice_no = data.getString("invoice_no");
                                    model.name = data.getString("name");
                                    model.method = data.getString("method");
                                    model.payment_status = data.getString("payment_status");
                                    model.delivery_order_status = data.getString("delivery_order_status");
                                    model.final_total = data.getString("final_total");
                                    model.tax_amount = data.getString("tax_amount");
                                    model.total_before_tax = data.getString("total_before_tax");
                                    model.total_paid = data.getString("total_paid");
                                    model.business_location = data.getString("business_location");
                                    model.return_exists = data.getInt("return_exists");
                                    model.amount_return = data.getString("amount_return");

                                    orderList.add(model);


                                }
                                JSONArray contacts = responseObject.getJSONArray("contacts");
                                for (int i = 0; i < contacts.length(); i++) {
                                    JSONObject data = contacts.getJSONObject(i);
                                    BusinessLocationData model = new BusinessLocationData();
                                    model.id = String.valueOf(data.getInt("id"));
                                    model.name = data.getString("name");
                                    contactList.add(model);
                                }

                                JSONArray service_staff = responseObject.getJSONArray("service_staff");
                                for (int i = 0; i < service_staff.length(); i++) {
                                    JSONObject data = service_staff.getJSONObject(i);
                                    ServiceStaff model = new ServiceStaff();
                                    model.id = String.valueOf(data.getInt("id"));
                                    model.first_name = data.getString("first_name");
                                    model.last_name = data.getString("last_name");
                                    service_staffList.add(model);
                                }

                                JSONArray businessarr = responseObject.getJSONArray("businesses");
                                for (int i = 0; i < businessarr.length(); i++) {
                                    JSONObject data = businessarr.getJSONObject(i);
                                    BusinessLocationData busimodel = new BusinessLocationData();
                                    busimodel.id = String.valueOf(data.getInt("id"));
                                    busimodel.name = data.getString("name");
                                    businessList.add(busimodel);
                                }

                                if(orderList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                serviceStaffAdapter.notifyDataSetChanged();
                                contactAdapter.notifyDataSetChanged();
                                businessAdapter.notifyDataSetChanged();
                                shippingListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "shipping API", "(ShippingFragment Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Delivery Order List. Please Try Again", Toast.LENGTH_LONG).show();
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

    @Override
    public void tableselect(int position, String id, String name) {

    }

    @Override
    public void staffselect(int position, String id, String name) {

    }
}