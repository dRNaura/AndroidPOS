package com.pos.salon.model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.pos.salon.R;
import com.pos.salon.utilConstant.AppConstant;

import java.util.Set;

public class BTDevicesList extends AppCompatActivity {

    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_devices);

        toolbar = (Toolbar) findViewById(R.id.toolbar_close_register);
        setSupportActionBar(toolbar);

        setBackNavgation();

        setResult(Activity.RESULT_CANCELED);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.bluetooth_devices_item);

        ListView mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.GONE);
            for (BluetoothDevice mDevice : mPairedDevices) {
                BluetoothClass bluetoothClass = mDevice.getBluetoothClass();
//                if(bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
//                    // allow user to select this device. I'm not sure exactly which type
//                    // headphones will be but this is a good guess. You can connect to
//                    // your Bluetooth headset to find out for sure.
//                }
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {

            try {

                mBluetoothAdapter.cancelDiscovery();
                String mDeviceInfo = ((TextView) mView).getText().toString();
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                Log.e(TAG, "Device_Address " + mDeviceAddress);

                Bundle mBundle = new Bundle();
                mBundle.putString("DeviceAddress", mDeviceAddress);
                Intent mBackIntent = new Intent();
                mBackIntent.putExtras(mBundle);
                setResult(Activity.RESULT_OK, mBackIntent);
                finish();
            } catch (Exception ex) {
                AppConstant.showToast(getApplicationContext(),""+ex.toString());
            }
        }
    };

    private void setBackNavgation() {
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Bluetooth Devices");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent i = new Intent(CloseRegister.this, HomeActivity.class);
//                    startActivity(i);
                    finish();
                }
            });
        }

    }


}
