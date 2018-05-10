package com.merrichat.net.activity.message;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.CustomVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VedioShowActivity extends BaseActivity {

//    @BindView(R.id.smallVideo_gl)
//    FrameLayout smallVideo_gl;
    @BindView(R.id.cumstom_videoView)
    CustomVideoView cumstom_videoView;
    @BindView(R.id.iv_finish)
    ImageView ivFinish;
    private MediaController media_control = null;
    private String vedio;
    private FrameLayout topVvFrameLayout;
    //从动态详情页面点击视频跳转过来的时候 从当前视频播放到哪  然后在传值回去
    private int activitId;
    //从动态详情页面点击视频跳转过来的时候 从当前视频播放到哪  然后在传值回去
    private int currentTime;
    public static final int ACTIVITYID = MiscUtil.getActivityId();
    //视频是否停止播放 如果是的话，值为true 为了返回动态详情界面也停止播放
    private boolean isPause;
    //视频是否已经播放完毕
    private boolean isCompleted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 255);
        setContentView(R.layout.activity_vedio_show);
//        initTitleBar();
        ButterKnife.bind(this);
        media_control = new MediaController(cnt);

        Intent intent = getIntent();
        if (intent != null) {
            vedio = intent.getStringExtra("vedio");
            currentTime = intent.getIntExtra("time", 0);
            activitId = intent.getIntExtra("activitId", 0);
//            smallVideo_gl.setVisibility(View.GONE);
            cumstom_videoView.setVideoPath(vedio);
            cumstom_videoView.setMediaController(media_control);
            cumstom_videoView.setZOrderOnTop(true);
            media_control.setMediaPlayer(cumstom_videoView);
            //让VideoView获取焦点
            cumstom_videoView.requestFocus();
            cumstom_videoView.seekTo(currentTime);
            cumstom_videoView.start();
            cumstom_videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                @Override
                public void onPlay() {//视频播放
                    isPause = false;
                    isCompleted = false;
                }

                @Override
                public void onPause() {//视频停止
                    isPause = true;
                    isCompleted = false;
                }
            });
            //视频播放完毕 监听 让他返回ispause = true
            cumstom_videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isCompleted = true;
                }
            });
            //showVedio();
        }

    }

//    @OnClick({R.id.smallVideo_gl})
//    public void OnClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.cumstom_videoView:
//
//                //cumstom_videoView.start();
//                break;
//            case R.id.smallVideo_gl:
//                FrameLayout smallVideo_gl = (FrameLayout) view;
//                VideoView videoView = (CustomVideoView) smallVideo_gl.getChildAt(0);
//                videoView.start();
//                break;
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

//    private void initTitleBar() {
//
//        //获取TtileBar
//        TitleBar titleBar = getTitleBar();
//        /*titleBar.setTitle("我要说");
//        titleBar.setTitleColor(getResources().getColor(R.color.white));*/
//        titleBar.setBackgroundColor(getResources().getColor(R.color.black));
//        //设置左边按钮
//        titleBar.setLeftImageResource(R.mipmap.public_back_btn_down);
//
//        titleBar.setActionTextColor(getResources().getColor(R.color.white));
//        titleBar.setLeftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showVedio() {
//        smallVideo_gl.removeAllViews();

        /***设置覆盖VideoView的布局**/
        topVvFrameLayout = new FrameLayout(cnt);
        FrameLayout.LayoutParams FrameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameParams.gravity = Gravity.CENTER;
        //FrameParams.height=FrameParams.width;
        topVvFrameLayout.setLayoutParams(FrameParams);
        topVvFrameLayout.setPadding(0, StringUtil.dip2px(cnt, 8), StringUtil.dip2px(cnt, 8), 0);

        /***设置ImageView的布局**/
        ImageView iv = new ImageView(cnt);
        FrameLayout.LayoutParams iv_FrameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv_FrameParams.gravity = Gravity.CENTER;
        iv.setLayoutParams(iv_FrameParams);
        iv.setBackgroundResource(R.mipmap.button_bofang2x);

//        Bitmap bitmap =StringUtil.createVideoThumbnail(vedio);
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(vedio);
        Bitmap bitmap = media.getFrameAtTime(1 * 1000 * 1000);
        topVvFrameLayout.addView(iv);
        topVvFrameLayout.setBackground(new BitmapDrawable(bitmap));

        iv.setFocusable(false);
        iv.setEnabled(false);
        topVvFrameLayout.setFocusable(false);

        //设置图片显示的imageview的宽高，根据屏幕宽度设置(一张图)
        CustomVideoView videoView = new CustomVideoView(cnt);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameParams.gravity = Gravity.CENTER;
        params.height = params.width;
        videoView.setLayoutParams(params);
        videoView.setPadding(0, StringUtil.dip2px(cnt, 8), StringUtil.dip2px(cnt, 8), 0);
        videoView.setVideoPath(vedio);
        videoView.setMediaController(media_control);
        videoView.setZOrderOnTop(true);
        media_control.setMediaPlayer(videoView);
        //让VideoView获取焦点
        videoView.requestFocus();

        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                System.out.println("Play!");//our needed process when the video is started
                isPause = false;
                topVvFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPause() {
                topVvFrameLayout.setVisibility(View.VISIBLE);
                isPause = true;
                System.out.println("Pause!");//our needed process when the video is paused
            }
        });
        videoView.seekTo(1 * 1000);
//        smallVideo_gl.addView(videoView);
//        smallVideo_gl.addView(topVvFrameLayout);
    }

    @OnClick({R.id.iv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_finish:
                finish();
                break;
        }
    }
}
