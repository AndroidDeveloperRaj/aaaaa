package com.merrichat.net.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.utils.GetToast;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, View.OnClickListener {

    private IWXAPI api;


    /**
     * 支付类型
     * 1 讯美币充值
     */
    public static int payType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, "" + R.string.weixin_app_id);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    /**
     * 支付类型（微信充值 1讯美币充值 2消息发红包 3群市场商品支付）
     *
     * @param paysType
     */
    public static void setPayType(int paysType) {
        payType = paysType;
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int number = resp.errCode;
            switch (number) {//支付失败
                case -1:
                    GetToast.useString(cnt, "支付失败");
                    finish();
                    break;
                case -2://支付取消
                    GetToast.useString(cnt, "支付取消");
                    finish();
                    break;
                case 0://支付成功
                    switch (payType) {
                        case 1://讯美币充值
                            GetToast.useString(cnt, "支付成功");
                            MyEventBusModel myEventBusModel = new MyEventBusModel();
                            myEventBusModel.REFRESH_WECHAT_MEIBI = true;
                            EventBus.getDefault().post(myEventBusModel);
                            finish();
                            break;
                        case 2://发红包支付
                            GetToast.useString(cnt, "支付成功");
                            MyEventBusModel myEventBusModel1 = new MyEventBusModel();
                            myEventBusModel1.REFRESH_WECHAT_FAHONGBAO = true;
                            EventBus.getDefault().post(myEventBusModel1);
                            finish();
                            break;
                        case 3://群市场商品支付
                            GetToast.useString(cnt, "支付成功");
                            MyEventBusModel myEventBusModel2 = new MyEventBusModel();
                            myEventBusModel2.WXPAY_SUCCESS = true;
                            EventBus.getDefault().post(myEventBusModel2);
                            finish();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
