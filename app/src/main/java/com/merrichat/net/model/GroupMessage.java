package com.merrichat.net.model;

import android.text.TextUtils;
import android.util.Log;

import com.merrichat.net.model.dao.GroupMessageDao;
import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amssy on 2018/1/27.
 */

@Entity
public class GroupMessage implements Serializable {

    public static final int TYPE_IMG = 1;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
    public static final int TYPE_VOICE = 2;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
    public static final int TYPE_GIF = 3;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
    public static final int TYPE_VIDEO = 4;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
    public static final int TYPE_TEXT = 5;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
//    public static final int TYPE_TEXT = 5;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
//    public static final int TYPE_TEXT = 5;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
//    public static final int TYPE_TEXT = 5;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
//    public static final int TYPE_TEXT = 5;//1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息

    @Id(autoincrement = true)
    public Long ID;
    @Property(nameInDb = "mid")
    public String mid;
    /**
     * 1-单聊 2-群聊
     */
    @Property(nameInDb = "type")
    public String type;

    @Property(nameInDb = "title")
    public String title;
    /**
     * 文本消息的内容
     */
    @Property(nameInDb = "content")
    public String content;
    /**
     * 发送人id memberId
     */
    @Property(nameInDb = "sender")
    public String sender;

    @Property(nameInDb = "senderName")
    public String senderName;
    /**
     * 接收人id memberId
     */
    @Property(nameInDb = "receiver")
    public String receiver;
    /**
     * 接收人姓名(群聊)
     */
    @Property(nameInDb = "receiverName")
    public String receiverName;

    /**
     * 文件的url地址(服务端返回的)
     */
    @Property(nameInDb = "file")
    public String file;
    /**
     * 语音时长
     */
    @Property(nameInDb = "speechTimeLength")
    public String speechTimeLength;

    /**
     * 文件下载后存到本地的路径
     */
    @Property(nameInDb = "filePath")
    public String filePath;
    /**
     * 1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
     */
    @Property(nameInDb = "fileType")
    public String fileType;

    @Property(nameInDb = "format")
    public String format = "txt";

    /**
     * 发送时间
     */
    @Property(nameInDb = "timestamp")
    public long timestamp = System.currentTimeMillis();

    /**
     * 群聊id
     */
    @Property(nameInDb = "groupId")
    public String groupId;

    /**
     * 群名字(数据库group为关键字，不可用)
     */
    @Property(nameInDb = "group")
    public String group;

    /**
     * 发送人头像（logo + id + .jpg）
     */
    @Property(nameInDb = "header")
    public String header;
    /**
     * 群头像url
     */
    @Property(nameInDb = "logo")
    public String logo;

    /**
     * 消息发送状态是否成功（只有sender为自己时才有用）
     * 0发送中， 1发送失败，  2发送成功
     */
    @Property(nameInDb = "sendState")
    public int sendState;
    /**
     * 视频第一帧的图片或图片的缩略图
     */
    @Property(nameInDb = "thumb")
    public String thumb;
    /**
     * 显示时间的状态
     * -1：未做判断
     * 0：判断后显示
     * 1:判断后隐藏
     */
    @Property(nameInDb = "showTimeState")
    public int showTimeState = -1;

    /**
     * 语音是否已读（只有fileType为语音时有效）
     */
    @Property(nameInDb = "is_read_voice")
    public boolean isReadVoice;
    /**
     * 是否消息免打扰0否1是
     */
    @Property(nameInDb = "inter")
    public String inter;
    /**
     * 是否置顶0否1是
     */
    @Property(nameInDb = "top")
    public String top;
    /**
     * 置顶时间
     */
    @Property(nameInDb = "topts")
    public String topts;

    /**
     * 这条消息是属于哪一个memberId的
     */
    @Property(nameInDb = "private_id")
    public String private_id;

    /**
     * 红包ID
     */
    @Property(nameInDb = "redTid")
    private String redTid;
    /**
     * 红包状态
     */
    @Property(nameInDb = "redStatus")
    private String redStatus;

    @Generated(hash = 1627595031)
    public GroupMessage(Long ID, String mid, String type, String title, String content, String sender, String senderName, String receiver, String receiverName, String file, String speechTimeLength, String filePath, String fileType, String format,
                        long timestamp, String groupId, String group, String header, String logo, int sendState, String thumb, int showTimeState, boolean isReadVoice, String inter, String top, String topts, String private_id, String redTid, String redStatus) {
        this.ID = ID;
        this.mid = mid;
        this.type = type;
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.senderName = senderName;
        this.receiver = receiver;
        this.receiverName = receiverName;
        this.file = file;
        this.speechTimeLength = speechTimeLength;
        this.filePath = filePath;
        this.fileType = fileType;
        this.format = format;
        this.timestamp = timestamp;
        this.groupId = groupId;
        this.group = group;
        this.header = header;
        this.logo = logo;
        this.sendState = sendState;
        this.thumb = thumb;
        this.showTimeState = showTimeState;
        this.isReadVoice = isReadVoice;
        this.inter = inter;
        this.top = top;
        this.topts = topts;
        this.private_id = private_id;
        this.redTid = redTid;
        this.redStatus = redStatus;
    }


    @Generated(hash = 159954481)
    public GroupMessage() {
    }

    public void setPrivate_id(String private_id) {
        this.private_id = private_id;
    }


    public String getPrivate_id() {
        return this.private_id;
    }

    public String getTopts() {
        return this.topts;
    }


    public void setTopts(String topts) {
        this.topts = topts;
    }


    public String getTop() {
        return this.top;
    }


    public void setTop(String top) {
        this.top = top;
    }


    public String getInter() {
        return this.inter;
    }


    public void setInter(String inter) {
        this.inter = inter;
    }


    public boolean getIsReadVoice() {
        return this.isReadVoice;
    }


    public void setIsReadVoice(boolean isReadVoice) {
        this.isReadVoice = isReadVoice;
    }


    public int getShowTimeState() {
        return this.showTimeState;
    }


    public void setShowTimeState(int showTimeState) {
        this.showTimeState = showTimeState;
    }


    public String getThumb() {
        return this.thumb;
    }


    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


    public int getSendState() {
        return this.sendState;
    }


    public void setSendState(int sendState) {
        this.sendState = sendState;
    }


    public String getLogo() {
        return this.logo;
    }


    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getHeader() {
        return this.header;
    }


    public void setHeader(String header) {
        this.header = header;
    }


    public String getGroup() {
        return this.group;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public String getGroupId() {
        return this.groupId;
    }


    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getFormat() {
        return this.format;
    }


    public void setFormat(String format) {
        this.format = format;
    }


    public String getFileType() {
        return this.fileType;
    }


    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public String getFilePath() {
        return this.filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getSpeechTimeLength() {
        return this.speechTimeLength;
    }


    public void setSpeechTimeLength(String speechTimeLength) {
        this.speechTimeLength = speechTimeLength;
    }


    public String getFile() {
        return this.file;
    }


    public void setFile(String file) {
        this.file = file;
    }


    public String getReceiverName() {
        return this.receiverName;
    }


    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


    public String getReceiver() {
        return this.receiver;
    }


    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public String getSenderName() {
        return this.senderName;
    }


    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public String getSender() {
        return this.sender;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getMid() {
        return this.mid;
    }


    public void setMid(String mid) {
        this.mid = mid;
    }


    public Long getID() {
        return this.ID;
    }


    public void setID(Long ID) {
        this.ID = ID;
    }


    public String getRedStatus() {
        return this.redStatus;
    }


    public void setRedStatus(String redStatus) {
        this.redStatus = redStatus;
    }


    public String getRedTid() {
        return this.redTid;
    }


    public void setRedTid(String redTid) {
        this.redTid = redTid;
    }

    /**
     * 设置或更改MessageModel的对象的值
     *
     * @param groupMessage
     */
    public static void setMessageModel(GroupMessage groupMessage) {
        GroupMessageDao groupMessageDao1 = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        QueryBuilder<GroupMessage> qb = groupMessageDao1.queryBuilder();
        List<GroupMessage> msgList = qb.where(GroupMessageDao.Properties.Timestamp.eq(groupMessage.getTimestamp())).list();
        if ((groupMessage.getID() != null && groupMessage.getID() > 0) || (null != msgList && msgList.size() > 0)) {
            GroupMessage entity = msgList.get(0);
            groupMessage.setID(entity.getID());
            updateUserModel(groupMessage);
        } else {
            GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getSession().getGroupMessageDao();
            groupMessageDao.save(groupMessage);
        }
    }

    /**
     * 更新MessageModel对象的信息
     *
     * @param groupMessage
     */
    public static void updateUserModel(GroupMessage groupMessage) {
        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getSession().getGroupMessageDao();
        groupMessageDao.update(groupMessage);
    }

    /**
     * 清除消息内容表
     */
    public static void clearMessageModel() {
        GreenDaoManager.getInstance().getSession().getGroupMessageDao().deleteAll();
    }


    /**
     * 按条件查询到的总条数
     *
     * @param groupId
     * @return
     */
    public static int getListMessageModelCount(String groupId) {
        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        QueryBuilder<GroupMessage> qb = groupMessageDao.queryBuilder();

        QueryBuilder<GroupMessage> where = qb.where(GroupMessageDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()), GroupMessageDao.Properties.GroupId.eq(groupId)).orderAsc(GroupMessageDao.Properties.Timestamp);
        List<GroupMessage> msgList = where.list();
        return msgList == null ? 0 : msgList.size();
    }

    /**
     * 分页查询
     *
     * @param currentPage 从第几条数据开始查询
     * @param pageSize    一共查询多少条
     * @return
     */
    public static List<GroupMessage> getListMessageModelByLimit(String groupId, int currentPage, int pageSize) {
        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        QueryBuilder<GroupMessage> qb = groupMessageDao.queryBuilder();
        QueryBuilder<GroupMessage> where = qb.where(GroupMessageDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()), GroupMessageDao.Properties.GroupId.eq(groupId)).orderAsc(GroupMessageDao.Properties.Timestamp);
        List<GroupMessage> msgList = where.offset(currentPage * 20).limit(pageSize).list();
        return msgList == null ? new ArrayList<GroupMessage>() : msgList;
    }


    /**
     * 查询与某一个群的聊天记录
     *
     * @param groupId
     * @return
     */
    public static List<GroupMessage> getListMessageModel(String groupId) {

        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        QueryBuilder<GroupMessage> qb = groupMessageDao.queryBuilder();

        QueryBuilder<GroupMessage> where = qb.where(GroupMessageDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()), GroupMessageDao.Properties.GroupId.eq(groupId));
        List<GroupMessage> msgList = where.list();
        return msgList == null ? new ArrayList<GroupMessage>() : msgList;
    }

    /**
     * 清除某一个分组的聊天记录
     */
    public static void clearSingleMessageModel(String groupId) {
        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        QueryBuilder<GroupMessage> qb = groupMessageDao.queryBuilder();

        QueryBuilder<GroupMessage> where = qb.where(GroupMessageDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()), GroupMessageDao.Properties.GroupId.eq(groupId));
        List<GroupMessage> msgList = where.list();
        if (msgList != null) {
            deleteAllModel(msgList);
        }
    }

    /***
     * 删除当前用户与某一个群的消息缓存（图片，视频，语言）
     */
    public static void clearThisGroupMessage(String groupId) {
        //清除
        List<GroupMessage> messageList = getListMessageModel(groupId);
        try {
            for (int i = 0; i < messageList.size(); i++) {
                String filePath = messageList.get(i).filePath;
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            //清除数据中记录
            clearSingleMessageModel(groupId);
        }
    }

    public GroupMessage parseListJSONInFor(String response) {
        GroupMessage listModle = new GroupMessage();
        try {
            JSONObject object = new JSONObject(response);
            listModle.setContent(object.optString("content"));
            listModle.setFileType(object.optString("fileType"));
            listModle.setMid(object.optString("mid"));
            listModle.setReceiver(object.optString("receiver"));
            listModle.setReceiverName(object.optString("receiverName"));
            listModle.setSender(object.optString("sender"));
            listModle.setSenderName(object.optString("senderName"));
            listModle.setTimestamp(object.getLong("ts"));
            listModle.setHeader(object.optString("headerUrl"));
            listModle.setLogo(object.optString("baseUrl"));//头像
            listModle.setSpeechTimeLength(object.optString("speechTimeLength"));//语音时长
            listModle.setGroup(object.optString("group"));//群名字
            listModle.setGroupId(object.optString("groupId"));
            listModle.setThumb(object.optString("thumb"));//-图片缩略图url  视频第一帧url
            listModle.setFile(object.optString("file"));//--图片 视频的url  此时content是空串
            listModle.setType(object.optString("type"));
            listModle.setRedTid(object.optString("redTid"));//红包Id
            listModle.setRedStatus(object.optString("redStatus"));//红包状态
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listModle;
    }

    /**
     * 清除与当前某个群的聊天记录
     *
     * @param groupMessage
     */
    public static void deleteAllModel(List<GroupMessage> groupMessage) {
        GroupMessageDao groupMessageDao = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao();
        groupMessageDao.deleteInTx(groupMessage);
    }

}
