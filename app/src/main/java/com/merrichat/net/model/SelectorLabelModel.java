package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/20.
 */

public class SelectorLabelModel implements Serializable{

    /**
     * data : [{"createTimes":1505391382606,"lable":"电器","lableId":301229967007744,"number":2,"type":1,"updateTime":1505391382606},{"createTimes":1505391390205,"lable":"视频","lableId":301229983784960,"number":3,"type":1,"updateTime":1505391417895},{"createTimes":1505391447869,"lable":"保温桶","lableId":301230103322624,"number":4,"type":1,"updateTime":1505391447869},{"createTimes":1505391458071,"lable":"天文","lableId":301230126391296,"number":5,"type":1,"updateTime":1505391458071},{"createTimes":1505391467850,"lable":"星座","lableId":301230145265664,"number":6,"type":1,"updateTime":1505391467850},{"createTimes":1505391483949,"lable":"拍摄","lableId":301230178820096,"number":7,"type":1,"updateTime":1505391483949},{"createTimes":1505391494930,"lable":"骑行1","lableId":301230201888768,"number":8,"type":1,"updateTime":1511945838195},{"createTimes":1511945829039,"lable":"按时","lableId":314975638642688,"number":1,"type":1,"updateTime":1511945829039}]
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

    public static class DataBean implements Serializable{
        /**
         * createTimes : 1505391382606
         * lable : 电器
         * lableId : 301229967007744
         * number : 2
         * type : 1
         * updateTime : 1505391382606
         */
        private boolean isChecked;
        private long createTimes;
        private String lable;
        private long lableId;
        private int number;
        private int type;
        private long updateTime;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public long getCreateTimes() {
            return createTimes;
        }

        public void setCreateTimes(long createTimes) {
            this.createTimes = createTimes;
        }

        public String getLable() {
            return lable;
        }

        public void setLable(String lable) {
            this.lable = lable;
        }

        public long getLableId() {
            return lableId;
        }

        public void setLableId(long lableId) {
            this.lableId = lableId;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }
}
