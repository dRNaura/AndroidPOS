package com.pos.salon.utils.update.utilUpdate;

import android.util.Log;


public final class L {
    private static final String TAG = "UpdatePluginLog";
    public static boolean ENABLE = true;

    public static void d(String message, Object... args){
        if (ENABLE) {
            Log.d(TAG, String.format(message, args));
        }
    }

    public static void e(Throwable t, String message, Object... args) {
        if (ENABLE) {
            Log.e(TAG, String.format(message, args), t);
        }
    }
}
