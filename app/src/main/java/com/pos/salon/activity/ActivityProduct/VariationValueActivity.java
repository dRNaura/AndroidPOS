package com.pos.salon.activity.ActivityProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.AddVariableProduct.VariationValueAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.VariableModel.AllVariationModel;
import com.pos.salon.model.VariableModel.VariationValueModel;
import com.pos.salon.utilConstant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VariationValueActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recycler_variation_value;
    VariationValueAdapter variableValueAdapter;
    TextView title;
    int template_id = 0,color_id=0,position=0;
    ArrayList<VariationValueModel> arrVariationValueList = new ArrayList<>();
    ArrayList<VariationValueModel> arrUpdateCopyValueList = new ArrayList<>();
    ImageView img_add_variation_value;
    AlertDialog alertDialogDelete;
    String temp_name="",color_name="",comingTo="";
    SharedPreferences sp_cartSave;
    SharedPreferences.Editor ed_cartSave;
    RelativeLayout rlAddVar;
    VariationValueModel productVariationDataSend=new VariationValueModel();
    ArrayList<VariationValueModel> arrAllVariationValues = new ArrayList<>();
    TextView txt_no_resut;
    double defaultMargin=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variation_value);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        template_id = getIntent().getIntExtra("template_id", 0);
        temp_name = getIntent().getStringExtra("temp_name");
        color_name = getIntent().getStringExtra("color_name");
        color_id = getIntent().getIntExtra("color_id",0);
        position = getIntent().getIntExtra("position",0);
        comingTo = getIntent().getStringExtra("comingTo");
        defaultMargin = getIntent().getDoubleExtra("defaultMargin",0.0);

        findView();
        setBackNavgation();
    }

    public void findView() {

        recycler_variation_value = findViewById(R.id.recycler_variation_value);
        title = findViewById(R.id.title);
        img_add_variation_value = findViewById(R.id.img_add_variation_value);
        rlAddVar = findViewById(R.id.rlAddVar);
        txt_no_resut = findViewById(R.id.txt_no_resut);

        title.setText("VARIATION VALUES");


        listeners();

        if(!comingTo.equalsIgnoreCase("fromProductDetail")){
            getVariationValues();
        }

    }

    public void listeners() {

        img_add_variation_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariationValueModel model=new VariationValueModel();
                arrVariationValueList.add(model);
                arrUpdateCopyValueList.add(model);
                variableValueAdapter.notifyItemInserted(arrUpdateCopyValueList.size());
                AppConstant.showToast(VariationValueActivity.this,"Variation Value Field Added");
            }
        });

        rlAddVar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<AllVariationModel> productsVariation = new ArrayList<>();
                if (arrUpdateCopyValueList.size() == 0) {
                    AppConstant.showToast(VariationValueActivity.this, "Empty");
                } else {
                    for (int cartCount = 0; cartCount < arrUpdateCopyValueList.size(); cartCount++) {
                        VariationValueModel objItem = arrUpdateCopyValueList.get(cartCount);
                        AllVariationModel product = new AllVariationModel();
                        product.id = objItem.id;
                        product.name = objItem.name;
                        product.variation_sku = objItem.variation_sku;
                        product.dpp_exc_tax = objItem.dpp_exc_tax;
                        product.dpp_inc_tax = objItem.dpp_inc_tax;
                        product.var_exc_margin = objItem.var_exc_margin;
                        product.var_dsp_exc_tax = objItem.var_dsp_exc_tax;
                        productsVariation.add(product);


                    }
                    productVariationDataSend.setVarialtionValues(productsVariation);
                    productVariationDataSend.variation_template_id=template_id;
                    productVariationDataSend.color_id=color_id;
                    productVariationDataSend.position=position;

                    boolean entered=true;

                    for(int a=0; a<arrAllVariationValues.size();a++){
                        if(position==arrAllVariationValues.get(a).position){
                            arrAllVariationValues.set(position,productVariationDataSend);
                            entered=false;
                            break;
                        }
                    }
                    if(entered){
                        arrAllVariationValues.add(productVariationDataSend);
                    }

                    Gson gson = new Gson();
                    ed_cartSave.putString("myVariation", gson.toJson(arrAllVariationValues));
                    ed_cartSave.commit();



                    Gson gson3 = new Gson();
                    String json = gson3.toJson(productVariationDataSend);
                    Log.e("var Json : ", json);

                    AppConstant.showToast(VariationValueActivity.this,"Variation Added Successfully");
                    finish();
                }
            }
        });

    }

    private void setAdapterMethod() {

        variableValueAdapter = new VariationValueAdapter(this,arrVariationValueList, arrUpdateCopyValueList, temp_name, color_name, new VariationValueAdapter.ClickItem() {
            @Override
            public void onDeleteClick(int position) {
                productDeleteAlertShow(position);
            }
            @Override
            public void setOnCopyItem(int position,Map<Integer, Double> arrExc_tax, Map<Integer, Double> arrMarginValue, Map<Integer, Double> arrtotal_exc_tax) {

                AppConstant.showProgress(VariationValueActivity.this,false);
                String dpp_exc_tax="0.0",dpp_inc_tax="0.0",margin="0.0",total_exc_tax="0.0";
                for(Map.Entry m : arrExc_tax.entrySet()){
                    if(m.getKey().equals(0)){
                        dpp_exc_tax= String.valueOf(m.getValue());
                        dpp_inc_tax= String.valueOf(m.getValue());
                        break;
                    }else {
                    }
                }

                for(Map.Entry m : arrMarginValue.entrySet()){
                    if(m.getKey().equals(0)){
                        margin= String.valueOf(m.getValue());
                        break;
                    }else {
                    }
                }
                for(Map.Entry m : arrtotal_exc_tax.entrySet()){
                    if(m.getKey().equals(0)){
                        total_exc_tax= String.valueOf(m.getValue());
                        break;
                    }else {
                    }
                }

                arrUpdateCopyValueList.clear();
                for(int i=0;i<arrVariationValueList.size();i++){
                    VariationValueModel model=new VariationValueModel();
                    model.name= arrVariationValueList.get(i).name;
                    model.dpp_exc_tax= String.valueOf(dpp_exc_tax);
                    model.dpp_inc_tax= String.valueOf(dpp_inc_tax);
                    model.var_exc_margin= String.valueOf(margin);
                    model.var_dsp_exc_tax= String.valueOf(total_exc_tax);
                    model.id=arrVariationValueList.get(i).id;
//                    model.name=data.getString("name");
//                    model.created_at=data.getString("created_at");
//                    model.updated_at=data.getString("updated_at");
                    arrUpdateCopyValueList.add(model);
//                    arrAllValueList.add(model);
                }
                AppConstant.hideProgress();

//                Gson gson = new Gson();
//                ed_cartSave.putString("myVariation", gson.toJson(arrAllValueList));
//                ed_cartSave.commit();

                variableValueAdapter.notifyDataSetChanged();
            }

        });
        recycler_variation_value.hasFixedSize();
        recycler_variation_value.setAdapter(variableValueAdapter);
        recycler_variation_value.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_variation_value.setItemAnimator(new DefaultItemAnimator());

    }

    private void productDeleteAlertShow(int position) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.product_delete_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        Button buttonYes = (Button) dialogView.findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogDelete.dismiss();
                if(arrUpdateCopyValueList.size() >0){
                    arrUpdateCopyValueList.remove(position);
                }
                if(arrVariationValueList.size() > 0){
                    arrVariationValueList.remove(position);
                }

                variableValueAdapter.notifyDataSetChanged();

                if(arrUpdateCopyValueList.size()==0){
                    txt_no_resut.setVisibility(View.VISIBLE);
                }else{
                    txt_no_resut.setVisibility(View.GONE);
                }


            }
        });

        Button buttonNo = (Button) dialogView.findViewById(R.id.buttonNo);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete.dismiss();
            }
        });
        alertDialogDelete = builder.create();
        alertDialogDelete.show();
    }


    public void getVariationValues() {
        AppConstant.showProgress(VariationValueActivity.this,false);
        Retrofit retrofit = APIClient.getClientToken(VariationValueActivity.this);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("template_id", template_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (retrofit != null) {
            Log.e("ObjArray", mainObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), mainObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.detail("products/get_variation_template", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            Log.e("get_variation_template", respo);
                            JSONObject responseObject = new JSONObject(respo);
                            arrVariationValueList.clear();
                            arrUpdateCopyValueList.clear();

                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                if (responseObject.has("template") && !responseObject.isNull("template")) {
                                    JSONObject jsonObject = responseObject.getJSONObject("template");

                                    if (jsonObject.has("values") && !jsonObject.isNull("values")) {
                                        JSONArray valueArray = jsonObject.getJSONArray("values");
                                        for (int i = 0; i < valueArray.length(); i++) {
                                            JSONObject data = valueArray.getJSONObject(i);
                                            VariationValueModel model = new VariationValueModel();
                                            model.id=data.getInt("id");
                                            model.variation_template_name_id=data.getInt("variation_template_name_id");
                                            model.variation_template_id=data.getInt("variation_template_id");
                                            model.name=data.getString("name");
                                            model.created_at=data.getString("created_at");
                                            model.updated_at=data.getString("updated_at");
                                            model.var_exc_margin= String.valueOf(defaultMargin);
                                            arrVariationValueList.add(model);
                                            arrUpdateCopyValueList.add(model);

                                            }

                                        if(arrUpdateCopyValueList.size()==0){
                                            txt_no_resut.setVisibility(View.VISIBLE);
                                        }else{
                                            txt_no_resut.setVisibility(View.GONE);
                                        }

                                    }
                                    setAdapterMethod();

//                                    variableValueAdapter.notifyDataSetChanged();
                                }

                            } else {
                                AppConstant.hideProgress();
//                                Toast.makeText(AddProduct.this, "Could Not Update Product . Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(VariationValueActivity.this, "products/get_variation_template API", "(AddProductActivity)", "Web API Error : API Response Is Null");
                        }
                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
//                    Toast.makeText(AddProduct.this, "Could Not Update Product. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (!sp_cartSave.getString("myVariation", "").equalsIgnoreCase("")) {//if not cart empty.

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<VariationValueModel>>() {
            }.getType();

            String strMyCart = sp_cartSave.getString("myVariation", "");

            Log.e("strCart", strMyCart);

            arrAllVariationValues = (ArrayList<VariationValueModel>) gson.fromJson(strMyCart, type);

            if(comingTo.equalsIgnoreCase("fromProductDetail")){
                arrUpdateCopyValueList.clear();

                if(arrAllVariationValues.size() >position){
                    ArrayList<AllVariationModel> list= arrAllVariationValues.get(position).getVarialtionValues();
                    for(int i=0;i<list.size();i++){
                        VariationValueModel model=new VariationValueModel();
                        model.name= list.get(i).name;
                        model.dpp_exc_tax=list.get(i).dpp_exc_tax;
                        model.dpp_inc_tax= list.get(i).dpp_inc_tax;
                        model.var_exc_margin=list.get(i).var_exc_margin;
                        model.var_dsp_exc_tax= list.get(i).var_dsp_exc_tax;
                        model.id=list.get(i).id;
                        model.variation_sku=list.get(i).variation_sku;
//                    model.created_at=data.getString("created_at");
//                    model.updated_at=data.getString("updated_at");
                        arrUpdateCopyValueList.add(model);
//                    arrAllValueList.add(model);
                    }
                }else{
                    getVariationValues();
                }

            }





            setAdapterMethod();


        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onBackPressed();
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