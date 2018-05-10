package com.merrichat.net.activity.message.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.video.MerriActionBarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by amssy on 18/1/25.
 * 查看群公告
 * 2018/02/08
 */

public class ModifyGroupIntroductionActivity extends MerriActionBarActivity {

    /**
     * 输入群介绍
     */
    @BindView(R.id.tv_group_introduction)
    TextView tvGroupIntroduction;

    private String communityNotice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_group_introduction);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }


    private void initTitle() {
        setTitle("群公告");
        setLeftBack();
    }

    private void initView() {
        communityNotice = getIntent().getStringExtra("communityNotice");
        tvGroupIntroduction.setText(communityNotice);
    }
}
