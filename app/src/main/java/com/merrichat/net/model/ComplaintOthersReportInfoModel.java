package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2017/12/5.
 */

public class ComplaintOthersReportInfoModel {
    /**
     * clientMemberName : 举报人
     * clientPhone : 183787887878
     * clientMemberId : 6736273273
     * beTipOffMemberName : 被举报人姓名
     * beTipOffPhone : 1576736767
     * beTipOffContent : 被举报的内容
     * beTipOffMemberId : 1783748374
     * beTipOffType : 举报类型
     */

    private String clientMemberName;
    private String clientPhone;
    private String clientMemberId;
    private String beTipOffMemberName;
    private String beTipOffPhone;
    private String beTipOffContent;
    private String beTipOffMemberId;
    private String beTipOffType;

    public String getClientMemberName() {
        return clientMemberName;
    }

    public void setClientMemberName(String clientMemberName) {
        this.clientMemberName = clientMemberName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientMemberId() {
        return clientMemberId;
    }

    public void setClientMemberId(String clientMemberId) {
        this.clientMemberId = clientMemberId;
    }

    public String getBeTipOffMemberName() {
        return beTipOffMemberName;
    }

    public void setBeTipOffMemberName(String beTipOffMemberName) {
        this.beTipOffMemberName = beTipOffMemberName;
    }

    public String getBeTipOffPhone() {
        return beTipOffPhone;
    }

    public void setBeTipOffPhone(String beTipOffPhone) {
        this.beTipOffPhone = beTipOffPhone;
    }

    public String getBeTipOffContent() {
        return beTipOffContent;
    }

    public void setBeTipOffContent(String beTipOffContent) {
        this.beTipOffContent = beTipOffContent;
    }

    public String getBeTipOffMemberId() {
        return beTipOffMemberId;
    }

    public void setBeTipOffMemberId(String beTipOffMemberId) {
        this.beTipOffMemberId = beTipOffMemberId;
    }

    public String getBeTipOffType() {
        return beTipOffType;
    }

    public void setBeTipOffType(String beTipOffType) {
        this.beTipOffType = beTipOffType;
    }
}
