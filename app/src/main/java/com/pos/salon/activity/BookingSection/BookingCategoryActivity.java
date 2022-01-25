package com.pos.salon.activity.BookingSection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivityPosTerminalDropdown;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.adapter.BookingAdapter.BookingCategoryADapter;
import com.pos.salon.adapter.ListAdapters.DraftListAdapter;
import com.pos.salon.adapter.LocationAdapter.LocationAdapter;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.listModel.ListPosModel;
import com.pos.salon.model.posLocation.BusinessLocationData;

import java.util.ArrayList;

import static com.pos.salon.activity.ActivityPosSale.ActivityPosTerminalDropdown.dropdownActivity;

public class BookingCategoryActivity extends AppCompatActivity {

    RelativeLayout rlNext;
    Toolbar toolbar;
    Spinner dropDownCustomer,dropDownCategory;
    private ArrayList<BusinessLocationData> locationList=new ArrayList<>();
    private ArrayList<BusinessLocationData> categoryList=new ArrayList<>();
    LocationAdapter locationAdapter;
    RecyclerView recyler_category;
    LinearLayoutManager mLayoutManager;
    BookingCategoryADapter bookingCategoryADapter;
    ArrayList<ContentModel> arrCategoryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_category);

        findViews();
    }
    public void findViews(){

        rlNext=findViewById(R.id.rlNext);
        dropDownCustomer=findViewById(R.id.dropDownCustomer);
        dropDownCategory=findViewById(R.id.dropDownCategory);
        recyler_category=findViewById(R.id.recyler_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listeners();

        setBackNavgation();
    }
    public void listeners(){

        BusinessLocationData locationDumy = new BusinessLocationData();
        locationDumy.setId("-1");
        locationDumy.setName("Select Customer");
        locationList.add(0, locationDumy);
        locationAdapter = new LocationAdapter(BookingCategoryActivity.this, locationList);
        dropDownCustomer.setAdapter(locationAdapter);

        BusinessLocationData categoryDumy = new BusinessLocationData();
        categoryDumy.setId("-1");
        categoryDumy.setName("Select Category");
        categoryList.add(0, categoryDumy);
        locationAdapter = new LocationAdapter(BookingCategoryActivity.this, categoryList);
        dropDownCategory.setAdapter(locationAdapter);


      GridLayoutManager mLayoutManager = new GridLayoutManager(this,3);
        recyler_category.setLayoutManager(mLayoutManager);
        bookingCategoryADapter = new BookingCategoryADapter(this, arrCategoryList);
        recyler_category.setAdapter(bookingCategoryADapter);

        bookingCategoryADapter.setOnItmeClicked(new BookingCategoryADapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                Intent intent =new Intent(BookingCategoryActivity.this,BookinDateTimeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("Service");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(HomeActivity.homeActivity !=null){
                        HomeActivity.homeActivity.finish();
                    }
                    Intent i=new Intent(BookingCategoryActivity.this, HomeActivity.class);
                    startActivity(i);
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
        Intent i=new Intent(BookingCategoryActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}