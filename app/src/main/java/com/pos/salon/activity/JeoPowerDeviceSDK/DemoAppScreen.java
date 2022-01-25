package com.pos.salon.activity.JeoPowerDeviceSDK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pos.salon.R;
import com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan.CaptureActivity;
import com.pos.salon.activity.JeoPowerDeviceSDK.Printer.ThermalActivityPrinter;

public class DemoAppScreen extends AppCompatActivity {

    TextView txt_Print,txt_scan_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_app_screen);

        txt_Print=findViewById(R.id.txt_Print);
        txt_scan_camera=findViewById(R.id.txt_scan_camera);

        txt_Print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DemoAppScreen.this, ThermalActivityPrinter.class);
                startActivity(i);
            }
        });
        txt_scan_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DemoAppScreen.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(DemoAppScreen.this, new String[] {Manifest.permission.CAMERA}, 123);
                }else{
                    Intent i=new Intent(DemoAppScreen.this, CaptureActivity.class);
                    startActivity(i);
                }


            }
        });

    }
}