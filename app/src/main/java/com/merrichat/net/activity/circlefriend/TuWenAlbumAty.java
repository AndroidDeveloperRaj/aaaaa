package com.merrichat.net.activity.circlefriend;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.merrichat.net.adapter.TuWenAlbumAdapter;
import com.merrichat.net.adapter.TuWenXiHuanPerAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.circlefriends.AllCommentActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AboutLogModel;
import com.merrichat.net.model.CircleDetailModel;
import com.merrichat.net.model.DashModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxTimeTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.DrawableCenterTextViewH;
import com.merrichat.net.view.HorizontalSpaceItemDecorations;
import com.merrichat.net.view.PopShare;
import com.merrichat.net.view.SpaceItemDecorations;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.merrichat.net.wxapi.WXEntryActivity.isShareLog;

/**
 * Created by amssy on 17/11/20.
 * <p>
 * 图文详情
 */

public class TuWenAlbumAty extends AppCompatActivity implements RedPacketDialog.MyRedPacketActivity_Listener, EvaluateDetailAdapter.OnItemClickListener, AboutLogAdapter.OnItemClickListener
        , OnLoadmoreListener {
    public final static int activityId = MiscUtil.getActivityId();
    //是否分享成功
    public static boolean isShareSuc = false;

    private final int PLAY_MUSIC = 0x0001;//播放音乐
    private final int ROTATE_START = 0x0002;//播放音乐
    private final int ROTATE_STOP = 0x003;
    @BindView(R.id.show_header)
    SimpleDraweeView showHeader;
    @BindView(R.id.show_name)
    TextView showName;
    @BindView(R.id.show_time)
    TextView showTime;
    @BindView(R.id.btn_re_select)
    TextView btnReSelect;
    /**
     * 图文title
     */
    @BindView(R.id.tv_show_title)
    TextView tvShowTitle;
    /**
     * 图文描述
     */
    @BindView(R.id.tv_show_content)
    TextView tvShowContent;
    @BindView(R.id.show_content_recyclerview)
    RecyclerView showContentRecyclerView;
    @BindView(R.id.rv_receclerView_xihuan)
    RecyclerView xiHuanPerRecyclerView;
    @BindView(R.id.show_bar_like_list)
    LinearLayout showBarLikeList;
    @BindView(R.id.show_dash_name)
    TextView showDashName;
    @BindView(R.id.show_dash_number)
    TextView showDashNumber;
    @BindView(R.id.show_dash_group1)
    LinearLayout showDashGroup1;
    @BindView(R.id.show_dash_group2)
    LinearLayout showDashGroup2;
    @BindView(R.id.show_dash_lin_group)
    LinearLayout showDashLinGroup;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.imageView_next)
    ImageView imageViewNext;
    @BindView(R.id.rel_evaluate)
    RelativeLayout relEvaluate;
    @BindView(R.id.show_evaluate_header)
    SimpleDraweeView showEvaluateHeader;
    @BindView(R.id.show_evaluate_textView)
    TextView showEvaluateTextView;
    @BindView(R.id.sv)
    NestedScrollView sv;
    @BindView(R.id.tv_dianzan)
    DrawableCenterTextViewH tvDianzan;
    @BindView(R.id.tv_pinglun)
    DrawableCenterTextViewH tvPinglun;
    @BindView(R.id.tv_fenxiang)
    DrawableCenterTextViewH tvFenxiang;
    @BindView(R.id.lay_three)
    LinearLayout layThree;
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
    /**
     * 发表评论输入框
     */
    ClearEditText showEditText;
    @BindView(R.id.recycler_view_detail_evaluate)
    RecyclerView recyclerViewEvaluate;
    /**
     * 打赏红包
     */
    @BindView(R.id.imageView_dash)
    ImageView imageViewDash;
    @BindView(R.id.tv_evaluate_title)
    TextView tvEvaluateTitle;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.recycler_view_show)
    RecyclerView recyclerViewShow;
    @BindView(R.id.iv_yinyue)
    SimpleDraweeView ivYinyue;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.tv_income)
    DrawableCenterTextViewH tvIncome;
    UMShareAPI umShareAPI = null;
    @BindView(R.id.lin_show_select)
    LinearLayout linShowSelect;
    @BindView(R.id.lin_show_dash)
    LinearLayout linShowDash;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setBackgroundAlpha((float) msg.obj);
                    break;
            }
        }
    };
    @BindView(R.id.tv_money)
    TextView tvMoney;
    private int heightPic = 0;
    private int preScrollY = 0;
    private String id;
    private String toMemberId;
    private ArrayList<PhotoVideoModel> contentList = new ArrayList<>();//影集内容集合
    private List<CircleDetailModel.DataBean.MemberIdBean> xiHuanList = new ArrayList<>();//点赞的人
    private TuWenAlbumAdapter tuWenAlbumAdapter;
    private TuWenXiHuanPerAdapter tuWenXiHuanPerAdapter;
    private int CommentOrReply = 1;//回复评论(2)还是评论(1)
    private CompositeDisposable compositeDisposable;
    private ProgressDialog dialog;
    private int commentPosition;
    private LinearLayoutManager layoutManager;
    private EvaluateDetailAdapter adapter;
    private CircleDetailModel detailModel;
    private int atteStatus = -1;//关注 0 /取消关注 1 (必填)
    private int isMy = -1;//查看是否是本人（0不是本人 1是本人）
    private long toMemberIdAttention = 0;
    private String toMemberName = "";
    private boolean isLike = false;//是否点赞帖子 true 点过赞 false 未点赞
    private int isDianZan = 0;//是否点赞 0点赞，1为取消点赞
    private ImageView imageCollect;
    private TextView tvCollect;
    private int collectFlag = 0;//0收藏 1取消收藏
    private List<CircleDetailModel.DataBean.CommentListBean> listComment;
    private NiceDialog niceDialog;
    private int tab_item;
    private CircleDetailModel.DataBean.BeautyLogBean beautyLogBean;
    private int DETAIL_RED_PACKET_RESULT = 100;
    private List<DashModel.DataBean> list_dash;
    private int pageSize = 8;
    private int pageNum = 1;
    private String classifystr = "";//标签 1,1,1格式
    private AboutLogAdapter aboutLogAdapter;
    private SpaceItemDecorations spaceItemDecoration;
    private List<AboutLogModel.DataBean.BeautyLogListBean> listAbout;
    private String shareUrl;
    private int commentCounts = 0;//评论数
    private int likeCounts = 0;//点赞数
    private int jurisdiction = 1;//权限管理
    private MediaPlayer mediaPlayer;
    private String musicUrl;//音乐路径
    private Animation rotate3dAnimationX;
    private boolean isCanPlay = false;//是否可以播放 prepare 完成
    private String shareImage = "";
    private ImageView iv_bulr;
    private String URL;
    private Bitmap bitmap;
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
    private boolean isStopMusic = false;//是否暂停播放 true暂停播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setBackgroundDrawableResource(R.color.background);
        }
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtil.setImgTransparent(this);
        setContentView(R.layout.activity_tuwen_album);
        ButterKnife.bind(this);
        MerriApp.COMMENT_LOG = 0;
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvStatus.getLayoutParams();
//        layoutParams.height = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
//        tvStatus.setLayoutParams(layoutParams);
        initView();
        EventBus.getDefault().register(this);
        umShareAPI = UMShareAPI.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //微信分享成功
        if (isShareLog) {
            if (popShare != null) {
                popShare.dismiss();
            }
            updateShare(Long.valueOf(id), toMemberId, UserModel.getUserModel().getMemberId());
            isShareLog = false;
        }
        if (isStopMusic) {
            if (mediaPlayer != null && !isCanPlay) {
                return;
            }
            if (mediaPlayer != null) {
                if (isCanPlay) {
                    ivYinyue.startAnimation(rotate3dAnimationX);
                    mediaPlayer.start();
                } else {
                    playMusic();
                }
            } else {
                playMusic();
            }
        }

        isStopMusic = true;
        //其他分享成功
        if (isShareSuc) {
            updateShare(Long.valueOf(id), toMemberId, UserModel.getUserModel().getMemberId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStopMusic) {
            if (mediaPlayer != null) {
                if (isCanPlay && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
//            else {
//                mediaPlayer.reset();
//            }
//            mediaPlayer = null;
            }
            if (ivYinyue != null) {
                ivYinyue.clearAnimation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        EventBus.getDefault().unregister(this);
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        UMShareAPI.get(this).release();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        ivYinyue.startAnimation(rotate3dAnimationX);
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(musicUrl);

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.setLooping(true);//是否循环播放
                    mediaPlayer.start();
                    isCanPlay = true;
                }

            });

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        id = getIntent().getStringExtra("contentId");
        toMemberId = getIntent().getStringExtra("toMemberId");
        tab_item = getIntent().getIntExtra("tab_item", 0);
        getCircleDetail();

        //关闭下拉刷新
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setEnableAutoLoadmore(true);
        //设置 Header 为 BezierRadar 样式
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        //设置 Footer 为 球脉冲
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));

        initRotateAnim();
//        heightPic = ivParams.height;
        heightPic = DensityUtils.dp2px(this, 450);


        initTuWenAlbumContentRec();
        initXiHuanRecyclerview();
        //相关日志
        initAboutRecyclerView();
        //ScrollView监听
//        sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > oldScrollY) {
//                    // 向下滑动
//                }
//
//                if (scrollY < oldScrollY) {
//                    // 向上滑动
//                }
//
//                if (scrollY == 0) {
//                    // 顶部
//                }
//
//                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    // 底部
//                }
//
//                if (scrollY <= 0) {
//                    titlePart.setBackgroundColor(Color.argb((int) 0, 101, 95, 95));
//                    tvTitle.setTextColor(Color.argb((int) 0, 255, 255, 255));
//                } else if (scrollY > 0 && scrollY <= heightPic) {
//                    float scale = (float) scrollY / heightPic;
//                    float alpha = (255 * scale);
//                    titlePart.setBackgroundColor(Color.argb((int) alpha, 101, 95, 95));
//                    tvTitle.setTextColor(Color.argb((int) alpha, 255, 255, 255));
//                } else {
//                    titlePart.setBackgroundColor(Color.argb((int) 255, 101, 95, 95));
//                    tvTitle.setTextColor(Color.argb((int) 255, 255, 255, 255));
//                }
//            }
//        });

    }

    private void initRotateAnim() {
        rotate3dAnimationX = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate3dAnimationX.setFillAfter(true); // 设置保持动画最后的状态
        rotate3dAnimationX.setDuration(4000); // 设置动画时间
        rotate3dAnimationX.setInterpolator(new LinearInterpolator()); // 设置插入器
        rotate3dAnimationX.setRepeatCount(-1);
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
        //recyclerViewShow.setNestedScrollingEnabled(false);
        recyclerViewShow.setHasFixedSize(true);
    }

    /**
     * 图文
     */
    private void initTuWenAlbumContentRec() {
        showContentRecyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        showContentRecyclerView.setLayoutManager(layoutManager);
        tuWenAlbumAdapter = new TuWenAlbumAdapter(R.layout.item_tuwenalbum, contentList);
        showContentRecyclerView.setAdapter(tuWenAlbumAdapter);
        showContentRecyclerView.setNestedScrollingEnabled(false);
        showContentRecyclerView.setHasFixedSize(true);
        tuWenAlbumAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_content_img:
                        isStopMusic = false;
                        //查看大图
                        Intent intent = new Intent(TuWenAlbumAty.this, ShowIMGActivity.class);
                        intent.putExtra("list", contentList);
                        intent.putExtra("position", position);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(TuWenAlbumAty.this, view, "DETAIL_VIEW").toBundle());
                        } else {
                            startActivity(intent);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 点赞的人
     */
    private void initXiHuanRecyclerview() {
        HorizontalSpaceItemDecorations xiHuanItemDecorations = new HorizontalSpaceItemDecorations(30, 30, 0, 30);
        xiHuanPerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        xiHuanPerRecyclerView.addItemDecoration(xiHuanItemDecorations);
        tuWenXiHuanPerAdapter = new TuWenXiHuanPerAdapter(R.layout.item_his_hearder, xiHuanList);
        xiHuanPerRecyclerView.setAdapter(tuWenXiHuanPerAdapter);
        tuWenXiHuanPerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!StringUtil.isLogin(TuWenAlbumAty.this)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                long personId = Long.parseLong(xiHuanList.get(position).getLikePersonId());
                String memberId = UserModel.getUserModel().getMemberId();
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                if (memberId.equals(String.valueOf(personId))) {
                    RxActivityTool.skipActivity(TuWenAlbumAty.this, MyDynamicsAty.class);
                } else {
                    Bundle bundle = new Bundle();
                    if (list_dash != null) {
                        bundle.putLong("hisMemberId", Long.parseLong(xiHuanList.get(position).getLikePersonId()));
                        bundle.putString("hisImgUrl", xiHuanList.get(position).getPersonUrl());
                        bundle.putString("hisNickName", xiHuanList.get(position).getNickName());
                    }
                    RxActivityTool.skipActivity(TuWenAlbumAty.this, HisYingJiAty.class, bundle);
                }
            }
        });
    }

    /**
     * 查询帖子详情
     */
    private void getCircleDetail() {
        String memberId = "";
        if (UserModel.getUserModel().getIsLogin()) {
            memberId = UserModel.getUserModel().getMemberId();
        } else {
            memberId = "0";
        }
        OkGo.<String>get(Urls.QUERY_BEAUTY_LOG)
                .tag(this)
                .params("id", id)
                .params("memberId", memberId)
                .params("toMemberId", toMemberId)
                .params("currentPage", 1)
                .execute(new StringDialogCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    detailModel = JSON.parseObject(response.body(), CircleDetailModel.class);
                                    listComment = detailModel.getData().getCommentList();
                                    CircleDetailModel.DataBean detailModelData = detailModel.getData();
                                    beautyLogBean = detailModelData.getBeautyLog();
                                    if (beautyLogBean != null && !beautyLogBean.equals("")) {
                                        int isBlack = beautyLogBean.getIsBlack();//帖子被举报并通过
                                        int isDelete = beautyLogBean.getIsDelete();//帖子被删除
                                        if (isBlack == 1 || isDelete == 1) {
                                            RxToast.showToast(beautyLogBean.getMsg());
                                            finish();
                                        }
                                        String cover = beautyLogBean.getCover();
                                        musicUrl = beautyLogBean.getMusicUrl();//音乐路径
                                        if (!RxDataTool.isNullString(musicUrl)) {
//                                        handler.sendEmptyMessage(PLAY_MUSIC);
                                            ivYinyue.setVisibility(View.VISIBLE);
                                            playMusic();
                                        }
                                        PhotoVideoModel coverModel = JSON.parseObject(cover, PhotoVideoModel.class);
                                        String content = beautyLogBean.getContent();
                                        List<PhotoVideoModel> photoContentList = JSON.parseArray(content, PhotoVideoModel.class);
                                        List<CircleDetailModel.DataBean.MemberIdBean> memberIdBeans = detailModelData.getMemberIdList();
                                        isDianZan = detailModelData.isLikes() ? 1 : 0;
                                        if (isDianZan == 1) {
                                            tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_dianzan), null, null, null);
                                        } else {
                                            tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null, null);
                                        }
                                        if (memberIdBeans != null && memberIdBeans.size() > 0) {
                                            linShowSelect.setVisibility(View.VISIBLE);
                                            xiHuanList.addAll(memberIdBeans);
                                        } else {
                                            linShowSelect.setVisibility(View.GONE);
                                        }
                                        if (photoContentList != null) {
                                            contentList.addAll(photoContentList);
                                        }

                                        shareImage = coverModel.getUrl();
                                        //Glide.with(TuWenAlbumAty.this).load(coverModel.getUrl()).into(ivPhoto);//封面
                                        atteStatus = detailModelData.isQueryIsAttentionRelation() ? 1 : 0;//是否关注该人 true 关注 false 未关注
                                        collectFlag = detailModelData.isCollects() ? 1 : 0;
                                        toMemberIdAttention = beautyLogBean.getMemberId();
                                        toMemberName = beautyLogBean.getMemberName();
                                        isMy = detailModelData.getIsMy();
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

                                        showHeader.setImageURI(beautyLogBean.getMemberImage());//头像
                                        showName.setText(beautyLogBean.getMemberName());//昵称
                                        showTime.setText(RxTimeTool.getDate(beautyLogBean.getCreateTimes() + "", "MM月dd日 HH:mm"));

                                        if (TextUtils.isEmpty(beautyLogBean.getTitle())) {
                                            tvTitle.setText("动态详情");//图文title
                                        } else {
                                            tvTitle.setText(beautyLogBean.getTitle());
                                        }

                                        tvShowTitle.setText(beautyLogBean.getTitle());//图文title
                                        if (RxDataTool.isNullString(beautyLogBean.getTitle())) {
                                            tvShowTitle.setVisibility(View.GONE);
                                        } else {
                                            tvShowTitle.setVisibility(View.VISIBLE);
                                        }

                                        String describe = beautyLogBean.getDescribe();//内容描述
                                        if (RxDataTool.isNullString(describe)) {
                                            tvShowContent.setVisibility(View.GONE);
                                        } else {
                                            tvShowContent.setVisibility(View.VISIBLE);
                                        }
                                        tvShowContent.setText(describe);
                                        tuWenAlbumAdapter.notifyDataSetChanged();
                                        tuWenXiHuanPerAdapter.notifyDataSetChanged();
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
                                            if (Double.valueOf(income) >= 10000) {
                                                double incomes = Double.valueOf(income);
                                                tvIncome.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                                tvMoney.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                            } else {
                                                tvIncome.setText(income);
                                                tvMoney.setText(income);
                                            }
                                        }

                                        //加载评论数据
                                        setEvaluate();
                                        /**
                                         * 本人不能给自己的帖子打赏
                                         */
                                        if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                                            imageViewDash.setVisibility(View.GONE);
                                        } else {
                                            imageViewDash.setVisibility(View.VISIBLE);
                                        }
                                        List<String> list = detailModel.getData().getBeautyLog().getClassifys();
                                        if (list != null) {
                                            for (int i = 0; i < list.size(); i++) {
                                                if (classifystr.equals("")) {
                                                    classifystr = list.get(i);
                                                } else {
                                                    classifystr = classifystr + "," + list.get(i);
                                                }
                                            }
                                        }
                                        //获取当前日志权限
                                        jurisdiction = beautyLogBean.getJurisdiction();

                                        //查询打赏记录
                                        queryDash();
                                        //查询相关日志
                                        queryAboutLog();

                                        updateBrowse(detailModel.getData().getBeautyLog().getId(), toMemberId, UserModel.getUserModel().getMemberId());
                                    } else {
                                        RxToast.showToast("帖子已被删除...");
                                        finish();
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

    /**
     * 浏览
     */
    private void updateBrowse(long logId, String memberId, String myMemberId) {
        if (TextUtils.isEmpty(myMemberId)) {
            myMemberId = "-1";
        }
        OkGo.<String>post(Urls.updateBrowse)
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
                                //刷新公分
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

    /**
     * 查询相关日志
     */
    private void queryAboutLog() {
        ApiManager.apiService(WebApiService.class).queryBeautyAboutLog(id, pageSize, pageNum, classifystr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<AboutLogModel>() {
                    @Override
                    public void onNext(AboutLogModel aboutLogModel) {
                        refreshLayout.finishLoadmore();
                        if (aboutLogModel.success) {
                            if (aboutLogModel.data.beautyLogList == null || aboutLogModel.data.beautyLogList.size() == 0) {
                                refreshLayout.setLoadmoreFinished(true);
                            } else {
                                listAbout.addAll(aboutLogModel.data.beautyLogList);
                                aboutLogAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RxToast.showToast(R.string.connect_to_server_fail);
                        if (refreshLayout != null) {
                            refreshLayout.finishLoadmore();
                        }

                    }
                });
    }

    /**
     * 点赞、取消点赞
     */
    private void updateLikes() {
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", id)
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
                                        //RxToast.showToast("已点赞");
                                        isDianZan = 1;
                                        CircleDetailModel.DataBean.MemberIdBean memberIdBean = new CircleDetailModel.DataBean.MemberIdBean();
                                        memberIdBean.setLikePersonId(UserModel.getUserModel().getMemberId());
                                        memberIdBean.setPersonUrl(UserModel.getUserModel().getImgUrl());
                                        memberIdBean.setNickName(UserModel.getUserModel().getRealname());
                                        xiHuanList.add(memberIdBean);
                                        tuWenXiHuanPerAdapter.notifyDataSetChanged();
                                        tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_dianzan), null, null, null);

                                    } else if (isDianZan == 1) {
                                        //RxToast.showToast("已取消点赞");
                                        isDianZan = 0;
                                        if (xiHuanList != null && xiHuanList.size() > 0) {
                                            Iterator<CircleDetailModel.DataBean.MemberIdBean> it = xiHuanList.iterator();
                                            while (it.hasNext()) {
                                                CircleDetailModel.DataBean.MemberIdBean memberIdBean = it.next();
                                                if (memberIdBean.getLikePersonId().equals(UserModel.getUserModel().getMemberId())) {
                                                    it.remove();
                                                }
                                            }
                                        }
                                        tuWenXiHuanPerAdapter.notifyDataSetChanged();
                                        tvDianzan.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null, null);
                                    }
//                                    ScaleAnimation sa4 = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                                    sa4.setDuration(500);
//                                    tvDianzan.startAnimation(sa4);

                                    tvDianzan.setText(likeCounts + "");
                                    //刷新公分
                                    freshDetail();
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

    @OnClick({R.id.iv_back, R.id.iv_yinyue, R.id.rl_more, R.id.show_header, R.id.show_name, R.id.show_evaluate_textView
            , R.id.rel_evaluate, R.id.btn_re_select, R.id.tv_dianzan, R.id.iv_right_menu, R.id.tv_pinglun, R.id.tv_fenxiang
            , R.id.imageView_dash, R.id.show_evaluate_header})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MyEventBusModel eventBusModel = new MyEventBusModel();
                eventBusModel.FRESH_LIST_DATA = true;
                if (detailModel != null) {
                    CircleDetailModel.DataBean data = detailModel.getData();
                    if (data != null) {
                        eventBusModel.income = data.getIncome();
                        eventBusModel.isLike = data.isLikes();
                        eventBusModel.likeCounts = data.getBeautyLog().getLikeCounts();
                    }
                }
                EventBus.getDefault().post(eventBusModel);
                finish();
                break;
            case R.id.iv_yinyue:
                ivYinyue.startAnimation(rotate3dAnimationX);
                if (mediaPlayer != null && !isCanPlay) {
                    RxToast.showToast("音乐准备中，请稍后……");
                    return;
                }
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        ivYinyue.clearAnimation();
                    } else {
                        ivYinyue.startAnimation(rotate3dAnimationX);
                        mediaPlayer.start();
                    }
                } else {
                    playMusic();
                }
                break;
            case R.id.rl_more:
                if (xiHuanList != null && xiHuanList.size() > 0) {

                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("xiHuanList", (Serializable) xiHuanList);
                    RxActivityTool.skipActivity(this, MorePersonDianZanAty.class, bundle2);
                } else {
                    RxToast.showToast("无更多点赞的人");
                }
                break;
            case R.id.show_header://标题头像
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                    if (detailModel == null) {
                        return;
                    }
                    if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                        RxActivityTool.skipActivity(this, MyDynamicsAty.class);
                    } else {
                        Bundle bundle = new Bundle();
                        if (beautyLogBean != null) {

                            bundle.putLong("hisMemberId", beautyLogBean.getMemberId());
                            bundle.putString("hisImgUrl", beautyLogBean.getMemberImage());
                            bundle.putString("hisNickName", beautyLogBean.getMemberName());
                        }
                        RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle);
                    }
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.show_evaluate_header://评论头像
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
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
                    if (beautyLogBean != null) {
                        bundle1.putLong("hisMemberId", beautyLogBean.getMemberId());
                        bundle1.putString("hisImgUrl", beautyLogBean.getMemberImage());
                        bundle1.putString("hisNickName", beautyLogBean.getMemberName());
                    }
                    RxActivityTool.skipActivity(this, HisYingJiAty.class, bundle1);
                }
                break;
            case R.id.show_evaluate_textView:////评论框
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
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
                                            if (MerriUtils.isFastDoubleClick2()) {
                                                return;
                                            }
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
                Intent intent1 = new Intent(TuWenAlbumAty.this, AllCommentActivity.class);
                intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
                intent1.putExtra("DETAIL_WHO", 3);
                startActivity(intent1);
                break;
            case R.id.btn_re_select:
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                    addToAttentionRelation();//是否关注该人 true 关注 false 未关注
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.tv_dianzan:
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                    updateLikes();
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
            case R.id.tv_pinglun:
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
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
            case R.id.tv_fenxiang:
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                    getPromoQRcodeLog(id, toMemberId);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.imageView_dash:
                if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                    RedPacketDialog dialog = RedPacketDialog.getInstance(TuWenAlbumAty.this, getSupportFragmentManager());
                    Bundle bundle = new Bundle();
                    bundle.putString("contentId", "" + id);
                    bundle.putString("toMemberId", "" + toMemberId);
                    bundle.putString("relName", "" + toMemberName);
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "", true);

/*
                    Intent intent = new Intent(TuWenAlbumAty.this, RedPacketActivity.class);
                    intent.putExtra("contentId", "" + id);
                    intent.putExtra("toMemberId", "" + toMemberId);
                    intent.putExtra("relName", "" + toMemberName);
                    startActivityForResult(intent, DETAIL_RED_PACKET_RESULT);
*/
                } else {
                    RxToast.showToast("请先登录哦");
                }
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
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {
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
                        Intent intent_lock = new Intent(TuWenAlbumAty.this, LockActivity.class);
                        intent_lock.putExtra("contentId", "" + id);
                        intent_lock.putExtra("jurisdiction", jurisdiction);//当前日志权限
                        startActivity(intent_lock);
                        popShare.dismiss();
                        break;
                    case R.id.lay_jubao://举报
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                            Intent intent = new Intent(TuWenAlbumAty.this, InformActivity.class);
                            intent.putExtra("contentId", "" + id);
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
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                            if (umShareAPI.isInstall(TuWenAlbumAty.this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
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
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {

                            if (umShareAPI.isInstall(TuWenAlbumAty.this, SHARE_MEDIA.WEIXIN)) {
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
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {

                            if (umShareAPI.isInstall(TuWenAlbumAty.this, SHARE_MEDIA.SINA)) {
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
                        if (StringUtil.isLogin(TuWenAlbumAty.this)) {
                            if (umShareAPI.isInstall(TuWenAlbumAty.this, SHARE_MEDIA.QQ)) {
                                shareMedia = SHARE_MEDIA.QQ;
                                getPromoQRcode();
                            } else {
                                RxToast.showToast("未安装QQ客户端...");
                            }
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
//                    case R.id.view_top_touch://点击其他关闭
//                        popShare.dismiss();
//                        break;
                    case R.id.btn_cancel://点击取消关闭
                        popShare.dismiss();
                        break;
                }
            }
        });

        final float[] alpha = {1f};
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (alpha[0] > 0.3f) {
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha[0] -= 0.01f;
                    msg.obj = alpha[0];
                    mHandler.sendMessage(msg);
                }
            }

        }).start();

        popShare.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(1.0f);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcode() {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", id)
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
     * 删除日志
     */
    private void deleteLog() {
        OkGo.<String>get(Urls.DELETE_LOG)
                .tag(this)
                .params("id", id)
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
                .params("id", id)
                .params("flag", collectFlag)
                .execute(new StringDialogCallback() {

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
                                            if (tab_item == 5) {
                                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                                myEventBusModel.FRESH_MY_COLLECTION = true;
                                                EventBus.getDefault().post(myEventBusModel);
                                            }
                                        } else if (collectFlag == 1) {
                                            collectFlag = 0;
                                            RxToast.showToast("已取消收藏");
                                            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
                                            tvCollect.setText("收藏");
                                            if (tab_item == 5) {
                                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                                myEventBusModel.FRESH_LIST_DATA = true;
                                                EventBus.getDefault().post(myEventBusModel);
                                            }
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
     * 关注/取消关注
     */
    private void addToAttentionRelation() {
        OkGo.<String>get(Urls.ADD_ATTENTIONRELATION)//
                .tag(this)//
                .params("fromMemberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", toMemberIdAttention)
                .params("toMemberName", toMemberName)
                .params("atteStatus", atteStatus)
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

    /**
     * 发表评论
     *
     * @param memberId
     */
    private void addComment(String memberId, final String context) {
        OkGo.<String>get(Urls.ADD_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("contentId", id)//帖子ID
                .params("context", context)//评论内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    //RxToast.showToast("评论成功，你的评论就是我的动力~");
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
                                    showEditText.setText("");
                                    commentCounts++;
                                    tvPinglun.setText(commentCounts + "");
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);

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
     * 加载评论数据
     */
    private void setEvaluate() {
        recyclerViewEvaluate.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewEvaluate.setLayoutManager(layoutManager);
        adapter = new EvaluateDetailAdapter(this, listComment);
        recyclerViewEvaluate.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        recyclerViewEvaluate.setNestedScrollingEnabled(false);
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
                                    niceDialog.dismiss();
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
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
        Intent intent1 = new Intent(TuWenAlbumAty.this, AllCommentActivity.class);
        intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
        intent1.putExtra("DETAIL_WHO", 3);
        startActivity(intent1);
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.COMMENT_EVALUATE3) {
            //刷新评论数据
            freshDetail();
        } else if (myEventBusModel.COMMENT_EVALUATE4) {
            //刷新评论数据
            freshDetail();
        } else if (myEventBusModel.CLOSE_MYACTIVITY) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else if (myEventBusModel.REFRESH_ATTENTION_STATUS_FLAG) {
            if (myEventBusModel.REFRESH_ATTENTION_STATUS == 0) {
                atteStatus = 0;
                btnReSelect.setText("+ 关注");
                btnReSelect.setSelected(false);
            } else if (myEventBusModel.REFRESH_ATTENTION_STATUS == 1) {
                atteStatus = 1;
                btnReSelect.setSelected(true);
                btnReSelect.setText("已关注");
            }
        }
    }

    /**
     * 刷新帖子数据
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
                .params("id", id)
                .params("memberId", memberId)
                .params("toMemberId", toMemberId)
                .params("currentPage", 1)
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
                                    CircleDetailModel.DataBean detailModelData = detailModel.getData();
                                    beautyLogBean = detailModelData.getBeautyLog();
                                    //获取当前日志权限
                                    jurisdiction = beautyLogBean.getJurisdiction();

                                    //评论头像
                                    showEvaluateHeader.setImageURI(UserModel.getUserModel().getImgUrl());
                                    int commentCounts = detailModel.getData().getBeautyLog().getCommentCounts();
                                    tvComment.setText("共" + commentCounts + "条评论");
                                    tvPinglun.setText("" + commentCounts);
                                    tvDianzan.setText("" + detailModel.getData().getBeautyLog().getLikeCounts());

                                    String income = detailModel.getData().getIncome();
                                    if (!TextUtils.isEmpty(income)) {
                                        //赚钱
                                        if (Double.valueOf(income) > 9999) {
                                            double incomes = Double.valueOf(income);
                                            tvIncome.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                            tvMoney.setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                        } else {
                                            tvIncome.setText(income);
                                            tvMoney.setText(income);
                                        }
                                    }

                                    List<CircleDetailModel.DataBean.MemberIdBean> memberIdBeans = detailModelData.getMemberIdList();
                                    if (memberIdBeans != null && memberIdBeans.size() > 0) {
                                        linShowSelect.setVisibility(View.VISIBLE);
                                    } else {
                                        linShowSelect.setVisibility(View.GONE);
                                    }

                                    //加载评论数据
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DETAIL_RED_PACKET_RESULT) {//打赏成功
                queryDash();
            }
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 查询打赏记录
     */
    private void queryDash() {
        OkGo.<String>get(Urls.GET_REWARD_LOG)
                .tag(this)
                .params("tieId", id)
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
     * 打赏的人
     */
    private void initDash() {
        if (list_dash.size() == 0) {
            if (UserModel.getUserModel().getMemberId().equals(String.valueOf(detailModel.getData().getBeautyLog().getMemberId()))) {
                linShowDash.setVisibility(View.GONE);
            }
            linShowDash.setVisibility(View.GONE);
            showDashLinGroup.setVisibility(View.GONE);
        } else {
            linShowDash.setVisibility(View.VISIBLE);
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
                            if (!StringUtil.isLogin(TuWenAlbumAty.this)) {
                                RxToast.showToast("请先登录哦～");
                                return;
                            }
                            long personId = list_dash.get(finalI).getRewardMemberId();
                            String memberId = UserModel.getUserModel().getMemberId();
                            //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                            if (memberId.equals(String.valueOf(personId))) {
                                RxActivityTool.skipActivity(TuWenAlbumAty.this, MyDynamicsAty.class);
                            } else {
                                Bundle bundle = new Bundle();
                                if (list_dash != null) {
                                    bundle.putLong("hisMemberId", list_dash.get(finalI).getRewardMemberId());
                                    bundle.putString("hisImgUrl", list_dash.get(finalI).getImgUrl());
                                    bundle.putString("hisNickName", list_dash.get(finalI).getName());
                                }
                                RxActivityTool.skipActivity(TuWenAlbumAty.this, HisYingJiAty.class, bundle);
                            }
                        }
                    });
                    showDashGroup1.addView(view);
                } else if (i >= 8 && i < 16) {//第二排显示8个
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
                                RxActivityTool.skipActivity(TuWenAlbumAty.this, MyDynamicsAty.class);
                            } else {
                                Bundle bundle = new Bundle();
                                if (list_dash != null) {
                                    bundle.putLong("hisMemberId", list_dash.get(finalI).getRewardMemberId());
                                    bundle.putString("hisImgUrl", list_dash.get(finalI).getImgUrl());
                                    bundle.putString("hisNickName", list_dash.get(finalI).getName());
                                }
                                RxActivityTool.skipActivity(TuWenAlbumAty.this, HisYingJiAty.class, bundle);
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

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageNum++;
        queryAboutLog();
    }

    /**
     * 相关日志的点击
     *
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //日志标识 1图文专辑 2视频 3照片 4录像
        int flag = listAbout.get(position).beautylog.flag;
        switch (flag) {
            case 2:
                Intent intent = new Intent(TuWenAlbumAty.this, CircleVideoActivity.class);
                intent.putExtra("contentId", "" + listAbout.get(position).beautylog.id);
                intent.putExtra("toMemberId", "" + listAbout.get(position).beautylog.memberId);
                intent.putExtra("tab_item", "3");
                intent.putExtra("flag", flag);
                startActivity(intent);
                break;
            case 1:
                Bundle bundle = new Bundle();
                bundle.putString("contentId", listAbout.get(position).beautylog.id + "");
                bundle.putString("toMemberId", listAbout.get(position).beautylog.memberId + "");
                bundle.putInt("tab_item", 3);
                RxActivityTool.skipActivity(TuWenAlbumAty.this, TuWenAlbumAty.class, bundle);
                break;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            //最大分配内存
            int memory = activityManager.getMemoryClass();
            //最大分配内存获取方法2
            float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
            //当前分配的总内存
            float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
            //剩余内存
            float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
            Log.d("LogTest", "最大分配内存:" + memory);
            Log.d("LogTest", "最大分配内存获取方法2:" + maxMemory);
            Log.d("LogTest", "当前分配的总内存:" + totalMemory);
            Log.d("LogTest", "剩余内存:" + freeMemory);

            //Fresco清除缓存
            //你需要实现一个MemoryTrimmableRegistry，它持有一个MemoryTrimmable的集合。Fresco的缓存就在它们中间。
            //当你接受到一个系统的内存事件时，你可以调用MemoryTrimmable的对应方法来释放资源。
        }
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        UMImage thumb = new UMImage(TuWenAlbumAty.this, shareImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        if (TextUtils.isEmpty(beautyLogBean.getDescribe())) {
            web.setDescription(getString(R.string.share_content_log));
        } else {
            web.setDescription(beautyLogBean.getDescribe());
        }
        if (!TextUtils.isEmpty(beautyLogBean.getTitle())) {
            web.setTitle(beautyLogBean.getTitle());
        } else {
            web.setTitle(getString(R.string.share_title_log));
        }
        new ShareAction(TuWenAlbumAty.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
    }

    /**
     * 监听返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (popShare != null && popShare.isShowing()) {
                popShare.dismiss();
            } else {
                MyEventBusModel eventBusModel = new MyEventBusModel();
                eventBusModel.FRESH_LIST_DATA = true;
                if (detailModel != null) {
                    CircleDetailModel.DataBean data = detailModel.getData();
                    if (data != null) {
                        eventBusModel.income = data.getIncome();
                        eventBusModel.isLike = data.isLikes();
                        eventBusModel.likeCounts = data.getBeautyLog().getLikeCounts();
                    }
                }
                EventBus.getDefault().post(eventBusModel);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //从dialog中获取数据
    @Override
    public void getDataFrom_DialogFragment(boolean isSuccess) {
        if (isSuccess) {
            queryDash();
        }
    }

    /**
     * 获取分享链接
     */
    private void getPromoQRcodeLog(String id, String toMemberId) {
        OkGo.<String>get(Urls.getPromoQRcode)
                .tag(this)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("source", "2")//分享来源 2为分享
                .params("articleId", id)
                .params("atlMemberId", toMemberId)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {

                                    shareUrl = data.optJSONObject("data").getString("url");

                                    Intent intent_share = new Intent(TuWenAlbumAty.this, ShareToActivity.class);
                                    intent_share.putExtra("ShareUrl", shareUrl);
                                    intent_share.putExtra("shareImage", shareImage);
                                    intent_share.putExtra("activityId", "" + activityId);
                                    intent_share.putExtra("title", "" + tvTitle.getText());
                                    intent_share.putExtra("content", "" + tvShowContent.getText());
                                    startActivity(intent_share);
                                    overridePendingTransition(R.anim.push_dialog_bottom_in, 0);
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
}
