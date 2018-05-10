package com.merrichat.net.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/12/2.
 * 添加朋友
 */

public class AddFriendsFragment extends BaseFragment {
    @BindView(R.id.lay_add_contact)
    LinearLayout layAddContact;
    //    @BindView(R.id.lay_add_red_package)
//    LinearLayout layAddRedPackage;
    @BindView(R.id.lay_add_wechat)
    LinearLayout layAddWechat;
    Unbinder unbinder1;
    //    @BindView(R.id.lay_add_qr_code)
//    LinearLayout layAddQrCode;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台
    private View view;
    private Unbinder unbinder;
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

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_add_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.lay_search, R.id.lay_add_contact, R.id.lay_add_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lay_search:
                RxActivityTool.skipActivity(getContext(), SearchAllPersonsAty.class);
                break;
            case R.id.lay_add_contact://通讯录
                startActivity(new Intent(cnt, AddressBookFriendsActivity.class));
                break;
//            case R.id.lay_add_red_package://发红包邀请
//                break;
            case R.id.lay_add_wechat://给微信好友发名片邀请
                if (StringUtil.isWeixinAvilible(cnt)) {
                    getPromoQRcode();
                } else {
                    RxToast.showToast("你还未安装微信~");
                }
                break;
//            case R.id.lay_add_qr_code://扫我的二维码
//                break;
        }
    }

    /**
     * 获取分享二维码
     */
    private void getPromoQRcode() {
        OkGo.<String>post(Urls.getPromoQRcode)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "3")//分享来源 source为0:天降红包，抢红包页面 1:答题红包页面 2:秀吧分享 3:邀请 4:天降红包弹窗 6:拼团
                .execute(new StringDialogCallback(getActivity()) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String url = data.optString("url");
                                shareMedia = SHARE_MEDIA.WEIXIN;
                                ShareWeb(shareMedia, url);
                            } else {
                                RxToast.showToast("分享失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void ShareWeb(SHARE_MEDIA share_media, String url) {
        UMImage thumb = new UMImage(cnt, ConstantsPath.share_to_invite_icon);
        UMWeb web = new UMWeb(url);
        web.setThumb(thumb);
        web.setDescription(cnt.getString(R.string.share_content));
        web.setTitle(cnt.getString(R.string.share_title));
        new ShareAction((Activity) cnt).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
