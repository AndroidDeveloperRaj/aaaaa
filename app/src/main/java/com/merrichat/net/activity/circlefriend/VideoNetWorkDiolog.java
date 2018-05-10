package com.merrichat.net.activity.circlefriend;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.pre.PrefAppStore;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 移动网络播放视频提示框
 * Created by amssy on 18/4/20.
 */

public class VideoNetWorkDiolog extends Activity {
    /**
     * 暂停播放
     */
    @BindView(R.id.btn_stop)
    Button btnStop;
    /**
     * 继续观看
     */
    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.rel_dialog)
    RelativeLayout relDialog;

    @BindView(R.id.rel_group)
    RelativeLayout relGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_network);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_stop, R.id.btn_start})
    public void onViewClick(View view) {
        switch (view.getId()) {
            //暂停播放
            case R.id.btn_stop:
                CircleVideoActivity.isResume = false;
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.VIDEO_PAUSE = true;
                EventBus.getDefault().post(myEventBusModel);
                finish();
                break;
            //继续观看
            case R.id.btn_start:
                //记录当前是否同意移动网络播放视频
                PrefAppStore.setWorkNetStatus(VideoNetWorkDiolog.this, 1);

                CircleVideoActivity.isResume = true;
                MyEventBusModel myEventBusModel1 = new MyEventBusModel();
                myEventBusModel1.VIDEO_START = true;
                EventBus.getDefault().post(myEventBusModel1);
                finish();
                break;
            case R.id.rel_dialog:
                break;
            case R.id.rel_group:
                break;
        }
    }
}
