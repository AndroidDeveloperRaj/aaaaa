package com.merrichat.net.activity.message;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImageUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.groupmarket.MarketActivity;
import com.merrichat.net.activity.grouporder.GroupListActivity;
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
import com.merrichat.net.activity.message.setting.GroupSettingActivity;
import com.merrichat.net.adapter.FaceGVAdapter;
import com.merrichat.net.adapter.FaceVPAdapter;
import com.merrichat.net.adapter.GroupMsgAdapter;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.fragment.GroupListFragment;
import com.merrichat.net.http.UrlConfig;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMarketModel;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.GroupMessageDao;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.pre.PrefAppStore;
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
import com.merrichat.net.view.AutoScrollUpView.BaseAutoScrollUpView;
import com.merrichat.net.view.AutoScrollUpView.MainScrollUpAdvertisementView;

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

import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_CHAI_RED_PACKAGE_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_JIAOYI_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_LOCATION_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_RED_PACKAGE_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_SELECT_PHOTO_CODE;
import static com.merrichat.net.activity.message.SingleChatActivity.REQUEST_VIDEO_CODE;


/**
 * Created by amssy on 2018/1/23.
 * 群组聊天
 */

public class GroupChattingAty extends AppCompatActivity implements CIMEventListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int activityId = MiscUtil.getActivityId();
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
     * 群设置返回
     */
    public final static int REQUEST_SETTING_CODE = 21;
    /**
     * 返回箭头
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;
    /**
     * title
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    /**
     * 右边设置按钮
     */
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;


    @BindView(R.id.lv_message)
    ListView lvMessage;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * 顶部tip布局
     */
    @BindView(R.id.auto_scroll_view)
    MainScrollUpAdvertisementView autoScrollVoew;
    /**
     * 语音切换
     */
    @BindView(R.id.voice_text_switch_iv)
    ImageView voiceTextSwitchIv;
    /**
     * 输入框
     */
    @BindView(R.id.send_edt)
    EditText sendEdt;
    /**
     * 按住录音
     */
    @BindView(R.id.send_voice_btn)
    AudioRecorderButton sendVoiceBtn;
    /**
     * 表情切换
     */
    @BindView(R.id.iv_face)
    ImageView ivFace;
    /**
     * +号切换
     */
    @BindView(R.id.iv_plus)
    ImageView ivPlus;
    /**
     * 发送按钮
     */
    @BindView(R.id.btn_send)
    TextView btnSend;

    /**
     * 输入框总布局
     */
    @BindView(R.id.sendMsgLayout)
    LinearLayout sendMsgLayout;
    /**
     * 照片
     */
    @BindView(R.id.lay_chatting_picture)
    LinearLayout layChattingPicture;
    /**
     * 拍照
     */
    @BindView(R.id.lay_chatting_take_photo)
    LinearLayout layChattingTakePhoto;
    /**
     * 小视频
     */
    @BindView(R.id.lay_chatting_samall_video)
    LinearLayout layChattingSamallVideo;
    /**
     * 位置
     */
    @BindView(R.id.lay_send_location)
    LinearLayout laySendLocation;
    /**
     * 红包
     */
    @BindView(R.id.lay_chatting_red_package)
    LinearLayout layChattingRedPackage;

    /**
     * +号布局顶行布局
     */
    @BindView(R.id.lay_top)
    LinearLayout layTop;
    /**
     * 发布交易
     */
    @BindView(R.id.lay_send_deal)
    LinearLayout laySendDeal;
    /**
     * +号布局低行布局
     */
    @BindView(R.id.lay_bottom)
    LinearLayout layBottom;

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
     * 底部扩展面板总布局
     */
    @BindView(R.id.panel_root)
    KPSwitchPanelLinearLayout panelRoot;

    /**
     * 功能面板
     */
    @BindView(R.id.ll_sub_panel)
    LinearLayout llSubPanel;
    /**
     * 编辑框当前输入类型（语音或文本）
     */
    private int editBoxInputType = TEXT_TYPE;
    private List<GroupMessage> msgList;
    private GroupMsgAdapter adapter;
    /**
     * 存放表情图片的list集合
     */
    private List<String> staticFacesList;
    private List<View> views = new ArrayList<View>();
    // 7列3行
    private int columns = 7;
    private int rows = 3;
    /**
     * 当前消息类型
     */
    private int typeChatNow;
    /**
     * 当前右下角按钮显示的是否是"+"号
     */
    private boolean typeIsJia = true;
    private Context mContext;
    /**
     * 群聊id
     */
    public static String groupId;
    /**
     * 群名称
     */
    private String group;
    /**
     * 群头像
     */
    private String groupLogoUrl;
    /**
     * 当前显示的剩余条数
     */
    private int startItemNum = 0;
    /**
     * 每页最大条数
     */
    private int pageSize = 20;
    /**
     * 查询的当前页
     */
    private int currentPage;
    private int groupType;//0普通交流群 1 个人私聊 2 C2C 3 B2C
    private CapturePhotoHelper mCapturePhotoHelper;
    private int isCanJiaoYi = 0;//0:准许交易 1：禁止交易
    private int isBanned = 0;//是否禁言0:否 1：是
    private int isMaster = 0;//0：成员 1：管理员 2：群主
    private int memberNum = 0;//群人数


    /**
     * 是否修改群名字标记
     */
    private boolean isUpdateGroupName = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setBackgroundDrawableResource(R.color.background);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white), 38);
        setContentView(R.layout.activity_group_chatting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        mCapturePhotoHelper = new CapturePhotoHelper(this, FolderManager.getPhotoFolder());
        //消息服务器的监听
        CIMListenerManager.registerMessageListener(this, this);
        //加入栈
        AppManager.getAppManager().addActivity(this);
        initView();
        //初始化数据
        initData();
        //键盘适配
        initKeyBoard();
        //输入的监听
        inputListener();
        //表情viewpager
        initFaceViewPager();
        findMarketList();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_MESSAGE) {//更新数据
            initData();
        } else if (myEventBusModel.REFRESH_GROUP_MESSAGE_NAME) {
            isUpdateGroupName = true;
            String groupName = myEventBusModel.REFRESH_GROUP_MESSAGE_NAME_STRING;
            group = groupName;
            MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.GroupId.eq(groupId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
            messageListModle.setGroup(group);
            MessageListModle.setMessageListModle(messageListModle);
        } else if (myEventBusModel.FINIIS_GROUP_CHAT) {
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.MESSAGE_IS_REFRESH = true;
            EventBus.getDefault().post(eventBusModel);
            finish();
        }
    }


    private void initView() {
        groupId = getIntent().getStringExtra("groupId");
        group = getIntent().getStringExtra("group");
        groupLogoUrl = getIntent().getStringExtra("groupLogoUrl");
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.icon_chatting_right_tip);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);

        tvTitleText.setText(group);
    }

    private void initData() {
        try {
            MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.GroupId.eq(groupId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
            if (messageListModle != null) {
                if ("-1".equals(messageListModle.getFileType())) {
                    //草稿
                    sendEdt.setText(messageListModle.getLast());
                }
            } else {
                messageListModle = new MessageListModle();
                messageListModle.setGroup(group);
                messageListModle.setGroupId(groupId);
                messageListModle.setHeadUrl(groupLogoUrl);
                messageListModle.setType(Constant.MessageType.TYPE_2);
                messageListModle.setPrivateID(UserModel.getUserModel().getMemberId());
                messageListModle.setMsgts(System.currentTimeMillis());
                messageListModle.setFileType("-2");
                messageListModle.setLast("");
                messageListModle.setText1("0");
                MessageListModle.setMessageListModle(messageListModle);
            }

            startItemNum = GroupMessage.getListMessageModelCount(groupId);
            currentPage = startItemNum / pageSize - 1;
            pageSize = 20 + 19;
            Log.e("@@@", "startItemNum======" + startItemNum);
            Log.e("@@@", "testNum======" + currentPage);
            msgList = GroupMessage.getListMessageModelByLimit(groupId, currentPage, pageSize);
            LogUtil.e("@@@", "msgList===" + msgList.size());
            adapter = new GroupMsgAdapter(mContext, msgList);
            lvMessage.setAdapter(adapter);
            lvMessage.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    lvMessage.setSelection(adapter.getCount() - 1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        isBannedOrTrade();
    }


    /**
     * 查询群的一些详情
     */
    private void isBannedOrTrade() {
        OkGo.<String>post(Urls.isBannedOrTrade)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (jsonObject.optBoolean("success")) {
                                if (data.optBoolean("success")) {
                                    isCanJiaoYi = data.optInt("tradeMsg");
                                    isBanned = data.optInt("isBanned");
                                    int type = data.optInt("type");//0：交流群，1：微商群（BTC） 2：集市群(CTC),groupType = 0;//0普通交流群 1 个人私聊 2 C2C 3 B2C
                                    isMaster = data.optInt("isMaster");
                                    memberNum = data.optInt("memberNum");
                                    groupType = -1;
                                    if (type == 1) {
                                        groupType = 3;
                                    } else if (type == 2) {
                                        groupType = 2;
                                    } else if (type == 0) {
                                        groupType = 0;
                                    }
                                } else {
                                    if (data.optString("message").equals("该用户不是群成员")) {
                                        RxToast.showToast("该用户不是群成员");
                                        GroupMessage.clearThisGroupMessage(groupId);
                                        MessageListModle.clearSingleMessageModel(groupId);
                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
                                        myEventBusModel.MESSAGE_IS_REFRESH = true;
                                        EventBus.getDefault().post(myEventBusModel);
                                        finish();
                                    }
                                }
                                if (isCanJiaoYi == 1) {
                                    laySendDeal.setVisibility(View.INVISIBLE);
                                } else if (isCanJiaoYi == 0) {
                                    if (groupType == 3 && (isMaster == 2 || isMaster == 1)) {
                                        laySendDeal.setVisibility(View.VISIBLE);
                                    } else if (groupType == 2) {
                                        laySendDeal.setVisibility(View.VISIBLE);
                                    } else if (groupType == 0) {
                                        laySendDeal.setVisibility(View.INVISIBLE);
                                    }
                                }
                                tvTitleText.setText(group + "(" + memberNum + ")");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    /**
     * 查询群市场商品列表
     */
    private void findMarketList() {
        OkGo.<String>get(Urls.findTransInfoByGroupId)
                .tag(this)
                .params("groupId", groupId)
                .params("pageSize", 0)
                .params("pageNum", 0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        GroupMarketModel groupMarketModel = new Gson().fromJson(response.body(), GroupMarketModel.class);
                        if (groupMarketModel.isSuccess()) {
                            if (groupMarketModel.getData().size() > 0) {
                                autoScrollVoew.setVisibility(View.VISIBLE);
                                autoScrollVoew.setData((ArrayList<GroupMarketModel.DataBean>) groupMarketModel.getData());
                                autoScrollVoew.setTextSize(15);
                                autoScrollVoew.setOnItemClickListener(new BaseAutoScrollUpView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        startActivity(new Intent(mContext, MarketActivity.class).putExtra("groupId", groupId));
                                    }
                                });
                                autoScrollVoew.setTimer(2000);
                                autoScrollVoew.stop();
                                autoScrollVoew.start();
                            } else {
                                autoScrollVoew.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    /**
     * 初始化表情
     */
    private void initFaceViewPager() {
        try {
            initStaticFaces();
            faceViewpager.setOnPageChangeListener(new PageChange());
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
        sendVoiceBtn.setAudioFinishRecorderListenter(new AudioRecorderButton.AudioFinishRecorderListenter() {
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
        });
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
                    new ImageSpan(mContext, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "[face/png/f_static_000.png]";
        String content = sendEdt.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(), content.length());
            String regex = "(\\[face/png/f_static_)\\d{3}(.png\\])";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
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
                String num = tempText.substring("[face/png/f_static_".length(), tempText.length() - ".png]".length());
                String gif = "face/gif/f" + num + ".gif";
                /**
                 * 如果open这里不抛异常说明存在gif，则显示对应的gif
                 * 否则说明gif找不到，则显示png
                 * */
                InputStream is = getAssets().open(gif);
                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener() {
                            @Override
                            public void update() {
                                gifTextView.postInvalidate();
                            }
                        })), m.start(), m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                is.close();
            } catch (Exception e) {
                String png = tempText.substring("[".length(), tempText.length() - "]".length());
                try {
                    sb.setSpan(new ImageSpan(mContext, BitmapFactory.decodeStream(mContext.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

                    }
                });
        KPSwitchConflictUtil.attach(panelRoot, sendEdt,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
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


    @OnClick({R.id.iv_back, R.id.tv_right_img, R.id.iv_face, R.id.lay_chatting_picture,
            R.id.lay_chatting_take_photo, R.id.lay_chatting_samall_video, R.id.lay_chatting_red_package,
            R.id.lay_send_deal, R.id.btn_send, R.id.lay_send_location, R.id.voice_text_switch_iv})
    public void onViewClicked(View view) {
        Intent intent;
        if (isBanned == 1 && view.getId() != R.id.iv_back
                && view.getId() != R.id.tv_right_img) {
            GetToast.useString(mContext, "你已被禁言");
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.voice_text_switch_iv:
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
            case R.id.tv_right_img:
                startActivityForResult(new Intent(mContext, GroupSettingActivity.class).putExtra("groupId", groupId).putExtra("isMaster", isMaster), REQUEST_SETTING_CODE);
                break;
            case R.id.btn_send://发送文本消息

                String sendContent = sendEdt.getText().toString().trim();
                if (TextUtils.isEmpty(sendContent)) {
                    GetToast.useString(mContext, "不能发送空白消息");
                    return;
                }
                if (!NetUtils.isNetworkAvailable(mContext)) {
                    GetToast.useString(mContext, "网络不可用，请稍后重试");
                    return;
                }
                if (typeChatNow == TEXT_TYPE) {
                    if (!MerriApp.isCIMConnectionSatus) {
                        GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
                        String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
                        int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
                        if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                            // 连接服务端(即时聊天)
                            CIMPushManager.init(mContext, host, port);
                        } else {
                            CIMPushManager.bindAccount(mContext, UserModel.getUserModel().getMemberId());
                        }
                        //异步获取CIM服务器连接状态
                        CIMPushManager.detectIsConnected(mContext);
                        return;
                    }
                    SentBody sentBody = new SentBody();
                    sentBody.setKey(Constant.SENT_BODY_KEY);
                    sentBody.put("sender", UserModel.getUserModel().getMemberId());
                    sentBody.put("senderName", UserModel.getUserModel().getRealname());
                    sentBody.put("group", group);
                    sentBody.put("groupId", groupId);
                    sentBody.put("title", "10");
                    sentBody.put("type", Constant.MessageType.TYPE_2);
                    sentBody.put("fileType", "5");
                    sentBody.put("content", sendContent);
                    sentBody.put("file", "");

                    //添加到Message中
                    GroupMessage message = new GroupMessage();
                    message.setTimestamp(sentBody.getTimestamp());
                    message.setSender(UserModel.getUserModel().getMemberId());
                    message.setSenderName(UserModel.getUserModel().getRealname());
                    message.setGroup(group);
                    message.setGroupId(groupId);
                    message.setType(Constant.MessageType.TYPE_2);
                    message.setFileType(Constant.MessageFileType.TYPE_OTHER_FILE);
                    message.setContent(sendContent);
                    message.setPrivate_id(UserModel.getUserModel().getMemberId());
                    GroupMessage.setMessageModel(message);
                    msgList.add(message);
                    adapter.notifyDataSetChanged();
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
                break;
            case R.id.lay_chatting_picture://选择图片发送
                select_photo();
                break;
            case R.id.lay_chatting_take_photo://拍照发送
                mCapturePhotoHelper.capture();
                typeChatNow = IMAGE_TYPE;
                break;
            case R.id.lay_chatting_samall_video://发送小视频
                typeChatNow = VIDEO_TYPE;
                intent = new Intent(mContext, SmallVideoRecoderAty.class);
                startActivityForResult(intent, REQUEST_VIDEO_CODE);
                break;
            case R.id.lay_chatting_red_package://发红包
                startActivityForResult(new Intent(mContext, GroupRedPackageAty.class).putExtra("memberNum", memberNum)
                        .putExtra("group", group)
                        .putExtra("groupId", groupId), REQUEST_RED_PACKAGE_CODE);
                break;
            case R.id.lay_send_location://发送位置
                startActivityForResult(new Intent(mContext, SendLocationAty.class).putExtra("type", 1), REQUEST_LOCATION_CODE);
                break;
            case R.id.lay_send_deal://发布交易
                sendDeal();
                break;
        }
    }

    //发布交易
    private void sendDeal() {
        startActivityForResult(new Intent(mContext, SendDealAty.class).putExtra("groupId", groupId).putExtra("groupType", groupType), REQUEST_JIAOYI_CODE);
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!NetUtils.isNetworkAvailable(mContext)) {
            GetToast.useString(mContext, "网络不可用，请稍后重试");
            return;
        }
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
                            new CompressImageUtil(mContext, config).compress(photoFile.getAbsolutePath(), new CompressImageUtil.CompressListener() {
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
                    new CompressImageUtil(mContext, config).compress(picturePath, new CompressImageUtil.CompressListener() {
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
            case REQUEST_JIAOYI_CODE:
                if (data != null) {
                    sendJiaoYi(data.getStringExtra("transInfo"));
                    findMarketList();
                }
                break;
            case REQUEST_LOCATION_CODE:
                if (null != PrefAppStore.getGroupLocation(mContext) && PrefAppStore.getGroupLocation(mContext).length() > 0) {
                    sendLocation(PrefAppStore.getGroupLocation(mContext));
                }
                break;
            case REQUEST_RED_PACKAGE_CODE://红包
                if (data != null) {
                    String tid = data.getStringExtra("tid");
                    String redPackageContent = data.getStringExtra("redPackageContent");
                    int hairType = data.getIntExtra("hairType", 0);
                    sendRedMessage(tid, redPackageContent, hairType);
                }
                break;
            case REQUEST_SETTING_CODE:
                isBannedOrTrade();
                break;
            case REQUEST_CHAI_RED_PACKAGE_CODE:
                if (data != null) {
                    String status = data.getStringExtra("status");
                    String tid = data.getStringExtra("tid");
                    int index = data.getIntExtra("index", -1);
                    msgList.get(index).setRedStatus(status);
                    adapter.notifyDataSetChanged();
                    GroupMessage groupMessage = GreenDaoManager.getInstance().getNewSession().getGroupMessageDao().queryBuilder().where(GroupMessageDao.Properties.RedTid.eq(tid)).unique();
                    groupMessage.setRedStatus(status);
                    GroupMessage.setMessageModel(groupMessage);
                }
                break;
        }
    }

    /**
     * 发送红包
     */
    private void sendRedMessage(String tid, String redPackageContent, int hairType) {
        //添加到Message中
        GroupMessage message = new GroupMessage();
        message.setTimestamp(System.currentTimeMillis());
        message.setSender(UserModel.getUserModel().getMemberId());
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setGroup(group);
        message.setGroupId(groupId);
        message.setType(Constant.MessageType.TYPE_2);
        message.setFileType(hairType + "");
        message.setContent(redPackageContent);
        message.setRedTid(tid);
        message.setHeader(UrlConfig.getChatHeaderUrl());
        message.setRedStatus("0");
        message.setSendState(MessageModel.SEND_STATE_SUCCEED);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        GroupMessage.setMessageModel(message);
        msgList.add(message);
        adapter.notifyDataSetChanged();
        lvMessage.post(new Runnable() {
            @Override
            public void run() {
                lvMessage.setSelection(ListView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 发送位置
     */
    private void sendLocation(String poiInfo) {
        if (!MerriApp.isCIMConnectionSatus) {
            GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
            String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                // 连接服务端(即时聊天)
                CIMPushManager.init(mContext, host, port);
            } else {
                CIMPushManager.bindAccount(mContext, UserModel.getUserModel().getMemberId());
            }
            //异步获取CIM服务器连接状态
            CIMPushManager.detectIsConnected(mContext);
            return;
        }
        SentBody sentBody = new SentBody();
        sentBody.setKey(Constant.SENT_BODY_KEY);
        sentBody.put("sender", UserModel.getUserModel().getMemberId());
        sentBody.put("senderName", UserModel.getUserModel().getRealname());
        sentBody.put("group", group);
        sentBody.put("groupId", groupId);
        sentBody.put("title", "10");
        sentBody.put("type", Constant.MessageType.TYPE_2);
        sentBody.put("fileType", Constant.MessageFileType.TYPE_LOCATION_FILE);
        sentBody.put("content", poiInfo);
        sentBody.put("file", "");

        //添加到Message中
        GroupMessage message = new GroupMessage();
        message.setTimestamp(sentBody.getTimestamp());
        message.setSender(UserModel.getUserModel().getMemberId());
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setGroup(group);
        message.setGroupId(groupId);
        message.setType(Constant.MessageType.TYPE_2);
        message.setFileType(Constant.MessageFileType.TYPE_LOCATION_FILE);
        message.setContent(poiInfo);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        GroupMessage.setMessageModel(message);
        msgList.add(message);
        adapter.notifyDataSetChanged();
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

    /**
     * 发布交易
     */
    private void sendJiaoYi(String transInfo) {
        if (!MerriApp.isCIMConnectionSatus) {
            GetToast.useString(mContext, "服务器断开，正在重新连接，请稍后重试");
            String host = CIMCacheTools.getString(mContext, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(mContext, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            if (!CIMPushManager.isServiceRun(mContext, "com.merrichat.net.activity.message.cim.android.CIMPushService")) {
                // 连接服务端(即时聊天)
                CIMPushManager.init(mContext, host, port);
            } else {
                CIMPushManager.bindAccount(mContext, UserModel.getUserModel().getMemberId());
            }
            //异步获取CIM服务器连接状态
            CIMPushManager.detectIsConnected(mContext);
            return;
        }
        SentBody sentBody = new SentBody();
        sentBody.setKey(Constant.SENT_BODY_KEY);
        sentBody.put("sender", UserModel.getUserModel().getMemberId());
        sentBody.put("senderName", UserModel.getUserModel().getRealname());
        sentBody.put("group", group);
        sentBody.put("groupId", groupId);
        sentBody.put("title", "10");
        sentBody.put("type", Constant.MessageType.TYPE_2);
        sentBody.put("fileType", Constant.MessageFileType.TYPE_JIAOYI_FILE);
        sentBody.put("content", transInfo);
        sentBody.put("file", "");

        //添加到Message中
        GroupMessage message = new GroupMessage();
        message.setTimestamp(sentBody.getTimestamp());
        message.setSender(UserModel.getUserModel().getMemberId());
        message.setSenderName(UserModel.getUserModel().getRealname());
        message.setGroup(group);
        message.setGroupId(groupId);
        message.setType(Constant.MessageType.TYPE_2);
        message.setFileType(Constant.MessageFileType.TYPE_JIAOYI_FILE);
        message.setContent(transInfo);
        message.setPrivate_id(UserModel.getUserModel().getMemberId());
        GroupMessage.setMessageModel(message);
        msgList.add(message);
        adapter.notifyDataSetChanged();
        lvMessage.post(new Runnable() {
            @Override
            public void run() {
                lvMessage.setSelection(ListView.FOCUS_DOWN);
            }
        });
        //发送消息
        CIMPushManager.sendRequest(mContext, sentBody);
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
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
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
                            RxToast.showToast("上传服务器出错请重试");
                            return;
                        }
                        //发送消息
                        SentBody sentBody = new SentBody();
                        sentBody.setKey(Constant.SENT_BODY_KEY);
                        sentBody.setTimestamp(timestamp);
                        sentBody.put("sender", UserModel.getUserModel().getMemberId());
                        sentBody.put("senderName", UserModel.getUserModel().getRealname());
                        sentBody.put("group", group);
                        sentBody.put("groupId", groupId);
                        sentBody.put("type", Constant.MessageType.TYPE_2);
                        sentBody.put("fileType", fileType);
                        sentBody.put("content", "");
                        sentBody.put("title", "10");
                        sentBody.put("file", data);
                        sentBody.put("filePath", filePath);
                        sentBody.put("speechTimeLength", seconds);

                        //添加到Message中
                        GroupMessage message = new GroupMessage();
                        message.setTimestamp(timestamp);
                        message.setSender(UserModel.getUserModel().getMemberId());
                        message.setSenderName(UserModel.getUserModel().getRealname());
                        message.setGroup(group);
                        message.setGroupId(groupId);
                        message.setType(Constant.MessageType.TYPE_2);
                        message.setFileType(fileType);
                        message.setFile(data);
                        message.setFilePath(filePath);
                        message.setSpeechTimeLength(seconds);
                        message.setPrivate_id(UserModel.getUserModel().getMemberId());
                        GroupMessage.setMessageModel(message);
                        msgList.add(message);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        CIMPushManager.sendRequest(mContext, sentBody);
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

    @Override
    public void onBackPressed() {

        //保存最新的一条聊天信息到消息列表
        MessageListModle messageListModle = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder().where(MessageListModleDao.Properties.GroupId.eq(groupId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
        String content = sendEdt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            if (msgList != null && msgList.size() > 0) {
                GroupMessage msg = msgList.get(msgList.size() - 1);
                if (messageListModle == null) {
                    messageListModle = new MessageListModle();
                    messageListModle.setGroupId(groupId);
                    messageListModle.setHeadUrl(groupLogoUrl);
                    messageListModle.setGroup(group);
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
                messageListModle.setGroupId(groupId);
                messageListModle.setHeadUrl(groupLogoUrl);
                messageListModle.setGroup(group);
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
        if (isUpdateGroupName && getIntent().getFlags() == GroupListFragment.activityId) {
            myEventBusModel.REFRESH_GROUP_LIST = true;
        }
        EventBus.getDefault().post(myEventBusModel);
        super.onBackPressed();
    }

    /**
     * 刷新，消息列表实现下拉加载
     */
    @Override
    public void onRefresh() {
        if (currentPage > 0) {
            currentPage--;
            pageSize = 20;
        } else {
            GetToast.useString(mContext, "没有更多数据了");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        List<GroupMessage> tempList = GroupMessage.getListMessageModelByLimit(groupId, currentPage, pageSize);
        msgList.addAll(0, tempList);
        if (adapter == null) {
            adapter = new GroupMsgAdapter(mContext, msgList);
            lvMessage.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 当收到消息时调用此方法
     */
    @Override
    public void onMessageReceived(MessageModel var1) {
        LogUtil.d("@@@", "Message===" + var1.toString());
        Logger.e("onMessageReceived+群");
        if (!Constant.MessageType.TYPE_1.equals(var1.getType())) {
            if (var1.getGroupId().equals(groupId)) {
                //如果是当前聊天对象发送过来的消息则展示
                GroupMessage groupMessage = MessageToGroupMessage(var1);
                if (Constant.MessageType.TYPE_5.equals(groupMessage.getType())) {
                    //服务器以群主名义发送公告，类型为5，这里修改为2
                    groupMessage.setType(Constant.MessageType.TYPE_2);
                    //将发送状态修改为发送成功
                    groupMessage.setSendState(MessageModel.SEND_STATE_SUCCEED);
                }
//                int samePosition = positionSameMessage(groupMessage);
                if ("1".equals(var1.getRevoke())) {
                    for (int i = 0; i < msgList.size(); i++) {
                        if ((msgList.get(i).getMid()).equals(var1.getMid())) {
                            msgList.get(i).setContent(groupMessage.getContent());
                            msgList.get(i).setType(Constant.MessageType.TYPE_3);
                            GroupMessage.setMessageModel(msgList.get(i));
                            break;
                        }
                    }
                } else {
                    msgList.add(groupMessage);
                    GroupMessage.setMessageModel(groupMessage);
                    Logger.e("群=====================个数" + GroupMessage.getListMessageModelCount(var1.getGroupId()));
                }
                adapter.notifyDataSetChanged();
                lvMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        lvMessage.setSelection(ListView.FOCUS_DOWN);
                    }
                });
            }
        }
    }

    /**
     * 将Message转换成GroupMessage
     *
     * @param message
     * @return
     */
    private GroupMessage MessageToGroupMessage(MessageModel message) {
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(message.getGroupId());
        groupMessage.setGroup(message.getGroup());
        groupMessage.setSenderName(message.getSenderName());
        groupMessage.setSender(message.getSender());
        groupMessage.setContent(message.getContent());
        groupMessage.setFile(message.getFile());
        groupMessage.setFilePath(message.getFilePath());
        groupMessage.setFileType(message.getFileType());
        groupMessage.setFormat(message.getFormat());
        groupMessage.setLogo(message.getLogo());
        groupMessage.setMid(message.getMid());
        groupMessage.setReceiver(message.getReceiver());
        groupMessage.setReceiverName(message.getReceiverName());
        groupMessage.setSpeechTimeLength(message.getSpeechTimeLength());
        groupMessage.setThumb(message.getThumb());
        groupMessage.setTimestamp(message.getTimestamp());
        groupMessage.setTitle(message.getTitle());
        groupMessage.setType(message.getType());
        groupMessage.setHeader(message.getHeader());
        groupMessage.setInter(message.getInter());
        groupMessage.setTop(message.getTop());
        groupMessage.setRedTid(message.getRedTid());
        groupMessage.setRedStatus(message.getRedStatus());
        groupMessage.setTopts(message.getTopts());
        groupMessage.setPrivate_id(UserModel.getUserModel().getMemberId());
        return groupMessage;
    }

    /**
     * 当调用CIMPushManager.sendRequest()获得相应时 调用此方法
     * ReplyBody.key 将是对应的 SentBody.key
     *
     * @param var1
     */
    @Override
    public void onReplyReceived(ReplyBody var1) {
        LogUtil.d("@@@", "ReplyBody===" + var1.toString());
        if (Constant.SENT_BODY_KEY.equals(var1.getKey())) {
            if (CIMConstant.ReturnCode.CODE_200.equals(var1.getCode())) {
                //发送消息时的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_SUCCEED);
                        msg.setMid(var1.getData().get("mid"));
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_201.equals(var1.getCode())) {
                //文本消息包含敏感字
                GetToast.useString(mContext, "文本消息包含敏感字");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);

                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_203.equals(var1.getCode())) {
                //无法找到对方登录ip
                GetToast.useString(mContext, "无法找到对方登录ip");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_500.equals(var1.getCode())) {
                //服务器错误
                GetToast.useString(mContext, "服务器错误");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_501.equals(var1.getCode())) {
                //memberId无效
                GetToast.useString(mContext, "memberId无效");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_504.equals(var1.getCode())) {
                //群聊时 群组不存在
                GetToast.useString(mContext, "该群已被群主解散");
                GroupMessage.clearThisGroupMessage(groupId);
                MessageListModle.clearSingleMessageModel(groupId);
                //刷新消息列表
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.MESSAGE_IS_REFRESH = true;
                EventBus.getDefault().post(myEventBusModel);
                finish();
            } else if (CIMConstant.ReturnCode.CODE_506.equals(var1.getCode())) {
                //用户未注册
                GetToast.useString(mContext, "用户未注册");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
                        lvMessage.post(new Runnable() {
                            @Override
                            public void run() {
                                lvMessage.setSelection(ListView.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                }
            } else if (CIMConstant.ReturnCode.CODE_507.equals(var1.getCode())) {
                //用户不在群中
                GetToast.useString(mContext, "消息发送失败，用户不在群中");
                GroupMessage.clearThisGroupMessage(groupId);
                MessageListModle.clearSingleMessageModel(groupId);
                //刷新消息列表
                MyEventBusModel myEventBusModel = new MyEventBusModel();
                myEventBusModel.MESSAGE_IS_REFRESH = true;
                EventBus.getDefault().post(myEventBusModel);
                finish();
            } else if (CIMConstant.ReturnCode.CODE_509.equals(var1.getCode())) {
                //被加入黑名单
                GetToast.useString(mContext, "您之前发布的内容包含违规内容，禁止发消息");
                //发送失败的响应
                for (int i = 0; i < msgList.size(); i++) {
                    GroupMessage msg = msgList.get(i);
                    if (var1.getTimestamp() == msg.getTimestamp()) {
                        //修改消息发送状态为成功
                        msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                        GroupMessage.setMessageModel(msg);
                        adapter.notifyDataSetChanged();
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
            GroupMessage msg = msgList.get(i);
            if (message.getTimestamp() == msg.getTimestamp()) {
                //修改消息发送状态为成功
                msg.setSendState(MessageModel.SEND_STATE_FAILURE);
                GroupMessage.setMessageModel(msg);
                adapter.notifyDataSetChanged();
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
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        //销毁正在播放的语音
        MediaManage.destory();
        AppManager.getAppManager().removeActivity(this);
        EventBus.getDefault().unregister(this);
        CIMListenerManager.removeMessageListener(this);
        if (autoScrollVoew != null) {
            autoScrollVoew.stop();
        }
        //取消adapter中下载文件的请求
        if (null != adapter) {
            adapter.cancelOkHttpClient();
        }
        super.onDestroy();
    }
}
