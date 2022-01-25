package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private static final String TAG = Preference.class.getSimpleName();

    private static SharedPreferences getSP(Context context) {
        String name = context.getString(ResourceUtil.getStringResIDByName(
                context, "app_name"));
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean isScreenOn(Context context) {
        return getSP(context).getBoolean("IsScreenOn", true);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param value [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScreenOn(Context context, boolean value) {
        getSP(context).edit().putBoolean("IsScreenOn", value).commit();
    }

    /**
     * <功能描述> 扫描头类型
     *
     * @param context
     * @return [参数说明] 默认为-1
     * @return int [返回类型说明]
     */
    public static int getScannerModel(Context context) {
        return getSP(context).getInt("ScannerModel", -1);
    }

    /**
     * 扫描头类型
     *
     * @param context
     * @param value
     */
    public static void setScannerModel(Context context, int value) {
        getSP(context).edit().putInt("ScannerModel", value).commit();
    }

    /**
     * <功能描述> 扫描头协议编号
     *
     * @param context
     * @return [参数说明] 默认为-1
     * @return int [返回类型说明]
     */
    public static int getScannerPrefix(Context context) {
        return getSP(context).getInt("ScannerPrefix", -1);
    }

    /**
     * <功能描述> 扫描头协议编号
     *
     * @param context
     * @param value [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScannerPrefix(Context context, int value) {
        getSP(context).edit().putInt("ScannerPrefix", value).commit();
    }

    /**
     * <功能描述> 扫描头是否恢复出厂设置
     *
     * @param context
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScannerIsReturnFactory(Context context) {
        return getSP(context).getBoolean("IsReturnFactory", false);
    }

    /**
     * <功能描述> 设置扫描头恢复出厂设置
     *
     * @param context
     * @param isReturnFactory [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScannerIsReturnFactory(Context context,
                                                 boolean isReturnFactory) {
        getSP(context).edit().putBoolean("IsReturnFactory", isReturnFactory)
                .commit();
    }

    /**
     * <功能描述> 扫描类型（一维、二维）
     *
     * @param context
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    public static int getScanDeviceType(Context context) {
        return getSP(context).getInt("ScanDeviceType", 1);
    }

    /**
     * <功能描述> 扫描类型（一维、二维）
     *
     * @param context
     * @param value [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanDeviceType(Context context, int value) {
        getSP(context).edit().putInt("ScanDeviceType", value).commit();
    }

    /**
     * <功能描述> 获取后台扫描输出模式 默认快速扫描
     *
     * @param context
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    public static int getScanOutMode(Context context) {
        return getSP(context).getInt("ScanOutMode", 1);
    }

    /**
     * <功能描述> 后台扫描输出模式 默认快速扫描
     *
     * @param context
     * @param ScanOutMode [参数说明] 1.快速扫描（文本框） 2.模拟键盘 3.广播
     * @return void [返回类型说明]
     */
    public static void setScanOutMode(Context context, int ScanOutMode) {
        getSP(context).edit().putInt("ScanOutMode", ScanOutMode).commit();
    }

    /**
     * <功能描述> 设置是否支持网页扫描
     *
     * @param context
     * @param isNetPageSupport [参数说明]
     * @return void [返回类型说明]
     */
    public static void setNetPageSupport(Context context,
                                         boolean isNetPageSupport) {
        getSP(context).edit().putBoolean("IsNetPageSupport", isNetPageSupport)
                .commit();
    }

    /**
     * <功能描述> 获取是否支持网页扫描
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getNetPageSupport(Context context,
                                            boolean defaultValues) {
        return getSP(context).getBoolean("IsNetPageSupport", defaultValues);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanSound [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanSound(Context context, boolean isScanSound) {
        getSP(context).edit().putBoolean("IsScanSound", isScanSound).commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanSound(Context context, boolean defaultValues) {
        return getSP(context).getBoolean("IsScanSound", defaultValues);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanVibration [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanVibration(Context context, boolean isScanVibration) {
        getSP(context).edit().putBoolean("IsScanVibration", isScanVibration)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanVibration(Context context,
                                           boolean defaultValues) {
        return getSP(context).getBoolean("IsScanVibration", defaultValues);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanSaveTxt [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanSaveTxt(Context context, boolean isScanSaveTxt) {
        getSP(context).edit().putBoolean("IsScanSaveTxt", isScanSaveTxt)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanSaveTxt(Context context, boolean defaultValues) {
        return getSP(context).getBoolean("IsScanSaveTxt", defaultValues);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanSaveTxt [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanTest(Context context, boolean isScanSaveTxt) {
        getSP(context).edit().putBoolean("IsScanTest", isScanSaveTxt).commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanTest(Context context) {
        return getSP(context).getBoolean("IsScanSaveTxt", false);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanShortcutSupport [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanShortcutSupport(Context context,
                                              boolean isScanShortcutSupport) {
        getSP(context).edit()
                .putBoolean("IsScanShortcutSupport", isScanShortcutSupport)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanShortcutSupport(Context context,
                                                 boolean defaultValues) {
        return getSP(context)
                .getBoolean("IsScanShortcutSupport", defaultValues);
    }

    /**
     * <功能描述> 设置扫描快捷键
     *
     * @param context
     * @param scanShortcutMode [参数说明] 1:左侧橙色按键 2:中间橙色按键 3:右边橙色按键
     * @return void [返回类型说明]
     */
    public static void setScanShortcutMode(Context context,
                                           String scanShortcutMode) {
        getSP(context).edit().putString("ScanShortcutMode", scanShortcutMode)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return String [返回类型说明]
     */
    public static String getScanShortcutMode(Context context,
                                             String defaultValues) {
        return getSP(context).getString("ScanShortcutMode", defaultValues);
    }

    /**
     * <功能描述> 设置扫描快捷键按键模式
     *
     * @param context
     * @param scanShortCutPressMode [参数说明] 1:3秒后自动收光 2:抬起按键收光
     * @return void [返回类型说明]
     */
    public static void setScanShortCutPressMode(Context context,
                                                int scanShortCutPressMode) {
        getSP(context).edit()
                .putInt("ScanShortCutPressMode", scanShortCutPressMode)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @return [参数说明]
     * @return int [返回类型说明]
     */
    public static int getScanShortCutPressMode(Context context) {
        return getSP(context).getInt("ScanShortCutPressMode", 1);
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param isScanSelfopenSupport [参数说明]
     * @return void [返回类型说明]
     */
    public static void setScanSelfopenSupport(Context context,
                                              boolean isScanSelfopenSupport) {
        getSP(context).edit()
                .putBoolean("IsScanSelfopenSupport", isScanSelfopenSupport)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getScanSelfopenSupport(Context context,
                                                 boolean defaultValues) {
        return getSP(context)
                .getBoolean("IsScanSelfopenSupport", defaultValues);
    }

    /**
     * <功能描述> 设置后台扫描的后缀
     *
     * @param context
     * @param suffixModel [参数说明] -1为没有，0为回车，1为分号
     * @return void [返回类型说明]
     */
    public static void setScanSuffixModel(Context context, int suffixModel) {
        if (suffixModel >= -1 && suffixModel <= 1)
            getSP(context).edit().putInt("ScanSuffixModel", suffixModel)
                    .commit();
    }

    /**
     * 获取后台扫描的后缀
     *
     * @param context
     * @param defalutValue -1为没有，0为回车，1为分号
     * @return -1为没有，0为回车，1为分号
     */
    public static int getScanSuffixModel(Context context, int defalutValue) {
        if (defalutValue >= -1 && defalutValue <= 1)
            return getSP(context).getInt("ScanSuffixModel", defalutValue);
        else
            return getSP(context).getInt("ScanSuffixModel", 0);
    }

    /**
     * <功能描述> 设置NFC一键出光
     *
     * @param context
     * @param isScanSelfopenSupport [参数说明]
     * @return void [返回类型说明]
     */
    public static void setNfcBackgroundSupport(Context context,
                                               boolean isScanSelfopenSupport) {
        getSP(context).edit()
                .putBoolean("IsScanSelfopenSupport", isScanSelfopenSupport)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getNfcBackgroundSupport(Context context,
                                                  boolean defaultValues) {
        return getSP(context)
                .getBoolean("IsScanSelfopenSupport", defaultValues);
    }

    /**
     * <功能描述> 设置NFC一键出光模式
     *
     * @param context
     * @param isNfcSimulateKeySupport [参数说明]
     * @return void [返回类型说明]
     */
    public static void setNfcSimulateKeySupport(Context context,
                                                boolean isNfcSimulateKeySupport) {
        getSP(context).edit()
                .putBoolean("IsNfcSimulateKeySupport", isNfcSimulateKeySupport)
                .commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @param defaultValues
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getNfcSimulateKeySupport(Context context,
                                                   boolean defaultValues) {
        return getSP(context).getBoolean("IsNfcSimulateKeySupport",
                defaultValues);
    }


    public static void setIsFirstStartNot(Context context) {
        getSP(context).edit().putBoolean("IsFirstStart", false).commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getIsFirstStartNot(Context context) {
        return getSP(context).getBoolean("IsFirstStart", true);
    }


    public static void setScanInit(Context context, boolean isture) {
        LogUtil.d(TAG, "setScanInit: parameter is " + isture);
        getSP(context).edit().putBoolean("IsScanInit", isture).commit();
    }

    /**
     * <功能描述>
     *
     * @param context
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    public static boolean getIsScanInit(Context context) {
        return getSP(context).getBoolean("IsScanInit", false);
    }
}
