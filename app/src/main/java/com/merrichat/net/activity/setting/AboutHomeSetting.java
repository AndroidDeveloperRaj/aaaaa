package com.merrichat.net.activity.setting;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.adapter.AboutHomeSettingAdapter;
import com.merrichat.net.model.AboutHomeSettingModel;
import com.merrichat.net.view.lockview.PreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/4.
 * <p>
 * 首页设置
 */

public class AboutHomeSetting extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    public final static String ABOUT_HOME_SETTING = "about_home_setting";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.rv_receclerView)
    RecyclerView rvRececlerView;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    private AboutHomeSettingAdapter aboutHomeSettingAdapter;
    private ArrayList<AboutHomeSettingModel> settingItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abouthomesetting);
        ButterKnife.bind(this);
        iniView();
    }

    public void iniView() {
        tvTitleText.setText("首页设置");
        settingItemList = new ArrayList<>();
        initData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRececlerView.setLayoutManager(layoutManager);
        aboutHomeSettingAdapter = new AboutHomeSettingAdapter(R.layout.item_abouthomesetting, settingItemList);
        rvRececlerView.setAdapter(aboutHomeSettingAdapter);
        aboutHomeSettingAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        settingItemList.add(new AboutHomeSettingModel("动态", false));
        settingItemList.add(new AboutHomeSettingModel("消息", false));
        settingItemList.add(new AboutHomeSettingModel("发现", false));
        settingItemList.add(new AboutHomeSettingModel("相机", false));
        int index = PreferencesUtils.getInt(this, ABOUT_HOME_SETTING);
        if (index != -1) {//已设置过
            for (int i = 0; i < settingItemList.size(); i++) {
                if (index == i) {
                    settingItemList.get(index).setChecked(true);
                }
            }
        } else {//从未设置过
            settingItemList.get(0).setChecked(true);
        }

    }


    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int j = 0; j < settingItemList.size(); j++) {
            AboutHomeSettingModel aboutHomeSettingModel = settingItemList.get(j);
            if (j == position) {
                if (!aboutHomeSettingModel.isChecked()) {
                    aboutHomeSettingModel.setChecked(true);
                    PreferencesUtils.putInt(AboutHomeSetting.this, ABOUT_HOME_SETTING, j);
                }
            } else {
                aboutHomeSettingModel.setChecked(false);
            }
        }
        aboutHomeSettingAdapter.notifyDataSetChanged();
    }
}
