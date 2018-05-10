package com.merrichat.net.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.ForceOfflineDialogAty;
import com.merrichat.net.activity.meiyu.NewMeetNiceActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.activity.message.ExpressNotificationActivity;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.NewFriendsActivity;
import com.merrichat.net.activity.message.cim.android.CIMCacheTools;
import com.merrichat.net.activity.message.cim.android.CIMPushManager;
import com.merrichat.net.activity.message.cim.constant.CIMConstant;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.activity.message.receiver.ServiceShowDialogReceiver;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.UrlConfig;
import com.merrichat.net.model.ExpressNotificationModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.PhoneUtils;
import com.merrichat.net.utils.SysApp;
import com.merrichat.net.view.DialogOnClickListener;
import com.merrichat.net.view.RemindDialog;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 讯美推送服务
 */
public class BackGroudService extends Service {

    private static final String TAG = "BackGroudService";
    public static boolean PUSH_SERVER_CONNECT = false;// 是否已启动推送服务
    public static IoSession session;
    Handler handler;
    // 启动推送服务
    IoConnector connector;
    private Notification notification;
    private String MSG_HOST_IP = UrlConfig.getPushUrl();//推送Socket的url
    private int MSG_HOST_PORT = UrlConfig.getPushPort();//推送Socket的端口号
    private boolean running = true;
    private ActivityManager activityManager;
    private Thread th;
    private String device_id;
    private int i = 0;
    private NotificationManager manager;// 通知管理器

    private void sendHeartBeat(Context context) {
        SentBody sent = new SentBody();
        sent.setKey(CIMConstant.RequestKey.CLIENT_HEARTBEAT);
        sent.put("account", UserModel.getUserModel().getMemberId());
        CIMPushManager.sendRequest(context, sent);
        Logger.e(sent.toString());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        device_id = SysApp.getDeviceId(getApplicationContext());
        startConnectSocket();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                final NoticeMsg noticeMsg = (NoticeMsg) msg.obj;
                switch (msg.what) {
                    case 0:
                        Logger.e("立即心跳");
//                        sendHeartBeat(BackGroudService.this);
                        String host = CIMCacheTools.getString(BackGroudService.this, CIMCacheTools.KEY_CIM_SERVIER_HOST);
                        int port = CIMCacheTools.getInt(BackGroudService.this, CIMCacheTools.KEY_CIM_SERVIER_PORT);
                        if (!CIMPushManager.isServiceRun(BackGroudService.this, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                            // 连接服务端(即时聊天)
                            CIMPushManager.init(BackGroudService.this, host, port);
                        } else {
                            CIMPushManager.bindAccount(BackGroudService.this, String.valueOf(UserModel.getUserModel().getMemberId()));
                        }
                        break;
                    case 1://应用在前端运行
                        Handler handlerRun = new Handler(Looper.getMainLooper());
                        handlerRun.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("noticeMsg", noticeMsg);
                                intent.setAction(ServiceShowDialogReceiver.SHOW_DIALOG_RECEIVER);
                                sendBroadcast(intent);
                            }
                        });
                        break;

                    case 2://应用在后台运行
                        showNotification(noticeMsg);
                        break;

                    case 3://帐号在其它设备登录
                        Handler handlerRun2 = new Handler(Looper.getMainLooper());
                        handlerRun2.post(new Runnable() {
                            @Override
                            public void run() {
                                if (UserModel.getUserModel().getMemberId().equals(noticeMsg.getMemberId())) {
                                    SysApp.lognOut(BackGroudService.this, true,true);
                                    SysApp.stopNoticeService(BackGroudService.this);
                                }
                            }
                        });
                        break;
                }

            }
        };
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.login_type || myEventBusModel.Login_type_change) {
            if (session != null) {
                if (UserModel.getUserModel().getIsLogin()) {
                    session.write("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_LOGIN_SUCCESS
                            + "}");
                    LogUtil.d("@@@", "{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_LOGIN_SUCCESS
                            + "}");

                } else {
                    session.write("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + myEventBusModel.LOGIN_EXIT_BEFORE_MEMBERID + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_LOGIN_EXIT
                            + "}");
                    LogUtil.d("@@@", "{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + myEventBusModel.LOGIN_EXIT_BEFORE_MEMBERID + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_LOGIN_EXIT
                            + "}");
                }
            }
        }
    }

    /**
     * 启动服务
     */
    private void startConnectSocket() {
        th = new Thread() {
            @Override
            public void run() {
                while (running) {
                    try {
                        if (!PUSH_SERVER_CONNECT && NetUtils.isNetworkAvailable(BackGroudService.this)) {
                            if (TextUtils.isEmpty(MSG_HOST_IP)) {
                                MSG_HOST_IP = UrlConfig.getPushUrl();
                            }
                            if (session != null) {
                                session.close(true);
                            }

                            startPushServer();
                            LogUtil.d(TAG, "=========startPushServer==========");
                        }
                        sleep(20000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        };
        th.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelNotification();
        running = false;
        EventBus.getDefault().unregister(this);
    }

    /**
     * 取消通知
     */
    private void cancelNotification() {
        LogUtil.d(TAG, "notification取消了");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void startPushServer() {
        connector = new NioSocketConnector();
        // 设置超时时间
        connector.setConnectTimeoutMillis(5000);
        // 设置字符串编码过滤器
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, NoticeMsg.CLIENT_HEART_TIME);// IoSession通道空闲时，每隔多长时间发送心跳包
        // 设置业务逻辑处理类
        MinaClientHandler mh = new MinaClientHandler("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_CONNECT
                + "}");
        connector.setHandler(mh);
        try {
            ConnectFuture future = connector.connect(new InetSocketAddress(MSG_HOST_IP, MSG_HOST_PORT));
            future.awaitUninterruptibly();// 等待连接创建完成
            session = future.getSession();// 获得session

            LogUtil.d(TAG, "session连接成功,id:" + session.getId());
            Logger.e("session连接成功,id:" + session.getId());
        } catch (Exception e) {
            connector.dispose(); //不关闭的话会运行一段时间后抛出，too many open files异常，导致无法连接
            LogUtil.d(TAG, "exception:" + e.getMessage());
            Logger.e("exception:" + e.getMessage());
            e.printStackTrace();
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 消息推送的内部处理类
     */
    class MinaClientHandler extends IoHandlerAdapter {

        public IoSession ioSession = null;
        private String message;
        private NoticeMsg noticeMsg = null;

        public MinaClientHandler(String message) {
            this.message = message;

        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            session.write(message);
            ioSession = session;
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            LogUtil.d(TAG, "exception occurred!--" + cause.getMessage());
            cause.printStackTrace();
            PUSH_SERVER_CONNECT = false;
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, NoticeMsg.CLIENT_HEART_TIME);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            LogUtil.d(TAG, "===session closed ==");
            PUSH_SERVER_CONNECT = false;
            connector.dispose();
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            // 如果IoSession闲置，则关闭连接
            if (status == IdleStatus.BOTH_IDLE) {
                session.write("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_HEART
                        + "}");
//
//                Logger.e("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_HEART
//                        + "}");
                Logger.e("发送心跳");
            }
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String json = message.toString();
            int end = json.lastIndexOf("}");
            json = json.substring(0, end + 1);
            LogUtil.e(TAG, "message from server:" + json);

            this.noticeMsg = NoticeMsg.parseJson(json);

            LogUtil.e("@@@", noticeMsg.toString());
            switch (this.noticeMsg.getStatus()) {
                case NoticeMsg.PUSH_SERVER_CONNECT://服务器端返回给客户端，表示建立长连接成功
                    PUSH_SERVER_CONNECT = true;
                    break;
                case NoticeMsg.PUSH_SERVER_HEART://服务器给客户端发送心跳包
                    Logger.e("发送心跳");
                    handler.sendEmptyMessage(0);
                    break;
                case NoticeMsg.PUSH_SERVER_CONTENT://服务端发送消息给客户端
                    session.write("{\"channelNo\":" + "\"" + NoticeMsg.MSG_CHANNELNO_MOB_MEMBER + "\",\"memberId\":\"" + UserModel.getUserModel().getMemberId() + "\",\"version\":\"" + MerriApp.curVersion + "\",\"computerName\":" + "\"" + device_id + "\"" + ",\"status\":" + NoticeMsg.PUSH_CLIENT_CONTENT + ",\"id\":"
                            + noticeMsg.getId() + "}");
                    String contentText = "";
                    contentText = this.noticeMsg.getContent();
                    Message notiMessage = Message.obtain();
                    notiMessage.obj = noticeMsg;

                    if (PhoneUtils.isForeground(getApplicationContext())) {
                        notiMessage.what = 1;
                    } else {
                        notiMessage.what = 2;
                    }

                    handler.sendMessage(notiMessage);
//                    LogUtil.e(TAG,notiMessage.toString());
                    break;
                case NoticeMsg.PUSH_SERVER_DISCONTENT://账号在其他设备登录
                    if (NewMeetNiceActivity.isVideoChatIng) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
                            jsonObject.put("toMemberId", NewMeetNiceActivity.toMemberId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MerriUtils.getApp().socket.emit("beautifulHangUp", jsonObject.toString());

                    }
                    Message msg = Message.obtain();
                    msg.obj = noticeMsg;
                    msg.what = 3;
                    handler.sendMessage(msg);
                    break;
            }
        }
    }

    /**
     * 应用在后台时显示的通知
     *
     * @param noticeMsg
     */
    private void showNotification(NoticeMsg noticeMsg) {
//        String titleStr = noticeMsg.getTitle();
        String titleStr = "讯美";
        String contentStr = noticeMsg.getContent();
        String typeStr = noticeMsg.getType();
        String extraParam = noticeMsg.getExtraParam();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (NoticeMsg.NOTICE_STATUS_10053.equals(typeStr)) {//快递通知
            contentStr = "您有一条新的快递通知";
            parseExpressJson(noticeMsg.getContent());
            if (AppManager.getAppManager().hasActivity(ExpressNotificationActivity.class)) {
                AppManager.getAppManager().finishActivity(ExpressNotificationActivity.class);
            }
            intent.setClass(BackGroudService.this, ExpressNotificationActivity.class);//跳转到快递通知
        } else if (NoticeMsg.NOTICE_STATUS_10054.equals(typeStr)) {
            if (AppManager.getAppManager().hasActivity(NewFriendsActivity.class)) {
                AppManager.getAppManager().finishActivity(NewFriendsActivity.class);
            }
            intent.setClass(BackGroudService.this, NewFriendsActivity.class);//跳转到新的朋友页面
        }
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(icon).setTicker(contentStr)
                .setContentTitle(titleStr).setContentText(contentStr)
                .setContentInfo("迅美").setNumber(i).setAutoCancel(true)
                .setSmallIcon(R.mipmap.icon_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent).build();
        manager.notify(i, notification);
        icon.recycle();
    }


    private void parseExpressJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String createTime = jsonObject.optString("createTime");
            String netName = jsonObject.optString("netName");
            String imageUrl = jsonObject.optString("imageUrl");
            String name = jsonObject.optString("name");
            String memberPhone = jsonObject.optString("memberPhone");
            String number = jsonObject.optString("number");
            String sendContent = jsonObject.optString("sendContent");
            String pickupAddr = jsonObject.optString("pickupAddr");
            String receiverPhone = jsonObject.optString("receiverPhone");
            String memberId = jsonObject.optString("memberId");

            ExpressNotificationModel model = new ExpressNotificationModel();
            model.setCreateTime(createTime);
            model.setNetName(netName);
            model.setImageUrl(imageUrl);
            model.setName(name);
            model.setMemberPhone(memberPhone);
            model.setNumber(number);
            model.setSendContent(sendContent);
            model.setPickupAddr(pickupAddr);
            model.setReceiverPhone(receiverPhone);
            model.setMemberId(memberId);
            ExpressNotificationModel.setNotificationModel(model);
            PrefAppStore.setExpressNotification(getApplicationContext(), true);
            int messageNumber = PrefAppStore.getNotificationNumber(getApplicationContext());
            int new_messageNumber = messageNumber + 1;
            PrefAppStore.setNotificationNumber(getApplicationContext(), new_messageNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
