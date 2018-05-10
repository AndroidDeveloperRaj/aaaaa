/**
 * probject:cim-android-sdk
 *
 * @version 2.0.0
 * @author 3979434@qq.com
 */
package com.merrichat.net.activity.message.cim.constant;

/**
 * 常量
 */
public interface CIMConstant {


    public static String UTF8 = "UTF-8";

    public static byte MESSAGE_SEPARATE = '\b';


    public static int CIM_DEFAULT_MESSAGE_ORDER = 1;

    public static class ReturnCode {
        /**
         * 消息发送成功
         */
        public static String CODE_200 = "200";
        /**
         * 文本消息包含敏感字
         */
        public static String CODE_201 = "201";
        /**
         * 无法找到对方登录ip
         */
        public static String CODE_203 = "203";
        /**
         * 服务器错误
         */
        public static String CODE_500 = "500";
        /**
         * memberId无效
         */
        public static String CODE_501 = "501";
        /**
         * 群聊时 群组不存在
         */
        public static String CODE_504 = "504";
        /**
         * 用户未注册
         */
        public static String CODE_506 = "506";
        /**
         * 用户不在群中
         */
        public static String CODE_507 = "507";
        /**
         * 消息超过一分钟，不能撤回
         */
        public static String CODE_508 = "508";
        /**
         * 黑名单用户
         */
        public static String CODE_509 = "509";

    }

    /**
     * 服务端心跳请求命令
     */
    public static final String CMD_HEARTBEAT_REQUEST = "cmd_server_hb_request";
    /**
     * 客户端心跳响应命令
     */
    public static final String CMD_HEARTBEAT_RESPONSE = "cmd_client_hb_response";


    public static class RequestKey {


        public static String CLIENT_BIND = "client_bind";
        public static String CLIENT_HEARTBEAT = "client_heartbeat";

        public static String CLIENT_LOGOUT = "client_logout";

        public static String CLIENT_OFFLINE_MESSAGE = "client_get_offline_message";

    }


    public static class MessageType {

        //用户会 踢出下线消息类型
        public static String TYPE_999 = "999";

    }

}