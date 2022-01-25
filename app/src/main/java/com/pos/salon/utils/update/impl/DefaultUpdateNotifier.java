
package com.pos.salon.utils.update.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.pos.salon.utils.update.base.CheckNotifier;
import com.pos.salon.utils.update.utilUpdate.SafeDialogHandle;
import java.util.Locale;

public class DefaultUpdateNotifier extends CheckNotifier {

    private static String mVersion = "Version: ";
    private static String mTitle = "Find new version";
    private static String mUpdate = "Update";
    private static String mIgnore = "Ignore";
    private static String mCancel = "Cancel";

    static {
        if (Locale.getDefault().toString().contains(Locale.CHINA.toString())) {
            mVersion = "版本号: ";
            mTitle = "你有新版本需要更新";
            mUpdate = "立即更新";
            mIgnore = "忽略此版本";
            mCancel = "取消";
        }
    }

    @Override
    public Dialog create(Activity activity) {
        StringBuilder content = new StringBuilder();
        if (update.getVersionName() != null) {
            content.append(mVersion + update.getVersionName() + "\n\n");
        }
        if (update.getUpdateContent() != null) {
            content.append(update.getUpdateContent());
        }
        String updateContent = content.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage(updateContent)
                .setTitle(mTitle)
                .setPositiveButton(mUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDownloadRequest();
                        SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                    }
                });
        if (update.isIgnore() && !update.isForced()) {
            builder.setNeutralButton(mIgnore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendUserIgnore();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                }
            });
        }

        if (!update.isForced()) {
            builder.setNegativeButton(mCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendUserCancel();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                }
            });
        }
        builder.setCancelable(false);
        return builder.create();
    }
}
