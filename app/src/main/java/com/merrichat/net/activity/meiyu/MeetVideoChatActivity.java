package com.merrichat.net.activity.meiyu;

import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.RendererCommon;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.emitter.Emitter;

/**
 * Created by amssy on 17/12/20.
 * 美遇聊天页面
 */

public class MeetVideoChatActivity extends BaseActivity implements WebRtcClient.RtcListener {
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    //对面视屏设置
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;

    //本地视屏设置
    private static final int LOCAL_X_CONNECTING = 6;
    private static final int LOCAL_Y_CONNECTING = 60;
    private static final int LOCAL_WIDTH_CONNECTING = 30;
    private static final int LOCAL_HEIGHT_CONNECTING = 30;


    @BindView(R.id.glview_call)
    GLSurfaceView videoView;

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
     * 关闭美遇
     */
    @BindView(R.id.iv_close_meet)
    ImageView ivCloseMeet;


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


    /**
     * 挂断flag
     * false为 自己退出、挂断等
     * true 为切换 、对方挂断等、 自己需要重新搜索
     */
    private boolean handgUpFlag = false;

    private  JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_video_chat);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mSocketAddress = "http://" + MerriApp.socketIp;
        mSocketAddress += (":" + MerriApp.socketPort + "/");
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
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, true);

        //本地流
        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);


        //监听---美遇切换
        MerriUtils.getApp().socket.on("change", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                handgUpFlag = true;
                finish();
            }
        });

        //监听---美遇对方挂断
        MerriUtils.getApp().socket.on("beautifulHangUp", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                handgUpFlag = true;
                finish();
            }
        });
    }

    private void initView() {
        String flag = getIntent().getStringExtra("flag");
        if ("answer".equals(flag)) {
            toMemberId = getIntent().getStringExtra("toMemberId");
        } else if ("call".equals(flag)) {
            toMemberId = getIntent().getStringExtra("toMemberId");
        }
        Log.e("@@@", "toMemberId======" + toMemberId);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(
                true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);
        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), toMemberId);
        client.setVideoChatConnection();
        if ("call".equals(flag)) {
            startCam();
            Log.e("@@@", "--------呼叫方call-----  call---");
        } else if ("answer".equals(flag)) {
            String toSocketId = getIntent().getStringExtra("toSocketId");
            try {
                answer(toSocketId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("@@@", "--------应答方------answer-------");
        }


        jsonObject = new JSONObject();
        try {
            jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
            jsonObject.put("toMemberId", toMemberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        startCam();
    }

    public void startCam() {
        client.setCamera();
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GetToast.useString(cnt, newStatus);
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
//        可以在我们的渲染器添加到VideoTrack
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
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING,
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

    @OnClick({R.id.iv_close_meet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_meet://关闭美遇
                handgUpFlag = false;
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        handgUpFlag = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            client.onDestroy();
        }
        closeVideoChat();
        super.onDestroy();
    }


    /**
     * 挂断美遇
     */
    private void closeVideoChat() {
        if (handgUpFlag) {//切换
            MerriUtils.getApp().socket.emit("change", jsonObject.toString());
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_NICE_MEET_TWO = true;
            EventBus.getDefault().post(eventBusModel);
            Log.e("@@@", "美遇视频聊天切换.....");
        } else {//挂断
            MerriUtils.getApp().socket.emit("beautifulHangUp", jsonObject.toString());
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_NICE_MEET_ONE = true;
            EventBus.getDefault().post(eventBusModel);
            Log.e("@@@", "美遇视频聊天关闭.....");
        }
    }
}
