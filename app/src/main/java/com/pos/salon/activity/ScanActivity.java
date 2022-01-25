package com.pos.salon.activity;



import android.os.Bundle;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.pos.salon.R;

public class ScanActivity extends AppCompatActivity {
    public String comingFrom = "";

    Toolbar toolbar;
    RelativeLayout lay_scan_product,lay_cancel_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan_activity);

        comingFrom = getIntent().getStringExtra("comingFrom");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lay_scan_product =findViewById(R.id.lay_scan_product);
        lay_cancel_scan =findViewById(R.id.lay_cancel_scan);
        setSupportActionBar(toolbar);

//        tv_scan = (TextView) findViewById(R.id.tv_scan);

//        mScanReceiver = new ScanReceiver();
//        IntentFilter scanFilter = new IntentFilter();
//        scanFilter.addAction(Scan.scanIntentFilter);
//        registerReceiver(mScanReceiver, scanFilter);

//        setBackNavgation();


    }

//    @Override
//    protected void onResume() {
//        scan.open();
//        lay_scan_product.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scan.scan2(0);
//            }
//        });

//        lay_cancel_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scan.cancelScan();
//                if (scan != null) {
//                    int close = scan.close();
//                    Log.e("close", "close return=" + close);
//                }
//                finish();
//            }
//        });
//
//        super.onResume();


//    }

//    @Override
//    protected void onPause() {
//        scan.cancelScan();
//        if (scan != null) {
//            int close = scan.close();
//            Log.e("close", "close return=" + close);
//        }
//
//        super.onPause();
//    }

//    public class ScanReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context arg0, Intent intent) {
//            if(!broadcastTriggerd) {
//                broadcastTriggerd = true;
//                int res = intent.getIntExtra(Scan.res, 0);
//                String scanDataString = intent.getStringExtra(Scan.scanDataString);
//                Log.e("scanDataString", scanDataString);
//
////            tv_scan.setText("Scan data:" + scanDataString);
//
//                if (scanDataString != null) {
//                    // when coming for scan repair item
//                    if (comingFrom.equalsIgnoreCase("fromRepair")) {
////                        Intent i = new Intent(ScanActivity.this, RepairDetailActivity.class);
////                        i.putExtra("repair_id", scanDataString);
////                        startActivity(i);
//
//                    }
//                    //when scan to search product
//                    else {
//                        ActivitySearchItemList.strScannedBarCode = scanDataString;
//                    }
//
//                } else {
//                    Toast.makeText(ScanActivity.this, "Invalid Product ", Toast.LENGTH_LONG).show();
//                }
//                scan.cancelScan();
//                finish();
//            }
//        }
//    }

//    private void setBackNavgation() {
//        // add back arrow to toolbar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setTitle("SCAN");
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    scan.cancelScan();
//                    if (scan != null) {
//                        int close = scan.close();
//                        Log.e("close", "close return=" + close);
//                    }
//                    onBackPressed();
//                    finish();
//                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//                }
//            });
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        scan.cancelScan();
//        if (scan != null) {
//            int close = scan.close();
//            Log.e("close", "close return=" + close);
//        }
//        onBackPressed();
//        finish();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
//    }
}

//    @Override
//    public void onCreate(Bundle state) {
//        super.onCreate(state);
//
//        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ScanActivity.this, Manifest.permission.CAMERA)) {
//
//            } else {
//                ActivityCompat.requestPermissions((Activity) ScanActivity.this,
//                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUESTS);
//            }
//
//        }
//
//
//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
//        setContentView(mScannerView);                // Set the scanner view as the content view
//        comingFrom = getIntent().getStringExtra("comingFrom");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();           // Stop camera on pause
//    }
//
//    @Override
//    public void handleResult(Result rawResult) {
//        // Do something with the result here
//        // Log.v("tag", rawResult.getText()); // Prints scan results
//        // Log.v("tag", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
//
//        // ticket details activity by passing barcode
//
//        if (rawResult != null) {
//            // when coming for scan repair item
//            if (comingFrom.equalsIgnoreCase("fromRepair")) {
//                Intent i = new Intent(ScanActivity.this, RepairDetailActivity.class);
//                i.putExtra("repair_id", Integer.parseInt(String.valueOf(rawResult)));
//                startActivity(i);
//
//            }
//            //when scan to search product
//            else {
//                ActivitySearchItemList.strScannedBarCode = String.valueOf(rawResult);
//            }
//            MediaPlayer mp;
//            mp = MediaPlayer.create(getApplicationContext(), R.raw.zxing_beep);
//            mp.start();
//
//        } else {
//            Toast.makeText(this, "Invalid Product ", Toast.LENGTH_LONG).show();
//        }
//        finish();
//
//
////        Toast.makeText(ScanActivity.this,""+rawResult,Toast.LENGTH_SHORT).show();
//////        MainActivity.tvresult.setText(rawResult.getText());
//////        onBackPressed();
////        mScannerView.resumeCameraPreview(this);
//        // If you would like to resume scanning, call this method below:
//        //mScannerView.resumeCameraPreview(this);
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        finish();
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_PERMISSIONS_REQUESTS) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//
//                AppConstant.showToast(ScanActivity.this, "Camera Permission Is Required To Use This Feature");
//                finish();
//
//            }
//        }
//    }
//}
//



/*BarcodeReader.BarcodeReaderListener {
    BarcodeReader barcodeReader;
    public  String comingFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the barcode reader instance

        comingFrom=getIntent().getStringExtra("comingFrom");
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);

    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();

        // ticket details activity by passing barcode
        if(barcode.displayValue !=null) {

            // when coming for scan repair item
            if(comingFrom.equalsIgnoreCase("fromRepair")){
                Intent i=new Intent(ScanActivity.this, RepairDetailActivity.class);
                i.putExtra("repair_id",Integer.parseInt(barcode.displayValue));
                startActivity(i);
                finish();
            }
            //when scan to search product
            else{
                ActivitySearchItemList.strScannedBarCode = barcode.displayValue;
                finish();
              }
            }

        else{
            Toast.makeText(this,"Invalid Product ",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Toast.makeText(this, "Denied3", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        Toast.makeText(this, "Denied2", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScanError(String errorMessage) {
        Toast.makeText(this, "Denied1", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}*/
