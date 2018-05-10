package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/8.
 */

public class BonusRecordModel {
    /**
     * data : {"bonusRecords":[{"amount":123,"createTime":1506244999156,"currentMonth":"2017-11","goodFriendsId":7789760,"goodFriendsName":"","goodFriendsUrl":"","grade":1,"gradeStr":"直接奖励","id":999999999,"memberId":292321952579584,"orderId":18889988,"sourceStr":"打赏","tid":"292777887700","type":4},{"amount":123,"createTime":1506072199156,"currentMonth":"2017-11","goodFriendsId":222200,"goodFriendsName":"","goodFriendsUrl":"","grade":0,"gradeStr":"好友送的","id":88888888,"memberId":292321952579584,"orderId":111999,"sourceStr":"打赏","tid":"292321950000","type":4}]}
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

    public  class DataBean {
        private List<BonusRecordsBean> bonusRecords;

        public List<BonusRecordsBean> getBonusRecords() {
            return bonusRecords;
        }

        public void setBonusRecords(List<BonusRecordsBean> bonusRecords) {
            this.bonusRecords = bonusRecords;
        }

        public  class BonusRecordsBean {
            /**
             * amount : 123.0
             * createTime : 1506244999156
             * currentMonth : 2017-11
             * goodFriendsId : 7789760
             * goodFriendsName :
             * goodFriendsUrl :
             * grade : 1
             * gradeStr : 直接奖励
             * id : 999999999
             * memberId : 292321952579584
             * orderId : 18889988
             * sourceStr : 打赏
             * tid : 292777887700
             * type : 4
             */

            private String amount;
            private long createTime;
            private String currentMonth;
            private String goodFriendsId;
            private String goodFriendsName;
            private String goodFriendsUrl;
            private int grade;
            private String gradeStr;
            private String id;
            private long memberId;
            private String orderId;
            private String sourceStr;
            private String tid;
            private int type;

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getCurrentMonth() {
                return currentMonth;
            }

            public void setCurrentMonth(String currentMonth) {
                this.currentMonth = currentMonth;
            }

            public String getGoodFriendsId() {
                return goodFriendsId;
            }

            public void setGoodFriendsId(String goodFriendsId) {
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

            public int getGrade() {
                return grade;
            }

            public void setGrade(int grade) {
                this.grade = grade;
            }

            public String getGradeStr() {
                return gradeStr;
            }

            public void setGradeStr(String gradeStr) {
                this.gradeStr = gradeStr;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getSourceStr() {
                return sourceStr;
            }

            public void setSourceStr(String sourceStr) {
                this.sourceStr = sourceStr;
            }

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }
}
