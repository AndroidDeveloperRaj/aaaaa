/**
 * probject:cim-android-sdk
 *
 * @version 2.0.0
 * @author 3979434@qq.com
 */
package com.merrichat.net.activity.message.cim.filter;

import android.text.TextUtils;
import android.util.Log;

import com.merrichat.net.activity.message.cim.constant.CIMConstant;
import com.merrichat.net.activity.message.cim.model.ReplyBody;
import com.merrichat.net.model.MessageModel;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *  客户端消息解码
 */
public class ClientMessageDecoder extends CumulativeProtocolDecoder {

    final static String TAG = ClientMessageDecoder.class.getSimpleName();

    private IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);

    @Override
    public boolean doDecode(IoSession iosession, IoBuffer iobuffer,
                            ProtocolDecoderOutput out) throws Exception {
        boolean complete = false;

        while (iobuffer.hasRemaining()) {
            byte b = iobuffer.get();
            /**
             * CIMConstant.MESSAGE_SEPARATE 为消息界限
             * 当一次收到多个消息时，以此分隔解析多个消息
             */
            if (b == CIMConstant.MESSAGE_SEPARATE) {

                complete = true;
                break;
            } else {
                buff.put(b);
            }
        }

        if (complete) {
            buff.flip();
            byte[] bytes = new byte[buff.limit()];
            buff.get(bytes);
            String message = new String(bytes, CIMConstant.UTF8);
            buff.clear();

            //打印出收到的消息
            Log.i(TAG, message);

            try {
                Object msg = mappingMessageObject(message);
                out.write(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return complete;
    }

    private Object mappingMessageObject(String message) throws Exception {

        if (CIMConstant.CMD_HEARTBEAT_REQUEST.equals(message)) {
            return message;
        }


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = (Document) builder.parse(new ByteArrayInputStream(message.getBytes(CIMConstant.UTF8)));

        String name = doc.getDocumentElement().getTagName();
        if (name.equals("reply")) {//接收到的服务器的响应（如：心跳响应，调用CIMPushManager.sendRequest()的响应）
            ReplyBody reply = new ReplyBody();
            reply.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
            reply.setCode(doc.getElementsByTagName("code").item(0).getTextContent());
            String timestampStr = doc.getElementsByTagName("timestamp").item(0).getTextContent();
            long timestamp;
            if (TextUtils.isEmpty(timestampStr)) {
                timestamp = System.currentTimeMillis();
            } else {
                timestamp = Long.parseLong(timestampStr);
            }
            reply.setTimestamp(timestamp);
            NodeList items = doc.getElementsByTagName("data").item(0).getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                reply.getData().put(node.getNodeName(), node.getTextContent());
            }
            return reply;
        }
        if (name.equals("message")) {//接收到的别人发的消息

            MessageModel body = new MessageModel();
            body.setType(doc.getElementsByTagName("type").item(0).getTextContent());
            body.setContent(doc.getElementsByTagName("content").item(0).getTextContent());
            body.setFile(doc.getElementsByTagName("file").item(0).getTextContent());
            body.setFileType(doc.getElementsByTagName("fileType").item(0).getTextContent());
            body.setTitle(doc.getElementsByTagName("title").item(0).getTextContent());
            body.setSender(doc.getElementsByTagName("sender").item(0).getTextContent());
            body.setReceiver(doc.getElementsByTagName("receiver").item(0).getTextContent());
            body.setFormat(doc.getElementsByTagName("format").item(0).getTextContent());
            body.setMid(doc.getElementsByTagName("mid").item(0).getTextContent());
            body.setTimestamp(Long.valueOf(doc.getElementsByTagName("timestamp").item(0).getTextContent()).longValue());
            body.setGroupId(doc.getElementsByTagName("groupId").item(0).getTextContent());
            body.setGroup(doc.getElementsByTagName("group").item(0).getTextContent());
            body.setLogo(doc.getElementsByTagName("logo").item(0).getTextContent());
            body.setReceiverName(doc.getElementsByTagName("receiverName").item(0).getTextContent());
            body.setSenderName(doc.getElementsByTagName("senderName").item(0).getTextContent());
            body.setSpeechTimeLength(doc.getElementsByTagName("speechTimeLength").item(0).getTextContent());
            body.setThumb(doc.getElementsByTagName("thumb").item(0).getTextContent());
            body.setHeader(doc.getElementsByTagName("header").item(0).getTextContent());
            body.setInter(doc.getElementsByTagName("inter").item(0).getTextContent());
            body.setTop(doc.getElementsByTagName("top").item(0).getTextContent());
            body.setTopts(doc.getElementsByTagName("topts").item(0).getTextContent());
            body.setRedTid(doc.getElementsByTagName("redTid").item(0).getTextContent());
            body.setRedStatus(doc.getElementsByTagName("redStatus").item(0).getTextContent());
//			LogUtil.e("@@@","doc.getElementsByTagName(\"topts\").item(0).getTextContent()-----"+doc.getElementsByTagName("topts").item(0).getTextContent());
//			LogUtil.e("@@@","doc.getElementsByTagName(revoke)-----"+doc.getElementsByTagName("revoke"));
//			LogUtil.e("@@@","doc.getElementsByTagName(revoke).item(0)-----"+doc.getElementsByTagName("revoke").item(0));
            if (null != doc.getElementsByTagName("revoke").item(0)) {
                body.setRevoke(doc.getElementsByTagName("revoke").item(0).getTextContent());
            }
            return body;
        }

        return null;
    }


}
