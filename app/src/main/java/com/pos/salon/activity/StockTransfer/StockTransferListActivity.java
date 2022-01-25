package com.pos.salon.activity.StockTransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pos.salon.R;
import com.pos.salon.adapter.StockTransferAdapter.StockTransferListAdapter;
import com.pos.salon.model.UserManageModel;
import java.util.ArrayList;

public class StockTransferListActivity extends AppCompatActivity {

    ImageView img_add_stock, img_clear_search;
    ProgressBar progressBar;
    private boolean isScrolling = true;
    ArrayList<UserManageModel> arrUserList = new ArrayList<>();
    TextView txt_no_resut;
    private int currentItems, totalItems, scrollItems;
    private String quaryText = "";
    RecyclerView recycler_stock;
    private LinearLayoutManager mLayoutManager;
    private Toolbar toolbar;
    StockTransferListAdapter stockTransferListAdapter;
    public EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        findView();
    }

    public void findView() {

        recycler_stock=findViewById(R.id.recycler_stock);
        img_add_stock=findViewById(R.id.img_add_stock);


        mLayoutManager = new LinearLayoutManager(this);
        recycler_stock.setLayoutManager(mLayoutManager);
        stockTransferListAdapter = new StockTransferListAdapter(this, arrUserList);
        recycler_stock.setAdapter(stockTransferListAdapter);

        listeners();

        setBackNavgation();

    }

    public void listeners() {

        img_add_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StockTransferListActivity.this, AddStockTransfer.class);
                i.putExtra("isComing", "");
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
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                    Intent i = new Intent(RepairListActivity.this, RepairSection.class);
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