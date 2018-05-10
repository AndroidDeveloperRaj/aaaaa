package com.merrichat.net.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Allen Cheng on 2018/4/28.
 * <p>
 * 邀请赚钱
 */
public class InviteToMakeMoneyAty extends MerriActionBarActivity {
    @BindView(R.id.sg_tablayout)
    SegmentTabLayout sgTablayout;
    @BindView(R.id.vp_view_pager)
    ViewPager vpViewPager;
    @BindView(R.id.tv_toshare_makemoney)
    TextView tvToshareMakemoney;
    private String[] mTitles = {"赚钱攻略", "我的徒弟"};
    private ArrayList<Fragment> fragmentsList;
    private MyPagerAdapter myPagerAdapter;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviteto_makemoney);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setLeftBack();
        setTitle("收徒赚钱");
        sgTablayout.setTabData(mTitles);
        fragmentsList = new ArrayList<>();
        fragmentsList.add(new MakeMoneyStrategyFragment());
        fragmentsList.add(new InviteReportFragment());
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vpViewPager.setAdapter(myPagerAdapter);
        vpViewPager.setCurrentItem(0);
        vpViewPager.setOffscreenPageLimit(2);
        sgTablayout.setCurrentTab(0);
        sgTablayout.setOnTabSelectListener(new OnTabSelectListener() {//Tab 监听
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        vpViewPager.setCurrentItem(0, true);
                        break;
                    case 1:
                        vpViewPager.setCurrentItem(1, true);

                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        vpViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                sgTablayout.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @OnClick(R.id.tv_toshare_makemoney)
    public void onViewClicked() {
        if (StringUtil.isWeixinAvilible(cnt)) {
            getPromoQRcode();
        } else {
            RxToast.showToast("你还未安装微信~");
        }
    }

    /**
     * 获取分享二维码
     */
    private void getPromoQRcode() {
        OkGo.<String>post(Urls.getPromoQRcode)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", ConstantsPath.share_to_invite_source)//分享来源 source为0:天降红包，抢红包页面 1:答题红包页面 2:秀吧分享 3:邀请 4:天降红包弹窗 6:拼团
                .params("activityId", "")
                .params("redParcelId", "")
                .params("articleId", "")
                .params("atlMemberId", "")
                .params("orderId", "")
                .execute(new StringDialogCallback(InviteToMakeMoneyAty.this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                String url = data.optString("url");
                                ShareWeb(url, shareMedia);
                            } else {
                                RxToast.showToast("分享失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void ShareWeb(String url, SHARE_MEDIA share_media) {
        String memeberId = UserModel.getUserModel().getMemberId();
        UMImage thumb = new UMImage(cnt, ConstantsPath.share_to_invite_icon);
        UMWeb web = new UMWeb(url);
        web.setThumb(thumb);
        web.setDescription(cnt.getString(R.string.share_content));
        web.setTitle(cnt.getString(R.string.share_title));
        new ShareAction((Activity) cnt).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

        }

        @Override
        public Fragment getItem(int i) {
            return fragmentsList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }
    }
}
