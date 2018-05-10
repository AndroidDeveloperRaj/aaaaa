package com.merrichat.net.activity.setting;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.AboutHomeSettingAdapter;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.AboutHomeSettingModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.pre.PrefAppStore;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.lockview.PreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/6.
 * <p>
 * 新消息通知
 */

public class NewMessageNotifyAty extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.sb_checked_message)
    SwitchButton sbCheckedMessage;
    @BindView(R.id.rl_message)
    RelativeLayout rlMessage;
    @BindView(R.id.tv_shipin_text)
    TextView tvShipinText;
    @BindView(R.id.sb_checked_shipin)
    SwitchButton sbCheckedShipin;
    @BindView(R.id.rl_shipin_yuyin)
    RelativeLayout rlShipinYuyin;
    @BindView(R.id.rv_receclerView)
    RecyclerView rvRececlerView;
    private ArrayList<AboutHomeSettingModel> list;
    private AboutHomeSettingAdapter aboutHomeSettingAdapter;
    private String sbCheckedType = "";//0 勿扰 1 可以视频(查询状态的时候,videoStatus传null就行)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmessage_notify);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("新消息通知");
        list = new ArrayList<>();
        //视频、语音电话设置
        aboutPhoneSetting();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRececlerView.setLayoutManager(layoutManager);
        aboutHomeSettingAdapter = new AboutHomeSettingAdapter(R.layout.item_abouthomesetting, list);
        rvRececlerView.setAdapter(aboutHomeSettingAdapter);
        aboutHomeSettingAdapter.setOnItemClickListener(this);

        String isReceiveNewMSG = PreferencesUtils.getString(NewMessageNotifyAty.this, "isReceiveNewMSG");
        if (RxDataTool.isNullString(isReceiveNewMSG)) {//未设置 默认可以接收新消息
            sbCheckedMessage.setCheckedImmediatelyNoEvent(true);
        } else {
            if (isReceiveNewMSG.equals("true")) {
                sbCheckedMessage.setCheckedImmediatelyNoEvent(true);

            } else if (isReceiveNewMSG.equals("false")) {
                sbCheckedMessage.setCheckedImmediatelyNoEvent(false);

            }
        }

        //接收新消息通知
        sbCheckedMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferencesUtils.putString(NewMessageNotifyAty.this, "isReceiveNewMSG", "true");
                } else {
                    PreferencesUtils.putString(NewMessageNotifyAty.this, "isReceiveNewMSG", "false");
                }
            }
        });
        //陌生视频开关
        sbCheckedShipin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sbCheckedType = "1";
                    setMVStatus(sbCheckedType);
                } else {
                    sbCheckedType = "0";
                    setMVStatus(sbCheckedType);
                }
            }
        });
        // 查询状态
        setMVStatus(sbCheckedType);
    }

    private void aboutPhoneSetting() {
        list.add(new AboutHomeSettingModel("声音", false));
        list.add(new AboutHomeSettingModel("震动", false));
        list.add(new AboutHomeSettingModel("声音+震动", false));
        int index = PrefAppStore.getAboutPhoneSetting(this);
        for (int i = 0; i < list.size(); i++) {
            if (index == i) {
                list.get(index).setChecked(true);
            }
        }
    }

    private void setMVStatus(final String type) {
        OkGo.<String>get(Urls.SET_MV_STATUS)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("videoStatus", type)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                    if (RxDataTool.isNullString(type)) {//查询
                                        int data = jsonObjectEx.optInt("data");
                                        String failedInfo = jsonObjectEx.optString("data");
                                        if (!RxDataTool.isNumber(failedInfo)) {
                                            RxToast.showToast(failedInfo);
                                            noChangeStateButtonStatus();
                                            return;
                                        }
                                        if (data == 1) {//0勿扰 1可以聊天
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(true);
                                        } else if (data == 0) {
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(false);
                                        }
                                    } else {
                                        String data = jsonObjectEx.optString("data");
                                        String failedInfo = jsonObjectEx.optString("data");
                                        if (!RxDataTool.isNumber(failedInfo)) {
                                            RxToast.showToast(failedInfo);
                                            noChangeStateButtonStatus();
                                            return;
                                        }
                                        if (type.equals("1")) {
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(true);
                                        } else if (type.equals("0")) {
                                            sbCheckedShipin.setCheckedImmediatelyNoEvent(false);

                                        }
                                    }
                                    MyEventBusModel myEventBusModel = new MyEventBusModel();
                                    myEventBusModel.REFRESH_MINE_MV = true;
                                    EventBus.getDefault().post(myEventBusModel);
                                } else {
                                    noChangeStateButtonStatus();
                                    RxToast.showToast(jsonObjectEx.optString("message"));
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
                        noChangeStateButtonStatus();
                    }
                });
    }

    /**
     * 修改失败时，不改变按钮状态
     */
    private void noChangeStateButtonStatus() {
        sbCheckedShipin.setCheckedImmediatelyNoEvent(!sbCheckedShipin.isChecked());
    }

    @OnClick({R.id.iv_back, R.id.rl_message, R.id.rl_shipin_yuyin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_message:
                break;
            case R.id.rl_shipin_yuyin:
                break;
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int j = 0; j < list.size(); j++) {
            AboutHomeSettingModel aboutHomeSettingModel = list.get(j);
            if (j == position) {
                if (!aboutHomeSettingModel.isChecked()) {
                    aboutHomeSettingModel.setChecked(true);
                    PrefAppStore.setAboutPhoneSetting(NewMessageNotifyAty.this, j);
                }
            } else {
                aboutHomeSettingModel.setChecked(false);
            }
        }
        aboutHomeSettingAdapter.notifyDataSetChanged();
    }
}
