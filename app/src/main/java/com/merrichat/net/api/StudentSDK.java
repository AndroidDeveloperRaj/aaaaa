package com.merrichat.net.api;

import android.content.Context;

public class StudentSDK {
    private static Context appCtx = null;

    public static void init(Context actx) {
        appCtx = actx;
    }

    public static Context getAppCtx() {
        checkIfInited();
        return appCtx;
    }

    public static void checkIfInited() {
        if (appCtx == null) {
            throw new IllegalStateException("请先在Application的onCreate()中初始化SudentSDK.init(...)!");
        }
    }
}
