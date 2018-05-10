package com.merrichat.net.activity.message.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.his.ComplaintOthersAty;
import com.merrichat.net.activity.his.HisYingJiAty;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/1/20.
 * 成员管理
 */

public class MemberManagementActivity extends MerriActionBarActivity {
    public static final int activityId = MiscUtil.getActivityId();
    private final int switchOne = 1;//是否禁言
    private final int switchTwo = 2;//禁止发布交易
    private final int switchThree = 3;//设置为管理员
    /**
     * 头像
     */
    @BindView(R.id.civ_member_photo)
    CircleImageView civMemberPhoto;
    /**
     * 名字
     */
    @BindView(R.id.tv_member_name)
    TextView tvMemberName;
    @BindView(R.id.rl_member_info)
    RelativeLayout rlMemberInfo;
    /**
     * 禁言默认关
     */
    @BindView(R.id.rl_gossip)
    RelativeLayout rlGossip;
    @BindView(R.id.ll_gossip)
    LinearLayout llGossip;
    /**
     * 禁止发布交易默认关
     */
    @BindView(R.id.rl_prohibition_trading)
    RelativeLayout rlProhibitionTrading;
    @BindView(R.id.ll_prohibition_trading)
    LinearLayout llProhibitionTrading;
    /**
     * 设为管理员默认关
     */
    @BindView(R.id.rl_set_administrator)
    RelativeLayout rlSetAdministrator;
    @BindView(R.id.ll_set_administrator)
    LinearLayout llSetAdministrator;
    /**
     * 举报
     */
    @BindView(R.id.rl_report)
    RelativeLayout rlReport;
    /**
     * 踢出群
     */
    @BindView(R.id.ll_remove_group)
    LinearLayout llRemoveGroup;
    /**
     * 禁言按钮
     * 0否1是
     */
    @BindView(R.id.sb_gossip)
    SwitchButton sbGossip;
    /**
     * 禁止发布交易按钮
     * 0：不能 1：可以
     */
    @BindView(R.id.sb_ban_trade)
    SwitchButton sbBanTrade;
    /**
     * 是否为管理员
     * 0否1是
     */
    @BindView(R.id.sb_master)
    SwitchButton sbMaster;
    private int switchButtonFlag = 0;


    /**
     * 群id
     */
    private String groupId = "";
    /**
     * 群成员id
     */
    private String communityMemberId;

    /**
     * 群成员头像
     */
    private String headImgUrl;

    /**
     * 群成员名字
     */
    private String memberName;


    /**
     * 0只显示举报
     * 1 权限全部显示
     */
    private int type;

    /**************以下为接口返回数据*********************/

    /**
     * 本人是否是管理员0:成员，1：管理员，2：群主
     */
    private int isMaster;

    /**
     * 查看的群成员是否是管理员0:成员，1：管理员，2：群主
     */
    private int communityMemberIsMaster;

    /**
     * 是否禁言0 否 1 是
     */
    private int isBanned;

    /**
     * 是否禁止交易 0：否 1：是
     */
    private int isBanTrade;
    private String hisPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_managment);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setTitle("成员管理");
        setLineGone();
        setLeftBack();
    }

    private void initView() {
        type = getIntent().getIntExtra("type", 0);
        groupId = getIntent().getStringExtra("groupId");
        communityMemberId = getIntent().getStringExtra("communityMemberId");
        headImgUrl = getIntent().getStringExtra("headImgUrl");
        memberName = getIntent().getStringExtra("memberName");
        hisPhone = getIntent().getStringExtra("hisPhone");

        tvMemberName.setText(memberName);
        Glide.with(cnt).load(headImgUrl).error(R.mipmap.ic_preloading).into(civMemberPhoto);
        if (type == 0) {
            llProhibitionTrading.setVisibility(View.GONE);
            llSetAdministrator.setVisibility(View.GONE);
            llRemoveGroup.setVisibility(View.GONE);
            llGossip.setVisibility(View.GONE);
        } else if (type == 1) {
            llProhibitionTrading.setVisibility(View.VISIBLE);
            llSetAdministrator.setVisibility(View.VISIBLE);
            llRemoveGroup.setVisibility(View.VISIBLE);
            llGossip.setVisibility(View.VISIBLE);
        }
        sbGossip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchOne;
                int isBanned;
                if (isChecked) {
                    isBanned = 1;
                } else {
                    isBanned = 0;
                }
                isBanned(isBanned);
            }
        });


        sbBanTrade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchTwo;
                int isBanTrade;
                if (isChecked) {
                    isBanTrade = 1;
                } else {
                    isBanTrade = 0;
                }
                isEmbargo(isBanTrade);
            }
        });

        sbMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchThree;
                int isMaster;
                if (isChecked) {
                    isMaster = 1;
                } else {
                    isMaster = 0;
                }
                isSetToAdministrator(isMaster);
            }
        });

        memberManagementPage();
    }

    @OnClick({R.id.rl_member_info, R.id.rl_report, R.id.ll_remove_group})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_member_info:
                Intent intent1 = new Intent(cnt, HisYingJiAty.class);
                intent1.putExtra("hisMemberId", Long.parseLong(communityMemberId));
                intent1.putExtra("hisImgUrl", headImgUrl);
                intent1.putExtra("hisPhone", hisPhone);
                intent1.putExtra("hisNickName", memberName);
                startActivity(intent1);
                break;
            case R.id.rl_report://举报
                Bundle bundle = new Bundle();
                bundle.putString("communityMemberId", communityMemberId);
                bundle.putString("memberName", memberName);
                bundle.putString("hisPhone", hisPhone);
                bundle.putInt("activityId", activityId);
                RxActivityTool.skipActivity(this, ComplaintOthersAty.class, bundle);
                break;
            case R.id.ll_remove_group://踢出群
                kickedOutCommunity();
                break;
        }
    }


    /**
     * .群成员管理踢出群
     */
    private void kickedOutCommunity() {
        OkGo.<String>post(Urls.kickedOutCommunity)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityMemberId", communityMemberId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "踢出群成功");
                                    MyEventBusModel eventBusModel = new MyEventBusModel();
                                    eventBusModel.REFRESH_GROUP_SETTING = true;
                                    EventBus.getDefault().post(eventBusModel);
                                    finish();
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
     * 查询成员管理设置
     */
    private void memberManagementPage() {
        OkGo.<String>post(Urls.memberManagementPage)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityMemberId", communityMemberId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    isMaster = data.optInt("isMaster");
                                    communityMemberIsMaster = data.optInt("communityMemberIsMaster");
                                    isBanned = data.optInt("isBanned");
                                    isBanTrade = data.optInt("isBanTrade");
                                    setSwithButton();
                                } else {
                                    String message = jsonObject.optString("message");
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
     * 设置按钮状态
     */
    private void setSwithButton() {
        if (communityMemberIsMaster == 1) {
            sbMaster.setCheckedImmediatelyNoEvent(true);
        } else {
            sbMaster.setCheckedImmediatelyNoEvent(false);
        }

        if (isBanned == 0) {
            sbGossip.setCheckedImmediatelyNoEvent(false);
        } else if (isBanned == 1) {
            sbGossip.setCheckedImmediatelyNoEvent(true);
        }

        if (isBanTrade == 0) {
            sbBanTrade.setCheckedImmediatelyNoEvent(false);
        } else if (isBanTrade == 1) {
            sbBanTrade.setCheckedImmediatelyNoEvent(true);
        }
    }

    /**
     * 成员管理页设置是否禁言
     */
    private void isBanned(final int isBanned) {
        OkGo.<String>post(Urls.isBanned)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityMemberId", communityMemberId)
                .params("isBanned", isBanned)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {

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
     * 成员管理页设置是否禁止发布交易
     */
    private void isEmbargo(final int isBanTrade) {
        OkGo.<String>post(Urls.isEmbargo)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityMemberId", communityMemberId)
                .params("isBanTrade", isBanTrade)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {

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
     * 成员管理页面是否设为管理员
     */
    private void isSetToAdministrator(final int isMaster) {
        OkGo.<String>post(Urls.isSetToAdministrator)
                .params("cid", groupId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("communityMemberId", communityMemberId)
                .params("isMaster", isMaster)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data.optBoolean("success")) {

                            } else {
                                noChangeStateButtonStatus();
                                String message = data.optString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    GetToast.useString(cnt, message);
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
                sbGossip.setCheckedImmediatelyNoEvent(!sbGossip.isChecked());
                break;

            case switchTwo:
                sbBanTrade.setCheckedImmediatelyNoEvent(!sbBanTrade.isChecked());
                break;

            case switchThree:
                sbMaster.setCheckedImmediatelyNoEvent(!sbMaster.isChecked());
                break;
        }
    }
}
