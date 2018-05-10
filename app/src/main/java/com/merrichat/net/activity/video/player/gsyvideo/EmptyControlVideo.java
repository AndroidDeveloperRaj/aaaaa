package com.merrichat.net.activity.video.player.gsyvideo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.merrichat.net.R;
import com.merrichat.net.adapter.CircleVideoAdapter;
import com.merrichat.net.utils.RxTools.RxToast;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 无任何控制ui的播放
 * Created by guoshuyu on 2017/8/6.
 */

public class EmptyControlVideo extends StandardGSYVideoPlayer {

    private GestureDetector mGestureDetector;

    public EmptyControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EmptyControlVideo(Context context) {
        super(context);
    }

    public EmptyControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new OnDoubleClick());
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //双击 （暂时不可用此方法实现需求）
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 双击事件
     */
    public onDoubleClicklister onDoubleClicks;

    public void setOnDoubleClicklister(onDoubleClicklister onDoubleClick) {
        this.onDoubleClicks = onDoubleClick;
    }

    public interface onDoubleClicklister {
        void onDoubleClicks(MotionEvent event);

        void onViewClicks();
    }

    public class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //双击
            if (onDoubleClicks != null) {
                onDoubleClicks.onDoubleClicks(e);
            }
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            //单击
            if (onDoubleClicks != null) {
                onDoubleClicks.onViewClicks();
            }
            return false;
        }

    }
}
