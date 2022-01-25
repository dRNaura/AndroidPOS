
package com.pos.salon.utils.update.utilUpdate;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

public class Utils {

    private static Handler handler;

    public static Handler getMainHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public static boolean isValid(Activity activity) {
        return activity != null && !activity.isFinishing();
    }
}
