package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2018/1/19.
 */

public class GroupListModel {


    /**
     * data : {"message":"","list":[{"communityName":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg"}],"success":true}
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
         * list : [{"communityName":"阿尔法","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg"}]
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
             * communityName : 阿尔法
             * communityId : 325471074902016
             * communityImgUrl : http://igomopub.igomot.net/nfs_data/igomo/groupOf/325471074902016.jpg
             */

            private String communityName;
            private String communityId;
            private String communityImgUrl;
            private String accountId;//群钱包id
            private String isMaster;//是否是管理员新加 0:成员 1:管理员 2:群主

            public String getAccountId() {
                return accountId;
            }

            public void setAccountId(String accountId) {
                this.accountId = accountId;
            }

            public String getIsMaster() {
                return isMaster;
            }

            public void setIsMaster(String isMaster) {
                this.isMaster = isMaster;
            }

            public String getCommunityName() {
                return communityName;
            }

            public void setCommunityName(String communityName) {
                this.communityName = communityName;
            }

            public String getCommunityImgUrl() {
                return communityImgUrl;
            }

            public void setCommunityImgUrl(String communityImgUrl) {
                this.communityImgUrl = communityImgUrl;
            }

            public String getCommunityId() {
                return communityId;
            }

            public void setCommunityId(String communityId) {
                this.communityId = communityId;
            }
        }
    }
}
