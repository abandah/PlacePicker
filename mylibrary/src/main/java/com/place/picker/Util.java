package com.place.picker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Abandah on 9/22/2020.
 */
public class Util {

    public static String getAppKey(Context activity) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
    }

}
