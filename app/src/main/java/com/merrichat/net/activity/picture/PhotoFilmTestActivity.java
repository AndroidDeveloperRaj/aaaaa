package com.merrichat.net.activity.picture;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

public class PhotoFilmTestActivity extends BaseActivity {


//    /storage/emulated/0/tencent/qqfile_recv/maple_story.mp3

    private TextView mImagesList;
    private TextView mFileList;
    private VideoView videoView;

    List<String> mFileLists = new ArrayList<>();  //要合成视频的照片
    List<String> mCmds = new ArrayList<>();  //命令集合

    String urlVideoView = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_film_test);

        mImagesList = (TextView) findViewById(R.id.imags_list);
        mFileList = (TextView) findViewById(R.id.file_list);

        videoView = (VideoView) this.findViewById(R.id.video_view);
        videoView.setMediaController(new MediaController(this));

        mImagesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlVideoView = System.currentTimeMillis() + "";
                execCmd(40, urlVideoView);

            }
        });

        mFileList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayView();
            }
        });


    }


    public void startPlayView() {
        Uri uri = Uri.parse("/storage/emulated/0/MerriChat/abcd123.mp4");
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();

    }

    /**
     * @param duration 时间
     */
    public void execCmd(long duration, String outputName) {


//        ffmpeg -threads2 -y -r 10 -i /tmpdir/image%04d.jpg -i audio.mp3 -absf aac_adtstoasc output.mp4
        String cmd = "-i /storage/emulated/0/MerriChat/1521042533345.mp4 -an -filter:v setpts=2*PTS -y /storage/emulated/0/MerriChat/abcd123.mp4";


//        String cmd = "-loop 0 -t 5 -i /storage/emulated/0/DCIM/Camera/IMG_20180130_113444.jpg -c:v libx264 -filter_complex [0:v]zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=200:s=1080x1920[v0];[v0]concat=n=1:v=1:a=0,format=yuv420p[v] -map [v] -preset ultrafast /storage/emulated/0/MerriChat/" + outputName + ".mp4";

        EpEditor.execCmd(cmd, duration, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.e("------->>>", " : onSuccess()");
            }

            @Override
            public void onFailure() {
                Log.e("------->>>", " : onFailure()");
            }

            @Override
            public void onProgress(float progress) {
                Log.e("------->>>", " :" + progress);

            }
        });
    }


}
