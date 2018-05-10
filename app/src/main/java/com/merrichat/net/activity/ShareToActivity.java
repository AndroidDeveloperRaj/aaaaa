package com.merrichat.net.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.activity.picture.ReleaseGraphicAlbumAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxFileTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.wxapi.WXEntryActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.utils.RxTools.RxActivityTool.activityStack;

/**
 * 朋友圈分享
 * Created by AMSSY1 on 2017/7/4.
 */

public class ShareToActivity extends Activity {
    public final static int mActivityId = MiscUtil.getActivityId();
    @BindView(R.id.rl_all)
    RelativeLayout rlAll;
    @BindView(R.id.iv_share_pengyouquan)
    ImageView ivSharePengyouquan;
    @BindView(R.id.iv_share_weixin)
    ImageView ivShareWeixin;
    @BindView(R.id.iv_share_weibo)
    ImageView ivShareWeibo;
    @BindView(R.id.iv_share_qq)
    ImageView ivShareQq;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    UMShareAPI umShareAPI = null;
    private Context mContext;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台
    private String shareUrl;
    private String shareImage = "http://okdi.oss-cn-beijing.aliyuncs.com/merrichat_image_logo.jpg";
    private boolean isBack = false;
    private String title = "";
    private String cotent = "";
    private String activityId = "";
    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            //分享开始的回调
            isBack = true;
            //finish();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享成功");
            //视频详情页分享成功
            if (activityId != null && activityId.equals(CircleVideoActivity.activityId + "")) {
                CircleVideoActivity.isShareSuc = true;
            }
            if (activityId != null && activityId.equals(TuWenAlbumAty.activityId + "")) {
                TuWenAlbumAty.isShareSuc = true;
            }

            toMainActivity();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享失败");
            toMainActivity();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享取消");
            toMainActivity();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        RxActivityTool.addActivity(this);
        mContext = this;
        initView();
        initGetItent();
    }

    private void initGetItent() {
        Intent intent = getIntent();
        if (intent != null) {
            shareUrl = intent.getStringExtra("ShareUrl");
            title = intent.getStringExtra("title");
            shareImage = intent.getStringExtra("shareImage");
            cotent = intent.getStringExtra("content");
            activityId = intent.getStringExtra("activityId");
        }
    }

    private void initView() {
        //StringUtil.setRelHeight(rlAll, this, 0.6f);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        umShareAPI = UMShareAPI.get(this);
    }

    @OnClick({R.id.iv_share_pengyouquan, R.id.iv_share_weixin, R.id.iv_share_weibo, R.id.iv_share_qq
            , R.id.tv_cancel})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel://取消
                toMainActivity();
                break;
            case R.id.iv_share_pengyouquan://朋友圈
                if (umShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                    if (activityId != null && activityId.equals(TuWenAlbumAty.activityId + "")) {
                        WXEntryActivity.isTrueShareLog = true;
                    }else if (activityId != null && activityId.equals(CircleVideoActivity.activityId + "")){
                        WXEntryActivity.isShareVideo = true;
                    }

                    shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                    ShareWeb(shareMedia);
                } else {
                    RxToast.showToast("未安装微信客户端...");
                }
                break;
            case R.id.iv_share_weixin://微信
                if (umShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                    if (activityId != null && activityId.equals(TuWenAlbumAty.activityId + "")) {
                        WXEntryActivity.isTrueShareLog = true;
                    }else if (activityId != null && activityId.equals(CircleVideoActivity.activityId + "")){
                        WXEntryActivity.isShareVideo = true;
                    }
                    shareMedia = SHARE_MEDIA.WEIXIN;
                    ShareWeb(shareMedia);
                } else {
                    RxToast.showToast("未安装微信客户端...");
                }
                break;
            case R.id.iv_share_weibo://微博
                if (umShareAPI.isInstall(this, SHARE_MEDIA.SINA)) {
                    shareMedia = SHARE_MEDIA.SINA;
                    ShareWeb(shareMedia);
                } else {
                    RxToast.showToast("未安装微博客户端...");
                }
                break;
            case R.id.iv_share_qq://QQ
                if (umShareAPI.isInstall(this, SHARE_MEDIA.QQ)) {
                    shareMedia = SHARE_MEDIA.QQ;
                    ShareWeb(shareMedia);
//                    ShareImage(shareMedia);
                } else {
                    RxToast.showToast("未安装QQ客户端...");
                }
                break;
        }

    }

    private void toMainActivity() {
        if (activityId != null && activityId.equals(ReleaseGraphicAlbumAty.activityId + "")) {
            /*Intent intent = new Intent(ShareToActivity.this, MainActivity.class);
            intent.putExtra("activityId", ShareToActivity.mActivityId);
            startActivity(intent);*/
            if (activityStack != null && activityStack.size() > 0) {
                for (int i = 0; i < activityStack.size(); i++) {
                    if (null != activityStack.get(i) && !activityStack.get(i).getLocalClassName().equals("MainActivity")) {
                        activityStack.get(i).finish();
                    }
                }
            }
            activityStack.clear();
            RxFileTool.cleanExternalCache(ShareToActivity.this);//发布成功后，清除压缩后的产生的缓存
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            myEventBusModel.REFRESH_MINE_CIRCLER = true;
            EventBus.getDefault().post(myEventBusModel);
        } else {
            finish();
            //关闭窗体动画显示
            this.overridePendingTransition(0,R.anim.push_dialog_bottom_out);
        }
    }

    /**
     * 分享回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    /**
     * 分享连接
     * @param share_media
     */
    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(ShareToActivity.this, shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        if (TextUtils.isEmpty(cotent)) {
            if (activityId != null && activityId.equals(CircleVideoActivity.activityId + "")) {
                web.setDescription(getString(R.string.share_content_log_video));
            }
            if (activityId != null && activityId.equals(TuWenAlbumAty.activityId + "")) {
                web.setDescription(getString(R.string.share_content_log));
            }
        } else {
            web.setDescription(cotent);
        }
        if (!TextUtils.isEmpty(title)) {
            web.setTitle(title);
        } else {
            web.setTitle(getString(R.string.share_title_log));
        }
        new ShareAction(ShareToActivity.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    /**
     * 分享图片
     * @param share_media
     */
    private void ShareImage(SHARE_MEDIA share_media) {
        //图片可直接替换成 网络图片 ／ 本地文件 ／ 资源文件 ／ bitmap文件 ／ 字节流
        shareImage = "http://okdi.oss-cn-beijing.aliyuncs.com/merrichat_image_1525438455.jpg";
        UMImage thumb = new UMImage(ShareToActivity.this, shareImage);
        new ShareAction(ShareToActivity.this).withMedia(thumb).setPlatform(share_media).setCallback(umShareListener).share();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBack) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (activityId != null && activityId.equals(ReleaseGraphicAlbumAty.activityId + "")) {
            return;
        }
    }
}
