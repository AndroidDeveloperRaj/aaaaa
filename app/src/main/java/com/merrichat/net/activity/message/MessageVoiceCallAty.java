package com.merrichat.net.activity.message;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.VoiceChatActivity;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.AgreeCallModel;
import com.merrichat.net.model.CallPeerModel;
import com.merrichat.net.model.HangUpModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CircleImageView;
import com.merrichat.net.view.lockview.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.IFSPanelConflictLayout;
import io.socket.client.IO;

/**
 * Created by amssy on 17/12/8.
 */

public class MessageVoiceCallAty extends BaseActivity {


    public static final String CALL_VIDEO_CALLER_TYPE = "isCaller";
    public static final String CALL_VIDEO_ANSWER_TYPE = "isAnswer";
    public static final String CALL_VIDEO_TYPE_KEY = "callVideoType";
    public static final String CALL_VIDEO_INFO_KEY = "callerInfo";
    public static final int VOICE_CONNECTION = 0x666;
    private boolean isOnDestroy = false;  //是否结束倒计时
    @BindView(R.id.iv_background)
    ImageView ivBg;
    @BindView(R.id.iv_other_img)
    CircleImageView ivOtherImg;
    @BindView(R.id.tv_other_name)
    TextView tvOtherName;
    @BindView(R.id.tv_connet_status)
    TextView tvConnetStatus;
    @BindView(R.id.tv_caller_hang_up)
    TextView tvCallHangUp;
    @BindView(R.id.tv_answer_hang_up)
    TextView tvAnswerHangUp;
    @BindView(R.id.tv_answer_answer)
    TextView tvAnswerAnswer;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.connection_time)
    TextView tvConnectionTime;
    private long timeConnection = 57600000;  //连接时间


    private String video_call_type;//CALL_VIDEO_CALLER呼叫者，CALL_VIDEO_ANSWER应答者
    private String toMemberId;
    private String toMemberName;
    private String toHeadImgUrl;
    private Thread threadStarAnim;
    private int connectionState = 1;   //state 1 表示已连接
    private Gson gson = new Gson();

    private MediaPlayer mMediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCheckConnectionState();
        //加载xml布局
        setContentView(R.layout.activity_message_voice_call);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        //获取上一界面传来的是呼叫还是应答的类型
        video_call_type = getIntent().getStringExtra(CALL_VIDEO_TYPE_KEY);
        initView();
        threadStarAnim = new Thread(new ThreadShow());  //倒计时线程
        threadStarAnim.start();


        if (PrefAppStore.getAboutPhoneSetting(cnt) == 0) {  //声音提示
            mMediaPlayer = MediaPlayer.create(MessageVoiceCallAty.this, R.raw.call_running);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } else if (PrefAppStore.getAboutPhoneSetting(cnt) == 1) {  //振动提示
            vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

            // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
            long[] pattern = {1000, 1000, 1000, 1000};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern, 0);

        } else if (PrefAppStore.getAboutPhoneSetting(cnt) == 2) {    //声音加振动提示
            mMediaPlayer = MediaPlayer.create(MessageVoiceCallAty.this, R.raw.call_running);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
            vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
            // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
            long[] pattern = {1000, 1000, 1000, 1000};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern, 0);

        }


    }

    private void initView() {
        if (video_call_type.equals(CALL_VIDEO_ANSWER_TYPE)) {
            tvConnetStatus.setText("邀请你进行语音聊天");
            rlAnswer.setVisibility(View.VISIBLE);
            tvCallHangUp.setVisibility(View.GONE);
            String callVideoInfo = getIntent().getStringExtra(CALL_VIDEO_INFO_KEY); //{"remoteMemberId":"315498810359809","remoteMemberName":"天天","remoteHeadImgUrl":""}
            try {
                JSONObject jsonObject = new JSONObject(callVideoInfo);
                toMemberId = jsonObject.optString("fromMemberId");
                toMemberName = jsonObject.optString("fromMemberName");
                toHeadImgUrl = jsonObject.optString("fromHeadImgUrl");
                Glide.with(this).load(toHeadImgUrl).error(R.mipmap.ic_preloading).into(ivOtherImg);
                tvOtherName.setText(toMemberName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (video_call_type.equals(CALL_VIDEO_CALLER_TYPE)) {
            tvConnetStatus.setText("正在等待对方接受邀请");
            rlAnswer.setVisibility(View.GONE);
            tvCallHangUp.setVisibility(View.VISIBLE);
            String callVideoInfo = getIntent().getStringExtra(CALL_VIDEO_INFO_KEY); //{"remoteMemberId":"315498810359809","remoteMemberName":"天天","remoteHeadImgUrl":""}
            try {
                JSONObject jsonObject = new JSONObject(callVideoInfo);
                toMemberId = jsonObject.optString("toMemberId");
                toMemberName = jsonObject.optString("toMemberName");
                toHeadImgUrl = jsonObject.optString("toHeadImgUrl");
                Glide.with(this).load(toHeadImgUrl).error(R.mipmap.ic_preloading).into(ivOtherImg);
                tvOtherName.setText(toMemberName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendCallInfo();
        }
    }


    public void sendCallInfo() {
        try {
            CallPeerModel callPeerModel = new CallPeerModel();
            callPeerModel.fromMemberId = UserModel.getUserModel().getMemberId();
            callPeerModel.toMemberId = toMemberId;
            callPeerModel.fromMemberName = UserModel.getUserModel().getRealname();
            callPeerModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
            callPeerModel.callSource = "0";
            callPeerModel.callType = "0";
            callPeerModel.fromLatitude = MerriApp.mLatitude;
            callPeerModel.fromLongitude = MerriApp.mLongitude;
            Log.e("@@@", "--------发送呼叫事件------" + gson.toJson(callPeerModel, CallPeerModel.class));
            MerriApp.socket.emit("callPeer", gson.toJson(callPeerModel, CallPeerModel.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        }
        super.onDestroy();
    }

    @OnClick({R.id.tv_caller_hang_up, R.id.tv_answer_hang_up, R.id.tv_answer_answer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_caller_hang_up:   //别人申请聊天的时候拒绝
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("isAgree", "0");//1-同意  0--拒绝
                    jsonObject1.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject1.put("toMemberId", toMemberId);
                    jsonObject1.put("callSource", "0");
                    jsonObject1.put("callType", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MerriApp.socket.emit("agreeCall", jsonObject1.toString());
                Logger.e("@@@", "发射挂断事件.........");
                isOnDestroy = true;
                finish();
                break;
            case R.id.tv_answer_hang_up:  // 我发起聊天，没人接我拒绝
                hangUp();
                isOnDestroy = true;
                finish();
                break;
            case R.id.tv_answer_answer:   //同意打电话跳到视频界面
                connectionState = 2;
                timeConnection = 57600000;

                isOnDestroy = true;
                if (mMediaPlayer != null) {
                    mMediaPlayer.release();
                }
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.cancel();
                }

//                AgreeCallModel agreeCallModel = new AgreeCallModel();
//                agreeCallModel.isAgree = "1";
//                agreeCallModel.fromMemberId = UserModel.getUserModel().getMemberId();
//                agreeCallModel.fromHeadImgUrl =  UserModel.getUserModel().getImgUrl();
//                agreeCallModel.fromMemberName = UserModel.getUserModel().getRealname();
//                agreeCallModel.toMemberId = toMemberId;
//                agreeCallModel.callSource = "0";
//                agreeCallModel.callType = "0";
//
//                MerriApp.socket.emit("agreeCall", gson.toJson(agreeCallModel));
                startActivityForResult(new Intent(getApplicationContext(), VoiceChatActivity.class)
                                .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_ANSWER_TYPE)
                                .putExtra("toMemberId", toMemberId)
                                .putExtra("toMemberName", toMemberName)
                                .putExtra("toHeadImgUrl", toHeadImgUrl)
                                .putExtra("callType", "0")
                        , VOICE_CONNECTION);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_CONNECTION:
                getConnectionState();
                break;
        }

    }

    //只有断开的时候才走这个方法
    public void getConnectionState() {
        getCheckConnectionState();
        MessageVoiceCallAty.this.finish();
    }


    //只有断开的时候才走这个方法
    public void getCheckConnectionState() {
        try {
            if (MerriApp.socket != null) {
                MerriApp.socket.disconnect();
            }
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(MessageVoiceCallAty.this), VideoChatModel.class);
            String mSocketAddress = "http://" + chatModel.data.socketIp + ":" + chatModel.data.socketPort + "/";
            MerriApp.socket = IO.socket(mSocketAddress);
            MerriApp.socket.io().reconnection(true);
            MerriApp.socket.connect();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("memberId", UserModel.getUserModel().getMemberId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MerriApp.socket.emit("connectAfter", jsonObject1.toString());
            MerriApp.socket.on("calledChat", MerriApp.calledChat);
            MerriApp.socket.on("callStatus", MerriApp.callStatus);
            MerriApp.socket.on("answerCall", MerriApp.answerCall);
            MerriApp.socket.on("hangUp", MerriApp.hangUp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        hangUp();
        Logger.e("@@@", "发射挂断事件.........");
        isOnDestroy = true;
        finish();
    }

    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tvConnectionTime.setText(DateTimeUtil.format9(new Date(timeConnection)));
                    break;
                case 2:
                    break;
            }
        }
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (isOnDestroy) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = connectionState;
                    timeConnection = timeConnection + 1000;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void hangUp() {  //挂断事件
        HangUpModel hangUpModel = new HangUpModel();
        hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
        hangUpModel.toMemberId = toMemberId;
        hangUpModel.callType = "0";
        hangUpModel.isAgree = "0";
        MerriApp.socket.emit("hangUp", gson.toJson(hangUpModel, HangUpModel.class));
        Logger.e("@@@", "发射挂断事件........." + gson.toJson(hangUpModel, HangUpModel.class));

    }

}
