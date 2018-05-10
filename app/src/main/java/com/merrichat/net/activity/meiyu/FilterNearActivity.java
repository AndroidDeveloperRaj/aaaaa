package com.merrichat.net.activity.meiyu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merrichat.net.R;
import com.merrichat.net.activity.BaseActivity;
import com.merrichat.net.utils.GetToast;
import com.merrichat.net.view.ChaiRedBaoDialog;
import com.merrichat.net.view.MyCheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by amssy on 17/11/15.
 * 筛选附近的人
 */

public class FilterNearActivity extends BaseActivity {

    /**
     * 返回按钮
     */
    @BindView(R.id.iv_back)
    ImageView ivBack;


    @BindView(R.id.tv_title_text)
    TextView tvTitleText;

    /**
     * 右侧按钮
     */
    @BindView(R.id.tv_right_img)
    ImageView ivRightImg;

    /**
     * 女生
     */
    @BindView(R.id.ll_girls)
    LinearLayout llGirls;

    @BindView(R.id.mb_girls)
    MyCheckBox mbGirls;

    /**
     * 男生
     */
    @BindView(R.id.ll_boys)
    LinearLayout llBoys;

    @BindView(R.id.mb_boys)
    MyCheckBox mbBoys;

    /**
     * 男神
     */
    @BindView(R.id.ll_nan_shen)
    LinearLayout llNanShen;

    @BindView(R.id.mb_nan_shen)
    MyCheckBox mbNanShen;

    /**
     * 女神
     */
    @BindView(R.id.ll_nv_shen)
    LinearLayout llNvShen;

    @BindView(R.id.mb_nv_shen)
    MyCheckBox mbNvShen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_filter);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitleText.setText("附近的人");
        ivRightImg.setVisibility(View.VISIBLE);
        ivRightImg.setBackgroundResource(R.mipmap.shouye_shaixuan_2x);
    }

    @OnClick({R.id.iv_back, R.id.tv_right_img, R.id.ll_girls, R.id.ll_boys, R.id.ll_nan_shen, R.id.ll_nv_shen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_img:
                GetToast.useString(cnt, "点击按钮....");
                ChaiRedBaoDialog dialog = new ChaiRedBaoDialog(cnt);
                dialog.show();
                dialog.setClicklistener(new ChaiRedBaoDialog.ClickListenerInterface() {
                    @Override
                    public void chaiRedBao() {
                        GetToast.useString(cnt,"拆红包......");
                    }
                });
                break;
            case R.id.ll_girls://女生
                mbGirls.setChecked(true);
                mbBoys.setChecked(false);
                mbNanShen.setChecked(false);
                mbNvShen.setChecked(false);
                break;
            case R.id.ll_boys://男生
                mbGirls.setChecked(false);
                mbBoys.setChecked(true);
                mbNanShen.setChecked(false);
                mbNvShen.setChecked(false);
                break;
            case R.id.ll_nan_shen://男神
                mbGirls.setChecked(false);
                mbBoys.setChecked(false);
                mbNanShen.setChecked(true);
                mbNvShen.setChecked(false);
                break;
            case R.id.ll_nv_shen://女神
                mbGirls.setChecked(false);
                mbBoys.setChecked(false);
                mbNanShen.setChecked(false);
                mbNvShen.setChecked(true);
                break;
        }
    }
}
