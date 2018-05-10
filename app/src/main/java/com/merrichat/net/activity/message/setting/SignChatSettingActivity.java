package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.message.SingleChatActivity;
import com.merrichat.net.activity.video.MerriActionBarActivity;
import com.merrichat.net.app.AppManager;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.MessageListModle;
import com.merrichat.net.model.MessageModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.model.dao.MessageListModleDao;
import com.merrichat.net.model.dao.utils.GreenDaoManager;
import com.merrichat.net.utils.GetToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 18/2/26.
 * 单聊设置
 */

public class SignChatSettingActivity extends MerriActionBarActivity {

    /**
     * 置顶
     */
    @BindView(R.id.sb_stick)
    SwitchButton sbStick;

    /**
     * 消息免打扰
     */
    @BindView(R.id.sb_not_disturb)
    SwitchButton sbNotDisturb;

    /**
     * 清空聊天记录
     */
    @BindView(R.id.rl_clear_chat_history)
    RelativeLayout rlClearChatHistory;

    private int switchButtonFlag = 0;

    private final int switchOne = 1;//置顶群
    private final int switchTwo = 2;//消息免打扰

    private String receiverMemberId;


    /**
     * 是否置顶 0：否 1：是
     */
    private int top;

    /**
     * 是否消息免打扰 0：否 1：是
     */
    private int interrupt;


    private MessageListModle modle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_chat_setting);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initTitle() {
        setLeftBack();
        setTitle("聊天设置");
    }

    private void initView() {
        receiverMemberId = getIntent().getStringExtra("receiverMemberId");

        sbStick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchOne;
                topCommunity(isChecked);
            }
        });

        sbNotDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchButtonFlag = switchTwo;
                interruptCommunity(isChecked);
            }
        });

        QueryBuilder<MessageListModle> queryBuilder = GreenDaoManager.getInstance().getNewSession().getMessageListModleDao().queryBuilder();
        modle = queryBuilder.where(MessageListModleDao.Properties.SenderId.eq(receiverMemberId), MessageListModleDao.Properties.PrivateID.eq(UserModel.getUserModel().getMemberId())).unique();
        if (modle == null) {
            //防止意外，一般不会走
            modle = new MessageListModle();
            modle.setSenderId(receiverMemberId);
            modle.setType("1");
        }
        chatSetting();
    }


    /**
     * 单聊设置页面--是否置顶聊天是否消息免打扰
     * top  是否置顶 0：否 1：是
     * interrupt  是否消息免打扰 0：否 1：是
     */
    private void chatSetting() {
        OkGo.<String>post(Urls.chatSetting)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("targetId", receiverMemberId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    top = data.optInt("top");
                                    interrupt = data.optInt("interrupt");
                                    if (null != modle) {
                                        modle.setTop(top);
                                        modle.setInter(interrupt);
                                        MessageListModle.setMessageListModle(modle);
                                    }
                                    setJieMian();
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

    private void setJieMian() {
        if (top == 0) {
            sbStick.setCheckedImmediatelyNoEvent(false);
        } else {
            sbStick.setCheckedImmediatelyNoEvent(true);
        }


        if (interrupt == 0) {
            sbNotDisturb.setCheckedImmediatelyNoEvent(false);
        } else {
            sbNotDisturb.setCheckedImmediatelyNoEvent(true);
        }
    }

    @OnClick({R.id.rl_clear_chat_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_clear_chat_history:
                truncLog();
                break;
        }
    }


    /**
     * 置顶群
     * isPersonal  0:个人 1：群聊
     */
    private void topCommunity(final boolean isTop) {
        OkGo.<String>post(Urls.topCommunity)
                .params("cid", receiverMemberId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("isPersonal", "0")
                .params("isTop", isTop)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    sbStick.setCheckedImmediatelyNoEvent(isTop);
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
     * 消息免打扰
     * <p>
     * isPersonal  0:个人 1：群聊
     */
    private void interruptCommunity(final boolean isInter) {
        OkGo.<String>post(Urls.interruptCommunity)
                .params("cid", receiverMemberId)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("isPersonal", "0")
                .params("isInter", isInter)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    sbNotDisturb.setCheckedImmediatelyNoEvent(isInter);
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
     * 清空聊天记录
     * isPersonal   0:个人 1：群聊
     */
    private void truncLog() {
        OkGo.<String>post(Urls.truncLog)
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("targetId", receiverMemberId)
                .params("isPersonal", "0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject data = jsonObject.optJSONObject("data");
                                if (data.optBoolean("success")) {
                                    GetToast.useString(cnt, "清除成功");
                                    MessageModel.clearSingleMessageModel(receiverMemberId);
                                    MessageListModle.clearSingleMessageModelByReceivedId(receiverMemberId);
                                    MyEventBusModel model = new MyEventBusModel();
                                    model.SINGLE_MESSAGE_IS_REFRESH = true;
                                    model.MESSAGE_IS_REFRESH = true;
                                    EventBus.getDefault().post(model);
                                    if (AppManager.getAppManager().hasActivity(SingleChatActivity.class)) {
                                        AppManager.getAppManager().finishActivity(SingleChatActivity.class);
                                    }
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
     * 修改失败时，不改变按钮状态
     */
    private void noChangeStateButtonStatus() {
        switch (switchButtonFlag) {
            case switchOne:
                sbStick.setCheckedImmediatelyNoEvent(!sbStick.isChecked());
                break;
            case switchTwo:
                sbNotDisturb.setCheckedImmediatelyNoEvent(!sbNotDisturb.isChecked());
                break;
        }
    }

}
