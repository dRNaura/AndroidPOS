package com.pos.salon.activity.ActivityProduct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.adapter.ProductsAdapters.BrandsAdapter;
import com.pos.salon.adapter.SpinnerAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.CategoryModel;
import com.pos.salon.model.SpinnerModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BrandsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView img_add_brands,img_clear_search;
    EditText et_search_brand;
    private BrandsAdapter brandsAdapter;
    ArrayList<CategoryModel> arrBrandsList=new ArrayList<>();
    private final ArrayList<SpinnerModel>  arrDepartmentList = new ArrayList<>();
    private RecyclerView list_brands_recycler;
    private TextView txt_no_resut;
    private int deptId=0,editbrandId=0;
    private boolean isScrolling = true;
    private int currentItems, totalItems, scrollItems;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        fetchIds();
    }
    public void fetchIds(){

        img_clear_search=findViewById(R.id.img_clear_search);
        img_add_brands=findViewById(R.id.img_add_brands);
        et_search_brand=findViewById(R.id.et_search_brand);
        list_brands_recycler=findViewById(R.id.list_brands_recycler);
        txt_no_resut = findViewById(R.id.txt_no_resut);
        TextView title = findViewById(R.id.title);
        progressBar = findViewById(R.id.progressBar);

        title.setText("BRANDS LIST");

         layoutManager = new LinearLayoutManager(this);
        list_brands_recycler.setLayoutManager(layoutManager);
        brandsAdapter = new BrandsAdapter(this, arrBrandsList);
        list_brands_recycler.setAdapter(brandsAdapter);

        setBackNavgation();

        listeners();

    }
    public void listeners(){

        img_add_brands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBrandDialog("toAdd","","");
            }
        });

        et_search_brand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(text.isEmpty()){
                    arrBrandsList.clear();
                    img_clear_search.setVisibility(View.GONE);
                    brandsList();
                }else{
                    filter(text);
                    img_clear_search.setVisibility(View.VISIBLE);
                }

            }
        });
        img_clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_brand.setText("");
            }
        });

        list_brands_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollItems = layoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            brandsList();
                        }
                    }
                }
            }
        });

        brandsAdapter.setOnEditClicked(new BrandsAdapter.OnEditClicked() {
            @Override
            public void setOnEditItem(int id) {
                getEditDetail(id);
            }
        });
        brandsAdapter.setOnDeleteClicked(new BrandsAdapter.OnDeleteCLicked() {
            @Override
            public void setOnDeleteItem(int id) {
                Log.e("id", "" + id);
                deleteDetail(id);
            }
        });

        brandsList();
        getDepartmentList();
    }
    void filter(String text) {
        ArrayList<CategoryModel> temp = new ArrayList<>();
        for (CategoryModel d : arrBrandsList) {
            if (d.name.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        if (temp.isEmpty()) {
            txt_no_resut.setVisibility(View.VISIBLE);
        }else{
            txt_no_resut.setVisibility(View.GONE);
        }


        //update recyclerview
        brandsAdapter.updateList(temp);
    }
    public void deleteDetail(int brandId) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(BrandsActivity.this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Detail");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBrand(brandId);

            }
        });
        builder1.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



    public void addBrandDialog(String to,String description,String name) {
        final Dialog optionDialog = new Dialog(BrandsActivity.this);
        optionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        optionDialog.setContentView(R.layout.add_brands_dialog_items);
        optionDialog.setCancelable(true);
        Window window = optionDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        ImageView img_cancel_dialog = optionDialog.findViewById(R.id.img_cancel_dialog);
        EditText et_brand_name = optionDialog.findViewById(R.id.et_brand_name);
        EditText et_short_desc = optionDialog.findViewById(R.id.et_short_desc);

        RelativeLayout lay_dept = optionDialog.findViewById(R.id.lay_dept);

        LinearLayout linear_submit = optionDialog.findViewById(R.id.linear_submit);
        TextView txt_title = optionDialog.findViewById(R.id.txt_title);
        TextView txt_dialog_dept = optionDialog.findViewById(R.id.txt_dialog_dept);

        if(to.equalsIgnoreCase("toAdd")){
            txt_title.setText("Add Brand");
            deptId=0;
        }else{
            txt_title.setText("Edit Brand");
        }
        et_brand_name.setText(name);
        et_short_desc.setText(description);

        for(int i=0;i<arrDepartmentList.size();i++){
            if(deptId==arrDepartmentList.get(i).id){
                txt_dialog_dept.setText(arrDepartmentList.get(i).name);
                break;
            }
        }

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });

        linear_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_brand_name.getText().toString().isEmpty()){
                    AppConstant.showToast(BrandsActivity.this,"Please Enter Brand Name");
                }else{
                    optionDialog.dismiss();
                    if(to.equalsIgnoreCase("toAdd")){
                        submitBrand(et_brand_name,et_short_desc);
                    }else{
                        updateBrand(et_brand_name,et_short_desc);
                    }

                }
            }
        });
        lay_dept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeptDialog(txt_dialog_dept);
            }
        });
        optionDialog.show();

    }
    public void openDeptDialog(TextView txt_dialog_dept) {

        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.new_list_view);

        final ListView listView = dialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Department");

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        SpinnerAdapter adapter = new SpinnerAdapter(this, arrDepartmentList);
        listView.setAdapter(adapter);

        adapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {
                deptId = arrDepartmentList.get(position).id;
                txt_dialog_dept.setText(arrDepartmentList.get(position).name);
                dialog.dismiss();


            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }



    public void getDepartmentList() {
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("brands/create");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            arrDepartmentList.clear();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("brands create", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray departments = responseObject.getJSONArray("departments");
                                for (int i = 0; i < departments.length(); i++) {
                                    JSONObject object = departments.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.name = object.getString("name");
                                    model.id = object.getInt("id");
                                    arrDepartmentList.add(model);

                                }
                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(BrandsActivity.this, "brands/create list API", "(brands/create)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Load Brands list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Load Brands List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


    public void brandsList() {
        if (isScrolling) {
            arrBrandsList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("brands?limit="+arrBrandsList.size());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.body() != null) {

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("brands List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray dataObj = responseObject.getJSONArray("brands");
                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject data = dataObj.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = data.getInt("id");
                                    model.name = data.getString("name");
                                    model.description = data.getString("description");
                                    arrBrandsList.add(model);
                                }
                                if(arrBrandsList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                brandsAdapter.updateList(arrBrandsList);
//                                brandsAdapter.notifyDataSetChanged();
                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(BrandsActivity.this, "brands list API", "(brands)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Load Brands list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Load Brands List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }
    public void submitBrand(EditText et_brand_name,EditText et_short_desc) {
        AppConstant.showProgress(BrandsActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(BrandsActivity.this);
        if (retrofit != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",et_brand_name.getText().toString() );
                jsonObject.put("description", et_short_desc.getText().toString());
                jsonObject.put("department_id",deptId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.openRegister("brands", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("AddBrand", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                brandsList();
                            }
                            AppConstant.showToast(BrandsActivity.this, "" + msg);

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(BrandsActivity.this, "add brand API", "(BrandsActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Add Brand. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Add Brand. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    public void getEditDetail(int id) {
        Log.e("editID",""+id);
        AppConstant.showProgress(BrandsActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("brands/" + id + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            arrDepartmentList.clear();
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("editbrand ", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                if(responseObject.has("department_id") && !responseObject.isNull("department_id")){
                                    deptId=responseObject.getInt("department_id");
                                }
                                JSONObject dataObj = responseObject.getJSONObject("brand");
                                String description="",name="";
                                if(dataObj.has("description") && !dataObj.isNull("description")){
                                     description=dataObj.getString("description");
                                }
                                if(dataObj.has("name") && !dataObj.isNull("name")){
                                    name=dataObj.getString("name");
                                }
                                if(dataObj.has("id") && !dataObj.isNull("id")){
                                    editbrandId=dataObj.getInt("id");
                                }

                                JSONArray departments = responseObject.getJSONArray("departments");
                                for (int i = 0; i < departments.length(); i++) {
                                    JSONObject object = departments.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.name = object.getString("name");
                                    model.id = object.getInt("id");
                                    arrDepartmentList.add(model);

                                }

                                  addBrandDialog("toUpdate",description,name);

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(BrandsActivity.this, "brands/edit list API", "(brands/edit)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Load Brand . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Load Brand . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }
    public void updateBrand(EditText et_brand_name,EditText et_short_desc) {
        AppConstant.showProgress(BrandsActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(BrandsActivity.this);
        if (retrofit != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",et_brand_name.getText().toString() );
                jsonObject.put("description", et_short_desc.getText().toString());
                jsonObject.put("department_id",deptId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.add("brands/"+editbrandId, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("AddBrand", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                brandsList();
                            }
                            AppConstant.showToast(BrandsActivity.this, "" + msg);

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(BrandsActivity.this, "add brand API", "(BrandsActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Add Brand. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Add Brand. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }
    public void deleteBrand(int id) {
        Log.e("deleteID",""+id);
//        AppConstant.showProgress(CategoryActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteCategory("brands/"+id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("delete brand", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                brandsList();
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(BrandsActivity.this, "brands/destroy list API", "(brands)", "Web API Error : API Response Is Null");
                            Toast.makeText(BrandsActivity.this, "Could Not Delete Brands . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(BrandsActivity.this, "Could Not Delete Brands . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

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