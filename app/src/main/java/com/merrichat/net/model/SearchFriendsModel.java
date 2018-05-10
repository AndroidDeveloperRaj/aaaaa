package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 18/2/27.
 * 搜索好友model
 */

public class SearchFriendsModel implements Serializable {

    private String nickName;
    private String imgUrl;
    private String mobile;
    private String memberId;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
