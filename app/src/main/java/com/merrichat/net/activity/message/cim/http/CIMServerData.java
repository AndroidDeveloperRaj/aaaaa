package com.merrichat.net.activity.message.cim.http;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.UrlConfig;
import com.merrichat.net.utils.GetToast;

import java.io.File;

/**
 * CIM用接口请求
 * Created by amssy on 2016/7/9.
 */
public class CIMServerData {
    private static String SERVER_URL = UrlConfig.getChatUrl();
    /**
     * 聊天时上传语音、视频、图片接口
     *
     * @param cnt
     * @param fileType         1: 图片 2:语音 4:视频
     * @param filePath         要上传的文件的路径
     * @param myStringCallback
     */
    public static void messageUpload(Context cnt, String fileType, String filePath, StringDialogCallback myStringCallback) {
        File file = new File(filePath);
        if (!file.exists()) {
            GetToast.useString(cnt, "文件不能为空");
        }
        String methodName = "message/upload";
        String httpUrl = SERVER_URL + methodName;
        OkGo.<String>post(httpUrl)//
                .params("fileType", fileType)
                .params("source", "android")
                .params("file",  new File(filePath))
                .execute(myStringCallback);

    }
}
