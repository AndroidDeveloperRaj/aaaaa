package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 2018/2/7.
 */

public class ServiceFeeModel implements Serializable{


    /**
     * data : [{"b2CValue":"1","c2CValue":"1","feeType":0,"id":325658272980992,"personalValue":"1"},{"b2CValue":"0.02","c2CValue":"0.02","feeType":1,"id":325658296049664,"personalValue":"0.02"},{"b2CValue":"0.03","c2CValue":"0.03","feeType":2,"id":325658323312640,"personalValue":"0.03"},{"b2CValue":"0.06","c2CValue":"0.06","feeType":5,"id":325658352672768,"personalValue":"0.06"},{"b2CValue":"0.05","c2CValue":"{\"min\":\"0.33\",\"max\":\"0.44\"}","feeType":4,"id":325658354769920,"personalValue":"0.05"},{"b2CValue":"0.04","c2CValue":"{\"min\":\"0.11\",\"max\":\"0.22\"}","feeType":3,"id":325658356867072,"personalValue":"0.04"}]
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
         * b2CValue : 1
         * c2CValue : 1
         * feeType : 0
         * id : 325658272980992
         * personalValue : 1
         */

        private String b2CValue;
        private String c2CValue;
        private int feeType;
        private long id;
        private String personalValue;

        public String getB2CValue() {
            return b2CValue;
        }

        public void setB2CValue(String b2CValue) {
            this.b2CValue = b2CValue;
        }

        public String getC2CValue() {
            return c2CValue;
        }

        public void setC2CValue(String c2CValue) {
            this.c2CValue = c2CValue;
        }

        public int getFeeType() {
            return feeType;
        }

        public void setFeeType(int feeType) {
            this.feeType = feeType;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPersonalValue() {
            return personalValue;
        }

        public void setPersonalValue(String personalValue) {
            this.personalValue = personalValue;
        }
    }
}
