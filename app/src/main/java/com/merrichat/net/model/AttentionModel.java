package com.merrichat.net.model;

import java.util.List;

/**
 * Created by amssy on 17/12/16.
 */

public class AttentionModel {

    /**
     * data : {"total":2,"pageSize":10,"list":[{"beautyLog":{"address":"北京市海淀区花园北路","classifys":["1"],"collectCounts":0,"commentCounts":1,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640},{\"text\":\"锦衣卫破哦\",\"height\":0,\"flag\":0,\"width\":0},{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"text\":\"里无声无息\",\"height\":1766,\"flag\":0,\"width\":1242}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640}","createTimes":1513329757062,"describe":"替我红","flag":1,"gender":1,"id":317877946015744,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984808,"likeCounts":-2,"longitude":116.383846,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1513326750178","memberName":"","musicUrl":"","phone":"13167547773","position":[116.383846,39.984808],"shareCounts":0,"title":"技术员婆娘","updateTimes":1513580686186,"videoUrl":""},"likes":false},{"beautyLog":{"address":"北京市海淀区花园北路","classifys":["1"],"collectCounts":0,"commentCounts":0,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"height\":1766,\"flag\":1,\"width\":1242},{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"height\":960,\"flag\":0,\"width\":640}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"height\":1766,\"flag\":1,\"width\":1242}","createTimes":1513565661513,"describe":"top而现在","flag":1,"gender":1,"id":318372672561152,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984778,"likeCounts":-4,"longitude":116.383868,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg","memberName":"金手指","musicUrl":"","phone":"13167547773","position":[116.383868,39.984778],"shareCounts":0,"title":"咯去学校容易","updateTimes":1513585896942,"videoUrl":""},"likes":false}],"pageNum":1}
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
         * total : 2
         * pageSize : 10
         * list : [{"beautyLog":{"address":"北京市海淀区花园北路","classifys":["1"],"collectCounts":0,"commentCounts":1,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640},{\"text\":\"锦衣卫破哦\",\"height\":0,\"flag\":0,\"width\":0},{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"text\":\"里无声无息\",\"height\":1766,\"flag\":0,\"width\":1242}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640}","createTimes":1513329757062,"describe":"替我红","flag":1,"gender":1,"id":317877946015744,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984808,"likeCounts":-2,"longitude":116.383846,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1513326750178","memberName":"","musicUrl":"","phone":"13167547773","position":[116.383846,39.984808],"shareCounts":0,"title":"技术员婆娘","updateTimes":1513580686186,"videoUrl":""},"likes":false},{"beautyLog":{"address":"北京市海淀区花园北路","classifys":["1"],"collectCounts":0,"commentCounts":0,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"height\":1766,\"flag\":1,\"width\":1242},{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"height\":960,\"flag\":0,\"width\":640}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"height\":1766,\"flag\":1,\"width\":1242}","createTimes":1513565661513,"describe":"top而现在","flag":1,"gender":1,"id":318372672561152,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984778,"likeCounts":-4,"longitude":116.383868,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg","memberName":"金手指","musicUrl":"","phone":"13167547773","position":[116.383868,39.984778],"shareCounts":0,"title":"咯去学校容易","updateTimes":1513585896942,"videoUrl":""},"likes":false}]
         * pageNum : 1
         */

        private int total;
        private int pageSize;
        private int pageNum;
        private List<ListBean> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * beautyLog : {"address":"北京市海淀区花园北路","classifys":["1"],"collectCounts":0,"commentCounts":1,"content":"[{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640},{\"text\":\"锦衣卫破哦\",\"height\":0,\"flag\":0,\"width\":0},{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg\",\"text\":\"里无声无息\",\"height\":1766,\"flag\":0,\"width\":1242}]","cover":"{\"url\":\"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg\",\"text\":\"听如影随形\",\"height\":960,\"flag\":1,\"width\":640}","createTimes":1513329757062,"describe":"替我红","flag":1,"gender":1,"id":317877946015744,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984808,"likeCounts":-2,"longitude":116.383846,"memberId":315917552893952,"memberImage":"http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1513326750178","memberName":"","musicUrl":"","phone":"13167547773","position":[116.383846,39.984808],"shareCounts":0,"title":"技术员婆娘","updateTimes":1513580686186,"videoUrl":""}
             * likes : false
             */

            private BeautyLogBean beautyLog;
            private boolean likes;
            public String income;

            public String getIncome() {
                return income;
            }

            public void setIncome(String income) {
                this.income = income;
            }

            public BeautyLogBean getBeautyLog() {
                return beautyLog;
            }

            public void setBeautyLog(BeautyLogBean beautyLog) {
                this.beautyLog = beautyLog;
            }

            public boolean isLikes() {
                return likes;
            }

            public void setLikes(boolean likes) {
                this.likes = likes;
            }

            public static class BeautyLogBean {
                /**
                 * address : 北京市海淀区花园北路
                 * classifys : ["1"]
                 * collectCounts : 0
                 * commentCounts : 1
                 * content : [{"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg","text":"听如影随形","height":960,"flag":1,"width":640},{"text":"锦衣卫破哦","height":0,"flag":0,"width":0},{"url":"http://okdi.oss-cn-beijing.aliyuncs.com/709603505.jpg","text":"里无声无息","height":1766,"flag":0,"width":1242}]
                 * cover : {"url":"http://okdi.oss-cn-beijing.aliyuncs.com/-1072679654.jpg","text":"听如影随形","height":960,"flag":1,"width":640}
                 * createTimes : 1513329757062
                 * describe : 替我红
                 * flag : 1
                 * gender : 1
                 * id : 317877946015744
                 * isBlack : -1
                 * isDelete : 0
                 * jurisdiction : 1
                 * latitude : 39.984808
                 * likeCounts : -2
                 * longitude : 116.383846
                 * memberId : 315917552893952
                 * memberImage : http://cas.okdit.net/nfs_data/mob/head/315917552893952.jpg?time=1513326750178
                 * memberName :
                 * musicUrl :
                 * phone : 13167547773
                 * position : [116.383846,39.984808]
                 * shareCounts : 0
                 * title : 技术员婆娘
                 * updateTimes : 1513580686186
                 * videoUrl :
                 */

                private String address;
                private int collectCounts;
                private int commentCounts;
                private String content;
                private String cover;
                private long createTimes;
                private String describe;
                private int flag;
                private int gender;
                private long id;
                private int isBlack;
                private int isDelete;
                private int jurisdiction;
                private double latitude;
                private int likeCounts;
                private double longitude;
                private long memberId;
                private String memberImage;
                private String memberName;
                private String musicUrl;
                private String phone;
                private int shareCounts;
                private String title;
                private long updateTimes;
                private String videoUrl;
                private List<String> classifys;
                private List<Double> position;

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public int getCollectCounts() {
                    return collectCounts;
                }

                public void setCollectCounts(int collectCounts) {
                    this.collectCounts = collectCounts;
                }

                public int getCommentCounts() {
                    return commentCounts;
                }

                public void setCommentCounts(int commentCounts) {
                    this.commentCounts = commentCounts;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public long getCreateTimes() {
                    return createTimes;
                }

                public void setCreateTimes(long createTimes) {
                    this.createTimes = createTimes;
                }

                public String getDescribe() {
                    return describe;
                }

                public void setDescribe(String describe) {
                    this.describe = describe;
                }

                public int getFlag() {
                    return flag;
                }

                public void setFlag(int flag) {
                    this.flag = flag;
                }

                public int getGender() {
                    return gender;
                }

                public void setGender(int gender) {
                    this.gender = gender;
                }

                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }

                public int getIsBlack() {
                    return isBlack;
                }

                public void setIsBlack(int isBlack) {
                    this.isBlack = isBlack;
                }

                public int getIsDelete() {
                    return isDelete;
                }

                public void setIsDelete(int isDelete) {
                    this.isDelete = isDelete;
                }

                public int getJurisdiction() {
                    return jurisdiction;
                }

                public void setJurisdiction(int jurisdiction) {
                    this.jurisdiction = jurisdiction;
                }

                public double getLatitude() {
                    return latitude;
                }

                public void setLatitude(double latitude) {
                    this.latitude = latitude;
                }

                public int getLikeCounts() {
                    return likeCounts;
                }

                public void setLikeCounts(int likeCounts) {
                    this.likeCounts = likeCounts;
                }

                public double getLongitude() {
                    return longitude;
                }

                public void setLongitude(double longitude) {
                    this.longitude = longitude;
                }

                public long getMemberId() {
                    return memberId;
                }

                public void setMemberId(long memberId) {
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

                public String getMusicUrl() {
                    return musicUrl;
                }

                public void setMusicUrl(String musicUrl) {
                    this.musicUrl = musicUrl;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public int getShareCounts() {
                    return shareCounts;
                }

                public void setShareCounts(int shareCounts) {
                    this.shareCounts = shareCounts;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public long getUpdateTimes() {
                    return updateTimes;
                }

                public void setUpdateTimes(long updateTimes) {
                    this.updateTimes = updateTimes;
                }

                public String getVideoUrl() {
                    return videoUrl;
                }

                public void setVideoUrl(String videoUrl) {
                    this.videoUrl = videoUrl;
                }

                public List<String> getClassifys() {
                    return classifys;
                }

                public void setClassifys(List<String> classifys) {
                    this.classifys = classifys;
                }

                public List<Double> getPosition() {
                    return position;
                }

                public void setPosition(List<Double> position) {
                    this.position = position;
                }
            }
        }
    }
}
