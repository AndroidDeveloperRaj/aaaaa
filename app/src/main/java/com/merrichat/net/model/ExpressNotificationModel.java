package com.merrichat.net.model;

import com.merrichat.net.model.dao.ExpressNotificationModelDao;
import com.merrichat.net.model.dao.NoticeModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by amssy on 18/3/20.
 * 快递通知model
 */

@Entity
public class ExpressNotificationModel implements Serializable {

    @Id(autoincrement = true)
    private Long ID;


    /**
     * 发送时间
     */
    @Property(nameInDb = "CREATE_TIME")
    private String createTime;


    /**
     * 快递公司
     */
    @Property(nameInDb = "NET_NAME")
    private String netName;


    /**
     * 快递员头像
     */
    @Property(nameInDb = "IMAGE_URL")
    private String imageUrl;

    /**
     * 快递员名字
     */
    @Property(nameInDb = "NAME")
    private String name;


    /**
     * 快递员手机号
     */
    @Property(nameInDb = "MEMBER_PHONE")
    private String memberPhone;


    /**
     * 取件编号
     */
    @Property(nameInDb = "NUM_BER")
    private String number;


    /**
     * 短信内容
     */
    @Property(nameInDb = "SEND_CONTENT")
    private String sendContent;


    /**
     * 取件地址
     */
    @Property(nameInDb = "PICKUP_ADDR")
    private String pickupAddr;


    /**
     * 收件人手机号
     */
    @Property(nameInDb = "RECEIVER_PHONE")
    private String receiverPhone;

    @Property(nameInDb = "MEMBER_ID")
    private String memberId;




    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getReceiverPhone() {
        return this.receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getPickupAddr() {
        return this.pickupAddr;
    }

    public void setPickupAddr(String pickupAddr) {
        this.pickupAddr = pickupAddr;
    }

    public String getSendContent() {
        return this.sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMemberPhone() {
        return this.memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNetName() {
        return this.netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    @Generated(hash = 1602558127)
    public ExpressNotificationModel(Long ID, String createTime, String netName, String imageUrl, String name, String memberPhone,
            String number, String sendContent, String pickupAddr, String receiverPhone, String memberId) {
        this.ID = ID;
        this.createTime = createTime;
        this.netName = netName;
        this.imageUrl = imageUrl;
        this.name = name;
        this.memberPhone = memberPhone;
        this.number = number;
        this.sendContent = sendContent;
        this.pickupAddr = pickupAddr;
        this.receiverPhone = receiverPhone;
        this.memberId = memberId;
    }

    @Generated(hash = 1884670870)
    public ExpressNotificationModel() {
    }




    public static List<ExpressNotificationModel> getListNotificationModel(String memberId) {
        ExpressNotificationModelDao notificationModelDao = GreenDaoManager.getInstance().getNewSession().getExpressNotificationModelDao();
        QueryBuilder<ExpressNotificationModel> qb = notificationModelDao.queryBuilder();
        List<ExpressNotificationModel> list = qb.where(ExpressNotificationModelDao.Properties.MemberId.eq(memberId)).orderDesc(ExpressNotificationModelDao.Properties.CreateTime).list();
        return list;
    }




    /**
     * 获取ExpressNotificationModel对象
     *
     * @return
     */
    public static ExpressNotificationModel getNotificationModel() {
        ExpressNotificationModelDao notificationModelDao = GreenDaoManager.getInstance().getSession().getExpressNotificationModelDao();
        List<ExpressNotificationModel> list = notificationModelDao.queryBuilder().list();
        ExpressNotificationModel notificationModel = list.size() > 0 ? list.get(0) : new ExpressNotificationModel();
        return notificationModel;
    }


    /**
     * 设置或更改ExpressNotificationModel的对象的值
     *
     * @param notificationModel
     */
    public static void setNotificationModel(ExpressNotificationModel notificationModel) {
        if (notificationModel.getID() != null && notificationModel.getID() > 0) {
            updateNotificationModel(notificationModel);
        } else {
            ExpressNotificationModelDao notificationModelDao = GreenDaoManager.getInstance().getSession().getExpressNotificationModelDao();
            notificationModelDao.insert(notificationModel);
        }
    }


    /**
     * 更新UserModel对象的信息
     *
     * @param notificationModel
     */
    public static void updateNotificationModel(ExpressNotificationModel notificationModel) {
        ExpressNotificationModelDao notificationModelDao = GreenDaoManager.getInstance().getSession().getExpressNotificationModelDao();
        notificationModelDao.update(notificationModel);
    }



    /**
     * 删除单条通知
     *
     * @param
     */
    public static void deleteOneNotifitionModel(ExpressNotificationModel notificationModel) {
        ExpressNotificationModelDao  notificationModelDao = GreenDaoManager.getInstance().getNewSession().getExpressNotificationModelDao();
        notificationModelDao.delete(notificationModel);
    }

    /**
     * 删除全部通知
     */
    public static void deleteAllModel() {
        ExpressNotificationModelDao notificationModelDao = GreenDaoManager.getInstance().getNewSession().getExpressNotificationModelDao();
        notificationModelDao.deleteAll();
    }

}
