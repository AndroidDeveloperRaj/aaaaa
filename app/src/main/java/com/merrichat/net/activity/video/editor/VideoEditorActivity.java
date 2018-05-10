package com.merrichat.net.activity.video.editor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.faceunity.wrapper.faceunity;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.video.complete.VideoReleaseActivity;
import com.merrichat.net.activity.video.media.MediaPlayerWrapper;
import com.merrichat.net.activity.video.media.VideoInfo;
import com.merrichat.net.activity.video.player.VideoFrameLayout;
import com.merrichat.net.adapter.VideoEditorFilterAdapter;
import com.merrichat.net.adapter.VideoEditorParentAdapter;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.encoder.TextureMovieEncoder;
import com.merrichat.net.gles.FullFrameRect;
import com.merrichat.net.gles.Texture2dProgram;
import com.merrichat.net.model.PhotoFilmPicModel;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.ImageUtil;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.authpack;
import com.merrichat.net.view.PopVideo;
import com.merrichat.net.view.SpaceItemDecorations;
import com.merrichat.net.view.VideoSpaceItemDecoration;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.encoder.TextureMovieEncoder.START_RECORDING;

/**
 * 视频编辑
 * Created by amssy on 17/11/2.
 */
public class VideoEditorActivity extends BaseActivity implements VideoEditorParentAdapter.OnItemClickListener
        , VideoEditorFilterAdapter.OnFilterItemClickListener, SurfaceTexture.OnFrameAvailableListener, GLSurfaceView.Renderer
        , MediaPlayerWrapper.IMediaCallback {
    /**
     * 取消
     */
    @BindView(R.id.tv_left)
    TextView tvClose;
    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitle;
    /**
     * 完成
     */
    @BindView(R.id.tv_right)
    TextView tvFinish;
    /**
     * 播放暂停按钮
     */
    @BindView(R.id.video_play)
    ImageView videoPlay;
    /**
     * 滤镜父容器
     */
    @BindView(R.id.recyclerView_filter)
    RecyclerView recyclerViewFilter;
    /**
     * 撤销
     */
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    /**
     * 插入
     */
    @BindView(R.id.btn_insert)
    Button btnInsert;
    /**
     * 拆分
     */
    @BindView(R.id.btn_spilt)
    Button btnSpilt;
    /**
     * 拷贝
     */
    @BindView(R.id.btn_copy)
    Button btnCopy;
    /**
     * 倒放
     */
    @BindView(R.id.btn_upend)
    Button btnUpend;
    /**
     * 删除
     */
    @BindView(R.id.btn_delete)
    Button btnDelete;
    /**
     * 剪切的功能界面
     */
    @BindView(R.id.lin_cut)
    LinearLayout linCut;
    /**
     * 功能版容器
     */
    @BindView(R.id.lin_function)
    LinearLayout linFunction;
    /**
     * 滤镜
     */
    @BindView(R.id.radio_filter)
    RadioButton radioFilter;
    /**
     * 音乐
     */
    @BindView(R.id.radio_music)
    RadioButton radioMusic;
    /**
     * 剪裁
     */
    @BindView(R.id.radio_cut)
    RadioButton radioCut;
    /**
     * 特效
     */
    @BindView(R.id.radio_special)
    RadioButton radioSpecial;
    /**
     * 更多
     */
    @BindView(R.id.radio_more)
    RadioButton radioMore;
    /**
     * 底部按钮容器
     */
    @BindView(R.id.radio_bottom)
    RadioGroup radioBottom;

    @BindView(R.id.recycler_view_child)
    RecyclerView recyclerView;

    @BindView(R.id.rel_player_view)
    VideoFrameLayout relPlayerView;

    @BindView(R.id.rel_group)
    RelativeLayout relGroup;
    /**
     * GLSurfaceView(系统版)
     */
    @BindView(R.id.glSurfaceView)
    GLSurfaceView glSurfaceView;

    private String outFile = Environment.getExternalStorageDirectory() + "/MerriChat/";//保存视频路径
    private List<List<Bitmap>> listBitmap;//图片列表
    private List<Bitmap> list;
    private String videoPath = "";//视频路径
    private int videoTime;//视频长度
    private boolean isGo = true;
    private VideoEditorParentAdapter adapter;
    private ArrayList<String> listPath;//存放视频路径集合
    private int position = 0;

    private int selectPosition = 0;//点击选中position
    private int selectPositionMove = 0;//滑动选中position
    private List<Bitmap> listSelect;//选中的集合
    private List<Bitmap> list1;
    private List<Bitmap> list2;
    private double scrollTime;
    private boolean isMoveHands = false;
    private float time;
    private List<Integer> listTime;
    private int videoPosition;//播放的position

    private static final int REQUEST_PERMISSIONS = 2001;
    private FFmpeg ffmpeg;
    private String videoFileName;
    private String[] command;
    private ArrayList<PhotoFilmPicModel> filterList;//
    private SpaceItemDecorations spaceItemDecoration;
    private Bitmap bitmap;
    private MyTask myTask;
    private MediaMetadataRetriever retriever;
    private int times2;
    private int times1;
    private String path1;
    private String path2;
    private Context context;
    private boolean isVideoSpeed = false;//是否选择变速
    private String bitmapCover;//封面图

    public final static int activityId = MiscUtil.getActivityId();

    private ProgressDialog dialog_load;
    private String musicPath = "";//音乐路径
    private ProgressBar progress;
    private TextView tvProgress;
    private String releasePath = "";//可发布视频路径
    private PopVideo popVideo;
    private float SPEED_VIDEO = 1f;//视频变速的倍率
    private float videoVolume = 0.5f;//视频音量(默认设置)
    private float musicVolume = 0.5f;//音乐音量(默认设置)
    private int REQUEST_CODE_VIDEO_CHOOSE = 1;
    private int REQUEST_CODE_VIDEO_SPEED = 2;
    private int REQUEST_CODE_VIDEO_COVER = 3;
    private int REQUEST_CODE_VIDEO_MUSIC = 4;

    private ArrayList<String> listCRPath;
    private int crPosition = 1;//插入标识 1 在视频中间插入   0 在间隙之间直接插入
    private boolean isCRVideo = false;//是否是插入视频

    private int numProgress = 0;
    private boolean isChangMethes = false;

    @SuppressLint("HandlerLeak")
    private Handler handlerFinish = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    if (!isChangMethes) {
                        numProgress = msg.arg1;
                    } else {
                        numProgress += msg.arg1;
                        isChangMethes = false;
                    }
                    Log.d("LogTest", msg.arg1 + "视频合成：" + numProgress);
                    if (numProgress <= 100) {
                        progress.setProgress(numProgress);
                        tvProgress.setText(numProgress + "%");
                    }
                    break;
                case 2:
                    progress.setProgress(100);
                    tvProgress.setText("100%");
                    onVideoIntent();//跳转到发布视频界面
                    break;
            }
        }
    };
    /**
     * 普通线程
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x123:
                    adapter.notifyDataSetChanged();
                    break;
                case 0x1234://完成跳转到发布视频界面
                    //默认传第一张图片作为封面图
                    Intent intent = new Intent(VideoEditorActivity.this, VideoReleaseActivity.class);
                    intent.putExtra("videoPath", releasePath);//视频路径
                    intent.putExtra("cover", bitmapCover);//封面图
                    intent.putExtra("activityId", activityId);
                    startActivity(intent);
                    break;
                case FILTER_SUCCESS://滤镜成功
                    onVideoIntent();
                    break;
                case FILTER_ERRER://滤镜失败
                    popVideo.dismiss();
                    RxToast.showToast("合成滤镜失败");
                    break;
                case 100:
                    popVideo.dismiss();
                    RxToast.showToast("合成速度失败");
                    break;
                case 101://合成速度成功
                    releasePath = videoFileName;
                    if (!musicPath.equals("")) {
                        onVideoMusic(releasePath, musicPath, videoVolume, musicVolume);
                    } else if (isFilter) {
                        videoFilter(releasePath);
                    } else {
                        onVideoIntent();
                    }
                    break;
                case 102:
                    popVideo.dismiss();
                    RxToast.showToast("合成音乐失败");
                    break;
                case 103://合成音乐成功
                    if (isFilter) {
                        videoFilter(path);
                    } else {
                        releasePath = path;
                        onVideoIntent();
                    }
                    break;
                case VIDEO_SUCCESS://合成视频成功
                    //是否选择音乐（未选则不合成音乐）
                    if (!musicPath.equals("")) {
                        onVideoMusic(videoPathFinish, musicPath, videoVolume, musicVolume);
                    } else {
                        if (isVideoSpeed) {
                            //执行视频变速
                            videoSpeed(videoPathFinish);
                        } else if (isFilter) {
                            videoFilter(videoPathFinish);
                        } else {
                            releasePath = videoPathFinish;
                            onVideoIntent();
                        }
                    }
                    break;
                case VIDEO_ERRER:
                    popVideo.dismiss();
                    RxToast.showToast("合成视频失败");
                    break;
            }
        }
    };
    /**
     * 插入视频线程
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerCR = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 0x123) {
                adapter.notifyDataSetChanged();
            }
        }
    };
    //是否返回播放
    private boolean isPlaying = false;
    static int mFaceBeautyItem = 0; //美颜道具
    static int mEffectItem = 0; //贴纸道具
    static int mGestureItem = 0; //手势道具
    static int[] itemsArray = {mFaceBeautyItem, mEffectItem, mGestureItem};
    final static String TAG = "LogTest";
    private MediaPlayer mediaPlayerMusic;
    private SurfaceTexture videoTexture;// 从视频流捕获帧作为GL的Texture
    /**
     * 生成的真实纹理数组
     */
    private int[] textures = new int[1];
    /**
     * 当前的视频帧是否可以得到
     */
    private boolean frameAvailable = false;
    /**
     * 矩阵来变换纹理坐标
     */
    private float[] videoTextureTransform = new float[16];
    private String mFilterName = "nature";//滤镜名

    private int width;
    private int height;
    private int frameId = 0;
    float mFaceBeautyColorLevel = 0.2f;//美白程度
    float mFaceBeautyBlurLevel = 0.0f;//磨皮
    float mFaceBeautyCheekThin = 0.0f;//控制脸大小
    float mFaceBeautyEnlargeEye = 0.0f;//控制眼睛大小
    float mFaceBeautyRedLevel = 0.5f;//红润程度
    int mFaceShape = 3;
    float mFaceShapeLevel = 0.0f;

    private FullFrameRect mFullScreenFUDisplay;
    private FullFrameRect mFullScreenCamera;
    private int videoPlayerPosition = 0;//视频正在播放哪一个

    private MediaPlayerWrapper mMediaPlayer;//播放视频控制器
    private int IN_RECORDING = 1;
    private int videoWidth;
    private int videoHeight;
    private TextureMovieEncoder mTextureMovieEncoder;
    private int orientation;//是否横屏或者竖屏
    private final int FILTER_ERRER = 0x00001;
    private final int FILTER_SUCCESS = 0x00002;
    private final int VIDEO_SUCCESS = 0x00003;
    private final int VIDEO_ERRER = 0x00004;
    private String videoPathFinish;//合成视频的时候输出路径
    private boolean isFilter = false;//是否合成滤镜
    private int numEMethods = 0;//完成视频时，执行方法数
    private String path;//合成音乐输出路径
    private boolean isMethed = false;//是否执行了其他方法

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editor);
        StatusBarUtil.setImgTransparent(this);
        context = this;
        ButterKnife.bind(this);
        // 首先创建视频文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "MerriChat");
        if (!appDir.exists()) {
            appDir.mkdir();
        } else {
            //FilterUtil.deleteDirWihtFile(appDir);
            //appDir.mkdir();
        }

        initView();
        initTitle();
        initRecyclerView();
        initFilterAdd();
    }

    private void videoPlayPath() {

    }

    private void initRecyclerView() {
        listPath = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = getIntent().getExtras();
            listPath.addAll((ArrayList<String>) bundle.getSerializable("videoList"));
            //拍摄传入的速度
            SPEED_VIDEO = bundle.getFloat("videoSpeed");

            /**
             * 获取第一个视频的宽高为标准传入FrameLayout
             */
            retriever.setDataSource(listPath.get(0));
            // 取得视频的宽高
            videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            orientation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            if (orientation == 90) {//竖屏
                relPlayerView.setAspectRatio(1.0f * videoHeight / videoWidth);
            } else {//横屏
                relPlayerView.setAspectRatio(1.0f * videoWidth / videoHeight);
            }

        }

        videoPlayPath();

        //获取视频长度集合
        listTime = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            retriever.setDataSource(listPath.get(i));
            // 取得视频的长度(单位为毫秒)
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            // 取得视频的长度(单位为秒)
            int videoTimes = Integer.valueOf(time) / 1000;
            listTime.add(videoTimes);
        }

        listBitmap = new ArrayList<>();
        //设置布局管理器s
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new VideoEditorParentAdapter(this, listBitmap);
        recyclerView.setAdapter(adapter);
        /**设置间距*/
        recyclerView.addItemDecoration(new VideoSpaceItemDecoration(this, 30));
        /**长按拖拽操作(备注，这里间距出问题了)*/
        //创建SimpleItemTouchHelperCallback
        //ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        //用Callback构造ItemtouchHelper
        //ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        //touchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(this);

        myTask = new MyTask();
        myTask.execute();

        /**
         * 滑动监听
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.SCROLL_STATE_DRAGGING == newState) {//表示手势滑动
                    isMoveHands = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollTime = scrollTime + dx;
                time = DensityUtils.rounded((float) (scrollTime * 1.0 / DensityUtils.dp2px(VideoEditorActivity.this, 40) * 1.0));

                //判断在哪一个视频上面进行播放视频
                for (int i = 0; i < listTime.size(); i++) {
                    if (i == 0 && time > 0 && time <= listTime.get(i)) {
                        Log.d("LogTest", "10000001===================time:" + (time) + "位置：" + i);
                        selectPosition = i;
                        selectPositionMove = i;
                        return;
                    }
                    if (i > 0 && time > listTime.get(i - 1)) {
                        time = (float) (time - (listTime.get(i - 1) + 0.4));
                        if (time <= listTime.get(i)) {
                            Log.d("LogTest", "10000002===================time:" + time + "位置：" + i);
                            selectPosition = i;
                            selectPositionMove = i;
                        }
                    }
                }

                if (isMoveHands) {
                    //Log.d("LogTest", "time:" + time);

                }
            }
        });
    }

    private void initTitle() {
        tvTitle.setText("编辑视频");
    }

    /**
     * 添加滤镜图片
     */
    private void initFilterAdd() {
        filterList = new ArrayList<>();
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_nature, "nature"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_delta, "delta"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_electric, "electric"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_slowlived, "slowlived"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_tokyo, "tokyo"));
        filterList.add(new PhotoFilmPicModel(R.mipmap.icon_fiter_warm, "warm"));

        spaceItemDecoration = new SpaceItemDecorations(20, filterList.size());
        //recyclerViewFilter.removeItemDecoration(spaceItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFilter.setLayoutManager(layoutManager);
        VideoEditorFilterAdapter filterAdapter = new VideoEditorFilterAdapter(VideoEditorActivity.this, filterList);
        recyclerViewFilter.setAdapter(filterAdapter);
        //设置ITEM间距
        recyclerViewFilter.addItemDecoration(spaceItemDecoration);
        filterAdapter.setOnFilterItemClickListener(this);
    }

    private void initView() {
        //初始化GLSurfaceView
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        //glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //获取视频时长的对象
        retriever = new MediaMetadataRetriever();

        //默认选中剪裁
        radioCut.setChecked(true);

        /**
         * 滤镜
         */
        radioFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    recyclerViewFilter.setVisibility(View.VISIBLE);
                    linCut.setVisibility(View.GONE);
                }
            }
        });

        /**
         * 剪切
         */
        radioCut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    recyclerViewFilter.setVisibility(View.GONE);
                    linCut.setVisibility(View.VISIBLE);
                }
            }
        });

        videoPosition = selectPosition;
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.tv_left, R.id.tv_right, R.id.radio_more, R.id.radio_music, R.id.radio_special, R.id.btn_spilt, R.id.btn_cancel,
            R.id.btn_insert, R.id.btn_upend, R.id.btn_delete, R.id.btn_copy, R.id.glSurfaceView, R.id.video_play})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left://取消
                finish();
                break;
            case R.id.tv_right://完成
//                if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
//                    mTextureMovieEncoder.stopRecording();
//                    startActivity(new Intent(VideoEditorActivity.this, VideoActivity.class).putExtra("videoPath", "" + videoFileName));
//                }
                //暂停视频
                mMediaPlayer.pause();
                if (mediaPlayerMusic != null) {
                    mediaPlayerMusic.pause();
                }
                numProgress = 0;//进度条初始化为0

                onPopShow();
                //videoFilter();
                break;
            case R.id.radio_music://音乐
                Intent intent = new Intent(VideoEditorActivity.this, VideoMusicActivity.class);
                startActivityForResult(intent, REQUEST_CODE_VIDEO_MUSIC);
                break;
            case R.id.radio_special://特效
                startActivityForResult(new Intent(VideoEditorActivity.this, VideoSpecialActivity.class).putExtra("VideoPath", listPath.get(selectPosition)), REQUEST_CODE_VIDEO_SPEED);
                break;

            case R.id.radio_more://更多
                startActivityForResult(new Intent(VideoEditorActivity.this, VideoMoreActivity.class).putExtra("VideoPath", listPath.get(selectPosition)), REQUEST_CODE_VIDEO_COVER);
                break;
            case R.id.btn_spilt://拆分
                if (selectPosition != -1 && time > 0) {
                    if (selectPosition == selectPositionMove) {//判断是否拆分选中的视频
                        //拆分视频的执行方法
                        cutVideo(listPath.get(selectPosition));
                    } else {
                        RxToast.showToast("拆分您选中的视频哦");
                    }
                } else {
                    RxToast.showToast("请选择您要操作的视频哦");
                }
                break;
            case R.id.btn_cancel://撤销
                RxToast.showToast("撤销功能敬请期待");
                break;
            case R.id.btn_insert://插入
                //RxToast.showToast(selectPosition + "当前位置" + time);
                Matisse.from(VideoEditorActivity.this)
                        .choose(MimeType.ofVideo(), false)
                        .capture(true)
                        .captureStrategy(
                                new CaptureStrategy(true, MerriApp.getFileProviderName(this)))
                        .maxSelectable(1)
                        .gridExpectedSize(
                                getResources().getDimensionPixelSize(R.dimen.dp120))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .theme(R.style.Matisse_MerriChat)

                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_VIDEO_CHOOSE);
                break;
            case R.id.btn_copy://复制
                if (listPath.size() > 0) {
                    listPath.add(selectPosition + 1, listPath.get(selectPosition));
                    listTime.add(selectPosition + 1, listTime.get(selectPosition));
                    adapter.addData(listBitmap.get(selectPosition), selectPosition + 1);
                }
                break;
            case R.id.btn_upend://倒放
                dialog_load = new ProgressDialog(VideoEditorActivity.this);
                dialog_load.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_load.setCanceledOnTouchOutside(false);
                dialog_load.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog_load.setMessage("正在倒放中(请勿关闭)...");
                dialog_load.show();
                //倒序(倒序之前一定要加这三个方法 不然不能成功)
                checkPermissions();
                createReversifyDirectory();
                loadFFmpegBinary();
                //视频倒序执行方法
                videoInverted(listPath.get(selectPosition));

                break;
            case R.id.btn_delete://删除
                if (listPath.size() > 0) {
                    listPath.remove(selectPosition);
                    adapter.removeData(selectPosition);
                    selectPosition = selectPosition - 1;
                    if (listPath.size() == 0) {
                        finish();
                    }
                }
                break;
            case R.id.glSurfaceView:
                //播放
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    videoPlay.setVisibility(View.GONE);
                } else if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    videoPlay.setVisibility(View.VISIBLE);
                }
                if (mediaPlayerMusic != null && !mediaPlayerMusic.isPlaying()) {
                    mediaPlayerMusic.start();
                } else if (mediaPlayerMusic != null && mediaPlayerMusic.isPlaying()) {
                    mediaPlayerMusic.pause();
                }
                break;
            case R.id.video_play:
                //播放
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    videoPlay.setVisibility(View.GONE);
                }
                if (mediaPlayerMusic != null && !mediaPlayerMusic.isPlaying()) {
                    mediaPlayerMusic.start();
                }
                break;
        }
    }

    /**
     * 合成进度
     */
    private void onPopShow() {
        numProgress = 0;
        //合成视频包括四种情况（多个视频合成、合成音乐、合成速度、合成滤镜）
        if (listPath.size() == 1 && !TextUtils.isEmpty(musicPath) && isVideoSpeed && isFilter) {
            numEMethods = 4;
        } else if ((listPath.size() > 1 && !TextUtils.isEmpty(musicPath) && isVideoSpeed && !isFilter)
                || (listPath.size() > 1 && !TextUtils.isEmpty(musicPath) && !isVideoSpeed && isFilter)
                || (listPath.size() > 1 && TextUtils.isEmpty(musicPath) && isVideoSpeed && isFilter)
                || (listPath.size() == 1 && !TextUtils.isEmpty(musicPath) && isVideoSpeed && isFilter)) {
            numEMethods = 3;
        } else if ((listPath.size() == 1 && TextUtils.isEmpty(musicPath) && isVideoSpeed && isFilter)
                || (listPath.size() == 1 && !TextUtils.isEmpty(musicPath) && !isVideoSpeed && isFilter)
                || (listPath.size() == 1 && !TextUtils.isEmpty(musicPath) && isVideoSpeed && !isFilter)
                || (listPath.size() > 1 && !TextUtils.isEmpty(musicPath) && !isVideoSpeed && !isFilter)
                || (listPath.size() > 1 && TextUtils.isEmpty(musicPath) && isVideoSpeed && !isFilter)
                || (listPath.size() > 1 && TextUtils.isEmpty(musicPath) && !isVideoSpeed && isFilter)) {
            numEMethods = 2;
        } else {
            numEMethods = 1;
        }
        popVideo = new PopVideo((Activity) context);
        progress = (ProgressBar) popVideo.getContentView().findViewById(R.id.progress_video);
        tvProgress = (TextView) popVideo.getContentView().findViewById(R.id.textView_video);
        popVideo.showAtLocation(relGroup, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        /**
         * 视频变速 这三个方法必须先执行
         */
        checkPermissions();
        createReversifyDirectory();
        loadFFmpegBinary();

        if (listPath.size() == 1 && musicPath.equals("")) {
            if (isVideoSpeed) {
                //执行视频变速
                videoSpeed(listPath.get(0));
            } else if (!isVideoSpeed && isFilter) {
                //执行滤镜
                videoFilter(listPath.get(0));
            } else {
                releasePath = listPath.get(0);
                onVideoIntent();
            }
        } else if (listPath.size() == 1 && !musicPath.equals("") && !isVideoSpeed) {
            releasePath = listPath.get(0);
            //isMethed = true;
            //执行合成音乐的方法
            onVideoMusic(listPath.get(0), musicPath, videoVolume, musicVolume);
        } else if (listPath.size() == 1 && !musicPath.equals("") && isVideoSpeed) {
            releasePath = listPath.get(0);
            isMethed = true;
            //执行视频变速
            videoSpeed(releasePath);
        } else {
            isMethed = true;
            //执行合成视频的方法
            onVideoFinish();
        }
    }

    /**
     * 合成视频（多个视频合成）
     */
    private void onVideoFinish() {
        ArrayList<EpVideo> epVideos = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            epVideos.add(new EpVideo(listPath.get(i)));//视频
            Log.d("LogTest", listPath.get(i));
        }
        // 首先创建视频文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "MerriChat");
        boolean isFound = false;
        if (!appDir.exists()) {
            isFound = appDir.mkdir();
        }

        if (isFound || appDir.exists()) {
            videoPathFinish = FileUtils.createVideoFileName();
            //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
            EpEditor.OutputOption outputOption = new EpEditor.OutputOption(videoPathFinish);
            if (orientation == 90) {//竖屏
                outputOption.setWidth(videoHeight);//输出视频宽，默认480
                outputOption.setHeight(videoWidth);//输出视频高度,默认360
            } else {//横屏
                outputOption.setWidth(videoWidth);//输出视频宽，默认480
                outputOption.setHeight(videoHeight);//输出视频高度,默认360
            }
            outputOption.frameRate = 30;//输出视频帧率,默认30
            outputOption.bitRate = 10;//输出视频码率,默认10

            Log.d("LogTest", "合成视频路径：" + videoPathFinish);
            EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    handler.sendEmptyMessage(VIDEO_SUCCESS);
                }

                @Override
                public void onFailure() {
                    handler.sendEmptyMessage(VIDEO_ERRER);
                }

                @Override
                public void onProgress(float progress) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = (int) (progress * 100 / numEMethods);
                    handlerFinish.sendMessage(msg);
                }
            });
        }
    }

    /**
     * 视频变速
     *
     * @param videoPath
     */
    private void videoSpeed(final String videoPath) {
        isChangMethes = true;

        videoFileName = FileUtils.createVideoFileName();
        //参数分别是视频路径,输出路径,变速倍率（仅支持0.25-4倍),变速类型(VIDEO-视频(选择VIDEO的话则会屏蔽音频),AUDIO-音频,ALL-视频音频同时变速)
        EpEditor.changePTS(videoPath, videoFileName, SPEED_VIDEO, EpEditor.PTS.ALL, new OnEditorListener() {
            @Override
            public void onSuccess() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(videoPath);
                }
                handler.sendEmptyMessage(101);
            }

            @Override
            public void onFailure() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(videoPath);
                }
                handler.sendEmptyMessage(100);
            }

            @Override
            public void onProgress(float progress) {
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = (int) (progress * 100 / numEMethods);
                handlerFinish.sendMessage(msg);
            }
        });
//        String executableCmd;
//
//        executableCmd = "-i " + videoPath + " -filter_complex " + "[0:v]setpts=" + SPEED_VIDEO + "*PTS[v];[0:a]atempo=" + SPEED_MUSIC + "[a] " + "-map [v] -map [a] -preset ultrafast " + videoFileName + " -y -threads 2";
//
//        command = executableCmd.split(" ");
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        executeFFmpegSpeed(videoFileName,videoPath);
//                    }
//                }
//        ).start();
    }

//    /**
//     * 执行变速的方法（执行FFMPeg命令的方法）
//     */
//    private void executeFFmpegSpeed(final String fileName, final String videoFileName) {
//        releasePath = videoFileName;
//        ffmpeg = FFmpeg.getInstance(getApplicationContext());
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onSuccess(String message) {
//                    //删除文件
//                    if (isMethed) {
//                        FileUtils.delFile(fileName);
//                    }
//                    handler.sendEmptyMessage(101);
////                    String path = Environment.getExternalStorageDirectory() + "/MerriChat/" + videoFileName;
////                    releasePath = path;
////                    onVideoIntent();
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Message msg = new Message();
//                    msg.what = 100;
//                    msg.obj = message;
//                    handlerFinish.sendMessage(msg);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    //删除文件
//                    if (isMethed) {
//                        FileUtils.delFile(fileName);
//                    }
//                    handler.sendEmptyMessage(100);
//                }
//
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            dialog_load.dismiss();
//            RxToast.showToast("变速失败");
//            Log.d("LogTest", "变速失败exception：" + e.getMessage());
//        }
//    }

    /**
     * 合成音乐
     *
     * @param videoPath   //视频路径
     * @param musicPath   //音乐路径
     * @param videoVolume //视频音量
     * @param musicVolume //音乐音量
     */
    private void onVideoMusic(final String videoPath, String musicPath, float videoVolume, float musicVolume) {
        isChangMethes = true;

        //合成之后的视频路径
        path = FileUtils.createVideoFileName();

        //参数分别是视频路径，音频路径，输出路径,原始视频音量(1为100%,0.7为70%,以此类推),添加音频音量
        Log.d("LogTest", videoPath + "|||" + musicPath + "|||" + path + "|||" + videoVolume + "|||" + musicVolume);
        EpEditor.music(videoPath, musicPath, path, videoVolume, musicVolume, new OnEditorListener() {
            @Override
            public void onSuccess() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(videoPath);
                }
                handler.sendEmptyMessage(103);
            }

            @Override
            public void onFailure() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(videoPath);
                }
                handler.sendEmptyMessage(102);
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = (int) (progress * 100 / numEMethods);
                handlerFinish.sendMessage(msg);
            }
        });
    }

    /**
     * 跳转到发布视频
     */
    private void onVideoIntent() {
        popVideo.dismiss();
        if (bitmapCover == null) {
            //压缩图片
            ImageUtil.saveBitmapToFile(listBitmap.get(0).get(0), listBitmap.get(0).get(0).getWidth(), listBitmap.get(0).get(0).getHeight(), new ImageUtil.SavePhotoCompletedCallBack() {
                @Override
                public void onCompleted(final String path) {
                    bitmapCover = path;
                    handler.sendEmptyMessage(0x1234);
                }
            });
        }
    }

    /**
     * 剪辑点击(帧图片的点击事件)
     *
     * @param position
     * @param v
     */
    @Override
    public void onItemClick(int position, View v) {
        //selectPosition = position;
        //RxToast.showToast("选中" + position);
    }

    /**
     * 滤镜点击
     *
     * @param position
     * @param v
     */
    @Override
    public void onFilterItemClick(int position, View v) {
        mFilterName = filterList.get(position).getCutName();
        if (position == 0) {
            isFilter = false;
        } else {
            isFilter = true;
        }
    }

    /**
     * 分割视频 1秒1帧
     *
     * @param dataPath
     */
    public void getBitmapsFromVideo(String dataPath) {
        retriever.setDataSource(dataPath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        videoTime = Integer.valueOf(time) / 1000;

        list = new ArrayList<>();
        listBitmap.add(list);
        // 得到每一秒时刻的bitmap比如第一秒,第二秒
        for (int i = 1; i <= videoTime; i++) {
            bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            list.add(bitmap);
            new Thread() {
                public void run() {
                    handler.sendEmptyMessage(0x123);
                }
            }.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                videoPlay.setVisibility(View.GONE);
            }
            if (mediaPlayerMusic != null && !mediaPlayerMusic.isPlaying()) {
                mediaPlayerMusic.start();
            }
            videoPlay.setVisibility(View.GONE);
            isPlaying = false;
        }
        //glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //如果异步任务不为空 并且状态是 运行时，就把他取消这个加载任务
        if (myTask != null && myTask.getStatus() == AsyncTask.Status.RUNNING) {
            myTask.cancel(true);
        }
        videoPlay.setVisibility(View.VISIBLE);
        //暂停
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            videoPlay.setVisibility(View.VISIBLE);
        }
        if (mediaPlayerMusic != null && mediaPlayerMusic.isPlaying()) {
            mediaPlayerMusic.pause();
        }
        isPlaying = true;
        //glSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.stop();
            mediaPlayerMusic = null;
        }
    }

    /******************************************************以下是倒序执行方法**************************************************/
    /**
     * 视频倒序
     *
     * @param videoPath
     */
    private void videoInverted(String videoPath) {
        String fileName[] = videoPath.split(File.separator);
        videoFileName = fileName[fileName.length - 1];
        //视频倒序命令
        String executableCmd = "-i " + videoPath + " -vf reverse -af areverse -preset ultrafast " + Environment.getExternalStorageDirectory().getPath() + File.separator + "MerriChat" + File.separator + videoFileName + " -y -threads 2";
        command = executableCmd.split(" ");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        executeFFmpegCommands();
                    }
                }
        ).start();
    }

    private void executeFFmpegCommands() {
//        about the tec guide docs
//        http://writingminds.github.io/ffmpeg-android-java/docs/
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    dialog_load.dismiss();
                    RxToast.showToast("倒序成功");
                    listPath.set(selectPosition, Environment.getExternalStorageDirectory() + "/MerriChat/" + videoFileName);
                    //对集合图片倒序
                    Collections.reverse(listBitmap.get(selectPosition));
                    adapter.notifyDataSetChanged();
                    //播放
                    videoPlayPath();
                }

                @Override
                public void onProgress(String message) {
                    Log.d("LogTest", "视频倒序" + message);
                }

                @Override
                public void onFailure(String message) {
                    dialog_load.dismiss();
                    RxToast.showToast("倒放失败");
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            RxToast.showToast("倒放失败");
            dialog_load.dismiss();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Storage Permissions not allowed, app might not work!", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        }
    }

    private void createReversifyDirectory() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

        } else {
            Runnable directoryCreateRunnable = new Runnable() {
                @Override
                public void run() {
                    boolean isDirectoryCreated = false;
                    while (!isDirectoryCreated) {
                        isDirectoryCreated = new File(Environment.getExternalStorageDirectory() + File.separator + "Reversify").mkdirs();
                    }

                }
            };
            Thread directoryCreateThread = new Thread(directoryCreateRunnable);
            directoryCreateThread.start();
        }
    }

    private void loadFFmpegBinary() {
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, "FFmpeg is not supported by this device!", Toast.LENGTH_SHORT).show();
        }
    }

    /******************************************************以下是拆分执行方法**************************************************/

    private void cutVideo(final String videoPath) {
        if (time > 0 && time < listTime.get(selectPosition)) {
            /**
             * 截取第一段
             */
            EpVideo epVideo = new EpVideo(videoPath);
            epVideo.clip(0, time);
            //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
            path1 = FileUtils.createVideoFileName();
            EpEditor.exec(epVideo, new EpEditor.OutputOption(path1), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //RxToast.showToast("拆分视频成功");
                    handlerUIWH.sendEmptyMessage(4);
                }

                @Override
                public void onFailure() {
                    handlerUIWH.sendEmptyMessage(6);
                }

                @Override
                public void onProgress(float progress) {
                    //这里获取处理进度
                }
            });

        } else {
            RxToast.showToast("拆分视频时间太短");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VIDEO_CHOOSE) {//插入视频
                //选取的视频
                listCRPath = new ArrayList<>();
                listCRPath.addAll(Matisse.obtainPathResult(data));
                //先拆分 再插入视频
                if (selectPosition != -1 && time > 0 && time < listTime.get(selectPosition)) {
                    crPosition = 1;
                    isCRVideo = true;
                    //拆分视频的执行方法
                    cutVideo(listPath.get(selectPosition));
                } else if (time >= listTime.get(selectPosition)) {//无需拆分  直接插入视频
                    //在选中视频右侧
                    crPosition = 1;
                    listPath.addAll(selectPosition + 1, listCRPath);

                    GLES20.glViewport(0, 0, this.width, this.height);
                    playVideo();
                    new MyTaskCR().execute();
                } else {
                    //在选中视频左侧
                    crPosition = 0;
                    listPath.addAll(selectPosition, listCRPath);

                    GLES20.glViewport(0, 0, this.width, this.height);
                    playVideo();
                    new MyTaskCR().execute();
                }
            } else if (requestCode == REQUEST_CODE_VIDEO_SPEED) {//速度
                isVideoSpeed = true;
                SPEED_VIDEO = data.getFloatExtra("VideoSpeed", 0);
            } else if (requestCode == REQUEST_CODE_VIDEO_COVER) {//封面
                bitmapCover = data.getStringExtra("Cover");//图片
                videoVolume = data.getFloatExtra("VideoVolume", 0);
                musicVolume = data.getFloatExtra("MusicVolume", 0);
            } else if (requestCode == REQUEST_CODE_VIDEO_MUSIC) {//音乐
                musicPath = data.getStringExtra("VideoMusic");
                playMusic();
            }
        }
        radioCut.setChecked(true);
    }

    /**
     * 分割视频 1秒1帧
     *
     * @param dataPath
     */
    public void getBitmapsFromVideoCR(String dataPath, int position) {
        retriever.setDataSource(dataPath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        videoTime = Integer.valueOf(time) / 1000;
        listTime.add(selectPosition, videoTime);
        list = new ArrayList<>();
        listBitmap.add(position, list);
        // 得到每一秒时刻的bitmap比如第一秒,第二秒
        for (int i = 1; i <= videoTime; i++) {
            bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            list.add(bitmap);
            new Thread() {
                public void run() {
                    handlerCR.sendEmptyMessage(0x123);
                }
            }.start();
        }
    }

    /**
     * 异步分割图片
     */
    class MyTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isCancelled()) {

            } else {
                getBitmapsFromVideo(listPath.get(position));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //adapter.notifyDataSetChanged();
            if (position != listPath.size() - 1) {
                position++;

                myTask = new MyTask();
                myTask.execute();
            }
            super.onPostExecute(aBoolean);
        }

    }

    /**
     * 异步分割图片
     */
    class MyTaskCR extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isCancelled()) {

            } else {
                getBitmapsFromVideoCR(listCRPath.get(0), selectPosition + crPosition);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            isCRVideo = false;
            //adapter.notifyDataSetChanged();
            super.onPostExecute(aBoolean);
        }
    }

    public void destroySurfaceTexture() {
        if (videoTexture != null) {
            videoTexture.release();
            videoTexture = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handlerUIWH = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    break;
                case 2://播放按钮隐藏
                    videoPlay.setVisibility(View.GONE);
                    break;
                case 3://播放按钮显示
                    videoPlay.setVisibility(View.VISIBLE);
                    break;
                case 4://拆分视频成功第一段
                    cutVideo2();
                    break;
                case 5://拆分视频成功第二段
                    final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path2);
                    // 取得视频的长度(单位为秒)
                    times2 = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;

                    listSelect = listBitmap.get(selectPosition);
                    list1 = new ArrayList<>();
                    list2 = new ArrayList<>();

                    for (int i = 0; i < listSelect.size(); i++) {
                        if (i < time) {
                            list1.add(listSelect.get(i));
                        } else {
                            list2.add(listSelect.get(i));
                        }
                    }
                    //模拟中间切
                            /*Bitmap bitmap1 = Bitmap.createBitmap(listBitmap.get(0).get(1), listBitmap.get(0).get(1).getWidth() / 2, 0, listBitmap.get(0).get(1).getWidth() / 2, listBitmap.get(0).get(1).getHeight());
                            list1.add(bitmap1);

                            Bitmap bitmap2 = Bitmap.createBitmap(listBitmap.get(0).get(1), listBitmap.get(0).get(1).getWidth() / 2, 0, listBitmap.get(0).get(1).getWidth() / 2, listBitmap.get(0).get(1).getHeight());
                            list2.add(0,bitmap2);*/

                    //需处理 视频拆分  视频路径  视频时长
                    listBitmap.remove(listSelect);
                    listBitmap.add(selectPosition, list1);
                    listBitmap.add(selectPosition + 1, list2);

                    listPath.remove(selectPosition);
                    listPath.add(selectPosition, path1);
                    listPath.add(selectPosition + 1, path2);

                    listTime.remove(selectPosition);
                    listTime.add(selectPosition, times1);
                    listTime.add(selectPosition + 1, times2);

                    adapter.notifyDataSetChanged();
                    if (isCRVideo) {
                        new MyTaskCR().execute();
                    }
                    break;
                case 6:
                    RxToast.showToast("拆分视频失败");
                    break;
            }
        }
    };

    private void cutVideo2() {
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path1);
        // 取得视频的长度(单位为秒)
        times1 = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;

        /**
         * 截取第二段
         */
        EpVideo epVideo1 = new EpVideo(listPath.get(selectPosition));
        epVideo1.clip(time, (listTime.get(selectPosition) - time));
        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        path2 = FileUtils.createVideoFileName();
        EpEditor.exec(epVideo1, new EpEditor.OutputOption(path2), new OnEditorListener() {
            @Override
            public void onSuccess() {
                //RxToast.showToast("拆分视频成功");
                handlerUIWH.sendEmptyMessage(5);
            }

            @Override
            public void onFailure() {
                handlerUIWH.sendEmptyMessage(6);
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
            }
        });
    }

    /**
     * GLSurfaceView渲染器
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                Texture2dProgram.ProgramType.TEXTURE_2D));
        mFullScreenCamera = new FullFrameRect(new Texture2dProgram(
                Texture2dProgram.ProgramType.TEXTURE_EXT));
        textures[0] = mFullScreenCamera.createTextureObject();

        if (videoTexture != null) {
            faceunity.fuOnCameraChange();
            destroySurfaceTexture();
        }

        videoTexture = new SurfaceTexture(textures[0]);
        videoTexture.setOnFrameAvailableListener(this);

        //创建美颜（滤镜）道具
        InputStream is = null;
        try {
            is = getAssets().open("v3.mp3");
            byte[] v3data = new byte[is.available()];
            is.read(v3data);
            is.close();
            faceunity.fuSetup(v3data, null, authpack.A());
            //faceunity.fuSetMaxFaces(1);//设置最大识别人脸数目

            is = getAssets().open("face_beautification.mp3");
            byte[] itemData = new byte[is.available()];
            is.read(itemData);
            is.close();
            mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
            itemsArray[0] = mFaceBeautyItem;
        } catch (IOException e) {
            Log.d("LogTest", "" + e.getMessage());
        }
        //创建播放视频控制器
        playVideo();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, this.width, this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //清除屏幕缓存和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        synchronized (this) {
            if (frameAvailable) {
                //更新Texture
                videoTexture.updateTexImage();
                videoTexture.getTransformMatrix(videoTextureTransform);
                frameAvailable = false;
            }
        }
        //绘制滤镜
        faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
        faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
        faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);
        //将处理的texture绘制到GLSurfaceView上浏览播放
        int fuTex = faceunity.fuBeautifyImage(textures[0], faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE, width, height, frameId++, new int[]{mEffectItem, mFaceBeautyItem});
        if (mFullScreenFUDisplay != null)
            mFullScreenFUDisplay.drawFrame(fuTex, videoTextureTransform);
        else throw new RuntimeException("HOW COULD IT HAPPEN!!! mFullScreenFUDisplay is null!!!");

        //下面方法类似于相机录像保存成文件  这里不合适
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(START_RECORDING)) {
            //保存视频输入路径
            videoFileName = FileUtils.createVideoFileName();
            File outFile = new File(videoFileName);
            if (orientation == 90) {//竖屏
                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, videoHeight, videoWidth,
                        3000000, EGL14.eglGetCurrentContext(), videoTexture.getTimestamp()
                ));
            } else {
                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, videoWidth, videoHeight,
                        3000000, EGL14.eglGetCurrentContext(), videoTexture.getTimestamp()
                ));
            }

            //forbid click until start or stop success
            mTextureMovieEncoder.setOnEncoderStatusUpdateListener(new TextureMovieEncoder.OnEncoderStatusUpdateListener() {
                @Override
                public void onStartSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "start encoder success");
                        }
                    });
                }

                @Override
                public void onStopSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "stop encoder success");
                        }
                    });
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("LogTest", "video file saved to :" + videoFileName);
                    //onPopShow();
                }
            });
        }

        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
            mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, videoTextureTransform);
            mTextureMovieEncoder.frameAvailable(videoTexture);
        }

        gl.glFinish();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            frameAvailable = true;
        }
    }

    /**
     * 创建循环视频播放控制器
     */
    private void playVideo() {
        mMediaPlayer = new MediaPlayerWrapper();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setDataSource(listPath);

        Surface surface = new Surface(videoTexture);
        mMediaPlayer.setSurface(surface);
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        //mTextureMovieEncoder = new TextureMovieEncoder();
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        mediaPlayerMusic = new MediaPlayer();
        try {
            mediaPlayerMusic.setDataSource(musicPath);
            mediaPlayerMusic.prepare();
            mediaPlayerMusic.setLooping(true);//是否循环播放
            mediaPlayerMusic.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoPrepare() {

    }

    @Override
    public void onVideoStart() {
        handlerUIWH.sendEmptyMessage(2);
    }

    @Override
    public void onVideoPause() {
        handlerUIWH.sendEmptyMessage(3);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.seekTo(0);
            mediaPlayerMusic.start();
        }
    }

    @Override
    public void onVideoChanged(VideoInfo info) {

    }

    /**
     * 视频滤镜
     *
     * @param spath
     */
    private void videoFilter(final String spath) {
        isChangMethes = true;
        releasePath = FileUtils.createVideoFileName();
        //滤镜命令（底片效果）
        //String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
        //String filter = "lutyuv=y=1:u=1:v=1";
        String filter = "ffplay -vf frei0r=sobel";//freiOr 参考 http://www.cnblogs.com/nlsoft/archive/2013/05/07/3065311.html
        EpVideo epVideo = new EpVideo(spath);
        epVideo.addFilter(filter);
        //输出选项，参数为输出文件路径(目前仅支持mp4格式输出)
        EpEditor.OutputOption outputOption = new EpEditor.OutputOption(releasePath);
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(spath);
                }
                handler.sendEmptyMessage(FILTER_SUCCESS);
            }

            @Override
            public void onFailure() {
                //删除文件
                if (isMethed) {
                    FileUtils.delFile(spath);
                }
                handler.sendEmptyMessage(FILTER_ERRER);
            }

            @Override
            public void onProgress(float progress) {
                //这里获取处理进度
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = (int) (progress * 100 / numEMethods);
                handlerFinish.sendMessage(msg);
            }
        });
    }
}