package com.merrichat.net.model;

import com.merrichat.net.activity.meiyu.fragments.Gift;

/**
 * Created by wangweiwei on 2018/3/31.
 */

public class SendGiftModel {

    public String fromMemberId;
    public String toMemberId;  // 对方 memberId
    public String fromMemberName;  //发送方的MemberName
    public String fromHeadImgUrl;  // 对方的头像
    public Gift sendGift;  //礼物的相关信息，此为json字符串

}
