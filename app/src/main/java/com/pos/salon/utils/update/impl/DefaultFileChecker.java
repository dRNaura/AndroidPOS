
package com.pos.salon.utils.update.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.pos.salon.utils.update.base.FileChecker;
import com.pos.salon.utils.update.utilUpdate.ActivityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


public class DefaultFileChecker extends FileChecker {

    private void check() throws Exception {
        if (!TextUtils.isEmpty(update.getMd5())) {
            // 当md5值不为null时。使用md5进行检查
            checkByMD5();
        } else {
            checkByPM();
        }
    }

    private void checkByPM() {
        Context context = ActivityManager.get().getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        if (packageInfo.packageName == null || !packageInfo.packageName.equals(update.getPkg())) {
            throw new IllegalStateException(
                    String.format("The version code not matched between apk and update entity. apk is %s but update is %s",
                            packageInfo.versionCode, update.getVersionCode())
            );
        } else {
            // 后台不传递versionCode 自己手动传递
            update.setVersionCode(packageInfo.versionCode);
            update.setVersionName(packageInfo.versionName);
        }
    }

    private void checkByMD5() throws Exception {
        String md5 = getMD5(file);
        if (!update.getMd5().equalsIgnoreCase(md5)) {
            throw new RuntimeException(
                    String.format("The md5 not matched between apk and update entity. apk is %s but update is %s",
                            md5, update.getMd5())
            );
        }
    }

    @Override
    public boolean onCheckBeforeDownload() {
        try {
            check();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onCheckBeforeInstall() throws Exception {
        try {
            check();
        } catch (Exception e) {
            // 检查失败。删除apk
            file.delete();
            throw e;
        }
    }

    private String getMD5(File file) throws IOException {
        String md5str = "";
        InputStream is = null;
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            is = new FileInputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest();

            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return md5str;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }
}
