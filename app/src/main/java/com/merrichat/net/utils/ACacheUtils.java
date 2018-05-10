package com.merrichat.net.utils;

import android.content.Context;
import android.text.TextUtils;


/**
 * 使用数据缓存时将值put进去，然后在所需值的界面去get获取就好了
 * Created by amssy on 17/12/29.
 */

public class ACacheUtils {
    /**
     * 设置支付宝账号id
     *
     * @param mContext
     * @param aliAcountId
     */
    public static void putAliPayAcountId(Context mContext, String aliAcountId) {
        ACache aCache = ACache.get(mContext);
        aCache.put("aliAcountId", aliAcountId);
    }

    public static void putString(Context mContext, String key, String value) {
        ACache aCache = ACache.get(mContext);
        aCache.put(key, value);
    }

    public static boolean getBoolean(Context mContext, String key) {
        ACache aCache = ACache.get(mContext);
        boolean value = aCache.getAsBoolean(key);
        return value;
    }

    public static String getString(Context mContext, String key) {
        ACache aCache = ACache.get(mContext);
        String value = aCache.getAsString(key);
        return value;
    }

    /**
     * 获取支付宝账号id
     *
     * @param mContext
     * @return
     */
    public static String getAliPayAcountId(Context mContext) {
        ACache aCache = ACache.get(mContext);
        String aliAcountId = aCache.getAsString("aliAcountId");
        return aliAcountId;
    }


    /**
     * 设置微信账号id
     *
     * @param mContext
     * @param weChatAccountId
     */
    public static void putWeChatAccountId(Context mContext, String weChatAccountId) {
        ACache aCache = ACache.get(mContext);
        aCache.put("weChatAccountId", weChatAccountId);
    }


    /**
     * 获取微信账号id
     *
     * @param mContext
     * @return
     */
    public static String getWeChatAccountId(Context mContext) {
        ACache aCache = ACache.get(mContext);
        String aliAcountId = aCache.getAsString("weChatAccountId");
        return aliAcountId;
    }
}
