package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android_serialport_api.SerialPort;

public class ScanController {
    public static final String TAG = ScanController.class.getSimpleName();
    // 串口地址
    private static final String SERIAL_PORT_PATH = "/dev/ttyS1";
    // 1开，0关
    private static final String SCAN_PWR_EN = "/proc/jbcommon/gpio_control/Scan_CTL";
    // 默认值：1，其他值无效
    private static final String IO_OE = "/proc/jbcommon/gpio_control/UART1_EN";
    // 默认值：1，其他值无效
    private static final String IO_CS0 = "/proc/jbcommon/gpio_control/UART1_SEL0";
    // 默认值：1，其他值无效
    private static final String IO_CS1 = "/proc/jbcommon/gpio_control/UART1_SEL1";

    // 扫描头协议内容
    public final static int Protocol_UNKONW = -1; // 无处理
    // N4313还原出厂设置
    public final static int Protocol_N4313_Default = 0;

    // 自定义N4313前缀1113 后缀1214协议 防止断码误码
    public final static int Protocol_N4313_Protocol = 1;
    // 设置N4313返回 AimId
    public final static int Protocol_N4313_Codeid = 2;
    // SE955还原出厂设置
    public final static int Protocol_SE955_Default = 3;
    // 设置SE955受保护的协议 返回CODEID 防止断码误码
    public final static int Protocol_SE955_Protocol = 4;
    // EM3095还原出厂设置
    public final static int Protocol_EM3095_Default = 5;
    // 设置EM3095受保护的协议 返回CODEID 防止断码误码
    public final static int Protocol_EM3095_Protocol = 6;
    // EM3070还原出厂设置
    public final static int Protocol_EM3070_Default = 7;
    // 设置EM3095受保护的协议 防止断码误码
    public final static int Protocol_EM3070_Protocol = 8;
    // N3680还原出厂设置
    public final static int Protocol_N3680_Default = 9;
    // 自定义N3680前缀Codeid
    public final static int Protocol_N3680_Codeid = 10;
    // 后缀1214协议 防止断码误码

    // 硬解模块信息
    public final static int MODEL_UNKONW = -1;
    public final static int MODEL_N4313 = 0;
    public final static int MODEL_SE955 = 1;
    public final static int MODEL_EM3095 = 2;
    public final static int MODEL_EM3070 = 3;
    public final static int MODEL_N3680 = 4;

    private final int MESSAGE_CHECK_INIT_N4313 = 6777;
    private final int MESSAGE_CHECK_INIT_EM3095 = 6778;
    private final int MESSAGE_CHECK_INIT_EM3070 = 6779;
    private final int MESSAGE_CHECK_INIT_SE955 = 6780;
    private final int MESSAGE_EM3095_CodeDate = 1110;
    private final int MESSAGE_EM3070_CodeDate = 1111;
    private final int MESSAGE_N4313_CodeDate = 1112;

    // 3095协议是否写入
    private boolean init_SE955_protocol = false;
    // 3095协议是否写入
    private boolean init_EM3070_protocol = false;
    // 3095协议是否写入
    private boolean init_EM3095_protocol = false;
    // n4313 CODEID协议写入
    private boolean init_N4313_Code_id = false;
    // n4313 前缀协议写入
    private boolean init_N4313_start = false;
    // n4313 后缀协议写入
    private boolean init_N4313_end = false;
    // n3680 CODEID协议写入
    private boolean init_N3680_Code_id = false;
    // private boolean init_N3680_end = false; // n3680 后缀协议写入

    // 新大陆头恢复出厂设置 新大陆3095返回0x02成功 0x00失败
    private final byte[] newLandPrefix_Default_3095 = new byte[] {
            0x7E, 0x00, 0x08, 0x01, 0x00, (byte) 0xD9, 0x00, (byte) 0xDB,
            (byte) 0x26
    };
    // 新大陆头加前缀 新大陆3095返回0x02成功 0x00失败
    private final byte[] newLandPrefix_Code_id_3095 = new byte[] {
            0x7E, 0x00, 0x08, 0x01, 0x00, (byte) 0x02, (byte) 0x80, 0x00,
            (byte) 0xD2, (byte) 0xEB
    };
    // 新大陆头加后缀TAB 09 新大陆3095返回0x02成功 0x00失败
    private final byte[] newLandPrefix_Tab_3095 = new byte[] {
            0x7E, 0x00, 0x08, 0x01, 0x00, (byte) 0x60, (byte) 0x41, 0x27,
            (byte) 0x56
    };
    // 新大陆头保存设置
    private final byte[] newLandPrefix_Save_3095 = new byte[] {
            0x7E, 0x00, 0x09, 0x01, 0x00, (byte) 0x00, 0x00, (byte) 0xDE,
            (byte) 0xC8
    };

    private final byte[] wakeUp = {
            0x00
    };

    // 迅宝不带协议包指令 原始数据
    private final byte[] pack_code_noProtoc = {
            0x07, (byte) 0xC6, 0x04, 0x00, (byte) 0xFF, (byte) 0xEE, 0x00,
            (byte) 0xFD, 0x42
    };
    // 迅宝协议包指令 带校验
    private final byte[] pack_code_protocol = {
            0x07, (byte) 0xC6, 0x04, 0x00, (byte) 0xFF, (byte) 0xEE, 0x01,
            (byte) 0xFD, 0x41
    };
    // 迅宝应答指令
    private final byte[] host_cmd_ack = {
            0x04, (byte) 0xD0, 0x04, 0x00, (byte) 0xFF, 0x28
    };

    // EM3070恢复出厂设置
    private final String newlandPrefix_Default_3070 = "NLS0006010;NLS0001000";
    // em3070设置前后缀指令 Coid
    private final String newlandPrefix_Protocol_3070 = "NLS0006010;NLS0305010;NLS0300000=0x1113;NLS0308030;NLS0310000=0x1214;NLS0502100;NLS0000160;NLS0006000"; // em3070设置前后缀指令

    // 添加前缀CODEID 返回50 52 45 42 4b 32 39 39 35 63 38 30 06 2e
    private final byte[] N4313Prefix_Code_id = new byte[] {
            0x16, 0x4D, 0x0D, (byte) 0x70, (byte) 0x72, (byte) 0x65,
            (byte) 0x62, (byte) 0x6b, (byte) 0x32, (byte) 0x39, (byte) 0x39,
            (byte) 0x35, (byte) 0x63, (byte) 0x38, (byte) 0x30, (byte) 0x2e
    };
    // 添加前缀1113 返回505245424b32393931313133062e
    private final byte[] N4313Prefix_start = new byte[] {
            0x16, 0x4D, 0x0D, (byte) 0x70, (byte) 0x72, (byte) 0x65,
            (byte) 0x62, (byte) 0x6b, (byte) 0x32, (byte) 0x39, (byte) 0x39,
            (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x33, (byte) 0x2e
    };
    // 添加后缀1214 返回 53 55 46 42 4b 32 39 39 31 32 31 34 06 2e
    private final byte[] N4313Prefix_end = new byte[] {
            0x16, 0x4D, 0x0D, (byte) 0x73, (byte) 0x75, (byte) 0x66,
            (byte) 0x62, (byte) 0x6b, (byte) 0x32, (byte) 0x39, (byte) 0x39,
            (byte) 0x31, (byte) 0x32, (byte) 0x31, (byte) 0x34, (byte) 0x2e
    };
    // 还原出厂设置 444546414c54062e 64 65 66 61 6c 74 2e
    private final byte[] N4313Prefix_default = new byte[] {
            0x16, 0x4D, 0x0D, (byte) 0x64, (byte) 0x65, (byte) 0x66,
            (byte) 0x61, (byte) 0x6c, (byte) 0x74, (byte) 0x2e
    };
    // N4313 CodeId
    private byte[] heads_byte = {
            (byte) 0x61, (byte) 0x68, (byte) 0x6A, (byte) 0x3C, (byte) 0x62,
            (byte) 0x54, (byte) 0x69, (byte) 0x64, (byte) 0x44, (byte) 0x79,
            (byte) 0x7B, (byte) 0x7D, (byte) 0x49, (byte) 0x51, (byte) 0x65,
            (byte) 0x6D, (byte) 0x59, (byte) 0x66, (byte) 0x67, (byte) 0x74,
            (byte) 0x63, (byte) 0x45, (byte) 0x7A, (byte) 0x48, (byte) 0x56,
            (byte) 0x71, (byte) 0x6C, (byte) 0x77, (byte) 0x79, (byte) 0x78,
            (byte) 0x72, (byte) 0x52, (byte) 0x73, (byte) 0x41, (byte) 0x42,
            (byte) 0x43, (byte) 0x51, (byte) 0x2c, (byte) 0x4D, (byte) 0x4A,
            (byte) 0x4B, (byte) 0x3F, (byte) 0x4C, (byte) 0x4E, (byte) 0x50
    };

    // 记录当前版本出光指令
    private String mTrigScan = "0";
    // 保存上次trig状态
    private String mLastTrigStatus = "0";

    private boolean begin;
    private int all_scan_count;// 记录出光次数
    private long scan_time_limit = 200; // 扫描间隔控制
    private long scan_time = 3000;// 扫描间隔时间
    private long mScanPreviousTime;
    private long mScanCurrentTime;

    // 协议没写入 重写次数s
    private int init_index = 0;
    private int middle_index = 0;
    private int current_check = 0;
    private long mLastTime = 0;

    private Context mServiceContext;
    private static ScanController mScanController;
    private static Object mLockObject = new Object();
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    // 此处的回调实例，实际上是ScanActivity中的对象
    private Callback mDataCallback;
    private ScanThread mScanThread;

    public static boolean mIsContinusStatus = false;
    // 扫描头状态 ture:出光 false:收光
    private boolean isScan = false;
    // 控制扫描串口读取线程状态
    private static boolean mIsReceiveData = false;
    // 是否打开串口
    private boolean mIsSerialPortOpen = false;
    // 是否为系统分割数据
    private volatile static boolean mIsCutData = false;

    // 缓存扫码数据
    private byte[] mSerialPortDataBuffer = null;
    private static byte[] mCutDataBuffer = null;
    private static int mCutDataBufferSize;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            LogUtil.trace("msg:" + msg);

            byte[] portData;

            switch (msg.what) {
                case MESSAGE_EM3070_CodeDate:
                    portData = null;
                    portData = (byte[]) msg.obj;
                    SerialPortData_total(portData);
                    break;

                case MESSAGE_EM3095_CodeDate:
                    portData = null;
                    portData = (byte[]) msg.obj;
                    SerialPortData_total_3095(portData);
                    break;

                case MESSAGE_N4313_CodeDate:
                    portData = null;
                    portData = (byte[]) msg.obj;
                    SerialPortData_total_4313_CodeId(portData);
                    break;

                default:
                    break;
            }
        };
    };

    // 协议写入检查
    private Runnable mCheckInitSuccess = new Runnable() {

        @Override
        public void run() {
            LogUtil.d(TAG, "mCheckInitSuccess");

            // true
            LogUtil.d(TAG, "mCheckInitSuccess::mIsSerialPortOpen is "
                    + mIsSerialPortOpen);
            if (!mIsSerialPortOpen) {
                return;
            }

            // 6778
            LogUtil.d(TAG, "mCheckInitSuccess::current_check is "
                    + current_check);
            switch (current_check) {
                case MESSAGE_CHECK_INIT_N4313:
                    // 检查协议是否写入
                    if (getScanPrefix() == Protocol_N4313_Protocol) {
                        if (init_N4313_start && init_N4313_end) {
                            Preference.setScanInit(mServiceContext, true);
                        } else {
                            if (init_index <= 5) {
                                if (!init_N4313_start) {
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(wakeUp);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(N4313Prefix_start);
                                }
                                if (!init_N4313_end) {
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(wakeUp);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(N4313Prefix_end);
                                }

                                mHandler.removeCallbacks(mCheckInitSuccess);
                                current_check = MESSAGE_CHECK_INIT_N4313;
                                mHandler.postDelayed(mCheckInitSuccess, 2000);
                                mHandler.removeCallbacks(stopReceice);
                                mHandler.postDelayed(stopReceice, 3 * 1000);
                                init_index++;
                            } else {
                                // TODO
                            }
                        }
                    } else if (getScanPrefix() == Protocol_N4313_Codeid
                            || getScanPrefix() == Protocol_N3680_Codeid) {
                        if (init_N4313_Code_id && init_N4313_end) {
                            Preference.setScanInit(mServiceContext, true);
                        } else {
                            if (init_index <= 5) {
                                if (!init_N4313_Code_id) {
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(wakeUp);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(N4313Prefix_Code_id);
                                }
                                if (!init_N4313_end) {
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(wakeUp);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    setIsReceive(true);
                                    notifyReader();
                                    writeCommand(N4313Prefix_end);
                                }

                                mHandler.removeCallbacks(mCheckInitSuccess);
                                current_check = MESSAGE_CHECK_INIT_N4313;
                                mHandler.postDelayed(mCheckInitSuccess, 2000);
                                mHandler.removeCallbacks(stopReceice);
                                mHandler.postDelayed(stopReceice, 3 * 1000);
                                init_index++;
                            } else {
                                // TODO
                            }
                        }
                    }
                    break;

                case MESSAGE_CHECK_INIT_EM3070:
                    if (init_EM3070_protocol) {
                        // TODO
                    } else {
                        if (init_index <= 5) {
                            initScanProtocol();
                        } else {
                            // TODO
                        }
                    }
                    break;

                case MESSAGE_CHECK_INIT_EM3095:
                    // false
                    LogUtil.d(TAG,
                            "mCheckInitSuccess::init_EM3095_protocol is "
                                    + init_EM3095_protocol);
                    if (init_EM3095_protocol) {
                        Preference.setScanInit(mServiceContext, true);
                    } else {
                        if (init_index <= 5) {
                            // 若协议未写入，每次都创建新线程，并写入协议
                            initScanProtocol();
                        } else {
                            // TODO
                        }
                    }
                    break;

                case MESSAGE_CHECK_INIT_SE955:
                    if (init_SE955_protocol) {
                        // TODO
                    } else {
                        if (init_index <= 5) {
                            initScanProtocol();
                        } else {
                            // TODO
                        }
                    }
                    break;

                default:
                    break;
            }

        }
    };

    // 防止后台扫描 接受数据线程被阻断
    private static Runnable stopReceice = new Runnable() {

        @Override
        public void run() {
            setIsReceive(false);
        }
    };

    private Runnable mInitRunnable = new Runnable() {

        @Override
        public void run() {
            LogUtil.d(TAG, "mInitRunnable running...");

            Barcode_Stop();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            boolean isScanInit = Preference.getIsScanInit(mServiceContext);
            // false
            LogUtil.d(TAG, "mInitRunnable::isScanInit:" + isScanInit);
            if (!isScanInit) {
                initScanProtocol();
            }
        }
    };

    private Runnable countdown = new Runnable() {

        @Override
        public void run() {
            Barcode_Stop();
            isScan = false;
        }
    };

    // 定时检查缓存区数据，处理缓存区数据
    private Runnable mSendDealDataBufferRunnable = new Runnable() {

        @Override
        public void run() {
            long nowTime = System.currentTimeMillis();
            // false
            LogUtil.d(TAG, "mSendDealDataBufferRunnable::mIsCutData is "
                    + mIsCutData);
            if (!mIsCutData) {
                // 实际传递的字节数
                LogUtil.d(TAG,
                        "mSendDealDataBufferRunnable::length of data is "
                                + mCutDataBufferSize);
                dealData(mCutDataBuffer, mCutDataBufferSize);
            }
        }
    };

    private ScanController() {
    }

    public static ScanController getInstance() {
        if (null == mScanController) {
            synchronized (mLockObject) {
                if (null == mScanController) {
                    mScanController = new ScanController();
                }
            }
        }
        return mScanController;
    }

    /**
     * <功能描述> 对扫描头写入数据，如扫描头设置参数等
     *
     * @param b [参数说明]
     * @return void [返回类型说明]
     */
    public void writeCommand(byte[] b) {
        try {
            if (allowToWrite()) {
                if (b == null)
                    return;
                mOutputStream.write(b);
                mOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 设置扫描间隔时间 time 间隔时间 单位ms
     *
     * @param time [参数说明]
     * @return void [返回类型说明]
     */
    public void setScanTime(long time) {
        scan_time = time;
    }

    public void setCallback(Callback callback) {
        this.mDataCallback = callback;
    }

    public synchronized static void setIsReceive(boolean b) {
        mIsReceiveData = b;
    }

    public int getScan_count() {
        return all_scan_count;
    }

    public void clearScan_count() {
        all_scan_count = 0;
    }

    /**
     * 打开条码设备，调用此方法将初始化扫描设备
     *
     * @return 0:成功，-1:失败
     */
    public int Barcode_Open(Context context, Callback dataReceive) {
        LogUtil.d(TAG, "Barcode_Open");

        int ret = -1;
        mServiceContext = context;
        // 此处的回调实例，实际上是ScanActivity中的对象
        this.mDataCallback = dataReceive;

        power_up();
        // 762C项目使用3095的扫描头
        setScannerModel(MODEL_EM3095);
        setScanPrefix();

        mTrigScan = "0";
        mLastTrigStatus = mTrigScan;
        int scanShortCutPressMode = Preference
                .getScanShortCutPressMode(mServiceContext);
        // scanShortCutPressMode:1
        LogUtil.d(TAG, "scanShortCutPressMode:" + scanShortCutPressMode);
        if (scanShortCutPressMode == 2) {
            setScanTime(10000);
        } else {
            setScanTime(3000);
        }

        // false
        LogUtil.d(TAG, "Barcode_Open::mIsSerialPortOpen is "
                + mIsSerialPortOpen);
        if (!mIsSerialPortOpen) {
            int scannerMode = getScannerModel();
            LogUtil.d(TAG, "Barcode_Open::scannerMode is " + scannerMode);
            if (scannerMode == MODEL_N3680) {
                ret = openserialPort(SERIAL_PORT_PATH, 115200, 8, 'N', 1);
            } else {
                // 使用3095扫描头
                ret = openserialPort(SERIAL_PORT_PATH, 9600, 8, 'N', 1);
            }
            mIsSerialPortOpen = true;
            mHandler.post(mInitRunnable);
        }
        return ret;
    }

    /**
     * <功能描述>关闭条码设备
     *
     * @return void [返回类型说明]
     */
    public void Barcode_Close() {
        Barcode_Continue_Stop();
        mSerialPortDataBuffer = null;
        mHandler.removeCallbacks(mCheckInitSuccess);
        begin = false;

        // 保证HONEYWELL的头上电时不出光
        Barcode_Stop();
        power_down();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        this.close();
        if (null != mReadThread) {
            notifyReader();
            mReadThread = null;
        }
        mIsSerialPortOpen = false;
    }

    /**
     * <功能描述> 出光开始扫描
     *
     * @return void [返回类型说明]
     */
    public synchronized void Barcode_Start() {
        LogUtil.d(TAG, "Barcode_Start()");
        scan("1");
        setIsScan(true);
    }

    /**
     * <功能描述> 闭光停止扫描
     *
     * @return void [返回类型说明]
     */
    public synchronized void Barcode_Stop() {
        if (isScan()) {
            scan("0");
            setIsScan(false);
        } else {
            // TODO
        }
    }

    /**
     * <功能描述> 出光开始扫描
     *
     * @param time [参数说明]
     * @return void [返回类型说明]
     */
    public synchronized void Barcode_Continue_Start(long time) {
        if (mIsSerialPortOpen) {
            if (time > 0) {
                scan_time_limit = time;
            }
            mIsContinusStatus = true;
            if (mScanThread != null) {
                mScanThread.interrupt();
                mScanThread.run = false;
            }
            mScanThread = new ScanThread();
            mScanThread.run = true;
            mScanThread.start();
        }
    }

    /**
     * <功能描述> 闭光停止扫描
     *
     * @return void [返回类型说明]
     */
    public synchronized void Barcode_Continue_Stop() {
        if (mScanThread != null) {
            mScanThread.interrupt();
            mScanThread.run = false;
        }
        mIsContinusStatus = false;
        mScanThread = null;
    }

    /**
     * <功能描述>
     *
     * @return [参数说明]
     * @return String [返回类型说明]
     */
    public String checkScannerModelSetting() {
        String text = "";
        int scannerModel = -1;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            String setting_path = "/dev/block/bootdevice/by-name/diag";
            File file = new File(setting_path);
            if (file.exists()) {
                InputStream stream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                byte[] buffer1 = new byte[1024 - 420];
                stream.read(buffer);
                int count = 0;
                for (int i = 420; i < 1024; i++) {
                    buffer1[count] = buffer[i];
                    count++;
                }
                String xml = new String(buffer1, "utf-8").trim()
                        + "\n</HT380k>";
                StringBuffer sb = new StringBuffer(xml);
                sb.insert(56, "\n<HT380k>");
                DocumentBuilder builder = factory.newDocumentBuilder();
                StringReader sr = new StringReader(sb.toString());
                InputSource is = new InputSource(sr);
                Document doc = (Document) builder.parse(is);
                Element rootElement = doc.getDocumentElement();
                NodeList phones = rootElement
                        .getElementsByTagName("ScanDeviceType");
                Node nodes = phones.item(0);
                text = nodes.getFirstChild().getNodeValue();
                scannerModel = Integer.parseInt(text);
                setScannerModel(scannerModel);
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * <功能描述>
     *
     * @return void [返回类型说明]
     */
    public static void parserXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            String setting_path = "/dev/block/bootdevice/by-name/diag";
            File file = new File(setting_path);
            if (file.exists()) {
                InputStream stream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                byte[] buffer1 = new byte[1024 - 420];
                stream.read(buffer);
                int count = 0;
                for (int i = 420; i < 1024; i++) {
                    buffer1[count] = buffer[i];
                    count++;
                }
                String xml = new String(buffer1, "utf-8").trim()
                        + "\n</HT380k>";
                StringBuffer sb = new StringBuffer(xml);
                sb.insert(56, "\n<HT380k>");
                DocumentBuilder builder = factory.newDocumentBuilder();
                StringReader sr = new StringReader(sb.toString());
                InputSource is = new InputSource(sr);
                Document doc = (Document) builder.parse(is);
                Element rootElement = doc.getDocumentElement();
                NodeList phones = rootElement
                        .getElementsByTagName("ScanDeviceType");
                Node nodes = phones.item(0);
                String values = nodes.getFirstChild().getNodeValue();

                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 是否恢复出厂设置 默认不恢复
     *
     * @return [参数说明] true 恢复；false 不恢复
     * @return boolean [返回类型说明]
     */
    public boolean getScanIsReturnFactory() {
        return Preference.getScannerIsReturnFactory(mServiceContext);
    }

    /**
     * <功能描述> 是否恢复出厂设置 默认不恢复 <code>true</code> 支持 <code>false</code>不支持
     *
     * @param b [参数说明]
     * @return void [返回类型说明]
     */
    public void setScanIsReturnFactory(boolean b) {
        Preference.setScanInit(mServiceContext, false);
        Preference.setScannerIsReturnFactory(mServiceContext, b);
        setScanPrefix();
        initScanProtocol();
    }

    /**
     * <功能描述> 获取扫描串口打开状态 ture:打开状态 <code>false</code>:关闭
     *
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public boolean isSerialPort_isOpen() {
        return mIsSerialPortOpen;
    }

    /**
     * <功能描述>
     *
     * @return void [返回类型说明]
     */
    private void notifyScanReader() {
        if (mScanThread != null && mScanThread.isAlive()) {
            mScanThread.interrupt();
        }
    }

    /**
     * 设置出光收光状态
     *
     * @param isScan ture:出光 <code>false</code>:收光
     */
    private void setIsScan(boolean isScan) {
        this.isScan = isScan;
    }

    /**
     * <功能描述> 获取出光收光状态
     *
     * @return [参数说明] true 出光；false 收光
     * @return boolean [返回类型说明]
     */
    private boolean isScan() {
        return isScan;
    }

    /**
     * <功能描述> 出光和收光设置
     *
     * @param trig [参数说明] 1拉高，关闭；0拉低，打开
     * @return void [返回类型说明]
     */
    private synchronized void scan(String trig) {
        LogUtil.d(TAG, "scan function and parameter trig is " + trig);
        // 两次触发信号的间隔时间不低于70ms
        mScanCurrentTime = (int) System.currentTimeMillis();
        if (mScanCurrentTime - mScanPreviousTime < 120) {
            if (!mLastTrigStatus.equals(trig)) {
                try {
                    if (mLastTrigStatus.equals(mTrigScan)) {
                        Thread.sleep(120);
                    }
                    doScan(trig);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // 相同trig指令间隔低于50ms 不做处理
            }
        } else {
            doScan(trig);
        }
    }

    /**
     * <功能描述> 执行具体的出光和收光动作
     *
     * @param trig [参数说明]
     * @return void [返回类型说明]
     */
    private void doScan(String trig) {
        if ("1".equals(trig)) {
            try {
                writeFile(new File(SCAN_PWR_EN), "1");
                all_scan_count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.removeCallbacks(stopReceice);
            mHandler.postDelayed(countdown, scan_time);
            setIsReceive(true);
        } else if ("0".equals(trig)) {
            writeFile(new File(SCAN_PWR_EN), "0");
            mHandler.postDelayed(stopReceice, 1000);
            mHandler.removeCallbacks(countdown);
        }

        notifyReader();
        mLastTrigStatus = trig;
        mScanPreviousTime = mScanCurrentTime;
    }

    /**
     * <功能描述>
     *
     * @return void [返回类型说明]
     */
    private void notifyReader() {
        if (mReadThread != null && mReadThread.isAlive()) {
            mReadThread.interrupt();
        }
    }

    /**
     * <功能描述>
     *
     * @param port
     * @param baudrate
     * @param bits
     * @param event
     * @param stop
     * @return
     * @throws SecurityException
     * @throws IOException
     * @throws InvalidParameterException [参数说明]
     * @return SerialPort [返回类型说明]
     */
    private SerialPort getSerialPort(String port, int baudrate, int bits,
                                     char event, int stop) throws SecurityException, IOException,
            InvalidParameterException {
        if (mSerialPort == null) {
            if ((port.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            mSerialPort = new SerialPort(new File(SERIAL_PORT_PATH), baudrate,
                    bits, event, stop, 0);
        }
        return mSerialPort;
    }

    /**
     * <功能描述> 关闭串口
     *
     * @return void [返回类型说明]
     */
    private void close() {
        try {
            if (mSerialPort != null) {
                if (mOutputStream != null)
                    mOutputStream.close();
                if (mInputStream != null)
                    mInputStream.close();
                if (mSerialPort != null)
                    mSerialPort.close();
                begin = false;
                mOutputStream = null;
                mInputStream = null;
                mSerialPort = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述>
     *
     * @param msg [参数说明]
     * @return void [返回类型说明]
     */
    private void writeCommand(String msg) {
        try {
            if (allowToWrite()) {
                if (msg == null)
                    msg = "";
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述>
     *
     * @param oneByte [参数说明]
     * @return void [返回类型说明]
     */
    private void write(int oneByte) {
        try {
            if (allowToWrite()) {
                mOutputStream.write(oneByte);
                mOutputStream.flush(); // 1
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述>
     *
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    private boolean allowToWrite() {
        if (mOutputStream == null) {
            return false;
        }
        return true;
    }

    /**
     * <功能描述> 根据不同扫描头 初始化协议内容 type 扫描头类型
     *
     * @param type [参数说明]
     * @return void [返回类型说明]
     */
    private void init(int type) {
        LogUtil.d(TAG, "init is start...value of type is " + type);

        try {
            switch (type) {
                case Protocol_SE955_Protocol:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    Thread.sleep(50);
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(pack_code_protocol);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_SE955;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_SE955_Default:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    Thread.sleep(50);
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(pack_code_noProtoc);

                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_EM3070_Protocol:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newlandPrefix_Protocol_3070);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_EM3070;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_EM3070_Default:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newlandPrefix_Default_3070);

                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_EM3095_Protocol:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newLandPrefix_Code_id_3095);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newLandPrefix_Tab_3095);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newLandPrefix_Save_3095);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_EM3095;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_EM3095_Default:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(newLandPrefix_Default_3095);

                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_N4313_Codeid:
                    // 返回CODEID 协议
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_Code_id);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_end);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_N4313;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_N4313_Protocol:
                    // 前后缀1113 1214协议 防止断码误码
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_start);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_end);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_N4313;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_N4313_Default:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_default);

                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_N3680_Default:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_default);

                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                case Protocol_N3680_Codeid:
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(wakeUp);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_Code_id);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setIsReceive(true);
                    notifyReader();
                    writeCommand(N4313Prefix_end);

                    mHandler.removeCallbacks(mCheckInitSuccess);
                    current_check = MESSAGE_CHECK_INIT_N4313;
                    mHandler.postDelayed(mCheckInitSuccess, 2000);
                    mHandler.removeCallbacks(stopReceice);
                    mHandler.postDelayed(stopReceice, 3 * 1000);
                    break;

                default:
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 向结点写值
     *
     * @param file
     * @param value [参数说明]
     * @return void [返回类型说明]
     */
    private void writeFile(File file, String value) {
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
     * <功能描述> 根据不同扫描头，初始化协议内容
     *
     * @return void [返回类型说明]
     */
    private synchronized void initScanProtocol() {
        LogUtil.d(TAG, "initScanProtocol");
        new Thread(new Runnable() {

            @Override
            public void run() {
                int scanPrefix = getScanPrefix();
                // 6
                LogUtil.d(TAG, "initScanProtocol::scanPrefix is " + scanPrefix);
                init(scanPrefix);
            }
        }).start();
    }

    /**
     * <功能描述> 返回数据判断处理
     *
     * @param buffer
     * @param size [参数说明]
     * @return void [返回类型说明]
     */
    private void dealData(byte[] buffer, int size) {
        try {
            if (buffer == null) {
                Barcode_Stop();
                return;
            }

            String str = Tools.bytesToHexString(buffer, 0, size);

            int scannerModel = getScannerModel();
            // 2
            LogUtil.d(TAG, "dealData::scannerModel is " + scannerModel);
            if (scannerModel == MODEL_EM3095) {
                // em3095 02000001003331
                if (buffer.length >= 7 && buffer[0] == 2 && buffer[1] == 0
                        && buffer[2] == 0 && buffer[3] == 1 && buffer[4] == 0
                        && buffer[5] == 51 && buffer[6] == 49) {
                    init_EM3095_protocol = true;
                    Barcode_Stop();
                    return;
                }
            }

            if (scannerModel == MODEL_SE955) {
                // "0005d1000001ff29"
                if (str.equals("0005d1000001ff29")) {
                    init_SE955_protocol = false;
                    Barcode_Stop();
                    return;
                }

                // 05d1000001ff29
                if (buffer.length >= 7 && buffer[0] == 5 && buffer[1] == -47
                        && buffer[2] == 0 && buffer[3] == 0 && buffer[4] == 1
                        && buffer[5] == -1 && buffer[6] == 41) {
                    init_SE955_protocol = false;
                    Barcode_Stop();
                    return;
                }

                // 04d00000ff2c
                if (buffer.length >= 6 && buffer[0] == 4 && buffer[1] == -48
                        && buffer[2] == 0 && buffer[3] == 0 && buffer[4] == -1
                        && buffer[5] == 44) {
                    init_SE955_protocol = true;
                    Barcode_Stop();
                    return;
                }

                // 0004d00000ff2c
                if (str.equals("0004d00000ff2c")) {
                    init_SE955_protocol = true;
                    Barcode_Stop();
                    return;
                }
            }

            if (scannerModel == MODEL_N4313 || scannerModel == MODEL_N3680) {
                if (str.equals("fc")) {
                    return;
                }

                if (str.equals("505245424b32393935633830062e")) {
                    init_N4313_Code_id = true;
                    Barcode_Stop();
                    return;
                }

                // 505245424b32393931313133062e
                if (str.equals("505245424b32393931313133062e")) {
                    init_N4313_start = true;
                    Barcode_Stop();
                    return;
                }

                if (str.equals("535546424b32393931323134062e")) {
                    init_N4313_end = true;
                    Barcode_Stop();
                    return;
                }

                // 444546414c54062e
                if (str.equals("444546414c54062e")) {
                    Barcode_Stop();
                    return;
                }
            }

            if (scannerModel == MODEL_EM3070) {
                if (buffer.length == 1 && buffer[0] == 21) {
                    init_EM3070_protocol = false;
                    Barcode_Stop();
                    return;
                }
                // 06060606060606
                if (str.equals("0606060606060606")) {
                    init_EM3070_protocol = true;
                    Barcode_Stop();
                    return;
                }

                // 060606060606
                if (buffer.length <= 7) {
                    boolean em3070_init = true;
                    for (int i = 0; i < buffer.length; i++) {
                        if (buffer[i] != 0x06) {
                            em3070_init = false;
                            init_EM3070_protocol = false;
                            break;
                        }
                    }
                    if (em3070_init) {
                        init_EM3070_protocol = true;
                        Barcode_Stop();
                        return;
                    }
                }
            }

            if (buffer.length == 1 && buffer[0] == 0x00) {
                // 兼容新大陆一维引擎
                // 新大陆一维引擎只能上电一次,下次开电源必须在500ms以上
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }

            int Scan_mode = getScanPrefix();
            // 6
            LogUtil.d(TAG, "dealData getScanPrefix(): " + Scan_mode);
            switch (Scan_mode) {
                case Protocol_SE955_Protocol:
                    writeCommand(host_cmd_ack);
                    getXunBao_Se955(buffer);
                    break;

                case Protocol_EM3095_Protocol:
                    // 21323330313439
                    if (size < buffer.length) {
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        buffer = null;
                        buffer = new byte[size];
                        buffer = temp;
                        temp = null;
                    }
                    byte[] buffer2 = delectBytesNull(buffer);
                    getNewLand_Em3095(buffer2);
                    break;

                case Protocol_EM3070_Protocol:
                    getNewLand_Em3070(buffer);
                    break;

                case Protocol_N4313_Protocol:
                    getHonyWell_N4313_Protocol(buffer);
                    break;

                case Protocol_N3680_Codeid:
                case Protocol_N4313_Codeid:
                    getHonyWell_N4313_Codeid(buffer);
                    break;

                default:
                    getDefault(buffer);
                    break;
            }
            mCutDataBuffer = null;
            mCutDataBufferSize = 0;
            Barcode_Stop();
        } catch (Exception e) {
            Barcode_Stop();
            e.printStackTrace();
        }
    }

    /**
     * <功能描述>
     *
     * @param xunBaoContent [参数说明]
     * @return void [返回类型说明]
     */
    private void handleXunBaoResult(byte[] xunBaoContent) {
        if (xunBaoContent != null) {
            // 讯宝数据包协议 ：Length(1)+Opcode(1)+Message Source(1)为0+Status(1)+Bar
            // Code
            // Type(1)+Decode Data(?)+Checksum(2)
            // lenght:数据包真实长度，不包含校验位长度（Checksum(2)）
            // 校验位长度
            // int Checksum_lenght = 0;
            if (null != mSerialPortDataBuffer) {
                if (mSerialPortDataBuffer[2] != 0) {
                    // 正确数据第三位为0
                    middle_index = 0;
                    mSerialPortDataBuffer = null;
                    checkXunBaoContent(xunBaoContent);
                } else {
                    arraycopy(xunBaoContent);
                    checkXunBaoContent(mSerialPortDataBuffer);
                }

            } else {
                checkXunBaoContent(xunBaoContent);
            }
        }
    }

    /**
     * <功能描述> 检查讯宝数据
     *
     * @param xunBaoContent [参数说明]
     * @return void [返回类型说明]
     */
    private void checkXunBaoContent(byte[] xunBaoContent) {
        LogUtil.d(TAG, "checkXunBaoContent");
        if (xunBaoContent.length > 7 && xunBaoContent[2] == 0) {
            // 长度位 xunBaoContent【0】
            int lenght = Tools.byteToInt(xunBaoContent[0]);
            // 减去校验2位
            if (lenght == (xunBaoContent.length - 2)) {
                // 是完整数据
                // Log.d("检测为完整数据：", Tools.getNowTimeHm());
                String str = Tools.bytesToHexString(xunBaoContent, 0,
                        xunBaoContent.length);
                // Log.d(TAG, "xunBaoContent:" + str);
                // Log.d("协议校验数据正确性：", Tools.getNowTimeHm());
                if (isChecksun(str)) {
                    // Log.d("协议校验完成：", Tools.getNowTimeHm());
                    ScanResultBean result = getXunBaoBuffer(xunBaoContent);
                    if (null != result) {
                        // if (mScanListener != null) {
                        // // mScanListener.result(xunBaoBuffer);
                        // mScanListener.xunbaoResult(result.getCodeType(),
                        // result.getContent());
                        // ////Log.d("协应用显示完成：", Tools.getNowTimeHm());
                        // }
                        // mBeepManager.play();
                        if (null != mDataCallback) {
                            mDataCallback
                                    .Barcode_Read(result.getContent(),
                                            CodeType.getXunbaoType(result
                                                    .getCodeType()), 0);
                            notifyScanReader();
                        }
                        // houtai_result(result.getContent());
                        // ////Log.d("后台显示完成：", Tools.getNowTimeHm());
                    }
                }
                mSerialPortDataBuffer = null;
                middle_index = 0;
            } else if (lenght > (xunBaoContent.length - 2)) {
                // 不够一条数据
                mSerialPortDataBuffer = new byte[xunBaoContent.length];
                mSerialPortDataBuffer = xunBaoContent;
                // total_lenght = lenght;
            } else if (lenght < (xunBaoContent.length - 2)) {
                // 超过一条数据
                data_cut(MODEL_SE955, xunBaoContent, (lenght + 2));
            }
        } else {
            // 数据不符合协议 丢掉
            mSerialPortDataBuffer = null;
            middle_index = 0;
            // 尝试重写迅宝协议指令 20150403
            initScanProtocol();
            init_index++;
        }
    }

    /**
     * <功能描述>
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void arraycopy(byte[] portData) {
        if (null != mSerialPortDataBuffer) {
            byte[] data3 = new byte[mSerialPortDataBuffer.length
                    + portData.length];
            System.arraycopy(mSerialPortDataBuffer, 0, data3, 0,
                    mSerialPortDataBuffer.length);
            System.arraycopy(portData, 0, data3, mSerialPortDataBuffer.length,
                    portData.length);
            mSerialPortDataBuffer = new byte[data3.length];
            mSerialPortDataBuffer = data3;
            middle_index = 0;
        }
    }

    /**
     * <功能描述> 分割超过一条数据的数据
     *
     * @param scanType
     * @param srcData
     * @param lenght [参数说明]
     * @return void [返回类型说明]
     */
    private void data_cut(int scanType, byte[] srcData, int lenght) {
        try {
            switch (scanType) {
                case MODEL_SE955:
                    if (srcData.length > lenght) {
                        byte[] buffer = new byte[lenght];
                        System.arraycopy(srcData, 0, buffer, 0, lenght);
                        String str = Tools.bytesToHexString(buffer, 0, lenght);
                        // Log.d(TAG, "data_cut buffer:" + str);
                        if (isChecksun(str)) {
                            ScanResultBean result = getXunBaoBuffer(buffer);
                            if (null != result) {
                                // if (mScanListener != null) {
                                // // mScanListener.result(xunBaoBuffer);
                                // mScanListener.xunbaoResult(
                                // result.getCodeType(),
                                // result.getContent());
                                // }
                                // houtai_result(result.getContent());
                                // mBeepManager.play();
                                if (mDataCallback != null) {
                                    mDataCallback.Barcode_Read(result
                                                    .getContent(),
                                            CodeType.getXunbaoType(result
                                                    .getCodeType()), 0);
                                    notifyScanReader();
                                }
                            }
                        }
                        // 分割后剩余数组
                        byte[] data3 = new byte[srcData.length - lenght];
                        System.arraycopy(srcData, lenght, data3, 0,
                                srcData.length - lenght);

                        // if (data3.length > lenght) {
                        // // handleXunBaoResult(data3);
                        // serialPortData_buffer = null;
                        // } else {
                        middle_index = 0;
                        mSerialPortDataBuffer = null;
                        mSerialPortDataBuffer = new byte[data3.length];
                        mSerialPortDataBuffer = data3;
                        checkXunBaoContent(mSerialPortDataBuffer);
                        // }
                    }

                    break;

                default:
                    break;
            }

        } catch (ArrayStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 校验讯宝包数据是否完整正确
     *
     * @param s
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    private boolean isChecksun(String s) {
        String checksun = s.substring(s.length() - 4).toUpperCase();
        Long sum = 0L;
        for (int i = 0; i < s.length() - 4; i += 2) {
            String hex = s.substring(i, i + 2);
            long x = Long.parseLong(hex, 16);
            sum = sum + x;
        }
        sum = ~sum + 1;
        String hex_sum = Long.toHexString(sum).toUpperCase();
        // 保留四位
        if (hex_sum.length() > 4) {
            hex_sum = hex_sum.substring(hex_sum.length() - 4);
        } else {
            int time = hex_sum.length();
            for (int i = 0; i < 4 - time; i++) {
                hex_sum = "0" + hex_sum;
            }
        }

        if (checksun.equals(hex_sum)) {
            return true;
        }
        return false;
    }

    /**
     * <功能描述> 从讯宝协议数据中获取真实扫描数据
     *
     * @param buffer
     * @return [参数说明]
     * @return ScanResultBean [返回类型说明]
     */
    private ScanResultBean getXunBaoBuffer(byte[] buffer) {
        ScanResultBean scanResult = null;
        if (buffer.length > 7) {
            byte[] byteData = new byte[buffer.length - 7];
            for (int i = 0; i < byteData.length; i++) {
                byteData[i] = buffer[i + 5];

            }
            // String encoding = Tools.guessEncoding(buffer);
            // encoding = encoding.toUpperCase().equals("WINDOWS-1252") ?
            // "GB2312"
            // : encoding;
            //
            // String data = null;
            // try {
            // data = new String(byteData, encoding);
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // }

            scanResult = new ScanResultBean();
            scanResult.setCodeType(buffer[4]);
            scanResult.setContent(byteData);
            // scanResult.setContent(data);
        }
        return scanResult;
    }

    /**
     * <功能描述> 对新大陆3095数据处理
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getNewLand_Em3095(byte[] buffer) {
        LogUtil.d(TAG, "getNewLan_Em3095");
        // 21687474703a2f2f75636d702e73662d657870726573732e636f6d2f736572766963652f77656978696e2f61637469766974792f77785f623273665f6f726465723f70313d30383631313630393739313109
        LogUtil.d(TAG, "getNewLand_Em3095:" + Tools.bytesToHexString(buffer));

        if (null == mSerialPortDataBuffer) {
            LogUtil.d(TAG,
                    "getNewLand_Em3095::mSerialPortDataBuffer is null...");
            LogUtil.d(TAG, "getNewLand_Em3095::buffer.length is "
                    + buffer.length);
            if (buffer.length >= 2 && buffer[0] <= 35
                    && buffer[buffer.length - 1] == (byte) 0x09) {
                // 完整数据 用线程处理
                // Message message = new Message();
                // message.what = EM3070_CodeDate_total;
                // message.obj = buffer;
                // mHandler.sendMessage(message);
                SerialPortData_total_3095(buffer);
            } else if (buffer.length >= 1 && buffer[0] <= 35) {
                // 数据头
                SerialPortData_head(buffer);
            } else {
                // 错误数据 ， 或者协议可能没写入
                // Log.d(TAG, "getNewLand_Em3095 错误数据 ， 或者协议可能没写入");
                mSerialPortDataBuffer = null;
                middle_index = 0;
                initScanProtocol();
                init_index++;
            }
        } else {
            if (buffer.length >= 1 && buffer[buffer.length - 1] == (byte) 0x09) {
                // 数据尾
                SerialPortData_ass(buffer);
            } else {
                // 数据中间
                SerialPortData_middle(buffer);
            }
        }
    }

    /**
     * <功能描述> 霍尼 N4313数据处理
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getHonyWell_N4313_Protocol(byte[] buffer) {
        byte code_id = 0x00;
        byte[] tempN = null;
        // if (has_N4313Prefix_start && has_N4313Prefix_end) {
        // 添加协议后和em3070协议处理完全相同
        getNewLand_Em3070(buffer);
    }

    /**
     * <功能描述> 霍尼 N4313数据处理
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getHonyWell_N4313_Codeid(byte[] buffer) {
        /*
         * Q,a,h,j,b,i,d,D,e,m,g,c,E j以外，判断接下来5位都不出现同个字母。
         */
        if (null != buffer) {
            if (null == mSerialPortDataBuffer) {
                if (buffer.length > 2 && isN4313Code_head(buffer[0])
                        && buffer[buffer.length - 1] == (byte) 0x14
                        && buffer[buffer.length - 2] == (byte) 0x12) {
                    SerialPortData_total_4313_CodeId(buffer);
                } else if (buffer.length >= 1 && isN4313Code_head(buffer[0])) {
                    SerialPortData_head(buffer);
                } else {
                    // 错误数据 ， 或者协议可能没写入
                    // Log.d(TAG, "错误数据 ， 或者协议可能没写入");
                }
            } else {
                if (buffer.length > 2 && isN4313Code_head(buffer[0])
                        && buffer[buffer.length - 1] == (byte) 0x14
                        && buffer[buffer.length - 2] == (byte) 0x12) {
                    SerialPortData_total_4313_CodeId(buffer);
                    middle_index = 0;
                    mSerialPortDataBuffer = null;
                } else if (buffer.length >= 2
                        && buffer[buffer.length - 1] == (byte) 0x14
                        && buffer[buffer.length - 2] == (byte) 0x12) {
                    SerialPortData_ass_4313_CodeId(buffer);
                } else {
                    // 数据中间
                    SerialPortData_middle_4313_CodeId(buffer);
                }
            }
        }
    }

    /**
     * <功能描述> 判断是否为4313Coidid头
     *
     * @param b
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    private boolean isN4313Code_head(byte b) {
        // N4313 Q,a,h,j,b,i,d,D,e,m,g,c,E j
        for (int i = 0; i < heads_byte.length; i++) {
            if (heads_byte[i] == b) {
                return true;
            }
        }
        return false;
    }

    /**
     * <功能描述> 迅宝se955数据处理
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getXunBao_Se955(byte[] buffer) {
        try {
            handleXunBaoResult(buffer);
            Barcode_Stop();
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 未做协议处理的数据
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getDefault(byte[] buffer) {
        if (null != mDataCallback) {
            mDataCallback.Barcode_Read(buffer, "", 0);
            notifyScanReader();
        }
        Barcode_Stop();
    }

    /**
     * <功能描述> 新大陆em3070数据处理
     *
     * @param buffer [参数说明]
     * @return void [返回类型说明]
     */
    private void getNewLand_Em3070(byte[] buffer) {
        if (buffer.length >= 4 && buffer[0] == (byte) 0x11
                && buffer[1] == (byte) 0x13
                && buffer[buffer.length - 1] == (byte) 0x14
                && buffer[buffer.length - 2] == (byte) 0x12) {
            // 完整数据 用线程处理
            // Message message = new Message();
            // message.what = EM3070_CodeDate_total;
            // message.obj = buffer;
            // mHandler.sendMessage(message);
            SerialPortData_total(buffer);
        } else if (buffer.length >= 2 && buffer[0] == (byte) 0x11
                && buffer[1] == (byte) 0x13) {
            // 数据头
            SerialPortData_head(buffer);
        } else if (buffer.length >= 4 && buffer[0] != (byte) 0x11
                && buffer[1] != (byte) 0x13
                && buffer[buffer.length - 1] != (byte) 0x14
                && buffer[buffer.length - 2] != (byte) 0x12) {
            // 数据中间
            SerialPortData_middle(buffer);
        } else if (buffer.length >= 2
                && buffer[buffer.length - 1] == (byte) 0x14
                && buffer[buffer.length - 2] == (byte) 0x12) {
            // 数据尾
            SerialPortData_ass(buffer);
        }
    }

    /**
     * <功能描述> em3095 完整数据处理
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_total_3095(byte[] portData) {
        LogUtil.trace();

        mSerialPortDataBuffer = null;
        middle_index = 0;
        byte codeId = 0x00;
        ArrayList<byte[]> list_byte;

        list_byte = checkSerialPortData_3095(portData);
        if (null == list_byte || list_byte.size() <= 0) {
            list_byte.add(portData);
        }
        for (int i = 0; i < list_byte.size(); i++) {
            if (list_byte.get(i)[0] <= 35
                    && list_byte.get(i)[list_byte.get(i).length - 1] == (byte) 0x09) {
                codeId = list_byte.get(i)[0];
                byte[] buffer = new byte[list_byte.get(i).length - 2];
                System.arraycopy(list_byte.get(i), 1, buffer, 0, buffer.length);
                if (null != mDataCallback) {
                    // 数据返回
                    mDataCallback.Barcode_Read(buffer,
                            CodeType.getNewLandCodeType_3095(codeId), 0);
                    notifyScanReader();
                }
            } else {
                continue;
            }
        }
        Barcode_Stop();
    }

    /**
     * <功能描述> em3095 完整数据处理 AIMID
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_total(byte[] portData) {
        LogUtil.trace();
        String val = null;
        String codeId = "";
        String codeType = Tools.returnType(portData);
        ArrayList<byte[]> list_byte;
        list_byte = checkSerialPortData(portData);
        if (null == list_byte || list_byte.size() <= 0) {
            list_byte.add(portData);
        }
        for (int i = 0; i < list_byte.size(); i++) {
            if (list_byte.get(i)[0] == (byte) 0x11
                    && list_byte.get(i)[1] == (byte) 0x13
                    && list_byte.get(i)[list_byte.get(i).length - 1] == (byte) 0x14
                    && list_byte.get(i)[list_byte.get(i).length - 2] == (byte) 0x12) {
                if (getScannerModel() == MODEL_EM3070) {
                    if (codeType.equals("default")) {
                        val = new String(list_byte.get(i));
                        codeId = val.substring(2, 5);
                        val = val.substring(5, val.length() - 2);
                    } else {
                        try {
                            val = new String(list_byte.get(i), codeType);
                            codeId = val.substring(2, 5);
                            val = val.substring(5, val.length() - 2);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] buffer = new byte[list_byte.get(i).length - 7];
                    System.arraycopy(list_byte.get(i), 5, buffer, 0,
                            buffer.length);
                    if (null != mDataCallback) {
                        mDataCallback.Barcode_Read(
                                buffer,
                                CodeType.getCodeTypeByAimId_3070(codeId,
                                        val.length()), 0);
                        notifyScanReader();
                    }
                } else {
                    byte[] buffer = new byte[list_byte.get(i).length - 4];
                    System.arraycopy(list_byte.get(i), 2, buffer, 0,
                            buffer.length);
                    if (null != mDataCallback) {
                        mDataCallback.Barcode_Read(buffer, "", 0);
                        notifyScanReader();
                    }
                }

            } else {
                continue;
            }
            Barcode_Stop();
        }
    }

    /**
     * <功能描述> 不完整数据头部
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_head(byte[] portData) {
        middle_index = 0;
        mSerialPortDataBuffer = null;
        mSerialPortDataBuffer = new byte[portData.length];
        mSerialPortDataBuffer = portData;
    }

    /**
     * <功能描述> 不完整数据中部 将数据拼接head数据后面
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_middle_4313_CodeId(byte[] portData) {
        if (null != mSerialPortDataBuffer) {
            if (middle_index <= 10) {
                byte[] data3 = new byte[mSerialPortDataBuffer.length
                        + portData.length];
                System.arraycopy(mSerialPortDataBuffer, 0, data3, 0,
                        mSerialPortDataBuffer.length);
                System.arraycopy(portData, 0, data3,
                        mSerialPortDataBuffer.length, portData.length);
                mSerialPortDataBuffer = new byte[data3.length];
                mSerialPortDataBuffer = data3;
                middle_index++;
            } else {
                mSerialPortDataBuffer = null;
                middle_index = 0;
            }
        }
    }

    /**
     * <功能描述> 不完整数据中部 将数据拼接head数据后面
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_middle(byte[] portData) {
        LogUtil.d(TAG, "SerialPortData_middle");
        if (null != mSerialPortDataBuffer) {
            if (middle_index <= 5) {
                byte[] data3 = new byte[mSerialPortDataBuffer.length
                        + portData.length];
                System.arraycopy(mSerialPortDataBuffer, 0, data3, 0,
                        mSerialPortDataBuffer.length);
                System.arraycopy(portData, 0, data3,
                        mSerialPortDataBuffer.length, portData.length);
                mSerialPortDataBuffer = new byte[data3.length];
                mSerialPortDataBuffer = data3;
                middle_index++;
            } else {
                mSerialPortDataBuffer = null;
                middle_index = 0;
                if (getScannerModel() == MODEL_EM3095) {
                    // 错误数据 ， 或者协议可能没写入
                    LogUtil.d(TAG, "SerialPortData_middle 错误数据 ， 或者3095协议可能没写入");
                    mSerialPortDataBuffer = null;
                    middle_index = 0;
                    initScanProtocol();
                    init_index++;
                }
            }
        }
    }

    /**
     * <功能描述> 不完整数据尾部
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_ass(byte[] portData) {
        LogUtil.d(TAG, "SerialPortData_ass");
        if (null != mSerialPortDataBuffer) {
            byte[] data3 = new byte[mSerialPortDataBuffer.length
                    + portData.length];
            System.arraycopy(mSerialPortDataBuffer, 0, data3, 0,
                    mSerialPortDataBuffer.length);
            System.arraycopy(portData, 0, data3, mSerialPortDataBuffer.length,
                    portData.length);
            mSerialPortDataBuffer = new byte[data3.length];
            mSerialPortDataBuffer = data3;
            middle_index = 0;
        }
        if (null != mSerialPortDataBuffer && mSerialPortDataBuffer.length > 0) {
            if (getScannerModel() == MODEL_EM3095) {
                if (mSerialPortDataBuffer[0] <= 35
                        && mSerialPortDataBuffer[mSerialPortDataBuffer.length - 1] == (byte) 0x09) {
                    // SerialPortData_total(serialPortData_buffer);
                    // 完整数据 用线程处理
                    Message message = new Message();
                    message.what = MESSAGE_EM3095_CodeDate;
                    message.obj = mSerialPortDataBuffer;
                    mHandler.sendMessage(message);
                }
            } else {
                if (mSerialPortDataBuffer[0] == (byte) 0x11
                        && mSerialPortDataBuffer[1] == (byte) 0x13
                        && mSerialPortDataBuffer[mSerialPortDataBuffer.length - 1] == (byte) 0x14
                        && mSerialPortDataBuffer[mSerialPortDataBuffer.length - 2] == (byte) 0x12) {
                    // SerialPortData_total(serialPortData_buffer);
                    // 完整数据 用线程处理
                    Message message = new Message();
                    message.what = MESSAGE_EM3070_CodeDate;
                    message.obj = mSerialPortDataBuffer;
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    /**
     * <功能描述> n4313不完整数据尾部 CodeId
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_ass_4313_CodeId(byte[] portData) {
        if (null != mSerialPortDataBuffer) {
            byte[] data3 = new byte[mSerialPortDataBuffer.length
                    + portData.length];
            System.arraycopy(mSerialPortDataBuffer, 0, data3, 0,
                    mSerialPortDataBuffer.length);
            System.arraycopy(portData, 0, data3, mSerialPortDataBuffer.length,
                    portData.length);
            mSerialPortDataBuffer = new byte[data3.length];
            mSerialPortDataBuffer = data3;
            middle_index = 0;
        }
        if (null != mSerialPortDataBuffer && mSerialPortDataBuffer.length > 0) {
            if (mSerialPortDataBuffer.length >= 3
                    && isN4313Code_head(mSerialPortDataBuffer[0])
                    && mSerialPortDataBuffer[mSerialPortDataBuffer.length - 1] == (byte) 0x14
                    && mSerialPortDataBuffer[mSerialPortDataBuffer.length - 2] == (byte) 0x12) {
                // 完整数据 用线程处理
                Message message = new Message();
                message.what = MESSAGE_N4313_CodeDate;
                message.obj = mSerialPortDataBuffer;
                mHandler.sendMessage(message);
            }
        }
    }

    /**
     * <功能描述> 4313 完整数据处理
     *
     * @param portData [参数说明]
     * @return void [返回类型说明]
     */
    private void SerialPortData_total_4313_CodeId(byte[] portData) {
        mSerialPortDataBuffer = null;
        middle_index = 0;
        byte code_id = 0x00;
        ArrayList<byte[]> list_byte;
        list_byte = checkSerialPortData_N4313_CodeId(portData);
        if (null == list_byte || list_byte.size() <= 0) {
            list_byte.add(portData);
        }
        for (int i = 0; i < list_byte.size(); i++) {
            if (isN4313Code_head(list_byte.get(i)[0])
                    && list_byte.get(i)[list_byte.get(i).length - 1] == (byte) 0x14
                    && list_byte.get(i)[list_byte.get(i).length - 2] == (byte) 0x12) {
                code_id = list_byte.get(i)[0];
                byte[] buffer = new byte[list_byte.get(i).length - 3];
                System.arraycopy(list_byte.get(i), 1, buffer, 0, buffer.length);
                if (null != mDataCallback) {
                    mDataCallback.Barcode_Read(buffer,
                            CodeType.getHonyWellType(code_id), 0);
                    notifyScanReader();
                }
            } else {
                continue;
            }

            Barcode_Stop();
        }
    }

    /**
     * <功能描述> 校验N4313模块数据
     *
     * @param serialPortData
     * @return [参数说明]
     * @return ArrayList<byte[]> [返回类型说明]
     */
    private ArrayList<byte[]> checkSerialPortData_N4313_CodeId(
            byte[] serialPortData) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int iStart = 0;
        int iEnd = 0;
        boolean isHasHead = false;
        for (int i = 0; i < serialPortData.length; i++) {

            if (!isHasHead) {
                if (i < serialPortData.length
                        && isN4313Code_head(serialPortData[i])) {
                    isHasHead = true;
                    iStart = i;
                }
            }

            if (isHasHead) {
                if (i > 0 && serialPortData[i - 1] == 0x12
                        && serialPortData[i] == 0x14) {
                    iEnd = i;
                    byte[] temp = new byte[iEnd - iStart + 1];
                    int index = 0;
                    for (int j = iStart; j <= iEnd; j++) {
                        temp[index] = serialPortData[j];
                        index++;
                    }
                    list.add(temp);
                    isHasHead = false;
                    iStart = 0;
                    iEnd = 0;
                }
            }
        }
        return list;
    }

    /**
     * <功能描述> em3070协议检查 以防出现乱码
     *
     * @param serialPortData
     * @return [参数说明]
     * @return ArrayList<byte[]> [返回类型说明]
     */
    private static ArrayList<byte[]> checkSerialPortData(byte[] serialPortData) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int iStart = 0;
        int iEnd = 0;
        boolean isHasHead = false;
        for (int i = 0; i < serialPortData.length; i++) {

            if (i < serialPortData.length && serialPortData[i] == 0x11
                    && serialPortData[i + 1] == 0x13) {
                isHasHead = true;
                iStart = i;
            }

            if (isHasHead) {
                if (i > 0 && serialPortData[i - 1] == 0x12
                        && serialPortData[i] == 0x14) {
                    iEnd = i;
                    byte[] temp = new byte[iEnd - iStart + 1];
                    int index = 0;
                    for (int j = iStart; j <= iEnd; j++) {
                        temp[index] = serialPortData[j];
                        index++;
                    }
                    list.add(temp);
                    isHasHead = false;
                    iStart = 0;
                    iEnd = 0;
                }
            }
        }
        return list;
    }

    /**
     * <功能描述> 解析3095数据
     *
     * @param serialPortData
     * @return [参数说明]
     * @return ArrayList<byte[]> [返回类型说明]
     */
    private static ArrayList<byte[]> checkSerialPortData_3095(
            byte[] serialPortData) {
        LogUtil.trace();

        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int iStart = 0;
        int iEnd = 0;
        boolean isHasHead = false;

        for (int i = 0; i < serialPortData.length; i++) {

            if (!isHasHead) {
                if (i < serialPortData.length && serialPortData[i] <= 35) {
                    isHasHead = true;
                    iStart = i;
                }
            }

            if (isHasHead) {
                if (i > 0 && serialPortData[i] == 0x09) {
                    iEnd = i;
                    byte[] temp = new byte[iEnd - iStart + 1];
                    int index = 0;
                    for (int j = iStart; j <= iEnd; j++) {
                        temp[index] = serialPortData[j];
                        index++;
                    }
                    list.add(temp);
                    isHasHead = false;
                    iStart = 0;
                    iEnd = 0;
                }
            }
        }

        return list;
    }

    /**
     * <功能描述> 4313协议检查 以防出现乱码
     *
     * @param serialPortData
     * @return [参数说明]
     * @return ArrayList<byte[]> [返回类型说明]
     */
    private static ArrayList<byte[]> checkSerialPortData_4313(
            byte[] serialPortData) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int iStart = 0;
        int iEnd = 0;
        boolean isHasHead = false;
        for (int i = 0; i < serialPortData.length; i++) {

            if (!isHasHead && i < serialPortData.length
                    && serialPortData[i] == 0x5D) {
                isHasHead = true;
                iStart = i;
            }

            if (isHasHead) {
                if (i > 0 && serialPortData[i - 1] == 0x12
                        && serialPortData[i] == 0x14) {
                    iEnd = i;
                    byte[] temp = new byte[iEnd - iStart + 1];
                    int index = 0;
                    for (int j = iStart; j <= iEnd; j++) {
                        temp[index] = serialPortData[j];
                        index++;
                    }
                    list.add(temp);
                    isHasHead = false;
                    iStart = 0;
                    iEnd = 0;
                }
            }
        }
        return list;
    }

    /**
     * <功能描述> 删除byte[] 里面连续为0x00的字段
     *
     * @param buffer
     * @return [参数说明]
     * @return byte[] [返回类型说明]
     */
    private byte[] delectBytesNull(byte[] buffer) {
        int len = 0;
        byte[] temp = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != 0x00) {
                temp[i] = buffer[i];
                len++;
            }
        }
        if (len != buffer.length) {
            buffer = null;
            buffer = new byte[len];
            System.arraycopy(temp, 0, buffer, 0, buffer.length);
        }
        return buffer;
    }

    /**
     * <功能描述> 打开扫描串口
     *
     * @param port
     * @param baudrate
     * @param bits
     * @param event
     * @param stop
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    private int openserialPort(String port, int baudrate, int bits, char event,
                               int stop) {
        // port:/dev/ttyS1; baudrate:9600
        LogUtil.trace("port:" + port + "; baudrate:" + baudrate);

        try {
            mSerialPort = this.getSerialPort(port, baudrate, bits, event, stop);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            begin = true;
            mReadThread = new ReadThread();
            mReadThread.start();
            return 0;
        } catch (SecurityException e) {
            LogUtil.d(TAG,
                    "You do not have read/write permission to the serial port.");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.d(TAG,
                    "The serial port can not be opened for an unknown reason.");
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            LogUtil.d(TAG, "Please configure your serial port first.");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * <功能描述> 自动选择协议内容 注意：使用本方法前 确保已经调用checkScannerModelSetting()方法 获取到了系统配置表中信息
     *
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    private int autoChoiseScanPrefix() {
        LogUtil.d(TAG, "autoChoiseScanPrefix");

        int model = getScannerModel();
        // 2
        LogUtil.d(TAG, "autoChoiseScanPrefix::return value of model is "
                + model);
        int prefix = -1;

        boolean isReturnFactory = getScanIsReturnFactory();
        LogUtil.d(TAG, "autoChoiseScanPrefix::isReturnFactory is "
                + isReturnFactory);

        switch (model) {
            case MODEL_N4313:
                if (isReturnFactory) {
                    prefix = Protocol_N4313_Default;
                } else {
                    // prefix = Prefix_N4313_Protocol;
                    prefix = Protocol_N4313_Codeid;
                }
                break;
            case MODEL_SE955:
                if (isReturnFactory) {
                    prefix = Protocol_SE955_Default;
                } else {
                    prefix = Protocol_SE955_Protocol;
                }
                break;
            case MODEL_EM3095:
                if (isReturnFactory) {
                    prefix = Protocol_EM3095_Default;
                } else {
                    prefix = Protocol_EM3095_Protocol;
                }
                break;
            case MODEL_EM3070:
                if (isReturnFactory) {
                    prefix = Protocol_EM3070_Default;
                } else {
                    prefix = Protocol_EM3070_Protocol;
                }
                break;
            case MODEL_N3680:
                if (isReturnFactory) {
                    prefix = Protocol_N3680_Default;
                } else {
                    prefix = Protocol_N3680_Codeid;
                    // 3680 协议与4313相同
                }
                break;

            default:
                break;
        }

        return prefix;
    }

    /**
     * <功能描述> 获取本地内存中扫描头设备类型 注意：使用本方法前 确保已经调用checkScannerModelSetting()方法
     * 获取到了系统配置表中信息
     *
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    private int getScannerModel() {
        int scannerModel = -1;
        scannerModel = Preference.getScannerModel(mServiceContext);
        return scannerModel;
    }

    /**
     * <功能描述> 设置扫描头设备类型 model 扫描头类型号
     *
     * @param model [参数说明]
     * @return void [返回类型说明]
     */
    private void setScannerModel(int model) {
        Preference.setScannerModel(mServiceContext, model);
    }

    /**
     * 设置扫描设备写入协议内容 解码将依据此设置 进行相应解码 <item>-1:无处理</item>
     * <item>0:N4313还原出厂设置</item> <item>1:N4313防止断码误码</item>
     * <item>2:N4313返回条码类型</item> <item>3:SE955还原出厂设置</item>
     * <item>4:SE955返回条码类型</item> <item>5:EM3095还原出厂设置</item>
     * <item>6:Em3095返回条码类型</item> <item>7:EM3070还原出厂设置</item>
     * <item>8:Em3070防止断码误码</item>
     */
    private void setScanPrefix() {
        LogUtil.d(TAG, "setScanPrefix");

        int prefix = autoChoiseScanPrefix();
        // 6
        LogUtil.trace("setScanPrefix::return value of prefix:" + prefix);

        mHandler.removeCallbacks(mCheckInitSuccess);
        init_index = 0;
        Preference.setScannerPrefix(mServiceContext, prefix);

        switch (prefix) {
            case Protocol_N4313_Codeid:
                setScannerModel(MODEL_N4313);
                break;

            case Protocol_N4313_Default:
                setScannerModel(MODEL_N4313);
                break;

            case Protocol_N4313_Protocol:
                setScannerModel(MODEL_N4313);
                break;

            case Protocol_EM3070_Default:
                setScannerModel(MODEL_EM3070);
                break;

            case Protocol_EM3070_Protocol:
                setScannerModel(MODEL_EM3070);
                break;

            case Protocol_EM3095_Default:
                setScannerModel(MODEL_EM3095);
                break;

            case Protocol_EM3095_Protocol:
                setScannerModel(MODEL_EM3095);
                break;

            case Protocol_SE955_Default:
                setScannerModel(MODEL_SE955);
                break;

            case Protocol_SE955_Protocol:
                setScannerModel(MODEL_SE955);
                break;

            default:
                break;
        }
    }

    /**
     * 返回扫描设备写入协议内容 解码将依据此设置 进行相应解码 <item>-1:无处理</item>
     * <item>0:N4313还原出厂设置</item> <item>1:N4313防止断码误码</item>
     * <item>2:N4313返回条码类型</item> <item>3:SE955还原出厂设置</item>
     * <item>4:SE955返回条码类型</item> <item>5:EM3095还原出厂设置</item>
     * <item>6:Em3095返回条码类型</item> <item>7:EM3070还原出厂设置</item>
     * <item>8:Em3070防止断码误码</item>
     */
    private int getScanPrefix() {
        return Preference.getScannerPrefix(mServiceContext);
    }

    /**
     * <功能描述> 模块上电
     *
     * @return void [返回类型说明]
     */
    private void power_up() {
        // SCAN_PWR_EM
        writeFile(new File(SCAN_PWR_EN), "0");
        writeFile(new File(IO_OE), "0");
        writeFile(new File(IO_CS0), "1");
        writeFile(new File(IO_CS1), "0");
    }

    /**
     * <功能描述> 模块下电
     *
     * @return void [返回类型说明]
     */
    private void power_down() {
        writeFile(new File(SCAN_PWR_EN), "0");
        writeFile(new File(IO_OE), "0");
        writeFile(new File(IO_CS0), "0");
        writeFile(new File(IO_CS1), "0");
    }

    /**
     * <功能描述> 扫描内容存储类 codeType 条码类型 content 条码String内存
     *
     * @author Administrator
     */
    private class ScanResultBean {
        private byte codeType;
        private byte[] content;

        public byte getCodeType() {
            return codeType;
        }

        public void setCodeType(byte codeType) {
            this.codeType = codeType;
        }

        public byte[] getContent() {
            return content;
        }

        public void setContent(byte[] content) {
            this.content = content;
        }
    }

    /**
     * 扫描数据回调接口 buffer：扫描数据byte数组 ， codeId : 条码类型
     */
    public interface Callback {
        /**
         * 扫描数据返回
         *
         * @param buffer 条码数据
         * @param codeId 条码类型
         * @param errorCode 错误码
         */
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode);
    }

    /**
     * <功能描述> 用于连续扫描线程
     *
     * @author Administrator
     */
    private class ScanThread extends Thread {
        public boolean run;

        @Override
        public void run() {
            super.run();
            LogUtil.d(TAG, "ScanThread is running...");

            while (run) {
                try {
                    if (mIsSerialPortOpen) {
                        Barcode_Start();
                        sleep(2000);
                        if (mIsSerialPortOpen) {
                            Barcode_Stop();
                        }
                    }
                } catch (InterruptedException e) {
                    try {
                        sleep(scan_time_limit);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
            if (mIsSerialPortOpen) {
                Barcode_Stop();
                mIsContinusStatus = false;
            }
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            LogUtil.d(TAG, "ReadThread is running...begin is " + begin
                    + "; mIsReceiveData is " + mIsReceiveData);

            while (begin) {
                if (mIsReceiveData) {
                    int size;

                    try {
                        if (mInputStream == null) {
                            LogUtil.d(TAG,
                                    "mInputStream is null, can not read from Serial Port...");
                            return;
                        }

                        int cout = mInputStream.available();
                        if (cout > 0) {
                            long nowTime = System.currentTimeMillis();
                            LogUtil.d(TAG, "ReadThread_Handle_Start:time is "
                                    + nowTime);
                            if (nowTime - mLastTime <= 20) {
                                // 时间相差较小，分割数据
                                mIsCutData = true;
                                mHandler.removeCallbacks(mSendDealDataBufferRunnable);
                            } else {
                                mIsCutData = false;
                            }
                        } else {
                            continue;
                        }

                        int temp = 0;
                        while (begin) {
                            if (mInputStream == null) {
                                return;
                            }

                            cout = mInputStream.available();
                            if (temp == cout) {
                                break;
                            }
                            temp = cout;
                        }

                        if (mInputStream == null) {
                            return;
                        }

                        cout = mInputStream.available();
                        byte[] buffer = new byte[cout];
                        size = mInputStream.read(buffer);
                        if (size > 0) {
                            if (mDataCallback != null) {
                                // TODO 为何只输出最后一个字节？
                                LogUtil.d(
                                        TAG,
                                        "ReadThread buffer: "
                                                + Tools.bytesToHexString(buffer));
                                if (!mIsCutData) {
                                    mCutDataBuffer = buffer;
                                    mCutDataBufferSize = size;
                                } else {
                                    if (null != mCutDataBuffer) {
                                        byte[] data3 = new byte[mCutDataBuffer.length
                                                + buffer.length];
                                        System.arraycopy(mCutDataBuffer, 0,
                                                data3, 0, mCutDataBuffer.length);
                                        System.arraycopy(buffer, 0, data3,
                                                mCutDataBuffer.length,
                                                buffer.length);
                                        mCutDataBuffer = new byte[data3.length];
                                        mCutDataBuffer = data3;
                                        mCutDataBufferSize += size;
                                    }
                                }
                                mIsCutData = false;
                                // 输出所有内容
                                LogUtil.d(
                                        TAG,
                                        "ReadThread cutData_buffer: "
                                                + Tools.bytesToHexString(mCutDataBuffer));
                                // 处理数据
                                mHandler.postAtTime(
                                        mSendDealDataBufferRunnable,
                                        SystemClock.uptimeMillis() + 19);
                                cout = 0;
                            }
                            mLastTime = System.currentTimeMillis();
                            mScanPreviousTime = mLastTime;
                        }
                    } catch (IOException e) { // end --> try
                        e.printStackTrace();
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                } else { // end --> if (isReceive)
                    // TODO do something
                }
            } // end --> while (begin)
        }
    }
}
