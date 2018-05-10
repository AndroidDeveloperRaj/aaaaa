package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/12/2.
 */

public class NewFriendsModel implements Serializable {

    /**
     * data : {"invitationRecords":[{"createTime":1512195892865,"flag":0,"id":315500058763264,"inviteMemberId":302472807890945,"inviteMemberName":"潇潇","inviteMemberUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","isValid":0,"source":"","toInviteMemberId":302466141044737,"toInviteMemberName":"老鳖","toInviteMemberUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg"}]}
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
        private List<InvitationRecordsBean> invitationRecords;

        public List<InvitationRecordsBean> getInvitationRecords() {
            return invitationRecords;
        }

        public void setInvitationRecords(List<InvitationRecordsBean> invitationRecords) {
            this.invitationRecords = invitationRecords;
        }

        public static class InvitationRecordsBean implements Serializable {
            /**
             * createTime : 1512195892865
             * flag : 0
             * id : 315500058763264
             * inviteMemberId : 302472807890945
             * inviteMemberName : 潇潇
             * inviteMemberUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg
             * isValid : 0
             * source :
             */
            private long createTime;
            private int flag;
            private long id;
            private long inviteMemberId;
            private String inviteMemberName;
            private String inviteMemberUrl;
            private int isValid;
            private String source;

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getInviteMemberId() {
                return inviteMemberId;
            }

            public void setInviteMemberId(long inviteMemberId) {
                this.inviteMemberId = inviteMemberId;
            }

            public String getInviteMemberName() {
                return inviteMemberName;
            }

            public void setInviteMemberName(String inviteMemberName) {
                this.inviteMemberName = inviteMemberName;
            }

            public String getInviteMemberUrl() {
                return inviteMemberUrl;
            }

            public void setInviteMemberUrl(String inviteMemberUrl) {
                this.inviteMemberUrl = inviteMemberUrl;
            }

            public int getIsValid() {
                return isValid;
            }

            public void setIsValid(int isValid) {
                this.isValid = isValid;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }
        }
    }
}
