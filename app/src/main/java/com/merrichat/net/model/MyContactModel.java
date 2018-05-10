package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/12/4.
 */

public class MyContactModel implements Serializable {

    /**
     * data : {"attentionGoodFriendsRelations":[{"argeeTime":1512372100656,"goodFriendsId":302466141044737,"goodFriendsName":"Just_doing","goodFriendsUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg","id":315869593722881,"isValid":1,"memberId":315860514553857},{"argeeTime":1512372173888,"goodFriendsId":302466141044737,"goodFriendsName":"Just_doing","goodFriendsUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg","id":315869746487297,"isValid":1,"memberId":315860514553857}],"num":""}
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

    public static class DataBean implements Serializable {
        /**
         * attentionGoodFriendsRelations : [{"argeeTime":1512372100656,"goodFriendsId":302466141044737,"goodFriendsName":"Just_doing","goodFriendsUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg","id":315869593722881,"isValid":1,"memberId":315860514553857},{"argeeTime":1512372173888,"goodFriendsId":302466141044737,"goodFriendsName":"Just_doing","goodFriendsUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg","id":315869746487297,"isValid":1,"memberId":315860514553857}]
         * num :
         */

        private String num;
        private List<AttentionGoodFriendsRelationsBean> attentionGoodFriendsRelations;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public List<AttentionGoodFriendsRelationsBean> getAttentionGoodFriendsRelations() {
            return attentionGoodFriendsRelations;
        }

        public void setAttentionGoodFriendsRelations(List<AttentionGoodFriendsRelationsBean> attentionGoodFriendsRelations) {
            this.attentionGoodFriendsRelations = attentionGoodFriendsRelations;
        }

        public static class AttentionGoodFriendsRelationsBean implements Serializable {
            /**
             * goodFriendsId : 302466141044737
             * goodFriendsName : Just_doing
             * goodFriendsUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg
             */
            private long goodFriendsId;
            private String goodFriendsName;
            private String goodFriendsUrl;

            public long getGoodFriendsId() {
                return goodFriendsId;
            }

            public void setGoodFriendsId(long goodFriendsId) {
                this.goodFriendsId = goodFriendsId;
            }

            public String getGoodFriendsName() {
                return goodFriendsName;
            }

            public void setGoodFriendsName(String goodFriendsName) {
                this.goodFriendsName = goodFriendsName;
            }

            public String getGoodFriendsUrl() {
                return goodFriendsUrl;
            }

            public void setGoodFriendsUrl(String goodFriendsUrl) {
                this.goodFriendsUrl = goodFriendsUrl;
            }

        }
    }
}
