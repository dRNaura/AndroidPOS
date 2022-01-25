package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivityPosItemList;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.activity.ActivityPurchases.AddPurchaseSection.PurchaseProductSearchList;
import com.pos.salon.client.APIClient;
import com.pos.salon.interfacesection.APIInterface;
import com.pos.salon.model.customerData.CustomerListData;
import com.pos.salon.model.posLocation.BusinessLocationData;
import com.pos.salon.model.posLocation.CurriencyData;
import com.pos.salon.model.searchData.SearchItem;
import com.pos.salon.model.searchData.SearchItemResponse;
import com.pos.salon.utilConstant.AppConstant;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener, ScanListener {
    private static final String TAG = ScannerActivity.class.getSimpleName();
    private static final int Handler_SHOW_RESULT = 1999;
    private static final int Handler_CLEAN = 2000;
    // 广播发送至Settings，执行开启和关闭NFC功能
    private static final String NFC_ACTION_OPEN_CLOSE_ACTION = "com.jiebaosz.nfc.detect.action";

    private String codeId = "", val = "";
    private long mNowTime = 0;
    private long mLastTime = 0;
    private int mSuccessCount = 0;
    boolean mIsServiceBind = false;

    private TextView mTvAllCount, mTvSuccessCount;
    private TextView mTvMain, mTvCodeid;
    private Button mBtnStartSingleScanner, mBtnStopScanner, mCleanContent, mBtnStartContinueScanner, mBtnStopContinueScanner, mBtnConnect, mBtnDisconnect, stop_scan;
    private ScrollView mScrollView;

    private ScanService mScanService = null;
    private ScanController mScanController;
    private NfcAdapter mNfcAdapter;

    private BeepManager mBeepManager;
    private Intent mServiceIntent;

    private UpadateThread mUpadateThread;

    //new items added for POs
    ArrayList<SearchItem> arrCartProducts = new ArrayList<SearchItem>();
    SharedPreferences sp_cartSave; // for fetch data from preference
    SharedPreferences.Editor ed_cartSave; //for save data in preference.
    SharedPreferences sp_countproduct, sp_modifiers;
    SharedPreferences.Editor ed_countproduct, ed_modifiers;
    int count = 0;
    int a = 0, locationId = 0, supplierID = 0;
    public String from = "";
    Toolbar toolbar;
    public boolean isSingle = false;
    ArrayList<String> productIdsList = new ArrayList<>();
    BusinessLocationData locationData;
    CurriencyData currencyData;
    CustomerListData customerData;
    String orderr_type = "", tablee_id = "", waiterr_id = "", comingFrom = "", purchaseStatus = "", purchaseDate = "", reference_no = "";

    private class UpadateThread extends Thread {
        public boolean run;

        @Override
        public void run() {
            while (run) {
                mHandler.sendEmptyMessage(777);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mHandler.removeMessages(777);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Handler_SHOW_RESULT:
                    mTvCodeid.setText("");
                    if (null != codeId) {
                        mTvCodeid.setText(codeId);
                    }
                    if (null != val) {
                        mTvMain.append(val + "\n");
                        mSuccessCount++;
                        mTvSuccessCount.setText("" + mSuccessCount);

                        mBeepManager.play();
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

                        Toast.makeText(ScannerActivity.this, "Scan Successfull", Toast.LENGTH_SHORT).show();

                        if (from.equalsIgnoreCase("searchActivity")) {
                            if (isSingle) {
                                searchItemCollection(val, locationData.getId());
                            } else {
                                productIdsList.add(val);
                            }

                        } else if (from.equalsIgnoreCase("purchaseActivity")) {

                            if (isSingle) {
                                searchItemCollection(val, String.valueOf(locationId));
                            } else {
                                productIdsList.add(val);
                            }

                        } else if (from.equalsIgnoreCase("findInvoice")) {

                            ActivitySearchItemList.strScannedBarCode=val;
                            finish();
                        } else if (from.equalsIgnoreCase("fromReturnSale")) {
                            ActivitySearchItemList.strScannedBarCode=val;
                            finish();
                        }
//                        else if (from.equalsIgnoreCase("fromRepair")) {
//                            Intent i = new Intent(ScannerActivity.this, RepairDetailActivity.class);
//                            i.putExtra("repair_id", Integer.parseInt(String.valueOf(val)));
//                            startActivity(i);
//                            finish();
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
//                        }
                    }
                    break;

                case Handler_CLEAN:
                    mTvCodeid.setText("");
                    mTvMain.setText("");
                    break;

                case 777:// 刷新UI
                    if (null != mScanController) {
                        mTvAllCount.setText("" + mScanController.getScan_count());
                    }
                    mTvSuccessCount.setText("" + mSuccessCount);
                    break;

                case 888:
                    if (null != mUpadateThread) {
                        mUpadateThread.run = false;
                        mUpadateThread = null;
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.trace();
            mIsServiceBind = true;

            ScanService.MyBinder myBinder = (ScanService.MyBinder) service;
            mScanService = myBinder.getService();

            // 通过ScanService获取到ScanController实例，在Service的onStart()初始化
            mScanController = mScanService.getScanManager();

            // ScanActivity.this需要覆写ScanListener的2个方法
            mScanService.setOnScanListener(ScannerActivity.this);
            mScanService.setActivityUp(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.trace();
            mIsServiceBind = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initView();
        setBackNavgation();


        mBeepManager = new BeepManager(this, true, false);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(ScannerActivity.this);

        mServiceIntent = new Intent(ScannerActivity.this, ScanService.class);
        // TODO 为何此处需要这么做？
        startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //items fo POS APp


        from = getIntent().getStringExtra("from");
        if (getIntent().getSerializableExtra("location") != null) {
            locationData = (BusinessLocationData) getIntent().getSerializableExtra("location");
        }
        if (getIntent().getSerializableExtra("currency") != null) {
            currencyData = (CurriencyData) getIntent().getSerializableExtra("currency");
        }
        if (getIntent().getSerializableExtra("customer") != null) {
            customerData = (CustomerListData) getIntent().getSerializableExtra("customer");
        }
        if (getIntent().getSerializableExtra("selected_ordertype") != null) {
            orderr_type = getIntent().getStringExtra("selected_ordertype");
        }
        if (getIntent().getSerializableExtra("tablee_id") != null) {
            tablee_id = getIntent().getStringExtra("tablee_id");
        }
        if (getIntent().getSerializableExtra("waiterr_id") != null) {
            waiterr_id = getIntent().getStringExtra("waiterr_id");
        }
        if (getIntent().getStringExtra("comingFrom") != null) {
            comingFrom = getIntent().getStringExtra("comingFrom");
        }
        if (from.equalsIgnoreCase("purchaseActivity")) {
            locationId = getIntent().getIntExtra("location_id", 0);
            reference_no = getIntent().getStringExtra("reference_no");
            purchaseDate = getIntent().getStringExtra("purchaseDate");
            purchaseStatus = getIntent().getStringExtra("purchaseStatus");
            supplierID = getIntent().getIntExtra("supplierID", 0);
            comingFrom = getIntent().getStringExtra("comingFrom");
        }

        if (from.equalsIgnoreCase("findInvoice") || from.equalsIgnoreCase("fromRepair") || from.equalsIgnoreCase("fromReturnSale")) {
            mBtnStartContinueScanner.setVisibility(View.GONE);
            stop_scan.setVisibility(View.GONE);
            mBtnStartSingleScanner.setVisibility(View.VISIBLE);
        } else {
            mBtnStartSingleScanner.setVisibility(View.VISIBLE);
            mBtnStartContinueScanner.setVisibility(View.VISIBLE);
            stop_scan.setVisibility(View.GONE);
        }

        sp_countproduct = getSharedPreferences("CountProduct", MODE_PRIVATE);
        ed_countproduct = sp_countproduct.edit();

        ///get cart shared preference
        sp_cartSave = getSharedPreferences("myCartPreference", MODE_PRIVATE);
        ed_cartSave = sp_cartSave.edit();

        if (null == mNfcAdapter) {
            LogUtil.d(TAG, "NFC module is unable to use...");
            return;
        } else {
            if (mNfcAdapter.isEnabled()) {
                // open --> close
                closeOrOpenNfcModel();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.trace("mScanService is null? --> " + (mScanService != null));

        // TODO 此处是否有问题？和onServiceConnected()时序冲突？
        // 事实证明：ScanService已经实例化
        if (mScanService != null) {
            mScanService.setActivityUp(true);
        }
        ScanService.isScanActivityUp = false;
    }

    @Override
    protected void onPause() {
        LogUtil.trace();
        super.onPause();

        if (mScanService != null)
            mScanService.setActivityUp(false);
    }

    @Override
    protected void onDestroy() {
        LogUtil.trace();
        super.onDestroy();

        if (null != mUpadateThread) {
            mUpadateThread.run = false;
            mUpadateThread = null;
        }

        if (mScanService != null) {
            if (null != mScanController) {
                mScanController.Barcode_Stop();

                // 从点击断开的onClick()中挪过来
                mScanController.Barcode_Close();
            }
        }

        if (mIsServiceBind) {
            unbindService(mServiceConnection);
            this.stopService(mServiceIntent);
        }

        mSuccessCount = 0;
        if (mScanController != null) {
            mScanController.clearScan_count();
        }
        mHandler.sendEmptyMessage(Handler_CLEAN);
        mHandler.sendEmptyMessage(777);

        if (null == mNfcAdapter) {
            LogUtil.d(TAG, "NFC module is unable to use...");
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                // 若当前是关闭的状态，则执行close --> open
//                closeOrOpenNfcModel();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean_bu: // 内容清空
                mSuccessCount = 0;
                if (mScanController != null) {
                    mScanController.clearScan_count();
                }
                mHandler.sendEmptyMessage(Handler_CLEAN);
                mHandler.sendEmptyMessage(777);
                break;

            case R.id.open_bu3: // 连接
                LogUtil.d(TAG, "onClick::connect button");
                setVisble(true);
                break;

            case R.id.start_bu1: // 开始单次扫描
                LogUtil.d(TAG, "onClick::single scan button");
                mNowTime = System.currentTimeMillis();
                if (mScanController != null) {
                    isSingle = true;
                    mScanController.Barcode_Stop();
                    if (mNowTime - mLastTime > 200) {
                        if (null != mScanController) {
                            mHandler.sendEmptyMessage(777);
                            mScanController.Barcode_Start();
                        }
                        mLastTime = mNowTime;
                    }

                    stop_scan.setVisibility(View.GONE);
                    mBtnStartContinueScanner.setVisibility(View.GONE);
                    mBtnStartSingleScanner.setVisibility(View.GONE);

                } else {
                    Toast.makeText(ScannerActivity.this, "Unable to Scan", Toast.LENGTH_SHORT).show();
                }


//                if (from.equalsIgnoreCase("findInvoice")) {
//                    ActivitySearchItemList.strScannedBarCode="0084";
//                    finish();
//                } else if (from.equalsIgnoreCase("fromReturnSale")) {
//                    ActivitySearchItemList.strScannedBarCode="54774";
//                    finish();
//
//                }
//                stop_scan.setVisibility(View.GONE);
//                mBtnStartContinueScanner.setVisibility(View.GONE);
//                mBtnStartSingleScanner.setVisibility(View.GONE);
//                searchItemCollection("999464415", locationData.getId());

                break;

            case R.id.stop_bu2: // 停止扫描
                if (mScanController != null) {
                    mScanController.Barcode_Stop();
                }
                break;

            case R.id.close_bu4: // 断开
                if (null != mScanController) {
                    mScanController.Barcode_Stop();
                }
                setVisble(false);
                break;

            case R.id.start_continue: // 开始连续扫描

                if (mScanController != null) {
                    isSingle = false;
                    mScanController.Barcode_Continue_Start(500);

                    mUpadateThread = new UpadateThread();
                    mUpadateThread.run = true;
                    mUpadateThread.start();

                    stop_scan.setVisibility(View.VISIBLE);
                    mBtnStartSingleScanner.setVisibility(View.GONE);
                    mBtnStartContinueScanner.setVisibility(View.GONE);


                } else {
                    Toast.makeText(ScannerActivity.this, "Unable to Scan", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.stop_continue: // 停止连续扫描
                if (mScanController != null) {
                    mScanController.Barcode_Continue_Stop();
                }
                mHandler.sendEmptyMessageDelayed(888, 500);
                break;
            case R.id.stop_scan: // 停止连续扫描

                isSingle = false;
//                String[] mStringArray = new String[productIdsList.size()];
//                mStringArray = productIdsList.toArray(mStringArray);

//                productIdsList.add("999464401-4");
//                productIdsList.add("999464401-4");
//                productIdsList.add("999463135");
//                productIdsList.add("999463135");

                if (mScanController != null) {
                    mScanController.Barcode_Continue_Stop();
                }
                if (null != mScanController) {
                    mScanController.Barcode_Stop();
                }

                mSuccessCount = 0;
                if (mScanController != null) {
                    mScanController.clearScan_count();
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(888, 500);
                    mHandler.sendEmptyMessage(Handler_CLEAN);
                    mHandler.sendEmptyMessage(777);
                }

                StringBuilder str = new StringBuilder("");
                for (String eachstring : productIdsList) {
                    str.append(eachstring).append(",");
                }
                String commaseparatedlist = str.toString();

                if (commaseparatedlist.length() > 0)
                    commaseparatedlist = commaseparatedlist.substring(0, commaseparatedlist.length() - 1);

                Log.e("products", commaseparatedlist);

                searchItemContinousScan(commaseparatedlist);
//                searchItemContinousScan("999464401-2,999464401-1");
                mTvMain.setText("");

                break;

            default:
                break;
        }
    }

    @Override
    public void result(String content) {

    }

    @Override
    public void henResult(String codeType, String context) {
        LogUtil.d(TAG, "henResult");
        if (null != codeType) {
            LogUtil.trace("codeType-->" + codeType);
        }

        if (null != context) {
            ScannerActivity.this.codeId = codeType;
            ScannerActivity.this.val = context;
            Message msg = new Message();
            msg.what = Handler_SHOW_RESULT;
            mHandler.sendMessage(msg);
            mScanController.Barcode_Stop();
        }
    }

    private void initView() {
        LogUtil.trace();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTvAllCount = (TextView) findViewById(R.id.tv_all_count);
        stop_scan = findViewById(R.id.stop_scan);
        mTvSuccessCount = (TextView) findViewById(R.id.tv_success_count);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mTvMain = (TextView) findViewById(R.id.main_tv);
        mTvCodeid = (TextView) findViewById(R.id.tv_codeid);

        // 清空
        mCleanContent = (Button) findViewById(R.id.clean_bu);
        // 连接
        mBtnConnect = (Button) findViewById(R.id.open_bu3);
        // 单次扫描
        mBtnStartSingleScanner = (Button) findViewById(R.id.start_bu1);
        // 停止扫描
        mBtnStopScanner = (Button) findViewById(R.id.stop_bu2);
        // 断开
        mBtnDisconnect = (Button) findViewById(R.id.close_bu4);
        // 连续扫描
        mBtnStartContinueScanner = (Button) findViewById(R.id.start_continue);
        // 停止连续扫描
        mBtnStopContinueScanner = (Button) findViewById(R.id.stop_continue);

        mBtnStartSingleScanner.setOnClickListener(this);
        mBtnStopScanner.setOnClickListener(this);
        mBtnStartContinueScanner.setOnClickListener(this);
        stop_scan.setOnClickListener(this);

        setVisble(true);
    }

    private void setVisble(boolean flag) {
        mCleanContent.setEnabled(flag);
        mBtnStartSingleScanner.setEnabled(flag);
        mBtnStopScanner.setEnabled(flag);
        mBtnStartContinueScanner.setEnabled(flag);
        mBtnStopContinueScanner.setEnabled(flag);
    }

    /**
     * <功能描述> 若在扫码过程中，NFC模块启动会导致执行onPause()，从而扫码停止；解决办法停止NFC功能 <实现方式>
     * 发送NFC当前状态的广播，在com.android.settings中对当前状态做处理：打开的 --> 关闭，反之亦然
     *
     * @return void [返回类型说明]
     */
    private void closeOrOpenNfcModel() {
//        if (mNfcAdapter.isEnabled()) {
//            // NFC模块可用，且已打开
//            int nfcState = mNfcAdapter.getAdapterState();
//            LogUtil.d(TAG, "nfcAdapter.getAdapterState()::" + nfcState);

        sendNfcActionBroadcast(true);
//        } else {
//            sendNfcActionBroadcast(false);
//        }
    }

    private void sendNfcActionBroadcast(boolean state) {
        Intent intent = new Intent();
        intent.setAction(NFC_ACTION_OPEN_CLOSE_ACTION);
        // 当前是open状态，传递true
        intent.putExtra("state_action", state);
        ScannerActivity.this.sendBroadcast(intent);
    }

    private void searchItemCollection(final String quaryText, String locationID) {

//        if(listsize==0){
        AppConstant.showProgress(this, false);
//        }
        Retrofit retrofit = APIClient.getClientToken(ScannerActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<SearchItemResponse> call = apiService.getSearchItem("get-product-suggestion?location_id=" + locationID + "&term=" + quaryText + "&limit=0"+"&enable_stock=1");
            call.enqueue(new Callback<SearchItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<SearchItemResponse> call, @NonNull Response<SearchItemResponse> response) {
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        Log.e("Product Search : ", "" + response.body());
                        SearchItemResponse searchResponse = response.body();
                        if (searchResponse.isSuccess()) {
//                            if (strFromWhere.equalsIgnoreCase("textChanged")) {
//                                itemArray.clear();
////                                strScannedBarCode="";
//                            } else if (strFromWhere.equalsIgnoreCase("scanBarCode")) {//direct add to cart product.


                            if (sp_countproduct.getString("countt", "").equals("")) {
                                count = 0;
                            } else {
                                count = Integer.parseInt(sp_countproduct.getString("countt", ""));
                            }

                            if (count > 0) {//reset cart products if comes from PosItemList(cart)
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<SearchItem>>() {
                                    }.getType();

                                    String strMyCart = sp_cartSave.getString("myCart", "");
                                    arrCartProducts = (ArrayList<SearchItem>) gson.fromJson(strMyCart, type);

                                }
                            }

                            ArrayList<SearchItem> arrScannedProducts = searchResponse.getProduct_list();


                            if (arrScannedProducts.size() > 0) {
                                SearchItem item1 = arrScannedProducts.get(0);

                                if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
                                    item1.setQty_available("0");
//                                    Toast.makeText(ScannerActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();

                                    return;
                                }

                                double dStockQuantityAvail = Double.valueOf(item1.getQty_available());
                                int iStockQuantityAvail = (int) dStockQuantityAvail;

                                if (iStockQuantityAvail > 0) {


                                    item1.setActiveProduct(true);

                                    // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                                    item1.setUnitFinalPrice(item1.getSelling_price());

                                    boolean isAlreadyAdded = false;
                                    for (int i = 0; i < arrCartProducts.size(); i++) {
                                        SearchItem objItem = arrCartProducts.get(i);


                                        if (objItem.getProduct_id().equalsIgnoreCase(item1.getProduct_id()) && objItem.getVariation_id().equalsIgnoreCase(item1.getVariation_id())) {
                                            if (objItem.getQuantity() < iStockQuantityAvail) {
                                                objItem.setQuantity(objItem.getQuantity() + 1);

                                            } else {
                                                mBtnStartSingleScanner.setVisibility(View.VISIBLE);
                                                Toast.makeText(ScannerActivity.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            isAlreadyAdded = true;

                                            break;
                                        }
                                    }

                                    if (isAlreadyAdded) {
//                                            if(iProductUpdatedQuantity > iStockQuantityAvail)
//                                            {
//                                                Toast.makeText(ActivitySearchItemList.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_LONG).show();
//                                                return;
//                                            }

                                    } else {
//                                        AppConstant.showToast(ScannerActivity.this, "Item Added To Cart");
                                        arrCartProducts.add(item1);
                                        // long insert = dbHelper.insertItem(item1);

                                        count = count + 1;
                                    }


                                    // added cart badge here
                                    ed_countproduct.putString("countt", String.valueOf(count));
                                    ed_countproduct.commit();
//                                    tv_productcount.setText(sp_countproduct.getString("countt", ""));
                                    // Toast.makeText(ActivitySearchItemList.this, "" +count, Toast.LENGTH_LONG).show();

                                    Gson gson = new Gson();
                                    ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                                    ed_cartSave.commit();

//                                        tv_productcount.setBackgroundResource(R.drawable.circle_back);

                                } else {

                                    Toast.makeText(ScannerActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            AppConstant.showToast(ScannerActivity.this, "Item Added To Cart");

                            if (from.equalsIgnoreCase("purchaseActivity")) {
                                Intent i = new Intent(ScannerActivity.this, PurchaseProductSearchList.class);
                                i.putExtra("location_id", locationId);
                                i.putExtra("reference_no", reference_no);
                                i.putExtra("purchaseDate", purchaseDate);
                                i.putExtra("purchaseStatus", purchaseStatus);
                                i.putExtra("supplierID", supplierID);
                                i.putExtra("comingFrom", "fromSearchItem");
                                startActivity(i);
                            } else {
                                Intent intent = new Intent(ScannerActivity.this, ActivityPosItemList.class);

                                intent.putExtra("location", locationData);
                                intent.putExtra("currency", currencyData);
                                intent.putExtra("customer", customerData);
                                intent.putExtra("selected_ordertype", orderr_type);
                                intent.putExtra("tablee_id", tablee_id);
                                intent.putExtra("waiterr_id", waiterr_id);
                                intent.putExtra("comingFrom", "fromSearchItem");

                                startActivity(intent);

                            }

                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }else{
                                AppConstant.showToast(ScannerActivity.this, "Item Added To Cart");
                            }
//                            if(listsize==4){
//                                if(arrCartProducts.size() >0){
//                                    AppConstant.showToast(ScannerActivity.this,"Items Added to Cart");
//                                }
//                                AppConstant.hideProgress();
//                                finish();
//                            }

//                            if( a > 1){
//                                finish();
//                            }else{
//                                searchItemCollection("999464401",locationID);
//                            }


//                            }

//                            if (!quaryText.equalsIgnoreCase("") && !strFromWhere.equals("scanBarCode")) {
//                                itemArray.addAll(searchResponse.getProduct_list());
//                            }
//                            // itemArray = searchResponse.getProduct_list();
//
//                            if (searchEditBox.getText().toString().isEmpty() && !strFromWhere.equals("scanBarCode")) {
//                                itemArray.clear();
//                            }

//                        }

//                        if (itemArray.size() > 0 || strFromWhere.equalsIgnoreCase("scanBarCode")) {
//                            if (strFromWhere.equalsIgnoreCase("scanBarCode")) {
//                                img_clear_search_product.setVisibility(View.GONE);
//                            } else {
//                                img_clear_search_product.setVisibility(View.VISIBLE);
//                            }
//                            imgNotFount.setVisibility(View.GONE);
//                            llAlert.setVisibility(View.GONE);
//                        } else {
//                            img_clear_search_product.setVisibility(View.GONE);
//                            imgNotFount.setVisibility(View.VISIBLE);
//                            llAlert.setVisibility(View.VISIBLE);
//                        }

//         /               adapter.updateNewItem(itemArray);

                        } else {
                            //AppConstant.hideProgress();
                            AppConstant.sendEmailNotification(ScannerActivity.this, "get-product-suggestion API", "(ScannerActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(ScannerActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<SearchItemResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Log.e("Throwable ", t.toString());
                    Toast.makeText(ScannerActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void searchItemContinousScan(String quaryText) {

//        if(listsize==0){
        AppConstant.showProgress(this, false);
//        }
        Retrofit retrofit = APIClient.getClientToken(ScannerActivity.this);
        if (retrofit != null) {
            APIInterface apiService = retrofit.create(APIInterface.class);
            Call<SearchItemResponse> call = apiService.getSearchItem("get-product-detail?term=" + quaryText);
            call.enqueue(new Callback<SearchItemResponse>() {
                @Override
                public void onResponse(@NonNull Call<SearchItemResponse> call, @NonNull Response<SearchItemResponse> response) {
                    AppConstant.hideProgress();
                    if (response.body() != null) {
                        productIdsList.clear();
                        Log.e("Product Search : ", "" + response.body());
                        SearchItemResponse searchResponse = response.body();
                        if (searchResponse.isSuccess()) {
//                            if (strFromWhere.equalsIgnoreCase("textChanged")) {
//                                itemArray.clear();
////                                strScannedBarCode="";
//                            } else if (strFromWhere.equalsIgnoreCase("scanBarCode")) {//direct add to cart product.


                            if (sp_countproduct.getString("countt", "").equals("")) {
                                count = 0;
                            } else {
                                count = Integer.parseInt(sp_countproduct.getString("countt", ""));
                            }

                            if (count > 0) {//reset cart products if comes from PosItemList(cart)
                                if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<SearchItem>>() {
                                    }.getType();

                                    String strMyCart = sp_cartSave.getString("myCart", "");
                                    arrCartProducts = (ArrayList<SearchItem>) gson.fromJson(strMyCart, type);

                                }
                            }

                            ArrayList<SearchItem> arrScannedProducts = searchResponse.getProduct_list();

                            for (int a = 0; a < arrScannedProducts.size(); a++) {
                                ArrayList<SearchItem> arrScannedProducts2 = arrScannedProducts.get(a).getDetails();
                                if (arrScannedProducts2.size() > 0) {
                                    SearchItem item1 = arrScannedProducts2.get(0);

                                    if (item1.getQty_available() == null || item1.getQty_available().equalsIgnoreCase("")) {
                                        item1.setQty_available("0");
//                                    Toast.makeText(ScannerActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();

                                        return;
                                    }

                                    double dStockQuantityAvail = Double.valueOf(item1.getQty_available());
                                    int iStockQuantityAvail = (int) dStockQuantityAvail;

                                    if (iStockQuantityAvail > 0) {


                                        item1.setActiveProduct(true);

                                        // arrCartOrignalProducts.add(item1); // add products to forwarding on cart screen for use in modifiers.
                                        item1.setUnitFinalPrice(item1.getSelling_price());

                                        boolean isAlreadyAdded = false;
                                        for (int i = 0; i < arrCartProducts.size(); i++) {
                                            SearchItem objItem = arrCartProducts.get(i);


                                            if (objItem.getProduct_id().equalsIgnoreCase(item1.getProduct_id()) && objItem.getVariation_id().equalsIgnoreCase(item1.getVariation_id())) {

                                                iStockQuantityAvail = iStockQuantityAvail - 1;
                                                if (objItem.getQuantity() <= iStockQuantityAvail) {
//                                                AppConstant.showToast(ScannerActivity.this, "Item Added To Cart");
                                                    objItem.setQuantity(objItem.getQuantity() + 1);

                                                }
//                                                else {
//                                                Toast.makeText(ScannerActivity.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_SHORT).show();
//                                                    return;
//                                                }
                                                isAlreadyAdded = true;

                                                break;
                                            }
                                        }

                                        if (isAlreadyAdded) {
//                                            if(iProductUpdatedQuantity > iStockQuantityAvail)
//                                            {
//                                                Toast.makeText(ActivitySearchItemList.this, "This Product Has No More Stock Available For Now", Toast.LENGTH_LONG).show();
//                                                return;
//                                            }

                                        } else {
//                                        AppConstant.showToast(ScannerActivity.this, "Item Added To Cart");
                                            arrCartProducts.add(item1);
                                            // long insert = dbHelper.insertItem(item1);

                                            count = count + 1;
                                        }


                                        // added cart badge here
                                        ed_countproduct.putString("countt", String.valueOf(count));
                                        ed_countproduct.commit();
//                                    tv_productcount.setText(sp_countproduct.getString("countt", ""));
                                        // Toast.makeText(ActivitySearchItemList.this, "" +count, Toast.LENGTH_LONG).show();

                                        Gson gson = new Gson();
                                        ed_cartSave.putString("myCart", gson.toJson(arrCartProducts));
                                        ed_cartSave.commit();

//                                        tv_productcount.setBackgroundResource(R.drawable.circle_back);

                                    } else {

//                                    Toast.makeText(ScannerActivity.this, "This Product Is Out Of Stock For Now", Toast.LENGTH_SHORT).show();
//                                        return;
                                    }
                                }

                            }

                            AppConstant.showToast(ScannerActivity.this, "Items Added to Cart");
                            if (from.equalsIgnoreCase("purchaseActivity")) {
                                Intent i = new Intent(ScannerActivity.this, PurchaseProductSearchList.class);
                                i.putExtra("location_id", locationId);
                                i.putExtra("reference_no", reference_no);
                                i.putExtra("purchaseDate", purchaseDate);
                                i.putExtra("purchaseStatus", purchaseStatus);
                                i.putExtra("supplierID", supplierID);
                                i.putExtra("comingFrom", "fromSearchItem");
                                startActivity(i);
                            } else {
                                Intent intent = new Intent(ScannerActivity.this, ActivityPosItemList.class);
                                intent.putExtra("location", locationData);
                                intent.putExtra("currency", currencyData);
                                intent.putExtra("customer", customerData);
                                intent.putExtra("selected_ordertype", orderr_type);
                                intent.putExtra("tablee_id", tablee_id);
                                intent.putExtra("waiterr_id", waiterr_id);
                                intent.putExtra("comingFrom", "fromSearchItem");

                                startActivity(intent);
                            }


                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);

                        } else {

                            AppConstant.sendEmailNotification(ScannerActivity.this, "get-product-detail API", "(ScannerActivity Screen)", "Web API Error : API Response Is Null");
                            Toast.makeText(ScannerActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<SearchItemResponse> call, Throwable t) {
                    AppConstant.hideProgress();
                    Log.e("Throwable ", t.toString());
                    Toast.makeText(ScannerActivity.this, "Could Not Search Your Product. Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Scan");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (comingFrom.equalsIgnoreCase("searchActivity") || comingFrom.equalsIgnoreCase("purchaseActivity")) {
//                        arrCartProducts.clear();
//
//                        //clear cart product data if comes on this screen.
//                        if (!sp_cartSave.getString("myCart", "").equalsIgnoreCase("")) {//if not cart empty.
//
//                            ed_cartSave.remove("myCart");
//                            ed_cartSave.commit();
//
//                        }
//                        // added cart badge here
//                        ed_countproduct.remove("countt");
//                        ed_countproduct.commit();
//
//
//
//                    } else {
//
//                    }
                    if (comingFrom.equalsIgnoreCase("fromSaleDetail")) {
                        Intent intent = new Intent(ScannerActivity.this, ActivityPosItemList.class);

                        intent.putExtra("location", locationData);
                        intent.putExtra("currency", currencyData);
                        intent.putExtra("customer", customerData);
                        intent.putExtra("selected_ordertype", orderr_type);
                        intent.putExtra("tablee_id", tablee_id);
                        intent.putExtra("waiterr_id", waiterr_id);
                        intent.putExtra("comingFrom", "fromSearchItem");

                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }

                }
            });
        }
    }
}