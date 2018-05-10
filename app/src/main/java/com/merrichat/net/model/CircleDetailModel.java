package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/12/8.
 */

public class CircleDetailModel implements Serializable {


    /**
     * data : {"beautyLog":{"address":"北京市海淀区花园北路","classifys":["1","1"],"collectCounts":0,"commentCounts":2,"content":"[{\"url\":\"http:\\/\\/okdi.oss-cn-beijing.aliyuncs.com\\/merrichat_video_1513068955\",\"type\":1,\"text\":\"你爱信不信，吃了就知道咯\",\"flag\":1}]","cover":"{\"url\":\"http:\\/\\/okdi.oss-cn-beijing.aliyuncs.com\\/merrichat_image_1513068955\",\"type\":0,\"text\":\"你爱信不信，吃了就知道咯\",\"flag\":1,\"width\":1280,\"height\":720}","createTimes":1513069113695,"describe":"","flag":2,"gender":1,"id":317331335258112,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984783,"likeCounts":0,"longitude":116.384197,"memberId":315498810359809,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg?time=1512614577584","memberName":"潇妃壮壮","musicUrl":"","phone":"15010222938","position":[116.384197,39.984783],"shareCounts":0,"title":"狗屎糖，吃了走狗屎运","updateTimes":1513069113695,"videoUrl":"http://okdi.oss-cn-beijing.aliyuncs.com/merrichat_video_1513068955"},"likes":false,"queryGoodFriendsState":0,"isMy":0,"commentList":[{"createTime":1513232590483,"replyMemberNick":"","replyMemberId":"","contentId":317331335258112,"commentId":"","replyNum":2,"id":317674172047360,"nick":"潇妃壮壮","commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","context":"不知道什么情况，评论了再说","likeCommentNum":0,"commentPersonId":315498810359809,"isLikeComment":false,"commentType":1,"replyComment":[{"id":317674910244864,"createTime":1513232942784,"replyMemberId":315498810359809,"nick":"潇潇","replyHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","likeReplyCommentNum":0,"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","context":"","isLikeReplyComment":false,"commentPersonId":302472807890945,"commentType":2,"replyNick":"潇妃壮壮"}]}],"memberIdList":[]}
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
         * beautyLog : {"address":"北京市海淀区花园北路","classifys":["1","1"],"collectCounts":0,"commentCounts":2,"content":"[{\"url\":\"http:\\/\\/okdi.oss-cn-beijing.aliyuncs.com\\/merrichat_video_1513068955\",\"type\":1,\"text\":\"你爱信不信，吃了就知道咯\",\"flag\":1}]","cover":"{\"url\":\"http:\\/\\/okdi.oss-cn-beijing.aliyuncs.com\\/merrichat_image_1513068955\",\"type\":0,\"text\":\"你爱信不信，吃了就知道咯\",\"flag\":1,\"width\":1280,\"height\":720}","createTimes":1513069113695,"describe":"","flag":2,"gender":1,"id":317331335258112,"isBlack":-1,"isDelete":0,"jurisdiction":1,"latitude":39.984783,"likeCounts":0,"longitude":116.384197,"memberId":315498810359809,"memberImage":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg?time=1512614577584","memberName":"潇妃壮壮","musicUrl":"","phone":"15010222938","position":[116.384197,39.984783],"shareCounts":0,"title":"狗屎糖，吃了走狗屎运","updateTimes":1513069113695,"videoUrl":"http://okdi.oss-cn-beijing.aliyuncs.com/merrichat_video_1513068955"}
         * likes : false
         * queryGoodFriendsState : 0
         * isMy : 0
         * commentList : [{"createTime":1513232590483,"replyMemberNick":"","replyMemberId":"","contentId":317331335258112,"commentId":"","replyNum":2,"id":317674172047360,"nick":"潇妃壮壮","commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","context":"不知道什么情况，评论了再说","likeCommentNum":0,"commentPersonId":315498810359809,"isLikeComment":false,"commentType":1,"replyComment":[{"id":317674910244864,"createTime":1513232942784,"replyMemberId":315498810359809,"nick":"潇潇","replyHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","likeReplyCommentNum":0,"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","context":"","isLikeReplyComment":false,"commentPersonId":302472807890945,"commentType":2,"replyNick":"潇妃壮壮"}]}]
         * memberIdList : []
         */

        private BeautyLogBean beautyLog;
        private boolean likes;
        private boolean queryIsAttentionRelation;
        private boolean collects;
        private int isMy;
        private List<CommentListBean> commentList;
        private List<MemberIdBean> memberIdList;
        private String income;

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public boolean isCollects() {
            return collects;
        }

        public void setCollects(boolean collects) {
            this.collects = collects;
        }

        public boolean isQueryIsAttentionRelation() {
            return queryIsAttentionRelation;
        }

        public void setQueryIsAttentionRelation(boolean queryIsAttentionRelation) {
            this.queryIsAttentionRelation = queryIsAttentionRelation;
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

        public int getIsMy() {
            return isMy;
        }

        public void setIsMy(int isMy) {
            this.isMy = isMy;
        }

        public List<CommentListBean> getCommentList() {
            return commentList;
        }

        public void setCommentList(List<CommentListBean> commentList) {
            this.commentList = commentList;
        }

        public List<MemberIdBean> getMemberIdList() {
            return memberIdList;
        }

        public void setMemberIdList(List<MemberIdBean> memberIdList) {
            this.memberIdList = memberIdList;
        }

        public static class BeautyLogBean implements Serializable {
            /**
             * address : 北京市海淀区花园北路
             * classifys : ["1","1"]
             * collectCounts : 0
             * commentCounts : 2
             * content : [{"url":"http:\/\/okdi.oss-cn-beijing.aliyuncs.com\/merrichat_video_1513068955","type":1,"text":"你爱信不信，吃了就知道咯","flag":1}]
             * cover : {"url":"http:\/\/okdi.oss-cn-beijing.aliyuncs.com\/merrichat_image_1513068955","type":0,"text":"你爱信不信，吃了就知道咯","flag":1,"width":1280,"height":720}
             * createTimes : 1513069113695
             * describe :
             * flag : 2
             * gender : 1
             * id : 317331335258112
             * isBlack : -1
             * isDelete : 0
             * jurisdiction : 1
             * latitude : 39.984783
             * likeCounts : 0
             * longitude : 116.384197
             * memberId : 315498810359809
             * memberImage : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg?time=1512614577584
             * memberName : 潇妃壮壮
             * musicUrl :
             * phone : 15010222938
             * position : [116.384197,39.984783]
             * shareCounts : 0
             * title : 狗屎糖，吃了走狗屎运
             * updateTimes : 1513069113695
             * videoUrl : http://okdi.oss-cn-beijing.aliyuncs.com/merrichat_video_1513068955
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
            private String msg;
            private String memberName;
            private String musicUrl;
            private String phone;
            private int shareCounts;
            private String title;
            private long updateTimes;
            private String videoUrl;
            private List<String> classifys;
            private List<Double> position;

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

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

        public static class CommentListBean implements Serializable {
            /**
             * createTime : 1513232590483
             * replyMemberNick :
             * replyMemberId :
             * contentId : 317331335258112
             * commentId :
             * replyNum : 2
             * id : 317674172047360
             * nick : 潇妃壮壮
             * commentHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg
             * context : 不知道什么情况，评论了再说
             * likeCommentNum : 0
             * commentPersonId : 315498810359809
             * isLikeComment : false
             * commentType : 1
             * replyComment : [{"id":317674910244864,"createTime":1513232942784,"replyMemberId":315498810359809,"nick":"潇潇","replyHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","likeReplyCommentNum":0,"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","context":"","isLikeReplyComment":false,"commentPersonId":302472807890945,"commentType":2,"replyNick":"潇妃壮壮"}]
             */

            private long createTime;
            private String replyMemberNick;
            private String replyMemberId;
            private long contentId;
            private String commentId;
            private int replyNum;
            private long id;
            private String nick;
            private String commentHeadImgUrl;
            private String context;
            private int likeCommentNum;
            private long commentPersonId;
            private boolean isLikeComment;
            private int commentType;
            private List<ReplyCommentBean> replyComment;

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getReplyMemberNick() {
                return replyMemberNick;
            }

            public void setReplyMemberNick(String replyMemberNick) {
                this.replyMemberNick = replyMemberNick;
            }

            public String getReplyMemberId() {
                return replyMemberId;
            }

            public void setReplyMemberId(String replyMemberId) {
                this.replyMemberId = replyMemberId;
            }

            public long getContentId() {
                return contentId;
            }

            public void setContentId(long contentId) {
                this.contentId = contentId;
            }

            public String getCommentId() {
                return commentId;
            }

            public void setCommentId(String commentId) {
                this.commentId = commentId;
            }

            public int getReplyNum() {
                return replyNum;
            }

            public void setReplyNum(int replyNum) {
                this.replyNum = replyNum;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public String getCommentHeadImgUrl() {
                return commentHeadImgUrl;
            }

            public void setCommentHeadImgUrl(String commentHeadImgUrl) {
                this.commentHeadImgUrl = commentHeadImgUrl;
            }

            public String getContext() {
                return context;
            }

            public void setContext(String context) {
                this.context = context;
            }

            public int getLikeCommentNum() {
                return likeCommentNum;
            }

            public void setLikeCommentNum(int likeCommentNum) {
                this.likeCommentNum = likeCommentNum;
            }

            public long getCommentPersonId() {
                return commentPersonId;
            }

            public void setCommentPersonId(long commentPersonId) {
                this.commentPersonId = commentPersonId;
            }

            public boolean isIsLikeComment() {
                return isLikeComment;
            }

            public void setIsLikeComment(boolean isLikeComment) {
                this.isLikeComment = isLikeComment;
            }

            public int getCommentType() {
                return commentType;
            }

            public void setCommentType(int commentType) {
                this.commentType = commentType;
            }

            public List<ReplyCommentBean> getReplyComment() {
                return replyComment;
            }

            public void setReplyComment(List<ReplyCommentBean> replyComment) {
                this.replyComment = replyComment;
            }

            public static class ReplyCommentBean implements Serializable {
                /**
                 * id : 317674910244864
                 * createTime : 1513232942784
                 * replyMemberId : 315498810359809
                 * nick : 潇潇
                 * replyHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg
                 * likeReplyCommentNum : 0
                 * commentHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg
                 * context :
                 * isLikeReplyComment : false
                 * commentPersonId : 302472807890945
                 * commentType : 2
                 * replyNick : 潇妃壮壮
                 */

                private long id;
                private long createTime;
                private long replyMemberId;
                private String nick;
                private String replyHeadImgUrl;
                private int likeReplyCommentNum;
                private String commentHeadImgUrl;
                private String context;
                private boolean isLikeReplyComment;
                private long commentPersonId;
                private int commentType;
                private String replyNick;

                public long getId() {
                    return id;
                }

                public void setId(long id) {
                    this.id = id;
                }

                public long getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(long createTime) {
                    this.createTime = createTime;
                }

                public long getReplyMemberId() {
                    return replyMemberId;
                }

                public void setReplyMemberId(long replyMemberId) {
                    this.replyMemberId = replyMemberId;
                }

                public String getNick() {
                    return nick;
                }

                public void setNick(String nick) {
                    this.nick = nick;
                }

                public String getReplyHeadImgUrl() {
                    return replyHeadImgUrl;
                }

                public void setReplyHeadImgUrl(String replyHeadImgUrl) {
                    this.replyHeadImgUrl = replyHeadImgUrl;
                }

                public int getLikeReplyCommentNum() {
                    return likeReplyCommentNum;
                }

                public void setLikeReplyCommentNum(int likeReplyCommentNum) {
                    this.likeReplyCommentNum = likeReplyCommentNum;
                }

                public String getCommentHeadImgUrl() {
                    return commentHeadImgUrl;
                }

                public void setCommentHeadImgUrl(String commentHeadImgUrl) {
                    this.commentHeadImgUrl = commentHeadImgUrl;
                }

                public String getContext() {
                    return context;
                }

                public void setContext(String context) {
                    this.context = context;
                }

                public boolean isIsLikeReplyComment() {
                    return isLikeReplyComment;
                }

                public void setIsLikeReplyComment(boolean isLikeReplyComment) {
                    this.isLikeReplyComment = isLikeReplyComment;
                }

                public long getCommentPersonId() {
                    return commentPersonId;
                }

                public void setCommentPersonId(long commentPersonId) {
                    this.commentPersonId = commentPersonId;
                }

                public int getCommentType() {
                    return commentType;
                }

                public void setCommentType(int commentType) {
                    this.commentType = commentType;
                }

                public String getReplyNick() {
                    return replyNick;
                }

                public void setReplyNick(String replyNick) {
                    this.replyNick = replyNick;
                }
            }
        }

        public static class MemberIdBean implements Serializable {
            private String likePersonId;
            private String nickName;
            private String personUrl;

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getLikePersonId() {
                return likePersonId;
            }

            public void setLikePersonId(String likePersonId) {
                this.likePersonId = likePersonId;
            }

            public String getPersonUrl() {
                return personUrl;
            }

            public void setPersonUrl(String personUrl) {
                this.personUrl = personUrl;
            }
        }
    }
}
