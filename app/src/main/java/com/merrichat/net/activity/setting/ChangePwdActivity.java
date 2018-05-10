package com.merrichat.net.activity.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.activity.login.LoginActivity;
import com.merrichat.net.callback.StringDialogCallback;
import com.merrichat.net.http.Urls;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.JSONObjectEx;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.RxTools.RxToast;
import com.merrichat.net.utils.StringUtil;
import com.merrichat.net.utils.SysApp;
import com.merrichat.net.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by amssy on 17/7/1.
 * 修改密码
 */

public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.editText_old_pwd)
    ClearEditText editText_old_pwd;
    @BindView(R.id.editText_new_pwd)
    ClearEditText editText_new_pwd;
    @BindView(R.id.editText_new_pwd_again)
    ClearEditText getEditText_new_pwd_again;
    @BindView(R.id.editText_nickname)
    ClearEditText editTextNickname;
    @BindView(R.id.layout_nickname)
    LinearLayout layoutNickname;
    @BindView(R.id.layout_agreement)
    LinearLayout layoutAgreement;
    @BindView(R.id.button_change_pwd)
    Button button_change_pwd;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        initTitleBar();
    }

    /**
     * 初始化titleBar
     */
    private void initTitleBar() {
        tvTitleText.setVisibility(View.GONE);
        tvTitleText.setText("修改密码");
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.button_change_pwd})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.button_change_pwd:
                if (TextUtils.isEmpty(editText_new_pwd.getText().toString()) && TextUtils.isEmpty(editText_old_pwd.getText().toString())
                        && TextUtils.isEmpty(getEditText_new_pwd_again.getText().toString())) {
                    RxToast.showToast("请输入新密码和旧密码");
                } else if (TextUtils.isEmpty(editText_old_pwd.getText().toString())) {
                    RxToast.showToast("请输入旧密码");

                } else if (TextUtils.isEmpty(editText_new_pwd.getText().toString())) {
                    RxToast.showToast("请输入新密码");

                } else if (TextUtils.isEmpty(getEditText_new_pwd_again.getText().toString())) {
                    RxToast.showToast("请重复输入新密码");

                } else if (editText_old_pwd.getText().toString().length() < 6 || editText_new_pwd.getText().toString().length() < 6 || getEditText_new_pwd_again.getText().toString().length() < 6) {
                    RxToast.showToast("密码不能少于6位");

                } else if (!editText_new_pwd.getText().toString().equals(getEditText_new_pwd_again.getText().toString())) {
                    RxToast.showToast("两次密码输入不一致");

                } else if (editText_old_pwd.getText().toString().equals(
                        editText_new_pwd.getText().toString())) {
                    RxToast.showToast("新旧密码不能相同");

                } else {
                    String oldPWD = editText_old_pwd.getText().toString().trim();
                    String newPWD = editText_new_pwd.getText().toString().trim();
                    updateMyPwd(oldPWD, newPWD);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 修改密码
     *
     * @param oldPWD
     * @param newPWD
     */
    private void updateMyPwd(String oldPWD, String newPWD) {
        OkGo.<String>get(Urls.UPDATE_MYPWD)//
                .tag(this)//
                .params("memberId", UserModel.getUserModel().getMemberId())
                .params("oldPwd", StringUtil.getBase64(oldPWD))
                .params("newPwd", StringUtil.getBase64(newPWD))
                .execute(new StringDialogCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (response != null) {
                            try {
                                JSONObjectEx jsonObjectEx = new JSONObjectEx(response.body());
                                if (jsonObjectEx.optBoolean("success")) {
                                    JSONObject data = jsonObjectEx.optJSONObject("data");
                                    RxToast.showToast(data.optString("msg"));
                                    if (data.optString("status").equals("1")) {//"1":修改成功 "0":修改失败
//                                        MyEventBusModel myEventBusModel = new MyEventBusModel();
//                                        myEventBusModel.REFRESH_MINE_HOME = true;
//                                        EventBus.getDefault().post(myEventBusModel);
//                                        UserModel.deleteUserModel(UserModel.getUserModel());
//                                        RxActivityTool.finishActivity(SettingActivity.class);
                                        SysApp.lognOut(cnt, true,false);
                                        SysApp.stopNoticeService(cnt);
//                                        RxActivityTool.skipActivityAndFinishAll(ChangePwdActivity.this, LoginActivity.class);
//                                        RxActivityTool.skipActivity(ChangePwdActivity.this, LoginActivity.class);
                                    }
                                } else {
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
                    }
                });
    }

    @OnTextChanged(value = R.id.editText_new_pwd_again, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged() {
        if (!TextUtils.isEmpty(editText_old_pwd.getText().toString()) && !TextUtils.isEmpty(editText_new_pwd.getText().toString())
                && !TextUtils.isEmpty(getEditText_new_pwd_again.getText().toString())) {
            button_change_pwd.setBackgroundResource(R.drawable.shape_button_login_true);
        } else {
            button_change_pwd.setBackgroundResource(R.drawable.shape_button_login);
        }
    }


}
