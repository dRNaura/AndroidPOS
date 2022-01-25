package com.pos.salon.fragment.BookingFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPayment.ActivityPayment;
import com.pos.salon.activity.ActivityPosSale.ActivityPosItemList;
import com.pos.salon.adapter.BookingAdapter.BookingListAdapter;
import com.pos.salon.adapter.EventCalenderAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BookingModels.BookingListModel;
import com.pos.salon.model.BookingModels.BookingModelCalender;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.model.payment.ProductDataSend;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.utilConstant.AppConstant;

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


public class CalenderViewFragment extends Fragment {

    CalendarView calendarView;
//    ArrayList<String> arrBookingdates= new ArrayList<>();
    private final ArrayList<BookingListModel> bookingList = new ArrayList<>();
    private final ArrayList<BookingListModel> eventList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender_view, container, false);
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

        tool_title.setText("BOOKING CALENDER");

        findViewIds(view);
    }


    public void findViewIds(View view) {

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);


//        CalendarUtils.getDrawableText(getContext(), "01-12-21", null,R.color.black, 16);

        listeners();
    }
    public String checkDigit (int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void listeners() {

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                final Date date = clickedDayCalendar.getTime();
                String day = new SimpleDateFormat("dd").format(date);    // always 2 digits
                String month = new SimpleDateFormat("MM").format(date);  // always 2 digits
                String year = new SimpleDateFormat("yyyy").format(date); // 4 digit year
                String dateCal=day+"-"+month+"-"+year;
                eventList.clear();
                boolean isDateMatch=false;
                for(int a=0;a<bookingList.size();a++) {
                    if (dateCal.equalsIgnoreCase(bookingList.get(a).date_time)) {
                        BookingListModel model = bookingList.get(a);
                        eventList.add(model);
                        isDateMatch=true;
                    }
                }
                if(isDateMatch){
                    openEventsDialog(dateCal);
                }

            }
        });
        calenderBookingList();
    }
    public void calenderBookingList() {
        AppConstant.showProgress(getContext(),false);
        Retrofit retrofit = APIClient.getClientToken(getContext());
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("calendar");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("calender list", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            bookingList.clear();
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
                                        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        SimpleDateFormat spf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date newDate = null;
                                        Date newDate2 = null;
                                        try {
                                            newDate = spf.parse(data.getString("date_time"));
                                            newDate2 = spf2.parse(data.getString("date_time"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        spf = new SimpleDateFormat("dd-MM-yyyy");
                                        spf2 = new SimpleDateFormat("hh:mm a");

                                        String strDate1 = spf.format(newDate);
                                        String strTime = spf2.format(newDate2);
                                        bookingListModel.date_time=strDate1;
                                        bookingListModel.time=strTime;

                                    }
                                    if(data.has("status") && !data.isNull("status")){
                                        bookingListModel.status=data.getString("status");
                                    }
                                    if(data.has("user") && !data.isNull("user")){
                                        JSONObject object=data.getJSONObject("user");
                                        String name="";
                                        if(object.has("first_name") && !object.isNull("first_name")){
                                            name=object.getString("first_name");
                                        }
                                        if(object.has("last_name") && !object.isNull("last_name")){
                                            name= name+" "+object.getString("last_name");
                                        }
                                        bookingListModel.name=name;
                                    }
                                    bookingList.add(bookingListModel);

                                }

                                List<EventDay> events = new ArrayList<>();

                                for(int i=0;i<bookingList.size();i++){
                                    BookingListModel model =bookingList.get(i);
                                    String dateTIme =model.date_time;
                                    int month =0, dd = 0, yer = 0;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                        Date d = sdf.parse(dateTIme);
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(d);
                                        month = Integer.parseInt(checkDigit(cal.get(Calendar.MONTH)+1));
                                        dd = Integer.parseInt(checkDigit(cal.get(Calendar.DATE)));
                                        yer = Integer.parseInt(checkDigit(cal.get(Calendar.YEAR)));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(yer,month-1,dd);

                                    try {
                                        calendarView.setDate(calendar);
                                    } catch (OutOfDateRangeException e) {
                                        e.printStackTrace();
                                    }
//                                    final Date date = calendar.getTime();
//                                    String day = new SimpleDateFormat("dd").format(date);    // always 2 digits
//                                    String month2 = new SimpleDateFormat("MM").format(date);  // always 2 digits
//                                    String year = new SimpleDateFormat("yyyy").format(date); // 4 digit year
//                                    String dateCal=day+"-"+month2+"-"+year;
//                                    arrBookingdates.add(dateCal);
                                    events.add(new EventDay(calendar, R.mipmap.booking, Color.parseColor("#228B22")));
                                }

                                calendarView.setEvents(events);
//        }

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(getContext(), "calender API", "(CalenderView Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(getContext(), "Could Not Load Booking List. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(getContext(), "Could Not Load Booking List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }
    }
    public void openEventsDialog(String dateCal) {
        Dialog filterDialog = new Dialog(getContext());
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.multiple_events_dialog);
        filterDialog.setCancelable(false);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageView img_cancel_dialog = filterDialog.findViewById(R.id.img_cancel_dialog);
        RecyclerView recycler_Events = filterDialog.findViewById(R.id.recycler_Events);
        TextView dialog_title = filterDialog.findViewById(R.id.dialog_title);
        filterDialog.show();

        dialog_title.setText("Bookings On: "+dateCal);

       LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycler_Events.setLayoutManager(mLayoutManager);
        EventCalenderAdapter eventAdapter = new EventCalenderAdapter(getContext(), eventList);
        recycler_Events.setAdapter(eventAdapter);

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });
        eventAdapter.setOnItmeClicked(new EventCalenderAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                filterDialog.dismiss();

                BookingListModel model=eventList.get(position);
                BookingDetailFragment ldf = new BookingDetailFragment();
                Bundle args = new Bundle();
                args.putInt("booking_id",model.id);
                ldf.setArguments(args);
                FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter,R.anim.exit,R.anim.left_to_right,R.anim.right_to_left);
                transaction.replace(R.id.lay_calenderVie, ldf);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

}