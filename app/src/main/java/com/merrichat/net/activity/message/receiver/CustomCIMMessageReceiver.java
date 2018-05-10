package com.merrichat.net.activity.message.receiver;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.android.CIMEventBroadcastReceiver;
import com.merrichat.net.activity.message.cim.android.CIMListenerManager;
import com.merrichat.net.activity.message.cim.model.ReplyBody;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.PhoneUtils;
import com.merrichat.net.utils.SysApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.List;

/**
 * 消息入口，所有消息都会经过这里
 *
 * @author 3979434
 */
public final class CustomCIMMessageReceiver extends CIMEventBroadcastReceiver {

    //当收到消息时，会执行onMessageReceived，这里是消息第一入口
    @Override
    public void onMessageReceived(final MessageModel message) {
        message.setPrivate_id(UserModel.getUserModel().getMemberId());

        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {

            Log.i(this.getClass().getSimpleName(), "########################" + (CIMListenerManager.getCIMListeners().get(index).getClass().getName() + ".onMessageReceived################"));

            CIMListenerManager.getCIMListeners().get(index).onMessageReceived(message);
        }
        LogUtil.e("@@@", "message===" + message.toString());
        //以开头的为动作消息，无须显示,如被强行下线消息Constant.TYPE_999
        if (Constant.MessageType.TYPE_999.equals(message.getType())) {
            showExitDialog(message);
            return;
        } else if (Constant.MessageType.TYPE_4.equals(message.getType())) {
            //该群被管理员解散了
//            showDialog(message);
            return;
        }
        if (Constant.MessageType.TYPE_5.equals(message.getType())) {
            //服务器以群主名义发送公告，类型为5，这里修改为2
            message.setType(Constant.MessageType.TYPE_2);
            //将发送状态修改为发送成功
            message.setSendState(MessageModel.SEND_STATE_SUCCEED);
        }


        if (!isInTopBackground(context)) {
            saveMessageList(context, message);
            if (Constant.MessageType.TYPE_1.equals(message.getType())) {
                List<MessageModel> singleList = MessageModel.getListMessageModelByLimit(message.getReceiver(), 0, 20);
                if ("1".equals(message.getRevoke())) {
                    for (int i = 0; i < singleList.size(); i++) {
                        if ((singleList.get(i).getMid()).equals(message.getMid())) {
                            singleList.get(i).setContent(message.getContent());
                            singleList.get(i).setTypeRevoke(Constant.MessageType.TYPE_3);
                            MessageModel.setMessageModel(singleList.get(i));
                        }
                    }
                } else {
                    //将此条消息存入数据库
                    message.setPrivate_id(UserModel.getUserModel().getMemberId());
                    MessageModel.setMessageModel(message);
                }

            } else {
                List<GroupMessage> qun1List = GroupMessage.getListMessageModelByLimit(message.getGroupId(), 0, 20);
                if ("1".equals(message.getRevoke())) {
                    for (int i = 0; i < qun1List.size(); i++) {
                        if ((qun1List.get(i).getMid()).equals(message.getMid())) {
                            qun1List.get(i).setContent(message.getContent());
                            qun1List.get(i).setType(Constant.MessageType.TYPE_3);
                            GroupMessage.setMessageModel(qun1List.get(i));
                        }
                    }
                } else {
                    GroupMessage.setMessageModel(MessageToGroupMessage(message));
                }
            }
        } else if (Constant.MessageType.TYPE_1.equals(message.getType())) { //单聊
            //判断单聊界面是否运行
            if (AppManager.getAppManager().hasActivity(SingleChatActivity.class)) {
                //当前在聊天界面,判断是否是与此人聊天，如果是不处理，不是则推送
                if (!message.getSender().equals(SingleChatActivity.receiverMemberId)) {
                    saveMessageList(context, message);
                    List<MessageModel> singleList = MessageModel.getListMessageModelByLimit(message.getReceiver(), 0, 20);
                    if ("1".equals(message.getRevoke())) {
                        for (int i = 0; i < singleList.size(); i++) {
                            if ((singleList.get(i).getMid()).equals(message.getMid())) {
                                singleList.get(i).setContent(message.getContent());
                                singleList.get(i).setTypeRevoke(Constant.MessageType.TYPE_3);
                                MessageModel.setMessageModel(singleList.get(i));
                            }
                        }
                    } else {
                        //将此条消息存入数据库
                        message.setPrivate_id(UserModel.getUserModel().getMemberId());
                        MessageModel.setMessageModel(message);
                    }
                }
            } else {
                saveMessageList(context, message);
                List<MessageModel> singleList = MessageModel.getListMessageModelByLimit(message.getReceiver(), 0, 20);
                if ("1".equals(message.getRevoke())) {
                    for (int i = 0; i < singleList.size(); i++) {
                        if ((singleList.get(i).getMid()).equals(message.getMid())) {
                            singleList.get(i).setContent(message.getContent());
                            singleList.get(i).setTypeRevoke(Constant.MessageType.TYPE_3);
                            MessageModel.setMessageModel(singleList.get(i));
                        }
                    }
                } else {
                    //将此条消息存入数据库
                    message.setPrivate_id(UserModel.getUserModel().getMemberId());
                    MessageModel.setMessageModel(message);
                }
            }
        } else {
            //判断群聊界面是否运行
            if (AppManager.getAppManager().hasActivity(GroupChattingAty.class)) {
//                if (SysApp.isInLauncher(context, GroupChatActivity.class)) {
                //当前在聊天界面,判断是否是与此群聊天，如果是不处理，不是则推送
                if (!message.getGroupId().equals(GroupChattingAty.groupId)) {
                    List<GroupMessage> qun1List = GroupMessage.getListMessageModelByLimit(message.getGroupId(), 0, 20);
                    if ("1".equals(message.getRevoke())) {
                        for (int i = 0; i < qun1List.size(); i++) {
                            if ((qun1List.get(i).getMid()).equals(message.getMid())) {
                                qun1List.get(i).setContent(message.getContent());
                                qun1List.get(i).setType(Constant.MessageType.TYPE_3);
                                GroupMessage.setMessageModel(qun1List.get(i));
                            }
                        }
                    } else {
                        GroupMessage.setMessageModel(MessageToGroupMessage(message));
                    }
                    saveMessageList(context, message);
                }
//                }
            } else {
                saveMessageList(context, message);
                List<GroupMessage> qun1List = GroupMessage.getListMessageModelByLimit(message.getGroupId(), 0, 20);
                if ("1".equals(message.getRevoke())) {
                    for (int i = 0; i < qun1List.size(); i++) {
                        if ((qun1List.get(i).getMid()).equals(message.getMid())) {
                            qun1List.get(i).setContent(message.getContent());
                            qun1List.get(i).setType(Constant.MessageType.TYPE_3);
                            GroupMessage.setMessageModel(qun1List.get(i));
                        }
                    }
                } else {
                    GroupMessage.setMessageModel(MessageToGroupMessage(message));
                }

            }
        }
        if (Constant.MessageType.TYPE_6.equals(message.getType())) {
//            toCircleList(context,message);
        }

    }

    /**
     * 将Message转换成GroupMessage
     *
     * @param message
     * @return
     */
    private GroupMessage MessageToGroupMessage(MessageModel message) {
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(message.getGroupId());
        groupMessage.setGroup(message.getGroup());
        groupMessage.setSenderName(message.getSenderName());
        groupMessage.setSender(message.getSender());
        groupMessage.setContent(message.getContent());
        groupMessage.setFile(message.getFile());
        groupMessage.setFilePath(message.getFilePath());
        groupMessage.setFileType(message.getFileType());
        groupMessage.setFormat(message.getFormat());
        groupMessage.setLogo(message.getLogo());
        groupMessage.setMid(message.getMid());
        groupMessage.setReceiver(message.getReceiver());
        groupMessage.setReceiverName(message.getReceiverName());
        groupMessage.setSpeechTimeLength(message.getSpeechTimeLength());
        groupMessage.setThumb(message.getThumb());
        groupMessage.setTimestamp(message.getTimestamp());
        groupMessage.setTitle(message.getTitle());
        groupMessage.setType(message.getType());
        groupMessage.setHeader(message.getHeader());
        groupMessage.setInter(message.getInter());
        groupMessage.setTop(message.getTop());
        groupMessage.setRedTid(message.getRedTid());
        groupMessage.setRedStatus(message.getRedStatus());
        groupMessage.setTopts(message.getTopts());
        groupMessage.setPrivate_id(UserModel.getUserModel().getMemberId());
        return groupMessage;
    }

//    private void toCircleList(Context context, Message message) {
//        Intent intent = new Intent();
//        intent.addFlags(MainActivity.activityidtocircle);
//        intent.putExtra("content",message.getContent());
//        intent.setClass(context, MainActivity.class);//跳转到首页
//        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                R.mipmap.ic_launcher);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification notification = new NotificationCompat.Builder(context)
//                .setLargeIcon(icon).setTicker("好递快递员")
//                .setContentTitle("好递快递员").setContentText(message.getContent())
//                .setContentInfo("好递").setNumber(0).setAutoCancel(true)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setWhen(System.currentTimeMillis())
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setContentIntent(pendingIntent).build();
//        manager.notify(0, notification);
//        icon.recycle();
////        MyEventBusModel myEventBusModel = new MyEventBusModel();
////        //选到消息标签
////        myEventBusModel.is_To_Circle = true;
////        myEventBusModel.is_To_CircleContent = message.getContent();
////        EventBus.getDefault().post(myEventBusModel);
//    }

    //当手机网络连接状态变化时，会执行onNetworkChanged
    @Override
    public void onNetworkChanged(NetworkInfo info) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onNetworkChanged(info);
        }
    }

    //当收到sendbody的响应时，会执行onReplyReceived
    @Override
    public void onReplyReceived(ReplyBody body) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onReplyReceived(body);
        }
    }

    private void saveMessageList(Context context, MessageModel msg) {
        int isInterru = 0;
        if (Constant.MessageType.TYPE_1.equals(msg.getType())) {//单聊
            if (!"1".equals(msg.getRevoke())) {
                //单聊时把消息存入消息列表中，并通知列表刷新
//                MessageListModle messageListModle = new Select().from(MessageListModle.class).where("senderId=?", msg.getSender()).and("privateID=?", UserModel.getUserModel().getMEMBER_ID()).executeSingle();

                QueryBuilder<MessageListModle> messageModelQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
                MessageListModle messageListModle = messageModelQueryBuilder.where(MessageListModleDao.Properties.SenderId.eq(msg.getSender()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
                if (null == messageListModle) {
                    messageListModle = new MessageListModle();
                    messageListModle.setSenderId(msg.getSender());
                    messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));

                } else {
                    isInterru = messageListModle.getInter();
                }
                messageListModle.setMsgts(msg.getTimestamp());
                messageListModle.setHeadUrl(msg.getHeader() + msg.getSender() + ".jpg");
                messageListModle.setType(msg.getType());
                messageListModle.setName(msg.getSenderName());
                messageListModle.setFileType(msg.getFileType());
                messageListModle.setText1("1");
                messageListModle.setLast(msg.getContent());
                messageListModle.setCount(messageListModle.getCount() + 1);
//                messageListModle.save();
                MessageListModle.setMessageListModle(messageListModle);
            }
        } else {//群消息
            QueryBuilder<MessageListModle> messageModelQueryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
            MessageListModle messageListModle = messageModelQueryBuilder.where(MessageListModleDao.Properties.GroupId.eq(msg.getGroupId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();

            if (null == messageListModle) {
                messageListModle = new MessageListModle();
                messageListModle.setGroupId(msg.getGroupId());
                messageListModle.setPrivateID(UserModel.getUserModel().getMemberId());

            }
            isInterru = messageListModle.getInter();
            messageListModle.setMsgts(msg.getTimestamp());
            messageListModle.setGroup(msg.getGroup());
            messageListModle.setHeadUrl(msg.getLogo() + msg.getGroupId() + ".jpg");
            ///存入消息列表为群聊
            messageListModle.setType(Constant.MessageType.TYPE_2);
            messageListModle.setFileType(msg.getFileType());
            messageListModle.setText1("1");
            messageListModle.setLast(msg.getContent());
            messageListModle.setCount(messageListModle.getCount() + 1);
            MessageListModle.setMessageListModle(messageListModle);
            LogUtil.e("@@@", "messageListModle===" + messageListModle.toString());
        }
        toNotificaiton(isInterru);
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        //消息列表刷新
        myEventBusModel.MESSAGE_IS_REFRESH = true;
        myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(myEventBusModel);

        if (0 == isInterru) {//没有设置为免打扰，可以播放声音
            //播放系统声音
            try {
                ring(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //消息通知
    private void toNotificaiton(int isInterru) {
        if (!isInTopBackground(context)&&isInterru==0) {
            Intent intent = new Intent();
//            intent.addFlags(MainActivity.activityidForChat);
            intent.setClass(context, MainActivity.class);//跳转到首页
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.icon_logo);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context)
                    .setLargeIcon(icon).setTicker("讯美")
                    .setContentTitle("讯美").setContentText("您有新的聊天消息，请及时查看！")
                    .setContentInfo("讯美").setNumber(0).setAutoCancel(true)
                    .setSmallIcon(R.mipmap.icon_logo)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent).build();
            manager.notify(0, notification);
            icon.recycle();
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            //选到消息标签
            myEventBusModel.MESSAGE_IS_T0_CHAT = true;
            EventBus.getDefault().post(myEventBusModel);
        }
    }

    @Override
    public void onCIMConnectionSucceed() {
        MerriApp.isCIMConnectionSatus = true;
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onCIMConnectionSucceed();
        }
    }


    @Override
    public void onConnectionStatus(boolean arg0) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onConnectionStatus(arg0);
        }
    }


    @Override
    public void onCIMConnectionClosed() {
        // TODO Auto-generated method stub
        MerriApp.isCIMConnectionSatus = false;
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onCIMConnectionClosed();
        }
    }

    @Override
    public void onMessageSendFailureReceived(MessageModel message) {
        //消息发送失败

    }

    /**
     * 消息发送失败
     *
     * @param sentBody
     */
    @Override
    public void onMessageSendFailure(SentBody sentBody) {
        MessageModel msg = new MessageModel();
        msg.setType(sentBody.get("type"));
        msg.setSender(sentBody.get("sender"));
        msg.setContent(sentBody.get("content"));
        msg.setFileType(sentBody.get("fileType"));
        msg.setSenderName(sentBody.get("senderName"));
        msg.setReceiver(sentBody.get("receiver"));
        msg.setReceiverName(sentBody.get("receiverName"));
        msg.setType(sentBody.get("type"));
        msg.setFile(sentBody.get("file"));
        msg.setSpeechTimeLength(sentBody.get("speechTimeLength"));
        if (Constant.MessageType.TYPE_2.equals(msg.getType())) {
            msg.setGroup(sentBody.get("group"));
            msg.setGroupId(sentBody.get("groupId"));
        }

        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onMessageSendFailureReceived(msg);
        }
    }

    /**
     * 振动和播放系统通知声音
     *
     * @param cnt
     * @return
     * @throws Exception
     * @throws IOException
     */
    private MediaPlayer ring(Context cnt) throws Exception, IOException {
        // TODO Auto-generated method stub
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        Vibrator vibrator = (Vibrator) cnt.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(cnt, alert);
        final AudioManager audioManager = (AudioManager) cnt.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);

            player.prepare();

            player.start();

        }

        return player;
    }

    /**
     * 退群对话框
     *
     * @param msg
     */
//    private void showDialog(final Message msg) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
//        dialog.setMessage(("【" + msg.getGroup() + "】已被群主解散"));
//        dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //判断是否在聊天界面，如果在，则关闭
//                if (SysApp.isInLauncher(context, GroupChatActivity.class)) {
//                    //当前在聊天界面并且是解散的这个圈子的聊天界面，如果是不处理，不是则推送
//                    if (msg.getGroupId().equals(GroupChatActivity.groupId)) {
//                        //发送广播到聊天界面关闭界面
//                        MyEventBusModel myEventBusModel = new MyEventBusModel();
//                        myEventBusModel.GROUP_MESSAGE_IS_FINISH = true;
//                        EventBus.getDefault().post(myEventBusModel);
//                    }
//                }
//
//                MessageListModle.clearSingleMessageModel(msg.getGroupId());
//                //清除与该群的聊天记录（包括视频、语音、图片缓存）
//
//                dialog.dismiss();
//
//            }
//        });
//
//        dialog.setCancelable(false);
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    //屏蔽返回键
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        dialog.show();
//
//
//    }

    /**
     * 账号被顶强制退出对话框
     *
     * @param msg
     */
    private void showExitDialog(final MessageModel msg) {
        final Context cnt = AppManager.getAppManager().currentActivity();

        AlertDialog.Builder dialog = new AlertDialog.Builder(cnt);
        dialog.setTitle("提示");
        dialog.setMessage("您好，您的账号已在其它设备上登录，请注意账户安全!");
        dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SysApp.stopNoticeService(cnt);
                SysApp.lognOut(cnt, true,true);
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    //屏蔽返回键
                    return true;
                }
                return false;
            }
        });

        dialog.show();


    }

}
