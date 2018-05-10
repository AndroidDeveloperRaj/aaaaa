package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/7.
 */

public class ShareToMakeMoneyModel {

    /**
     * data : {"data":[{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322366366597120.jpg?1515809934990","fromMemberName":"13426460007","createTime":1515470161552,"level":1,"nickName":"13426460010","status":0,"gender":1,"toMemberId":322366366597120,"mobile":"13426460010"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322363938095104.jpg?1515813953597","fromMemberName":"13426460007","createTime":1515469239192,"level":1,"nickName":"13426460009","status":0,"gender":2,"toMemberId":322363938095104,"mobile":"13426460009"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/323087415689216.jpg","fromMemberName":"13426460009","createTime":1515469239192,"level":2,"nickName":"13426460014","status":0,"gender":1,"toMemberId":323087415689216,"mobile":"13426460014"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/323122704465920.jpg","fromMemberName":"13426460009","createTime":1515469239192,"level":2,"nickName":"13426115517","status":0,"gender":1,"toMemberId":323122704465920,"mobile":"13426115517"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322222864777216.jpg?1515829470367","fromMemberName":"13426460007","createTime":1515401651335,"level":1,"nickName":"13426460008","status":0,"gender":2,"toMemberId":322222864777216,"mobile":"13426460008"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322581584723968.jpg?1515828487631","fromMemberName":"13426460008","createTime":1515401651335,"level":2,"nickName":"18317773871","status":0,"gender":1,"toMemberId":322581584723968,"mobile":"18317773871"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/319704705572864.jpg","fromMemberName":"13426460008","createTime":1515401651335,"level":2,"nickName":"13426115501","status":2,"gender":1,"toMemberId":319704705572864,"mobile":"13426115501"}],"h5Url":"http://igomopub.igomot.net/clip-pub/img/profit.png","success":true}
     * success : true
     */

    private DataBeanX data;
    private boolean success;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBeanX {
        /**
         * data : [{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322366366597120.jpg?1515809934990","fromMemberName":"13426460007","createTime":1515470161552,"level":1,"nickName":"13426460010","status":0,"gender":1,"toMemberId":322366366597120,"mobile":"13426460010"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322363938095104.jpg?1515813953597","fromMemberName":"13426460007","createTime":1515469239192,"level":1,"nickName":"13426460009","status":0,"gender":2,"toMemberId":322363938095104,"mobile":"13426460009"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/323087415689216.jpg","fromMemberName":"13426460009","createTime":1515469239192,"level":2,"nickName":"13426460014","status":0,"gender":1,"toMemberId":323087415689216,"mobile":"13426460014"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/323122704465920.jpg","fromMemberName":"13426460009","createTime":1515469239192,"level":2,"nickName":"13426115517","status":0,"gender":1,"toMemberId":323122704465920,"mobile":"13426115517"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322222864777216.jpg?1515829470367","fromMemberName":"13426460007","createTime":1515401651335,"level":1,"nickName":"13426460008","status":0,"gender":2,"toMemberId":322222864777216,"mobile":"13426460008"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/322581584723968.jpg?1515828487631","fromMemberName":"13426460008","createTime":1515401651335,"level":2,"nickName":"18317773871","status":0,"gender":1,"toMemberId":322581584723968,"mobile":"18317773871"},{"imgUrl":"http://cas.okdit.net/nfs_data/mob/head/319704705572864.jpg","fromMemberName":"13426460008","createTime":1515401651335,"level":2,"nickName":"13426115501","status":2,"gender":1,"toMemberId":319704705572864,"mobile":"13426115501"}]
         * h5Url : http://igomopub.igomot.net/clip-pub/img/profit.png
         * success : true
         */

        private String h5Url;
        private boolean success;
        private List<DataBean> data;

        public String getH5Url() {
            return h5Url;
        }

        public void setH5Url(String h5Url) {
            this.h5Url = h5Url;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * imgUrl : http://cas.okdit.net/nfs_data/mob/head/322366366597120.jpg?1515809934990
             * fromMemberName : 13426460007
             * createTime : 1515470161552
             * level : 1
             * nickName : 13426460010
             * status : 0
             * gender : 1
             * toMemberId : 322366366597120
             * mobile : 13426460010
             */

            private String imgUrl;
            private String fromMemberName;
            private long createTime;
            private int level;
            private String nickName;
            private int status;
            private int gender;
            private long toMemberId;
            private String mobile;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getFromMemberName() {
                return fromMemberName;
            }

            public void setFromMemberName(String fromMemberName) {
                this.fromMemberName = fromMemberName;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public long getToMemberId() {
                return toMemberId;
            }

            public void setToMemberId(long toMemberId) {
                this.toMemberId = toMemberId;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }
        }
    }
}
