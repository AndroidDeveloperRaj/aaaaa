package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/15.
 */

public class MyFansModel {

    /**
     * data : [{"attention":0,"createTime":"","fans":0,"fromMemberId":317831596261376,"headUrl":"http://cas.okdit.net/nfs_data/mob/head/317831596261376.jpg","id":317852501270528,"introduction":"","toMemberId":315917552893952,"toMemberName":"15010222938"}]
     * success : true
     */

    private boolean success;
    private List<DataBean> data;

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
         * attention : 0
         * createTime :
         * fans : 0
         * fromMemberId : 317831596261376
         * headUrl : http://cas.okdit.net/nfs_data/mob/head/317831596261376.jpg
         * id : 317852501270528
         * introduction :
         * toMemberId : 315917552893952
         * toMemberName : 15010222938
         */

        private int attention;
        private String createTime;
        private int fans;
        private long fromMemberId;
        private String headUrl;
        private long id;
        private String introduction;
        private long toMemberId;
        private String toMemberName;

        public int getAttention() {
            return attention;
        }

        public void setAttention(int attention) {
            this.attention = attention;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getFans() {
            return fans;
        }

        public void setFans(int fans) {
            this.fans = fans;
        }

        public long getFromMemberId() {
            return fromMemberId;
        }

        public void setFromMemberId(long fromMemberId) {
            this.fromMemberId = fromMemberId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public long getToMemberId() {
            return toMemberId;
        }

        public void setToMemberId(long toMemberId) {
            this.toMemberId = toMemberId;
        }

        public String getToMemberName() {
            return toMemberName;
        }

        public void setToMemberName(String toMemberName) {
            this.toMemberName = toMemberName;
        }
    }
}
