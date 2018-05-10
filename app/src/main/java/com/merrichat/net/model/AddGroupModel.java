package com.merrichat.net.model;

import java.util.List;

/**
 * Created by amssy on 18/1/27.
 */

public class AddGroupModel {

    /**
     * data : {"message":"","list":[{"distance":"0米","address":"北京香格里拉饭店西137米","communityNotice":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg","communityName":"阿尔法","type":"","isJoin":2,"communityId":325471074902016},{"distance":"0米","address":"北京香格里拉饭店西140米","communityNotice":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325472335290368.jpg","communityName":"阿尔法","type":"","isJoin":2,"communityId":325472335290368}],"success":true}
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
         * message :
         * list : [{"distance":"0米","address":"北京香格里拉饭店西137米","communityNotice":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg","communityName":"阿尔法","type":"","isJoin":2,"communityId":325471074902016},{"distance":"0米","address":"北京香格里拉饭店西140米","communityNotice":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325472335290368.jpg","communityName":"阿尔法","type":"","isJoin":2,"communityId":325472335290368}]
         * success : true
         */

        private String message;
        private boolean success;
        private List<ListBean> list;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * distance : 0米
             * address : 北京香格里拉饭店西137米
             * communityNotice : 阿尔法
             * communityImgUrl : http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg
             * communityName : 阿尔法
             * type :
             * isJoin : 2
             * communityId : 325471074902016
             */

            private String distance;
            private String address;
            private String communityNotice;
            private String communityImgUrl;
            private String communityName;
            private String type;
            private int isJoin;
            private long communityId;

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getCommunityNotice() {
                return communityNotice;
            }

            public void setCommunityNotice(String communityNotice) {
                this.communityNotice = communityNotice;
            }

            public String getCommunityImgUrl() {
                return communityImgUrl;
            }

            public void setCommunityImgUrl(String communityImgUrl) {
                this.communityImgUrl = communityImgUrl;
            }

            public String getCommunityName() {
                return communityName;
            }

            public void setCommunityName(String communityName) {
                this.communityName = communityName;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getIsJoin() {
                return isJoin;
            }

            public void setIsJoin(int isJoin) {
                this.isJoin = isJoin;
            }

            public long getCommunityId() {
                return communityId;
            }

            public void setCommunityId(long communityId) {
                this.communityId = communityId;
            }
        }
    }
}
