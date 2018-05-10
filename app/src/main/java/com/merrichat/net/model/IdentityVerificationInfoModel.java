package com.merrichat.net.model;

/**
 * Created by AMSSY1 on 2017/12/1.
 */

public class IdentityVerificationInfoModel {
    /**
     * data : {"data":{"address":"河北衡水","birthday":162712121212,"checkInfo":"","checkStatus":"0","createTime":1511407819817,"gender":1,"id":313847334404096,"identityCard":41672887872323223,"imageUrl":"www.baidu.com","memberId":1234567,"mobile":"15082983923","modifyTime":1511407821639,"name":"张梦","nation":"汉"},"info":"正在审核,请等待审核结果","status":"0","success":true}
     * success : true
     */

    private DataBeanX data;
    private boolean success;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBeanX {
        /**
         * data : {"address":"河北衡水","birthday":162712121212,"checkInfo":"","checkStatus":"0","createTime":1511407819817,"gender":1,"id":313847334404096,"identityCard":41672887872323223,"imageUrl":"www.baidu.com","memberId":1234567,"mobile":"15082983923","modifyTime":1511407821639,"name":"张梦","nation":"汉"}
         * info : 正在审核,请等待审核结果
         * status : 0
         * success : true
         */

        private DataBean data;
        private String info;
        private String status;
        private boolean success;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public static class DataBean {
            /**
             * address : 河北衡水
             * birthday : 162712121212
             * checkInfo :
             * checkStatus : 0
             * createTime : 1511407819817
             * gender : 1
             * id : 313847334404096
             * identityCard : 41672887872323223
             * imageUrl : www.baidu.com
             * memberId : 1234567
             * mobile : 15082983923
             * modifyTime : 1511407821639
             * name : 张梦
             * nation : 汉
             */

            private String address;
            private String birthday;
            private String checkInfo;
            private String checkStatus;
            private String createTime;
            private String gender;
            private String id;
            private String identityCard;
            private String imageUrl;
            private String memberId;
            private String mobile;
            private String modifyTime;
            private String name;
            private String nation;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getCheckInfo() {
                return checkInfo;
            }

            public void setCheckInfo(String checkInfo) {
                this.checkInfo = checkInfo;
            }

            public String getCheckStatus() {
                return checkStatus;
            }

            public void setCheckStatus(String checkStatus) {
                this.checkStatus = checkStatus;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIdentityCard() {
                return identityCard;
            }

            public void setIdentityCard(String identityCard) {
                this.identityCard = identityCard;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getModifyTime() {
                return modifyTime;
            }

            public void setModifyTime(String modifyTime) {
                this.modifyTime = modifyTime;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNation() {
                return nation;
            }

            public void setNation(String nation) {
                this.nation = nation;
            }
        }
    }
}
