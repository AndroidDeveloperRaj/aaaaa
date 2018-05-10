package com.merrichat.net.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.AddFriendsActivity;
import com.merrichat.net.activity.message.ChatFragment;
import com.merrichat.net.activity.message.ContactPersonFragement;
import com.merrichat.net.activity.message.SelectFriendAty;
import com.merrichat.net.adapter.MyShowPagerAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.ConfirmPopWindow;
import com.merrichat.net.view.DialogWebView;
import com.merrichat.net.view.NoScrollViewPager;
import com.merrichat.net.view.PagerSlidingTabStrip;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by amssy on 17/11/4.
 * 消息
 */

public class MessageFragment extends BaseFragment {

    public static final int activityId = MiscUtil.getFragmentId();
    @BindView(R.id.page_tabs)
    PagerSlidingTabStrip pageTabs;

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @BindView(R.id.iv_right)
    ImageView ivRight;

    Unbinder unbinder;

    private View view;
    private List<Fragment> fragmentList;
    /**
     * 消息(聊天)
     */
    private ChatFragment chatFragment;
    /**
     * 联系人
     */
    private ContactPersonFragement contactPersonFragement;
    private MyShowPagerAdapter myShowPagerAdapter;
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

    /**
     * Fragment 的构造函数。
     */
    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(savedInstanceState);
        return view;
    }

    /**
     * 初始化数据
     */
    private void initView(Bundle savedInstanceState) {
        fragmentList = new ArrayList<>();

        if (savedInstanceState != null) {
            chatFragment = (ChatFragment) getChildFragmentManager().getFragment(savedInstanceState, "chatFragment");
        } else {
            chatFragment = ChatFragment.newInstance();
        }

        if (savedInstanceState != null) {
            contactPersonFragement = (ContactPersonFragement) getChildFragmentManager().getFragment(savedInstanceState, "contactPersonFragement");
        } else {
            contactPersonFragement = ContactPersonFragement.newInstance();
        }

        fragmentList.add(chatFragment);
        fragmentList.add(contactPersonFragement);

        myShowPagerAdapter = new MyShowPagerAdapter(getChildFragmentManager(), new String[]{" 消息 ", "联系人"}, fragmentList);
        viewPager.setAdapter(myShowPagerAdapter);
        pageTabs.setViewPager(viewPager);
        pageTabs.setSmoothScrollWhenClickTab(false);
        viewPager.setScanScroll(false);
        viewPager.setCurrentItem(0,false);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0://消息
                        break;
                    case 1://联系人
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        dialogWebView = new DialogWebView(cnt, url) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.iv_right)
    public void onViewClicked() {
        if (!UserModel.getUserModel().getIsLogin()) {
            RxToast.showToast("您还没有登录，请先登录");
            return;
        }
        ConfirmPopWindow confirmPopWindow = new ConfirmPopWindow(getActivity());
        confirmPopWindow.showAtBottom(ivRight);
        confirmPopWindow.setOnTopItemClick(new ConfirmPopWindow.OnTopItemClick() {
            @Override
            public void itemClick(int position) {
                if (position == 0) {
                    startActivityForResult(new Intent(cnt, SelectFriendAty.class).putExtra("activityId", activityId), 10);
                } else if (position == 1) {
                    startActivity(new Intent(cnt, AddFriendsActivity.class));
                }
            }
        });

    }

    /**
     * 当活动被回收时，存储当前的状态。
     *
     * @param outState 状态数据。
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chatFragment != null && chatFragment.isAdded()) {
            getChildFragmentManager().putFragment(outState, "chatFragment", chatFragment);
        }
        if (contactPersonFragement != null && contactPersonFragement.isAdded()) {
            getChildFragmentManager().putFragment(outState, "contactPersonFragement", contactPersonFragement);
        }
    }


    //
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
//            System.out.println("不可见");
        } else {//消息fragmen可见时
//            System.out.println("当前可见");
            if (UserModel.getUserModel().getIsLogin()) {
                if (chatFragment != null) {
                    chatFragment.offline();
                }
                if (viewPager.getCurrentItem() == 0) {
                    if (chatFragment != null) {
                        chatFragment.loginSuessDo();
                    }
                } else if (viewPager.getCurrentItem() == 1) {
                    if (contactPersonFragement != null) {
                        contactPersonFragement.loginSuessDo();
                    }
                }
            }
        }
    }
}
