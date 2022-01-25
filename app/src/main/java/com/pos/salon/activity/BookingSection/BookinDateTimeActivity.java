package com.pos.salon.activity.BookingSection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPayment.ActivityPayment;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.BookingAdapter.BookingTimeAdapter;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.payment.PaymentDataSend;
import com.pos.salon.model.payment.ProductDataSend;
import com.pos.salon.model.posLocation.CurriencyData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookinDateTimeActivity extends AppCompatActivity {

    Toolbar toolbar;
    CalendarView calendarView;
    LinearLayout lay_select_time;
    RelativeLayout rlNext;
    TextView txt_available_time;
    RecyclerView recycler_time;
    BookingTimeAdapter bookingTimeAdapter;
    ArrayList<ContentModel> arrTimeList = new ArrayList<>();
    String orderr_type = "", tablee_id = "", waiterr_id = "", comingFrom = "", parentComingFrom = "",repair_product_id="";
    PaymentDataSend paymemtData;
    ArrayList<ProductDataSend> cartData;
    String paidamount="",editOrExchange="";
    String taxrate="";
    String taxcalculation="";
    String shippingdetail="";
    String shippingcharges="";
    String subtotal="";
    String changereturn="";
    String locationId="";
    int customerId=0, transactionId = 0;
    String partialpayment="";
    String discounttype="";
    Double discountamt = 0.0;
    CurriencyData currencydata;
    SharedPreferences sp_selectedcustomer, sp_countproduct, sp_modifiers, sp_cartSave;
    SharedPreferences.Editor ed_selectedcustomer, ed_countproduct, ed_modifiers, ed_cartSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookin_date_time);


        findViews();

    }
    public void findViews(){

        comingFrom = getIntent().getStringExtra("comingFrom");
        parentComingFrom = getIntent().getStringExtra("parentfrom");

        paymemtData = (PaymentDataSend) getIntent().getSerializableExtra("paymentObject");

        cartData = paymemtData.getProducts();// dbHelper.getAllItem();
        paidamount = getIntent().getStringExtra("paidamount");
        taxrate = getIntent().getStringExtra("taxrate");
        taxcalculation = getIntent().getStringExtra("taxcalculation"); //in this tax id => tax amount value sent.
        subtotal = getIntent().getStringExtra("subtotal");
        changereturn = getIntent().getStringExtra("changereturn");
        orderr_type = getIntent().getStringExtra("selected_ordertype");
        tablee_id = getIntent().getStringExtra("tablee_id");
        waiterr_id = getIntent().getStringExtra("waiterr_id");
        repair_product_id = getIntent().getStringExtra("repair_product_id");
         locationId = getIntent().getStringExtra("locationId");

        customerId = getIntent().getIntExtra("customerId", 0);
        Log.e("CustomerId", "" + customerId);

        transactionId = getIntent().getIntExtra("transaction_id", 0);
        System.out.println("THE TRANSACTION ID IS " + transactionId);

        if(getIntent().hasExtra("editOrExchnage")){
            editOrExchange=getIntent().getStringExtra("editOrExchnage");
        }

        if (getIntent().hasExtra("partialpayment")) {
            partialpayment = getIntent().getStringExtra("partialpayment");
            //Toast.makeText(this, "NotPartial"+partialpayment, Toast.LENGTH_LONG).show();
        } else {
            partialpayment = "null";
            //Toast.makeText(this, "NotPartial", Toast.LENGTH_LONG).show();

        }

        if (getIntent().hasExtra("shippingdetail")) {
            shippingdetail = getIntent().getStringExtra("shippingdetail");
        } else {
            shippingdetail = "";
        }
        if (getIntent().hasExtra("shippingcharges")) {
            shippingcharges = getIntent().getStringExtra("shippingcharges");
        } else {
            shippingcharges = "0.00";
        }

        if (getIntent().hasExtra("discounttype")) {
            discounttype = getIntent().getStringExtra("discounttype");

        } else {
            discounttype = "";
        }
        // Toast.makeText(this, ""+discounttype, Toast.LENGTH_LONG).show();
        if (getIntent().hasExtra("discountamount")) {

            discountamt = getIntent().getDoubleExtra("discountamount", 0.0);
        } else {
            discountamt = 0.0;
        }

        currencydata = (CurriencyData) getIntent().getSerializableExtra("currency");


        // Fetched shared preference values
        sp_modifiers = getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE);
        ed_modifiers = sp_modifiers.edit();

        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
        ed_selectedcustomer = sp_selectedcustomer.edit();
        //end : Fetched shared prefernece values.

        lay_select_time = findViewById(R.id.lay_select_time);
        rlNext = findViewById(R.id.rlNext);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        txt_available_time =findViewById(R.id.txt_available_time);
        recycler_time =findViewById(R.id.recycler_time);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        List<Calendar> calendars = new ArrayList<>();
        calendars.add(min);
        calendarView.setMinimumDate(min);
        calendarView.setForwardButtonImage(getResources().getDrawable(R.mipmap.forward));
        calendarView.setHeaderColor(R.color.white);
        calendarView.setHeaderLabelColor(R.color.black);
        calendarView.setPreviousButtonImage(getResources().getDrawable(R.mipmap.backward));

//        calendarView.setDisabledDays();

//        calendarView.setMaximumDate(max);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this,2);
        recycler_time.setLayoutManager(mLayoutManager);
        bookingTimeAdapter = new BookingTimeAdapter(this, arrTimeList);
        recycler_time.setAdapter(bookingTimeAdapter);

        lsiteners();
        setBackNavgation();
    }

    public void lsiteners(){

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar min = Calendar.getInstance();
                Calendar clickedDayCalendar = eventDay.getCalendar();
                final Date date = clickedDayCalendar.getTime();
                String day = new SimpleDateFormat("dd").format(date);    // always 2 digits
                String month = new SimpleDateFormat("MM").format(date);  // always 2 digits
                String year = new SimpleDateFormat("yyyy").format(date); // 4 digit year
                Log.e("time", String.valueOf(clickedDayCalendar.getTime()));

                try {
                    if (new SimpleDateFormat("dd/MM/yyyy").parse(day+"/"+month+"/"+year).before(new Date())) {
                        txt_available_time.setText("");
                        lay_select_time.setVisibility(View.GONE);
                        Toast.makeText(BookinDateTimeActivity.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                    }else{
                        txt_available_time.setText(day+"-"+month+"-"+year);
                        lay_select_time.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                lay_select_time.setVisibility(View.GONE);
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                lay_select_time.setVisibility(View.GONE);
            }
        });
        rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForFilters();
            }
        });




    }
    public void openDialogForFilters() {

        Dialog filterDialog = new Dialog(this);
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.booking_detail_dialog);
        filterDialog.setCancelable(false);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.CENTER);

        ImageView img_cancel_dialog=filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_apply_changes=filterDialog.findViewById(R.id.linear_apply_changes);


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
               Intent intent = new Intent(BookinDateTimeActivity.this, ActivityPayment.class);
               intent.putExtra("subtotal", subtotal);
                intent.putExtra("paymentObject", paymemtData);
                intent.putExtra("paidamount", String.valueOf(paidamount));
                intent.putExtra("comingFrom", comingFrom);
                intent.putExtra("editOrExchnage", editOrExchange);
                intent.putExtra("discounttype", discounttype);
                intent.putExtra("discountamount", Double.valueOf(discountamt)); // sending text value only.
                intent.putExtra("taxrate", taxrate);
                intent.putExtra("taxcalculation", taxcalculation);
                intent.putExtra("shippingdetail", shippingdetail);
                intent.putExtra("shippingcharges", shippingcharges);
                intent.putExtra("subTotal", subtotal);
                intent.putExtra("changereturn", paymemtData.getChange_return());
                intent.putExtra("customerId", customerId);
                intent.putExtra("transaction_id", transactionId);
                intent.putExtra("locationId",locationId);
                intent.putExtra("currency", currencydata);
                intent.putExtra("selected_ordertype", orderr_type);
                intent.putExtra("tablee_id", tablee_id);
                intent.putExtra("waiterr_id", waiterr_id);

                intent.putExtra("repair_product_id", repair_product_id);
                //update edit sale.
                if (null != getIntent().getStringExtra("updateSaleDetailObject")) {
                    String strMainObject = getIntent().getStringExtra("updateSaleDetailObject");
                    intent.putExtra("updateSaleDetailObject", strMainObject);
                }

                if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                    if (!parentComingFrom.equalsIgnoreCase("") && (parentComingFrom.equalsIgnoreCase("fromListDraftFragment") || parentComingFrom.equalsIgnoreCase("fromQuatationListFragment"))) {//when comes from draft list and quotation list
                        intent.putExtra("comingFrom", "fromSearchItem");

                    } else {
                        //when come from list pos list.
                        intent.putExtra("comingFrom", comingFrom);
                    }
                }

                intent.putExtra("parentfrom", parentComingFrom);

                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
//                    finish();
            }
        });

        filterDialog.show();
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Booking Schedule");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(HomeActivity.homeActivity !=null){
                        HomeActivity.homeActivity.finish();
                    }
//                    Intent i=new Intent(BookinDateTimeActivity.this, BookingCategoryActivity.class);
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
        if(HomeActivity.homeActivity !=null){
            HomeActivity.homeActivity.finish();
        }
//        Intent i=new Intent(BookinDateTimeActivity.this, BookingCategoryActivity.class);
//        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}