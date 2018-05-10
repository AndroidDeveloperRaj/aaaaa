/*
 * 文 件 名:  NoticeMsg.java
 * 版    权:  Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 创 建 人:  文超
 * 创建时间:  Mar 20, 2013 5:32:29 PM
 *
 * 修改内容:  <修改内容>
 * 修改时间:  <修改时间>
 * 修改人:    <修改人>
 */
package com.merrichat.net.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * <功能详细描述>
 *
 * @author 文超
 * @version [版本号, Mar 20, 2013 5:32:29 PM ]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class NoticeMsg implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6814129555219764732L;
    /**
     * 服务器端返回给客户端，表示建立长连接成功
     */
    public static final int PUSH_SERVER_CONNECT = 0;
    /**
     * 服务端发送消息给客户端 1
     */
    public static final int PUSH_SERVER_CONTENT = 1;
    /**
     * 服务器端收到客户端请求类型，2=请求建立长连接
     */
    public static final int PUSH_CLIENT_CONNECT = 2;
    /**
     * 服务器端收到客户端请求类型，3=推送客户端消息成功
     */
    public static final int PUSH_CLIENT_CONTENT = 3;
    /**
     * 客户端向服务器发送心跳包的，4=客户端发送心跳包
     */
    public static final int PUSH_CLIENT_HEART = 4;
    /**
     * 当登录成功时客户端要服务器发送数据(返回的status为500证明服务器错误)
     */
    public static final int PUSH_CLIENT_LOGIN_SUCCESS = 5;

    /**
     * 当退出登录时客户端要服务器发送数据(返回的status为500证明成功)
     */
    public static final int PUSH_CLIENT_LOGIN_EXIT = 6;
    /**
     * 服务器返回给客户端，表示该账号已在其他设备登录
     **/
    public static final int PUSH_SERVER_DISCONTENT = 20;

    /**
     * 服务器给客户端发送心跳包，41
     */
    public static final int PUSH_SERVER_HEART = 41;

    /**
     * 长连客户端心跳包发送时间间隔(默认300秒)(单位 秒)
     */
    public static int CLIENT_HEART_TIME = 80;

    /**
     * 待推送的消息已收到 00
     */
    public static final String NOTICE_STATUS_NEW = "00";
    /**
     * 待推送的消息客户端未发现长连接，不推送 01
     */
    public static final String NOTICE_STATUS_STOP = "01";
    /**
     * 待推送的消息已推送 02
     */
    public static final String NOTICE_STATUS_SEND = "02";
    /**
     * 待推送的消息已推送成功 03
     */
    public static final String NOTICE_STATUS_SUCC = "03";

    /**
     * 美遇的手机客户端 03
     */
    public static final String MSG_CHANNELNO_MOB_MEMBER = "03";


    /************************任务推送类型******************************/


    /**
     * 推送类型：快递通知 10053
     */
    public static final String NOTICE_STATUS_10053 = "10053";
    /**
     * 推送类型：红包超时 10050
     */
    public static final String NOTICE_STATUS_10050 = "10050";

    /**
     * 推送类型： 收到邀请通知 10054
     */
    public static final String NOTICE_STATUS_10054 = "10054";
    /**
     * 推送类型： 收到同意添加好友通知 10055
     */
    public static final String NOTICE_STATUS_10055 = "10055";


    /**
     * 推送消息的唯一ID
     */
    private Long id;
    /**
     * 渠道编号 01：快递员手机客户端
     */
    private String channelNo;

    /**
     * 快递员ID
     */
    private Long channelId; //memberId
    /**
     * 消息类型
     */
    private String type;
    /**
     * 消息对应的业务ID
     */
    private Long documentId;
    /**
     * 消息对应的标题
     */
    private String title;
    /**
     * 消息对应的内容
     */
    private String content;
    /**
     * 额外参数
     **/
    private String extraParam;
    /**
     * 消息响应状态 0：服务器返回给客户端的状态，标识建立长连接成功，
     * 1:服务器推送消息给客户端
     * 2:客户端请求建立长连接
     * 3:客户端推送消息给服务器端
     */
    private int status;

    private String memberId;

    public NoticeMsg() {
    }

    public NoticeMsg(Long channelId, String type, Long documentId,
                     String content) {
        super();
        this.channelId = channelId;
        this.type = type;
        this.documentId = documentId;
        this.content = content;
    }

    public NoticeMsg(Long channelId, String type, Long documentId,
                     String content, String extraParam) {
        super();
        this.channelId = channelId;
        this.type = type;
        this.documentId = documentId;
        this.content = content;
        this.extraParam = extraParam;
    }


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public static NoticeMsg parseJson(String json) {
//        {"channelNo":"01","computerName":"863723020300052","memberId":"208244482293760","status":501}
//        {"":"01","":"有快递员转给你一批代收包裹，请及时查看","":"","":212231130497028,"memberId":"208244482293760","":1,"subtitle":"","taskNum":0,"":"好递","":"999"}
        NoticeMsg noticeMsg = new NoticeMsg();
        try {
            JSONObject jsonObject = new JSONObject(json);
            noticeMsg.setChannelId(jsonObject.optLong("channelId"));
            noticeMsg.setMemberId(jsonObject.optString("memberId"));
            noticeMsg.setChannelNo(jsonObject.optString("channelNo"));
            noticeMsg.setContent(jsonObject.optString("content"));
            noticeMsg.setExtraParam(jsonObject.optString("extraParam"));
            noticeMsg.setId(jsonObject.optLong("id"));
            noticeMsg.setStatus(jsonObject.optInt("status"));
            noticeMsg.setTitle(jsonObject.optString("title"));
            noticeMsg.setType(jsonObject.optString("type"));
            noticeMsg.setDocumentId(jsonObject.optLong("documentId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return noticeMsg;
    }

    @Override
    public String toString() {
        return "NoticeMsg{" +
                "channelId=" + channelId +
                ", id=" + id +
                ", channelNo='" + channelNo + '\'' +
                ", type='" + type + '\'' +
                ", documentId=" + documentId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", extraParam='" + extraParam + '\'' +
                ", status=" + status +
                '}';
    }
}
