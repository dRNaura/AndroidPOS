package com.pos.salon.activity.StockTransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.pos.salon.R;

public class AddStockTransfer extends AppCompatActivity {
    private Toolbar toolbar;
    public String isComing="";
    TextView txt_save_stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_transfer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();
    }
    public void findViews() {


        txt_save_stock=findViewById(R.id.txt_save_stock);
        isComing = getIntent().getStringExtra("isComing");

        if (isComing.equalsIgnoreCase("toUpdate")) {

            txt_save_stock.setText("Update");

//            editContactDetail();
        } else {
            txt_save_stock.setText("Save");
        }

        clickListeners();
        setBackNavgation();
    }
    public void clickListeners(){


    }
    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            if (isComing.equalsIgnoreCase("toUpdate")) {
                getSupportActionBar().setTitle("EDIT STOCK TRANSFER");
            } else {
                getSupportActionBar().setTitle("ADD STOCK TRANSFER");
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    Intent i=new Intent(AddRepairActivity.this, HomeActivity.class);
//                    startActivity(i);
//                    finish();
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