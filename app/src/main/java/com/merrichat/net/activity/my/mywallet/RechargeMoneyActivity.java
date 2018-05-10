package com.merrichat.net.activity.my.mywallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.aLiPayUtils.AuthResult;
import com.merrichat.net.utils.aLiPayUtils.PayResult;
import com.merrichat.net.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/12/14.
 * 讯美币充值
 */

public class RechargeMoneyActivity extends BaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    /**
     * title
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * 讯美币余额
     */
    @BindView(R.id.tv_mei_bi)
    TextView tvMeiBi;
    /**
     * 10讯美币
     */
    @BindView(R.id.ll_money_ten)
    LinearLayout llMoneyTen;
    /**
     * 20讯美币
     */
    @BindView(R.id.ll_money_twenty)
    LinearLayout llMoneyTwenty;
    /**
     * 30讯美币
     */
    @BindView(R.id.ll_money_thirty)
    LinearLayout llMoneyThirty;
    /**
     * 60讯美币
     */
    @BindView(R.id.ll_money_sixty)
    LinearLayout llMoneySixty;
    /**
     * 100讯美币
     */
    @BindView(R.id.ll_money_hundred)
    LinearLayout llMoneyHundred;
    /**
     * 200讯美币
     */
    @BindView(R.id.ll_money_two_hundred)
    LinearLayout llMoneyTwoHundred;
    /**
     * 300讯美币
     */
    @BindView(R.id.ll_money_three_hundred)
    LinearLayout llMoneyThreeHundred;
    /**
     * 500讯美币
     */
    @BindView(R.id.ll_money_five_hundred)
    LinearLayout llMoneyFiveHundred;
    /**
     * 支付宝充值
     */
    @BindView(R.id.ll_alipay)
    LinearLayout llAlipay;
    /**
     * 微信充值
     */
    @BindView(R.id.ll_wechat)
    LinearLayout llWechat;
    /**
     * 余额充值
     */
    @BindView(R.id.ll_balance_recharge)
    LinearLayout llBalanceRecharge;
    /**
     * 立即充值
     */
    @BindView(R.id.ll_immediately_recharge)
    LinearLayout llImmediatelyRecharge;
    /**
     * 账户余额
     */
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    /**
     * 立即充值
     */
    @BindView(R.id.tv_chong_zhi)
    TextView tvChongZhi;
    /**
     * 讯美币充值金额
     */
    private int rechargeMoney = 0;
    /**
     * 充值方式
     * 默认0
     * 支付宝1   微信 2  余额3
     */
    private int rechargeMethod = 0;
    /**
     * 账户余额
     */
    private double cashBalance = 0.00;
    /**
     * 金币数量
     */
    private String giftBalance;
    private IWXAPI api;
    /**********
     * 微信支付 --返回值
     ************/

    private String appid = "";
    private String partnerid = "";
    private String prepayId = "";
    private String noncestr = "";
    private String timestamp = "";
    private String sign = "";
    private String accountId;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeMoneyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        MyEventBusModel eventBusModel = new MyEventBusModel();
                        eventBusModel.REFRESH_MY_WALLET = true;
                        EventBus.getDefault().post(eventBusModel);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(RechargeMoneyActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(RechargeMoneyActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_money);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        tvTitleText.setText("讯美币充值");
    }


    private void initView() {
        api = WXAPIFactory.createWXAPI(this, "" + R.string.weixin_app_id, false);
        api.registerApp("" + R.string.weixin_app_id);
        accountId = UserModel.getUserModel().getAccountId();
        queryWalletInfo();
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_WECHAT_MEIBI) {//关闭本页面
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_MY_WALLET = true;
            EventBus.getDefault().post(eventBusModel);
            finish();
        }
    }


    /**
     * 获取余额
     */
    private void queryWalletInfo() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", accountId)
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                cashBalance = data.optDouble("cashBalance");//账户余额
                                giftBalance = data.optString("giftBalance");//金币数量

                                tvBalance.setText("余额充值(可用￥" + cashBalance + ")");
                                tvMeiBi.setText(giftBalance);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @OnClick({R.id.iv_back, R.id.ll_money_ten, R.id.ll_money_twenty, R.id.ll_money_thirty, R.id.ll_money_sixty, R.id.ll_money_hundred, R.id.ll_money_two_hundred, R.id.ll_money_three_hundred, R.id.ll_money_five_hundred, R.id.ll_alipay, R.id.ll_wechat, R.id.ll_balance_recharge, R.id.ll_immediately_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_money_ten://30讯美币
                rechargeMoney = 30;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥30)");

                break;
            case R.id.ll_money_twenty://50讯美币
                rechargeMoney = 50;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥50)");
                break;
            case R.id.ll_money_thirty://98讯美币
                rechargeMoney = 98;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥98)");
                break;
            case R.id.ll_money_sixty://298讯美币
                rechargeMoney = 298;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥298)");
                break;
            case R.id.ll_money_hundred://488讯美币
                rechargeMoney = 488;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥488)");
                break;
            case R.id.ll_money_two_hundred://998讯美币
                rechargeMoney = 998;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥998)");
                break;
            case R.id.ll_money_three_hundred://4998讯美币
                rechargeMoney = 4998;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_yes);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                tvChongZhi.setText("立即充值(￥4998)");
                break;
            case R.id.ll_money_five_hundred://6498讯美币
                rechargeMoney = 6498;
                llMoneyTen.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwenty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThirty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneySixty.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyTwoHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyThreeHundred.setBackgroundResource(R.drawable.shape_recharge_no);
                llMoneyFiveHundred.setBackgroundResource(R.drawable.shape_recharge_yes);
                tvChongZhi.setText("立即充值(￥6498)");
                break;
            case R.id.ll_alipay://支付宝充值
                rechargeMethod = 1;
                llAlipay.setBackgroundResource(R.drawable.shape_recharge_yes);
                llWechat.setBackgroundResource(R.drawable.shape_recharge_no);
                llBalanceRecharge.setBackgroundResource(R.drawable.shape_recharge_no);
                break;
            case R.id.ll_wechat://微信充值
                rechargeMethod = 2;
                llAlipay.setBackgroundResource(R.drawable.shape_recharge_no);
                llWechat.setBackgroundResource(R.drawable.shape_recharge_yes);
                llBalanceRecharge.setBackgroundResource(R.drawable.shape_recharge_no);
                break;
            case R.id.ll_balance_recharge://余额充值
                rechargeMethod = 3;
                llAlipay.setBackgroundResource(R.drawable.shape_recharge_no);
                llWechat.setBackgroundResource(R.drawable.shape_recharge_no);
                llBalanceRecharge.setBackgroundResource(R.drawable.shape_recharge_yes);
                break;
            case R.id.ll_immediately_recharge://立即充值
                immediatelyRecharge();
                break;
        }
    }

    /**
     * 立即充值
     */
    private void immediatelyRecharge() {
        if (rechargeMoney == 0) {
            GetToast.useString(cnt, "请选择充值讯美币金额");
            return;
        }
        if (rechargeMethod == 0) {
            GetToast.useString(cnt, "请选择充值方式");
            return;
        }
        if (rechargeMethod == 3) {
            if (rechargeMethod > cashBalance) {
                GetToast.useString(cnt, "余额不足");
                return;
            }

            recharge("2");
        }

        if (rechargeMethod == 2) {//微信
            if (!StringUtil.isWeixinAvilible(this)) {
                GetToast.useString(this, "你还未安装微信");
                return;
            }
            recharge("1");
        }

        if (rechargeMethod == 1) {//支付宝
            recharge("4");
        }
    }


    /**
     * 充值业务(微信)（余额或金币充值） /billInfo/rechargeAli（支付宝） 充值业务（余额或金币充值)
     * <p>
     * money   充值金额或 金币数
     * <p>
     * tradeType   充值类型0:微信充值到余额 1：微信充值到金币2：余额充值到金币 3: 支付宝充值到余额 4 支付宝充值到金币
     * <p>
     * platform_id  平台id(爱购猫 100001 快递员 100002 好递支付 100003 其他 100004  讯美 100005) 不能为空
     */
    private void recharge(final String tradeType) {
        OkGo.<String>post(Urls.billInfoRecharge)
                .params("accountId", accountId)
                .params("uid", UserModel.getUserModel().getMemberId())
                .params("money", rechargeMoney)
//                .params("money", "0.01")
                .params("tradeType", tradeType)
                .params("platform_id", "100005")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if ("1".equals(tradeType)) {
                                    JSONObject data = jsonObject.optJSONObject("data");
                                    appid = data.optString("appid");
                                    partnerid = data.optString("partnerid");
                                    prepayId = data.optString("prepayId");
                                    noncestr = data.optString("noncestr");
                                    timestamp = data.optString("timestamp");
                                    sign = data.optString("sign");
                                    WXPayEntryActivity.setPayType(1);//讯美币充值
                                    WeChatPay();//调起微信支付
                                } else if ("2".equals(tradeType)) {
                                    GetToast.useString(cnt, "充值成功");
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.REFRESH_MY_WALLET = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                    finish();
                                } else if ("4".equals(tradeType)) {
                                    String data = jsonObject.optString("data");
                                    aLiPay(data);
                                }
                            } else {
                                String error_msg = jsonObject.optString("error_msg");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(cnt, error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 微信支付
     */
    private void WeChatPay() {
        PayReq request = new PayReq();
        request.appId = appid;
        request.partnerId = partnerid;
        request.prepayId = prepayId;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = noncestr;
        request.timeStamp = timestamp;
        request.sign = sign;

        api.sendReq(request);
    }


    /**
     * 支付宝支付
     */
    private void aLiPay(String info) {
        final String orderInfo = info;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeMoneyActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
