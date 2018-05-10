package com.merrichat.net.model;

import android.text.TextUtils;
import android.util.Log;

import com.merrichat.net.model.dao.GroupMessageDao;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.NoticeModelDao;
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
 * Created by amssy on 17/7/29.
 * 消息列表聊天记录model
 */

@Entity
public class MessageModel implements Serializable {
    /**
     * 发送消息send_state状态，0发送中
     */
    public static final int SEND_STATE_SENDING = 0;
    /**
     * 发送消息send_state状态，1发送成功
     */
    public static final int SEND_STATE_SUCCEED = 1;
    /**
     * 发送消息send_state状态，2发送失败
     */
    public static final int SEND_STATE_FAILURE = 2;

    private static final long serialVersionUID = 1L;


    @Id(autoincrement = true)
    private Long ID;


    @Property(nameInDb = "mid")
    private String mid;


    //消息撤回标识  1: 撤回
    @Property(nameInDb = "revoke")
    private String revoke;

    /**
     * 1-单聊 2-群聊 3-系统消息（消息类型）
     */
    @Property(nameInDb = "type")
    private String type;

    @Property(nameInDb = "title")
    private String title;
    /**
     * 文本消息的内容
     */
    @Property(nameInDb = "content")
    private String content;
    /**
     * 发送人id memberId
     */
    @Property(nameInDb = "sender")
    private String sender;

    @Property(nameInDb = "senderName")
    private String senderName;
    /**
     * 接收人id memberId
     */
    @Property(nameInDb = "receiver")
    private String receiver;
    /**
     * 接收人姓名(群聊)
     */
    @Property(nameInDb = "receiverName")
    private String receiverName;

    /**
     * 文件的url地址(服务端返回的)
     */
    @Property(nameInDb = "file")
    private String file;
    /**
     * 语音时长
     */
    @Property(nameInDb = "speech_time_length")
    private String speechTimeLength;

    /**
     * 文件下载后存到本地的路径
     */
    @Property(nameInDb = "file_path")
    private String filePath;
    /**
     * 1-静态图片 2-语音 3-gif图片 4-视频文件 5-普通文本消息
     */
    @Property(nameInDb = "fileType")
    private String fileType;

    @Property(nameInDb = "format")
    private String format = "txt";

    /**
     * 发送时间
     */
    @Property(nameInDb = "timestamp")
    private long timestamp = System.currentTimeMillis();

    /**
     * 群聊id
     */
    @Property(nameInDb = "groupId")
    private String groupId;

    /**
     * 群名字(数据库group为关键字，不可用)
     */
    @Property(nameInDb = "group_name")
    private String group;

    /**
     * 群聊时用： 圈子头像（logo + groupId + .jpg）
     */
    @Property(nameInDb = "logo")
    private String logo;

    /**
     * 发送人头像（logo + id + .jpg）
     */
    @Property(nameInDb = "header")
    private String header;

    /**
     * 消息发送状态是否成功（只有sender为自己时才有用）
     * 0发送中， 1发送失败，  2发送成功
     */
    @Property(nameInDb = "send_state")
    private int sendState;
    /**
     * 视频第一帧的图片或图片的缩略图
     */
    @Property(nameInDb = "thumb")
    private String thumb;
    /**
     * 显示时间的状态
     * -1：未做判断
     * 0：判断后显示
     * 1:判断后隐藏
     */
    @Property(nameInDb = "showTimeState")
    private int showTimeState = -1;

    /**
     * 语音是否已读（只有fileType为语音时有效）
     */
    @Property(nameInDb = "is_read_voice")
    private boolean isReadVoice;

    /**
     * 是否消息免打扰0否1是
     */
    @Property(nameInDb = "inter")
    private String inter;
    /**
     * 是否置顶0否1是
     */
    @Property(nameInDb = "top")
    private String top;
    /**
     * 置顶时间
     */
    @Property(nameInDb = "topts")
    private String topts;
    /**
     * 这条消息是属于哪一个memberId的
     */
    @Property(nameInDb = "private_id")
    private String private_id;

    /**
     * 临时新加字段（为以后留着扩展）
     */
    @Property(nameInDb = "new_1")
    private String new_1;
    /**
     * 临时新加字段（为以后留着扩展）
     */
    @Property(nameInDb = "new_2")
    private String new_2;
    /**
     * 临时新加字段（为以后留着扩展）
     */
    @Property(nameInDb = "new_3")
    private String new_3;
    /**
     * 判断撤销的显示问题
     */
    @Property(nameInDb = "typeRevoke")
    private String typeRevoke = "0";
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

    @Generated(hash = 1699352037)
    public MessageModel() {
    }

    @Generated(hash = 1826731661)
    public MessageModel(Long ID, String mid, String revoke, String type, String title, String content, String sender, String senderName, String receiver, String receiverName, String file, String speechTimeLength, String filePath,
                        String fileType, String format, long timestamp, String groupId, String group, String logo, String header, int sendState, String thumb, int showTimeState, boolean isReadVoice, String inter, String top, String topts,
                        String private_id, String new_1, String new_2, String new_3, String typeRevoke, String redTid, String redStatus) {
        this.ID = ID;
        this.mid = mid;
        this.revoke = revoke;
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
        this.logo = logo;
        this.header = header;
        this.sendState = sendState;
        this.thumb = thumb;
        this.showTimeState = showTimeState;
        this.isReadVoice = isReadVoice;
        this.inter = inter;
        this.top = top;
        this.topts = topts;
        this.private_id = private_id;
        this.new_1 = new_1;
        this.new_2 = new_2;
        this.new_3 = new_3;
        this.typeRevoke = typeRevoke;
        this.redTid = redTid;
        this.redStatus = redStatus;
    }

    public static List<MessageModel> getListMessageModel() {
        QueryBuilder<MessageModel> messageQueryBuilder = GreenDaoManager.getInstance().getSession().getMessageModelDao().queryBuilder();
        QueryBuilder<MessageModel> where = messageQueryBuilder.where(MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()));
        List<MessageModel> msgList = where.list();
//        List<Message> msgList = new Select().from(Message.class).where("private_id=?", UserModel.getUserModel().getMEMBER_ID()).execute();
        return msgList == null ? new ArrayList<MessageModel>() : msgList;
    }


    /**
     * 按条件查询到的总条数
     *
     * @param memberId
     * @return
     */
    public static int getListMessageModelCount(String memberId) {
        QueryBuilder<MessageModel> messageQueryBuilder = GreenDaoManager.getInstance().getSession().getMessageModelDao().queryBuilder();

        QueryBuilder<MessageModel> where = messageQueryBuilder.where(MessageModelDao.Properties.Type.eq(1), MessageModelDao.Properties.Receiver.eq(memberId), MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()),
                messageQueryBuilder.or(MessageModelDao.Properties.Sender.eq(memberId), MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId())));
        int size = where.list().size();
        return size;
    }


    /**
     * 查询与某一个人的聊天记录
     *
     * @param memberId
     * @return
     */
    public static List<MessageModel> getListMessageModel(String memberId) {
//        QueryBuilder<MessageModel> messageQueryBuilder = GreenDaoManager.getInstance().getSession().getMessageModelDao().queryBuilder();

//        QueryBuilder<MessageModel> where = messageQueryBuilder.where(MessageModelDao.Properties.Type.eq(1), MessageModelDao.Properties.Receiver.eq(memberId), MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId()),
//                messageQueryBuilder.or(MessageModelDao.Properties.Sender.eq(memberId), MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId())));
//        List<MessageModel> msgList = where.list();

        MessageModelDao messageDao = GreenDaoManager.getInstance().getNewSession().getMessageModelDao();
        QueryBuilder<MessageModel> qb = messageDao.queryBuilder();

        QueryBuilder<MessageModel> where = qb.where(MessageModelDao.Properties.Type.eq("1")
                , MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId())
                , qb.or(MessageModelDao.Properties.Sender.eq(memberId)
                        , MessageModelDao.Properties.Receiver.eq(memberId))).orderAsc(MessageModelDao.Properties.Timestamp);
        List<MessageModel> msgList = where.list();
        return msgList == null ? new ArrayList<MessageModel>() : msgList;
    }


    /**
     * 分页查询
     *
     * @param currentPage 从第几条数据开始查询
     * @param pageSize    一共查询多少条
     * @return
     */
    public static List<MessageModel> getListMessageModelByLimit(String memberId, int currentPage, int pageSize) {


        MessageModelDao messageDao = GreenDaoManager.getInstance().getNewSession().getMessageModelDao();
        QueryBuilder<MessageModel> qb = messageDao.queryBuilder();


        QueryBuilder<MessageModel> where = qb.where(MessageModelDao.Properties.Type.eq("1")
                , MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId())

                , qb.or(MessageModelDao.Properties.Sender.eq(memberId)
                        , MessageModelDao.Properties.Receiver.eq(memberId))).orderAsc(MessageModelDao.Properties.Timestamp);

        List<MessageModel> msgList = where.offset(currentPage * 20).limit(pageSize).list();
        return msgList == null ? new ArrayList<MessageModel>() : msgList;
    }

    /**
     * 清除与某一个人的聊天记录
     */
    public static void clearSingleMessageModel(String receiver) {
        MessageModelDao messageDao = GreenDaoManager.getInstance().getNewSession().getMessageModelDao();
        QueryBuilder<MessageModel> qb = messageDao.queryBuilder();
        QueryBuilder<MessageModel> where = qb.where(MessageModelDao.Properties.Type.eq("1")
                , MessageModelDao.Properties.Private_id.eq(UserModel.getUserModel().getMemberId())

                , qb.or(MessageModelDao.Properties.Sender.eq(receiver)
                        , MessageModelDao.Properties.Receiver.eq(receiver)));
        messageDao.deleteInTx(where.list());
    }

    /***
     * 删除某个人的消息缓存（图片，视频，语言）
     */
    public static void clearThisUserMessage() {
        List<MessageModel> messageList = getListMessageModel();
        for (int i = 0; i < messageList.size(); i++) {
            String filePath = messageList.get(i).filePath;
            if (!TextUtils.isEmpty(filePath)) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 删除与某一个人的聊天记录
     *
     * @param memberId
     */
    public static void clearThisUserMessagePerson(String memberId) {
        List<MessageModel> messageList = getListMessageModel(memberId);
        for (int i = 0; i < messageList.size(); i++) {
            String filePath = messageList.get(i).filePath;
            if (!TextUtils.isEmpty(filePath)) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 清除消息内容表
     */
    public static void clearMessageModel() {
        GreenDaoManager.getInstance().getSession().getMessageModelDao().deleteAll();
//        new Delete().from(Message.class).execute();
    }


    /**
     * 获取MessageModel对象
     *
     * @return
     */
    public static MessageModel getMessageModel() {
        MessageModelDao messageDao = GreenDaoManager.getInstance().getSession().getMessageModelDao();
        List<MessageModel> list = messageDao.queryBuilder().list();
        MessageModel message = list.size() > 0 ? list.get(0) : new MessageModel();
        return message;
    }


    /**
     * 设置或更改MessageModel的对象的值
     *
     * @param messageModel
     */
    public static void setMessageModel(MessageModel messageModel) {
        MessageModelDao messageDao1 = GreenDaoManager.getInstance().getNewSession().getMessageModelDao();
        QueryBuilder<MessageModel> qb = messageDao1.queryBuilder();
        List<MessageModel> msgList = qb.where(MessageModelDao.Properties.Timestamp.eq(messageModel.getTimestamp())).list();
        if ((messageModel.getID() != null && messageModel.getID() > 0) || (null != msgList && msgList.size() > 0)) {
            MessageModel entity = msgList.get(0);
            messageModel.setID(entity.getID());
            updateUserModel(messageModel);
        } else {
            MessageModelDao messageDao = GreenDaoManager.getInstance().getSession().getMessageModelDao();
            messageDao.save(messageModel);
        }
    }


    /**
     * 更新MessageModel对象的信息
     *
     * @param messageModel
     */
    public static void updateUserModel(MessageModel messageModel) {
        MessageModelDao messageDao = GreenDaoManager.getInstance().getSession().getMessageModelDao();
        messageDao.update(messageModel);
    }


    public MessageModel parseListJSONInFor(String response) {
        MessageModel listModle = new MessageModel();
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
            listModle.setType(object.optString("type"));
            listModle.setThumb(object.optString("thumb"));//-图片缩略图url  视频第一帧url
            listModle.setFile(object.optString("file"));//--图片 视频的url  此时content是空串
            listModle.setRevoke(object.optString("revoke"));//消息撤回标识  1: 撤回
            listModle.setRedTid(object.optString("redTid"));//红包Id
            listModle.setRedStatus(object.optString("redStatus"));//红包状态
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listModle;
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

    public String getTypeRevoke() {
        return this.typeRevoke;
    }

    public void setTypeRevoke(String typeRevoke) {
        this.typeRevoke = typeRevoke;
    }

    public String getNew_3() {
        return this.new_3;
    }

    public void setNew_3(String new_3) {
        this.new_3 = new_3;
    }

    public String getNew_2() {
        return this.new_2;
    }

    public void setNew_2(String new_2) {
        this.new_2 = new_2;
    }

    public String getNew_1() {
        return this.new_1;
    }

    public void setNew_1(String new_1) {
        this.new_1 = new_1;
    }

    public String getPrivate_id() {
        return this.private_id;
    }

    public void setPrivate_id(String private_id) {
        this.private_id = private_id;
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

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getRevoke() {
        return this.revoke;
    }

    public void setRevoke(String revoke) {
        this.revoke = revoke;
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
}
