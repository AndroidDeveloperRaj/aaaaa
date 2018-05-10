package com.merrichat.net.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.RxTools.RxToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMSSY1 on 2017/11/7.
 */

public class MyInfoSettingAty extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_complaint)
    TextView tvComplaint;
    @BindView(R.id.tv_cansee_text)
    TextView tvCanseeText;
    @BindView(R.id.iv_cansee_img)
    ImageView ivCanseeImg;
    @BindView(R.id.rl_cansee)
    RelativeLayout rlCansee;
    @BindView(R.id.tv_nosee_text)
    TextView tvNoseeText;
    @BindView(R.id.iv_nosee_img)
    ImageView ivNoseeImg;
    @BindView(R.id.rl_nosee)
    RelativeLayout rlNosee;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    /**
     * 0 不让看我的朋友圈
     * 1 不看他的朋友圈
     */
    private int CANSEE_STATE = -1;
    private int NOSEE_STATE = -1;
    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfosetting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("资料设置");
    }

    @OnClick({R.id.iv_back, R.id.tv_complaint, R.id.rl_cansee, R.id.rl_nosee, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_complaint:
                break;
            case R.id.rl_cansee:
                CANSEE_STATE = 0;
                changeState(CANSEE_STATE);
                break;
            case R.id.rl_nosee:
                NOSEE_STATE = 1;
                changeState(NOSEE_STATE);
                break;
            case R.id.tv_delete:
                RxToast.showToastLong("CANSEE_STATE:"+CANSEE_STATE+" NOSEE_STATE::"+NOSEE_STATE);
                break;
        }
    }

    private void changeState(int state) {
        switch (state) {
            case 0:
                if (tvCanseeText.isSelected()) {
                    CANSEE_STATE = -1;
                    tvCanseeText.setSelected(false);
                    ivCanseeImg.setVisibility(View.GONE);
                } else {
                    tvCanseeText.setSelected(true);
                    ivCanseeImg.setVisibility(View.VISIBLE);
                }

                break;
            case 1:
                if (tvNoseeText.isSelected()) {
                    NOSEE_STATE = -1;
                    tvNoseeText.setSelected(false);
                    ivNoseeImg.setVisibility(View.GONE);
                } else {
                    tvNoseeText.setSelected(true);
                    ivNoseeImg.setVisibility(View.VISIBLE);
                }
                break;
        }

    }
}
