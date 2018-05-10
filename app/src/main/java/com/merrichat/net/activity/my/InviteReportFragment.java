package com.merrichat.net.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.adapter.ShareToMakeMoneyAdapter;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.BaseFragment;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ShareToMakeMoneyModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.JSONObjectEx;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Allen Cheng on 2018/4/28.
 * <p>
 * 邀请记录
 */
public class InviteReportFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.rl_recyclerview)
    RecyclerView rlRecyclerview;
    @BindView(R.id.tv_toshare_makemoney)
    TextView tvToshareMakemoney;
    /**
     * 头部广告
     */
    @BindView(R.id.iv_ad_header)
    ImageView ivAdHeader;
    /**
     * 无数据 加载页
     */
    @BindView(R.id.tv_no_text)
    TextView tvNoText;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    Unbinder unbinder;
    private ArrayList<ShareToMakeMoneyModel.DataBeanX.DataBean> makeMoneyList;
    private ShareToMakeMoneyAdapter shareToMakeMoneyAdapter;
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
    private ImageView ivAdImage;
    private UMShareAPI umShareAPI;


    private void initView() {
        umShareAPI = UMShareAPI.get(getContext());
        rlTitle.setVisibility(View.GONE);
        tvToshareMakemoney.setVisibility(View.GONE);
        makeMoneyList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rlRecyclerview.setLayoutManager(layoutManager);
        shareToMakeMoneyAdapter = new ShareToMakeMoneyAdapter(R.layout.item_shareto_makemoney, makeMoneyList);
        rlRecyclerview.setAdapter(shareToMakeMoneyAdapter);
//        shareToMakeMoneyAdapter.addHeaderView(getHeaderView(), -1, LinearLayout.VERTICAL);
        shareToMakeMoneyAdapter.setOnItemChildClickListener(this);

        myPeerInfo();
    }

    private View getHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.item_shareto_makemoney_header, (ViewGroup) rlRecyclerview.getParent(), false);
        ivAdImage = (ImageView) view.findViewById(R.id.iv_ad_image);
        view.setOnClickListener(headerViewOnClickListener());
        return view;
    }

    private View.OnClickListener headerViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        };
    }

    /**
     * 邀请赚钱-记录
     */
    private void myPeerInfo() {
        OkGo.<String>get(Urls.MY_PEER)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            Gson gson = new Gson();
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    ShareToMakeMoneyModel shareToMakeMoneyModel = gson.fromJson(response.body(), ShareToMakeMoneyModel.class);
                                    if (shareToMakeMoneyModel.isSuccess()) {
                                        ShareToMakeMoneyModel.DataBeanX dataBeanX = shareToMakeMoneyModel.getData();
                                        List<ShareToMakeMoneyModel.DataBeanX.DataBean> dataBeans = dataBeanX.getData();
                                        makeMoneyList.addAll(dataBeans);
                                        if (makeMoneyList.size() > 0) {
                                            tvNoText.setVisibility(View.GONE);
                                        } else {
                                            tvNoText.setVisibility(View.VISIBLE);
                                        }
                                        shareToMakeMoneyAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    RxToast.showToast(jsonObjectEx.optString("message"));
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


    @OnClick({R.id.tv_toshare_makemoney, R.id.iv_ad_header, R.id.tv_no_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_toshare_makemoney:
                if (StringUtil.isWeixinAvilible(cnt)) {
                    getPromoQRcode();
                } else {
                    RxToast.showToast("你还未安装微信~");
                }
                break;
            case R.id.iv_ad_header://赚钱攻略
                RxActivityTool.skipActivity(getContext(), ShareToMakeMoneyStrategyAty.class);
                break;
        }
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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.cv_header:
                Bundle bundle = new Bundle();
                bundle.putLong("hisMemberId", makeMoneyList.get(position).getToMemberId());
                bundle.putString("hisImgUrl", makeMoneyList.get(position).getImgUrl());
                bundle.putString("hisNickName", makeMoneyList.get(position).getNickName());
                RxActivityTool.skipActivity(getContext(), HisYingJiAty.class, bundle);
                break;
            case R.id.sb_add_friend:
                addGoodFriends(position);
                break;
        }
    }

    /**
     * 添加好友——接口
     *
     * @param position
     */
    private void addGoodFriends(final int position) {
        OkGo.<String>post(Urls.addGoodFriends)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", makeMoneyList.get(position).getToMemberId())
                .params("toMemberName", makeMoneyList.get(position).getNickName())
                .params("toMemberUrl", makeMoneyList.get(position).getImgUrl())
                .params("source", "0")
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                RxToast.showToast(data.optString("message"));
                                makeMoneyList.get(position).setStatus(1);
                                shareToMakeMoneyAdapter.notifyItemChanged(position + 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 获取分享二维码
     */
    private void getPromoQRcode() {
        OkGo.<String>post(Urls.getPromoQRcode)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "3")//分享来源 source为0:天降红包，抢红包页面 1:答题红包页面 2:秀吧分享 3:邀请 4:天降红包弹窗 6:拼团
                .params("activityId", "")
                .params("redParcelId", "")
                .params("articleId", "")
                .params("atlMemberId", "")
                .params("orderId", "")
                .execute(new StringDialogCallback() {
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

    @Override
    public View setContentViewResId(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.activity_shareto_makemoney, container, false);
        return inflateView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
