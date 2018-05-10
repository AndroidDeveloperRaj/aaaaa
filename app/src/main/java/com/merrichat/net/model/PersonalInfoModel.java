package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2017/11/27.
 */

public class PersonalInfoModel implements Serializable{

    /**
     * data : {"info":{"accountId":1234567,"birthday":"2017-7-1","bloodType":"你猜","company":"北京好递","currentAddress":"北京海淀","currentCity":"北京","currentCounty":"","currentProvince":"","eduBackGround":"","eduExperience":[{"startTime":868888,"eduId":317883754450944,"school":"清华附中","profession":"理科","eduBackGround":"高中","endTime":232323233},{},{"startTime":868888,"eduId":313487307026432,"school":"清华大学","profession":"电子信息","eduBackGround":"本科","endTime":232323233}],"emotionalStatus":"","gender":1,"height":213,"hometown":"","imgUrl":"","memberId":1234567,"mobile":"","nation":"","nickName":"11","occupation":"","realname":"","religion":"","signature":"","weight":"","workExperience":[{"startTime":3125639,"occupation":"开发部","company":"好递","workId":3136347642032128,"endTime":3125689},{}]}}
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
         * info : {"accountId":1234567,"birthday":"2017-7-1","bloodType":"你猜","company":"北京好递","currentAddress":"北京海淀","currentCity":"北京","currentCounty":"","currentProvince":"","eduBackGround":"","eduExperience":[{"startTime":868888,"eduId":317883754450944,"school":"清华附中","profession":"理科","eduBackGround":"高中","endTime":232323233},{},{"startTime":868888,"eduId":313487307026432,"school":"清华大学","profession":"电子信息","eduBackGround":"本科","endTime":232323233}],"emotionalStatus":"","gender":1,"height":213,"hometown":"","imgUrl":"","memberId":1234567,"mobile":"","nation":"","nickName":"11","occupation":"","realname":"","religion":"","signature":"","weight":"","workExperience":[{"startTime":3125639,"occupation":"开发部","company":"好递","workId":3136347642032128,"endTime":3125689},{}]}
         */

        private InfoBean info;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean implements Serializable{
            /**
             * accountId : 1234567
             * birthday : 2017-7-1
             * bloodType : 你猜
             * company : 北京好递
             * currentAddress : 北京海淀
             * currentCity : 北京
             * currentCounty :
             * currentProvince :
             * eduBackGround :
             * eduExperience : [{"startTime":868888,"eduId":317883754450944,"school":"清华附中","profession":"理科","eduBackGround":"高中","endTime":232323233},{},{"startTime":868888,"eduId":313487307026432,"school":"清华大学","profession":"电子信息","eduBackGround":"本科","endTime":232323233}]
             * emotionalStatus :
             * gender : 1
             * height : 213
             * hometown :
             * imgUrl :
             * memberId : 1234567
             * mobile :
             * nation :
             * nickName : 11
             * occupation :
             * realname :
             * religion :
             * signature :
             * weight :
             * workExperience : [{"startTime":3125639,"occupation":"开发部","company":"好递","workId":3136347642032128,"endTime":3125689},{}]
             */

            private String accountId;
            private String birthday;
            private String bloodType;
            private String company;
            private String currentAddress;
            private String currentCity;
            private String currentCounty;
            private String currentProvince;
            private String eduBackGround;
            private String emotionalStatus;
            private String gender;
            private String height;
            private String hometown;
            private String imgUrl;
            private String memberId;
            private String mobile;
            private String nation;
            private String nickName;
            private String occupation;
            private String realname;
            private String religion;
            private String signature;
            private String weight;
            private List<EduExperienceBean> eduExperience;
            private List<WorkExperienceBean> workExperience;

            public String getAccountId() {
                return accountId;
            }

            public void setAccountId(String accountId) {
                this.accountId = accountId;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getBloodType() {
                return bloodType;
            }

            public void setBloodType(String bloodType) {
                this.bloodType = bloodType;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getCurrentAddress() {
                return currentAddress;
            }

            public void setCurrentAddress(String currentAddress) {
                this.currentAddress = currentAddress;
            }

            public String getCurrentCity() {
                return currentCity;
            }

            public void setCurrentCity(String currentCity) {
                this.currentCity = currentCity;
            }

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

            public String getEduBackGround() {
                return eduBackGround;
            }

            public void setEduBackGround(String eduBackGround) {
                this.eduBackGround = eduBackGround;
            }

            public String getEmotionalStatus() {
                return emotionalStatus;
            }

            public void setEmotionalStatus(String emotionalStatus) {
                this.emotionalStatus = emotionalStatus;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getHometown() {
                return hometown;
            }

            public void setHometown(String hometown) {
                this.hometown = hometown;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
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

            public String getNation() {
                return nation;
            }

            public void setNation(String nation) {
                this.nation = nation;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getOccupation() {
                return occupation;
            }

            public void setOccupation(String occupation) {
                this.occupation = occupation;
            }

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            public String getReligion() {
                return religion;
            }

            public void setReligion(String religion) {
                this.religion = religion;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getWeight() {
                return weight;
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public List<EduExperienceBean> getEduExperience() {
                return eduExperience;
            }

            public void setEduExperience(List<EduExperienceBean> eduExperience) {
                this.eduExperience = eduExperience;
            }

            public List<WorkExperienceBean> getWorkExperience() {
                return workExperience;
            }

            public void setWorkExperience(List<WorkExperienceBean> workExperience) {
                this.workExperience = workExperience;
            }

            public static class EduExperienceBean implements Serializable{
                /**
                 * startTime : 868888
                 * eduId : 317883754450944
                 * school : 清华附中
                 * profession : 理科
                 * eduBackGround : 高中
                 * endTime : 232323233
                 */

                private String startTime;
                private String eduId;
                private String school;
                private String profession;
                private String eduBackGround;
                private String endTime;

                public String getStartTime() {
                    return startTime;
                }

                public void setStartTime(String startTime) {
                    this.startTime = startTime;
                }

                public String getEduId() {
                    return eduId;
                }

                public void setEduId(String eduId) {
                    this.eduId = eduId;
                }

                public String getSchool() {
                    return school;
                }

                public void setSchool(String school) {
                    this.school = school;
                }

                public String getProfession() {
                    return profession;
                }

                public void setProfession(String profession) {
                    this.profession = profession;
                }

                public String getEduBackGround() {
                    return eduBackGround;
                }

                public void setEduBackGround(String eduBackGround) {
                    this.eduBackGround = eduBackGround;
                }

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }
            }

            public static class WorkExperienceBean implements Serializable{
                /**
                 * startTime : 3125639
                 * occupation : 开发部
                 * company : 好递
                 * workId : 3136347642032128
                 * endTime : 3125689
                 */

                private String startTime;
                private String occupation;
                private String company;
                private String workId;
                private String endTime;

                public String getStartTime() {
                    return startTime;
                }

                public void setStartTime(String startTime) {
                    this.startTime = startTime;
                }

                public String getOccupation() {
                    return occupation;
                }

                public void setOccupation(String occupation) {
                    this.occupation = occupation;
                }

                public String getCompany() {
                    return company;
                }

                public void setCompany(String company) {
                    this.company = company;
                }

                public String getWorkId() {
                    return workId;
                }

                public void setWorkId(String workId) {
                    this.workId = workId;
                }

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }
            }
        }
    }
}
