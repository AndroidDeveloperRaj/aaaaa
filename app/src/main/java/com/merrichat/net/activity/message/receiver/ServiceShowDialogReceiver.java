package com.merrichat.net.activity.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.merrichat.net.activity.message.ExpressNotificationActivity;
import com.merrichat.net.activity.message.NewFriendsActivity;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.ExpressNotificationModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.service.NoticeMsg;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.view.DialogOnClickListener;
import com.merrichat.net.view.RemindDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 主要代替BackGroudService中弹出对话框
 * Created by amssy on 2016/7/18.
 */
public class ServiceShowDialogReceiver extends BroadcastReceiver {
    /**
     * service中发送广播显示对话框的广播
     */
    public static final String SHOW_DIALOG_RECEIVER = "com.receiver.service.show.dialog";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context.getApplicationContext();
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if (SHOW_DIALOG_RECEIVER.equals(action)) {
            NoticeMsg noticeMsg = (NoticeMsg) intent.getSerializableExtra("noticeMsg");
            if (noticeMsg != null) {
                Logger.e(noticeMsg.toString());
                showDialog(noticeMsg);
            }
        }
    }

    /**
     * 应用在前台时弹出dialog时用到
     */
    private Intent intent;
    /**
     * 应用 在前台时的推送弹框
     */
    private RemindDialog remindDialog;

    private FragmentActivity activity;

    /**
     * 应用在前台时显示的弹框
     *
     * @param noticeMsg
     */
    private void showDialog(final NoticeMsg noticeMsg) {
        final String typeStr = noticeMsg.getType();
        String titleStr = "";
        String contentStr = "";
        if (NoticeMsg.NOTICE_STATUS_10053.equals(typeStr)) {
            String content = noticeMsg.getContent();
            try {
                JSONObject jsonObject = new JSONObject(content);
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

                PrefAppStore.setExpressNotification(context, true);
                int messageNumber = PrefAppStore.getNotificationNumber(context);
                int new_messageNumber = messageNumber + 1;
                PrefAppStore.setNotificationNumber(context, new_messageNumber);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            titleStr = noticeMsg.getTitle();
            contentStr = noticeMsg.getContent();
        }
        String extraParam = noticeMsg.getExtraParam();
        //由于在BackGroudService中使用AppManager获取最上面的actvity时，集合对象会为空，所以放到广播里面展示
        activity = AppManager.getAppManager().currentActivity();
        if (activity == null) {
            return;
        }
        if (NoticeMsg.NOTICE_STATUS_10050.equals(typeStr)) {
            return;
        }

        if (NoticeMsg.NOTICE_STATUS_10054.equals(typeStr)||NoticeMsg.NOTICE_STATUS_10055.equals(typeStr)) {
            //加好友
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_NEW_FRIENDS_NUM = true;
            EventBus.getDefault().post(eventBusModel);
            return;
        }

        final RemindDialog remindDialog = new RemindDialog(activity);
        remindDialog.setTitle(titleStr);
        remindDialog.setContent(contentStr);
        intent = new Intent();

        if (NoticeMsg.NOTICE_STATUS_10053.equals(typeStr)) {
            //快递通知
            remindDialog.setTitle("提示");
            remindDialog.setContent("您有新的快递通知,是否查看？");
            remindDialog.setButtonNo("知道了");
            remindDialog.setButtonYes("立即查看");
            if (AppManager.getAppManager().hasActivity(ExpressNotificationActivity.class)) {
                AppManager.getAppManager().finishActivity(ExpressNotificationActivity.class);
            }
            intent.setClass(activity, ExpressNotificationActivity.class);
        }
        remindDialog.setDialogOnClickListener(new DialogOnClickListener() {
            @Override
            public void Yes() {
                if (NoticeMsg.NOTICE_STATUS_10053.equals(typeStr)) {
                    if (intent != null) {
                        activity.startActivity(intent);
                    }
                }
                remindDialog.dismiss();
            }

            @Override
            public void No() {
                remindDialog.dismiss();
            }
        });
        remindDialog.show();
    }
}
