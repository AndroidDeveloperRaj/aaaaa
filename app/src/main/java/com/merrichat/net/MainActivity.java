package com.merrichat.net;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.android.CIMCacheTools;
import com.merrichat.net.activity.message.cim.android.CIMPushManager;
import com.merrichat.net.activity.setting.AboutHomeSetting;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.FindFragment;
import com.merrichat.net.fragment.MessageFragment;
import com.merrichat.net.fragment.MineFragment;
import com.merrichat.net.fragment.circlefriends.CircleFriendsFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AllocateModel;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.UploadModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.service.BackGroudService;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.SysApp;
import com.merrichat.net.view.DialogWebView;
import com.merrichat.net.view.RegisterRedPakageShareBottomDialog;
import com.merrichat.net.view.lockview.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;

/**
 * app主入口
 */
public class MainActivity extends BaseActivity {
    public final static int activityId = MiscUtil.getActivityId();
    /**
     * 朋友圈
     */
    @BindView(R.id.iv_friend)
    ImageView ivFriend;
    @BindView(R.id.tv_friend)
    TextView tvFriend;
    @BindView(R.id.lay_friend)
    RelativeLayout layFriend;
    @BindView(R.id.ll_friend)
    LinearLayout llFriend;

    /**
     * 消息
     */
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.lay_message)
    RelativeLayout layMessage;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.tv_message_num)
    TextView tvMessageNum;

    /**
     * 发现
     */
    @BindView(R.id.iv_find)
    ImageView ivFind;
    @BindView(R.id.tv_find)
    TextView tvFind;
    @BindView(R.id.lay_find)
    RelativeLayout layFind;
    @BindView(R.id.ll_find)
    LinearLayout llFind;

    /**
     * 我的
     */
    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lay_mine)
    RelativeLayout layMine;
    @BindView(R.id.ll_mine)
    LinearLayout llMine;

    @BindView(R.id.fl_content)
    FrameLayout flContent;
    boolean isShareDialogShow;
    private CircleFriendsFragment circleFriendsFragment;
    private MessageFragment messageFragment;
    private MineFragment mineFragment;
    private FindFragment findFragment;
    //记录Fragment的位置
    private int index = 0;
    private FragmentManager mFragmentManager;//fragment管理者
    private int requestAllocateCount = 0;//聊天分配长连接失败次数
    private long exitTime = 0;
    private AnimationSet animationSet;
    private DialogWebView dialogWebView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:// 朋友圈
                    llFriend.startAnimation(animationSet);
                    break;
                case 1://消息
                    llMessage.startAnimation(animationSet);
                    break;
                case 2://发现
                    llFind.startAnimation(animationSet);
                    break;
                case 3://我的
                    llMine.startAnimation(animationSet);
                    break;
            }
        }
    };
    private String logShareUrl;
    private String logImageUrl;
    private String logTitle;
    private String logContent;
    private String logId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        MobclickAgent.openActivityDurationTrack(false); // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        AppManager.getAppManager().addActivity(this);
        // 友盟调试模式开关[打印日志][上线时关闭] !!!!!上线时候一定要为false
        MobclickAgent.setDebugMode(false);
        initView();
        initRotateAnim();
        //判断是否更新
        queryAppUpgrade();

        //记录当前是否同意移动网络播放视频
        PrefAppStore.setWorkNetStatus(MainActivity.this, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserModel.getUserModel().getIsLogin()) {
            checkPermission();
            if (PrefAppStore.getIsFirstJiangliDialogShowTagValue(cnt)) {
                PrefAppStore.setIsFirstJiangliDialogShowTagValue(cnt, false);
                showRegisteredRedEnvelopesDialog(PrefAppStore.getIsFirstJiangliDialogShowUrlValue(cnt));
            }
            if (!PrefAppStore.getIsAlreadyUploadTxl(cnt)) {
                uploadContacts();
            }
        } else {
            tvMessageNum.setText("");
            tvMessageNum.setVisibility(View.GONE);
        }
    }

    /**
     * 上传手机通讯录
     */
    private void uploadContacts() {
        Gson gson = new Gson();
        ContactsContractListModel contacts = getContacts();
        if (null == contacts) {
        } else {
            if (contacts.memberJson.size() == 0) {

            } else {
                List<ContactsContractModel> list = new ArrayList<>();
                for (int i = 0; i < contacts.memberJson.size(); i++) {
                    if (isRepeat(list, contacts.memberJson.get(i).phone.get(0).phoneNumber)) {

                    } else {
                        list.add(contacts.memberJson.get(i));
                    }
                }
                Logger.e(new Gson().toJson(contacts));
                ApiManager.getApiManager().getService(WebApiService.class).synchroAddressBook(UserModel.getUserModel().getMemberId(), UserModel.getUserModel().getMobile(), UserModel.getUserModel().getUploadFlag(), gson.toJson(list).replace(" ", ""))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscribe<UploadModel>() {
                            @Override
                            public void onNext(UploadModel uploadModel) {
                                PrefAppStore.setIsAlreadyUploadTxl(cnt, true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

    /**
     * 获取手机通讯录
     *
     * @return
     */
    private ContactsContractListModel getContacts() {
        if (!(PackageManager.PERMISSION_GRANTED == cnt.getPackageManager().checkPermission("android.permission.READ_CONTACTS", "com.merrichat.net"))) {
            return null;
        }
        ContactsContractListModel contactsContractListModel = new ContactsContractListModel();
        List<ContactsContractModel> list = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (!number.replace(" ", "").equals(UserModel.getUserModel().getMobile())) {
                ContactsContractModel contractModel = new ContactsContractModel();
                ContactsContractModel.PhoneModel phone = new ContactsContractModel.PhoneModel();
                contractModel.phone = new ArrayList<>();
                phone.phoneNumber = number.replace(" ", "");
                contractModel.phone.add(phone);
                contractModel.name = name.replace(" ", "");
                contractModel.surname = "";
                list.add(contractModel);
            }
        }
        cursor.close();
        contactsContractListModel.memberJson = list;
        return contactsContractListModel;
    }

    /**
     * @param list   手机通讯录
     * @param mobile 手机号
     * @return 是否重复
     */
    private boolean isRepeat(List<ContactsContractModel> list, String mobile) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).phone.get(0).phoneNumber.equals(mobile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CIMPushManager.destory(cnt);
        EventBus.getDefault().unregister(this);
    }

    private void queryAppUpgrade() {
        OkGo.<String>post(Urls.queryAppVersionInfoAndroid)
                .params("appType", "1")
                .params("version", MerriApp.curVersion)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                String downloadUrl = data.optString("downloadUrl");
                                boolean isLatest = data.optBoolean("isLatest");
                                String isUpdate = data.optString("isUpdate");
                                String versionDescription = data.optString("versionDescription");
                                showVersionDialog(downloadUrl, isLatest, isUpdate, versionDescription);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initRotateAnim() {
        animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //3秒完成动画
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setDuration(100);
        //将AlphaAnimation这个已经设置好的动画添加到 AnimationSet中
        animationSet.addAnimation(scaleAnimation);
    }

    /**
     * 更新提示框 更新判断条件为版本号
     */
    protected void showVersionDialog(final String downloadUrl, boolean isLatest, String isUpdate, String versionDescription) {
        if (isLatest) {
            return;
        }
        if (!TextUtils.isEmpty(isUpdate)) {
            final MaterialDialog dialog = new MaterialDialog(cnt);
            dialog.content(versionDescription)//
                    .show();
            if (("1").equals(isUpdate)) {
                dialog.btnNum(1);
                dialog.btnText("立即更新");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {//right btn click listener
                            @Override
                            public void onBtnClick() {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri content_url = Uri.parse(downloadUrl);
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        }
                );
            } else if (("0").equals(isUpdate)) {
                dialog.btnText("稍后再说", "立即更新");
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {//left btn click listener
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {//right btn click listener
                            @Override
                            public void onBtnClick() {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri content_url = Uri.parse(downloadUrl);
                                intent.setData(content_url);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        }
                );
            }

        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {

        Logger.e("onEvent");
        if (myEventBusModel.FRIEND_RELEASE) {
            //updateTabLayoutUI(0);
        }
        if (myEventBusModel.REFRESH_MINE_CIRCLER) {//接收广播 首页切换到朋友圈推荐界面
            //发送广播 选中推荐界面及刷新
            MyEventBusModel mEventBusModel = new MyEventBusModel();
            mEventBusModel.REFRESH_RECOMMENT_FRAGMENT = true;
            EventBus.getDefault().post(mEventBusModel);

            logId = myEventBusModel.id;
            logTitle = myEventBusModel.title;
            logContent = myEventBusModel.content;
            logImageUrl = myEventBusModel.imageUrl;
            if (myEventBusModel.REFRESH_MINE_CIRCLER_SHARE) {
                getPromoQRcodeLog(logId, UserModel.getUserModel().getMemberId());
            }
            setTabSelection(0);
            myEventBusModel.REFRESH_MINE_CIRCLER = false;
            myEventBusModel.REFRESH_MINE_CIRCLER_SHARE = false;

        } else if (myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM) {
            setMessageNum();
        }
        if (myEventBusModel.CLOSE_MYACTIVITY) {
            tvMessageNum.setText("");
        }
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcodeLog(String id, String toMemberId) {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", id)
                .params("atlMemberId", toMemberId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    logShareUrl = data.optJSONObject("data").getString("url");

                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            Intent intent = new Intent(MainActivity.this, ShareToActivity.class);
                                            intent.putExtra("ShareUrl", "" + logShareUrl);
                                            intent.putExtra("shareImage", "" + logImageUrl);
                                            intent.putExtra("activityId", "");
                                            intent.putExtra("title", "" + logTitle);
                                            intent.putExtra("content", "" + logContent);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.push_dialog_bottom_in, 0);
                                        }
                                    }, 500);

//                                    switch (shareTo) {
//                                        case 1://分享到朋友圈
//                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE)) {
//
//                                                ShareWebLog(SHARE_MEDIA.WEIXIN_CIRCLE);
//                                            } else {
//                                                RxToast.showToast("未安装微信客户端...");
//                                            }
//                                            break;
//                                        case 2://分享到微信
//                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
//                                                ShareWebLog(SHARE_MEDIA.WEIXIN);
//                                            } else {
//                                                RxToast.showToast("未安装微信客户端...");
//                                            }
//                                            break;
//                                        case 3://分享到微博
//                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.SINA)) {
//                                                ShareWebLog(SHARE_MEDIA.SINA);
//                                            } else {
//                                                RxToast.showToast("未安装微博客户端...");
//                                            }
//                                            break;
//                                        case 4://分享到QQ
//                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.QQ)) {
//                                                ShareWebLog(SHARE_MEDIA.QQ);
//                                            } else {
//                                                RxToast.showToast("未安装QQ客户端...");
//                                            }
//                                            break;
//                                    }
                                } else {
                                    RxToast.showToast(data.optString("message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 弹出注册红包
     */
    private void showRegisteredRedEnvelopesDialog(String url) {
        Logger.e("showRegisteredRedEnvelopesDialog" + url);
        dialogWebView = new DialogWebView(MainActivity.this, url) {
            @Override
            public void toDialogClose() {
                goShareDialog(PrefAppStore.getIsFirstJiangliDialogShareUrlKey(cnt));
                dialogWebView.dismiss();
                isShareDialogShow = false;
            }

            @Override
            public void toDialogShare(String activityId, String RedEnvelopeId) {
                goShareDialog(PrefAppStore.getIsFirstJiangliDialogShareUrlKey(cnt));
                dialogWebView.dismiss();
                isShareDialogShow = false;
            }
        };
//        dialogWebView.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialogWebView.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialogWebView.show();
    }

    /**
     * 去邀请
     */
    private void goShareDialog(final String url) {
        if (TextUtils.isEmpty(url) || isShareDialogShow) {
            return;
        }
        final UMShareAPI umShareAPI = UMShareAPI.get(cnt);
        final RegisterRedPakageShareBottomDialog dialog = new RegisterRedPakageShareBottomDialog(cnt);
        dialog.showAnim(null);
        dialog.show();
        isShareDialogShow = true;
        dialog.setOnShareClick(new RegisterRedPakageShareBottomDialog.OnShareClick() {
            @Override
            public void onShareClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_wechat_friend_circle:
                        if (umShareAPI.isInstall((Activity) cnt, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                            shareTolatform(SHARE_MEDIA.WEIXIN_CIRCLE, url);
                        } else {
                            RxToast.showToast("未安装微信客户端...");
                        }
                        break;
                    case R.id.ll_wechat_friend:

                        if (umShareAPI.isInstall((Activity) cnt, SHARE_MEDIA.WEIXIN)) {
                            shareTolatform(SHARE_MEDIA.WEIXIN, url);
                        } else {
                            RxToast.showToast("未安装微信客户端...");
                        }
                        break;
                    case R.id.ll_weibo:
                        if (umShareAPI.isInstall((Activity) cnt, SHARE_MEDIA.SINA)) {
                            shareTolatform(SHARE_MEDIA.SINA, url);
                        } else {
                            RxToast.showToast("未安装微博客户端...");
                        }
                        break;
                    case R.id.ll_qq:
                        if (umShareAPI.isInstall((Activity) cnt, SHARE_MEDIA.QQ)) {
                            shareTolatform(SHARE_MEDIA.QQ, url);
                        } else {
                            RxToast.showToast("未安装QQ客户端...");
                        }
                        break;
                    case R.id.tv_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        });

    }

    private void shareTolatform(SHARE_MEDIA share_media, String url) {
        UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
            }

            @Override
            public void onResult(SHARE_MEDIA platform) {
                RxToast.showToast("分享成功");
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                RxToast.showToast("分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                RxToast.showToast("分享取消");
            }
        };
        UMImage thumb = new UMImage(cnt, ConstantsPath.share_to_invite_icon);
        UMWeb web = new UMWeb(url);
        web.setThumb(thumb);
        web.setDescription(cnt.getString(R.string.share_content));
        web.setTitle(cnt.getString(R.string.share_title));
        new ShareAction((Activity) cnt).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        index = PreferencesUtils.getInt(this, AboutHomeSetting.ABOUT_HOME_SETTING);// 我的--设置--首页设置 0 消息 1 动态 2 发现 3 我的
        index = index == -1 ? 0 : index;
        if (!TextUtils.isEmpty(getIntent().getStringExtra("notification_type")) && "chat_message".equals(getIntent().getStringExtra("notification_type"))) {
            setTabSelection(1);
        } else {
            switch (index) {
                case 0://朋友圈
                    setTabSelection(0);
                    break;
                case 1://消息
                    setTabSelection(1);
                    break;
                case 2://发现
                    setTabSelection(2);
                    break;
                case 3://相机
                    if (UserModel.getUserModel().getIsLogin()) {
                        startActivity(new Intent(cnt, MerriCameraFunctionAty.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    setTabSelection(0);
                    break;
            }
        }
        setMessageNum();
    }

    /**
     * 按钮点击
     *
     * @param view
     */
    @OnClick({R.id.lay_friend, R.id.lay_message, R.id.lay_find, R.id.lay_mine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lay_friend:
                handler.sendEmptyMessageDelayed(0, 300);
                setTabSelection(0);
                break;
            case R.id.lay_message:
                handler.sendEmptyMessageDelayed(1, 300);
                setTabSelection(1);
                break;
            case R.id.lay_find:
                handler.sendEmptyMessageDelayed(2, 300);
                setTabSelection(2);
                break;
            case R.id.lay_mine:
                handler.sendEmptyMessageDelayed(3, 300);
                setTabSelection(3);
                break;
        }
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //连接CIM聊天服务器
            allocate();
            videoChat();
            if (!SysApp.isServiceRun(cnt)) {
                cnt.startService(new Intent(cnt, BackGroudService.class));
                LogUtil.d("@@@", "服务开启.....");
            }
            String host = CIMCacheTools.getString(cnt, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(cnt, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            if (!CIMPushManager.isServiceRun(cnt, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                // 连接服务端(即时聊天)
                CIMPushManager.init(cnt, host, port);
            }
        }
    }

    /**
     * 点击tab更新UI
     */
    private void setTabSelection(int position) {
        //index
        this.index = position;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragment(transaction);
        switch (position) {
            case 0://朋友圈
                tvFriend.setTextColor(getResources().getColor(R.color.normal_red));
                ivFriend.setImageResource(R.mipmap.dongtai_icon);

                tvMessage.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMessage.setImageResource(R.mipmap.xiaoxi_icon_weidianji);

                tvFind.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFind.setImageResource(R.mipmap.faxian_icon_weidianji);

                tvMine.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMine.setImageResource(R.mipmap.wode_icon_weidianji);
                if (circleFriendsFragment == null) {
                    circleFriendsFragment = new CircleFriendsFragment();
                    transaction.add(R.id.fl_content, circleFriendsFragment);
                } else {
                    transaction.show(circleFriendsFragment);
                }
                break;
            case 1://消息
                tvFriend.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFriend.setImageResource(R.mipmap.dongtai_icon_weidianji);

                tvMessage.setTextColor(getResources().getColor(R.color.normal_red));
                ivMessage.setImageResource(R.mipmap.xiaoxi_icon);

                tvFind.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFind.setImageResource(R.mipmap.faxian_icon_weidianji);

                tvMine.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMine.setImageResource(R.mipmap.wode_icon_weidianji);
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.fl_content, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                break;
            case 2://发现
                tvFriend.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFriend.setImageResource(R.mipmap.dongtai_icon_weidianji);

                tvMessage.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMessage.setImageResource(R.mipmap.xiaoxi_icon_weidianji);

                tvFind.setTextColor(getResources().getColor(R.color.normal_red));
                ivFind.setImageResource(R.mipmap.faxian_icon);

                tvMine.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMine.setImageResource(R.mipmap.wode_icon_weidianji);
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    transaction.add(R.id.fl_content, findFragment);
                } else {
                    transaction.show(findFragment);
                }
                break;
            case 3://我的
                tvFriend.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFriend.setImageResource(R.mipmap.dongtai_icon_weidianji);

                tvMessage.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivMessage.setImageResource(R.mipmap.xiaoxi_icon_weidianji);

                tvFind.setTextColor(getResources().getColor(R.color.color_5F646E));
                ivFind.setImageResource(R.mipmap.faxian_icon_weidianji);

                tvMine.setTextColor(getResources().getColor(R.color.normal_red));
                ivMine.setImageResource(R.mipmap.wode_icon);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.fl_content, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 视频聊天分配长连接地址(HTTP)
     */
    private void videoChat() {
        ApiManager.apiService(WebApiService.class).IM_VIDEO_CHAT(UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<AllocateModel>() {
                    @Override
                    public void onNext(AllocateModel giftListsMode) {
                        Log.e("@@@", "success-----");
                        if (giftListsMode.success) {
                            PrefAppStore.setSocketAddress(MainActivity.this, new Gson().toJson(giftListsMode));
                            Log.e("@@@", "success-----");
                            Log.e("@@@", "httpPort=====" + giftListsMode.data.httpPort);
                            Log.e("@@@", "socketIp=====" + giftListsMode.data.socketIp);
                            Log.e("@@@", "socketPort=====" + giftListsMode.data.socketPort);
                            if (MerriApp.getSocket() != null) {
                                MerriApp.getSocket().disconnect();
                            }
                            MerriApp.socketIp = giftListsMode.data.socketIp;
                            MerriApp.socketPort = giftListsMode.data.socketPort;

                            String mSocketAddress = "http://" + MerriApp.socketIp + ":" + MerriApp.socketPort + "/";
                            try {
                                MerriApp.setSocket(IO.socket(mSocketAddress));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            MerriApp.getSocket().io().reconnection(true);
                            MerriApp.getSocket().connect();
                            JSONObject jsonObject1 = new JSONObject();
                            try {
                                jsonObject1.put("memberId", UserModel.getUserModel().getMemberId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MerriApp.getSocket().emit("connectAfter", jsonObject1.toString());
                            Log.e("@@@", "------连接之后发射事件");
                            MerriApp.getSocket().on("calledChat", MerriUtils.getApp().calledChat);
                            MerriApp.getSocket().on("callStatus", MerriUtils.getApp().callStatus);
                            MerriApp.getSocket().on("answerCall", MerriUtils.getApp().answerCall);
                            MerriApp.getSocket().on("hangUp", MerriUtils.getApp().hangUp);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }
                });
    }

    /**
     * 获取CIM聊天连接地址和端口
     */
    public void allocate() {

        if (UserModel.getUserModel().getIsLogin() && !MerriApp.isCIMConnectionSatus) {
            OkGo.<String>post(Urls.CIM_ALLOCATE)
                    .params("mid", UserModel.getUserModel().getMemberId())
                    .execute(new StringDialogCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                boolean success = jsonObject.optBoolean("success");
                                if (success) {
                                    JSONObject data = jsonObject.optJSONObject("data");
//                                  String receiver = data.optString("receiver");
                                    //服务器ip
                                    String socketIp = data.optString("socketIp");
                                    //服务器socket端口
                                    String socketPort = data.optString("socketPort");
                                    //服务器CIM请求接口端口
                                    String httpPort = data.optString("httpPort");
                                    if (!TextUtils.isEmpty(httpPort)) {
                                        Constant.CIM_SERVER_HTTP_PORT = httpPort;
                                    }
                                    if (TextUtils.isEmpty(socketIp) || TextUtils.isEmpty(socketPort)) {
                                        requestAllocateCount++;
                                        if (requestAllocateCount < 5) {
                                            allocate();
                                        }
                                    } else {
                                        Constant.CIM_SERVER_HOST = socketIp;
                                        Constant.CIM_SERVER_PORT = Integer.parseInt(socketPort);
                                        //连接CIM聊天服务器
                                        if (!CIMPushManager.isServiceRun(cnt, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                                            // 连接服务端(即时聊天)连接成功后会调用bindAccount方法
                                            CIMPushManager.init(cnt, Constant.CIM_SERVER_HOST, Constant.CIM_SERVER_PORT);
                                        } else {
                                            CIMPushManager.bindAccount(cnt, String.valueOf(UserModel.getUserModel().getMemberId()));
                                        }
                                    }
                                } else {
                                    requestAllocateCount++;
                                    if (requestAllocateCount < 5) {
                                        allocate();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                requestAllocateCount++;
                                if (requestAllocateCount < 5) {
                                    allocate();
                                }
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            requestAllocateCount++;
                            if (requestAllocateCount < 5) {
                                allocate();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        connectionSocket();
    }

    //连接socket
    public void connectionSocket() {
        try {
            if (MerriApp.socket != null) {
                MerriApp.socket.disconnect();
            }
            if (TextUtils.isEmpty(PrefAppStore.getSocketAddress(MainActivity.this))) {
                return;
            }
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(MainActivity.this), VideoChatModel.class);
            String mSocketAddress = "http://" + chatModel.data.socketIp + ":" + chatModel.data.socketPort + "/";
            MerriApp.socket = IO.socket(mSocketAddress);
            MerriApp.socket.io().reconnection(true);
            MerriApp.socket.connect();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("memberId", UserModel.getUserModel().getMemberId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MerriApp.socket.emit("connectAfter", jsonObject1.toString());
            MerriApp.socket.on("calledChat", MerriApp.calledChat);
            MerriApp.socket.on("callStatus", MerriApp.callStatus);
            MerriApp.socket.on("answerCall", MerriApp.answerCall);
            MerriApp.socket.on("hangUp", MerriApp.hangUp);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (UserModel.getUserModel().getIsLogin()) {
                    //连接CIM聊天服务器
                    allocate();
                    videoChat();
                    if (!SysApp.isServiceRun(cnt)) {
                        cnt.startService(new Intent(cnt, BackGroudService.class));
                        LogUtil.d("@@@", "服务开启.....");
                    }
                }
            } else {
                AppManager.getAppManager().AppExit(cnt);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 监听返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                RxToast.showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                //完全销毁CIM，一般用于完全退出程序，调用resume将不能恢复(解决了刚进入应用重复给对方发送消息)
                CIMPushManager.destory(cnt);
                AppManager.getAppManager().AppExit(cnt);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        index = savedInstanceState.getInt("position");
        setTabSelection(index);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 当活动被回收时，存储当前的状态。
     *
     * @param outState 状态数据。
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", index);
    }

    /**
     * 用来隐藏fragment的方法
     *
     * @param fragmentTransaction
     */
    private void hideFragment(FragmentTransaction fragmentTransaction) {
        //如果此fragment不为空的话就隐藏起来
        if (circleFriendsFragment != null) {
            fragmentTransaction.hide(circleFriendsFragment);
        }
        if (messageFragment != null) {
            fragmentTransaction.hide(messageFragment);
        }
        if (findFragment != null) {
            fragmentTransaction.hide(findFragment);
        }
        if (mineFragment != null) {
            fragmentTransaction.hide(mineFragment);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (circleFriendsFragment == null && fragment instanceof CircleFriendsFragment) {
            circleFriendsFragment = (CircleFriendsFragment) fragment;
        } else if (messageFragment == null && fragment instanceof MessageFragment) {
            messageFragment = (MessageFragment) fragment;
        } else if (messageFragment == null && fragment instanceof MessageFragment) {
            messageFragment = (MessageFragment) fragment;
        } else if (findFragment == null && fragment instanceof FindFragment) {
            findFragment = (FindFragment) fragment;
        } else if (mineFragment == null && fragment instanceof MineFragment) {
            mineFragment = (MineFragment) fragment;
        }
    }

    /**
     * 刷新消息未读数量
     */
    public void setMessageNum() {
        if (UserModel.getUserModel().getIsLogin()) {
            QueryBuilder<MessageListModle> queryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
            List<MessageListModle> list = queryBuilder.where(MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).list();
            int messageNum = 0;
            for (int i = 0; i < list.size(); i++) {
                messageNum = messageNum + list.get(i).getCount();
            }

            int notificationNumber = PrefAppStore.getNotificationNumber(cnt);
            int noticeNumNew = PrefAppStore.getNoticeNumNew(cnt);
            int zanPingLunNum = PrefAppStore.getZanPingLunNum(cnt);
            int groupNumNEW = PrefAppStore.getGroupNumNEW(cnt);
            int newFriendNum = PrefAppStore.getNewFriendNum(cnt);

            int allMessageNum = messageNum + notificationNumber + noticeNumNew + zanPingLunNum + groupNumNEW + newFriendNum;
            if (allMessageNum > 0) {
                tvMessageNum.setVisibility(View.VISIBLE);
                if (allMessageNum > 99) {
                    tvMessageNum.setText("99+");
                } else {
                    tvMessageNum.setText(allMessageNum + "");
                }
            } else {
                tvMessageNum.setVisibility(View.GONE);
            }
        } else {
            tvMessageNum.setText("");
            tvMessageNum.setVisibility(View.GONE);
        }

    }

    public static class ContactsContractModel {
        public List<PhoneModel> phone;
        public String name;
        public String surname;

        public static class PhoneModel {
            public String phoneNumber;
        }
    }

    public class ContactsContractListModel {
        List<ContactsContractModel> memberJson;
    }
}
