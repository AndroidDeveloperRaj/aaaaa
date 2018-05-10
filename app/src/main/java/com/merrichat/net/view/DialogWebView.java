package com.merrichat.net.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.http.SslError;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.merrichat.net.R;

/**
 * Created by amssy on 18/1/10.
 * WebView的 Dialog(天降红包)
 */

public abstract class DialogWebView extends Dialog{

    private Context mContext;
    private String loadUrl;// 加载的网址
    private WebView webView;

    public DialogWebView(Context mContext, String loadUrl) {
        super(mContext, R.style.Dialog_FS);
        this.mContext = mContext;
        this.loadUrl = loadUrl;
        setContentView(R.layout.dialog_webview_layout);
        webView = (WebView) findViewById(R.id.webView);
        webView.addJavascriptInterface(new WebViewInterface(), "Android");

        //设置圆角度数
        //webView.setRound(30.0f);
        initData();
    }

    /**
     * 初使化数据
     */
    @SuppressLint("SetJavaScriptEnabled") private void initData() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);// 开启js功能
        //getContext().getResources().getColor(R.color.black)  0   100
        webView.setBackgroundColor(600); // 设置背景色
        webView.getBackground().setAlpha(20); // 设置填充透明度 范围：0-255
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // TODO Auto-generated method stub
//				只需像这样重载WebViewClient的onReceivedSslError()函数并在其中执行handler.proceed()，即可忽略SSL证书错误，继续加载页面。
//				这里要注意的是，千万不要调用super.onReceivedSslError()。这是因为WebViewClient的onReceivedSslError()函数中包含了一条handler.cancel()
//				super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        webView.loadUrl(loadUrl);
    }

    public class WebViewInterface {
        Context context;
        /** Instantiate the interface and set the context *//*
			public WebViewInterface(Context c) {
				context = c;
			}
			/**
			 *关闭新用户登录的奖励弹框
			 */
        @JavascriptInterface
        public void toShare(String activityId,String RedEnvelopeId){
            toDialogShare(activityId,RedEnvelopeId);
        }
        /**
         mRegWebViewDialog.dismiss();
         //GetToast.useString(context, "新用户登录弹出框关闭");
         context.sendBroadcast(new Intent(MainActivity.SHOW_FRIST_DIALOG));
         */


        @JavascriptInterface
        public void toClose() {
            toDialogClose();
        }
        /**
         mRewWebViewDialog.dismiss();
         if(null!=mRegWebViewDialog&&null!=mRewWebViewDialog){
         mRegWebViewDialog.dismiss();
         mRewWebViewDialog.dismiss();
         }
         */
    }
    public abstract void toDialogShare(String activityId,String RedEnvelopeId);
    public abstract void toDialogClose();
    public void setWebViewRound(int width, int height, float round){
//	webView.setRadius(webView.getWidth(), webView.getHeight(), round);
    }

}
