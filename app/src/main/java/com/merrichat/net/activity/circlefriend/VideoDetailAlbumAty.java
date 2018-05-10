package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.adapter.AboutLogAdapter;
import com.merrichat.net.adapter.EvaluateDetailAdapter;
import com.merrichat.net.adapter.TuWenXiHuanPerAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.circlefriends.AllCommentActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AboutLogModel;
import com.merrichat.net.model.CircleDetailModel;
import com.merrichat.net.model.DashModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.networklistening.ConnectivityStatus;
import com.merrichat.net.networklistening.ReactiveNetwork;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.TimeUtil;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.DrawableCenterTextView;
import com.merrichat.net.view.HorizontalSpaceItemDecorations;
import com.merrichat.net.view.PopShare;
import com.merrichat.net.view.SpaceItemDecorations;
import com.merrichat.net.view.universalvideoview.SmallMediaController;
import com.merrichat.net.view.universalvideoview.SmallVideoView;
import com.merrichat.net.wxapi.WXEntryActivity;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.wxapi.WXEntryActivity.isShareLog;

/**
 * Created by amssy on 17/11/20.
 */

public class VideoDetailAlbumAty extends AppCompatActivity implements SmallVideoView.VideoViewCallback, RedPacketDialog.MyRedPacketActivity_Listener, EvaluateDetailAdapter.OnItemClickListener, AboutLogAdapter.OnItemClickListener
        , OnLoadmoreListener {

    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @BindView(R.id.title_part)
    RelativeLayout titlePart;
    @BindView(R.id.sv)
    NestedScrollView sv;
    @BindView(R.id.rel_video_group)
    FrameLayout relVideoGroup;
    @BindView(R.id.image_cover)
    ImageView imageCover;
    /**
     * 作者头像
     */
    @BindView(R.id.show_header)
    SimpleDraweeView showHeader;
    /**
     * 作者名称
     */
    @BindView(R.id.show_name)
    TextView showName;
    /**
     * 创建时间
     */
    @BindView(R.id.show_time)
    TextView showTime;
    /**
     * 关注按钮
     */
    @BindView(R.id.btn_re_select)
    TextView btnReSelect;
    /**
     * 帖子标题
     */
    @BindView(R.id.tv_show_title)
    TextView tvShowTitle;
    /**
     * 帖子内容
     */
    @BindView(R.id.tv_show_content)
    TextView tvShowContent;
    /**
     * 播放器
     */
    @BindView(R.id.videoView)
    SmallVideoView videoView;
    /**
     * 播放器控制层
     */
    @BindView(R.id.media_controller)
    SmallMediaController mediaController;
    /**
     * 播放按钮
     */
    @BindView(R.id.video_player)
    ImageView videoPlayer;
    /**
     * 评论头像
     */
    @BindView(R.id.show_evaluate_header)
    SimpleDraweeView showEvaluateHeader;
    /**
     * 发表评论输入框
     */
    ClearEditText showEditText;
    /**
     * 评论框
     */
    @BindView(R.id.show_evaluate_textView)
    TextView showEvaluateTextView;
    /**
     * 评论列表
     */
    @BindView(R.id.recycler_view_detail_evaluate)
    RecyclerView recyclerView;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    /**
     * 全部评论
     */
    @BindView(R.id.rel_evaluate)
    RelativeLayout relEvaluate;
    @BindView(R.id.tv_dianzan)
    DrawableCenterTextView tvDianzan;
    @BindView(R.id.tv_pinglun)
    DrawableCenterTextView tvPinglun;
    @BindView(R.id.tv_fenxiang)
    DrawableCenterTextView tvFenxiang;
    @BindView(R.id.rv_receclerView_xihuan)
    RecyclerView xiHuanPerRecyclerView;
    /**
     * 红包打赏
     */
    @BindView(R.id.imageView_dash)
    ImageView imageViewDash;
    @BindView(R.id.show_dash_group1)
    LinearLayout showDashGroup1;
    @BindView(R.id.show_dash_group2)
    LinearLayout showDashGroup2;
    @BindView(R.id.show_dash_name)
    TextView showDashName;
    @BindView(R.id.show_dash_number)
    TextView showDashNumber;
    @BindView(R.id.recycler_view_show)
    RecyclerView recyclerViewShow;
    @BindView(R.id.show_dash_lin_group)
    LinearLayout showDashLinGroup;

    /**
     * 数据流量播放视频提示
     */
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.btn_other)
    Button btnOther;
    @BindView(R.id.rel_toast)
    RelativeLayout relToast;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_income)
    DrawableCenterTextView tvIncome;
    //    @BindView(R.id.lin_show_select)
//    LinearLayout linShowSelect;
//    @BindView(R.id.lin_show_dash)
//    LinearLayout linShowDash;
    UMShareAPI umShareAPI = null;
    private int heightPic = 0;
    private int preScrollY = 0;
    private String videoUrl;
    private CircleDetailModel detailModel;
    private int mSeekPosition;//播放的当前位置
    private String contentId;
    private EvaluateDetailAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int commentPosition;
    private int currentPage = 1;
    private String toMemberId;
    private NiceDialog niceDialog;
    private List<CircleDetailModel.DataBean.CommentListBean> listComment;
    private int atteStatus = -1;//关注 0 /取消关注 1 (必填);
    private long toMemberIdAttention = 0;
    private String toMemberName = "";
    private ArrayList<CircleDetailModel.DataBean.MemberIdBean> memberIdLists = new ArrayList<>();
    private TuWenXiHuanPerAdapter tuWenXiHuanPerAdapter;
    private int DETAIL_RED_PACKET_RESULT = 100;
    private List<DashModel.DataBean> list_dash;
    private CircleDetailModel.DataBean.BeautyLogBean beautyLog;
    private String classifystr = "";//标签  1,1,1格式
    private int pageSize = 10;
    private int pageNum = 1;
    private SpaceItemDecorations spaceItemDecoration;
    private AboutLogAdapter aboutLogAdapter;
    private List<AboutLogModel.DataBean.BeautyLogListBean> listAbout;
    private String shareUrl;
    private List<PhotoVideoModel> list;
    private boolean isChecked = false;
    private int likeCounts = 0;//点赞数
    private int commentCounts = 0;//评论数
    private int isDianZan = 0;//是否点赞 0点赞，1为取消点赞
    private ReactiveNetwork reactiveNetwork;
    private boolean isMobileNet = false;// 0 连接mobileNet 1 未连接mobileNet
    private boolean isCheckWIFI = false;// 0 连接wifi 1 未连接WiFi
    private ImageButton mTurnButton;
    private String shareImage;
    private int isMy = -1;//查看是否是本人（0不是本人 1是本人）
    private int collectFlag = 0;//0收藏 1取消收藏
    private ImageView imageCollect;
    private ImageView iv_bulr;
    private TextView tvCollect;
    private Bitmap bitmap;
    private int tab_item;
    private PopShare popShare;
    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台
    /**
     * 友盟分享回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            RxToast.showToast("分享成功");
            if (popShare != null) {
                popShare.dismiss();
            }
            updateShare(detailModel.getData().getBeautyLog().getId(), toMemberId, UserModel.getUserModel().getMemberId());
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            RxToast.showToast("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            RxToast.showToast("分享取消");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImgTransparent(this);
        setContentView(R.layout.activity_video_detail_album);
        ButterKnife.bind(this);
        initGetIntent();
        initView();
        //绑定数据
        initData();
        EventBus.getDefault().register(this);
        umShareAPI = UMShareAPI.get(this);
    }

    /**
     * 加载数据
     */
    private void initData() {
        listComment = detailModel.getData().getCommentList();
        beautyLog = detailModel.getData().getBeautyLog();
        toMemberIdAttention = beautyLog.getMemberId();
        toMemberName = beautyLog.getMemberName();
        atteStatus = detailModel.getData().isQueryIsAttentionRelation() ? 1 : 0;//是否关注该人 true 关注 false 未关注
        if (atteStatus == 1) {// 是否关注该人 true 关注 false 未关注
            btnReSelect.setSelected(true);
            btnReSelect.setText("已关注");
        } else {
            btnReSelect.setText("+ 关注");
            btnReSelect.setSelected(false);
        }
        /**
         * 自己不能关注自己
         */
        if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
            btnReSelect.setVisibility(View.GONE);
        } else {
            btnReSelect.setVisibility(View.VISIBLE);
        }

        showHeader.setImageURI(beautyLog.getMemberImage());
        showName.setText(beautyLog.getMemberName());
        showTime.setText(TimeUtil.getDetailStrTime(String.valueOf(beautyLog.getCreateTimes())));
        tvShowTitle.setText(beautyLog.getTitle());
        if (TextUtils.isEmpty(beautyLog.getTitle())) {
            tvTitle.setText("动态详情");
        } else {
            tvTitle.setText(beautyLog.getTitle());
        }
        if (TextUtils.isEmpty(beautyLog.getTitle())) {
            tvShowTitle.setVisibility(View.GONE);
        } else {
            tvShowTitle.setVisibility(View.VISIBLE);
        }

        isMy = detailModel.getData().getIsMy();
        collectFlag = detailModel.getData().isCollects() ? 1 : 0;

        String content = beautyLog.getContent();
        list = JSON.parseArray(content, PhotoVideoModel.class);
        tvShowContent.setText(list.get(0).getText());
        if (TextUtils.isEmpty(list.get(0).getText())) {
            tvShowContent.setVisibility(View.GONE);
        } else {
            tvShowContent.setVisibility(View.VISIBLE);
        }

        PhotoVideoModel coverModel = JSON.parseObject(beautyLog.getCover(), PhotoVideoModel.class);
        shareImage = coverModel.getUrl();

        /**
         * 本人不能给自己的帖子打赏
         */
        if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
            imageViewDash.setVisibility(View.GONE);
        } else {
            imageViewDash.setVisibility(View.VISIBLE);
        }

        //评论头像
        showEvaluateHeader.setImageURI(UserModel.getUserModel().getImgUrl());
        commentCounts = detailModel.getData().getBeautyLog().getCommentCounts();
        tvComment.setText("共" + commentCounts + "条评论");
        if (commentCounts > 9999) {
            tvPinglun.setText(StringUtil.rounded(commentCounts / 10000, 1) + "w");
        } else {
            tvPinglun.setText("" + commentCounts);
        }
        likeCounts = detailModel.getData().getBeautyLog().getLikeCounts();
        if (likeCounts > 9999) {
            tvDianzan.setText(StringUtil.rounded(likeCounts / 10000, 1) + "w");
        } else {
            tvDianzan.setText("" + likeCounts);
        }

        String income = detailModel.getData().getIncome();
        if (!TextUtils.isEmpty(income)) {
            //赚钱
            if (Double.valueOf(income) > 9999) {
                double incomes = Double.valueOf(income);
                tvIncome.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
            } else {
                tvIncome.setText(income);
            }
        }
        CircleDetailModel.DataBean detailModelData = detailModel.getData();
        List<CircleDetailModel.DataBean.MemberIdBean> memberIdBeans = detailModelData.getMemberIdList();
        isDianZan = detailModelData.isLikes() ? 1 : 0;
        if (isDianZan == 1) {
            tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_dianzan), null, null, null);
        } else {
            tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null, null);
        }
        if (memberIdBeans != null && memberIdBeans.size() > 0) {
            //linShowSelect.setVisibility(View.VISIBLE);
            memberIdLists.addAll(memberIdBeans);
        } else {
            //linShowSelect.setVisibility(View.GONE);
        }

        //判断网络是否是WIFI
        if (NetUtils.isWifiAvailable(VideoDetailAlbumAty.this)) {
            relToast.setVisibility(View.GONE);
            //播放视频
            videoView.setVideoPath(list.get(0).getUrl());
            videoView.start();
            videoView.seekTo(mSeekPosition);
        } else {
            relToast.setVisibility(View.VISIBLE);
        }


        //评论列表
        setEvaluate();
        queryDash();//查询打赏记录
        List<String> list_Class = detailModel.getData().getBeautyLog().getClassifys();
        if (list_Class != null) {
            for (int i = 0; i < list_Class.size(); i++) {
                if (classifystr.equals("")) {
                    classifystr = list_Class.get(i);
                } else {
                    classifystr = classifystr + "," + list_Class.get(i);
                }
            }
        }
        queryAboutLog();//查询相关帖子
    }

    /**
     * 查询相关日志
     */
    private void queryAboutLog() {
        OkGo.<String>get(Urls.QUERY_ABOUT_LOG)
                .tag(this)
                .params("id", contentId)
                .params("pageSize", pageSize)
                .params("pageNum", pageNum)
                .params("classifystr", classifystr)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            //加载完成
                            refreshLayout.finishLoadmore();
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    AboutLogModel aboutLogModel = JSON.parseObject(response.body(), AboutLogModel.class);
                                    if (aboutLogModel.data.beautyLogList == null || aboutLogModel.data.beautyLogList.size() == 0) {
                                        refreshLayout.setLoadmoreFinished(true);
                                    } else {
                                        listAbout.addAll(aboutLogModel.data.beautyLogList);
                                        aboutLogAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    //RxToast.showToast(data.optString("message"));
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
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadmore();
                        }
                    }
                });
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            detailModel = (CircleDetailModel) intent.getSerializableExtra("CircleDetailModel");
            contentId = intent.getStringExtra("contentId");
            toMemberId = intent.getStringExtra("toMemberId");
            likeCounts = intent.getIntExtra("likeCounts", 0);
            commentCounts = intent.getIntExtra("commentCounts", 0);
            isDianZan = intent.getIntExtra("isDianZan", -1);
            mSeekPosition = intent.getIntExtra("mSeekPosition", 0);
            tab_item = getIntent().getIntExtra("tab_item", 0);

            if (isDianZan == 1) {
                tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_dianzan), null, null, null);
            } else {
                tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null, null);
            }
        }
    }

    private void initView() {
        //关闭下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setEnableAutoLoadmore(true);

        //设置 Header 为 BezierRadar 样式
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        //设置 Footer 为 球脉冲
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));


        videoView.setVideoViewCallback(this);
        videoView.setMediaController(mediaController);
        //网络连接状态的监听
        reactiveNetwork = new ReactiveNetwork();
        reactiveNetwork.setNetworkEvent(new ReactiveNetwork.NetworkEvent() {
            @Override
            public void enent(ConnectivityStatus status) {
                if (status.description.equals("offline") || status.description.equals("unknown")) {
                    isCheckWIFI = false;
                    isMobileNet = false;
                    RxToast.showToast("无网络连接，请检查网络！");
                } else if (status.description.equals("connected to WiFi network")) {
                    isCheckWIFI = true;
                    isMobileNet = false;
                    errorLayout.setVisibility(View.GONE);
                    relToast.setVisibility(View.GONE);
                    if (list == null) {

                    } else {
                        videoView.setVideoPath(list.get(0).getUrl());
                        videoView.start();
                    }
                    //RxToast.showToast("connected to WiFi");
                } else if (status.description.equals("connected to mobile network")) {
                    isCheckWIFI = false;
                    isMobileNet = true;
                    videoView.pause();
                    errorLayout.setVisibility(View.GONE);
                    relToast.setVisibility(View.VISIBLE);
                    //RxToast.showToast("connected to mobile");
                }
            }
        });
        mediaController.setOnErrorViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckWIFI) {
                    relToast.setVisibility(View.GONE);
                    //WIFI链接
                    videoView.setVideoPath(list.get(0).getUrl());
                    videoView.start();
                } else if (isMobileNet) {

                    //移动网络的情况
                    relToast.setVisibility(View.VISIBLE);
                } else {
                    relToast.setVisibility(View.GONE);
                    RxToast.showToast("无网络连接，请检查网络！");
                }
            }
        });
        reactiveNetwork.observeNetworkConnectivity(this);

        heightPic = DensityUtils.dp2px(this, 220);
        tvTitle.setTextColor(Color.argb((int) 0, 255, 255, 255));
        initXiHuanRecyclerview();

        initAboutRecyclerView();

        //播放按钮
        mTurnButton = (ImageButton) findViewById(R.id.turn_button);
        //ScrollVIew的监听
        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // 向下滑动
                }

                if (scrollY < oldScrollY) {
                    // 向上滑动
                }

                if (scrollY == 0) {
                    // 顶部
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // 底部
                }

                if (scrollY <= 0) {
                    titlePart.setBackgroundColor(Color.argb((int) 0, 101, 95, 95));
                    tvTitle.setTextColor(Color.argb((int) 0, 255, 255, 255));
                    //ivRightMenu.setBackgroundColor(Color.argb((int) 0, 101, 95, 95));
                } else if (scrollY > 0 && scrollY <= heightPic) {
                    float scale = (float) scrollY / heightPic;
                    float alpha = (255 * scale);
                    titlePart.setBackgroundColor(Color.argb((int) alpha, 101, 95, 95));
                    tvTitle.setTextColor(Color.argb((int) alpha, 255, 255, 255));
                    //ivRightMenu.setBackgroundColor(Color.argb((int) alpha, 101, 95, 95));
                } else {
                    titlePart.setBackgroundColor(Color.argb((int) 255, 101, 95, 95));
                    tvTitle.setTextColor(Color.argb((int) 255, 255, 255, 255));
                    //ivRightMenu.setBackgroundColor(Color.argb((int) 255, 101, 95, 95));
                }
            }
        });
    }

    /**
     * 相关日志列表
     */
    private void initAboutRecyclerView() {
        listAbout = new ArrayList<>();
        recyclerViewShow.setHasFixedSize(true);
        recyclerViewShow.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        aboutLogAdapter = new AboutLogAdapter(R.layout.item_about_log, listAbout);
        recyclerViewShow.setAdapter(aboutLogAdapter);
        spaceItemDecoration = new SpaceItemDecorations(2, 2);
        recyclerViewShow.addItemDecoration(spaceItemDecoration);
        aboutLogAdapter.setOnItemClickListener(this);
        recyclerViewShow.setNestedScrollingEnabled(false);
    }

    /**
     * 点赞的人
     */
    private void initXiHuanRecyclerview() {
        HorizontalSpaceItemDecorations xiHuanItemDecorations = new HorizontalSpaceItemDecorations(30, 30, 0, 30);
        xiHuanPerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        xiHuanPerRecyclerView.addItemDecoration(xiHuanItemDecorations);
        tuWenXiHuanPerAdapter = new TuWenXiHuanPerAdapter(R.layout.item_his_hearder, memberIdLists);
        xiHuanPerRecyclerView.setAdapter(tuWenXiHuanPerAdapter);
        tuWenXiHuanPerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                long personId = Long.parseLong(memberIdLists.get(position).getLikePersonId());
                String memberId = UserModel.getUserModel().getMemberId();
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (memberId.equals(String.valueOf(personId))) {
                    RxActivityTool.skipActivity(VideoDetailAlbumAty.this, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (list_dash != null) {
                        bundle.putLong("hisMemberId", Long.parseLong(memberIdLists.get(position).getLikePersonId()));
                        bundle.putString("hisImgUrl", memberIdLists.get(position).getPersonUrl());
                    }
                    RxActivityTool.skipActivity(VideoDetailAlbumAty.this, HisYingJiAty.class, bundle);
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.video_player, R.id.btn_re_select, R.id.rl_more, R.id.show_evaluate_textView, R.id.rel_evaluate
            , R.id.imageView_dash, R.id.show_header, R.id.show_name, R.id.tv_fenxiang, R.id.btn_play, R.id.btn_other, R.id.tv_dianzan, R.id.tv_pinglun
            , R.id.show_evaluate_header, R.id.iv_right_menu})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.rl_more://点赞的人
                if (memberIdLists != null && memberIdLists.size() > 0) {

                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("xiHuanList", memberIdLists);
                    RxActivityTool.skipActivity(this, MorePersonDianZanAty.class, bundle2);
                } else {
                    RxToast.showToast("无更多点赞的人");
                }
                break;
            case R.id.show_header:
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                        RxActivityTool.skipActivity(this, MyDynamicsAty.class);
                    } else {
                        Bundle bundle = new Bundle();
                        if (beautyLog != null) {
                            bundle.putLong("hisMemberId", beautyLog.getMemberId());
                            bundle.putString("hisImgUrl", beautyLog.getMemberImage());
                            bundle.putString("hisNickName", beautyLog.getMemberName());
                        }
                        RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle);
                    }
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.show_evaluate_header:
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    RxActivityTool.skipActivity(this, MyDynamicsAty.class);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.show_name:
                if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                    RxActivityTool.skipActivity(this, MyDynamicsAty.class);
                } else {
                    Bundle bundle1 = new Bundle();
                    if (beautyLog != null) {
                        bundle1.putLong("hisMemberId", beautyLog.getMemberId());
                        bundle1.putString("hisImgUrl", beautyLog.getMemberImage());
                        bundle1.putString("hisNickName", beautyLog.getMemberName());
                    }
                    RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle1);
                }
                break;
            case R.id.video_player://播放按钮
                if (videoView != null) {
                    videoView.start();
                    mTurnButton.setImageResource(R.mipmap.uvv_stop_btn);
                }
                break;
            case R.id.btn_re_select://关注
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    addToAttentionRelation();//是否关注该人 true 关注 false 未关注
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.show_evaluate_textView://评论框
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    niceDialog = NiceDialog.init();
                    niceDialog.setLayoutId(R.layout.item_fabu_pinglun)
                            .setConvertListener(new ViewConvertListener() {
                                @Override
                                public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                    showEditText = holder.getView(R.id.show_editText);
                                    final Button btnShowRelease = holder.getView(R.id.btn_show_release);
                                    btnShowRelease.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String context = showEditText.getText().toString();
                                            if (!TextUtils.isEmpty(context)) {
                                                //评论
                                                addComment(UserModel.getUserModel().getMemberId(), context);

                                            } else {
                                                RxToast.showToast("说点你的心得吧");
                                            }
                                        }
                                    });
                                    showEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(showEditText, 0);
                                        }
                                    });
                                }
                            })
                            .setShowBottom(true)
                            .show(getSupportFragmentManager());
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.rel_evaluate:
                Intent intent1 = new Intent(VideoDetailAlbumAty.this, AllCommentActivity.class);
                intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
                intent1.putExtra("DETAIL_WHO", 2);
                startActivity(intent1);
                break;
            case R.id.tv_pinglun:
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    niceDialog = NiceDialog.init();
                    niceDialog.setLayoutId(R.layout.item_fabu_pinglun)
                            .setConvertListener(new ViewConvertListener() {
                                @Override
                                public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                                    showEditText = holder.getView(R.id.show_editText);
                                    final Button btnShowRelease = holder.getView(R.id.btn_show_release);
                                    btnShowRelease.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String context = showEditText.getText().toString();
                                            if (!TextUtils.isEmpty(context)) {
                                                //评论
                                                addComment(UserModel.getUserModel().getMemberId(), context);

                                            } else {
                                                RxToast.showToast("说点你的心得吧");
                                            }
                                        }
                                    });
                                    showEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(showEditText, 0);
                                        }
                                    });
                                }
                            })
                            .setShowBottom(true)
                            .show(getSupportFragmentManager());
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.tv_dianzan:
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    updateLikes();
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.imageView_dash://打赏
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    RedPacketDialog dialog = RedPacketDialog.getInstance(VideoDetailAlbumAty.this, getSupportFragmentManager());
                    Bundle bundle = new Bundle();
                    bundle.putString("contentId", "" + contentId);
                    bundle.putString("toMemberId", "" + toMemberId);
                    bundle.putString("relName", "" + toMemberName);
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "", true);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.tv_fenxiang:
//                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
//                    shareUrl = "http://gzhgr.igomot.net/weixin-shop/xunmei/showBox/show.html?"
//                            + "st=" + System.currentTimeMillis()
//                            + "&cid=" + toMemberId//帖子创建人id
//                            + "&mid=" + UserModel.getUserModel().getMemberId()//分享人memberId
//                            + "&sid=" + contentId//帖子ID
//                            + "&source=2";
//                    Intent intent_share = new Intent(VideoDetailAlbumAty.this, ShareToActivity.class);
//                    intent_share.putExtra("ShareUrl", "" + shareUrl);
//                    intent_share.putExtra("shareImage", shareImage);
//                    intent_share.putExtra("title", "" + tvTitle.getText());
//                    intent_share.putExtra("content", "" + tvShowContent.getText());
//                    startActivity(intent_share);
//                } else {
//                    RxToast.showToast("请先登录哦");
//                }
                break;
            case R.id.btn_play:
                isChecked = true;
                relToast.setVisibility(View.GONE);
                //播放视频
                videoView.setVideoPath(list.get(0).getUrl());
                videoView.start();
                break;
            case R.id.btn_other://分享好友
                if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                    shareUrl = "http://gzhgr.igomot.net/weixin-shop/xunmei/showBox/show.html?"
                            + "st=" + System.currentTimeMillis()
                            + "&cid=" + toMemberId//帖子创建人id
                            + "&mid=" + UserModel.getUserModel().getMemberId()//分享人memberId
                            + "&sid=" + contentId//帖子ID
                            + "&source=2";
                    Intent intent_share1 = new Intent(VideoDetailAlbumAty.this, ShareToActivity.class);
                    intent_share1.putExtra("ShareUrl", "" + shareUrl);
                    intent_share1.putExtra("title", "" + tvTitle.getText());
                    intent_share1.putExtra("content", "" + tvShowContent.getText());
                    startActivity(intent_share1);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.iv_right_menu:
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                showShareView();
                break;
        }

    }

    private void showShareView() {
        popShare = new PopShare(this);
        imageCollect = (ImageView) popShare.conentView.findViewById(R.id.image_collect);
        iv_bulr = (ImageView) popShare.conentView.findViewById(R.id.iv_bulr);
        tvCollect = (TextView) popShare.conentView.findViewById(R.id.tv_collect);
//        bitmap = StringUtil.getIerceptionScreen(this);
//        iv_bulr.setImageBitmap(bitmap);

        // 设置popWindow的显示和消失动画
        popShare.setAnimationStyle(R.style.AnimPopShareDialog);
        // 设置点击popupwindow外屏幕其它地方消失
        popShare.setOutsideTouchable(true);

        if (collectFlag == 1) {
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_click_2x));
            tvCollect.setText("已收藏");
        } else {
            tvCollect.setText("收藏");
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
        }
        if (UserModel.getUserModel().getMemberId().equals(toMemberId)) {
            popShare.deleteBtn(true);
        } else {
            popShare.deleteBtn(false);
        }
        popShare.showAtLocation(sv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popShare.setOnPopuClick(new PopShare.OnPopuClick() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.layout_collect://收藏
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                            if (isMy == 1) {
                                RxToast.showToast("无法收藏自己的帖子");
                                return;
                            }
                            updateCollections();
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lay_private_lock://权限
                        Intent intent_lock = new Intent(VideoDetailAlbumAty.this, LockActivity.class);
                        intent_lock.putExtra("contentId", "" + contentId);
                        intent_lock.putExtra("jurisdiction", detailModel.getData().getBeautyLog().getJurisdiction());//当前日志权限
                        startActivity(intent_lock);
                        popShare.dismiss();
                        break;
                    case R.id.lay_jubao://举报
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                            Intent intent = new Intent(VideoDetailAlbumAty.this, InformActivity.class);
                            intent.putExtra("contentId", "" + contentId);
                            intent.putExtra("memberId", "" + detailModel.getData().getBeautyLog().getMemberId());
                            intent.putExtra("memberPhone", "" + detailModel.getData().getBeautyLog().getPhone());
                            intent.putExtra("memberName", "" + detailModel.getData().getBeautyLog().getMemberName());
                            intent.putExtra("title", "" + detailModel.getData().getBeautyLog().getTitle());
                            startActivity(intent);
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lay_dele://删除
                        deleteLog();
                        break;
                    case R.id.lin_pengyouquan://朋友圈
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        WXEntryActivity.isTrueShareLog = true;
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                            if (umShareAPI.isInstall(VideoDetailAlbumAty.this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                                getPromoQRcode();
                            } else {
                                RxToast.showToast("未安装微信客户端...");
                            }
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lin_wechat://微信
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        WXEntryActivity.isTrueShareLog = true;
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {

                            if (umShareAPI.isInstall(VideoDetailAlbumAty.this, SHARE_MEDIA.WEIXIN)) {
                                shareMedia = SHARE_MEDIA.WEIXIN;
                                getPromoQRcode();
                            } else {
                                RxToast.showToast("未安装微信客户端...");
                            }
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lin_weibo://微博
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {

                            if (umShareAPI.isInstall(VideoDetailAlbumAty.this, SHARE_MEDIA.SINA)) {
                                shareMedia = SHARE_MEDIA.SINA;
                                getPromoQRcode();
                            } else {
                                RxToast.showToast("未安装微博客户端...");
                            }
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lin_qq://QQ
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(VideoDetailAlbumAty.this)) {
                            if (umShareAPI.isInstall(VideoDetailAlbumAty.this, SHARE_MEDIA.QQ)) {
                                shareMedia = SHARE_MEDIA.QQ;
                                getPromoQRcode();
                            } else {
                                RxToast.showToast("未安装QQ客户端...");
                            }
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.view_top_touch://点击其他关闭
                        popShare.dismiss();
                        break;
                }
            }
        });
    }

    /**
     * 删除日志
     */
    private void deleteLog() {
        OkGo.<String>get(Urls.DELETE_LOG)
                .tag(this)
                .params("id", contentId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    switch (tab_item) {
                                        case 1:
                                            myEventBusModel.DELEATE_LOG1 = true;
                                            break;
                                        case 2:
                                            myEventBusModel.DELEATE_LOG2 = true;
                                            break;
                                        case 3:
                                            myEventBusModel.DELEATE_LOG3 = true;
                                            break;
                                        case 4:
                                            myEventBusModel.DELEATE_LOG4 = true;
                                            break;
                                        case 5:
                                            myEventBusModel.FRESH_LIST_DATA = true;
                                            break;
                                    }
                                    EventBus.getDefault().post(myEventBusModel);
                                    finish();
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
     * 收藏/取消收藏
     */
    private void updateCollections() {
        OkGo.<String>get(Urls.UPDATE_COLLECTIONS)//
                .tag(this)//
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", toMemberIdAttention)
                .params("id", contentId)
                .params("flag", collectFlag)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
//                        refreshLayout.finishRefresh();
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    boolean data = jsonObjectEx.optBoolean("data");
                                    if (data) {
                                        if (collectFlag == 0) {
                                            collectFlag = 1;
                                            RxToast.showToast("已收藏");
                                            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_click_2x));
                                            tvCollect.setText("已收藏");
                                        } else if (collectFlag == 1) {
                                            collectFlag = 0;
                                            RxToast.showToast("已取消收藏");
                                            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
                                            tvCollect.setText("收藏");

                                        }
                                        //刷新公分
                                        freshDetail();

                                    } else {
                                        if (collectFlag == 0) {

                                            RxToast.showToast("收藏失败");
                                        } else {
                                            RxToast.showToast("取消收藏失败");
                                        }
                                    }

                                } else {
                                    if (collectFlag == 0) {
                                        RxToast.showToast("收藏失败");

                                    } else if (collectFlag == 1) {
                                        RxToast.showToast("取消收藏失败");
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

    /**
     * 点赞、取消点赞
     */
    private void updateLikes() {
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", contentId)
                .params("personUrl", UserModel.getUserModel().getImgUrl())
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", toMemberIdAttention)
                .params("flag", isDianZan)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data.optBoolean("result")) {
                                    likeCounts = data.optInt("count");
                                    if (isDianZan == 0) {
                                        RxToast.showToast("已点赞");
                                        isDianZan = 1;
                                        CircleDetailModel.DataBean.MemberIdBean memberIdBean = new CircleDetailModel.DataBean.MemberIdBean();
                                        memberIdBean.setLikePersonId(UserModel.getUserModel().getMemberId());
                                        memberIdBean.setPersonUrl(UserModel.getUserModel().getImgUrl());
                                        memberIdLists.add(memberIdBean);
                                        tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_dianzan), null, null, null);
                                    } else if (isDianZan == 1) {
                                        RxToast.showToast("已取消点赞");
                                        isDianZan = 0;
                                        if (memberIdLists != null && memberIdLists.size() > 0) {
                                            for (CircleDetailModel.DataBean.MemberIdBean memberIdBean :
                                                    memberIdLists
                                                    ) {
                                                if (memberIdBean.getLikePersonId().equals(UserModel.getUserModel().getMemberId())) {
                                                    memberIdLists.remove(memberIdBean);
                                                }
                                            }
                                        }
                                        tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null, null);
                                    }
                                    tvDianzan.setText("赞·" + likeCounts);
                                    //刷新公分
                                    freshDetail();

                                    tuWenXiHuanPerAdapter.notifyDataSetChanged();
                                    refreshCircleDetailCountsEvent();
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

    /**
     * 关注/取消关注
     */
    private void addToAttentionRelation() {
        OkGo.<String>get(Urls.ADD_ATTENTIONRELATION)//
                .tag(this)//
                .params("fromMemberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", toMemberIdAttention)
                .params("toMemberName", toMemberName)
                .params("atteStatus", atteStatus)
                .execute(new StringDialogCallback(this) {

                    @Override
                    public void onSuccess(Response<String> response) {
//                        refreshLayout.finishRefresh();
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        if (atteStatus == 0) {
                                            atteStatus = 1;
                                            RxToast.showToast("已添加关注");
                                            btnReSelect.setSelected(true);
                                            btnReSelect.setText("已关注");
                                        } else if (atteStatus == 1) {
                                            atteStatus = 0;
                                            RxToast.showToast("已取消关注");
                                            btnReSelect.setText("+ 关注");
                                            btnReSelect.setSelected(false);
                                        }
                                    } else {
                                        RxToast.showToast(data.optString("info"));
                                    }

                                } else {
                                    if (atteStatus == 0) {
                                        RxToast.showToast("添加关注失败");

                                    } else if (atteStatus == 1) {
                                        RxToast.showToast("取消关注失败");
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

//    @Override
//    public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
//        if (y <= 0) {
//            titlePart.setBackgroundColor(Color.argb((int) 0, 101, 95, 95));
//            tvTitle.setTextColor(Color.argb((int) 0, 255, 255, 255));
//        } else if (y > 0 && y <= heightPic) {
//            float scale = (float) y / heightPic;
//            float alpha = (255 * scale);
//            titlePart.setBackgroundColor(Color.argb((int) alpha, 101, 95, 95));
//            tvTitle.setTextColor(Color.argb((int) alpha, 255, 255, 255));
//        } else {
//            titlePart.setBackgroundColor(Color.argb((int) 255, 101, 95, 95));
//            tvTitle.setTextColor(Color.argb((int) 255, 255, 255, 255));
//        }
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        videoPlayer.setVisibility(View.VISIBLE);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            videoView.pause();
        }
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        videoPlayer.setVisibility(View.GONE);
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSeekPosition > 0 && videoView != null) {
            videoView.seekTo(mSeekPosition);
            videoView.start();
        }
        //微信分享成功
        if (isShareLog) {
            if (popShare != null) {
                popShare.dismiss();
            }
            updateShare(detailModel.getData().getBeautyLog().getId(), toMemberId, UserModel.getUserModel().getMemberId());
            isShareLog = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            mSeekPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    /**
     * 发表评论
     *
     * @param memberId
     */
    private void addComment(String memberId, final String context) {
        OkGo.<String>get(Urls.ADD_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("contentId", contentId)//帖子ID
                .params("context", context)//评论内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    CircleDetailModel.DataBean.CommentListBean commentListBean = new CircleDetailModel.DataBean.CommentListBean();
                                    UserModel userModel = UserModel.getUserModel();
                                    commentListBean.setCommentHeadImgUrl(userModel.getImgUrl());
                                    commentListBean.setNick(userModel.getRealname());
                                    commentListBean.setContext(context);
                                    commentListBean.setCreateTime(Long.valueOf(data.optJSONObject("data").optString("createTime")));
                                    commentListBean.setIsLikeComment(false);
                                    commentListBean.setCommentPersonId(Long.valueOf(userModel.getMemberId()));
                                    commentListBean.setLikeCommentNum(0);
                                    commentListBean.setId(Long.parseLong(data.optJSONObject("data").optString("commentId")));
                                    listComment.add(0, commentListBean);
                                    adapter.notifyDataSetChanged();
                                    niceDialog.dismiss();
                                    niceDialog.dismiss();
                                    //RxToast.showToast("评论成功，你的评论就是我的动力~");
                                    showEditText.setText("");
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    refreshCircleDetailCountsEvent();
                                    freshDetail();
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

    private void setEvaluate() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EvaluateDetailAdapter(this, listComment);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * 回复评论
     *
     * @param contentId
     * @param memberId
     * @param replyMemberId
     * @param commentId
     * @param replyCommentId
     * @param replyContext
     */
    private void replyDynamic(String contentId, String memberId, String replyMemberId, String commentId, String replyCommentId, final String replyContext, final String nickName) {
        OkGo.<String>get(Urls.REPLY_DYNAMIC)
                .tag(this)
                .params("contentId", contentId)//帖子id
                .params("memberId", memberId)
                .params("replyMemberId", replyMemberId)//被回复人id
                .params("commentId", commentId)//你要回复的那条评论的id(评论列表查询出来的返回值中的id)
                .params("replyCommentId", replyCommentId)//回复的评论的子评论ID
                .params("replyContext", replyContext)//回复内容
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //niceDialog.dismiss();
                                    //RxToast.showToast("回复成功");
                                    CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean replyCommentBean = new CircleDetailModel.DataBean.CommentListBean.ReplyCommentBean();
                                    UserModel userModel = UserModel.getUserModel();
                                    replyCommentBean.setId(data.optJSONObject("data").optLong("replyId"));
                                    replyCommentBean.setCommentHeadImgUrl(userModel.getImgUrl());
                                    replyCommentBean.setNick(userModel.getRealname());
                                    replyCommentBean.setContext(replyContext);
                                    replyCommentBean.setCreateTime(Long.valueOf(data.optJSONObject("data").optString("createTime")));
                                    replyCommentBean.setIsLikeReplyComment(false);
                                    replyCommentBean.setLikeReplyCommentNum(0);
                                    replyCommentBean.setCommentPersonId(Long.valueOf(userModel.getMemberId()));
                                    replyCommentBean.setReplyNick(nickName);
                                    replyCommentBean.setCommentType(2);
                                    listComment.get(commentPosition).getReplyComment().add(0, replyCommentBean);
                                    adapter.notifyDataSetChanged();
                                    freshDetail();
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
     * Item的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(int position) {

    }

    /**
     * 回复得点击事件
     *
     * @param position
     */
    @Override
    public void onHuiFuClick(int position) {
        commentPosition = position;
        niceDialog = NiceDialog.init();
        niceDialog.setLayoutId(R.layout.item_fabu_pinglun)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        showEditText = holder.getView(R.id.show_editText);
                        final Button btnShowRelease = holder.getView(R.id.btn_show_release);
                        btnShowRelease.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String context = showEditText.getText().toString();
                                if (!TextUtils.isEmpty(context)) {
                                    //回复评论
                                    List<CircleDetailModel.DataBean.CommentListBean> commentListBean = detailModel.getData().getCommentList();
                                    final String toMemberId = String.valueOf(commentListBean.get(commentPosition).getCommentPersonId());
                                    final String contendId = String.valueOf(commentListBean.get(commentPosition).getContentId());
                                    final String commentId = String.valueOf(commentListBean.get(commentPosition).getId());
                                    final String reyCommentId = String.valueOf(commentListBean.get(commentPosition).getId());
                                    final String nickName = commentListBean.get(commentPosition).getNick();
                                    replyDynamic(contendId, UserModel.getUserModel().getMemberId(), toMemberId, commentId, reyCommentId, context, nickName);

                                } else {
                                    RxToast.showToast("说点你的心得吧");
                                }
                            }
                        });
                        showEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(showEditText, 0);
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    /**
     * 查看全部回复得点击事件
     *
     * @param position
     */
    @Override
    public void onAllHuiFuClick(int position) {
        Intent intent1 = new Intent(VideoDetailAlbumAty.this, AllCommentActivity.class);
        intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
        intent1.putExtra("DETAIL_WHO", 2);
        startActivity(intent1);
    }

    /**
     * 查询帖子详情(用于刷新评论或者其他 不做页面进入时的加载数据)
     */
    private void freshDetail() {
        String memberId = "";
        if (UserModel.getUserModel().getIsLogin()) {
            memberId = UserModel.getUserModel().getMemberId();
        } else {
            memberId = "0";
        }
        OkGo.<String>get(Urls.QUERY_BEAUTY_LOG)
                .tag(this)
                .params("id", contentId)
                .params("memberId", memberId)
                .params("toMemberId", toMemberId)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    detailModel = JSON.parseObject(response.body(), CircleDetailModel.class);
                                    listComment.clear();
                                    listComment.addAll(detailModel.getData().getCommentList());

                                    //评论头像
                                    showEvaluateHeader.setImageURI(UserModel.getUserModel().getImgUrl());
                                    int commentCounts = detailModel.getData().getBeautyLog().getCommentCounts();
                                    tvComment.setText("共" + commentCounts + "条评论");
                                    tvDianzan.setText("" + detailModel.getData().getBeautyLog().getLikeCounts());
                                    tvPinglun.setText("" + commentCounts);
                                    String income = detailModel.getData().getIncome();
                                    //赚钱
                                    if (Double.valueOf(income) > 9999) {
                                        double incomes = Double.valueOf(income);
                                        tvIncome.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                    } else {
                                        tvIncome.setText(income);
                                    }

                                    adapter.notifyDataSetChanged();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reactiveNetwork.relese(this);
        EventBus.getDefault().unregister(this);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.COMMENT_EVALUATE2) {
            freshDetail();
            refreshCircleDetailCountsEvent();
        }
    }

    /**
     * 刷新 CircleDetailsVideoAty 点赞数和评论数
     */
    private void refreshCircleDetailCountsEvent() {
        MyEventBusModel eventBusModel = new MyEventBusModel();
        eventBusModel.COMMENT_EVALUATE1 = true;
        EventBus.getDefault().post(eventBusModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DETAIL_RED_PACKET_RESULT) {//打赏成功
                queryDash();
            }
        }

    }

    /**
     * 打赏的人
     */
    private void initDash() {
        if (list_dash.size() == 0) {
            if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                //linShowDash.setVisibility(View.GONE);
            }
            showDashLinGroup.setVisibility(View.GONE);
        } else {
            //linShowDash.setVisibility(View.VISIBLE);
            showDashLinGroup.setVisibility(View.VISIBLE);
            String name = "";
            showDashGroup1.removeAllViews();
            showDashGroup2.removeAllViews();
            for (int i = 0; i < list_dash.size(); i++) {
                if (i < 8) {//第一排显示8个
                    if (name != "") {
                        name = name + "、" + list_dash.get(i).getName();
                    } else {
                        name = list_dash.get(i).getName();
                    }
                    View view = LayoutInflater.from(this).inflate(R.layout.simple_drawee_view_dash_header, null);
                    SimpleDraweeView simpleHeader = (SimpleDraweeView) view.findViewById(R.id.simple_header_show);
                    simpleHeader.setImageURI(list_dash.get(i).getImgUrl());
                    final int finalI = i;
                    simpleHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long personId = list_dash.get(finalI).getRewardMemberId();
                            String memberId = UserModel.getUserModel().getMemberId();
                            //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                            if (memberId.equals(String.valueOf(personId))) {
                                RxActivityTool.skipActivity(VideoDetailAlbumAty.this, MyDynamicsAty.class);
                            } else {
                                Bundle bundle = new Bundle();
                                if (list_dash != null) {
                                    bundle.putLong("hisMemberId", list_dash.get(finalI).getRewardMemberId());
                                    bundle.putString("hisImgUrl", list_dash.get(finalI).getImgUrl());
                                    bundle.putString("hisNickName", list_dash.get(finalI).getName());
                                }
                                RxActivityTool.skipActivity(VideoDetailAlbumAty.this, HisYingJiAty.class, bundle);
                            }
                        }
                    });
                    showDashGroup1.addView(view);
                } else if (i >= 8 && i < 16) {//第二排显示8个
                    View view = LayoutInflater.from(this).inflate(R.layout.simple_drawee_view_dash_header, null);
                    SimpleDraweeView simpleHeader = (SimpleDraweeView) view.findViewById(R.id.simple_header_show);
                    simpleHeader.setImageURI(list_dash.get(i).getImgUrl());
                    final int finalI1 = i;
                    simpleHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long personId = list_dash.get(finalI1).getRewardMemberId();
                            String memberId = UserModel.getUserModel().getMemberId();
                            //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                            if (memberId.equals(String.valueOf(personId))) {
                                RxActivityTool.skipActivity(VideoDetailAlbumAty.this, MyDynamicsAty.class);
                            } else {
                                Bundle bundle = new Bundle();
                                if (list_dash != null) {
                                    bundle.putLong("hisMemberId", list_dash.get(finalI1).getRewardMemberId());
                                    bundle.putString("hisImgUrl", list_dash.get(finalI1).getImgUrl());
                                    bundle.putString("hisNickName", list_dash.get(finalI1).getName());
                                }
                                RxActivityTool.skipActivity(VideoDetailAlbumAty.this, HisYingJiAty.class, bundle);
                            }
                        }
                    });
                    showDashGroup2.addView(view);
                }
            }
            showDashName.setText("" + name);
            showDashNumber.setText("" + list_dash.size());
        }
    }

    /**
     * 查询打赏记录
     */
    private void queryDash() {
        OkGo.<String>get(Urls.GET_REWARD_LOG)
                .tag(this)
                .params("tieId", contentId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    DashModel dashModel = JSON.parseObject(response.body(), DashModel.class);
                                    list_dash = dashModel.getData();
                                    initDash();
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
     * 上拉加载更多
     *
     * @param refreshlayout
     */
    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageNum++;
        queryAboutLog();
    }

    /**
     * 相关日志的点击事件
     *
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //日志标识 1图文专辑 2视频 3照片 4录像
        int flag = listAbout.get(position).beautylog.flag;
        switch (flag) {
            case 2:
                Intent intent = new Intent(VideoDetailAlbumAty.this, CircleDetailsVideoAty.class);
                intent.putExtra("contentId", "" + listAbout.get(position).beautylog.id);
                intent.putExtra("toMemberId", "" + listAbout.get(position).beautylog.memberId);
                intent.putExtra("tab_item", 3);
                startActivity(intent);
                break;
            case 1:
                Bundle bundle = new Bundle();
                bundle.putString("contentId", listAbout.get(position).beautylog.id + "");
                bundle.putString("toMemberId", listAbout.get(position).beautylog.memberId + "");
                bundle.putInt("tab_item", 3);
                RxActivityTool.skipActivity(VideoDetailAlbumAty.this, TuWenAlbumAty.class, bundle);
                break;
        }
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(VideoDetailAlbumAty.this, shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        if (TextUtils.isEmpty(detailModel.getData().getBeautyLog().getDescribe())) {
            web.setDescription(getString(R.string.share_content_log));
        } else {
            web.setDescription(detailModel.getData().getBeautyLog().getDescribe());
        }
        if (!TextUtils.isEmpty(detailModel.getData().getBeautyLog().getTitle())) {
            web.setTitle(detailModel.getData().getBeautyLog().getTitle());
        } else {
            web.setTitle(getString(R.string.share_title_log));
        }
        new ShareAction(VideoDetailAlbumAty.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcode() {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", contentId)
                .params("atlMemberId", toMemberId)
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    shareUrl = data.optJSONObject("data").getString("url");
                                    ShareWeb(shareMedia);
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
     * 分享
     */
    private void updateShare(long logId, String memberId, String myMemberId) {
        if (TextUtils.isEmpty(myMemberId)) {
            myMemberId = "-1";
        }
        OkGo.<String>post(Urls.updateShare)
                .tag(this)
                .params("memberId", memberId)//原创人Id
                .params("logId", logId)
                .params("myMemberId", myMemberId)//登陆人Id
                .params("flag", 1)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                //RxToast.showToast("挖矿公分＋1");
                                //刷新评论数据
                                freshDetail();
                            } else {
                                //RxToast.showToast(R.string.connect_to_server_fail);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //RxToast.showToast(R.string.connect_to_server_fail);
                    }
                });

    }

    //从dialog中获取数据
    @Override
    public void getDataFrom_DialogFragment(boolean isSuccess) {
        if (isSuccess) {
            queryDash();
        }
    }
}
