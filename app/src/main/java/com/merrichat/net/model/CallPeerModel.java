package com.merrichat.net.model;

/**
 * Created by wangweiwei on 2018/3/16.
 */

public class CallPeerModel {
    public String fromMemberId;
    public String toMemberId;
    public String fromMemberName;
    public String fromHeadImgUrl;
    public String callSource;   // 1 美遇， 0 消息聊天
    public String callType;  //  消息聊天中使用   1，视频聊天 ，0，语音聊天
    public String fromLatitude;
    public String fromLongitude;
}
