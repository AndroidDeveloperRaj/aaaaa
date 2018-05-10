package com.merrichat.net.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.UrlConfig;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Allen Cheng on 2018/3/28.
 * <p>
 * 邀请赚钱--奖励报表---赚钱攻略
 */

public class ShareToMakeMoneyStrategyAty extends MerriActionBarActivity {
    @BindView(R.id.webView_bang_dan)
    WebView webViewBangDan;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    private String uri = UrlConfig.getLifeUrl() + "profit/inviteExplain.html";

    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_dan);
        ButterKnife.bind(this);
        setLeftBack();
        setTitle("赚钱攻略");
        rlTitle.setVisibility(View.GONE);
        initView();
    }

    private void initView() {
        webViewBangDan.loadUrl(uri);
        //启用支持javascript
        WebSettings settings = webViewBangDan.getSettings();
        settings.setJavaScriptEnabled(true);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webViewBangDan.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //showErrorPage();//显示错误页面
            }
        });

        webViewBangDan.setWebChromeClient(new WebChromeClient() {
            /**
             * 当WebView加载之后，返回 HTML 页面的标题 Title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                if (!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) {
                    //showErrorPage();//显示错误页面
                }
            }
        });
        //添加js对象(必要)
        webViewBangDan.addJavascriptInterface(new JsOperation(), "Android");
    }

    /**
     * 获取分享二维码
     */
    private void getPromoQRcode() {
        OkGo.<String>post(Urls.getPromoQRcode)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "3")//分享来源 source为0:天降红包，抢红包页面 1:答题红包页面 2:秀吧分享 3:邀请 4:天降红包弹窗 6:拼团
                .params("activityId", "")
                .params("redParcelId", "")
                .params("articleId", "")
                .params("atlMemberId", "")
                .params("orderId", "")
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String url = data.optString("url");
                                ShareWeb(url, shareMedia);
                            } else {
                                RxToast.showToast("分享失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void ShareWeb(String url, SHARE_MEDIA share_media) {
        String memeberId = UserModel.getUserModel().getMemberId();
        UMImage thumb = new UMImage(cnt, ConstantsPath.share_to_invite_icon);
        UMWeb web = new UMWeb(url);
        web.setThumb(thumb);
        web.setDescription(cnt.getString(R.string.share_content));
        web.setTitle(cnt.getString(R.string.share_title));
        new ShareAction((Activity) cnt).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    private class JsOperation {
        //Html调用此方法传递数据，注解一定要留着否则会出错
        @JavascriptInterface
        public void share() {
            if (StringUtil.isWeixinAvilible(cnt)) {
                getPromoQRcode();
            } else {
                RxToast.showToast("你还未安装微信~");
            }
        }
    }
}
