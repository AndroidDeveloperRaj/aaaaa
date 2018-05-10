package com.merrichat.net.activity.video.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.CoverAdapter;
import com.merrichat.net.utils.ImageUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CircularProgressView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更多
 * Created by amssy on 17/11/6.
 */

public class VideoMoreActivity extends BaseActivity implements CoverAdapter.OnItemClickListener {
    /**
     * 播放器
     */
    @BindView(R.id.videoView)
    VideoView videoView;
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
     * 播放器外层布局
     */
    @BindView(R.id.rel_videoView)
    RelativeLayout relVideoView;
    /**
     * 功能板
     */
    @BindView(R.id.lin_function)
    LinearLayout linFunction;
    /**
     * 贴纸
     */
    @BindView(R.id.radio_sticker)
    RadioButton radioSticker;
    /**
     * 字幕
     */
    @BindView(R.id.radio_subtitle)
    RadioButton radioSubtitle;
    /**
     * 音量
     */
    @BindView(R.id.radio_volume)
    RadioButton radioVolume;
    /**
     * 封面
     */
    @BindView(R.id.radio_cover)
    RadioButton radioCover;
    /**
     * 视频原声
     */
    @BindView(R.id.seekBar_normal)
    SeekBar seekBarNormal;
    /**
     * 音乐声音
     */
    @BindView(R.id.seekBar_music)
    SeekBar seekBarMusic;
    /**
     * 进度条
     */
    @BindView(R.id.progressView)
    CircularProgressView progressView;
    /**
     * 贴纸
     */
    @BindView(R.id.lin_sticker)
    LinearLayout linSticker;
    /**
     * 字幕
     */
    @BindView(R.id.lin_subtitle)
    LinearLayout linSubtitle;
    /**
     * 音量
     */
    @BindView(R.id.lin_volume)
    LinearLayout linVolume;
    /**
     * 显示封面图
     */
    @BindView(R.id.imageView_cover)
    ImageView imageViewCover;
    /**
     * 封面图列表
     */
    @BindView(R.id.recyclerView_cover)
    RecyclerView recyclerViewCover;
    @BindView(R.id.lin_cover)
    LinearLayout linCover;

    private String videoPath = "";
    //private String musicPath = "http://sc1.111ttt.com/2017/4/05/10/298101104389.mp3";
    ///storage/emulated/0/MIUI/music/mp3/男人花_黄勇.mp3
    private String musicPath = "";
    private MediaPlayer mediaPlayer;//播放音乐
    private float videoVolume = 0.5f;//视频音量(默认设置)
    private float musicVolume = 0.5f;//音乐音量(默认设置)
    private int videoTime;//视频长度
    private Bitmap bitmap;//取得的图片
    private boolean isBitmap = false;//是否取帧图片
    private List<Bitmap> list;
    private int finalI;
    private CoverAdapter coverAdapter;
    private int selectPosition = 0;
    private String name = "";
    private boolean isCover = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        initView();
        initTitle();
    }

    private void initTitle() {
        tvClose.setText("返回");
        tvTitle.setText("更多");
    }

    private void initView() {
        isBitmap = false;
        //默认选中第一个
        radioVolume.setChecked(true);
        linVolume.setVisibility(View.VISIBLE);

        videoView.setZOrderOnTop(true);

        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getStringExtra("VideoPath");
            onVideoViewPath(videoPath);
            //new MyTask().execute();
            onStarts();
        }

        list = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCover.setLayoutManager(linearLayoutManager);
        int width = (StringUtil.getWidths(this) - StringUtil.dip2px(this, 50)) / 8;
        coverAdapter = new CoverAdapter(this, list, width);
        recyclerViewCover.setAdapter(coverAdapter);
        coverAdapter.setOnItemClickListener(this);

        /**
         * 播放结束监听
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //音乐重置
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                }
                //播放结束后的动作
                onStarts();
            }
        });

        /**
         * 调节视频音量
         */
        seekBarNormal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoVolume = (float) (progress * 1.0 / 100);
                setVolume(videoVolume, videoView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 调节音乐音量
         */
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    musicVolume = (float) (progress * 1.0 / 100);
                    mediaPlayer.setVolume(musicVolume, (float) (progress * 1.0 / 100));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.tv_left, R.id.tv_right, R.id.radio_sticker, R.id.radio_subtitle, R.id.radio_volume, R.id.radio_cover})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left://取消
                showExitScreenDialog(VideoMoreActivity.this);
                break;
            case R.id.tv_right://完成
                if (isCover) {
                    //压缩图片
                    ImageUtil.saveBitmapToFile(list.get(selectPosition), list.get(selectPosition).getWidth(), list.get(selectPosition).getHeight(), new ImageUtil.SavePhotoCompletedCallBack() {
                        @Override
                        public void onCompleted(final String path) {
                            name = path;
                            handler.sendEmptyMessage(1);
                        }
                    });
                }
                Intent intent = new Intent();
                intent.putExtra("Cover", name);
                intent.putExtra("VideoVolume", "" + videoVolume);
                intent.putExtra("MusicVolume", "" + musicVolume);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.radio_sticker://贴纸
                linSticker.setVisibility(View.VISIBLE);
                linSubtitle.setVisibility(View.GONE);
                linVolume.setVisibility(View.GONE);
                linCover.setVisibility(View.GONE);
                break;
            case R.id.radio_subtitle://字幕
                linSticker.setVisibility(View.GONE);
                linSubtitle.setVisibility(View.VISIBLE);
                linVolume.setVisibility(View.GONE);
                linCover.setVisibility(View.GONE);
                break;
            case R.id.radio_volume://音量
                //继续播放
                if (mediaPlayer != null) {
                    //onMusicStart();
                }
                if (!videoView.isPlaying()) {
                    onStarts();
                }
                videoView.setVisibility(View.VISIBLE);
                imageViewCover.setVisibility(View.GONE);
                linSticker.setVisibility(View.GONE);
                linSubtitle.setVisibility(View.GONE);
                linVolume.setVisibility(View.VISIBLE);
                linCover.setVisibility(View.GONE);
                break;
            case R.id.radio_cover://封面
                //是否已经获取了图片
                if (!isBitmap) {
                    new MyTaskImage().execute();
                }
                //暂停播放
                if (mediaPlayer != null) {
                    onMusicPause();
                }
                if (videoView.isPlaying()) {
                    onPauses();
                }
                videoView.setVisibility(View.GONE);
                imageViewCover.setVisibility(View.VISIBLE);
                linSticker.setVisibility(View.GONE);
                linSubtitle.setVisibility(View.GONE);
                linVolume.setVisibility(View.GONE);
                linCover.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showExitScreenDialog(Context context) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content("是否放弃当前操作?")//
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

    /**
     * 播放视频路径
     *
     * @param path
     */
    public void onVideoViewPath(String path) {
        this.videoView.setVideoPath(path);
    }

    /**
     * 开始播放视频
     */
    public void onStarts() {
        this.videoView.start();
        super.onStart();
    }

    /**
     * 暂停播放视频
     */
    public void onPauses() {
        this.videoView.pause();
        super.onPause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            onPauses();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * @param volume 音量大小
     * @param object VideoView实例
     */
    public void setVolume(float volume, Object object) {
        try {
            Class<?> forName = Class.forName("android.widget.VideoView");
            Field field = forName.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            MediaPlayer mMediaPlayer = (MediaPlayer) field.get(object);
            mMediaPlayer.setVolume(volume, volume);
        } catch (Exception e) {

        }
    }

    private void setMusicPlayer(String musicPath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(musicPath);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);//是否循环播放
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音乐开始播放
     */
    private void onMusicStart() {
        if (musicPath != "" && musicPath != null) {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }

    /**
     * 音乐暂停
     */
    private void onMusicPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    /**
     * 音乐结束
     */
    private void onMusicStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onItemClick(int position) {
        isCover = true;
        selectPosition = position;
        imageViewCover.setImageBitmap(list.get(position));
    }

    /**
     * 异步播放音乐
     */
    class MyTask extends AsyncTask<Void, Integer, Boolean> {
        /**
         * 这个方法会在后台任务开始执行之间调用，用于进行一些界面上的初始化操作，
         * 比如显示一个进度条对话框等。
         */
        @Override
        protected void onPreExecute() {
            progressView.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        /**
         * 这个方法中的所有代码都会在子线程中运行，我们应该在这里去处理所有的耗时任务。
         * 任务一旦完成就可以通过return语句来将任务的执行结果进行返回，如果AsyncTask的
         * 第三个泛型参数指定的是Void，就可以不返回任务执行结果。注意，在这个方法中是不
         * 可以进行UI操作的，如果需要更新UI元素，比如说反馈当前任务的执行进度，可以调用
         * publishProgress(Progress...)方法来完成。
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            setMusicPlayer(musicPath);
            return null;
        }

        /**
         * 当在后台任务中调用了publishProgress(Progress...)方法后，这个方法就很快会被调用，
         * 方法中携带的参数就是在后台任务中传递过来的。在这个方法中可以对UI进行操作，利用参
         * 数中的数值就可以对界面元素进行相应的更新。
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 当后台任务执行完毕并通过return语句进行返回时，这个方法就很快会被调用。返回的数据
         * 会作为参数传递到此方法中，可以利用返回的数据来进行一些UI操作，比如说提醒任务执行
         * 的结果，以及关闭掉进度条对话框等。
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressView.setVisibility(View.GONE);
            onMusicStart();
            onStarts();
            mediaPlayer.setVolume(musicVolume, musicVolume);
            setVolume(videoVolume, videoView);
            super.onPostExecute(aBoolean);
        }

    }


    /**
     * 异步分割图片
     */
    class MyTaskImage extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            getBitmapsFromVideo(videoPath);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            isBitmap = true;//完成之后不再取图片
            super.onPostExecute(aBoolean);
        }

    }

    /**
     * 分割视频 1秒1帧
     *
     * @param dataPath
     */
    public void getBitmapsFromVideo(String dataPath) {
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(dataPath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        videoTime = Integer.valueOf(time) / 1000;

        // 得到每一秒时刻的bitmap比如第一秒,第二秒
        for (int i = 1; i <= videoTime; i += videoTime / 8) {
            bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            list.add(bitmap);
            finalI = i;
            new Thread() {
                public void run() {
                    handler.sendEmptyMessage(finalI);
                }
            }.start();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == finalI) {
                if (finalI == 1) {
                    imageViewCover.setImageBitmap(bitmap);
                }
                coverAdapter.notifyDataSetChanged();
            }
        }
    };
}
