package com.merrichat.net.wxapi;

import android.os.Bundle;

import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.utils.RxTools.RxToast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;


public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    //帖子分享专用
    public static boolean isShareLog = false;
    public static boolean isTrueShareLog = false;
    public static boolean isShareVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MerriApp.iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (MerriApp.sendReq) {
            switch (resp.errCode) {

                case BaseResp.ErrCode.ERR_OK:
                    if (MerriApp.sendReq) {
                        SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                        MerriApp.WX_CODE = sendResp.code;
                        finish();
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //RxToast.showToast("登录取消");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    RxToast.showToast("登录失败,请稍后再试");
                    finish();
                    break;
                default:
                    RxToast.showToast("登录失败,请稍后再试");
                    finish();
                    break;

            }
        } else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    RxToast.showToast("分享成功");
                    if (!isShareLog && isTrueShareLog){
                        isShareLog = true;
                    }
                    //视频播放分享
                    if (isShareVideo) {
                        CircleVideoActivity.isShareSuc = true;
                        isShareVideo = false;
                    }
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    RxToast.showToast("分享取消");
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    RxToast.showToast("分享失败");
                    finish();
                    break;
                default:
                    RxToast.showToast("分享失败");
                    finish();
                    break;
            }
            isTrueShareLog = false;
        }
        //发布图文 微信分享成功后 返回推荐
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.COLSE_GRAPHICALBUM_ACTIVITY = true;
        EventBus.getDefault().post(myEventBusModel);
    }

}
