package com.czhappy.autoinstall.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/8 0008
 * Time: 14:08
 */
public class OpenOtherApp {

    public static void openOtherAPP(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi != null) {
                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                resolveIntent.setPackage(pi.packageName);
                List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
                ResolveInfo ri = apps.iterator().next();
                if (ri != null) {
                    String pName = ri.activityInfo.packageName;
                    String className = ri.activityInfo.name;
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(pName, className);
                    intent.setComponent(cn);
                    context.startActivity(intent);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
