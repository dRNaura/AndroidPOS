
package com.pos.salon.utils.update.utilUpdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;


public class SafeDialogHandle {

    public static void safeShowDialog(Dialog dialog) {
        if (dialog == null || dialog.isShowing()) {
            return;
        }
        Activity bindAct = getActivity(dialog);

        if (!Utils.isValid(bindAct)) {
            L.d("Dialog shown failed:%s", "The Dialog bind's Activity was recycled or finished!");
            return;
        }

        dialog.show();
    }

    private static Activity getActivity(Dialog dialog) {
        Activity bindAct = null;
        Context context = dialog.getContext();
        do {
            if (context instanceof Activity) {
                bindAct = (Activity) context;
                break;
            } else if (context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
            } else {
                break;
            }
        } while (true);
        return bindAct;
    }

    public static void safeDismissDialog(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) {
            return;
        }

        Activity bindAct = getActivity(dialog);
        if (bindAct != null && !bindAct.isFinishing()) {
            dialog.dismiss();
        }
    }
}
