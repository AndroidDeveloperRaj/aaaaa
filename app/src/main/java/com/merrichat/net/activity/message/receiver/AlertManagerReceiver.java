package com.merrichat.net.activity.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlertManagerReceiver extends BroadcastReceiver {
    public AlertManagerReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        String name = "";
        final String groupId = intent.getStringExtra("groupId");
//        if (!TextUtils.isEmpty(groupId)){
//
//            DelaySendAlertModel model = DelaySendAlertModel.getDelaySendAlertModel("groupId");
//            if (model!= null){
//                name =  model.getName();
//
//                final DelaySendAlertModel delaySendAlertModel = model;
//                final RemindDialog dialog = new RemindDialog(context);
//                dialog.setContent("您设置的"+name+"分组有100个通知已到发送时间，是否立即发出？");
//                dialog.setButtonNo("取消");
//                dialog.setButtonYes("点击查看");
//                dialog.setDialogOnClickListener(new DialogOnClickListener() {
//                    @Override
//                    public void Yes() {
//                        if (delaySendAlertModel != null){
//                            delaySendAlertModel.delete();
//                        }
//                        Intent intent1 = new Intent(context, DelayedSendDetailActivity.class);
//                        //在广播接收器中启动活动，一定要给Intent加入FLAG_ACTIVITY_NEW_TASK标志
//                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent1.putExtra("groupId",groupId);
//                        context.startActivity(intent1);
//                        if (delaySendAlertModel != null){
//                            delaySendAlertModel.delete();
//                        }
//                        dialog.dismiss();
//                    }
//
//
//                    @Override
//                    public void No() {
//                        if (delaySendAlertModel != null){
//                            delaySendAlertModel.delete();
//                        }
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                dialog.show();
//
//
//            }
//
//        }


    }
}
