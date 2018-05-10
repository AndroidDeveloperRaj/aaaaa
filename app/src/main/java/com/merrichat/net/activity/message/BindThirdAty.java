package com.merrichat.net.activity.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.my.mywallet.MyWalletActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.WeiXinLoginModel;
import com.merrichat.net.utils.ACacheUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.app.MerriApp.WX_CODE;

/**
 * Created by xly on 2018/5/3.
 * 绑定支付宝账户/微信账户
 */
public class BindThirdAty extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_title_text)
    TextView tvTitleText;


    @BindView(R.id.iv_third)
    ImageView iv_third;
    @BindView(R.id.tv_tag)
    TextView tv_tag;
    @BindView(R.id.tv_bind)
    TextView tv_bind;
    public static IWXAPI api; // 微信开放平台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_third);
        ButterKnife.bind(this);
        regToWx();
        initView();
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

    private void initView() {
        tvTitleText.setText("绑定微信账户");
        getWeixinAccountId();
    }

    @OnClick({R.id.iv_back, R.id.tv_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_bind:
                UMShareAPI umShareAPI = null;
                umShareAPI = UMShareAPI.get(cnt);
                if (!umShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                    RxToast.showToast("未安装微信客户端...");
                    return;
                }else {
                    bindWeChat();
                }
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
                                iv_third.setImageResource(R.mipmap.wechat_login);
                                tv_tag.setText("当前您已绑定微信");
                                tv_bind.setVisibility(View.GONE);
                            } else {
                                iv_third.setImageResource(R.mipmap.wechat_login);
                                tv_tag.setText("您尚未绑定微信" + "\n微信账户将作为您在交易安全中的认证信息");
                                tv_bind.setVisibility(View.VISIBLE);
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
                .params("deviceToken", StringUtil.getDeviceId(cnt))
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
                                        iv_third.setImageResource(R.mipmap.wechat_login);
                                        tv_tag.setText("当前您已绑定微信");
                                        tv_bind.setVisibility(View.GONE);
                                        GetToast.useString(cnt, "绑定成功");
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.REFRESH_NOTICE_ATY = true;
                                        EventBus.getDefault().post(myEventBusModel);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (MerriApp.sendReq) {
            //RxToast.showToast("微信返回code："+WX_CODE);
            //通过code获取token
            getAccessToken(WX_CODE);
        }
    }
}
