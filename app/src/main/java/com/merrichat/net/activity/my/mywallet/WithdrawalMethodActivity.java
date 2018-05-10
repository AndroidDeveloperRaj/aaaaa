package com.merrichat.net.activity.my.mywallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.MiscUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/12/28.
 * 提现方式
 */

public class WithdrawalMethodActivity extends BaseActivity {
    public static final int activityid = MiscUtil.getActivityId();

    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;

    /**
     * 标题
     */
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;

    /**
     * 支付宝
     */
    @BindView(R.id.ll_alipay)
    LinearLayout llAlipay;

    /**
     * 微信
     */
    @BindView(R.id.ll_wechat)
    LinearLayout llWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_method);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.ll_alipay, R.id.ll_wechat})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_alipay://支付宝
                intent = new Intent();
                intent.putExtra("withdrawalMethod", 2);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.ll_wechat://微信
                intent = new Intent();
                intent.putExtra("withdrawalMethod", 1);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
