package com.merrichat.net.model;


import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.NoticeModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 17/9/4.
 * 通知列表
 */

@Entity
public class NoticeModel {


    /**
     * id : 314023608418306
     * content : 这是一个测试的推送
     * title : 我是第一个推送
     * pushTime : 1511491866787
     * success : true
     * openFlag : 2
     * accountStatus：1未绑定微信，2未实名认证
     */
    @Id(autoincrement = true)
    private Long ID;

    @Property(nameInDb = "UID")
    private long uid;

    @Property(nameInDb = "CONTENT")
    private String content;

    @Property(nameInDb = "TITLE")
    private String title;

    @Property(nameInDb = "PUSH_TIME")
    private long pushTime;

    @Property(nameInDb = "SUCCESS")
    private boolean success;

    @Property(nameInDb = "OPEN_FLAG")
    private int openFlag;


    @Property(nameInDb = "ACCOUNT_STATUS")
    private String accountStatus;


    @Property(nameInDb = "MEMBER_ID")
    private String memberId;


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getOpenFlag() {
        return this.openFlag;
    }

    public void setOpenFlag(int openFlag) {
        this.openFlag = openFlag;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getPushTime() {
        return this.pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUid() {
        return this.uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Generated(hash = 1664255074)
    public NoticeModel(Long ID, long uid, String content, String title, long pushTime, boolean success, int openFlag, String accountStatus,
            String memberId) {
        this.ID = ID;
        this.uid = uid;
        this.content = content;
        this.title = title;
        this.pushTime = pushTime;
        this.success = success;
        this.openFlag = openFlag;
        this.accountStatus = accountStatus;
        this.memberId = memberId;
    }

    @Generated(hash = 15871595)
    public NoticeModel() {

    }


    /**
     * 查询本人名下的通知列表
     *
     * @param memberId
     * @return
     */
    public static List<NoticeModel> getListNoticeModel(String memberId) {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getNewSession().getNoticeModelDao();
        QueryBuilder<NoticeModel> qb = noticeModelDao.queryBuilder();
        List<NoticeModel> list = qb.where(NoticeModelDao.Properties.MemberId.eq(memberId)).orderDesc(NoticeModelDao.Properties.PushTime).list();
        return list;
    }


    /**
     * 获取NoticeModel对象
     *
     * @return
     */
    public static NoticeModel getNoticeModel() {

        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getSession().getNoticeModelDao();
        List<NoticeModel> list = noticeModelDao.queryBuilder().list();
        NoticeModel noticeModel = list.size() > 0 ? list.get(0) : new NoticeModel();
        return noticeModel;
    }

    /**
     * 设置或更改NoticeModel的对象的值
     *
     * @param noticeModel
     */
    public static void setNoticeModel(NoticeModel noticeModel) {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getNewSession().getNoticeModelDao();
        QueryBuilder<NoticeModel> qb = noticeModelDao.queryBuilder();
        List<NoticeModel> noticeModelList = qb.where(NoticeModelDao.Properties.PushTime.eq(noticeModel.getPushTime())).list();
        if ((noticeModel.getID() != null && noticeModel.getID() > 0) || (null != noticeModelList && noticeModelList.size() > 0)) {
            NoticeModel entity = noticeModelList.get(0);
            noticeModel.setID(entity.getID());
            updateNoticeModel(noticeModel);
        } else {
            NoticeModelDao messageDao1 = GreenDaoManager.getInstance().getSession().getNoticeModelDao();
            messageDao1.save(noticeModel);
        }
    }


    /**
     * 更新NoticeModel对象的信息
     *
     * @param noticeModel
     */
    public static void updateNoticeModel(NoticeModel noticeModel) {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getSession().getNoticeModelDao();
        noticeModelDao.update(noticeModel);
    }


    public static void setNoticeList(List<NoticeModel> list) {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getSession().getNoticeModelDao();
        noticeModelDao.insertInTx(list);
    }


    /**
     * 删除单条通知
     *
     * @param
     */
    public static void deleteOneNoticeModel(NoticeModel noticeModel) {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getNewSession().getNoticeModelDao();
        noticeModelDao.delete(noticeModel);
    }

    /**
     * 删除全部通知
     */
    public static void deleteAllModel() {
        NoticeModelDao noticeModelDao = GreenDaoManager.getInstance().getNewSession().getNoticeModelDao();
        noticeModelDao.deleteAll();
    }

    public String getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
    /**
     * 根据用户类,删除信息      * @param user    用户信息类
     */
//    public void deleteNote(Users user) {
//        userDao.delete(user);
//    }

    /**
     * 根据id,删除数据      * @param id      用户id
     */
//    public void deleteNote(long id) {
//        userDao.deleteByKey(id);
//        Log.i(TAG, "delete");
//    }
}
