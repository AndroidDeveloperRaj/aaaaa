package com.merrichat.net.activity.video.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.video.player.gsyvideo.EmptyControlVideo;
import com.merrichat.net.adapter.VideoSpecialAdapter;
import com.merrichat.net.utils.DensityUtils;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 特效
 * Created by amssy on 17/11/6.
 */
public class VideoSpecialActivity extends BaseActivity implements GSYMediaPlayerListener {
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
     * 图片帧列表
     */
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    /**
     * 播放器
     */
    @BindView(R.id.videoView)
    EmptyControlVideo videoView;
    /**
     * 极慢
     */
    @BindView(R.id.imageView_speed1)
    ImageView imageViewSpeed1;

    @BindView(R.id.video_play)
    CheckBox videoPlay;
    /**
     * 慢
     */
    @BindView(R.id.imageView_speed2)
    ImageView imageViewSpeed2;
    /**
     * 快
     */
    @BindView(R.id.imageView_speed3)
    ImageView imageViewSpeed3;
    /**
     * 极快
     */
    @BindView(R.id.imageView_speed4)
    ImageView imageViewSpeed4;
    @BindView(R.id.imageView_speed_normal)
    ImageView imageViewSpeedNormal;

    //截取每秒一帧图片集合
    private List<Bitmap> list;
    //图片适配器
    private VideoSpecialAdapter adapter;
    //视频总长度（单位秒）
    private int videoTime;
    //视频路径
    private String videoPath = "/storage/emulated/0/DCIM/Camera/VID_20171101_102416.mp4";
    //private String videoPath = "/storage/emulated/0/super_screenshot/screen_record/20171031_185648.mp4";
    private boolean stopThreed = false;
    private boolean isMoveHands = false;
    private int PROGRESS_CHANGED = 1000;
    private int videoPlayerTime = 0;//视频播放过的时间
    private int time;
    private double scrollTime = 0;

    //变速相关
    private String videoFileName;
    private float SPEED_VIDEO = 1f;//视频变速的倍率
    private String[] command;
    private FFmpeg ffmpeg;

    private static final int REQUEST_PERMISSIONS = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special);
        ButterKnife.bind(this);
        //设置TITLE
        initTitle();
        //绑定适配器
        initRecyclerView();
        initView();

    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getStringExtra("VideoPath");
            /**
             * 播放器加载视频
             */
            videoView.setUp(videoPath, false, "");
            videoView.startPlayLogic();

            /**
             * 异步分割视频获取绝对帧
             */
            new MyTask().execute();
        }

        /**
         * 播放按钮
         */
        videoPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    videoView.startPlayLogic();
                } else {
                    videoView.onVideoPause();
                }
            }
        });

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
                time = (int) (scrollTime / DensityUtils.dp2px(VideoSpecialActivity.this, 40) * 1000);

                if (isMoveHands) {
                    //让视频暂停

                    videoView.seekTo(time);
                    Log.d("LogTest", "MOVE_TIME" + time);
                }
            }
        });
    }

    /**
     * 绑定适配器
     */
    private void initRecyclerView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new VideoSpecialAdapter(VideoSpecialActivity.this, list);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置TITLE
     */
    private void initTitle() {
        tvClose.setText("返回");
        tvTitle.setText("特效");
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.tv_left, R.id.tv_right, R.id.imageView_speed1, R.id.imageView_speed2, R.id.imageView_speed3, R.id.imageView_speed4, R.id.imageView_speed_normal})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left://取消
                finish();
                break;
            case R.id.tv_right://完成
                Intent intent = new Intent();
                intent.putExtra("VideoSpeed", SPEED_VIDEO);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.imageView_speed1:
                SPEED_VIDEO = 0.25f;
                videoView.setSpeedPlaying(0.25f, true);
                break;
            case R.id.imageView_speed2:
                SPEED_VIDEO = 0.5f;
                videoView.setSpeedPlaying(0.5f, true);
                break;
            case R.id.imageView_speed3:
                SPEED_VIDEO = 2f;
                videoView.setSpeedPlaying(2f, true);
                break;
            case R.id.imageView_speed4:
                SPEED_VIDEO = 4f;
                videoView.setSpeedPlaying(4f, true);
                break;
            case R.id.imageView_speed_normal:
                SPEED_VIDEO = 1.0f;
                videoView.setSpeedPlaying(1f, true);
                break;

        }
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onAutoCompletion() {

    }

    @Override
    public void onCompletion() {
        videoView.startPlayLogic();
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onError(int what, int extra) {

    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onVideoSizeChanged() {

    }

    @Override
    public void onBackFullscreen() {

    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void onVideoResume() {

    }

    @Override
    public void onVideoResume(boolean seek) {

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
            getBitmapsFromVideo(videoPath);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            adapter.notifyDataSetChanged();
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
        for (int i = 1; i <= videoTime; i++) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            list.add(bitmap);
            new Thread() {
                public void run() {
                    handler.sendEmptyMessage(0x123);
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
            if (msg.what == 0x123) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
        stopThreed = false;
    }

    //视频进度条更新
    class VideoThreed extends Thread {
        public void run() {
            while (stopThreed) {
                if (videoView.getCurrentPositionWhenPlaying() == videoView.getDuration()) {
                    return;
                }
                Message message = new Message();
                message.what = PROGRESS_CHANGED;
                myHandler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    //更新UI
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    int time = videoView.getCurrentPositionWhenPlaying();
                    double scrollby = ((double) (time - videoPlayerTime) / 1000) * DensityUtils.dp2px(VideoSpecialActivity.this, 40);
                    Log.d("LogTest", "========" + scrollby);
                    if (scrollby >= 0) {
                        DecimalFormat dfi = new DecimalFormat("#");

                        RoundingMode roundingMode = RoundingMode.UP;

                        dfi.setRoundingMode(roundingMode);

                        int parseInt = Integer.parseInt(dfi.format(scrollby));

                        recyclerView.smoothScrollBy(parseInt, 0);
                        videoPlayerTime = time;

                    }
                    break;
            }
        }
    };

    /**
     * 视频变速
     *
     * @param videoPath
     */
    private void videoSpeed(String videoPath) {

    }

}
