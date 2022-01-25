
package com.pos.salon.utils.update.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.pos.salon.utils.update.base.InstallNotifier;
import com.pos.salon.utils.update.utilUpdate.SafeDialogHandle;
import java.lang.reflect.Field;
import java.util.Locale;


public class DefaultInstallNotifier extends InstallNotifier {

    private static String mVersion = "Version: ";
    private static String mTitle = "Is it installed?";
    private static String mInstall = "Install";
    private static String mIgnore = "Ignore";
    private static String mCancel = "Cancel";

    static {
        if (Locale.getDefault().toString().contains(Locale.CHINA.toString())) {
            mVersion = "版本号: ";
            mTitle = "安装包已就绪，是否安装？";
            mInstall = "立即安装";
            mIgnore = "忽略此版本";
            mCancel = "取消";
        }
    }
    @Override
    public Dialog create(Activity activity) {
        String updateContent = String.format(mVersion + "%s\n\n%s",
                update.getVersionName(), update.getUpdateContent());
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(mTitle)
                .setMessage(updateContent)
                .setPositiveButton(mInstall, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (update.isForced()) {
                            preventDismissDialog(dialog);
                        } else {
                            SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                        }
                        sendToInstall();
                    }
                });

        if (!update.isForced() && update.isIgnore()) {
            builder.setNeutralButton(mIgnore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendCheckIgnore();
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
        AlertDialog installDialog = builder.create();
        installDialog.setCancelable(false);
        installDialog.setCanceledOnTouchOutside(false);
        return installDialog;
    }

    /**
     * 通过反射 阻止自动关闭对话框
     */
    private void preventDismissDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialog, false);
        } catch (Exception e) {
            // ignore
        }
    }

}
