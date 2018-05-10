package com.merrichat.net.adapter;

import java.io.Serializable;
import java.util.List;

/**
 * 拼团列表实体类
 * Created by amssy on 18/1/30.
 */

public class PintuanModel implements Serializable{

    /**
     * data : {"time":1517389670992,"sum":2,"list":[{"difference":1,"seriaCreateMemberUrl":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942","seriaCreateMemberId":315917552893952,"createdTime":1517312386748,"shopId":326020860076032,"serialNumber":326230124388353,"serialMember":[{"createdTime":1517312386748,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326230124388353,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}],"seriaCreateMemberName":"锦衣卫","orderId":326230124388352},{"difference":1,"seriaCreateMemberUrl":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942","seriaCreateMemberId":315917552893952,"createdTime":1517388585563,"shopId":326020860076032,"serialNumber":326389925273601,"serialMember":[{"createdTime":1517388585563,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326389925273601,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}],"seriaCreateMemberName":"锦衣卫","orderId":326389925273600}]}
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

    public static class DataBean implements Serializable{
        /**
         * time : 1517389670992
         * sum : 2
         * list : [{"difference":1,"seriaCreateMemberUrl":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942","seriaCreateMemberId":315917552893952,"createdTime":1517312386748,"shopId":326020860076032,"serialNumber":326230124388353,"serialMember":[{"createdTime":1517312386748,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326230124388353,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}],"seriaCreateMemberName":"锦衣卫","orderId":326230124388352},{"difference":1,"seriaCreateMemberUrl":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942","seriaCreateMemberId":315917552893952,"createdTime":1517388585563,"shopId":326020860076032,"serialNumber":326389925273601,"serialMember":[{"createdTime":1517388585563,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326389925273601,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}],"seriaCreateMemberName":"锦衣卫","orderId":326389925273600}]
         */

        private long time;
        private int sum;
        private List<ListBean> list;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable{
            /**
             * difference : 1
             * seriaCreateMemberUrl : http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942
             * seriaCreateMemberId : 315917552893952
             * createdTime : 1517312386748
             * shopId : 326020860076032
             * serialNumber : 326230124388353
             * serialMember : [{"createdTime":1517312386748,"memberId":315917552893952,"memberName":"锦衣卫","serialNumber":326230124388353,"url":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?1516419975942"}]
             * seriaCreateMemberName : 锦衣卫
             * orderId : 326230124388352
             */

            private int difference;
            private String seriaCreateMemberUrl;
            private long seriaCreateMemberId;
            private long createdTime;
            private long shopId;
            private long serialNumber;
            private String seriaCreateMemberName;
            private long orderId;
            private List<SerialMemberBean> serialMember;
            private long elapsedTime;

            public long getElapsedTime() {
                return elapsedTime;
            }

            public void setElapsedTime(long elapsedTime) {
                this.elapsedTime = elapsedTime;
            }

            public int getDifference() {
                return difference;
            }

            public void setDifference(int difference) {
                this.difference = difference;
            }

            public String getSeriaCreateMemberUrl() {
                return seriaCreateMemberUrl;
            }

            public void setSeriaCreateMemberUrl(String seriaCreateMemberUrl) {
                this.seriaCreateMemberUrl = seriaCreateMemberUrl;
            }

            public long getSeriaCreateMemberId() {
                return seriaCreateMemberId;
            }

            public void setSeriaCreateMemberId(long seriaCreateMemberId) {
                this.seriaCreateMemberId = seriaCreateMemberId;
            }

            public long getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(long createdTime) {
                this.createdTime = createdTime;
            }

            public long getShopId() {
                return shopId;
            }

            public void setShopId(long shopId) {
                this.shopId = shopId;
            }

            public long getSerialNumber() {
                return serialNumber;
            }

            public void setSerialNumber(long serialNumber) {
                this.serialNumber = serialNumber;
            }

            public String getSeriaCreateMemberName() {
                return seriaCreateMemberName;
            }

            public void setSeriaCreateMemberName(String seriaCreateMemberName) {
                this.seriaCreateMemberName = seriaCreateMemberName;
            }

            public long getOrderId() {
                return orderId;
            }

            public void setOrderId(long orderId) {
                this.orderId = orderId;
            }

            public List<SerialMemberBean> getSerialMember() {
                return serialMember;
            }

            public void setSerialMember(List<SerialMemberBean> serialMember) {
                this.serialMember = serialMember;
            }

            public static class SerialMemberBean implements Serializable{
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
}
