package com.merrichat.net.activity.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.app.MerriApp;
import com.merrichat.net.app.MyEventBusModel;
import com.merrichat.net.model.UserModel;
import com.merrichat.net.utils.MerriUtils;
import com.merrichat.net.utils.RxTools.RxActivityTool;
import com.merrichat.net.utils.SysApp;
import com.merrichat.net.view.CommomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/4.
 * <p>
 * 设置
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rl_new_tongzhi)
    RelativeLayout rlNewTongzhi;
    @BindView(R.id.rl_home_setting)
    RelativeLayout rlHomeSetting;
    @BindView(R.id.rl_yinsi)
    RelativeLayout rlYinsi;
    @BindView(R.id.rl_change_password)
    RelativeLayout rlChangePassword;
    @BindView(R.id.rl_functions)
    RelativeLayout rlFunctions;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.tv_register_num)
    TextView tvRegisterNum;
    private CommomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("设置");
        tvRegisterNum.setText(UserModel.getUserModel().getMobile());
    }


    @Subscribe
    public void onEvent(MyEventBusModel myEventBusModel) {
        if (myEventBusModel.CLOSE_MYACTIVITY) {
            if (null != dialog) {
                dialog.dismiss();
            }
            this.finish();
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.rl_new_tongzhi, R.id.rl_home_setting, R.id.rl_yinsi, R.id.rl_change_password, R.id.rl_functions, R.id.tv_exit})
    public void onViewClicked(View view) {
        //判断是否是快速点击
        if (MerriUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
            case R.id.rl_new_tongzhi:
                RxActivityTool.skipActivity(this, NewMessageNotifyAty.class);
                break;
            case R.id.rl_home_setting:
                RxActivityTool.skipActivity(this, AboutHomeSetting.class);
                break;
            case R.id.rl_yinsi:
                RxActivityTool.skipActivity(this, PrivacySetting.class);
                break;
            case R.id.rl_change_password:
                RxActivityTool.skipActivity(this, ChangePwdActivity.class);
                break;
            case R.id.rl_functions:
                RxActivityTool.skipActivity(this, FunctionsActivity.class);

                break;
            case R.id.tv_exit:
                //弹出提示框
                if (dialog != null) {
                    dialog.show();
                } else {
                    dialog = new CommomDialog(cnt, R.style.dialog, "您确定要退出登录吗？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                dialog.dismiss();
                                SysApp.stopNoticeService(cnt);
                                SysApp.lognOut(cnt, true, false);
                                if (MerriApp.socket != null && MerriApp.socket.connected()) {
                                    MerriApp.socket.disconnect();
                                }
                            }
                        }
                    }).setTitle("退出登录");
                    dialog.show();
                }

                break;
        }
    }


    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }
}
