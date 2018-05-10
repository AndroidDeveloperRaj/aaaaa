package com.merrichat.net.activity.circlefriend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.ShareToActivity;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.adapter.EvaluateDetailAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.circlefriends.AllCommentActivity;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.CircleDetailModel;
import com.merrichat.net.model.PhotoVideoModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.networklistening.ConnectivityStatus;
import com.merrichat.net.networklistening.ReactiveNetwork;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.ScreenUtils;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.view.ClearEditText;
import com.merrichat.net.view.ObservableScrollView;
import com.merrichat.net.view.PopShare;
import com.merrichat.net.view.universalvideoview.UniversalMediaController;
import com.merrichat.net.view.universalvideoview.UniversalVideoView;
import com.merrichat.net.wxapi.WXEntryActivity;
import com.othershe.nicedialog.BaseNiceDialog;
import com.othershe.nicedialog.NiceDialog;
import com.othershe.nicedialog.ViewConvertListener;
import com.othershe.nicedialog.ViewHolder;
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
 * Created by amssy on 17/11/18.
 * <p>
 * 视频详情
 */

public class CircleDetailsVideoAty extends AppCompatActivity implements UniversalVideoView.VideoViewCallback, EvaluateDetailAdapter.OnItemClickListener {
    private static final String TAG = "CircleDetailsVideoAty";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    @BindView(R.id.videoView)
    UniversalVideoView videoView;
    @BindView(R.id.media_controller)
    UniversalMediaController mediaController;
    @BindView(R.id.video_layout)
    FrameLayout videoLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_look_more)
    TextView tvLookMore;
    @BindView(R.id.lay_title_content)
    LinearLayout layTitleContent;
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
    ObservableScrollView sv;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.lay_input)
    LinearLayout layInput;

    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.tv_zan)
    TextView tvZan;
    @BindView(R.id.tv_pinglun)
    TextView tvPingLun;
    @BindView(R.id.tv_shoucang)
    TextView tvShouCang;
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;

    @BindView(R.id.iv_user)
    SimpleDraweeView ivUser;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_add_friend)
    TextView tvAddFriend;
    /**
     * 发表评论输入框
     */
    ClearEditText showEditText;
    /**
     * 评论
     */
    @BindView(R.id.recycler_view_detail_evaluate)
    RecyclerView recyclerViewEvaluate;

    /**
     * 数据流量播放视频提示
     */
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.btn_other)
    Button btnOther;
    @BindView(R.id.rel_toast)
    RelativeLayout relToast;

    /**
     * 播放按钮
     */
    @BindView(R.id.video_player)
    ImageView videoPlayer;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    UMShareAPI umShareAPI = null;
    private Context mContext;
    private int mSeekPosition;//播放的当前位置
    private String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private String id = "";//日志ID
    private String memberId = "0";//登录人的ID
    private String toMemberId = "";//发帖人的ID
    private int currentPage = 1;//当前页数
    private CircleDetailModel detailModel;
    private LinearLayoutManager layoutManager;
    private EvaluateDetailAdapter adapter;
    private int commentPosition;
    private int atteStatus = -1;//关注 0 /取消关注 1 (必填)
    private long toMemberIdAttention = 0;
    private String toMemberName = "";
    private int flag = 0;
    private ImageView imageCollect;
    private TextView tvCollect;
    private NiceDialog niceDialog;
    private List<CircleDetailModel.DataBean.CommentListBean> listComment;
    private int tab_item;
    private int isDianZan = 0;//是否点赞 0点赞，1为取消点赞
    private ArrayList<CircleDetailModel.DataBean.MemberIdBean> memberIdList = new ArrayList<>();
    private CircleDetailModel.DataBean.BeautyLogBean beautyLogBean;
    private String shareUrl = "";
    private int isMy = -1;//查看是否是本人（0不是本人 1是本人）
    private List<PhotoVideoModel> list;
    private boolean isCheckWIFI = false;// 0 连接wifi 1 未连接WiFi
    private int commentCounts = 0;//评论数
    private int likeCounts = 0;//点赞数
    private ReactiveNetwork reactiveNetwork;
    private boolean isMobileNet = false;// 0 连接mobileNet 1 未连接mobileNet
    private int jurisdiction = 1;//帖子权限
    private boolean isPlayer = true;
    private ImageButton mTurnButton;
    private String shareImage;
    private String title;
    private String content;
    private ImageView iv_bulr;
    private Bitmap bitmap;
    private PopShare popShare;
    private int activityId;
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().setBackgroundDrawableResource(R.color.background);
//        }
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //StatusBarUtil.setImgTransparent(this);
        StatusBarUtil.setTranslucent(CircleDetailsVideoAty.this);
        setContentView(R.layout.activity_circle_details_video);
        ButterKnife.bind(this);
        mContext = this;
        setVideoAreaSize();
        initGetIntent();
        //查询日志详情
        getCircleDetail();

//        int height = StringUtil.getHeight(CircleDetailsVideoAty.this);
//        double statusHeight = StringUtil.getStatusBarHeight(CircleDetailsVideoAty.this);
//
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mediaController.getLayoutParams();
//        params.height  = (int) (height - statusHeight);
//        mediaController.setLayoutParams(params);
//
//        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) videoView.getLayoutParams();
//        params1.height  = (int) (height - statusHeight);
//        videoView.setLayoutParams(params1);

        videoView.setVideoViewCallback(this);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                mp.start();
                videoPlayer.setVisibility(View.VISIBLE);
            }
        });

        //网络连接状态的监听
        reactiveNetwork = new ReactiveNetwork();
        reactiveNetwork.setNetworkEvent(new ReactiveNetwork.NetworkEvent() {
            @Override
            public void enent(ConnectivityStatus status) {
                Logger.e("获取网络状态：status.description＝＝＝" + status.description);
                if (status.description.equals("offline") || status.description.equals("unknown")) {
                    isCheckWIFI = false;
                    isMobileNet = false;
                    videoView.pause();
                    errorLayout.setVisibility(View.VISIBLE);
                    RxToast.showToast("无网络连接，请检查网络！");
                } else if (status.description.equals("connected to WiFi network")) {
                    isCheckWIFI = true;
                    isMobileNet = false;
                    errorLayout.setVisibility(View.GONE);
                    relToast.setVisibility(View.GONE);
                    if (list != null) {
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
                    errorLayout.setVisibility(View.VISIBLE);
                    relToast.setVisibility(View.GONE);
                    RxToast.showToast("无网络连接，请检查网络！");
                }
            }
        });
        reactiveNetwork.observeNetworkConnectivity(this);
        //merrichat_video_1512735113

        EventBus.getDefault().register(this);
        //播放按钮
        mTurnButton = (ImageButton) findViewById(R.id.turn_button);

        umShareAPI = UMShareAPI.get(this);
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        videoPlayer.setVisibility(View.GONE);
        //跳转暂停播放
        if (!isPlayer) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSeekPosition > 0 && videoView != null) {
            isPlayer = true;
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
    public void onPause(MediaPlayer mediaPlayer) {
        videoPlayer.setVisibility(View.VISIBLE);
        //跳转界面暂停播放
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (videoView != null) {
            videoView.stopPlayback();
        }
        UMShareAPI.get(this).release();
        if (bitmap != null) {
            bitmap.recycle();
        }
        reactiveNetwork.relese(CircleDetailsVideoAty.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            mSeekPosition = videoView.getCurrentPosition();
            videoView.pause();
            //isPlayer = false;
        }
    }

    private void initGetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("contentId");
            toMemberId = intent.getStringExtra("toMemberId");
            activityId = getIntent().getIntExtra("activityId", -1);
            tab_item = intent.getIntExtra("tab_item", 0);
            if (UserModel.getUserModel().getIsLogin()) {
                memberId = UserModel.getUserModel().getMemberId();
            } else {
                memberId = "0";
            }
        }
    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        videoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = videoLayout.getWidth();
//                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                LinearLayout.LayoutParams videoLayoutParams = (LinearLayout.LayoutParams) videoLayout.getLayoutParams();
                videoLayoutParams.width = ScreenUtils.getScreenWidth(mContext);
                videoLayoutParams.height = ScreenUtils.getScreenHeight(mContext);
                videoLayout.setLayoutParams(videoLayoutParams);
            }
        });
    }

    @OnClick({R.id.imageView_next, R.id.tv_zan, R.id.tv_pinglun, R.id.tv_shoucang, R.id.iv_right_menu, R.id.tv_look_more
            , R.id.iv_user, R.id.iv_back, R.id.tv_add_friend, R.id.show_evaluate_textView, R.id.rel_evaluate, R.id.btn_play, R.id.btn_other
            , R.id.rel_toast, R.id.show_evaluate_header, R.id.video_player})
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
            case R.id.tv_zan://点赞
                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                    updateLikes();
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.tv_pinglun://评论
                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
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
            case R.id.tv_shoucang://分享有奖
//                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
//                    shareUrl = "http://gzhgr.igomot.net/weixin-shop/xunmei/showBox/show.html?"
//                            + "st=" + System.currentTimeMillis()
//                            + "&cid=" + toMemberId//帖子创建人id
//                            + "&mid=" + memberId//分享人memberId
//                            + "&sid=" + id//帖子ID
//                            + "&source=2";
//                    Intent intent_share = new Intent(CircleDetailsVideoAty.this, ShareToActivity.class);
//                    intent_share.putExtra("ShareUrl", "" + shareUrl);
//                    intent_share.putExtra("shareImage", shareImage);
//                    intent_share.putExtra("title", "" + tvTitle.getText());
//                    intent_share.putExtra("content", "" + tvContent.getText());
//                    startActivity(intent_share);
//                } else {
//                    RxToast.showToast("登录之后就能分享了");
//                }
                break;
            case R.id.iv_user://用户头像
                if (!StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                    RxToast.showToast("请先登录哦");
                    return;
                }
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
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
                break;
            case R.id.show_evaluate_header:
                RxActivityTool.skipActivity(this, MyDynamicsAty.class);
                break;
            case R.id.tv_user://用户名
                //判断是否是自己的帖子（自己的帖子进入自己的动态，别人的帖子进入他的主页）
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
            case R.id.tv_add_friend://加关注
                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                    addToAttentionRelation();
                } else {
                    RxToast.showToast("请登录才能关注");
                }
                break;
            case R.id.iv_right_menu:
                //跳转界面暂停播放
                if (videoView != null && videoView.isPlaying()) {
                    mSeekPosition = videoView.getCurrentPosition();
                    videoView.pause();
                }
                if (MerriUtils.isFastDoubleClick2()) {
                    return;
                }
                showShareView();
                break;
            case R.id.tv_look_more://跳转到查看全文
                if (beautyLogBean != null) {
                    Intent intent = new Intent(mContext, VideoDetailAlbumAty.class);
                    intent.putExtra("CircleDetailModel", detailModel);
                    intent.putExtra("contentId", "" + id);
                    intent.putExtra("likeCounts", likeCounts);
                    intent.putExtra("commentCounts", commentCounts);
                    intent.putExtra("toMemberId", "" + toMemberId);
                    intent.putExtra("memberIdList", memberIdList);
                    intent.putExtra("isDianZan", isDianZan);
                    intent.putExtra("mSeekPosition", mSeekPosition);
                    intent.putExtra("tab_item", tab_item);
                    startActivity(intent);
                } else {

                }
                break;
            case R.id.show_evaluate_textView://点击评论
                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                    niceDialog = NiceDialog.init();
                    niceDialog
                            .setLayoutId(R.layout.item_fabu_pinglun)
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
                    RxToast.showToast("登录之后才能评论");
                }
                break;
            case R.id.rel_evaluate:
                Intent intent1 = new Intent(CircleDetailsVideoAty.this, AllCommentActivity.class);
                intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
                intent1.putExtra("DETAIL_WHO", 1);
                startActivity(intent1);
                break;
            case R.id.btn_play://继续播放
                isCheckWIFI = true;
                relToast.setVisibility(View.GONE);
                //播放视频
                videoUrl = list.get(0).getUrl();
                videoView.setVideoPath(videoUrl);
                videoView.start();
                break;
            case R.id.btn_other://分享好友
                if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                    getPromoQRcode();
                    Intent intent_share1 = new Intent(CircleDetailsVideoAty.this, ShareToActivity.class);
                    intent_share1.putExtra("ShareUrl", "" + shareUrl);
                    intent_share1.putExtra("shareImage", "" + shareImage);
                    intent_share1.putExtra("title", "" + tvTitle.getText());
                    intent_share1.putExtra("content", "" + tvContent.getText());
                    startActivity(intent_share1);
                } else {
                    RxToast.showToast("请先登录哦");
                }
                break;
            case R.id.rel_toast://用移动数据的情况

                break;
            case R.id.video_player://播放按钮
                if (videoView != null) {
                    videoView.start();
                    mTurnButton.setImageResource(R.mipmap.uvv_stop_btn);
                }
                break;
        }
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
                                        RxToast.showToast("已点赞");
                                        isDianZan = 1;
                                        CircleDetailModel.DataBean.MemberIdBean memberIdBean = new CircleDetailModel.DataBean.MemberIdBean();
                                        memberIdBean.setLikePersonId(UserModel.getUserModel().getMemberId());
                                        memberIdBean.setPersonUrl(UserModel.getUserModel().getImgUrl());
                                        memberIdList.add(memberIdBean);
                                        tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_dianzan), null, null);
                                    } else if (isDianZan == 1) {
                                        RxToast.showToast("已取消点赞");
                                        isDianZan = 0;
                                        if (memberIdList != null && memberIdList.size() > 0) {

                                            for (CircleDetailModel.DataBean.MemberIdBean memberIdBean :
                                                    memberIdList
                                                    ) {
                                                if (memberIdBean.getLikePersonId().equals(UserModel.getUserModel().getMemberId())) {
                                                    memberIdList.remove(memberIdBean);
                                                }
                                            }
                                        }
                                        tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null);
                                    }
                                    tvZan.setText("赞·" + likeCounts);
                                    //刷新公分
                                    freshComment();
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
                                            tvAddFriend.setText("已关注");
                                            RxToast.showToast("已添加关注");
                                        } else if (atteStatus == 1) {
                                            atteStatus = 0;
                                            RxToast.showToast("已取消关注");
                                            tvAddFriend.setText("+ 关注");
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
     * 收藏/取消收藏
     */
    private void updateCollections() {
        OkGo.<String>get(Urls.UPDATE_COLLECTIONS)//
                .tag(this)//
                .params("myMemberId", UserModel.getUserModel().getMemberId())
                .params("memberId", toMemberIdAttention)
                .params("id", id)
                .params("flag", flag)
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
                                        if (flag == 0) {
                                            flag = 1;
                                            RxToast.showToast("已收藏");
                                            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_click_2x));
                                            tvCollect.setText("已收藏");
                                            if (tab_item == 5) {
                                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                                myEventBusModel.FRESH_MY_COLLECTION = true;
                                                EventBus.getDefault().post(myEventBusModel);
                                            }
                                        } else if (flag == 1) {
                                            flag = 0;
                                            RxToast.showToast("已取消收藏");
                                            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
                                            tvCollect.setText("收藏");
                                            if (tab_item==5) {
                                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                                myEventBusModel.FRESH_LIST_DATA = true;
                                                EventBus.getDefault().post(myEventBusModel);
                                            }


                                        }
                                        //刷新公分
                                        freshComment();

                                    } else {
                                        if (flag == 0) {

                                            RxToast.showToast("收藏失败");
                                        } else {
                                            RxToast.showToast("取消收藏失败");
                                        }
                                    }

                                } else {
                                    if (flag == 0) {
                                        RxToast.showToast("收藏失败");

                                    } else if (flag == 1) {
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

        if (flag == 1) {
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_click_2x));
            tvCollect.setText("已收藏");
        } else {
            tvCollect.setText("收藏");
            imageCollect.setImageDrawable(getResources().getDrawable(R.mipmap.shoucang_gengduo_2x));
        }
        popShare.showAtLocation(videoView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        if (memberId.equals(toMemberId)) {
            popShare.deleteBtn(true);
        } else {
            popShare.deleteBtn(false);
        }
        popShare.setOnPopuClick(new PopShare.OnPopuClick() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.layout_collect://收藏
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
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
                        Intent intent_lock = new Intent(CircleDetailsVideoAty.this, LockActivity.class);
                        intent_lock.putExtra("contentId", "" + id);
                        intent_lock.putExtra("jurisdiction", jurisdiction);//当前日志权限
                        startActivity(intent_lock);
                        popShare.dismiss();
                        break;
                    case R.id.lay_jubao://举报
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                            Intent intent = new Intent(CircleDetailsVideoAty.this, InformActivity.class);
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
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                            if (umShareAPI.isInstall(CircleDetailsVideoAty.this, SHARE_MEDIA.WEIXIN_CIRCLE)) {
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
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                            if (umShareAPI.isInstall(CircleDetailsVideoAty.this, SHARE_MEDIA.WEIXIN)) {
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
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                            if (umShareAPI.isInstall(CircleDetailsVideoAty.this, SHARE_MEDIA.SINA)) {
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
                        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
                            if (umShareAPI.isInstall(CircleDetailsVideoAty.this, SHARE_MEDIA.QQ)) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + videoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    /**
     * 查询帖子详情
     */
    private void getCircleDetail() {
        OkGo.<String>get(Urls.QUERY_BEAUTY_LOG)
                .tag(this)
                .params("id", id)
                .params("memberId", memberId)
                .params("toMemberId", toMemberId)
                .params("currentPage", currentPage)
                .execute(new StringDialogCallback(this) {
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
//                                            MyEventBusModel eventBusModel = new MyEventBusModel();
//                                            eventBusModel.REMOVE_LOG = true;
//                                            EventBus.getDefault().post(eventBusModel);
                                            finish();
                                        }
                                        memberIdList = (ArrayList<CircleDetailModel.DataBean.MemberIdBean>) detailModelData.getMemberIdList();
                                        String memberImage = beautyLogBean.getMemberImage();
                                        String memberName = beautyLogBean.getMemberName();
                                        ivUser.setImageURI(memberImage);//用户头像
                                        showEvaluateHeader.setImageURI(UserModel.getUserModel().getImgUrl());//评论头像
                                        tvUser.setText(memberName);//用户名
                                        atteStatus = detailModelData.isQueryIsAttentionRelation() ? 1 : 0;// 是否关注的状态
                                        flag = detailModelData.isCollects() ? 1 : 0;//0收藏 1取消收藏
                                        isMy = detailModelData.getIsMy();
                                        isDianZan = detailModelData.isLikes() ? 1 : 0;//是否点赞 0点赞，1为取消点赞
                                        if (isDianZan == 1) {
                                            tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_dianzan), null, null);
                                        } else {
                                            tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null);
                                        }
                                        if (atteStatus == 1) {
                                            tvAddFriend.setText("已关注");
                                        } else {
                                            tvAddFriend.setText("+ 关注");
                                        }
                                        /**
                                         * 自己的帖子不显示关注按钮
                                         */
                                        if (TextUtils.equals(UserModel.getUserModel().getMemberId(), toMemberId)) {
                                            tvAddFriend.setVisibility(View.GONE);
                                        } else {
                                            tvAddFriend.setVisibility(View.VISIBLE);
                                        }
                                        toMemberIdAttention = beautyLogBean.getMemberId();
                                        toMemberName = beautyLogBean.getMemberName();
                                        commentCounts = beautyLogBean.getCommentCounts();
                                        likeCounts = beautyLogBean.getLikeCounts();
                                        tvZan.setText("赞·" + likeCounts);
                                        tvPingLun.setText("评论·" + commentCounts);
                                        tvComment.setText("共" + commentCounts + "条评论");

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


                                        title = beautyLogBean.getTitle();

                                        content = beautyLogBean.getContent();
                                        list = JSON.parseArray(content, PhotoVideoModel.class);
                                        tvTitle.setText(title);
                                        tvContent.setText(list.get(0).getText());

                                        PhotoVideoModel coverModel = JSON.parseObject(beautyLogBean.getCover(), PhotoVideoModel.class);
                                        shareImage = coverModel.getUrl();

                                        //判断网络是否是WIFI
                                        if (NetUtils.isWifiAvailable(CircleDetailsVideoAty.this)) {
                                            relToast.setVisibility(View.GONE);
                                            //播放视频
                                            videoUrl = list.get(0).getUrl();
                                            videoView.setVideoPath(videoUrl);
                                            videoView.start();
                                        } else {
                                            relToast.setVisibility(View.VISIBLE);
                                        }
                                        //获取当前日志权限
                                        jurisdiction = beautyLogBean.getJurisdiction();
                                        //显示评论
                                        setEvaluate();

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
                                //刷新公分
                                freshComment();
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
                                freshComment();

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
     * 显示评论（2条）
     */
    private void setEvaluate() {
        recyclerViewEvaluate.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewEvaluate.setLayoutManager(layoutManager);
        adapter = new EvaluateDetailAdapter(this, listComment);
        recyclerViewEvaluate.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

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
                                    commentCounts++;
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
                                    tvPingLun.setText("评论·" + commentCounts);
                                    tvComment.setText("共" + commentCounts + "条评论");
                                    RxToast.showToast("评论成功，你的评论就是我的动力~");
                                    showEditText.setText("");
                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    niceDialog.dismiss();
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

                                    //关闭软键盘
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(showEditText.getWindowToken(), 0);
                                    niceDialog.dismiss();

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
        if (StringUtil.isLogin(CircleDetailsVideoAty.this)) {
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
        } else {
            RxToast.showToast("请先登录哦");
        }
    }

    /**
     * 查看全部回复得点击事件
     *
     * @param position
     */
    @Override
    public void onAllHuiFuClick(int position) {
        Intent intent1 = new Intent(CircleDetailsVideoAty.this, AllCommentActivity.class);
        intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
        intent1.putExtra("DETAIL_WHO", 1);
        intent1.putExtra("contentId", String.valueOf(detailModel.getData().getBeautyLog().getId()));
        startActivity(intent1);
    }

    /**
     * 刷新评论数据
     */
    private void freshComment() {
        OkGo.<String>get(Urls.QUERY_BEAUTY_LOG)
                .tag(this)
                .params("id", id)
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

                                    CircleDetailModel.DataBean detailModelData = detailModel.getData();
                                    beautyLogBean = detailModelData.getBeautyLog();
                                    //获取当前日志权限
                                    jurisdiction = beautyLogBean.getJurisdiction();

                                    ArrayList<CircleDetailModel.DataBean.MemberIdBean> memberList = (ArrayList<CircleDetailModel.DataBean.MemberIdBean>) detailModel.getData().getMemberIdList();
                                    if (memberList != null && memberList.size() > 0) {
                                        if (memberIdList != null && memberIdList.size() > 0) {
                                            memberList.clear();
                                            memberIdList.addAll(memberList);
                                        }

                                    }
                                    commentCounts = detailModel.getData().getBeautyLog().getCommentCounts();
                                    likeCounts = detailModel.getData().getBeautyLog().getLikeCounts();
                                    isDianZan = detailModel.getData().isLikes() ? 1 : 0;//是否点赞 0点赞，1为取消点赞
                                    if (isDianZan == 1) {
                                        tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.icon_dianzan), null, null);
                                    } else {
                                        tvZan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.tuwen_dianzan2x), null, null);
                                    }
                                    tvZan.setText("赞·" + likeCounts);
                                    tvPingLun.setText("评论·" + commentCounts);
                                    tvComment.setText("共" + commentCounts + "条评论");
                                    atteStatus = detailModel.getData().isQueryIsAttentionRelation() ? 1 : 0;// 是否关注的状态
                                    if (atteStatus == 1) {
                                        tvAddFriend.setText("已关注");
                                    } else {
                                        tvAddFriend.setText("+ 关注");
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

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.COMMENT_EVALUATE1) {
            freshComment();
        } else if (myEventBusModel.COMMENT_EVALUATE4) {
            freshComment();
        } else if (myEventBusModel.DELEATE_LOG2) {
            finish();
        }
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
        UMImage thumb = new UMImage(CircleDetailsVideoAty.this, shareImage);
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
        new ShareAction(CircleDetailsVideoAty.this).withMedia(web).setPlatform(share_media).setCallback(umShareListener).share();
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
}
