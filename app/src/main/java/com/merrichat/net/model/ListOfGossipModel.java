package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 18/2/1.
 * 禁言名单model
 */

public class ListOfGossipModel implements Serializable{

    /**
     * headImgUrl : http://cas.okdit.net/nfs_data/mob/head/320233812574208.jpg
     * memberId : 320233812574208
     * isMaster : 0
     * memberName : 15311806662
     * isBanned : 1
     */

    private String headImgUrl;
    private String memberId;
    private int isMaster;
    private String memberName;
    private int isBanned;

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(int isMaster) {
        this.isMaster = isMaster;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(int isBanned) {
        this.isBanned = isBanned;
    }
}
