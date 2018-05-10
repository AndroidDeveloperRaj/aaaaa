package com.merrichat.net.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.merrichat.net.app.MerriApp;

import java.util.List;

/**
 * 手机工具类
 *
 * @author shupeng
 */
public class PhoneUtils {

    /**
     * 跳转到拨号面板
     *
     * @param context
     * @param phoneNumber
     */
    public static void callPhone(Context context, String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    /**
     * 跳转到发送短信界面
     *
     * @param context
     * @param phoneNum
     * @param boby
     */
    public static void sendSMS(Context context, String phoneNum, String boby) {
        Uri uri = Uri.parse("smsto:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", boby);
        context.startActivity(intent);
    }

    /**
     * 获得版本号
     *
     * @param c
     * @return
     */
    public static String getVersionName(Context c) {
        PackageManager pm = c.getPackageManager();
        PackageInfo pinfo = null;
        try {
            pinfo = pm.getPackageInfo(c.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionName;
    }

    /**
     * 获得版本code
     *
     * @param c
     * @return
     */
    public static int getVersionCode(Context c) {
        PackageManager pm = c.getPackageManager();
        PackageInfo pinfo = null;
        try {
            pinfo = pm.getPackageInfo(c.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo.versionCode;
    }

    /**
     * 检查sd卡是否存在
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 隐藏键盘
     *
     * @param context
     */
    public static void hidekeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     *
     * @param context
     */
    public static void showkeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    /**
     * 判断App是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Logger.i("后台", appProcess.processName);
                    return true;
                } else {
                    Logger.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    //当前应用是否处于前台
    public static boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
                return true;
            }
            return false;
        }
        return false;
    }

}
