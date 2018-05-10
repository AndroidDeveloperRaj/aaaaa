package com.merrichat.net.activity.groupmarket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.RxTools.RxToast;
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
 * 订单支付
 * Created by amssy on 18/1/20.
 */

public class OrderPayActivity extends Activity {
    @BindView(R.id.iv_close)
    ImageView ivClose;
    /**
     * 余额
     */
    @BindView(R.id.tv_yue)
    TextView tvYue;
    @BindView(R.id.iv_qianbao_check)
    ImageView ivQianbaoCheck;
    @BindView(R.id.rl_qianbao_pay)
    RelativeLayout rlQianbaoPay;
    /**
     * 微信
     */
    @BindView(R.id.tv_wechat)
    TextView tvWechat;
    @BindView(R.id.iv_weixin_check)
    ImageView ivWeixinCheck;
    @BindView(R.id.rl_weixin_pay)
    RelativeLayout rlWeixinPay;
    /**
     * 支付宝
     */
    @BindView(R.id.tv_alipay)
    TextView tvAlipay;
    @BindView(R.id.iv_ali_check)
    ImageView ivAliCheck;
    @BindView(R.id.rl_alipay)
    RelativeLayout rlAlipay;

    /**
     * 需支付金额
     */
    @BindView(R.id.tv_need_pay)
    TextView tvNeedPay;
    @BindView(R.id.tv_now_buy)
    TextView tvNowBuy;
    @BindView(R.id.ll_all)
    RelativeLayout llAll;
    @BindView(R.id.ll_boot)
    RelativeLayout llBoot;

    private Double giftBalance;//可用讯美币
    private Double cashBalance;//可用余额
    private Double payPrice = 0.0;
    private int payWays = 1;//1：钱包支付 2：微信支付 3：支付宝支付

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

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;


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
                    //9000为支付成功 4000为支付失败 6001为取消支付 8000为支付结果确认中
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(OrderPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                        updateOrderPayment();

                    }else if (TextUtils.equals(resultStatus, "6001")){
                        Toast.makeText(OrderPayActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                    }else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OrderPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(OrderPayActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(OrderPayActivity.this,
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

    private int flag;//1:正常下单,2拼团下单,3参团下单
    private String orderId;//订单ID
    private Double tradeTotalAmount;//订单总金额
    private int payType;//支付方式 1 支付宝，2微信，3余额
    private double outcomeAccountPayAmount;//账户支付实际金额
    private double couponsTotalAmount;//优惠券金额
    private double goldAmount;//金币支付金额
    private double thirdPayAmount;//第三方支付金额/微信支付
    private String couponsInfo = "";//消费券信息(由一张或多张消费券组成，采用逗号分隔 每张消费券信息，包含消费券ID、消费券实际消费金额，采用|作为分隔符)
    private String orderTitle = "";//订单title
    private String remark = "";//订单备注
    private String channelsCode;//渠道简码 例如：weixinpay 微信
    private boolean isJSAPI = false;//是否为微信公众号支付（false不是 true 是）
    private String platform_id = "100005";//平台id(爱购猫 100001 快递员 100002 好递支付 100003 其他 100004 讯美100005) 不能为空
    private String openid = "";
    private String address;
    private String phone;
    private String name;
    private String tid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfir_order_pay);
        ButterKnife.bind(this);
        initView();
        queryWalletInfo();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            flag = intent.getIntExtra("flag", 0);
            orderId = intent.getStringExtra("orderId");
            //总金额保留两位小数
            tradeTotalAmount = Double.valueOf(StringUtil.rounded(intent.getDoubleExtra("tradeTotalAmount", 0)));
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
            address = intent.getStringExtra("address");
            tvNeedPay.setText("" + tradeTotalAmount);
            payPrice = tradeTotalAmount;
        }

        api = WXAPIFactory.createWXAPI(this, "" + R.string.weixin_app_id, false);
        api.registerApp("" + R.string.weixin_app_id);

        StringUtil.setRelHeight(llAll, this, 0.55f);
        //默认钱包支付
        tvYue.setTextColor(getResources().getColor(R.color.normal_red));
        ivQianbaoCheck.setVisibility(View.VISIBLE);
        tvWechat.setTextColor(getResources().getColor(R.color.normal_black));
        tvAlipay.setTextColor(getResources().getColor(R.color.normal_black));
        ivWeixinCheck.setVisibility(View.GONE);
        ivAliCheck.setVisibility(View.GONE);
        //注册广播
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.iv_close, R.id.rl_qianbao_pay, R.id.rl_weixin_pay, R.id.rl_alipay, R.id.tv_now_buy})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close://关闭
                //发送广播关闭之前的界面
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.CLOSE_NO_ORDER = true;
                EventBus.getDefault().post(myEventBusModel);
                finish();
                break;
            case R.id.rl_qianbao_pay://点击钱包支付
                payWays = 1;
                tvYue.setTextColor(getResources().getColor(R.color.normal_red));
                ivQianbaoCheck.setVisibility(View.VISIBLE);
                tvWechat.setTextColor(getResources().getColor(R.color.normal_black));
                tvAlipay.setTextColor(getResources().getColor(R.color.normal_black));
                ivWeixinCheck.setVisibility(View.GONE);
                ivAliCheck.setVisibility(View.GONE);
                break;
            case R.id.rl_weixin_pay://点击微信支付
                payWays = 2;
                tvYue.setTextColor(getResources().getColor(R.color.normal_black));
                ivQianbaoCheck.setVisibility(View.GONE);
                tvWechat.setTextColor(getResources().getColor(R.color.normal_red));
                tvAlipay.setTextColor(getResources().getColor(R.color.normal_black));
                ivWeixinCheck.setVisibility(View.VISIBLE);
                ivAliCheck.setVisibility(View.GONE);
                break;
            case R.id.rl_alipay://点击支付宝支付
                payWays = 3;
                tvYue.setTextColor(getResources().getColor(R.color.normal_black));
                ivQianbaoCheck.setVisibility(View.GONE);
                tvWechat.setTextColor(getResources().getColor(R.color.normal_black));
                tvAlipay.setTextColor(getResources().getColor(R.color.normal_red));
                ivWeixinCheck.setVisibility(View.GONE);
                ivAliCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_now_buy://立即购买
                if (payWays == 1 && cashBalance < payPrice) {
                    RxToast.showToast("余额不足,请选择其他支付方式");
                } else {
                    //1:正常下单,2拼团下单,3参团下单
                    switch (flag) {
                        case 1:
                            orderTitle = "购买商品付款";
                            break;
                        case 2:
                            orderTitle = "商品拼团付款";
                            break;
                        case 3:
                            orderTitle = "商品参团付款";
                            break;
                    }

                    switch (payWays) {
                        case 1://钱包支付
                            //支付方式 1 支付宝，2微信，3余额
                            payType = 3;
                            //渠道简码 例如：weixinpay 微信
                            channelsCode = "balance";
                            break;
                        case 2://微信支付
                            payType = 2;
                            channelsCode = "weixinpay";
                            break;
                        case 3://支付宝支付
                            payType = 1;
                            channelsCode = "alipay";
                            break;
                    }
                    orderPay();
                }
                break;
        }
    }

    /**
     * 查询可用余额
     */
    private void queryWalletInfo() {
        OkGo.<String>get(Urls.queryWalletInfo)
                .tag(this)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("maxMoney", "1000")//帖子ID
                .params("uid", UserModel.getUserModel().getMemberId())//评论内容
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //可用余额
                                    cashBalance = data.optJSONObject("data").getDouble("cashBalance");
                                    //可用讯美币
                                    giftBalance = data.optJSONObject("data").getDouble("giftBalance");
                                    tvYue.setText("钱包余额(可用余额" + cashBalance + "元)");
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
     * 订单支付
     */
    private void orderPay() {
        //暂时不传
        //第三方支付金额/微信支付
        thirdPayAmount = 0.0;
        //账户支付实际金额
        outcomeAccountPayAmount = 0.0;

        OkGo.<String>post(Urls.orderInfoPay)
                .tag(this)
                .params("orderId", orderId)//订单Id
                .params("paymentType", payType)//支付方式 1 支付宝，2微信，3余额
                .params("tradeTotalAmount", tradeTotalAmount)//订单总金额
                .params("outcomeAccountPayAmount", outcomeAccountPayAmount)//账户支付实际金额
                .params("couponsTotalAmount", couponsTotalAmount)//优惠券金额
                .params("goldAmount", goldAmount)//金币支付金额
                .params("thirdPayAmount", thirdPayAmount)//第三方支付金额/微信支付
                .params("couponsInfo", couponsInfo)//消费券信息(由一张或多张消费券组成，采用逗号分隔 每张消费券信息，包含消费券ID、消费券实际消费金额，采用|作为分隔符)
                .params("title", orderTitle)//订单title
                .params("remark", remark)//订单备注
                .params("channelsCode", channelsCode)//渠道简码 例如：weixinpay 微信
                .params("isJSAPI", isJSAPI)//是否为微信公众号支付（false不是 true 是）
                .params("openid", openid)//用户标识（微信isJSAPI为true时必须传）
                .params("platform_id", platform_id)//平台id(爱购猫 100001 快递员 100002 好递支付 100003 其他 100004 讯美100005) 不能为空
                .params("flag", flag)//1:正常, 2:拼团 ，3参团
                .execute(new StringDialogCallback(this, "正在支付中...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //支付成功
                                    if (data.optJSONObject("data").optBoolean("success")) {
                                        //支付方式 1：钱包支付 2：微信支付 3：支付宝支付
                                        switch (payWays) {
                                            //钱包支付
                                            case 1:
                                                tid = data.optJSONObject("data").optJSONObject("data").optString("tid");
                                                //支付回调
                                                updateOrderPayment();

                                                break;
                                            //微信支付
                                            case 2:
                                                //支付结果{"data":{"total":"0","hasnext":false,"time":1517391718450,"page":"0","data":{"sign":"E6D117B92E679E7447DAB53BBE735D2B","timestamp":"1517391649","noncestr":"958udtk0jn6a9us7vnq499sf3ghenx88","partnerid":"1486139572","thirdPayAmount":11.00,"package":"Sign=WXPay","appid":"wxa1cb75818e93b070","outcomeAccountPayAmount":0.0,"tid":"163198173694668800","prepayId":"wx20180131174050ebc42191700564144476","channelsCode":"weixinpay"},"error_code":"","success":true,"error_msg":"","rows":"0"},"success":true}
                                                //判断微信是否安装
                                                if (StringUtil.isWeixinAvilible(OrderPayActivity.this)) {
                                                    tid = data.optJSONObject("data").optJSONObject("data").optString("tid");

                                                    appid = data.optJSONObject("data").optJSONObject("data").optString("appid");
                                                    partnerid = data.optJSONObject("data").optJSONObject("data").optString("partnerid");
                                                    prepayId = data.optJSONObject("data").optJSONObject("data").optString("prepayId");
                                                    noncestr = data.optJSONObject("data").optJSONObject("data").optString("noncestr");
                                                    timestamp = data.optJSONObject("data").optJSONObject("data").optString("timestamp");
                                                    sign = data.optJSONObject("data").optJSONObject("data").optString("sign");
                                                    WeChatPay();
                                                } else {
                                                    RxToast.showToast("您未安装微信客户端");
                                                }
                                                break;
                                            //支付宝支付
                                            case 3:
                                                tid = data.optJSONObject("data").optJSONObject("data").optString("tid");

                                                //支付结果{"data":{"total":"0","hasnext":false,"time":1517455436249,"page":"0","data":{"sign":"alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2017111509942745&biz_content=%7B%22out_trade_no%22%3A%22163264987605005312%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E8%B4%AD%E4%B9%B0%E5%95%86%E5%93%81%E4%BB%98%E6%AC%BE%22%2C%22total_amount%22%3A%2211.00%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F119.57.115.198%3A8180%2Fclip-pub%2Fweixin%2FpaycallbackAli&sign=cxJWtCbKkw%2FfnyYs5UZgzbyGPdqi7dnKaFGcdnHo0dso%2B1WIUhid3uR3d0jzBRhMQtuhOI8DXvg%2FlvXmfd3A4oXwN0cgCSTqJTk%2Fb7doPVu1jQ6J7iDXQB0JNmjMNSQ3GpQJUHtuihnmCrnZxHLDbo2Id4r2V7L2uajODwKuld5ekZKg1LQ4TX0ZHDcrnKs2mVUcY1WZnqBhOFZLHD2z488%2BMpaGsUuY0ATc6WrKqNMCkcs9LCfGU4jG4R7f0FKb43HO5fPfFR67qkqMb%2FSnS%2Fqh7sN13G8YUNGa5BF%2BmLqgLQ9w%2FS2%2BDDpcqPTLduH3iaxaYcW0cAtZ7KWGWik%2Bcw%3D%3D&sign_type=RSA2&timestamp=2018-02-01+11%3A22%3A47&version=1.0","thirdPayAmount":11.00,"outcomeAccountPayAmount":0.0,"tid":"163264987605005312"},"error_code":"","success":true,"error_msg":"","rows":"0"},"success":true}
                                                String info = data.optJSONObject("data").optJSONObject("data").optString("sign");
                                                aLiPay(info);
                                                break;
                                        }
                                    } else {
                                        RxToast.showToast(data.optJSONObject("data").optString("error_msg"));
                                    }
                                } else {
                                    RxToast.showToast(data.optString("msg"));
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
     * 微信支付
     */
    private void WeChatPay() {
        //群市场商品支付
        WXPayEntryActivity.payType = 3;

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
                PayTask alipay = new PayTask(OrderPayActivity.this);
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

    /**
     * 接受广播关闭界面
     *
     * @param myEventBusModel
     */
    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_ORDER) {
            finish();
        }else if (myEventBusModel.WXPAY_SUCCESS){//微信支付成功
            updateOrderPayment();
        }
    }

    /**
     * 支付回调
     */
    private void updateOrderPayment() {
        OkGo.<String>get(Urls.updateOrderPayment)
                .tag(this)
                .params("tid", tid)
                .params("flag", true)
                .execute(new StringDialogCallback(OrderPayActivity.this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    Intent intent = new Intent(OrderPayActivity.this, OrderBuyFinishActivity.class);
                                    intent.putExtra("tradeTotalAmount", tradeTotalAmount);//支付总金额
                                    intent.putExtra("address", address);
                                    intent.putExtra("name", name);
                                    intent.putExtra("phone", phone);
                                    intent.putExtra("flag", flag);
                                    startActivity(intent);
                                    finish();
                                } else {

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
     * 返回键监听
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //发送广播关闭之前的界面
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            myEventBusModel.CLOSE_NO_ORDER = true;
            EventBus.getDefault().post(myEventBusModel);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
