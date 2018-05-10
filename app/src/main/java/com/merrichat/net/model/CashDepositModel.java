package com.merrichat.net.model;

import java.util.List;

/**
 * 群保证金实体类
 * Created by amssy on 18/2/1.
 */

public class CashDepositModel {

    /**
     * data : {"message":"","list":[{"communityName":"测试1群","communityMargin":"","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/326020860076032.jpg","communityId":326020860076032}],"success":true}
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
         * list : [{"communityName":"测试1群","communityMargin":"","communityImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/groupOf/326020860076032.jpg","communityId":326020860076032}]
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
             * communityName : 测试1群
             * communityMargin :
             * communityImgUrl : http://igomopub.igomot.net/nfs_data/igomo/groupOf/326020860076032.jpg
             * communityId : 326020860076032
             */

            private String communityName;
            private String communityMargin;
            private String communityImgUrl;
            private long communityId;

            public String getCommunityName() {
                return communityName;
            }

            public void setCommunityName(String communityName) {
                this.communityName = communityName;
            }

            public String getCommunityMargin() {
                return communityMargin;
            }

            public void setCommunityMargin(String communityMargin) {
                this.communityMargin = communityMargin;
            }

            public String getCommunityImgUrl() {
                return communityImgUrl;
            }

            public void setCommunityImgUrl(String communityImgUrl) {
                this.communityImgUrl = communityImgUrl;
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
