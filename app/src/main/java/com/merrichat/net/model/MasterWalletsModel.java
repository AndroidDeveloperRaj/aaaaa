package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 18/1/31.
 * 群主钱包设置页面
 * model存储的为该群管理员数据
 */

public class MasterWalletsModel implements Serializable{

    /**
     * masterName : 15311806662
     * isShowWallet : 0
     * masterId : 320233812574208
     * masterImgUrl : http://cas.okdit.net/nfs_data/mob/head/320233812574208.jpg
     */


    /**
     * 群管理员名字
     */
    private String masterName;

    /**
     * 是否显示钱包0：不能 1：可以
     */
    private int isShowWallet;

    /**
     * 群管理员id
     */
    private String masterId;

    /**
     * 群管理员头像url
     */
    private String masterImgUrl;

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public int getIsShowWallet() {
        return isShowWallet;
    }

    public void setIsShowWallet(int isShowWallet) {
        this.isShowWallet = isShowWallet;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getMasterImgUrl() {
        return masterImgUrl;
    }

    public void setMasterImgUrl(String masterImgUrl) {
        this.masterImgUrl = masterImgUrl;
    }
}
