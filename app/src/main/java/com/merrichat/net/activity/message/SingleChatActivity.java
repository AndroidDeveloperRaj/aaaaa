package com.merrichat.net.activity.message;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImageUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.meiyu.VideoChatActivity;
import com.merrichat.net.activity.meiyu.VoiceChatActivity;
import com.merrichat.net.activity.meiyu.fragments.Gift;
import com.merrichat.net.activity.meiyu.fragments.view.MyViewPager;
import com.merrichat.net.activity.message.cim.Constant;
import com.merrichat.net.activity.message.cim.android.CIMCacheTools;
import com.merrichat.net.activity.message.cim.android.CIMEventListener;
import com.merrichat.net.activity.message.cim.android.CIMListenerManager;
import com.merrichat.net.activity.message.cim.android.CIMPushManager;
import com.merrichat.net.activity.message.cim.constant.CIMConstant;
import com.merrichat.net.activity.message.cim.http.CIMServerData;
import com.merrichat.net.activity.message.cim.model.ReplyBody;
import com.merrichat.net.activity.message.cim.model.SentBody;
import com.merrichat.net.activity.message.gif.AnimatedGifDrawable;
import com.merrichat.net.activity.message.gif.AnimatedImageSpan;
import com.merrichat.net.activity.message.setting.SignChatSettingActivity;
import com.merrichat.net.activity.message.weidget.ChatPanelGridViewAdapter;
import com.merrichat.net.activity.message.weidget.CirclePageIndicator;
import com.merrichat.net.activity.message.weidget.SingleChatPanelBean;
import com.merrichat.net.activity.message.weidget.ViewPagerAdapter;
import com.merrichat.net.activity.my.mywallet.RechargeMoneyActivity;
import com.merrichat.net.adapter.FaceGVAdapter;
import com.merrichat.net.adapter.FaceVPAdapter;
import com.merrichat.net.adapter.GiftGridViewAdapter;
import com.merrichat.net.adapter.SingleChatAdapter;
import com.merrichat.net.api.ApiManager;
import com.merrichat.net.api.WebApiService;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.ClipOrderPayModel;
import com.merrichat.net.model.GiftListsMode;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.PresentGiftModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.VideoChatModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.MessageModelDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.rxjava.BaseSubscribe;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.FileUtils;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.LogUtil;
import com.merrichat.net.utils.Logger;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.NetUtils;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StatusBarUtil;
import com.merrichat.net.utils.camera.CapturePhotoHelper;
import com.merrichat.net.utils.camera.FolderManager;
import com.merrichat.net.utils.sound.AudioRecorderButton;
import com.merrichat.net.utils.sound.MediaManage;
import com.merrichat.net.view.BottomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.dreamtobe.kpswitch.widget.KPSwitchRootLinearLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;

import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_INFO_KEY;
import static com.merrichat.net.activity.message.MessageVideoCallAty.CALL_VIDEO_TYPE_KEY;
import static com.merrichat.net.activity.message.weidget.ChatPanelGridViewAdapter.SingleChatPanelName;
import static com.merrichat.net.activity.message.weidget.ChatPanelGridViewAdapter.SingleChatPanelName_;
import static com.merrichat.net.activity.message.weidget.ChatPanelGridViewAdapter.SingleChatPanelResId;
import static com.merrichat.net.activity.message.weidget.ChatPanelGridViewAdapter.SingleChatPanelResId_;

/**
 * Created by amssy on 17/11/6.
 * 聊天页面--单聊
 */

public class SingleChatActivity extends AppCompatActivity implements CIMEventListener, SwipeRefreshLayout.OnRefreshListener, AudioRecorderButton.AudioFinishRecorderListenter, ChatPanelGridViewAdapter.OnItemClickListener, SingleChatAdapter.IReciveMeiBi {

    public static int item_single_chat_panel_grid_num = 8;//每一页中GridView中item的数量
    public static int item_single_chat_panel_grid_num_columns = 4;//gridview一行展示的数目
    public static final int activityId = MiscUtil.getActivityId();
    /**
     * 录制视频请求码
     */
    public final static int REQUEST_VIDEO_CODE = 1;
    /**
     * 输入红包请求码
     */
    public final static int REQUEST_RED_PACKAGE_CODE = 2;
    /**
     * 拆红包请求码
     */
    public final static int REQUEST_CHAI_RED_PACKAGE_CODE = 3;
    /**
     * 选图片
     */
    public final static int REQUEST_SELECT_PHOTO_CODE = 4;
    /**
     * 位置
     */
    public final static int REQUEST_LOCATION_CODE = 5;
    /**
     * 转账
     */
    public final static int REQUEST_ZHUANZHANG_CODE = 6;
    /**
     * 发布交易
     */
    public final static int REQUEST_JIAOYI_CODE = 7;
    /**
     * 转账
     */
    public final static int REQUEST_ZHUANZHANG_DETIAL_CODE = 8;


    public static String receiverMemberId;
    /**
     * 语音类型
     */
    private final int VOICE_TYPE = 1;
    /**
     * 文字类型
     */
    private final int TEXT_TYPE = 2;
    /**
     * 图片类型
     */
    private final int IMAGE_TYPE = 3;
    /**
     * 视频类型
     */
    private final int VIDEO_TYPE = 4;
    /**
     * 视频聊天
     */
    private final int VIDEO_CHAT = 0x100;
    /**
     * 音频聊天
     */
    private final int AUDIO_CHAT = 0x200;
    /**
     * title
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.lv_message)
    ListView lvMessage;

    /**
     * 两者不是好友
     * 互相添加  显示状态
     */
    @BindView(R.id.lay_add)
    LinearLayout layAdd;
    @BindView(R.id.tv_add_agree)
    TextView tvAddAgree;
    @BindView(R.id.tv_add_text)
    TextView tvAddText;
    /**
     * 语音键盘切换按钮
     */
    @BindView(R.id.voice_text_switch_iv)
    ImageView voiceTextSwitchIv;
    /**
     * 输入框
     */
    @BindView(R.id.send_edt)
    EditText sendEdt;
    /**
     * 按住说话
     */
    @BindView(R.id.send_voice_btn)
    AudioRecorderButton sendVoiceBtn;
    /**
     * 表情按钮
     */
    @BindView(R.id.iv_face)
    ImageView ivFace;
    /**
     * +号按钮
     */
    @BindView(R.id.iv_plus)
    ImageView ivPlus;
    /**
     * 发送按钮
     */
    @BindView(R.id.btn_send)
    TextView btnSend;
    /**
     * 下面输入框布局
     */
    @BindView(R.id.sendMsgLayout)
    LinearLayout sendMsgLayout;

    /**
     * 功能面板
     */
    @BindView(R.id.vp_single_panel)
    MyViewPager vpSinglePanel;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

//    /**
//     * 照片
//     */
//    @BindView(R.id.lay_chatting_picture)
//    LinearLayout layChattingPicture;
//    /**
//     * 拍照
//     */
//    @BindView(R.id.lay_chatting_take_photo)
//    LinearLayout layChattingTakePhoto;
//    /**
//     * 小视频
//     */
//    @BindView(R.id.lay_chatting_samall_video)
//    LinearLayout layChattingSamallVideo;
//    /**
//     * 视频电话
//     */
//    @BindView(R.id.lay_chatting_video_call)
//    LinearLayout layChattingVideoCall;
//    /**
//     * 语音电话
//     */
//    @BindView(R.id.lay_chatting_voice_call)
//    LinearLayout layChattingVoiceCall;
//    /**
//     * 发红包
//     */
//    @BindView(R.id.lay_chatting_red_package)
//    LinearLayout layChattingRedPackage;
//    /**
//     * 送礼物
//     */
//    @BindView(R.id.lay_chatting_liwu)
//    LinearLayout layChattingLiwu;
    /**
     * /**
     * lay_top
     */
//    @BindView(R.id.lay_top)
//    LinearLayout layTop;
//    /**
//     * /**
//     * lay_bottom
//     */
//    @BindView(R.id.lay_bottom)
//    LinearLayout layBottom;
    /**
     * 表情viewpager
     */
    @BindView(R.id.face_viewpager)
    ViewPager faceViewpager;
    /**
     * 表情页小点指示器
     */
    @BindView(R.id.face_dots_container)
    LinearLayout faceDotsContainer;
    /**
     * 表情布局
     */
    @BindView(R.id.ll_face_book)
    LinearLayout llFaceBook;

    /**
     * 功能面板
     */
    @BindView(R.id.ll_sub_panel)
    LinearLayout llSubPanel;
    /**
     * 底部扩展面板总布局
     */
    @BindView(R.id.panel_root)
    KPSwitchPanelLinearLayout panelRoot;
    /**
     * 根布局
     */
    @BindView(R.id.rootView)
    KPSwitchRootLinearLayout rootView;

    /**
     * 礼物布局
     * ****************************************************************
     */
    @BindView(R.id.view_pager)
    MyViewPager vpGift;
    @BindView(R.id.submit)
    TextView giftSubmit;
    @BindView(R.id.coin_count)
    TextView coinCount;
    @BindView(R.id.tv_chongzhi)
    TextView tv_chongzhi;
    @BindView(R.id.gif_ll)
    LinearLayout gif_ll;
    @BindView(R.id.tv_dialog_title)
    TextView tvDialogTitle;
    @BindView(R.id.radio_group)
    RadioGroup radio_group;
    @BindView(R.id.iv_gift_close)
    ImageView iv_gift_close;
    @BindView(R.id.vl_line1)
    View vlLine1;
    @BindView(R.id.vl_line2)
    View vlLine2;
    /**
     * 礼物布局
     * ****************************************************************
     */

    private List<MessageModel> msgList;
    private SingleChatAdapter chatAdapter;
    /**
     * 存放表情图片的list集合
     */
    private List<String> staticFacesList;
    private List<View> views = new ArrayList<View>();
    // 7列3行
    private int columns = 7;
    private int rows = 3;
    /**
     * 编辑框当前输入类型（语音或文本）
     */
    private int editBoxInputType = TEXT_TYPE;
    /**
     * 当前右下角按钮显示的是否是"+"号
     */
    private boolean typeIsJia = true;
    /**
     * 当前消息类型
     */
    private int typeChatNow;
    private String receiverHeadUrl;
    private String receiverName;
    private Context mContext;
    /**
     * 当前显示的剩余条数
     */
    private int startItemNum = 0;
    /**
     * 每页最大条数
     */
    private int pageSize = 20;

    private boolean isFriend;// true 好友 false 陌生人

    /**
     * 查询的当前页
     */
    private int currentPage;

    private CapturePhotoHelper mCapturePhotoHelper;
    private int activityIdFrom;
    private ArrayList<Gift> catogarys;//礼物数据
    private List<View> gridViews;//礼物view
    private int giftShowNum = 4;//每页多少个
    private int selectPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setBackgroundDrawableResource(R.color.background);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 38);
        setContentView(R.layout.activity_chat_with_one);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        AppManager.getAppManager().addActivity(this);
        sendVoiceBtn.setAudioFinishRecorderListenter(this);
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.icon_chatting_right_tip);
        mContext = this;
        receiverMemberId = getIntent().getStringExtra("receiverMemberId");
        receiverHeadUrl = getIntent().getStringExtra("receiverHeadUrl");
        receiverName = getIntent().getStringExtra("receiverName");
        activityIdFrom = getIntent().getIntExtra("activityId", -1);

        mCapturePhotoHelper = new CapturePhotoHelper(this, FolderManager.getPhotoFolder());
        tvTitleText.setText(receiverName);

        //查询是否有好友请求
        queryGoodFriendRequest();
        InitFaceViewPager();
        //消息服务器的监听
        CIMListenerManager.registerMessageListener(this, this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);
        initData();
        //键盘适配
        initKeyBoard();
        //输入的监听
        inputListener();
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.SINGLE_MESSAGE_IS_REFRESH) {//刷新消息列表的广播
            initData();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initGift() {
        vpGift.heightLevel = 1;
        gif_ll.setVisibility(View.VISIBLE);
        gif_ll.setBackgroundColor(Color.WHITE);
        tvDialogTitle.setTextColor(Color.BLACK);
        coinCount.setTextColor(Color.BLACK);
        vlLine1.setBackgroundResource(R.color.background);
        vlLine2.setBackgroundResource(R.color.background);
        iv_gift_close.setImageResource(R.mipmap.login_close);
        //钱包余额请求
        getWalletInfo();
        catogarys = new ArrayList<Gift>();
        getGifts();
        tv_chongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gif_ll.setVisibility(View.GONE);
                startActivity(new Intent(mContext, RechargeMoneyActivity.class));
            }
        });
    }

    public void getGifts() {
        OkGo.<String>post(Urls.findGift)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        GiftListsMode mode = new Gson().fromJson(response.body(), GiftListsMode.class);
                        if (mode.success) {
                            if (mode.data.size() > 0) {
                                catogarys = mode.data;
                                initViewPager(catogarys);
                            }
                        } else {
                            Toast.makeText(mContext, "获取礼物列表失败！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(mContext, "没有网络了，检查一下吧！", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void initViewPager(final ArrayList<Gift> gifts) {
        gridViews = new ArrayList<View>();
        radio_group.removeAllViews();
        int gift_page;
        if (gifts.size() % giftShowNum == 0) {
            gift_page = gifts.size() / giftShowNum;
        } else {
            gift_page = gifts.size() / giftShowNum + 1;
        }

        ///定义第一个GridView
        for (int i = 0; i < (gift_page); i++) {
            GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.grid_fragment_home, null);
            final GiftGridViewAdapter giftGridViewAdapter = new GiftGridViewAdapter(mContext, i, giftShowNum);

            List<Gift> gifts1 = new ArrayList<>();
            for (int j = 0; j < giftShowNum; j++) {
                int temp = i * giftShowNum + j;
                if (temp < gifts.size()) {
                    gifts1.add(gifts.get(temp));
                }

            }
            giftGridViewAdapter.setGifts(gifts1);
            gridView.setAdapter(giftGridViewAdapter);
            final int page = i;
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectPosition = page * giftShowNum + position;
                    Log.e("---->>>", selectPosition + "");
                    giftGridViewAdapter.setSelectedPosition(position);
                    giftGridViewAdapter.notify(page);
                }
            });
            gridViews.add(gridView);

            RadioButton rb = new RadioButton(mContext);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = DensityUtils.dp2px(mContext, 5);
            params.width = DensityUtils.dp2px(mContext, 5);
            params.setMargins(DensityUtils.dp2px(mContext, 3), 0, DensityUtils.dp2px(mContext, 3), 0);
            rb.setLayoutParams(params);
            rb.setBackgroundResource(R.drawable.bg_red_radio);
            rb.setButtonDrawable(null);
            radio_group.addView(rb);
            ((RadioButton) radio_group.getChildAt(0)).setChecked(true);

        }


        giftSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPosition == -1) {
                    RxToast.showToast("请选择一个礼物");
                } else {
                    Log.e("---->>>", selectPosition + "setOnClickListener");
//                    onGridViewClickListener.click(catogarys.get(selectPosition));
                    senGift(catogarys.get(selectPosition));
                }
            }
        });
        ///定义viewpager的PagerAdapter
        vpGift.setAdapter(new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;

            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return gridViews.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(gridViews.get(position));
                //super.destroyItem(container, position, object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(gridViews.get(position));
                return gridViews.get(position);
            }
        });
        ///注册viewPager页选择变化时的响应事件
        vpGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton radioButton = (RadioButton) radio_group.getChildAt(position);
                radioButton.setChecked(true);
                Log.e("---->>>", selectPosition + " onPageSelected");
                if (giftShowNum != 0 && selectPosition / giftShowNum == position) {

                } else {
                    ((GiftGridViewAdapter) ((GridView) gridViews.get(position)).getAdapter()).setSelectedPosition(-1);
                    ((GiftGridViewAdapter) ((GridView) gridViews.get(position)).getAdapter()).notifyDataSetChanged();
                }
            }
        });
    }

    //键盘适配
    private void initKeyBoard() {
        //面板自适应高度，即：有多高显示多高
        panelRoot.setIgnoreRecommendHeight(true);
        //键盘弹起隐藏 的监听
        KeyboardUtil.attach(this, panelRoot,
                // Add keyboard showing state callback, do like this when you want to listen in the
                // keyboard's show/hide change.
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        Logger.e("isShowing" + isShowing);
                        if (isShowing) {
                            gif_ll.setVisibility(View.GONE);
                        }
                    }
                });
        KPSwitchConflictUtil.attach(panelRoot, sendEdt,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        gif_ll.setVisibility(View.GONE);
                        if (switchToPanel) {
                            if (editBoxInputType == VOICE_TYPE) {
                                sendEdt.setVisibility(View.VISIBLE);
                                sendVoiceBtn.setVisibility(View.GONE);
                                voiceTextSwitchIv.setImageResource(R.mipmap.icon_chatting_voice_btn);
                                editBoxInputType = TEXT_TYPE;
                                typeChatNow = TEXT_TYPE;
                            }
                            sendEdt.clearFocus();
                            Logger.e("clearFocus");
                        } else {
                            sendEdt.requestFocus();
                            Logger.e("requestFocus");
                        }
                    }
                },
                new KPSwitchConflictUtil.SubPanelAndTrigger(llSubPanel, ivPlus),
                new KPSwitchConflictUtil.SubPanelAndTrigger(llFaceBook, ivFace));
        //触摸消息列表，键盘或面板隐藏
        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
                }
                return false;
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        try {
            //        判断消息列表中是否有此聊天记录
            MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.SenderId.eq(receiverMemberId),
                    MessageListModleDao.Properties.PrivateID.eq(String.valueOf(UserModel.getUserModel().getMemberId()))).unique();
            if (messageListModle != null) {
                if ("-1".equals(messageListModle.getFileType())) {
                    //草稿
                    sendEdt.setText(messageListModle.getLast());
                }
            } else {
                messageListModle = new MessageListModle();
                messageListModle.setSenderId(receiverMemberId);
                messageListModle.setType(Constant.MessageType.TYPE_1);
                messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));
                messageListModle.setHeadUrl(receiverHeadUrl);
                messageListModle.setName(receiverName);
                messageListModle.setMsgts(System.currentTimeMillis());
                messageListModle.setFileType("-2");
                messageListModle.setLast("");
                messageListModle.setText1("0");
                MessageListModle.setMessageListModle(messageListModle);
            }
            startItemNum = MessageModel.getListMessageModel(receiverMemberId).size();
            currentPage = startItemNum / pageSize - 1;
            pageSize = 20 + 19;
//        if (startItemNum - pageSize > 0) {
//            startItemNum -= 20;
//        } else {
//            startItemNum = 0;
//        }
            msgList = MessageModel.getListMessageModelByLimit(receiverMemberId, currentPage, pageSize);
//        msgList = MessageModel.getListMessageModel(receiverMemberId);
            chatAdapter = new SingleChatAdapter(mContext, msgList);
            chatAdapter.setiReciveMeiBi((SingleChatActivity) mContext);
            lvMessage.setAdapter(chatAdapter);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    lvMessage.setSelection(chatAdapter.getCount() - 1);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    R.id.lay_chatting_picture, R.id.lay_chatting_take_photo, R.id.lay_chatting_samall_video,
//    R.id.lay_chatting_video_call, R.id.lay_chatting_voice_call, R.id.lay_chatting_red_package,
//    R.id.lay_chatting_liwu,
    @OnClick({R.id.voice_text_switch_iv, R.id.send_voice_btn, R.id.iv_face, R.id.iv_plus, R.id.btn_send,
            R.id.iv_back, R.id.iv_gift_close, R.id.tv_add_agree, R.id.tv_right_img})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.voice_text_switch_iv://语音键盘切换
                if (editBoxInputType == TEXT_TYPE) {//当前输入框状态是文本输入
                    //切换到语音输入，隐藏底部布局
                    voiceTextSwitchIv.setImageResource(R.mipmap.icon_chat_keyboard);
                    sendEdt.setVisibility(View.GONE);
                    sendVoiceBtn.setVisibility(View.VISIBLE);
                    editBoxInputType = VOICE_TYPE;
                    typeChatNow = VOICE_TYPE;
                    //隐藏软键盘
                    KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
                } else {
                    //当前显示的是语音,显示输入框
                    sendEdt.setVisibility(View.VISIBLE);
                    sendVoiceBtn.setVisibility(View.GONE);
                    voiceTextSwitchIv.setImageResource(R.mipmap.icon_chatting_voice_btn);
                    editBoxInputType = TEXT_TYPE;
                    typeChatNow = TEXT_TYPE;
                    KeyboardUtil.showKeyboard(sendEdt);
                    lvMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            lvMessage.setSelection(ListView.FOCUS_DOWN);
                        }
                    });
                }
                break;
            case R.id.send_voice_btn://按住录音

                break;
            case R.id.iv_face://表情键盘切换

                break;
            case R.id.iv_plus://加号
                break;
            case R.id.tv_right_img:
                startActivity(new Intent(mContext, SignChatSettingActivity.class).putExtra("receiverMemberId", receiverMemberId));
                break;
            case R.id.btn_send://发送
                String sendContent = sendEdt.getText().toString().trim();
                if (TextUtils.isEmpty(sendContent)) {
                    GetToast.useString(mContext, "不能发送空白消息");
                    return;
                }
                if (!NetUtils.isNetworkAvailable(mContext)) {
                    GetToast.useString(mContext, "网络不可用，请稍后重试");
                    return;
                }
                //发送
                if (typeChatNow == TEXT_TYPE) {
                    if (!MerriApp.isCIMConnectionSatus) {
                        GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
                        String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
                        int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
                        if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                            // 连接服务端(即时聊天)
                            CIMPushManager.init(mContext, host, port);
                        } else {
                            CIMPushManager.bindAccount(mContext, String.valueOf(UserModel.getUserModel().getMemberId()));
                        }
                        //异步获取CIM服务器连接状态
                        CIMPushManager.detectIsConnected(mContext);
                    } else {

                        SentBody sentBody = new SentBody();
                        sentBody.setKey(Constant.SENT_BODY_KEY);
                        sentBody.put("sender", String.valueOf(UserModel.getUserModel().getMemberId()));
                        sentBody.put("senderName", UserModel.getUserModel().getRealname());
                        sentBody.put("receiverName", receiverName);
                        sentBody.put("receiver", receiverMemberId);
                        sentBody.put("type", "1");
                        sentBody.put("fileType", "5");
                        sentBody.put("content", sendContent);
                        sentBody.put("file", "");
                        //添加到Message中
                        MessageModel message = new MessageModel();
                        message.setTimestamp(sentBody.getTimestamp());
                        message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
                        message.setSenderName(UserModel.getUserModel().getRealname());
                        message.setReceiverName(receiverName);
                        message.setReceiver(receiverMemberId);
                        message.setType("1");
                        message.setFileType("5");
                        message.setContent(sendContent);
                        message.setPrivate_id(UserModel.getUserModel().getMemberId());
                        MessageModel.setMessageModel(message);
                        msgList.add(message);
                        chatAdapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        //发送消息
                        CIMPushManager.sendRequest(mContext, sentBody);
                        sendEdt.setText("");
                    }
                }
                break;
//            case R.id.lay_chatting_picture://照片
//                select_photo();
//                break;
//            case R.id.lay_chatting_take_photo://拍照
//                mCapturePhotoHelper.capture();
//                typeChatNow = IMAGE_TYPE;
//                break;
//            case R.id.lay_chatting_samall_video://小视频
//                typeChatNow = VIDEO_TYPE;
//                intent = new Intent(mContext, RecordVideoActivity.class);
//                startActivityForResult(intent, REQUEST_VIDEO_CODE);
//                break;
//            case R.id.lay_chatting_video_call://视频通话
//                if (!MerriApp.socket.connected()) {
//                    getConnectionState();
//                }
//                PrefAppStore.setIsFriend(SingleChatActivity.this, 1);
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("toMemberId", receiverMemberId);
//                    jsonObject.put("toMemberName", receiverName);
//                    jsonObject.put("toHeadImgUrl", receiverHeadUrl);
//                    startActivityForResult(new Intent(mContext, MessageVideoCallAty.class)
//                            .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_CALLER_TYPE)
//                            .putExtra(CALL_VIDEO_INFO_KEY, jsonObject.toString()), VIDEO_CHAT
//                    );
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.lay_chatting_voice_call://语音通话
//                if (!MerriApp.socket.connected()) {
//                    getConnectionState();
//                }
//                JSONObject jsonObject1 = new JSONObject();
//                try {
//                    jsonObject1.put("toMemberId", receiverMemberId);
//                    jsonObject1.put("toMemberName", receiverName);
//                    jsonObject1.put("toHeadImgUrl", receiverHeadUrl);
//                    startActivityForResult(new Intent(mContext, MessageVoiceCallAty.class)
//                            .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_CALLER_TYPE)
//                            .putExtra(CALL_VIDEO_INFO_KEY, jsonObject1.toString()), AUDIO_CHAT);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.lay_chatting_red_package://发红包
//                startActivityForResult(new Intent(mContext, RedEnvelopesActivity.class).putExtra("collectMemberId", receiverMemberId), REQUEST_RED_PACKAGE_CODE);
//                break;
//            case R.id.lay_chatting_liwu://送礼物
//                if (panelRoot.getVisibility() == View.VISIBLE) {
//                    KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
//                }
//                //初始化礼物
//                initGift();
//                break;
            case R.id.iv_gift_close:
                gif_ll.setVisibility(View.GONE);
                break;
            case R.id.tv_add_agree:
                if (tvAddAgree.getText().toString().equals("添加")) {
                    addGoodFriends();
                } else if (tvAddAgree.getText().toString().equals("同意")) {
                    agreeFriendsRequest();
                }

                break;
        }
    }

    /**
     * 弹出送礼物 dialog
     */
    private void sendGiftDialog() {


        if (panelRoot.getVisibility() == View.VISIBLE) {
            KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
        }
        //初始化礼物
        initGift();

//        FragmentGiftDialog.newInstance().setOnGridViewClickListener(4, new FragmentGiftDialog.OnGridViewClickListener() {
//            @Override
//            public void click(Gift gift) {
//                senGift(gift);
//            }
//        }).show(getSupportFragmentManager(), "dialog");
    }


    /**
     * 赠送礼物
     *
     * @param gift
     */
    public void senGift(final Gift gift) {
        ApiManager.getApiManager().getService(WebApiService.class).clipOrderPay(UserModel.getUserModel().getMemberId(),
                UserModel.getUserModel().getMemberId(),
                UserModel.getUserModel().getAccountId(),
                receiverMemberId,
                "",
                gift.giftPrice,
                gift.giftName,
                3,
                "测试",
                gift.giftId + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<ClipOrderPayModel>() {
                    @Override
                    public void onNext(ClipOrderPayModel clipOrderPayModel) {
                        if (clipOrderPayModel.success) {
                            float cashBalance = Float.parseFloat(PrefAppStore.getCashBalance(mContext)) - gift.giftPrice;
                            PrefAppStore.setCashBalance(mContext, cashBalance + "");
                            presentGift(gift, receiverMemberId);
                        } else {
                            RxToast.showToast(clipOrderPayModel.error_msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RxToast.showToast("没有网络了，检查一下吧～！");
                    }
                });
    }

    /**
     * @param gift
     * @param toMemberId 赠送完礼物进行通知服务器
     */
    public void presentGift(final Gift gift, String toMemberId) {
        ApiManager.getApiManager().getService(WebApiService.class).presentGift(UserModel.getUserModel().getMemberId(),
                toMemberId,
                gift.giftId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<PresentGiftModel>() {
                    @Override
                    public void onNext(PresentGiftModel presentGiftModel) {
                        if (presentGiftModel.success) {
                            if (presentGiftModel.data.success) {
                                giftMessage(gift);
                                Log.e("---->>>>", presentGiftModel.data.message);
                            } else {
                                Log.e("---->>>>", presentGiftModel.data.message);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, "没有网络了，检查一下吧～！", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * 发送礼物消息
     */

    private void giftMessage(final Gift gift) {
        OkGo.<String>post(Urls.giftMessage)
                .params("hairMemberId", UserModel.getUserModel().getMemberId())
                .params("collectMemberId", receiverMemberId)
                .params("gifUrl", gift.giftUrl)
                .params("giftName", gift.giftName)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                sendGiftMessage(gift.giftUrl);
                                gif_ll.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 查询是否有好友请求
     */

    private void queryGoodFriendRequest() {
        OkGo.<String>post(Urls.queryGoodFriendRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("toMemberId", receiverMemberId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                int state = data.optInt("state");
                                yaoQingId = data.optString("id");
                                if (state == 0) {
                                    layAdd.setVisibility(View.VISIBLE);
                                    tvAddAgree.setText("添加");
                                    tvAddAgree.setTextColor(Color.parseColor("#FFFFFF"));
                                    tvAddAgree.setBackgroundResource(R.drawable.shape_button_video_music);
                                    isFriend = false;
                                } else if (state == 1) {
                                    layAdd.setVisibility(View.VISIBLE);
                                    tvAddAgree.setText("等待同意");
                                    tvAddAgree.setTextColor(Color.parseColor("#888888"));
                                    tvAddAgree.setBackground(null);
                                    isFriend = false;
                                } else if (state == 2) {

                                    layAdd.setVisibility(View.VISIBLE);
                                    tvAddText.setText("对方申请加你为好友");
                                    tvAddAgree.setText("同意");
                                    tvAddAgree.setTextColor(Color.parseColor("#FFFFFF"));
                                    tvAddAgree.setBackgroundResource(R.drawable.shape_button_video_music);
                                    isFriend = false;
                                } else {
                                    layAdd.setVisibility(View.GONE);
                                    isFriend = true;
                                }

                                if (activityIdFrom == HisYingJiAty.activityId && !isFriend) {
                                    sendGiftDialog();
//                                    layBottom.removeView(layChattingVoiceCall);
//                                    layBottom.removeView(layChattingRedPackage);
//                                    layBottom.removeView(layChattingLiwu);
//                                    layTop.removeView(layChattingVideoCall);
//                                    layTop.addView(layChattingLiwu);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initChatPanel();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initChatPanel() {
        //初始化ViewPager
        vpSinglePanel.heightLevel = 2;
        List<SingleChatPanelBean> dataList = new ArrayList<>();
        List<GridView> gridList = new ArrayList<>();
        if (isFriend) {
            for (int i = 0; i < ChatPanelGridViewAdapter.SingleChatPanelResId.length; i++) {
                dataList.add(new SingleChatPanelBean(SingleChatPanelName[i], SingleChatPanelResId[i]));
            }
            //圆点指示器
            indicator.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < ChatPanelGridViewAdapter.SingleChatPanelResId_.length; i++) {
                dataList.add(new SingleChatPanelBean(SingleChatPanelName_[i], SingleChatPanelResId_[i]));
            }
            //圆点指示器
            indicator.setVisibility(View.GONE);
        }
        //计算viewpager一共显示几页
        int pageSize = dataList.size() % item_single_chat_panel_grid_num == 0
                ? dataList.size() / item_single_chat_panel_grid_num
                : dataList.size() / item_single_chat_panel_grid_num + 1;
        // 获取页数
        for (int i = 0; i < pageSize; i++) {
            GridView gridView = new GridView(mContext);
            ChatPanelGridViewAdapter adapter = new ChatPanelGridViewAdapter(dataList, i);
            gridView.setNumColumns(item_single_chat_panel_grid_num_columns);
            gridView.setAdapter(adapter);
            gridList.add(gridView);
            adapter.setOnItemClickListener(this);
        }
        ViewPagerAdapter chatVpAdapter = new ViewPagerAdapter(gridList);
        vpSinglePanel.setAdapter(chatVpAdapter);
        vpSinglePanel.setOffscreenPageLimit(2);
        indicator.setViewPager(vpSinglePanel);
    }

    @Override
    public void onItemClick(SingleChatPanelBean dataBean) {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            GetToast.useString(mContext, "网络不可用，请稍后重试");
            return;
        }
        Intent intent;
        if (dataBean.name.equals("照片")) {
            select_photo();
        } else if (dataBean.name.equals("拍照")) {
            mCapturePhotoHelper.capture();
            typeChatNow = IMAGE_TYPE;
        } else if (dataBean.name.equals("小视频")) {
            typeChatNow = VIDEO_TYPE;
            intent = new Intent(mContext, SmallVideoRecoderAty.class);
            startActivityForResult(intent, REQUEST_VIDEO_CODE);
        } else if (dataBean.name.equals("视频通话")) {
            showCallDialog();
        } else if (dataBean.name.equals("红包")) {
            startActivityForResult(new Intent(mContext, RedEnvelopesActivity.class).putExtra("collectMemberId", receiverMemberId), REQUEST_RED_PACKAGE_CODE);
        } else if (dataBean.name.equals("礼物")) {
            if (panelRoot.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
            }
            //初始化礼物
            initGift();
        } else if (dataBean.name.equals("发布交易")) {
            startActivityForResult(new Intent(mContext, SendDealAty.class).putExtra("groupType", 1), REQUEST_JIAOYI_CODE);
        } else if (dataBean.name.equals("转账")) {
            startActivityForResult(new Intent(mContext, ZhuanZhangAty.class).putExtra("collectMemberId", receiverMemberId), REQUEST_ZHUANZHANG_CODE);
        } else if (dataBean.name.equals("位置")) {
            startActivityForResult(new Intent(mContext, SendLocationAty.class).putExtra("type", 1), REQUEST_LOCATION_CODE);
        }
    }

    /**
     * 视频通话语音通话dialog
     */
    private void showCallDialog() {
        final BottomDialog bottomDialog = new BottomDialog(mContext, "视频通话", "语音通话");
        bottomDialog.showAnim(null);
        bottomDialog.show();
        bottomDialog.setOnViewClick(new BottomDialog.OnViewClick() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_first://视频通话
                        if (!MerriApp.socket.connected()) {
                            getConnectionState();
                        }
                        PrefAppStore.setIsFriend(SingleChatActivity.this, 1);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("toMemberId", receiverMemberId);
                            jsonObject.put("toMemberName", receiverName);
                            jsonObject.put("toHeadImgUrl", receiverHeadUrl);
//                            startActivityForResult(new Intent(mContext, MessageVideoCallAty.class)
//                                    .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_CALLER_TYPE)
//                                    .putExtra(CALL_VIDEO_INFO_KEY, jsonObject.toString()), VIDEO_CHAT
//                            );


                            startActivityForResult(new Intent(SingleChatActivity.this, VideoChatActivity.class)
                                    .putExtra(CALL_VIDEO_TYPE_KEY, MessageVideoCallAty.CALL_VIDEO_CALLER_TYPE)
                                    .putExtra("toMemberId", receiverMemberId)
                                    .putExtra("toMemberName", receiverName)
                                    .putExtra("toHeadImgUrl", receiverHeadUrl)
                                    .putExtra("callType", "1"), VIDEO_CHAT);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.tv_second://语音通话
                        if (!MerriApp.socket.connected()) {
                            getConnectionState();
                        }

                        JSONObject jsonObject1 = new JSONObject();
                        try {
                            jsonObject1.put("toMemberId", receiverMemberId);
                            jsonObject1.put("toMemberName", receiverName);
                            jsonObject1.put("toHeadImgUrl", receiverHeadUrl);

                            startActivityForResult(new Intent(mContext, VoiceChatActivity.class)
                                    .putExtra(CALL_VIDEO_TYPE_KEY, CALL_VIDEO_CALLER_TYPE)
                                    .putExtra("toMemberId", receiverMemberId)
                                    .putExtra("toMemberName", receiverName)
                                    .putExtra("toHeadImgUrl", receiverHeadUrl)
                                    .putExtra("callType", "0")
                                    .putExtra(CALL_VIDEO_INFO_KEY, jsonObject1.toString()), AUDIO_CHAT
                            );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        });
    }


    String yaoQingId;

    /**
     * 添加好友——接口
     */
    private void addGoodFriends() {
        OkGo.<String>post(Urls.addGoodFriends)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", receiverMemberId)
                .params("toMemberName", receiverName)
                .params("toMemberUrl", receiverHeadUrl)
                .params("source", "0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                yaoQingId = data.optString("id");
                                RxToast.showToast(data.optString("message"));
                                tvAddAgree.setText("等待同意");
                                tvAddAgree.setTextColor(Color.parseColor("#888888"));
                                tvAddAgree.setBackground(null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }


    /**
     * 同意——接口
     */
    private void agreeFriendsRequest() {
        OkGo.<String>post(Urls.agreeFriendsRequest)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("memberName", UserModel.getUserModel().getRealname())
                .params("memberUrl", UserModel.getUserModel().getImgUrl())
                .params("toMemberId", receiverMemberId)
                .params("toMemberName", receiverName)
                .params("toMemberUrl", receiverHeadUrl)
                .params("id", yaoQingId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                RxToast.showToast("已同意对方的邀请");
                                layAdd.setVisibility(View.GONE);
                            } else {
                                RxToast.showToast(jsonObject.optString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 发送礼物
     *
     * @param giftUrl 礼物图标
     */
    private void sendGiftMessage(String giftUrl) {
        //添加到Message中
        MessageModel message = new MessageModel();
        message.setTimestamp(System.currentTimeMillis());
        message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setReceiverName(receiverName);
        message.setReceiver(receiverMemberId);
        message.setType("1");
        message.setFileType("7");
        message.setContent(giftUrl);
        message.setSendState(MessageModel.SEND_STATE_SUCCEED);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        MessageModel.setMessageModel(message);
        msgList.add(message);
        chatAdapter.notifyDataSetChanged();
        lvMessage.post(new Runnable() {
            @Override
            public void run() {
                lvMessage.setSelection(ListView.FOCUS_DOWN);
            }
        });
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CapturePhotoHelper.CAPTURE_PHOTO_REQUEST_CODE: //拍照

                File photoFile = mCapturePhotoHelper.getPhoto();
                if (photoFile != null) {
                    if (resultCode == RESULT_OK) {
                        typeChatNow = IMAGE_TYPE;
                        //处理图片反转的问题
                        FileUtils.doRotateImageAndSaveStrategy2(photoFile.getAbsolutePath());
                        if (typeChatNow == IMAGE_TYPE) {
                            //图片压缩
                            CompressConfig config = new CompressConfig.Builder()
                                    .setMaxSize(250 * 1024)
                                    .setMaxPixel(800)
                                    .enableReserveRaw(true)
                                    .create();
                            new CompressImageUtil(SingleChatActivity.this, config).compress(photoFile.getAbsolutePath(), new CompressImageUtil.CompressListener() {
                                @Override
                                public void onCompressSuccess(String imgPath) {
                                    messageUpload(System.currentTimeMillis(), Constant.MessageFileType.TYPE_STATIC_IMAGE, imgPath, "");
                                }

                                @Override
                                public void onCompressFailed(String imgPath, String msg) {
                                }
                            });
                        }
                    } else {
                        if (photoFile.exists()) {
                            photoFile.delete();
                        }
                    }
                }

                break;

            case REQUEST_VIDEO_CODE: //小视频
                if (data != null) {
                    String path = data.getStringExtra("path");
                    typeChatNow = VIDEO_TYPE;
                    if (typeChatNow == VIDEO_TYPE) {
                        messageUpload(System.currentTimeMillis(), Constant.MessageFileType.TYPE_VIDEO_FILE, path, "");
                    }
                }
                break;

            case REQUEST_RED_PACKAGE_CODE:
                if (data != null) {
                    String tid = data.getStringExtra("tid");
                    String redPackageContent = data.getStringExtra("redPackageContent");
                    sendRedMessage(tid, redPackageContent);
                }
                break;
            case REQUEST_ZHUANZHANG_CODE://转账
                if (data != null) {
                    String tid = data.getStringExtra("tid");
                    String redPackageContent = data.getStringExtra("redPackageContent");
                    String money = data.getStringExtra("money");
                    sendZhuanZhang(tid, money, redPackageContent);
                }
                break;
            case REQUEST_ZHUANZHANG_DETIAL_CODE://转账详情返回
                if (data != null) {
                    String status = data.getStringExtra("status");
                    int index = data.getIntExtra("index", -1);
                    long msgId = data.getLongExtra("id", 0);
                    String tid = data.getStringExtra("tid");
                    String money = data.getStringExtra("money");
                    boolean isLeft = data.getBooleanExtra("isLeft", false);
                    boolean isAddMsg = data.getBooleanExtra("isAddMsg", false);
                    String redPackageContent = data.getStringExtra("redPackageContent");
                    String hairMemberId = data.getStringExtra("hairMemberId");
                    msgList.get(index).setRedStatus(status);
                    chatAdapter.notifyDataSetChanged();
                    MessageModel messageModel = GreenDaoManager.getInstance().getNewSession().getMessageModelDao().queryBuilder().where(MessageModelDao.Properties.ID.eq(msgId)).unique();
                    messageModel.setRedStatus(status);
                    MessageModel.setMessageModel(messageModel);
                    if (hairMemberId.equals(msgList.get(index).getSender()) && isLeft && "10".equals(status) && isAddMsg) {
                        sendZhuanZhangOK(tid, money, "1", redPackageContent);
                    }
                }
                break;
            case REQUEST_CHAI_RED_PACKAGE_CODE:
                if (data != null) {
                    String status = data.getStringExtra("status");
                    String tid = data.getStringExtra("tid");
                    int index = data.getIntExtra("index", -1);
                    boolean isChai = data.getBooleanExtra("isChai", false);
                    boolean isControl = data.getBooleanExtra("isControl", false);
                    if (isControl) {
                        msgList.get(index).setRedStatus(status);
                        chatAdapter.notifyDataSetChanged();
                        MessageModel messageModel = GreenDaoManager.getInstance().getNewSession().getMessageModelDao().queryBuilder().where(MessageModelDao.Properties.RedTid.eq(tid), MessageModelDao.Properties.FileType.eq("8")).unique();
                        messageModel.setRedStatus(status);
                        MessageModel.setMessageModel(messageModel);
                        if (isChai && status.equals("1")) {//如果是拆红包的人才给对方发拆红包的消息
                            chaiRedMessage(tid);
                        }
                    }
                }
                break;
            case REQUEST_SELECT_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    //图片压缩
                    CompressConfig config = new CompressConfig.Builder()
                            .setMaxSize(250 * 1024)
                            .setMaxPixel(800)
                            .enableReserveRaw(true)
                            .create();
                    new CompressImageUtil(SingleChatActivity.this, config).compress(picturePath, new CompressImageUtil.CompressListener() {
                        @Override
                        public void onCompressSuccess(String imgPath) {
                            messageUpload(System.currentTimeMillis(), Constant.MessageFileType.TYPE_STATIC_IMAGE, imgPath, "");
                        }

                        @Override
                        public void onCompressFailed(String imgPath, String msg) {
                        }
                    });
                }
                break;

            case VIDEO_CHAT:  //视频聊天
                Log.e("222222222", "VIDEO_CHAT");
                getConnectionState();
                break;
            case AUDIO_CHAT:    //语音聊天
                Log.e("111111111", "AUDIO_CHAT");
                getConnectionState();  //返回判断当前socket是否还在连接（有时候会出现socket断开的问题）
                break;
            case REQUEST_LOCATION_CODE:
                if (null != PrefAppStore.getGroupLocation(mContext) && PrefAppStore.getGroupLocation(mContext).length() > 0) {
                    sendLocation(PrefAppStore.getGroupLocation(mContext));
                }
                break;
            case REQUEST_JIAOYI_CODE:
                if (data != null) {
                    sendJiaoYi(data.getStringExtra("transInfo"));
                }
        }
    }

    //发送位置
    private void sendLocation(String poiInfo) {
        if (!MerriApp.isCIMConnectionSatus) {
            GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
            String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                // 连接服务端(即时聊天)
                CIMPushManager.init(mContext, host, port);
            } else {
                CIMPushManager.bindAccount(mContext, String.valueOf(UserModel.getUserModel().getMemberId()));
            }
            //异步获取CIM服务器连接状态
            CIMPushManager.detectIsConnected(mContext);
        } else {

            SentBody sentBody = new SentBody();
            sentBody.setKey(Constant.SENT_BODY_KEY);
            sentBody.put("sender", String.valueOf(UserModel.getUserModel().getMemberId()));
            sentBody.put("senderName", UserModel.getUserModel().getRealname());
            sentBody.put("receiverName", receiverName);
            sentBody.put("receiver", receiverMemberId);
            sentBody.put("type", "1");
            sentBody.put("fileType", "12");
            sentBody.put("content", poiInfo);
            sentBody.put("file", "");
            //添加到Message中
            MessageModel message = new MessageModel();
            message.setTimestamp(sentBody.getTimestamp());
            message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
            message.setSenderName(UserModel.getUserModel().getRealname());
            message.setReceiverName(receiverName);
            message.setReceiver(receiverMemberId);
            message.setType("1");
            message.setFileType("12");
            message.setContent(poiInfo);
            message.setPrivate_id(UserModel.getUserModel().getMemberId());
            MessageModel.setMessageModel(message);
            msgList.add(message);
            chatAdapter.notifyDataSetChanged();
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    lvMessage.setSelection(ListView.FOCUS_DOWN);
                }
            });
            //发送消息
            CIMPushManager.sendRequest(mContext, sentBody);
            PrefAppStore.setGroupLocation(mContext, "");
        }
    }

    //发布交易
    private void sendJiaoYi(String transInfo) {
        if (!MerriApp.isCIMConnectionSatus) {
            GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
            String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                // 连接服务端(即时聊天)
                CIMPushManager.init(mContext, host, port);
            } else {
                CIMPushManager.bindAccount(mContext, String.valueOf(UserModel.getUserModel().getMemberId()));
            }
            //异步获取CIM服务器连接状态
            CIMPushManager.detectIsConnected(mContext);
        } else {

            SentBody sentBody = new SentBody();
            sentBody.setKey(Constant.SENT_BODY_KEY);
            sentBody.put("sender", String.valueOf(UserModel.getUserModel().getMemberId()));
            sentBody.put("senderName", UserModel.getUserModel().getRealname());
            sentBody.put("receiverName", receiverName);
            sentBody.put("receiver", receiverMemberId);
            sentBody.put("type", "1");
            sentBody.put("fileType", "11");
            sentBody.put("content", transInfo);
            sentBody.put("file", "");
            //添加到Message中
            MessageModel message = new MessageModel();
            message.setTimestamp(sentBody.getTimestamp());
            message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
            message.setSenderName(UserModel.getUserModel().getRealname());
            message.setReceiverName(receiverName);
            message.setReceiver(receiverMemberId);
            message.setType("1");
            message.setFileType("11");
            message.setContent(transInfo);
            message.setPrivate_id(UserModel.getUserModel().getMemberId());
            MessageModel.setMessageModel(message);
            msgList.add(message);
            chatAdapter.notifyDataSetChanged();
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    lvMessage.setSelection(ListView.FOCUS_DOWN);
                }
            });
            //发送消息
            CIMPushManager.sendRequest(mContext, sentBody);
        }
    }


    //判断当前连接状态 如果连接为空，则重新连接
    public void getConnectionState() {
        try {
            MerriApp.socket.disconnect();
            VideoChatModel chatModel = new Gson().fromJson(PrefAppStore.getSocketAddress(SingleChatActivity.this), VideoChatModel.class);
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


    /**
     * 发送红包
     *
     * @param redTid            红包id
     * @param redPackageContent 红包寄语
     */
    private void sendRedMessage(String redTid, String redPackageContent) {
        //添加到Message中
        MessageModel message = new MessageModel();
        message.setTimestamp(System.currentTimeMillis());
        message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setReceiverName(receiverName);
        message.setReceiver(receiverMemberId);
        message.setType("1");
        message.setFileType("8");
        message.setContent(redPackageContent);
        message.setRedTid(redTid);
        message.setRedStatus("0");
        message.setSendState(MessageModel.SEND_STATE_SUCCEED);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        MessageModel.setMessageModel(message);
        msgList.add(message);
        chatAdapter.notifyDataSetChanged();
        lvMessage.post(new Runnable() {
            @Override
            public void run() {
                lvMessage.setSelection(ListView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 讯美币转账
     *
     * @param redTid            转账id
     * @param money             money
     * @param redPackageContent 转账寄语
     */
    private void sendZhuanZhang(String redTid, String money, String redPackageContent) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("money", money);
            jsonObject.put("redPackageContent", redPackageContent);
            //添加到Message中
            MessageModel message = new MessageModel();
            message.setTimestamp(System.currentTimeMillis());
            message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
            message.setSenderName(UserModel.getUserModel().getRealname());
            message.setReceiverName(receiverName);
            message.setReceiver(receiverMemberId);
            message.setType("1");
            message.setFileType("10");
            message.setContent(money);
            message.setRedTid(redTid);
            message.setRedStatus("0");
            message.setSendState(MessageModel.SEND_STATE_SUCCEED);
            message.setPrivate_id(UserModel.getUserModel().getMemberId());
            MessageModel.setMessageModel(message);
            msgList.add(message);
            chatAdapter.notifyDataSetChanged();
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    lvMessage.setSelection(ListView.FOCUS_DOWN);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 讯美币转账
     *
     * @param redTid            转账id
     * @param money             money
     * @param status            领取成功的状态
     * @param redPackageContent 转账寄语
     */
    private void sendZhuanZhangOK(String redTid, String money, String status, String redPackageContent) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("money", money);
            jsonObject.put("redPackageContent", redPackageContent);
            //添加到Message中
            MessageModel message = new MessageModel();
            message.setTimestamp(System.currentTimeMillis());
            message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
            message.setSenderName(UserModel.getUserModel().getRealname());
            message.setReceiverName(receiverName);
            message.setReceiver(receiverMemberId);
            message.setType("1");
            message.setFileType("10");
            message.setContent(money);
            message.setRedTid(redTid);
            message.setRedStatus(status);
            message.setSendState(MessageModel.SEND_STATE_SUCCEED);
            message.setPrivate_id(UserModel.getUserModel().getMemberId());
            MessageModel.setMessageModel(message);
            msgList.add(message);
            chatAdapter.notifyDataSetChanged();
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    lvMessage.setSelection(ListView.FOCUS_DOWN);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拆红包
     *
     * @param redTid 红包id
     */
    private void chaiRedMessage(String redTid) {
        //添加到Message中
        MessageModel message = new MessageModel();
        message.setTimestamp(System.currentTimeMillis());
        message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setReceiverName(receiverName);
        message.setReceiver(receiverMemberId);
        message.setType("1");
        message.setFileType("9");
        message.setContent("我领取了" + receiverName + "的红包");
        message.setRedTid(redTid);
        message.setRedStatus("1");
        message.setSendState(MessageModel.SEND_STATE_SUCCEED);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        MessageModel.setMessageModel(message);
        msgList.add(message);
        chatAdapter.notifyDataSetChanged();
        lvMessage.post(new Runnable() {
            @Override
            public void run() {
                lvMessage.setSelection(ListView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 上传语音、图片、视频接口，上传完后通过
     */
    private void messageUpload(final long timestamp, final String fileType, final String filePath, final String seconds) {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            GetToast.useString(mContext, "网络不可用，请稍后重试");
            return;
        }
        CIMServerData.messageUpload(mContext, fileType, filePath, new StringDialogCallback(mContext, "上传中...") {
            @Override
            public void onSuccess(Response<String> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body());

                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        if (!MerriApp.isCIMConnectionSatus) {
                            GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
                            String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
                            int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
                            if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                                // 连接服务端(即时聊天)
                                CIMPushManager.init(mContext, host, port);
                            } else {
                                CIMPushManager.bindAccount(mContext, String.valueOf(UserModel.getUserModel().getMemberId()));
                            }
                            //异步获取CIM服务器连接状态
                            CIMPushManager.detectIsConnected(mContext);
                            return;
                        }
                        String data = jsonObject.optString("data");
                        if (TextUtils.isEmpty(data)) {
                            //如果返回地址为空，直接返回不发送
                            GetToast.useString(mContext, "上传失败");
                            return;
                        }
                        //发送消息
                        SentBody sentBody = new SentBody();
                        sentBody.setKey(Constant.SENT_BODY_KEY);
                        sentBody.setTimestamp(timestamp);
                        sentBody.put("sender", String.valueOf(UserModel.getUserModel().getMemberId()));
                        sentBody.put("senderName", UserModel.getUserModel().getRealname());
                        sentBody.put("receiverName", receiverName);
                        sentBody.put("receiver", receiverMemberId);
                        sentBody.put("type", Constant.MessageType.TYPE_1);
                        sentBody.put("fileType", fileType);
                        sentBody.put("content", "");
                        sentBody.put("file", data);
                        sentBody.put("filePath", filePath);
                        sentBody.put("speechTimeLength", seconds);

                        //添加到Message中
                        MessageModel message = new MessageModel();
                        message.setTimestamp(timestamp);
                        message.setSender(String.valueOf(UserModel.getUserModel().getMemberId()));
                        message.setSenderName(UserModel.getUserModel().getRealname());
                        message.setReceiver(receiverMemberId);
                        message.setReceiverName(receiverName);
                        message.setType(Constant.MessageType.TYPE_1);
                        message.setFileType(fileType);
                        message.setFile(data);
                        message.setFilePath(filePath);
                        message.setSpeechTimeLength(seconds);
                        message.setPrivate_id(UserModel.getUserModel().getMemberId());
                        MessageModel.setMessageModel(message);
                        msgList.add(message);
                        chatAdapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });

                        CIMPushManager.sendRequest(mContext, sentBody);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 输入框监听事件
     */
    private void inputListener() {
        sendEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (typeIsJia) {
                        btnSend.setVisibility(View.VISIBLE);
                        ivPlus.setVisibility(View.GONE);
                        typeIsJia = false;
                    }
                    typeChatNow = TEXT_TYPE;
                } else if (!typeIsJia) {
                    btnSend.setVisibility(View.GONE);
                    ivPlus.setVisibility(View.VISIBLE);
                    typeIsJia = true;
                }
            }
        });
        if (!TextUtils.isEmpty(sendEdt.getText())) {
            SpannableStringBuilder sb = handler(sendEdt, sendEdt.getText().toString());
            sendEdt.setText(sb);
        }
    }

    /**
     * 进入页面判断输入框是否有文字
     * 如果有文字，判断是否包含表情名字，并把表情名字转换成表情
     *
     * @param content
     * @return
     */
    private SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[face/png/f_static_)\\d{3}(.png\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String num = tempText.substring(
                        "[face/png/f_static_".length(), tempText.length()
                                - ".png]".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,
                                new AnimatedGifDrawable.UpdateListener() {
                                    @Override
                                    public void update() {
                                        gifTextView.postInvalidate();
                                    }
                                })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("[".length(),
                        tempText.length() - "]".length());
                try {
                    sb.setSpan(
                            new ImageSpan(SingleChatActivity.this, BitmapFactory
                                    .decodeStream(SingleChatActivity.this.getAssets()
                                            .open(png))), m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        return sb;
    }

    /**
     * 初始化表情
     */
    private void InitFaceViewPager() {
        try {
            initStaticFaces();
            faceViewpager.setOnPageChangeListener(new SingleChatActivity.PageChange());
            // 获取页数
            for (int i = 0; i < getPagerCount(); i++) {
                views.add(viewPagerItem(i));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
                params.leftMargin = 8;
                params.rightMargin = 8;
                faceDotsContainer.addView(dotsItem(i), params);
            }
            FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
            faceViewpager.setAdapter(mVpAdapter);
            faceDotsContainer.getChildAt(0).setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                try {
                    String png = ((TextView) ((LinearLayout) view)
                            .getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return gridview;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        //limit("2,3"), 2:代表从第个条目开始查询，3代表一共查询3条
//        if (startItemNum == 0) {
//            GetToast.useString(mContext, "没有更多数据了");
//            swipeRefreshLayout.setRefreshing(false);
//            return;
//        }
//        if (startItemNum - pageSize > 0) {
//            startItemNum -= 20;
//        } else {
//            //当没有20条时，要查询的总条数也就是上一次的条目索引位置
//            pageSize = startItemNum;
//            startItemNum = 0;
//        }

        if (currentPage > 0) {
            currentPage--;
            pageSize = 20;
        } else {
            GetToast.useString(mContext, "没有更多数据了");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        List<MessageModel> tempList = MessageModel.getListMessageModelByLimit(receiverMemberId, currentPage, pageSize);
        msgList.addAll(0, tempList);
        if (chatAdapter == null) {
            chatAdapter = new SingleChatAdapter(mContext, msgList);
            lvMessage.setAdapter(chatAdapter);
        } else {
            chatAdapter.notifyDataSetChanged();
        }
        //设置滚动到刚加载数据的最后一条
//        lvMessage.post(new Runnable() {
//            @Override
//            public void run() {
//                lvMessage.setSelection(ListView.FOCUS_DOWN);
//
//            }
//        });
        swipeRefreshLayout.setRefreshing(false);
//        pageSize = 20;
    }

    @Override
    public void onFinish(float seconds, String FilePath) {
        if (seconds < 1) {
            //小于1秒时不发送
            GetToast.useString(mContext, "时间太短无法记录");
            return;
        }

        typeChatNow = VOICE_TYPE;
        if (typeChatNow == VOICE_TYPE) {
            messageUpload(System.currentTimeMillis(), Constant.MessageFileType.TYPE_VOICE, FilePath, String.valueOf((int) seconds));
        }
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((sendEdt.getText()));
        int iCursorEnd = Selection.getSelectionEnd((sendEdt.getText()));
        if (iCursorStart != iCursorEnd) {
            (sendEdt.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((sendEdt.getText()));
        (sendEdt.getText()).insert(iCursor, text);
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "[" + png + "]";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(SingleChatActivity.this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (sendEdt.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(sendEdt.getText());
            int iCursorStart = Selection.getSelectionStart(sendEdt.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "[face/png/f_static_000.png]";
                        (sendEdt.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        (sendEdt.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    (sendEdt.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "[face/png/f_static_000.png]";
        String content = sendEdt.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\[face/png/f_static_)\\d{3}(.png\\])";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (panelRoot.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {

        //保存最新的一条聊天信息到消息列表
        MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.SenderId.eq(receiverMemberId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
        String content = sendEdt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            if (msgList.size() > 0) {
                MessageModel msg = msgList.get(msgList.size() - 1);
                if (messageListModle == null) {
                    messageListModle = new MessageListModle();
                    messageListModle.setSenderId(receiverMemberId);
                    messageListModle.setHeadUrl(receiverHeadUrl);
                    messageListModle.setName(receiverName);
                    messageListModle.setType(Constant.MessageType.TYPE_1);
                    messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));
                }
                messageListModle.setMsgts(msg.getTimestamp());
                messageListModle.setFileType(msg.getFileType());
                messageListModle.setLast(msg.getContent());
                messageListModle.setText1("1");
                MessageListModle.setMessageListModle(messageListModle);
            } else {
                //文本输入框内容为空，并且聊天记录也为空，此时为没有聊天信息，不保存到消息列表
                if (messageListModle != null) {
                    messageListModle.setMsgts(System.currentTimeMillis());
                    //当界面为空的
                    messageListModle.setFileType("5");
                    messageListModle.setLast("");
                }
            }
        } else {
            //-1的时候为草稿
            if (messageListModle == null) {
                messageListModle = new MessageListModle();
                messageListModle.setSenderId(receiverMemberId);
                messageListModle.setHeadUrl(receiverHeadUrl);
                messageListModle.setName(receiverName);
                messageListModle.setType(Constant.MessageType.TYPE_1);
                messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));
            } else {
                messageListModle.setFileType("-1");
                messageListModle.setLast(content);
                messageListModle.setMsgts(System.currentTimeMillis());
                messageListModle.setText1("1");
                MessageListModle.setMessageListModle(messageListModle);
            }
        }
        //刷新消息列表
        MyEventBusModel myEventBusModel = new MyEventBusModel();
        myEventBusModel.MESSAGE_IS_REFRESH = true;
        myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
        EventBus.getDefault().post(myEventBusModel);
        super.onBackPressed();
    }

    /**
     * 当收到消息时调用此方法
     */
    @Override
    public void onMessageReceived(MessageModel var1) {
        LogUtil.d("@@@", "Message===" + var1.toString());
        Logger.e("onMessageReceived+单");
        if (Constant.MessageType.TYPE_1.equals(var1.getType())) {
            //如果是当前聊天对象发送过来的消息则展示
            if (var1.getSender().equals(receiverMemberId) || var1.getSender().equals(String.valueOf(UserModel.getUserModel().getMemberId()))) {
                if ("1".equals(var1.getRevoke())) {
                    for (int i = 0; i < msgList.size(); i++) {
                        if ((msgList.get(i).getMid()).equals(var1.getMid())) {
                            msgList.get(i).setContent(var1.getContent());
                            msgList.get(i).setTypeRevoke(Constant.MessageType.TYPE_3);
                            MessageModel.setMessageModel(msgList.get(i));
                            break;
                        }
                    }
                } else {
                    msgList.add(var1);
                    MessageModel.setMessageModel(var1);
                }
                chatAdapter.notifyDataSetChanged();
                lvMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        lvMessage.setSelection(ListView.FOCUS_DOWN);
                    }
                });
            }
            //保存最新的一条聊天信息到消息列表
            MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.SenderId.eq(receiverMemberId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
            if (msgList.size() > 0) {
                MessageModel msg = msgList.get(msgList.size() - 1);
                if (messageListModle == null) {
                    messageListModle = new MessageListModle();
                    messageListModle.setSenderId(receiverMemberId);
                    messageListModle.setHeadUrl(receiverHeadUrl);
                    messageListModle.setName(receiverName);
                    messageListModle.setType(Constant.MessageType.TYPE_1);
                    messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));
                }
                messageListModle.setMsgts(msg.getTimestamp());
                messageListModle.setFileType(msg.getFileType());
                messageListModle.setLast(msg.getContent());
                messageListModle.setText1("1");
                MessageListModle.setMessageListModle(messageListModle);
            }
            //刷新消息列表
            MyEventBusModel myEventBusModel = new MyEventBusModel();
            myEventBusModel.MESSAGE_IS_REFRESH = true;
            myEventBusModel.MESSAGE_IS_MAIN_MESSAGE_NUM = true;
            EventBus.getDefault().post(myEventBusModel);
        }
        Logger.e(msgList.toString());
    }

    /**
     * 当调用CIMPushManager.sendRequest()获得相应时 调用此方法
     * ReplyBody.key 将是对应的 SentBody.key
     *
     * @param replybody
     */
    @Override
    public void onReplyReceived(ReplyBody replybody) {
        LogUtil.d("@@@", "ReplyBody===" + replybody.toString());
        if (Constant.SENT_BODY_KEY.equals(replybody.getKey())) {
            if (CIMConstant.ReturnCode.CODE_200.equals(replybody.getCode())) {
                //发送消息时的响应
                for (int i = 0; i < msgList.size(); i++) {
                    MessageModel msg = msgList.get(i);
                    if (replybody.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_SUCCEED);
                        msg.setMid(replybody.getData().get("mid"));
                        MessageModel.setMessageModel(msg);
                        chatAdapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.SenderId.eq(receiverMemberId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();


                        //是否消息免打扰0否1是
                        int inter = 0;
                        int top = 0;
                        try {
                            inter = Integer.parseInt(msg.getInter());
                            top = Integer.parseInt(msg.getTop());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } finally {
                            if (messageListModle == null) {
                                messageListModle = new MessageListModle();
                                messageListModle.setSenderId(receiverMemberId);
                                messageListModle.setType(Constant.MessageType.TYPE_1);
                                messageListModle.setPrivateID(String.valueOf(UserModel.getUserModel().getMemberId()));
                            }
                            messageListModle.setHeadUrl(receiverHeadUrl);
                            messageListModle.setName(receiverName);
                            messageListModle.setMsgts(msg.getTimestamp());
                            messageListModle.setFileType(msg.getFileType());
                            messageListModle.setLast(msg.getContent());
                            messageListModle.setInter(inter);
                            messageListModle.setTop(top);
                            messageListModle.setText1("1");
                            MessageListModle.setMessageListModle(messageListModle);
                        }
                        break;
                    }
                }
                return;
            } else if (CIMConstant.ReturnCode.CODE_201.equals(replybody.getCode())) {
                //文本消息包含敏感字
                GetToast.useString(mContext, "文本消息包含敏感字");
            } else if (CIMConstant.ReturnCode.CODE_203.equals(replybody.getCode())) {
                //无法找到对方登录ip
                GetToast.useString(mContext, "无法找到对方登录ip");
            } else if (CIMConstant.ReturnCode.CODE_500.equals(replybody.getCode())) {
                //服务器错误
                GetToast.useString(mContext, "服务器错误");
            } else if (CIMConstant.ReturnCode.CODE_501.equals(replybody.getCode())) {
                //memberId无效
                GetToast.useString(mContext, "memberId无效");
            } else if (CIMConstant.ReturnCode.CODE_504.equals(replybody.getCode())) {
                //用户未注册
                GetToast.useString(mContext, "用户未注册");
            } else if (CIMConstant.ReturnCode.CODE_509.equals(replybody.getCode())) {
                //被加入黑名单
                GetToast.useString(mContext, "您之前发布的内容包含违规内容，禁止发消息");
            }
            //发送消息时的响应
            for (int i = 0; i < msgList.size(); i++) {
                MessageModel msg = msgList.get(i);
                if (replybody.getTimestamp() == msg.getTimestamp()) {
                    //修改消息发送状态为失败
                    msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                    MessageModel.setMessageModel(msg);
                    chatAdapter.notifyDataSetChanged();
                    lvMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            lvMessage.setSelection(ListView.FOCUS_DOWN);
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * 当手机网络发生变化时调用
     *
     * @param networkinfo
     */
    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {

    }

    /**
     * 获取到是否连接到服务端 通过调用CIMPushManager.detectIsConnected()来异步获取
     *
     * @param isConnected
     */
    @Override
    public void onConnectionStatus(boolean isConnected) {
        LogUtil.e("@@@", "******************CIM与服务器连接广播onConnectionStatus状态:" +isConnected);
        MerriApp.isCIMConnectionSatus = isConnected;
    }

    /**
     * 连接服务端成功
     */
    @Override
    public void onCIMConnectionSucceed() {
        LogUtil.e("@@@", "CIM连接服务端成功-----");
    }

    /**
     * 连接断开
     */
    @Override
    public void onCIMConnectionClosed() {
        LogUtil.e("@@@", "CIM连接断开-----");
    }

    /**
     * 消息发送失败
     *
     * @param message
     */
    @Override
    public void onMessageSendFailureReceived(MessageModel message) {
        for (int i = 0; i < msgList.size(); i++) {
            MessageModel tempMsg = msgList.get(i);
            if (message.getTimestamp() == tempMsg.getTimestamp()) {
                //修改消息发送状态为失败
                message.setSendState(MessageModel.SEND_STATE_FAILURE);
                MessageModel.setMessageModel(message);
                chatAdapter.notifyDataSetChanged();
                lvMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        lvMessage.setSelection(ListView.FOCUS_DOWN);
                    }
                });
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        //销毁正在播放的语音
        MediaManage.destory();
        AppManager.getAppManager().removeActivity(this);
        CIMListenerManager.removeMessageListener(this);
        EventBus.getDefault().unregister(this);
        //取消adapter中下载文件的请求
        if (null != chatAdapter) {
            chatAdapter.cancelOkHttpClient();
        }
        super.onDestroy();
    }

    /**
     * 从相册中获取图片
     */
    public void select_photo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    /**
     * 打开相册的方法
     */
    private void openAlbum() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询余额、红包和金币
     */
    private void getWalletInfo() {
        OkGo.<String>post(Urls.queryWalletInfo)
                .params("accountId", UserModel.getUserModel().getAccountId())
                .params("uid", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                coinCount.setText("讯美币：" + data.optString("giftBalance"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void changeMeiBiViewValue() {
        //钱包余额请求
        getWalletInfo();
    }


    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            for (int i = 0; i < faceDotsContainer.getChildCount(); i++) {
                faceDotsContainer.getChildAt(i).setSelected(false);
            }
            faceDotsContainer.getChildAt(arg0).setSelected(true);
        }

    }


}
