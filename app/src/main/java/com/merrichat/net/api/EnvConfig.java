package com.merrichat.net.api;

import com.merrichat.net.app.MerriApp;

public class EnvConfig {

    public static String getWebApiBaseUrl() {
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 2:
                return "http://igomopub.igomot.net/clip-pub/";
            case 1:
                return "http://39.107.25.150:8083/clippub/";
            default:
                return "";

        }
    }

    public static String getSocketBaseUrl() {
        switch (MerriApp.URL_ENVIRONMENT_VARIABLE) {
            case 2:
                return "http://igomopub.igomot.net/clip-pub/";
            case 1:
                return "http://39.107.25.150:8080/clippub/";
            default:
                return "";
        }
    }

    public static String getExpressCompany(){
        return "http://publicapi.okdit.net/publicapi/";
    }

}
