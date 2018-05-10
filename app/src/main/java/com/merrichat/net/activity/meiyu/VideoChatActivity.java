package com.merrichat.net.activity.meiyu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.fragments.FragmentGiftDialog;
import com.merrichat.net.activity.meiyu.fragments.Gift;
import com.merrichat.net.activity.meiyu.fragments.view.GiftItemView;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.CallPeerModel;
import com.merrichat.net.model.ClipOrderPayModel;
import com.merrichat.net.model.HangUpModel;
import com.merrichat.net.model.PresentGiftModel;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.SendGiftModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.permission.Permission;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
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
 * 视频聊天页面
 */

public class VideoChatActivity extends BaseActivity implements WebRtcClient.RtcListener {
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";

    /**
     * Local preview screen position before call is connected.
     * 连接呼叫前的本地预览屏幕位置。
     */
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    /**
     * Local preview screen position after call is connected.
     * 电话后的本地预览屏幕位置已连接。
     */
    private static final int LOCAL_X_CONNECTED = 5;
    private static final int LOCAL_Y_CONNECTED = 63;
    private static final int LOCAL_WIDTH_CONNECTED = 31;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    /**
     * Remote video screen position
     * 远程视频屏幕位置
     */
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    @BindView(R.id.glview_call)
    GLSurfaceView glviewCall;

    @BindView(R.id.tv_hang_up)
    LinearLayout tvHangUp;

    @BindView(R.id.touch_surface_view)
    RelativeLayout touchSurfaceView;

    @BindView(R.id.tv_gift_label)
    TextView mGiftLabel;

    @BindView(R.id.gift_item_first)  //礼物弹出动画
            GiftItemView giftView;


    @BindView(R.id.iv_video_img)
    CircleImageView mVideoImg;   //对方头像


    @BindView(R.id.tv_video_name)
    TextView mVideoName; // 对方昵称


    @BindView(R.id.tv_video_conneting)
    TextView mVideoConneting; //连接等待对方接受邀请


    @BindView(R.id.tv_answer_hang_up)
    TextView mVideoAnswerHangUp;   // 挂断按钮


    @BindView(R.id.tv_hang_up_2)
    TextView mVideoHangUp2;   //挂断下面的文字提示


    @BindView(R.id.connection_time)
    TextView mConnectionTime;   //连接时间显示


    /**
     * 点击小星星view事件
     */
    private TopicDetailTouchListener topicDetailTouchListener;
    private Thread threadStarAnim;  //线程小星星
    public double giftBalance = 0;          //金币余额


    /**
     * VideoRendererGui是一个GLSurfaceView，使用它可以绘制自己的视频流
     */
    private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

    /**
     * WebRTC库允许通过VideoRenderer.Callbacks实现自己的渲染
     * 另外，它提供了一种非常好的默认方式VideoRendererGui
     */
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private WebRtcClient client;
    private String mSocketAddress;
    private String toMemberId;
    private String toMemberName;
    private String toHeadImgUrl;
    private int isFriend = 0;  //是不是好友

    /**
     * CALL_VIDEO_CALLER呼叫者，
     * <p>
     * CALL_VIDEO_ANSWER应答者
     */
    private String video_call_type;

    private Gson gson = new Gson();
    private long timeConnection = 57600_000;  //连接开始时间
    private String[] requestPermissions = Permission.CAMERA;
    private MediaPlayer mMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mVolumeReceiver, filter);
        if (MerriApp.socket == null) {
            try {
                MerriApp.isChatting = false;
                VideoChatModel chatModel = gson.fromJson(PrefAppStore.getSocketAddress(VideoChatActivity.this), VideoChatModel.class);
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
                MerriApp.socket.on("sendGift", sendGift);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //设置状态栏黑色
        setContentView(R.layout.activity_video_chat);

        mMediaPlayer = MediaPlayer.create(VideoChatActivity.this, R.raw.call_running);
        mMediaPlayer.setLooping(true);

        ActivityCompat.requestPermissions(this, requestPermissions, 10086);

        EventBus.getDefault().register(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setSpeakerphoneOn(true);
        isFriend = PrefAppStore.getIsFriend(VideoChatActivity.this);  //是不是好友

        ButterKnife.bind(this);
        glviewCall = (GLSurfaceView) findViewById(R.id.glview_call);
        glviewCall.setPreserveEGLContextOnPause(true);
        glviewCall.setKeepScreenOn(true);
        VideoRendererGui.setView(glviewCall, new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });

        //远程流
        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);

        //本地流
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        topicDetailTouchListener = new TopicDetailTouchListener(touchSurfaceView, this);
        touchSurfaceView.setOnTouchListener(topicDetailTouchListener);  //布局可以点击出现小星星
        threadStarAnim = new Thread(new ThreadShow());  //小星星线程
        threadStarAnim.start();

        getWalletInfo();
        mGiftLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGiftDialog.newInstance().setOnGridViewClickListener(8, new FragmentGiftDialog.OnGridViewClickListener() {
                    @Override
                    public void click(Gift gift) {
                        double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(VideoChatActivity.this)) - gift.giftPrice;
                        if (cashBalance >= 0) {
                            //送礼物
                            senGift(gift);
                        } else {
                            Toast.makeText(VideoChatActivity.this, "当前账户余额不足～！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show(getSupportFragmentManager(), "dialog");
            }
        });

        mVideoAnswerHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (MerriApp.isChatting){
                        HangUpModel hangUpModel = new HangUpModel();
                        hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
                        hangUpModel.toMemberId = toMemberId;
                        hangUpModel.callType = "1";
                        MerriUtils.getApp().socket.emit("hangUp", gson.toJson(hangUpModel, HangUpModel.class));
                        Logger.e("@@@", "发射挂断事件.........4");
                        finish();
                    }else {
                        mVideoAnswerHangUp.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HangUpModel hangUpModel = new HangUpModel();
                                hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
                                hangUpModel.toMemberId = toMemberId;
                                hangUpModel.callType = "1";
                                MerriUtils.getApp().socket.emit("hangUp", gson.toJson(hangUpModel, HangUpModel.class));
                                Logger.e("@@@", "发射挂断事件.........5");
                                finish();
                            }
                        },1200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("RequestCode", requestCode + "");
        for (int i = 0; i < permissions.length; i++) {
            Log.e("permissionInfo", "permissionName：" + permissions[i] + "permission State：" + grantResults[i]);
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_VIDEO_CHAT_CALL) {//call   我是主叫的时候返回
            if (mMediaPlayer!= null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            String answerInfo = myEventBusModel.VIDEO_CHAT_CALL_CALLINFO;
            toMemberId = JSON.parseObject(answerInfo).getString("answerMemberId");
            client.setVideoChatConnection();
            timeConnection = 57600_000;
        }
    }

    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mConnectionTime.setText(DateTimeUtil.formatDaojiShi(new Date(timeConnection)));
            if (MerriApp.isChatting) {
                topicDetailTouchListener.startAnimation((mGiftLabel.getRight() + mGiftLabel.getLeft()) / 2, (mGiftLabel.getTop() - 100));  //动画出现的位置
                mVideoImg.setVisibility(View.GONE);
                mVideoName.setVisibility(View.GONE);
                mVideoConneting.setVisibility(View.GONE);
                mGiftLabel.setVisibility(View.VISIBLE);
                mVideoAnswerHangUp.setVisibility(View.GONE);
                mVideoHangUp2.setVisibility(View.GONE);
            } else {
                if (!MerriApp.isChatting && CALL_VIDEO_ANSWER_TYPE.equals(video_call_type)){
                    if (timeConnection == 57608000l) {
                        GetToast.useString(cnt, "对方已挂断～！");
                        hangUp();
                        finish();
                    }
                }
                Glide.with(VideoChatActivity.this).load(toHeadImgUrl).into(mVideoImg);   //拨打电话的时候显示
                mVideoName.setText(toMemberName);
                mVideoImg.setVisibility(View.VISIBLE);
                mVideoName.setVisibility(View.VISIBLE);
                mVideoConneting.setVisibility(View.VISIBLE);
                mGiftLabel.setVisibility(View.GONE);
                mVideoAnswerHangUp.setVisibility(View.VISIBLE);
                mVideoHangUp2.setVisibility(View.GONE);

                if (timeConnection == 57620000l) {
                    GetToast.useString(cnt, "对方可能不在身边，稍等下再试吧～！");
                }
                if (timeConnection == 57634500l) {
                    GetToast.useString(cnt, "对方未接听～！");
                }
                if (timeConnection == 57635000l) {
                    hangUp();
                    Logger.e("@@@", "发射挂断事件.........6");
                    finish();
                }
            }
        }
    };

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = 1;
                    timeConnection = timeConnection + 500;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initView() {
        video_call_type = getIntent().getStringExtra(CALL_VIDEO_TYPE_KEY);
        toMemberId = getIntent().getStringExtra("toMemberId");
        toMemberName = getIntent().getStringExtra("toMemberName");
        toHeadImgUrl = getIntent().getStringExtra("toHeadImgUrl");
        String toSocketId = getIntent().getStringExtra("toSocketId");

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), toMemberId);
        client.setVideoChatConnection();
        startCam();
        if (CALL_VIDEO_CALLER_TYPE.equals(video_call_type)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVideoConneting.setVisibility(View.VISIBLE);
                    mVideoConneting.setText("正在等待对方接受邀请...");

                }
            });
            mMediaPlayer.start();
            sendCallInfo();
        } else if (CALL_VIDEO_ANSWER_TYPE.equals(video_call_type)) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoConneting.setVisibility(View.GONE);
                    }
                });
                answer(toSocketId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCallInfo() {    // 发送呼叫事件
        CallPeerModel callPeerModel = new CallPeerModel();
        callPeerModel.fromMemberId = UserModel.getUserModel().getMemberId();
        callPeerModel.toMemberId = toMemberId;
        callPeerModel.fromMemberName = UserModel.getUserModel().getRealname();
        callPeerModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
        callPeerModel.callSource = "0";
        callPeerModel.callType = "1";
        callPeerModel.fromLatitude = MerriApp.mLatitude;
        callPeerModel.fromLongitude = MerriApp.mLongitude;
        MerriApp.socket.emit("callPeer", gson.toJson(callPeerModel, CallPeerModel.class));
        Logger.e("@@@", "--------发送呼叫事件------" + gson.toJson(callPeerModel, CallPeerModel.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        glviewCall.onPause();
        if (client != null) {
            client.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glviewCall.onResume();
        if (client != null) {
            client.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null){
            mMediaPlayer.release();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
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
                    GetToast.useString(cnt, "视频聊天结束");
                    android.os.Process.killProcess(android.os.Process.myPid());
                    Logger.e("@@@", "发射挂断事件.........7");
                    finish();
                } else if (newStatus.equals("CONNECTED")) {
                    timeConnection = 57600_000;  //连接开始时间
                    MerriApp.isChatting = true;
                    GetToast.useString(cnt, "连接成功");
                    if (isFriend == 1) {  //好友计费
                        Log.e("---------->>>>", "是好友");
                    } else {
                        Log.e("---------->>>>", "不是好友");
                        startLiving();
                    }
                }
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        //可以在我们的渲染器添加到VideoTrack
        localStream.videoTracks.getFirst().addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, true);

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        //远程
        VideoRendererGui.update(remoteRender,
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);

        //本地
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED,
                LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED,
                scalingType, true);
    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {
        //本地
        VideoRendererGui.update(localRender,
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                scalingType, true);
    }

    @OnClick({R.id.tv_hang_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hang_up:
                try {
                    if (MerriApp.isChatting){
                        hangUp();
                        Logger.e("@@@", "发射挂断事件.........1");
                        finish();
                    }else {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hangUp();
                                Logger.e("@@@", "发射挂断事件.........2");
                                finish();
                            }
                        },1200);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 视频聊天开始
     * 视频开始之后开始发送计费逻事件
     */
    private void startLiving() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
            jsonObject.put("toMemberId", toMemberId);
            jsonObject.put("callSource", 0);
            jsonObject.put("chatPrice", 2);
            jsonObject.put("isFriend", isFriend);
            if (CALL_VIDEO_CALLER_TYPE.equals(video_call_type)) {
                jsonObject.put("isCaller", 1);
            } else {
                jsonObject.put("isCaller", 0);
            }

            MerriUtils.getApp().socket.emit("startVchat", jsonObject.toString());//发射搜索事件
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("@@@", "视频聊天开始发射事件......");
    }


    private AudioManager audioManager;
    /**
     * 耳机事件
     */
    private MyVolumeReceiver mVolumeReceiver = null;

    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                            giftView.setGift(gift,true);
                            giftView.addNum(1);
                            presentGift(gift, toMemberId);
                            double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(VideoChatActivity.this)) - gift.giftPrice;
                            PrefAppStore.setCashBalance(VideoChatActivity.this, cashBalance + "");


                        } else {
                            Toast.makeText(VideoChatActivity.this, clipOrderPayModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VideoChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
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
                            Logger.e("---->>>>", presentGiftModel.data.message);
                            SendGiftModel sendGiftModel = new SendGiftModel();
                            sendGiftModel.toMemberId = toMemberId;
                            sendGiftModel.fromMemberId = UserModel.getUserModel().getMemberId();
                            sendGiftModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
                            sendGiftModel.sendGift = gift;
                            MerriUtils.getApp().socket.emit("sendGift", gson.toJson(sendGiftModel));//切换聊天对象

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VideoChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
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
                            if (null == queryWalletInfoModel.data.giftBalance || "".equals(queryWalletInfoModel.data.giftBalance)) {
                                giftBalance = 0;
                            } else {
                                giftBalance = Double.parseDouble(queryWalletInfoModel.data.giftBalance);
                                PrefAppStore.setCashBalance(VideoChatActivity.this, giftBalance + "");

                            }
                        } else {
                            Toast.makeText(VideoChatActivity.this, queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(VideoChatActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
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
        try {
            if (mMediaPlayer != null){
                mMediaPlayer.release();
            }
            hangUp();
            Logger.e("@@@", "发射挂断事件.........3");
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hangUp() {  //挂断事件
        HangUpModel hangUpModel = new HangUpModel();
        hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
        hangUpModel.toMemberId = toMemberId;
        hangUpModel.callType = "1";
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
                    giftView.setGift(sendGiftModel.sendGift,false);
                    giftView.addNum(1);
                    Logger.e("---->>", callInfo);
                }
            });
        }
    };


}
