package com.merrichat.net.model;

/**
 * Created by amssy on 17/7/20.
 */

public class LoginModel {

    /**
     * data : {"accessToken":"","accountId":150418926390489088,"binding":0,"gender":1,"imgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/300837857705984.jpg","memberId":300837857705984,"mobile":"15010222938","realname":"","refreshToken":"","status":"1","userFlag":"0","weixinAccountId":""}
     * success : true
     */

    private DataBean data;
    private boolean success;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * accessToken :
         * accountId : 150418926390489088
         * binding : 0
         * gender : 1
         * imgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/300837857705984.jpg
         * memberId : 300837857705984
         * mobile : 15010222938
         * realname :
         * refreshToken :
         * status : 1
         * userFlag : 0
         * weixinAccountId :
         */

        private String accessToken;
        private long accountId;
        private int binding;
        private int gender;
        private String imgUrl;
        private long memberId;
        private String mobile;
        private String realname;
        private String refreshToken;
        private String uploadFlag;
        private String status;
        private String userFlag;
        private String weixinAccountId;
        private String registTime;

        public String getRegistTime() {
            return registTime;
        }

        public void setRegistTime(String registTime) {
            this.registTime = registTime;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public long getAccountId() {
            return accountId;
        }

        public void setAccountId(long accountId) {
            this.accountId = accountId;
        }

        public int getBinding() {
            return binding;
        }

        public void setBinding(int binding) {
            this.binding = binding;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public long getMemberId() {
            return memberId;
        }

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getUploadFlag() {
            return uploadFlag;
        }

        public void setUploadFlag(String uploadFlag) {
            this.uploadFlag = uploadFlag;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUserFlag() {
            return userFlag;
        }

        public void setUserFlag(String userFlag) {
            this.userFlag = userFlag;
        }

        public String getWeixinAccountId() {
            return weixinAccountId;
        }

        public void setWeixinAccountId(String weixinAccountId) {
            this.weixinAccountId = weixinAccountId;
        }
    }
}
