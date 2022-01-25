
package com.pos.salon.utils.update.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import com.pos.salon.utils.update.base.InstallStrategy;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.UpdateInstallProvider;
import java.io.File;

public class DefaultInstallStrategy extends InstallStrategy {

    private static String DEFAULT_AUTHOR = null;

    @Override
    public void install(Context context, String filename, final Update update) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        File apkFile = new File(filename);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            // Adaptive with api version 24+
            uri = UpdateInstallProvider.getUriByFile(apkFile, getAuthor(context));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);

    }

    private String getAuthor(Context context) {
        if (TextUtils.isEmpty(DEFAULT_AUTHOR)) {
            DEFAULT_AUTHOR = "update.plugin." + context.getPackageName() + ".UpdateInstallProvider";
        }
        return DEFAULT_AUTHOR;
    }
}
