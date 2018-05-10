package com.merrichat.net.activity.message.cim;

/**
 * Created by amssy on 17/7/26.
 */

public class Constant {

    /**
     * 服务端IP地址
     */
    public static String CIM_SERVER_HOST = "";

    /**
     * 注意，这里的端口不是tomcat的端口，CIM端口在服务端spring-cim.xml中配置的，没改动就使用默认的23456
     */
    public static int CIM_SERVER_PORT = 0;
    /**
     * 请求接口时的端口
     */
    public static String CIM_SERVER_HTTP_PORT = "";

    /**********
     * 保存在sp中的登录帐号的key
     ***************/
    public static final String LOGIN_ACCOUNT = "login_account";

    /*********
     * 发送到服务器时的SentBody时的key
     ***************/
    public static final String SENT_BODY_KEY = "client_send_message";

    public static interface MessageType {


        //用户之间的普通消息
        /**
         * 单聊
         */
        public static final String TYPE_1 = "1";
        /**
         * 群聊
         */
        public static final String TYPE_2 = "2";
        /**
         * 系统消息（如：更换群主、成员加入）
         */
        public static final String TYPE_3 = "3";
        /**
         * 群解散
         */
        public static final String TYPE_4 = "4";
        /**
         * 群公告修改
         */
        public static final String TYPE_5 = "5";//系统消息（如：公告修改）
        /**
         * 移出群成员
         */
        public static final String TYPE_6 = "6";//系统消息


        //下线类型
        String TYPE_999 = "999";
    }


    public static interface MessageStatus {

        //消息未读
        public static final String STATUS_0 = "0";
        //消息已经读取
        public static final String STATUS_1 = "1";
    }

    /**
     * 发送消息时如果包含文件，以下则是文件的类型
     *
     * @author shiqiang
     * @ClassName: MessageFileType
     * @date 2015-12-18 上午10:05:29
     */
    public static interface MessageFileType {
        /******
         * 1：静态图片
         *******/
        public static final String TYPE_STATIC_IMAGE = "1";
        /******
         * 2：语音
         *******/
        public static final String TYPE_VOICE = "2";
        /******
         * 3 gif图片
         *******/
        public static final String TYPE_GIF_IMAGE = "3";
        /******
         * 4:视频文件
         *******/
        public static final String TYPE_VIDEO_FILE = "4";
        /******
         * 5：文字信息（单独发文字消息时，文件为空，文件类型传5）
         *******/
        public static final String TYPE_OTHER_FILE = "5";

        /******
         * 6：发布交易
         *******/
        public static final String TYPE_JIAOYI_FILE = "11";

        /******
         * 10：发送位置
         *******/
        public static final String TYPE_LOCATION_FILE = "12";

    }
}
