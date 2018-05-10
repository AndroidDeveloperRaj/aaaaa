package com.merrichat.net.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmanage.GroupOrderManagementActivity;
import com.merrichat.net.activity.groupmarket.AddressActivity;
import com.merrichat.net.activity.grouporder.SellOrderManageAty;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.activity.message.ChatAmplificationActivity;
import com.merrichat.net.activity.my.AttentionsActivity;
import com.merrichat.net.activity.my.BonusReportAty;
import com.merrichat.net.activity.my.FansActivity;
import com.merrichat.net.activity.my.InviteToMakeMoneyAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.my.MyWorkPointsAty;
import com.merrichat.net.activity.my.PersonalInfoActivity;
import com.merrichat.net.activity.my.mywallet.MyWalletActivity;
import com.merrichat.net.activity.setting.IdentityVerificationAty;
import com.merrichat.net.activity.setting.IdentityVerificationSuccessAty;
import com.merrichat.net.activity.setting.SettingActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MyHomeModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.DialogWebView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/11/4.
 * <p>
 * 我的主页
 */

public class MineFragment extends Fragment implements OnRefreshListener {
    /**
     * 返回
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * 头像
     */
    @BindView(R.id.clv_header)
    SimpleDraweeView clvHeader;
    /**
     * 昵称
     */
    @BindView(R.id.tv_my_nickname)
    TextView tvMyNickname;
    /**
     * 编辑个人资料
     */
    @BindView(R.id.tv_edit_info)
    TextView tvEditInfo;
    /**
     * 魅力数
     */
    @BindView(R.id.tv_meili_num)
    TextView tvMeiliNum;

    /**
     * 动态数
     */
    @BindView(R.id.tv_dongtai_num)
    TextView tvDongtaiNum;

    @BindView(R.id.rl_dong_tai)
    RelativeLayout rlDongTai;
    /**
     * 关注数
     */
    @BindView(R.id.tv_guanzhu_num)
    TextView tvGuanzhuNum;

    @BindView(R.id.rl_guan_zhu)
    RelativeLayout rlGuanZhu;


    /**
     * 销售订单
     */
    @BindView(R.id.rl_sales_order)
    RelativeLayout rl_sales_order;


    /**
     * 购买订单
     */
    @BindView(R.id.rl_buy_order)
    RelativeLayout rlBuyOrder;


    /**
     * 收货地址
     */
    @BindView(R.id.rl_shipping_address)
    RelativeLayout rlShippingAddress;


    /**
     * 新加粉丝数
     */
    @BindView(R.id.round_fensi_num)
    TextView roundFensiNum;
    /**
     * 粉丝数
     */
    @BindView(R.id.tv_fensi_num)
    TextView tvFensiNum;
    @BindView(R.id.ll_fensi)
    LinearLayout llFensi;
    @BindView(R.id.rl_fen_si)
    RelativeLayout rlFenSi;
    /**
     * 迅美钱包
     */
    @BindView(R.id.rl_qianbao)
    RelativeLayout rlQianbao;
    /**
     * 邀请赚钱
     */
    @BindView(R.id.rl_yaoqing_zhuanqian)
    RelativeLayout rlYaoqingZhuanqian;
    /**
     * 奖励报表
     */
    @BindView(R.id.rl_jiangli_baobiao)
    LinearLayout rlJiangliBaobiao;
    /**
     * 我的认证状态
     */
    @BindView(R.id.tv_renzheng_status)
    TextView tvRenZhengStatus;
    /**
     * 我的认证
     */
    @BindView(R.id.rl_wode_renzheng)
    LinearLayout rlWodeRenzheng;
    /**
     * 陌生视频开关
     */
    @BindView(R.id.rl_shipin_kaiguan)
    LinearLayout rlShipinKaiguan;
    /**
     * 设置
     */
    @BindView(R.id.rl_shezhi)
    LinearLayout rlShezhi;
    /**
     * 刷新 加载
     */
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.sb_checked_shipin)
    SwitchButton sbCheckedShipin;
    @BindView(R.id.sv_scrollview)
    ScrollView svScrollview;
    @BindView(R.id.tv_none)
    TextView tvNone;
    @BindView(R.id.btn_login_friend)
    Button btnLoginFriend;
    @BindView(R.id.lin_toast)
    LinearLayout linToast;
    private MyHomeModel.DataBean myHomeModelData;
    private boolean isRefresh = false;

    private DialogWebView dialogWebView;
    private UMShareAPI umShareAPI;
    private String shareUrl;
    private String shareUrlHead = "http://gzhgr.igomot.net/weixin-shop/xunmei/redpack/red.html?";
    private String shareImage = "http://igomopub.igomot.net/nfs_data/igomo/igomoLife/2018011111111.png";
    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享成功");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            RxToast.showToast("分享取消");
        }
    };
    private String imgUrl;//头像url

    /**
     * Fragment 的构造函数。
     */
    public MineFragment() {
    }

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!UserModel.getUserModel().getIsLogin()) {
            linToast.setVisibility(View.VISIBLE);
            tvNone.setText("你还未登录请前去登录");
            return;

        } else {
            linToast.setVisibility(View.GONE);
            if (unbinder != null && tvMeiliNum != null) {
                myHomeInfo();
            }
            // 查询状态
            setMVStatus("");
            queryRealNameVerfyStatus(1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
        initTitle();
        svScrollview.smoothScrollTo(0, 0);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableLoadmore(false);
        sbCheckedShipin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //0 勿扰 1 可以视频(查询状态的时候,videoStatus传null就行)
                if (isChecked) {
                    setMVStatus("1");
                } else {
                    setMVStatus("0");
                }
            }
        });


        /**
         * 查询是否天降红包弹窗
         */
        umShareAPI = UMShareAPI.get(getActivity());
        if (UserModel.getUserModel().getIsLogin()) {
            //判断当前网络是否可用
            if (NetUtils.isNetworkAvailable(getActivity())) {
                isShowDialog();
            }
        }

    }

    /**
     * 获取红包弹出链接
     */
    private void isShowDialog() {
        OkGo.<String>get(Urls.ACTIVITY_POPUP)
                .tag(this)
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    String status = data.optJSONObject("data").optString("status");
                                    if (status.equals("1")) {//弹
                                        String url = data.optJSONObject("data").getString("url");
                                        showRedEnvelopeDialog(url);
                                    } else {//不弹

                                    }
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
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });
    }

    /**
     * 弹出天降红包
     */
    private void showRedEnvelopeDialog(String url) {
        dialogWebView = new DialogWebView(getContext(), url) {
            @Override
            public void toDialogClose() {
                dialogWebView.dismiss();
            }

            @Override
            public void toDialogShare(String activityId, String RedEnvelopeId) {
                //把红包发送给好友
                if (umShareAPI.isInstall(getActivity(), SHARE_MEDIA.WEIXIN)) {
                    shareUrl = shareUrlHead
                            + "st=" + System.currentTimeMillis()
                            + "&mid=" + UserModel.getUserModel().getMemberId()
                            + "&rid=" + String.valueOf(RedEnvelopeId)
                            + "&aid=" + String.valueOf(activityId)
                            + "&source=0";
                    ShareWeb(SHARE_MEDIA.WEIXIN);
                } else {
                    RxToast.showToast("未安装微信客户端...");
                }
            }
        };
//        dialogWebView.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialogWebView.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialogWebView.show();
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(getActivity(), shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        web.setDescription("MerriChat天降红包,领取红包及时到账，登录MerriChat App即可使用");
        web.setTitle("MerriChat给你一个大红包");
        new ShareAction(getActivity()).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        umShareAPI.release();
    }

    private void setMVStatus(final String type) {
        OkGo.<String>get(Urls.SET_MV_STATUS)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("videoStatus", type)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                    if (RxDataTool.isNullString(type)) {//查询
                                        int data = jsonObjectEx.optInt("data");
                                        if (sbCheckedShipin != null) {
                                            if (data == 1) {//0勿扰 1可以聊天
                                                sbCheckedShipin.setCheckedImmediatelyNoEvent(true);
                                            } else if (data == 0) {
                                                sbCheckedShipin.setCheckedImmediatelyNoEvent(false);
                                            }
                                        }
                                    } else {
                                        String failedInfo = jsonObjectEx.optString("data");
                                        if (!RxDataTool.isNumber(failedInfo)) {
                                            RxToast.showToast(failedInfo);
                                            noChangeStateButtonStatus();
                                            return;
                                        }
                                        if (type.equals("1")) {
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(true);
                                        } else if (type.equals("0")) {
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(false);

                                        }
                                        RxToast.showToast("修改成功！");
                                    }
                                } else {
                                    noChangeStateButtonStatus();
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
                        noChangeStateButtonStatus();
                    }
                });
    }

    /**
     * 修改失败时，不改变按钮状态
     */
    private void noChangeStateButtonStatus() {
        sbCheckedShipin.setCheckedImmediatelyNoEvent(!sbCheckedShipin.isChecked());
    }

    private void initTitle() {
        tvTitleText.setText("我的");
        ivBack.setVisibility(View.GONE);

    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_MINE_HOME) {//刷新数据
            myHomeInfo();
        } else if (myEventBusModel.REFRESH_MINE_MV) {
            setMVStatus("");

        }
    }


    /**
     * 我的主页
     */
    private void myHomeInfo() {
        OkGo.<String>get(Urls.MY_HOME)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (isRefresh) {
                            refreshLayout.finishRefresh();
                        }
                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    MyHomeModel myHomeModel = gson.fromJson(response.body(), MyHomeModel.class);
                                    if (myHomeModel.isSuccess()) {
                                        myHomeModelData = myHomeModel.getData();
                                        MyHomeModel.DataBean.InfoBean myHomeModelDataInfo = myHomeModelData.getInfo();
                                        if (tvMeiliNum != null && myHomeModelData != null) {
                                            tvMeiliNum.setText(myHomeModelData.getCharm() + "分");
                                        }
                                        tvMyNickname.setText(myHomeModelDataInfo.getNickName());
                                        UserModel userModel = UserModel.getUserModel();
                                        userModel.setRealname(myHomeModelDataInfo.getNickName());
                                        userModel.setGender(myHomeModelDataInfo.getGender() + "");
                                        UserModel.updateUserModel(userModel);
                                        imgUrl = myHomeModelDataInfo.getImgUrl();

                                        if (!RxDataTool.isNullString(imgUrl)) {
                                            clvHeader.setImageURI(imgUrl);
                                            if (RxDataTool.isNullString(UserModel.getUserModel().getImgUrl())) {
                                                UserModel userModelSet = UserModel.getUserModel();
                                                userModelSet.setImgUrl(imgUrl);
                                                UserModel.updateUserModel(userModelSet);
                                            }
                                        }
                                        tvDongtaiNum.setText(myHomeModelData.getMyDynamicNum() + "");
                                        int addFans = myHomeModelData.getAddFans();
                                        if (addFans != 0) {
                                            roundFensiNum.setVisibility(View.VISIBLE);
                                            roundFensiNum.setText("+" + addFans);
                                        } else {
                                            roundFensiNum.setVisibility(View.GONE);
                                        }
                                        tvGuanzhuNum.setText(myHomeModelData.getAttenCount() + "");
                                        tvFensiNum.setText(myHomeModelData.getFansCount() + "");
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
                        refreshLayout.finishRefresh();
                        RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });


    }


    @OnClick({R.id.clv_header, R.id.btn_login_friend, R.id.tv_edit_info, R.id.ll_person_info, R.id.rl_likeme_person, R.id.rl_qianbao, R.id.rl_yaoqing_zhuanqian,
            R.id.rl_jiangli_baobiao, R.id.rl_dong_tai, R.id.rl_guan_zhu, R.id.rl_fen_si, R.id.rl_wode_renzheng,
            R.id.rl_shipin_kaiguan, R.id.rl_shezhi, R.id.rl_sales_order, R.id.rl_buy_order, R.id.rl_shipping_address})
    public void onViewClicked(View view) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.clv_header:
                ArrayList<String> corverUrlList = new ArrayList<>();
                if (!RxDataTool.isNullString(imgUrl)) {
                    corverUrlList.add(imgUrl);
                }
                if (corverUrlList.size() == 0) {
                    return;
                }
                Intent intent = new Intent(getContext(), ChatAmplificationActivity.class);
                intent.putStringArrayListExtra("imgUrl", corverUrlList);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                break;
            case R.id.btn_login_friend://未登录
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.rl_sales_order://销售订单
                startActivity(new Intent(getContext(), SellOrderManageAty.class));
                break;
            case R.id.rl_buy_order://购买订单
                startActivity(new Intent(getContext(), GroupOrderManagementActivity.class));
                break;
            case R.id.rl_shipping_address://收货地址
                startActivity(new Intent(getContext(), AddressActivity.class));
                break;
            case R.id.tv_edit_info:
                RxActivityTool.skipActivity(getContext(), PersonalInfoActivity.class);

                break;
            case R.id.ll_person_info:
                RxActivityTool.skipActivity(getContext(), PersonalInfoActivity.class);

                break;
            case R.id.rl_likeme_person://挖矿工分
                String memberId = UserModel.getUserModel().getMemberId();
                if (RxDataTool.isNullString(memberId)) {
                    RxToast.showToast("请登录后查看！");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("memberId", memberId);
                RxActivityTool.skipActivity(getContext(), MyWorkPointsAty.class);
//                RxActivityTool.skipActivity(getContext(), WhoLikeMeAty.class, bundle);//谁喜欢我
                break;
            case R.id.rl_qianbao:
                RxActivityTool.skipActivity(getContext(), MyWalletActivity.class);
                break;
            case R.id.rl_dong_tai:

                RxActivityTool.skipActivity(getContext(), MyDynamicsAty.class);
                break;
            case R.id.rl_yaoqing_zhuanqian:
                RxActivityTool.skipActivity(getContext(), InviteToMakeMoneyAty.class);
//                startActivity(new Intent(getContext(), GroupSettingActivity.class));
                break;
            case R.id.rl_jiangli_baobiao:
                RxActivityTool.skipActivity(getContext(), BonusReportAty.class);

                break;
            case R.id.rl_wode_renzheng:
                queryRealNameVerfyStatus(-1);
                break;
            case R.id.rl_shipin_kaiguan:
                break;
            case R.id.rl_shezhi:
                RxActivityTool.skipActivity(getContext(), SettingActivity.class);

                break;
            case R.id.rl_guan_zhu:
                RxActivityTool.skipActivity(getContext(), AttentionsActivity.class);

                break;
            case R.id.rl_fen_si:
                RxActivityTool.skipActivity(getContext(), FansActivity.class);
                break;
        }
    }

    /**
     * 查询实名认证状态
     */
    private void queryRealNameVerfyStatus(final int queryFrom) {
        OkGo.<String>post(Urls.queryRealNameVerfyStatus)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("mobile", UserModel.getUserModel().getMobile())
                .params("accountId", UserModel.getUserModel().getAccountId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                //状态码0:尚未实名认证 1:已实名认证
                                int realNameStatus = jsonObject.optJSONObject("data").optInt("realNameStatus");
                                switch (realNameStatus) {
                                    case 0:
                                        if (queryFrom == 1) {
                                            tvRenZhengStatus.setText("未认证,认证后可提现");
                                        } else {
                                            RxActivityTool.skipActivity(getContext(), IdentityVerificationAty.class);
                                        }
                                        break;
                                    case 1:
                                        //金额／提现次数／最高金额／最低金额
                                        if (queryFrom == 1) {
                                            tvRenZhengStatus.setText("已认证");
                                            tvRenZhengStatus.setTextColor(getResources().getColor(R.color.ffff3d6f));
                                        } else {
                                            RxActivityTool.skipActivity(getContext(), IdentityVerificationSuccessAty.class);
                                        }
                                        break;
                                }
                            } else {
                                String error_msg = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(error_msg)) {
                                    GetToast.useString(getContext(), error_msg);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        isRefresh = true;
        myHomeInfo();
    }

}
