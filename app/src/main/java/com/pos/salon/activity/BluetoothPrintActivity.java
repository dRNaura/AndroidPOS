package com.pos.salon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.pos.salon.R;
import com.pos.salon.model.BTDevicesList;
import com.pos.salon.model.UnicodeFormatter;
import com.pos.salon.utilConstant.AppConstant;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class BluetoothPrintActivity extends AppCompatActivity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    Toolbar toolbar;
    String bill_receipt="";
    boolean isConnected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_print2);

        toolbar = (Toolbar) findViewById(R.id.toolbar_close_register);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        bill_receipt=getIntent().getStringExtra("bill_receipt");

        setBackNavgation();

        mScan = (Button) findViewById(R.id.Scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(BluetoothPrintActivity.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(BluetoothPrintActivity.this, BTDevicesList.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        mPrint = (Button) findViewById(R.id.mPrint);
        mPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (isConnected==true) {
                    Thread t = new Thread() {
                        public void run() {
                            try {
                                OutputStream os = mBluetoothSocket.getOutputStream();

                                os.write(bill_receipt.getBytes());
                                //This is printer specific code you can comment ==== > Start

                                // Setting height
                                int gs = 39;
                                os.write(intToByteArray(gs));
                                int h = 114;
                                os.write(intToByteArray(h));
                                int n = 172;
                                os.write(intToByteArray(n));

                                // Setting Width
                                int gs_width = 39;
                                os.write(intToByteArray(gs_width));
                                int w = 129;
                                os.write(intToByteArray(w));
                                int n_width = 2;
                                os.write(intToByteArray(n_width));


                            } catch (Exception e) {
                                AppConstant.showToast(getApplicationContext(), "" + e.toString());
                                Log.e("MainActivity", "Exe ", e);
                            }
                        }
                    };
                    t.start();

                }
                else {
                    AppConstant.showToast(getApplicationContext(), "No Device Connected");
            }
            }
        });

        mDisc = (Button) findViewById(R.id.dis);
        mDisc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                if (mBluetoothAdapter != null){
                    isConnected=false;
                    mBluetoothAdapter.disable();
                }
            }
        });

    }// onCreate

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null){
                mBluetoothSocket.close();
            }

        } catch (Exception e) {
            AppConstant.showToast(getApplicationContext(),""+e.toString());
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null){
                mBluetoothSocket.close();
            }

        } catch (Exception e) {
            AppConstant.showToast(getApplicationContext(),""+e.toString());
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void onActivityResult(int mRequestCode, int mResultCode, Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.e(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BluetoothPrintActivity.this, BTDevicesList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BluetoothPrintActivity.this, "", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.e("", "PairedDevices: " + mDevice.getName() + "  " + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            runOnUiThread(new Runnable() {
                public void run() {
                    isConnected=false;
                    mBluetoothConnectProgressDialog.dismiss();
                    final Toast toast = Toast.makeText(BluetoothPrintActivity.this, "This Is Not A Printer Device" , Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            Log.e(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.e("", "SocketClosed");
        } catch (IOException ex) {
            AppConstant.showToast(BluetoothPrintActivity.this,""+ex.toString());
            Log.e("TAG", "CouldNotCloseSocket");
        }
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                isConnected=true;
//                mBluetoothConnectProgressDialog.dismiss();
//                Toast.makeText(BluetoothPrintActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
         //Device is now connected
                mBluetoothConnectProgressDialog.dismiss();
                isConnected=true;
                Toast.makeText(BluetoothPrintActivity.this, "Device Connected", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
         //Done searching
                Toast.makeText(BluetoothPrintActivity.this, "Done Searching", Toast.LENGTH_LONG).show();
                mBluetoothConnectProgressDialog.dismiss();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
        //Device is about to disconnect
                mBluetoothConnectProgressDialog.dismiss();
                Toast.makeText(BluetoothPrintActivity.this, "Device is about to disconnect", Toast.LENGTH_LONG).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
           //Device has disconnected
                isConnected=false;
                mBluetoothConnectProgressDialog.dismiss();
                Toast.makeText(BluetoothPrintActivity.this, "Device has disconnected", Toast.LENGTH_LONG).show();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isConnected=true;
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BluetoothPrintActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(b[k]));
            Log.e("test","Selva  [" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setTitle("Bluetooth Print");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent i = new Intent(CloseRegister.this, HomeActivity.class);
//                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

                }
            });
        }

    }

}
