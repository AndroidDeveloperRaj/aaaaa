package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.groupmarket.GroupAdminstorActivity;
import com.merrichat.net.activity.his.ComplaintOthersAty;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.message.GroupChattingAty;
import com.merrichat.net.activity.message.SelectFriendAty;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.my.MyDynamicsAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.adapter.AllGroupMemberAdapter;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.GroupMemberModel;
import com.merrichat.net.model.GroupMessage;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/18.
 * 群设置
 */

public class GroupSettingActivity extends MerriActionBarActivity {

    public static final int activityId = MiscUtil.getActivityId();



    /**
     * 修改群资料
     */
    @BindView(R.id.rl_modify_information)
    RelativeLayout rlModifyInformation;

    @BindView(R.id.tv_modify_information)
    TextView tvModifyInformation;

    @BindView(R.id.iv_modify_information)
    ImageView ivModifyInformation;


    /**
     * 群主钱包
     */
    @BindView(R.id.ll_master_wallets)
    LinearLayout llMasterWallets;

    /**
     * 群订单管理
     */
    @BindView(R.id.ll_group_order_management)
    LinearLayout llGroupOrderManagement;

    /**
     * 群公告
     */
    @BindView(R.id.rl_group_announcement)
    RelativeLayout rlGroupAnnouncement;

    /**
     * 设置群管理员
     */
    @BindView(R.id.ll_set_group_manager)
    LinearLayout llSetGroupManager;

    /**
     * 群交易设置
     */
    @BindView(R.id.ll_group_trading_settings)
    LinearLayout llGroupTradingSettings;


    /**
     * 保存通讯录
     */
    @BindView(R.id.ll_save_address_book)
    LinearLayout llSaveAddressBook;

    /**
     * 群二维码
     */
    @BindView(R.id.rl_twodimensional_code)
    RelativeLayout rlTwodimensionalCode;

    /**
     * 加群设置
     */
    @BindView(R.id.ll_add_group_settings)
    LinearLayout llAddGroupSettings;

    /**
     * 设置群禁言
     */
    @BindView(R.id.ll_set_the_gag)
    LinearLayout llSetTheGag;

    /**
     * 群投诉
     */
    @BindView(R.id.ll_group_complaints)
    LinearLayout llGroupComplaints;

    /**
     * 清空聊天记录
     */
    @BindView(R.id.rl_clear_chat_history)
    RelativeLayout rlClearChatHistory;

    /**
     * 删除并退出
     */
    @BindView(R.id.ll_delete_and_exit)
    LinearLayout llDeleteAndExit;


    /**
     * 查看全部群成员
     */
    @BindView(R.id.ll_all_group_members)
    LinearLayout llAllGroupMembers;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    /**
     * 置顶群
     */
    @BindView(R.id.sb_group_top)
    SwitchButton sbGroupTop;

    /**
     * 消息免打扰
     */
    @BindView(R.id.sb_message_not_disturb)
    SwitchButton sbMessageNotSisturb;

    /**
     * 保存通讯录
     */
    @BindView(R.id.sb_save_address_book)
    SwitchButton sbSaveAddressBook;


    private AllGroupMemberAdapter adapter;

    private List<GroupMemberModel> groupMemberList;
    private List<GroupMemberModel> newGroupMemberList;

    private int switchButtonFlag = 0;

    private final int switchOne = 1;//置顶群
    private final int switchTwo = 2;//消息免打扰
    private final int switchThree = 3;//保存通讯录


    /**
     * 群成员显示个数
     */
    private int showSize = 10;


    /**
     * 清空聊天记录标记
     */
    private boolean isUpdateGroupMessage = false;

    /**
     * 修改群名字标记
     */
    private boolean isUpdateGroupName = false;


    /***************接口返回数据************************/


    /**
     * 是否置顶
     * 0：不是 1：是
     */
    private String isTop;

    /**
     * 是否显示钱包 默认0：不能 1：可以
     */
    private int isShowWallet;

    /**
     * 是否是管理员 0：成员 1：管理员 2：群主
     */
    private int isMaster;

    /**
     * 是否保存到通讯录列表 0：不是 1：是
     */
    private String isSaveList;


    /**
     * 是否消息免打扰 0：不是 1：是
     */
    private String isInterrupt;


    /**
     * 加群设置默认为1 1：默认允许任何人加群 2：不允许任何人加群 3：需要管理员审核
     */
    private int joinSetUp;


    /**
     * 群钱包id
     */
    private String communityAccountId;

    /**
     * 群人数
     */
    private String memberNum;

    /**
     * 群名字
     */
    private String communityName;


    /**
     * 群头像
     */
    private String communityImgUrl;


    /**
     * 群公告
     */
    private String communityNotice;


    /**
     * 身份flag
     * 默认为false
     * 如果是群主and管理员  则为  true
     * 如果为群成员 则为false
     */
    private boolean IdentityFlag;

    private int NumFlag;


    /**
     * 群id
     */
    private String groupId;

    private MessageListModle modle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setLeftBack();
        initView();
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.REFRESH_GROUP_SETTING) {//刷新数据
            groupMemberList.clear();
            newGroupMemberList.clear();
            communityHome();
        } else if (myEventBusModel.REFRESH_GROUP_SETTING_NAME) {
            isUpdateGroupName = true;
            groupMemberList.clear();
            newGroupMemberList.clear();
            communityHome();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("groupId");
            isMaster = intent.getIntExtra("isMaster", 0);
        }

        groupMemberList = new ArrayList<>();
        newGroupMemberList = new ArrayList<>();

        if (isMaster == 1 || isMaster == 2) {
            showSize = 8;
            IdentityFlag = true;
        } else {
            showSize = 10;
            IdentityFlag = false;
        }
        NumFlag = 0;
        GridLayoutManager layoutManage = new GridLayoutManager(cnt, 5);
        recyclerView.setLayoutManager(layoutManage);
        adapter = new AllGroupMemberAdapter(cnt, newGroupMemberList, NumFlag, IdentityFlag);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);


        adapter.setOnItemClickListener(new AllGroupMemberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GroupMemberModel model = newGroupMemberList.get(position);
                if (IdentityFlag) {
                    if (model.getTypeFlag() == 1) {//点击 + 号   添加人
                        if (joinSetUp == 2) {
                            GetToast.useString(cnt, "群主已设置禁止任何人加群");
                            return;
                        }
                        startActivity(new Intent(cnt, SelectFriendAty.class).addFlags(activityId).putExtra("groupId", groupId));
                    } else if (model.getTypeFlag() == 2) {//点击 - 号  踢人
                        startActivity(new Intent(cnt, KickOutGroupActivity.class).putExtra("groupId", groupId).putExtra("isMaster", isMaster));
                    } else {//点击的是头像
                        if (model.getMemberId().equals(UserModel.getUserModel().getMemberId())) {
                            startActivity(new Intent(cnt, MyDynamicsAty.class));
                        } else {
                            if (isMaster == 2) {//群主身份
                                startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 1));
                            } else if (isMaster == 1) {//管理员身份
                                if (model.getIsMater() == 2) {//管理员点击群主头像--进入群主主页
                                    Intent intent1 = new Intent(cnt, HisYingJiAty.class);
                                    intent1.putExtra("hisMemberId", Long.parseLong(model.getMemberId()));
                                    intent1.putExtra("hisImgUrl", model.getHeadImgUrl());
                                    intent1.putExtra("hisNickName", model.getMemberName());
                                    startActivity(intent1);
                                } else if (model.getIsMater() == 1) {//管理员点击管理员头像---进入成员管理--只有部分权限
                                    startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 0));
                                } else {//管理员点击群成员头像---进入成员管理---拥有全部权限
                                    startActivity(new Intent(cnt, MemberManagementActivity.class).putExtra("groupId", groupId).putExtra("communityMemberId", model.getMemberId()).putExtra("headImgUrl", model.getHeadImgUrl()).putExtra("memberName", model.getMemberName()).putExtra("type", 1));
                                }
                            }
                        }
                    }
                } else {
                    if (model.getMemberId().equals(UserModel.getUserModel().getMemberId())) {
                        startActivity(new Intent(cnt, MyDynamicsAty.class));
                    } else {
                        Intent intent = new Intent(cnt, HisYingJiAty.class);
                        intent.putExtra("hisMemberId", Long.parseLong(model.getMemberId()));
                        intent.putExtra("hisImgUrl", model.getHeadImgUrl());
                        intent.putExtra("hisNickName", model.getMemberName());
                        startActivity(intent);
                    }
                }
            }
        });


        sbGroupTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchOne;
                topCommunity(isChecked);
            }
        });

        sbMessageNotSisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchTwo;
                interruptCommunity(isChecked);
            }
        });

        sbSaveAddressBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchThree;
                int isSaveList;
                if (isChecked) {
                    isSaveList = 1;
                } else {
                    isSaveList = 0;
                }
                saveList(isSaveList);
            }
        });


        QueryBuilder<MessageListModle> queryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
        modle = queryBuilder.where(MessageListModleDao.Properties.GroupId.eq(groupId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();

        communityHome();
    }


    /**
     * 群主页数据
     */
    private void communityHome() {
        OkGo.<String>post(Urls.communityHome)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("showSize", showSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    isTop = data.optString("isTop");
                                    isShowWallet = data.optInt("isShowWallet");
                                    isMaster = data.optInt("isMaster");
                                    isSaveList = data.optString("isSaveList");
                                    isInterrupt = data.optString("isInterrupt");
                                    joinSetUp = data.optInt("joinSetUp");
                                    communityAccountId = data.optString("communityAccountId");
                                    memberNum = data.optString("memberNum");
                                    communityName = data.optString("communityName");
                                    communityImgUrl = data.optString("communityImgUrl");
                                    communityNotice = data.optString("communityNotice");
                                    Gson gson = new Gson();
                                    JSONArray list = data.optJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        GroupMemberModel model = gson.fromJson(list.get(i).toString(), GroupMemberModel.class);
                                        groupMemberList.add(model);
                                    }

                                    if (null != modle) {
                                        modle.setTop(Integer.parseInt(isTop));
                                        modle.setInter(Integer.parseInt(isInterrupt));
                                        MessageListModle.setMessageListModle(modle);
                                    }
                                    setPageDisplay();
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }

                            } else {
                                String message = jsonObject.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 设置页面展示
     */
    private void setPageDisplay() {

        if (isMaster == 2) {//群主
            tvModifyInformation.setText("修改群资料");
            ivModifyInformation.setVisibility(View.VISIBLE);
            llMasterWallets.setVisibility(View.VISIBLE);
            llGroupOrderManagement.setVisibility(View.VISIBLE);
            llSetGroupManager.setVisibility(View.VISIBLE);
            llGroupTradingSettings.setVisibility(View.VISIBLE);
            llSaveAddressBook.setVisibility(View.GONE);
            llAddGroupSettings.setVisibility(View.VISIBLE);
            llSetTheGag.setVisibility(View.VISIBLE);
            llGroupComplaints.setVisibility(View.GONE);
            llDeleteAndExit.setVisibility(View.GONE);
        } else if (isMaster == 1) {//管理员
            tvModifyInformation.setText("查看群资料");


            if (isShowWallet == 1) {
                llMasterWallets.setVisibility(View.VISIBLE);
            } else {
                llMasterWallets.setVisibility(View.GONE);
            }

            llGroupOrderManagement.setVisibility(View.VISIBLE);
            llSetGroupManager.setVisibility(View.GONE);
            llGroupTradingSettings.setVisibility(View.GONE);
            llSaveAddressBook.setVisibility(View.VISIBLE);
            llAddGroupSettings.setVisibility(View.GONE);
            llSetTheGag.setVisibility(View.VISIBLE);
            llGroupComplaints.setVisibility(View.VISIBLE);
            llDeleteAndExit.setVisibility(View.VISIBLE);
        } else {//群成员
            tvModifyInformation.setText("查看群资料");

            llMasterWallets.setVisibility(View.GONE);
            llGroupOrderManagement.setVisibility(View.GONE);
            llSetGroupManager.setVisibility(View.GONE);
            llGroupTradingSettings.setVisibility(View.GONE);
            llSaveAddressBook.setVisibility(View.VISIBLE);
            llAddGroupSettings.setVisibility(View.GONE);
            llSetTheGag.setVisibility(View.GONE);
            llGroupComplaints.setVisibility(View.VISIBLE);
            llDeleteAndExit.setVisibility(View.VISIBLE);
        }


        setTitle(communityName + "(" + memberNum + ")");
        newGroupMemberList.clear();

        if (IdentityFlag) {
            if (groupMemberList.size() > 8) {
                for (int i = 0; i < groupMemberList.size(); i++) {
                    if (i < 8) {
                        newGroupMemberList.add(groupMemberList.get(i));
                    }
                }
                NumFlag = 8;
            } else {
                for (int i = 0; i < groupMemberList.size(); i++) {
                    newGroupMemberList.add(groupMemberList.get(i));
                }
                NumFlag = groupMemberList.size();
            }
            for (int i = 0; i < 2; i++) {
                GroupMemberModel model = new GroupMemberModel();
                model.setMemberName("");
                if (i == 0) {
                    model.setTypeFlag(1);
                } else {
                    model.setTypeFlag(2);
                }
                newGroupMemberList.add(model);
            }
        } else {
            if (groupMemberList.size() > 10) {
                for (int i = 0; i < groupMemberList.size(); i++) {
                    if (i < 10) {
                        newGroupMemberList.add(groupMemberList.get(i));
                    }
                }
            } else {
                for (int i = 0; i < groupMemberList.size(); i++) {
                    newGroupMemberList.add(groupMemberList.get(i));
                }
            }
        }

        adapter.setNotifyDataSetChanged(cnt, newGroupMemberList, NumFlag, IdentityFlag);

        if ("0".equals(isTop)) {
            sbGroupTop.setCheckedImmediatelyNoEvent(false);
        } else if ("1".equals(isTop)) {
            sbGroupTop.setCheckedImmediatelyNoEvent(true);
        }

        if ("0".equals(isSaveList)) {
            sbSaveAddressBook.setCheckedImmediatelyNoEvent(false);
        } else if ("1".equals(isSaveList)) {
            sbSaveAddressBook.setCheckedImmediatelyNoEvent(true);
        }

        if ("0".equals(isInterrupt)) {
            sbMessageNotSisturb.setCheckedImmediatelyNoEvent(false);
        } else if ("1".equals(isInterrupt)) {
            sbMessageNotSisturb.setCheckedImmediatelyNoEvent(true);
        }
    }

    @OnClick({R.id.rl_modify_information, R.id.ll_all_group_members, R.id.ll_master_wallets, R.id.ll_group_order_management, R.id.rl_group_announcement, R.id.ll_set_group_manager, R.id.ll_group_trading_settings, R.id.rl_twodimensional_code, R.id.ll_add_group_settings, R.id.ll_set_the_gag, R.id.ll_group_complaints, R.id.rl_clear_chat_history, R.id.ll_delete_and_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_modify_information://修改群资料
                startActivity(new Intent(cnt, ModifyTheGroupDataActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.ll_master_wallets://群主钱包
                startActivity(new Intent(cnt, MasterWalletsActivity.class).putExtra("groupId", groupId).putExtra("communityAccountId", communityAccountId).putExtra("isMaster", isMaster));
                break;
            case R.id.ll_group_order_management://群订单管理
                startActivity(new Intent(cnt, OrderManagemenGrouptActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.rl_group_announcement://群公告
                if (isMaster == 1 || isMaster == 2) {
                    startActivity(new Intent(cnt, SetGroupAnnouncementActivity.class).putExtra("type", 2).putExtra("groupId", groupId));
                } else {
                    startActivity(new Intent(cnt, ModifyGroupIntroductionActivity.class).putExtra("communityNotice", communityNotice));
                }
                break;
            case R.id.ll_set_group_manager://设置群管理
                startActivity(new Intent(cnt, GroupAdminstorActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.ll_group_trading_settings://群交易设置
                startActivity(new Intent(cnt, TradingSettingActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.rl_twodimensional_code://群二维码
                if (joinSetUp == 2) {
                    GetToast.useString(cnt, "群主已设置禁止任何人加群");
                    return;
                }
                startActivity(new Intent(cnt, TwoDimensionalCodeActivity.class).putExtra("groupId", groupId).putExtra("communityImgUrl", communityImgUrl).putExtra("communityName", communityName));
                break;
            case R.id.ll_add_group_settings://加群设置
                startActivity(new Intent(cnt, AddGroupSettingActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.ll_set_the_gag://设置群禁言
                startActivity(new Intent(cnt, SetGroupGossipActivity.class).putExtra("groupId", groupId));
                break;
            case R.id.ll_group_complaints://群投诉
                startActivity(new Intent(cnt, ComplaintOthersAty.class).putExtra("groupId", groupId).addFlags(GroupSettingActivity.activityId));
                break;
            case R.id.rl_clear_chat_history://清空聊天记录
                truncLog();
                break;
            case R.id.ll_delete_and_exit://删除并退出
                deleteAndExit();
                break;
            case R.id.ll_all_group_members://群成员列表
                startActivity(new Intent(cnt, GroupMembersListActivity.class).putExtra("groupId", groupId).putExtra("isMaster", isMaster));
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishThisActivity();
    }


    /**
     * 关闭这个页面逻辑处理
     * 如果isUpdateGroupMessage为true  代表清空了聊天记录
     */
    private void finishThisActivity() {
        if (isUpdateGroupMessage) {
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_GROUP_MESSAGE = true;
            EventBus.getDefault().post(eventBusModel);
        }
        if (isUpdateGroupName) {
            MyEventBusModel eventBusModel = new MyEventBusModel();
            eventBusModel.REFRESH_GROUP_MESSAGE_NAME = true;
            eventBusModel.REFRESH_GROUP_MESSAGE_NAME_STRING = communityName;
            EventBus.getDefault().post(eventBusModel);
        }
        setResult(RESULT_OK);
        finish();
    }


    /**
     * 删除并退出群组
     */
    private void deleteAndExit() {
        OkGo.<String>post(Urls.deleteAndExit)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                GetToast.useString(cnt, "退出成功");
                                GroupMessage.clearThisGroupMessage(groupId);
                                MessageListModle.clearSingleMessageModel(groupId);
                                MyEventBusModel myEventBusModel = new MyEventBusModel();
                                myEventBusModel.FINIIS_GROUP_CHAT = true;
                                EventBus.getDefault().post(myEventBusModel);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 清空聊天记录
     * isPersonal  0:个人 1：群聊
     */
    private void truncLog() {
        OkGo.<String>post(Urls.truncLog)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("targetId", groupId)
                .params("isPersonal", "1")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "清除成功");
                                    GroupMessage.clearThisGroupMessage(groupId);
                                    MessageListModle.clearSingleMessageModel(groupId);
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.MESSAGE_IS_REFRESH = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                    if (AppManager.getAppManager().hasActivity(GroupChattingAty.class)) {
                                        AppManager.getAppManager().finishActivity(GroupChattingAty.class);
                                    }
                                    isUpdateGroupMessage = true;
                                } else {
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 置顶群
     * isPersonal  0:个人 1：群聊
     */
    private void topCommunity(final boolean isTop) {
        OkGo.<String>post(Urls.topCommunity)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("isPersonal", "1")
                .params("isTop", isTop)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    sbGroupTop.setCheckedImmediatelyNoEvent(isTop);
                                    if (null != modle) {
                                        if (isTop) {
                                            modle.setTop(1);
                                        } else {
                                            modle.setTop(0);
                                        }
                                        MessageListModle.setMessageListModle(modle);
                                    }
                                } else {
                                    noChangeStateButtonStatus();
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            noChangeStateButtonStatus();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        noChangeStateButtonStatus();
                    }
                });

    }


    /**
     * 群消息免打扰
     * <p>
     * isPersonal  0:个人 1：群聊
     */
    private void interruptCommunity(final boolean isInter) {
        OkGo.<String>post(Urls.interruptCommunity)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("isPersonal", "1")
                .params("isInter", isInter)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    sbMessageNotSisturb.setCheckedImmediatelyNoEvent(isInter);
                                    if (null != modle) {
                                        if (isInter) {
                                            modle.setInter(1);
                                        } else {
                                            modle.setInter(0);
                                        }
                                        MessageListModle.setMessageListModle(modle);
                                    }
                                } else {
                                    noChangeStateButtonStatus();
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            noChangeStateButtonStatus();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        noChangeStateButtonStatus();
                    }
                });

    }

    /**
     * 是否保存到通讯录
     *
     * @param isSaveList
     */
    private void saveList(final int isSaveList) {
        OkGo.<String>post(Urls.saveList)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("isSaveList", isSaveList)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    if (isSaveList == 1) {
                                        sbSaveAddressBook.setCheckedImmediatelyNoEvent(true);
                                    } else {
                                        sbSaveAddressBook.setCheckedImmediatelyNoEvent(false);
                                    }
                                } else {
                                    noChangeStateButtonStatus();
                                    String message = data.optString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        GetToast.useString(cnt, message);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            noChangeStateButtonStatus();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        noChangeStateButtonStatus();
                    }
                });

    }


    /**
     * 修改失败时，不改变按钮状态
     */
    private void noChangeStateButtonStatus() {
        switch (switchButtonFlag) {
            case switchOne:
                sbGroupTop.setCheckedImmediatelyNoEvent(!sbGroupTop.isChecked());
                break;

            case switchTwo:
                sbMessageNotSisturb.setCheckedImmediatelyNoEvent(!sbMessageNotSisturb.isChecked());
                break;

            case switchThree:
                sbSaveAddressBook.setCheckedImmediatelyNoEvent(!sbSaveAddressBook.isChecked());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


