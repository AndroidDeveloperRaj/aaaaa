package com.merrichat.net.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.WeiXinLoginModel;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.app.MerriApp.WX_CODE;

/**
 * Created by amssy on 17/12/4.
 */

public class BoundWechatActivity extends BaseActivity {
    @BindView(R.id.choose_close)
    ImageView chooseClose;
    @BindView(R.id.btn_wechat_login)
    Button btnWechatLogin;

    public static IWXAPI api; // 微信开放平台
    private String accountId;
    private String memberId;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_wechat);
        ButterKnife.bind(this);
        regToWx();
        initGetIntent();
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent!=null){
            accountId=intent.getStringExtra("accountId");
            memberId=intent.getStringExtra("memberId");
            mobile=intent.getStringExtra("mobile");
        }
    }

    @OnClick({R.id.choose_close,R.id.btn_wechat_login})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.choose_close:
                if (LoginActivity.isLoginOrRegister) {//返回登录界面
                    setResult(RESULT_CANCELED);
                }else {//注册取消绑定微信直接跳转首页
                    RxToast.showToast("取消绑定,仍处于未登录状态");
                    RxActivityTool.skipActivity(BoundWechatActivity.this, MainActivity.class);
                }
                finish();
                break;
            case R.id.btn_wechat_login:
                //判断是否安装微信
                if (StringUtil.isWeixinAvilible(BoundWechatActivity.this)) {
                    if (NetUtils.isNetworkAvailable(BoundWechatActivity.this)) {
                        weixinLogin();
                    }else {
                        RxToast.showToast("网络不可用,请检查网络连接");
                    }
                }else {
                    RxToast.showToast("您未安装微信...");
                }
                break;
        }
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

    /**
     * 调起微信
     *
     * @return void 返回类型
     * @throws
     * @Title: weixinLogin
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void weixinLogin() {
        // TODO Auto-generated method stub
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        MerriApp.sendReq = api.sendReq(req);
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
                .execute(new StringDialogCallback(this) {
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
                .params("accountId", accountId)
                .params("headImgUrl", headerUrl)
                .params("nick", nickName)
                .params("memberId", memberId)
                .params("mobile", mobile)
                .params("deviceToken", StringUtil.getDeviceId(BoundWechatActivity.this))
                .execute(new StringDialogCallback(this, "正在绑定微信...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                boolean isSuccess = jsonObject.optBoolean("success");
                                if (isSuccess) {
                                    if (jsonObject.optJSONObject("data").optString("flag").equals("0")){
                                        RxToast.showToast("该微信号已被绑定");
                                    }else if (jsonObject.optJSONObject("data").optString("flag").equals("1")){
                                        RxToast.showToast("绑定成功");
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            } catch (Exception e) {

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
        //RxToast.showToast(""+sendReq);
        if (MerriApp.sendReq) {
            //RxToast.showToast("微信返回code："+WX_CODE);
            //通过code获取token
            getAccessToken(WX_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MerriApp.sendReq = false;
    }
}
