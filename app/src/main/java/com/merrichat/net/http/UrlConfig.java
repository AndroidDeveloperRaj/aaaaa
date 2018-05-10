package com.merrichat.net.http;


import com.merrichat.net.app.MerriApp;

/**
 * Created by amssy on 17/6/7.
 * url环境切换配置文件
 */

public class UrlConfig {

    /**
     * 获取访问网络开头的baseUrl
     *
     * @return
     */
    public static String getLifeUrl() {
        String url = null;
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 1:
//                url = "http://39.107.25.150:8083/clippub/";
                url = "http://clippub.merrichat.com/clippub/";
//                url = "http://192.168.53.189:8080/clippub/";
                break;
            case 2:
                url = "http://igomopub.igomot.net/clip-pub/";
                break;
        }
        return url;
    }


    /**
     * 获取socket的url,用于app推送服务
     *
     * @return
     */
    public static String getPushUrl() {
        String url = null;
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 1:
                url = "push.okdi.net";
                break;
            case 2:
                url = "push.okdit.net";
                break;
        }
        return url;
    }


    /**
     * 获取socket的端口号,用于app推送服务
     *
     * @return
     */
    public static int getPushPort() {
        int port = 0;
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 1:
                port = 4001;
                break;
            case 2:
                port = 40008;
                break;
        }
        return port;
    }


    /**
     * 获取群聊单聊上传的消息url
     *
     * @return
     */
    public static String getChatUrl() {
        String url = null;
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 1:
                url = "http://ichat.merrichat.com/ichat/";
//                url = "http://ichat.okdi.net/ichat/";
                break;
            case 2:
                url = "http://ichat.okdit.net/ichat/";
                break;
        }
        return url;
    }

    /**
     * 获取群聊单聊投降前缀url
     *
     * @return
     */
    public static String getChatHeaderUrl() {
        String url = null;
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 1:
                url = "http://xmgzh.okdi.net/nfs_data/mob/head/";
//                url = "http://ichat.okdi.net/ichat/";
                break;
            case 2:
                url = "http://cas.okdit.net/nfs_data/mob/head/";
                break;
        }
        return url;
    }
}
