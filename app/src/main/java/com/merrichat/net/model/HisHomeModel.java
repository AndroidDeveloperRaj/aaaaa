package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMSSY1 on 2017/12/4.
 */

public class HisHomeModel implements Serializable {

    /**
     * data : {"charm":0,"karmaValue":4,"favorList":[],"isFriend":0,"hisMemberInfo":{"currentCounty":"海淀","currentProvince":"北京","birthday":"","createTime":1512394969785,"accountId":157958776351388672,"weight":0,"religion":"","temporaryAddress":"北京北京","height":0,"nickName":"今天是","eduBackGround":"","gender":1,"modifyTime":1512394969785,"signature":"","emotionalStatus":"","eduExperience":[],"occupation":"","hometown":"","currentCity":"北京","nation":"","imgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","bloodType":"","currentAddress":"北京北京","workExperience":[],"company":"","memberId":315917552893952,"realname":"","mobile":"13167547773"},"movieList":[{"id":317497596043264,"content":[{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}],"title":"他我就是想","cover":{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},"time":"1天前","isLike":1,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":10},{"id":317497870770176,"content":[{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},{"text":"继续取消","height":1766,"flag":0,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}],"title":"金五台子乡经","cover":{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},"time":"1天前","isLike":1,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":-3}],"movieListStatus":1}
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
         * charm : 0
         * karmaValue : 4
         * favorList : []
         * isFriend : 0
         * hisMemberInfo : {"currentCounty":"海淀","currentProvince":"北京","birthday":"","createTime":1512394969785,"accountId":157958776351388672,"weight":0,"religion":"","temporaryAddress":"北京北京","height":0,"nickName":"今天是","eduBackGround":"","gender":1,"modifyTime":1512394969785,"signature":"","emotionalStatus":"","eduExperience":[],"occupation":"","hometown":"","currentCity":"北京","nation":"","imgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","bloodType":"","currentAddress":"北京北京","workExperience":[],"company":"","memberId":315917552893952,"realname":"","mobile":"13167547773"}
         * movieList : [{"id":317497596043264,"content":[{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}],"title":"他我就是想","cover":{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},"time":"1天前","isLike":1,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":10},{"id":317497870770176,"content":[{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},{"text":"继续取消","height":1766,"flag":0,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}],"title":"金五台子乡经","cover":{"text":"体育中心","height":960,"flag":1,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"},"time":"1天前","isLike":1,"commentCounts":0,"memberId":315917552893952,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg","memberName":"","jurisdiction":1,"likeCounts":-3}]
         * movieListStatus : 1
         */

        private String charm;
        private String karmaValue;
        private String hisLike;
        private String hisFans;
        private String isFriend;
        private String friendStatus;
        private boolean attentionStatus;
        private HisMemberInfoBean hisMemberInfo;
        private String movieListStatus;
        private List<FavorListBean> favorList;
        private List<MovieListBean> movieList;

        public boolean isAttentionStatus() {
            return attentionStatus;
        }

        public void setAttentionStatus(boolean attentionStatus) {
            this.attentionStatus = attentionStatus;
        }

        public String getFriendStatus() {
            return friendStatus;
        }

        public void setFriendStatus(String friendStatus) {
            this.friendStatus = friendStatus;
        }

        public String getHisLike() {
            return hisLike;
        }

        public void setHisLike(String hisLike) {
            this.hisLike = hisLike;
        }

        public String getHisFans() {
            return hisFans;
        }

        public void setHisFans(String hisFans) {
            this.hisFans = hisFans;
        }

        public String getCharm() {
            return charm;
        }

        public void setCharm(String charm) {
            this.charm = charm;
        }

        public String getKarmaValue() {
            return karmaValue;
        }

        public void setKarmaValue(String karmaValue) {
            this.karmaValue = karmaValue;
        }

        public String getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(String isFriend) {
            this.isFriend = isFriend;
        }

        public HisMemberInfoBean getHisMemberInfo() {
            return hisMemberInfo;
        }

        public void setHisMemberInfo(HisMemberInfoBean hisMemberInfo) {
            this.hisMemberInfo = hisMemberInfo;
        }

        public String getMovieListStatus() {
            return movieListStatus;
        }

        public void setMovieListStatus(String movieListStatus) {
            this.movieListStatus = movieListStatus;
        }

        public List<FavorListBean> getFavorList() {
            return favorList;
        }

        public void setFavorList(List<FavorListBean> favorList) {
            this.favorList = favorList;
        }

        public List<MovieListBean> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<MovieListBean> movieList) {
            this.movieList = movieList;
        }

        public static class HisMemberInfoBean implements Serializable {
            /**
             * currentCounty : 海淀
             * currentProvince : 北京
             * birthday :
             * createTime : 1512394969785
             * accountId : 157958776351388672
             * weight : 0
             * religion :
             * temporaryAddress : 北京北京
             * height : 0
             * nickName : 今天是
             * eduBackGround :
             * gender : 1
             * modifyTime : 1512394969785
             * signature :
             * emotionalStatus :
             * eduExperience : []
             * occupation :
             * hometown :
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
            private String birthday;
            private String videoStatus;
            private String createTime;
            private String accountId;
            private String weight;
            private String age;
            private String constellation;
            private String religion;
            private String temporaryAddress;
            private String height;
            private String nickName;
            private String eduBackGround;
            private String gender;
            private String modifyTime;
            private String signature;
            private String emotionalStatus;
            private String occupation;
            private String hometown;
            private String currentCity;
            private String nation;
            private String imgUrl;
            private String bloodType;
            private String currentAddress;
            private String company;
            private String memberId;
            private String realname;
            private String mobile;
            private List<EduExperienceBean> eduExperience;
            private List<WorkExperienceBean> workExperience;

            public String getVideoStatus() {
                return videoStatus;
            }

            public void setVideoStatus(String videoStatus) {
                this.videoStatus = videoStatus;
            }

            public String getAge() {
                return age;
            }

            public void setAge(String age) {
                this.age = age;
            }

            public String getConstellation() {
                return constellation;
            }

            public void setConstellation(String constellation) {
                this.constellation = constellation;
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

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getAccountId() {
                return accountId;
            }

            public void setAccountId(String accountId) {
                this.accountId = accountId;
            }

            public String getWeight() {
                return weight;
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public String getReligion() {
                return religion;
            }

            public void setReligion(String religion) {
                this.religion = religion;
            }

            public String getTemporaryAddress() {
                return temporaryAddress;
            }

            public void setTemporaryAddress(String temporaryAddress) {
                this.temporaryAddress = temporaryAddress;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getEduBackGround() {
                return eduBackGround;
            }

            public void setEduBackGround(String eduBackGround) {
                this.eduBackGround = eduBackGround;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getModifyTime() {
                return modifyTime;
            }

            public void setModifyTime(String modifyTime) {
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

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
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

            public static class EduExperienceBean implements Serializable {
                /**
                 * eduBackGround : 高中
                 * eduId : 317883754450944
                 * endTime : 1511798400000
                 * profession : 提6'呀你
                 * school : 清华附中
                 * startTime : 975340800000
                 */

                private String eduBackGround;
                private String eduId;
                private String endTime;
                private String profession;
                private String school;
                private String startTime;

                public String getEduBackGround() {
                    return eduBackGround;
                }

                public void setEduBackGround(String eduBackGround) {
                    this.eduBackGround = eduBackGround;
                }

                public String getEduId() {
                    return eduId;
                }

                public void setEduId(String eduId) {
                    this.eduId = eduId;
                }

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }

                public String getProfession() {
                    return profession;
                }

                public void setProfession(String profession) {
                    this.profession = profession;
                }

                public String getSchool() {
                    return school;
                }

                public void setSchool(String school) {
                    this.school = school;
                }

                public String getStartTime() {
                    return startTime;
                }

                public void setStartTime(String startTime) {
                    this.startTime = startTime;
                }
            }

            public static class WorkExperienceBean implements Serializable {
                /**
                 * company : 好递
                 * endTime : 1448640000000
                 * occupation : 技术员
                 * startTime : -28800000
                 * workId : 3136347642032128
                 */

                private String company;
                private String endTime;
                private String occupation;
                private String startTime;
                private String workId;

                public String getCompany() {
                    return company;
                }

                public void setCompany(String company) {
                    this.company = company;
                }

                public String getEndTime() {
                    return endTime;
                }

                public void setEndTime(String endTime) {
                    this.endTime = endTime;
                }

                public String getOccupation() {
                    return occupation;
                }

                public void setOccupation(String occupation) {
                    this.occupation = occupation;
                }

                public String getStartTime() {
                    return startTime;
                }

                public void setStartTime(String startTime) {
                    this.startTime = startTime;
                }

                public String getWorkId() {
                    return workId;
                }

                public void setWorkId(String workId) {
                    this.workId = workId;
                }
            }
        }

        public static class MovieListBean implements Serializable {
            /**
             * id : 317497596043264
             * content : [{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}]
             * title : 他我就是想
             * cover : {"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}
             * time : 1天前
             * isLike : 1
             * commentCounts : 0
             * memberId : 315917552893952
             * memberImage : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg
             * memberName :
             * jurisdiction : 1
             * likeCounts : 10
             */

            private String id;
            private String title;
            private String RMBSign;
            private CoverBean cover;
            private String time;
            private int isLike;
            private int flag;
            private String commentCounts;
            private String memberId;
            private String memberImage;
            private String memberName;
            private String jurisdiction;
            private int likeCounts;
            private List<ContentBean> content;

            public String getRMBSign() {
                return RMBSign;
            }

            public void setRMBSign(String RMBSign) {
                this.RMBSign = RMBSign;
            }

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public CoverBean getCover() {
                return cover;
            }

            public void setCover(CoverBean cover) {
                this.cover = cover;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getIsLike() {
                return isLike;
            }

            public void setIsLike(
                    int isLike) {
                this.isLike = isLike;
            }

            public String getCommentCounts() {
                return commentCounts;
            }

            public void setCommentCounts(String commentCounts) {
                this.commentCounts = commentCounts;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getMemberImage() {
                return memberImage;
            }

            public void setMemberImage(String memberImage) {
                this.memberImage = memberImage;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public String getJurisdiction() {
                return jurisdiction;
            }

            public void setJurisdiction(String jurisdiction) {
                this.jurisdiction = jurisdiction;
            }

            public int getLikeCounts() {
                return likeCounts;
            }

            public void setLikeCounts(int likeCounts) {
                this.likeCounts = likeCounts;
            }

            public List<ContentBean> getContent() {
                return content;
            }

            public void setContent(List<ContentBean> content) {
                this.content = content;
            }

            public static class CoverBean implements Serializable {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private int height;
                private String flag;
                private int width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public String getFlag() {
                    return flag;
                }

                public void setFlag(String flag) {
                    this.flag = flag;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

            public static class ContentBean implements Serializable {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private String height;
                private String flag;
                private String width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public String getFlag() {
                    return flag;
                }

                public void setFlag(String flag) {
                    this.flag = flag;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }

        public static class FavorListBean implements Serializable {
            /**
             * id : 317497596043264
             * content : [{"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"},{"text":"路由器上","height":960,"flag":0,"width":640,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg"}]
             * title : 他我就是想
             * cover : {"text":"记住我","height":1766,"flag":1,"width":1242,"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg"}
             * time : 1天前
             * isLike : 1
             * commentCounts : 0
             * memberId : 315917552893952
             * memberImage : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315917552893952.jpg
             * memberName :
             * jurisdiction : 1
             * likeCounts : 10
             */

            private String id;
            private String imgUrl;
            private String nickName;
            private String giftValue;
            private String title;
            private CoverBean cover;
            private String time;
            private String isLike;
            private String commentCounts;
            private String memberId;
            private String memberImage;
            private String memberName;
            private String jurisdiction;
            private String likeCounts;
            private List<ContentBean> content;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getGiftValue() {
                return giftValue;
            }

            public void setGiftValue(String giftValue) {
                this.giftValue = giftValue;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public CoverBean getCover() {
                return cover;
            }

            public void setCover(CoverBean cover) {
                this.cover = cover;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getIsLike() {
                return isLike;
            }

            public void setIsLike(String isLike) {
                this.isLike = isLike;
            }

            public String getCommentCounts() {
                return commentCounts;
            }

            public void setCommentCounts(String commentCounts) {
                this.commentCounts = commentCounts;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public String getMemberImage() {
                return memberImage;
            }

            public void setMemberImage(String memberImage) {
                this.memberImage = memberImage;
            }

            public String getMemberName() {
                return memberName;
            }

            public void setMemberName(String memberName) {
                this.memberName = memberName;
            }

            public String getJurisdiction() {
                return jurisdiction;
            }

            public void setJurisdiction(String jurisdiction) {
                this.jurisdiction = jurisdiction;
            }

            public String getLikeCounts() {
                return likeCounts;
            }

            public void setLikeCounts(String likeCounts) {
                this.likeCounts = likeCounts;
            }

            public List<ContentBean> getContent() {
                return content;
            }

            public void setContent(List<ContentBean> content) {
                this.content = content;
            }

            public static class CoverBean implements Serializable {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private String height;
                private String flag;
                private String width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public String getFlag() {
                    return flag;
                }

                public void setFlag(String flag) {
                    this.flag = flag;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

            public static class ContentBean implements Serializable {
                /**
                 * text : 记住我
                 * height : 1766
                 * flag : 1
                 * width : 1242
                 * url : http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg
                 */

                private String text;
                private String height;
                private String flag;
                private String width;
                private String url;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public String getFlag() {
                    return flag;
                }

                public void setFlag(String flag) {
                    this.flag = flag;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
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
