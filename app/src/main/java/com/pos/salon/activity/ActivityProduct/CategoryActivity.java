package com.pos.salon.activity.ActivityProduct;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.adapter.CategorySearchAdapter;
import com.pos.salon.adapter.ProductsAdapters.AddAsSubSubAdapter;
import com.pos.salon.adapter.ProductsAdapters.CategoryAdapter;
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

public class CategoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView img_filter_category, img_add_category;
    TextView txt_no_resut,txt_as_subCateory,txt_as_sub_subCategory, title_category, txt_dialog_category, txt_dialog_sub_category, txt_dialog_sub_sub_category;
    RecyclerView list_cayegory_recycler;
    private CategoryAdapter categoryAdapter;
    private String str_category_name = "ALL", str_sub_category = "ALL", str_sub_sub_category = "ALL", strAddCatName = "", strAddShortName = "";
    private final ArrayList<CategoryModel> arrAddASSubSUbCategoryList = new ArrayList<>();
    private final ArrayList<CategoryModel> arrAddASSUbCategoryList = new ArrayList<>();
    private final ArrayList<CategoryModel> arrDropCategoryList = new ArrayList<>();
    private final ArrayList<SpinnerModel>  arrDropSubCategoryList = new ArrayList<>();
    private final ArrayList<SpinnerModel> arrDropSubSubCategoryList = new ArrayList<>();
    private final ArrayList<CategoryModel> arrCategoryList = new ArrayList<>();
    private final ArrayList<CategoryModel> arrSubCategoryList = new ArrayList<>();
    private final ArrayList<CategoryModel> arrSubSubCategoryList = new ArrayList<>();
    int cat_id = 0, sub_Cat_id = 0, sub_sub_catID = 0, strID = 0,add_as_sub_cat=0,parent_id=0,sub_parent_id=0,userBusinessId=0;
    private CategorySearchAdapter catSearchAdapter;
    private AddAsSubSubAdapter addAsSubSubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        userBusinessId= sharedPreferences.getInt("business_id",0);
        Log.e("userBusinessId",""+userBusinessId);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fetchView();
    }

    public void fetchView() {

        img_filter_category = findViewById(R.id.img_filter_category);
        img_add_category = findViewById(R.id.img_add_category);
        title_category = findViewById(R.id.title_category);
        list_cayegory_recycler = findViewById(R.id.list_cayegory_recycler);
        txt_no_resut = findViewById(R.id.txt_no_resut);

        title_category.setText("CATEGORIES LIST");

        if(userBusinessId==1){
            img_add_category.setVisibility(View.VISIBLE);
        }else{
            img_add_category.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list_cayegory_recycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(this, arrCategoryList, arrSubCategoryList, arrSubSubCategoryList,userBusinessId);
        list_cayegory_recycler.setAdapter(categoryAdapter);

        setBackNavgation();
        viewClickListeners();
    }

    public void viewClickListeners() {

        img_filter_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForFilters();
            }
        });
        img_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCategoryDialog("toAdd");
            }
        });

        categoryAdapter.setOnEditClicked(new CategoryAdapter.OnEditClicked() {
            @Override
            public void setOnEditItem(int iD) {
                Log.e("id", "" + iD);
                getEditDetail(iD);
            }
        });

        categoryAdapter.setOnDeleteClicked(new CategoryAdapter.OnDeleteCLicked() {
            @Override
            public void setOnDeleteItem(int id) {
                Log.e("id", "" + id);
                deleteDetail(id);
            }
        });

        categoriesList();
        getAddCategoriesList();
    }

    public void deleteDetail(int catid) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CategoryActivity.this);
        builder1.setMessage("Are you sure you want to Delete this?");
        builder1.setTitle("Delete Detail");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteCategory(catid);

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

    public void addCategoryDialog(String to) {
        final Dialog optionDialog = new Dialog(CategoryActivity.this);
        optionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        optionDialog.setContentView(R.layout.add_edit_category);
        optionDialog.setCancelable(true);
        Window window = optionDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        ImageView img_cancel_dialog = optionDialog.findViewById(R.id.img_cancel_dialog);
        EditText et_category_name = optionDialog.findViewById(R.id.et_category_name);
        EditText et_category_code = optionDialog.findViewById(R.id.et_category_code);
        RelativeLayout lay_as_sub_category = optionDialog.findViewById(R.id.lay_as_sub_category);
        RelativeLayout lay_as_sub_Sub_category = optionDialog.findViewById(R.id.lay_as_sub_Sub_category);
        LinearLayout linear_submit = optionDialog.findViewById(R.id.linear_submit);
        txt_as_subCateory = optionDialog.findViewById(R.id.txt_as_subCateory);
        txt_as_sub_subCategory = optionDialog.findViewById(R.id.txt_as_sub_subCateory);
        RadioGroup radioGroup = optionDialog.findViewById(R.id.radioGroup);
        RadioButton rd_as_sub = optionDialog.findViewById(R.id.rd_as_sub);
        RadioButton rd_as_sub_sub = optionDialog.findViewById(R.id.rd_as_sub_sub);
        TextView txt_title = optionDialog.findViewById(R.id.txt_title);

        if(to.equalsIgnoreCase("toAdd")){
            strAddCatName="";
            strAddShortName="";
            parent_id=0;
            sub_parent_id=0;
            txt_title.setText("Add Category");
            txt_as_subCateory.setText("");
            txt_as_sub_subCategory.setText("");
            et_category_name.setText("");
            et_category_code.setText("");
        }else{
            txt_title.setText("Edit Category");
        }

        et_category_name.setText(strAddCatName);
        et_category_code.setText(strAddShortName);
        add_as_sub_cat=0;
        parent_id=0;
        sub_parent_id=0;
        rd_as_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_as_sub_cat=1;
                lay_as_sub_category.setVisibility(View.VISIBLE);
                lay_as_sub_Sub_category.setVisibility(View.GONE);

            }
        });
        rd_as_sub_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_as_sub_cat=2;
                lay_as_sub_category.setVisibility(View.GONE);
                lay_as_sub_Sub_category.setVisibility(View.VISIBLE);

            }
        });
        lay_as_sub_Sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSelectCategoryDialog("fromSubSUbCategory");
            }
        });

        lay_as_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSelectCategoryDialog("fromSUbCategory");
            }
        });

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });
//        lay_add_category.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openAddSelectCategoryDialog(txt_add_category);
//            }
//        });
        linear_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strAddCatName=et_category_name.getText().toString();
                strAddShortName=et_category_code.getText().toString();
                if (strAddCatName.equalsIgnoreCase("")) {
                    AppConstant.showToast(CategoryActivity.this, "Please Enter Category Name");
                } else {
                    optionDialog.dismiss();
                    if (to.equalsIgnoreCase("toUpdate")) {
                        toUpdateCategory(strID);
                    } else {
                        submitCategory();
                    }

                }
            }
        });
        optionDialog.show();

    }

    public void openDialogForFilters() {

        Dialog filterDialog = new Dialog(this);
        filterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        filterDialog.setContentView(R.layout.category_filter_dialog);
        filterDialog.setCancelable(false);

        Window window = filterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        RelativeLayout lay_category = filterDialog.findViewById(R.id.lay_category);
        RelativeLayout lay_sub_category = filterDialog.findViewById(R.id.lay_sub_category);
        RelativeLayout lay_sub_sub_category = filterDialog.findViewById(R.id.lay_sub_sub_category);
        ImageView img_cancel_dialog = filterDialog.findViewById(R.id.img_cancel_dialog);
        LinearLayout linear_apply_changes = filterDialog.findViewById(R.id.linear_apply_changes);

        txt_dialog_category = filterDialog.findViewById(R.id.txt_dialog_category);
        txt_dialog_sub_category = filterDialog.findViewById(R.id.txt_dialog_sub_category);
        txt_dialog_sub_sub_category = filterDialog.findViewById(R.id.txt_dialog_sub_sub_category);


        txt_dialog_category.setText(str_category_name);
        txt_dialog_sub_category.setText(str_sub_category);
        txt_dialog_sub_sub_category.setText(str_sub_sub_category);

        lay_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCatDialog();
            }
        });


        lay_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cat_id == 0) {
                    AppConstant.showToast(CategoryActivity.this, "Please Select Category");
                } else {
                    openSubCategoryDialog();
                }

            }
        });

        lay_sub_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sub_Cat_id == 0) {
                    AppConstant.showToast(CategoryActivity.this, "Please Select Sub Category");
                } else {
                    openSubSubCategoryDialog();
                }
            }
        });

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

                categoriesList();

            }
        });

        filterDialog.show();
    }

    public void openAddSelectCategoryDialog(String from) {

        final Dialog dialog = new Dialog(CategoryActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.search_filter_list_items);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        if(from.equalsIgnoreCase("fromSUbCategory")){
            LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(CategoryActivity.this);
            recycler_category.setLayoutManager(mLayoutManager2);
            catSearchAdapter = new CategorySearchAdapter(CategoryActivity.this, arrAddASSUbCategoryList);
            recycler_category.setAdapter(catSearchAdapter);

            catSearchAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
                @Override
                public void setOnItem(ArrayList<CategoryModel> list, int position) {
                    CategoryModel model=list.get(position);
                    parent_id=model.parent_id;
                    sub_parent_id=model.sub_parent_id;
                    txt_as_subCateory.setText(model.name);
                    dialog.dismiss();
                }
            });

        }else{
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(CategoryActivity.this);
            recycler_category.setLayoutManager(mLayoutManager);
            addAsSubSubAdapter = new AddAsSubSubAdapter(CategoryActivity.this, arrAddASSubSUbCategoryList);
            recycler_category.setAdapter(addAsSubSubAdapter);

            addAsSubSubAdapter.setOnItemClicked(new AddAsSubSubAdapter.OnClicked() {
                @Override
                public void setOnItem(ArrayList<CategoryModel> list, int position) {
                    CategoryModel model=list.get(position);
                    parent_id=model.parent_id;
                    sub_parent_id=model.sub_parent_id;
                    txt_as_sub_subCategory.setText(model.name);
                    dialog.dismiss();
                }
            });
        }


        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                if(from.equalsIgnoreCase("fromSUbCategory")) {
                    filterAddSearch(s.toString());
                }else{
                    filterAddSearchSub(s.toString());
                }
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }

    public void openCatDialog() {
        final Dialog dialog = new Dialog(CategoryActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.search_filter_list_items);
        final RecyclerView recycler_category = dialog.findViewById(R.id.recycler_category);
        ImageView img_cancel_dialog = dialog.findViewById(R.id.img_cancel_dialog);
        final EditText et_search = dialog.findViewById(R.id.et_search);
//         TextView txt_search_cancel = vendorDialog.findViewById(R.id.txt_search_cancel);
        ImageView img_cancel_search = dialog.findViewById(R.id.img_cancel_search);

        Window window = dialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(CategoryActivity.this);
        recycler_category.setLayoutManager(mLayoutManager);
        catSearchAdapter = new CategorySearchAdapter(CategoryActivity.this, arrDropCategoryList);
        recycler_category.setAdapter(catSearchAdapter);

        catSearchAdapter.setOnItemClicked(new CategorySearchAdapter.OnClicked() {
            @Override
            public void setOnItem(ArrayList<CategoryModel> list, int position) {
                CategoryModel model = list.get(position);
                cat_id = model.id;
                str_category_name = model.name;
                txt_dialog_category.setText(str_category_name);
                dialog.dismiss();

                arrDropSubCategoryList.clear();
                arrDropSubSubCategoryList.clear();

                sub_Cat_id = 0;
                str_sub_category = "ALL";
                txt_dialog_sub_category.setText(str_sub_category);

                sub_sub_catID = 0;
                str_sub_sub_category = "ALL";
                txt_dialog_sub_sub_category.setText(str_sub_sub_category);
                if (cat_id == 0) {

                } else {
                    getSubcategoriesList();
                }

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        img_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


        dialog.show();

    }

    void filter(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrDropCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        catSearchAdapter.updateList(temp);

    }

    void filterAddSearch(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();

        for (CategoryModel d : arrAddASSubSUbCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.name.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        catSearchAdapter.updateList(temp);

    }

    void filterAddSearchSub(String text) {
        ArrayList<CategoryModel> temp = new ArrayList();
        for (CategoryModel d : arrAddASSubSUbCategoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (d.catName.toLowerCase().contains(text.toLowerCase())) {
//            if(d.name.contains(text)){
                temp.add(d);

            }
        }
        //update recyclerview
        addAsSubSubAdapter.updateList(temp);

    }


    public void openSubCategoryDialog() {

        final Dialog subCategoryDialog = new Dialog(this);
        subCategoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        subCategoryDialog.setContentView(R.layout.new_list_view);

        final ListView listView = subCategoryDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = subCategoryDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = subCategoryDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Sub Category");
        Window window = subCategoryDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        SpinnerAdapter adapter = new SpinnerAdapter(this, arrDropSubCategoryList);
        listView.setAdapter(adapter);

        adapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {
                sub_Cat_id = arrDropSubCategoryList.get(position).id;
                str_sub_category = arrDropSubCategoryList.get(position).name;
                txt_dialog_sub_category.setText(str_sub_category);
                subCategoryDialog.dismiss();

                sub_sub_catID = 0;
                str_sub_sub_category = "ALL";
                txt_dialog_sub_sub_category.setText(str_sub_sub_category);
                arrDropSubSubCategoryList.clear();
                if (sub_Cat_id == 0) {

                } else {
                    getSubSubcategoriesList();
                }


            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryDialog.dismiss();
            }
        });

        subCategoryDialog.show();

    }

    public void openSubSubCategoryDialog() {

        final Dialog subCategoryDialog = new Dialog(this);
        subCategoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        subCategoryDialog.setContentView(R.layout.new_list_view);

        final ListView listView = subCategoryDialog.findViewById(R.id.list_class);
        ImageView img_cancel_dialog = subCategoryDialog.findViewById(R.id.img_cancel_dialog);
        TextView dialog_title = subCategoryDialog.findViewById(R.id.dialog_title);

        dialog_title.setText("Select Sub Sub Category");

        Window window = subCategoryDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
        window.getAttributes().x = 100;
        window.getAttributes().y = 100;
        window.setGravity(Gravity.TOP);

        SpinnerAdapter projectTypeAdapter = new SpinnerAdapter(this, arrDropSubSubCategoryList);
        listView.setAdapter(projectTypeAdapter);

        projectTypeAdapter.setOnItemClicked(new SpinnerAdapter.OnClicked() {
            @Override
            public void setOnItem(int position) {

                sub_sub_catID = arrDropSubSubCategoryList.get(position).id;
                str_sub_sub_category = arrDropSubSubCategoryList.get(position).name;
                txt_dialog_sub_sub_category.setText(str_sub_sub_category);
                subCategoryDialog.dismiss();

            }
        });
        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategoryDialog.dismiss();
            }
        });

        subCategoryDialog.show();

    }

    public void categoriesList() {
//
//        if (isScrolling) {
//            arrProductList.clear();
        AppConstant.showProgress(this, false);
//        } else {
//            progressBar.setVisibility(View.VISIBLE);
//        }
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("categories?category=" + cat_id + "&sub_category=" + sub_Cat_id + "&sub_sub_category=" + sub_sub_catID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            arrCategoryList.clear();
                            arrSubCategoryList.clear();
                            arrSubSubCategoryList.clear();
                            arrDropCategoryList.clear();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("categories List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {

                                JSONArray catObj = responseObject.getJSONArray("category_list");
                                CategoryModel catmodel = new CategoryModel();
                                catmodel.id = 0;
                                catmodel.name = "ALL";
                                arrDropCategoryList.add(catmodel);

                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrDropCategoryList.add(model);

                                }
                                JSONArray dataObj = responseObject.getJSONArray("result");
                                for (int i = 0; i < dataObj.length(); i++) {
                                    JSONObject data = dataObj.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.id = data.getInt("id");
                                    model.name = data.getString("name");

                                    JSONArray sub_categoriesObj = data.getJSONArray("sub_categories");
                                    for (int a = 0; a < sub_categoriesObj.length(); a++) {
                                        JSONObject data2 = sub_categoriesObj.getJSONObject(a);
                                        CategoryModel modelSub = new CategoryModel();
                                        modelSub.sub_id = data2.getInt("sub_id");
                                        modelSub.parent_id = data2.getInt("parent_id");
                                        modelSub.sub_name = data2.getString("sub_name");

                                        JSONArray sub_subcategoriesObj = data2.getJSONArray("sub_sub_categories");
                                        for (int b = 0; b < sub_subcategoriesObj.length(); b++) {
                                            JSONObject data3 = sub_subcategoriesObj.getJSONObject(b);
                                            CategoryModel modelSubSub = new CategoryModel();
                                            modelSubSub.sub_sub_id = data3.getInt("sub_sub_id");
                                            modelSubSub.parent_id = data3.getInt("parent_id");
                                            modelSubSub.sub_sub_name = data3.getString("sub_sub_name");
                                            arrSubSubCategoryList.add(modelSubSub);
                                        }
                                        arrSubCategoryList.add(modelSub);
                                    }
                                    arrCategoryList.add(model);
                                }

                                if(arrCategoryList.size()==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else{
                                    txt_no_resut.setVisibility(View.GONE);
                                }
                                categoryAdapter.notifyDataSetChanged();
                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(CategoryActivity.this, "category list API", "(categories)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load Category list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load Category List. Please Try Again", Toast.LENGTH_LONG).show();
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

    public void getSubcategoriesList() {
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("getSubCategories?category_id=" + cat_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("subCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray catObj = responseObject.getJSONArray("sub_categories");
//                                SpinnerModel catmodel = new SpinnerModel();
//                                catmodel.id = 0;
//                                catmodel.name = "ALL";
//                                arrDropSubCategoryList.add(catmodel);
                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrDropSubCategoryList.add(model);

                                }

                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(CategoryActivity.this, "category list API", "(getSubCategories)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load SubCategory list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load SubCategory List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void getSubSubcategoriesList() {
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("getSubCategories?sub_category_id=" + sub_Cat_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("subCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray catObj = responseObject.getJSONArray("sub_sub_categories");
                                SpinnerModel catmodel = new SpinnerModel();
                                catmodel.id = 0;
                                catmodel.name = "ALL";
                                arrDropSubSubCategoryList.add(catmodel);
                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    SpinnerModel model = new SpinnerModel();
                                    model.id = object.getInt("id");
                                    model.name = object.getString("name");
                                    arrDropSubSubCategoryList.add(model);

                                }

                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(CategoryActivity.this, "category list API", "(getSubCategories)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load SubSubCategory list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load SubSubCategory List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void getAddCategoriesList() {
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("categories/create");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            arrAddASSubSUbCategoryList.clear();
                            arrAddASSUbCategoryList.clear();

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("addCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray parent_categoriesOb = responseObject.getJSONArray("parent_categories");
                                for (int i = 0; i < parent_categoriesOb.length(); i++) {
                                    JSONObject object = parent_categoriesOb.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.name = object.getString("name");
                                    model.parent_id = object.getInt("id");
                                    arrAddASSUbCategoryList.add(model);

                                }
                                JSONArray catObj = responseObject.getJSONArray("categories");
                                for (int i = 0; i < catObj.length(); i++) {
                                    JSONObject object = catObj.getJSONObject(i);
                                    CategoryModel model=new CategoryModel();
                                    model.catName = object.getString("name");
                                    if (object.has("sub_categories") && !object.isNull("sub_categories")) {
                                        JSONArray subcatObj = object.getJSONArray("sub_categories");
                                        for (int a = 0; a < subcatObj.length(); a++) {
                                            JSONObject subobject = subcatObj.getJSONObject(a);
                                            model.short_code = subobject.getString("short_code");
                                            model.name = subobject.getString("name");
                                            model.sub_parent_id = subobject.getInt("id");
                                            model.parent_id = subobject.getInt("parent_id");
                                            arrAddASSubSUbCategoryList.add(model);
                                             model=new CategoryModel();
                                        }
                                    }

                                }

                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(CategoryActivity.this, "categories/create list API", "(categories/create)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load Category list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load Category List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void submitCategory() {
        AppConstant.showProgress(CategoryActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(CategoryActivity.this);

        if (retrofit != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", strAddCatName);
                jsonObject.put("short_code", strAddShortName);
                jsonObject.put("add_as_sub_cat", add_as_sub_cat);
                jsonObject.put("parent_id", parent_id);
                jsonObject.put("sub_parent_id", sub_parent_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.openRegister("categories", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            Log.e("AddCategory", respo.toString());
                            JSONObject responseObject = new JSONObject(respo);
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                categoriesList();
                            }
                            AppConstant.showToast(CategoryActivity.this, "" + msg);

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(CategoryActivity.this, "add categories API", "(CategoryActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Add Category. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Add Category. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    public void getEditDetail(int iD) {
        Log.e("editID",""+iD);
        AppConstant.showProgress(CategoryActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("categories/" + iD + "/edit");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            arrAddASSubSUbCategoryList.clear();
                            arrAddASSUbCategoryList.clear();
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("addCat List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONObject catObj = responseObject.getJSONObject("category");

                                strID = catObj.getInt("id");
                                strAddCatName = catObj.getString("name");
                                strAddShortName = catObj.getString("short_code");


                                JSONArray parent_categoriesOb = responseObject.getJSONArray("parent_categories");
                                for (int i = 0; i < parent_categoriesOb.length(); i++) {
                                    JSONObject object = parent_categoriesOb.getJSONObject(i);
                                    CategoryModel model = new CategoryModel();
                                    model.name = object.getString("name");
                                    model.parent_id = object.getInt("id");
                                    arrAddASSUbCategoryList.add(model);

                                }
                                JSONArray arrcatObj = responseObject.getJSONArray("categories");
                                for (int i = 0; i < arrcatObj.length(); i++) {
                                    JSONObject object = arrcatObj.getJSONObject(i);
                                    if (object.has("sub_categories") && !object.isNull("sub_categories")) {
                                        JSONArray subcatObj = object.getJSONArray("sub_categories");
                                        for (int a = 0; a < subcatObj.length(); a++) {
                                            JSONObject subobject = subcatObj.getJSONObject(a);
                                            CategoryModel model = new CategoryModel();
                                            model.short_code = subobject.getString("short_code");
                                            model.name = subobject.getString("name");
                                            model.sub_parent_id = subobject.getInt("id");
                                            model.parent_id = subobject.getInt("parent_id");
                                            arrAddASSubSUbCategoryList.add(model);
                                        }
                                    }

                                }

                                addCategoryDialog("toUpdate");

                            }

                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(CategoryActivity.this, "categories/edit list API", "(categories/edit)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load Category . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load Category . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void toUpdateCategory(int id) {
        Log.e("updateId",""+id);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", strAddCatName);
                jsonObject.put("short_code", strAddShortName);
                jsonObject.put("add_as_sub_cat", add_as_sub_cat);
                jsonObject.put("parent_id", parent_id);
                jsonObject.put("sub_parent_id", sub_parent_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.add("categories/"+id, body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("update", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {

                                categoriesList();

                            }

                        } else {
                            AppConstant.hideProgress();

//                            AppConstant.sendEmailNotification(CategoryActivity.this, "categories/edit list API", "(categories/edit)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Load Category . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Load Category . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }
    public void deleteCategory(int iD) {
        Log.e("deleteID",""+iD);
//        AppConstant.showProgress(CategoryActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.deleteCategory("categories/"+iD);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {

                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("delete res", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                cat_id=0;
                                sub_Cat_id=0;
                                sub_Cat_id=0;
                                categoriesList();
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(CategoryActivity.this, "categories/destroy list API", "(categories)", "Web API Error : API Response Is Null");
                            Toast.makeText(CategoryActivity.this, "Could Not Delete Category . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(CategoryActivity.this, "Could Not Delete Category . Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }



}