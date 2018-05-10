package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/8.
 */

public class BonusReportModel implements Serializable {
    /**
     * data : {"bonusReports":[{"acountId":"292321952579584","amount":123,"createTime":1506072199156,"currentMonth":"2017-11","currentYear":"2017","id":292321952579584,"memberId":292321952579584,"month":"11","source":"ceshi","tax":21,"taxFlag":0,"updateTime":1506072199156}],"img":"http://igomopub.igomot.net/clip-pub/img/profit.png"}
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
         * bonusReports : [{"acountId":"292321952579584","amount":123,"createTime":1506072199156,"currentMonth":"2017-11","currentYear":"2017","id":292321952579584,"memberId":292321952579584,"month":"11","source":"ceshi","tax":21,"taxFlag":0,"updateTime":1506072199156}]
         * img : http://igomopub.igomot.net/clip-pub/img/profit.png
         */

        private String img;
        private List<BonusReportsBean> bonusReports;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public List<BonusReportsBean> getBonusReports() {
            return bonusReports;
        }

        public void setBonusReports(List<BonusReportsBean> bonusReports) {
            this.bonusReports = bonusReports;
        }

        public static class BonusReportsBean implements Serializable {
            /**
             * acountId : 292321952579584
             * amount : 123.0
             * createTime : 1506072199156
             * currentMonth : 2017-11
             * currentYear : 2017
             * id : 292321952579584
             * memberId : 292321952579584
             * month : 11
             * source : ceshi
             * tax : 21.0
             * taxFlag : 0
             * updateTime : 1506072199156
             */

            private String acountId;
            private double amount;
            private long createTime;
            private String currentMonth;
            private String currentYear;
            private long id;
            private long memberId;
            private String month;
            private String source;
            private double tax;
            private int taxFlag;
            private long updateTime;

            public String getAcountId() {
                return acountId;
            }

            public void setAcountId(String acountId) {
                this.acountId = acountId;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
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

            public String getCurrentYear() {
                return currentYear;
            }

            public void setCurrentYear(String currentYear) {
                this.currentYear = currentYear;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getMonth() {
                return month;
            }

            public void setMonth(String month) {
                this.month = month;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public double getTax() {
                return tax;
            }

            public void setTax(double tax) {
                this.tax = tax;
            }

            public int getTaxFlag() {
                return taxFlag;
            }

            public void setTaxFlag(int taxFlag) {
                this.taxFlag = taxFlag;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }
    }
}
