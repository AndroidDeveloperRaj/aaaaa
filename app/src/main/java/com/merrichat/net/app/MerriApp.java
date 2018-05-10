package com.merrichat.net.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.merrichat.net.R;
import com.merrichat.net.activity.meiyu.VideoChatActivity;
import com.merrichat.net.activity.meiyu.VoiceChatActivity;
import com.merrichat.net.activity.message.MessageVideoCallAty;
import com.merrichat.net.activity.message.MessageVoiceCallAty;
import com.merrichat.net.callback.MyHttpLoggingInterceptor;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.utils.LocationService;
import com.merrichat.net.utils.LolipopBitmapMemoryCacheSupplier;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_ANSWER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_INFO_KEY;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_TYPE_KEY;

/**
 * Created by amssy on 17/10/25.
 * 程序入口
 */

public class MerriApp extends MultiDexApplication {
    /**
     * 控制rul环境切换
     * 1：生产环境
     * 2：测试环境
     */
    public static final int URL_ENVIRONMENT_VARIABLE = 2 ;
    /**
     * App 接口请求Debug 关键字
     */
    public static final String TAG_HTTP = "life_http";
    /**
     * 是否需要打印bug
     */
    public static final boolean isDebug = true;


    /**
     * socketIp
     */
    public static String socketIp = "";
    /**
     * socket端口号
     */
    public static String socketPort = "";
    /**
     * CIM连接状态
     */
    public static boolean isCIMConnectionSatus;
    /**
     * 经纬度
     */
    public static String mLatitude;
    public static String mLongitude;
    /**
     * 对方经纬度
     */
    public static String toLongitude;
    public static String toLatitude;
    public static String address;
    public static Context mContext;
    public static String alipay_app_id = "2017111509942745";
    public static String alipay_pid = "2088912503145187";
    /**
     * 视频聊天socket
     */
    public static Socket socket;
    /**
     * 当前版本名
     */
    public static String curVersion;
    public static String WX_CODE = "";//微信CODE
    public static boolean sendReq = false; //是否调了微信登陆
    public static boolean isChatting = false;   //判断是否正在聊天
    /**
     * 对外返回一个全局Context
     *
     * @return
     */
    public static IWXAPI iwxapi;

    /**
     * 帖子评论  0代表图文全部评论列表  1表示视频评论列表
     */
    public static int COMMENT_LOG = 0;
    /**
     * 监听到呼叫事件、然后做相应的操作
     * <p>
     * callSource 呼叫来源  0为消息、1为美遇
     * <p>
     * callType---消息
     * 0 为语音
     * 1 为视频
     */
    public static Emitter.Listener calledChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String callInfo = args[0].toString();
            Log.e("@@@", "监听到呼叫的监听事件...callInfo====" + callInfo);
            String callSource = JSON.parseObject(callInfo).getString("callSource");
            if ("0".equals(callSource)) {//消息--视频聊天
                String callType = JSON.parseObject(callInfo).getString("callType");
                Class atyClass = null;
                if (callType.equals("0")) {
                    atyClass = MessageVoiceCallAty.class;
                } else if (callType.equals("1")) {
                    atyClass = MessageVideoCallAty.class;
                } else {
                    atyClass = MessageVideoCallAty.class;
                }
                getContext().startActivity(new Intent(getContext(), atyClass)
                        .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_ANSWER_TYPE)
                        .putExtra(CALL_VIDEO_INFO_KEY, callInfo)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if ("1".equals(callSource)) {//美遇匹配，收到呼叫事件
                String fromMemberId = JSON.parseObject(callInfo).getString("fromMemberId");

                String toLongitude = JSON.parseObject(callInfo).getString("fromLongitude");
                String toLatitude = JSON.parseObject(callInfo).getString("fromLatitude");

                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("isAgree", "1");//1-同意  0--拒绝
                    jsonObject1.put("fromMemberId", UserModel.getUserModel().getMemberId());
                    jsonObject1.put("toMemberId", fromMemberId);
                    jsonObject1.put("callSource", "1");// 0消息 1视频
                    jsonObject1.put("callType", "1");//0 语音  1视频
                    jsonObject1.put("fromHeadImgUrl", UserModel.getUserModel().getImgUrl());
                    jsonObject1.put("fromMemberName", UserModel.getUserModel().getRealname());
                    jsonObject1.put("fromMobile", UserModel.getUserModel().getMobile());
                    jsonObject1.put("fromAccountId", UserModel.getUserModel().getAccountId());

                    jsonObject1.put("fromLongitude", mLongitude);
                    jsonObject1.put("fromLatitude", mLatitude);

                    jsonObject1.put("toLongitude", toLongitude);
                    jsonObject1.put("toLatitude", toLatitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MerriUtils.getApp().socket.emit("agreeCall", jsonObject1.toString());
                MyEventBusModel eventBusModel = new MyEventBusModel();
                eventBusModel.REFRESH_MEET_NICE_ANSWER = true;
                eventBusModel.NICE_MEET_ANSWER_CALLINFO = callInfo;
                EventBus.getDefault().post(eventBusModel);
            }
        }
    };
    /**
     * 监听呼叫事件的呼叫状态/比如 对方是否在线、是否正在通话
     * <p>
     * 通话状态，参数：status 0:可以通话 除了0都是不能通话
     */
    public static Emitter.Listener callStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String callStatus = args[0].toString();
            Log.e("@@@", "呼叫状态----收到了--callStatus===" + callStatus);
            String status = JSON.parseObject(callStatus).getString("status");
            String callSource = JSON.parseObject(callStatus).getString("callSource");
            if ("0".equals(callSource)) {//消息视频聊天
                if (!"0".equals(status)) {
                    Log.e("@@@", "不能呼叫，可以挂断了...");
                }
            } else if ("1".equals(callSource)) {//美遇呼叫、得到状态
                if ("0".equals(status)) {
                    Log.e("@@@", "美遇匹配成功....");
                } else {
                    Log.e("@@@", "美遇匹配呼叫失败，重新搜索匹配");
                }
            }
        }
    };
    /**
     * 美遇搜索结果 如果监听到这个事件，证明匹配到了
     */
    public static Emitter.Listener searchResult = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("@@@", "监听到搜索结果、匹配成功。。。。");
            String searchResult = args[0].toString();
            Log.e("@@@", "监听到搜索结果、匹配成功。。。。" + searchResult);
            String toMemberId = JSON.parseObject(searchResult).getString("toMemberId");
            JSONObject callJsonObject = new JSONObject();
            try {
                callJsonObject.put("fromMemberId", UserModel.getUserModel().getMemberId());
                callJsonObject.put("toMemberId", toMemberId);
                callJsonObject.put("fromMemberName", UserModel.getUserModel().getRealname());
                callJsonObject.put("fromHeadImgUrl", UserModel.getUserModel().getImgUrl());
                callJsonObject.put("callSource", "1");//呼叫来源： 0：消息   1美遇
                callJsonObject.put("callType", "1");// 0语音 1视频
                callJsonObject.put("fromMobile", UserModel.getUserModel().getMobile());
                callJsonObject.put("fromAccountId", UserModel.getUserModel().getAccountId());
                callJsonObject.put("fromLatitude", mLatitude);
                callJsonObject.put("fromLongitude", mLongitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MerriUtils.getApp().socket.emit("callPeer", callJsonObject.toString());
            Log.e("@@@", "匹配成功，发送呼叫事件....");
        }
    };
    /**
     * 监听呼叫事件的 应答状态 / 比如：接听、拒绝
     */
    public static Emitter.Listener answerCall = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String answerInfo = args[0].toString();
            Log.e("@@@", "监听到应答事件的状态......answerInfo====" + answerInfo);
            String callSource = JSON.parseObject(answerInfo).getString("callSource");
            if ("0".equals(callSource)) {
                String callType = JSON.parseObject(answerInfo).getString("callType");
                if (callType.equals("0")) {
                    try {
                        JSONObject jsonObject = new JSONObject(answerInfo);
                        String isAgree = jsonObject.optString("isAgree");
                        if ("1".equals(isAgree)) {
                            MyEventBusModel eventBusModel = new MyEventBusModel();
                            eventBusModel.REFRESH_VOICE_CHAT_ANSWER = true;
                            eventBusModel.VOICE_CHAT_ANSWER_CALLINFO = answerInfo;
                            EventBus.getDefault().post(eventBusModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(answerInfo);
                        String isAgree = jsonObject.optString("isAgree");
                        if ("1".equals(isAgree)) {
                            MyEventBusModel eventBusModel = new MyEventBusModel();
                            eventBusModel.REFRESH_VIDEO_CHAT_CALL = true;
                            eventBusModel.VIDEO_CHAT_CALL_CALLINFO = answerInfo;
                            EventBus.getDefault().post(eventBusModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if ("1".equals(callSource)) {//美遇
                try {
                    JSONObject jsonObject = new JSONObject(answerInfo);
                    String isAgree = jsonObject.optString("isAgree");
                    if ("1".equals(isAgree)) {
                        MyEventBusModel eventBusModel = new MyEventBusModel();
                        eventBusModel.REFRESH_MEET_NICE_CALL = true;
                        eventBusModel.NICE_MEET_CALL_ANSWERINFO = answerInfo;
                        EventBus.getDefault().post(eventBusModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * 监听呼叫事件的 应答状态 / 比如：接听、拒绝
     * 消息---视频聊天专用
     */
    public static Emitter.Listener hangUp = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String callInfo = args[0].toString();
            String callSource = JSON.parseObject(callInfo).getString("callSource");
            String fromMemberId = JSON.parseObject(callInfo).getString("fromMemberId");

            String callType = JSON.parseObject(callInfo).getString("callType");
            String isAgree = JSON.parseObject(callInfo).getString("isAgree");

            Log.e("@@@", "监听到挂断的监听事件。。。。callType:" + callType + "  isAgree:" + isAgree + "    " + callInfo);
            if (callType.equals("0")) {
                if (AppManager.getAppManager().hasActivity(VoiceChatActivity.class)) {
                    AppManager.getAppManager().finishActivity(VoiceChatActivity.class);
                }
                if (AppManager.getAppManager().hasActivity(MessageVoiceCallAty.class)) {
                    AppManager.getAppManager().finishActivity(MessageVoiceCallAty.class);
                }
            } else if (callType.equals("1")) {
                if (AppManager.getAppManager().hasActivity(VideoChatActivity.class)) {
                    android.os.Process.killProcess(android.os.Process.myPid());
//                    AppManager.getAppManager().finishActivity(VideoChatActivity.class);
                }
                if (AppManager.getAppManager().hasActivity(MessageVideoCallAty.class)) {
                    AppManager.getAppManager().finishActivity(MessageVideoCallAty.class);

                }
            }

        }
    };

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, R.color.base_888888);//全局设置主题颜色
                layout.setHeaderHeight(70);
                layout.setFooterHeight(70);
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                layout.setPrimaryColorsId(R.color.white, R.color.base_888888);//全局设置主题颜色
                layout.setHeaderHeight(70);
                layout.setFooterHeight(70);
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    /**
     * 定位
     */
    public LocationService locationService;
    /**
     * 网络超时时长
     */
    private int DEFAULT_MILLISECONDS = 15000;
    //定位回调，根据需求获取所需要的参数，下面是所有参数
    private BDLocationListener merriLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mLatitude = String.valueOf(location.getLatitude());
                mLongitude = String.valueOf(location.getLongitude());
                address = location.getCity() + location.getDistrict() + location.getStreet();
                if (locationService != null) {
                    locationService.stop();
                }
            }
        }
    };

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        MerriApp.socket = socket;
    }

    public static Context getContext() {
        return mContext;
    }

    public final static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        if (null == mContext) {
            mContext = this;
        }
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //友盟分享配置
        PlatformConfig.setWeixin(mContext.getString(R.string.weixin_app_id), mContext.getString(R.string.weixin_app_secret));
        PlatformConfig.setQQZone(mContext.getString(R.string.qq_app_id), mContext.getString(R.string.qq_app_key));
        PlatformConfig.setSinaWeibo(mContext.getString(R.string.sina_app_id), mContext.getString(R.string.sina_app_secret), "http://sns.whalecloud.com");

        MerriUtils.init(this);
        initOkGo();
        RxTool.init(this);
        //Fresco
        frescoCreate();

        iwxapi = WXAPIFactory.createWXAPI(mContext, mContext.getResources().getString(R.string.weixin_app_id), true);
        iwxapi.registerApp(mContext.getResources().getString(R.string.weixin_app_id));
        /**
         * 友盟注册
         */
        Config.DEBUG = false;

        UMShareAPI.get(this);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        try {
            curVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(merriLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        new Thread(new Runnable() {
            public void run() {
                locationService.start();
            }
        }).start();
        GreenDaoManager.getInstance();
    }

    private void initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        HttpParams params = new HttpParams();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        MyHttpLoggingInterceptor loggingInterceptor = new MyHttpLoggingInterceptor(TAG_HTTP, isDebug);
        loggingInterceptor.setPrintLevel(MyHttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.SEVERE);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
//        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//        builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);
    }

    /**
     * compile 'com.android.support:multidex:1.0.1'
     * 如果你的工程中已经含有Application类,那么让它继承android.support.multidex.MultiDexApplication类,
     * 如果你的Application已经继承了其他类并且不想做改动，那么还有另外一种使用方式,覆写attachBaseContext()方法:
     * <p>
     * http://blog.csdn.net/yanzhenjie1003/article/details/51818269
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void frescoCreate() {
        //这里是添加Fresco的日志
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());

        //当内存紧张时采取的措施
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                Log.e("Fresco", String.format("onCreate suggestedTrimRatio : %d", suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    //清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                    Fresco.getImagePipeline().clearCaches();
                }
            }
        });

        //小图片的磁盘配置,用来储存用户头像之类的小图
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(this.getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(getString(R.string.app_name))//文件夹名
                .setMaxCacheSize(20 * ByteConstants.MB)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(5 * ByteConstants.MB)//缓存的最大大小,当设备极低磁盘空间
                .build();

        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setRequestListeners(requestListeners)
                .setBitmapMemoryCacheParamsSupplier(new LolipopBitmapMemoryCacheSupplier((ActivityManager) getSystemService(ACTIVITY_SERVICE)))
                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();

        Fresco.initialize(this, imagePipelineConfig);
    }
}
