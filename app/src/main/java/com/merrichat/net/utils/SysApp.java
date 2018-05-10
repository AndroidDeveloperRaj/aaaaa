package com.merrichat.net.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.message.cim.android.CIMPushManager;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.NoticeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.service.BackGroudService;
import com.merrichat.net.utils.RxTools.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.List;

/**
 * Created by amssy on 18/1/13.
 * 调用系统应用的类
 */

public final class SysApp {

    /**
     * @param @param  context
     * @param @return
     * @return boolean
     * @throws
     * @Title: isServiceRun
     * @Description: TODO判断service是否正在运行
     */
    public static boolean isServiceRun(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceInfos == null)
            return false;

        for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
            if (serviceInfo.process.equals(packageName) && serviceInfo.service.getClassName().equals("com.merrichat.net.service.BackGroudService")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取设备Id
     *
     * @return
     */
    public static String getDeviceId(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    // 注销账号的回调
    public static void lognOut(Context context, boolean isLogout,boolean IS_OFF_LINE) {
        if (isLogout) {
            loginOut(context);
        }

        // 注销当前账号，保存账号名，去掉密码，取消自动登录。
        UserModel.getUserModel().setIsLogin(false);


        //清空当前账号资料信息
        UserModel.deleteUserModel(UserModel.getUserModel());

        //断开连接(即时通讯中session断开)
        CIMPushManager.destory(context);

        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.CLOSE_MYACTIVITY = true;
        EventBus.getDefault().post(myEventBusModel);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("IS_OFF_LINE",IS_OFF_LINE);
        context.startActivity(intent);
    }

    /**
     * @param @param cnt
     * @return void
     * @throws
     * @Title: stopNoticeService
     * @Description: TODO 停止推送service
     */
    public static void stopNoticeService(Context cnt) {
        if (BackGroudService.session != null) {
            BackGroudService.session.close(true);
        }
        BackGroudService.PUSH_SERVER_CONNECT = false;
        cnt.stopService(new Intent(cnt, BackGroudService.class));
        LogUtil.e("@@@", "服务停止");
    }


    /**
     * 退出登录
     */
    private static void loginOut(final Context context) {
        OkGo.<String>get(Urls.deleteMobMemberLogin)
                .tag(context)
                .params("mobMemberLoginId", UserModel.getUserModel().getMobMemberLoginId())
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    RxToast.showToast("退出成功");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }
                });

    }
}
