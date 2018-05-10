package com.merrichat.net.activity.meiyu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.fragments.FragmentGiftDialog;
import com.merrichat.net.activity.meiyu.fragments.Gift;
import com.merrichat.net.activity.meiyu.fragments.view.GiftItemView;
import com.merrichat.net.activity.message.MessageVoiceCallAty;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.AgreeCallModel;
import com.merrichat.net.model.CallPeerModel;
import com.merrichat.net.model.ClipOrderPayModel;
import com.merrichat.net.model.HangUpModel;
import com.merrichat.net.model.PresentGiftModel;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.SendGiftModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.FastBlur;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.VideoRendererGui;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_ANSWER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_TYPE_KEY;

/**
 * Created by amssy on 17/12/6.
 * <p>
 * 音频聊天界面
 */

public class VoiceChatActivity extends BaseActivity implements WebRtcClient.RtcListener {

    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";


    @BindView(R.id.iv_background)
    ImageView ivBg;
    @BindView(R.id.iv_other_img)
    CircleImageView ivOtherImg;
    @BindView(R.id.tv_other_name)
    TextView tvOtherName;
    @BindView(R.id.tv_hang_up)
    TextView tvHangUp;
    @BindView(R.id.connection_time)
    TextView tvConnectionTime;

    //声音状态
    @BindView(R.id.mianti)
    RelativeLayout rlMianti;
    @BindView(R.id.ic_voice_state)
    ImageView mVoiceState;

    //送礼物
    @BindView(R.id.ic_gift)
    ImageView mSendGift;

    @BindView(R.id.touch_surface_view)
    RelativeLayout touchSurfaceView;

    @BindView(R.id.gift_item_first)  //礼物弹出动画
            GiftItemView giftView;

    @BindView(R.id.rl_contour)
    RelativeLayout rlContour;


    @BindView(R.id.rl_chatting)
    RelativeLayout mRlChatting;

    @BindView(R.id.rl_call)
    RelativeLayout mRlCall;

    @BindView(R.id.iv_my_img)
    CircleImageView ivMyImg;

    @BindView(R.id.tv_my_name)
    TextView tvMyName;

    @BindView(R.id.tv_answer_hang_up)
    TextView tvAnswerHangUp;   //电话挂断

    @BindView(R.id.voice_top_close)
    LinearLayout mVoiceTopClose;    //声音控制按钮

    private WebRtcClient client;
    private String mSocketAddress;  //socket 连接地址

    private String toMemberId;  //对方的memberid
    private String toMemberName;  //对方的名称
    private String toHeadImgUrl;  //对方头像
    private String video_call_type;//CALL_VIDEO_CALLER呼叫者，CALL_VIDEO_ANSWER应答者
    private String callType = "";  //呼叫类型
    private long timeConnection = 57600_000;  //连接开始时间

    private TopicDetailTouchListener topicDetailTouchListener;  //弹出小星星
    private Thread threadStarAnim;  //倒计时线程

    private AudioManager audioManager;  //声音控制管理

    private Gson gson = new Gson();
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        super.onCreate(savedInstanceState);
        initAudioManager();
        mVolumeReceiver = new MyVolumeReceiver();   //设置声音service 控制耳机
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mVolumeReceiver, filter);
        setContentView(R.layout.activity_voice_chat);
        MerriApp.isChatting = false;
        getConnectionState();
        ButterKnife.bind(this);
        initView();

        EventBus.getDefault().register(this);
        threadStarAnim = new Thread(new ThreadShow());  //倒计时线程
        threadStarAnim.start();
        getWalletInfo();

    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_VOICE_CHAT_ANSWER) {//call   我是主叫的时候返回
            MerriApp.isChatting = true;
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            String answerInfo = myEventBusModel.VOICE_CHAT_ANSWER_CALLINFO;
            toMemberId = JSON.parseObject(answerInfo).getString("answerMemberId");
            String toSocketId = JSON.parseObject(answerInfo).getString("toSocketId");
            client.setVideoChatConnection();
            startCam();
        }
    }


    private void initView() {
        video_call_type = getIntent().getStringExtra(CALL_VIDEO_TYPE_KEY);  //来源
        toMemberId = getIntent().getStringExtra("toMemberId");
        toMemberName = getIntent().getStringExtra("toMemberName");
        toHeadImgUrl = getIntent().getStringExtra("toHeadImgUrl");
        callType = getIntent().getStringExtra("callType");
        String toSocketId = getIntent().getStringExtra("toSocketId");

        Glide.with(this).load(toHeadImgUrl).into(ivOtherImg);   //拨打电话的时候显示
        Glide.with(this).load(toHeadImgUrl).into(ivMyImg);   //在呼叫的时候显示
        tvMyName.setText(toMemberName);  //在呼叫时显示
        tvOtherName.setText(toMemberName);
        setBlurBackground();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                false, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), toMemberId);

        if (CALL_VIDEO_CALLER_TYPE.equals(video_call_type)) {
            sendCallInfo();
            mMediaPlayer = MediaPlayer.create(VoiceChatActivity.this, R.raw.call_running);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            Log.e("@@@", "--------呼叫方call-----  call---");
        } else if (CALL_VIDEO_ANSWER_TYPE.equals(video_call_type)) {
            AgreeCallModel agreeCallModel = new AgreeCallModel();
            agreeCallModel.isAgree = "1";
            agreeCallModel.fromMemberId = UserModel.getUserModel().getMemberId();
            agreeCallModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
            agreeCallModel.fromMemberName = UserModel.getUserModel().getRealname();
            agreeCallModel.toMemberId = toMemberId;
            agreeCallModel.callSource = "0";
            agreeCallModel.callType = "0";

            MerriApp.socket.emit("agreeCall", gson.toJson(agreeCallModel));

            client.setVideoChatConnection();
            startCam();
            try {
                answer(toSocketId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        rlMianti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);//关闭扬声器
                    audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
                    setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    //把声音设定成Earpiece（听筒）出来，设定为正在通话中
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    rlMianti.setBackgroundResource(R.drawable.shape_radius_wite_100);
                    mVoiceState.setBackgroundResource(R.mipmap.ic_mianti);
                } else {
                    audioManager.setSpeakerphoneOn(true);
                    rlMianti.setBackgroundResource(R.drawable.shape_radius_content_wite_100);
                    mVoiceState.setBackgroundResource(R.mipmap.ic_mianti_click);

                }

            }
        });

        mSendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGiftDialog.newInstance().setOnGridViewClickListener(8, new FragmentGiftDialog.OnGridViewClickListener() {
                    @Override
                    public void click(Gift gift) {
                        double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(VoiceChatActivity.this)) - gift.giftPrice;
                        if (cashBalance > 0) {
                            //送礼物
                            senGift(gift);
                        } else {
                            Toast.makeText(VoiceChatActivity.this, "当前账户余额不足～！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show(getSupportFragmentManager(), "dialog");
            }
        });

        mVoiceTopClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriApp.isChatting){
                    hangUp();
                    finish();
                }else {
                    mVoiceTopClose.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hangUp();
                            finish();
                        }
                    }, 1500);
                }
            }
        });

        tvAnswerHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MerriApp.isChatting){
                    hangUp();
                    finish();
                }else {
                    mVoiceTopClose.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hangUp();
                            finish();
                        }
                    }, 1500);
                }
            }
        });

        topicDetailTouchListener = new TopicDetailTouchListener(touchSurfaceView, this);
        touchSurfaceView.setOnTouchListener(topicDetailTouchListener);  //布局可以点击出现小星星

    }


    public void sendCallInfo() {    // 发送呼叫事件
        CallPeerModel callPeerModel = new CallPeerModel();
        callPeerModel.fromMemberId = UserModel.getUserModel().getMemberId();
        callPeerModel.toMemberId = toMemberId;
        callPeerModel.fromMemberName = UserModel.getUserModel().getRealname();
        callPeerModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
        callPeerModel.callSource = "0";
        callPeerModel.callType = "0";
        callPeerModel.fromLatitude = MerriApp.mLatitude;
        callPeerModel.fromLongitude = MerriApp.mLongitude;
        MerriApp.socket.emit("callPeer", gson.toJson(callPeerModel, CallPeerModel.class));
        Logger.e("@@@", "callPeer ---->>" + gson.toJson(callPeerModel, CallPeerModel.class));
    }


    //设置模糊化头像背景
    private void setBlurBackground() {
        Glide.with(this).load(toHeadImgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource,
                        resource.getWidth() / 10,
                        resource.getHeight() / 10,
                        false);
                Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 10, true);
                ivBg.setImageBitmap(blurBitmap);
            }
        }); //方法中设置asBitmap可以设置回调类型
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer!= null){
            mMediaPlayer.release();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
        if (callType.equals("0")) {
            if (AppManager.getAppManager().hasActivity(MessageVoiceCallAty.class)) {
                AppManager.getAppManager().finishActivity(MessageVoiceCallAty.class);
            }
        }
    }

    public void startCam() {
        client.setCamera();
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (newStatus.equals("DISCONNECTED")) {
                    GetToast.useString(cnt, "聊天结束");
                    finish();
                } else if (newStatus.equals("CONNECTED")) {
                    GetToast.useString(cnt, "连接成功");
                    MerriApp.isChatting = true;
                }
            }
        });
        if (newStatus.equals("CONNECTED")) {
            timeConnection = 57600_000;
        }
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {
    }

    @OnClick({R.id.tv_hang_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hang_up:
                hangUp();
                finish();
                break;
        }
    }

    //判断当前连接状态 如果连接为空，则重新连接
    public void getConnectionState() {
        try {
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(VoiceChatActivity.this), VideoChatModel.class);
            mSocketAddress = "http://" + chatModel.data.socketIp + ":" + chatModel.data.socketPort + "/";
            if (null != MerriApp.socket) {
                return;
            }
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
            MerriApp.socket.on("sendGift", sendGift);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tvConnectionTime.setText(DateTimeUtil.formatDaojiShi(new Date(timeConnection)));
                    break;
            }
            if (MerriApp.isChatting) {
                topicDetailTouchListener.startAnimation((mSendGift.getRight() + mSendGift.getLeft()) / 2, (rlContour.getTop() - 40));  //动画出现的位置
                mRlChatting.setVisibility(View.VISIBLE);
                mRlCall.setVisibility(View.GONE);

            } else {
                if (timeConnection == 57620000l) {
                    GetToast.useString(cnt, "对方可能不在身边，稍等下再试吧～！");
                }
                if (timeConnection == 57634500l) {
                    GetToast.useString(cnt, "对方未接听～！");
                }
                if (timeConnection > 57635000l) {
                    hangUp();
                    finish();
                }

                mRlChatting.setVisibility(View.GONE);
                mRlCall.setVisibility(View.VISIBLE);
            }

        }
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            boolean b = true;
            while (true) {
                try {
                    Thread.sleep(500);
                    Message msg = new Message();
                    if (b == true) {
                        b = false;
                        msg.what = 1;
                        timeConnection = timeConnection + 500;
                    } else {
                        timeConnection = timeConnection + 500;
                        b = true;
                        msg.what = 2;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private MyVolumeReceiver mVolumeReceiver = null;

    //监听耳机的事件
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //检测是否插入耳机，是的话关闭扬声器，否则反之
            if (intent.getAction() == Intent.ACTION_HEADSET_PLUG) {
                if (intent.getIntExtra("state", 0) == 0) {
                    audioManager.setSpeakerphoneOn(true);
                } else if (intent.getIntExtra("state", 0) == 1) {
                    audioManager.setSpeakerphoneOn(false);
                }
            }

        }
    }


    /**
     * 赠送礼物
     *
     * @param gift
     */
    public void senGift(final Gift gift) {
        ApiManager.getApiManager().getService(WebApiService.class).clipOrderPay(UserModel.getUserModel().getMemberId(),
                UserModel.getUserModel().getMemberId(),
                UserModel.getUserModel().getAccountId(),
                toMemberId,
                "",
                gift.giftPrice,
                gift.giftName,
                3,
                "测试",
                gift.giftId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<ClipOrderPayModel>() {
                    @Override
                    public void onNext(ClipOrderPayModel clipOrderPayModel) {
                        if (clipOrderPayModel.success) {
                            giftView.setGift(gift, true);
                            giftView.addNum(1);
                            presentGift(gift, toMemberId);
                            double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(VoiceChatActivity.this)) - gift.giftPrice;
                            PrefAppStore.setCashBalance(VoiceChatActivity.this, cashBalance + "");


                        } else {
                            Toast.makeText(VoiceChatActivity.this, clipOrderPayModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VoiceChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * @param gift
     * @param toMemberId 赠送完礼物进行通知服务器
     */
    public void presentGift(final Gift gift, final String toMemberId) {
        ApiManager.getApiManager().getService(WebApiService.class).presentGift(UserModel.getUserModel().getMemberId(),
                toMemberId,
                gift.giftId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<PresentGiftModel>() {
                    @Override
                    public void onNext(PresentGiftModel presentGiftModel) {
                        if (presentGiftModel.success) {
                            if (presentGiftModel.data.success) {
                                SendGiftModel sendGiftModel = new SendGiftModel();
                                sendGiftModel.toMemberId = toMemberId;
                                sendGiftModel.fromMemberId = UserModel.getUserModel().getMemberId();
                                sendGiftModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
                                sendGiftModel.sendGift = gift;
                                MerriUtils.getApp().socket.emit("sendGift", gson.toJson(sendGiftModel));//切换聊天对象
                                Log.e("---->>>>", presentGiftModel.data.message);
                            } else {
                                Log.e("---->>>>", presentGiftModel.data.message);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VoiceChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 获取查询余额，红包和金币
     */
    public void getWalletInfo() {
        ApiManager.getApiManager().getService(WebApiService.class).queryWalletInfo(UserModel.getUserModel().getAccountId(), "0", UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryWalletInfoModel>() {
                    @Override
                    public void onNext(QueryWalletInfoModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            double giftBalance = 0;
                            if (null == queryWalletInfoModel.data.giftBalance || "".equals(queryWalletInfoModel.data.giftBalance)) {

                            } else {
                                giftBalance = Double.parseDouble(queryWalletInfoModel.data.giftBalance);
                            }
                            PrefAppStore.setCashBalance(VoiceChatActivity.this, giftBalance + "");

                        } else {
                            Toast.makeText(VoiceChatActivity.this, queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VoiceChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
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
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        hangUp();
        finish();
    }


    private void initAudioManager() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void hangUp() {  //挂断事件
        HangUpModel hangUpModel = new HangUpModel();
        hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
        hangUpModel.toMemberId = toMemberId;
        hangUpModel.callType = "0";
        MerriApp.socket.emit("hangUp", gson.toJson(hangUpModel, HangUpModel.class));
        Logger.e("@@@", "发射挂断事件........." + gson.toJson(hangUpModel, HangUpModel.class));
    }


    /**
     * 监听送礼物事件
     * 视频聊天专用
     */
    public Emitter.Listener sendGift = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String callInfo = args[0].toString();
            final SendGiftModel sendGiftModel = gson.fromJson(callInfo, SendGiftModel.class);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    giftView.setGift(sendGiftModel.sendGift, false);
                    giftView.addNum(1);
                    Log.e("---->>", callInfo);
                }
            });


        }
    };


}
