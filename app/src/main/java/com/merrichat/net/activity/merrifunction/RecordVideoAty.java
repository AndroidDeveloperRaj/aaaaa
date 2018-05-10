package com.merrichat.net.activity.merrifunction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.dyhdyh.compat.mmrc.MediaMetadataRetrieverCompat;
import com.faceunity.wrapper.faceunity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.complete.VideoReleaseActivity;
import com.merrichat.net.activity.video.editor.VideoMusicActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.encoder.TextureMovieEncoder;
import com.merrichat.net.gles.CameraClipFrameRect;
import com.merrichat.net.gles.FullFrameRect;
import com.merrichat.net.gles.LandmarksPoints;
import com.merrichat.net.gles.Texture2dProgram;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.CameraUtils;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.ImageUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.authpack;
import com.merrichat.net.view.AspectFrameLayout;
import com.merrichat.net.view.CameraSettingPopuWindow;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

import static com.merrichat.net.encoder.TextureMovieEncoder.IN_RECORDING;
import static com.merrichat.net.encoder.TextureMovieEncoder.START_RECORDING;

/**
 * Created by amssy on 17/11/13.
 */

public class RecordVideoAty extends IRecordVideoUIAty implements Camera.PreviewCallback, SurfaceTexture.OnFrameAvailableListener {

    final static String TAG = "RecordVideoAty";
    public static float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    public static int activityId = MiscUtil.getActivityId();
    private Context mContext;
    private Camera mCamera;

    private GLSurfaceView glSf;
    private GLRenderer glRenderer;

    private byte[][] previewCallbackBuffer;
    private final int PREVIEW_BUFFER_COUNT = 3;


    private int cameraWidth = 1280;
    private int cameraHeight = 720;

    private Handler mMainHandler;

    private HandlerThread mCreateItemThread;
    private Handler mCreateItemHandler;
    private int currentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean boostBestCameraFPS = false;
    private int cameraDataAlreadyCount = 0;
    private boolean isInPause = false;
    private static String mEffectFileName = "none";
    private String mFilterName = "none";
    private static int mFaceBeautyItem = 0; //美颜道具
    private static int mEffectItem = 0; //贴纸道具
    private static int[] itemsArray = {mFaceBeautyItem, mEffectItem};

    private TextureMovieEncoder mTextureMovieEncoder;
    private boolean mUseBeauty = true;
    private boolean isNeedSwitchCameraSurfaceTexture = true;

    private long resumeTimeStamp;
    private boolean isFirstOnFrameAvailable;
    private long frameAvailableTimeStamp;

    private long lastOneHundredFrameTimeStamp = 0;
    private int currentFrameCnt = 0;
    private long oneHundredFrameFUTime = 0;
    private boolean isNeedEffectItem = true;
    private String videoFileName;
    private final Object prepareCameraDataLock = new Object();
    private boolean isBenchmarkFPS = true;
    private boolean isBenchmarkTime = false;
    private boolean isInAvatarMode = false;
    private byte[] mCameraNV21Byte;
    private byte[] fuImgNV21Bytes;
    private int mFrameId = 0;

    float mFaceBeautyColorLevel = 0.8f;
    float mFaceBeautyBlurLevel = 6.0f;
    float mFaceBeautyCheekThin = 0.8f;
    float mFaceBeautyEnlargeEye = 0.8f;
    private static boolean mIsOpen = true;

    private List<String> videoList;//视频段

    //视频速度
    private float videoSpeed = 1.0f;


    private boolean isHasMusic;//是否选的有音乐
    private String songPath;//音乐路径
    private MediaPlayer mPlayer;//音乐播放器


    private int mergeVideoIndex;//视频在列表的索引

    String outPath;
    private ProgressDialog mergeDialog;//合并dialog

    private final int REQUEST_CODE_MUSIC = 0x0013;

    boolean isFirstCameraOnDrawFrame;

    private boolean isChangeVideo = true;//视频合成之后是否改变

    private Bitmap bitmap = null;
    private String coverPath = "";//视频封面
    @SuppressLint("HandlerLeak")
    private Handler mergeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://速率为1.0的情况
//                    mergeVideoIndex++;
//                    if (mergeVideoIndex < videoList.size()) {
//                        changePTS();
//                    } else {
//                        mergeVideo();
//                    }
                    boolean isMusicFile = RxFileTool.isFileExists(songPath);
                    //是否已选音乐和文件是否存在
                    if (isHasMusic && isMusicFile) {
                        Logger.e("PTSPath:" + PTSPath);
                        //合成音乐
                        mergeMusicVideo();
//                        finishPath = PTSPath;
//
//                        //取封面图是耗时操作  需放到子线程中
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                bitmap = decodeThumbBitmapForFile(videoList.get(0));
//                                if (bitmap == null) {
//                                    bitmap = decodeThumbBitmapForFile1(videoList.get(0));
//                                    if (bitmap == null) {
//                                        Message message = new Message();
//                                        message.what = 4;
//                                        message.obj = coverPath;
//                                        mergeHandler.sendMessage(message);
//                                    } else {
//                                        ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
//                                            @Override
//                                            public void onCompleted(String path) {
//                                                if (bitmap != null) {
//                                                    bitmap.recycle();
//                                                }
//                                                coverPath = path;
//                                                Message message = new Message();
//                                                message.what = 4;
//                                                message.obj = path;
//                                                mergeHandler.sendMessage(message);
//                                            }
//                                        });
//                                    }
//                                } else {
//                                    ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
//                                        @Override
//                                        public void onCompleted(String path) {
//                                            if (bitmap != null) {
//                                                bitmap.recycle();
//                                            }
//                                            coverPath = path;
//                                            Message message = new Message();
//                                            message.what = 4;
//                                            message.obj = path;
//                                            mergeHandler.sendMessage(message);
//                                        }
//                                    });
//                                }
//                            }
//                        }).start();

                    } else {//未选音乐  直接开拍
                        Intent intent = new Intent(RecordVideoAty.this, VideoMusicActivity.class);
                        intent.putExtra("activityId", RecordVideoAty.activityId);
                        startActivityForResult(intent, REQUEST_CODE_MUSIC);
                    }

                    break;
                case 2://一段视频变速
                    changePTS1();
//                    if (isHasMusic) {
//                        mergeMusicVideo();
//                    } else {
//
//                        bitmap = StringUtil.decodeThumbBitmapForFile(PTSPath,cameraWidth , cameraHeight);
//                    if (bitmap == null) {
//                        Message message = new Message();
//                        message.what = 1;
//                        mergeHandler.sendMessage(message);
//                        return;
//                    }
//                        ImageUtil.saveBitmapToFile(mContext, bitmap, bitmap.getWidth(), bitmap.getHeight(), new ImageUtil.SavePhotoCompletedCallBack() {
//                            @Override
//                            public void onCompleted(String path) {
//                    if (bitmap != null){
//                        bitmap.recycle();
//                    }
//                    coverPath = path;
//                                Message message = new Message();
//                                message.what = 4;
//                                message.obj = path;
//                                mergeHandler.sendMessage(message);
//                            }
//                        });
//                    }

                    break;
                case 3:
                    //取封面图是耗时操作  需放到子线程中
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                bitmap = decodeThumbBitmapForFile(videoList.get(0));
                                if (bitmap == null) {
                                    bitmap = decodeThumbBitmapForFile1(videoList.get(0));
                                    if (bitmap == null) {
                                        Message message = new Message();
                                        message.what = 4;
                                        message.obj = coverPath;
                                        mergeHandler.sendMessage(message);
                                    } else {
                                        ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
                                            @Override
                                            public void onCompleted(String path) {
                                                if (bitmap != null) {
                                                    bitmap.recycle();
                                                }
                                                coverPath = path;
                                                Message message = new Message();
                                                message.what = 4;
                                                message.obj = path;
                                                mergeHandler.sendMessage(message);
                                            }
                                        });
                                    }
                                } else {
                                    ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
                                        @Override
                                        public void onCompleted(String path) {
                                            if (bitmap != null) {
                                                bitmap.recycle();
                                            }
                                            coverPath = path;
                                            Message message = new Message();
                                            message.what = 4;
                                            message.obj = path;
                                            mergeHandler.sendMessage(message);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case 4:
                    if (mergeDialog != null) {
                        mergeDialog.dismiss();
                    }
                    isChangeVideo = false;
                    Intent intent = new Intent(RecordVideoAty.this, VideoReleaseActivity.class);
                    intent.putExtra("cover", (String) msg.obj);
                    if (isCheckMusic || isHasMusic) {
                        intent.putExtra("videoPath", finishPath);
                    } else {
                        intent.putExtra("videoPath", PTSPath);
                    }
                    intent.putExtra("activityId", activityId);
                    startActivity(intent);
                    break;
                case 5:
                    if (mergeDialog != null && mergeDialog.isShowing()) {
                        Double progress = Double.valueOf(String.valueOf(msg.obj));
                        DecimalFormat df = new DecimalFormat("0.00%");
                        if (progress < 1 && msg.arg1 != 101) {
                            mergeDialog.setMessage("合成中..." + df.format(progress));
                        } else if (progress == 1) {
                            if (msg.arg1 != 100) {
                                mergeDialog.setMessage("合成中..." + df.format(1f));
                            } else {
                                mergeDialog.setMessage("合成中..." + df.format(0.99f));
                            }
                        }
                    }
                    break;
                case 6:
                    composeVideoErrorNum = composeVideoErrorNum++;
                    if (composeVideoErrorNum < 3) {
                        //视频合成出错 重新合成视频
//                        if (mergeDialog != null) {
//                            mergeDialog.dismiss();
//                        }
                        //RxToast.showToast("视频合成出错请重试");
                        composeVideoError = msg.arg1;
                        switch (composeVideoError) {
                            case 1://单个视频变速失败
                                //changePTS1();
                                break;
                            case 2://速率为1.0合成音乐失败
                                //mergeMusicVideo();
                                break;
                            case 3://分段同速率合并失败
                                mergeVideo1();
                                break;
                        }
                    } else {
                        if (mergeDialog != null) {
                            mergeDialog.dismiss();
                        }
                        RxToast.showToast("视频合成出错请重试");
                    }
                    break;
                case 7:
                    if (bitmap == null) {
                        if (mergeDialog != null) {
                            mergeDialog.dismiss();
                        }
                        RxToast.showToast("获取封面图失败");
                        return;
                    }
                    break;
            }
        }
    };

    //视频合成出错编码 1： 单个视频变速失败 2： 速率为1.0合成音乐失败  3： 分段同速率合并失败
    private int composeVideoError = 0;
    //失败次数 超过3次停止继续
    private int composeVideoErrorNum = 0;
    private boolean isReslMusic = false;//是否重置音乐
    private boolean isCheckMusic = false;//直接开拍是否选择音乐
    private MediaMetadataRetrieverCompat mmrc;
    private CommomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mContext = this;
        mMainHandler = new MainHandler(this);
        glSf = (GLSurfaceView) findViewById(R.id.glsv);
        glSf.setEGLContextClientVersion(2);
        glRenderer = new GLRenderer();
        glSf.setRenderer(glRenderer);
        glSf.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper(), mContext);
        videoList = new ArrayList<>();
        //音乐路径必须加请求头"file://" Epmedia合成音乐 不写会报一个 Header Missing
        songPath = getIntent().getStringExtra("VideoMusic");//"/storage/emulated/0/ez.mp3";
        isHasMusic = getIntent().getBooleanExtra("isHasMusic", false);//是否选音乐

        mergeDialog = new ProgressDialog(this);
        mergeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mergeDialog.setCanceledOnTouchOutside(false);
        mergeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mergeDialog.setMessage("合成中...");

        if (isHasMusic) {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(songPath);
                mPlayer.setLooping(true);
                mPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        EventBus.getDefault().register(this);
        //查询魔拍是否是HOT状态
        queryIsHotStatus(UserModel.getUserModel().getMemberId());
    }

    @Override
    protected void onResume() {
        Logger.d(TAG, "onResume");
        resumeTimeStamp = System.nanoTime();
        isFirstOnFrameAvailable = true;
        isInPause = false;
        super.onResume();
        openCamera(currentCameraType, cameraWidth, cameraHeight);

        /**
         * 请注意这个地方, camera返回的图像并不一定是设置的大小（因为可能并不支持）
         */
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        cameraWidth = size.width;
        cameraHeight = size.height;
        Logger.d(TAG, "open camera size width : " + size.width + " height : " + size.height);

        AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
        aspectFrameLayout.setAspectRatio(1.0f * cameraHeight / cameraWidth);
        glSf.onResume();
    }


    @Override
    protected void onPause() {
        Logger.d(TAG, "onPause");

        isInPause = true;

        super.onPause();

        mCreateItemHandler.removeMessages(RecordVideoAty.CreateItemHandler.HANDLE_CREATE_ITEM);

        releaseCamera();

        glSf.queueEvent(new Runnable() {
            @Override
            public void run() {
                glRenderer.notifyPause();
                glRenderer.destroySurfaceTexture();

                itemsArray[1] = mEffectItem = 0;
                itemsArray[0] = mFaceBeautyItem = 0;
                //Note: 切忌使用一个已经destroy的item
                faceunity.fuDestroyAllItems();
                //faceunity.fuDestroyItem(mEffectItem);
                //faceunity.fuDestroyItem(mFaceBeautyItem);
                isNeedEffectItem = true;
                faceunity.fuOnDeviceLost();
                // faceunity.fuClearReadbackRelated();
                mFrameId = 0;
            }
        });

        glSf.onPause();

        lastOneHundredFrameTimeStamp = 0;
        oneHundredFrameFUTime = 0;
        mergeVideoIndex = 0;
        if (tempList != null) {
            tempList.clear();
        }
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (isFirstOnFrameAvailable) {
            frameAvailableTimeStamp = System.nanoTime();
            isFirstOnFrameAvailable = false;
            Logger.d(TAG, "first frame available time cost " +
                    (frameAvailableTimeStamp - resumeTimeStamp) / NANO_IN_ONE_MILLI_SECOND);
        }
        Logger.d(TAG, "onFrameAvailable");
        synchronized (prepareCameraDataLock) {
            cameraDataAlreadyCount++;
            prepareCameraDataLock.notify();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Logger.d(TAG, "onPreviewFrame len " + data.length);
        Logger.d(TAG, "onPreviewThread " + Thread.currentThread());
        mCameraNV21Byte = isInPause ? null : data;
        mCamera.addCallbackBuffer(data);
        synchronized (prepareCameraDataLock) {
            cameraDataAlreadyCount++;
            prepareCameraDataLock.notify();
        }
    }

    @Override
    protected void onEffectItemSelected(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        isInAvatarMode = effectItemName.equals("lixiaolong.bundle");
        mCreateItemHandler.removeMessages(RecordVideoAty.CreateItemHandler.HANDLE_CREATE_ITEM);
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
    }

    @Override
    protected void onFilterSelected(String filterName) {
        mFilterName = filterName;
    }

    @Override
    protected void onBlurLevelSelected(int level) {
        switch (level) {
            case 0:
                mFaceBeautyBlurLevel = 0;
                break;
            case 1:
                mFaceBeautyBlurLevel = 1.0f;
                break;
            case 2:
                mFaceBeautyBlurLevel = 2.0f;
                break;
            case 3:
                mFaceBeautyBlurLevel = 3.0f;
                break;
            case 4:
                mFaceBeautyBlurLevel = 4.0f;
                break;
            case 5:
                mFaceBeautyBlurLevel = 5.0f;
                break;
            case 6:
                mFaceBeautyBlurLevel = 6.0f;
                break;
        }
    }

    @Override
    protected void onColorLevelSelected(int progress, int max) {
        mFaceBeautyColorLevel = 1.0f * progress / max;
    }

    @Override
    protected void onCheekThinSelected(int progress, int max) {
        mFaceBeautyCheekThin = 1.0f * progress / max;
    }

    @Override
    protected void onEnlargeEyeSelected(int progress, int max) {
        mFaceBeautyEnlargeEye = 1.0f * progress / max;
    }

    @Override
    protected void onCameraChange() {
        Logger.d(TAG, "onCameraChange");
        synchronized (prepareCameraDataLock) {

            isNeedSwitchCameraSurfaceTexture = true;

            cameraDataAlreadyCount = 0;

            releaseCamera();

            mCameraNV21Byte = null;
            mFrameId = 0;
            if (currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, cameraWidth, cameraHeight);
            } else {
                openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraWidth, cameraHeight);
            }
        }
    }

    @Override
    protected void onStartRecording() {
        Logger.e("onStartRecording" + isChangeVideo);
        if (isChangeVideo) {
            if (isHasMusic) {
                if (mPlayer != null) {
                    if (isReslMusic) {
                        try {
                            mPlayer = new MediaPlayer();
                            mPlayer.setDataSource(songPath);
                            mPlayer.setLooping(true);
                            mPlayer.prepare();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(songPath);
                        mPlayer.setLooping(true);
                        mPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mPlayer.start();
                isReslMusic = false;
            }
            mTextureMovieEncoder = new TextureMovieEncoder();
        } else {
            Message message = new Message();
            message.what = 4;
            message.obj = coverPath;
            mergeHandler.sendMessage(message);
        }
    }

    @Override
    protected void onStopRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
            if (isHasMusic && mPlayer != null) {
                mPlayer.pause();
            }
            mTextureMovieEncoder.stopRecording();
            lastOneHundredFrameTimeStamp = 0;
            oneHundredFrameFUTime = 0;
            mergeVideoIndex = 0;
        }
        isFirstCameraOnDrawFrame = true;
    }

    /**
     * 拍摄成功自动合成
     */
    @Override
    protected void onRecordComplete() {
        Logger.e("onRecordComplete" + isChangeVideo);
        if (isChangeVideo) {
            if (isHasMusic && mPlayer != null) {
                mPlayer.stop();
            }
            if (videoList.size() > 0) {
                if (mergeDialog == null) {
                    mergeDialog = new ProgressDialog(this);
                    mergeDialog.show();
                    mergeDialog.setMessage("合成中...");
                } else {
                    mergeDialog.show();
                    mergeDialog.setMessage("合成中...");
                }
                mergeVideo1();
            }
        } else {
            Message message = new Message();
            message.what = 4;
            message.obj = coverPath;
            mergeHandler.sendMessage(message);
        }
    }

    @Override
    protected void onFinshThisScreen() {
//        if (isTaking) {
//            return;
//        }
        if (isHasRecorded) {
            showExitScreenDialog(mContext);
        } else {
            finish();
        }
    }

    @Override
    protected void onCameraSetting(View clickView) {
        CameraSettingPopuWindow cameraSettingPopuWindow = new CameraSettingPopuWindow((Activity) mContext, is_timer_down_open, is_flash_light_open, currentCameraType, false, false, clickView);
        cameraSettingPopuWindow.showPopupWindow(ivCameraSetting);
        cameraSettingPopuWindow.setOnPopuCameraSettingClick(new CameraSettingPopuWindow.OnPopuCameraSettingClick() {
            @Override
            public void onFlashLight(boolean isOpen) {
                switchFlash();
            }

            @Override
            public void onTimerDownSelet(boolean isOpen) {
                is_timer_down_open = isOpen;
            }

            @Override
            public void onZidong(boolean isOpen) {
            }

            @Override
            public void onDismiss() {
                onBackControlUI(false);
            }
        });
    }

    /**
     * 手动合成
     */
    @Override
    protected void onCompleted() {
        Logger.e("onCompleted" + isChangeVideo);
        if (isChangeVideo) {
            if (videoList.size() > 0) {
                if (mergeDialog == null) {
                    mergeDialog = new ProgressDialog(this);
                    mergeDialog.show();
                    mergeDialog.setMessage("合成中...");
                } else {
                    mergeDialog.show();
                    mergeDialog.setMessage("合成中...");
                }
                mergeVideo1();
            }
        } else {
            Message message = new Message();
            message.what = 4;
            message.obj = coverPath;
            mergeHandler.sendMessage(message);
        }
    }

    String finishPath;

    /**
     * 合成音乐
     */
    private void mergeMusicVideo() {
        //视频路径
        Logger.e(PTSPath);
        //音乐路径
        Logger.e(songPath);
        //输出路径
        finishPath = FileUtils.createVideoEditFileName("finish");
        Logger.e(finishPath);

        //无声视频
        final String demuxerVideo = FileUtils.createVideoEditFileName("demuxerVideo");
        //音频
//        final String demuxerAudio = FileUtils.createVideoMusicFileName("demuxerAudio");
//        //音频加音乐
//        final String composeAudio = FileUtils.createVideoMusicFileName("composeAudio");
//
//        //参数分别是视频路径，输出路径，输出类（去掉音频 取出视频  然后合成音乐）
//        EpEditor.demuxer(PTSPath, demuxerVideo, EpEditor.Format.MP4, new OnEditorListener() {
//            @Override
//            public void onSuccess() {
//                Logger.e("分离视频成功－－－ onSuccess()");
//                //参数分别是视频路径，输出路径，输出类（去掉音频 取出视频  然后合成音乐）
//                EpEditor.demuxer(PTSPath, demuxerAudio, EpEditor.Format.MP3, new OnEditorListener() {
//                    @Override
//                    public void onSuccess() {
//                        handler.post(runnable);
//                        Logger.e("分离音频成功－－－ onSuccess()");
//                        Logger.e("合成音频命令：" + String.valueOf(FFmpegCommands.composeAudio(demuxerAudio, songPath, composeAudio)));
//                        EpEditor.execCmd(String.valueOf(FFmpegCommands.composeAudio(demuxerAudio, songPath, composeAudio)), 0, new OnEditorListener() {
//                            @Override
//                            public void onSuccess() {
//                                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//                                mmr.setDataSource(PTSPath);
//                                long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//                                Logger.e("合成音频成功－－－ onSuccess()");
//                                Logger.e("合成视频命令：" + String.valueOf(FFmpegCommands.composeVideo(demuxerVideo, composeAudio, finishPath, getDateToString(duration))));
//                                EpEditor.execCmd(String.valueOf(FFmpegCommands.composeVideo(demuxerVideo, composeAudio, finishPath, getDateToString(duration))), 0, new OnEditorListener() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Logger.e("合成视频成功－－－ onSuccess()" + finishPath);
//                                        Message message = new Message();
//                                        message.what = 3;
//                                        mergeHandler.sendMessage(message);
//                                    }
//
//                                    @Override
//                                    public void onFailure() {
//                                        Logger.e("合成视频失败－－－ onFailure");
//                                    }
//
//                                    @Override
//                                    public void onProgress(float progress) {
//                                        Logger.e("合成视频" + progress);
//                                        Message message = new Message();
//                                        message.what = 5;
//                                        message.obj = 1;
//                                        mergeHandler.sendMessage(message);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                Logger.e("合成音频失败－－－ onFailure");
//                            }
//
//                            @Override
//                            public void onProgress(float progress) {
//                                Logger.e("合成音频" + progress);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        Logger.e("demuxerAudio-onFailure");
//                        //合成失败  重新合成
//                        mergeMusicVideo();
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//                        //这里获取处理进度
//                        Logger.e("EpEditor.demuxer" + progress);
//                        Message message = new Message();
//                        message.what = 5;
//                        message.obj = progress / 2;
//                        mergeHandler.sendMessage(message);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure() {
//                Logger.e("demuxerVideo-onFailure");
//                //合成失败  重新合成
//                mergeMusicVideo();
//            }
//
//            @Override
//            public void onProgress(float progress) {
//                //这里获取处理进度
//                Logger.e("EpEditor.demuxer" + progress);
//            }
//        });


        //参数分别是视频路径，输出路径，输出类（去掉音频 取出视频  然后合成音乐）
        EpEditor.demuxer(PTSPath, demuxerVideo, EpEditor.Format.MP4, new OnEditorListener() {
            @Override
            public void onSuccess() {
                //参数分别是视频路径，音频路径，输出路径,原始视频音量(1为100%,0.7为70%,以此类推),添加音频音量
                try {
                    EpEditor.music(demuxerVideo, songPath, finishPath, 0f, 1.0f, new OnEditorListener() {
                        @Override
                        public void onSuccess() {
                            Logger.e("合成音乐成功－－－onSuccess()");
                            Message message = new Message();
                            message.what = 3;
                            mergeHandler.sendMessage(message);
                        }

                        @Override
                        public void onFailure() {
                            Logger.e("mergeMusicVideo-onFailure");
//                            Message message = new Message();
//                            message.what = 6;
//                            message.arg1 = 2;
//                            mergeHandler.sendMessage(message);
                            finishPath = PTSPath;
                            Message message = new Message();
                            message.what = 3;
                            mergeHandler.sendMessage(message);
                        }

                        @Override
                        public void onProgress(float progress) {
                            //这里获取处理进度
                            Logger.e("视频音乐合并中——————" + progress);
                            Message message = new Message();
                            message.what = 5;
                            message.arg1 = 101;//不等于100 就行
                            message.obj = progress;
                            mergeHandler.sendMessage(message);
                        }
                    });

                } catch (Exception e) {
                    Logger.e("视频合成音乐出错,重新启动合成——————" + e);
                    //合成失败  重新合成
                    if (videoList.size() > 0) {
                        if (mergeDialog == null) {
                            mergeDialog = new ProgressDialog(RecordVideoAty.this);
                            mergeDialog.show();
                            mergeDialog.setMessage("合成中...");
                        } else {
                            mergeDialog.show();
                            mergeDialog.setMessage("合成中...");
                        }
                        mergeVideo1();
                    } else {
                        mergeMusicVideo();
                    }

                }
            }

            @Override
            public void onFailure() {
                Logger.e("demuxerVideo-onFailure");
                //合成失败  重新合成
                mergeMusicVideo();
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                Logger.e("EpEditor.demuxer" + progress);
            }
        });
    }

    private Double progress = 0.5;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            //if ()
            if (progress == 1) {
                handler.removeCallbacks(runnable);
                return;
            }
            Message message = new Message();
            message.what = 5;
            message.obj = progress;
            mergeHandler.sendMessage(message);
            progress += 0.01;
            handler.postDelayed(runnable, 100);//每0.5秒监听一次是否在播放视频
        }
    };

    private void mergeVideo() {

        if (tempList.size() == 1) {
            outPath = tempList.get(0);
            Message message = new Message();
            message.what = 2;
            mergeHandler.sendMessage(message);
        } else {
            ArrayList<EpVideo> epVideos = new ArrayList<>();
            for (int i = 0; i < tempList.size(); i++) {
                epVideos.add(new EpVideo(tempList.get(i)));
            }
            outPath = FileUtils.createVideoEditFileName("mergeVideo");
            EpEditor.mergeByLc(mContext, epVideos, new EpEditor.OutputOption(outPath), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Message message = new Message();
                    message.what = 2;
                    mergeHandler.sendMessage(message);
                }

                @Override
                public void onFailure() {
                    Logger.e("mergeVideo-onFailure");

                }

                @Override
                public void onProgress(float progress) {
                    //这里获取处理进度
                    Logger.e("视频合并中——————" + progress);
                }
            });
        }

    }


    /**
     * 分段同速率合并
     */
    private void mergeVideo1() {
        if (videoList.size() == 1) {
            outPath = videoList.get(0);
            Message message = new Message();
            message.what = 2;
            mergeHandler.sendMessage(message);
        } else {
            ArrayList<EpVideo> epVideos = new ArrayList<>();
            for (int i = 0; i < videoList.size(); i++) {
                epVideos.add(new EpVideo(videoList.get(i)));
                Logger.e("" + videoList.get(i));
            }
            outPath = FileUtils.createVideoEditFileName("mergeVideo");

            EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outPath);
            outputOption.setWidth(720);//输出视频宽
            outputOption.setHeight(1280);//输出视频高度
            outputOption.frameRate = 30;//输出视频帧率,默认30
            outputOption.bitRate = 10;//输出视频码率,默认10
            EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Logger.e("合成视频成功－－－ onSuccess()");
                    Message message = new Message();
                    message.what = 2;
                    mergeHandler.sendMessage(message);
                }

                @Override
                public void onFailure() {
                    Logger.e("mergeVideo-onFailure");
                    Message message = new Message();
                    message.what = 6;
                    message.arg1 = 3;
                    mergeHandler.sendMessage(message);
                }

                @Override
                public void onProgress(float progress) {
                    //这里获取处理进度
                    Logger.e("视频合并中——————" + progress);
                    Message message = new Message();
                    message.what = 5;
                    message.arg1 = 99;
                    message.obj = progress;
                    mergeHandler.sendMessage(message);
                }
            });
        }

    }


    List<String> tempList = new ArrayList<>();

    //视频变速
    private void changePTS() {
//        Logger.e(speedTypeList.toString());
//        if (speedTypeList.size() > 0 && mergeVideoIndex < speedTypeList.size()) {
//            if (speedTypeList.get(mergeVideoIndex) == 1.0f) {
//                tempList.add(videoList.get(mergeVideoIndex));
//                Message message = new Message();
//                message.what = 1;
//                mergeHandler.sendMessage(message);
//            } else {
//                final String tempPath = FileUtils.createVideoEditFileName("PTS" + mergeVideoIndex);
//                //参数分别是视频路径,输出路径,变速倍率（仅支持0.25-4倍),变速类型(VIDEO-视频(选择VIDEO的话则会屏蔽音频),AUDIO-音频,ALL-视频音频同时变速)
//                EpEditor.changePTS(videoList.get(mergeVideoIndex), tempPath,currentSpeed, EpEditor.PTS.ALL, new OnEditorListener() {
//                    @Override
//                    public void onSuccess() {
//                        tempList.add(tempPath);
//                        Message message = new Message();
//                        message.what = 1;
//                        mergeHandler.sendMessage(message);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        Logger.e("changePTS-onFailure");
//                    }
//
//                    @Override
//                    public void onProgress(float progress) {
//                        Logger.e("视频" + (mergeVideoIndex + 1) + "---------视频变速中——————" + progress);
//                        Message message = new Message();
//                        message.what = 5;
//                        message.obj = progress;
//                        mergeHandler.sendMessage(message);
//                    }
//                });
//            }
//        }
    }

    String PTSPath;

    /**
     * 一段视频变速
     */
    private void changePTS1() {
        Logger.e(currentSpeed + "");
        if (currentSpeed == 1.0f) {
            PTSPath = outPath;
            Message message = new Message();
            message.what = 1;
            mergeHandler.sendMessage(message);
        } else {

            PTSPath = FileUtils.createVideoEditFileName("PTS");
            //参数分别是视频路径,输出路径,变速倍率（仅支持0.25-4倍),变速类型(VIDEO-视频(选择VIDEO的话则会屏蔽音频),AUDIO-音频,ALL-视频音频同时变速)
            EpEditor.changePTS(outPath, PTSPath, currentSpeed, EpEditor.PTS.ALL, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    Message message = new Message();
                    message.what = 1;
                    mergeHandler.sendMessage(message);
                }

                @Override
                public void onFailure() {
                    Logger.e("changePTS-onFailure");
                    Message message = new Message();
                    message.what = 6;
                    message.arg1 = 1;
                    mergeHandler.sendMessage(message);
                }

                @Override
                public void onProgress(float progress) {
                    Logger.e("视频" + (mergeVideoIndex + 1) + "---------视频变速中——————" + progress);
                    Message message = new Message();
                    message.what = 5;
                    message.obj = progress;
                    mergeHandler.sendMessage(message);
                }
            });
        }
    }


    @Override
    protected void onRecordSpeed(float speedType) {
        videoSpeed = speedType;
    }

    /**
     * 删除视频
     */
    @Override
    protected void onDeletPartVideo() {
        if (videoList != null && videoList.size() > 0) {
            isChangeVideo = true;
            RxFileTool.deleteFile(videoList.get(videoList.size() - 1));
            videoList.remove(videoList.size() - 1);
            if (videoList.size() == 0) {
                isReslMusic = true;
            } else {
                isReslMusic = false;
            }
        }
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mFullScreenCamera;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        int faceTrackingStatus = 0;
        int systemErrorStatus = 0;//success number

        CameraClipFrameRect cameraClipFrameRect;

        LandmarksPoints landmarksPoints;
        float[] landmarksData = new float[150];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Logger.d(TAG, "onSurfaceCreated fu version " + faceunity.fuGetVersion());

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));
            mFullScreenCamera = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mFullScreenCamera.createTextureObject();

            cameraClipFrameRect = new CameraClipFrameRect(0.4f, 0.4f * 0.8f); //clip 20% vertical
            landmarksPoints = new LandmarksPoints();//如果有证书权限可以获取到的话，绘制人脸特征点

            switchCameraSurfaceTexture();

            try {
                InputStream is = getAssets().open("v3.mp3");
                byte[] v3data = new byte[is.available()];
                int len = is.read(v3data);
                is.close();
                faceunity.fuSetup(v3data, null, authpack.A());
                faceunity.fuSetMaxFaces(3);//设置最大识别人脸数目
                Logger.d(TAG, "fuSetup v3 len " + len);

                if (mUseBeauty) {
                    is = getAssets().open("face_beautification.mp3");
                    byte[] itemData = new byte[is.available()];
                    len = is.read(itemData);
                    Logger.d(TAG, "beautification len " + len);
                    is.close();
                    mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                    itemsArray[0] = mFaceBeautyItem;
                }

//                if (mUseGesture) {
//                    is = getAssets().open("heart.mp3");
//                    byte[] itemData = new byte[is.available()];
//                    len = is.read(itemData);
//                    Logger.d(TAG, "heart len " + len);
//                    is.close();
//                    mGestureItem = faceunity.fuCreateItemFromPackage(itemData);
//                    itemsArray[2] = mGestureItem;
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            isFirstCameraOnDrawFrame = true;
        }

        public void switchCameraSurfaceTexture() {
            Logger.d(TAG, "switchCameraSurfaceTexture");
            isNeedSwitchCameraSurfaceTexture = false;
            if (mCameraSurfaceTexture != null) {
                faceunity.fuOnCameraChange();
                destroySurfaceTexture();
            }
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            Logger.d(TAG, "send start camera message");
            mMainHandler.sendMessage(mMainHandler.obtainMessage(
                    RecordVideoAty.MainHandler.HANDLE_CAMERA_START_PREVIEW,
                    mCameraSurfaceTexture));
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Logger.d(TAG, "onSurfaceChanged " + width + " " + height);
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Logger.d(TAG, "onDrawFrame");

            if (isInPause) {
                //glSf.requestRender();
                return;
            }

            if (isNeedSwitchCameraSurfaceTexture) {
                switchCameraSurfaceTexture();
            }

            Logger.d(TAG, "after switchCameraSurfaceTexture");

            /**
             * If camera texture data not ready there will be low possibility in meizu note3 which maybe causing black screen.
             */
            while (cameraDataAlreadyCount < 2) {
                Logger.d(TAG, "while cameraDataAlreadyCount < 2");
                if (isFirstCameraOnDrawFrame) {
                    glSf.requestRender();
                    return;
                }
                synchronized (prepareCameraDataLock) {
                    //block until new camera frame comes.
                    try {
                        prepareCameraDataLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            isFirstCameraOnDrawFrame = false;

            if (++currentFrameCnt == 100) {
                currentFrameCnt = 0;
                long tmp = System.nanoTime();
                if (isBenchmarkFPS)
                    Logger.d(TAG, "dualInput FPS : " + (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - lastOneHundredFrameTimeStamp) / 100.0f)));
                lastOneHundredFrameTimeStamp = tmp;
                if (isBenchmarkTime)
                    Logger.d(TAG, "dualInput cost time avg : " + oneHundredFrameFUTime / 100.f / NANO_IN_ONE_MILLI_SECOND);
                oneHundredFrameFUTime = 0;
            }

            /**
             * 获取camera数据, 更新到texture
             */
            float[] mtx = new float[16];
            if (mCameraSurfaceTexture != null) {
                try {
                    mCameraSurfaceTexture.updateTexImage();
                    mCameraSurfaceTexture.getTransformMatrix(mtx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                throw new RuntimeException("HOW COULD IT HAPPEN!!! mCameraSurfaceTexture is null!!!");

            final int isTracking = faceunity.fuIsTracking();
            if (isTracking != faceTrackingStatus) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTracking == 0) {
//                            mFaceTrackingStatusImageView.setVisibility(View.VISIBLE);
                            Arrays.fill(landmarksData, 0);
                        } else {
//                            mFaceTrackingStatusImageView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                faceTrackingStatus = isTracking;
            }
            Logger.d(TAG, "isTracking " + isTracking);

            final int systemError = faceunity.fuGetSystemError();
            if (systemError != systemErrorStatus) {
                systemErrorStatus = systemError;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d(TAG, "system error " + systemError + " " + faceunity.fuGetSystemErrorString(systemError));
//                        tvSystemError.setText(faceunity.fuGetSystemErrorString(systemError));
                    }
                });
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendEmptyMessage(RecordVideoAty.CreateItemHandler.HANDLE_CREATE_ITEM);
            }

            faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
            faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
            faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
//            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
//            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
//            faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);

            //faceunity.fuItemSetParam(mFaceBeautyItem, "use_old_blur", 1);

            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Logger.d(TAG, "camera nv21 bytes null");
                glSf.requestRender();
                glSf.requestRender();
                return;
            }

            boolean isOESTexture = true; //Tip: camera texture类型是默认的是OES的，和texture 2D不同
            int flags = isOESTexture ? faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE : 0;
            boolean isNeedReadBack = false; //是否需要写回，如果是，则入参的byte[]会被修改为带有fu特效的；支持写回自定义大小的内存数组中，即readback custom img
            flags = isNeedReadBack ? flags | faceunity.FU_ADM_FLAG_ENABLE_READBACK : flags;
            if (isNeedReadBack) {
                if (fuImgNV21Bytes == null) {
                    fuImgNV21Bytes = new byte[mCameraNV21Byte.length];
                }
                System.arraycopy(mCameraNV21Byte, 0, fuImgNV21Bytes, 0, mCameraNV21Byte.length);
            } else {
                fuImgNV21Bytes = mCameraNV21Byte;
            }
            flags |= currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X;

            if (isInAvatarMode)
                faceunity.fuItemSetParam(mEffectItem, "default_rotation_mode", (currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) ? 1 : 3);

            long fuStartTime = System.nanoTime();
            /*
             * 这里拿到fu处理过后的texture，可以对这个texture做后续操作，如硬编、预览。
             */
//            int fuTex = 0;
//            Log.d("LogTest", "mFrameId:" + mFrameId);
//            if (mFrameId % 2 == 0) {
            int fuTex = faceunity.fuDualInputToTexture(fuImgNV21Bytes, mCameraTextureId, flags,
                    cameraWidth, cameraHeight, mFrameId, itemsArray);
            long fuEndTime = System.nanoTime();
            oneHundredFrameFUTime += fuEndTime - fuStartTime;

            //int fuTex = faceunity.fuBeautifyImage(mCameraTextureId, flags,
            //            cameraWidth, cameraHeight, mFrameId++, new int[] {mEffectItem, mFaceBeautyItem});
            //mFullScreenCamera.drawFrame(mCameraTextureId, mtx);
            if (mFullScreenFUDisplay != null) mFullScreenFUDisplay.drawFrame(fuTex, mtx);
            else
                throw new RuntimeException("HOW COULD IT HAPPEN!!! mFullScreenFUDisplay is null!!!");
//            }
            mFrameId++;
            /**
             * 绘制Avatar模式下的镜头内容以及landmarks
             **/
            if (isInAvatarMode) {
                cameraClipFrameRect.drawFrame(mCameraTextureId, mtx);
                faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
                landmarksPoints.refresh(landmarksData, cameraWidth, cameraHeight, 0.1f, 0.8f, currentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT);
                landmarksPoints.draw();
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(START_RECORDING)) {
                videoFileName = FileUtils.createVideoFileName();
                File outFile = new File(videoFileName);
                videoList.add(videoFileName);
                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, cameraHeight, cameraWidth,
                        3000000, EGL14.eglGetCurrentContext(), mCameraSurfaceTexture.getTimestamp()
                ));

                //forbid click until start or stop success
                mTextureMovieEncoder.setOnEncoderStatusUpdateListener(new TextureMovieEncoder.OnEncoderStatusUpdateListener() {
                    @Override
                    public void onStartSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Logger.e("start encoder success");
//                                mRecordingBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onStopSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Logger.e("stop encoder success");
//                                mRecordingBtn.setVisibility(View.VISIBLE);
                                //暂停之后需要做相应的操作  不然再次拍摄的视频有问题
                                isFirstCameraOnDrawFrame = true;
                                resumeTimeStamp = System.nanoTime();
                                isFirstOnFrameAvailable = true;
                                isInPause = false;
                                /**
                                 * 请注意这个地方, camera返回的图像并不一定是设置的大小（因为可能并不支持）
                                 */
                                if (mCamera != null) {
                                    Camera.Size size = mCamera.getParameters().getPreviewSize();
                                    cameraWidth = size.width;
                                    cameraHeight = size.height;
                                    Logger.d(TAG, "open camera size width : " + size.width + " height : " + size.height);
                                }
                                AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
                                aspectFrameLayout.setAspectRatio(1.0f * cameraHeight / cameraWidth);
                            }
                        });
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(RecordVideoAty.this, "video file saved to "
//                                + videoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                mTextureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            if (!isInPause) glSf.requestRender();

        }

        public void notifyPause() {
            faceTrackingStatus = 0;
            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivTpTake.performClick();
                    }
                });
            }

            if (mFullScreenFUDisplay != null) {
                mFullScreenFUDisplay.release(false);
                mFullScreenFUDisplay = null;
            }

            if (mFullScreenCamera != null) {
                mFullScreenCamera.release(false);
                mFullScreenCamera = null;
            }
        }

        public void destroySurfaceTexture() {
            if (mCameraSurfaceTexture != null) {
                mCameraSurfaceTexture.release();
                mCameraSurfaceTexture = null;
            }
        }
    }

    static class MainHandler extends Handler {

        static final int HANDLE_CAMERA_START_PREVIEW = 1;

        private WeakReference<RecordVideoAty> mActivityWeakReference;

        MainHandler(RecordVideoAty activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecordVideoAty activity = mActivityWeakReference.get();
            switch (msg.what) {
                case HANDLE_CAMERA_START_PREVIEW:
                    Logger.d(TAG, "HANDLE_CAMERA_START_PREVIEW");
                    activity.handleCameraStartPreview((SurfaceTexture) msg.obj);
                    break;
            }
        }
    }

    /**
     * set preview and start preview after the surface created
     */
    private void handleCameraStartPreview(SurfaceTexture surfaceTexture) {
        Logger.d(TAG, "handleCameraStartPreview");
        if (previewCallbackBuffer == null) {
            Logger.d(TAG, "allocate preview callback buffer");
            previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][cameraWidth * cameraHeight * 3 / 2];
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
            mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceTexture.setOnFrameAvailableListener(this);
        mCamera.startPreview();
    }

    static class CreateItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;

        WeakReference<Context> mContext;

        CreateItemHandler(Looper looper, Context context) {
            super(looper);
            mContext = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = mContext.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CREATE_ITEM:
                    try {
                        if (mEffectFileName.equals("none")) {
                            itemsArray[1] = mEffectItem = 0;
                        } else {

                            FileInputStream is = new FileInputStream(mEffectFileName);
                            int length = is.available();
                            byte[] itemData = new byte[length];
                            is.read(itemData);
                            int len = is.read(itemData);
                            Logger.e("FU", "effect len " + len);
                            final int tmp = itemsArray[1];
                            itemsArray[1] = mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                            faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
                            faceunity.fuItemSetParam(mEffectItem, "rotationAngle",
                                    ((RecordVideoAty) mContext.get()).getCurrentCameraType()
                                            == Camera.CameraInfo.CAMERA_FACING_FRONT ? 90 : 270);
                            if (tmp != 0) {
                                faceunity.fuDestroyItem(tmp);
                            }
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }


    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType, int desiredWidth, int desiredHeight) {
        Logger.d(TAG, "openCamera");

        cameraDataAlreadyCount = 0;

        if (mCamera != null) {
            throw new RuntimeException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraType) {
                cameraId = i;
                mCamera = Camera.open(i);
                currentCameraType = cameraType;
                break;
            }
        }
        if (mCamera == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RecordVideoAty.this,
                            "Open Camera Failed! Make sure it is not locked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
            throw new RuntimeException("unable to open camera");
        }

        CameraUtils.setCameraDisplayOrientation(this, cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        /**
         * 设置对焦，会影响camera吞吐速率
         */
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        /**
         * 设置fps
         * */
        if (boostBestCameraFPS) {
            int[] closetFramerate = CameraUtils.closetFramerate(parameters, 30);
            Logger.d(TAG, "closet framerate min " + closetFramerate[0] + " max " + closetFramerate[1]);
            parameters.setPreviewFpsRange(closetFramerate[0], closetFramerate[1]);
        }

        CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCamera.setParameters(parameters);
    }

    public int getCurrentCameraType() {
        return currentCameraType;
    }

    private void releaseCamera() {
        Logger.d(TAG, "release camera");
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
        mCreateItemThread.quit();
        mCreateItemThread = null;
        mCreateItemHandler = null;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (mergeDialog != null) {
            mergeDialog.dismiss();
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        EventBus.getDefault().unregister(this);
        if (mmrc != null) {
            mmrc.release();
        }
    }

    private void showExitScreenDialog(Context context) {
        dialog = new CommomDialog(RecordVideoAty.this, R.style.dialog, "确定退出录制吗?", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    dialog.dismiss();
                    finish();
                }
            }
        }).setTitle("温馨提示");
        dialog.show();
    }

    //参考二维码工具的闪光灯
    public void switchFlash() {
        if (mIsOpen) {
            setFlashLight(true);
        } else {
            setFlashLight(false);
        }
        mIsOpen = !mIsOpen;
    }

    public boolean setFlashLight(boolean open) {
        if (mCamera == null) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return false;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (null == flashModes || 0 == flashModes.size()) {
            // Use the screen as a flashlight (next best thing)
            return false;
        }
        String flashMode = parameters.getFlashMode();
        if (open) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        } else {
            if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MUSIC && resultCode == RESULT_OK) {//选择音乐、
            if (data != null) {
                songPath = data.getStringExtra("VideoMusic");
                //songPath = data.getStringExtra("VideoMusic");
                isCheckMusic = true;
                mergeMusicVideo();
            }
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CANCEL_CHOOSE_MUSIC) {
            //取消选择音乐
            Logger.e("PTSPath:" + PTSPath);
            //取封面图是耗时操作  需放到子线程中
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap = decodeThumbBitmapForFile(videoList.get(0));
                        if (bitmap == null) {
                            bitmap = decodeThumbBitmapForFile1(videoList.get(0));
                            if (bitmap == null) {
                                Message message = new Message();
                                message.what = 4;
                                message.obj = coverPath;
                                mergeHandler.sendMessage(message);
                            } else {
                                ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
                                    @Override
                                    public void onCompleted(String path) {
                                        if (bitmap != null) {
                                            bitmap.recycle();
                                        }
                                        coverPath = path;
                                        Message message = new Message();
                                        message.what = 4;
                                        message.obj = path;
                                        mergeHandler.sendMessage(message);
                                    }
                                });
                            }
                        }
                        ImageUtil.saveBitmapToFile(mContext, bitmap, cameraHeight, cameraWidth, new ImageUtil.SavePhotoCompletedCallBack() {
                            @Override
                            public void onCompleted(String path) {
                                if (bitmap != null) {
                                    bitmap.recycle();
                                }
                                coverPath = path;
                                Message message = new Message();
                                message.what = 4;
                                message.obj = path;
                                mergeHandler.sendMessage(message);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (myEventBusModel.CLOSE_VIDEO_ACTIVITY) {
            finish();
        }

        if (myEventBusModel.MAGIC_TACK_STYLE) {
            String callInfo = myEventBusModel.MAGIC_TACK_STYLE_INFO;
            onEffectItemSelected(callInfo);
        }

        if (myEventBusModel.MAGIC_TACK_STYLE) {
            String callInfo = myEventBusModel.MAGIC_TACK_STYLE_INFO;
            onEffectItemSelected(callInfo);
        }
    }

    /**
     * 查询魔拍是否是HOT状态
     *
     * @param memberId
     */
    private void queryIsHotStatus(String memberId) {
        OkGo.<String>get(Urls.queryIsHotStatus)
                .tag(this)
                .params("memberId", memberId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    String isHot = data.optJSONObject("data").optString("isHot");
                                    //是否显示Hot 0:不显示 1:显示
                                    if (TextUtils.equals(isHot, "1")) {
                                        btnHot.setVisibility(View.VISIBLE);
                                    } else {
                                        btnHot.setVisibility(View.GONE);
                                    }
                                } else {
                                    //RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitScreenDialog(RecordVideoAty.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @param path 文件路径
     * @return Bitmap 缩略图
     * @Description 定宽高解码缩略图
     */
    private Bitmap decodeThumbBitmapForFile(final String path) {
        Bitmap bitmaps = null;
        if (mmrc == null) {
            //自动 - 推荐
            mmrc = new MediaMetadataRetrieverCompat();
        }
        //FFmpeg
        //MediaMetadataRetrieverCompat  mmrc = new MediaMetadataRetrieverCompat(MediaMetadataRetrieverCompat.RETRIEVER_FFMPEG);
        try {
            try {
                mmrc.setMediaDataSource(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //bitmap = mmrc.getFrameAtTime();
            mmrc.getScaledFrameAtTime(500 * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST, cameraHeight, cameraWidth);
            //获取指定位置指定宽高的缩略图
            bitmaps = mmrc.getScaledFrameAtTime(1000 * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST, cameraHeight, cameraWidth);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        }
        return bitmaps;
    }

    /**
     * 获取封面图失败 用系统MediaMetadataRetriever获取封面图
     *
     * @param path
     * @return
     */
    private Bitmap decodeThumbBitmapForFile1(final String path) {
        Bitmap bitmaps = null;
        MediaMetadataRetriever mmrc = new MediaMetadataRetriever();
        try {
            mmrc.setDataSource(path);
            //获取指定位置指定宽高的缩略图
            bitmaps = mmrc.getFrameAtTime(1000 * 1000, MediaMetadataRetrieverCompat.OPTION_CLOSEST_SYNC);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        }
        mmrc.release();
        return bitmaps;
    }

    /*时间戳转换成字符窜*/
    private String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        return sf.format(d);
    }
}
