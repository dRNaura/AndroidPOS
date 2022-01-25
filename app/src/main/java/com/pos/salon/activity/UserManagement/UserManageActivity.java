package com.pos.salon.activity.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.adapter.UserManageAdapter.UserListAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.UserManageModel;
import com.pos.salon.utilConstant.AppConstant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserManageActivity extends AppCompatActivity {

    ImageView img_add_user,img_clear_search;
    ProgressBar progressBar;
    private boolean isScrolling = true;
    ArrayList<UserManageModel> arrUserList = new ArrayList<>();
    TextView txt_no_resut;
    private int currentItems, totalItems, scrollItems;
    RecyclerView recycler_User;
    private LinearLayoutManager mLayoutManager;
    private Toolbar toolbar;
    UserListAdapter userListAdapter;
    @SuppressLint("StaticFieldLeak")
    public static Activity userManageActivityActivity;
    public EditText et_search_name;
   public boolean isUserCreate=false;
   public boolean isUserView=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);

        userManageActivityActivity=this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findView();
    }
    public void findView(){

        img_add_user=findViewById(R.id.img_add_user);
        progressBar=findViewById(R.id.progressBar);
        txt_no_resut=findViewById(R.id.txt_no_resut);
        recycler_User=findViewById(R.id.recycler_User);
        TextView title = findViewById(R.id.title);
        et_search_name = findViewById(R.id.et_search_name);
        img_clear_search = findViewById(R.id.img_clear_search);

        title.setText("USERS");

        mLayoutManager = new LinearLayoutManager(this);
        recycler_User.setLayoutManager(mLayoutManager);
        userListAdapter = new UserListAdapter(this, arrUserList);
        recycler_User.setAdapter(userListAdapter);

        listeners();

        setBackNavgation();
    }
    public void listeners(){

        ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String strPermission = sharedPreferences.getString("permissionDataList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();
        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (permissionsDataList.isEmpty()) {
            isUserCreate = true;
            isUserView = true;
        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("user.create")) {
                    isUserCreate = true;
                }
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("user.view")) {
                    isUserView = true;
                }

            }
        }


        img_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUserCreate){
                    Intent i=new Intent(UserManageActivity.this, AddUserManageActivity.class);
                    i.putExtra("isComing", "");
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(UserManageActivity.this,"You Don't Have Permission for this");
                }

            }
        });

        recycler_User.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    currentItems = mLayoutManager.getChildCount();
                    totalItems = mLayoutManager.getItemCount();
                    scrollItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (isScrolling) {
                        if ((currentItems + scrollItems) >= totalItems) {
                            isScrolling = false;
                            userList();
                        }
                    }
                }
            }
        });

        userListAdapter.setOnItmeClicked(new UserListAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {
                if(isUserView){
                    Intent i=new Intent(UserManageActivity.this,UserDetailViewActivity.class);
                    i.putExtra("user_id",arrUserList.get(position).id);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else{
                    AppConstant.showToast(UserManageActivity.this,"You Don't Have Permission to View Detail");
                }

            }
        });


        et_search_name.addTextChangedListener(new TextWatcher() {
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
                    arrUserList.clear();
                    img_clear_search.setVisibility(View.GONE);
                    userList();
                }else{
                    filter(text);
                    img_clear_search.setVisibility(View.VISIBLE);
                }

            }
        });

        img_clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_name.setText("");
            }
        });



//        et_search_name.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                if(s.toString().isEmpty()){
//                    img_clear_search.setVisibility(View.GONE);
//                }else{
//                    img_clear_search.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });
//        img_clear_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                hideSoftKeyboard(UserManageActivity.this);
//                et_search_name.setText("");
//                arrUserList.clear();
//                quaryText=et_search_name.getText().toString();
//                userList();
//            }
//        });
//        et_search_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if (event == null || !event.isShiftPressed()) {
//                        // the user is done typing.
//
//                        hideSoftKeyboard(UserManageActivity.this);
//
//                        arrUserList.clear();
////                    getReceiptSummary();
//                        quaryText=et_search_name.getText().toString();
//
//                        userList();
//
//                        return true; // consume.
//                    }
//
//                }
//                return false; // pass on to other listeners.
//
//            }
//        });



        userList();
    }

    void filter(String text) {
        ArrayList<UserManageModel> temp = new ArrayList<>();
        for (UserManageModel d : arrUserList) {
            if (d.full_name.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        if (temp.isEmpty()) {
            txt_no_resut.setVisibility(View.VISIBLE);
        }else{
            txt_no_resut.setVisibility(View.GONE);
        }
        //update recyclerview
        userListAdapter.updateList(temp);
    }


    public void userList() {
        if (isScrolling) {
            arrUserList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("users?limit=" + arrUserList.size());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("user List", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            if (successstatus.equalsIgnoreCase("true")) {
                                JSONArray dataObj = responseObject.getJSONArray("users");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    UserManageModel model = new UserManageModel();
                                    if(data.has("id") && !data.isNull("id")){
                                        model.id=data.getInt("id");
                                    }
                                    if(data.has("username") && !data.isNull("username")){
                                        model.username=data.getString("username");
                                    }
                                    if(data.has("full_name") && !data.isNull("full_name")){
                                        model.full_name=data.getString("full_name");
                                    }
                                    if(data.has("email") && !data.isNull("email")){
                                        model.email=data.getString("email");
                                    }
                                    arrUserList.add(model);

                                }

                                if(arrUserList.size() ==0){
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                }else {
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                userListAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(UserManageActivity.this, "users API", "(UserList Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(UserManageActivity.this, "Could Not Load User List. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        AppConstant.hideProgress();
                        Log.e("Excetion", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(UserManageActivity.this, "Could Not Load User List. Please Try Again", Toast.LENGTH_LONG).show();
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