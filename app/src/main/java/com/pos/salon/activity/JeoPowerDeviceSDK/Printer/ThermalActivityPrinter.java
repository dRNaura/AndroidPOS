package com.pos.salon.activity.JeoPowerDeviceSDK.Printer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.pos.salon.R;
import java.io.UnsupportedEncodingException;

public class ThermalActivityPrinter extends AppCompatActivity {

    TextView txt_print;
    private PrinterController mPrinterController = null;
    int flag,Language=0;
    String data="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermal_printer);

        txt_print = findViewById(R.id.txt_print);

         data = "";

        data = " ----- " + "test" + " Copy ----\n" +
                "\n " +
                " " + "address" + "\n\n" +
                " " + "Mobile :" + "+91234567890" + "\n\n" +
                " \n\n";
        data = data
                + " " + "Invoice No : " + "1234" + " \n\n"
                + " " + "Customer  : " + "test" + " \n\n"
                + " " + "Date  : " + "16 Apr" + " , " + "2021" + " \n\n"

                + "--------------------------------\n";
        //set currency type from receipt object.
        String strCurrencyType = "";
        //end.

        data = data + "            Product           \n"
                + "--------------------------------";

        data = data + "\n" + "product Name";

        data = data + "\n\n" + "Quantity  :  " + "1" + " Pc(s)";
        data = data + "\n\n" + "Unit Price  :  " + "20";


        data = data + "\n\n" + "Subtotal  :   " + "20";

        data = data + "\n\n" + "-------------------------------";


        data = data + "\n" + "Subtotal :" + "25";

        data = data + "\n\n" + "-------------------------------";
        data = data + "\n\n" + "Grand Total   :  " + strCurrencyType + " " + "30";
        data = data + "\n\n" + "-------------------------------";
        data = data + "\n\n" + "Paid By   :";
        data = data + "\n\n" + "CASH   : 30";


        data = data + "\n\n\n";
        data = data + "     ***** THANK YOU *****";
        data = data + "\n\n\n\n\n ";

        Toast.makeText(this, " " + data, Toast.LENGTH_SHORT).show();

        if (null == mPrinterController) {
            mPrinterController = PrinterController.getInstance(ThermalActivityPrinter.this);
        }


        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

//        Toast.makeText(this, "battery " + battery, Toast.LENGTH_SHORT).show();

        flag = mPrinterController.PrinterController_Open();

        if (flag == 0) {
//            settrue();
//            if (battery >= 30) {
                Toast.makeText(this, "connect_Success", Toast.LENGTH_SHORT).show();
//            }
        } else {
            Toast.makeText(this, "connect_Failure", Toast.LENGTH_SHORT).show();
        }
        txt_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPrinterController.PrinterController_PrinterLanguage(Language);
                mPrinterController.PrinterController_Take_The_Paper(1);
                print();
                mPrinterController.PrinterController_Take_The_Paper(2);
//                if (mCut) {
//                    cut();
//                }
                Toast.makeText(ThermalActivityPrinter.this, "Print", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void print() {
//        String data = et_print_text.getText().toString();


        if (Language == 2) {
            try {
                mPrinterController.PrinterController_Print(data.getBytes("GB2312"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mPrinterController.PrinterController_Print(data.getBytes());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (null != mPrinterController)
            mPrinterController.PrinterController_Close();
    }
}