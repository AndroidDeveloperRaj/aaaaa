package com.merrichat.net.model;

import java.io.Serializable;

/**
 * Created by amssy on 17/11/14.
 * 赞和评论model
 */

public class PraiseAndCommentModel implements Serializable {


    /**
     * flag:帖子的类型
     * comment : @AILLS:这种难以言语不想做梦
     * content : ( •̅_•̅ )啦啦啦啦V5了！t
     * contentId : 301525409587200
     * cover : http://igomopub.igomot.net/nfs_data/igomo/showBar/_image_301525376032768_.jpg
     * headImgUrl : http://igomopub.igomot.net/nfs_data/igomo/igomoLife/297387772133376.jpg
     * memberId : 297387772133376
     * name : 小生睿谨
     * noticeId : 302295009845248
     * revert : 回复@AILLS:������������
     * revertId : 302295007748096
     * time : 09-20 17:20
     * title : 借我用哦
     * type : 3
     */


    /**
     * 发帖人id
     */
    private String sendMemberId;


    /**
     * 评论
     */
    private String comment;


    /**
     * 秀吧内容
     */
    private String content;

    /**
     * 帖子id
     */
    private String contentId;

    /**
     * 评论id
     */
    private String commentId;


    /**
     * 秀吧封面
     */
    private String cover;

    /**
     * 赞或评论人头像
     */
    private String headImgUrl;

    /**
     * 赞或评论人memberId
     */
    private long memberId;

    /**
     * 赞或评论人名字
     */
    private String name;


    /**
     * 评论id
     */
    private String noticeId;

    /**
     * 回复 （赞和评论为""）
     */
    private String revert;


    /**
     * 回复id
     */
    private String revertId;

    /**
     * 时间
     */
    private String time;

    /**
     * 秀吧标题
     */
    private String title;


    /**
     * 2:赞 3：回复 4：评论
     */
    private String type;

    /**
     * 帖子的类型
     */
    private String flag;


    public String getSendMemberId() {
        return sendMemberId;
    }

    public void setSendMemberId(String sendMemberId) {
        this.sendMemberId = sendMemberId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getRevert() {
        return revert;
    }

    public void setRevert(String revert) {
        this.revert = revert;
    }

    public String getRevertId() {
        return revertId;
    }

    public void setRevertId(String revertId) {
        this.revertId = revertId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
