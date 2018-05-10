package com.merrichat.net.activity.video.complete;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 浏览视频
 * Created by amssy on 17/12/22.
 */
public class VideoActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("浏览效果");

        Intent intent = getIntent();
        if (intent != null) {
            videoPath = intent.getStringExtra("videoPath");
            //设置视频控制器
            MediaController mc = new MediaController(this);
            mc.setVisibility(View.INVISIBLE);
            videoView.setMediaController(mc);

            //播放完成回调
            videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Logger.e("VideoView播放视频出错");
                    return true;
                }
            });

            //设置视频路径
            videoView.setVideoURI(Uri.parse(videoPath));

            //开始播放视频
            videoView.start();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //开始播放视频
            videoView.start();
        }
    }
}
