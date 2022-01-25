package com.pos.salon.activity.ActivityProduct;

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
import com.pos.salon.activity.HomeActivity;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.utilConstant.AppConstant;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class ProductSection extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout lay_add_product,lay_list_products,lay_category,lay_brands;
    SharedPreferences sharedPreferences;
    public boolean isProductCreate=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_section);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setids();

        setBackNavgation();
    }
    public void setids(){
        lay_add_product = (LinearLayout) findViewById(R.id.lay_add_product);
        lay_list_products = (LinearLayout) findViewById(R.id.lay_list_products);
        lay_category = (LinearLayout) findViewById(R.id.lay_category);
        lay_brands = (LinearLayout) findViewById(R.id.lay_brands);

//        Gson gson = new Gson();
//        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {}.getType();
//
//        String strRolesPermissions = sharedPreferences.getString("rolesPermission", "");
//
//        if (strRolesPermissions != null) {
//            arrRolesPermissionList = (ArrayList<LoginPermissionsData>) gson.fromJson(strRolesPermissions, type);
//        }

//        if (!arrRolesPermissionList.isEmpty()) {
//            for (int i = 0; i < arrRolesPermissionList.size(); i++) {
//                if (arrRolesPermissionList.get(i).getPermission_name().equalsIgnoreCase("category.view")) {
//                    lay_category.setVisibility(View.INVISIBLE);
//                    break;
//                } else {
////                    lay_products.setVisibility(View.INVISIBLE);
//                }
//            }
//
//        }
//        else {
//            lay_category.setVisibility(View.VISIBLE);
//        }


        listeners();
    }
    public void listeners() {

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {

            isProductCreate = true;

        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("product.create")) {
                    isProductCreate = true;
                }

            }
        }

        lay_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isProductCreate){
                    Intent i = new Intent(ProductSection.this, AddProduct.class);
                    i.putExtra("comingFrom", "repairSection");
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(ProductSection.this,"You Don't Have Permission For this Action");
                }

            }
        });

        lay_list_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ProductSection.this, ProductList.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        lay_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductSection.this, CategoryActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            }
        });
        lay_brands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductSection.this, BrandsActivity.class);
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
                getSupportActionBar().setTitle("PRODUCTS");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(HomeActivity.homeActivity !=null){
                            HomeActivity.homeActivity.finish();
                        }
                        Intent i=new Intent(ProductSection.this, HomeActivity.class);
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
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
