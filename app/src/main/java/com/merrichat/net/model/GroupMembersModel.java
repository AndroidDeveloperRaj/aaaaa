package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by AMSSY1 on 2018/1/27.
 * <p>
 * 群成员列表
 */

public class GroupMembersModel implements Serializable {


    /**
     * headImgUrl : http://cas.okdit.net/nfs_data/mob/head/320233812574208.jpg
     * isSaveList : 0
     * inviterName : 默默无闻
     * memberId : 320233812574208
     * memberName : 15311806662
     * isMater : 0
     * isBanned : 0
     */

    private String headImgUrl;

    /**
     * 是否保存到通讯录0：没有 1：保存
     */
    private int isSaveList;

    /**
     * 邀请人名字
     */
    private String inviterName;
    private String memberId;
    private String memberName;
    /**
     * 成员手机号
     */
    private String phone;
    /**
     * 是否是管理员0:成员 1:管理员 2:群主
     */
    private int isMater;
    /**
     * 是否被禁言0 否 1 是
     */
    private int isBanned;
    /**
     * 自己自定义的字段
     * 是否选中
     */
    private boolean isChecked;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getIsSaveList() {
        return isSaveList;
    }

    public void setIsSaveList(int isSaveList) {
        this.isSaveList = isSaveList;
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

    public int getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(int isBanned) {
        this.isBanned = isBanned;
    }
}
