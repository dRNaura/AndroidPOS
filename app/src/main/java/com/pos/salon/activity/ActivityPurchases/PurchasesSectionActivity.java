package com.pos.salon.activity.ActivityPurchases;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.AddPurchaseActivity;
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class PurchasesSectionActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout lay_add_purchases,lay_list_purchases;
    SharedPreferences sharedPreferences;
    ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
    public boolean isPurchaseCreate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_section);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setids();

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {}.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        setBackNavgation();
    }

    public void setids(){
        lay_add_purchases = (LinearLayout) findViewById(R.id.lay_add_purchases);
        lay_list_purchases = (LinearLayout) findViewById(R.id.lay_list_purchases);

        if (permissionsDataList.isEmpty()) {

            isPurchaseCreate = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {

                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("purchase.create")) {
                    isPurchaseCreate = true;
                }
            }
        }

        listeners();
    }


    public void listeners() {

        lay_add_purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPurchaseCreate){
                    Intent i = new Intent(PurchasesSectionActivity.this, AddPurchaseActivity.class);
                    i.putExtra("comingFrom","purchaseSection");
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(PurchasesSectionActivity.this,"You Don't Have Permission For this Action");
                }

            }
        });

        lay_list_purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PurchasesSectionActivity.this, PurchasesListActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("PURCHASES");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(HomeActivity.homeActivity !=null){
                        HomeActivity.homeActivity.finish();
                    }
                    Intent i=new Intent(PurchasesSectionActivity.this, HomeActivity.class);
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
        Intent i=new Intent(PurchasesSectionActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
