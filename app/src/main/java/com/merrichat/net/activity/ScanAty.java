package com.merrichat.net.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.zxing.android.CaptureActivity;
import com.merrichat.net.R;
import com.merrichat.net.utils.DensityUtils;
import com.merrichat.net.utils.MiscUtil;
import com.merrichat.net.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenjingjing on 17/8/11.
 * 扫描运单号
 */

public class ScanAty extends CaptureActivity {
    public static final int activityId = MiscUtil.getActivityId();

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right_img)
    ImageView tvRightImg;
    @BindView(R.id.tv_title_text)
    TextView tvTitleText;
    private View addView;

    @Override
    public void addView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView = inflater.inflate(R.layout.activity_scan, lay_parent, false);
        lay_parent.addView(addView);
        ButterKnife.bind(this, addView);

        tvTitleText.setText("扫描运单号");
        tvRightImg.setVisibility(View.VISIBLE);
        tvRightImg.setImageResource(R.mipmap.icon_camera_light);

    }
//条形码框
//    @Override
//    public void initBarcodelay() {
//        int screenHeight = DensityUtils.px2icon_camera_lightdp(ScanAty.this, ScreenUtils.getScreenHeight(ScanAty.this));
//        int recoderHeight = 150;
//        getCameraManager().setPoint_left(30);
//        getCameraManager().setPoint_top(screenHeight / 2 - recoderHeight / 2);
//        getCameraManager().setView_recoder_hight(recoderHeight);
//        getViewfinderView().setmTipText("将码放入框内，即可自动扫码");
//    }

    //二维码框
    @Override
    public void initBarcodelay() {
        int screenHeight = DensityUtils.px2dp(ScanAty.this, ScreenUtils.getScreenHeight(ScanAty.this));
        int recoderHeight = DensityUtils.px2dp(ScanAty.this, ScreenUtils.getScreenWidth(ScanAty.this)) / 6;
        getCameraManager().setPoint_left(recoderHeight);
        getCameraManager().setPoint_top(screenHeight / 2 - recoderHeight * 2);
        getCameraManager().setView_recoder_hight(recoderHeight * 4);
        getViewfinderView().setmTipText("将码放入框内，即可自动扫码");
    }

    @Override
    public void decodeResult(String rawResult, Bitmap barcode) {
        Intent intent = new Intent();
        intent.putExtra("code", rawResult);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.iv_back, R.id.tv_right_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_img:
                turnFlashLight();
                break;
        }
    }
}
