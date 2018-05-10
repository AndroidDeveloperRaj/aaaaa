package com.merrichat.net.model;

import java.util.List;

/**
 * Created by amssy on 17/12/18.
 */

public class ResonModel {

    /**
     * data : [{"createTime":1513164278106,"id":317530911399936,"number":1,"reasonText":"违规帖子","type":1,"updateTime":""}]
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
         * createTime : 1513164278106
         * id : 317530911399936
         * number : 1
         * reasonText : 违规帖子
         * type : 1
         * updateTime :
         */

        private long createTime;
        private long id;
        private int number;
        private String reasonText;
        private int type;
        private String updateTime;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getReasonText() {
            return reasonText;
        }

        public void setReasonText(String reasonText) {
            this.reasonText = reasonText;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
