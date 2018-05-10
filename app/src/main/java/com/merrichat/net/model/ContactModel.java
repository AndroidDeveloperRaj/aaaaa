package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/11/30.
 */

public class ContactModel implements Serializable {

    /**
     * data : {"list":[{"flag":0,"goodFriendsName":"","hasRegister":false,"headUrl":"","memberId":"","mobile":"106575258192144","nick":"钉钉官方短信"},{"flag":0,"goodFriendsName":"","hasRegister":false,"headUrl":"","memberId":"","mobile":"057156215888","nick":"钉钉专属顾问"},{"flag":1,"goodFriendsName":"","hasRegister":true,"headUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302466141044737.jpg","memberId":302466141044737,"mobile":"13167547773","nick":"老鳖"}]}
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
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable {
            /**
             * flag : 0
             * goodFriendsName :
             * hasRegister : false
             * headUrl :
             * inviteId :
             * memberId :
             * message : 123产品订内容！
             * mobile : 106575258192144
             * nick : 钉钉官方短信
             * KeyWords:搜索关键字
             */

            private int flag;
            private String goodFriendsName;
            private boolean hasRegister;
            private String headUrl;
            private String inviteId;
            private String memberId;
            private String message;
            private String mobile;
            private String nick;
            private String keyWords;

            public String getKeyWords() {
                return keyWords;
            }

            public void setKeyWords(String keyWords) {
                this.keyWords = keyWords;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public String getGoodFriendsName() {
                return goodFriendsName;
            }

            public void setGoodFriendsName(String goodFriendsName) {
                this.goodFriendsName = goodFriendsName;
            }

            public boolean isHasRegister() {
                return hasRegister;
            }

            public void setHasRegister(boolean hasRegister) {
                this.hasRegister = hasRegister;
            }

            public String getHeadUrl() {
                return headUrl;
            }

            public void setHeadUrl(String headUrl) {
                this.headUrl = headUrl;
            }

            public String getInviteId() {
                return inviteId;
            }

            public void setInviteId(String inviteId) {
                this.inviteId = inviteId;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }


        }
    }
}
