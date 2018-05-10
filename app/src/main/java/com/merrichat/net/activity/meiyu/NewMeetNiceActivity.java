package com.merrichat.net.activity.meiyu;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.fragments.FragmentGiftDialog;
import com.merrichat.net.activity.meiyu.fragments.Gift;
import com.merrichat.net.activity.meiyu.fragments.view.GiftItemView;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AddGoodFriendsModel;
import com.merrichat.net.model.BeReportModel;
import com.merrichat.net.model.BeautifulHangUpModel;
import com.merrichat.net.model.ClipOrderPayModel;
import com.merrichat.net.model.GetCommunicationFeeModel;
import com.merrichat.net.model.HangUpModel;
import com.merrichat.net.model.PresentGiftModel;
import com.merrichat.net.model.QueryGoodFriendRequestModel;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.SearchModel;
import com.merrichat.net.model.SendGiftModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.permission.Permission;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.CheckHttpUtil;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CircleImageView;
import com.merrichat.net.view.ReportDialog;
import com.othershe.nicedialog.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.emitter.Emitter;

/**
 * Created by amssy on 17/12/21.
 * 美遇--匹配聊天页面
 */

public class NewMeetNiceActivity extends BaseActivity implements NiceMeetWebRtcClient.RtcListener {
    private static final String VIDEO_CODEC_H264 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";

    private static final int videoWidth = 480;
    private static final int videoHeight = 800;
    //对面视屏设置
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;

    //本地视屏设置
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;

    //连接成功、本地视屏设置
    private static final int LOCAL_X_CONNECTED = 6;
    private static final int LOCAL_Y_CONNECTED = 60;
    private static final int LOCAL_WIDTH_CONNECTED = 30;
    private static final int LOCAL_HEIGHT_CONNECTED = 30;

    public static double giftBalance = 0;          //金币余额


    @BindView(R.id.glview_call)
    GLSurfaceView videoView;

    @BindView(R.id.rl_video_chat_ing)
    RelativeLayout rlVideoChatIng;


    @BindView(R.id.touch_surface_view)
    RelativeLayout touchSurfaceView;

    /**
     * 头像
     */
    @BindView(R.id.civ_meet_photo)
    CircleImageView civMeetPhoto;

    /**
     * 对方昵称
     */
    @BindView(R.id.tv_meet_name)
    TextView tvMeetName;

    /**
     * 退出美遇视频聊天
     */
    @BindView(R.id.ll_colse_meet)
    LinearLayout llColseMeet;


    /**
     * 举报
     */
    @BindView(R.id.ll_report)
    LinearLayout llReport;


    /**
     * 切换
     */
    @BindView(R.id.ll_switch)
    LinearLayout llSwitch;


    /************以下为美遇搜索页面的各种id**********************/

    @BindView(R.id.ll_search_ing)
    LinearLayout llSearchIng;


    /**
     * 关闭本页面
     */
    @BindView(R.id.iv_close_nice_meet)
    ImageView ivCloseNiceMeet;

    /**
     * 筛选 and 遇到的人  and  左上角X号布局
     */
    @BindView(R.id.rl_shai_xuan)
    RelativeLayout rlShaiXuan;


    /**
     * 筛选条件
     */
    @BindView(R.id.tv_filter_conditions)
    TextView tvFilterConditions;

    /**
     * 遇到的人
     */
    @BindView(R.id.tv_encounter)
    TextView tvEncounter;

    /*****以下为搜索中状态下的各种id**********/
    @BindView(R.id.ll_search_time)
    LinearLayout llSearchTime;

    @BindView(R.id.civ_my_photo)
    SimpleDraweeView civMyPhoto;

    /**
     * 时间  寻找配对时间
     */
    @BindView(R.id.tv_time)
    TextView tvTime;

    /**
     * 添加好友
     */
    @BindView(R.id.tv_add_friend)
    TextView addFriend;

    @BindView(R.id.tv_search)
    TextView tvSearch;

    @BindView(R.id.tv_searching)
    TextView tvSearching;


    @BindView(R.id.iv_gift)
    ImageView ivGift;//礼物 点击按钮


    @BindView(R.id.gift_item_first)
    GiftItemView giftView; //礼物弹出动画

    /**
     * 筛选男女、 默认为 女：2
     * 2：女
     * 1：男
     * 3 男神
     * 4女神
     */
    private String filterGender = "2";


    /**
     * 是否正在搜索
     * true 正在搜索
     * false  没有正在搜索
     */
    private boolean isSearch = false;


    /**
     * 是否是美遇视频聊天、然后退出视频（并停止搜索）
     */
    private boolean isVideoFlag = false;


    /**
     * 是否正在视频
     * true 是 ---正在视频
     * false 不是 ---- 搜索或者搜索中的状态
     */
    public static boolean isVideoChatIng = false;


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
    private NiceMeetWebRtcClient client;

    /**
     * 对方的memberId
     */
    public static String toMemberId;

    /**
     * 对方的名字
     */
    private String fromMemberName;

    /**
     * 对方的头像url
     */
    private String fromHeadImgUrl;

    /**
     * 对方手机号
     */
    private String fromMobile;

    /**
     * 对方acctoutId
     */
    private String fromAccountId;

    /**
     * 点击小星星view事件
     */
    private TopicDetailTouchListener topicDetailTouchListener;
    private Thread threadStarAnim;  //线程小星星

    private ReportDialog reportDialog; // 举报弹窗
    private double longitude;  //精度
    private double latitude;  //维度


    private int connectionTime = 0;  //网络视频连接时间，默认时间是0单位毫秒
    private long countDown = 0;  //倒计时时间
    private int mConnectionTime = 0;

    private Gson gson = new Gson();
    private double mChatPrice = 0;  //  聊天单价
    private boolean isFriends = false;  //是否是好友

    /**
     * 自己的身份
     * 2女
     * 1男
     * 3男神
     * 4女神
     */
    private String niceMeetGender = "2";

    private String[] requestPermissions = Permission.CAMERA;

    private FilterBodysMenu filterBodysMenu;  //美遇dialog筛选条件显示
    private int filterSelectPosition = 0;

    private AnimatorSet set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC); // AudioManager.STREAM_MUSIC
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        super.onCreate(savedInstanceState);
        mVolumeReceiver = new MyVolumeReceiver(); //麦克连接状态监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mVolumeReceiver, filter);

        if (MerriApp.socket == null) {  //重新建立socket连接
            try {
                VideoChatModel chatModel = gson.fromJson(PrefAppStore.getSocketAddress(NewMeetNiceActivity.this), VideoChatModel.class);
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
        setContentView(R.layout.activity_new_meet_video_chat);
        ActivityCompat.requestPermissions(this, requestPermissions, 10086);
        ButterKnife.bind(this);

        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        initData();  //1
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("RequestCode", requestCode + "");
        for (int i = 0; i < permissions.length; i++) {
            Logger.e("permissionInfo", "permissionName：" + permissions[i] + "permission State：" + grantResults[i]);
        }
    }


    //EventBus  回调返回
    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_MEET_NICE_ANSWER) {//answer   判断当前用户的状态是否收到answer
            String callInfo = myEventBusModel.NICE_MEET_ANSWER_CALLINFO;
            toMemberId = JSON.parseObject(callInfo).getString("fromMemberId");
            fromMemberName = JSON.parseObject(callInfo).getString("fromMemberName");
            fromHeadImgUrl = JSON.parseObject(callInfo).getString("fromHeadImgUrl");
            fromMobile = JSON.parseObject(callInfo).getString("fromMobile");
            fromAccountId = JSON.parseObject(callInfo).getString("fromAccountId");
            String toSocketId = JSON.parseObject(callInfo).getString("toSocketId");
            client.setVideoChatConnectionNew(toMemberId);
            Logger.e("@@@", "------收到answer事件.....");
            try {
                answer(toSocketId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rlVideoChatIng.setVisibility(View.VISIBLE);
                    llSearchIng.setVisibility(View.GONE);

                    tvMeetName.setText(fromMemberName);
                    Glide.with(cnt).load(fromHeadImgUrl).into(civMeetPhoto);
                }
            });
            isSearch = false;
            getWalletInfo();

        } else if (myEventBusModel.REFRESH_MEET_NICE_CALL) {//call
            String answerInfo = myEventBusModel.NICE_MEET_CALL_ANSWERINFO;
            toMemberId = JSON.parseObject(answerInfo).getString("answerMemberId");
            fromMemberName = JSON.parseObject(answerInfo).getString("fromMemberName");
            fromHeadImgUrl = JSON.parseObject(answerInfo).getString("fromHeadImgUrl");
            fromMobile = JSON.parseObject(answerInfo).getString("fromMobile");
            fromAccountId = JSON.parseObject(answerInfo).getString("fromAccountId");
            String toSocketId = JSON.parseObject(answerInfo).getString("toSocketId");

            client.setVideoChatConnectionNew(toMemberId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rlVideoChatIng.setVisibility(View.VISIBLE);
                    llSearchIng.setVisibility(View.GONE);

                    tvMeetName.setText(fromMemberName);
                    Glide.with(cnt).load(fromHeadImgUrl).into(civMeetPhoto);
                }
            });
            isSearch = false;
            getWalletInfo();
        }
    }

    private void initData() {
        topicDetailTouchListener = new TopicDetailTouchListener(touchSurfaceView, this);
        touchSurfaceView.setOnTouchListener(topicDetailTouchListener);  //布局可以点击出现小星星
        threadStarAnim = new Thread(new ThreadShow());  //小星星线程
        threadStarAnim.start();

        llSearchIng.setVisibility(View.VISIBLE);
        rlVideoChatIng.setVisibility(View.GONE);
        rlShaiXuan.setVisibility(View.VISIBLE);
        llSearchTime.setVisibility(View.GONE);
        tvSearch.setText("搜索");
        tvSearching.setBackgroundResource(R.drawable.merri_chat_search);
        tvSearch.setBackgroundResource(R.drawable.shape_sou_suo);

        civMyPhoto.setImageURI(UserModel.getUserModel().getImgUrl());


        videoView = (GLSurfaceView) findViewById(R.id.glview_call);
        videoView.setPreserveEGLContextOnPause(true);
        videoView.setKeepScreenOn(true);

        VideoRendererGui.setView(videoView, new Runnable() {
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

        if (!CheckHttpUtil.isNetworkConnected(NewMeetNiceActivity.this)) {
            Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧！", Toast.LENGTH_LONG).show();
        }
        if (MerriUtils.getApp() != null && null != MerriUtils.getApp().searchResult) {
            MerriApp.socket.on("searchResult", MerriApp.searchResult);
            //监听---美遇对方切换
            MerriApp.socket.on("change", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    restrStartSearch();
                }
            });
            //监听---美遇对方挂断
            MerriApp.socket.on("beautifulHangUp", new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    Logger.e("@@@", "---->>收到对方挂断事件.......");
                    restrStartSearch();
                }
            });
        }
        searchBtnStopAnimation();//第一次家在的时候默认停止动画
        queryStar();

    }


    /**
     * 查询是否是男神、女神
     */
    private void queryStar() {
        OkGo.<String>post(Urls.okdiLifeResiterQueryStar)
                .params("mobile", UserModel.getUserModel().getMobile())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                niceMeetGender = UserModel.getUserModel().getGender();
                                getCommunicationFee(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initView() {  //2
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, videoWidth, videoHeight, 30, 1, VIDEO_CODEC_H264, true, 1, AUDIO_CODEC_OPUS, true);
        client = new NiceMeetWebRtcClient(this, params, VideoRendererGui.getEGLContext());
        startCam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.onPause();
        if (client != null) {
            client.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
        if (client != null) {
            client.onResume();
        }
    }


    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
    }

    public void startCam() {
        client.setCamera();
    }


    @Override
    public void onLocalStream(MediaStream localStream) {
        //可以在我们的渲染器添加到VideoTrack
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
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
        if (isVideoFlag) {
            VideoRendererGui.update(localRender,
                    LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                    LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
                    scalingType, true);
        }
    }

    @Override
    public void connectionSucceeded() {
        startLiving();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isVideoChatIng = true;
                GetToast.useString(cnt, "连接成功");
                queryGoodFriendRequest(toMemberId);
            }
        });

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
            jsonObject.put("callSource", 1);
            jsonObject.put("chatPrice", mChatPrice);
            MerriUtils.getApp().socket.emit("startVchat", jsonObject.toString());//发射搜索事件
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("@@@", "-->> 视频聊天开始发射事件......");
    }


    @Override
    public void disConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GetToast.useString(cnt, "断开连接");
                closeVideoChat();
            }
        });
    }


    /**
     * 美遇对方挂断/ 双方切换   重新搜索
     */
    private void restrStartSearch() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rlVideoChatIng.setVisibility(View.GONE);
                llSearchIng.setVisibility(View.VISIBLE);
                rlShaiXuan.setVisibility(View.GONE);
                llSearchTime.setVisibility(View.VISIBLE);
                tvSearch.setText("停止");
            }
        });
        isSearch = true;
        countDown = 0;
        isVideoFlag = true;
        isVideoChatIng = false;
        client.removePeer();

        //判断当前用户是否被举报
        ApiManager.getApiManager().getService(WebApiService.class).beReport(UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<BeReportModel>() {
                    @Override
                    public void onNext(BeReportModel beReportModel) {
                        if (beReportModel.success) {
                            MerriUtils.isBeReport = beReportModel.data.flag;
                            if (!beReportModel.data.flag) {
                                isSearch = true;
                                countDown = 0;
                                tvSearch.setText("停止");
                                rlShaiXuan.setVisibility(View.GONE);
                                llSearchTime.setVisibility(View.VISIBLE);
                                startSearch();
                            } else {
                                GetToast.showToast(NewMeetNiceActivity.this, "您已被举报，暂时停用美遇功能");

                                isSearch = false;
                                isVideoFlag = true;
                                isVideoChatIng = false;
                            }
                        } else {
                            GetToast.showToast(NewMeetNiceActivity.this, "服务端返回异常");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        GetToast.showToast(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！");
                    }
                });

    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        searchBtnStartAnimation();
        SearchModel searchModel = new SearchModel();
        try {
            searchModel.fromMemberId = UserModel.getUserModel().getMemberId();
            searchModel.fromMemberName = UserModel.getUserModel().getRealname();
            searchModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
            searchModel.fromGender = UserModel.getUserModel().getGender();
            searchModel.searchGender = filterGender + "";
            searchModel.longitude = longitude;
            searchModel.latitude = latitude;
            if (longitude + "".length() < 8) {
                GetToast.showToast(NewMeetNiceActivity.this, "打开定位才能遇到心仪的人哦～！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MerriUtils.getApp().socket.emit("search", gson.toJson(searchModel));//发射搜索事件
        Log.e("@@@", "发射搜索事件......");
    }


    /**
     * 停止搜索
     */
    private void stopSearch() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
            MerriUtils.getApp().socket.emit("beautifulExit", jsonObject.toString());
            Log.e("@@@", "停止搜索。。。。");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 退出美遇视频聊天(同时也退出搜索池)
     */
    private void closeVideoChat() {
        BeautifulHangUpModel beautifulHangUpModel = new BeautifulHangUpModel();
        beautifulHangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
        beautifulHangUpModel.toMemberId = toMemberId;

        MerriUtils.getApp().socket.emit("beautifulHangUp", gson.toJson(beautifulHangUpModel));

        Log.e("@@@", "退出美遇聊天：--->>" + gson.toJson(beautifulHangUpModel));
        isSearch = false;
        isVideoFlag = true;
        isVideoChatIng = false;
        client.removePeer();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rlVideoChatIng.setVisibility(View.GONE);
                llSearchIng.setVisibility(View.VISIBLE);
                rlShaiXuan.setVisibility(View.VISIBLE);
                llSearchTime.setVisibility(View.GONE);
                tvSearch.setText("搜索");
                tvSearching.setBackgroundResource(R.drawable.merri_chat_search);
                tvSearch.setBackgroundResource(R.drawable.shape_sou_suo);

            }
        });

    }

    @OnClick({R.id.ll_colse_meet, R.id.iv_close_nice_meet, R.id.tv_search, R.id.iv_gift, R.id.ll_report, R.id.ll_switch, R.id.tv_filter_conditions, R.id.tv_encounter, R.id.tv_add_friend})
    public void onViewClicked(View view) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ll_colse_meet://退出美遇视频聊天
                if (isVideoChatIng && mConnectionTime > 12) {
                    closeVideoChat();
                    searchBtnStopAnimation();  //按钮停止动画
                } else {
                    if (!MerriUtils.isFastDoubleClick()) {
                        llColseMeet.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                closeVideoChat();
                                searchBtnStopAnimation();  //按钮停止动画
                            }
                        }, 4500);
                    }
                }
                isVideoChatIng = false;

                break;
            case R.id.iv_close_nice_meet://关闭本页面
                isVideoChatIng = false;
                finish();
                break;
            case R.id.tv_search:  //搜索
                if (isSearch) {  //是否正在搜索中
                    isSearch = false;
                    tvSearch.setText("搜索");
                    rlShaiXuan.setVisibility(View.VISIBLE);
                    llSearchTime.setVisibility(View.GONE);
                    stopSearch();

                    tvSearching.setBackgroundResource(R.drawable.merri_chat_search);
                    tvSearch.setBackgroundResource(R.drawable.shape_sou_suo);
                    searchBtnStopAnimation();  //按钮停止动画
                } else {
                    //出现动画
                    getWalletCoinInfo();
                }


                break;
            case R.id.iv_gift://送礼物
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                FragmentGiftDialog.newInstance().setOnGridViewClickListener(8, new FragmentGiftDialog.OnGridViewClickListener() {
                    @Override
                    public void click(Gift gift) {
                        double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(NewMeetNiceActivity.this)) - gift.giftPrice;
                        if (cashBalance > 0) {
                            //送礼物
                            senGift(gift);
                        } else {
                            GetToast.showToast(NewMeetNiceActivity.this, "当前账户余额不足～！");
                        }
                    }
                }).show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.ll_report://举报
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                reportDialog = new ReportDialog(cnt);
                reportDialog.setOnclickInterReport(new ReportDialog.ReportOnclick() {
                    @Override
                    public void report(String type, String typeName) {
                        reportingAudit(type, typeName);
                    }
                });
                reportDialog.show();
                break;
            case R.id.ll_switch://切换
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                SwitchMeet();
                break;
            case R.id.tv_filter_conditions://筛选
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                getCommunicationFee(false);
                break;

            case R.id.tv_encounter:  //遇到的人
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                Intent intent = new Intent(NewMeetNiceActivity.this, MeatBodysActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_add_friend:  //添加好友
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                addGoodFriends(toMemberId, fromMemberName, fromHeadImgUrl);
                break;
        }
    }


    /**
     * 搜索按钮停止动画
     */
    private void searchBtnStopAnimation() {
        if (set != null) {
            set.end();
        }
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(tvSearching, "alpha", 1f, 1f);
        alphaIn.setDuration(1000);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvSearching, "scaleX", 0.85f, 0.85f);
        scaleX.setDuration(1000);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvSearching, "scaleY", 0.85f, 0.85f);
        scaleY.setDuration(1000);
        set = new AnimatorSet();
        set.play(alphaIn).with(scaleX).with(scaleY);
        set.start();
    }


    /**
     * 搜索按钮开始动画
     */
    private void searchBtnStartAnimation() {
        if (set != null) {
            set.end();
        }
        //按钮透明变化 从不透明变化到透明
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(tvSearching, "alpha", 1f, 0f);
        //执行的总时间为1000毫秒
        alphaIn.setDuration(1000);
        //设置无限循环模式
        alphaIn.setRepeatCount(ValueAnimator.INFINITE);

        //按钮横向动画 初始化0.9倍大小 最大变化到1.2倍大小
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvSearching, "scaleX", 0.75f, 1.0f);
        //执行的总时间为1000毫秒
        scaleX.setDuration(1000);
        //设置无限循环模式
        scaleX.setRepeatCount(ValueAnimator.INFINITE);

        //按钮纵向变化
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvSearching, "scaleY", 0.75f, 1.0f);
        scaleY.setDuration(1000);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);

        set = new AnimatorSet();
        set.play(alphaIn).with(scaleX).with(scaleY);
        set.start();   //启动搜索动画

    }


    /**
     * 举报对方
     *
     * @param type
     */
    private void reportingAudit(String type, String typeName) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientMemberName", UserModel.getUserModel().getRealname());
            jsonObject.put("clientPhone", UserModel.getUserModel().getMobile());
            jsonObject.put("clientMemberId", UserModel.getUserModel().getMemberId());
            jsonObject.put("beTipOffMemberName", fromMemberName);
            jsonObject.put("beTipOffPhone", fromMobile);
            jsonObject.put("beTipOffContent", typeName);
            jsonObject.put("beTipOffMemberId", toMemberId);
            jsonObject.put("beTipOffType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Urls.reportingAudit)
                .params("file", "")
                .params("jsonReportInfo", jsonObject.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body());
                            if (jsonObject1.optBoolean("success")) {
                                JSONObject data = jsonObject1.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "举报成功");
                                    if (reportDialog != null) {
                                        reportDialog.dismiss();
                                    }
                                }
                            } else {
                                String message = jsonObject1.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 切换美遇聊天对象
     */
    private void SwitchMeet() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
            jsonObject.put("toMemberId", toMemberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MerriUtils.getApp().socket.emit("change", jsonObject.toString());//切换聊天对象
        restrStartSearch();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        if (isVideoChatIng) {//正在视频聊天、退出视频聊天
            closeVideoChat();
        } else {
            if (isSearch) {//正在搜索、停止搜索
                stopSearch();
                isSearch = false;
            }
            isVideoChatIng = false;
            finish();
        }
    }

    int[] location = new int[2];

    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (isVideoChatIng) {
                ivGift.getLocationOnScreen(location);
                topicDetailTouchListener.startAnimation((ivGift.getLeft() + ivGift.getRight()) / 2, (ivGift.getTop() - 120));  //动画出现的位置
            }
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    if (isSearch) {
                        countDown = countDown + 1000;
                        tvTime.setText(DateTimeUtil.format9(new Date(countDown))); //计算显示搜索的时间
                    } else {
                        countDown = 0;
                    }
                    break;
            }
            if (isVideoChatIng) {  // 当且只有正在直播的时候开始记录连接时间
                mConnectionTime = mConnectionTime + 1;
                if (mConnectionTime > 1200) {  //判断当前的连接时间是否大于10分钟
                    if (isFriends) {   //判断是否是好友  如果是好友则不显示加好友按钮
                        addFriend.setVisibility(View.GONE);
                    } else {
                        addFriend.setVisibility(View.VISIBLE);
                    }

                } else {
                    addFriend.setVisibility(View.GONE);
                }
                connectionTime = connectionTime - 1;

            } else {
                if (addFriend.getVisibility() == View.VISIBLE) {
                    addFriend.setVisibility(View.GONE);
                }
                mConnectionTime = 0;
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
                    } else {
                        b = true;
                        msg.what = 2;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        locationService = ((MerriApp) getApplication()).locationService;
        locationService.registerListener(merriLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(merriLocationListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    /**
     * 百度定位
     */
    private LocationService locationService;
    //定位回调，根据需求获取所需要的参数，下面是所有参数
    private BDLocationListener merriLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    };


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
                fromAccountId,
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
                            double cashBalance = Double.parseDouble(PrefAppStore.getCashBalance(NewMeetNiceActivity.this)) - gift.giftPrice;
                            PrefAppStore.setCashBalance(NewMeetNiceActivity.this, cashBalance + "");
                        } else {
                            Toast.makeText(NewMeetNiceActivity.this, clipOrderPayModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
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
                            Log.e("---->>>>", "礼物赠送成功");
                            SendGiftModel sendGiftModel = new SendGiftModel();
                            sendGiftModel.toMemberId = toMemberId;
                            sendGiftModel.fromMemberId = UserModel.getUserModel().getMemberId();
                            sendGiftModel.fromHeadImgUrl = UserModel.getUserModel().getImgUrl();
                            sendGiftModel.sendGift = gift;
                            Log.e("---->>>>", gson.toJson(sendGiftModel));
                            MerriUtils.getApp().socket.emit("sendGift", gson.toJson(sendGiftModel));//切换聊天对象
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
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
                                PrefAppStore.setCashBalance(cnt, queryWalletInfoModel.data.giftBalance);
                            }
                            connectionTime = (int) giftBalance;
                        } else {
                            Toast.makeText(NewMeetNiceActivity.this, queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 添加好友接口
     *
     * @param toMemberId   好友的 memberId
     * @param toMemberName 好友的名字
     * @param toMemberUrl  好友头像
     */
    public void addGoodFriends(String toMemberId, String toMemberName, String toMemberUrl) {
        ApiManager.getApiManager().getService(WebApiService.class).addGoodFriends(
                UserModel.getUserModel().getMemberId(),
                UserModel.getUserModel().getRealname(),
                UserModel.getUserModel().getImgUrl(),
                toMemberId,
                toMemberName,
                toMemberUrl,
                "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<AddGoodFriendsModel>() {
                    @Override
                    public void onNext(AddGoodFriendsModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            if (queryWalletInfoModel.data.suc) {
                                Toast.makeText(NewMeetNiceActivity.this, queryWalletInfoModel.data.message, Toast.LENGTH_LONG).show();
                                addFriend.setBackgroundResource(R.drawable.bg_round_gray70);
                                addFriend.setText("等待对方接受");
                                addFriend.setClickable(false);

                            } else {
                                Toast.makeText(NewMeetNiceActivity.this, queryWalletInfoModel.data.message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(NewMeetNiceActivity.this, queryWalletInfoModel.msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private MyVolumeReceiver mVolumeReceiver = null;


    private AudioManager audioManager;

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
     * 判断当前用户是否被举报
     */
    public void getBeReport() {
        ApiManager.getApiManager().getService(WebApiService.class).beReport(UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<BeReportModel>() {
                    @Override
                    public void onNext(BeReportModel beReportModel) {
                        if (beReportModel.success) {
                            MerriUtils.isBeReport = beReportModel.data.flag;
                            if (!beReportModel.data.flag) {
                                isSearch = true;
                                countDown = 0;
                                tvSearch.setText("停止");
                                rlShaiXuan.setVisibility(View.GONE);
                                llSearchTime.setVisibility(View.VISIBLE);
                                startSearch();

                            } else {
                                Toast.makeText(NewMeetNiceActivity.this, beReportModel.data.message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(NewMeetNiceActivity.this, "服务端返回异常", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 获取查询金币
     */
    public void getWalletCoinInfo() {
        ApiManager.getApiManager().getService(WebApiService.class).queryWalletInfo(UserModel.getUserModel().getAccountId(), "0", UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryWalletInfoModel>() {
                    @Override
                    public void onNext(QueryWalletInfoModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            if (TextUtils.isEmpty(queryWalletInfoModel.data.giftBalance)) {
                                giftBalance = 0;
                                Toast.makeText(NewMeetNiceActivity.this, "服务端返钱包回异常", Toast.LENGTH_LONG).show();
                            } else {
                                giftBalance = Double.parseDouble(queryWalletInfoModel.data.giftBalance);
                                if (giftBalance >= 0) {
                                    getBeReport();
                                    PrefAppStore.setCashBalance(cnt, queryWalletInfoModel.data.giftBalance);
                                } else {
                                    Toast.makeText(NewMeetNiceActivity.this, "当前余额不足", Toast.LENGTH_LONG).show();
                                }

                            }

                        } else {
                            Toast.makeText(NewMeetNiceActivity.this, queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 查询是否有好友请求
     */
    private void queryGoodFriendRequest(String toMemberId) {
        ApiManager.getApiManager().getService(WebApiService.class).queryGoodFriendRequest(UserModel.getUserModel().getMemberId(), toMemberId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<QueryGoodFriendRequestModel>() {
                    @Override
                    public void onNext(QueryGoodFriendRequestModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            if (null == queryWalletInfoModel.data) {
                                return;
                            }
                            isFriends = (queryWalletInfoModel.data.state == 3);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(NewMeetNiceActivity.this, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 美遇筛选条件
     */
    public void getCommunicationFee(final boolean isFirstShow) {
        ApiManager.getApiManager().getService(WebApiService.class).getCommunicationFee(UserModel.getUserModel().getMemberId(), "1", niceMeetGender)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<GetCommunicationFeeModel>() {
                    @Override
                    public void onNext(GetCommunicationFeeModel communicationFeeModel) {
                        if (communicationFeeModel.success) {
                            filterBodysMenu = new FilterBodysMenu(NewMeetNiceActivity.this);
                            List<FilterItem> menuItems = new ArrayList<>();
                            for (int i = 0; i < 4; i++) {
                                if (i == 0) {
                                    FilterItem filterItem = new FilterItem();
                                    filterItem.filterName = "女生";
                                    filterItem.genderType = "2";
                                    filterItem.price = communicationFeeModel.data.list.girl;
                                    if (isFirstShow) {
                                        filterGender = "2";
                                        mChatPrice = communicationFeeModel.data.list.girl;
                                    }
                                    menuItems.add(filterItem);
                                } else if (i == 1) {
                                    FilterItem filterItem = new FilterItem();
                                    filterItem.filterName = "男生";
                                    filterItem.genderType = "1";
                                    filterItem.price = communicationFeeModel.data.list.boy;
                                    menuItems.add(filterItem);
                                }
                            }

                            filterBodysMenu
                                    .setHeight(DensityUtils.dp2px(NewMeetNiceActivity.this, 251))     //默认高度480
                                    .showIcon(true)     //显示菜单图标，默认为true
                                    .dimBackground(true)           //背景变暗，默认为true
                                    .needAnimationStyle(true)   //显示动画，默认为true
                                    .setAnimationStyle(R.style.TRM_ANIM_STYLE)  //默认为R.style.TRM_ANIM_STYLE
                                    .addMenuList(menuItems)
                                    .setOnMenuItemClickListener(new FilterBodysMenu.OnMenuItemClickListener() {
                                        @Override
                                        public void onMenuItemClick(int position, String gender, double price) {
                                            filterSelectPosition = position;
                                            filterGender = gender;
                                            mChatPrice = price;
                                            if(filterGender.equals("2")){
                                                tvFilterConditions.setText("筛选条件（女生）");
                                            }else if(filterGender.equals("1")){
                                                tvFilterConditions.setText("筛选条件（男生）");
                                            }else {
                                                tvFilterConditions.setText("筛选条件（女生）");
                                            }
                                            filterBodysMenu.dismiss();

                                        }
                                    })
                                    .showAsDropDown(tvFilterConditions, -115, 0);

                            filterBodysMenu.mAdapter.setSelectPosition(filterSelectPosition);

                        } else {
                            Toast.makeText(cnt, "获取列表失败", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
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
                }
            });


        }
    };


}
