package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxDataTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2018/1/5.
 * <p>
 * 谁喜欢我
 */

public class WhoLikeMeAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.webView_bang_dan)
    WebView webViewBangDan;
    private String uri = "http://igomopub.igomot.net/clip-pub/rank/like.html?" + "mid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_dan);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String memberId = getIntent().getStringExtra("memberId");
        if (!RxDataTool.isNullString(memberId)) {
            if (memberId.equals(UserModel.getUserModel().getMemberId())) {
                tvTitleText.setText("喜欢我的人");
            } else {
                tvTitleText.setText("喜欢Ta的人");

            }
        }
        webViewBangDan.loadUrl(uri + memberId);
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
//        webViewBangDan.addJavascriptInterface(new MenAndWomenVerificationAty.JsOperation(), "Android");
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();

    }
}
