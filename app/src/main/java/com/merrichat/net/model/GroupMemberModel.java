package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 18/1/22.
 * 群主页--群成员数据model
 */

public class GroupMemberModel implements Serializable {


    /**
     * headImgUrl : http://cas.okdit.net/nfs_data/mob/head/316236519227392.jpg
     * inviterName :
     * memberId : 316236519227392
     * memberName : 默默无闻
     * isMater : 2
     */

    private String headImgUrl;

    /**
     * 邀请人名字
     */
    private String inviterName;
    private String memberId;
    private String memberName;

    /**
     * 是否是管理员0：成员 1：管理员 2：群主
     */
    private int isMater;


    /**
     * 类型flag
     * 1为加号
     * 2为减号
     */
    private int typeFlag;


    public int getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(int typeFlag) {
        this.typeFlag = typeFlag;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getIsMater() {
        return isMater;
    }

    public void setIsMater(int isMater) {
        this.isMater = isMater;
    }
}
