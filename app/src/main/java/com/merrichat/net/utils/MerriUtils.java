package com.merrichat.net.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.merrichat.net.app.MerriApp;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by amssy on 17/12/6.
 */

public class MerriUtils {
    public static boolean isBeReport = false;
    @SuppressLint("StaticFieldLeak")
    public static MerriApp sApplication;
    public static WeakReference<Activity> sTopActivityWeakRef;
    public static List<Activity> sActivityList = new LinkedList<>();
    private static long lastClickTime = 0;
    private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private MerriUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final MerriApp app) {
        MerriUtils.sApplication = app;
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static MerriApp getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    private static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    public static boolean isFastDoubleClick2() {
        long currentClickTime = System.currentTimeMillis();
        long duration = currentClickTime - lastClickTime;
        if (0 < duration && duration < 1000) {
            return true;
        }
        lastClickTime = currentClickTime;
        return false;
    }


    public static boolean isFastDoubleClick() {
        long currentClickTime = System.currentTimeMillis();
        long duration = currentClickTime - lastClickTime;
        if (0 < duration && duration < 4500) {
            return true;
        }
        lastClickTime = currentClickTime;
        return false;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS M
     * @return
     */
    public static double FormetFileSize(long fileS) {
        if (fileS == 0) {
            return 0;
        }
        return ((double) fileS / 1048576);

    }


    public static List<MultipartBody.Part> filesToMultipartBodyParts(ArrayList<String> files) {
        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
        for (String filePath : files) {
            File file = new File(filePath);
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(file.getName(), file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

    /**
     * @param view        view
     * @param enlargement 放大时间
     * @param shrink      缩小时间
     */
    public static void updateView(View view, long enlargement, long shrink) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        scaleX.setDuration(enlargement);
        scaleX.setRepeatCount(-1);

        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.2f);
        scaleX2.setDuration(shrink);
        scaleX2.setRepeatCount(-1);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleX2);
        set.start();
    }

    /**
     * 判断当前字符是否是汉字
     *
     * @param str 字符
     * @return
     */
    public static boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())
            flg = true;
        return flg;
    }


}
