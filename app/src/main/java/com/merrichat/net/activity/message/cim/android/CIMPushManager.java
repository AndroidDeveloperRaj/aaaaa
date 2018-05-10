/**
 * probject:cim-android-sdk
 *
 * @version 2.0.0
 * @author 3979434@qq.com
 */
package com.merrichat.net.activity.message.cim.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.constant.CIMConstant;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import okhttp3.Call;


/**
 * CIM 功能接口
 */
public class CIMPushManager {

    public static String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

    public static String ACTION_CONNECTION = "ACTION_CONNECTION";

    public static String ACTION_CONNECTION_STATUS = "ACTION_CONNECTION_STATUS";

    public static String ACTION_SENDREQUEST = "ACTION_SENDREQUEST";

    public static String ACTION_DISCONNECTION = "ACTION_DISSENDREQUEST";

    public static String ACTION_DESTORY = "ACTION_DESTORY";

    public static String SERVICE_ACTION = "SERVICE_ACTION";

    public static String KEY_SEND_BODY = "KEY_SEND_BODY";

    public static String KEY_CIM_CONNECTION_STATUS = "KEY_CIM_CONNECTION_STATUS";

    /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
     *
     * @param context
     * @param ip
     * @param port
     */
    public static void init(Context context, String ip, int port) {
        LogUtil.e("@@@", "init111111");
        if (TextUtils.isEmpty(ip) || port == 0) {
            //请求接口获取聊天服务器ip和端口
            allocate(context);
        } else {
            CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED, false);
            CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_MANUAL_STOP, false);

            CIMCacheTools.putString(context, CIMCacheTools.KEY_CIM_SERVIER_HOST, ip);
            CIMCacheTools.putInt(context, CIMCacheTools.KEY_CIM_SERVIER_PORT, port);
            Intent serviceIntent = new Intent(context, CIMPushService.class);
            serviceIntent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST, ip);
            serviceIntent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, port);
            serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION);
            context.startService(serviceIntent);
        }
    }

    protected static void init(Context context) {
        LogUtil.e("@@@", "init");
        boolean isManualStop = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_MANUAL_STOP);
        boolean isManualDestory = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED);

        if (isManualStop || isManualDestory) {
            return;
        }

        String host = CIMCacheTools.getString(context, CIMCacheTools.KEY_CIM_SERVIER_HOST);
        int port = CIMCacheTools.getInt(context, CIMCacheTools.KEY_CIM_SERVIER_PORT);

        init(context, host, port);

    }


    /**
     * 设置一个账号登录到服务端
     *
     * @param account 用户唯一ID
     */
    public static void bindAccount(Context context, String account) {
        LogUtil.e("@@@", "bindAccount");
        boolean isManualDestory = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED);
        if (isManualDestory || account == null || account.trim().length() == 0) {
            return;
        }

        CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_MANUAL_STOP, false);
        CIMCacheTools.putString(context, CIMCacheTools.KEY_ACCOUNT, account);

        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        imei += context.getPackageName();
        SentBody sent = new SentBody();
        sent.setKey(CIMConstant.RequestKey.CLIENT_BIND);
        sent.put("account", account);
        sent.put("deviceId", UUID.nameUUIDFromBytes(imei.getBytes()).toString().replaceAll("-", ""));
        sent.put("channel", "android");
        sent.put("device", android.os.Build.MODEL);
        sent.put("appVersion", getVersionName(context));
        sent.put("osVersion", android.os.Build.VERSION.RELEASE);
        sent.put("source", "1");
        sent.put("mstype", "1");
        sendRequest(context, sent);
        LogUtil.e("@@@", "绑定到CIM服务器");
    }


    protected static void bindAccount(Context context) {
        LogUtil.e("@@@", "bindAccount22222");
        String account = CIMCacheTools.getString(context, CIMCacheTools.KEY_ACCOUNT);
        LogUtil.e("@@@", "account111===" + account);
        if (TextUtils.isEmpty(account)) {
            account = String.valueOf(UserModel.getUserModel().getMemberId());
        }
        LogUtil.e("@@@", "account222===" + account);
        bindAccount(context, account);

    }


    /**
     * 发送一个CIM请求
     *
     * @param context
     * @body
     */
    public static void sendRequest(Context context, SentBody body) {
        boolean isManualStop = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_MANUAL_STOP);
        boolean isManualDestory = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED);

        if (isManualStop || isManualDestory) {
            return;
        }

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(KEY_SEND_BODY, body);
        serviceIntent.putExtra(SERVICE_ACTION, ACTION_SENDREQUEST);
        context.startService(serviceIntent);

    }

    /**
     * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
     *
     * @param context
     */
    public static void stop(Context context) {

        boolean isManualDestory = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED);
        if (isManualDestory) {
            return;
        }

        CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_MANUAL_STOP, true);

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(SERVICE_ACTION, ACTION_DISCONNECTION);
        context.startService(serviceIntent);

    }


    /**
     * 完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复
     *
     * @param context
     */
    public static void destory(Context context) {


        CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED, true);
        CIMCacheTools.putString(context, CIMCacheTools.KEY_ACCOUNT, null);

        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(SERVICE_ACTION, ACTION_DESTORY);
        context.startService(serviceIntent);

    }


    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号
     *
     * @param context
     */
    public static void resume(Context context) {

        boolean isManualDestory = CIMCacheTools.getBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED);
        if (isManualDestory) {
            return;
        }

        bindAccount(context);
    }

    public static void detectIsConnected(Context context) {
        Intent serviceIntent = new Intent(context, CIMPushService.class);
        serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION_STATUS);
        context.startService(serviceIntent);
    }


    private static String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = mPackageInfo.versionName;
        } catch (NameNotFoundException e) {
        }
        return versionName;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceRun(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    /**
     * 获取CIM聊天连接地址和端口
     */
    private static void allocate(final Context context) {
        OkGo.<String>post(Urls.CIM_ALLOCATE)
                .params("mid", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());

                            boolean success = jsonObject.optBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.optJSONObject("data");
//                        String receiver = data.optString("receiver");
                                //服务器ip
                                String socketIp = data.optString("socketIp");
                                //服务器socket端口
                                String socketPort = data.optString("socketPort");
                                //服务器CIM请求接口端口
                                String httpPort = data.optString("httpPort");
                                if (!TextUtils.isEmpty(httpPort)) {
                                    Constant.CIM_SERVER_HTTP_PORT = httpPort;
                                }
                                Constant.CIM_SERVER_HOST = socketIp;
                                Constant.CIM_SERVER_PORT = Integer.parseInt(socketPort);

                                CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_CIM_DESTORYED, false);
                                CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_MANUAL_STOP, false);

                                CIMCacheTools.putString(context, CIMCacheTools.KEY_CIM_SERVIER_HOST, Constant.CIM_SERVER_HOST);
                                CIMCacheTools.putInt(context, CIMCacheTools.KEY_CIM_SERVIER_PORT, Constant.CIM_SERVER_PORT);

                                Intent serviceIntent = new Intent(context, CIMPushService.class);
                                serviceIntent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST, Constant.CIM_SERVER_HOST);
                                serviceIntent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, Constant.CIM_SERVER_PORT);
                                serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION);
                                context.startService(serviceIntent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                });
    }

}