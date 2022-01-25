
package com.pos.salon.utils.update.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.pos.salon.utils.update.base.UpdateStrategy;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.ActivityManager;

public class WifiFirstStrategy extends UpdateStrategy {

    private boolean isWifi;

    @Override
    public boolean isShowUpdateDialog(Update update) {
        //isWifi = isConnectedByWifi();
        isWifi = true;
        return isWifi;
    }

    @Override
    public boolean isAutoInstall() {
        return isWifi;
    }

    @Override
    public boolean isShowDownloadDialog() {
        return isWifi;
    }

    private boolean isConnectedByWifi() {
        Context context = ActivityManager.get().getApplicationContext();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null
                && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
