package com.merrichat.net.activity.video.player.gsyvideo;

import android.graphics.Bitmap;


/**
 * Created by yuejiaoli on 2017/7/21.
 */

public class Edit {

    public interface OnCutChangeListener {
        void onCutChangeKeyDown();

        void onCutChangeKeyUp(int startTime, int endTime);
    }

    public interface OnSpeedChangeListener {
        void onSpeedChange(float speed);
    }

    public interface OnFilterChangeListener {
        void onFilterChange(Bitmap bitmap);
    }

    public interface OnBGMChangeListener {
        void onBGMSeekChange(float progress);

        void onBGMDelete();

        boolean onBGMInfoSetting(TCBGMInfo info);

        void onBGMRangeKeyDown();

        void onBGMRangeKeyUp(long startTime, long endTime); //ms
    }

    public interface OnWordChangeListener {
        void onWordClick();

    }
}
