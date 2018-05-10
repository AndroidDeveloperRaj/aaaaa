package com.merrichat.net.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.merrichat.net.app.AppManager;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.SysApp;

/**
 * 用于监听手机开机启动的广播（主要用于开机启动服务）
 */
public class PowerOnBroadCastReceiver extends BroadcastReceiver {
    public PowerOnBroadCastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        LogUtil.d("@@@", "action===" + action);
        if (!SysApp.isServiceRun(context)) {
            context.startService(new Intent(context, BackGroudService.class));
            LogUtil.d("@@@", "服务开启");
        }
    }
}
