package com.merrichat.net.activity.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/7/1.
 * 关于我们
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_qq)
    TextView tvQq;
    @BindView(R.id.tv_gongzhonghao)
    TextView tvGongzhonghao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initTitleBar();
        // 当前版本名
        try {
            tvVersion.setText("讯美V" + getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化titleBar
     */
    private void initTitleBar() {
        tvTitleText.setText("关于我们");
    }

    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    @OnClick({R.id.iv_back, R.id.tv_gongzhonghao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_gongzhonghao:
                break;
        }
    }
}
