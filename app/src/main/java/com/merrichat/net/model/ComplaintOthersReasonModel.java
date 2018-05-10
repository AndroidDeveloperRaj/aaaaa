package com.merrichat.net.model;

import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/5.
 */

public class ComplaintOthersReasonModel {
    private final String reasonName;
    /**
     * data : [{"createTime":1512476689783,"number":2,"typeName":"邪教"},{"createTime":1512476689783,"number":1,"typeName":"暴力"}]
     * success : true
     */

    private boolean success;
    private List<DataBean> data;


    public ComplaintOthersReasonModel(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getReasonName() {
        return reasonName;
    }

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
         * createTime : 1512476689783
         * number : 2
         * typeName : 邪教
         */
        private boolean isChecked;
        private long createTime;
        private int number;
        private String typeName;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}
