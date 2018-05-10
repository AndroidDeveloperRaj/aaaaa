package com.merrichat.net.activity.meiyu;

import android.opengl.EGLContext;
import android.util.Log;

import com.merrichat.net.app.MerriApp;
import com.merrichat.net.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.LinkedList;

import io.socket.emitter.Emitter;

/**
 * Created by amssy on 17/11/22.
 * 视频通话核心类
 */

public class WebRtcClient {
    private final static String TAG = WebRtcClient.class.getCanonicalName();
    private final static int MAX_PEER = 2;
    private boolean[] endPoints = new boolean[MAX_PEER];

    /**
     * ## PeerConnectionFactory Android WebRTC最核心的类。
     * 理解这个类并了解它如何创建其他任何事情是深入了解Android中WebRTC的关键。
     */
    public PeerConnectionFactory peerConnectionFactory;
    private HashMap<String, Peer> peers = new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;


    /**
     * MediaConstraints是支持不同约束的WebRTC库方式的类，可以加载到MediaStream中的音频和视频轨道。
     * 具体参考规范查看支持列表。对于大多数需要MediaConstraints的方法，一个简单的MediaConstraints实例就可以做到。
     * <p>
     * 要添加实际约束，可以定义KeyValuePairs，其并将推送到约束的mandatory或者optional列表。
     */
    private MediaConstraints pcConstraints = new MediaConstraints();

    /**
     * 视频流
     */
    public MediaStream mediaStream;

    /**
     * 允许方法开启、停止设备捕获视频。
     * 这在为了延长电池寿命而禁止视频捕获的情况下比较有用
     */
    public AudioSource audioSource;
    public VideoSource videoSource;
    public RtcListener mListener;
    private String socketHost;
    private String toMemberId;

    private MessageHandler messageHandler;

    /**
     * Implement this interface to be notified of events.
     */
    public interface RtcListener {
        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onRemoveRemoteStream(int endPoint);
    }

    private interface Command {
        void execute(String peerId, JSONObject payload) throws JSONException;
    }


    /**
     * WebRtcClient 构造方法
     * PeerConnectionFactory.initializeAndroidGlobals(
     * context,//上下文，可自定义监听
     * initializeAudio,//是否初始化音频，布尔值
     * initializeVideo,//是否初始化视频，布尔值
     * videoCodecHwAcceleration,//是否支持硬件加速，布尔值
     * renderEGLContext);//是否支持硬件渲染，布尔值
     */
    public WebRtcClient(RtcListener listener, String host, PeerConnectionParameters params, EGLContext mEGLcontext, String toMemberId) {
        mListener = listener;
        pcParams = params;
        this.socketHost = host;
        this.toMemberId = toMemberId;
        PeerConnectionFactory.initializeAndroidGlobals(mListener, true, true,
                pcParams.videoCodecHwAcceleration, mEGLcontext);
        peerConnectionFactory = new PeerConnectionFactory();

    }


    public void setVideoChatConnection() {
        messageHandler = new MessageHandler();
        iceServers = new LinkedList<>();
        iceServers.add(new PeerConnection.IceServer("stun:59.110.12.50:3478"));
        if (MerriApp.URL_ENVIRONMENT_VARIABLE == 1) {
            iceServers.add(new PeerConnection.IceServer("turn:39.107.36.3:3478", "okdi", "123456"));
        } else if (MerriApp.URL_ENVIRONMENT_VARIABLE == 2) {
            iceServers.add(new PeerConnection.IceServer("turn:39.106.2.162:3478", "okdi", "123456"));
            iceServers.add(new PeerConnection.IceServer("turn:123.56.228.241:3478", "okdi", "123456"));
        }
        /**
         * 媒体限制  约束
         */
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        MerriApp.socket.on("message", messageHandler.onMessage);
    }


    /**
     * Send a message through the signaling server
     */
    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        message.put("toMemberId", toMemberId);
        message.put("fromMemberId", UserModel.getUserModel().getMemberId());
        MerriApp.socket.emit("message", message.toString());
        Log.e("@@@", "------执行sendMessage事件-------");
    }

    /**
     * Send a message through the signaling server
     */
    public void sendMessage(String to, String type, String toMemberId, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);
        message.put("toMemberId", toMemberId);
        message.put("fromMemberId", UserModel.getUserModel().getMemberId());
        MerriApp.socket.emit("message", message.toString());
        Log.e("@@@", "------执行sendMessage事件-------");
    }

    private class MessageHandler {
        private HashMap<String, Command> commandMap;

        private MessageHandler() {
            Log.d("@@@", "MessageHandler.MessageHandler 126 行");
            this.commandMap = new HashMap<>();
            commandMap.put("init", new CreateOfferCommand());
            commandMap.put("offer", new CreateAnswerCommand());
            commandMap.put("answer", new SetRemoteSDPCommand());
            commandMap.put("candidate", new AddIceCandidateCommand());
        }

        private Emitter.Listener onMessage = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("@@@", "------监听到onMessage事件-------");
                String message = "";
                for (Object objs : args) {
                    Log.e("@@@", "************* objs:" + objs);
                    message = objs.toString();
                }
                Log.d("@@@", "onMessage.calls 137行");
                try {
                    JSONObject data = new JSONObject(message);
                    String from = data.getString("from");
                    String type = data.getString("type");
                    JSONObject payload = null;
                    if (!type.equals("init")) {
                        payload = data.getJSONObject("payload");
                    }
                    // if peer is unknown, try to add him
                    if (!peers.containsKey(from)) {
                        // if MAX_PEER is reach, ignore the call
                        int endPoint = findEndPoint();
                        if (endPoint != MAX_PEER) {
                            Peer peer = addPeer(from, endPoint);

                            /**
                             * ### addStream这个是用来将MediaStream添加到PeerConnection中的，如同它的命名一样。
                             * 如果你想要对方看到你的视频，听到你的声音，就需要用到这个方法
                             */
                            peer.peerConnection.addStream(mediaStream);
                            commandMap.get(type).execute(from, payload);
                        }
                    } else {
                        commandMap.get(type).execute(from, payload);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * ###createOffer/createAnswer 这两个方法用于原始通话的建立。
     * 如你所知，在WebRTC中，已经有了caller和callee的概念，一个是呼叫，一个是应答。
     * createOffer是caller使用的，它需要一个sdpObserver，它允许获取和传输会话描述协议Session Description Protocol (SDP)给对方，
     * 还需要一个MediaConstraint。一旦对方得到了这个请求，它将创建一个应答并将其传输给caller。
     * SDP是用来给对方描述期望格式的数据（如video、formats、codecs、encryption、resolution、 size等）。
     * 一旦caller收到这个应答信息，双方就相互建立的通信需求达成了一致，如视频、音频、解码器等。
     */
    private class CreateOfferCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("@@@", "CreateOfferCommand 58 行");
            Log.d(TAG, "CreateOfferCommand");
            Peer peer = peers.get(peerId);

            /**
             * ### createOffer / createAnswer这两个方法用于原始通话的建立。
             * 如你所知，在WebRTC中，已经有了caller的概念，一个是呼叫，一个是应答.createOffer是caller使用的，它需要一个sdpObserver，
             * 它允许获取和传输会话描述协议会话描述协议（SDP）给对方，还需要一个MediaConstraint。一旦对方得到了这个请求，
             * 它将创建一个应答并将其传输给caller.SDP是用来给对方描述期望格式的数据（如视频，格式，编码解码器，加密，分辨率，大小等）
             * 一旦caller收到这个应答信息，双方就相互建立的通信需求达成了一致，如视频，音频，解码器等
             */
            peer.peerConnection.createOffer(peer, pcConstraints);
        }
    }


    /**
     * ###setLocalDescription/setRemoteDescription 这个是用来设置createOffer和createAnswer产生的SDP数据的，
     * 包含从远端获取到的数据。它允许内部PeerConnection 配置链接以便一旦开始传输音频和视频就可以开始真正工作。
     */
    private class CreateAnswerCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("@@@", "CreateAnswerCommand 67 行");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );

            /**
             * ### setLocalDescription / setRemoteDescription这个是用来设置createOffer和createAnswer产生的SDP数据，包含从远端获取到的数据。
             * 它允许内部PeerConnection配置链接以便一旦开始传输音频和视频就可以开始真正工作。
             */

            peer.peerConnection.setRemoteDescription(peer, sdp);

            /**
             * ### createOffer / createAnswer这两个方法用于原始通话的建立。
             * 如你所知，在WebRTC中，已经有了caller的概念，一个是呼叫，一个是应答.createOffer是caller使用的，它需要一个sdpObserver，
             * 它允许获取和传输会话描述协议会话描述协议（SDP）给对方，还需要一个MediaConstraint。一旦对方得到了这个请求，
             * 它将创建一个应答并将其传输给caller.SDP是用来给对方描述期望格式的数据（如视频，格式，编码解码器，加密，分辨率，大小等）
             * 一旦caller收到这个应答信息，双方就相互建立的通信需求达成了一致，如视频，音频，解码器等
             */
            peer.peerConnection.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("@@@", "SetRemoteSDPCommand 80 行");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.peerConnection.setRemoteDescription(peer, sdp);
        }
    }


    /**
     * ###addIceCandidate 一旦内部IceFramework发现有candidates允许其他方连接你时，就会创建IceCandidates 。
     * 当通过PeerConnectionObserver.onIceCandidate传递数据到对方时，需要通过任何一个你选择的信号通道获取到对方的IceCandidates。
     * 使用addIceCandidate 添加它们到PeerConnection，以便PeerConnection可以通过已有信息试图连接对方。
     */
    private class AddIceCandidateCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("@@@", "AddIceCandidateCommand  92 行");
            PeerConnection pc = peers.get(peerId).peerConnection;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("id"),
                        payload.getInt("label"),
                        payload.getString("candidate")
                );

                /**
                 * ### addIceCandidate一旦内部IceFramework发现有考生允许其他方连接你时，就会创建IceCandidates。
                 * 当通过PeerConnectionObserver.onIceCandidate传递数据到对方时，需要通过任何一个你选择的信号通道获取到对方的IceCandidates。
                 * 使用addIceCandidate添加它们到PeerConnection等，以便PeerConnection这可以通过已有信息试图连接对方
                 */
                pc.addIceCandidate(candidate);
            }
        }
    }


    /**
     * ###PeerConnectionObserver 这个接口提供了一种监测PeerConnection事件的方法，
     * 例如收到MediaStream时，或者发现iceCandidates 时，或者需要重新建立通讯时。
     * 这些在功能上与web相对应，如果你学习过相关web开发理解这个不会很困难，或者学习WebRTC入门。
     * 这个接口必须被实现，以便你可以有效处理收到的事件，例如当对方变为可见时，向他们发送信号iceCandidates。
     * <p>
     * <p>
     * peer监听器
     */
    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection peerConnection;
        private String id;
        private int endPoint;


        /**
         * offer信令创建成功后会调用SdpObserver监听中的onCreateSuccess()响应函数，
         * 在这里B会通过pc.setLocalDescription将offer信令（SDP描述符）赋给自己的PC对象，同时将offer信令发送给A 。
         *
         * @param sdp
         */
        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            Log.d("@@@", "Peer.onCreateSuccess 188 行");
            // TODO: modify sdp to use pcParams prefered codecs
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
                sendMessage(id, sdp.type.canonicalForm(), payload);


                /**
                 * ### setLocalDescription / setRemoteDescription这个是用来设置createOffer和createAnswer产生的SDP数据，包含从远端获取到的数据。
                 * 它允许内部PeerConnection配置链接以便一旦开始传输音频和视频就可以开始真正工作。
                 *
                 *
                 */
                peerConnection.setLocalDescription(Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {
        }

        @Override
        public void onCreateFailure(String s) {
        }

        @Override
        public void onSetFailure(String s) {
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                Log.d("@@@", "onIceConnectionChange 216 行");
//                removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");  //断开连接 测试不再活跃，这可能是一个暂时的状态，可以自我恢复。
            } else if (iceConnectionState == PeerConnection.IceConnectionState.CLOSED) {
                mListener.onStatusChanged("CLOSED");  //ICE代理关闭，不再应答任何请求。
            } else if (iceConnectionState == PeerConnection.IceConnectionState.CHECKING) {
                mListener.onStatusChanged("CHECKING");  // ICE 代理已收到至少一个远程候选，并进行校验，无论此时是否有可用连
            } else if (iceConnectionState == PeerConnection.IceConnectionState.COMPLETED) {
                mListener.onStatusChanged("COMPLETED"); //ICE代理已经发现了可用的连接，不再测试远程候选。
            } else if (iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                mListener.onStatusChanged("FAILED"); //ICE候选测试了所有远程候选没有发现匹配的候选。也可能有些候选中发现了
            } else if (iceConnectionState == PeerConnection.IceConnectionState.NEW) {
                mListener.onStatusChanged("NEW"); // ICE 代理正在搜集地址或者等待远程候选可用。
            } else if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                mListener.onStatusChanged("CONNECTED"); // ICE代理至少对每个候选发现了一个可用的连接，此时仍然会继续测试远程候选以便发现更优的连接。同时可能在继续收集候选。
            }


        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        }


        /**
         * @param candidate A、B分别创建PC实例pc（配置了穿透服务器地址） 。
         *                  当网络候选可用时，PeerConnection.Observer监听会调用onIceCandidate()响应函数并提供IceCandidate（里面包含穿透所需的信息）的对象。
         *                  在这里，我们可以让A、B将IceCandidate对象的内容发送给对方。
         *                  <p>
         *                  A、B收到对方发来的candidate信令后，利用pc.addIceCandidate()方法将穿透信息赋给各自的PeerConnection对象。
         */
        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            Log.d("@@@", "onIceCandidate 227 行");
            try {
                JSONObject payload = new JSONObject();
                payload.put("label", candidate.sdpMLineIndex);
                payload.put("id", candidate.sdpMid);
                payload.put("candidate", candidate.sdp);
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /**
         * ###addStream 这个是用来将MediaStream 添加到PeerConnection中的,如同它的命名一样。
         * 如果你想要对方看到你的视频、听到你的声音，就需要用到这个方法。
         * <p>
         * <p>
         * 在连接通道正常的情况下，对方的PeerConnection.Observer监听就会调用onAddStream()响应函数并提供接收到的媒体流
         *
         * @param mediaStream
         */
        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "onAddStream " + mediaStream.label() + "241 行");
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            mListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream " + mediaStream.label() + " 248 行");
            removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
        }

        @Override
        public void onRenegotiationNeeded() {

        }

        public Peer(String id, int endPoint) {
            Log.d(TAG, "new Peer: " + id + " " + endPoint + " 261 行");

            /**
             * ### PeerConnectionObserver这个接口提供了一种监测PeerConnection事件的方法，例如收到MediaStream时，
             * 或者发现iceCandidates时，或需要重新建立通讯时。这些在功能上与web相对应，如果你学习过相关的web开发理解这个不会很困难，
             * 或者学习的WebRTC入门。这个接口必须被实现，以便你可以有效处理收到的事件，例如当对方变为可见时，向他们发送信号iceCandidates。
             *
             *
             * 创建PeerConnection的对象
             * iceServers  ICE服务器列表
             * pcConstraints  MediaConstraints   约束  媒体限制
             */
            this.peerConnection = peerConnectionFactory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;
            peerConnection.addStream(mediaStream); //, new MediaConstraints();
            mListener.onStatusChanged("CONNECTING");
            // 添加本地的视频回调
            mListener.onLocalStream(mediaStream);

        }
    }


    /**
     * 添加用户
     *
     * @param id
     * @param endPoint
     * @return
     */
    private Peer addPeer(String id, int endPoint) {
        Log.d("@@@", "addPeer 273 行");
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    /**
     * 移除  断开
     *
     * @param id
     */
    private void removePeer(String id) {
        Log.d("@@@", "removePeer 282 行");
        Peer peer = peers.get(id);
        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.peerConnection.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }


    private int findEndPoint() {
        Log.d("@@@", "findEndPoint 378 行");
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }


    public void setCamera() {
        Log.d("@@@", "setCamera 404 行");
        mediaStream = peerConnectionFactory.createLocalMediaStream("ARDAMS");
        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoSource = peerConnectionFactory.createVideoSource(getVideoCapturer(), videoConstraints);
            VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);

            mediaStream.addTrack(localVideoTrack);

            audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
            mediaStream.addTrack(peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource));
            mListener.onLocalStream(mediaStream);
            Log.e("----->>", "pcParams.videoCallEnabled :" + pcParams.videoCallEnabled);
        } else {
            audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
            mediaStream.addTrack(peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource));
            mListener.onLocalStream(mediaStream);
        }
    }


    /**
     * 获取视频源videoSource
     *
     * @return
     */
    private VideoCapturer getVideoCapturer() {
        Log.d("@@@", "setCamera 424 行");
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName);
    }

    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (audioSource != null) {
            audioSource.dispose();
        }
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        for (Peer peer : peers.values()) {
            peer.peerConnection.dispose();
        }
        if (audioSource != null) {
            audioSource.dispose();
        }
//        if (videoSource != null) {
//            videoSource.stop();
//        }
        peerConnectionFactory.dispose();
//        if (socket != null) {
//            socket.disconnect();
//            socket.close();
//        }
    }
}
