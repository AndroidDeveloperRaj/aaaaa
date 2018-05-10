package com.merrichat.net.app;

import android.app.ActivityManager;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

/**
 * Created by amssy on 17/6/6.
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */

public class AppManager {

    private static Stack<AppCompatActivity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(AppCompatActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<AppCompatActivity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public AppCompatActivity currentActivity() {
        AppCompatActivity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 获取界面第二个Activity（堆栈中倒数第二个压入的）
     */
    public AppCompatActivity getLastButOneActivity() {
        AppCompatActivity activity = activityStack.get(activityStack.size() - 2);
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        AppCompatActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(AppCompatActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i).getClass().equals(cls)) {
                finishActivity(activityStack.get(i));
            }
        }
    }

    public void removeActivity(AppCompatActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<AppCompatActivity>();
        }
        activityStack.remove(activity);
    }

    /**
     * 是否存在指定类名的Activity
     *
     * @param cls
     * @return
     */
    public Boolean hasActivity(Class<?> cls) {
        Boolean boolean1 = false;
        if (null != activityStack && activityStack.size() > 0) {
            for (AppCompatActivity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    boolean1 = true;
                }
            }
        } else {
            boolean1 = false;
        }
        return boolean1;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            MobclickAgent.onKillProcess(context);
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
//            LogUtil.e(e.toString());
        }
    }
}
