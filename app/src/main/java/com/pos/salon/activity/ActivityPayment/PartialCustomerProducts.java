package com.pos.salon.activity.ActivityPayment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.adapter.ProductsAdapters.PartialProductsAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.BusinessDetails;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.customerData.FetchPartialCustomer;
import com.pos.salon.model.customerData.PartialCustomerList;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.utilConstant.AppConstant;
import java.lang.reflect.Type;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PartialCustomerProducts extends AppCompatActivity {
    Toolbar toolbarr;
    TextView txt_skip,txtTotalProduct1, txtTotalProductPrice1, edtDiscountTotal1, txtTotalDiscountPrice1, spnTotalDiscountType1,
            txtTotalTax1, edtShipingDetailTotal1, edtShipingChargesTotal1, txtTotalShipingCharge1, txtpaid, txtpayable;//, tv_addpos;
    RecyclerView rv_partialproducts;
    PartialProductsAdapter pda;
    ArrayList<PartialCustomerList> partialList = new ArrayList<PartialCustomerList>();
    BusinessLocationData locationData;
    CurriencyData currencyData2;
    CustomerListData customerData;
    CurriencyData currencyData=new CurriencyData();
    BusinessDetails objMyBusiness=new BusinessDetails();
    FetchPartialCustomer fp = new FetchPartialCustomer();
    Double payable=0.0;
    @SuppressLint("StaticFieldLeak")
    public static Activity partialproductsactivity;
    RelativeLayout rlAddPayment2;
    SharedPreferences sp_selectedcustomer,sharedPreferences;
    SharedPreferences.Editor ed_selectedcustomer;
    String selected_ordertype="",tablee_id="",waiterr_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_customer_products);
        toolbarr = (Toolbar) findViewById(R.id.toolbarr);
        txtTotalProduct1 = findViewById(R.id.txtTotalProduct1);
        txtTotalProductPrice1 = findViewById(R.id.txtTotalProductPrice1);
        edtDiscountTotal1 = findViewById(R.id.edtDiscountTotal1);
        txtTotalDiscountPrice1 = findViewById(R.id.txtTotalDiscountPrice1);
        txtTotalTax1 = findViewById(R.id.txtTotalTax1);
        edtShipingDetailTotal1 = findViewById(R.id.edtShipingDetailTotal1);
        edtShipingChargesTotal1 = findViewById(R.id.edtShipingChargesTotal1);
        txtTotalShipingCharge1 = findViewById(R.id.txtTotalShipingCharge1);
        spnTotalDiscountType1 = findViewById(R.id.spnTotalDiscountType1);
        txt_skip = findViewById(R.id.txt_skip);
       // tv_addpos = findViewById(R.id.tv_addpos);
        txtpaid = findViewById(R.id.txtpaid);
        txtpayable = findViewById(R.id.txtpayable);
        rv_partialproducts = findViewById(R.id.rv_partialproducts);
        rlAddPayment2 = findViewById(R.id.rlAddPayment2);
        partialproductsactivity = this;
        setSupportActionBar(toolbarr);
        setBackNavgation();

        sp_selectedcustomer = getSharedPreferences("SelectedCustomer", MODE_PRIVATE);
        ed_selectedcustomer = sp_selectedcustomer.edit();

        locationData = (BusinessLocationData) getIntent().getSerializableExtra("location");
        currencyData2 = (CurriencyData) getIntent().getSerializableExtra("currency");
        customerData = (CustomerListData) getIntent().getSerializableExtra("customer");
        selected_ordertype = getIntent().getStringExtra("selected_ordertype");
        tablee_id = getIntent().getStringExtra("tablee_id");
        waiterr_id = getIntent().getStringExtra("waiterr_id");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        ed_selectedcustomer.putString("customernamee", customerData.getName());
        ed_selectedcustomer.commit();


        String success = getIntent().getStringExtra("statuspartial");
        String customerId = getIntent().getStringExtra("partialcustomerid");

        Gson gson = new Gson();
        Type type = new TypeToken<BusinessDetails>() {}.getType();
        final String myBusinessDetail = sharedPreferences.getString("myBusinessDetail", "");

        if (myBusinessDetail != null) {
            objMyBusiness = (BusinessDetails) gson.fromJson(myBusinessDetail, type);

        }
        if (success.equals("true")) {
            if(AppConstant.isNetworkAvailable(PartialCustomerProducts.this)){
                fetch(customerId); //fetch details of sale
            }else{
                AppConstant.openInternetDialog(PartialCustomerProducts.this);
            }

        }

//        tv_addpos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(PartialCustomerProducts.this, ActivitySearchItemList.class);
//
//                intent.putExtra("location", locationData);
//                intent.putExtra("currency", currencyData);
//                intent.putExtra("customer", customerData);
//
//                startActivity(intent);
//                finish();
//            }
//        });

        rlAddPayment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Toast.makeText(PartialCustomerProducts.this, "Payment", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(PartialCustomerProducts.this, ActivityPayment.class);
                intent.putExtra("comingFrom","partialCustomer");
                intent.putExtra("transaction_id",Integer.parseInt(fp.getTransaction().getId()));
                intent.putExtra("customerId", customerData.getId());
                intent.putExtra("currency", currencyData);
                intent.putExtra("totalPayable",String.format("%.2f", payable));
                intent.putExtra("locationId",locationData.getId());
                overridePendingTransition(R.anim.enter, R.anim.exit);
                startActivity(intent);
                finish();

            }
        });

        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PartialCustomerProducts.this, ActivitySearchItemList.class);
                i.putExtra("location", locationData);
                i.putExtra("currency", currencyData2);
                i.putExtra("customer", customerData);
                i.putExtra("selected_ordertype", selected_ordertype);
                i.putExtra("tablee_id", tablee_id);
                i.putExtra("waiterr_id", waiterr_id);
                i.putExtra("comingFrom", "ActivityPosTerminalDropDown");
                overridePendingTransition(R.anim.enter, R.anim.exit);
                startActivity(i);
                finish();
            }
        });

    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("PARTIAL PRODUCTS");
            toolbarr.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

    private void fetch(final String customerid) {

        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(PartialCustomerProducts.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<FetchPartialCustomer> call = apiService.getpartialCustomer("fetchPartialSale?contact_id=" + customerid);
            call.enqueue(new Callback<FetchPartialCustomer>() {
                @Override
                public void onResponse(@NonNull Call<FetchPartialCustomer> call, @NonNull Response<FetchPartialCustomer> response) {
                    if (response.body() != null) {
                        //AppConstant.hideProgress();
                        fp = response.body();

                        for(int i=0;i<fp.getSell_details().size();i++)
                        {
                            partialList.add(fp.getSell_details().get(i));
                        }

                        if (fp.isSuccess()) {
                            if (partialList != null) {

                                rv_partialproducts.setLayoutManager(new LinearLayoutManager(PartialCustomerProducts.this, LinearLayoutManager.VERTICAL, false));
                                pda = new PartialProductsAdapter(PartialCustomerProducts.this, partialList,fp.getTransaction().getCurrency());
                                rv_partialproducts.setAdapter(pda);

                                payable = Double.parseDouble(fp.getTransaction().getFinal_total()) - Double.parseDouble(fp.getPaid_amount());

                                txtTotalProduct1.setText(String.valueOf(partialList.size()));

                                if(!objMyBusiness.getCurrency_code().equals(fp.getTransaction().getCurrency())){
                                    Double exchageRate=Double.valueOf(objMyBusiness.getExchange_rate());

                                    Double totalproPrice=Double.valueOf(fp.getTransaction().getTotal_before_tax());
                                    Double total=totalproPrice * exchageRate ;
                                    txtTotalProductPrice1.setText(fp.getTransaction().getCurrency()+" "+String.format("%.2f",total));

                                    Double totalPaid=Double.valueOf(fp.getPaid_amount());
                                    Double paid=totalPaid *exchageRate;

                                     payable=payable * exchageRate;
                                    txtpaid.setText("Paid amount: " + fp.getTransaction().getCurrency() + " " + paid);

                                    Double tax=Double.valueOf(fp.getTransaction().getTax_amount());
                                    Double taxAmount= tax * exchageRate;
                                    txtTotalTax1.setText(String.format("%.2f",taxAmount));
                                    Double excDiscount=0.0;
                                    if(fp.getTransaction().getDiscount_amount() !=null  && !fp.getTransaction().getDiscount_amount().isEmpty() && !fp.getTransaction().getDiscount_amount().equals("null") ){
                                       excDiscount =Double.valueOf(fp.getTransaction().getDiscount_amount());
                                    }
                                    Double discount=0.0;
                                    String discount1="0.0";
                                    if(fp.getTransaction().getDiscount_amount() !=null  && !fp.getTransaction().getDiscount_amount().isEmpty() && !fp.getTransaction().getDiscount_amount().equals("null") ){
                                         discount1= fp.getTransaction().getDiscount_amount();
                                    }
                                    if(fp.getTransaction().getDiscount_type() !=null  && !fp.getTransaction().getDiscount_type().isEmpty() && !fp.getTransaction().getDiscount_type().equals("null") ) {
                                        if (fp.getTransaction().getDiscount_type().equalsIgnoreCase("percentage")) {
                                            discount = total * Double.valueOf(discount1) / 100;
                                        } else {
                                            discount = excDiscount * exchageRate;
                                        }
                                    }
                                    edtDiscountTotal1.setText("Discount Amount: " + String.valueOf(discount1));
                                    txtTotalDiscountPrice1.setText(String.format("%.2f",discount));

                                }else{
                                    txtTotalTax1.setText(fp.getTransaction().getTax_amount());
                                    Double discount=0.0;
                                    if(fp.getTransaction().getDiscount_type().equalsIgnoreCase("percentage")){
                                        discount= Double.valueOf(fp.getTransaction().getFinal_total()) * Double.valueOf(fp.getTransaction().getDiscount_amount()) /100;
                                    }else{
                                        discount= Double.valueOf(fp.getTransaction().getDiscount_amount());
                                    }
                                    txtTotalDiscountPrice1.setText(String.valueOf(discount));
                                    txtTotalProductPrice1.setText(fp.getTransaction().getCurrency() + " " + String.format("%.2f",Double.valueOf(fp.getTransaction().getFinal_total())));
                                    txtpaid.setText("Paid amount: " + fp.getTransaction().getCurrency() + " " + fp.getPaid_amount());
                                    edtDiscountTotal1.setText("Discount Amount: " + fp.getTransaction().getDiscount_amount());
                               }
                                txtpayable.setText("Total Payable: " + fp.getTransaction().getCurrency() + " " +  String.format("%.2f", payable));
                                edtShipingChargesTotal1.setText("Shipping Charges: " + fp.getTransaction().getShippingcharges());
                                txtTotalShipingCharge1.setText(fp.getTransaction().getShippingcharges());
                                spnTotalDiscountType1.setText("Discount Type: " + fp.getTransaction().getDiscount_type());

                               currencyData.setId(fp.getTransaction().getCurrency());
                               currencyData.setName(fp.getTransaction().getCurrency());

                                locationData.setId(fp.getTransaction().getLocation_id());

                               /* if(fp.getTransaction().getShipping_details().equals("null")){}
                                else {
                                    edtShipingChargesTotal1.setText(fp.getTransaction().getShipping_details());
                                }*/


                            }
                        } else {

                        }
                        AppConstant.hideProgress();
                    } else {
                        AppConstant.sendEmailNotification(PartialCustomerProducts.this,"fetchPartialSale API","(PartialCustomerProducts Screen)","Web API Error : API Response Is Null");
                        AppConstant.hideProgress();
                        // Toast.makeText(PartialCustomerProducts.this, "Network error" + response.errorBody(), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<FetchPartialCustomer> call, Throwable t) {
                    AppConstant.hideProgress();
                    //Toast.makeText(PartialCustomerProducts.this, "eror" + t.toString(), Toast.LENGTH_LONG).show();
                }


            });
        }

    }

}
