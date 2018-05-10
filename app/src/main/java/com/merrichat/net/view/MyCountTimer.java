package com.merrichat.net.view;


import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.merrichat.net.utils.Logger;


/**
 * Created by amssy on 17/11/7.
 * 延时拍照
 */

public class MyCountTimer extends CountDownTimer {
    public static final int TIME_COUNT = 31000;//倒计时总时间为31S，时间防止从29s开始显示（以倒计时30s为例子）
    private TextView tv;
    private String endStrRid;
    private long totalTime;
    private DownTimeWatcher downTimeWatcher;

    /**
     * 参数 millisInFuture         倒计时总时间（如30s,60S，120s等）
     * 参数 countDownInterval    渐变时间（每次倒计1s）
     * 参数 btn               点击的按钮(因为Button是TextView子类，为了通用我的参数设置为TextView）
     * 参数 endStrRid   倒计时结束后，按钮对应显示的文字
     */
    public MyCountTimer(long millisInFuture, long countDownInterval, TextView btn, String endStrRid) {
        super(millisInFuture, countDownInterval);
        this.tv = btn;
        this.totalTime = millisInFuture;
        this.endStrRid = endStrRid;
    }

    /**
     * 参数上面有注释
     */
    public MyCountTimer(TextView btn, String endStrRid) {
        super(TIME_COUNT, 1000);
        this.tv = btn;
        this.endStrRid = endStrRid;
    }

    /**
     * 计时完毕时触发
     */
    @Override
    public void onFinish() {
        tv.setText(endStrRid);
        tv.setEnabled(true);
        this.cancel();
        downTimeWatcher.onDownTimeFinish();
    }


    public interface DownTimeWatcher {
        void onTime(int num);

        void onDownTimeFinish();
    }

    /**
     * 监听倒计时的变化
     *
     * @param downTimeWatcher
     */
    public void setOnTimeDownListener(DownTimeWatcher downTimeWatcher) {
        this.downTimeWatcher = downTimeWatcher;
    }

    /**
     * 计时过程显示
     */
    @Override
    public void onTick(long millisUntilFinished) {
        Logger.e(millisUntilFinished + "");

        tv.setEnabled(false);
        //每隔一秒修改一次UI millisUntilFinished 比如：{10999,9994,8987...}{11000,9984,8967...}
        if (millisUntilFinished == totalTime) {
            tv.setText(millisUntilFinished / 1000 - 1 + "");
            downTimeWatcher.onTime((int) millisUntilFinished / 1000 - 1);
        } else {
            tv.setText(millisUntilFinished / 1000 + "");
            downTimeWatcher.onTime((int) millisUntilFinished / 1000);
        }

        // 设置透明度渐变动画
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        //设置动画持续时间
        alphaAnimation.setDuration(1000);
        tv.startAnimation(alphaAnimation);

        // 设置缩放渐变动画
        final ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 0.5f, 2f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        tv.startAnimation(scaleAnimation);
        if (tv.getText().toString().equals("1")) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    tv.setText("");
                }
            }, 1000);
        }
    }
}