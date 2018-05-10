package com.merrichat.net.service;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.merrichat.net.interfaces.GlideImageDownLoadCallBack;

import java.io.File;

/**
 * Created by Allen Cheng on 2018/4/29.
 */
public class GlideDownLoadImageService implements Runnable {
    private String url;
    private Context context;
    private GlideImageDownLoadCallBack callBack;

    public GlideDownLoadImageService(Context context, String url, GlideImageDownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void run() {
        File file = null;
        try {
            file = Glide.with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                callBack.onDownLoadSuccess(file);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

}
