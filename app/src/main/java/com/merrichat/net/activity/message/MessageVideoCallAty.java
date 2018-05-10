package com.merrichat.net.activity.message;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.meiyu.VideoChatActivity;
import com.merrichat.net.activity.meiyu.WebRtcClient;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.HangUpModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.CameraUtil;
import com.merrichat.net.utils.DateTimeUtil;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.view.AspectFrameLayout;
import com.merrichat.net.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;

/**
 * Created by amssy on 17/12/8.
 * 消息---视频聊天----呼叫and接听页面
 * implements  SurfaceHolder.Callback目的是获取本地流
 */

public class MessageVideoCallAty extends BaseActivity implements SurfaceHolder.Callback, WebRtcClient.RtcListener {


    public static final String CALL_VIDEO_CALLER_TYPE = "isCaller";
    public static final String CALL_VIDEO_ANSWER_TYPE = "isAnswer";
    public static final String CALL_VIDEO_TYPE_KEY = "callVideoType";
    public static final String CALL_VIDEO_INFO_KEY = "callerInfo";
    public static final int CALL_CONNECTION = 0x100;
    public static final int CALL_DISCONNECTION = 0x101;


    @BindView(R.id.my_surfaceView)
    SurfaceView mySurfaceView;

    //视频聊天
    @BindView(R.id.iv_video_img)
    CircleImageView ivVideoImg;
    @BindView(R.id.tv_video_name)
    TextView tvVideoName;
    @BindView(R.id.tv_video_conneting)
    TextView tvVideoConneting;
    @BindView(R.id.lay_video_top_close)
    LinearLayout layVideoTopClose;

    @BindView(R.id.iv_video_answer_hangup)
    ImageView ivVideoAnswerHangup;
    @BindView(R.id.iv_video_answer_qiehuan_voice)
    ImageView ivVideoAnswerQiehuanVoice;
    @BindView(R.id.iv_video_answer_answer)
    ImageView ivVideoAnswerAnswer;

    @BindView(R.id.connection_time)
    TextView mConnectionTime;   //呼叫倒计时

    private String video_call_type;//CALL_VIDEO_CALLER呼叫者，CALL_VIDEO_ANSWER应答者
    private String toMemberId;
    private String toMemberName;
    private String toHeadImgUrl;

    /**
     * 相机
     */
    private Camera mCamera;


    /**
     * 1 ：前置摄像头
     * 0： 后置摄像头
     * 默认前置
     */
    private int cameraPosition = 1;


    private SurfaceHolder holder;
    private Gson gson = new Gson();
    private Thread threadStarAnim;  //线程小星星
    private MediaPlayer mMediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEnterConnectionState();

        //加载xml布局
        setContentView(R.layout.activity_message_video_call);
        ButterKnife.bind(this);
        CameraUtil.init(this);
        AppManager.getAppManager().addActivity(this);
        //获取上一界面传来的是呼叫还是应答的类型
        video_call_type = getIntent().getStringExtra(CALL_VIDEO_TYPE_KEY);
        initView();
        initData();

        threadStarAnim = new Thread(new ThreadShow());  //小星星线程
        threadStarAnim.start();

        if (PrefAppStore.getAboutPhoneSetting(cnt) == 0) {  //声音提示
            mMediaPlayer = MediaPlayer.create(MessageVideoCallAty.this, R.raw.call_running);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } else if (PrefAppStore.getAboutPhoneSetting(cnt) == 1) {  //振动提示
            vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

            // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
            long[] pattern = {1000, 1000, 1000, 1000};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern, 0);

        } else if (PrefAppStore.getAboutPhoneSetting(cnt) == 2) {    //声音加振动提示
            mMediaPlayer = MediaPlayer.create(MessageVideoCallAty.this, R.raw.call_running);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
            vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
            // 下边是可以使震动有规律的震动 -1：表示不重复 0：循环的震动
            long[] pattern = {1000, 1000, 1000, 1000};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern, 0);

        }
    }

    private void initData() {
        holder = mySurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this); // 回调接口
    }


    private void initView() {
        //设置顶部头像昵称，获取远程用户信息
        if (video_call_type.equals(CALL_VIDEO_ANSWER_TYPE)) {
            tvVideoConneting.setText("邀请你进行视频聊天");
            String callVideoInfo = getIntent().getStringExtra(CALL_VIDEO_INFO_KEY);
            try {
                JSONObject jsonObject = new JSONObject(callVideoInfo);
                toMemberId = jsonObject.optString("fromMemberId");
                toMemberName = jsonObject.optString("fromMemberName");
                toHeadImgUrl = jsonObject.optString("fromHeadImgUrl");
                Glide.with(this).load(toHeadImgUrl).into(ivVideoImg);
                tvVideoName.setText(toMemberName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(cameraPosition);
            if (holder != null) {
                startPreview(mCamera, holder);
            }
        }
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
        aspectFrameLayout.setAspectRatio(1.0f * size.height / size.width);

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
        handler.removeCallbacks(threadStarAnim);
        releaseCamera();

    }

    @OnClick({R.id.iv_video_answer_hangup, R.id.iv_video_answer_answer, R.id.lay_video_top_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {  //右上角的拒绝按钮
            case R.id.lay_video_top_close:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("isAgree", "0");//1-同意  0--拒绝
                    jsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject.put("toMemberId", toMemberId);
                    jsonObject.put("callSource", "0");
                    jsonObject.put("callType", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MerriApp.socket.emit("hangUp", jsonObject.toString());
                Logger.e("@@@", "发射挂断事件......... jsonObject");
                finish();

                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                break;

            case R.id.iv_video_answer_hangup:   //挂断拒绝按钮
                JSONObject jsonObject2 = new JSONObject();
                try {
                    jsonObject2.put("isAgree", "0");//1-同意  0--拒绝
                    jsonObject2.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject2.put("toMemberId", toMemberId);
                    jsonObject2.put("callSource", "0");
                    jsonObject2.put("callType", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MerriApp.socket.emit("hangUp", jsonObject2.toString());
                Logger.e("@@@", "发射挂断事件......... jsonObject2");
                finish();
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                break;
            case R.id.iv_video_answer_answer:
                GetToast.useString(cnt, "接听。。。。");
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                if (vibrator!= null && vibrator.hasVibrator()){
                    vibrator.cancel();
                }

                JSONObject jsonObject3 = new JSONObject();
                try {
                    jsonObject3.put("isAgree", "1");//1-同意  0--拒绝
                    jsonObject3.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject3.put("toMemberId", toMemberId);
                    jsonObject3.put("fromHeadImgUrl", UserModel.getUserModel().getImgUrl());
                    jsonObject3.put("fromMemberName", UserModel.getUserModel().getRealname());
                    jsonObject3.put("callSource", "0");
                    jsonObject3.put("callType", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MerriApp.socket.emit("agreeCall", jsonObject3.toString());
                startActivityForResult(new Intent(cnt, VideoChatActivity.class)
                        .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_ANSWER_TYPE)
                        .putExtra("toMemberId", toMemberId)
                        .putExtra("callType", "1"), CALL_CONNECTION);

                break;
        }
    }


    /**
     * 画布创建
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }


    /**
     * 画布改变
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }


    /**
     * 画布销毁的时候：   释放相机资源
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }


    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            CameraUtil.getInstance().setCameraDisplayOrientation(this, cameraPosition, camera);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 设置 相机参数
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported 自动对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        Camera.Size previewSize = CameraUtil.findBestPreviewResolution(camera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 1000);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);
    }

    /**
     * 获取Camera实例
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getConnectionState();
    }

    public void getConnectionState() {
        getEnterConnectionState();
        MessageVideoCallAty.this.finish();

    }

    public void getEnterConnectionState() {
        try {
            if (MerriApp.socket != null) {
                MerriApp.socket.disconnect();
            }
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(MessageVideoCallAty.this), VideoChatModel.class);
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


    @Override
    public void onStatusChanged(final String newStatus) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GetToast.useString(cnt, newStatus);
                if (newStatus.equals("DISCONNECTED")) {
                    GetToast.useString(cnt, "音频聊天结束");
                    finish();
                } else if (newStatus.equals("CLOSED")) {
                    GetToast.useString(cnt, "音频聊天结束");
                    finish();
                }
            }
        });

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
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            hangUp();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hangUp() {  //挂断事件
        HangUpModel hangUpModel = new HangUpModel();
        hangUpModel.isAgree = "0";
        hangUpModel.fromMemberId = UserModel.getUserModel().getMemberId();
        hangUpModel.toMemberId = toMemberId;
        hangUpModel.callSource = "0";
        hangUpModel.callType = "1";
        MerriApp.socket.emit("hangUp", gson.toJson(hangUpModel, HangUpModel.class));
        Logger.e("@@@", "发射挂断事件......... agreeCall" + gson.toJson(hangUpModel, HangUpModel.class));
    }


    private long timeConnection = 57600_000;  //连接开始时间


    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mConnectionTime.setText(DateTimeUtil.formatDaojiShi(new Date(timeConnection)));
        }
    };


    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    timeConnection = timeConnection + 1000;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
