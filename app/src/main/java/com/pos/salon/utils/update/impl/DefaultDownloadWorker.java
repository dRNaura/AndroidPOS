
package com.pos.salon.utils.update.impl;

import android.text.TextUtils;
import com.pos.salon.utils.update.base.DownloadWorker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultDownloadWorker extends DownloadWorker {

    private HttpURLConnection urlConn;
    private File original;
    private File bak;
    private long contentLength;

    @Override
    protected void download(String url, File target) throws Exception {
        original = target;
        URL httpUrl = new URL(url);
        urlConn = (HttpURLConnection) httpUrl.openConnection();
        setDefaultProperties();
        urlConn.connect();

        int responseCode = urlConn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            urlConn.disconnect();
            throw new HttpException(responseCode,urlConn.getResponseMessage());
        }

        contentLength = urlConn.getContentLength();
        // 使用此bak文件进行下载。辅助进行断点下载。
        if (checkIsDownAll()) {
            urlConn.disconnect();
            urlConn = null;
            // notify download completed
            sendDownloadComplete(original);
            return;
        }

        createBakFile();
        FileOutputStream writer = supportBreakpointDownload(httpUrl);

        long offset = bak.length();
        InputStream inputStream = urlConn.getInputStream();
        byte[] buffer = new byte[8 * 1024];
        int length;
        long start = System.currentTimeMillis();
        while ((length = inputStream.read(buffer)) != -1) {
            writer.write(buffer, 0, length);
            offset += length;
            long end = System.currentTimeMillis();
            if (end - start > 1000) {
                sendDownloadProgress(offset,contentLength);
                start = System.currentTimeMillis();
            }
        }

        urlConn.disconnect();
        writer.close();
        urlConn = null;

        // notify download completed
        renameAndNotifyCompleted();
    }

    private boolean checkIsDownAll() {
        return original.length() == contentLength
                && contentLength > 0;
    }

    private FileOutputStream supportBreakpointDownload(URL httpUrl) throws IOException {

        String range = urlConn.getHeaderField("Accept-Ranges");
        if (TextUtils.isEmpty(range) || !range.startsWith("bytes")) {
            bak.delete();
            return new FileOutputStream(bak, false);
        }

        long length = bak.length();

        urlConn.disconnect();
        urlConn = (HttpURLConnection) httpUrl.openConnection();

        urlConn.setRequestProperty("RANGE", "bytes=" + length + "-" + contentLength);
        setDefaultProperties();
        urlConn.connect();

        int responseCode = urlConn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new HttpException(responseCode,urlConn.getResponseMessage());
        }

        return new FileOutputStream(bak, true);
    }

    private void setDefaultProperties() throws IOException {
        urlConn.setRequestProperty("Content-Type","text/html; charset=UTF-8");
        urlConn.setRequestMethod("GET");
        urlConn.setConnectTimeout(10000);
    }

    // 创建bak文件。
    private void createBakFile() {
        bak = new File(String.format("%s_%s", original.getAbsolutePath(), contentLength));
    }

    private void renameAndNotifyCompleted() {
        original.delete();
        bak.renameTo(original);
        sendDownloadComplete(original);
    }
}
