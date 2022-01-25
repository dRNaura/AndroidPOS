package com.pos.salon.fragment.BookingFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.AddPurchaseActivity;
import com.pos.salon.adapter.BookingAdapter.BookingListAdapter;
import com.pos.salon.adapter.CustomerAdapters.CustomerSearchAdapter;
import com.pos.salon.adapter.LocationAdapter.LocationAdapter;
import com.pos.salon.adapter.SpinerItemAdapter;
import com.pos.salon.adapter.SpinnerSelectionAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.customview.DelayAutoCompleteTextView;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BookingModels.BookingListModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.pos.salon.activity.HomeActivity.tool_title;


public class BookingListFragment extends Fragment {

    RecyclerView listBookingRecycler;
    ProgressBar progressBar;
    TextView txt_no_resut;
    private LinearLayoutManager mLayoutManager;
    private BookingListAdapter bookingListAdapter;
    private final ArrayList<BookingListModel> bookingList = new ArrayList<>();
    private RelativeLayout lay_filters;
    private boolean isScrolling = true;
    private int currentItems, totalItems, scrollItems;
    private ArrayList<SpinnerModel> arrStatusList = new ArrayList<>();
    private ArrayList<BusinessLocationData> locationList=new ArrayList<>();
    DelayAutoCompleteTextView et_customer_name;
    private final ArrayList<CustomerListData> searchCustomerList = new ArrayList<CustomerListData>();
    private CustomerListData customerData;
    private String contactID = "",filter_status="",filter_date="",filter_sort ="",filter_location="";
    TextView txt_date;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_list, container, false);
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

        tool_title.setText("BOOKING LIST");

        findViewIds(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        Fragment currentFragment =getActivity().getSupportFragmentManager().findFragmentByTag("bookingListFragment");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();
    }

    public void findViewIds(View view) {


//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        fm.popBackStack("bookingListFragment", 0);
//        fm.popBackStack("bookingDetailFragment", 0);
//        fm.popBackStack("bookingEditFragment", 0);

        listBookingRecycler = view.findViewById(R.id.listBookingRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        txt_no_resut = view.findViewById(R.id.txt_no_resut);
        lay_filters = view.findViewById(R.id.lay_filters);


        mLayoutManager = new LinearLayoutManager(getContext());
        listBookingRecycler.setLayoutManager(mLayoutManager);
        bookingListAdapter = new BookingListAdapter(getContext(), bookingList);
        listBookingRecycler.setAdapter(bookingListAdapter);


        listeners();
    }

    public void listeners() {

        lay_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForFilters();
            }
        });
        bookingListAdapter.setOnItmeClicked(new BookingListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                BookingListModel model=bookingList.get(position);
                BookingDetailFragment ldf = new BookingDetailFragment();
                Bundle args = new Bundle();
                args.putInt("booking_id", model.id);
                ldf.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
                transaction.replace(R.id.lay_booking, ldf,"bookingDetailFragment");
//                    activeCenterFragments.add(ldf);
                transaction.addToBackStack("bookingDetailFragment");
                transaction.commit();
//                tool_title.setText("SALE DETAIL");
            }
        });

//        listBookingRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true;
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if (dy > 0) //check for scroll down
//                {
//                    currentItems = mLayoutManager.getChildCount();
//                    totalItems = mLayoutManager.getItemCount();
//                    scrollItems = mLayoutManager.findFirstVisibleItemPosition();
//
//                    if (isScrolling) {
//                        if ((currentItems + scrollItems) >= totalItems) {
//                            isScrolling = false;
////                            bookingList();
//                        }
//                    }
//                }
//            }
//        });
        bookingList();

    }

    public void openDialogForFilters() {
        Dialog filterDialog = new Dialog(getContext());
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.booking_filter_dialog);
        filterDialog.setCancelable(false);
        filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        ImageView img_cancel_dialog = filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_apply_changes = filterDialog.findViewById(R.id.linear_apply_changes);
        LinearLayout linear_reset = filterDialog.findViewById(R.id.linear_reset);
        Spinner dropdown_BookingStatus = filterDialog.findViewById(R.id.dropdown_BookingStatus);
        Spinner dropdown_location = filterDialog.findViewById(R.id.dropdown_location);
        Spinner dropdown_sortBy = filterDialog.findViewById(R.id.dropdown_sortBy);
         et_customer_name= filterDialog.findViewById(R.id.et_customer_name);
        RelativeLayout lay_calender= filterDialog.findViewById(R.id.lay_calender);
         txt_date= filterDialog.findViewById(R.id.txt_date);

        ArrayList<SpinnerModel> arrSortByList=new ArrayList<>();
        SpinnerModel model =new SpinnerModel();
        model.name="ALL";
        arrSortByList.add(model);

        SpinnerModel newmodel =new SpinnerModel();
        newmodel.name="Newest";
        arrSortByList.add(newmodel);

        SpinnerModel oldmodel =new SpinnerModel();
        oldmodel.name="Oldest";
        arrSortByList.add(oldmodel);

        if(customerData !=null){
            et_customer_name.setText(customerData.getText());
            contactID = String.valueOf(customerData.getId());
        }

        txt_date.setText(filter_date);

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        linear_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                bookingList();
            }
        });
        linear_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
                contactID = "";
                customerData=null;
                et_customer_name.setText("");
                et_customer_name.setText("");
                dropdown_location.setSelection(0);
                dropdown_BookingStatus.setSelection(0);
                dropdown_sortBy.setSelection(0);
                filter_location="";
                filter_sort="";
                filter_status="";
                filter_date="";

                bookingList();
            }
        });
        LocationAdapter locationAdapter = new LocationAdapter(getContext(), locationList);
        dropdown_location.setAdapter(locationAdapter);

        SpinerItemAdapter statusAdapter = new SpinerItemAdapter(getContext(), arrStatusList);
        dropdown_BookingStatus.setAdapter(statusAdapter);

        SpinerItemAdapter spinerItemAdapter = new SpinerItemAdapter(getContext(), arrSortByList);
        dropdown_sortBy.setAdapter(spinerItemAdapter);

        dropdown_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    filter_location=locationList.get(position).id;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown_BookingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    filter_status=arrStatusList.get(position).spinner_id;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown_sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position ==1){
                        filter_sort="desc";
                    }else if (position==2){
                        filter_sort="asc";

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                  String quaryText = s.toString();
                    searchCollection(quaryText);
                }
            }
        });
        lay_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), datee, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        for(int a=0;a<arrStatusList.size();a++){
            if(filter_status.equalsIgnoreCase(arrStatusList.get(a).spinner_id)){
                dropdown_BookingStatus.setSelection(a);
                break;
            }
        }
        for(int a=0;a<locationList.size();a++){
            if(filter_location.equalsIgnoreCase(locationList.get(a).id)){
                dropdown_location.setSelection(a);
                break;
            }
        }
        for(int a=0;a<arrSortByList.size();a++){
            if(arrSortByList.get(a).name.equalsIgnoreCase("Newest")) {
                if(filter_sort.equalsIgnoreCase("desc")){
                    dropdown_sortBy.setSelection(a);
                    break;
                }

            }
            else if(arrSortByList.get(a).name.equalsIgnoreCase("Oldest")) {
                if(filter_sort.equalsIgnoreCase("asc")){
                    dropdown_sortBy.setSelection(a);
                    break;
                }
            }
        }
        filterDialog.show();
    }
    final DatePickerDialog.OnDateSetListener datee = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        filter_date=sdf.format(myCalendar.getTime());
        txt_date.setText(sdf.format(myCalendar.getTime()));

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
                                CustomerSearchAdapter cutoCompleteCustomerAapter = new CustomerSearchAdapter(getActivity(), searchCustomerList);
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


    public void bookingList() {

        if (isScrolling) {
            bookingList.clear();
            AppConstant.showProgress(getContext(), false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
//            https://devpos.shopplusglobal.com/api/bookings
//            ?filter_status=canceled&filter_date=16-08-2021&filter_location=86&filter_sort =newest
            Call<ResponseBody> call = apiService.getList("bookings?filter_status="+filter_status+"&filter_date="+filter_date+"&filter_location="+filter_location+"&filter_sort="+filter_sort+"&customer_id="+contactID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();

                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("booking list", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray dataObj = responseObject.getJSONArray("bookings");

                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject data = dataObj.getJSONObject(i);
                                    BookingListModel bookingListModel = new BookingListModel();
                                    if(data.has("id") && !data.isNull("id")){
                                        bookingListModel.id=data.getInt("id");
                                    }
                                    if(data.has("company_id") && !data.isNull("company_id")){
                                        bookingListModel.company_id=data.getInt("company_id");
                                    }
                                    if(data.has("business_id") && !data.isNull("business_id")){
                                        bookingListModel.business_id=data.getInt("business_id");
                                    }
                                    if(data.has("user_id") && !data.isNull("user_id")){
                                        bookingListModel.user_id=data.getInt("user_id");
                                    }
                                    if(data.has("contact_id") && !data.isNull("contact_id")){
                                        bookingListModel.contact_id=data.getInt("contact_id");
                                    }
                                    if(data.has("location_id") && !data.isNull("location_id")){
                                        bookingListModel.location_id=data.getInt("location_id");
                                    }
                                    if(data.has("currency_id") && !data.isNull("currency_id")){
                                        bookingListModel.currency_id=data.getInt("currency_id");
                                    }
                                    if(data.has("date_time") && !data.isNull("date_time")){
                                        bookingListModel.date_time=data.getString("date_time");
                                    }
                                    if(data.has("status") && !data.isNull("status")){
                                        bookingListModel.status=data.getString("status");
                                    }

                                    if(data.has("contact") && !data.isNull("contact")){
                                        if(data.has("contact") && !data.isNull("contact")){
                                            JSONObject object=data.getJSONObject("contact");
                                            if(object.has("name") && !object.isNull("name")){
                                                bookingListModel.name=object.getString("name");
                                            }
                                            if(object.has("email") && !object.isNull("email")){
                                                bookingListModel.email=object.getString("email");
                                            }
                                            if(object.has("mobile") && !object.isNull("mobile")){
                                                bookingListModel.mobile=object.getString("mobile");
                                            }
                                        }
                                    }
                                    if(data.has("items") && !data.isNull("items")){
                                            JSONArray itemsObj = data.getJSONArray("items");
                                        String service_="";
                                            for (int a = 0; a < itemsObj.length(); a++) {
                                                JSONObject business_serviceObject=itemsObj.getJSONObject(a);
                                                JSONObject object=business_serviceObject.getJSONObject("business_service");

                                                if(object.has("name") && !object.isNull("name")){
//                                                   String dot="";
                                                    service_=service_+object.getString("name")+" . ";

                                                }

                                        }
                                        bookingListModel.service_name=service_;

                                    }
                                    bookingList.add(bookingListModel);

                                }
                                if(locationList.size() > 0){
                                    locationList.clear();
                                }
                                BusinessLocationData businessLocationData = new BusinessLocationData();
                                businessLocationData.id = "";
                                businessLocationData.name = "ALL";
                                locationList.add(businessLocationData);
                                if(responseObject.has("business_locations") && !responseObject.isNull("business_locations")) {
                                    JSONArray arrayLocation = responseObject.getJSONArray("business_locations");

                                    for (int a = 0; a < arrayLocation.length(); a++) {
                                        JSONObject locationdata = arrayLocation.getJSONObject(a);
                                        BusinessLocationData data1 = new BusinessLocationData();
                                        if (locationdata.has("id") && !locationdata.isNull("id")) {
                                            data1.id = String.valueOf(locationdata.getInt("id"));
                                        }
                                        if (locationdata.has("name") && !locationdata.isNull("name")) {
                                            data1.name = locationdata.getString("name");
                                        }
                                        locationList.add(data1);
                                    }
                                }
                                if(arrStatusList.size() > 0){
                                    arrStatusList.clear();
                                }
                                SpinnerModel statusMOdel = new SpinnerModel();
                                statusMOdel.id = 0;
                                statusMOdel.name = "ALL";
                                arrStatusList.add(statusMOdel);

                                if(responseObject.has("status") && !responseObject.isNull("status")) {
                                    JSONArray arrayLocation = responseObject.getJSONArray("status");

                                    for (int a = 0; a < arrayLocation.length(); a++) {
                                        JSONObject locationdata = arrayLocation.getJSONObject(a);
                                        SpinnerModel statusMOdel2 = new SpinnerModel();
                                        if (locationdata.has("id") && !locationdata.isNull("id")){
                                            statusMOdel2.spinner_id = locationdata.getString("id");
                                        }
                                        if (locationdata.has("name") && !locationdata.isNull("name")){
                                            statusMOdel2.name = locationdata.getString("name");
                                        }
                                        arrStatusList.add(statusMOdel2);
                                    }
                                }
                                if(bookingList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                bookingListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(getContext(), "bookings API", "(BookingList Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load BookingList. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Could Not Load Booking List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }

}