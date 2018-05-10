package com.merrichat.net.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.merrichat.net.R;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.model.Response;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.StatusBarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
/**
 * Activity基类
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    public Context cnt;
    public LinearLayout llContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setColorForSwipeBack(this, getResources().getColor(R.color.white), 38);
        final int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (getRequestedOrientation() != orientation) {
            setRequestedOrientation(orientation);
        }
        super.onCreate(savedInstanceState);
        llContent = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        RxActivityTool.addActivity(this);
        AppManager.getAppManager().addActivity(this);
        cnt = this;
    }

    private boolean mCalledRequestedOrientation = false;

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (!mCalledRequestedOrientation) {
            super.setRequestedOrientation(requestedOrientation);
            mCalledRequestedOrientation = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 统计页面
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        //统计时长
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        AppManager.getAppManager().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration =new Configuration();
        configuration.setToDefaults();

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics != null){

            resources.updateConfiguration(configuration,resources.getDisplayMetrics());
            return resources;
        }else {
            return super.getResources();
        }
    }
}
