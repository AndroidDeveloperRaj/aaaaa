package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 18/3/1.
 * 群通知model
 */

public class GroupNotificationModel implements Serializable{


    /**
     * headImgUrl : http://cas.okdit.net/nfs_data/mob/head/320233812574208.jpg
     * memberId : 320233812574208
     * memberName : 15311806662
     * status : 0
     * remark : 申请加入吊炸天无敌群
     * communityId : 331671577419776
     */

    private String headImgUrl;
    private long memberId;
    private String memberName;

    /**
     * 申请状态0:申请中 1:已同意  2:已拒绝
     */
    private int status;
    private String remark;
    private String communityId;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
}
