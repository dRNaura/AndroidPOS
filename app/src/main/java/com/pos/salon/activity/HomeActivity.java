package com.pos.salon.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivityPosTerminalDropdown;
import com.pos.salon.activity.ActivityProduct.ProductSection;
import com.pos.salon.activity.ActivityPurchases.PurchasesSectionActivity;
import com.pos.salon.activity.BookingSection.BookingCategoryActivity;
import com.pos.salon.activity.SupplierActivity.SupplierSection;
import com.pos.salon.activity.UserManagement.UserManageActivity;
import com.pos.salon.adapter.ContentAdapter;
import com.pos.salon.adapter.NotificationAdapter;
import com.pos.salon.client.APIClient;
import com.pos.salon.fragment.BookingFragment.BookingDetailFragment;
import com.pos.salon.fragment.BookingFragment.BookingEditFragment;
import com.pos.salon.fragment.BookingFragment.BookingListFragment;
import com.pos.salon.fragment.BookingFragment.BookingSectionFragment;
import com.pos.salon.fragment.BookingFragment.CalenderViewFragment;
import com.pos.salon.fragment.CustomerListFragment;
import com.pos.salon.fragment.CustomerViewDetail;
import com.pos.salon.fragment.DeliveryOrderFragments.ShippingListFragment;
import com.pos.salon.fragment.FindInvoiceFragment;
import com.pos.salon.fragment.ListDraftFragment;
import com.pos.salon.fragment.ListPosFragment;
import com.pos.salon.fragment.QuatationListFragment;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleDetailFragment;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleFragment;
import com.pos.salon.fragment.ReturnFragments.ReturnSaleListFragment;
import com.pos.salon.fragment.SaleDetailFragment;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.ContentModel;
import com.pos.salon.model.LoginPermissionsData;
import com.pos.salon.model.NotificationModel;
import com.pos.salon.model.PosSettingDefault;
import com.pos.salon.model.login.LoginParse;
import com.pos.salon.model.logout.LogoutResponse;
import com.pos.salon.model.posLocation.CountryData;
import com.pos.salon.utilConstant.AppConstant;
import com.wonderpush.sdk.WonderPush;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.wonderpush.sdk.WonderPush.getDeviceId;
import static com.wonderpush.sdk.WonderPush.getInstallationId;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgMenu, imgBack, img_notification, img_profit_loss;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_close_register;
    Dialog openRegisterDialog, notificationDialog;
    EditText et_register_amount;
    private DrawerLayout drawer;
    @SuppressLint("StaticFieldLeak")
    public static TextView tool_title;
    ArrayList<CountryData> countryList;
    ArrayList<LoginPermissionsData> permissionsDataList = new ArrayList<LoginPermissionsData>();
    ArrayList<LoginPermissionsData> arrRolesPermissionList = new ArrayList<LoginPermissionsData>();
    ArrayList<String> enablesModulesList = new ArrayList<String>();
    public boolean isCustomerCreate = false;
    public boolean isSaleCreate = false;
    public static boolean isRegisterOpen = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static int MY_PERMISSIONS_REQUESTS = 1;
    private final ArrayList<NotificationModel> arrNotificationList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static TextView txt_notification_count;
    private boolean isScrolling = true;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    LinearLayout content;
    int currentItems, totalItems, scrollItems, page_number = 0;
    TextView txt_no_resut;
    NotificationAdapter notificationAdapter;
    String user_id = "0";
    int login_business_id = 0;
    ArrayList<ContentModel> arrDashList = new ArrayList<>();
    RecyclerView recycler_content;
    ContentAdapter contentAdapter;
    public static int CAMERA_REQUEST = 123;
    public static int MY_CAMERA_PERMISSION_CODE = 123;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @SuppressLint("StaticFieldLeak")
    public static Activity homeActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeActivity = this;

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, MY_CAMERA_PERMISSION_CODE);
//                }
        }


        String udid = Settings.Secure.getString(HomeActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("android_id", udid);

        ContentModel addModel = new ContentModel();
        addModel.dashImage = R.mipmap.add_user_new;
        addModel.dashText = "ADD CUSTOMER";
        arrDashList.add(addModel);

        ContentModel listModel = new ContentModel();
        listModel.dashImage = R.mipmap.list_customer_new;
        listModel.dashText = "LIST CUSTOMER";
        arrDashList.add(listModel);

        ContentModel posModel = new ContentModel();
        posModel.dashImage = R.drawable.pos_sale_active;
        posModel.dashText = "POS SALE";
        arrDashList.add(posModel);



        ContentModel findMOdel = new ContentModel();
        findMOdel.dashImage = R.mipmap.find;
        findMOdel.dashText = "FIND INVOICE";
        arrDashList.add(findMOdel);

        ContentModel listposModel = new ContentModel();
        listposModel.dashImage = R.mipmap.new_draft;
        listposModel.dashText = "LIST POS";
        arrDashList.add(listposModel);

        ContentModel returnModel = new ContentModel();
        returnModel.dashImage = R.mipmap.return_new;
        returnModel.dashText = "RETURN SALE";
        arrDashList.add(returnModel);

        ContentModel drafModel = new ContentModel();
        drafModel.dashImage = R.mipmap.new_draft;
        drafModel.dashText = "LIST DRAFTS";
        arrDashList.add(drafModel);

        ContentModel quotationModel = new ContentModel();
        quotationModel.dashImage = R.mipmap.new_draft;
        quotationModel.dashText = "QUOTATIONS";
        arrDashList.add(quotationModel);

//        ContentModel stockModel = new ContentModel();
//        stockModel.dashImage = R.drawable.list_pos_deactive;
//        stockModel.dashText = "Stock Transfers";
//        arrDashList.add(stockModel);


        HomeActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        login_business_id = sharedPreferences.getInt("business_id", 0);
        Log.e("business", "" + login_business_id);

        //check for departments
        ArrayList<String> arrdepartmentsList = new ArrayList<String>();
        Gson gsonenables = new Gson();
        Type typeDept = new TypeToken<ArrayList<String>>() {
        }.getType();
        String strdepartmentrs = sharedPreferences.getString("departments", "");

        if (strdepartmentrs != null) {
            arrdepartmentsList = (ArrayList<String>) gsonenables.fromJson(strdepartmentrs, typeDept);
            if (arrdepartmentsList.contains("4")) {
                ContentModel bookingMOdel = new ContentModel();
                bookingMOdel.dashImage = R.mipmap.booking;
                bookingMOdel.dashText = "BOOKINGS";
                arrDashList.add(bookingMOdel);
            }
        }


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Read whether the user clicked the notification (true) or if it was automatically opened (false)
                boolean fromUserInteraction = intent.getBooleanExtra(WonderPush.INTENT_NOTIFICATION_OPENED_EXTRA_FROM_USER_INTERACTION, true);
                // Get the original push notification received intent
                Intent pushNotif = intent.getParcelableExtra(WonderPush.INTENT_NOTIFICATION_OPENED_EXTRA_RECEIVED_PUSH_NOTIFICATION);
                if (pushNotif != null) {

                    try {
                        Log.e("p", pushNotif.toString());
                        // Perform desired action, like reading custom key-value payload
                        int transaction_id = 0, business_id = 0;
                        if (pushNotif.getStringExtra("transaction_id") != null) {
                            transaction_id = Integer.parseInt(pushNotif.getStringExtra("transaction_id"));
                        }
                        if (pushNotif.getStringExtra("business_id") != null) {
                            business_id = Integer.parseInt(pushNotif.getStringExtra("business_id"));
                        }

                        if (transaction_id == 0) {
//                            AppConstant.showToast(HomeActivity.this,"No Matching Transaction ID Found");
                        } else {
                            SaleDetailFragment ldf = new SaleDetailFragment();
                            Bundle args = new Bundle();
                            args.putString("transactionId", String.valueOf(transaction_id));
                            args.putString("from", "PushNotification");
                            args.putString("subFrom", "fromPshNotification");
                            args.putInt("return_exists", 0);
                            ldf.setArguments(args);
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content_main, ldf);
                            transaction.addToBackStack(null);
                            transaction.commitAllowingStateLoss();

                            if (ShortcutBadger.isBadgeCounterSupported(HomeActivity.this)) {
                                ShortcutBadger.removeCount(HomeActivity.this); //for 1.1.4+
                            }
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());

                    }


                }
            }
        }, new IntentFilter(WonderPush.INTENT_NOTIFICATION_OPENED));

        WonderPush.initialize(this);


        //this method is to add device info in wonderpush server
        addDeviceToServer();

        setView();
    }

    private void setView() {

        tool_title = (TextView) findViewById(R.id.tool_title);
        img_close_register = findViewById(R.id.img_close_register);
        content = findViewById(R.id.content);
        img_notification = findViewById(R.id.img_notification);
        txt_notification_count = findViewById(R.id.txt_notification_count);
        img_profit_loss = findViewById(R.id.img_profit_loss);
        recycler_content = findViewById(R.id.recycler_content);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recycler_content.setLayoutManager(gridLayoutManager);
        contentAdapter = new ContentAdapter(this, arrDashList);
        recycler_content.setAdapter(contentAdapter);


        Date currentTime = Calendar.getInstance().getTime();
        Log.e("currentTime", String.valueOf(currentTime));
        Gson gson = new Gson();


        Type type = new TypeToken<ArrayList<LoginPermissionsData>>() {
        }.getType();

        Gson gsonenables = new Gson();
        Type typeEnable = new TypeToken<ArrayList<String>>() {
        }.getType();

        String strPermission = sharedPreferences.getString("permissionDataList", "");
        String strEnablesModules = sharedPreferences.getString("enableList", "");
        String strRolesPermissions = sharedPreferences.getString("rolesPermission", "");


        if (strEnablesModules != null) {
            enablesModulesList = (ArrayList<String>) gsonenables.fromJson(strEnablesModules, typeEnable);
        }

        if (strPermission != null) {
            permissionsDataList = (ArrayList<LoginPermissionsData>) gson.fromJson(strPermission, type);
        }

        if (strRolesPermissions != null) {
            arrRolesPermissionList = (ArrayList<LoginPermissionsData>) gson.fromJson(strRolesPermissions, type);
        }

        // to show repair section

        // to show delivery section
        if (!enablesModulesList.isEmpty()) {
            boolean isenable = false;
            for (int i = 0; i < enablesModulesList.size(); i++) {
                if (enablesModulesList.get(i).equalsIgnoreCase("shopplus_delivery")) {
                    isenable = true;
//                    lay_delivery_orders.setVisibility(View.INVISIBLE);
                    break;
                }
            }
            if (isenable == false) {
                ContentModel deliveryModel = new ContentModel();
                deliveryModel.dashImage = R.drawable.list_pos_deactive;
                deliveryModel.dashText = "DELIVERY ORDERS";
                arrDashList.add(deliveryModel);
                contentAdapter.notifyDataSetChanged();
//                    lay_delivery_orders.setVisibility(View.VISIBLE);
            }
//        }
        }

        // to show products section

        if (!arrRolesPermissionList.isEmpty()) {
            for (int i = 0; i < arrRolesPermissionList.size(); i++) {
                if (arrRolesPermissionList.get(i).getPermission_name().equalsIgnoreCase("product.view")) {
//                    lay_products.setVisibility(View.VISIBLE);
                    ContentModel deliveryModel = new ContentModel();
                    deliveryModel.dashImage = R.mipmap.products_new;
                    deliveryModel.dashText = "PRODUCTS";
                    arrDashList.add(deliveryModel);
                    contentAdapter.notifyDataSetChanged();
                    break;
                } else {
//                    lay_products.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            ContentModel deliveryModel = new ContentModel();
            deliveryModel.dashImage = R.mipmap.products_new;
            deliveryModel.dashText = "PRODUCTS";
            arrDashList.add(deliveryModel);
            contentAdapter.notifyDataSetChanged();
//            lay_products.setVisibility(View.VISIBLE);
        }

        // to show Supplier section

        if (!arrRolesPermissionList.isEmpty()) {
            for (int i = 0; i < arrRolesPermissionList.size(); i++) {
                if (arrRolesPermissionList.get(i).getPermission_name().equalsIgnoreCase("supplier.view")) {
//                    lay_products.setVisibility(View.VISIBLE);
                    ContentModel deliveryModel = new ContentModel();
                    deliveryModel.dashImage = R.mipmap.supplier_new;
                    deliveryModel.dashText = "SUPPLIER";
                    arrDashList.add(deliveryModel);
                    contentAdapter.notifyDataSetChanged();
                    break;
                } else {
//                    lay_products.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            ContentModel deliveryModel = new ContentModel();
            deliveryModel.dashImage = R.mipmap.supplier_new;
            deliveryModel.dashText = "SUPPLIER";
            arrDashList.add(deliveryModel);
            contentAdapter.notifyDataSetChanged();
//            lay_products.setVisibility(View.VISIBLE);
        }


        // show purchase section
//hide for now
        if (!arrRolesPermissionList.isEmpty()) {
            for (int i = 0; i < arrRolesPermissionList.size(); i++) {
                if (arrRolesPermissionList.get(i).getPermission_name().equalsIgnoreCase("purchase.view")) {
//                    lay_products.setVisibility(View.VISIBLE);
                    ContentModel repairModel = new ContentModel();
                    repairModel.dashImage = R.mipmap.purchases_new;
                    repairModel.dashText = "PURCHASES";
                    arrDashList.add(repairModel);
                    contentAdapter.notifyDataSetChanged();
                    break;
                } else {
//                    lay_products.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            ContentModel repairModel = new ContentModel();
            repairModel.dashImage = R.mipmap.purchases_new;
            repairModel.dashText = "PURCHASES";
            arrDashList.add(repairModel);
            contentAdapter.notifyDataSetChanged();
//            lay_products.setVisibility(View.VISIBLE);
        }
//ends

//        if (!enablesModulesList.isEmpty()) {
//            for (int i = 0; i < enablesModulesList.size(); i++) {
//                if (enablesModulesList.get(i).equalsIgnoreCase("purchase.view")) {
////                    lay_repairs.setVisibility(View.VISIBLE);
//                    ContentModel repairModel = new ContentModel();
//                    repairModel.dashImage = R.drawable.list_pos_deactive;
//                    repairModel.dashText = "PURCHASES";
//                    arrDashList.add(repairModel);
//                    contentAdapter.notifyDataSetChanged();
//
//                    break;
//                }
//            }
//        } else {
////            lay_repairs.setVisibility(View.INVISIBLE);
//        }

        // to show user section

        if (!arrRolesPermissionList.isEmpty()) {
            for (int i = 0; i < arrRolesPermissionList.size(); i++) {
                if (arrRolesPermissionList.get(i).getPermission_name().equalsIgnoreCase("user.view")) {
//                    lay_products.setVisibility(View.VISIBLE);
                    ContentModel userMangeModel = new ContentModel();
                    userMangeModel.dashImage = R.mipmap.manage_user_new;
                    userMangeModel.dashText = "MANAGE USER";
                    arrDashList.add(userMangeModel);
                    contentAdapter.notifyDataSetChanged();
                    break;
                } else {
//                    lay_products.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            ContentModel userMangeModel = new ContentModel();
            userMangeModel.dashImage = R.mipmap.manage_user_new;
            userMangeModel.dashText = "MANAGE USER";
            arrDashList.add(userMangeModel);
            contentAdapter.notifyDataSetChanged();
//            lay_products.setVisibility(View.VISIBLE);
        }


        if (permissionsDataList.isEmpty()) {
            isCustomerCreate = true;
            isSaleCreate = true;


        } else {

            for (int i = 0; i < permissionsDataList.size(); i++) {
                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("customer.create")) {
                    isCustomerCreate = true;
                }

                if (permissionsDataList.get(i).getPermission_name().equalsIgnoreCase("sell.create")) {
                    isSaleCreate = true;
                }

            }
        }


        imgMenu = (ImageView) findViewById(R.id.imgMenu);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawer,
                        R.string.open, R.string.close) {

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        float slideX = drawerView.getWidth() * slideOffset;
                        content.setTranslationX(slideX);
                    }
                };
                drawer.addDrawerListener(actionBarDrawerToggle);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                ShowFragment(id);
                return true;
            }
        });

        View headerview = navigationView.getHeaderView(0);
        imgBack = headerview.findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        img_close_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, CloseRegister.class);
                intent.putExtra("comingFrom", "");
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        img_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notificationDialog();

            }
        });

        img_profit_loss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, TodayProfitActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        contentAdapter.setOnItmeClicked(new ContentAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position, String fromLayout) {
                switch (fromLayout) {
                    case "POS SALE":

                        if (isSaleCreate == true) {

                            if (isRegisterOpen == true) {

                                startActivity(new Intent(HomeActivity.this, ActivityPosTerminalDropdown.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                openRegisterDialog();
                            }
                        } else {
                            AppConstant.showToast(HomeActivity.this, "You Don't Have Permission To Create New Sale");
                        }

                        break;
                    case "LIST POS":

                        ShowFragment(R.id.nav_listpos);

                        break;

                    case "RETURN SALE":

                        ShowFragment(R.id.nav_returnsale);
                        break;

                    case "QUOTATIONS":

                        ShowFragment(R.id.nav_quotations);
                        break;

                    case "LIST DRAFTS":

                        ShowFragment(R.id.nav_drafts);
                        break;

                    case "LIST CUSTOMER":

                        ShowFragment(R.id.nav_listcustomer);
                        break;

                        case "BOOKINGS":

                            openFragment(new BookingSectionFragment());
//                            Intent booking = new Intent(HomeActivity.this, BookingCategoryActivity.class);
//                            startActivity(booking);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;


                    case "PRODUCTS":
                        Intent product = new Intent(HomeActivity.this, ProductSection.class);

                        startActivity(product);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;


                    case "ADD CUSTOMER":

                        if (isCustomerCreate == true) {
                            Intent i1 = new Intent(HomeActivity.this, AddCustomerActivity.class);
                            i1.putExtra("country_list", sharedPreferences.getString("country_list", ""));
                            i1.putExtra("isComing", "toAdd");

                            startActivity(i1);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            AppConstant.showToast(HomeActivity.this, "You Don't Have Permission To Add Customer");
                        }
                        break;

                    case "DELIVERY ORDERS":

                        openFragment(new ShippingListFragment());

                        break;

                    case "PURCHASES":

                        Intent purchases = new Intent(HomeActivity.this, PurchasesSectionActivity.class);

                        startActivity(purchases);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        break;

                    case "SUPPLIER":

                        Intent supplier = new Intent(HomeActivity.this, SupplierSection.class);

                        startActivity(supplier);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        break;

                    case "MANAGE USER":

                        Intent userManagement = new Intent(HomeActivity.this, UserManageActivity.class);
                        startActivity(userManagement);
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                        break;

                    case "FIND INVOICE":

                        ShowFragment(R.id.nav_findInvoice);

                        break;

                }


            }
        });

        checkRegisterOpen();

        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawer,
                R.string.open, R.string.close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


    }


    @Override
    public void onResume() {
        super.onResume();


        if (AppConstant.isNetworkAvailable(HomeActivity.this)) {
            commonDataApi();
            checkNotificationCount();

        } else {
            AppConstant.openInternetDialog(HomeActivity.this);
        }
    }

    private void ShowFragment(int itemId) {


        switch (itemId) {


            case R.id.nav_addcustomer:

                if (isCustomerCreate == true) {
                    Intent i = new Intent(HomeActivity.this, AddCustomerActivity.class);
                    i.putExtra("country_list", sharedPreferences.getString("country_list", ""));
                    i.putExtra("isComing", "toAdd");
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    AppConstant.showToast(this, "You Don't Have Permission To Add Customer");
                }

                break;
            case R.id.nav_listcustomer:

                openFragment(new CustomerListFragment());

                break;
            case R.id.nav_possale:

                if (isSaleCreate == true) {

                    if (isRegisterOpen == true) {
                        startActivity(new Intent(this, ActivityPosTerminalDropdown.class));
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        openRegisterDialog();
                    }
                } else {
                    AppConstant.showToast(this, "You Don't Have Permission To Create New Sale");
                }
                break;

            case R.id.nav_returnsale:
                openFragment(new ReturnSaleListFragment());
                break;

            case R.id.nav_listpos:

                openFragment(new ListPosFragment());

                break;

            case R.id.nav_findInvoice:
                openFragment(new FindInvoiceFragment());
                break;

            case R.id.nav_drafts:
                openFragment(new ListDraftFragment());
                break;

            case R.id.nav_quotations:

                openFragment(new QuatationListFragment());

                break;

            case R.id.nav_logout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                builder1.setMessage("Are you sure you want to logout?");
                builder1.setTitle("Logout");
                builder1.setCancelable(true);

                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (AppConstant.isNetworkAvailable(HomeActivity.this)) {
                            logOutAPI();
                        } else {
                            AppConstant.openInternetDialog(HomeActivity.this);
                        }

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

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {

    }


    public void logOutAPI() {
        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(HomeActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<LogoutResponse> call = apiService.userLogout("logout?device_id=" + getDeviceId() + "&business_id=" + login_business_id);
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(@NonNull Call<LogoutResponse> call, @NonNull Response<LogoutResponse> response) {
                    if (response.body() != null) {
                        AppConstant.hideProgress();
                        LogoutResponse logoutData = response.body();
                        if (logoutData.getStatus().equalsIgnoreCase("200")) {

                            getApplicationContext().getSharedPreferences("login", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("CountProduct", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("myCartPreference", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("MODIFIERNAME", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("SAVEMODIFIERS", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("SelectedCustomer", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("defaultTax", MODE_PRIVATE).edit().clear().commit();
                            getApplicationContext().getSharedPreferences("mycart", MODE_PRIVATE).edit().clear().commit();


                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

                        Toast.makeText(HomeActivity.this, logoutData.getMessage(), Toast.LENGTH_LONG).show();

                    } else {

                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(HomeActivity.this, "logout API", "(HomeActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(HomeActivity.this, "Could Not Logout. Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(HomeActivity.this, "Could Not Logout. Please Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        } else {
            super.onBackPressed();
        }

        checkNavigation();
    }

    public void checkNavigation() {

        if (getVisibleFragment() instanceof ListPosFragment) {
            tool_title.setText("LIST POS SALE");
        } else if (getVisibleFragment() instanceof FindInvoiceFragment) {
            tool_title.setText("FIND INVOICE");
        } else if (getVisibleFragment() instanceof SaleDetailFragment) {
            tool_title.setText("SALE DETAIL");
        } else if (getVisibleFragment() instanceof ReturnSaleListFragment) {
            tool_title.setText("RETURN SALE LIST");
        } else if (getVisibleFragment() instanceof QuatationListFragment) {
            tool_title.setText("LIST QUATATION");
        } else if (getVisibleFragment() instanceof ListDraftFragment) {
            tool_title.setText("LIST DRAFTS");
        } else if (getVisibleFragment() instanceof CustomerListFragment) {
            tool_title.setText("CUSTOMER LIST");
        } else if (getVisibleFragment() instanceof CustomerViewDetail) {
            tool_title.setText("VIEW CONTACT");
        } else if (getVisibleFragment() instanceof SaleDetailFragment) {
            tool_title.setText("SALE DEATIL");
        } else if (getVisibleFragment() instanceof ReturnSaleDetailFragment) {
            tool_title.setText("RETURN SALE DETAIL");
        } else if (getVisibleFragment() instanceof ReturnSaleFragment) {
            tool_title.setText("RETURN SALE");
        } else if (getVisibleFragment() instanceof ShippingListFragment) {
            tool_title.setText("DELIVERY ORDERS");
        } else if (getVisibleFragment() instanceof BookingListFragment) {
            tool_title.setText("BOOKING LIST");
        } else if (getVisibleFragment() instanceof BookingDetailFragment) {
            tool_title.setText("BOOKING DETAIL");
        } else if (getVisibleFragment() instanceof BookingSectionFragment) {
            tool_title.setText("BOOKING");
        }else if (getVisibleFragment() instanceof CalenderViewFragment) {
            tool_title.setText("BOOKING CALENDAR");
        } else if (getVisibleFragment() instanceof BookingEditFragment) {
            tool_title.setText("EDIT BOOKING");
        }
        else {

            tool_title.setText("DASHBOARD");
            FragmentManager fm = this.getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Collections.reverse(fragments);
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.content_main, fragment,"home");
        transaction.addToBackStack("home");
        transaction.commit();

    }


    public void notificationDialog() {

        notificationDialog = new Dialog(HomeActivity.this);
        notificationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        notificationDialog.setContentView(R.layout.activity_notification);

        Window window = notificationDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        RecyclerView recycler_notification;

        SharedPreferences sharedPreferences;

        recycler_notification = notificationDialog.findViewById(R.id.recycler_notification);
        progressBar = notificationDialog.findViewById(R.id.progressBar);
        txt_no_resut = notificationDialog.findViewById(R.id.txt_no_resut);
        ImageView img_cancel_dialog = notificationDialog.findViewById(R.id.img_cancel_dialog);

        getNotificationList();

        if (ShortcutBadger.isBadgeCounterSupported(HomeActivity.this)) {
            ShortcutBadger.removeCount(HomeActivity.this); //for 1.1.4+
        }

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", "");

        layoutManager = new LinearLayoutManager(this);
        recycler_notification.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this, arrNotificationList, user_id);
        recycler_notification.setAdapter(notificationAdapter);

        recycler_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            getNotificationList();
                        }
                    }
                }
            }
        });

        notificationAdapter.setOnItmeClicked(new NotificationAdapter.OnClicked() {
            @Override
            public void setOnClickedItem(int position) {


                NotificationModel model = arrNotificationList.get(position);

                String all_vals = model.viewed_by;
                List<String> list = Arrays.asList(all_vals.split(","));
                if (!list.contains(user_id)) {
                    changeStatusNotification(model.comment_id);
                } else {

                }

                SaleDetailFragment ldf = new SaleDetailFragment();
                Bundle args = new Bundle();
                args.putString("transactionId", String.valueOf(model.transaction_id));
                args.putString("from", "fromNotificationActivity");
                args.putString("subFrom", "fromNotificationActivity");
                args.putInt("return_exists", 0);
                ldf.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_main, ldf);
                transaction.addToBackStack(null);
                transaction.commit();
                notificationDialog.dismiss();

            }
        });

        img_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationDialog.dismiss();
            }
        });


        notificationDialog.show();
    }

    public void openRegisterDialog() {
        openRegisterDialog = new Dialog(HomeActivity.this);
        openRegisterDialog.setContentView(R.layout.open_register_items);
        openRegisterDialog.setCancelable(false);
        Window window = openRegisterDialog.getWindow();
        window.setLayout(GridLayoutManager.LayoutParams.MATCH_PARENT, GridLayoutManager.LayoutParams.MATCH_PARENT);
        TextView txt_open_register = openRegisterDialog.findViewById(R.id.txt_open_register);
        TextView txt_cancel_register = openRegisterDialog.findViewById(R.id.txt_cancel_register);
        et_register_amount = openRegisterDialog.findViewById(R.id.et_register_amount);

        openRegisterDialog.show();

        txt_cancel_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterDialog.dismiss();
            }
        });

        et_register_amount.setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {

            final int beforeDecimal = 7;
            final int afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String etText = et_register_amount.getText().toString();
                String temp = et_register_amount.getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = et_register_amount.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }
                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }
                }
                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});

        txt_open_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_register_amount.getText().toString().isEmpty()) {
                    AppConstant.showToast(HomeActivity.this, "Please Enter Amount");
                } else {

                    if (AppConstant.isNetworkAvailable(HomeActivity.this)) {
                        openRegisterApiCall(); //here we are calling the api to open register
                    } else {
                        AppConstant.openInternetDialog(HomeActivity.this);
                    }

                }

            }
        });

    }

    public void openRegisterApiCall() {
        AppConstant.showProgress(HomeActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(HomeActivity.this);
        if (retrofit != null) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("amount", et_register_amount.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("json", jsonObject.toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.openRegister("cash-register", body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    AppConstant.hideProgress();
                    try {
                        if (response.body() != null) {
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);

                            Log.e("Open Register Response", respo.toString());
                            String successstatus = responseObject.optString("success");
                            //Toast.makeText( this, ""+successstatus, Toast.LENGTH_LONG).show();
                            if (successstatus.equalsIgnoreCase("true")) {
                                AppConstant.hideKeyboardFrom(HomeActivity.this);
                                openRegisterDialog.dismiss();

                                Intent i = new Intent(HomeActivity.this, ActivityPosTerminalDropdown.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                img_close_register.setVisibility(View.VISIBLE);

                                AppConstant.showToast(HomeActivity.this, "Register Opened Successfully");

                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(HomeActivity.this, "cash-register API", "(HomeActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(HomeActivity.this, "Could Not Open Register. Please Try Again", Toast.LENGTH_LONG).show();

                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(HomeActivity.this, "Could Not Open Register. Please Try Again", Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    // check notification count
    public void checkNotificationCount() {
//        AppConstant.showProgress(HomeActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(HomeActivity.this);
        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = apiService.getList("fetch-unread-notifications");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        AppConstant.hideProgress();
                        String respo = response.body().string();
                        Log.e("check notification", respo.toString());
                        JSONObject responseObject = new JSONObject(respo);
                        String successstatus = responseObject.optString("success");
                        if (successstatus.equalsIgnoreCase("true")) {

                            int unseen_notification = responseObject.getInt("unseen_notification");
                            if (unseen_notification == 0) {
                                txt_notification_count.setVisibility(View.GONE);
                                txt_notification_count.setText(String.valueOf(unseen_notification));
                            } else {
                                txt_notification_count.setVisibility(View.VISIBLE);
                                txt_notification_count.setText(String.valueOf(unseen_notification));
                            }

                        }

                    } else {
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(HomeActivity.this, "fetch-unread-notifications API", "(HomeActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(HomeActivity.this, "Could Not Check Notification Count. Please Try Again", Toast.LENGTH_LONG).show();

                    }

                } catch (Exception e) {
                    AppConstant.hideProgress();
                    Log.e("Exception", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppConstant.hideProgress();
                Toast.makeText(HomeActivity.this, "Could Not Check Notification Count. Please Try Again", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void checkRegisterOpen() {
        AppConstant.showProgress(HomeActivity.this, false);
        Retrofit retrofit = APIClient.getClientToken(HomeActivity.this);
        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<ResponseBody> call = apiService.checkRegisterOpen("cash-register/check-open-register");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AppConstant.hideProgress();
                try {
                    if (response.body() != null) {

                        String respo = response.body().string();
                        Log.e("check-open-register", respo.toString());
                        JSONObject responseObject = new JSONObject(respo);
                        String successstatus = responseObject.optString("success");
                        if (successstatus.equalsIgnoreCase("true")) {
                            isRegisterOpen = true;
                            img_close_register.setVisibility(View.VISIBLE);

                        } else {
                            isRegisterOpen = false;
                            img_close_register.setVisibility(View.GONE);

                        }
                    } else {
                        isRegisterOpen = false;
                        img_close_register.setVisibility(View.GONE);
                        AppConstant.hideProgress();
                        AppConstant.sendEmailNotification(HomeActivity.this, "cash-register/check-open-register API", "(HomeActivity Screen)", "Web API Error : API Response Is Null");
                        Toast.makeText(HomeActivity.this, "Could Not Check Register Status. Please Try Again", Toast.LENGTH_LONG).show();

                    }

                } catch (Exception e) {
                    isRegisterOpen = false;
                    img_close_register.setVisibility(View.GONE);
                    AppConstant.hideProgress();
                    Log.e("Exception", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AppConstant.hideProgress();
                isRegisterOpen = false;
                img_close_register.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Could Not Check Register Status. Please Try Again", Toast.LENGTH_LONG).show();

            }
        });
    }

    //common data api
    private void commonDataApi() {
//        AppConstant.showProgress(this, false);
        Retrofit retrofit = APIClient.getClientToken(HomeActivity.this);
        APIInterface apiService = retrofit.create(APIInterface.class);
        Call<LoginParse> call = apiService.commonApi("common");
        call.enqueue(new Callback<LoginParse>() {
            @Override
            public void onResponse(@NonNull Call<LoginParse> call, @NonNull Response<LoginParse> response) {
                if (response.body() != null) {
                    AppConstant.hideProgress();
                    LoginParse loginData = response.body();
                    countryList = loginData.getCountryLists();

                    String gson3 = new Gson().toJson(loginData.getBusiness_details());
                    editor.putString("myBusinessDetail", gson3);
                    editor.commit();

                    String gson = new Gson().toJson(countryList);
                    editor.putString("country_list", gson);
                    editor.commit();

                    permissionsDataList = loginData.getPermissionsDataLists();
                    String gson1 = new Gson().toJson(permissionsDataList);
                    editor.putString("permissionDataList", gson1);
                    editor.commit();

                    Log.e("permisionListData", gson1);
                    ArrayList<String> repairModuleList = loginData.getenabled_modules();
                    String gson2 = new Gson().toJson(repairModuleList);
                    editor.putString("enableList", gson2);
                    editor.commit();

                    Log.e("enableList", gson2);

                    //save default pos setting in APP Constant
                    if (!loginData.getBusiness_details().getPos_settings().equals("")) {
                        String json = loginData.getBusiness_details().getPos_settings();

                        PosSettingDefault objDefault = fetchedDettingFromLoginResponse(json);

                        if (objDefault != null) {
                            AppConstant.objPosSetting = fetchedDettingFromLoginResponse(json);
                        }

                    }

                } else {
                    AppConstant.hideProgress();
                    AppConstant.sendEmailNotification(HomeActivity.this, "Common API", "(HomeActivity Screen)", "Web API Error : API Response Is Null");
                    Toast.makeText(HomeActivity.this, "Could Not Fetched Data. Please Try Again", Toast.LENGTH_LONG).show();
                }
//                    }
            }


            @Override
            public void onFailure(Call<LoginParse> call, Throwable t) {
                AppConstant.hideProgress();
                Toast.makeText(HomeActivity.this, "Could Not Fetched Data. Please Try Again", Toast.LENGTH_LONG).show();
            }
        });

    }

    public PosSettingDefault fetchedDettingFromLoginResponse(String strJson) {

        try {
            JSONObject objJsonObject = new JSONObject(strJson);
            PosSettingDefault obj = new PosSettingDefault();
            if (objJsonObject.getInt("disable_pay_checkout") == 1) {
                obj.disable_pay_checkout = true;
            }
            if (objJsonObject.getInt("disable_draft") == 1) {
                obj.disable_draft = true;
            }
            if (objJsonObject.getInt("disable_express_checkout") == 1) {
                obj.disable_express_checkout = true;
            }
            if (objJsonObject.getInt("hide_product_suggestion") == 1) {
                obj.hide_product_suggestion = true;
            }
            if (objJsonObject.getInt("hide_recent_trans") == 1) {
                obj.hide_recent_trans = true;
            }

            if (objJsonObject.getInt("hide_recent_trans") == 1) {
                obj.hide_recent_trans = true;
            }

            if (objJsonObject.getInt("disable_discount") == 1) {
                obj.disable_discount = true;
            }
            if (objJsonObject.getInt("disable_order_tax") == 1) {
                obj.disable_order_tax = true;
            }

            if (objJsonObject.getInt("is_pos_subtotal_editable") == 1) {
                obj.is_pos_subtotal_editable = true;
            }

            return obj;
        } catch (Exception e) {
            Log.e("Exception", e.toString());
            return null;
        }
    }

    public void getNotificationList() {
        if (isScrolling) {
            page_number = 0;
            arrNotificationList.clear();
            AppConstant.showProgress(this, false);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("fetch-notifications?limit=10" + "&page_number=" + page_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("notification List", responseObject.toString());
                            String successstatus = responseObject.optString("success");

                            if (successstatus.equalsIgnoreCase("true")) {
                                page_number++;
                                JSONArray dataObj = responseObject.getJSONArray("notifications_data");
                                for (int i = 0; i < dataObj.length(); i++) {

                                    JSONObject data = dataObj.getJSONObject(i);
                                    NotificationModel notificationModel = new NotificationModel();
                                    if (data.has("comment_id") && !data.isNull("comment_id")) {
                                        notificationModel.comment_id = data.getInt("comment_id");
                                    }
                                    if (data.has("transaction_id") && !data.isNull("transaction_id")) {
                                        notificationModel.transaction_id = data.getInt("transaction_id");
                                    }
                                    if (data.has("comment_status") && !data.isNull("comment_status")) {
                                        notificationModel.comment_status = data.getInt("comment_status");
                                    }
                                    if (data.has("comment_text") && !data.isNull("comment_text")) {
                                        notificationModel.comment_text = data.getString("comment_text");
                                    }
                                    if (data.has("comment_subject") && !data.isNull("comment_subject")) {
                                        notificationModel.comment_subject = data.getString("comment_subject");
                                    }
                                    if (data.has("icon_class") && !data.isNull("icon_class")) {
                                        notificationModel.icon_class = data.getString("icon_class");
                                    }
                                    if (data.has("link") && !data.isNull("link")) {
                                        notificationModel.link = data.getString("link");
                                    }
                                    if (data.has("viewed_by") && !data.isNull("viewed_by")) {
                                        notificationModel.viewed_by = data.getString("viewed_by");
                                    }
                                    if (data.has("viewed_by") && !data.isNull("viewed_by")) {
                                        notificationModel.notification_customer = data.getString("customer_name");
                                    }
                                    if (data.has("created_at") && !data.isNull("created_at")) {
                                        JSONObject created_atObj = data.getJSONObject("created_at");
                                        notificationModel.notification_date = created_atObj.getString("date");
                                    }
                                    arrNotificationList.add(notificationModel);

                                }

                                if (arrNotificationList.size() == 0) {
                                    txt_no_resut.setVisibility(View.VISIBLE);
                                } else {
                                    txt_no_resut.setVisibility(View.GONE);
                                }

                                notificationAdapter.notifyDataSetChanged();

                            }

                        } else {
                            AppConstant.hideProgress();
                            progressBar.setVisibility(View.GONE);
                            AppConstant.sendEmailNotification(HomeActivity.this, "notification API", "(HomeActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(HomeActivity.this, "Could Not Load Notification list. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Could Not Load Notification List. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void changeStatusNotification(int id) {
        Retrofit retrofit = APIClient.getClientToken(this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getNotificationDetail("change-notification-status", id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("change status", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");

                            if (successstatus.equalsIgnoreCase("true")) {

                                checkNotificationCount();
//                                getNotificationList();
                            }
//                            AppConstant.showToast(HomeActivity.this, "" + msg);
                        } else {
                            AppConstant.hideProgress();

                            AppConstant.sendEmailNotification(HomeActivity.this, "change-notification-status API", "(HomeActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(HomeActivity.this, "Could Not Change Status. Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(HomeActivity.this, "Could Not Change Status. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    public void addDeviceToServer() {
        Retrofit retrofit = APIClient.getClientToken(this);
        Log.e("installation_id", "" + getInstallationId());
        Log.e("device_id", "" + getDeviceId());
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("androidId", "" + androidId);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<ResponseBody> call = apiService.getList("subscribeWonderPush?installation_id=" + getInstallationId() + "&device_id=" + getDeviceId() + "&business_id=" + login_business_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        if (response.body() != null) {
                            AppConstant.hideProgress();
                            String respo = response.body().string();
                            JSONObject responseObject = new JSONObject(respo);
                            Log.e("addtoWonderpush", responseObject.toString());
                            String successstatus = responseObject.optString("success");
                            String msg = responseObject.optString("msg");

                            if (successstatus.equalsIgnoreCase("true")) {
//                                AppConstant.showToast(HomeActivity.this, "" + msg);
                            }

                        } else {
                            AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(HomeActivity.this, "subscribeWonderPushNotification API", "(HomeActivity)", "Web API Error : API Response Is Null");
                            Toast.makeText(HomeActivity.this, "Could Not Subscribe To Wonderpush . Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppConstant.hideProgress();
                    Toast.makeText(HomeActivity.this, "Could Not Could Not Subscribe To Wonderpush. Please Try Again", Toast.LENGTH_LONG).show();
                }

            });

        }

    }


}
