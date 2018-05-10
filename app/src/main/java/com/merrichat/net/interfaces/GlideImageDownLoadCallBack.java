package com.merrichat.net.interfaces;

import java.io.File;

/**
 * Created by Allen Cheng on 2018/4/29.
 *
 * glide下载图片的监听
 */
public interface GlideImageDownLoadCallBack {

    void onDownLoadSuccess(File file);

    void onDownLoadFailed();
}
