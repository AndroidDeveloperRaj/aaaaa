package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.player.gsyvideo.EmptyControlVideo;
import com.merrichat.net.adapter.CircleVideoAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CircleVideoModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.networklistening.ConnectivityStatus;
import com.merrichat.net.networklistening.ReactiveNetwork;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.ViewSizeChangeAnimation;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.PopShare;
import com.merrichat.net.view.SmallBang;
import com.merrichat.net.view.SoftKeyBoardListener;
import com.merrichat.net.wxapi.WXEntryActivity;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.merrichat.net.wxapi.WXEntryActivity.isShareLog;

/**
 * 视频播放列表
 * <p>
 * Created by amssy on 18/4/11.
 */

public class CircleVideoActivity extends AppCompatActivity implements CircleVideoAdapter.OnVideoPlayerListener, RedPacketDialog.MyRedPacketActivity_Listener, EmptyControlVideo.onDoubleClicklister, OnLoadmoreListener {
    public final static int activityId = MiscUtil.getActivityId();
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    public static boolean isResume = true;//是否执行onResume方法
    /**
     * RecyclerView
     */
    @BindView(R.id.recycler_view_video)
    RecyclerView recyclerViewVideo;
    /**
     * 加载动画
     */
    @BindView(R.id.view_anim)
    View viewAnim;
    /**
     * 评论布局
     */
    @BindView(R.id.rel_comment)
    RelativeLayout relComment;
    /**
     * 返回键
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    /**
     * 菜单键
     */
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @BindView(R.id.iv_close_comment)
    ImageView ivCloseComment;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Handler handler = new Handler();

    private EmptyControlVideo videoView;
    private int isLiveStreaming = 0;
    private int mCurrentItem = 0;
    private List<CircleVideoModel.DataBean.ListBean> beautyLogBeanList = new ArrayList<>();

    private CircleVideoAdapter videoAdapter;
    private int pageSize = 10;
    private int pageNum = 1;
    private String id;//日志ID
    private String toMemberId = "";//发帖人的ID
    private int tab_item;//分别代表（  好友  推荐  关注  附近 ）
    private int flag;//日志标识 1图文专辑 2视频 3照片 4 录像
    private int collectFlag = 0;
    private CircleVideoModel circleVideoModel;
    private int isDianZan = 0;//是否点赞 0点赞，1为取消点赞
    private int atteStatus = -1;//关注 0 /取消关注 1 (必填)
    private NiceDialog niceDialog;
    private ClearEditText showEditText;
    private PopShare popShare;
    private ImageView imageCollect;
    private TextView tvCollect;
    private ImageView iv_bulr;
    private String shareUrl;
    private UMShareAPI umShareAPI;
    private int commentNumber = 0;
    private ReactiveNetwork reactiveNetwork;
    private int networkStatus = 1;//0代表无网络  1表示WiFi 2表示移动数据流量
    private boolean isPause = true;//是否可暂停
    private ProgressBar progressBar;
    Runnable runnable = new Runnable() {
        public void run() {
            progressBar.setMax(videoView.getDuration());
            progressBar.setProgress(videoView.getCurrentPositionWhenPlaying());
            //int percentage = videoView.getBufferPercentage();
            //Logger.e(videoView.getCurrentPositionWhenPlaying()+"+进度+"+videoView.getDuration() );

            handler.postDelayed(runnable, 100);//每0.5秒监听一次是否在播放视频
        }
    };
    private SmallBang mSmallBang;

    private SHARE_MEDIA shareMedia = SHARE_MEDIA.WEIXIN;//分享平台

    //是否分享成功
    public static boolean isShareSuc = false;

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
            if (popShare != null) {
                popShare.dismiss();
            }
            updateShare(beautyLogBeanList.get(mCurrentItem).beautyLog1.id, String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId), UserModel.getUserModel().getMemberId());
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_circle_video);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        umShareAPI = UMShareAPI.get(this);
        initView();
    }

    private void initView() {
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setEnableAutoLoadmore(true);
        refreshLayout.setEnableScrollContentWhenLoaded(false);//是否在加载完成时滚动列表显示新的内容


        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("contentId");
            toMemberId = intent.getStringExtra("toMemberId");
            tab_item = intent.getIntExtra("tab_item", 0);
            flag = intent.getIntExtra("flag", 0);
            //查询视频列表
            queryBeautyVideoLog();
        }
        //加载动画
        MerriUtils.updateView(viewAnim, 600, 600);
        softKeyboardListnenr();

        mSmallBang = SmallBang.attach2Window(CircleVideoActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewVideo.setLayoutManager(linearLayoutManager);
        recyclerViewVideo.setHasFixedSize(true);
        videoAdapter = new CircleVideoAdapter(R.layout.fragment_room, beautyLogBeanList);
        recyclerViewVideo.setAdapter(videoAdapter);
        videoAdapter.setVideoPlayerListener(this);
        //item切换监听
        new GravityPagerSnapHelper(Gravity.BOTTOM, false, new GravitySnapHelper.SnapListener() {
            @Override
            public void onSnap(final int position) {
//                if (position == beautyLogBeanList.size() - 2 && mCurrentItem <= position){
//                    pageNum ++;
//                    //查询视频列表
//                    queryBeautyVideoLog();
//                }
                if (mCurrentItem != position) {
                    mCurrentItem = position;
                    commentNumber = beautyLogBeanList.get(mCurrentItem).beautyLog1.commentCounts;
                    if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                        ((ProgressBar) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini)).setProgress(0);
                    }
                    //判断网络状态  2表示为移动网络 需要提示
                    if (networkStatus == 2) {
                        //是否同意移动网络播放视频 同意则不弹框
                        if (PrefAppStore.getWorkNetStatus(CircleVideoActivity.this) == 0) {
                            startActivity(new Intent(CircleVideoActivity.this, VideoNetWorkDiolog.class));
                        } else {
                            viewAnim.setVisibility(View.VISIBLE);
                            autoPlayVideo();
                        }
                    } else {
                        viewAnim.setVisibility(View.VISIBLE);
                        autoPlayVideo();
                    }
                    //浏览加公分
                    updateBrowse(beautyLogBeanList.get(mCurrentItem).beautyLog1.id, UserModel.getUserModel().getMemberId(), String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId));
                } else {//表示还是同一个ITEM
                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.VISIBLE);
                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
                    if (videoView != null && !videoView.getGSYVideoManager().getMediaPlayer().isPlaying()) {
                        videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.VISIBLE);
                    }
                }

            }
        }).attachToRecyclerView(recyclerViewVideo);

        //item切换监听
        recyclerViewVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                viewAnim.setVisibility(View.GONE);
                switch (newState) {
                    case 0:
                        //System.out.println("recyclerview已经停止滚动");
                        if (!recyclerView.canScrollVertically(-1)) {//到顶部了
                            if (mCurrentItem != 0) {
                                mCurrentItem = 0;
                                if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                                    ((ProgressBar) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini)).setProgress(0);
                                }
                                //判断网络状态  2表示为移动网络 需要提示
                                if (networkStatus == 2) {
                                    //是否同意移动网络播放视频 同意则不弹框
                                    if (PrefAppStore.getWorkNetStatus(CircleVideoActivity.this) == 0) {
                                        startActivity(new Intent(CircleVideoActivity.this, VideoNetWorkDiolog.class));
                                    } else {
                                        viewAnim.setVisibility(View.VISIBLE);
                                        autoPlayVideo();
                                    }
                                } else {
                                    viewAnim.setVisibility(View.VISIBLE);
                                    autoPlayVideo();
                                }
                                //浏览加公分
                                updateBrowse(beautyLogBeanList.get(mCurrentItem).beautyLog1.id, UserModel.getUserModel().getMemberId(), String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId));
                            } else {//表示还是同一个ITEM
                                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.VISIBLE);
                                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
                                if (videoView != null && !videoView.getGSYVideoManager().getMediaPlayer().isPlaying()) {
                                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        break;
                    case 1:
                        System.out.println("recyclerview正在被拖拽");
                        break;
                    case 2:
                        //System.out.println("recyclerview正在依靠惯性滚动");
                        //进度条
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.GONE);
                        }
                        //封面图
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.VISIBLE);
                        }
                        //播放按钮
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
                        }
                        //点赞红心
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageView_heart) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageView_heart).setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });

        //ITEM的控件的点击事件
        videoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_comment://评论
                        ScaleAnimation sa2 = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa2.setDuration(500);
                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.iv_comment).startAnimation(sa2);
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
//                        relComment.setVisibility(View.VISIBLE);
//                        Animation animation = new ViewSizeChangeAnimation(recyclerViewVideo, 600);
//                        animation.setDuration(500);
//                        recyclerViewVideo.startAnimation(animation);
//
//                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.anchor_img).setVisibility(View.GONE);
//                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.lin_right).setVisibility(View.GONE);
//                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.lin_title).setVisibility(View.GONE);
//                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.lin_bottom).setVisibility(View.GONE);
                        VideoCommentDialog dialog1 = VideoCommentDialog.getInstance(CircleVideoActivity.this, getSupportFragmentManager());
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("contentId", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.id);
                        bundle1.putInt("commentNumber", commentNumber);
                        dialog1.setArguments(bundle1);
                        dialog1.show(getSupportFragmentManager(), "", true);
                        break;
                    case R.id.room_view://item
                        break;
                    case R.id.sv_header://头像
//                        ScaleAnimation sa3 = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        sa3.setDuration(500);
//                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.sv_header).startAnimation(sa3);
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (!StringUtil.isLogin(CircleVideoActivity.this)) {
                            RxToast.showToast("请先登录哦");
                            return;
                        }
                        //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
                        if (UserModel.getUserModel().getMemberId().equals(String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId))) {
                            RxActivityTool.skipActivity(CircleVideoActivity.this, MyDynamicsAty.class);
                        } else {
                            Bundle bundle = new Bundle();
                            if (beautyLogBeanList.get(mCurrentItem).beautyLog1 != null) {
                                bundle.putLong("hisMemberId", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId);
                                bundle.putString("hisImgUrl", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberImage);
                                bundle.putString("hisNickName", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberName);
                            }
                            RxActivityTool.skipActivity(CircleVideoActivity.this, HisYingJiAty.class, bundle);
                        }
                        break;
                    case R.id.iv_collect://关注
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            addToAttentionRelation();
                        } else {
                            RxToast.showToast("请登录才能关注");
                        }
                        break;
                    case R.id.shb_like://点赞
                        ((CheckBox) adapter.getViewByPosition(recyclerViewVideo, position, R.id.shb_like)).setChecked(beautyLogBeanList.get(mCurrentItem).likes);
                        if (MerriUtils.isFastDoubleClick2()) {
                            RxToast.showToast("你真淘气");
                            return;
                        }
                        ScaleAnimation sa4 = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa4.setDuration(500);
                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.shb_like).startAnimation(sa4);
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            updateLikes();
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.iv_money://收入公分
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        ScaleAnimation sa = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(500);
                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.iv_money).startAnimation(sa);
                        break;
                    case R.id.iv_share://分享
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            ScaleAnimation sas = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            sas.setDuration(500);
                            adapter.getViewByPosition(recyclerViewVideo, position, R.id.iv_share).startAnimation(sas);

                            getPromoQRcodeLog(String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.id), String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId));
                        } else {
                            RxToast.showToast("请登录才能分享");
                        }
                        break;
                    case R.id.iv_dash://打赏
                        ScaleAnimation sa1 = new ScaleAnimation(0.8f, 1, 0.8f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa1.setDuration(500);
                        adapter.getViewByPosition(recyclerViewVideo, position, R.id.iv_dash).startAnimation(sa1);
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            VideoDashDialog dialog = VideoDashDialog.getInstance(CircleVideoActivity.this, getSupportFragmentManager());
                            Bundle bundle = new Bundle();
                            bundle.putString("contentId", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.id);
                            bundle.putString("toMemberId", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId);
                            bundle.putString("relName", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.memberName);
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "", true);
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.tv_write_comment://写评论
                        if (MerriUtils.isFastDoubleClick2()) {
                            return;
                        }
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
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
                            RxToast.showToast("登录之后才能评论哦");
                        }
                        break;
                }
            }
        });
        //网络连接状态的监听
        reactiveNetwork = new ReactiveNetwork();
        reactiveNetwork.setNetworkEvent(new ReactiveNetwork.NetworkEvent() {
            @Override
            public void enent(ConnectivityStatus status) {
                //Logger.e("获取网络状态：status.description＝＝＝" + status.description);
                if (status.description.equals("offline") || status.description.equals("unknown")) {
                    RxToast.showToast("请检查网络连接");
                    networkStatus = 0;
                } else if (status.description.equals("connected to WiFi network")) {
                    //RxToast.showToast("connected to WiFi");
                    networkStatus = 1;
                } else if (status.description.equals("connected to mobile network")) {
                    //RxToast.showToast("connected to mobile");
                    networkStatus = 2;
                    startActivity(new Intent(CircleVideoActivity.this, VideoNetWorkDiolog.class));
                }
            }
        });
        reactiveNetwork.observeNetworkConnectivity(this);
    }

    /**
     * 查询视频数据
     */
    private void queryBeautyVideoLog() {
        //未登录状态memberId为0
        String memberId = UserModel.getUserModel().getMemberId();
        if (TextUtils.isEmpty(memberId)) {
            memberId = "-1";
        }
        OkGo.<String>get(Urls.queryBeautyVideoLog)
                .tag(this)
                .params("id", id)
                .params("pageSize", pageSize)
                .params("pageNum", pageNum)
                .params("memberId", memberId)
                .params("flag", flag)//日志标识 1图文专辑 2视频 3照片 4 录像
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            if (refreshLayout != null) {
                                refreshLayout.finishLoadmore();
                            }
                            try {
                                viewAnim.setVisibility(View.GONE);
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    circleVideoModel = JSON.parseObject(response.body(), CircleVideoModel.class);
                                    if (circleVideoModel.data.list != null && circleVideoModel.data.list.size() == 0) {
                                        if (refreshLayout != null) {
                                            refreshLayout.setLoadmoreFinished(true);
                                        }
                                    }
                                    beautyLogBeanList.addAll(circleVideoModel.data.list);

                                    commentNumber = beautyLogBeanList.get(mCurrentItem).beautyLog1.commentCounts;
                                    videoAdapter.notifyDataSetChanged();
//                                    new Handler().postDelayed(new Runnable() {
//                                        public void run() {
//                                            videoAdapter.notifyDataSetChanged();
//                                        }
//                                    }, 2000);
                                    //视频加载动画显示
                                    viewAnim.setVisibility(View.VISIBLE);
                                    //浏览加公分
                                    updateBrowse(Long.parseLong(id), UserModel.getUserModel().getMemberId(), toMemberId);
                                } else {
//                                    RxToast.showToast(data.optString("message"));
                                    RxToast.showToast(R.string.connect_to_server_fail);
                                    //视频加载动画隐藏
                                    viewAnim.setVisibility(View.GONE);
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

    /**
     * 获取播放控件播放视频
     */
    private void autoPlayVideo() {
        //VideoView
        if (videoView != null) {
            videoView.onVideoPause();
            videoView.release();
        }
        videoView = (EmptyControlVideo) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.ijkVideoView);
        progressBar = (ProgressBar) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini);
        if (videoView != null) {
            videoPalyer();
        } else {
            Logger.e("error:PLVideoTextureView is null");
        }

    }

    /**
     * VideoView播放视频
     */
    private void videoPalyer() {
        //触摸事件
        videoView.setOnDoubleClicklister(this);
        //循环播放
        videoView.setLooping(true);
//        //是否隐藏虚拟按钮
//        videoView.setFullHideActionBar(true);
//        videoView.setFullHideStatusBar(true);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        /**
         * 监听
         */
        videoView.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {
                Logger.e("onStartPrepared");
                if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img) != null) {
                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.VISIBLE);
                }
                viewAnim.setVisibility(View.VISIBLE);
                //进度条显示
                if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                    //videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPrepared(String url, Object... objects) {
                Logger.e("onPrepared");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
                        }
                        viewAnim.setVisibility(View.GONE);
                        //进度条显示
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.VISIBLE);
                        }
                    }
                }, 200);
            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {
                Logger.e("onClickStartIcon");
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                Logger.e("onClickStartError");
            }

            @Override
            public void onClickStop(String url, Object... objects) {
                Logger.e("onClickStop");
            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {
                Logger.e("onClickStopFullscreen");
            }

            @Override
            public void onClickResume(String url, Object... objects) {
                Logger.e("onClickResume");
            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {
                Logger.e("onClickResumeFullscreen");
            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {
                Logger.e("onClickSeekbar");
            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {
                Logger.e("onClickSeekbarFullscreen");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                //播放完成
                //videoView.startPlayLogic();
                Logger.e("onAutoComplete");
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                Logger.e("onEnterFullscreen");
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                Logger.e("onQuitFullscreen");
            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {
                Logger.e("onQuitSmallWidget");
            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {
                Logger.e("onEnterSmallWidget");
            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {
                Logger.e("onTouchScreenSeekVolume");
            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {
                Logger.e("onTouchScreenSeekPosition");
            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {
                Logger.e("onTouchScreenSeekLight");
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                Logger.e("onPlayError");
            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {
                Logger.e("onClickStartThumb");
            }

            @Override
            public void onClickBlank(String url, Object... objects) {
                Logger.e("onClickBlank");
            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {
                Logger.e("onClickBlankFullscreen");
            }
        });

        //设置视频路径
//        videoView.setVideoURI(Uri.parse(beautyLogBeanList.get(mCurrentItem).beautyLog1.videoUrl));
        videoView.setUp(beautyLogBeanList.get(mCurrentItem).beautyLog1.videoUrl, true, "");

        //开始播放视频
        if (networkStatus != 2) {
            videoView.startPlayLogic();
        } else {
            viewAnim.setVisibility(View.GONE);
        }
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mVideoView != null) {
//            mVideoView.pause();
//        }
        if (videoView != null && isResume) {
            videoView.onVideoPause();
            //暂停按钮显示
            //videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.VISIBLE);
        } else {
            isResume = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mVideoView != null && isResume) {
//            //mVideoView.start();
//        }else {
//            isResume = true;
//        }
//        if (videoView != null && isResume) {
//            //暂停按钮隐藏
//            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
//            if (videoView.getGSYVideoManager().getMediaPlayer() != null) {
//                if (videoView.getGSYVideoManager().getMediaPlayer().isPlaying()) {
//                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
//                    viewAnim.setVisibility(View.GONE);
//                } else {
//                    //封面图显示
//                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.VISIBLE);
//                    viewAnim.setVisibility(View.VISIBLE);
//                    videoView.onVideoResume(false);
//                }
//            }
//        } else {
//            isResume = true;
//        }
        //微信分享成功
        if (isShareLog) {
            if (popShare != null) {
                popShare.dismiss();
            }
            updateShare(Long.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.id), String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId), UserModel.getUserModel().getMemberId());
            isShareLog = false;
        }

        //ShareToActivity 分享成功
        if (isShareSuc) {
            updateShare(Long.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.id), String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId), UserModel.getUserModel().getMemberId());
            isShareSuc = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mVideoView != null) {
//            mVideoView.stopPlayback();
//        }

        handler.removeCallbacks(runnable);

        if (videoView != null) {
            videoView.release();
        }
        EventBus.getDefault().unregister(this);
        reactiveNetwork.relese(CircleVideoActivity.this);
    }

    /**
     * 开始播放列表第一个视频
     */
    @Override
    public void onVideoPlayer(EmptyControlVideo videoViews, ProgressBar mProgressBar) {
        videoView = videoViews;
        progressBar = mProgressBar;
        //initAVOptions();
        //loadVideo();
        videoPalyer();
    }

    @OnClick({R.id.iv_back, R.id.iv_right_menu, R.id.iv_close_comment})
    public void onViewClick(View view) {
        switch (view.getId()) {
            /**返回*/
            case R.id.iv_back:
                finish();
                break;
            /**菜单*/
            case R.id.iv_right_menu:
                showShareView();
                break;
            /**关闭评论界面*/
            case R.id.iv_close_comment:
                //relComment.setVisibility(View.GONE);
                Animation animation = new ViewSizeChangeAnimation(recyclerViewVideo, StringUtil.getHeight(this));
                animation.setDuration(500);
                recyclerViewVideo.startAnimation(animation);

                //videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.VISIBLE);
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.lin_right).setVisibility(View.VISIBLE);
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.lin_title).setVisibility(View.VISIBLE);
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.lin_bottom).setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 打赏的回调
     *
     * @param isSuccess
     */
    @Override
    public void getDataFrom_DialogFragment(boolean isSuccess) {
        //RxToast.showToast("打赏成功");
    }

    /**
     * 关注/取消关注
     */
    private void addToAttentionRelation() {
        atteStatus = beautyLogBeanList.get(mCurrentItem).queryIsAttentionRelation ? 1 : 0;
        OkGo.<String>get(Urls.ADD_ATTENTIONRELATION)//
                .tag(this)//
                .params("fromMemberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId)
                .params("toMemberName", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberName)
                //关注 0 /取消关注 1 (必填)
                .params("atteStatus", atteStatus)//关注的状态
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    if (data.optBoolean("success")) {
                                        //if (atteStatus == 0) {
                                        atteStatus = 1;
                                        RxToast.showToast("关注成功");
                                        AnimationSet animationSet = new AnimationSet(true);
                                        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0f, 1, 0f,
                                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        //3秒完成动画
                                        scaleAnimation.setDuration(500);
                                        //将AlphaAnimation这个已经设置好的动画添加到 AnimationSet中
                                        animationSet.addAnimation(scaleAnimation);
                                        //启动动画
                                        videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.iv_collect).startAnimation(animationSet);
                                        //动画完成之后隐藏控件
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.iv_collect).setVisibility(View.GONE);
                                            }
                                        }, 500);
                                        beautyLogBeanList.get(mCurrentItem).queryIsAttentionRelation = true;
                                        //}
//                                        else if (atteStatus == 1) {
//                                            atteStatus = 0;
//                                            RxToast.showToast("已取消关注");
//                                            //tvAddFriend.setText("+ 关注");
//                                        }
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
        commentNumber = Integer.parseInt(((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_comment)).getText().toString());
        OkGo.<String>get(Urls.ADD_COMMENT)
                .tag(this)
                .params("memberId", memberId)
                .params("contentId", beautyLogBeanList.get(mCurrentItem).beautyLog1.id)//帖子ID
                .params("context", context)//评论内容
                .execute(new StringDialogCallback(this, "正在发表评论...") {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObject data = new JSONObject(response.body());
                                if (data.optBoolean("success")) {
                                    RxToast.showToast("评论成功，你的评论就是我的动力~");
                                    showEditText.setText("");
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    niceDialog.dismiss();
                                    //刷新公分
                                    beautyLogIncome();
                                    commentNumber += 1;
                                    ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_comment)).setText("" + commentNumber);
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
     * 点赞、取消点赞
     */
    private void updateLikes() {
        isDianZan = beautyLogBeanList.get(mCurrentItem).likes ? 1 : 0;//是否点赞 0点赞，1为取消点赞
        OkGo.<String>get(Urls.UPDATE_LIKES)//
                .tag(this)//
                .params("logId", id)
                .params("personUrl", UserModel.getUserModel().getImgUrl())
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId)
                .params("flag", isDianZan)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                JSONObject data = jsonObjectEx.optJSONObject("data");
                                if (data.optBoolean("result")) {
                                    int likeCounts = data.optInt("count");
                                    if (isDianZan == 0) {
                                        //RxToast.showToast("已点赞");
                                        beautyLogBeanList.get(mCurrentItem).likes = true;
                                        isDianZan = 1;
                                        addSelect(videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like));
                                        ((CheckBox) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like)).setChecked(true);
                                        //                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_like)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.shiupin_zan_click), null, null);
                                    } else if (isDianZan == 1) {
                                        //RxToast.showToast("已取消点赞");
                                        isDianZan = 0;
                                        beautyLogBeanList.get(mCurrentItem).likes = false;
//                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_like)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.shipin_zan), null, null);
                                        ((CheckBox) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like)).setChecked(false);
                                    }
                                    ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_like)).setText("" + likeCounts);

                                    ScaleAnimation animation_ScaleAnimation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f,
                                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                    animation_ScaleAnimation.setDuration(300);
                                    videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like).setAnimation(animation_ScaleAnimation);

                                    //刷新公分
                                    beautyLogIncome();
                                } else {
                                    RxToast.showToast(data.optString("msg"));
                                    ((CheckBox) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like)).setChecked(beautyLogBeanList.get(mCurrentItem).likes);
                                }

                            } else {
                                RxToast.showToast(R.string.connect_to_server_fail);
                                ((CheckBox) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like)).setChecked(beautyLogBeanList.get(mCurrentItem).likes);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        RxToast.showToast(R.string.connect_to_server_fail);
                        ((CheckBox) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.shb_like)).setChecked(beautyLogBeanList.get(mCurrentItem).likes);
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
                                //刷新公分
                                beautyLogIncome();
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
                .params("memberId", myMemberId)//原创人Id
                .params("logId", logId)
                .params("myMemberId", memberId)//登陆人Id
                .params("flag", 1)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                //RxToast.showToast("挖矿公分＋1");
                                //刷新公分
                                beautyLogIncome();
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
     * 查询帖子的预计收入
     */
    private void beautyLogIncome() {
        id = String.valueOf(beautyLogBeanList.get(mCurrentItem).beautyLog1.id);
        OkGo.<String>post(Urls.beautyLogIncome)
                .tag(this)
                .params("id", id)
                .execute(new StringDialogCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                            if (jsonObjectEx.optBoolean("success")) {
                                String income = jsonObjectEx.optString("data");
                                if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_money) != null) {
                                    if (Double.valueOf(income) >= 10000) {
                                        double incomes = Double.valueOf(income);
                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_money)).setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_money1)).setText(StringUtil.rounded(incomes / 10000, 1) + "w");
                                    } else {
                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_money)).setText("" + income);
                                        ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_money1)).setText("" + income);
                                    }
                                }
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

    private void showShareView() {
        popShare = new PopShare(this);
        imageCollect = (ImageView) popShare.conentView.findViewById(R.id.image_collect);
        tvCollect = (TextView) popShare.conentView.findViewById(R.id.tv_collect);
        iv_bulr = (ImageView) popShare.conentView.findViewById(R.id.iv_bulr);

        //bitmap = StringUtil.getIerceptionScreen(this);
        //iv_bulr.setImageBitmap(bitmap);

        // 设置popWindow的显示和消失动画
        popShare.setAnimationStyle(R.style.AnimPopShareDialog);
        // 设置点击popupwindow外屏幕其它地方消失
        popShare.setOutsideTouchable(true);

        collectFlag = beautyLogBeanList.get(mCurrentItem).collects ? 1 : 0;//0收藏 1取消收藏
        if (collectFlag == 1) {
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_click_2x));
            tvCollect.setText("已收藏");
        } else {
            tvCollect.setText("收藏");
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
        }
        popShare.showAtLocation(recyclerViewVideo, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        if (beautyLogBeanList.get(mCurrentItem).isMy == 1) {
            popShare.deleteBtn(true);
        } else {
            popShare.deleteBtn(false);
        }
        popShare.setOnPopuClick(new PopShare.OnPopuClick() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.layout_collect://收藏
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            if (beautyLogBeanList.get(mCurrentItem).isMy == 1) {
                                RxToast.showToast("无法收藏自己的帖子");
                                return;
                            }
                            updateCollections();
                        } else {
                            RxToast.showToast("请先登录哦");
                        }
                        break;
                    case R.id.lay_private_lock://权限
                        Intent intent_lock = new Intent(CircleVideoActivity.this, LockActivity.class);
                        intent_lock.putExtra("contentId", "" + id);
                        intent_lock.putExtra("jurisdiction", beautyLogBeanList.get(mCurrentItem).beautyLog1.jurisdiction);//当前日志权限
                        startActivity(intent_lock);
                        popShare.dismiss();
                        break;
                    case R.id.lay_jubao://举报
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            Intent intent = new Intent(CircleVideoActivity.this, InformActivity.class);
                            intent.putExtra("contentId", "" + id);
                            intent.putExtra("memberId", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId);
                            intent.putExtra("memberPhone", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.phone);
                            intent.putExtra("memberName", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.memberName);
                            intent.putExtra("title", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.title);
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
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            if (umShareAPI.isInstall(CircleVideoActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
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
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            if (umShareAPI.isInstall(CircleVideoActivity.this, SHARE_MEDIA.WEIXIN)) {
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
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            if (umShareAPI.isInstall(CircleVideoActivity.this, SHARE_MEDIA.SINA)) {
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
                        if (StringUtil.isLogin(CircleVideoActivity.this)) {
                            if (umShareAPI.isInstall(CircleVideoActivity.this, SHARE_MEDIA.QQ)) {
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
                    case R.id.btn_cancel://点击取消关闭
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
     * 分享回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void ShareWeb(SHARE_MEDIA share_media) {
        PhotoVideoModel model = JSON.parseObject(beautyLogBeanList.get(mCurrentItem).beautyLog1.cover, PhotoVideoModel.class);
        String coverImage = model.getUrl();

        UMImage thumb = new UMImage(CircleVideoActivity.this, coverImage);
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(thumb);
        if (TextUtils.isEmpty(beautyLogBeanList.get(mCurrentItem).beautyLog1.describe)) {
            web.setDescription(getString(R.string.share_content_log));
        } else {
            web.setDescription(beautyLogBeanList.get(mCurrentItem).beautyLog1.describe);
        }
        if (!TextUtils.isEmpty(beautyLogBeanList.get(mCurrentItem).beautyLog1.title)) {
            web.setTitle(beautyLogBeanList.get(mCurrentItem).beautyLog1.title);
        } else {
            web.setTitle(getString(R.string.share_title_log));
        }
        new ShareAction(CircleVideoActivity.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
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
     * 收藏/取消收藏
     */
    private void updateCollections() {
        OkGo.<String>get(Urls.UPDATE_COLLECTIONS)//
                .tag(this)//
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", beautyLogBeanList.get(mCurrentItem).beautyLog1.memberId)
                .params("id", beautyLogBeanList.get(mCurrentItem).beautyLog1.id)
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
                                            beautyLogBeanList.get(mCurrentItem).collects = true;
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
                                            beautyLogBeanList.get(mCurrentItem).collects = false;
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
                                        beautyLogIncome();
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

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        //刷新公分
        if (myEventBusModel.VIDEO_COMMENT_SUCCESS) {
            commentNumber = myEventBusModel.commentNumber;
            ((TextView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.tv_comment)).setText("" + commentNumber);
            //刷新公分
            beautyLogIncome();
        }
        /**
         * 暂停播放
         */
        else if (myEventBusModel.VIDEO_PAUSE) {
            //RxToast.showToast("暂停播放");
            if (videoView != null) {
                videoView.onVideoPause();
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
            }
            isPause = false;
        }
        /**
         * 继续播放
         */
        else if (myEventBusModel.VIDEO_START) {
            //RxToast.showToast("继续观看");
            if (videoView != null) {
                //mVideoView.start();
                videoView.startPlayLogic();
                //暂停按钮隐藏
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
                if (videoView.getGSYVideoManager().getMediaPlayer() != null) {
                    if (videoView.getGSYVideoManager().getMediaPlayer().isPlaying()) {
                        videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
                        viewAnim.setVisibility(View.GONE);
                    } else {
                        //封面图显示
                        videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.VISIBLE);
                        viewAnim.setVisibility(View.VISIBLE);
                        videoView.onVideoResume(false);
                    }
                }
            }
            isPause = true;
        }
    }

    /**
     * 双击事件
     */
    @Override
    public void onDoubleClicks(MotionEvent event) {
        if (UserModel.getUserModel().getIsLogin()) {
            if (!beautyLogBeanList.get(mCurrentItem).likes) {
                updateLikes();
            }
        } else {

        }
        switch (event.getAction()) {
            /**
             * 点击的开始位置
             */
            case MotionEvent.ACTION_DOWN:
                final ImageView imageView = (ImageView) videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageView_heart);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.setMargins((int) event.getX() - 80, (int) event.getY() - 80, 0, 0);// 通过自定义坐标来放置你的控件
                imageView.setLayoutParams(params);

                //加载动画
                Animation animation = AnimationUtils.loadAnimation(CircleVideoActivity.this, R.anim.animation_like);
                imageView.startAnimation(animation);//开始动画
                imageView.setVisibility(View.VISIBLE);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {//动画结束
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Animation animation1 = AnimationUtils.loadAnimation(CircleVideoActivity.this, R.anim.animation_like_out);
                                imageView.startAnimation(animation1);//开始动画
                                animation1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {//动画结束
                                        imageView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }, 200);
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 单击事件
     */
    @Override
    public void onViewClicks() {
//        if (mVideoView != null && isPause) {
//            if (mVideoView.isPlaying()) {
//                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.VISIBLE);
//                mVideoView.pause();
//            } else {
//                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
//                mVideoView.start();
//            }
//        }
        if (videoView != null && isPause) {
            if (videoView.getGSYVideoManager().getMediaPlayer().isPlaying()) {
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.VISIBLE);
                videoView.onVideoPause();
            } else {
                videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.imageVideoButton).setVisibility(View.GONE);
                videoView.onVideoResume(false);
            }
        }
    }

    /**
     * 点赞动效
     *
     * @param view
     */
    public void addSelect(View view) {
        mSmallBang.bang(view, 80, new SmallBang.SmallBangListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {

            }
        });
    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener(CircleVideoActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示*/

            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏*/
                if (niceDialog != null) {
                    niceDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageNum++;
        //查询视频列表
        queryBeautyVideoLog();
    }

    /**
     * 播放完成回调
     */
    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            progressBar.setProgress(videoView.getDuration());
            //重复播放视频
            //videoView.start();
        }
    }

    /**
     * 开始播放回调
     */
    class MyPlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.anchor_img).setVisibility(View.GONE);
                        }
                        viewAnim.setVisibility(View.GONE);
                        //进度条显示
                        if (videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini) != null) {
                            videoAdapter.getViewByPosition(recyclerViewVideo, mCurrentItem, R.id.pb_mini).setVisibility(View.VISIBLE);
                        }
                    }
                    //视频加载图标监听
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        viewAnim.setVisibility(View.VISIBLE);
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                        if (mp.isPlaying()) {
                            viewAnim.setVisibility(View.GONE);
                        }
                    }
                    return true;
                }
            });
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
                                    isResume = false;

                                    String logShareUrl = data.optJSONObject("data").getString("url");

                                    PhotoVideoModel model = JSON.parseObject(beautyLogBeanList.get(mCurrentItem).beautyLog1.cover, PhotoVideoModel.class);
                                    String coverImage = model.getUrl();

                                    Intent intent = new Intent(CircleVideoActivity.this, ShareToActivity.class);
                                    intent.putExtra("ShareUrl", "" + logShareUrl);
                                    intent.putExtra("shareImage", "" + coverImage);
                                    intent.putExtra("activityId", "" + activityId);
                                    intent.putExtra("title", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.title);
                                    intent.putExtra("content", "" + beautyLogBeanList.get(mCurrentItem).beautyLog1.describe);
                                    startActivity(intent);
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
