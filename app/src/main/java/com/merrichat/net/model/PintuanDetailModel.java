package com.merrichat.net.model;

import java.util.List;

/**
 * Created by amssy on 18/1/31.
 */

public class PintuanDetailModel {

    /**
     * data : {"time":1517401188555,"serialTime":1517312394801,"serialMember":[{"createdTime":1517312386748,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326230124388353,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}],"size":1}
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
         * time : 1517401188555
         * serialTime : 1517312394801
         * serialMember : [{"createdTime":1517312386748,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326230124388353,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}]
         * size : 1
         */

        private long time;
        private long serialTime;
        private int flag;
        private int sum;

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        private int size;
        private List<SerialMemberBean> serialMember;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getSerialTime() {
            return serialTime;
        }

        public void setSerialTime(long serialTime) {
            this.serialTime = serialTime;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public List<SerialMemberBean> getSerialMember() {
            return serialMember;
        }

        public void setSerialMember(List<SerialMemberBean> serialMember) {
            this.serialMember = serialMember;
        }

        public static class SerialMemberBean {
            /**
             * createdTime : 1517312386748
             * memberId : 315917552893952
             * memberName : 锦衣卫
             * serialNumber : 326230124388353
             * url : http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942
             */

            private long createdTime;
            private long memberId;
            private String memberName;
            private long serialNumber;
            private String url;

            public long getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(long createdTime) {
                this.createdTime = createdTime;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public long getSerialNumber() {
                return serialNumber;
            }

            public void setSerialNumber(long serialNumber) {
                this.serialNumber = serialNumber;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
