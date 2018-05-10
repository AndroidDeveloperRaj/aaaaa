/**
 * probject:cim-android-sdk
 *
 * @version 2.0.0
 * @author 3979434@qq.com
 */
package com.merrichat.net.activity.message.cim.android;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.google.gson.Gson;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.constant.CIMConstant;
import com.merrichat.net.activity.message.cim.exception.CIMSessionDisableException;
import com.merrichat.net.activity.message.cim.model.ReplyBody;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.Logger;

import java.util.List;

/**
 * 消息入口，所有消息都会经过这里
 */
public abstract class CIMEventBroadcastReceiver extends BroadcastReceiver implements CIMEventListener {


    public Context context;
    public NotificationManager manager;// 通知管理器

    @Override
    public void onReceive(Context ctx, Intent it) {

        context = ctx;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         * 操作事件广播，用于提高service存活率
         */
        if (it.getAction().equals(Intent.ACTION_USER_PRESENT)
                || it.getAction().equals(Intent.ACTION_POWER_CONNECTED)
                || it.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)
                ) {
            startPushService();
        }


        /*
         * 设备网络状态变化事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_NETWORK_CHANGED)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            onDevicesNetworkChanged(info);
        }

        /*
         * cim断开服务器事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_CLOSED)) {
            if (CIMConnectorManager.netWorkAvailable(context)) {
                CIMPushManager.init(context);
            }
            onCIMConnectionClosed();
        }
        /*
         * cim连接服务器失败事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_FAILED)) {
            onConnectionFailed((Exception) it.getSerializableExtra("exception"));
            //重新连接
            CIMPushManager.init(context);
        }

        /*
         * cim连接服务器成功事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_SUCCESS)) {
            //保存当前账号,用于与服务器绑定
            CIMCacheTools.putString(context, CIMCacheTools.KEY_ACCOUNT, String.valueOf(UserModel.getUserModel().getMemberId()));
            CIMPushManager.bindAccount(context);

            onCIMConnectionSucceed();
        }

        /*
         * 收到推送消息事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_MESSAGE_RECEIVED)) {
            Logger.e(it.toString());
            MessageModel messageModel = (MessageModel) it.getSerializableExtra("message");
            Gson gson = new Gson();
            String string = gson.toJson(messageModel).toString();
            filterType999Message((MessageModel) it.getSerializableExtra("message"));
        }


        /*
         * 获取收到replybody成功事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_REPLY_RECEIVED)) {
            onReplyReceived((ReplyBody) it.getSerializableExtra("replyBody"));
        }


        /*
         * 获取sendbody发送失败事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_SENT_FAILED)) {
            SentBody sentBody = (SentBody) it.getSerializableExtra("sentBody");
            //如果是发送的消息类型，则不重新发送，直接保存发送失败状态
            if (Constant.SENT_BODY_KEY.equals(sentBody.getKey())) {
                onMessageSendFailure(sentBody);
            } else {
                //其它心跳什么的正常重新发送
                onSentFailed((Exception) it.getSerializableExtra("exception"), sentBody);
            }
        }

        /*
         * 获取sendbody发送成功事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_SENT_SUCCESS)) {
            onSentSucceed((SentBody) it.getSerializableExtra("sentBody"));
        }


        /*
         * 获取cim数据传输异常事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_UNCAUGHT_EXCEPTION)) {
            onUncaughtException((Exception) it.getSerializableExtra("exception"));
        }

        /*
         * 获取cim连接状态事件
         */
        if (it.getAction().equals(CIMConnectorManager.ACTION_CONNECTION_STATUS)) {
            onConnectionStatus(it.getBooleanExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, false));
            LogUtil.e("@@@", "******************CIM与服务器连接广播===》状态:" + it.getBooleanExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, false));
        }
    }

    protected boolean isInTopBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
    private void startPushService() {
        Intent intent = new Intent(context, CIMPushService.class);
        intent.putExtra(CIMPushManager.SERVICE_ACTION, CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE);
        context.startService(intent);
    }


    private void onConnectionFailed(Exception e) {

        if (CIMConnectorManager.netWorkAvailable(context)) {
            //间隔30秒后重连
            connectionHandler.sendMessageDelayed(connectionHandler.obtainMessage(), 30 * 1000);
        }
    }


    Handler connectionHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {

            CIMPushManager.init(context);
        }

    };


    private void onUncaughtException(Throwable arg0) {
    }


    private void onDevicesNetworkChanged(NetworkInfo info) {

        if (info != null) {
            CIMPushManager.init(context);
        }

        onNetworkChanged(info);
    }

    private void filterType999Message(MessageModel message) {
        if (CIMConstant.MessageType.TYPE_999.equals(message.getType())) {
            CIMCacheTools.putBoolean(context, CIMCacheTools.KEY_MANUAL_STOP, true);
        }

        onMessageReceived(message);
    }

    private void onSentFailed(Exception e, SentBody body) {

        //与服务端端开链接，重新连接
        if (e instanceof CIMSessionDisableException) {
            CIMPushManager.init(context);
        } else {
            //发送失败 重新发送
            CIMPushManager.sendRequest(context, body);
        }

    }

    private void onSentSucceed(SentBody body) {
    }

    @Override
    public abstract void onMessageReceived(MessageModel message);

    @Override
    public abstract void onReplyReceived(ReplyBody body);

    public abstract void onNetworkChanged(NetworkInfo info);

    public abstract void onCIMConnectionSucceed();

    public abstract void onCIMConnectionClosed();

    /**
     * 消息发送失败
     *
     * @param sentBody
     */
    public abstract void onMessageSendFailure(SentBody sentBody);
}
