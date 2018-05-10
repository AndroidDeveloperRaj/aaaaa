package com.merrichat.net.activity.grouporder;

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
import com.merrichat.net.http.Urls;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 17/9/25.
 * 查看物流
 */

public class ChaKanWuLiuActivity extends MerriActionBarActivity {


    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.webView_bang_dan)
    WebView webViewBangDan;
    private String billId = "";
    private String expressCode = "";
    private String serviceTel = "";
    private String expLogo = "";
    private String expName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_dan);
        ButterKnife.bind(this);
        setTitle("查看物流");
        setLeftBack();
        rlTitle.setVisibility(View.GONE);
        initView();
    }


    private void initView() {
        String orderId = getIntent().getStringExtra("orderId");
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

        queryExpressTrack(orderId);
    }

    /**
     * 查询物流详情
     *
     * @param orderId
     */
    private void queryExpressTrack(String orderId) {

        OkGo.<String>post(Urls.queryExpressTrack)//
                .tag(this)//
                .params("orderId", orderId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {

                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        String uri = data.optString("message");
                                        webViewBangDan.loadUrl(uri);
                                    } else {
                                        RxToast.showToast(data.optString("message"));
                                    }

                                } else {
                                    RxToast.showToast(R.string.connect_to_server_fail);
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

    private class JsOperation {
        //Html调用此方法传递数据，注解一定要留着否则会出错
        @JavascriptInterface
        public void shoot() {
        }
    }

}
