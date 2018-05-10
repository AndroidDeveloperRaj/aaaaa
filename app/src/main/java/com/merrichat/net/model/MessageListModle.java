package com.merrichat.net.model;


import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 17/7/27.
 */

@Entity
public class MessageListModle {


//     "baseUrl": "http://publicapi.okdit.net/nfs_data/complogo/",
//             "content": "羽绒哦几个OK了咯",
//             "file": "",
//             "fileType": "5",
//             "group": "",
//             "groupId": 0,
//             "headerUrl": "http://cas.okdit.net/nfs_data/mob/head/",
//             "mid": 323154382471168,
//             "net": "",
//             "receiver": 316069722243072,
//             "receiverName": "18600867834",
//             "redStatus": "",
//             "redTid": "",
//             "sender": 317870888501248,
//             "senderName": "官方机器人",
//             "speechTimeLength": "",
//             "thumb": "",
//             "ts": 1515845758499,
//             "type": "1"

    /**
     * count : 19    --消息个数
     * fileType : 1   -- 1-静态图片  2-语音 3-gif图片 4-视频文件 5-普通文本消息
     * headUrl : http://cas.okdit.net/nfs_data/mob/head/151131561205760.jpg        --头像url
     * inter : 0     --是否消息免打扰  0否1是
     * last :   --发送人id-1最后的一条消息
     * msgts : 1467193947998      --消息时间
     * name : 哈哈   --发送人名字(用户姓名  或者  群名字)
     * senderId : 151131561205760     --发送人id
     * top : 0      --是否置顶  0否1是
     * topts :      --置顶的时间  top = 0时  字段值是null
     * type : 1    	--1单聊 2群聊
     */

    @Id(autoincrement = true)
    private Long ID;

    @Property(nameInDb = "count")
    private int count;


    @Property(nameInDb = "fileType")
    private String fileType;


    @Property(nameInDb = "headUrl")
    private String headUrl;


    @Property(nameInDb = "inter")
    private int inter;


    @Property(nameInDb = "last")
    private String last;


    @Property(nameInDb = "msgts")
    private long msgts;


    @Property(nameInDb = "name")
    private String name;


    @Property(nameInDb = "senderId")
    private String senderId;


    @Property(nameInDb = "top")
    private int top;


    @Property(nameInDb = "topts")
    private String topts;


    @Property(nameInDb = "type")
    private String type;


    @Property(nameInDb = "group_name")
    private String group;


    @Property(nameInDb = "groupId")
    private String groupId;


    @Property(nameInDb = "privateID")
    private String privateID;
    /**
     * text1是判断列表数据是否有值，1有0无，是作为查询条件
     */
    @Property(nameInDb = "Test1")
    private String text1;


    @Property(nameInDb = "Test2")
    private String test2;


    @Property(nameInDb = "Test3")
    private String test3;


    @Generated(hash = 1741866123)
    public MessageListModle(Long ID, int count, String fileType, String headUrl, int inter, String last, long msgts, String name, String senderId,
                            int top, String topts, String type, String group, String groupId, String privateID, String text1, String test2, String test3) {
        this.ID = ID;
        this.count = count;
        this.fileType = fileType;
        this.headUrl = headUrl;
        this.inter = inter;
        this.last = last;
        this.msgts = msgts;
        this.name = name;
        this.senderId = senderId;
        this.top = top;
        this.topts = topts;
        this.type = type;
        this.group = group;
        this.groupId = groupId;
        this.privateID = privateID;
        this.text1 = text1;
        this.test2 = test2;
        this.test3 = test3;
    }

    @Generated(hash = 1396585079)
    public MessageListModle() {
    }


    /**
     * 获取MessageListModle的集合
     */
    public static List<MessageListModle> getListModleInfor() {

        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getSession().getMessageListModleDao().queryBuilder();
        List<MessageListModle> modelList = messageListModleQueryBuilder.list();


//        List<MessageListModle> modelList = new Select().from(MessageListModle.class).execute();
        return modelList == null ? new ArrayList<MessageListModle>() : modelList;
    }

    /**
     * 删除MessageListModle的表
     */
    public static void clearMessageListModele() {
        GreenDaoManager.getInstance().getSession().getMessageListModleDao().deleteAll();

//        new Delete().from(MessageListModle.class).execute();
    }

    /**
     * 删除单条数据
     */
    public static void clearSingleMessageModel(String groupId) {
        MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getSession().getMessageListModleDao();
        MessageListModle modle = messageListModleDao.queryBuilder().where(MessageListModleDao.Properties.GroupId.eq(groupId)).unique();
        messageListModleDao.delete(modle);
    }

    /**
     * 删除单条数据
     */
    public static void clearSingleMessageModelById(String id) {
        QueryBuilder<MessageListModle> messageListModleQueryBuilder = GreenDaoManager.getInstance().getSession().getMessageListModleDao().queryBuilder();
        MessageListModle unique = messageListModleQueryBuilder.where(MessageListModleDao.Properties.ID.eq(id)).unique();
        if (unique != null) {
            GreenDaoManager.getInstance().getSession().delete(unique);
        }
//        new Delete().from(MessageListModle.class).where("groupId=?", groupId).execute();
    }

    /**
     * 删除单条数据
     */
    public static void clearSingleMessageModelByReceivedId(String memberId) {
        MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao();
        QueryBuilder<MessageListModle> qb = messageListModleDao.queryBuilder();
        QueryBuilder<MessageListModle> where = qb.where(MessageListModleDao.Properties.Type.eq("1"), MessageListModleDao.Properties.SenderId.eq(memberId));
        messageListModleDao.deleteInTx(where.list());
    }


    /**
     * 获取messageListModle的信息
     *
     * @return
     */
    public static MessageListModle getMessageListModle() {
        MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getSession().getMessageListModleDao();
        List<MessageListModle> list = messageListModleDao.queryBuilder().list();
        MessageListModle messageListModle = list.size() > 0 ? list.get(0) : new MessageListModle();
        return messageListModle;
    }


    /**
     * 设置messageListModle信息
     *
     * @param messageListModle
     */
    public static void setMessageListModle(MessageListModle messageListModle) {
        List<MessageListModle> msgList = null;
        if ("1".equals(messageListModle.getType())) {
            MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao();
            QueryBuilder<MessageListModle> qb = messageListModleDao.queryBuilder();
            msgList = qb.where(MessageListModleDao.Properties.SenderId.eq(messageListModle.getSenderId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).list();
        } else {
            MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao();
            QueryBuilder<MessageListModle> qb = messageListModleDao.queryBuilder();
            msgList = qb.where(MessageListModleDao.Properties.GroupId.eq(messageListModle.getGroupId()), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).list();
        }
        if (messageListModle.getID() != null && messageListModle.getID() > 0 || (null != msgList && msgList.size() > 0)) {
            MessageListModle entity = msgList.get(0);
            messageListModle.setID(entity.getID());
            updateMessageListModle(messageListModle);
        } else {
//            if (messageListModle.getID() != null && messageListModle.getID() > 0) {
//                updateMessageListModle(messageListModle);
//            } else {
            MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getSession().getMessageListModleDao();
            messageListModleDao.save(messageListModle);
        }
    }


    /**
     * 更新messageListModle对象的信息
     *
     * @param
     */
    public static void updateMessageListModle(MessageListModle messageListModle) {
        MessageListModleDao messageListModleDao = GreenDaoManager.getInstance().getSession().getMessageListModleDao();
        messageListModleDao.update(messageListModle);
    }


    public MessageListModle parseListJSON(String response) {
        MessageListModle listModle = new MessageListModle();
        try {
            JSONObject object = new JSONObject(response);
            listModle.setFileType(object.optString("fileType"));
            listModle.setLast(object.optString("last"));
            listModle.setCount(object.optInt("count"));
            listModle.setType(object.optString("type"));
            listModle.setName(object.optString("name"));
            listModle.setSenderId(object.optString("senderId"));
            listModle.setHeadUrl(object.optString("headUrl"));
            listModle.setTop(object.optInt("top"));
            listModle.setTopts(object.optString("topts"));
            listModle.setInter(object.optInt("inter"));
            listModle.setMsgts(object.optLong("msgts"));
            listModle.setGroupId(object.optString("groupId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listModle;
    }

    public String getTest3() {
        return this.test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }

    public String getTest2() {
        return this.test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }

    public String getText1() {
        return this.text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getPrivateID() {
        return this.privateID;
    }

    public void setPrivateID(String privateID) {
        this.privateID = privateID;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopts() {
        return this.topts;
    }

    public void setTopts(String topts) {
        this.topts = topts;
    }

    public int getTop() {
        return this.top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMsgts() {
        return this.msgts;
    }

    public void setMsgts(long msgts) {
        this.msgts = msgts;
    }

    public String getLast() {
        return this.last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public int getInter() {
        return this.inter;
    }

    public void setInter(int inter) {
        this.inter = inter;
    }

    public String getHeadUrl() {
        return this.headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }


}
