package com.merrichat.net.activity.merrifunction;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.faceunity.wrapper.faceunity;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.merrichat.net.R;
import com.merrichat.net.activity.picture.PhotoFilmActivity;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.adapter.PhotoAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.encoder.TextureMovieEncoder;
import com.merrichat.net.gles.CameraClipFrameRect;
import com.merrichat.net.gles.FullFrameRect;
import com.merrichat.net.gles.LandmarksPoints;
import com.merrichat.net.gles.Texture2dProgram;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.CameraUtils;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.ImageUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.authpack;
import com.merrichat.net.view.AspectFrameLayout;
import com.merrichat.net.view.CameraSettingPopuWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.merrichat.net.encoder.TextureMovieEncoder.IN_RECORDING;
import static com.merrichat.net.encoder.TextureMovieEncoder.START_RECORDING;


/**
 * Created by amssy on 17/11/6.
 * 拍照
 */

public class TakePictureAty extends ITakePictureUIAty implements Camera.PreviewCallback, SurfaceTexture.OnFrameAvailableListener, BaseQuickAdapter.OnItemClickListener {
    final static String TAG = "TakePictureAty";
    public static float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private Context mContext;
    private Camera mCamera;

    private GLSurfaceView glSf;
    private GLRenderer glRenderer;
    private RecyclerView rvImage;
    private PhotoAdapter photoAdapter;
    private List<String> photoList;

    private byte[][] previewCallbackBuffer;
    private final int PREVIEW_BUFFER_COUNT = 3;


    private int cameraWidth = 1280;
    private int cameraHeight = 720;

    private int surfaceWidth = 0;
    private int surfaceHeight = 0;

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
    float mFaceBeautyBlurLevel = 4.0f;
    float mFaceBeautyCheekThin = 0.8f;
    float mFaceBeautyEnlargeEye = 0.8f;
    private boolean isTakePicture;//开始拍照
    private boolean isTaking;//正在拍照
    private boolean isZidong = true;//自动保存
    private static boolean mIsOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        RxActivityTool.addActivity(this);
        mContext = this;
        EventBus.getDefault().register(this);
        mMainHandler = new MainHandler(this);

        glSf = (GLSurfaceView) findViewById(R.id.glsv);
        rvImage = (RecyclerView) findViewById(R.id.rv_image);
        glSf.setEGLContextClientVersion(2);
        glRenderer = new GLRenderer();
        glSf.setRenderer(glRenderer);
        glSf.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper(), mContext);

        photoList = new ArrayList<>();
        rvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImage.addItemDecoration(new SpaceItemDecorations(DensityUtils.dp2px(mContext, 20)));
        photoAdapter = new PhotoAdapter(R.layout.item_photo, photoList);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(photoAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rvImage);
        // 开启拖拽
        photoAdapter.enableDragItem(itemTouchHelper, R.id.iv_photo, true);
        photoAdapter.setOnItemDragListener(onItemDragListener);
        rvImage.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickListener(this);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.MAGIC_TACK_STYLE) {
            String callInfo = myEventBusModel.MAGIC_TACK_STYLE_INFO;
            onEffectItemSelected(callInfo);
        }
    }

    class SpaceItemDecorations extends RecyclerView.ItemDecoration {

        int mSpace;
        private int spanNum = 0;

        public SpaceItemDecorations(int space, int spanNum) {
            this.mSpace = space;
            this.spanNum = spanNum;
        }

        public SpaceItemDecorations(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.top = mSpace;
            }
            if (spanNum != 0) {
                if ((parent.getChildAdapterPosition(view) + 1) % spanNum == 0) {
                    outRect.right = mSpace;
                }
            }

        }
    }


    int fromIndex, toIndex;
    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            fromIndex = pos;
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            toIndex = pos;
//            Collections.swap(photoList, fromIndex, toIndex);
            Logger.e(photoList.toString());
        }
    };

    @Override
    protected void onResume() {
        Logger.e(TAG, "onResume");
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
        Logger.e(TAG, "open camera size width : " + size.width + " height : " + size.height);

        AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
        aspectFrameLayout.setAspectRatio(1.0f * cameraHeight / cameraWidth);
        glSf.onResume();
    }


    @Override
    protected void onPause() {
        Logger.e(TAG, "onPause");

        isInPause = true;

        super.onPause();

        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);

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
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (isFirstOnFrameAvailable) {
            frameAvailableTimeStamp = System.nanoTime();
            isFirstOnFrameAvailable = false;
            Logger.e(TAG, "first frame available time cost " +
                    (frameAvailableTimeStamp - resumeTimeStamp) / NANO_IN_ONE_MILLI_SECOND);
        }
        Logger.e(TAG, "onFrameAvailable");
        synchronized (prepareCameraDataLock) {
            cameraDataAlreadyCount++;
            prepareCameraDataLock.notify();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Logger.e(TAG, "onPreviewFrame len " + data.length);
        Logger.e(TAG, "onPreviewThread " + Thread.currentThread());
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
        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);
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
        Logger.e(TAG, "onCameraChange");
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
    protected void onCameraTakePicture() {
        isTakePicture = true;
    }

    @Override
    protected void onFinshThisScreen() {
        if (isTaking) {
            return;
        }
        if (photoList.size() > 0) {
            showExitScreenDialog(mContext);
        } else {
            finish();
        }
    }

    @Override
    protected void onCameraSetting(View clickView) {
        CameraSettingPopuWindow cameraSettingPopuWindow = new CameraSettingPopuWindow((Activity) mContext, is_timer_down_open, is_flash_light_open, currentCameraType, true, isZidong, clickView);
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
                isZidong = isOpen;
            }

            @Override
            public void onDismiss() {
                onBackControlUI(false);
            }
        });
    }

    @Override
    protected void onCompleted() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("imgPathList", (Serializable) photoList);
        RxActivityTool.skipActivity(mContext, ReleaseGraphicAlbumAty.class, bundle);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        showDeleItemDialog(mContext, position);
    }


    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mFullScreenCamera;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        boolean isFirstCameraOnDrawFrame;

        int faceTrackingStatus = 0;
        int systemErrorStatus = 0;//success number

        CameraClipFrameRect cameraClipFrameRect;

        LandmarksPoints landmarksPoints;
        float[] landmarksData = new float[150];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Logger.e(TAG, "onSurfaceCreated fu version " + faceunity.fuGetVersion());

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
                Logger.e(TAG, "fuSetup v3 len " + len);

                if (mUseBeauty) {
                    is = getAssets().open("face_beautification.mp3");
                    byte[] itemData = new byte[is.available()];
                    len = is.read(itemData);
                    Logger.e(TAG, "beautification len " + len);
                    is.close();
                    mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                    itemsArray[0] = mFaceBeautyItem;
                }

//                if (mUseGesture) {
//                    is = getAssets().open("heart.mp3");
//                    byte[] itemData = new byte[is.available()];
//                    len = is.read(itemData);
//                    Logger.e(TAG, "heart len " + len);
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
            Logger.e(TAG, "switchCameraSurfaceTexture");
            isNeedSwitchCameraSurfaceTexture = false;
            if (mCameraSurfaceTexture != null) {
                faceunity.fuOnCameraChange();
                destroySurfaceTexture();
            }
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            Logger.e(TAG, "send start camera message");
            mMainHandler.sendMessage(mMainHandler.obtainMessage(
                    MainHandler.HANDLE_CAMERA_START_PREVIEW,
                    mCameraSurfaceTexture));
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Logger.e(TAG, "onSurfaceChanged " + width + " " + height);
            GLES20.glViewport(0, 0, width, height);
            surfaceWidth = width;
            surfaceHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Logger.e(TAG, "onDrawFrame");

            if (isInPause) {
                //glSf.requestRender();
                return;
            }

            if (isNeedSwitchCameraSurfaceTexture) {
                switchCameraSurfaceTexture();
            }

            Logger.e(TAG, "after switchCameraSurfaceTexture");

            /**
             * If camera texture data not ready there will be low possibility in meizu note3 which maybe causing black screen.
             */
            while (cameraDataAlreadyCount < 2) {
                Logger.e(TAG, "while cameraDataAlreadyCount < 2");
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
                    Logger.e(TAG, "dualInput FPS : " + (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - lastOneHundredFrameTimeStamp) / 100.0f)));
                lastOneHundredFrameTimeStamp = tmp;
                if (isBenchmarkTime)
                    Logger.e(TAG, "dualInput cost time avg : " + oneHundredFrameFUTime / 100.f / NANO_IN_ONE_MILLI_SECOND);
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
            Logger.e(TAG, "isTracking " + isTracking);

            final int systemError = faceunity.fuGetSystemError();
            if (systemError != systemErrorStatus) {
                systemErrorStatus = systemError;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.e(TAG, "system error " + systemError + " " + faceunity.fuGetSystemErrorString(systemError));
//                        tvSystemError.setText(faceunity.fuGetSystemErrorString(systemError));
                    }
                });
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendEmptyMessage(CreateItemHandler.HANDLE_CREATE_ITEM);
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
                Logger.e(TAG, "camera nv21 bytes null");
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
            int fuTex = faceunity.fuDualInputToTexture(fuImgNV21Bytes, mCameraTextureId, flags,
                    cameraWidth, cameraHeight, mFrameId++, itemsArray);
            long fuEndTime = System.nanoTime();
            oneHundredFrameFUTime += fuEndTime - fuStartTime;

            //int fuTex = faceunity.fuBeautifyImage(mCameraTextureId, flags,
            //            cameraWidth, cameraHeight, mFrameId++, new int[] {mEffectItem, mFaceBeautyItem});
            //mFullScreenCamera.drawFrame(mCameraTextureId, mtx);
            if (mFullScreenFUDisplay != null) mFullScreenFUDisplay.drawFrame(fuTex, mtx);
            else
                throw new RuntimeException("HOW COULD IT HAPPEN!!! mFullScreenFUDisplay is null!!!");

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
                videoFileName = FileUtils.createVideoFileName() + "_camera.mp4";
                File outFile = new File(videoFileName);
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
                                Logger.e(TAG, "start encoder success");
//                                mRecordingBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onStopSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Logger.e(TAG, "stop encoder success");
//                                mRecordingBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TakePictureAty.this, "video file saved to "
                                + videoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                mTextureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            if (!isInPause) glSf.requestRender();
            if (isTakePicture) {
                isTakePicture = false;
                try {
                    shootTakePictureSound(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isTaking = true;
                final Bitmap bmp = createBitmapFromGLSurface(0, 0, surfaceWidth, surfaceHeight, gl);
                ImageUtil.saveBitmapToFile(mContext, bmp, bmp.getWidth(), bmp.getHeight(), new ImageUtil.SavePhotoCompletedCallBack() {
                    @Override
                    public void onCompleted(final String path) {
                        if (null != bmp) {
                            bmp.recycle();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isZidong) {
                                    String newPath = FileUtils.createPhotoFileName();
                                    copyFile(path, newPath);
                                    int index = newPath.split("/").length;
                                    copyFile(path, FileUtils.CameraPictureRootPath + newPath.split("/")[index - 1]);
                                    //保存图片后发送广播通知更新数据库
                                    Uri uri = Uri.fromFile(new File(path));
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                                }
                                findViewById(R.id.iv_tp_take).setEnabled(true);
                                photoList.add(path);
                                photoAdapter.notifyDataSetChanged();
                                //滚动到最后一张
                                rvImage.smoothScrollToPosition(photoList.size() - 1);

                                isTaking = false;
                                if (photoList.size() == 0) {
                                    ivTpComplete.setVisibility(View.GONE);
                                }
                                if (photoList != null && photoList.size() > 0) {
                                    ivTpComplete.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        }

        /**
         * 播放系统拍照声音
         *
         * @param cnt
         * @return
         * @throws Exception
         * @throws IOException
         */
        private MediaPlayer shootTakePictureSound(Context cnt) throws Exception, IOException {
            /*
             * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
             * */
            Vibrator vibrator = (Vibrator) cnt.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
            vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

            Uri alert = Uri.parse("file:///system/media/audio/ui/camera_click.ogg");
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

        public void notifyPause() {
            faceTrackingStatus = 0;
            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mRecordingBtn.performClick();
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

        private WeakReference<TakePictureAty> mActivityWeakReference;

        MainHandler(TakePictureAty activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TakePictureAty activity = mActivityWeakReference.get();
            switch (msg.what) {
                case HANDLE_CAMERA_START_PREVIEW:
                    Logger.e(TAG, "HANDLE_CAMERA_START_PREVIEW");
                    activity.handleCameraStartPreview((SurfaceTexture) msg.obj);
                    break;
            }
        }
    }

    /**
     * set preview and start preview after the surface created
     */
    private void handleCameraStartPreview(SurfaceTexture surfaceTexture) {
        Logger.e(TAG, "handleCameraStartPreview");
        if (previewCallbackBuffer == null) {
            Logger.e(TAG, "allocate preview callback buffer");
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
                                    ((TakePictureAty) mContext.get()).getCurrentCameraType()
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
        Logger.e(TAG, "openCamera");

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
            showCameraPermissionDialog();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TakePictureAty.this,
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
            Logger.e(TAG, "closet framerate min " + closetFramerate[0] + " max " + closetFramerate[1]);
            parameters.setPreviewFpsRange(closetFramerate[0], closetFramerate[1]);
        }

        CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCamera.setParameters(parameters);
    }


    private void showCameraPermissionDialog() {
        final MaterialDialog dialog = new MaterialDialog(mContext);
        dialog.content("相机启动失败，请尝试在手机应用权限管理中打开权限")//
                .btnNum(1)
                .btnText("确定")
                .show();
        dialog.btnNum(1);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }
        );
    }

    public int getCurrentCameraType() {
        return currentCameraType;
    }

    private void releaseCamera() {
        Logger.e(TAG, "release camera");
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

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        mCreateItemThread.quit();
        mCreateItemThread = null;
        mCreateItemHandler = null;
    }


    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }


    private void showExitScreenDialog(Context context) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("确定退出拍照吗?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //处理监听事件
                                finish();
                            }
                        });
                    }
                });
    }

    private void showDeleItemDialog(Context context, final int index) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("确定要删除此张照片吗?")//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(new BounceTopEnter())//
                .dismissAnim(new SlideBottomExit())//
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        photoList.remove(index);
                        photoAdapter.notifyDataSetChanged();
                        if (photoList.size() == 0) {
                            ivTpComplete.setVisibility(View.GONE);
                        }
                        if (photoList != null && photoList.size() > 0) {
                            ivTpComplete.setVisibility(View.VISIBLE);
                        }
                    }
                });
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

}
