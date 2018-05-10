package com.merrichat.net.activity.his;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.MediaVariations;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.MainActivity;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.circlefriend.CircleVideoActivity;
import com.merrichat.net.activity.circlefriend.TuWenAlbumAty;
import com.merrichat.net.activity.meiyu.VideoChatActivity;
import com.merrichat.net.activity.meiyu.VoiceChatActivity;
import com.merrichat.net.activity.message.ChatAmplificationActivity;
import com.merrichat.net.activity.message.MessageVideoCallAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.my.WhoLikeMeAty;
import com.merrichat.net.adapter.HisXiHuanAdapter;
import com.merrichat.net.adapter.HisYingJiAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.HisHomeModel;
import com.merrichat.net.model.QueryWalletInfoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.ConstantsPath;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.HorizontalSpaceItemDecorations;
import com.merrichat.net.view.NoScrollRecyclerView;
import com.merrichat.net.view.SpaceItemDecorationsOfHisHome;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;

import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_INFO_KEY;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_TYPE_KEY;

/**
 * Created by AMSSY1 on 2017/11/16.
 * <p>
 * Ta的影集
 */

public class HisYingJiAty extends BaseActivity implements HisYingJiAdapter.DianZanOnCheckListener, OnLoadmoreListener, OnRefreshListener {
    public final static int activityId = MiscUtil.getActivityId();
    /**
     * 视频聊天
     */
    private final int VIDEO_CHAT = 0x100;
    /**
     * 音频聊天
     */
    private final int AUDIO_CHAT = 0x200;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.clv_header)
    SimpleDraweeView clvHeader;
    @BindView(R.id.tv_my_nickname)
    TextView tvMyNickname;
    @BindView(R.id.tv_edit_info)
    TextView tvEditInfo;
    @BindView(R.id.tv_yuanfen_num)
    TextView tvYuanfenNum;
    @BindView(R.id.tv_jianjie)
    TextView tvJianjie;
    @BindView(R.id.rv_receclerView_xihuan)
    RecyclerView rvRececlerViewXihuan;
    @BindView(R.id.iv_xihuan_next)
    ImageView ivXihuanNext;
    @BindView(R.id.rv_receclerView_yingji)
    NoScrollRecyclerView rvRececlerViewYingji;
    @BindView(R.id.iv_faxiaoxi)
    ImageView ivFaxiaoxi;
    @BindView(R.id.ll_shipin_dianhua)
    LinearLayout llShipinDianhua;
    @BindView(R.id.iv_songliwu)
    ImageView ivSongliwu;
    @BindView(R.id.tv_nian_ling)
    TextView tvNianLing;
    @BindView(R.id.tv_sheng_gao)
    TextView tvShengGao;
    @BindView(R.id.tv_zhuan_ye)
    TextView tvZhuanYe;
    @BindView(R.id.tv_xue_li)
    TextView tvXueLi;
    @BindView(R.id.tv_add_attend)
    TextView tvAddAttend;
    @BindView(R.id.tv_xing_zuo)
    TextView tvXingZuo;
    @BindView(R.id.tv_mei_li)
    TextView tvMeiLi;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    /**
     * 个性签名布局
     */
    @BindView(R.id.ll_jianjie)
    LinearLayout llJianjie;
    /**
     * 加关注 取消关注布局
     */
    @BindView(R.id.ll_attend)
    LinearLayout llAttend;
    @BindView(R.id.tv_his_funs_num)
    TextView tvHisFunsNum;
    /**
     * 加好友
     */
    @BindView(R.id.tv_add_friends)
    TextView tvAddFriends;
    @BindView(R.id.tv_his_attention_num)
    TextView tvHisAttentionNum;
    private ArrayList<HisHomeModel.DataBean.MovieListBean> yingJiList;
    private HisYingJiAdapter hisYingJiAdapter;
    private HisXiHuanAdapter hisXiHuanAdapter;
    private String memberId = "1234567";
    private List<HisHomeModel.DataBean.FavorListBean> xiHuanList;
    private HisHomeModel.DataBean hisHomeModelData;
    private String hisMemberId;
    private int fromId;
    private String hisImgUrl;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    loadThumb(clvHeader, ImageRequest.RequestLevel.DISK_CACHE);
                    loadThumb(clvHeader, ImageRequest.RequestLevel.FULL_FETCH);
                    break;
                case 1:
                    loadThumb(clvHeader, ImageRequest.RequestLevel.DISK_CACHE);
                    break;
            }
        }
    };
    private String hisNickName;
    private int pageSize = 15;
    private int currentPage = 1;
    private int REFRESHORLOADMORE = -1;// 5 刷新 6 加载更多
    private int SPAN_NUM = 3;
    private SpaceItemDecorationsOfHisHome spaceItemDecoration;
    private int clickPosition = 0;
    private boolean isFriend = false;
    private HisHomeModel.DataBean.HisMemberInfoBean hisMemberInfo;
    private int SPACE_NUM = 40;
    private String friendStatus;//0 没有邀请记录 1 等待对方同意 2 好友
    private int atteStatus = -1;//关注 0 /取消关注 1 (必填)
    private String giftBalance = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_yingji);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_HIS_HOME) {//刷新数据
            hisAlbum();
        }
    }

    private void initView() {
        if (UserModel.getUserModel().getIsLogin()) {
            memberId = UserModel.getUserModel().getMemberId();
        } else {
            memberId = "0";
        }
        hisMemberId = getIntent().getLongExtra("hisMemberId", -1) + "";
        hisImgUrl = getIntent().getStringExtra("hisImgUrl");
        hisNickName = getIntent().getStringExtra("hisNickName");
        fromId = getIntent().getIntExtra("fromId", -1);
        initTitle();
        initData();
    }

    private void initData() {
        yingJiList = new ArrayList<>();
        xiHuanList = new ArrayList<>();

        hisAlbum();
        initYingJiRecyclerview();
        initXiHuanRecyclerview();
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);


    }

    private void hisAlbum() {
        OkGo.<String>get(Urls.hisAlbum)//
                .tag(this)//
                .params("memberId", memberId)
                .params("hisMemberId", hisMemberId)
                .params("pageSize", pageSize)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                xiHuanList.clear();
                                if (REFRESHORLOADMORE == 5) {
                                    yingJiList.clear();
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        RxToast.showToast(data.optString("info"));
                                        return;
                                    }
                                    Gson gson = new Gson();
                                    HisHomeModel hisHomeModel = gson.fromJson(response.body(), HisHomeModel.class);
                                    if (hisHomeModel != null) {
                                        hisHomeModelData = hisHomeModel.getData();
                                        hisMemberInfo = hisHomeModelData.getHisMemberInfo();
                                        isFriend = hisHomeModelData.getIsFriend().equals("2") ? true : false;
                                        friendStatus = hisHomeModelData.getFriendStatus();//0 没有邀请记录 1 等待对方同意 2 好友
                                        atteStatus = hisHomeModelData.isAttentionStatus() ? 1 : 0;//关注 0 /取消关注 1 (必填)
                                        String gender = hisMemberInfo.getGender();
                                        String birthday = hisMemberInfo.getBirthday();
                                        String age = hisMemberInfo.getAge();
                                        String constellation = hisMemberInfo.getConstellation();
                                        hisImgUrl = hisMemberInfo.getImgUrl();
                                        hisNickName = hisMemberInfo.getNickName();
                                        String height = hisMemberInfo.getHeight() + "";
                                        String occupation = hisMemberInfo.getOccupation();
                                        String eduBackGround = hisMemberInfo.getEduBackGround();
                                        String hisFans = hisHomeModelData.getHisFans();
                                        String hisLike = hisHomeModelData.getHisLike();
                                        List<HisHomeModel.DataBean.MovieListBean> movieList = hisHomeModelData.getMovieList();
                                        List<HisHomeModel.DataBean.FavorListBean> favorList = hisHomeModelData.getFavorList();
                                       /* if ("2".equals(friendStatus)) {//是否是好友的状态
                                            tvAddFriends.setVisibility(View.GONE);
                                        } else if ("1".equals(friendStatus)) {
                                            tvAddFriends.setVisibility(View.VISIBLE);
                                            tvAddFriends.setText("等待同意");
                                        } else if ("0".equals(friendStatus)) {
                                            tvAddFriends.setVisibility(View.VISIBLE);
                                            tvAddFriends.setText("加好友");

                                        }*/
                                        if (atteStatus == 1) {//是否已关注
                                            llAttend.setSelected(true);
                                            tvAddAttend.setText("已关注");
                                        } else if (atteStatus == 0) {
                                            llAttend.setSelected(false);
                                            tvAddAttend.setText("加关注");
                                        }
                                        if (favorList != null && favorList.size() > 0) {
                                            xiHuanList.addAll(favorList);
                                        }
                                        if (movieList != null) {
                                            yingJiList.addAll(movieList);
                                        }

                                        if (!RxDataTool.isNullString(gender)) {//男女及年龄
                                            tvNianLing.setVisibility(View.VISIBLE);
                                            if (gender.equals("1")) {
                                                tvNianLing.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.zhuye_nan2x), null, null, null);

                                            } else if (gender.equals("2")) {
                                                tvNianLing.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.zhuye_nv), null, null, null);

                                            }
                                            tvNianLing.setText(age == null ? "0" : age);
                                        }
                                        if (!RxDataTool.isNullString(constellation)) {
                                            tvXingZuo.setVisibility(View.VISIBLE);
                                            tvXingZuo.setText(constellation);
                                        }
                                        if (!RxDataTool.isNullString(height) && !height.equals("0")) {
                                            tvShengGao.setVisibility(View.VISIBLE);
                                            tvShengGao.setText(height + "cm");
                                        }
                                        if (!RxDataTool.isNullString(occupation)) {
                                            tvZhuanYe.setVisibility(View.VISIBLE);
                                            tvZhuanYe.setText(occupation);
                                        }
                                        if (!RxDataTool.isNullString(eduBackGround)) {
                                            tvXueLi.setText(eduBackGround);
                                            tvXueLi.setVisibility(View.VISIBLE);
                                        }
//                                        if (!RxDataTool.isNullString(age)) {
//                                            tvNianLing.setText(age);
//                                            tvNianLing.setVisibility(View.VISIBLE);
//                                        }
//                                        if (!RxDataTool.isNullString(constellation)) {
//                                            tvXingZuo.setText(constellation);
//                                            tvXingZuo.setVisibility(View.VISIBLE);
//                                        }
                                        if (!RxDataTool.isNullString(hisImgUrl)) {

                                            clvHeader.setImageURI(hisImgUrl);
//                                            handler.sendEmptyMessage(0);

                                        }

                                        tvYuanfenNum.setText(hisHomeModelData.getKarmaValue());
                                        tvMeiLi.setText(hisHomeModelData.getCharm() + "分");
                                        tvHisFunsNum.setText(hisFans);
                                        tvHisAttentionNum.setText(hisLike);
                                        tvMyNickname.setText(hisNickName);
                                        String signature = hisMemberInfo.getSignature();
                                        if (RxDataTool.isNullString(signature)) {
                                            llJianjie.setVisibility(View.GONE);
                                        } else {
                                            llJianjie.setVisibility(View.VISIBLE);
                                            tvJianjie.setText(signature);
                                        }
                                        hisXiHuanAdapter.notifyDataSetChanged();
                                        hisYingJiAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    RxToast.showToast("请求失败，请重试！");
                                }
                            } catch (JSONException e) {
                                if (REFRESHORLOADMORE == 5) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
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

    private void loadThumb(
            SimpleDraweeView draweeView,
            ImageRequest.RequestLevel requestLevel) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(String.format(hisImgUrl, "full", "jpg")))
                .setMediaVariations(MediaVariations.newBuilderForMediaId(ConstantsPath.MEDIA_ID)
                        .setForceRequestForSpecifiedUri(true)
                        .build())
                .setLowestPermittedRequestLevel(requestLevel)
                .setResizeOptions(new ResizeOptions(draweeView.getWidth(), draweeView.getHeight()))
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .build();
        draweeView.setController(controller);
    }

    private void loadMainImage() {
        // Request a non-existent image to force fallback to the variations
        Uri uri = Uri.parse(String.format(hisImgUrl, "full", "jpg"));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setMediaVariations(MediaVariations.forMediaId(ConstantsPath.MEDIA_ID))
                .setResizeOptions(new ResizeOptions(
                        clvHeader.getWidth(),
                        clvHeader.getHeight()))
                .build();
        setDraweeControllerForRequest(request);
    }

    private void setDraweeControllerForRequest(ImageRequest imageRequest) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(clvHeader.getController())
                .setRetainImageOnFailure(true)
                .build();
        clvHeader.setController(controller);
    }


    /**
     * 单独查询是否是勿扰模式
     */
    private void queryVideoStatus() {
        OkGo.<String>get(Urls.queryVideoStatus)//
                .tag(this)//
                .params("memberId", memberId)
                .params("hisMemberId", hisMemberId)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data != null && data.optBoolean("success")) {
                                        if (isFriend) {
                                            PrefAppStore.setIsFriend(cnt, 1);
                                        } else {
                                            PrefAppStore.setIsFriend(cnt, 0);
                                        }
                                        startActivityForResult(new Intent(cnt, VideoChatActivity.class)
                                                .putExtra(CALL_VIDEO_TYPE_KEY, MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE)
                                                .putExtra("toMemberId", hisMemberId)
                                                .putExtra("toHeadImgUrl", hisMemberInfo.getImgUrl())
                                                .putExtra("toMemberName", hisNickName)
                                                .putExtra("isFriend", isFriend)
                                                .putExtra("callType", "1"), VIDEO_CHAT);


                                    } else {
                                        RxToast.showToast(data.optString("message"));
                                    }

                                } else {
                                    RxToast.showToast("无法进行视频聊天,请重试!");
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
     * 获取当前账号的美币余额
     */
    private void getWalletInfo() {
        ApiManager.getApiManager().getService(WebApiService.class).queryWalletInfo(UserModel.getUserModel().getAccountId(), "0", UserModel.getUserModel().getMemberId())
                .subscribeOn(Schedulers.io()) //调用观察者
                .observeOn(AndroidSchedulers.mainThread())  //执行完毕后返回到主线程
                .subscribe(new BaseSubscribe<QueryWalletInfoModel>() {  //观察者回掉参数
                    @Override
                    public void onNext(QueryWalletInfoModel queryWalletInfoModel) {
                        if (queryWalletInfoModel.success) {
                            giftBalance = queryWalletInfoModel.data.giftBalance;
                            PrefAppStore.setCashBalance(cnt, giftBalance);
                            if (Double.parseDouble(giftBalance) >= 0) {
                                queryVideoStatus();
                            } else {
                                RxToast.showToast("当前余额不足，无法视频聊天！");
                            }
                        } else {
                            Toast.makeText(cnt, queryWalletInfoModel.error_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(cnt, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void initXiHuanRecyclerview() {
        HorizontalSpaceItemDecorations xiHuanItemDecorations = new HorizontalSpaceItemDecorations(30, 30, 0, 30);
        rvRececlerViewXihuan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRececlerViewXihuan.addItemDecoration(xiHuanItemDecorations);
        hisXiHuanAdapter = new HisXiHuanAdapter(R.layout.item_his_hearder, xiHuanList);
        rvRececlerViewXihuan.setAdapter(hisXiHuanAdapter);
    }

    private void initYingJiRecyclerview() {
        spaceItemDecoration = new SpaceItemDecorationsOfHisHome(SPACE_NUM, SPAN_NUM);
        rvRececlerViewYingji.setNestedScrollingEnabled(false);
        rvRececlerViewYingji.addItemDecoration(spaceItemDecoration);
        rvRececlerViewYingji.setLayoutManager(new GridLayoutManager(HisYingJiAty.this, 3));
        hisYingJiAdapter = new HisYingJiAdapter(R.layout.item_his_yingji, yingJiList);
        hisYingJiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                clickPosition = position;
                HisHomeModel.DataBean.MovieListBean movieListBean = yingJiList.get(position);

                if (movieListBean.getFlag() == 1) {//1照片 2视频
                    Intent intent = new Intent(HisYingJiAty.this, TuWenAlbumAty.class);
                    intent.putExtra("toMemberId", movieListBean.getMemberId() + "");
                    intent.putExtra("contentId", movieListBean.getId() + "");
                    intent.putExtra("activityId", activityId + "");
                    startActivityForResult(intent, MainActivity.activityId);
                } else {
                    Intent intent = new Intent(HisYingJiAty.this, CircleVideoActivity.class);
                    intent.putExtra("toMemberId", movieListBean.getMemberId() + "");
                    intent.putExtra("contentId", movieListBean.getId() + "");
                    intent.putExtra("activityId", activityId + "");
                    startActivityForResult(intent, MainActivity.activityId);
                }
            }
        });
        hisYingJiAdapter.setDianZanOnCheckListener(this);
        rvRececlerViewYingji.setAdapter(hisYingJiAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.activityId) {
            if (data != null) {
                int likeCounts = data.getIntExtra("likeCounts", 0);
                int commentCounts = data.getIntExtra("commentCounts", 0);
                int isDianZan = data.getIntExtra("isDianZan", 0);//是否喜欢 0:不喜欢 1:喜欢
                if (yingJiList != null && yingJiList.size() > 0) {
                    yingJiList.get(clickPosition).setIsLike(isDianZan);
                    yingJiList.get(clickPosition).setLikeCounts(likeCounts);
                    yingJiList.get(clickPosition).setCommentCounts(commentCounts + "");
                    hisYingJiAdapter.notifyItemChanged(clickPosition);
                }
            }
        }
        switch (requestCode) {
            case VIDEO_CHAT:
                getCheckConnectionState();
                break;

            case AUDIO_CHAT:
                getCheckConnectionState();
                break;
        }

    }

    private void initTitle() {
        tvTitleText.setText("Ta的主页");
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.tade_gengduo3x);

    }

    /**
     * 添加好友——接口
     */
    private void addGoodFriends() {
        OkGo.<String>post(Urls.addGoodFriends)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", hisMemberId)
                .params("toMemberName", hisNickName)
                .params("toMemberUrl", hisImgUrl)
                .params("source", "0")
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 try {
                                     JSONObject jsonObject = new JSONObject(response.body());
                                     JSONObject data = jsonObject.optJSONObject("data");
                                     if (jsonObject.optBoolean("success")) {
                                         RxToast.showToast(data.optString("message"));
                                         tvAddFriends.setText("等待同意");
                                         friendStatus = "1";
                                     } else {
                                         RxToast.showToast("添加好友失败，请重试！");
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
                         }
                );
    }

    /**
     * 查询是否有好友请求
     *
     * @param viewId 控件ID
     */
    private void queryGoodFriendRequest(final int viewId) {
        OkGo.<String>post(Urls.queryGoodFriendRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", hisMemberId)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 try {
                                     JSONObject jsonObject = new JSONObject(response.body());
                                     JSONObject data = jsonObject.optJSONObject("data");
                                     if (jsonObject.optBoolean("success")) {
                                         int state = data.optInt("state");//0空 ,1等待同意,2加好友,3已是好友
                                         isFriend = state == 3 ? true : false;
                                         if (isFriend) {
                                             PrefAppStore.setIsFriend(cnt, 1);
                                         } else {
                                             PrefAppStore.setIsFriend(cnt, 0);
                                         }
                                         switch (viewId) {
                                             case R.id.iv_songliwu:
                                                 if (isFriend) {
                                                     requestPhoneCall();
                                                 } else {
                                                     RxToast.showToast("他不是你的好友，无法拨打电话！");
                                                 }
                                                 break;
                                             case R.id.ll_shipin_dianhua:
                                                 getWalletInfo();

                                                 break;
                                             case R.id.iv_faxiaoxi:
                                                 Intent intent = new Intent(cnt, SingleChatActivity.class);
                                                 intent.putExtra("receiverMemberId", hisMemberId);
                                                 intent.putExtra("receiverHeadUrl", hisImgUrl);
                                                 intent.putExtra("isFriend", isFriend);
                                                 intent.putExtra("activityId", activityId);
                                                 intent.putExtra("receiverName", hisNickName);
                                                 startActivity(intent);
                                                 break;
                                         }

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
                         }
                );
    }

    /**
     * 关注/取消关注
     *
     * @param id
     * @param toMemberId
     * @param toMemberName
     */
    private void addToAttentionRelation(long id, String toMemberId, String toMemberName) {
        OkGo.<String>get(Urls.ADD_ATTENTIONRELATION)//
                .tag(this)//
                .params("id", id)
                .params("fromMemberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", toMemberId)
                .params("toMemberName", toMemberName)
                .params("atteStatus", atteStatus)//关注 0 /取消关注 1 (必填)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
//                        refreshLayout.finishRefresh();
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        if (atteStatus == 0) {
                                            RxToast.showToast("已添加关注！");
                                            llAttend.setSelected(true);
                                            tvAddAttend.setText("已关注");
                                            atteStatus = 1;
                                        } else if (atteStatus == 1) {
                                            RxToast.showToast("已取消关注！");
                                            llAttend.setSelected(false);
                                            tvAddAttend.setText("加关注");
                                            atteStatus = 0;
                                        }
                                        myEventBusModel.REFRESH_ATTENTION_STATUS_FLAG = true;
                                        myEventBusModel.REFRESH_ATTENTION_STATUS = atteStatus;
                                        EventBus.getDefault().post(myEventBusModel);
                                    } else {
                                        RxToast.showToast(data.optString("info"));
                                    }

                                } else {
                                    if (atteStatus == 0) {
                                        RxToast.showToast("添加关注失败！");

                                    } else if (atteStatus == 1) {
                                        RxToast.showToast("取消关注失败！");
                                    }
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

    @OnClick({R.id.iv_back, R.id.ll_his_attentions, R.id.tv_add_friends, R.id.ll_attend, R.id.ll_his_funs, R.id.tv_right_img, R.id.clv_header, R.id.tv_my_nickname, R.id.tv_edit_info, R.id.tv_yuanfen_num, R.id.iv_xihuan_next, R.id.iv_faxiaoxi, R.id.ll_shipin_dianhua, R.id.iv_songliwu})
    public void onViewClicked(View view) {
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_attend://加关注
                addToAttentionRelation(100, hisMemberId, hisNickName);
                break;
            case R.id.tv_add_friends://加好友
                if ("1".equals(friendStatus)) {//0 没有邀请记录 1 等待对方同意 2 好友
                    RxToast.showToast("已添加好友，请等待对方同意！");
                    return;
                }
                if ("0".equals(friendStatus)) {
                    addGoodFriends();
                }
                break;
            case R.id.ll_his_attentions://他的关注
                Intent hisAttentionsIntent = new Intent(this, HisFunsAndAttentionsAty.class);
                hisAttentionsIntent.putExtra("hisMemberId", hisMemberId);
                hisAttentionsIntent.putExtra("fromHisFunsOrAttentions", 0);
                startActivity(hisAttentionsIntent);
                break;
            case R.id.ll_his_funs://他的粉丝
                Intent hisFunsIntent = new Intent(this, HisFunsAndAttentionsAty.class);
                hisFunsIntent.putExtra("hisMemberId", hisMemberId);
                hisFunsIntent.putExtra("fromHisFunsOrAttentions", 1);
                startActivity(hisFunsIntent);
                break;
            case R.id.tv_right_img:
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("hisHomeModelData", hisHomeModelData);
                RxActivityTool.skipActivity(this, HisZiLiaoSettingAty.class, bundle1);
                break;
            case R.id.clv_header:
                ArrayList<String> corverUrlList = new ArrayList<>();
                if (!RxDataTool.isNullString(hisImgUrl)) {
                    corverUrlList.add(hisImgUrl);
                }
                if (corverUrlList.size() == 0) {
                    return;
                }
                Intent intent = new Intent(this, ChatAmplificationActivity.class);
                intent.putStringArrayListExtra("imgUrl", corverUrlList);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_activity_in, R.anim.anim_activity_out);
                break;
            case R.id.tv_my_nickname:
                break;
            case R.id.tv_edit_info:

                Bundle bundle = new Bundle();
                bundle.putSerializable("hisHomeModelData", hisHomeModelData);
                RxActivityTool.skipActivity(this, HisXiangXiInfoAty.class, bundle);
                break;
            case R.id.tv_yuanfen_num:
                break;
            case R.id.iv_xihuan_next:
                Bundle bundleLikes = new Bundle();
                bundleLikes.putString("memberId", hisMemberId);
                RxActivityTool.skipActivity(this, WhoLikeMeAty.class, bundleLikes);
                break;
            case R.id.iv_faxiaoxi:
                if (memberId.equals(hisMemberId)) {
                    RxToast.showToast("自己无法和自己聊天");
                    return;
                }
                if (RxDataTool.isNullString(hisMemberId) || hisMemberInfo == null) {
                    RxToast.showToast("获取对方信息失败，请刷新后重连！");
                    return;
                }

                if (fromId == SingleChatActivity.activityId) {
                    finish();
                } else {
                    queryGoodFriendRequest(R.id.iv_faxiaoxi);
                }
                break;
            case R.id.ll_shipin_dianhua:
                if (memberId.equals(hisMemberId)) {
                    RxToast.showToast("自己无法和自己聊天");
                    return;
                }
                if (RxDataTool.isNullString(hisMemberId) || hisMemberInfo == null) {
                    RxToast.showToast("获取对方信息失败，请刷新后重连！");
                    return;
                }
                queryGoodFriendRequest(R.id.ll_shipin_dianhua);

/*                if (!isFriend && hisMemberInfo.getVideoStatus().equals("0")) { //是否勿扰 0:勿扰 1:可以视频
                    RxToast.showToast("对方开启陌生人视频功能，无法拨打视频电话！");
                    return;
                }
                */

                break;
            case R.id.iv_songliwu://语音通话
                if (memberId.equals(hisMemberId)) {
                    RxToast.showToast("自己无法和自己聊天");
                    return;
                }
                if (RxDataTool.isNullString(hisMemberId) || hisMemberInfo == null) {
                    RxToast.showToast("获取对方信息失败，请刷新后重连！");
                    return;
                }
                queryGoodFriendRequest(R.id.iv_songliwu);


                break;
        }
    }

    /**
     * 语音通话
     */
    private void requestPhoneCall() {
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("toMemberId", hisMemberId);
            jsonObject1.put("toMemberName", hisNickName);
            jsonObject1.put("toHeadImgUrl", hisMemberInfo.getImgUrl());

            startActivityForResult(new Intent(HisYingJiAty.this, VoiceChatActivity.class)
                    .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_CALLER_TYPE)
                    .putExtra("toMemberId", hisMemberId)
                    .putExtra("toMemberName", hisNickName)
                    .putExtra("toHeadImgUrl", hisMemberInfo.getImgUrl())
                    .putExtra("callType", "0")
                    .putExtra(CALL_VIDEO_INFO_KEY, jsonObject1.toString()), AUDIO_CHAT
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dianZanOnCheckListener(boolean isChecked, int position) {
        if (!isChecked) {//是否点赞 0点赞，1为取消点赞
            updateLikes(isChecked, 0, position);
        } else {
            updateLikes(isChecked, 1, position);
        }
    }

    /**
     * 点赞、取消点赞
     *
     * @param isChecked
     * @param flag
     * @param position
     */
    private void updateLikes(final boolean isChecked, final int flag, final int position) {
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", yingJiList.get(position).getId())
                .params("personUrl", UserModel.getUserModel().getImgUrl())
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", yingJiList.get(position).getMemberId())
                .params("flag", flag)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data.optBoolean("result")) {
                                    int count = data.optInt("count");
                                    if (flag == 0) {
                                        RxToast.showToast("已点赞！");
                                        yingJiList.get(position).setIsLike(1);

                                    } else {
                                        RxToast.showToast("已取消点赞！");
                                        yingJiList.get(position).setIsLike(0);

                                    }
                                    yingJiList.get(position).setLikeCounts(count);

                                    hisYingJiAdapter.notifyItemChanged(position);
                                } else {
                                    RxToast.showToast(data.optString("msg"));
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

    //只有断开的时候才走这个方法
    public void getCheckConnectionState() {
        try {
            if (MerriApp.socket != null) {
                MerriApp.socket.disconnect();
            }
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(HisYingJiAty.this), VideoChatModel.class);
            String mSocketAddress = "http://" + chatModel.data.socketIp + ":" + chatModel.data.socketPort + "/";
            MerriApp.socket = IO.socket(mSocketAddress);
            MerriApp.socket.io().reconnection(true);
            MerriApp.socket.connect();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("memberId", UserModel.getUserModel().getMemberId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MerriApp.socket.emit("connectAfter", jsonObject1.toString());
            MerriApp.socket.on("calledChat", MerriApp.calledChat);
            MerriApp.socket.on("callStatus", MerriApp.callStatus);
            MerriApp.socket.on("answerCall", MerriApp.answerCall);
            MerriApp.socket.on("hangUp", MerriApp.hangUp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        REFRESHORLOADMORE = 6;
        currentPage++;
        hisAlbum();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        currentPage = 1;
        REFRESHORLOADMORE = 5;
        hisAlbum();
    }
}
