package com.merrichat.net.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amssy on 17/8/31.
 */

public class AllCommentModel implements Serializable {

//    private boolean testFlag;
//
//    public boolean isTestFlag() {
//        return testFlag;
//    }
//
//    public void setTestFlag(boolean testFlag) {
//        this.testFlag = testFlag;
//    }

    /**
     * data : {"showBarComment":[{"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","commentId":"","commentPersonId":315498810359809,"commentType":1,"contentId":317330838904832,"context":"大跌扣女女模","createTime":1513070667483,"id":317334557933568,"isLikeComment":false,"likeCommentNum":0,"nick":"潇妃壮壮","replyComment":[{"id":317537668423690,"createTime":1513167500143,"replyMemberId":315498810359809,"replyHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","nick":"潇潇","likeReplyCommentNum":0,"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","context":"你就知道女模，能不能好好学习，天天向上","isLikeReplyComment":false,"commentPersonId":302472807890945,"commentType":2,"replyNick":"潇妃壮壮"}],"replyMemberId":"","replyMemberNick":"","replyNum":1}]}
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
        private List<ShowBarCommentBean> showBarComment;

        public List<ShowBarCommentBean> getShowBarComment() {
            return showBarComment;
        }

        public void setShowBarComment(List<ShowBarCommentBean> showBarComment) {
            this.showBarComment = showBarComment;
        }

        public static class ShowBarCommentBean {
            /**
             * commentHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg
             * commentId :
             * commentPersonId : 315498810359809
             * commentType : 1
             * contentId : 317330838904832
             * context : 大跌扣女女模
             * createTime : 1513070667483
             * id : 317334557933568
             * isLikeComment : false
             * likeCommentNum : 0
             * nick : 潇妃壮壮
             * replyComment : [{"id":317537668423690,"createTime":1513167500143,"replyMemberId":315498810359809,"replyHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg","nick":"潇潇","likeReplyCommentNum":0,"commentHeadImgUrl":"http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg","context":"你就知道女模，能不能好好学习，天天向上","isLikeReplyComment":false,"commentPersonId":302472807890945,"commentType":2,"replyNick":"潇妃壮壮"}]
             * replyMemberId :
             * replyMemberNick :
             * replyNum : 1
             */

            private boolean testFlag;

            public boolean isTestFlag() {
                return testFlag;
            }

            public void setTestFlag(boolean testFlag) {
                this.testFlag = testFlag;
            }

            private String commentHeadImgUrl;
            private String commentId;
            private long commentPersonId;
            private int commentType;
            private long contentId;
            private String context;
            private String createTime;
            private long id;
            private boolean isLikeComment;
            private int likeCommentNum;
            private String nick;
            private long replyMemberId;
            private String replyMemberNick;
            private int replyNum;
            private List<ReplyCommentBean> replyComment;

            public String getCommentHeadImgUrl() {
                return commentHeadImgUrl;
            }

            public void setCommentHeadImgUrl(String commentHeadImgUrl) {
                this.commentHeadImgUrl = commentHeadImgUrl;
            }

            public String getCommentId() {
                return commentId;
            }

            public void setCommentId(String commentId) {
                this.commentId = commentId;
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

            public long getContentId() {
                return contentId;
            }

            public void setContentId(long contentId) {
                this.contentId = contentId;
            }

            public String getContext() {
                return context;
            }

            public void setContext(String context) {
                this.context = context;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public boolean isIsLikeComment() {
                return isLikeComment;
            }

            public void setIsLikeComment(boolean isLikeComment) {
                this.isLikeComment = isLikeComment;
            }

            public int getLikeCommentNum() {
                return likeCommentNum;
            }

            public void setLikeCommentNum(int likeCommentNum) {
                this.likeCommentNum = likeCommentNum;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public long getReplyMemberId() {
                return replyMemberId;
            }

            public void setReplyMemberId(long replyMemberId) {
                this.replyMemberId = replyMemberId;
            }

            public String getReplyMemberNick() {
                return replyMemberNick;
            }

            public void setReplyMemberNick(String replyMemberNick) {
                this.replyMemberNick = replyMemberNick;
            }

            public int getReplyNum() {
                return replyNum;
            }

            public void setReplyNum(int replyNum) {
                this.replyNum = replyNum;
            }

            public List<ReplyCommentBean> getReplyComment() {
                return replyComment;
            }

            public void setReplyComment(List<ReplyCommentBean> replyComment) {
                this.replyComment = replyComment;
            }

            public static class ReplyCommentBean {
                /**
                 * id : 317537668423690
                 * createTime : 1513167500143
                 * replyMemberId : 315498810359809
                 * replyHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/315498810359809.jpg
                 * nick : 潇潇
                 * likeReplyCommentNum : 0
                 * commentHeadImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/302472807890945.jpg
                 * context : 你就知道女模，能不能好好学习，天天向上
                 * isLikeReplyComment : false
                 * commentPersonId : 302472807890945
                 * commentType : 2
                 * replyNick : 潇妃壮壮
                 */

                private long id;
                private String createTime;
                private long replyMemberId;
                private String replyHeadImgUrl;
                private String nick;
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

                public String getCreateTime() {
                    return createTime;
                }

                public void setCreateTime(String createTime) {
                    this.createTime = createTime;
                }

                public long getReplyMemberId() {
                    return replyMemberId;
                }

                public void setReplyMemberId(long replyMemberId) {
                    this.replyMemberId = replyMemberId;
                }

                public String getReplyHeadImgUrl() {
                    return replyHeadImgUrl;
                }

                public void setReplyHeadImgUrl(String replyHeadImgUrl) {
                    this.replyHeadImgUrl = replyHeadImgUrl;
                }

                public String getNick() {
                    return nick;
                }

                public void setNick(String nick) {
                    this.nick = nick;
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
    }
}
