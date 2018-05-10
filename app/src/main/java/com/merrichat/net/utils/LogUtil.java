package com.merrichat.net.utils;

import android.util.Log;
import com.merrichat.net.app.MerriApp;


/**
 * Class: LogUtil
 * Describe:Log统一管理类
 * Date: 2016-04-14
 * Time: 18:34
 * FIXME
 */
public class LogUtil {

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (MerriApp.isDebug)
            Log.i(MerriApp.TAG_HTTP, msg);
    }

    public static void d(String msg) {
        if (MerriApp.isDebug)
            Log.d(MerriApp.TAG_HTTP, msg);
    }

    public static void e(String msg) {
        if (MerriApp.isDebug)
            Log.e(MerriApp.TAG_HTTP, msg);
    }

    public static void v(String msg) {
        if (MerriApp.isDebug)
            Log.v(MerriApp.TAG_HTTP, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (MerriApp.isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (MerriApp.isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (MerriApp.isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (MerriApp.isDebug)
            Log.v(tag, msg);
    }
}  
