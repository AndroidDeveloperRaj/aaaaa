package com.merrichat.net.pre;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

/**
 * PrefAppStore.java
 *
 * @author bing on 2018年1月2日 下午2:20:11
 */
public class PrefAppStore {
    private static final String TAG = PrefAppStore.class.getName();
    private static final String IS_FIRST_JIANGLI_DIALOG_SHOW_TAG_KEY = "is_first_jiangli_dialog_show_tag_key";
    private static final String IS_FIRST_JIANGLI_DIALOG_SHOW_URL_KEY = "is_first_jiangli_dialog_show_url_key";
    private static final String IS_FIRST_JIANGLI_DIALOG_SHARE_URL_KEY = "is_first_jiangli_dialog_share_url_key";
    private static final String IS_ALREADY_UPLOAD_TXL = "is_already_upload_txl";//是否已经同步过通讯录
    private static final String USER_NAME = "user_name";
    private static final String NOTICE_NUM = "notice_num";
    private static final String NOTICE_NUM_NEW = "notice_num_new";
    private static final String ZAN_PING_LUN = "zan_pinglun";
    private static final String ZAN_PING_LUN_NEW = "zan_pinglun_new";
    private static final String GROUP_NUM = "group_num";
    private static final String GROUP_NUM_NEW = "group_num_new";
    private static final String NEW_FRIEND_NUM = "new_friend_num";
    private static final String Express_NOTIFICATION = "express_notification";
    private static final String MESSAGE_NUMBER = "message_number";
    private static final String SOCKET_ADDRESS = "mSocketAddress";  //socket 连接地址
    private static final String CASH_BALANCE = "cashBalance";//账户余额
    private static final String IS_FRIEND = "isFriend";
    private static final String LOGIN_OUT_MOBLIE = "login_out_mobile";
    private static final String WORK_NET_STATUS = "work_net_status";

    private static final String GROUP_NAME = "group_name"; //群名称
    private static final String GROUP_INFO = "group_info"; //群名称
    private static final String GROUP_LOCATION = "group_location"; //群位置
    private static final String MESSAGE_HEADER_IMG_TIMESTAMP = "message_header_img_timestamp"; //群位置

    private static final String MAGIC_SELECT_PAGE = "magic_select_page";
    private static final String MAGIC_SELECT_POSITION = "magic_select_position";

    /**
     * 我的-设置-新消息通知-视频电话、语音电话 设置状态
     */
    public final static String ABOUT_PHONE_SETTING = "about_phone_setting";    // 0：声音   1：振动  2：声音加振动


    static SharedPreferences getSharedPreferences(final Context mContext) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext);
        } else {
            return null;
        }
    }

    public static void setUserName(final Context mContext, final String userName) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(final Context ctx) {
        return getSharedPreferences(ctx).getString(USER_NAME, "");
    }


    public static void setMagicSelectPage(final Context mContext, final int magicSelectPage) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(MAGIC_SELECT_PAGE, magicSelectPage);
        editor.commit();
    }

    public static int getMagicSelectPage(final Context ctx) {
        return getSharedPreferences(ctx).getInt(MAGIC_SELECT_PAGE, -1);
    }

    public static void setMagicSelectPosition(final Context mContext, final int magicSelectPosition) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(MAGIC_SELECT_POSITION, magicSelectPosition);
        editor.commit();
    }

    public static int getMagicSelectPosition(final Context ctx) {
        return getSharedPreferences(ctx).getInt(MAGIC_SELECT_POSITION, -1);
    }


    /**
     * 消息列表--头布局----通知是否显示
     * 默认不显示
     *
     * @param mContext
     * @param noticeNum
     */
    public static void setNoticeNum(final Context mContext, final boolean noticeNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(NOTICE_NUM, noticeNum);
        editor.commit();
    }

    public static boolean getNoticeNum(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(NOTICE_NUM, false);
    }


    public static void setNoticeNumNew(final Context mContext, final int noticeNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(NOTICE_NUM_NEW, noticeNum);
        editor.commit();
    }

    public static int getNoticeNumNew(final Context ctx) {
        return getSharedPreferences(ctx).getInt(NOTICE_NUM_NEW, 0);
    }


    /**
     * 消息列表--头布局----赞和评论是否显示
     * 默认不显示
     *
     * @param mContext
     * @param commentNum
     */
    public static void setZanPingLun(final Context mContext, final boolean commentNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(ZAN_PING_LUN, commentNum);
        editor.commit();
    }

    public static boolean getZanPingLun(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(ZAN_PING_LUN, false);
    }


    public static void setZanPingLunNum(final Context mContext, final int commentNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(ZAN_PING_LUN_NEW, commentNum);
        editor.commit();
    }

    public static int getZanPingLunNum(final Context ctx) {
        return getSharedPreferences(ctx).getInt(ZAN_PING_LUN_NEW, 0);
    }

    /**
     * 消息列表--头布局----群通知是否显示
     * 默认不显示
     *
     * @param mContext
     * @param groupNum
     */
    public static void setGroupNum(final Context mContext, final boolean groupNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(GROUP_NUM, groupNum);
        editor.commit();
    }

    public static boolean getGroupNum(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(GROUP_NUM, false);
    }

    public static void setGroupNumNew(final Context mContext, final int groupNum) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(GROUP_NUM_NEW, groupNum);
        editor.commit();
    }

    public static int getGroupNumNEW(final Context ctx) {
        return getSharedPreferences(ctx).getInt(GROUP_NUM_NEW, 0);
    }


    /**
     * 消息列表--头布局----快递通知通知是否显示
     * 默认不显示
     *
     * @param mContext
     * @param expressNotification
     */
    public static void setExpressNotification(final Context mContext, final boolean expressNotification) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(Express_NOTIFICATION, expressNotification);
        editor.commit();
    }

    public static boolean getExpressNotification(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(Express_NOTIFICATION, false);
    }

    /**
     * 新的朋友数量
     *
     * @param mContext
     * @param num
     */
    public static void setNewFriendNum(final Context mContext, final int num) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(NEW_FRIEND_NUM, num);
        editor.commit();
    }

    public static int getNewFriendNum(final Context ctx) {
        return getSharedPreferences(ctx).getInt(NEW_FRIEND_NUM, 0);
    }


    /**
     * 快递通知未读数量
     *
     * @param mContext
     * @param notificationNumber
     */
    public static void setNotificationNumber(final Context mContext, final int notificationNumber) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(MESSAGE_NUMBER, notificationNumber);
        editor.commit();
    }

    public static int getNotificationNumber(final Context ctx) {
        return getSharedPreferences(ctx).getInt(MESSAGE_NUMBER, 0);
    }


    public static void setSocketAddress(final Context mContext, final String userName) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(SOCKET_ADDRESS, userName);
        editor.commit();
    }

    public static String getSocketAddress(final Context ctx) {
        return getSharedPreferences(ctx).getString(SOCKET_ADDRESS, "");
    }


    public static void setCashBalance(final Context mContext, final String cashBalance) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(CASH_BALANCE, cashBalance);
        editor.commit();
    }

    public static String getCashBalance(final Context ctx) {
        return getSharedPreferences(ctx).getString(CASH_BALANCE, "0.0");
    }

    public static void setIsFriend(final Context mContext, final int isFriend) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(IS_FRIEND, isFriend);
        editor.commit();
    }

    public static int getIsFriend(final Context ctx) {
        return getSharedPreferences(ctx).getInt(IS_FRIEND, 0);
    }

    public static void setGroupName(final Context mContext, final String groupName) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(GROUP_NAME, groupName);
        editor.commit();
    }

    public static String getGroupName(final Context ctx) {
        return getSharedPreferences(ctx).getString(GROUP_NAME, "");
    }


    public static void setGroupInfo(final Context mContext, final String groupInfo) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(GROUP_INFO, groupInfo);
        editor.commit();
    }

    public static String getGroupInfo(final Context ctx) {
        return getSharedPreferences(ctx).getString(GROUP_INFO, "");
    }


    public static void setGroupLocation(final Context mContext, final String groupLocation) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(GROUP_LOCATION, groupLocation);
        editor.commit();
    }

    public static String getGroupLocation(final Context ctx) {
        return getSharedPreferences(ctx).getString(GROUP_LOCATION, "");
    }

    public static void setMessageHeaderImgTimestamp(final Context mContext) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(MESSAGE_HEADER_IMG_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        editor.commit();
    }

    public static String getMessageHeaderImgTimestamp(final Context ctx) {
        return getSharedPreferences(ctx).getString(MESSAGE_HEADER_IMG_TIMESTAMP, "");
    }

    public static void setLoginOutMoblie(final Context mContext, String moblie) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(LOGIN_OUT_MOBLIE, moblie);
        editor.commit();
    }

    public static String getLoginOutMoblie(final Context ctx) {
        return getSharedPreferences(ctx).getString(LOGIN_OUT_MOBLIE, "");
    }

    /**
     * //记录当前是否同意移动网络播放视频 0 不同意  1 同意
     * @param mContext
     */
    public static void setWorkNetStatus(final Context mContext, int networkStatus) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(WORK_NET_STATUS, networkStatus);
        editor.commit();
    }

    public static int getWorkNetStatus(final Context ctx) {
        return getSharedPreferences(ctx).getInt(WORK_NET_STATUS, 0);
    }

    public static void setAboutPhoneSetting(final Context mContext, final int magicSelectPosition) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putInt(ABOUT_PHONE_SETTING, magicSelectPosition);
        editor.commit();
    }

    public static int getAboutPhoneSetting(final Context ctx) {
        return getSharedPreferences(ctx).getInt(ABOUT_PHONE_SETTING, 2);
    }


    public static void setIsFirstJiangliDialogShowTagValue(final Context mContext, final boolean isFirst) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(IS_FIRST_JIANGLI_DIALOG_SHOW_TAG_KEY, isFirst);
        editor.commit();
    }

    public static boolean getIsFirstJiangliDialogShowTagValue(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(IS_FIRST_JIANGLI_DIALOG_SHOW_TAG_KEY, false);
    }


    public static void setIsFirstJiangliDialogShowUrlValue(final Context mContext, final String url) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(IS_FIRST_JIANGLI_DIALOG_SHOW_URL_KEY, url);
        editor.commit();
    }

    public static String getIsFirstJiangliDialogShowUrlValue(final Context ctx) {
        return getSharedPreferences(ctx).getString(IS_FIRST_JIANGLI_DIALOG_SHOW_URL_KEY, "");
    }

    public static void setIsFirstJiangliDialogShareUrlKey(final Context mContext, final String url) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putString(IS_FIRST_JIANGLI_DIALOG_SHARE_URL_KEY, url);
        editor.commit();
    }

    public static String getIsFirstJiangliDialogShareUrlKey(final Context ctx) {
        return getSharedPreferences(ctx).getString(IS_FIRST_JIANGLI_DIALOG_SHARE_URL_KEY, "");
    }


    public static void setIsAlreadyUploadTxl(final Context mContext, final boolean flag) {
        Editor editor = getSharedPreferences(mContext).edit();
        editor.putBoolean(IS_ALREADY_UPLOAD_TXL, flag);
        editor.commit();
    }

    public static boolean getIsAlreadyUploadTxl(final Context ctx) {
        return getSharedPreferences(ctx).getBoolean(IS_ALREADY_UPLOAD_TXL, false);
    }
}
