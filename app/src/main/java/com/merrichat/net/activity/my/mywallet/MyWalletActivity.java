package com.merrichat.net.activity.my.mywallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.groupmarket.CashDepositActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupListModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WeiXinLoginModel;
import com.merrichat.net.utils.ACacheUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.aLiPayUtils.AuthResult;
import com.merrichat.net.utils.aLiPayUtils.OrderInfoUtil2_0;
import com.merrichat.net.utils.aLiPayUtils.PayResult;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.app.MerriApp.WX_CODE;

/**
 * Created by amssy on 17/7/1.
 * 我的钱包
 */

public class MyWalletActivity extends BaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    public static boolean isSetPassword = false;//是否设置了支付密码
    public static int activityId = MiscUtil.getActivityId();
    public static IWXAPI api; // 微信开放平台
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    /**
     * 现金余额
     */
    @BindView(R.id.tv_cash_balance)
    TextView tvCashBalance;
    /**
     * 讯美币余额
     */
    @BindView(R.id.tv_dollars_balance)
    TextView tvDollarsBalance;
    /**
     * 赠送话费余额
     */
    @BindView(R.id.tv_communication_balance)
    TextView tvCommunicationBalance;
    /**
     * 现金余额
     */
    @BindView(R.id.ll_cash_balance)
    LinearLayout llCashBalance;
    /**
     * 讯美币余额
     */
    @BindView(R.id.ll_redenvelops_num)
    LinearLayout llRedenvelopsNum;
    @BindView(R.id.rl_transaction_record)
    RelativeLayout rlTransactionRecord;
    @BindView(R.id.textView_pwd)
    TextView textViewPwd;
    /**
     * 设置/修改支付密码
     */
    @BindView(R.id.rl_set_password)
    RelativeLayout rlSetPassword;
    /**
     * 绑定支付宝账户
     */
    @BindView(R.id.rl_bind_alipay)
    RelativeLayout rlBindAlipay;
    /**
     * 绑定微信
     */
    @BindView(R.id.rl_bind_wechat)
    RelativeLayout rlBindWechat;
    /**
     * 讯美币充值
     */
    @BindView(R.id.rl_recharge_money)
    RelativeLayout rlRechargeMoney;
    /**
     * 支付宝是否绑定
     */
    @BindView(R.id.tv_alipay_isbinding)
    TextView tvAlipayIsbinding;
    /**
     * 微信是否绑定
     */
    @BindView(R.id.tv_wechat_isbinding)
    TextView tvWechatIsbinding;
    @BindView(R.id.rl_cash_deposit)
    RelativeLayout rlCashDeposit;
    @BindView(R.id.rel_group_wallet)
    RelativeLayout relGroupWallet;
    @BindView(R.id.tv_group_wallet_num)
    TextView tvGroupWalletNum;


    /**
     * 账户余额
     */
    private String cashBalance;

    /**
     * 讯美币余额
     */
    private String giftBalance;

    /**
     * 通讯余额
     */
    private String couponBalance;


    /**
     * 支付宝是否绑定
     * 默认false
     */
    private boolean isAliPayBing = false;


    /**
     * 微信是否绑定
     * 默认false
     */
    private boolean isWeChatBing = false;

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
                        Toast.makeText(MyWalletActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MyWalletActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(MyWalletActivity.this,
//                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
//                                .show();
                        Toast.makeText(MyWalletActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                        tvAlipayIsbinding.setText("已绑定");

                        String authCode = authResult.getAuthCode();
                        String alipayOpenId = authResult.getAlipayOpenId();
                        aliPaybound(authCode, alipayOpenId);
                    } else {
                        // 其他状态值则为授权失败
//                        Toast.makeText(MyWalletActivity.this,
//                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MyWalletActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    private List<GroupListModel.DataBean.ListBean> listBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initTitleBar();
        regToWx();
        queryWalletInfo();
    }

    private void initTitleBar() {
        tvTitleText.setText("我的钱包");
    }

    /**
     * 注册微信
     *
     * @return void 返回类型
     * @throws
     * @Title: regToWx
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void regToWx() {
        // TODO Auto-generated method stub
        api = WXAPIFactory.createWXAPI(cnt, cnt.getResources().getString(R.string.weixin_app_id), true);
        api.registerApp(cnt.getResources().getString(R.string.weixin_app_id));
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_MY_WALLET) {//刷新页面
            queryWalletInfo();
        }
    }

    @OnClick({R.id.iv_back, R.id.rl_transaction_record, R.id.rl_set_password
            , R.id.ll_cash_balance, R.id.rl_recharge_money, R.id.ll_redenvelops_num
            , R.id.rl_bind_alipay, R.id.rl_bind_wechat, R.id.rl_cash_deposit
            , R.id.rel_group_wallet})
    public void onViewClicked(View view) {
        //判断是否是快速点击
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_transaction_record://交易记录
                startActivity(new Intent(this, TransactionRecordActivity.class).putExtra("cashBalance", cashBalance).putExtra("giftBalance", giftBalance).putExtra("couponBalance", couponBalance));
                break;
            case R.id.rl_set_password://设置/修改支付密码
                startActivity(new Intent(this, PasswardActivity.class));
                break;
            case R.id.ll_cash_balance://现金余额
                RxActivityTool.skipActivity(this, WalletBalanceActivity.class);
                break;
            case R.id.rl_recharge_money://讯美币充值
                startActivity(new Intent(cnt, RechargeMoneyActivity.class));
                break;
            case R.id.ll_redenvelops_num://讯美币余额
                startActivity(new Intent(this, TransactionRecordActivity.class).putExtra("cashBalance", cashBalance).putExtra("giftBalance", giftBalance).putExtra("couponBalance", couponBalance));
                break;
            case R.id.rl_bind_alipay://绑定支付宝
                if (isAliPayBing) {
                    GetToast.useString(cnt, "您已绑定过支付宝账户");
                    return;
                }
                boundnAli();
                break;
            case R.id.rl_bind_wechat://绑定微信
                if (isWeChatBing) {
                    GetToast.useString(cnt, "您已绑定过微信");
                    return;
                } else {
                    UMShareAPI umShareAPI = null;
                    umShareAPI = UMShareAPI.get(this);
                    if (!umShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                        RxToast.showToast("未安装微信客户端...");
                        return;
                    }
                }
                bindWeChat();
                break;
            case R.id.rl_cash_deposit://我的保证金
                startActivity(new Intent(cnt, CashDepositActivity.class));
                break;
            case R.id.rel_group_wallet://我的群钱包
                startActivity(new Intent(cnt, MyGroupWalletAty.class));
                break;
        }
    }


    private void bindWeChat() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        MerriApp.sendReq = api.sendReq(req);
    }


    /**
     * 查询该账户是否绑定了微信
     */
    private void getWeixinAccountId() {
        OkGo.<String>post(Urls.getWeixinAccountId)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("platformId", "100005")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                tvWechatIsbinding.setText("已绑定");
                                tvWechatIsbinding.setTextColor(cnt.getResources().getColor(R.color.FF3D6F));
                                JSONObject data = jsonObject.optJSONObject("data");
                                String id = data.optString("id");
                                ACacheUtils.putWeChatAccountId(cnt, id);
                                isWeChatBing = true;
                            } else {
                                tvWechatIsbinding.setText("未绑定");
                                tvWechatIsbinding.setTextColor(cnt.getResources().getColor(R.color.base_888888));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 查询该账户是否绑定了支付宝账号
     */
    private void getAliPayAccountId() {
        OkGo.<String>post(Urls.getAliPayAccountId)
                .params("platformId", "100005")
                .params("accountId", UserModel.getUserModel().getAccountId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                tvAlipayIsbinding.setText("已绑定");
                                tvAlipayIsbinding.setTextColor(cnt.getResources().getColor(R.color.FF3D6F));
                                JSONObject data = jsonObject.optJSONObject("data");
                                String id = data.optString("id");
                                ACacheUtils.putAliPayAcountId(cnt, id);
                                isAliPayBing = true;
                            } else {
                                tvAlipayIsbinding.setText("未绑定");
                                tvAlipayIsbinding.setTextColor(cnt.getResources().getColor(R.color.base_888888));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 绑定支付宝账户
     */
    private void boundnAli() {
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(MerriApp.alipay_pid, MerriApp.alipay_app_id, System.currentTimeMillis() + "", true);
        final String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
        OkGo.<String>post(Urls.addAliPaySign)
                .params("all", info)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                String sign = data.optString("sign");

                                final String authInfo = info + "&" + sign;
                                Runnable authRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 构造AuthTask 对象
                                        AuthTask authTask = new AuthTask(MyWalletActivity.this);
                                        // 调用授权接口，获取授权结果
                                        Map<String, String> result = authTask.authV2(authInfo, true);

                                        Message msg = new Message();
                                        msg.what = SDK_AUTH_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };

                                // 必须异步调用
                                Thread authThread = new Thread(authRunnable);
                                authThread.start();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 查询余额、红包和金币
     */
    private void queryWalletInfo() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                cashBalance = data.optString("cashBalance");//账户余额
                                giftBalance = data.optString("giftBalance");//讯美币余额
                                couponBalance = data.optString("couponBalance");//通讯余额

                                tvCashBalance.setText("￥" + cashBalance);
                                tvDollarsBalance.setText(giftBalance);
                                tvCommunicationBalance.setText(couponBalance);

                                getIsCheckPWD();
                                getAliPayAccountId();
                                getWeixinAccountId();
                                communityList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    /**
     * 查询是否设置支付密码
     */
    private void getIsCheckPWD() {
        OkGo.<String>post(Urls.checkAccountPwdIsExist)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                if (jsonObject.getJSONObject("data").optString("isSetPassword").equals("0")) {//0：没设置
                                    textViewPwd.setText("设置支付密码");
                                    isSetPassword = false;
                                } else {//已设置
                                    textViewPwd.setText("修改支付密码");
                                    isSetPassword = true;
                                }
                            } else {
                                GetToast.showToast(cnt, "" + jsonObject.optString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 绑定支付宝账号成功，上传告诉服务器
     */
    private void aliPaybound(String authCode, String alipayOpenId) {
        OkGo.<String>post(Urls.aliPaybound)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("app", "100005")
                .params("openId", alipayOpenId)
                .params("auth_code", authCode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                String apliAccountId = data.optString("apliAccountId");
                                ACacheUtils.putAliPayAcountId(cnt, apliAccountId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 微信登录获取code
     *
     * @param code
     */
    private void getAccessToken(String code) {
        OkGo.<String>get(Urls.WX_LOGIN_CODE)
                .tag(this)
                .params("appid", cnt.getResources().getString(R.string.weixin_app_id))
                .params("secret", cnt.getResources().getString(R.string.weixin_app_secret))
                .params("code", code)
                .params("grant_type", "authorization_code")
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                String result = response.body();
                                try {
                                    JSONObject jsonResult = new JSONObject(result);
                                    String access_token = jsonResult
                                            .getString("access_token");
                                    String openid = jsonResult.getString("openid");
                                    if (!TextUtils.isEmpty(access_token)
                                            && !TextUtils.isEmpty(openid)) {
                                        getWeixinUserInfo(access_token, openid);
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
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
     * 微信登录获取用户信息
     *
     * @param access_token
     * @param openid
     */
    private void getWeixinUserInfo(String access_token, String openid) {
        OkGo.<String>get(Urls.WX_LOGIN_USER_INFO)
                .tag(this)
                .params("access_token", access_token)
                .params("openid", openid)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                String result = response.body();
                                try {
                                    WeiXinLoginModel weiXinLoginModel = WeiXinLoginModel.parseJSON(result);
                                    String headimgurl = weiXinLoginModel.getHeadimgurl();
                                    String openid = weiXinLoginModel.getOpenid();
                                    String nickname = weiXinLoginModel.getNickname();
                                    String sex = weiXinLoginModel.getSex();
                                    String unionid = weiXinLoginModel.getUnionid();
                                    String country = weiXinLoginModel.getCountry();
                                    String city = weiXinLoginModel.getCity();
                                    String province = weiXinLoginModel.getProvince();

                                    //Log.d("LogTest","|||"+unionid+"|||"+ nickname+"|||"+headimgurl+"|||"+ openid+"|||"+country+"|||"+city+"|||"+province+"|||"+sex);
                                    check_bing(unionid, nickname, headimgurl, openid);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
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
     * 绑定微信
     *
     * @param unionid
     * @param nickName
     * @param headerUrl
     * @param openId
     */
    private void check_bing(final String unionid, final String nickName, final String headerUrl, final String openId) {
        OkGo.<String>get(Urls.WX_BOUND)
                .tag(this)
                .params("unionId", unionid)
                .params("openId", openId)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("headImgUrl", headerUrl)
                .params("nick", nickName)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("deviceToken", StringUtil.getDeviceId(MyWalletActivity.this))
                .execute(new StringDialogCallback(this, "正在绑定微信...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                boolean isSuccess = jsonObject.optBoolean("success");
                                if (isSuccess) {
                                    String flag = jsonObject.optJSONObject("data").optString("flag");
                                    if ("0".equals(flag)) {
                                        GetToast.useString(cnt, "该微信号已被其他账号绑定");
                                    } else if ("1".equals(flag)) {
                                        tvWechatIsbinding.setText("已绑定");
                                        GetToast.useString(cnt, "绑定成功");
                                        String weixinAccountId = jsonObject.optString("weixinAccountId");
                                        ACacheUtils.putWeChatAccountId(cnt, weixinAccountId);
                                    }
                                } else {
                                    String message = jsonObject.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            } catch (Exception e) {
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
     * 群组列表
     */
    private void communityList() {
        OkGo.<String>post(Urls.communityList)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data != null && data.optBoolean("success")) {
                                    listBeans = new ArrayList<>();
                                    GroupListModel groupListModel = new Gson().fromJson(response.body(), GroupListModel.class);
                                    List<GroupListModel.DataBean.ListBean> listBean = groupListModel.getData().getList();
                                    if (listBean != null && listBean.size() > 0) {
                                        for (int i = 0; i < listBean.size(); i++) {
                                            GroupListModel.DataBean.ListBean B = listBean.get(i);
                                            //是否是管理员新加 0:成员 1:管理员 2:群主
                                            if ("2".equals(B.getIsMaster())) {
                                                listBeans.add(B);
                                            }
                                        }
                                        tvGroupWalletNum.setText(listBeans.size() + "个");
                                    } else {
                                        tvGroupWalletNum.setText("0个");
                                    }

                                }
                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MerriApp.sendReq) {
            //RxToast.showToast("微信返回code："+WX_CODE);
            //通过code获取token
            getAccessToken(WX_CODE);
        }

        getIsCheckPWD();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
