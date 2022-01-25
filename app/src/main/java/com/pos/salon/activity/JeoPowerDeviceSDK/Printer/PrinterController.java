package com.pos.salon.activity.JeoPowerDeviceSDK.Printer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

public class PrinterController {
    private static final String TAG = PrinterController.class.getSimpleName();

    private static final int PrinterStatus_Null = -1;
    private static final int PrinterStatus_Normal = 0;
    private static final int PrinterStatus_CacheNoEmpty = 1;
    private static final int PrinterStatus_Fault_PE = 4;  //压轴开或缺纸状态
    private static final int PrinterStatus_Fault_Overheat = 8;  //过热

    private static PrinterController printerController = null;
    private SerialPort mSerialPort;;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private int language = 0;
    int mun1;
    private static Context context;
    private boolean pintimage = true;
    // 获取打印机状态指令 0x1c 0x73 x00, 0x1c 0x73 x01,0x1c 0x73 x02
    private static final byte[] Set_Type = new byte[] {
            0x10, 0x04, 0x05
    };
    private static final byte[] STATUS_SET = new byte[] {
            0x1D, 0x61, 0x2F
    };
    // 自检：打印机自检功能
    private static final byte[] PRINTE_TEST = new byte[] {
            0x1D, 0x28, 0x41
    };
    // 裁纸指令
    private static final byte[] PRINTE_CUT = new byte[] {
            0x1D, 0x56, 0x42, 0x00
    };
    // 走纸
    private static final byte[] Take_The_Paper = new byte[] {
            0x1B, 0x4A, 0x40
    };
    // 换行
    private static final byte Line_feed = 0x0A;
    // 设置打印的灰度（打印的字符颜色深浅 分 8 个等级 1 ~ 8，"1"为最浅，"8"为最深）
    private static final byte[] Gray = new byte[] {
            0x1B, 0x6D, 0x08
    };
    // 正常模式
    private static final byte[] Font_Normal_mode = new byte[] {
            0x1B, 0x21, 0x00
    };
    // 斜体
    private static final byte[] Font_Italics = new byte[] {
            0x1B, 0x21, 0x02
    };
    // 加粗
    private static final byte[] Font_Bold = new byte[] {
            0x1B, 0x21, 0x08
    };
    // 倍宽
    private static final byte[] Font_Double_width = new byte[] {
            0x1B, 0x21, 0x20
    };
    // 倍高
    private static final byte[] Font_Times = new byte[] {
            0x1B, 0x21, 0x10
    };
    // 下划线
    private static final byte[] Font_Underline = new byte[] {
            0x1B, 0x21, (byte) 0x80
    };
    // 靠右
    private static final byte[] Set_Right = new byte[] {
            0x1B, 0x61, 0x02
    };
    // 靠左
    private static final byte[] Set_Left = new byte[] {
            0x1B, 0x61, 0x00
    };
    // 居中
    private static final byte[] Set_Center = new byte[] {
            0x1B, 0x61, 0x01
    };
    // 复位打印机命令
    private static final byte[] PRINTE_RESET = new byte[] {
            0x1B, 0x40
    };

    private static final byte[] lSpeed = new byte[] {
            0x1c, 0x73, 0x00
    };// 低速
    private static final byte[] hSpeed = new byte[] {
            0x1c, 0x73, 0x02
    };// 高速
    private static final byte[] mSpeed = new byte[] {
            0x1c, 0x73, 0x01
    };// 高速

    private String IR_PWR_EN = "/proc/jbcommon/gpio_control/Printer_CTL";// 1开，0关
    private String IO_OE = "/proc/jbcommon/gpio_control/UART3_EN"; // 默认值：1，其他值无效
    private String IO_CS0 = "/proc/jbcommon/gpio_control/UART3_SEL0";// 默认值：1，其他值无效
    private String IO_CS1 = "/proc/jbcommon/gpio_control/UART3_SEL1";// 默认值：1，其他值无效
    private int printStatus = 0;
    boolean firstConnect = false;
    private Handler connectDelayHandler = new Handler();
    private List<PrintInfo> printList = new ArrayList<PrintInfo>();
    private PrintThread printThread = null;
    private ReadThread readThread = null;
//    String version = MachineVersion.getMachineVersion().substring(0, 7);

    private static String  printerMCUVersion = "no Version info";
    public static PrinterController getInstance(Context contexts) {
        context = contexts;
        if (null == printerController) {
            printerController = new PrinterController();
        }
        return printerController;

    }

    private class PrintInfo{
        int type;
        byte[] datas;
    }

    private void AddPrintList(int type ,byte[] datas)
    {
        PrintInfo mPrintInfo = new  PrintInfo();
        mPrintInfo.type = type;
        mPrintInfo.datas = datas;
        printList.add(mPrintInfo);
    }

    private void SetPrintList(int index,int type ,byte[] datas) {
        PrintInfo mPrintInfo = new PrintInfo();
        mPrintInfo.type = type;
        mPrintInfo.datas = datas;

        if(printList.size()>0)
            printList.set(index, mPrintInfo);
    }


    public int PrinterController_Open() {
        try {
            writeFile(new File(IR_PWR_EN), "1");
            writeFile(new File(IO_OE), "0");
            writeFile(new File(IO_CS0), "0");
            writeFile(new File(IO_CS1), "1");
            Thread.sleep(100);
            try{
                mSerialPort = new SerialPort(new File("/dev/ttyS3"), 115200, 8, '0', 1, 0, 0);
            }catch (Exception e){
                Log.e("Exception",e.toString());
            }

            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            connectDelayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (readThread == null) {
                        readThread = new ReadThread();
                        readThread.start();
                    }
                    if (printThread == null) {
                        printThread = new PrintThread();
                        printThread.start();

                    }
                }
            }, 2000);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    private class ReadThread extends Thread {
        private boolean isThreadRun = true;

        @Override
        public void run() {
            byte[] readBytes = new byte[512];
            int cmdStatus = 0;
            int cmdLen = 0;
            int cmdReadCnt = 0;
            while (isThreadRun) {
                try {

                    int size = 0;
                    int coutq = 0;
                    int ReadCnt = 0;
                    if(mInputStream == null)
                        continue;

                    coutq = mInputStream.available();
                    byte[] buffer = new byte[coutq];
                    size = mInputStream.read(buffer);


                    if (size > 0) {
//                    	Log.e("jiebao", "size" + size + " coutq " + coutq);
//
//                        Log.e("jiebao", "buffer " + bytesToHexString(buffer, 0, size));
                        for(int i=0;i<size;i++) {

                            switch(cmdStatus) {
                                case 0:
                                    switch(buffer[i]&0xff) {
                                        case 0x01:
                                            cmdStatus = 1;
                                            cmdLen = 1; //指令数据区长度
                                            cmdReadCnt = 0;
                                            readBytes[cmdReadCnt++] = buffer[i];
                                            break;
                                        case 0x02:
                                            cmdStatus = 1;
                                            cmdLen = 1;//指令数据区长度
                                            cmdReadCnt = 0;
                                            readBytes[cmdReadCnt++] = buffer[i];
                                            break;
                                        case 0x11:
                                            cmdStatus = 0;
                                            readBytes[cmdReadCnt++] = buffer[i];
                                            cmdProcess(readBytes,cmdReadCnt);
                                            cmdReadCnt = 0;
                                            break;
                                        case 0x13:
                                            cmdStatus = 0;
                                            readBytes[cmdReadCnt++] = buffer[i];
                                            cmdProcess(readBytes,cmdReadCnt);
                                            cmdReadCnt = 0;
                                            break;
                                        default:;
                                    }
                                    break;
                                case 1:
                                    readBytes[cmdReadCnt++] = buffer[i];

                                    if(cmdReadCnt == 2) {
                                        switch(readBytes[0]) {
                                            case 0x02:	 // 修正 0x02指令长度信息
                                                cmdLen = readBytes[1]+1;
                                                break;
                                            default:;
                                        }
                                    }

                                    if(cmdReadCnt==(cmdLen+1))
                                        cmdStatus = 2;
                                    else if(cmdReadCnt>(cmdLen+1))
                                        cmdStatus = 0;

                                    break;
                                case 2:
                                    readBytes[cmdReadCnt] = buffer[i];
                                    byte  checkNum = 0;
                                    for(int j = 0; j < cmdReadCnt ; j++) {
                                        checkNum ^= readBytes[j]&0xff;
                                    }

                                    if(checkNum == (readBytes[cmdReadCnt]&0xff))
                                    {
                                        cmdReadCnt++;
                                        cmdProcess(readBytes,cmdReadCnt);

                                    }else {

                                    }
                                    cmdStatus = 0;
                                    cmdReadCnt = 0;
                                    break;
                                default:
                                    cmdStatus = 0;
                                    cmdReadCnt = 0;
                                    break;

                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int printStatusChange = 0;
    private int flowCtrlStatus = 0;  // 0:接收缓存空闲，1：接收缓存快满
    private Handler flowCtrlHandeler = new Handler();
    private Runnable flowCtrlRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO 自动生成的方法存根
            flowCtrlStatus = 0;
        }
    };
    private void cmdProcess(byte[] datas, int len) {

        // 	Log.d("1kaka","cmdProcess "+byte2hexString(datas,len ));
        switch((datas[0])&0xff) {
            case 0x01:
                printStatus = datas[1];
                if(firstConnect == false) {
                    firstConnect = true;
                    sendDataToGetPrinterVersion();
                }
                if((printStatus&0xf8) != printStatusChange)
                {
                    printStatusChange = printStatus&0xf8;
                    PrinterController_PrinterStatusChangeCallback();
                }
                if((printStatus&0x07)<2)
                    flowCtrlStatus = 0;
                break;
            case 0x02:
                byte[] ver = new byte[datas[1]];
                System.arraycopy(datas, 2, ver, 0, datas[1]);
                printerMCUVersion = new String(ver);
                Log.d("kaka","printerMCUVersion "+printerMCUVersion);

                break;
            case 0x11:
                flowCtrlStatus = 0;
//    		Log.d("kaka","uart 0x11");

                break;
            case 0x13:
                flowCtrlStatus = 1;
                flowCtrlHandeler.removeCallbacks(flowCtrlRunnable);
                flowCtrlHandeler.postDelayed(flowCtrlRunnable, 3000);
//    		Log.d("kaka","uart 0x13");

                break;
            default:;
        }
    }

    private static void writeFile(File file, String value) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(value);
            writer.flush();
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 功能：关闭设备
     *
     * @return 0:成功，-1:失败
     */
    public int PrinterController_Close() {
        writeFile(new File(IR_PWR_EN), "0");
        writeFile(new File(IO_CS1), "0");
        flowCtrlStatus = 0;

        if(readThread != null) {
            readThread.isThreadRun = false;
            readThread = null;
        }

        if(printThread != null) {
            printThread.isThreadRun = false;
            printThread = null;
        }
        if(printList!=null)
            printList.clear();

        try {
            if(mOutputStream!=null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if(mInputStream!=null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
                return 0;
            }else
                return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 功能：复位设备
     *
     * @return 0:成功，-1:失败
     */
    public int PrinterController_reset() {
        return Write_Command(PRINTE_RESET);
    }

    /**
     * 功能：获取打印机mcu版本信息
     *
     * @return 0:版本描述
     */
    public String PrinterController_GetVersion() {

        return printerMCUVersion;
    }

    /**
     * 功能：发送打印机指令
     *
     * @param command 打印机指令
     * @return 0：成功，-1：失败
     */
    public int Write_Command(byte[] command) {
//        try {
        if (mOutputStream == null) {
            return -1;
        }

        if (null != command) {
            AddPrintList(0,command);
//                mOutputStream.write(command);
//                mOutputStream.flush();
//            LogUtil.d("RS232Controller", "Rs232_Write:" + bytesToHexString(command, 0, command.length));
            return 0;
        } else {
            return -1;
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        }
    }


    private String bytesToHexString(byte[] src, int start, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = start; i < size; i++) {
            int v = src[i] & 0xFF;

            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public int Write_Command(byte command) {
//        try {
        if (mOutputStream == null) {
            return -1;
        }
        byte[] buf = new byte[1];
        buf[0] = command;
        AddPrintList(0,buf);
//            mOutputStream.write(command);
//            mOutputStream.flush();
        return 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        }
    }

    /**
     * 功能：换行
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Linefeed() {

        if ((0 == language)||(2 == language)) {
            AddPrintList(0,"\n".getBytes());
            return 0;
        }else
            return Write_Command(Line_feed);
    }

    private void sendDataToGetPrinterVersion()
    {
//    	if(mOutputStream == null) {
//            return;
//        }
//    	byte datas[] = {0x02,0x03,0x01};  //获取mcu版本描述指令
//        printList.add(datas);
//        Log.d("kaka", "sendDataToGetPrinterVersion "+byte2hexString(datas, datas.length));
    }

    private void sendDataToPrinterGB1312(final byte data[]) {
        if(data == null || data.length <= 0 || mOutputStream == null) {
            return;
        }

        Log.e("jiebao", "sendDataToPrinterGB1312 data length " + data.length);
        AddPrintList(1,data);



//        if(data.length <= 1024) {
//            try {
//                mOutputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//	        new Thread(new Runnable() {
//	            @Override
//	            public void run() {
//                    int index = 0;
//                    final int totalLen = data.length;
//                    while(true){
//                        if(index >= totalLen) {
//                            break;
//                        }
//
//                        byte bytes[] = null;
//                        if(totalLen - index >= 1024) {
//                            bytes = new byte[1024];
//                            System.arraycopy(data,index, bytes,0,1024);
//                            index += 1024;
//                        } else {
//                            int len = totalLen - index;
//                            bytes = new byte[len];
//                            System.arraycopy(data,index, bytes,0, len);
//                            index += len;
//                        }
//
//                        try {
//                            mOutputStream.write(bytes);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        Log.d("jiebao", "sendDataToPrinterGB1312 Time " + System.currentTimeMillis());
//                        try {
//                            Thread.sleep(1800L);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//	                }
//	            }
//
//	        }).start();
//        }
    }

    private void sendDataToPrinterPersianArabic(final String str) {
        if(str == null || str.length() <= 0 || mOutputStream == null) {
            return;
        }

        Log.e("jiebao", "sendDataToPrinterPersianArabic str length " + str.length());

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList strs16 = str16(str);

                for (int j = 0; j < strs16.size(); j++) {
                    int a = (Integer) strs16.get(j);
                    if (10 == a) {
                        PrinterController_Linefeed();
                    } else {
                        try {
                            mOutputStream.write(a);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if((j % 1024 == 0) && (j != 0)) {
                        try {
                            Thread.sleep(1800L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("jiebao", "sendDataToPrinterPersianArabic Time " + System.currentTimeMillis());
                    }
                }
            }
        }).start();
    }

    private void sendDataToPrinterUnicode(final String str) {
        if(str == null || str.length() <= 0 || mOutputStream == null) {
            return;
        }

        String[] strs = new String[str.length()];
        for (int i = 0; i < str.length(); i++) {
            strs[i] = str.substring(i, i + 1);
        }

        byte[] strArray = new byte[strs.length*2];
        int cnt=0;
        for (int j = 0; j < strs.length; j++) {

            if ("\n".equals(strs[j])) {
                strArray[cnt++] = Line_feed;

            } else {

//				byte[] a = new byte[0];
                try {
//					a = new byte[] {
//							strs[j].getBytes("unicode")[3],
//							strs[j].getBytes("unicode")[2] };
                    strArray[cnt++] = strs[j].getBytes("unicode")[3];
                    strArray[cnt++] = strs[j].getBytes("unicode")[2];
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        byte[] bytes = new byte[cnt];
        System.arraycopy(strArray,0, bytes,0, cnt);
        AddPrintList(1,bytes);

        Log.e("jiebao", "sendDataToPrinterUnicode str length " + str.length());

    }


    public String byte2hexString(byte[] b,int length) {
        String str = "";
        if(b.length<length)
            length = b.length;

        for (int n = 0; n < length; n++) {
            str += String.format("%02X ", b[n]);
        }

        return str;
    }




    private int sendDatas(byte[] datas)
    {
        try {
            //Log.d("kaka", "flowCtrlStatus:"+flowCtrlStatus+"out" + byte2hexString(datas, datas.length));

            while(flowCtrlStatus>0)
            {

            }

            if(mOutputStream!=null) {
                mOutputStream.write(datas);
                mOutputStream.flush();
            }

            return 0;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
    int printTimerCnt = 0;

    private class PrintThread extends Thread {
        private boolean isThreadRun = true;

        public void run() {
            int cnt = 0;
            int printerStatus = 0;
            int sendOffset = 700;

            while (isThreadRun) {
                printerStatus = PrinterController_PrinterStatus();
//					if(printerStatus == 4)// 发现缺纸等待5S
//					{
//						try {
////							Log.d("kaka","printerStatus == 4");
//							Thread.sleep(5000L);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						continue;
//
//					}
                if ((printList.size() > 0) && (printerStatus == 0)) {
                    PrintInfo mPrintInfo = printList.get(0);
                    byte[] bytes_tmp = mPrintInfo.datas;

                    int writeLoop = 0;
                    int offset = 0;
                    int delayTime = 50;
                    if (mPrintInfo.type == 1) {
                        sendOffset = 400;
                        delayTime = 420;
                    } else {
                        sendOffset = 600;
                        delayTime = 55;
                    }
                    writeLoop = bytes_tmp.length / sendOffset;
                    for (int i = 0; i < writeLoop; i++) {
                        //	Log.d("jiebao",
                        //			"writeLoop:" + writeLoop + "/" + i + " printList: " +printList.size());
                        if (isThreadRun == false) {
//								Log.d("2kaka",
//										"isThreadRun == false " + writeLoop + "/" + i );

                            return;
                        }

                        if ((printStatus & 0x00007) > 3) {

                            try {
                                Thread.sleep(320L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        printerStatus = PrinterController_PrinterStatus();
                        if (printerStatus != 0) {
//								if(printList.size()>0)
//									printList.remove(0);
                            byte[] bytes_staging = new byte[(bytes_tmp.length - offset)];
                            System.arraycopy(bytes_tmp, offset, bytes_staging, 0, (bytes_tmp.length - offset));

                            SetPrintList(0, mPrintInfo.type, bytes_staging);

                            break;
                        }

                        byte buf[] = new byte[sendOffset];
                        System.arraycopy(bytes_tmp, offset, buf, 0, sendOffset);

                        sendDatas(buf);

                        offset += sendOffset;

                        try {
                            Thread.sleep(delayTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int length = bytes_tmp.length % sendOffset;
                    if (printerStatus == 0 && length > 0) {

                        byte buf[] = new byte[length];
                        System.arraycopy(bytes_tmp, offset, buf, 0, length);

                        sendDatas(buf);

                        if (printList.size() > 0)
                            printList.remove(0);
                    }

                    try {
                        Thread.sleep(1 + 85 * length / sendOffset);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    /**
     * 功能：打印字符
     *
     * @param bytes 字符数据
     * @return 0：成功，-1：失败
     */
    @SuppressWarnings("rawtypes")
    public int PrinterController_Print(byte[] bytes) {

//        for (int i = 0; i < bytes.length; i++) {
//            System.out.println(bytes[i]);
//        }

//    	int status = PrinterController_PrinterStatus();
//		if ((status != 0)&&(status != 1)) {
//			return -1;
//		}


        if (mOutputStream == null) {
            return -1;
        }

        try {
            String str = new String(bytes);
            Log.e("jiebao", "language " + language);

            if (0 == language) {
                sendDataToPrinterUnicode(str);
                return 0;
            } else if (1 == language) {
                sendDataToPrinterPersianArabic(str);
                return 0;
            } else if (2 == language) {
                sendDataToPrinterGB1312(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    /**
     * 功能：打印 Bitmap
     *
     * @param bmp 把需要打印的图片转换成 Bitmap
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Bitmap(Bitmap bmp) {
//        try {
        if (bmp != null) {
            byte[] command = decodeBitmap(bmp);
            AddPrintList(0,command);
//                mOutputStream.write(command);
            return 0;
        } else {
            return -1;
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        }
    }

    /**
     * 功能：打印图片与二维码 （图片（二维码）必须放在assets目录，目前只支持该目录图片（二维码）的打印）
     *
     * @param ICname ：只传图片名称即可
     * @return 0：成功，-1：失败
     */
    public int PrinterController_ImageAddCode(String ICname) {
        AssetManager asm = context.getResources().getAssets();
        InputStream is;
        try {
            is = asm.open(ICname);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            is.close();
            if (bmp != null) {
                byte[] command = decodeBitmap(bmp);
//                LogUtil.d("PrinterController", "command-----:" + bytesToHexString(command));
//                mOutputStream.write(command);

                AddPrintList(0,command);
                return 0;
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    public void printController_setStatus() {
//        LogUtil.d(TAG, "printController_setStatus::+++");

        Write_Command(STATUS_SET);
    }

    /**
     * 功能：获取打印机状态
     *
     * @return -1：设备没有打开，0：打印机正常，4：压轴开和缺纸,   5: 过热
     */
    public int PrinterController_PrinterStatus() {
//        LogUtil.d(TAG, "PrinterController_PrinterStatus::+++");


        if (mInputStream == null) {
//            LogUtil.d(TAG, "PrinterController_PrinterStatus::mInputString is null...");
            return -1;
        }
        if((printStatus & 0x10)==0x10) {
            return 4;
        }else if((printStatus & 0x20)==0x20) {
            return 5;
        }else  if((printStatus & 0x07)<3)
        {
            return 0;
        }

        return 1;
    }

    /**
     * 功能：打印机状态变化回调
     *
     * @return
     */
    private void PrinterController_PrinterStatusChangeCallback() {
        int status = PrinterController_PrinterStatus();

        switch (status) {
            case PrinterStatus_Null:
                mCallback.statusCallback(PrinterStatus_Null);
                break;
            case PrinterStatus_Normal:
            case PrinterStatus_CacheNoEmpty:
                mCallback.statusCallback(PrinterStatus_Normal);
                break;
            case PrinterStatus_Fault_PE:
                mCallback.statusCallback(PrinterStatus_Fault_PE);
                break;
            case PrinterStatus_Fault_Overheat:
                mCallback.statusCallback(PrinterStatus_Fault_Overheat);
                break;
            default:
                ;
        }


    }
    /**
     * 功能：设置语言
     *
     * @param language 语言
     * @return 0：成功，-1：失败
     */
    public int PrinterController_PrinterLanguage(int language) {
        this.language = language;
        return 0;
    }

    /**
     * 功能：打印机自检
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_PrintText() {
        int tf = Write_Command(PRINTE_TEST);
        return tf;
    }

    /**
     * 功能：打印机自检
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Cut() {
        int tf = Write_Command(PRINTE_CUT);
        return tf;
    }

    /**
     * 功能：走纸
     *
     * @param l 走几行纸
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Take_The_Paper(int l) {
        if (l < 1) {
            return -1;
        }
        int tf = -1;
        for (int i = 0; i < l; i++) {
            tf = Write_Command(Take_The_Paper);
        }
        return tf;
    }

    /**
     * 功能：设置打印的灰度（打印的字符颜色深浅 分 8 个等级 1 ~ 8，"1"为最浅，"8"为最深）
     *
     * @param i 灰度级别
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Gray(int i) {
        Gray[2] = (byte) i;
        int tf = Write_Command(Gray);
        return tf;
    }

    /**
     * 功能：设置字体正常模式
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Font_Normal_mode() {
        int tf = Write_Command(Font_Normal_mode);
        return tf;
    }

    /**
     * 功能：设置字体斜体模式
     *
     * @return 0：成功，-1：失败
     */
    // public int PrinterController_Font_Italics() {
    // int tf = Write_Command(Font_Italics);
    // return tf;
    // }

    /**
     * 功能：设置字体加粗模式
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Font_Bold() {
        int tf = Write_Command(Font_Bold);
        return tf;
    }

    /**
     * 功能：设置字体倍宽模式
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Font_Double_width() {
        int tf = Write_Command(Font_Double_width);
        return tf;
    }

    /**
     * 功能：设置字体倍高模式
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Font_Times() {
        int tf = Write_Command(Font_Times);
        return tf;
    }

    /**
     * 功能：设置字体带下划线模式
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Font_Underline() {
        int tf = Write_Command(Font_Underline);
        return tf;
    }

    /**
     * 功能：设置打印起始位置为右边
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Set_Right() {
        int tf = Write_Command(Set_Right);
        return tf;
    }

    /**
     * 功能：设置打印起始位置为左边
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Set_Left() {
        int tf = Write_Command(Set_Left);
        return tf;
    }

    /**
     * 功能：设置打印内容居中
     *
     * @return 0：成功，-1：失败
     */
    public int PrinterController_Set_Center() {
        int tf = Write_Command(Set_Center);
        return tf;
    }

    /*
     * 设置打印低速
     *  @return 0：成功，-1：失败
     */
    public int PrinterController_Set_lowSpeed() {
        int tf = Write_Command(lSpeed);
        return tf;
    }

    /*
     * 设置打印高速
     *  @return 0：成功，-1：失败
     */
    public int PrinterController_Set_highSpeed() {
        int tf = Write_Command(hSpeed);
        return tf;
    }

    /*
     * 设置打印中速
     *  @return 0：成功，-1：失败
     */
    public int PrinterController_Set_midSpeed() {
        int tf = Write_Command(mSpeed);
        return tf;
    }

    // 《=====阿拉伯字符与波斯字符的处理
    @SuppressWarnings({
            "rawtypes", "unused", "unchecked"
    })
    private ArrayList str16(String s) {

        int str16s[] = new int[s.length() + 1];
        int str16sb[] = new int[s.length() + 1];

        ArrayList albstr = new ArrayList();
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            str16s[i] = ch;

        }
        BS bs = new BS();
        boolean zj = false;
        int c = bs.IsIncludeArbic(str16s);
        if (c == 1) {
            zj = true;
        }
        if (zj) {
            bs.Arbic_Convert(str16s, str16sb);
        } else {
            str16sb = str16s;
        }
        ArrayList<Integer> sgb1 = new ArrayList<Integer>();
        ArrayList<Integer> sgb2 = new ArrayList<Integer>();
        for (int i = 0; i < str16sb.length; i++) {
            if (i == str16sb.length - 1) {
                for (int i1 = sgb1.size(); i1 > 0; i1--) {
                    sgb2.add(sgb1.get(i1 - 1));
                }
            }
            if (str16sb[i] != 10) {
                sgb1.add(str16sb[i]);
            } else {
                for (int c1 = sgb1.size(); c1 > 0; c1--) {
                    sgb2.add(sgb1.get(c1 - 1));
                }
                sgb1.clear();
                sgb2.add(10);
            }
        }
        for (int i = 0; i < sgb2.size(); i++) {
            if (sgb2.get(i) == 10) {
                albstr.add(sgb2.get(i));
            } else {
                int b = sgb2.get(i) / 256;
                albstr.add(b);
                int d = sgb2.get(i) % 256;
                albstr.add(d);
            }
        }
        return albstr;
    }

    @SuppressWarnings("unused")
    private byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); // binaryString list
        StringBuffer sb;

        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    /**
     * 指令list转换为byte[]指令
     */
    private byte[] hexList2Byte(List<String> list) {

        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    /** Convert hexString to bytes(可用) */
    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 系统提供的数组拷贝方法arraycopy
     */
    private static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    /** 二进制List<String>转为HexString */
    private List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                // 转成16进制
                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    private String hexStr = "0123456789ABCDEF";
    private String[] binaryArray = {
            "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"
    };

    private String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public boolean printBitmap(Bitmap bmp) {
        try {
            if (bmp != null) {
                int e = bmp.getWidth();
                int height = bmp.getHeight();
                int w = e + 8 - e % 8;
                int h = w * height / e;
                Bitmap bitmap = this.scaleBitmap(bmp, w, h);
                byte[] head = new byte[] {
                        (byte) 29, (byte) 118, (byte) 48, (byte) 48,
                        (byte) (w / 8 >> 0), (byte) (w / 8 >> 8),
                        (byte) (h >> 0), (byte) (h >> 8)
                };
                byte[] data = this.decodeBitmap(bitmap);
                byte[] command = new byte[head.length + data.length];
                System.arraycopy(head, 0, command, 0, head.length);
                System.arraycopy(data, 0, command, head.length, data.length);
                Thread.sleep(1000L);
                AddPrintList(0, command);

                return true;
            } else {
                return false;
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            return false;
        }
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        } else {
            int height = origin.getHeight();
            int width = origin.getWidth();
            float scaleWidth = (float) newWidth / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height,
                    matrix, false);
            if (!origin.isRecycled()) {
                origin.recycle();
            }

            return newBM;
        }
    }

    public interface StatusCallback {
        void statusCallback(int value);
    }

    public void setStatusCallback(StatusCallback callback) {
        this.mCallback = callback;
    }

    private StatusCallback mCallback;
}
