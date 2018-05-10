package com.merrichat.net.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxDataTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.view.lockview.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/4.
 * <p>
 * 我的--设置--隐私
 */

public class PrivacySetting extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.sb_see_mosheng)
    SwitchButton sbSeeMosheng;
    @BindView(R.id.sb_see_haoyou)
    SwitchButton sbSeeHaoyou;
    @BindView(R.id.sb_see_meiyu)
    SwitchButton sbSeeMeiyu;
    private String SEE_TYPE = "";//查询的时候,status不传, 0:允许陌生人查看,1:允许好友查看 2:美遇自动匹配屏蔽好友

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("隐私");
        String memberId = UserModel.getUserModel().getMemberId();
        String privacySetting_memberId = PreferencesUtils.getString(this, "PrivacySetting_memberId");
        if (!RxDataTool.isNullString(privacySetting_memberId) && privacySetting_memberId.equals(memberId)) {

            int permitUnfamiliar = PreferencesUtils.getInt(this, "permitUnfamiliar");
            int permitFriend = PreferencesUtils.getInt(this, "permitFriend");
            int meetStatus = PreferencesUtils.getInt(this, "meetStatus");
            if (permitUnfamiliar == 0) {
                sbSeeMosheng.setCheckedImmediatelyNoEvent(true);
            } else {
                sbSeeMosheng.setCheckedImmediatelyNoEvent(false);
            }
            if (permitFriend == 0) {
                sbSeeHaoyou.setCheckedImmediatelyNoEvent(true);

            } else {
                sbSeeHaoyou.setCheckedImmediatelyNoEvent(false);
            }
            if (meetStatus == 0) {
                sbSeeMeiyu.setCheckedImmediatelyNoEvent(true);

            } else {
                sbSeeMeiyu.setCheckedImmediatelyNoEvent(false);
            }
        } else {
            PreferencesUtils.putString(this, "PrivacySetting_memberId", memberId);
            queryAndSetPrivacy(SEE_TYPE);
        }
        sbSeeMosheng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SEE_TYPE = "0";
                queryAndSetPrivacy(SEE_TYPE);
            }
        });
        sbSeeHaoyou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SEE_TYPE = "1";

                queryAndSetPrivacy(SEE_TYPE);
            }
        });
        sbSeeMeiyu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SEE_TYPE = "2";
                queryAndSetPrivacy(SEE_TYPE);
            }
        });
    }

    /**
     * 0:允许陌生人查看,1:允许好友查看 2:美遇自动匹配屏蔽好友
     *
     * @param type
     */
    private void queryAndSetPrivacy(final String type) {
        OkGo.<String>get(Urls.QUERY_SETPRIVACY)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("status", type)
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx != null && jsonObjectEx.optBoolean("success")) {
                                    if (RxDataTool.isNullString(type)) {//查询
                                        JSONObject data = jsonObjectEx.optJSONObject("data");
                                        if (data != null) {
                                            int permitUnfamiliar = data.optInt("permitUnfamiliar");//允许陌生人查看 0:打开,1:关闭
                                            int permitFriend = data.optInt("permitFriend");//允许好友查看 0:打开,1:关闭
                                            int meetStatus = data.optInt("meetStatus");//美遇自动匹配屏蔽好友 0:打开,1:关闭
                                            if (!data.optBoolean("success")) {
                                                String info = data.optString("info");
                                                RxToast.showToast(info);
                                                return;
                                            }
                                            PreferencesUtils.putInt(PrivacySetting.this, "permitUnfamiliar", permitUnfamiliar);
                                            PreferencesUtils.putInt(PrivacySetting.this, "permitFriend", permitFriend);
                                            PreferencesUtils.putInt(PrivacySetting.this, "meetStatus", meetStatus);
                                            if (permitUnfamiliar == 0) {
                                                sbSeeMosheng.setCheckedImmediatelyNoEvent(true);
                                            } else {
                                                sbSeeMosheng.setCheckedImmediatelyNoEvent(false);
                                            }
                                            if (permitFriend == 0) {
                                                sbSeeHaoyou.setCheckedImmediatelyNoEvent(true);

                                            } else {
                                                sbSeeHaoyou.setCheckedImmediatelyNoEvent(false);
                                            }
                                            if (meetStatus == 0) {
                                                sbSeeMeiyu.setCheckedImmediatelyNoEvent(true);

                                            } else {
                                                sbSeeMeiyu.setCheckedImmediatelyNoEvent(false);
                                            }
                                        }
                                    } else {//修改设置
                                        JSONObject data = jsonObjectEx.optJSONObject("data");
                                        if (data != null && !data.optBoolean("success")) {
                                            noChangeStateButtonStatus();
                                            RxToast.showToast("操作失败！");
                                        } else {
                                            JSONObject jsonData = jsonObjectEx.optJSONObject("data");
                                            if (jsonData != null) {
                                                JSONObject status = jsonData.optJSONObject("status");
                                                switch (type) {
                                                    case "0":
                                                        int permitUnfamiliar = status.optInt("permitUnfamiliar");
                                                        PreferencesUtils.putInt(PrivacySetting.this, "permitUnfamiliar", permitUnfamiliar);
                                                        break;
                                                    case "1":
                                                        int permitFriend = status.optInt("permitFriend");
                                                        PreferencesUtils.putInt(PrivacySetting.this, "permitFriend", permitFriend);
                                                        break;
                                                    case "2":
                                                        int meetStatus = status.optInt("meetStatus");
                                                        PreferencesUtils.putInt(PrivacySetting.this, "meetStatus", meetStatus);

                                                        break;
                                                }
                                            }
                                            String failedInfo = data.optString("info");
                                            RxToast.showToast(failedInfo);
                                        }
                                    }
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
        if (SEE_TYPE.equals("0")) {
            sbSeeMosheng.setCheckedImmediatelyNoEvent(!sbSeeMosheng.isChecked());
        } else if (SEE_TYPE.equals("1")) {
            sbSeeHaoyou.setCheckedImmediatelyNoEvent(!sbSeeHaoyou.isChecked());
        } else if (SEE_TYPE.equals("2")) {
            sbSeeMeiyu.setCheckedImmediatelyNoEvent(!sbSeeMeiyu.isChecked());

        }
    }


    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
