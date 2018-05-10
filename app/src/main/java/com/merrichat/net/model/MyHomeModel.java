package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/27.
 */

public class MyHomeModel implements Serializable {


    /**
     * data : {"charm":0,"balance":"","incomeRecord":"0","addFans":0,"attenCount":0,"fansCount":0,"myPeerNum":0,"info":{"currentCounty":"海淀","currentProvince":"北京","videoStatus":0,"birthday":"","createTime":1512394969785,"constellation":"","accountId":157958776351388672,"weight":0,"religion":"","meetStatus":0,"temporaryAddress":"北京北京","permitFriend":0,"height":0,"nickName":"今天是","age":0,"eduBackGround":"","gender":1,"modifyTime":1512394969785,"signature":"","eduExperience":[],"emotionalStatus":"","occupation":"","hometown":"","permitUnfamiliar":0,"currentCity":"北京","nation":"","imgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","bloodType":"","currentAddress":"北京北京","workExperience":[],"company":"","memberId":315917552893952,"realname":"","mobile":"13167547773"}}
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
         * charm : 0
         * balance :
         * incomeRecord : 0
         * addFans : 0
         * attenCount : 0
         * fansCount : 0
         * myPeerNum : 0
         * info : {"currentCounty":"海淀","currentProvince":"北京","videoStatus":0,"birthday":"","createTime":1512394969785,"constellation":"","accountId":157958776351388672,"weight":0,"religion":"","meetStatus":0,"temporaryAddress":"北京北京","permitFriend":0,"height":0,"nickName":"今天是","age":0,"eduBackGround":"","gender":1,"modifyTime":1512394969785,"signature":"","eduExperience":[],"emotionalStatus":"","occupation":"","hometown":"","permitUnfamiliar":0,"currentCity":"北京","nation":"","imgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","bloodType":"","currentAddress":"北京北京","workExperience":[],"company":"","memberId":315917552893952,"realname":"","mobile":"13167547773"}
         */

        private int charm;
        private int myDynamicNum;
        private String balance;
        private String incomeRecord;
        private int addFans;
        private int attenCount;
        private int fansCount;
        private int myPeerNum;
        private InfoBean info;

        public int getMyDynamicNum() {
            return myDynamicNum;
        }

        public void setMyDynamicNum(int myDynamicNum) {
            this.myDynamicNum = myDynamicNum;
        }

        public int getCharm() {
            return charm;
        }

        public void setCharm(int charm) {
            this.charm = charm;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getIncomeRecord() {
            return incomeRecord;
        }

        public void setIncomeRecord(String incomeRecord) {
            this.incomeRecord = incomeRecord;
        }

        public int getAddFans() {
            return addFans;
        }

        public void setAddFans(int addFans) {
            this.addFans = addFans;
        }

        public int getAttenCount() {
            return attenCount;
        }

        public void setAttenCount(int attenCount) {
            this.attenCount = attenCount;
        }

        public int getFansCount() {
            return fansCount;
        }

        public void setFansCount(int fansCount) {
            this.fansCount = fansCount;
        }

        public int getMyPeerNum() {
            return myPeerNum;
        }

        public void setMyPeerNum(int myPeerNum) {
            this.myPeerNum = myPeerNum;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * currentCounty : 海淀
             * currentProvince : 北京
             * videoStatus : 0
             * birthday :
             * createTime : 1512394969785
             * constellation :
             * accountId : 157958776351388672
             * weight : 0
             * religion :
             * meetStatus : 0
             * temporaryAddress : 北京北京
             * permitFriend : 0
             * height : 0
             * nickName : 今天是
             * age : 0
             * eduBackGround :
             * gender : 1
             * modifyTime : 1512394969785
             * signature :
             * eduExperience : []
             * emotionalStatus :
             * occupation :
             * hometown :
             * permitUnfamiliar : 0
             * currentCity : 北京
             * nation :
             * imgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg
             * bloodType :
             * currentAddress : 北京北京
             * workExperience : []
             * company :
             * memberId : 315917552893952
             * realname :
             * mobile : 13167547773
             */

            private String currentCounty;
            private String currentProvince;
            private int videoStatus;
            private String birthday;
            private long createTime;
            private String constellation;
            private long accountId;
            private int weight;
            private String religion;
            private int meetStatus;
            private String temporaryAddress;
            private int permitFriend;
            private int height;
            private String nickName;
            private int age;
            private String eduBackGround;
            private int gender;
            private long modifyTime;
            private String signature;
            private String emotionalStatus;
            private String occupation;
            private String hometown;
            private int permitUnfamiliar;
            private String currentCity;
            private String nation;
            private String imgUrl;
            private String bloodType;
            private String currentAddress;
            private String company;
            private long memberId;
            private String realname;
            private String mobile;
            private List<?> eduExperience;
            private List<?> workExperience;

            public String getCurrentCounty() {
                return currentCounty;
            }

            public void setCurrentCounty(String currentCounty) {
                this.currentCounty = currentCounty;
            }

            public String getCurrentProvince() {
                return currentProvince;
            }

            public void setCurrentProvince(String currentProvince) {
                this.currentProvince = currentProvince;
            }

            public int getVideoStatus() {
                return videoStatus;
            }

            public void setVideoStatus(int videoStatus) {
                this.videoStatus = videoStatus;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getConstellation() {
                return constellation;
            }

            public void setConstellation(String constellation) {
                this.constellation = constellation;
            }

            public long getAccountId() {
                return accountId;
            }

            public void setAccountId(long accountId) {
                this.accountId = accountId;
            }

            public int getWeight() {
                return weight;
            }

            public void setWeight(int weight) {
                this.weight = weight;
            }

            public String getReligion() {
                return religion;
            }

            public void setReligion(String religion) {
                this.religion = religion;
            }

            public int getMeetStatus() {
                return meetStatus;
            }

            public void setMeetStatus(int meetStatus) {
                this.meetStatus = meetStatus;
            }

            public String getTemporaryAddress() {
                return temporaryAddress;
            }

            public void setTemporaryAddress(String temporaryAddress) {
                this.temporaryAddress = temporaryAddress;
            }

            public int getPermitFriend() {
                return permitFriend;
            }

            public void setPermitFriend(int permitFriend) {
                this.permitFriend = permitFriend;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public String getEduBackGround() {
                return eduBackGround;
            }

            public void setEduBackGround(String eduBackGround) {
                this.eduBackGround = eduBackGround;
            }

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public long getModifyTime() {
                return modifyTime;
            }

            public void setModifyTime(long modifyTime) {
                this.modifyTime = modifyTime;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getEmotionalStatus() {
                return emotionalStatus;
            }

            public void setEmotionalStatus(String emotionalStatus) {
                this.emotionalStatus = emotionalStatus;
            }

            public String getOccupation() {
                return occupation;
            }

            public void setOccupation(String occupation) {
                this.occupation = occupation;
            }

            public String getHometown() {
                return hometown;
            }

            public void setHometown(String hometown) {
                this.hometown = hometown;
            }

            public int getPermitUnfamiliar() {
                return permitUnfamiliar;
            }

            public void setPermitUnfamiliar(int permitUnfamiliar) {
                this.permitUnfamiliar = permitUnfamiliar;
            }

            public String getCurrentCity() {
                return currentCity;
            }

            public void setCurrentCity(String currentCity) {
                this.currentCity = currentCity;
            }

            public String getNation() {
                return nation;
            }

            public void setNation(String nation) {
                this.nation = nation;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getBloodType() {
                return bloodType;
            }

            public void setBloodType(String bloodType) {
                this.bloodType = bloodType;
            }

            public String getCurrentAddress() {
                return currentAddress;
            }

            public void setCurrentAddress(String currentAddress) {
                this.currentAddress = currentAddress;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public long getMemberId() {
                return memberId;
            }

            public void setMemberId(long memberId) {
                this.memberId = memberId;
            }

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public List<?> getEduExperience() {
                return eduExperience;
            }

            public void setEduExperience(List<?> eduExperience) {
                this.eduExperience = eduExperience;
            }

            public List<?> getWorkExperience() {
                return workExperience;
            }

            public void setWorkExperience(List<?> workExperience) {
                this.workExperience = workExperience;
            }
        }
    }
}
