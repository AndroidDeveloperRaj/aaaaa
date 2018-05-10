package com.merrichat.net.fragment.circlefriends;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.merrifunction.MerriCameraFunctionAty;
import com.merrichat.net.adapter.MyShowPagerAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoReleaseModel;
import com.merrichat.net.ossfile.Config;
import com.merrichat.net.ossfile.ProgressCallback;
import com.merrichat.net.ossfile.PutObjectSamples;
import com.merrichat.net.ossfile.STSGetter;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DialogWebView;
import com.merrichat.net.view.PagerSlidingTabStrip;
import com.merrichat.net.view.PopShare;
import com.merrichat.net.view.RankingShaiXuanPopuwindow;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.merrichat.net.ossfile.Config.endpoint;

/**
 * Created by amssy on 17/11/15.
 * 朋友圈
 */

public class CircleFriendsFragment extends BaseFragment {


    private static final int UPLOAD_SUC = 0x007;
    private static final int UPLOAD_Fail = 0x006;
    private static final int UPLOAD_PROGRESS = 0x005;
    @BindView(R.id.page_tabs)
    PagerSlidingTabStrip pageTabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.iv_refresh_upload)
    ImageView ivRefreshUpload;//重新发布
    @BindView(R.id.iv_dele_upload)
    ImageView ivDeleUpload;//取消发布
    @BindView(R.id.lay_iv)
    LinearLayout layIv;
    @BindView(R.id.pb_upload)
    ProgressBar pbUpload;
    @BindView(R.id.lay_upload)
    LinearLayout layUpload;
    Unbinder unbinder;
    String publicImgUrl = "http://" + Config.bucket + "." + Config.publicImgPoint;
    @BindView(R.id.rel_button)
    RelativeLayout relButton;
    @BindView(R.id.start)
    LinearLayout start;
    //申请 相机、写SD卡、录音的权限
    String[] mutiPermissions;
    //需要请求授权的权限
    ArrayList<String> needRequest = new ArrayList<>();
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setBackgroundAlpha((float) msg.obj);
                    break;
            }
        }
    };
    private View view;
    private List<Fragment> fragmentList;
    private GoodFriendsFragment goodFriendsFragment;
    private NearFragment nearFragment;
    private RecommendFragment rankingFragment;
    private AttentionFragment attentionFragment;
    private RankingShaiXuanPopuwindow popuwindow;
    private MyShowPagerAdapter myShowPagerAdapter;
    private UMShareAPI umShareAPI;
    //分享图片路径和链接
    private String shareImage = "";
    private String shareUrlHead = "http://gzhgr.igomot.net/weixin-shop/xunmei/redpack/red.html?";
    private String registerededenvelopesUrl = "http://igomopub.igomot.net/clip-pub/pop-up/getMoney.html?mid=";
    private String shareUrl = "";
    private VideoReleaseModel videoReleaseModel;
    private String videoUrl;
    private String content;
    private String imageUrl;
    private String cover;
    private String pictureUrls;
    private int videoRelease = 1;
    private int numTuwen = 1;
    private boolean isRelease = true;//true标识发布视频  false表示发布图文
    private boolean isCancelRelease = false;//是否取消发布日志
    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享成功");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享取消");
        }
    };
    private boolean isChange = false;
    private int changeProgress = 0;
    private int progress = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    if (isChange) {
                        changeProgress = progress;
                        progress = changeProgress + msg.arg1;
                        isChange = false;
                    } else {
                        progress = changeProgress + msg.arg1;
                    }
                    if (progress < 100) {
                        layUpload.setVisibility(View.VISIBLE);
                        layIv.setVisibility(View.GONE);
                        tvProgress.setText("正在上传中(" + progress + "%)…");
                        tvProgress.setTextColor(getResources().getColor(R.color.normal_black));
                        pbUpload.setProgress(progress);
                    }
                    break;
                case 0:
                    layUpload.setVisibility(View.VISIBLE);
                    tvProgress.setText("上传失败");
                    tvProgress.setTextColor(getResources().getColor(R.color.normal_red));
                    layIv.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    RxToast.showToast("上传成功");
                    tvProgress.setText("上传成功");
                    tvProgress.setTextColor(getResources().getColor(R.color.normal_black));
                    pbUpload.setProgress(100);
                    try {
                        Thread.sleep(2000);
                        layUpload.setVisibility(View.GONE);
                        //刷新视频列表

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    /**
     * 图文专辑发布
     */

    private String fileSuffix;
    private ArrayList<String> urlsNameList;
    private Gson gson;
    private Handler handlerImg = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean handled = false;
            switch (msg.what) {
                case UPLOAD_SUC:

                    String title = videoReleaseModel.getTitle();
                    String describe = videoReleaseModel.getText();
                    String content = "";
                    String coverJson = "";
                    String musicUrl = "";
                    String pictureUrls = "";
                    ArrayList<PhotoVideoModel> contentList = new ArrayList<>();
                    for (PhotoVideoModel photoVideoModel : videoReleaseModel.getPhotoVideoList()
                            ) {
                        PhotoVideoModel contentModel = new PhotoVideoModel();
                        contentModel.setFlag(photoVideoModel.getFlag());
                        contentModel.setUrl(photoVideoModel.getUrl());
                        contentModel.setText(photoVideoModel.getText());
                        contentModel.setHeight(photoVideoModel.getHeight());
                        contentModel.setWidth(photoVideoModel.getWidth());
                        contentModel.setType(photoVideoModel.getType());
                        contentList.add(contentModel);
                    }
                    for (PhotoVideoModel model : contentList) {
                        String url = model.getUrl();
                        if (!RxDataTool.isNullString(url)) {
                            model.setUrl(publicImgUrl + url.substring(url.lastIndexOf("/") + 1));
                        }
                        if (model.getFlag() == 1) {
                            coverJson = gson.toJson(model);
                        }
                    }
                    content = gson.toJson(contentList);
                    if (urlsNameList.size() > 0) {
                        for (int i = 0; i < urlsNameList.size(); i++) {
                            pictureUrls += "\"" + urlsNameList.get(i) + "\"" + ",";
                        }
                        pictureUrls = "[" + pictureUrls.substring(0, pictureUrls.length() - 1) + "]";
                    }
                    if (!isCancelRelease) {
                        beautyPublish(title, content, describe, coverJson, videoReleaseModel.getClassifystr(), musicUrl, pictureUrls);
                        handled = true;
                    }
                    break;
                case UPLOAD_Fail:
                    RxToast.showToast("上传失败，请重试！");
                    handled = true;
                    break;
                case UPLOAD_PROGRESS:
                    Bundle data = msg.getData();
                    long currentSize = data.getLong("currentSize");
                    long totalSize = data.getLong("totalSize");
                    OSSLog.logDebug("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                    handled = true;
                    break;
            }

            return handled;
        }
    });
    private TranslateAnimation animation;
    private String logId;
    private String logTitle;
    private String logContent;
    private String logImageUrl;
    private int shareTo = 1;
    private String logShareUrl;
    private DialogWebView dialogWebView;

    /**
     * Fragment 的构造函数。
     */
    public CircleFriendsFragment() {
    }

    public static CircleFriendsFragment newInstance() {
        return new CircleFriendsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserModel.getUserModel().getIsLogin()) {
            //relButton.setVisibility(View.GONE);
        } else {
            //relButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        umShareAPI.release();
        //注册广播
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circle_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initView() {
        //注册广播
        EventBus.getDefault().register(this);
        umShareAPI = UMShareAPI.get(getActivity());
        fragmentList = new ArrayList<>();
        fragmentList.add(goodFriendsFragment = new GoodFriendsFragment());
        fragmentList.add(rankingFragment = new RecommendFragment());
        fragmentList.add(attentionFragment = new AttentionFragment());
        fragmentList.add(nearFragment = new NearFragment());

        myShowPagerAdapter = new MyShowPagerAdapter(getChildFragmentManager(), new String[]{"好友", "推荐", "关注", "附近"}, fragmentList);
        viewPager.setAdapter(myShowPagerAdapter);
        pageTabs.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0://好友
                        ivRightMenu.setVisibility(View.GONE);
                        break;
                    case 1://附近
                        ivRightMenu.setVisibility(View.GONE);
                        break;
                    case 2://关注
                        ivRightMenu.setVisibility(View.GONE);
                        break;
                    case 3://附近
                        ivRightMenu.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (UserModel.getUserModel().getIsLogin()) {
            //判断当前网络是否可用
            if (NetUtils.isNetworkAvailable(getActivity())) {
                isShowDialog();
            }
        }

        relButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionCamera();
            }
        });
    }


    /**
     * 权限申请
     */
    private void checkPermissionCamera() {
        mutiPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        needRequest.clear();
        //遍历 过滤已授权的权限，防止重复申请
        for (String permission : mutiPermissions) {
            int check = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                needRequest.add(permission);
            }
        }
        //如果没有全部授权
        if (needRequest.size() > 0) {
            //请求权限，此方法异步执行，会弹出权限请求对话框，让用户授权，并回调 onRequestPermissionsResult 来告知授权结果
            requestPermissions(needRequest.toArray(new String[needRequest.size()]), 0);
        } else {//已经全部授权过
            //做一些你想做的事情，即原来不需要动态授权时做的操作
            startActivity(new Intent(cnt, MerriCameraFunctionAty.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0) {
                //被拒绝的权限列表
                ArrayList<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
                if (deniedPermissions.size() <= 0) {//已全部授权
                    startActivity(new Intent(cnt, MerriCameraFunctionAty.class));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {//没有全部授权
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        //需要引导用户手动开启的权限列表
                        ArrayList<String> needShow = new ArrayList<>();
                        //从没有授权的权限中判断是否需要引导用户
                        for (int i = 0; i < deniedPermissions.size(); i++) {
                            String permission = deniedPermissions.get(i);
                            if (!shouldShowRequestPermissionRationale(permission)) {
                                needShow.add(permission);
                            }
                        }
                        //需要引导用户
                        if (needShow.size() > 0) {
                            //需要弹出自定义对话框，引导用户去应用的设置界面手动开启权限
//                            showMissingPermissionDialog();
                            Toast.makeText(getActivity(), "您需要去应用的设置界面手动开启权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 获取红包弹出链接
     */
    private void isShowDialog() {
        OkGo.<String>get(Urls.ACTIVITY_POPUP)
                .tag(this)
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    String status = data.optJSONObject("data").optString("status");
                                    if (status.equals("1")) {//弹
                                        String url = data.optJSONObject("data").getString("url");
                                        showRedEnvelopeDialog(url);
                                    } else {//不弹

                                    }
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
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 弹出天降红包
     */
    private void showRedEnvelopeDialog(String url) {
        dialogWebView = new DialogWebView(cnt, url) {
            @Override
            public void toDialogClose() {
                dialogWebView.dismiss();
            }

            @Override
            public void toDialogShare(String activityId, String RedEnvelopeId) {
                //把红包发送给好友
                if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
                    shareUrl = shareUrlHead
                            + "st=" + System.currentTimeMillis()
                            + "&mid=" + UserModel.getUserModel().getMemberId()
                            + "&rid=" + String.valueOf(RedEnvelopeId)
                            + "&aid=" + String.valueOf(activityId)
                            + "&source=0";
                    ShareWeb(SHARE_MEDIA.WEIXIN);
                } else {
                    RxToast.showToast("未安装微信客户端...");
                }

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


    @OnClick({R.id.iv_right_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_right_menu://榜单筛选
//                if (viewPager.getCurrentItem() == 1) {
//                    startActivity(new Intent(getActivity(), SelectSexDialogAty.class));
//                }
                //startActivity(new Intent(getActivity(), MarketActivity.class));
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                popuwindow = new RankingShaiXuanPopuwindow(cnt, 1);
                // 设置popWindow的显示和消失动画
                popuwindow.setAnimationStyle(R.style.AnimPopShareDialog);
                // 设置点击popupwindow外屏幕其它地方消失
                popuwindow.setOutsideTouchable(true);

                popuwindow.showAtLocation(start, Gravity.BOTTOM, 0, 0);

                popuwindow.setOnPopuClick(new PopShare.OnPopuClick() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.lay_sex_all:
                                MyEventBusModel myEventBusModel1 = new MyEventBusModel();
                                myEventBusModel1.CHOOSE_SEX1 = true;
                                EventBus.getDefault().post(myEventBusModel1);

                                popuwindow.dismiss();
                                break;
                            case R.id.lay_sex_male:
                                MyEventBusModel myEventBusModel2 = new MyEventBusModel();
                                myEventBusModel2.CHOOSE_SEX2 = true;
                                EventBus.getDefault().post(myEventBusModel2);
                                popuwindow.dismiss();
                                break;
                            case R.id.lay_sex_female:
                                MyEventBusModel myEventBusModel3 = new MyEventBusModel();
                                myEventBusModel3.CHOOSE_SEX3 = true;
                                EventBus.getDefault().post(myEventBusModel3);
                                popuwindow.dismiss();
                                break;
                        }
                    }
                });

                final float[] alpha = {1f};
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (alpha[0] > 0.3f) {
                            try {
                                //4是根据弹出动画时间和减少的透明度计算
                                Thread.sleep(6);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            //每次减少0.01，精度越高，变暗的效果越流畅
                            alpha[0] -= 0.01f;
                            msg.obj = alpha[0];
                            mHandler.sendMessage(msg);
                        }
                    }

                }).start();

                popuwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // popupWindow隐藏时恢复屏幕正常透明度
                        setBackgroundAlpha(1.0f);
                    }
                });
                break;
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 分享回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(getActivity(), shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        web.setDescription("MerriChat天降红包,领取红包及时到账，登录MerriChat App即可使用");
        web.setTitle("MerriChat给你一个大红包");
        new ShareAction(getActivity()).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.FRIEND_RELEASE) {//发布视频广播
            layUpload.setVisibility(View.VISIBLE);
            videoReleaseModel = myEventBusModel.videoReleaseModel;
            isCancelRelease = false;//不取消发布
            videoRelease = 0;
            String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
            setVideoURL(videoName);
        }
        //选中推荐
        if (myEventBusModel.REFRESH_RECOMMENT_FRAGMENT) {
            if (viewPager != null) {
                viewPager.setCurrentItem(1);
            }
        }
        //视频上传成功 跳转到这 执行分享操作
//        if (myEventBusModel.REFRESH_MINE_CIRCLER) {
////            logId = myEventBusModel.id;
////            logTitle = myEventBusModel.title;
////            logContent = myEventBusModel.content;
////            logImageUrl = myEventBusModel.imageUrl;
////            shareTo = myEventBusModel.shareTo;
////            if (myEventBusModel.REFRESH_MINE_CIRCLER_SHARE) {
////                getPromoQRcodeLog(logId, UserModel.getUserModel().getMemberId());
////            }
//        }
        /*if (myEventBusModel.FRIEND_RELEASE_TUWEN) {//发布图文
            videoReleaseModel = myEventBusModel.videoReleaseModel;
            gson = new Gson();
            urlsNameList = new ArrayList<>();
            isCancelRelease = false;//不取消发布
            videoRelease = 0;
            numTuwen = videoReleaseModel.getUrls().size() + 1;
            ossUpload(videoReleaseModel.getUrls());
        }*/
    }

    private boolean checkNotNull(Object obj) {
        if (obj != null) {
            return true;
        }
        return false;
    }

    /**
     * 上传视频到阿里云
     */
    private void setVideoURL(final String videoName) {
        PutObjectSamples putObjectSamples = new PutObjectSamples(getActivity().getApplicationContext(), Config.bucket, videoName, videoReleaseModel.getVideoPath());
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    getUrl(videoName);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    videoRelease = 1;
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    //android.util.Log.d("LogTest", "进度：" + (int) ((currentSize * 100) / totalSize));
                    int arg1 = (int) ((currentSize * 100) / totalSize);
                    if (arg1 < 99) {
                        Message message = new Message();
                        message.what = 1;
                        message.arg1 = arg1;
                        handler.sendMessage(message);
                    }
                }
            });
        }
    }

    private void getUrl(final String videoName) {
        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        credentialProvider = new STSGetter(Config.STSSERVER);

        final OSS oss = new OSSClient(getActivity().getApplicationContext(), endpoint, credentialProvider);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    videoUrl = oss.presignConstrainedObjectURL(Config.bucket, videoName, 30 * 60);
                    videoUrl = "http://okdi.oss-cn-beijing.aliyuncs.com/" + videoName;
                    JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
                    JSONObject jsonObj = new JSONObject();//pet对象，json形式
                    try {
                        jsonObj.put("url", videoUrl);//视频链接
                        jsonObj.put("type", 1);//1是为视频
                        jsonObj.put("text", videoReleaseModel.getText());
                        jsonObj.put("flag", 1);//1不是封面
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 把每个数据当作一对象添加到数组里
                    jsonarray.put(jsonObj);
                    content = jsonarray.toString();

                    //删除本地合成的视频
                    FileUtils.delFile(videoReleaseModel.getVideoPath());
                    if (!isCancelRelease) {
                        String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                        setImageURL(imageName);//上传图片
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 上传图片到阿里云
     *
     * @param imageName
     */
    private void setImageURL(final String imageName) {
        PutObjectSamples putObjectSamples = new PutObjectSamples(getActivity().getApplicationContext(), Config.bucket, imageName, videoReleaseModel.getVideoCover());
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    getImageUrl(imageName);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    videoRelease = 2;
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = 99;
                    handler.sendMessage(message);
                }
            });
        }
    }

    private void getImageUrl(final String imageName) {
        OSSCredentialProvider credentialProvider;
        //使用自己的获取STSToken的类
        credentialProvider = new STSGetter(Config.STSSERVER);

        final OSS oss = new OSSClient(getActivity().getApplicationContext(), endpoint, credentialProvider);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    imageUrl = oss.presignConstrainedObjectURL(Config.bucket, imageName, 30 * 60);
                    imageUrl = "http://okdi.oss-cn-beijing.aliyuncs.com/" + imageName;
                    JSONObject jsonObj = new JSONObject();//pet对象，json形式
                    try {
                        jsonObj.put("url", imageUrl);//视频链接
                        jsonObj.put("type", 0);//1是为视频 0图片
                        jsonObj.put("text", videoReleaseModel.getText());
                        jsonObj.put("flag", 1);//1不是封面
                        jsonObj.put("width", videoReleaseModel.getWidth());
                        jsonObj.put("height", videoReleaseModel.getHeight());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cover = jsonObj.toString();
                    pictureUrls = "[\"" + imageUrl + "\"]";
                    //删除本地封面图
                    FileUtils.delFile(videoReleaseModel.getCover());
                    if (!isCancelRelease) {
                        videoRelease();
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发布视频
     */
    private void videoRelease() {
        OkGo.<String>get(Urls.BEAUTY_PULISH)
                .tag(this)
                .params("title", videoReleaseModel.getTitle())
                .params("content", content)
                .params("phone", videoReleaseModel.getPhone())
                .params("cover", cover)
                .params("classifystr", videoReleaseModel.getClassifystr())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("gender", videoReleaseModel.getGender())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("address", videoReleaseModel.getAddress())
                .params("memberImage", UserModel.getUserModel().getImgUrl())
                .params("musicUrl", videoReleaseModel.getMusicUrl())
                .params("videoUrl", videoUrl)
                .params("longitude", videoReleaseModel.getLongitude())
                .params("latitude", videoReleaseModel.getLatitude())
                .params("jurisdiction", videoReleaseModel.getJurisdiction())
                .params("flag", videoReleaseModel.getFlag())
                .params("isBlack", videoReleaseModel.getIsBlack())
                .params("isDelete", videoReleaseModel.getIsDelete())
                .params("pictureUrls", pictureUrls)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    if (data.optJSONObject("data").optBoolean("result")) {
                                        tvProgress.setText("正在上传中(" + 100 + "%)…");

                                        String id = data.optJSONObject("data").optString("id");
                                        handler.sendEmptyMessage(2);

                                        getPromoQRcode(id, UserModel.getUserModel().getMemberId());

                                    } else {
                                        RxToast.showToast(data.optJSONObject("data").optString("msg"));
                                    }
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
                        videoRelease = 3;
                        handler.sendEmptyMessage(0);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcode(String id, String toMemberId) {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", id)
                .params("atlMemberId", toMemberId)
                .execute(new StringDialogCallback(getContext()) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    shareUrl = data.optJSONObject("data").getString("url");
                                    switch (videoReleaseModel.getShareTo()) {
                                        case 1://分享到朋友圈
                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN_CIRCLE)) {
                                                ShareWeb(SHARE_MEDIA.WEIXIN);
                                            } else {
                                                RxToast.showToast("未安装微信客户端...");
                                            }
                                            break;
                                        case 2://分享到微信
                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {

                                                ShareWeb(SHARE_MEDIA.WEIXIN);
                                            } else {
                                                RxToast.showToast("未安装微信客户端...");
                                            }
                                            break;
                                        case 3://分享到微博
                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.SINA)) {

                                                ShareWeb(SHARE_MEDIA.WEIXIN);
                                            } else {
                                                RxToast.showToast("未安装微信客户端...");
                                            }
                                            break;
                                        case 4://分享到QQ
                                            if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.QQ)) {

                                                ShareWeb(SHARE_MEDIA.WEIXIN);
                                            } else {
                                                RxToast.showToast("未安装微信客户端...");
                                            }
                                            break;
                                    }
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


    @OnClick({R.id.iv_refresh_upload, R.id.iv_dele_upload})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh_upload://重新上传
                switch (videoRelease) {
                    case 1://上传视频失败
                        progress = 0;
                        changeProgress = 0;
                        tvProgress.setText("0");
                        pbUpload.setProgress(0);
                        String videoName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".mp4";
                        setVideoURL(videoName);
                        break;
                    case 2://上传图片失败
                        String imageName = "merrichat_image_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
                        setImageURL(imageName);//上传图片
                        break;
                    case 3://发布日志失败
                        videoRelease();
                        break;
                    case 4://发布图文上传图片到阿里云失败
                        //重新上传到阿里云
                        ossUpload(videoReleaseModel.getUrls());
                        break;
                    case 5://发布图文到服务器失败
                        //重新发布到服务器
                        handlerImg.sendEmptyMessage(UPLOAD_SUC);
                        break;
                }
                break;
            case R.id.iv_dele_upload://删除（取消发布）
                isCancelRelease = true;
                if (isRelease) {//发布视频
                    //删除本地合成的视频
                    FileUtils.delFile(videoReleaseModel.getVideoPath());
                    //删除本地封面图
                    FileUtils.delFile(videoReleaseModel.getCover());
                } else {//发布图文

                }
                layUpload.setVisibility(View.GONE);
                tvProgress.setText("0");
                pbUpload.setProgress(0);
                changeProgress = 0;
                break;
        }
    }

    /**
     * 阿里云OSS上传（默认是异步多文件上传）
     *
     * @param urls 上传图文
     */
    private void ossUpload(final List<String> urls) {
        isChange = true;
        if (urls.size() <= 0) {
            // 文件全部上传完毕，这里编写上传结束的逻辑，如果要在主线程操作，最好用Handler或runOnUiThead做对应逻辑
            return;// 这个return必须有，否则下面报越界异常，原因自己思考下哈
        }
        final String url = urls.get(0);
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            // url为空就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(urls);
            return;
        }

        File file = new File(url);
        if (null == file || !file.exists()) {
            urls.remove(0);
            // 文件为空或不存在就没必要上传了，这里做的是跳过它继续上传的逻辑。
            ossUpload(urls);
            return;
        }
        // 文件后缀
        fileSuffix = "";
        if (file.isFile()) {
            // 获取文件后缀名
            fileSuffix = file.getName();
            Logger.e("fileSuffix：：：：", fileSuffix);
        }
        // 文件标识符objectKey
        final String objectKey = "alioss_" + System.currentTimeMillis() + fileSuffix;
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectSamples putObjectSamples = new PutObjectSamples(getActivity().getApplicationContext(), Config.bucket, fileSuffix, url);
        if (checkNotNull(putObjectSamples)) {
            putObjectSamples.asyncPutObjectFromLocalFile(new ProgressCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    urls.remove(0);
                    Logger.e("onSuccess：：：：", "onSuccess::上传成功！");
//                    urlsNameList.add(publicImgUrl + fileSuffix);
                    if (urls.size() == 0) {
                        handlerImg.sendEmptyMessage(UPLOAD_SUC);
                    }
                    if (!isCancelRelease) {
                        ossUpload(urls);// 递归同步效果
                    }
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    Logger.e("onFailure：：：：", "onFailure::上传失败！");
                    handlerImg.sendEmptyMessage(UPLOAD_Fail);
                    videoRelease = 4;
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    int arg1 = (int) (((currentSize * 100) / totalSize) / numTuwen);
                    Message message = new Message();
                    message.what = 1;
                    message.arg1 = arg1;
                    handler.sendMessage(message);
                }
            });
        }
    }

    /**
     * 发布图文专辑
     */
    private void beautyPublish(String title, String content, String describe, String coverJson, String classifystr,
                               String musicUrl, String pictureUrls) {
        OkGo.<String>get(Urls.BEAUTY_PULISH)
                .tag(this)
                .params("title", title)
                .params("content", content)
                .params("describe", describe)
                .params("phone", UserModel.getUserModel().getMobile())
                .params("cover", coverJson)
                .params("classifystr", classifystr)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("gender", UserModel.getUserModel().getGender())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("address", videoReleaseModel.getAddress())
                .params("memberImage", UserModel.getUserModel().getImgUrl())
                .params("musicUrl", musicUrl)
                .params("videoUrl", "")
                .params("longitude", videoReleaseModel.getLongitude())
                .params("latitude", videoReleaseModel.getLatitude())
                .params("jurisdiction", videoReleaseModel.getJurisdiction())
                .params("flag", 1)//日志标识 1图文专辑 2视频 3照片 4录像
                .params("isBlack", -1)//是否举报 -1:未举报 ,0:举报待审核 ,1:举报通过 ,2:举报失败
                .params("isDelete", 0)//是否删除 0否 1是
                .params("pictureUrls", pictureUrls)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                    if (jsonObjectEx.optJSONObject("data").optBoolean("result")) {
                                        tvProgress.setText("正在上传中(" + 100 + "%)…");
                                        long id = jsonObjectEx.optJSONObject("data").optLong("id");
                                        handler.sendEmptyMessage(2);
                                    } else {
                                        RxToast.showToast(jsonObjectEx.optJSONObject("data").optString("msg"));
                                    }
                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        videoRelease = 5;
                        handler.sendEmptyMessage(0);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
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
                                            Intent intent = new Intent(getActivity(), ShareToActivity.class);
                                            intent.putExtra("ShareUrl", "" + logShareUrl);
                                            intent.putExtra("shareImage", "" + logImageUrl);
                                            intent.putExtra("activityId", "");
                                            intent.putExtra("title", "" + logTitle);
                                            intent.putExtra("content", "" + logContent);
                                            startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.push_dialog_bottom_in, 0);
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

    private void ShareWebLog(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(getActivity(), logImageUrl);
        UMWeb web = new UMWeb(logShareUrl);
        web.setThumb(thumb);
        if (TextUtils.isEmpty(logContent)) {
            web.setDescription(getString(R.string.share_content_log));
        } else {
            web.setDescription(logContent);
        }
        if (!TextUtils.isEmpty(logTitle)) {
            web.setTitle(logTitle);
        } else {
            web.setTitle(getString(R.string.share_title_log));
        }
        new ShareAction(getActivity()).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }
}
