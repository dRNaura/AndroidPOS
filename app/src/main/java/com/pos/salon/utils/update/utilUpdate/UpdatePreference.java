
package com.pos.salon.utils.update.utilUpdate;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;


public class UpdatePreference {

    private static final String PREF_NAME = "update_preference";

    public static Set<String> getIgnoreVersions () {
        return getUpdatePref().getStringSet("ignoreVersions", new HashSet<String>());
    }

    public static void saveIgnoreVersion(int versionCode) {
        Set<String> ignoreVersions = getIgnoreVersions();
        if (!ignoreVersions.contains(String.valueOf(versionCode))) {
            ignoreVersions.add(String.valueOf(versionCode));
            getUpdatePref().edit().putStringSet("ignoreVersions",ignoreVersions).apply();
        }
    }

    public static void saveIgnoreVersion(String versionName) {
        Set<String> ignoreVersions = getIgnoreVersions();
        if (!ignoreVersions.contains(versionName)) {
            ignoreVersions.add(versionName);
            getUpdatePref().edit().putStringSet("ignoreVersions",ignoreVersions).apply();
        }
    }

    private static SharedPreferences getUpdatePref () {
        return ActivityManager.get().getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
